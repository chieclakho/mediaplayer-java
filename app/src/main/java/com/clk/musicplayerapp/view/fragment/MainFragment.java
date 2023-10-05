package com.clk.musicplayerapp.view.fragment;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.clk.musicplayerapp.CommonUtils;
import com.clk.musicplayerapp.MediaManager;
import com.clk.musicplayerapp.OnSeekBarChange;
import com.clk.musicplayerapp.R;
import com.clk.musicplayerapp.adapter.SongAdapter;
import com.clk.musicplayerapp.databinding.FragmentManiBinding;
import com.clk.musicplayerapp.service.MediaService;
import com.clk.musicplayerapp.view.model.Song;
import com.clk.musicplayerapp.view.viewmodel.CommonVM;

public class MainFragment extends BaseFragment<FragmentManiBinding, CommonVM> {
    public static final String TAG = MainFragment.class.getName();
    public static final int LEVER_PLAY = 1;
    public static final int LEVER_IDLE = 0;
    private SongAdapter adapter;
    private LinearLayoutManager layoutManager;
    @Override
    protected Class<CommonVM> initClassModel() {
        return CommonVM.class;
    }

    @Override
    protected void initViews() {
        binding.controller.ivBack.setOnClickListener(this);
        binding.controller.ivNext.setOnClickListener(this);
        binding.controller.ivPlay.setOnClickListener(this);
        binding.controller.ivOpen.setOnClickListener(this);
        binding.controller.ivShuffle.setOnClickListener(this);
        binding.menu.ivMenu.setImageResource(R.drawable.ic_menu);
        layoutManager = new LinearLayoutManager(context);
        binding.rcSong.setLayoutManager(layoutManager);
        MediaManager.getInstance().loadOffLine();
        String current = CommonUtils.getInstance().getPref("KEY_CURRENT");
        if (current != null) {
            MediaManager.getInstance().setCurrent(current);
            initListSong();
            adapter.setCurrent(current);
        } else {
            initListSong();
        }
        MediaManager.getInstance().getLiveData().observe(this, integer -> updateUi());
        MediaManager.getInstance().getShuffle().observe(this, this::setUpImageShuffle);
        MediaManager.getInstance().setCompleteCallBack(mp -> upDateUi(binding.controller.ivPlay, binding.controller.tvName, binding.controller.tvAlbum));
        binding.controller.seekbar.setOnSeekBarChangeListener((OnSeekBarChange) seekBar -> MediaManager.getInstance().seekTo(seekBar.getProgress()));
    }

    private void updateUi() {
        layoutManager.smoothScrollToPosition(binding.rcSong, null, MediaManager.getInstance().getCurrentSong());
        upDateUi(binding.controller.ivPlay, binding.controller.tvName, binding.controller.tvAlbum);
    }

    private void setUpImageShuffle(int integer) {
        if (integer == 0) {
            binding.controller.ivShuffle.setImageLevel(0);
        } else if (integer == 1) {
            binding.controller.ivShuffle.setImageLevel(1);
        } else if (integer == 2) {
            binding.controller.ivShuffle.setImageLevel(2);
        }
    }

    @Override
    protected void clickView(View v) {
        if (v.getId() == R.id.iv_play) {
            MediaManager.getInstance().play();
        } else if (v.getId() == R.id.iv_next) {
            MediaManager.getInstance().nextSong();
            adapter.upDateUi(MediaManager.getInstance().getCurrentSong());
        } else if (v.getId() == R.id.iv_back) {
            MediaManager.getInstance().backSong();
            adapter.upDateUi(MediaManager.getInstance().getCurrentSong());
        } else if (v.getId() == R.id.tb_song) {
            callBack.showFragment(PlayFragment.TAG, null, true);
            adapter.upDateUi(MediaManager.getInstance().getCurrentSong());
            MediaManager.getInstance().playSong((Song) v.getTag());
        } else if (v.getId() == R.id.iv_open) {
            callBack.showFragment(PlayFragment.TAG, null, true);
        } else if (v.getId() == R.id.iv_shuffle) {
            MediaManager.getInstance().setShuffle();
        }
    }

    private void initListSong() {
        adapter = new SongAdapter(MediaManager.getInstance().getListSong(), context, this::clickView);
        binding.rcSong.setAdapter(adapter);
        context.startService(new Intent(context, MediaService.class));
        new Thread(() -> updateBeekbar(binding.controller.seekbar, binding.controller.tvDuration)).start();
    }

    @Override
    protected FragmentManiBinding initViewBinding(LayoutInflater inflater, ViewGroup container) {
        return FragmentManiBinding.inflate(inflater, container, false);
    }

    @Override
    public void onPause() {
        CommonUtils.getInstance().savePref("KEY_CURRENT", String.valueOf(MediaManager.getInstance().getCurrentSong()));
        super.onPause();
    }
}
