package com.clk.musicplayerapp.view.fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.clk.musicplayerapp.MediaManager;
import com.clk.musicplayerapp.OnSeekBarChange;
import com.clk.musicplayerapp.R;
import com.clk.musicplayerapp.databinding.FragmentPlayBinding;
import com.clk.musicplayerapp.view.viewmodel.CommonVM;
import com.gauravk.audiovisualizer.visualizer.BarVisualizer;

public class PlayFragment extends BaseFragment<FragmentPlayBinding, CommonVM> {
    public static final String TAG = PlayFragment.class.getName();
    private BarVisualizer visualizer;
    @Override
    protected Class<CommonVM> initClassModel() {
        return CommonVM.class;
    }

    @Override
    protected void initViews() {
        binding.ivPlay.setOnClickListener(this);
        binding.ivBack.setOnClickListener(this);
        binding.ivNext.setOnClickListener(this);
        binding.ivOpen.setOnClickListener(this);
        binding.ivShuffle.setOnClickListener(this);
        binding.menu.ivMenu.setOnClickListener(this);
        binding.menu.ivMenu.setImageResource(R.drawable.ic_back_frg);
        visualizer = binding.blast;
        MediaManager.getInstance().getShuffle().observe(this, integer -> {
            if (integer == 0) {
                binding.ivShuffle.setImageLevel(0);
            } else if (integer == 1) {
                binding.ivShuffle.setImageLevel(1);
            } else if (integer == 2) {
                binding.ivShuffle.setImageLevel(2);
            }
        });
        new Thread(() -> {
            visualizer.setAudioSessionId(MediaManager.getInstance().getPlayer());
            updateBeekbar(binding.seekbar, binding.tvDuration );
        }).start();
        upDateUi(binding.ivPlay, binding.tvName, binding.tvAlbum);
        MediaManager.getInstance().getLiveData().observe(this, integer -> upDateUi(binding.ivPlay, binding.tvName, binding.tvAlbum));
        binding.seekbar.setOnSeekBarChangeListener((OnSeekBarChange) seekBar -> MediaManager.getInstance().seekTo(seekBar.getProgress()));
    }

    @Override
    protected void clickView(View v) {
        if (v.getId() == R.id.iv_play) {
            MediaManager.getInstance().play();
        } else if (v.getId() == R.id.iv_next) {
            MediaManager.getInstance().nextSong();
        } else if (v.getId() == R.id.iv_back) {
            MediaManager.getInstance().backSong();
        } else if (v.getId() == R.id.iv_menu || v.getId() == R.id.iv_open) {
            callBack.backToPrevious();
        } else if (v.getId() == R.id.iv_shuffle) {
            MediaManager.getInstance().setShuffle();
        }
    }

    @Override
    protected FragmentPlayBinding initViewBinding(LayoutInflater inflater, ViewGroup container) {
        return FragmentPlayBinding.inflate(inflater, container, false);
    }

    @Override
    public void onDestroy() {
        if (visualizer != null) {
            visualizer.release();
            visualizer = null;
        }
        super.onDestroy();
    }
}
