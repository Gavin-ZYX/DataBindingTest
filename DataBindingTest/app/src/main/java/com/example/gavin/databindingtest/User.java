package com.example.gavin.databindingtest;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import com.example.gavin.databindingtest.BR;

/**
 * Created by Gavin on 2016/8/8.
 */
public class User extends BaseObservable {
    private String firstName;
    private String lastName;
    public boolean isStudent = true;

    public User(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }
    @Bindable
    public String getFirstName() {
        return this.firstName;
    }
    @Bindable
    public String getLastName() {
        return this.lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
        notifyPropertyChanged(BR.lastName);
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
        notifyPropertyChanged(BR.firstName);
    }
}