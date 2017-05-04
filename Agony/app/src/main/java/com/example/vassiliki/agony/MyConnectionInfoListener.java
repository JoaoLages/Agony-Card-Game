package com.example.vassiliki.agony;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.NetworkOnMainThreadException;
import android.widget.EditText;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Created by Vassiliki on 29/4/2016.
 */
public class MyConnectionInfoListener implements WifiP2pManager.ConnectionInfoListener {

    public static int  num_of_clients = 0;
    public MainActivity mainActivity;
    private Game the_game;
    public static boolean ready=true, created=false;
    private int numberOfClients, num=0;
    private long Time;
    private final long mili=1000;
    ServerAsyncTask serverAsyncTask1;
    public void setMainActivity(MainActivity a)
    {
        mainActivity = a;
        the_game= new Game();
        //Toast.makeText(this.mainActivity," just made the game object. Face down pack: "+the_game.face_down_pack.toString(),Toast.LENGTH_LONG).show();
    }
    @Override
    public void onConnectionInfoAvailable(WifiP2pInfo info) {

// InetAddress from WifiP2pInfo struct.
        //InetAddress groupOwnerAddress = info.groupOwnerAddress.getHostAddress();
        String groupOwnerAddress = info.groupOwnerAddress.getHostAddress();
        // After the group negotiation, we can determine the group owner.

        // if (info.groupFormed && info.isGroupOwner) {
        if ((info.groupFormed && MainActivity.createdGroup) ||  (info.groupFormed && info.isGroupOwner )){


            numberOfClients= MainActivity.numberOfPlayers;

            // Do whatever tasks are specific to the group owner.
            // One common case is creating a server thread and accepting
            // incoming connections.
            ////////////////////////////////////////
            // Intent intent = new Intent();

            //NetworkInfo networkinfo = getIntent().getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);



            //Toast.makeText(mainActivity, "networkinfo= "+networkinfo.toString(), Toast.LENGTH_SHORT).show();

            ////////vassiliki test code/////////////////////////////
            if(MainActivity.justCreatedGroup){
                MainActivity.justCreatedGroup=false;
            }else if(ready && !MainActivity.paused ){
                Time = System.currentTimeMillis()/mili - Time;
                if(Time>2) {
                    num++;
                    created = true;
                    if (num == 1) {
                        try {
                            MainActivity.serverSocket = new ServerSocket(8888);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    Toast.makeText(mainActivity, "Player " + num+" connected!", Toast.LENGTH_SHORT).show();
                    if (num == numberOfClients - 1) {
                        ServerAsyncTask serverAsyncTask1 = new ServerAsyncTask(this.mainActivity, info.groupOwnerAddress, numberOfClients);

                        serverAsyncTask1.execute(the_game);
                    }
                }


            }else if(MainActivity.paused){
                MainActivity.paused=false;
            }

            ////////vassiliki end/////////////////////////////////////////////
        }
        else if (info.groupFormed) {

            numberOfClients= MainActivity.numberOfPlayers;



            if(ready){
                ready=false;
                ClientAsyncTask clientAsyncTask = new ClientAsyncTask(this.mainActivity);
                clientAsyncTask.execute(info.groupOwnerAddress);
            }

        }
    }
}
