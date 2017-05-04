
package com.example.vassiliki.agony;

        import android.content.Context;

        import java.io.ObjectOutputStream;
        import java.net.Socket;

/**
 * Created by joao on 29-05-2016.
 */
public class BroadcastToAClient extends Thread {
    Context context;
    Socket socket;
    int priority, index;
    String test_string = "";
    Game game;

    public BroadcastToAClient(Context c,Socket s,int pri,Game g, int j)
    {
        super();
        context = c;
        socket = s;
        priority = pri;
        game = g;
        index=j;
    }

    public void run()
    {
        if (socket != null)
        {
            try {
                //Thread.sleep(priority*100);
               // ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
                test_string += "socket != null...new out..";
                PlayGame.OutOfServer.get(index).writeObject(game);
                test_string += " object_written, ";
                //objectOutputStream.close();

            }
            catch (Exception e)
            {

            }
        }
    }
}