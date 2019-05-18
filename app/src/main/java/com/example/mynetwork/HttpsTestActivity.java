package com.example.mynetwork;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.util.Pair;
import android.view.View;

import java.io.UnsupportedEncodingException;

import javax.crypto.SecretKey;

public class HttpsTestActivity extends AppCompatActivity {
    private static final String TAG = "HttpsTestActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_https_test);
    }

    public void testEnc(View view) {
        String message = "Hello World!!!";
        String key = "GoodBye";

        SecretKey secretKey = EncryUtils.initKeyForAES(key);
        byte[] cipher = EncryUtils.AESEncode(secretKey, message);
        Log.e(TAG, "cipher = " + Base64.encodeToString(cipher, 0));

        byte[] plain = EncryUtils.AESDecode(secretKey, cipher);
        try {
            Log.e(TAG, "plain = " + new String(plain, "utf-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public void testNonEnc(View view) {
        String message = "Hello World!!!";
        try {
            Pair<String, String> secretKey = EncryUtils.genKeyPair();
            Log.e(TAG, "public key = " + secretKey.first);
            Log.e(TAG, "private key = " +  secretKey.second);
            String cipher = EncryUtils.encrypt(message, secretKey.first);
            Log.e(TAG, "cipher = " + cipher);
            String plain = EncryUtils.decrypt(cipher, secretKey.second);
            Log.e(TAG, "plain = " + plain);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void testMD5(View view) {
        String message = "Hello World!!!";
        String md5 = EncryUtils.MD5Encode(message);
        Log.e(TAG, "md5 =" + md5);

        String text = "HelloWorld!!!";
        md5 = EncryUtils.MD5Encode(text);
        Log.e(TAG, "md5 =" + md5);

    }

    public void testHttps(View view) {

    }
}
