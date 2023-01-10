package com.example.myapplication.ui.battery;

import android.app.Dialog;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;

import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.myapplication.MainActivity;
import com.example.myapplication.R;
import com.example.myapplication.databinding.FragmentBatteryBinding;
import com.example.myapplication.mqtt_service;

public class BatteryFragment extends Fragment implements mqtt_service.MyListener {

    private boolean is_garage=false;
    private boolean is_marino=false;
    private boolean is_chiara=false;
    private boolean is_ada=false;

    private static String [] zona=null;
    private static String [] nome=null;
    private static String zona_btn = null;
    private static String nome_btn = null;
    private FragmentBatteryBinding binding;
    private TableLayout _tab_battery;
    mqtt_service _mqtt_service;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_battery, container, false);

        _mqtt_service = MainActivity.get_mqtt_service();
        if(_mqtt_service.get_ret_val()== true) {
            _mqtt_service.emptyListener();
            _mqtt_service.addListener(this);
            _tab_battery = view.findViewById(R.id.tab_battery);
            _mqtt_service.publish_message("?STATO_BATTERIE");
        }
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void _messageArrived(String message) {
        decode_message(message);
    }

    private void decode_message(String msg)
    {
        String[] msg_split = msg.split(";");
        switch(msg_split[0])
        {
            case("!STATO_BATTERIE"):

                int count = _tab_battery.getChildCount();
                for (int i = 0; i < count; i++) {
                    View child = _tab_battery.getChildAt(i);
                    if (child instanceof TableRow) ((ViewGroup) child).removeAllViews();
                }
                zona = new String[msg_split.length - 1];
                nome = new String[msg_split.length - 1];
                for(int i =0; i< msg_split.length;i++) {

                    String[] msg_split_split = msg_split[i+1].split(" ");
                    zona[i]=msg_split_split[2];
                    nome[i]=msg_split_split[1];
                    /* Create a new row to be added. */
                    TableRow tr = new TableRow(this.getContext());
                    tr.setGravity(Gravity.CENTER);
                    tr.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT));
                    /* Create a Button to be the row-content. */
                    ImageView img_battery = new ImageView(this.getContext());
                    img_battery.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                            TableRow.LayoutParams.WRAP_CONTENT));
                    img_battery.getLayoutParams().height = 100;

                    img_battery.getLayoutParams().width = 170;

                    img_battery.setScaleType(ImageView.ScaleType.FIT_XY);
                    if(Integer.valueOf(msg_split_split[3])>=40)
                        img_battery.setImageResource(R.drawable.bat_100);
                    else if(Integer.valueOf(msg_split_split[3])>=38)
                        img_battery.setImageResource(R.drawable.bat_80);
                    else if(Integer.valueOf(msg_split_split[3])>=36)
                        img_battery.setImageResource(R.drawable.bat_60);
                    else if(Integer.valueOf(msg_split_split[3])>=34)
                        img_battery.setImageResource(R.drawable.bat_40);
                    else if(Integer.valueOf(msg_split_split[3])>=32)
                        img_battery.setImageResource(R.drawable.bat_20);
                    else
                        img_battery.setImageResource(R.drawable.bat_00);


                    /* Add Button to row. */
                    tr.addView(img_battery);
                    TextView battery_pos = new TextView(this.getContext());
                    battery_pos.setText("   "+msg_split_split[1]+"   ");
                    battery_pos.setGravity(Gravity.CENTER);
                    tr.addView(battery_pos);

                    Button battery_set = new Button(this.getContext());
                    battery_set.setTag(String.valueOf(i));
                    battery_set.setOnClickListener(listener);
                    battery_set.setText("SET");
                    battery_set.setGravity(Gravity.CENTER);
                    battery_set.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                            TableRow.LayoutParams.WRAP_CONTENT));
                    battery_set.getLayoutParams().height = 130;

                    battery_set.getLayoutParams().width = 170;



                    tr.addView(battery_set);

                    _tab_battery.addView(tr, new TableLayout.LayoutParams(TableLayout.LayoutParams.FILL_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));
                }
                break;

        }
    }

    View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View btn)
        {
            boolean save_ok=false;
            set_zona_btn(zona[Integer.parseInt(btn.getTag().toString())]);
            set_nome_btn(nome[Integer.parseInt(btn.getTag().toString())]);
            final Dialog dialog = new Dialog(getActivity());
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(true);
            dialog.setContentView(R.layout.activity_battery_dialog);


            CheckBox _checkBox_garage=dialog.findViewById(R.id.checkBox_garage);
            CheckBox _checkBox_marino=dialog.findViewById(R.id.checkBox_marino);
            CheckBox _checkBox_chiara=dialog.findViewById(R.id.checkBox_chiara);
            CheckBox _checkBox_ada=dialog.findViewById(R.id.checkBox_ada);
            EditText _allarm_name = dialog.findViewById(R.id.allarm_name);
            _allarm_name.setText(nome_btn);
            if(zona_btn.equals("garage"))
            {
                is_garage=true;
                _checkBox_garage.setChecked(true);
            }
            else if(zona_btn.equals("marino"))
            {
                is_marino=true;
                _checkBox_marino.setChecked(true);
            }
            else if(zona_btn.equals("chiara"))
            {
                is_chiara=true;
                _checkBox_chiara.setChecked(true);
            }
            else if(zona_btn.equals("ada"))
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
                        is_garage=true;

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
                        is_marino=true;

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
                        is_chiara=true;

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
                        is_ada=true;

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

            Button _button_exit = dialog.findViewById(R.id.button_exit);
            _button_exit.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                dialog.dismiss();
                }
            });

            Button _button_save = dialog.findViewById(R.id.button_save);
            _button_save.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    if(_allarm_name.getText().toString().contains(" "))
                    {
                        Log.d("aa","aa");
                    }
                    else
                    {
                        if (is_ada== true || is_garage ==true || is_chiara==true || is_marino==true)
                        {
                            String selected_zona = null;
                            if(is_ada== true)
                                selected_zona="ada";
                            if(is_garage== true)
                                selected_zona="garage";
                            if(is_chiara== true)
                                selected_zona="chiara";
                            if(is_marino== true)
                                selected_zona="marino";
                            dialog.dismiss();
                            _mqtt_service.publish_message("MODIFICA_ASSOCIAZIONE;"+String.valueOf((Integer.parseInt(btn.getTag().toString()))+1)+";"+_allarm_name.getText().toString()+";"+selected_zona);
                        }
                        else
                        {
                            Log.d("aa","aa");
                        }
                    }


                }
            });
            dialog.show();



        }
    };
    public static String get_zona_btn()
    {
        return zona_btn;
    }
    public static void set_zona_btn(String _zona_btn)
    {
        zona_btn = _zona_btn;
    }
    public static void set_nome_btn(String _nome_btn)
    {
        nome_btn = _nome_btn;
    }
}