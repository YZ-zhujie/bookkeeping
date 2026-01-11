package com.zzj.myapplication;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;


/**
 * 主应用程序入口 Activity
 * 负责初始化底部导航栏 (BottomNavigationView) 并管理 Fragment 的切换
 */
public class MainActivity extends AppCompatActivity {

    /**
     * Activity 创建时的回调
     * 初始化布局和各个视图组件
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        让内容可以显示到状态栏/导航栏后面
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        
        // 处理系统窗口边距，确保内容不被系统栏遮挡
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0); // 底部内边距由 BottomNav 处理
            return insets;
        });

        // 初始化底部导航栏 (自定义实现)
        findViewById(R.id.nav_dashboard).setOnClickListener(v -> switchFragment(new DashboardFragment()));
        findViewById(R.id.nav_accounting).setOnClickListener(v -> switchFragment(new RecordListFragment()));
        findViewById(R.id.nav_items).setOnClickListener(v -> switchFragment(new ItemListFragment()));

        // 默认选中首页 (Dashboard)
        if (savedInstanceState == null) {
            switchFragment(new DashboardFragment());
        }
    }

    // 替换内容区的内容
    private void switchFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.nav_host_fragment_activity_main, fragment)
                .commit();
    }
}