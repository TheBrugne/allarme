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



public class MainActivity extends AppCompatActivity {

    MqttAndroidClient  client;
    private ActivityMainBinding binding;
    private String fragment_vis = "Home";
    private BottomNavigationView navView;
    TextView _textView_error_conn;
    ImageView _imageView_error_conn;
    ScrollView _scrollView_main;
    CheckBox _checkBox_test;
    SwitchCompat _switch_garage;
    SwitchCompat _switch_marino;
    SwitchCompat _switch_chiara;
    SwitchCompat _switch_ada;
    Button _button_allarm;
    ImageView _imageView_stato_sistema;
    TextView  _textView_stato_sistema;

    /* variabili per lo stato */
    static final String topic = "casabrugne_allarme";
    static final int N_STATI_ALLARME = 6;
    static final int TEST_POS = 0;
    static final int ARMATO_POS = 1;
    static final int GARAGE_POS = 2;
    static final int MARINO_POS = 3;
    static final int CHIARA_POS = 4;
    static final int ADA_POS = 5;
    private boolean stato_allarme[]= new boolean[N_STATI_ALLARME];
    private boolean init_eseguito=false;
    private boolean disable_switch=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        Arrays.fill(stato_allarme, Boolean.FALSE);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        _textView_error_conn = findViewById(R.id.textView_error_conn);
        _imageView_error_conn = findViewById(R.id.imageView_error_conn);
        _scrollView_main = findViewById(R.id.scrollView_main);
        _checkBox_test = findViewById(R.id.checkBox_test);
        _switch_garage = findViewById(R.id.switch_garage);
        _switch_marino = findViewById(R.id.switch_marino);
        _switch_chiara = findViewById(R.id.switch_chiara);
        _switch_ada = findViewById(R.id.switch_ada);
        _button_allarm = findViewById(R.id.button_allarm);
        _imageView_stato_sistema = findViewById(R.id.imageView_stato_sistema);
        _textView_stato_sistema = findViewById(R.id.textView_stato_sistema);

        _button_allarm.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (init_eseguito == true) {
                    String status_pos = "0";
                    if (stato_allarme[TEST_POS] == true) {
                        status_pos = "1";
                    }

                    if (stato_allarme[ARMATO_POS] == false) {
                        stato_allarme[ARMATO_POS] = true;
                        publish_message("SET_ALLARME;test;" + status_pos + ";armato;1;garage;1;marino;1;chiara;1;ada;1");

                    } else {
                        stato_allarme[ARMATO_POS] = false;
                        publish_message("SET_ALLARME;test;" + status_pos + ";armato;0;garage;0;marino;0;chiara;0;ada;0");
                    }
                    publish_message("?STATO_ALLARME");
                }
            }
        });

        _switch_garage.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean status) {
                {
                    if (disable_switch==false)
                    {
                        if(status == true)
                        {
                            stato_allarme[GARAGE_POS]=true;
                        }
                        else
                        {
                            stato_allarme[GARAGE_POS]=false;
                        }
                        publish_message("SET_ALLARME;test;"+convert_tostring01(stato_allarme[TEST_POS])+";ARMATO;"
                                +convert_tostring01(stato_allarme[ARMATO_POS])+";GARAGE;"
                                +convert_tostring01(stato_allarme[GARAGE_POS])+";MARINO;"
                                +convert_tostring01(stato_allarme[MARINO_POS])+";CHIARA;"
                                +convert_tostring01(stato_allarme[CHIARA_POS])+";ADA;"
                                +convert_tostring01(stato_allarme[ADA_POS]));
                        publish_message("?STATO_ALLARME");
                    }

                }
            }
        });

        _switch_marino.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean status) {
                {
                    if (disable_switch==false)
                    {
                        if(status == true)
                        {
                            stato_allarme[MARINO_POS]=true;
                        }
                        else
                        {
                            stato_allarme[MARINO_POS]=false;
                        }
                        publish_message("SET_ALLARME;test;"+convert_tostring01(stato_allarme[TEST_POS])+";ARMATO;"
                                +convert_tostring01(stato_allarme[ARMATO_POS])+";GARAGE;"
                                +convert_tostring01(stato_allarme[GARAGE_POS])+";MARINO;"
                                +convert_tostring01(stato_allarme[MARINO_POS])+";CHIARA;"
                                +convert_tostring01(stato_allarme[CHIARA_POS])+";ADA;"
                                +convert_tostring01(stato_allarme[ADA_POS]));
                        publish_message("?STATO_ALLARME");
                    }

                }
            }
        });

        _switch_chiara.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean status) {
                {
                    if (disable_switch == false) {
                        if (status == true) {
                            stato_allarme[CHIARA_POS] = true;
                        } else {
                            stato_allarme[CHIARA_POS] = false;
                        }
                        publish_message("SET_ALLARME;test;" + convert_tostring01(stato_allarme[TEST_POS]) + ";ARMATO;"
                                + convert_tostring01(stato_allarme[ARMATO_POS]) + ";GARAGE;"
                                + convert_tostring01(stato_allarme[GARAGE_POS]) + ";MARINO;"
                                + convert_tostring01(stato_allarme[MARINO_POS]) + ";CHIARA;"
                                + convert_tostring01(stato_allarme[CHIARA_POS]) + ";ADA;"
                                + convert_tostring01(stato_allarme[ADA_POS]));
                        publish_message("?STATO_ALLARME");
                    }
                }
            }
        });

        _switch_ada.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean status) {
                {
                    if (disable_switch == false) {
                        if (status == true) {
                            stato_allarme[ADA_POS] = true;
                        } else {
                            stato_allarme[ADA_POS] = false;
                        }
                        publish_message("SET_ALLARME;test;" + convert_tostring01(stato_allarme[TEST_POS]) + ";ARMATO;"
                                + convert_tostring01(stato_allarme[ARMATO_POS]) + ";GARAGE;"
                                + convert_tostring01(stato_allarme[GARAGE_POS]) + ";MARINO;"
                                + convert_tostring01(stato_allarme[MARINO_POS]) + ";CHIARA;"
                                + convert_tostring01(stato_allarme[CHIARA_POS]) + ";ADA;"
                                + convert_tostring01(stato_allarme[ADA_POS]));
                        publish_message("?STATO_ALLARME");
                    }
                }
            }
        });

        _checkBox_test.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean status) {
                if (status == false)
                {
                    publish_message("SET_ALLARME_TEST;test;0");
                }
                else
                {
                    publish_message("SET_ALLARME_TEST;test;1");
                }
                publish_message("?STATO_ALLARME");
            }

        });
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

                    publish_message("?STATO_ALLARME");
                    publish_message("?ALLARME_ATTIVO");
                    publish_message("?TEMPERATURA_RASPY");

                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
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
                decode_message(message.toString());

            }
            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }

        });


    }

    @Override
    public void onPause() {
        super.onPause();
        try {
            IMqttToken token = client.disconnect();
        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }


    @Override
    public void onStop() {
        super.onStop();
        try {
            IMqttToken token = client.disconnect();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setSubscription(){
        try{
            client.subscribe(topic,0);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private byte[] get_byte_array(String msg)
    {
        return msg.getBytes();
    }

    public void publish_message(String msg)
    {
        if(msg.equals("?STATO_ALLARME"))
        {
            disable_switch=true;
        }
        client.publish(topic,get_byte_array(msg),0,false);
    }
    public void decode_message(String msg)
    {
        String[] msg_split = msg.toString().split(";");
        switch(msg_split[0])
        {
            case("!STATO_ALLARME"):
                stato_allarme[ARMATO_POS]= false;
                set_checkbox(_checkBox_test,msg_split[1].split(" ")[1], TEST_POS);
                set_switchcompact(_switch_garage,msg_split[3].split(" ")[1], GARAGE_POS);
                set_switchcompact(_switch_marino,msg_split[4].split(" ")[1],MARINO_POS);
                set_switchcompact(_switch_chiara,msg_split[5].split(" ")[1],CHIARA_POS);
                set_switchcompact(_switch_ada,msg_split[6].split(" ")[1],ADA_POS);
                set_btn_text(_button_allarm,stato_allarme[ARMATO_POS]);
                init_eseguito = true;
                disable_switch = false;
                break;
            case("!ALLARME_ATTIVO"):
            {
                if (msg_split[1].equals("nessuno") == false) {
                    set_image_background(_imageView_stato_sistema, false, true);
                    set_textview_text(_textView_stato_sistema, false, true, msg_split[1]);
                }
            }

        }
    }

    public void set_checkbox(CheckBox checkbox, String stato, int pos)
    {
        if (stato.equals("0"))
        {
            checkbox.setChecked(false);
            stato_allarme[pos]= false;
        }
        else
        {
            checkbox.setChecked(true);
            stato_allarme[pos]= true;
        }

    }

    public void set_switchcompact(SwitchCompat switchcompact, String stato, int pos)
    {
        if (stato.equals("0"))
        {
            switchcompact.setChecked(false);
            stato_allarme[pos]= false;
        }
        else
        {
            switchcompact.setChecked(true);
            stato_allarme[pos]= true;
            stato_allarme[ARMATO_POS]= true;
        }

    }

    public void set_btn_text(Button button,boolean status)
    {
        if (status == false)
        {
            button.setText("ATTIVA ALLARMI");
            button.setBackgroundResource(R.drawable.custom_button);


        }
        else
        {
            button.setText("DISATTIVA ALLARMI");
            button.setBackgroundResource(R.drawable.custom_button_dark);

        }
        set_image_background(_imageView_stato_sistema,status,false);
        set_textview_text(_textView_stato_sistema,status,false, "");

    }

    public void set_image_background(ImageView image,boolean status, boolean allarme_attivo)
    {
        if (allarme_attivo == false)
        {
            if (status == false) {
                image.setImageResource(R.drawable.unlocked);
            } else {
                image.setImageResource(R.drawable.locked);
            }
        }
        else
        {
            image.setImageResource(R.drawable.ladro);
        }

    }

    public void set_textview_text(TextView textview,boolean status, boolean allarme_attivo, String text)
    {
        if(allarme_attivo == false)
        {
            if (status == false)
            {
                textview.setText("ALLARME SPENTO");
            } else
            {
                textview.setText("ALLARME INSERITO");
            }
        }
        else
        {
            textview.setText(text);
        }

    }
    private String convert_tostring01(boolean status)
    {
        if (status == false)
        {
            return "0";
        }
        else
        {
            return "1";
        }

    }


}