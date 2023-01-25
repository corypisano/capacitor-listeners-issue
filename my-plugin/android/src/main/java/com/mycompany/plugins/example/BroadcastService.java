package com.mycompany.plugins.example;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

public class BroadcastService extends Service {

  public static final String CHANNEL_ID = "ForegroundServiceChannel";
  public static final String COUNTDOWN_BR = "com.mycompany.plugins.example";
  private static final String TAG = "BroadcastService";

  Intent broadcastIntent = new Intent(COUNTDOWN_BR);
  CountDownTimer timer = null;

  @Override
  public void onCreate() {
      Log.d(TAG, "BroadcastService.onCreate");
      super.onCreate();
      Log.d(TAG, "Starting timer...");
      timer = new CountDownTimer(120000, 2000) {
          @Override
          public void onTick(long millisUntilFinished) {
              long countdown = millisUntilFinished / 1000;
              Log.i(TAG, "Countdown seconds remaining: " + countdown);
              broadcastIntent.putExtra("countdown", countdown);
              broadcastIntent.putExtra("countdownTimerFinished", false);
              sendBroadcast(broadcastIntent);
          }
          @Override
          public void onFinish() {
              Log.i(TAG, "Timer finished");
              broadcastIntent.putExtra("countdownTimerFinished", true);
              sendBroadcast(broadcastIntent);
              stopForeground(true);
              stopSelf();
          }
      };
      timer.start();
  }

  @Override
  public void onDestroy() {
      timer.cancel();
      Log.d(TAG, "BroadcastService.onDestroy");
      broadcastIntent.putExtra("countdownTimerRunning", false);
      sendBroadcast(broadcastIntent);
      super.onDestroy();
  }

  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
      /* Notification */
      String input = intent.getStringExtra("inputExtra");
      createNotificationChannel();
      Intent notificationIntent = new Intent(this, BroadcastReceiver.class);
      PendingIntent pendingIntent = PendingIntent.getActivity(this,
              0, notificationIntent, PendingIntent.FLAG_IMMUTABLE);
      /* NotificationBuilder */
      Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
              .setContentTitle("Foreground Service")
              .setContentText(input)
              .setContentIntent(pendingIntent)
              .build();
      startForeground(1, notification);
      return START_NOT_STICKY;
  }

  @Nullable
  @Override
  public IBinder onBind(Intent intent) {
      return null;
  }

  private void createNotificationChannel() {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
          NotificationChannel serviceChannel = new NotificationChannel(
                  CHANNEL_ID,
                  "Foreground Service Channel",
                  NotificationManager.IMPORTANCE_DEFAULT
          );
          NotificationManager manager = getSystemService(NotificationManager.class);
          manager.createNotificationChannel(serviceChannel);
      }
  }
}