package com.wharton.sample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.wharton.ui.keyboard.NumberKeyboard;
import com.wharton.ui.weight.GridTextView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    @BindView(R.id.tv_main)
    GridTextView tvMain;
    @BindView(R.id.keyboard_main)
    NumberKeyboard keyboardMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        keyboardMain.bindView(tvMain);
        tvMain.setOnGridActionListener(new GridTextView.OnGridActionListener() {
            @Override
            public void onMaxLength(String s) {
                Log.e(TAG, "onMaxLength: " + s);
            }
        });
    }
}
