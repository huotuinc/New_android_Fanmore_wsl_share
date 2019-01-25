package com.ar.wsl;

import android.content.Context;

import com.unity3d.player.UnityPlayer;

public class MyUnityPlayer extends UnityPlayer {
    public MyUnityPlayer(Context context) {
        super(context);
    }

    @Override
    protected void kill() {
        //不要杀死当前应用进程
    }
}