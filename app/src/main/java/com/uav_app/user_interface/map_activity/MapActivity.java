package com.uav_app.user_interface.map_activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.amap.api.maps.MapView;
import com.amap.api.maps.MapsInitializer;
import com.uav_app.uav_manager.R;
import com.uav_app.usb_manager.UsbConnectManager;
import com.uav_app.user_interface.map_activity.managers.Connector;
import com.uav_app.user_interface.map_activity.managers.MapManager;
import com.uav_app.user_interface.map_activity.managers.TabManager;

public class MapActivity extends AppCompatActivity {
    // 地图view
    private MapView mMapView = null;
    // 地图管理组件
    public MapManager mapManager;
    // 面板管理组件
    public TabManager tabManager;
    // 权限申请返回代码
    private static final int OPEN_SET_REQUEST_CODE = 100;
    // 权限数组（申请定位）
    private final String[] permissions = new String[]{
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_LOCATION_EXTRA_COMMANDS
    };

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        // 初始化定位权限
        initPermissions();
        // 声明隐私合规接口
        MapsInitializer.updatePrivacyShow(this, true, true);
        MapsInitializer.updatePrivacyAgree(this, true);
        // 在activity执行onCreate时执行mMapView.onCreate(savedInstanceState)，创建地图
        mMapView = findViewById(R.id.map);
        mMapView.onCreate(savedInstanceState);
        // 设置状态栏
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        // 获取状态栏高度
        int statusBarHeight = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            statusBarHeight = getResources().getDimensionPixelSize(resourceId);
        }
        int finalStatusBarHeight = statusBarHeight;
        // 定义中介器对象
        Connector mediator = new Connector(this);
        // 初始化管理类
        mapManager = new MapManager(mMapView, mediator);
        tabManager = new TabManager(mediator);
        mMapView.post(() -> {
            tabManager.initTab(mMapView.getHeight(), finalStatusBarHeight);
            mapManager.initMap();
            MapActivityState.getMapActivityState().applyChange();
        });
        // 初始化中介器
        mediator.initConnector(mapManager, tabManager);
        // 尝试连接数传
        UsbConnectManager.getConnectManager().connectDevice();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 在activity执行onDestroy时执行mMapView.onDestroy()，销毁地图
        mMapView.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 在activity执行onResume时执行mMapView.onResume ()，重新绘制加载地图
        mMapView.onResume();
        mapManager.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // 在activity执行onPause时执行mMapView.onPause ()，暂停地图的绘制
        mMapView.onPause();
        mapManager.onPause();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        // 在activity执行onSaveInstanceState时执行mMapView.onSaveInstanceState (outState)，保存地图当前的状态
        mMapView.onSaveInstanceState(outState);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // 检查权限申请返回代码
        if (requestCode == OPEN_SET_REQUEST_CODE) {
            if (grantResults.length > 0) {
                // 检查权限是否授予完整
                for (int grantResult : grantResults) {
                    if (grantResult != PackageManager.PERMISSION_GRANTED) {
                        // 未授予权限
                        Toast.makeText(this, "未获得定位权限，无法获取您当前的位置", Toast.LENGTH_LONG).show();
                        return;
                    }
                }
            } else {
                // 未授予权限
                Toast.makeText(this, "未获得定位权限，无法获取您当前的位置", Toast.LENGTH_LONG).show();
            }
        }
    }

    /**
     * 初始化权限
     */
    private void initPermissions() {
        for (String permission : permissions) {
            // 判断是否缺少权限，true=缺少权限
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                // 请求权限，第二参数权限String数据，第三个参数是请求码便于在onRequestPermissionsResult 方法中根据code进行判断
                ActivityCompat.requestPermissions(this, permissions, OPEN_SET_REQUEST_CODE);
                return;
            }
        }
    }
}
