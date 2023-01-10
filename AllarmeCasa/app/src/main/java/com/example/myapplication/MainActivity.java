package com.example.myapplication;



import android.os.Bundle;

import com.example.myapplication.ui.battery.BatteryFragment;
import com.example.myapplication.ui.home.HomeFragment;
import com.example.myapplication.ui.log.LogFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.myapplication.databinding.ActivityMainBinding;
import com.google.android.material.navigation.NavigationBarView;


import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;



public class MainActivity extends AppCompatActivity implements mqtt_service.MyListener {

    static mqtt_service _mqtt_service = new mqtt_service();

    ScrollView _scrollView_main;
    private ActivityMainBinding binding;
    private String fragment_vis = "Home";
    private BottomNavigationView navView;
    private int mInterval = 100; // 5 seconds by default, can be changed later
    private Handler mHandler;

    public static mqtt_service get_mqtt_service() {
        return _mqtt_service;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        mHandler = new Handler();
        startRepeatingTask();
       // _mqtt_service.addListener(this);
       //binding = ActivityMainBinding.inflate(getLayoutInflater());
       setContentView(R.layout.activity_main);
        _scrollView_main = findViewById(R.id.scrollView_main);



        navView= findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_battery, R.id.navigation_log, R.id.navigation_siren)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        //NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
     ///   NavigationUI.setupWithNavController(binding.navView, navController);
        navView.getMenu().getItem(0).setEnabled(false);
        navView.getMenu().getItem(1).setEnabled(false);
        navView.getMenu().getItem(2).setEnabled(false);
        navView.getMenu().getItem(3).setEnabled(false);
        navView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener(){
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch(item.toString()) {

                    case ("Siren"):
                            navView.setSelectedItemId(R.id.navigation_home);
                            getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment_activity_main, new HomeFragment()).commit(); //this contains recycleview
                        break;
                    case ("Logs"):
                        fragment_vis = ("Log");
                        getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment_activity_main, new LogFragment()).commit(); //this contains recycleview
                        break;
                    case ("Battery"):
                        fragment_vis = ("Battery");
                        getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment_activity_main, new BatteryFragment()).commit(); //this contains recycleview
                        break;
                    case ("Home"):
                            fragment_vis = ("Home");
                            getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment_activity_main, new HomeFragment()).commit(); //this contains recycleview

                        break;
                }

                return true;
            }
        });

    }
    @Override
    public void onResume(){
        super.onResume();

        _mqtt_service.init_mqtt(this);



    }

    @Override
    public void onPause() {
        super.onPause();
        //stopRepeatingTask();
        //_mqtt_service.stop_mqtt();
    }


    @Override
    public void onStop() {
        super.onStop();
        stopRepeatingTask();
        _mqtt_service.stop_mqtt();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopRepeatingTask();
        _mqtt_service.stop_mqtt();
    }

    void startRepeatingTask() {
        mStatusChecker.run();
    }

    void stopRepeatingTask() {
        mHandler.removeCallbacks(mStatusChecker);
    }

    private Runnable mStatusChecker = new Runnable() {
        @Override
        public void run() {
            try {
                if (_mqtt_service.get_ret_val() == true)
                {

                    _scrollView_main.setVisibility(View.VISIBLE);
                    navView.getMenu().getItem(0).setEnabled(true);
                    navView.getMenu().getItem(1).setEnabled(true);
                    navView.getMenu().getItem(2).setEnabled(true);
                    navView.getMenu().getItem(3).setEnabled(true);
                    navView.setSelectedItemId(R.id.navigation_home);

                    mHandler.removeCallbacks(mStatusChecker);
                }
            } finally {
                // 100% guarantee that this always happens, even if
                // your update method throws an exception
                {
                    if (_mqtt_service.get_ret_val() == false)
                        mHandler.postDelayed(mStatusChecker, mInterval);

                }
            }
        }
    };



    @Override
    public void _messageArrived(String message) {

    }



}