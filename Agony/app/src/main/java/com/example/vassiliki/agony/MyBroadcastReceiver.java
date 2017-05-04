package com.example.vassiliki.agony;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import java.lang.reflect.Array;

import static com.example.vassiliki.agony.R.layout.simple_list_item;

/**
 * Created by Vassiliki on 11/4/2016.
 */
public class MyBroadcastReceiver extends BroadcastReceiver {
    private MainActivity activity;
    private WifiP2pManager.Channel mychannel;
    private WifiP2pManager mymanage;
    private MyConnectionInfoListener myconnectionlistener;

    public MyBroadcastReceiver(WifiP2pManager manager,WifiP2pManager.Channel channel,MainActivity mainActivity)
    {
        super();
        this.mymanage = manager;
        this.mychannel = channel;
        this.activity = mainActivity;
        this.myconnectionlistener = new MyConnectionInfoListener();
        myconnectionlistener.setMainActivity(activity);
    }
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action))
        {
            // Determine if Wifi P2P mode is enabled or not, alert
            // the Activity.
            int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE,-1);
            if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED)
            {
                Toast.makeText(context, "Wifi state changed: enabled.", Toast.LENGTH_LONG).show();
                //activity.setIsWifiP2pEnabled(true);
            }
            else
            {
                Toast.makeText(context, "Wifi state changed: diasabled.",Toast.LENGTH_LONG).show();
                //activity.setIsWifiP2pEnabled(false);
            }
        }
        else
        {
            if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action))
            {
                /// Request available peers from the wifi p2p manager. This is an
                // asynchronous call and the calling activity is notified with a
                // callback on PeerListListener.onPeersAvailable()
                if (mymanage != null) {
                    mymanage.requestPeers(mychannel,activity.getPeerListListener());
/*                    if (this.activity.getPeerListListener().getPeers().size() == 0)
                    {
                        this.activity.flag = false;
                    }
                    else
                    {
                        this.activity.flag = true;
                    }*/
                    //this.activity.flag = true;
                    //this.activity.updatePeerList(this.activity.getPeerListListener().getPeers());
                }
            }
            else
            {
                if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action))
                {
                    //Toast.makeText(context, "Wifi connection state changed.",Toast.LENGTH_LONG).show();
                    // Connection state changed!  We should probably do something about
                    // that.
                    if (mymanage == null) {
                        return;
                    }

                    NetworkInfo networkInfo = (NetworkInfo) intent
                            .getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);

                    if (networkInfo.isConnected()) {

                        // We are connected with the other device, request connection
                        // info to find group owner IP

                        mymanage.requestConnectionInfo(mychannel, myconnectionlistener);
                    }
                }
                else
                {
                    if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action))
                    {
                        //Toast.makeText(context, "This device changed.",Toast.LENGTH_LONG).show();
                        //DeviceListFragment fragment = (DeviceListFragment) activity.getFragmentManager().findFragmentById(R.id.frag_list);
                        //fragment.updateThisDevice((WifiP2pDevice) intent.getParcelableExtra(WifiP2pManager.EXTRA_WIFI_P2P_DEVICE));
                    }
                }
            }
        }
    }
}
