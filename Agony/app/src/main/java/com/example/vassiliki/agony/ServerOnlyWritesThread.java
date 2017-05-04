package com.example.vassiliki.agony;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by joao on 31-05-2016.
 */
public class ServerOnlyWritesThread extends Thread {
    Context context;
    ServerSocket serverSocket;
    Socket client;
    String test_string;
    Game game;
    Handler handler;
    Handler debugging;
    int n;
    int priority; /**always 0 for the server, but will be sent as an arument anyway*/

    public ServerOnlyWritesThread(Context c,ServerSocket ss,Socket s,Game g,int p,Handler h,Handler h2)
    {
        super();
        context = c;
        serverSocket = ss;
        client = s;
        test_string = " arxi ";
        game = g;
        priority = p+1;
        handler = h;
        debugging = h2;
        n=g.numberOfPlayers;
        //Toast.makeText(context," Successfull creation!!! Me prior= "+priority,Toast.LENGTH_LONG).show();
    }

    public void run()
    {
        try {
            if (client != null && serverSocket != null )
            {
                PlayGame.OutOfServer.get(priority - 1).reset();
                PlayGame.OutOfServer.get(priority-1).writeObject(game);

            }
            else
            {
                test_string += " kati itan null: ";
            }
        }
        catch (Exception e)
        {
            ////////////////////////////////////////
            test_string+=e.toString();
            Message msg = debugging.obtainMessage();
            Bundle bundle = new Bundle();
            bundle.putSerializable("nothing", test_string);
            msg.setData(bundle);
            debugging.sendMessage(msg);
            ////////////////////////////////////////
        }
    }
}

