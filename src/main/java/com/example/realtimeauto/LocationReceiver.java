package com.example.realtimeauto;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class LocationReceiver extends BroadcastReceiver {




    @Override
    public void onReceive(Context context, Intent intent) {
        intent=new Intent(context, RealTimeLocationService.class);
        context.startService(intent);
    }
}
