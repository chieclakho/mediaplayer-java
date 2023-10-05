package com.clk.musicplayerapp.view.activity;

import android.os.Bundle;
import android.view.View;
import android.view.animation.AnimationUtils;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewbinding.ViewBinding;


import com.clk.musicplayerapp.OnMainCallBack;
import com.clk.musicplayerapp.R;
import com.clk.musicplayerapp.view.fragment.BaseFragment;
import com.clk.musicplayerapp.view.viewmodel.BaseViewModel;

import java.lang.reflect.Constructor;

public abstract class BaseActivity<T extends ViewBinding, VM extends BaseViewModel>
        extends AppCompatActivity implements View.OnClickListener, OnMainCallBack {
    protected T binding;
    protected VM model;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = initViewBinding();
        model = new ViewModelProvider(this).get(initModelClass());
        setContentView(binding.getRoot());
        initViews();
    }

    protected abstract Class<VM> initModelClass();

    protected abstract void initViews();

    protected abstract T initViewBinding();

    @Override
    public final void onClick(View v) {
        v.startAnimation(AnimationUtils.loadAnimation(this, androidx.appcompat.R.anim.abc_fade_in));
        clickView(v);
    }

    protected void clickView(View v) {
        // do nothiing
    }

    public void showFragment(String tag, Object data, boolean isBacked) {
        try {
            Class<?> clazz = Class.forName(tag);
            Constructor<?> constructor = clazz.getConstructor();
            BaseFragment<?, ?> fragment = (BaseFragment<?, ?>) constructor.newInstance();
            fragment.setCallBack(this);
            fragment.setData(data);
            FragmentTransaction trans = getSupportFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left,
                            R.anim.enter_from_left, R.anim.exit_to_right)
                    .replace(R.id.ln_main, fragment, tag);
            if (isBacked) {
                trans.addToBackStack(null);
            }
            trans.commit();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void runOnUi(Runnable runnable) {
        runOnUiThread(runnable);
    }
}
