package com.example.myapplication.ui.siren;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SirenViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public SirenViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is siren fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}