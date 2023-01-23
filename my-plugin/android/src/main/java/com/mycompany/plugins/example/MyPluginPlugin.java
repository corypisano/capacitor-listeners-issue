package com.mycompany.plugins.example;

import android.os.CountDownTimer;
import android.util.Log;

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
        Log.d(TAG, "load() called");

        // debug log for duration of timer
        Long millisInFuture = 1800000L; // for 30 minutes
        Long countDownInterval = 15000L; // every 15 seconds
        new CountDownTimer(millisInFuture, countDownInterval) {
            public void onTick(long millisUntilFinished) {
                Long secondsRemaining = millisUntilFinished / 1000;
                Log.d(TAG, "seconds remaining: " + secondsRemaining);

                // emit plugin event
                JSObject ret = new JSObject();
                ret.put("secondsRemaining", secondsRemaining);
                notifyListeners("myPluginEvent", ret);
            }

            public void onFinish() {
                Log.d(TAG, "done!");
            }
        }.start();
    }

    @PluginMethod
    public void echo(PluginCall call) {
        String value = call.getString("value");

        JSObject ret = new JSObject();
        ret.put("value", implementation.echo(value));
        call.resolve(ret);
    }
}
