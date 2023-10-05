package com.clk.musicplayerapp;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.provider.MediaStore;

import androidx.lifecycle.MutableLiveData;

import com.clk.musicplayerapp.view.model.Song;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class MediaManager {
    private static final String TAG = MediaManager.class.getName();
    private final MediaPlayer player;
    public static final int STATE_IDLE = 1;
    public static final int STATE_PLAYING = 2;
    public static final int STATE_PAUSED = 3;
    private int state = STATE_IDLE;
    private static MediaManager instance;
    private final List<Song> listSong = new ArrayList<>();
    private int currentSong = 0;
    private int play = 0;
    private final MutableLiveData<Integer> liveData = new MutableLiveData<>();
    private final MutableLiveData<Integer> shuffle = new MutableLiveData<>(0);
    private MediaPlayer.OnCompletionListener onCompletionevent;

    public int getState() {
        return state;
    }

    public MutableLiveData<Integer> getLiveData() {
        return liveData;
    }

    public List<Song> getListSong() {
        return listSong;
    }

    public MutableLiveData<Integer> getShuffle() {
        return shuffle;
    }

    public static MediaManager getInstance() {
        if (instance == null) {
            instance = new MediaManager();
        }
        return instance;
    }

    private MediaManager() {
        player = new MediaPlayer();
        player.setOnCompletionListener(mp -> {
            if (play != 0) {
                nextSong();
            }
            play++;
            onCompletionevent.onCompletion(null);
        });
        player.setAudioAttributes(new AudioAttributes.Builder()
                .setLegacyStreamType(AudioManager.STREAM_MUSIC)
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .build());
    }

    public void loadOffLine() {
        if (!listSong.isEmpty()) return;
        Cursor cursor = App.getInstance().getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null,
                null, null, MediaStore.Audio.Media.TITLE + " ASC");
        if (cursor == null) return;
        cursor.moveToFirst();

        int cursorTitle = cursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
        int cursorPath = cursor.getColumnIndex(MediaStore.Audio.Media.DATA);
        int cursorAlbum = cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM);
        int cursorArtist = cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        listSong.clear();
        while (!cursor.isAfterLast()) {
            String title = cursor.getString(cursorTitle);
            String path = cursor.getString(cursorPath);
            String album = cursor.getString(cursorAlbum);
            String artist = cursor.getString(cursorArtist);
            retriever.setDataSource(path);
            String duration = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
            Song song = new Song(title, path, album, artist, duration);
            listSong.add(song);
            cursor.moveToNext();
        }
        cursor.close();
    }

    public void pause() {
        if (player.isPlaying()) {
            player.pause();
            state = STATE_PAUSED;
            liveData.postValue(state);
        }
    }

    public void play() {
        if (state == STATE_IDLE) {
            player.reset();
            try {
                player.setDataSource(listSong.get(currentSong).path);
                player.prepare();
                player.start();
                state = STATE_PLAYING;
                liveData.postValue(state);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else if (state == STATE_PAUSED) {
            state = STATE_PLAYING;
            liveData.postValue(state);
            player.start();
        } else {
            state = STATE_PAUSED;
            liveData.postValue(state);
            player.pause();
        }
    }


    public void nextSong() {
        if (shuffle.getValue() == null) return;
        if (shuffle.getValue() == 2) {
            currentSong = new Random().nextInt(listSong.size());
        } else if (shuffle.getValue() == 0) {
            currentSong++;
            if (currentSong >= listSong.size()) {
                currentSong = 0;
            }
        }
        state = STATE_IDLE;
        liveData.postValue(state);
        play();
    }

    public void backSong() {
        if (shuffle.getValue() == null) return;
        if (shuffle.getValue() == 2) {
            currentSong = new Random().nextInt(listSong.size());
        } else if (shuffle.getValue() == 0) {
            currentSong--;
            if (currentSong <= 0) {
                currentSong = listSong.size() - 1;
            }
        }
        state = STATE_IDLE;
        liveData.postValue(state);
        play();
    }

    public int getCurrentSong() {
        return currentSong;
    }

    public void setCurrent(String current) {
        currentSong = Integer.parseInt(current);
    }

    public void playSong(Song song) {
        currentSong = listSong.indexOf(song);
        state = STATE_IDLE;
        liveData.postValue(state);
        play();
    }

    public Song getSong() {
        return listSong.get(currentSong);
    }

    public String getCurrentTimeText() {
        try {
            @SuppressLint("SimpleDateFormat")
            SimpleDateFormat dateFormat = new SimpleDateFormat("mm:ss");
            return dateFormat.format(new Date(player.getCurrentPosition()));
        } catch (Exception ignored) {
        }
        return "--";
    }

    public String getTotalTimeText() {
        try {
            @SuppressLint("SimpleDateFormat")
            SimpleDateFormat dateFormat = new SimpleDateFormat("mm:ss");
            return dateFormat.format(new Date(player.getDuration()));
        } catch (Exception ignored) {
        }
        return "--";
    }

    public int getTotalTime() {
        return player.getDuration();
    }

    public int getCurrentTime() {
        return player.getCurrentPosition();
    }

    public void seekTo(int progress) {
        try {
            player.seekTo(progress);
        } catch (Exception ignored) {
        }
    }

    public void setCompleteCallBack(MediaPlayer.OnCompletionListener event) {
        onCompletionevent = event;
    }

    public int getPlayer() {
        return player.getAudioSessionId();
    }

    public void setShuffle() {
        if (shuffle.getValue() == null) return;
        shuffle.postValue(shuffle.getValue() + 1);
        if (shuffle.getValue() >= 2) {
            shuffle.postValue(0);
        }
    }
}
