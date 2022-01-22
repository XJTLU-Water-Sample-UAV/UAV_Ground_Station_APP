package com.uav_app.front_end.map_activity.child_view.tab_child;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.uav_app.back_end.uav_manager.R;
import com.uav_app.back_end.uav_manager.coordinator.NavCoordManager;
import com.uav_app.back_end.usb_manager.UsbConnectManager;
import com.uav_app.front_end.OperationStateMachine;
import com.uav_app.front_end.map_activity.MapActivityState;
import com.uav_app.front_end.map_activity.child_view.point_list.SwipeView;
import com.uav_app.front_end.map_activity.managers.TabManager;

import java.util.ArrayList;

@SuppressLint("ViewConstructor")
public class SelectView extends ChildView {
    private final Button showButton;
    private final ListView mListView;
    private final PointListAdapter mAdapter;

    public SelectView(Context context, TabManager tabManager) {
        super(context, tabManager);
        LayoutInflater.from(context).inflate(R.layout.mode_select, this);
        this.tabManager = tabManager;
        this.showButton = findViewById(R.id.showButton);
        Button cancelButton = findViewById(R.id.cancelButton);
        Button sendButton = findViewById(R.id.sendButton);
        showButton.setOnClickListener(v -> {
            if (tabManager.isTabShow()) {
                tabManager.closeTab();
            } else {
                tabManager.openTab();
            }
        });
        cancelButton.setOnClickListener(v -> OperationStateMachine.getOperationStateMachine()
                .nextState(OperationStateMachine.SwitchCondition.CONDITION_ON_CLICK_CANCEL));
        sendButton.setOnClickListener(v -> {
            // 判断是否没有选点
            if (MapActivityState.getMapActivityState().pointManager.getPointNum() == 0) {
                Toast.makeText(context, "请选择至少一个航点", Toast.LENGTH_SHORT).show();
                return;
            }
            if (UsbConnectManager.getConnectManager().isConnect()) {
                // UavStateManager.getUavStateManager().connectUav();
                Toast.makeText(context, "发送成功", Toast.LENGTH_SHORT).show();
            }
            OperationStateMachine.getOperationStateMachine().nextState(OperationStateMachine
                    .SwitchCondition.CONDITION_ON_CLICK_CONFIRM);
        });
        // 设置选点列表适配器
        mAdapter = new PointListAdapter();
        mListView = findViewById(R.id.pointList);
        mListView.setAdapter(mAdapter);
    }

    private void listDeleteAnim(int position) {
        int first = mListView.getFirstVisiblePosition();
        // 存储所有的Animator，利用AnimatorSet直接播放
        ArrayList<Animator> animators = new ArrayList<>();
        // 获得要删除的View
        View itemToDelete = mListView.getChildAt(position - first);
        // 获取View的尺寸
        int viewHeight = itemToDelete.getHeight();
        int dividerHeight = mListView.getDividerHeight();
        ObjectAnimator hideAnimator = ObjectAnimator.ofFloat(itemToDelete, "alpha", 1f, 0f);
        animators.add(hideAnimator);
        int delay = 0;
        int firstViewToMove = position + 1;
        // 设置每一个view的动画
        for (int i = firstViewToMove; i < mListView.getChildCount(); ++i) {
            View viewToMove = mListView.getChildAt(i);
            ObjectAnimator moveAnimator = ObjectAnimator.ofFloat(viewToMove, "translationY",
                    0, -dividerHeight - viewHeight);
            moveAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
            moveAnimator.setStartDelay(delay);
            delay += 100;
            animators.add(moveAnimator);
        }
        // 设置动画集
        AnimatorSet set = new AnimatorSet();
        set.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                tabManager.getConnector().deletePoint(position);
                // 动画结束后，恢复ListView所有子View的属性
                for (int i = 0; i < mListView.getChildCount(); ++i) {
                    View view = mListView.getChildAt(i);
                    view.setAlpha(1f);
                    view.setTranslationY(0);
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });
        set.playTogether(animators);
        set.start();
    }

    public void refreshList() {
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onHide() {
        refreshList();
    }

    @Override
    public void onUp() {
        showButton.setText("关闭航点");
    }

    @Override
    public void onDown() {
        showButton.setText("展开航点");
    }

    private class PointListAdapter extends BaseAdapter {
        // 选点管理对象
        private final NavCoordManager pointManager;

        public PointListAdapter() {
            pointManager = MapActivityState.getMapActivityState().getPointManager();
        }

        @Override
        public int getCount() {
            return pointManager.getPointNum();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @SuppressLint({"SetTextI18n", "ViewHolder", "InflateParams"})
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // 加载布局
            LayoutInflater factory = LayoutInflater.from(tabManager.getConnector().getContext());
            View outView = factory.inflate(R.layout.listview_point, null);
            SwipeView view = outView.findViewById(R.id.aPoint);
            view.setText(pointManager.getPointDescription(position));
            // 设置点击事件监听器
            view.setTextOnClickListener(v -> {
                tabManager.closeTab();
                tabManager.getConnector().moveToPoint(pointManager.getLng(position), pointManager.getLat(position));
                tabManager.getConnector().showPointInfoWindow(position);
            });
            view.setModifyOnClickListener(v -> tabManager.getConnector().modifyPoint(position));
            view.setDelOnClickListener(v -> listDeleteAnim(position));
            view.setOnSlidingListener(new SwipeView.OnSlidingListener() {
                @Override
                public void onSliding() {
                    // 暂时禁用listview
                    mListView.setEnabled(false);
                    // 获取当前展示的子view序号
                    int first = mListView.getFirstVisiblePosition();
                    int last = mListView.getLastVisiblePosition();
                    // 逐个检查是否关闭
                    for (int i = first; i <= last; i++) {
                        // 判断是否是当前正在滑动的界面
                        if (i != position) {
                            SwipeView each = mListView.getChildAt(i - first).findViewById(R.id.aPoint);
                            each.closeMenus();
                        }
                    }
                }

                @Override
                public void onEndSlide() {
                    mListView.setEnabled(true);
                }
            });
            return outView;
        }
    }
}
