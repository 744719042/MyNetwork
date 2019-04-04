package com.example.mynetwork;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;

public class RestartService extends IntentService {

    public RestartService() {
        super("RestartService");
    }

    public static void restart(Context context) {
        Intent intent = new Intent(context, RestartService.class);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Intent mainIntent = new Intent(getApplication(), MainActivity.class);
        mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(mainIntent);
    }
}
