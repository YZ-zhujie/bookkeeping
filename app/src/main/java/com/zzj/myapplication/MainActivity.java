package com.zzj.myapplication;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.navigation.NavController;
import androidx.navigation.NavController;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;

/**
 * 主应用程序入口 Activity
 * 负责初始化底部导航栏 (BottomNavigationView) 并管理 Fragment 的切换 (使用 Navigation 组件)
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        
        // 处理系统窗口边距
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0); 
            return insets;
        });

        // 初始化 Navigation
        // 初始化 Navigation
        BottomNavigationView navView = findViewById(R.id.nav_view);

        // 获取 NavHostFragment
        androidx.navigation.fragment.NavHostFragment navHostFragment = 
            (androidx.navigation.fragment.NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment_activity_main);
        
        if (navHostFragment != null) {
            NavController navController = navHostFragment.getNavController();
            // 配置底部导航栏与 NavController 联动
            NavigationUI.setupWithNavController(navView, navController);
        }
    }
}