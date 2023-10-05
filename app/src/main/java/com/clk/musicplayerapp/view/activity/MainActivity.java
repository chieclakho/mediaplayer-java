package com.clk.musicplayerapp.view.activity;

import androidx.annotation.NonNull;

import android.Manifest;
import android.content.pm.PackageManager;

import com.clk.musicplayerapp.databinding.ActivityMainBinding;
import com.clk.musicplayerapp.view.fragment.MainFragment;
import com.clk.musicplayerapp.view.viewmodel.CommonVM;

public class MainActivity extends BaseActivity<ActivityMainBinding, CommonVM> {
    @Override
    protected Class<CommonVM> initModelClass() {
        return CommonVM.class;
    }

    @Override
    protected ActivityMainBinding initViewBinding() {
        return ActivityMainBinding.inflate(getLayoutInflater());
    }

    @Override
    protected void initViews() {
        checkPermission();
    }

    private void checkPermission() {
        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.RECORD_AUDIO
            }, 101);
        } else {
            showFragment(MainFragment.TAG, null, false);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
            showFragment(MainFragment.TAG, null, false);
        }
    }

    @Override
    public void backToPrevious() {
        onBackPressed();
    }
}