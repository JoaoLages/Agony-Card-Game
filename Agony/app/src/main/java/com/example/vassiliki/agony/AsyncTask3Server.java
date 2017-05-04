package com.example.vassiliki.agony;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.widget.Toast;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Created by Vassiliki on 28/5/2016.
 */
public class AsyncTask3Server extends AsyncTask <InetAddress,Void,String>  {
    Context context;
    String teststring;
    Handler handler;
    int priority;
    int times_exe;

    public AsyncTask3Server( Context c,Handler h,int p,int times)
    {
        super();
        context = c;
        teststring = null;
        handler = h;
        priority = p;
        times_exe = times;
    }
    @Override
    protected String doInBackground(InetAddress... params) {
        try {
            if (times_exe == 0)
            {
                PlayGame.serverSending = new ServerSocket(8888);
                teststring += " prwti fora ara cteated new server socket";
                PlayGame.OutOfServer = new ArrayList<>();
                PlayGame.IntoServer = new ArrayList<>();
            }
            teststring += " cteated new socket";
            for (int j = 0; j < PlayGame.game.numberOfPlayers - 1; j++){
                Socket client = PlayGame.serverSending .accept();

                teststring += " accepted";
                //((PlayGame)context).serverSending = serverSocket;
                teststring += ".. kai assigned";
                //((PlayGame)context).server_send_client = client;
                PlayGame.server_send_client.add(client);
                ObjectOutputStream obj = new ObjectOutputStream(client.getOutputStream());
                teststring += " to idio kai to client tou";
                PlayGame.OutOfServer.add(obj);
                ObjectInputStream in = new ObjectInputStream(client.getInputStream());
                PlayGame.IntoServer.add(in);
            }

        }
        catch (Exception e)
        {
            teststring += " exeption";
        }
        return teststring;
    }

    protected void onPostExecute(String result)
    {
        //Toast.makeText(context," RESULT: "+result,Toast.LENGTH_LONG).show();
    }
}
