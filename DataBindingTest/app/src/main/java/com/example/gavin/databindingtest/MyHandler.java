package com.example.gavin.databindingtest;

import android.view.View;
import android.widget.Toast;

/**
 * Created by Gavin on 2016/8/9.
 */
public class MyHandler {
    public void onClick(View view) {
        Toast.makeText(view.getContext(), "点击事件", Toast.LENGTH_LONG).show();
    }
}
