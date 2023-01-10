package com.example.myapplication.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.myapplication.MainActivity;
import com.example.myapplication.R;
import com.example.myapplication.databinding.FragmentHomeBinding;
import com.example.myapplication.mqtt_service;

import java.util.Arrays;

public class HomeFragment extends Fragment implements mqtt_service.MyListener {

    private FragmentHomeBinding binding;
    mqtt_service _mqtt_service;

    TextView _textView_error_conn;
    ImageView _imageView_error_conn;
    ScrollView _scrollView_main;
    CheckBox _checkBox_test;
    Button _button_allarm;
    SwitchCompat _switch_garage;
    SwitchCompat _switch_marino;
    SwitchCompat _switch_chiara;
    SwitchCompat _switch_ada;

    ImageView _imageView_stato_sistema;
    TextView  _textView_stato_sistema;

    /* variabili per lo stato */

    static final int N_STATI_ALLARME = 6;
    static final int TEST_POS = 0;
    static final int ARMATO_POS = 1;
    static final int GARAGE_POS = 2;
    static final int MARINO_POS = 3;
    static final int CHIARA_POS = 4;
    static final int ADA_POS = 5;
    private boolean stato_allarme[]= new boolean[N_STATI_ALLARME];
    private boolean init_eseguito=false;
    public  static boolean disable_switch=false;

    public static void set_disable_switch(boolean status)
    {
        disable_switch=status;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);


    }
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        _mqtt_service = MainActivity.get_mqtt_service();
        if(_mqtt_service.get_ret_val()== true) {

            _mqtt_service.emptyListener();
            _mqtt_service.addListener(this);


            _mqtt_service.publish_message("?STATO_ALLARME");
            Arrays.fill(stato_allarme, Boolean.FALSE);


            // _textView_error_conn = view.findViewById(R.id.textView_error_conn);
            // _imageView_error_conn = view.findViewById(R.id.imageView_error_conn);

            _checkBox_test = view.findViewById(R.id.checkBox_test);
            _switch_garage = view.findViewById(R.id.switch_garage);
            _switch_marino = view.findViewById(R.id.switch_marino);
            _switch_chiara = view.findViewById(R.id.switch_chiara);
            _switch_ada = view.findViewById(R.id.switch_ada);
            _button_allarm = view.findViewById(R.id.button_allarm);
            _imageView_stato_sistema = view.findViewById(R.id.imageView_stato_sistema);
            _textView_stato_sistema = view.findViewById(R.id.textView_stato_sistema);


            _button_allarm.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    if (init_eseguito == true) {
                        String status_pos = "0";
                        if (stato_allarme[TEST_POS] == true) {
                            status_pos = "1";
                        }

                        if (stato_allarme[ARMATO_POS] == false) {
                            stato_allarme[ARMATO_POS] = true;
                            _mqtt_service.publish_message("SET_ALLARME;test;" + status_pos + ";armato;1;garage;1;marino;1;chiara;1;ada;1");

                        } else {
                            stato_allarme[ARMATO_POS] = false;
                            _mqtt_service.publish_message("SET_ALLARME;test;" + status_pos + ";armato;0;garage;0;marino;0;chiara;0;ada;0");
                        }
                        _mqtt_service.publish_message("?STATO_ALLARME");
                    }
                }
            });

            _switch_garage.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean status) {
                    {
                        if (disable_switch == false) {
                            if (status == true) {
                                stato_allarme[GARAGE_POS] = true;
                            } else {
                                stato_allarme[GARAGE_POS] = false;
                            }
                            _mqtt_service.publish_message("SET_ALLARME;test;" + convert_tostring01(stato_allarme[TEST_POS]) + ";ARMATO;"
                                    + convert_tostring01(stato_allarme[ARMATO_POS]) + ";GARAGE;"
                                    + convert_tostring01(stato_allarme[GARAGE_POS]) + ";MARINO;"
                                    + convert_tostring01(stato_allarme[MARINO_POS]) + ";CHIARA;"
                                    + convert_tostring01(stato_allarme[CHIARA_POS]) + ";ADA;"
                                    + convert_tostring01(stato_allarme[ADA_POS]));
                            _mqtt_service.publish_message("?STATO_ALLARME");
                        }

                    }
                }
            });

            _switch_marino.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean status) {
                    {
                        if (disable_switch == false) {
                            if (status == true) {
                                stato_allarme[MARINO_POS] = true;
                            } else {
                                stato_allarme[MARINO_POS] = false;
                            }
                            _mqtt_service.publish_message("SET_ALLARME;test;" + convert_tostring01(stato_allarme[TEST_POS]) + ";ARMATO;"
                                    + convert_tostring01(stato_allarme[ARMATO_POS]) + ";GARAGE;"
                                    + convert_tostring01(stato_allarme[GARAGE_POS]) + ";MARINO;"
                                    + convert_tostring01(stato_allarme[MARINO_POS]) + ";CHIARA;"
                                    + convert_tostring01(stato_allarme[CHIARA_POS]) + ";ADA;"
                                    + convert_tostring01(stato_allarme[ADA_POS]));
                            _mqtt_service.publish_message("?STATO_ALLARME");
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
                            _mqtt_service.publish_message("SET_ALLARME;test;" + convert_tostring01(stato_allarme[TEST_POS]) + ";ARMATO;"
                                    + convert_tostring01(stato_allarme[ARMATO_POS]) + ";GARAGE;"
                                    + convert_tostring01(stato_allarme[GARAGE_POS]) + ";MARINO;"
                                    + convert_tostring01(stato_allarme[MARINO_POS]) + ";CHIARA;"
                                    + convert_tostring01(stato_allarme[CHIARA_POS]) + ";ADA;"
                                    + convert_tostring01(stato_allarme[ADA_POS]));
                            _mqtt_service.publish_message("?STATO_ALLARME");
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
                            _mqtt_service.publish_message("SET_ALLARME;test;" + convert_tostring01(stato_allarme[TEST_POS]) + ";ARMATO;"
                                    + convert_tostring01(stato_allarme[ARMATO_POS]) + ";GARAGE;"
                                    + convert_tostring01(stato_allarme[GARAGE_POS]) + ";MARINO;"
                                    + convert_tostring01(stato_allarme[MARINO_POS]) + ";CHIARA;"
                                    + convert_tostring01(stato_allarme[CHIARA_POS]) + ";ADA;"
                                    + convert_tostring01(stato_allarme[ADA_POS]));
                            _mqtt_service.publish_message("?STATO_ALLARME");
                        }
                    }
                }
            });

            _checkBox_test.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean status) {
                    if (status == false) {
                        _mqtt_service.publish_message("SET_ALLARME_TEST;test;0");
                    } else {
                        _mqtt_service.publish_message("SET_ALLARME_TEST;test;1");
                    }
                    _mqtt_service.publish_message("?STATO_ALLARME");
                }

            });

            //HomeViewModel homeViewModel =
            //        new ViewModelProvider(this).get(HomeViewModel.class);

            //binding = FragmentHomeBinding.inflate(inflater, container, false);
            //View root = binding.getRoot();

            //final TextView textView = binding.textHome;
            //homeViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
            //return root;


        }
        return view;
    }


    private void decode_message(String msg)
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

        image.setImageDrawable(null); // <--- added to force redraw of ImageView
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




    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void _messageArrived(String message) {
decode_message(message);
    }
}