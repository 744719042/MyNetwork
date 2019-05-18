package com.example.mynetwork;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.util.Pair;
import android.view.View;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.Map;

import javax.crypto.SecretKey;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

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

    public void testCertificate(View view) {
        CertificateFactory certificateFactory = null;
        try {
            certificateFactory = CertificateFactory.getInstance("X509");
            X509Certificate certificate = (X509Certificate) certificateFactory.generateCertificate(getAssets().open("tomcat.cer"));
            Log.e(TAG, certificate.getSigAlgName());
            Log.e(TAG, certificate.getIssuerDN().toString());
            Log.e(TAG, certificate.getSubjectDN().toString());
            Log.e(TAG, certificate.getPublicKey().toString());
            Log.e(TAG, Base64.encodeToString(certificate.getSignature(), 0));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    final HostnameVerifier hostnameVerifier = new HostnameVerifier() {
        @Override
        public boolean verify(String hostname, SSLSession session) {
            Log.e(TAG, "hostname = " + hostname);
            return true;
        }
    };

    private static class MyTrustManager implements X509TrustManager {

        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {

        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            CertificateFactory certificateFactory = CertificateFactory.getInstance("X509");
            try {
                X509Certificate certificate = (X509Certificate) certificateFactory.
                        generateCertificate(MyApplication.getContext().getAssets().open("tomcat.cer"));
                X509Certificate server = chain[0];
                if (certificate.getIssuerDN().equals(server.getIssuerDN())) {
                    if (certificate.getPublicKey().equals(server.getPublicKey())) {
                        certificate.checkValidity();
                        return;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
        }
    }

    public void testHttps(View view) {
        final String LOGIN_URL = "https://192.168.137.240:8443/HttpServer/login";

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    TrustManager trustManager = new MyTrustManager();
                    SSLContext sslContext = SSLContext.getInstance("TLS");
                    sslContext.init(null, new TrustManager[]{trustManager}, new SecureRandom());
                    SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

                    HttpsURLConnection httpsURLConnection = (HttpsURLConnection) new URL(LOGIN_URL).openConnection();
                    httpsURLConnection.setRequestMethod("POST");
                    httpsURLConnection.setConnectTimeout(3000);
                    httpsURLConnection.setReadTimeout(3000);
                    httpsURLConnection.setDoInput(true);
                    httpsURLConnection.setDoOutput(false);
                    httpsURLConnection.setInstanceFollowRedirects(true);
                    httpsURLConnection.setHostnameVerifier(hostnameVerifier);
                    httpsURLConnection.setSSLSocketFactory(sslSocketFactory);

                    OutputStream outputStream = httpsURLConnection.getOutputStream();
                    outputStream.write("name=xxxx".getBytes("UTF-8"));
                    outputStream.flush();
                    outputStream.close();
                    httpsURLConnection.getResponseCode();
                    printHeads(httpsURLConnection.getHeaderFields());
                    InputStream inputStream = httpsURLConnection.getInputStream();
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    byte[] buf = new byte[1024];
                    int len = -1;
                    while ((len = inputStream.read(buf)) != -1) {
                        bos.write(buf, 0, len);
                    }

                    String text = new String(bos.toByteArray(), "UTF-8");
                    Log.e(TAG, text);
                    httpsURLConnection.disconnect();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void printHeads(Map<String, List<String>> headerFields) {
        if (headerFields != null) {
            for (Map.Entry<String, List<String>> entry : headerFields.entrySet()) {
                Log.e(TAG, entry.getKey() + ": " + entry.getValue());
            }
        }
    }
}
