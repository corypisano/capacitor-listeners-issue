package com.mycompany.plugins.example;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.CountDownTimer;
import android.util.Log;

import androidx.core.content.ContextCompat;

import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;

@CapacitorPlugin(name = "MyPlugin")
public class MyPluginPlugin extends Plugin {

    private MyPlugin implementation = new MyPlugin();
    private static final String TAG = "MyPlugin";

    @Override
    public void load() {
        Log.d(TAG, "MyPlugin.load()");
        this.handleStartTimer();
    }

    public void handleStartTimer() {
        Intent intent = new Intent(this.getContext(), BroadcastService.class);
        intent.putExtra("inputExtra", "");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Log.d(TAG, "handleStartTimer: startForegroundService");
            ContextCompat.startForegroundService(this.getContext(), intent);
        } else {
            Log.d(TAG, "handleStartTimer: didnt start foreground service, unexpected sdk version");
        }
    }

    final private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            emitPluginEvent(intent);
        }
    };

    @Override
    public void handleOnResume() {
        Log.i(TAG, "handleOnResume: Registered broadcast receiver");
        this.getContext().registerReceiver(broadcastReceiver, new IntentFilter(BroadcastService.COUNTDOWN_BR));
        super.handleOnResume();
    }

    @Override
    public void handleOnDestroy() {
        Log.i(TAG, "handleOnDestroy");
        super.handleOnDestroy();
    }

    private void emitPluginEvent(Intent intent) {
        if (intent.getExtras() != null) {
            long countdown = intent.getLongExtra("countdown", 0);
            long seconds = countdown % 60;
            long minutes = (countdown / 60) % 60;
            String time = (minutes + "m : " + seconds + "s");
           
            Log.d(TAG, "time: " + time);

            boolean countdownTimerFinished = intent.getBooleanExtra("countdownTimerFinished", false);

            // emit plugin event
            JSObject ret = new JSObject();
            ret.put("time", time);
            if (countdownTimerFinished) {
                Log.d(TAG, "countdownTimerFinished");
                ret.put("countdownTimerFinished", countdownTimerFinished);
            }
            notifyListeners("myPluginEvent", ret);
        }
    }

    @PluginMethod
    public void echo(PluginCall call) {
        String value = call.getString("value");

        JSObject ret = new JSObject();
        ret.put("value", implementation.echo(value));
        call.resolve(ret);
    }
}
