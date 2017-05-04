package com.example.vassiliki.agony;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import java.io.ObjectInputStream;
import java.net.Socket;

/**
 * Created by Vassiliki on 24/5/2016.
 */
public class ClientThread extends Thread {
    Handler handler;
    Context context;
    Socket socket;
    String teststring;
    int priority;
    Game anewgame = null;
    Handler debugging;

    public ClientThread(Context c, Handler h,Socket sock,int prior,Handler h2)
    {
        super();
        context = c;
        handler = h;
        socket = sock;
        teststring = "";
        priority = prior;
        debugging = h2;
    }

    public void run()
    {
        try {
            teststring+= "beginning";
           // ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
            teststring += " created in obj ,";
            if (socket != null)
            {
                teststring += " NOT NULL";
                do {
                    teststring += " I will read from thge server."+"AND MY PRIORITY IS "+priority;
                    ////////////////////////////////////////
                    ////////////////////////////////////////
                    anewgame = (Game)PlayGame.InToClient.readObject();
                    teststring = " I JUST RECEIVED: TURN = "+anewgame.get_turn();
                    PlayGame.round++;

                    teststring += " NEXT PLAYER IS: "+anewgame.get_turn()+" AND MY PRIORITY IS "+priority;
                    ////////////////////////////////////////
                    /////////////////pass_it_to_the_activity///////////////////////
                    Message msg = handler.obtainMessage();
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("myGame", anewgame);
                    msg.setData(bundle);
                    handler.sendMessage(msg);
                    ////////////////////////////////////////
                } while ( (anewgame.get_turn() != priority) && (anewgame.winner == null));
             //   objectInputStream.close();
            }
        }
        catch (Exception e)
        {
            teststring+=e.toString();
            ////////////////////////////////////////
            Message msg7 = debugging.obtainMessage();
            Bundle bundle7 = new Bundle();
            bundle7.putSerializable("nothing", teststring);
            msg7.setData(bundle7);
            debugging.sendMessage(msg7);
            ////////////////////////////////////////
        }
    }
}
