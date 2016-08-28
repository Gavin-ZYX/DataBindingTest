package com.example.gavin.databindingtest;

import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.databinding.ObservableInt;

/**
 * Created by Gavin on 2016/8/9.
 */
public class User2 {
    public final ObservableField<String> firstName = new ObservableField<>();
    public final ObservableField<String> lastName = new ObservableField<>();
    public final ObservableInt age = new ObservableInt();
    public final ObservableBoolean isStudent = new ObservableBoolean();
    public boolean isS = true;
}
