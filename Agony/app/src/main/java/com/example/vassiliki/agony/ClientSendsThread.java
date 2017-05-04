package com.example.vassiliki.agony;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * Created by Vassiliki on 25/5/2016.
 */
public class ClientSendsThread extends Thread {
    Context context;
    Socket socket;
    int priority;
    String test_string = "START";
    Game game;
    Handler debugging;

    public ClientSendsThread(Context c,Socket s,int pri,Game g,Handler h)
    {
        super();
        context = c;
        socket = s;
        priority = pri;
        game = g;
        debugging = h;
    }

    public void run()
    {
        if (socket != null)
        {
            try {
               // ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
                test_string += "socket != null...I will send to SERVER new turn = "+game.get_turn();
                PlayGame.OutOfClient.reset();
                PlayGame.OutOfClient.writeObject(game);
                test_string += " object_written, ";
               // objectOutputStream.close();


            }
            catch (Exception e)
            {
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
}
