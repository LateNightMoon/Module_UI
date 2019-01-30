package com.wharton.sample;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.wharton.ui.keyboard.NumberKeyboard;
import com.wharton.ui.weight.GridTextView;
import com.wharton.ui.weight.dialog.NumberDialog;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

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

    @OnClick(R.id.btn_main_number_dialog)
    public void onClickNumber() {
        NumberDialog.Options options = new NumberDialog.Options();
        options.title = "请输入支付密码";
        options.subTitle = "充值";
        options.price = "100.00";
        NumberDialog dialog = NumberDialog.newInstance(options);
        dialog.show(getSupportFragmentManager(), "NumberDialog");
        dialog.setItemClickListener(new NumberDialog.OnItemClickListener() {
            @Override
            public void onMaxLength(String s, DialogFragment dialog) {
                Toast.makeText(MainActivity.this, "输入的信息: " + s, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onClickHint(DialogFragment dialog) {
                Toast.makeText(MainActivity.this, "忘记密码", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
