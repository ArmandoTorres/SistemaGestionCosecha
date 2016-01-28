package com.cac.tools;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.cac.sgc.MainActivity;
import com.cac.R;
import com.cac.services.SyncServerService;

/**
 * Created by miguel on 03/11/15.
 */
public class ServerStarter extends BroadcastReceiver {

    private MainActivity mainActivity;

    public ServerStarter(MainActivity mainActivity){
        this.mainActivity = mainActivity;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals(SyncServerService.SGC)){
            int status = intent.getIntExtra("status",SyncServerService.UNCONNECTED);
            try {

                switch (status){
                    case SyncServerService.CONNECTED:
                        mainActivity.getStatusConnection()
                                .setIcon(R.drawable.plugged)
                                .setTitle(R.string.plugged);
                        break;

                    case SyncServerService.UNCONNECTED:
                        mainActivity.getStatusConnection()
                                .setIcon(R.drawable.unplugged)
                                .setTitle(R.string.unplugged);
                        break;

                    case SyncServerService.LAST_SERVER_SYNC:
                        mainActivity.getSettingFragment().updateLastExecutionSummary();
                        break;
                }
            }catch(NullPointerException e){}
        }
    }
}
