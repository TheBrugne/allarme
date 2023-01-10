package com.example.myapplication;
//import org.eclipse.paho.android.service.MqttAndroidClient;
import android.content.Context;
import android.view.View;

import com.example.myapplication.ui.battery.BatteryFragment;
import com.example.myapplication.ui.home.HomeFragment;

import info.mqtt.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.ArrayList;
import java.util.List;

public class mqtt_service {

    public interface MyListener {
        void _messageArrived(String message);
    }
    MqttAndroidClient  client;
    private Context context;
    private List<MyListener> listeners = new ArrayList<MyListener>();
    static final String topic = "casabrugne_allarme";
    boolean ret_val = false;
    public void init_mqtt(final Context context) {

        String clientId = MqttClient.generateClientId();
        try
        {
            client = new MqttAndroidClient(context, "tcp://casabrugne.ddns.net:1883", clientId);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        try
        {
            IMqttToken token = client.connect();
            token.setActionCallback(new IMqttActionListener()
            {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {


                    //Toast.makeText(MainActivity.this,"connected!!",Toast.LENGTH_SHORT).show();
                    setSubscription();

                    publish_message("?STATO_ALLARME");
                    publish_message("?ALLARME_ATTIVO");
                    publish_message("?TEMPERATURA_RASPY");
                    ret_val = true;
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                   // _textView_error_conn.setVisibility(View.VISIBLE);
                  //  _imageView_error_conn.setVisibility(View.VISIBLE);
                    ret_val = false;
                }
            });
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }


        client.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {

            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                for(MyListener listener : listeners){
                    listener._messageArrived(message.toString());
                }


            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        });

    }

    public void stop_mqtt()
    {
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

    public void publish_message(String msg)
    {
        if(msg.equals("?STATO_ALLARME"))
        {
            HomeFragment.set_disable_switch(true);
        }
        client.publish(topic,get_byte_array(msg),0,false);
    }
    private byte[] get_byte_array(String msg)
    {
        return msg.getBytes();
    }

    public void addListener(MyListener listener) {
        listeners.add(listener);
    }

    public void emptyListener()
    {
        listeners.clear();
    }

    public boolean get_ret_val()
    {
        return ret_val;
    }
}
