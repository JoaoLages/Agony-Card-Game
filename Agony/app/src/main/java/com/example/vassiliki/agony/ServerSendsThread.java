package com.example.vassiliki.agony;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.gsm.GsmCellLocation;
import android.widget.Toast;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by Vassiliki on 24/5/2016.
 */
public class ServerSendsThread extends Thread {
    Context context;
    ServerSocket serverSocket;
    Socket client;
    String test_string;
    Game game;
    Handler handler;
    Handler debugging;
    int counter = 0;
    int n;
    int priority; /**always 0 for the server, but will be sent as an arument anyway*/

    public ServerSendsThread(Context c,ServerSocket ss,Socket s,Game g,int p,Handler h,Handler h2)
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
                test_string = " I WILL WRITE TO CLIENT for the "+counter+" time! prior = "+priority;
                ////////////////////////////////////////
               // ObjectOutputStream objectOutputStream = new ObjectOutputStream(client.getOutputStream());
                test_string += "out_obj_created, ";
                counter++;

                test_string =" i WILL SEND TURN = "+game.get_turn();
                PlayGame.round++;
                PlayGame.OutOfServer.get(priority - 1).reset();
                PlayGame.OutOfServer.get(priority-1).writeObject(game);
               // objectOutputStream.close();
                //objectOutputStream.reset();
                test_string += " object_written, ";

                //ObjectInputStream objectInputStream;

                //if (game.get_turn() == priority)
                //while (game.get_turn() != 0)
                //{
                    test_string += " this is not my turn, it is "+game.get_turn();
                    //objectInputStream = new ObjectInputStream(client.getInputStream());
                    if(game.get_turn()==priority && (game.winner == null)) {
                        test_string += " I WILL READ FROM CLIENT "+priority;

                        try {
                            game = (Game) PlayGame.IntoServer.get(priority-1).readObject();
                        }
                        catch (Exception e)
                        {
                            test_string += " ...";
                        }

                        test_string += " I JUST READ FROM CLIENT "+priority+" and n="+n;
                        ////////////////////////////////////////


                        //send the new game obj to all clients
                        for (int j = 0; j < PlayGame.server_send_client.size(); j++) {

                            test_string += " I WILL SEND TO CLIENT "+j;
                            ////////////////////////////////////////


                            BroadcastToAClient broadcastToAClient = new BroadcastToAClient(context, PlayGame.server_send_client.get(j), j + 1, game, j);
                            broadcastToAClient.start();
                        }
                        //send the new game obj to all clients
                        Message msg = handler.obtainMessage();
                        test_string += "message_obtained";
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("myGame", game);
                        msg.setData(bundle);
                        handler.sendMessage(msg);
                        test_string += "message_send";

                        if (game.get_turn() !=  0 && (game.winner == null))
                        {
                            PlayGame.serverOnlyReadsThread = new ServerOnlyReadsThread(context,serverSocket,PlayGame.server_send_client.get(game.get_turn()-1),game,game.get_turn(),handler,debugging);
                            PlayGame.serverOnlyReadsThread.start();
                        }

                   // }

                    /*
                    game = (Game)objectInputStream.readObject();
                    //send the new game obj to all clients

                    ////////////////////////////////////////
                    Message msg = handler.obtainMessage();
                    test_string += "message_obtained";
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("myGame", game);
                    msg.setData(bundle);
                    handler.sendMessage(msg);
                    test_string += "message_send";
                    ////////////////////////////////////////*/
                }
                test_string += " OUT OF WHILE";

                ////////////////////////////////////////

            }
            else
            {
                test_string += " kati itan null: ";
              /*  test_string += client.toString();
                test_string += " ...kai..";
                test_string += serverSocket.toString();*/
            }
        }
        catch (Exception e)
        {
            //Toast.makeText(context,"Exeption: "+test_string,Toast.LENGTH_LONG).show();
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
