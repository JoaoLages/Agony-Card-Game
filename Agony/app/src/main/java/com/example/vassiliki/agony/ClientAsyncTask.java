package com.example.vassiliki.agony;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.NetworkOnMainThreadException;
import android.widget.Toast;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.UnknownHostException;

/**
 * Created by Vassiliki on 7/5/2016.
 */
public class ClientAsyncTask extends AsyncTask<InetAddress,Void,Game> {
    Context mainActivity;
    InetAddress inetAddress;
    int priority;
    public ClientAsyncTask(Context c)
    {
        this.mainActivity = c;
    }
    @Override
    protected Game doInBackground(InetAddress... params) {
        Socket socket = null;
        String test_string = null;
        Game received_g = null;
        try {
            ////////vassiliki test code/////////////////////////////
            /**
             * Create a client socket with the host,
             * port, and timeout information.
             */
            socket = new Socket();
            socket.bind(null);
            test_string = "a";
            inetAddress = params[0];
            socket.connect((new InetSocketAddress(params[0], 8888)), 1000000000);

            test_string = "b";
            /**
             * Create a byte stream from a JPEG file and pipe it to the output stream
             * of the socket. This data will be retrieved by the server device.
             */
            //ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream objectInputStream = null;
            if (socket != null ) /**&& objectOutputStream != null */
            {
                 objectInputStream = new ObjectInputStream(socket.getInputStream());
                try {

                    received_g = (Game)objectInputStream.readObject();
                    priority=received_g.aux_prior;
                    //while (test_string == null) {
                    //received_g = (Game)objectInputStream.readObject();
                    //}
                    test_string = received_g.get_face_up_card();
                    /*while (test_string == null)
                    {
                        //nothing
                        test_string = (String)objectInputStream.readObject();
                        test_string  += "inwhile";
                    }*/
                }
                catch (UnknownHostException e) {
                    test_string += "UnknownHostException";
                }
                catch (NetworkOnMainThreadException e)
                {
                    test_string += "NetworkOnMainThreadException";
                    //Toast.makeText(this.mainActivity,"Network...thread exception",Toast.LENGTH_LONG).show();
                }
                catch (IOException e)
                {
                    test_string += "IOException";
                    //Toast.makeText(this.mainActivity,"IO exception",Toast.LENGTH_LONG).show();
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                    test_string = "GeneralException";
                    //Toast.makeText(this.mainActivity,"General exception",Toast.LENGTH_LONG).show();
                }
            }
            objectInputStream.close();
            //objectOutputStream.close();
            ////////vassiliki end/////////////////////////////////////////////
        }
        catch (Exception e)
        {
            //Toast.makeText(mainActivity,"Error!!",Toast.LENGTH_LONG).show();
        }
        /**
         * Clean up any open sockets when done
         * transferring or if an exception occurred.
         */
        finally {
            if (socket != null) {
                if (socket.isConnected()) {
                    try {
                        socket.close();
                    } catch (IOException e) {
                        //catch logic
                    }
                    catch (Exception e) {
                        //catch logic
                    }
                }
            }
        }
        /*if (test_game == null)
            return test_string;
        else
            return test_game.get_face_up_card();*/
        return received_g;
    }
    protected void onPostExecute(Game result) {
        if (result != null)
        {

            try {

                Intent intent = new Intent(mainActivity, PlayGame.class);
                intent.putExtra("Game", result);
                intent.putExtra("owner", false);
                intent.putExtra("turn", this.priority);
                intent.putExtra("inet", inetAddress);
                mainActivity.startActivity(intent);
            }catch (Exception e){
                Toast.makeText(mainActivity,"Exception="+e.toString() , Toast.LENGTH_LONG).show();

            }

        }
        else
        {
            Toast.makeText(mainActivity,"the result is null: ",Toast.LENGTH_LONG).show();
        }
    }
}