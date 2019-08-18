package com.example.mynetwork;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

public class NetworkActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_network);
        // 匿名内部类创建的Callback对象会包含NetworkActivity.this对象
        HttpNetUtils.get("http://www.baidu.com", new HttpNetUtils.Callback() {
            @Override
            public void onSuccess(final String text) {
                showSuccess();
            }
            @Override
            public void onFailure() { }
        });
        // 能够解决内存泄漏的回调
        // HttpNetUtils.get("http://www.baidu.com", new MyCallback(this));
    }

    void showSuccess() {
        getWindow().getDecorView().post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), "+" +
                        "", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
