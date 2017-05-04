package com.example.vassiliki.agony;

import android.content.Context;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pManager;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Vassiliki on 11/4/2016.
 */
public class MyPeerListListener implements WifiP2pManager.PeerListListener {
    private List peers ;
    //for debug reasons///
    private Context context;
    private MainActivity main;
    //public List<WifiP2pDevice> devices;

    public MyPeerListListener(Context con)
    {
        super();
        peers = new ArrayList();
        this.context = con;
        this.main = (MainActivity)context;
        //devices = new ArrayList<>();
    }
    public int returnDevice(String device_name)
    {
        int answer = 0;
        for (int i = 0;i < peers.size(); i++)
        {
            if (peers.get(i).equals(device_name))
            {
                answer = i;
                break;
            }
        }
        return answer;
    }
    public boolean isInDeviceList(String device_name)
    {
        boolean answer = false;
        for (int i = 0;i < peers.size(); i++)
        {
            if (peers.get(i).equals(device_name))
            {
                answer = true;
                break;
            }
        }
        return answer;
    }
    public List getPeers()
    {
        return peers;
    }

    @Override
    public void onPeersAvailable(WifiP2pDeviceList peersList) {
        peers.clear();
        //devices.clear();
        peers.addAll(peersList.getDeviceList());
        //devices.addAll((List<WifiP2pDevice>)peersList);
        //Toast.makeText(context,"peer list changed: ",Toast.LENGTH_SHORT).show();
        //showPeerList();
        // If an AdapterView is backed by this data, notify it
        // of the change.  For instance, if you have a ListView of available
        // peers, trigger an update.
            /*((WiFiPeerListAdapter) getListAdapter()).notifyDataSetChanged();*/
        this.main.flag = true;
        if (peers.size() == 0) {
            Toast.makeText(context,"No devices found: "+peers.toString(),Toast.LENGTH_SHORT).show();
        }
        main.updatePeerList(peers);
    }
}
