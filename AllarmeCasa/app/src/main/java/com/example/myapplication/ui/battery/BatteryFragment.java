package com.example.myapplication.ui.battery;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.myapplication.databinding.FragmentBatteryBinding;

public class BatteryFragment extends Fragment {

    private FragmentBatteryBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        BatteryViewModel dashboardViewModel =
                new ViewModelProvider(this).get(BatteryViewModel.class);

        binding = FragmentBatteryBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textBattery;
        dashboardViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}