package com.example.vassiliki.agony;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.NetworkOnMainThreadException;
import android.widget.Toast;

import java.io.DataInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OptionalDataException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Vassiliki on 1/5/2016.
 */
public class ServerAsyncTask extends AsyncTask<Game,Void,Game> {
    Context context;
    InetAddress address;
    int numberOfClients;
    List<Socket> client;
    public static boolean flag=true;

    public ServerAsyncTask(Context c,InetAddress ad, int n)
    {
        super();
        this.context = c;
        this.address =ad;
        this.numberOfClients=n;
    }

    @Override
    protected Game doInBackground(Game... params) {
        ServerSocket serverSocket = null;

        String clients_upper = null;
        String received = null;
        try {
            /**
             * Create a server socket and wait for client connections. This
             * call blocks until a connection is accepted from a client
             */

            serverSocket= MainActivity.serverSocket;
             client = new ArrayList<>();
            List<ObjectOutputStream> objectOutputStream = new ArrayList<>();
            MyConnectionInfoListener.num_of_clients++;
            int connectedClients=MyConnectionInfoListener.num_of_clients;
            //numberOfClients=connectedClients+1;
            flag=true;
            for(int i=1; i<numberOfClients; i++){

                Socket aux = serverSocket.accept();
                client.add(aux);
            }
            flag=false;
            for(int i=1; i<numberOfClients; i++){
                ObjectOutputStream obj_aux = new ObjectOutputStream(client.get(i-1).getOutputStream());
                objectOutputStream.add(obj_aux);
            }
            if (client != null && serverSocket != null && objectOutputStream != null) /** && inputstream != null*/
            {
                try {

                    int priority=1;
                    params[0].set_numberOfPlayers(numberOfClients);
                    for(int i=1; i<numberOfClients; i++){
                        params[0].set_prior(priority);
                        objectOutputStream.get(i-1).writeObject(params[0]);
                        priority++;
                    }
                    //clients_upper = "duo";
                    //break;
                    //}
                    //}
                }
                catch (NullPointerException e)
                {
                    //Toast.makeText(context,"Error!!",Toast.LENGTH_LONG).show();
                    clients_upper += "NullPointerException";
                }
  /*              catch (ClassNotFoundException e)
                {
                    clients_upper += "ClassNotFoundException";
                }*/
                catch (OptionalDataException e)
                {
                    clients_upper += "OptionalDataException";
                }
                catch (Exception e)
                {
                    //Toast.makeText(context,"Error!!",Toast.LENGTH_LONG).show();
                    clients_upper += "exe";
                }
            }
            for(int i=1; i<numberOfClients; i++){
                objectOutputStream.get(i-1).close();
            }
            /*if (inputstream != null)
                inputstream.close();*/
        }
        catch (NetworkOnMainThreadException e)
        {
            //Toast.makeText(this.context,"Network...thread exception",Toast.LENGTH_LONG).show();
            clients_upper += "NetworkOnMainThreadException";
            //return null;
        }
        catch (IOException e)
        {
            // Toast.makeText(this.context,"IO exception",Toast.LENGTH_LONG).show();
            clients_upper += "IOexeption";
            // return null;
        }
        catch (Exception e)
        {
            //Toast.makeText(this.context,"General exception",Toast.LENGTH_LONG).show();
            clients_upper += "Exception";
            //return null;
        }
        finally {
            if (serverSocket != null)
            {
                try {
                    serverSocket.close();
                }
                catch (IOException e)
                {
                    Toast.makeText(this.context,"IO Error in server's socket in server",Toast.LENGTH_LONG).show();
                }
                catch (Exception e)
                {
                    Toast.makeText(this.context,"Error",Toast.LENGTH_LONG).show();
                }
            }
            if (client != null)
            {
                for(int i=1; i<client.size(); i++){

                    try {
                        if (client.get(i).isConnected())
                            client.get(i).close();
                    }
                    catch (Exception e)
                    {

                    }
                }
            }
        }
        return params[0];
    }
    protected void onPostExecute(Game result) {
        if (result != null)
        {
/*            if (result.equals("b"))
                Toast.makeText(this.context,"b __>egine accepted!!! but then..",Toast.LENGTH_LONG).show();
            else if (result.equals("c"))
                Toast.makeText(this.context,"c--> sunedese to input",Toast.LENGTH_LONG).show();
            else if (result.equals("d"))
                Toast.makeText(this.context,"d--> dimiourgise to output",Toast.LENGTH_LONG).show();
            else if (result.equals("lala"))
                Toast.makeText(this.context,"lala--> mesa sto while",Toast.LENGTH_LONG).show();
            else
                Toast.makeText(context,"asynctask!!! Douepse to timimeno! From the client, the game upper card: "+result,Toast.LENGTH_LONG).show();*/
            MyConnectionInfoListener.ready=false;
            //Toast.makeText(context,"asynctask!!! Douepse to timimeno! the game upper card: "+result.get_face_up_card(),Toast.LENGTH_LONG).show();
            Intent intent = new Intent(context,PlayGame.class);
            intent.putExtra("Game", result);
            intent.putExtra("owner",true);
            intent.putExtra("inet",address);
            ///a priority number for each player
            intent.putExtra("turn",0);

            //Toast.makeText(context," RESULT: "+result,Toast.LENGTH_LONG).show();
            context.startActivity(intent);
        }
        else
        {
            Toast.makeText(context,"tzifos...einai null to result: ",Toast.LENGTH_LONG).show();
        }
    }
}