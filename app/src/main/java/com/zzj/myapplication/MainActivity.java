package com.zzj.myapplication;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

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
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        
        // 处理系统窗口边距，确保内容不被系统栏遮挡
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0); // 底部内边距由 BottomNav 处理
            return insets;
        });

        // 初始化底部导航栏
        BottomNavigationView navView = findViewById(R.id.nav_view);
        navView.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int itemId = item.getItemId();
            
            // 根据点击的菜单项切换 Fragment
            if (itemId == R.id.navigation_dashboard) {
                selectedFragment = new DashboardFragment();
            } else if (itemId == R.id.navigation_accounting) {
                selectedFragment = new RecordListFragment();
            } else if (itemId == R.id.navigation_items) {
                selectedFragment = new ItemListFragment();
            }

            // 替换当前的 Fragment
            if (selectedFragment != null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.nav_host_fragment_activity_main, selectedFragment)
                        .commit();
                return true;
            }
            return false;
        });

        // 默认选中首页 (Dashboard)
        if (savedInstanceState == null) {
            navView.setSelectedItemId(R.id.navigation_dashboard);
        }
    }
}