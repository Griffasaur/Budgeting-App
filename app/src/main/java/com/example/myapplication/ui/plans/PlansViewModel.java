package com.example.myapplication.ui.plans;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class PlansViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public PlansViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("Plans screen will be implemented here");
    }

    public LiveData<String> getText() {
        return mText;
    }
} 