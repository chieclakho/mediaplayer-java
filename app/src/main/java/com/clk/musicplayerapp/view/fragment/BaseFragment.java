package com.clk.musicplayerapp.view.fragment;

import static com.clk.musicplayerapp.view.fragment.MainFragment.LEVER_IDLE;
import static com.clk.musicplayerapp.view.fragment.MainFragment.LEVER_PLAY;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewbinding.ViewBinding;

import com.clk.musicplayerapp.MediaManager;
import com.clk.musicplayerapp.OnMainCallBack;
import com.clk.musicplayerapp.view.model.Song;
import com.clk.musicplayerapp.view.viewmodel.BaseViewModel;

public abstract class BaseFragment<T extends ViewBinding, VM extends BaseViewModel> extends Fragment implements View.OnClickListener {
    protected T binding;
    protected VM model;
    protected Object data;
    protected Context context;
    protected OnMainCallBack callBack;

    @Nullable
    @Override
    public Context getContext() {
        return context;
    }

    public void setCallBack(OnMainCallBack callBack) {
        this.callBack = callBack;
    }

    public void setData(Object data) {
        this.data = data;
    }

    @Override
    public final void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public final View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = initViewBinding(inflater, container);
        model = new ViewModelProvider(this).get(initClassModel());
        initViews();
        return binding.getRoot();
    }

    protected abstract Class<VM> initClassModel();


    protected abstract void initViews();

    protected abstract T initViewBinding(LayoutInflater inflater, ViewGroup container);


    @Override
    public final void onClick(View v) {
        v.startAnimation(AnimationUtils.loadAnimation(context, androidx.appcompat.R.anim.abc_fade_in));
        clickView(v);
    }

    protected void clickView(View v) {
        // do Nothing
    }

    protected void updateBeekbar(SeekBar seekbar, TextView tvDuration ) {
        boolean appRunning = true;
        while (appRunning) {
            try {
                Thread.sleep(500);
                callBack.runOnUi(() -> {
                    String currentTimeText = MediaManager.getInstance().getCurrentTimeText();
                    String totalTimeText = MediaManager.getInstance().getTotalTimeText();
                    int currentTime = MediaManager.getInstance().getCurrentTime();
                    int totalTime = MediaManager.getInstance().getTotalTime();
                    seekbar.setMax(totalTime);
                    seekbar.setProgress(currentTime);
                    tvDuration.setText(String.format("%s/%s", currentTimeText, totalTimeText));
                });
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    protected void upDateUi(ImageView ivPlay, TextView tvName, TextView tvAlbum) {
        if (MediaManager.getInstance().getState() == MediaManager.STATE_PLAYING) {
            ivPlay.setImageLevel(LEVER_PLAY);
        } else {
            ivPlay.setImageLevel(LEVER_IDLE);
        }
        Song song = MediaManager.getInstance().getSong();
        tvName.setText(song.title);
        tvAlbum.setText(song.album);
    }
}
