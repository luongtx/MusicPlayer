package com.example.mymusicapp.controller;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.v4.media.session.MediaSessionCompat;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.mymusicapp.R;
import com.example.mymusicapp.entity.Song;
import com.example.mymusicapp.service.MusicService;
import com.example.mymusicapp.service.NotificationActionService;
import com.example.mymusicapp.service.OnClearFromRecentService;

import static com.example.mymusicapp.controller.ActivityMain.musicSrv;

public class NotificationPlaybackController implements MusicService.ServiceCallbacks {

    public static final String CHANNEL_ID = "channel1";

    public static final String ACTION_PREVIUOS = "actionprevious";
    public static final String ACTION_PLAY = "actionplay";
    public static final String ACTION_NEXT = "actionnext";

    public static Notification notification;

    private Context context;

    NotificationManager notificationManager;

    MediaPlaybackController playbackController;

    public NotificationPlaybackController(Context context, MediaPlaybackController playbackController) {
        this.context = context;
        this.playbackController = playbackController;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            createChannel();
            context.registerReceiver(broadcastReceiver, new IntentFilter("TRACKS_TRACKS"));
            context.startService(new Intent(context, OnClearFromRecentService.class));
        }
    }

    private void createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                    "KOD Dev", NotificationManager.IMPORTANCE_LOW);

            notificationManager = context.getSystemService(NotificationManager.class);
            if (notificationManager != null){
                notificationManager.createNotificationChannel(channel);
            }
        }
    }

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getExtras().getString("actionname");
            switch (action) {
                case ACTION_PREVIUOS:
                    playbackController.previous();
                    break;
                case ACTION_NEXT:
                    playbackController.next();
                    break;
                case ACTION_PLAY:
                    playbackController.toggle_play();
                    break;
            }
        }
    };

    @Override
    public void onPlayNewSong() {
        NotificationPlaybackController.createNotification(context, musicSrv.getCurrentSong(),
                R.drawable.ic_pause_not, musicSrv.getPlayingSongPos(), musicSrv.getSongs().size()-1);
    }

    @Override
    public void onMusicPause() {
        NotificationPlaybackController.createNotification(context, musicSrv.getCurrentSong(),
                R.drawable.ic_play_not, musicSrv.getPlayingSongPos(), musicSrv.getSongs().size()-1);
    }

    @Override
    public void onMusicResume() {
        NotificationPlaybackController.createNotification(context, musicSrv.getCurrentSong(),
                R.drawable.ic_pause_not, musicSrv.getPlayingSongPos(), musicSrv.getSongs().size()-1);
    }

    public static void createNotification(Context context, Song track, int playbutton, int pos, int size){

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){

            NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
            MediaSessionCompat mediaSessionCompat = new MediaSessionCompat( context, "tag");

//            int drawableId = 700025 + (int) (Math.random() * 4);
            Bitmap icon = BitmapFactory.decodeResource(context.getResources(), R.drawable.t1);

            PendingIntent pendingIntentPrevious;
            int drw_previous;
            if (pos == 0){
                pendingIntentPrevious = null;
                drw_previous = 0;
            } else {
                Intent intentPrevious = new Intent(context, NotificationActionService.class)
                        .setAction(ACTION_PREVIUOS);
                pendingIntentPrevious = PendingIntent.getBroadcast(context, 0,
                        intentPrevious, PendingIntent.FLAG_UPDATE_CURRENT);
                drw_previous = R.drawable.ic_previous;
            }

            Intent intentPlay = new Intent(context, NotificationActionService.class)
                    .setAction(ACTION_PLAY);
            PendingIntent pendingIntentPlay = PendingIntent.getBroadcast(context, 0,
                    intentPlay, PendingIntent.FLAG_UPDATE_CURRENT);

            PendingIntent pendingIntentNext;
            int drw_next;
            if (pos == size){
                pendingIntentNext = null;
                drw_next = 0;
            } else {
                Intent intentNext = new Intent(context, NotificationActionService.class)
                        .setAction(ACTION_NEXT);
                pendingIntentNext = PendingIntent.getBroadcast(context, 0,
                        intentNext, PendingIntent.FLAG_UPDATE_CURRENT);
                drw_next = R.drawable.ic_next;
            }

            Intent notIntent = new Intent(context, ActivityMain.class);
            notIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            PendingIntent pendInt = PendingIntent.getActivity(context, 0,
                    notIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            notification = new NotificationCompat.Builder(context, CHANNEL_ID)
                    .setContentIntent(pendInt)
                    .setSmallIcon(R.drawable.ic_audiotrack)
                    .setContentTitle(track.getTitle())
                    .setContentText(track.getArtist())
                    .setLargeIcon(icon)
                    .addAction(drw_previous, "Previous", pendingIntentPrevious)
                    .addAction(playbutton, "Play", pendingIntentPlay)
                    .addAction(drw_next, "Next", pendingIntentNext)
                    .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                            .setShowActionsInCompactView(0, 1, 2)
                            .setMediaSession(mediaSessionCompat.getSessionToken()))
                    .build();

            notificationManagerCompat.notify(1, notification);

        }
    }

    public BroadcastReceiver getBroadcastReceiver() {
        return broadcastReceiver;
    }
}
