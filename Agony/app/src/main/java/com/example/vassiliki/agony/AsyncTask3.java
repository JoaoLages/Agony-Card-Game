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

/**
 * Created by Vassiliki on 24/5/2016.
 */
public class AsyncTask3 extends AsyncTask<InetAddress,Void,String>  {
    Context context;
    String teststring;
    Handler handler;
    int priority;
    Handler handler2;

    public AsyncTask3(Context c,Handler h,int p,Handler h2)
    {
        super();
        context = c;
        teststring = null;
        handler = h;
        priority = p;
        handler2 = h2;
    }
    @Override
    protected String doInBackground(InetAddress... params) {
        /**client code*/
        Socket sending_socket = null;

        try {
		
            sending_socket = new Socket();
            teststring += " socket created...";
            sending_socket.bind(null);
            teststring += "bind..";
		    //Thread.sleep(priority*100);
            sending_socket.connect((new InetSocketAddress(params[0], 8888)), 500);
            teststring += "connected...";
            if (sending_socket != null ) /**&& objectOutputStream != null */ {
                teststring += " NOT null to socket";
                //((PlayGame)context).client_sending = sending_socket;/**sending socket*/
                PlayGame.client_sending = sending_socket;
                PlayGame.InToClient = new ObjectInputStream(sending_socket.getInputStream());
                teststring += " kai assign sto playgame";
                PlayGame.OutOfClient = new ObjectOutputStream(sending_socket.getOutputStream());

                ClientThread clientThread = new ClientThread(context,handler,sending_socket,priority,handler2);
                teststring += " client thread creation ";
                clientThread.start();
                teststring += "  kai started ";
            }
            else
                teststring += "null...";
        }
        catch (Exception e)
        {
            teststring += " EXCEPTION !";
        }
        return teststring;
    }

    protected void onPostExecute(String result)
    {
    }
}
