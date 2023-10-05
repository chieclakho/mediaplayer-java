package com.clk.musicplayerapp;

public interface OnMainCallBack {
    void showFragment(String tag, Object data, boolean isBacked);

    void runOnUi(Runnable runnable);

    void backToPrevious();
}
