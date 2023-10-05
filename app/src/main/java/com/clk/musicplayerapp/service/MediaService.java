package com.clk.musicplayerapp.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.widget.RemoteViews;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.clk.musicplayerapp.App;
import com.clk.musicplayerapp.MediaManager;
import com.clk.musicplayerapp.R;
import com.clk.musicplayerapp.view.activity.MainActivity;
import com.clk.musicplayerapp.view.model.Song;

public class MediaService extends Service {
    private static final String CHANNEL_ID = "music";
    private static final String PLAY_EVENT = "PLAY_EVENT";
    private static final String NEXT_EVENT = "NEXT_EVENT";
    private static final String BACK_EVENT = "BACK_EVENT";
    private static final String CLOSE_EVENT = "CLOSE_EVENT";
    private static final String KEY_EVENT = "KEY_EVENT";
    private RemoteViews views;
    private Song song;
    private boolean appRunning;
    private Notification notify;
    private RemoteViews viewCollapse;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private PendingIntent getPendingIntent(int number, String key) {
        Intent intent = new Intent(this, MediaService.class);
        intent.putExtra(KEY_EVENT, key);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        return PendingIntent.getService(this, number, intent, PendingIntent.FLAG_IMMUTABLE);
    }


    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID);
        views = new RemoteViews(getPackageName(), R.layout.item_notify_media);
        viewCollapse = new RemoteViews(getPackageName(), R.layout.item_notify_collapsed);
        song = MediaManager.getInstance().getSong();
        viewCollapse.setTextViewText(R.id.tv_name, song.title);
        viewCollapse.setTextViewText(R.id.tv_album, song.album);
        views.setTextViewText(R.id.tv_name, song.title);
        views.setTextViewText(R.id.tv_album, song.album);
        views.setOnClickPendingIntent(R.id.iv_close, getPendingIntent(1, CLOSE_EVENT));
        views.setOnClickPendingIntent(R.id.iv_play, getPendingIntent(2, PLAY_EVENT));
        views.setOnClickPendingIntent(R.id.iv_next, getPendingIntent(3, NEXT_EVENT));
        views.setOnClickPendingIntent(R.id.iv_back, getPendingIntent(5, BACK_EVENT));
        Intent intent = new Intent(App.getInstance(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(App.getInstance(), 0, intent, PendingIntent.FLAG_IMMUTABLE);
        appRunning = true;
        new Thread(this::updateBeekbar).start();
        builder.setSmallIcon(R.drawable.ic_mp3)
                .setOnlyAlertOnce(true)
                .setOngoing(true)
                .setChannelId(CHANNEL_ID)
                .setContentTitle(song.title)
                .setContentText(song.album)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setSound(null)
                .setCustomContentView(viewCollapse)
                .setCustomBigContentView(views)
                .setStyle(new NotificationCompat.DecoratedCustomViewStyle())
                .setStyle(new NotificationCompat.DecoratedCustomViewStyle())
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setAutoCancel(true)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC);
        notify = builder.build();
        startForeground(1001, notify);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            String key = intent.getStringExtra(KEY_EVENT);
            if (key != null && key.equals(NEXT_EVENT)) {
                MediaManager.getInstance().nextSong();
                Message.obtain(handler).sendToTarget();
            } else if (key != null && key.equals(BACK_EVENT)) {
                MediaManager.getInstance().backSong();
                Message.obtain(handler).sendToTarget();
            } else if (key != null && key.equals(PLAY_EVENT)) {
                MediaManager.getInstance().play();
                Message.obtain(handler).sendToTarget();
            } else if (key != null && key.equals(CLOSE_EVENT)) {
                MediaManager.getInstance().pause();
                stopService(new Intent(this, MediaService.class));
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private final Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            song = MediaManager.getInstance().getSong();
            String currentTimeText = MediaManager.getInstance().getCurrentTimeText();
            String totalTimeText = MediaManager.getInstance().getTotalTimeText();
            int currentTime = MediaManager.getInstance().getCurrentTime();
            int totalTime = MediaManager.getInstance().getTotalTime();
            views.setTextViewText(R.id.tv_name, song.title);
            views.setTextViewText(R.id.tv_album, song.album);
            viewCollapse.setTextViewText(R.id.tv_name, song.title);
            viewCollapse.setTextViewText(R.id.tv_album, song.album);
            views.setProgressBar(R.id.progressbar, totalTime, currentTime, false);
            viewCollapse.setProgressBar(R.id.progressbar, totalTime, currentTime, false);
            views.setTextViewText(R.id.tv_Duration, String.format("%s/%s", currentTimeText, totalTimeText));
            viewCollapse.setTextViewText(R.id.tv_Duration, String.format("%s/%s", currentTimeText, totalTimeText));
            if (MediaManager.getInstance().getState() == MediaManager.STATE_PLAYING) {
                views.setImageViewResource(R.id.iv_play, R.drawable.ic_pause);
            } else {
                views.setImageViewResource(R.id.iv_play, R.drawable.ic_play);
            }
            startForeground(1001, notify);
            return false;
        }
    });

    private void updateBeekbar() {
        while (appRunning) {
            try {
                Thread.sleep(1000);
                Message.obtain(handler).sendToTarget();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void createNotificationChannel() {
        String description = "Enjoy music :))";
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel;
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            channel = new NotificationChannel(CHANNEL_ID, CHANNEL_ID, importance);
            channel.setDescription(description);
            channel.setSound(null, null);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
