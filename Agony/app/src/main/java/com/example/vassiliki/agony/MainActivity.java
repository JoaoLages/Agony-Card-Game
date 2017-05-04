package com.example.vassiliki.agony;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceInfo;
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceRequest;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Array;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private IntentFilter intentFilter;
    private static WifiP2pManager myManager;
    private static WifiP2pManager.Channel myChannel;
    private MyBroadcastReceiver receiver;
    private MyPeerListListener peerListListener;
    private List<String> peers;
    private List<String> available_peers = new ArrayList<String>();
    private ArrayAdapter<String> adapterPeers;
    private ArrayAdapter<String> adapterInvitations;
    private ListView allPeers;
    private ListView available ;
    private Button create_group;
    private Button play_with_2;
    public boolean flag = false;
    private ArrayList<String> peeColors;
    private static final String TAG = "MyActivity";
    public static ServerSocket serverSocket;
    public static int numberOfPlayers=0;
    public static volatile List<Integer> positions = new ArrayList<>();
    public static volatile boolean paused=false, createdGroup=false, DoConnect=true, justCreatedGroup=false;
    private int maxPlayers;
    public static boolean hasPressed = false;
    public static boolean no_group_connection;


    static final String STATE_COLORS = "colors";

    public MyPeerListListener getPeerListListener()
    {
        return this.peerListListener;
    }

/*    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save the current color array
        savedInstanceState.putStringArrayList(STATE_COLORS, peeColors);
        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }*/   /**save instance code*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        /////////////////////////////////
        intentFilter = new IntentFilter();
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

        myManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        myChannel = myManager.initialize(this, getMainLooper(), null);


                disconnect();
              //  remove_service();




        ///search for peers
        myManager.discoverPeers((WifiP2pManager.Channel) myChannel, new WifiP2pManager.ActionListener(){

            @Override
            public void onSuccess() {
                // Code for when the discovery initiation is successful goes here.
                // No services have actually been discovered yet, so this method
                // can often be left blank.  Code for peer discovery goes in the
                // onReceive method, detailed below.
                //Toast.makeText(getApplicationContext(), "discover peers success!!!!", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(int reasonCode) {
                // Code for when the discovery initiation fails goes here.
                // Alert the user that something went wrong.
                Toast.makeText(getApplicationContext(),"Peers discovery failed",Toast.LENGTH_LONG).show();
            }
        });
        ///////
        create_group = (Button)findViewById(R.id.creategroup);
        play_with_2 = (Button)findViewById(R.id.game_for_2);
        peerListListener = new MyPeerListListener(this);
        allPeers = (ListView)findViewById(R.id.peers1);
        available = (ListView)findViewById(R.id.peers2);
        maxPlayers = 2;
        no_group_connection = false;

        peers = new ArrayList<String>();
        /*if (savedInstanceState != null) {
            // Restore value of members from saved state
            peeColors = savedInstanceState.getStringArrayList(STATE_COLORS);
        }*/  /**save instance code*/
        if (peerListListener.getPeers() == null || peerListListener.getPeers().size() == 0)
        {
            if (peeColors == null)
            {
                //Toast.makeText(this,"peercolorlist is null",Toast.LENGTH_SHORT).show();
                peeColors = new ArrayList<String>();
                peeColors.add("g");
            }
            peers.add("");/**na valw to flag false kai edw na valw "" */
        }
        else
        {
            for (int i = 0; i < peerListListener.getPeers().size() ;i++)
            {
                peers.add(((WifiP2pDevice)peerListListener.getPeers().get(i)).deviceName);
            }
        }
        if (flag)
        {/**if true, the peer list has already initialized at least once, so peer is a non null list*/
            adapterPeers = new ArrayAdapter<String>(this,R.layout.simple_list_item,R.id.text1,peers);
            allPeers.setAdapter(adapterPeers);
            allPeers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if ((peeColors.get(position)).equals("r")) {
                        allPeers.getChildAt(position).setBackgroundColor(Color.GREEN);
                        peeColors.set(position, "g");
                    } else {
                        allPeers.getChildAt(position).setBackgroundColor(Color.RED);
                        peeColors.set(position, "r");
                    }
                }
            });
            for (int i = 0; i < peers.size();i++)
            {/**den thymamai giati*/
                if (peeColors.get(i).equals("r"))
                {
                    try {
                        allPeers.getChildAt(i).setBackgroundColor(Color.RED);
                    }
                    catch (Exception e)
                    {
                        Toast.makeText(this, e.getLocalizedMessage(),Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public void discoverService(View view) { //REFRESH BUTTON

        /*
         * Register listeners for DNS-SD services. These are callbacks invoked
         * by the system when a service is actually discovered.
         */

        myManager.setDnsSdResponseListeners(myChannel,
                new WifiP2pManager.DnsSdServiceResponseListener() {

                    @Override
                    public void onDnsSdServiceAvailable(String instanceName,
                                                        String registrationType, final WifiP2pDevice srcDevice) {

                        Toast.makeText(getApplicationContext(), srcDevice.deviceName+"'s group is available ", Toast.LENGTH_LONG).show();
                        if (!available_peers.contains(srcDevice.deviceName)){
                            available_peers.add(srcDevice.deviceName);
                            adapterInvitations = new ArrayAdapter<String>(getApplicationContext(),R.layout.simple_list_item,R.id.text1,available_peers);
                            available.setAdapter(adapterInvitations);
                            available.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    hasPressed = true;
                                    if (peerListListener.isInDeviceList(available_peers.get(position)))
                                        connect(peerListListener.returnDevice(available_peers.get(position)));
                                }
                            });
                        }

                    }
                }, new WifiP2pManager.DnsSdTxtRecordListener() {

                    /**
                     * A new TXT record is available. Pick up the advertised
                     * buddy name.
                     */
                    @Override
                    public void onDnsSdTxtRecordAvailable(
                            String fullDomainName, Map<String, String> record,
                            final WifiP2pDevice device) {
                        Toast.makeText(getApplicationContext(), device.deviceName+"'s group is available ", Toast.LENGTH_LONG).show();
                        if (!available_peers.contains(device.deviceName)){
                            available_peers.add(device.deviceName);
                            adapterInvitations = new ArrayAdapter<String>(getApplicationContext(),R.layout.simple_list_item,R.id.text1,available_peers);
                            available.setAdapter(adapterInvitations);
                            available.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    connect(device);
                                }
                            });
                        }
                    }
                });

        // After attaching listeners, create a service request and initiate
        // discovery.
        WifiP2pDnsSdServiceRequest serviceRequest = WifiP2pDnsSdServiceRequest.newInstance();
        myManager.addServiceRequest(myChannel, serviceRequest,
                new WifiP2pManager.ActionListener() {

                    @Override
                    public void onSuccess() {
                    }

                    @Override
                    public void onFailure(int arg0) {
                    }
                });
        myManager.discoverServices(myChannel, new WifiP2pManager.ActionListener() {

            @Override
            public void onSuccess() {
                Toast.makeText(getApplicationContext(), " Trying to discover groups", Toast.LENGTH_LONG).show();

            }

            @Override
            public void onFailure(int arg0) {
            }
        });
    }
    public void updatePeers(View view)
    {

        /**create a group with the selected peers*/
            //Toast.makeText(this,"just test: peerlist--> "+peers.toString()+"  and colors--> "+peeColors.toString(),Toast.LENGTH_LONG).show();
            for (int i = 0;i < peeColors.size();i++)/*(int i = 0;i < peeColors.size();i++)*/
            {
                if (peeColors.get(i).equals("r"))
                {
                    /**connect with the 1 device found red (only 1 can be red at a time, anyway)*/
                    play_with_2.setEnabled(false);
                    create_group.setEnabled(false);
                    //Toast.makeText(this," WHAT THE FUCK!! Ta ekana disabled? play->"+play_with_2.isEnabled()+" KAI CREAT GROUP-->"+create_group.isEnabled(),Toast.LENGTH_LONG).show();
                    no_group_connection = true;
                    numberOfPlayers = 2;
                    connect(i);
                    break;
                }
            }

    }

    public void updatePeerList(List newPeers)
    {
        //List<String> temp_list = new ArrayList<>(peers);
        peers = new ArrayList<String>();
        peeColors = new ArrayList<String>();
        if ((newPeers == null) || newPeers.size() == 0)
        {
            Toast.makeText(this,"Peerlist changed: now is empty. All peers lost.",Toast.LENGTH_SHORT).show();
            peers.add("");
            peeColors.add("g");
            create_group.setEnabled(false);
            play_with_2.setEnabled(false);
        }
        else
        {
            String current_name;
            for(int i = 0; i < newPeers.size();i++)
            {/**kanonika edw prepei na valw ta proigoumena xrwmata. exei bug.*/
                ///////////test code to be tested.///////////////////////////////
                current_name = ((WifiP2pDevice)peerListListener.getPeers().get(i)).deviceName;
                peers.add(current_name);
/*                if (temp_list.contains(current_name)&& peeColors.get(temp_list.indexOf(current_name)).equals("r"))
                {
                    peeColors.add("r");
                }
                else*/
                peeColors.add("g");/**n ftiaxtei*/
            }
            if (!createdGroup && !no_group_connection)
            {
                create_group.setEnabled(true);
                play_with_2.setEnabled(true);
            }
        }
        if (flag)
        {
            adapterPeers = new ArrayAdapter<String>(this,R.layout.simple_list_item,R.id.text1,peers);
            allPeers.setAdapter(adapterPeers);
            allPeers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if ((peeColors.get(position)).equals("r"))
                    {
                        allPeers.getChildAt(position).setBackgroundColor(Color.GREEN);
                        peeColors.set(position, "g");
                    }
                    else
                    {
                        /**only one item can be selected at a time, some make all the onthers green again(unselected)*/
                        for (int j = 0; j < peeColors.size(); j++)
                        {
                            allPeers.getChildAt(j).setBackgroundColor(Color.GREEN);
                            peeColors.set(j, "g");
                        }
                        /**now make the just selected red...*/
                        allPeers.getChildAt(position).setBackgroundColor(Color.RED);
                        peeColors.set(position, "r");
                    }
                }
            });
        }
    }
    @Override
    protected void onResume()
    {
        super.onResume();
        try {
            receiver = new MyBroadcastReceiver(myManager, myChannel,this);
            this.registerReceiver(receiver, intentFilter);
        }
        catch (Exception e)
        {
            Toast.makeText(this,"Resume failed.",Toast.LENGTH_LONG).show();
        }

    }

    @Override
    protected void onPause() {
        paused=true;
        super.onPause();
        try {
            unregisterReceiver(receiver);
        }
        catch (Exception e)
        {
            Toast.makeText(this,"Pause failed.",Toast.LENGTH_LONG).show();
        }
    }
    @Override
    protected void onDestroy()
    {
        try{
            disconnect();
        }catch (Exception e){

        }
        super.onDestroy();

        if (serverSocket != null) {
            try {
                serverSocket.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
    public void connect(int pos) {
        // Picking the first device found on the network.

        Toast.makeText(this,"Connecting with "+pos+"...",Toast.LENGTH_SHORT).show();
        final WifiP2pDevice device = (WifiP2pDevice)peerListListener.getPeers().get(pos);

        WifiP2pConfig config = new WifiP2pConfig();
        config.deviceAddress = device.deviceAddress;
        config.wps.setup = WpsInfo.PBC;
        //config.groupOwnerIntent=0;/**for group only*/
        myManager.connect(myChannel, config, new WifiP2pManager.ActionListener() {

            @Override
            public void onSuccess() {
                // WiFiDirectBroadcastReceiver will notify us. Ignore for now.
                Toast.makeText(getApplicationContext(), "Connection with "+device.deviceName+" successful!!.", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int reason) {
                Toast.makeText(getApplicationContext(), "Connection with "+device.deviceName+" failed for the reason No. "+reason+". Retry", Toast.LENGTH_SHORT).show();
                play_with_2.setEnabled(true);
                no_group_connection = false; /**???not sure if it works.If it works properly,the onConnectionInfoAvailable*/
                /**method will be never called*/
            }
        });
    }
    public void connect(WifiP2pDevice device) {
        // Picking the first device found on the network.
        if(DoConnect){
            Toast.makeText(this,"connecting with "+device.deviceName,Toast.LENGTH_SHORT).show();

            WifiP2pConfig config = new WifiP2pConfig();
            config.deviceAddress = device.deviceAddress;
            config.wps.setup = WpsInfo.PBC;
            config.groupOwnerIntent=0;
            myManager.connect(myChannel, config, new WifiP2pManager.ActionListener() {

                @Override
                public void onSuccess() {
                    // WiFiDirectBroadcastReceiver will notify us. Ignore for now.
                    Toast.makeText(getApplicationContext(), "Connect success!!.", Toast.LENGTH_SHORT).show();
                    DoConnect=false;
                    //available.setEnabled(false);
                }

                @Override
                public void onFailure(int reason) {
                    Toast.makeText(getApplicationContext(), "Connect failed. Retry.for the reason No. "+reason, Toast.LENGTH_SHORT).show();
                }
            });
        }

    }
    public void players2(View view)
    {
        maxPlayers = 2;
    }
    public void players3(View view)
    {
        maxPlayers = 3;
    }
    public void players4(View view)
    {
        maxPlayers = 4;
    }
    public void create_group1(View view)
    {/**create a group with the selected peers*/
        create_group.setEnabled(false);
        play_with_2.setEnabled(false);/**if this device created a group, it shouldn't be able to connect directly with I device.*/
        numberOfPlayers = maxPlayers;
        if(numberOfPlayers>=2 && numberOfPlayers<=4){
            create_group2();
            //Toast.makeText(this,"just test: peerlist--> "+peers.toString()+"  and colors--> "+peeColors.toString(),Toast.LENGTH_LONG).show();

            Map<String, String> record = new HashMap<String, String>();
            record.put("available", "visible");
            WifiP2pDnsSdServiceInfo service = WifiP2pDnsSdServiceInfo.newInstance("_group", "_presence._tcp", record);

            // Add the local service, sending the service info, network channel,
            // and listener that will be used to indicate success or failure of
            // the request.

            myManager.addLocalService(myChannel, service,
                    new WifiP2pManager.ActionListener() {
                        @Override
                        public void onSuccess() {
                            Toast.makeText(getApplicationContext(),"Created group with "+numberOfPlayers+" players",Toast.LENGTH_LONG).show();
                        }

                        @Override
                        public void onFailure(int errorCode) {
                            Toast.makeText(getApplicationContext(),"Creating group service failed ",Toast.LENGTH_LONG).show();
                        }
                    });
        }else{
            Toast.makeText(getApplicationContext(),"Number of players out of bounds, choose between 2-4",Toast.LENGTH_LONG).show();

        }

    }
    public void remove_service(){

            Map<String, String> record = new HashMap<String, String>();
            record.put("available", "visible");
            WifiP2pDnsSdServiceInfo service = WifiP2pDnsSdServiceInfo.newInstance("_group", "_presence._tcp", record);

            // Add the local service, sending the service info, network channel,
            // and listener that will be used to indicate success or failure of
            // the request.

            myManager.removeLocalService(myChannel, service,
                    new WifiP2pManager.ActionListener() {
                        @Override
                        public void onSuccess() {
                        }

                        @Override
                        public void onFailure(int errorCode) {
                        }
                    });


    }

    public void create_group2(){
        createdGroup=true;
        myManager.createGroup(myChannel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Toast.makeText(getApplicationContext(), "Wait", Toast.LENGTH_SHORT).show();
                justCreatedGroup=true;

            }

            @Override
            public void onFailure(int reason) {
                Toast.makeText(getApplicationContext(), "Group creation failed. Retry.", Toast.LENGTH_SHORT).show();
                create_group.setEnabled(true);
            }
        });
    }

    public static void disconnect(){
        try{
            myManager.removeGroup(myChannel, new WifiP2pManager.ActionListener() {
                @Override
                public void onSuccess() {
                    Log.i(TAG,"disconnected !");
                }

                @Override
                public void onFailure(int reason) {
                    Log.i(TAG,"still connected for reason="+reason);

                }
            });
        }catch (Exception e){

        }



    }


}
