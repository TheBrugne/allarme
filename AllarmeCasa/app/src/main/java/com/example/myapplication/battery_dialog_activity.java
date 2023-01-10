package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.example.myapplication.ui.battery.BatteryFragment;

public class battery_dialog_activity extends AppCompatActivity {
    CheckBox _checkBox_garage;
    CheckBox _checkBox_marino;
    CheckBox _checkBox_chiara;
    CheckBox _checkBox_ada;
    Button _button_save;
    Button _button_exit;
    private boolean is_garage=false;
    private boolean is_marino=false;
    private boolean is_chiara=false;
    private boolean is_ada=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_battery_dialog);
       /* _button_exit= findViewById(R.id.button_exit);
        _button_save= findViewById(R.id.button_save);
        _checkBox_garage=findViewById(R.id.checkBox_garage);
        _checkBox_marino=findViewById(R.id.checkBox_marino);
        _checkBox_chiara=findViewById(R.id.checkBox_chiara);
        _checkBox_ada=findViewById(R.id.checkBox_ada);
        String zona = BatteryFragment.get_zona_btn();
        if(zona.equals("GARAGE"))
        {
            is_garage=true;
            _checkBox_garage.setChecked(true);
        }
        else if(zona.equals("MARINO"))
        {
            is_marino=true;
            _checkBox_marino.setChecked(true);
        }
        else if(zona.equals("CHIARA"))
        {
            is_chiara=true;
            _checkBox_chiara.setChecked(true);
        }
        else if(zona.equals("ADA"))
        {
            is_ada=true;
            _checkBox_ada.setChecked(true);
        }
        _checkBox_garage.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean status) {
                if(_checkBox_garage.isChecked()== true)
                {
                    _checkBox_ada.setChecked(false);
                    _checkBox_chiara.setChecked(false);
                    _checkBox_marino.setChecked(false);
                    is_ada=false;
                    is_chiara=false;
                    is_marino=false;

                }
                else
                {
                    is_ada=false;
                    is_chiara=false;
                    is_marino=false;
                    is_garage=false;
                }

            }

        });

        _checkBox_marino.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean status) {

                if(_checkBox_marino.isChecked()== true)
                {
                    _checkBox_ada.setChecked(false);
                    _checkBox_chiara.setChecked(false);
                    _checkBox_garage.setChecked(false);
                    is_ada=false;
                    is_chiara=false;
                    is_garage=false;

                }
                else
                {
                    is_ada=false;
                    is_chiara=false;
                    is_marino=false;
                    is_garage=false;
                }

            }

        });

        _checkBox_chiara.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean status) {

                if(_checkBox_chiara.isChecked()== true)
                {
                    _checkBox_ada.setChecked(false);
                    _checkBox_marino.setChecked(false);
                    _checkBox_garage.setChecked(false);
                    is_ada=false;
                    is_marino=false;
                    is_garage=false;

                }
                else
                {
                    is_ada=false;
                    is_chiara=false;
                    is_marino=false;
                    is_garage=false;
                }



            }

        });

        _checkBox_ada.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean status) {

                if(_checkBox_ada.isChecked()== true)
                {
                    _checkBox_marino.setChecked(false);
                    _checkBox_chiara.setChecked(false);
                    _checkBox_garage.setChecked(false);
                    is_marino=false;
                    is_chiara=false;
                    is_garage=false;

                }
                else
                {
                    is_ada=false;
                    is_chiara=false;
                    is_marino=false;
                    is_garage=false;
                }



            }

        });

        */
    }
}