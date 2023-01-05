package com.example.myapplication.ui.siren;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.myapplication.databinding.FragmentSirenBinding;

public class SirenFragment extends Fragment {

    private FragmentSirenBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        SirenViewModel notificationsViewModel =
                new ViewModelProvider(this).get(SirenViewModel.class);

        binding = FragmentSirenBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

       final TextView textView = binding.textSiren;
        notificationsViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}