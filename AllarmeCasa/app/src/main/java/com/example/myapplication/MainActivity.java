package com.example.myapplication;



import android.os.Bundle;

import com.example.myapplication.ui.battery.BatteryFragment;
import com.example.myapplication.ui.home.HomeFragment;
import com.example.myapplication.ui.log.LogFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.myapplication.databinding.ActivityMainBinding;
import com.google.android.material.navigation.NavigationBarView;


//import org.eclipse.paho.android.service.MqttAndroidClient;
import info.mqtt.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View;

public class MainActivity extends AppCompatActivity {
    MqttAndroidClient  client;
    private ActivityMainBinding binding;
    private String fragment_vis = "Home";
    private BottomNavigationView navView;
    TextView _textView_error_conn;
    ImageView _imageView_error_conn;
    ScrollView _scrollView_main;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        _textView_error_conn=findViewById(R.id.textView_error_conn);
        _imageView_error_conn= findViewById(R.id.imageView_error_conn);
        _scrollView_main = findViewById(R.id.scrollView_main);
        navView= findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_battery, R.id.navigation_log, R.id.navigation_siren)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        //NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);
        navView.getMenu().getItem(0).setEnabled(false);
        navView.getMenu().getItem(1).setEnabled(false);
        navView.getMenu().getItem(2).setEnabled(false);
        navView.getMenu().getItem(3).setEnabled(false);
        navView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener(){
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch(item.toString()) {
                    case ("Siren"):
                        if (fragment_vis != "Home")
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
        String clientId = MqttClient.generateClientId();
        try {
            client = new MqttAndroidClient(this.getApplicationContext(), "tcp://casabrugne.ddns.net:1883", clientId);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        try {
            IMqttToken token = client.connect();
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    _scrollView_main.setVisibility(View.VISIBLE);
                    navView.getMenu().getItem(0).setEnabled(true);
                    navView.getMenu().getItem(1).setEnabled(true);
                    navView.getMenu().getItem(2).setEnabled(true);
                    navView.getMenu().getItem(3).setEnabled(true);
                    //Toast.makeText(MainActivity.this,"connected!!",Toast.LENGTH_SHORT).show();
                    setSubscription();
                    //_stato_txt.setText(" SISTEMA ONLINE");

                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    //Toast.makeText(MainActivity.this,"connection failed!!",Toast.LENGTH_SHORT).show();
                   // _stato_txt.setText(" SISTEMA OFFLINE");
                    _textView_error_conn.setVisibility(View.VISIBLE);
                    _imageView_error_conn.setVisibility(View.VISIBLE);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        client.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {

            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                Toast.makeText(MainActivity.this,"messaggio arrivatooo!",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        });

    }

    private void setSubscription(){
        try{
            client.subscribe("casabrugne_allarme",0);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void published(View v){

        String topic = "event";
        String message = "the payload";
        try {
            client.publish(topic, message.getBytes(),0,false);
            Toast.makeText(this,"Published Message",Toast.LENGTH_SHORT).show();
        } catch ( Exception e) {
            e.printStackTrace();
        }
    }

}