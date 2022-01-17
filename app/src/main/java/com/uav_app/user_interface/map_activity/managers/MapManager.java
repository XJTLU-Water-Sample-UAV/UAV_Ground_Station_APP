package com.uav_app.user_interface.map_activity.managers;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import com.amap.api.maps.AMap;
import com.amap.api.maps.AMapOptions;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.CoordinateConverter;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.maps.model.Polyline;
import com.amap.api.maps.model.PolylineOptions;
import com.uav_app.tools.AccessParameter;
import com.uav_app.uav_manager.R;
import com.uav_app.uav_manager.nav_point.CoordinateTransformUtil;
import com.uav_app.uav_manager.nav_point.NavPoint;
import com.uav_app.uav_manager.nav_point.NavPointManager;
import com.uav_app.user_interface.map_activity.MapActivity;
import com.uav_app.user_interface.map_activity.MapActivityState;

import java.util.ArrayList;
import java.util.List;

public class MapManager extends Manager implements MapActivityState.StateChangeListener {
    // 地图对象
    private final AMap aMap;
    // 选点回调函数
    private final AMap.OnMapClickListener clickListener;
    // 选点管理对象
    private final NavPointManager pointManager;
    // 选点列表
    private final ArrayList<Marker> markerList;
    // 画线对象
    private Polyline polyline;

    public MapManager(MapActivity activity, MapView mMapView) {
        super(activity, 0x02);
        // 获取map对象
        aMap = mMapView.getMap();
        UiSettings uiSettings = aMap.getUiSettings();
        uiSettings.setZoomPosition(AMapOptions.ZOOM_POSITION_RIGHT_CENTER);
        uiSettings.setCompassEnabled(true);
        // 初始化选点列表和管理对象
        markerList = MapActivityState.getMapActivityState().mapViewState.markerList;
        pointManager = MapActivityState.getMapActivityState().getPointManager();
        // 初始化选点回调函数
        clickListener = this::selectPoint;
        // 初始化定位蓝点样式类
        MyLocationStyle myLocationStyle = new MyLocationStyle();
        // 设置连续定位模式下的定位间隔，只在连续定位模式下生效，单次定位模式下不会生效。单位为毫秒。
        myLocationStyle.interval(2000);
        // 连续定位、蓝点不会移动到地图中心点，定位点依照设备方向旋转，并且蓝点会跟随设备移动。
        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE_NO_CENTER);
        // 显示定位蓝点
        myLocationStyle.showMyLocation(true);
        // 设置定位蓝点的Style
        aMap.setMyLocationStyle(myLocationStyle);
        // 设置默认定位按钮是否显示，非必需设置。
        aMap.getUiSettings().setMyLocationButtonEnabled(true);
        // 设置为true表示启动显示定位蓝点，false表示隐藏定位蓝点并不进行定位，默认是false。
        aMap.setMyLocationEnabled(true);
        // 添加选点连线
        PolylineOptions polylineOptions = new PolylineOptions();
        polyline = aMap.addPolyline(polylineOptions);
    }

    public void init(Connector connector) {
        super.init(connector);
        MapActivityState.getMapActivityState().addListener(LISTENER_ID, this);
        // 移动到用户位置
        String provider = null;
        LocationManager locationManager = (LocationManager) connector.getContext()
                .getSystemService(Context.LOCATION_SERVICE);
        //获取当前可用的位置控制器
        List<String> list = locationManager.getProviders(true);
        if (list.contains(LocationManager.GPS_PROVIDER)) {
            // GPS位置控制器
            provider = LocationManager.GPS_PROVIDER;
        } else if (list.contains(LocationManager.NETWORK_PROVIDER)) {
            // 网络位置控制器
            provider = LocationManager.NETWORK_PROVIDER;
        }
        // 将地图移动到上次的位置
        if (provider != null) {
            if (ActivityCompat.checkSelfPermission(connector.getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(connector.getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            Location lastKnownLocation = locationManager.getLastKnownLocation(provider);
            if (lastKnownLocation != null) {
                moveToPoint(lastKnownLocation.getLongitude(), lastKnownLocation.getLatitude());
            } else {
                moveToRememberLocation();
            }
        } else {
            moveToRememberLocation();
        }
    }

    private void moveToRememberLocation() {
        AccessParameter parameter = new AccessParameter(connector.getContext(), "lastLocation");
        int lng = parameter.readParameters("wgs84Lng");
        int lat = parameter.readParameters("wgs84Lat");
        if (lng != 0xFFFFFF && lat != 0xFFFFFF) {
            moveToPoint(((float) lng) / 10000, ((float) lat) / 10000);
        }
    }

    public void onPause() {
        AccessParameter parameter = new AccessParameter(connector.getContext(), "lastLocation");
        CameraPosition position = aMap.getCameraPosition();
        double longitude = position.target.longitude;
        double latitude = position.target.latitude;
        // 使用自定义函数将坐标转换为WGS84
        double[] wgs84LatLng = CoordinateTransformUtil.gcj02ToWgs84(longitude, latitude);
        parameter.storageParameters("wgs84Lng", (int) (wgs84LatLng[0] * 10000));
        parameter.storageParameters("wgs84Lat", (int) (wgs84LatLng[1] * 10000));
    }

    /**
     * activity重新加载时调用此方法
     */
    public void onResume() {
        reloadPoints();
    }

    private void reloadPoints() {
        // 清除地图上的所有点，重新绘制
        markerList.clear();
        aMap.clear(true);
        for (int i = 0; i < pointManager.getPointNum(); i++) {
            addPointToMap(pointManager.getPoint(i), i + 1);
        }
    }

    /**
     * 将地图视图转移至指定位置
     *
     * @param wgs84Lat 纬度
     * @param wgs84Lng 经度
     */
    public void moveToPoint(double wgs84Lng, double wgs84Lat) {
        LatLng wgs84LatLng = new LatLng(wgs84Lat, wgs84Lng);
        // 用官方函数将WGS84转换回GCJ02
        CoordinateConverter converter = new CoordinateConverter(connector.getContext());
        converter.from(CoordinateConverter.CoordType.GPS);
        converter.coord(wgs84LatLng);
        LatLng gcjLatLng = converter.convert();
        aMap.animateCamera(CameraUpdateFactory.changeLatLng(gcjLatLng));
    }

    /**
     * 显示选点信息
     *
     * @param position 点的序号
     */
    public void showPointInfoWindow(int position) {
        for (int i = 0; i < markerList.size(); i++) {
            Marker marker = markerList.get(i);
            marker.hideInfoWindow();
        }
        Marker marker = markerList.get(position);
        marker.showInfoWindow();
        // 显示10秒后隐藏
        Thread thread = new Thread(() -> {
            try {
                Thread.sleep(1000 * 10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            marker.hideInfoWindow();
        });
        thread.start();
    }

    /**
     * 开始选点
     */
    private void startSelectPoint() {
        // 设置地图选点监听器
        aMap.addOnMapClickListener(clickListener);
    }

    /**
     * 停止选点
     */
    private void stopSelectPoint() {
        aMap.removeOnMapClickListener(clickListener);
    }

    /**
     * 清除选点
     */
    private void clearPoint() {
        // 清空所有选点
        pointManager.deleteAll();
        markerList.clear();
        aMap.clear(true);
    }

    private void selectPoint(LatLng latLng) {
        // 使用自定义函数将坐标转换为WGS84
        double[] wgs84LatLng = CoordinateTransformUtil.gcj02ToWgs84(latLng.longitude, latLng.latitude);
        // 设置选点对话框
        View dialog = View.inflate(connector.getContext(), R.layout.dialog_set_point, null);
        final EditText e1 = dialog.findViewById(R.id.e1);
        final EditText e2 = dialog.findViewById(R.id.e2);
        AlertDialog.Builder builder = new AlertDialog.Builder(connector.getContext());
        builder.setTitle(R.string.pointSettingBoxTitle);
        builder.setCancelable(false);
        builder.setView(dialog);
        builder.setPositiveButton("确定", (dialogInterface, i) -> {
            // 如果视图已经更改，直接返回
            if (connector.getCurrentTabView() != TabManager.TabState.VIEW_SELECT) {
                return;
            }
            boolean isValueSet = true;
            String toastWord = "";
            String height = e1.getText().toString();
            String time = e2.getText().toString();
            // 进行正则表达检查输入
            if (!height.matches("^\\d+$")) {
                isValueSet = false;
                toastWord = "高度";
                height = "20";
            }
            if (!time.matches("^\\d+$")) {
                isValueSet = false;
                if (!toastWord.equals("")) {
                    toastWord = toastWord + "和";
                }
                toastWord = toastWord + "悬停时间";
                time = "10";
            }
            if (!isValueSet) {
                Toast.makeText(connector.getContext(), "您未输入" + toastWord + "，已设为默认值", Toast.LENGTH_LONG).show();
            }
            // 将选点添加至管理对象
            pointManager.addPoint(wgs84LatLng[0], wgs84LatLng[1], Integer.parseInt(height),
                    Integer.parseInt(time));
            // 将点添加至地图
            addPointToMap(pointManager.getPoint(pointManager.getPointNum() - 1), pointManager.getPointNum());
            // 更新listview
            connector.refreshList();
        });
        builder.setNegativeButton("取消", null);
        // 显示设置弹窗
        builder.show();
    }

    public void modifyPoint(int position) {
        NavPoint point = pointManager.getPoint(position);
        // 初始化对话框
        View dialog = View.inflate(connector.getContext(), R.layout.dialog_modify_point, null);
        final EditText m1 = dialog.findViewById(R.id.m1);
        final EditText m2 = dialog.findViewById(R.id.m2);
        final EditText m3 = dialog.findViewById(R.id.m3);
        final EditText m4 = dialog.findViewById(R.id.m4);
        // 设置初始值
        m1.setText(String.valueOf(point.getLng()));
        m2.setText(String.valueOf(point.getLat()));
        m3.setText(String.valueOf(point.getHeight()));
        m4.setText(String.valueOf(point.getStayTime()));
        // 设置对话框
        AlertDialog.Builder builder = new AlertDialog.Builder(connector.getContext());
        builder.setTitle(R.string.pointSettingBoxTitle);
        builder.setCancelable(false);
        builder.setView(dialog);
        builder.setPositiveButton("确定", (dialogInterface, i) -> {
            // 如果视图已经更改，直接返回
            if (connector.getCurrentTabView() != TabManager.TabState.VIEW_SELECT) {
                return;
            }
            // 获得更改后的值
            String lng = m1.getText().toString();
            String lat = m2.getText().toString();
            String height = m3.getText().toString();
            String time = m4.getText().toString();
            // 检查输入合法性
            if (!lng.matches("^\\d+\\.\\d+$")) {
                Toast.makeText(connector.getContext(), "输入格式错误，修改已取消", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!lat.matches("^\\d+\\.\\d+$")) {
                Toast.makeText(connector.getContext(), "输入格式错误，修改已取消", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!height.matches("^\\d+$")) {
                Toast.makeText(connector.getContext(), "输入格式错误，修改已取消", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!height.matches("^\\d+$")) {
                Toast.makeText(connector.getContext(), "输入格式错误，修改已取消", Toast.LENGTH_SHORT).show();
                return;
            }
            point.setLng(Double.parseDouble(lng));
            point.setLat(Double.parseDouble(lat));
            point.setHeight(Integer.parseInt(height));
            point.setStayTime(Integer.parseInt(time));
            // 刷新列表
            connector.refreshList();
            // 刷新地图点信息
            reloadPoints();
        });
        builder.setNegativeButton("取消", null);
        // 显示设置弹窗
        builder.show();
    }

    public void deletePoint(int position) {
        pointManager.deletePoint(position);
        // 刷新列表
        connector.refreshList();
        // 刷新地图点信息
        reloadPoints();
    }

    private void addPointToMap(NavPoint point, int index) {
        LatLng new84LatLng = new LatLng(point.getLat(), point.getLng());
        // 用官方函数将WGS84转换回GCJ02
        CoordinateConverter converter = new CoordinateConverter(connector.getContext());
        converter.from(CoordinateConverter.CoordType.GPS);
        converter.coord(new84LatLng);
        LatLng gcjLatLng = converter.convert();
        // 将转换回去的坐标显示在地图上
        MarkerOptions options = new MarkerOptions();
        options.position(gcjLatLng);
        options.title("航点" + index);
        options.snippet(point.toString());
        // 更新地图，将点添加至列表
        Marker marker = aMap.addMarker(options);
        marker.setClickable(false);
        markerList.add(marker);
        // 移除之前的连线
        polyline.remove();
        // 重新设置连线
        List<LatLng> latLngs = new ArrayList<>();
        for (int i = 0; i < pointManager.getPointNum(); i++) {
            NavPoint eachPoint = pointManager.getPoint(i);
            CoordinateConverter eachConverter = new CoordinateConverter(connector.getContext());
            eachConverter.from(CoordinateConverter.CoordType.GPS);
            eachConverter.coord(new LatLng(eachPoint.getLat(), eachPoint.getLng()));
            latLngs.add(eachConverter.convert());
        }
        // 添加连线
        PolylineOptions polylineOptions = new PolylineOptions();
        polylineOptions.width(10);
        polylineOptions.color(Color.parseColor("#555555"));
        polylineOptions.addAll(latLngs);
        polyline = aMap.addPolyline(polylineOptions);
    }

    @Override
    public void onStateChange(MapActivityState mapActivityState) {
        // 开始/关闭选点
        if (mapActivityState.mapViewState.isCanBeSelect) {
            startSelectPoint();
        } else {
            stopSelectPoint();
        }
        if (!mapActivityState.waitViewState.isPointSelected) {
            // 如果没有选点，清除地图上的点
            clearPoint();
        }
        // 如果选点列表和地图上的点不一致，重新加载选点
        if (pointManager.getPointNum() != markerList.size()) {
            // 清除地图上的所有点，重新绘制
            reloadPoints();
        }
    }
}
