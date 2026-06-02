package com.example.labo;
import android.Manifest;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import androidx.annotation.RequiresPermission;

public class detectarinternet {
    Context context;
    public detectarinternet(Context context) {
        this.context = context;
    }
    @RequiresPermission(Manifest.permission.ACCESS_NETWORK_STATE)
    public boolean hayConexionInternet(){
        ConnectivityManager connectivityManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager==null) return false;

        NetworkInfo[] info = connectivityManager.getAllNetworkInfo();
        if(info==null) return false;

        for (int i = 0; i < info.length; i++) {
            if(info[i].getState()== NetworkInfo.State.CONNECTED){
                return true;
            }
        }
        return false;
    }
}
