package com.example.myapplication.ui.log;

import android.app.Dialog;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.myapplication.MainActivity;
import com.example.myapplication.R;
import com.example.myapplication.databinding.FragmentLogBinding;
import com.example.myapplication.mqtt_service;

public class LogFragment extends Fragment implements mqtt_service.MyListener{

    private FragmentLogBinding binding;
    private TableLayout _tab_log;
    private Button _button_cancella_log;
    mqtt_service _mqtt_service;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_log, container, false);

        _button_cancella_log= view.findViewById(R.id.button_cancella_log);
        _button_cancella_log.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v) {

                final Dialog dialog = new Dialog(getActivity());
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setCancelable(true);
                dialog.setContentView(R.layout.activity_log_dialog);
                dialog.show();

                Button _button_no = dialog.findViewById(R.id.button_no);
                _button_no.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                Button _button_si = dialog.findViewById(R.id.button_si);
                _button_si.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        dialog.dismiss();
                        _mqtt_service.publish_message("CANCELLA_LOG");
                        _mqtt_service.publish_message("?MOSTRA_LOG");
                    }
                });
            }
        });
        _mqtt_service = MainActivity.get_mqtt_service();
        if(_mqtt_service.get_ret_val()== true) {
            _mqtt_service.emptyListener();
            _mqtt_service.addListener(this);
            _tab_log = view.findViewById(R.id.tab_log);
            _mqtt_service.publish_message("?MOSTRA_LOG");
        }
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }



    public void _messageArrived(String message) {
        decode_message(message);
    }

    private void decode_message(String msg)
    {
        String[] msg_split = msg.split(";");
        switch(msg_split[0]) {
            case ("!MOSTRA_LOG"):
                int count = _tab_log.getChildCount();
                for (int i = 0; i < count; i++) {
                    View child = _tab_log.getChildAt(i);
                    if (child instanceof TableRow) ((ViewGroup) child).removeAllViews();
                }
                for(int i =1; i< msg_split.length;i=i+2) {
                    String log=msg_split[i]+ " "+msg_split[i+1];


                    TableRow tr = new TableRow(this.getContext());
                    tr.setGravity(Gravity.CENTER);
                    tr.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT));

                    TextView battery_pos = new TextView(this.getContext());
                    battery_pos.setText(log);
                    battery_pos.setGravity(Gravity.CENTER);
                    tr.addView(battery_pos);
                    _tab_log.addView(tr, new TableLayout.LayoutParams(TableLayout.LayoutParams.FILL_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));
                }
                break;
        }
    }
}