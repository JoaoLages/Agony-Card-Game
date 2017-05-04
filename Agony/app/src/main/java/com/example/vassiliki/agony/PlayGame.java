package com.example.vassiliki.agony;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OptionalDataException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;


public class PlayGame extends AppCompatActivity {
    static final String STATE_CLIENT = "client_flag";
    static final String STATE_SERVER = "server_flag";

    public static String test_string = null;
    public static volatile Game game;
    ImageView face_up, face_down;
    private LinearLayout players_cards;
    private TableLayout players_list;
    private TextView playing_sign;
    private PlayerState state;
    private TextView suit_view;
    private Button pass1, seven;
    TableRow row1 ;
    TableRow row0;
    View num_of_cards;
    RelativeLayout background;
    public boolean isOwner;
    public InetAddress address;
    private AsyncTask3 asyncTask3;
    private AsyncTask3Server asyncTask3Server;
    public static Socket client_sending;
    public static volatile List<ObjectOutputStream> OutOfServer;
    public static volatile List<ObjectInputStream> IntoServer;
    public static volatile ObjectOutputStream OutOfClient;
    public static volatile ObjectInputStream InToClient;
    private ServerSendsThread serverSendsThread;
    private ClientThread clientThread;
    public static  ServerOnlyReadsThread serverOnlyReadsThread;

    public static volatile int round = 0;

    public static volatile List<Socket> server_send_client;
    public static volatile ServerSocket serverSending;/**this and server_send_client is for the server to send*/
    private HorizontalScrollView signs;
    private boolean very_first_time, picked_once, pick_cards_by7,passed;/**shows if the player has already picked a card from the face down pale*/
    private boolean played_once; /**a variable for 8,to show if player has already played once*/
    public enum AllCards {
        s1,s2,s3,s4,s5,s6,s7,s8,s9,s10,s11,s12,s13,d1,d2,d3,d4,d5,d6,d7,d8,d9,d10,d11,d12,d13,c1,c2,c3,c4,c5,c6,c7,c8,c9,c10,c11,c12,c13,h1,h2,h3,h4,h5,h6,h7,h8,h9,h10,h11,h12,h13
    }
    Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                Bundle bundle = msg.getData();
                game = (Game)bundle.getSerializable("myGame");
                //update
                updateGame();
        }
    };
    //////////////debugging//////////////////////
    Handler handler2 = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Bundle bundle2 = msg.getData();
            String mymessage = (String)bundle2.getString("nothing");
            Toast.makeText(getApplicationContext()," UP TO HERE: "+mymessage,Toast.LENGTH_LONG).show();
        }
    };
    ////////////end_debugging////////////////////
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save the current THREADS state
        if (clientThread != null)
        {
            if (clientThread.isAlive())
                savedInstanceState.putBoolean(STATE_CLIENT, true);
        }
        if (serverSendsThread != null)
        {
            if (serverSendsThread.isAlive())
            {
                savedInstanceState.putBoolean(STATE_SERVER, true);
            }

        }

        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    protected void onStop()
    {/***/
        super.onStop();
        /**client*/
        if (client_sending != null) {
            if (client_sending.isConnected()) {
                try {
                    client_sending.close();
                    //Toast.makeText(this,"Closed client's socket.",Toast.LENGTH_LONG).show();
                } catch (IOException e) {
                    Toast.makeText(this,"IO Error in client's socket in server",Toast.LENGTH_LONG).show();
                }
                catch (Exception e) {
                    Toast.makeText(this,"Error in server's socket in server",Toast.LENGTH_LONG).show();
                }
            }
        }

        /**server*/
        if (serverSending != null)
        {
            try {
                serverSending.close();
            }
            catch (IOException e)
            {
                Toast.makeText(this,"IO Error in server's socket in server",Toast.LENGTH_LONG).show();
            }
            catch (Exception e)
            {
                Toast.makeText(this,"Error in server's socket in server",Toast.LENGTH_LONG).show();
            }
        }
        if (server_send_client != null)
        {
            for (int i = 0; i < server_send_client.size(); i++)
            {
                if (server_send_client.get(i) != null)
                {
                    try {
                        server_send_client.get(i).close();
                    }
                    catch (IOException e)
                    {
                        Toast.makeText(this,"IO Error in client'socket in server.",Toast.LENGTH_LONG).show();
                    }
                    catch (Exception e)
                    {
                        Toast.makeText(this,"Error in client'socket in server.",Toast.LENGTH_LONG).show();
                    }
                }
            }
        }
        if (OutOfClient != null)
        {
            try {
                OutOfClient.close();
            }
            catch (Exception e)
            {

            }
        }
        if (InToClient != null)
        {
            try {
                InToClient.close();
            }
            catch (Exception e)
            {

            }
        }
        if (OutOfServer != null)
        {
            for (int j = 0; j < OutOfServer.size(); j++)
            {
                try {
                    OutOfServer.get(j).close();
                }
                catch (Exception e)
                {

                }
            }
        }
        if (IntoServer != null)
        {
            for (int j = 0; j < IntoServer.size(); j++)
            {
                try {
                    IntoServer.get(j).close();
                }
                catch (Exception e)
                {

                }
            }
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_game);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        // Check whether we're recreating a previously destroyed instance
        /**if (savedInstanceState != null) {
            Toast.makeText(this,"Recreated!!",Toast.LENGTH_LONG).show();
            boolean clie = savedInstanceState.getBoolean(STATE_CLIENT);
            boolean serv = savedInstanceState.getBoolean(STATE_SERVER);
            if (clie)
            {
                try {
                    if (!clientThread.isAlive())
                    {
                        Toast.makeText(this," Yes, client thread was running but now not alive.",Toast.LENGTH_LONG).show();
                        clientThread = new ClientThread(this,handler,client_sending,state.priority_number,handler2);
                        clientThread.start();
                    }
                }
                catch (Exception e)
                {

                }

            }
            if (serv)
            {
                try {
                    if (!serverSendsThread.isAlive())
                    {
                        Toast.makeText(this,"Yes, Server thread was running..Now not alive.",Toast.LENGTH_LONG).show();
                        serverOnlyReadsThread = new ServerOnlyReadsThread(this,serverSending,server_send_client.get(game.get_turn()),game,game.get_turn(),handler,handler2);
                        serverOnlyReadsThread.start();
                    }
                }
                catch (Exception e)
                {

                }

            }
        }*/

        Bundle bundle = new Bundle(this.getIntent().getExtras());
        game = (Game)bundle.get("Game");
        //Toast.makeText(this," face down: "+game.face_down_pack.toString(),Toast.LENGTH_LONG).show();
        int prior = bundle.getInt("turn");

        isOwner = bundle.getBoolean("owner");
        address = (InetAddress)bundle.get("inet");

        state = new PlayerState(game,prior,game.dealCards(prior));/**to be changed.Playstate is created after the broadcast of the game*/
        server_send_client = new ArrayList<>(game.numberOfPlayers);
        //Toast.makeText(this," cards: "+state.getPlayersCards().toString(),Toast.LENGTH_LONG).show();
        //Toast.makeText(this," TEST! PRIORITY NUMBER: "+state.priority_number+" kai isOwnwer? = "+isOwner, Toast.LENGTH_LONG).show();
        ////////////accepting data thread/////////////////////
        try {
            if (isOwner)
            {
                //for (int j = 0; j < game.numberOfPlayers - 1; j++)
                //{
                    asyncTask3Server = new AsyncTask3Server(this,handler,state.priority_number,0);
                    //Toast.makeText(this,"Created async3 with priority = "+state.priority_number,Toast.LENGTH_LONG).show();
                    asyncTask3Server.execute(address);
                    //Toast.makeText(this, " Created a server thread!! The No. "+j, Toast.LENGTH_LONG).show();
               // }
                Thread.sleep(250*(game.numberOfPlayers));
            }
            else
            {
                Thread.sleep(250*prior);
                asyncTask3 = new AsyncTask3(this,handler,state.priority_number,handler2);

                //Toast.makeText(this, " Created the client thread!!", Toast.LENGTH_LONG).show();
                //Toast.makeText(this,"Created async3 with priority = "+state.priority_number,Toast.LENGTH_LONG).show();
                asyncTask3.execute(address);
            }
        }
        catch (Exception e)
        {
            Toast.makeText(this, " Problem me forever running thread!!!", Toast.LENGTH_LONG).show();
        }
        //Toast.makeText(this,  " prior=" + prior+"="+state.priority_number, Toast.LENGTH_LONG).show();

        //Toast.makeText(this, " Ola kala me to forever running thread!!!", Toast.LENGTH_LONG).show();
        ////////////end of thread code////////////////////////
        background = (RelativeLayout)findViewById(R.id.game);
        face_up = (ImageView)findViewById(R.id.face_up_card);
        players_cards = (LinearLayout)findViewById(R.id.players_cards);
        playing_sign = (TextView)findViewById(R.id.playing);
        suit_view = (TextView)findViewById(R.id.suit_sign2);/**the sign that presents the suit that the players should follow*/
        signs = (HorizontalScrollView)findViewById(R.id.suit_sign);
        signs.setVisibility(View.INVISIBLE);
        update_ui_uppersuit(game.getUpper_card_suit());/**update it according the corresponding variable*/
        update_ui();
        /**to be removed from here...test code for the creation of imageviews*/
        pick_cards_by7 = false;
        passed = false;
        players_list = (TableLayout)findViewById(R.id.players_layout);
        row1 = (TableRow)players_list.getChildAt(1);
        row0 = (TableRow)players_list.getChildAt(0);
        face_down = (ImageView)findViewById(R.id.face_down_pale);
        for (int i = 0; i < state.getPlayersCards().size();i++)
        {/**displays the card images in imageviews for all cards in player's cards set*/
            addCardToPersonalSet(state.getPlayersCards().get(i));
        }
        for (int i = 0;i < game.numberOfPlayers;i++)
        {/**creates textview for each player*/
            TextView tx = new TextView(this);
            TextView num = new TextView(this);
            tx.setText(game.players.get(i));
            num.setText("7");
            if (state.priority_number == i)
            {
                num.setTextColor(Color.RED);
                //num.setTypeface(null, Typeface.BOLD);
                //tx.setTypeface(null, Typeface.BOLD);
            }
            else
            {
                num.setTextColor(Color.BLACK);
            }
            if (i == game.turn)
                tx.setTextColor(Color.GREEN);
            TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,TableRow.LayoutParams.WRAP_CONTENT);
            lp.setMargins(0,10,10,5);
            tx.setLayoutParams(lp);
            num.setLayoutParams(lp);/**peritto*/
            row1.addView(tx);
            row0.addView(num);
        }
        pass1 = (Button)findViewById(R.id.pass);
        pass1.setEnabled(false);
        seven = (Button)findViewById(R.id.not_play_7);
        seven.setVisibility(View.INVISIBLE);
        //View player_child = row1.getChildAt(game.get_turn());
        /*if (state.priority_number == game.turn)
            ((TextView)player_child).setTextColor(Color.GREEN);
        else
            ((TextView)player_child).setTextColor(Color.BLACK);*/
        //for ()
        num_of_cards = row0.getChildAt(state.priority_number);
        //players_cards.removeAllViews();/**sos this is the code that removes all imageviews from the linearlayout*/
        very_first_time = true;
        played_once = false;
        picked_once = false;
        //create new thread to receive forever new game objects

        applyTheRules(); /**apply the rules for the first time*/
        ///end of test code
    }
    private void updateGame()
    {
        TextView player_child;
        int num;
        //game = the new game object received
        update_ui_uppersuit(game.getUpper_card_suit());/**update it according the corresponding variable*/
        update_ui();
        for (int i = 0; i < game.numberOfPlayers;i++)
        {
            player_child = (TextView)row0.getChildAt(i);
            TextView player_child_name = (TextView)row1.getChildAt(i);
            if (i == game.get_turn())
            {
                //Toast.makeText(this,"einai i seira tou "+game.get_turn(),Toast.LENGTH_LONG).show();
                player_child_name.setTextColor(Color.GREEN);
            }
            else
                player_child_name.setTextColor(Color.BLACK);
            num = game.num_of_players_cards.get(i);
            //Toast.makeText(this," num of player No."+i+" is "+num,Toast.LENGTH_LONG).show();
            player_child.setText(Integer.toString(num));
            if (num == 0)
            {
                /*if (game.winner == null)
                {
                    finish_it();
                    game.winner = game.players.get(i);
                }*/
                game.winner = game.players.get(i);
            }
        }
        if (game.winner == null)
            applyTheRules();
        else
        {
            finish_it();
        }
    }
    private void finish_it()
    {
        Toast.makeText(this,"WINNER: "+game.winner+" . GAME OVER",Toast.LENGTH_LONG).show();
        Intent intent = new Intent(this,Winner.class);
        intent.putExtra("winner", game.winner);
        intent.putExtra("thisPlayer", game.players.get(state.priority_number));
        try {
            startActivity(intent);
            //finish();
        }
        catch (Exception e)
        {

        }
    }
    public void pickACard(View view)
    {/**called when a player touches the face down pale. The player picks the upper card of that pale, and places it in his own set*/
        pickingCardProcess();
    }
    private void pickingCardProcess()
    {/**does the requred actions for picking a new card.*/
        String newcard = game.remove_upper_downpale();
        //Toast.makeText(this," new down pale: "+game.face_down_pack.toString(),Toast.LENGTH_LONG).show();
        if (newcard.equals(""))
        {
            Toast.makeText(this,"Game Over",Toast.LENGTH_LONG).show();
        }
        else
        {
            state.addToPlayersCards(newcard);/**add the new card to the list of the player's cards*/
            addCardToPersonalSet(newcard);/**show at the user interface*/
            changeTheNumberOfPersonalCards();/**update the textview that shows this player's number of cards*/
        }
        if (!picked_once && !pick_cards_by7)
        {
            picked_once = true;
            pass1.setEnabled(true);
        }
    }
    private void changeTheNumberOfPersonalCards()
    {
        int new_num = state.getNumberOfCards();
        game.num_of_players_cards.set(state.priority_number, new_num);/**update the list*/
        //Toast.makeText(this," current num of cards: "+new_num,Toast.LENGTH_LONG).show();
        //new_num++; /**increase by 1 the number of cards(of this player)*/
        //num_of_cards = row0.getChildAt(state.priority_number);
        try {
            ((TextView)num_of_cards).setText(Integer.toString(new_num));
        }
        catch (Exception e)
        {
        }
        // View number1 = row0.getChildAt(state.priority_number);
        //((TextView)number1).setTextColor(Color.GREEN);
        //((TextView)num_of_cards).setText("lala");/**update ui*/
    }
    private void addCardToPersonalSet(String cardToAdd)
    {
        final Card c = new Card(this,cardToAdd);
        mapImageWithCard(c, cardToAdd);
        c.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {/**play that card if possible*/
                String number = c.id.substring(1);
                String suit = c.id.substring(0, 1);
                String upper_number = game.get_face_up_card().substring(1);
                String upper_suit = game.getUpper_card_suit().substring(0, 1);
                if (very_first_time)
                    very_first_time = false;
                if (number.equals("1") && upper_number.equals("1"))
                {/**testing for an ace first(an ace cannot be played right after another ace has been played before*/
                    Toast.makeText(getApplicationContext(),"Illegal move: an ace cannot be played right after another ace has been played before.",Toast.LENGTH_LONG).show();
                }
                else if (number.equals("1") && state.getPlayersCards().size() == 1)
                {/**you cannot finish a game by playing an ace*/
                    Toast.makeText(getApplicationContext(),"Illegal move: you cannot finish with an ace.",Toast.LENGTH_LONG).show();
                }
                else if (number.equals(upper_number) || suit.equals(upper_suit))
                {/**if true,it is a legal move*/
                    //Toast.makeText(getApplicationContext()," right move.",Toast.LENGTH_LONG).show();
                    playThatCard(c);
                    game.set_upper_suit(suit);/**update the upper suit*/
                    update_ui_uppersuit(suit);/**update also the user interface*/
                    if (number.equals("8") && !played_once)
                    {
                        played_once = true;
                    }
                    else if (number.equals("8"))
                    {
                        //nothing....
                    }
                    else
                    {
                        played_once = false;
                        finish_playing();
                        if (number.equals("1"))
                        {/**the player played an ace,so he can change the suit of the upper card*/
                            signs.setVisibility(View.VISIBLE);
                        }
                        else
                        {
                            //broadcast
                            //make_new_thread and send game object
                            try {
                                if (isOwner)
                                {
                                    for (int j = 0;j < game.numberOfPlayers-1; j++)
                                    {
                                        serverSendsThread = new ServerSendsThread(getApplicationContext(),serverSending,server_send_client.get(j),game,j,handler,handler2);
                                        serverSendsThread.start();
                                        //Thread.sleep(1000);
                                    }
                                }
                                else
                                {
                                    ClientSendsThread clientSendsThread = new ClientSendsThread(getApplicationContext(),client_sending,state.priority_number,game,handler2);
                                    clientSendsThread.start();
                                    clientSendsThread.join();



                                    clientThread = new ClientThread(getApplicationContext(),handler,client_sending,state.priority_number,handler2);
                                    clientThread.start();
                                }
                            }
                            catch (NullPointerException e)
                            {
                                Toast.makeText(getApplicationContext(),"Problem in creating thread: NullPointerException.",Toast.LENGTH_LONG).show();
                            }
                            catch (Exception e)
                            {
                                Toast.makeText(getApplicationContext(),"Problem in creating thread.",Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                }
            }
        });
        players_cards.addView(c);
    }
    private void update_ui_uppersuit(String asuit)
    {/**sets the TextView that indicates the current suit*/
        if (asuit.equals("c"))
        {
            suit_view.setText(" CLUBS");
        }
        else if (asuit.equals("d"))
        {
            suit_view.setText(" DIAMONDS");
        }
        else if (asuit.equals("h"))
        {
            suit_view.setText(" HEARTS");
        }
        else if (asuit.equals("s"))
        {
            suit_view.setText(" SPADES");
        }
    }
    private void applyTheRules()
    {
        /**applies the game rules taking into account what card the previous player played*/
        /**if it is the first round, takes into account the upper card.This function should
         *  be called whenever data are received by the server,after the new game object is assigned to the game variable,and also the time*/
        //Toast.makeText(this," lETS APPLY THE RULES. state.priority_number: "+state.priority_number+" game.turn"+game.turn,Toast.LENGTH_LONG).show();
        if (state.priority_number == game.turn)
        {/**it is this player's turn*/
            String prev_num = game.getPrevious_players_card().substring(1);
            //Toast.makeText(this,"Test: This is my turn!!!And this is the previous card: "+game.getPrevious_players_card(),Toast.LENGTH_LONG).show();
            if (game.get_face_up_card().substring(1).equals("1") && very_first_time && isOwner)/***an ace*/
            {/***TO SOLVE THE BUG ..the initial upper card is an ace,so he can change the suit of the upper card**/
                disableEverything();
                signs.setVisibility(View.VISIBLE);
            }
            else
            {
                if (prev_num.equals("9"))
                {/**previous player played 9,this player has to loose their turn*/
                    game.setPrevious_players_card("");
                    try{
                        Thread.sleep(500);
                    }
                    catch (Exception e)
                    {

                    }
                    finish_playing();
                    //Toast.makeText(this,"9, so broadcast",Toast.LENGTH_LONG).show();
                    //broadcast
                    //make_new_thread and send game object
                    try {
                        if (isOwner)
                        {
                            for (int j = 0;j < game.numberOfPlayers-1; j++)
                            {
                                //Thread.sleep(1000);
                                serverSendsThread = new ServerSendsThread(this,serverSending,server_send_client.get(j),game,j,handler,handler2);
                                serverSendsThread.start();
                            }
                        }
                        else
                        {
                            ClientSendsThread clientSendsThread = new ClientSendsThread(this,client_sending,state.priority_number,game,handler2);
                            clientSendsThread.start();
                            clientSendsThread.join();

                            clientThread = new ClientThread(this,handler,client_sending,state.priority_number,handler2);
                            clientThread.start();
                        }
                    }
                    catch (NullPointerException e)
                    {
                        Toast.makeText(getApplicationContext(),"Problem in creating thread: NullPointerException.",Toast.LENGTH_LONG).show();
                    }
                    catch (Exception e)
                    {
                        Toast.makeText(getApplicationContext(),"Problem in creating thread.",Toast.LENGTH_LONG).show();
                    }
                }
                else if (prev_num.contains("7"))/*(prev_num.equals("7"))*/
                {/**the palyer must pick 2 cards from the face down pale,unless they have a seven*/
                    Toast.makeText(this,"Previous card has sevens! "+game.getPrevious_players_card(),Toast.LENGTH_LONG).show();
                    int sevens = state.searchFor7InCards();
                    //Toast.makeText(this,"I have "+sevens+" sevens!",Toast.LENGTH_LONG).show();
                    if (sevens > 0 )
                    {/**enable only sevens,disable rest of the cards.Make visible the not play 7 button*/
                        Toast.makeText(this,"You can eighter play one of the 7-cards, or play something else. Press the buttom above to play something else.",Toast.LENGTH_LONG).show();
                        seven.setVisibility(View.VISIBLE);
                        //game.setPrevious_players_card(game.getPrevious_players_card().concat("7"));
                        enableOnlySevens();
                    }
                    else
                    {/***The player doesn't have a seven.Enable all cards,but add 2 cards to the personal set*/
                        //int num_of_cards_to_take = game.getPrevious_players_card().substring(1).length();
                        ////////////////////////////////////
                        int num_of_cards_to_take = 0;/*game.getPrevious_players_card().substring(1).length();*/
                        String substring = game.getPrevious_players_card().substring(1);
                        int index = 0;
                        num_of_cards_to_take = game.getPrevious_players_card().lastIndexOf("7");
                        /*for (int j = 0; j < substring.length(); j++)
                        {
                            index =  substring.indexOf("7");
                            if (index >0)
                            {
                                num_of_cards_to_take++;
                                substring.substring(index + 1);
                            }
                        }*/
                        /////////////////////////////////////
                        //Toast.makeText(this,".  Oups! You don't have 7...You should take "+2*num_of_cards_to_take+"  eftaria. To previous card= "+game.getPrevious_players_card(),Toast.LENGTH_LONG).show();
                        game.setPrevious_players_card("n0");
                        pick_cards_by7 = true;
                        for (int j = 0; j < num_of_cards_to_take; j++)
                        {/**the player has to take 7*number of sevens cards*/
                        //Toast.makeText(this,j+".  Oups! Tha pareis "+num_of_cards_to_take*2+" eftaria",Toast.LENGTH_LONG).show();
                            pickingCardProcess();/**pick 2 new cards*/
                            pickingCardProcess();/**animation could be added*/
                        }
                        pick_cards_by7 = false;
                        enableEverything();
                        //Toast.makeText(this,"Seven: take 2!!",Toast.LENGTH_LONG).show();
                    }
                }
                else if (prev_num.equals(""))/**redundant*/
                {/**prev player passed,or lost their turn(because of an 9)*/
                    game.setPrevious_players_card("n0");
                    enableEverything();
                    /**redundant case!!!*/
                    //Toast.makeText(this,"The previous player lost their turn because of a 9, or they passed. It is your turn!!",Toast.LENGTH_LONG).show();
                }
                else
                {/**the rest of the cases*/
                    game.setPrevious_players_card("n0");
                    enableEverything();
                    //Toast.makeText(this,"It is your turn!!",Toast.LENGTH_LONG).show();
                }
            }
        }
        else
        {/**it is someone else's turn*/
            disableEverything();/**redundant: it is already disabled. NOO*/
            Toast.makeText(this,"It is "+game.turn+" player's turn.",Toast.LENGTH_LONG).show();
        }
    }
    public void play_not_7(View view)
    {
        //take 2 cards
        int sevens_num = game.getPrevious_players_card().length();
        int num_of_cards_to_take = 0;/*game.getPrevious_players_card().substring(1).length();*/
        num_of_cards_to_take = game.getPrevious_players_card().lastIndexOf("7");
        /*String substring = game.getPrevious_players_card().substring(1);
        int index = 0;
        for (int j = 0; j < substring.length(); j++)
        {
         index =  substring.indexOf("7");
            if (index >0)
            {
                num_of_cards_to_take++;
                substring.substring(index + 1);
            }
        }*/
        //Toast.makeText(this,". Oups! Tha pareis "+num_of_cards_to_take*2+" eftaria, "+game.getPrevious_players_card()+"..kai sevensnum = "+sevens_num,Toast.LENGTH_LONG).show();
        game.setPrevious_players_card("n0");
        seven.setVisibility(View.INVISIBLE);
        pick_cards_by7 = true;
        for (int j = 0; j < num_of_cards_to_take; j++)
        {
            pickingCardProcess();/**pick 2 new cards*/
            pickingCardProcess();/**animation could be added*/
        }

        pick_cards_by7 = false;
        for (int i = 0; i < players_cards.getChildCount(); i++)
        {/**disable 'click' for player's cards*/
            Card child = (Card)players_cards.getChildAt(i);
            if (child.id.substring(1).equals("7"))
                child.setEnabled(false);
            else
                child.setEnabled(true);
        }
        face_down.setEnabled(true);
    }
    private void playThatCard(Card a_card)
    {/**play that card*/
        /**add that card to the face up pale(on top)*/
        game.add_to_face_up_pack(a_card.id);/**SOSOS And after broacast to all players.to be implemented*/
        //Toast.makeText(this,"new face up pale: "+game.face_up_pack.toString(),Toast.LENGTH_LONG).show();
        update_ui();/**display the new upper card at the user interface*/
        state.removeFromPlayersCards(a_card.id);/**remove that card from the list that contains player's cards*/
        players_cards.removeView(a_card);/**update the ui (the cards set)*/
        changeTheNumberOfPersonalCards(); /**update the ui - the number of this player's cards*/
    }
    private void enableOnlySevens()
    {
        playing_sign.setTextColor(Color.GREEN); /**red sign when playing,black otherwise*/
        background.setBackgroundColor(Color.YELLOW);
        for (int i = 0; i < players_cards.getChildCount(); i++)
        {/**disable 'click' for player's cards*/
            Card child = (Card)players_cards.getChildAt(i);
            if (child.id.substring(1).equals("7"))
                child.setEnabled(true);
            else
                child.setEnabled(false);
        }
        face_down.setEnabled(false);/**disable the face down pale click(picking a card)*/
    }
    private void enableEverything()
    {
        playing_sign.setTextColor(Color.GREEN); /**red sign when playing,black otherwise*/
        background.setBackgroundColor(Color.YELLOW);
        for (int i = 0; i < players_cards.getChildCount(); i++)
        {/**disable 'click' for player's cards*/
            View child = players_cards.getChildAt(i);
            child.setEnabled(true);
        }
        face_down.setEnabled(true);/**disable the face down pale click(picking a card)*/
    }
    private void disableEverything()
    {
        playing_sign.setTextColor(Color.BLACK); /**red sign when playing,black otherwise*/
        background.setBackgroundColor(Color.WHITE);
        //players_cards.setEnabled(false);
        for (int i = 0; i < players_cards.getChildCount(); i++)
        {/**disable 'click' for player's cards*/
            View child = players_cards.getChildAt(i);
            child.setEnabled(false);
        }
        face_down.setEnabled(false);/**disable the face down pale click(picking a card)*/
    }
    private void finish_playing()
    {
        ///////SOSOS END OF MOVE. player played. Next player should take turn.broadcast////////////////
        /**disable moves (at user interface)*/
        if (game.getPrevious_players_card().equals(""))
        {
            //Toast.makeText(this,"STRANGE previous card must stay the same: "+game.getPrevious_players_card(),Toast.LENGTH_LONG).show();
            game.setPrevious_players_card("n0");
        }
        else if (passed)
        {
            game.setPrevious_players_card("n0");
            //Toast.makeText(this," passed ...: "+game.getPrevious_players_card(),Toast.LENGTH_LONG).show();
        }
        else if (game.getPrevious_players_card().contains("7"))
        {
            game.setPrevious_players_card(game.getPrevious_players_card().concat("7"));
        }
        else
        {
            //Toast.makeText(this," previous card nothing: "+game.getPrevious_players_card(),Toast.LENGTH_LONG).show();
            game.setPrevious_players_card(game.get_face_up_card());
            //Toast.makeText(this,"..And after being changed, previous card: "+game.getPrevious_players_card(),Toast.LENGTH_LONG).show();
        }
        seven.setVisibility(View.INVISIBLE);
        disableEverything();
        state.setMy_turn(false);
        pass1.setEnabled(false);
        picked_once = false;
        TextView player_child_name = (TextView)row1.getChildAt(game.get_turn());
        player_child_name.setTextColor(Color.BLACK);/**set the color of the name of this played (that has just played) black*/

        game.set_next_turn();/**variable that indecates the turns. Next player plays*/

        player_child_name = (TextView)row1.getChildAt(game.get_turn());
        player_child_name.setTextColor(Color.GREEN);/**set the color of the name of the player that plays next green*/
    }
    public void change_suit_to_clubs(View view)
    {
        //signs.setVisibility(View.INVISIBLE);
        game.set_upper_suit("c");/**update the upper suit*/
        update_ui_uppersuit("c");/**update also the user interface*/
    }
    public void change_suit_to_spades(View view)
    {
        //signs.setVisibility(View.INVISIBLE);
        game.set_upper_suit("s");/**update the upper suit*/
        update_ui_uppersuit("s");/**update also the user interface*/
    }
    public void change_suit_to_diamonds(View view)
    {
        //signs.setVisibility(View.INVISIBLE);
        game.set_upper_suit("d");/**update the upper suit*/
        update_ui_uppersuit("d");/**update also the user interface*/
    }
    public void change_suit_to_hearts(View view)
    {
        //signs.setVisibility(View.INVISIBLE);
        game.set_upper_suit("h");/**update the upper suit*/
        update_ui_uppersuit("h");/**update also the user interface*/
    }
    public void ok_change(View view)
    {/**at the end should finish playing*/
        signs.setVisibility(View.INVISIBLE);
        if (very_first_time)
        {
            very_first_time = false;/**pleonasmos*/
            enableEverything();
        }
        else
        {
            //Toast.makeText(this,"broadcast after ok",Toast.LENGTH_LONG).show();
            //make_new_thread and send game object
            try {
                if (isOwner)
                {
                    for (int j = 0;j < game.numberOfPlayers-1; j++)
                    {
                        //Thread.sleep(1000);
                        serverSendsThread = new ServerSendsThread(this,serverSending,server_send_client.get(j),game,j,handler,handler2);
                        serverSendsThread.start();
                    }
                }
                else
                {
                    ClientSendsThread clientSendsThread = new ClientSendsThread(this,client_sending,state.priority_number,game,handler2);
                    clientSendsThread.start();
                    clientSendsThread.join();

                    clientThread = new ClientThread(this,handler,client_sending,state.priority_number,handler2);
                    clientThread.start();
                }
            }
            catch (NullPointerException e)
            {
                Toast.makeText(getApplicationContext(),"Problem in creating thread: NullPointerException.",Toast.LENGTH_LONG).show();
            }
            catch (Exception e)
            {
                Toast.makeText(getApplicationContext(),"Problem in creating thread.",Toast.LENGTH_LONG).show();
            }
        }
    }
    public void pass_the_turn(View view)
    {
        passed = true;
        finish_playing();
        passed = false;
        //broadcast
        //Toast.makeText(this,"broadcast, after pass",Toast.LENGTH_LONG).show();
        //make_new_thread and send game object
        try {
            if (isOwner)
            {
                for (int j = 0;j <game.numberOfPlayers-1; j++)
                {
                    //Thread.sleep(1000);
                    serverSendsThread = new ServerSendsThread(this,serverSending,server_send_client.get(j),game,j,handler,handler2);
                    serverSendsThread.start();
                }
            }
            else
            {
                ClientSendsThread clientSendsThread = new ClientSendsThread(this,client_sending,state.priority_number,game,handler2);
                clientSendsThread.start();
                clientSendsThread.join();

                clientThread = new ClientThread(this,handler,client_sending,state.priority_number,handler2);
                clientThread.start();
            }
        }
        catch (NullPointerException e)
        {
            Toast.makeText(getApplicationContext(),"Problem in creating thread: NullPointerException.",Toast.LENGTH_LONG).show();
        }
        catch (Exception e)
        {
            Toast.makeText(getApplicationContext(),"Problem in creating thread.",Toast.LENGTH_LONG).show();
        }
    }
    private void mapImageWithCard(ImageView view,String upper_card)
    {
        AllCards card;
        //Toast.makeText(this,"upper card: "+upper_card,Toast.LENGTH_LONG).show();
        /*String path_to_image = "res/drawable/"+upper_card+".png";*/
        try {
            card = AllCards.valueOf(upper_card);
            switch (card)
            {
                case h1:  view.setImageResource(R.drawable.h1);
                    break;
                case h2:  view.setImageResource(R.drawable.h2);
                    break;
                case h3:  view.setImageResource(R.drawable.h3);
                    break;
                case h4:  view.setImageResource(R.drawable.h4);
                    break;
                case h5:  view.setImageResource(R.drawable.h5);
                    break;
                case h6:  view.setImageResource(R.drawable.h6);
                    break;
                case h7:  view.setImageResource(R.drawable.h7);
                    break;
                case h8:  view.setImageResource(R.drawable.h8);
                    break;
                case h9:  view.setImageResource(R.drawable.h9);
                    break;
                case h10:  view.setImageResource(R.drawable.h10);
                    break;
                case h11:  view.setImageResource(R.drawable.h11);
                    break;
                case c1:  view.setImageResource(R.drawable.c1);
                    break;
                case c2:  view.setImageResource(R.drawable.c2);
                    break;
                case c3:  view.setImageResource(R.drawable.c3);
                    break;
                case c4:  view.setImageResource(R.drawable.c4);
                    break;
                case c5:  view.setImageResource(R.drawable.c5);
                    break;
                case c6:  view.setImageResource(R.drawable.c6);
                    break;
                case c7:  view.setImageResource(R.drawable.c7);
                    break;
                case c8:  view.setImageResource(R.drawable.c8);
                    break;
                case c9:  view.setImageResource(R.drawable.c9);
                    break;
                case c10:  view.setImageResource(R.drawable.c10);
                    break;
                case c11:  view.setImageResource(R.drawable.c11);
                    break;
                case d1:  view.setImageResource(R.drawable.d1);
                    break;
                case d2:  view.setImageResource(R.drawable.d2);
                    break;
                case d3:  view.setImageResource(R.drawable.d3);
                    break;
                case d4:  view.setImageResource(R.drawable.d4);
                    break;
                case d5:  view.setImageResource(R.drawable.d5);
                    break;
                case d6:  view.setImageResource(R.drawable.d6);
                    break;
                case d7:  view.setImageResource(R.drawable.d7);
                    break;
                case d8:  view.setImageResource(R.drawable.d8);
                    break;
                case d9:  view.setImageResource(R.drawable.d9);
                    break;
                case d10:  view.setImageResource(R.drawable.d10);
                    break;
                case d11:  view.setImageResource(R.drawable.d11);
                    break;
                case s1:  view.setImageResource(R.drawable.s1);
                    break;
                case s2:  view.setImageResource(R.drawable.s2);
                    break;
                case s3:  view.setImageResource(R.drawable.s3);
                    break;
                case s4:  view.setImageResource(R.drawable.s4);
                    break;
                case s5:  view.setImageResource(R.drawable.s5);
                    break;
                case s6:  view.setImageResource(R.drawable.s6);
                    break;
                case s7:  view.setImageResource(R.drawable.s7);
                    break;
                case s8:  view.setImageResource(R.drawable.s8);
                    break;
                case s9:  view.setImageResource(R.drawable.s9);
                    break;
                case s10:  view.setImageResource(R.drawable.s10);
                    break;
                case s11:  view.setImageResource(R.drawable.s11);
                    break;
                case d13:  view.setImageResource(R.drawable.d13);
                    break;
                case h13:  view.setImageResource(R.drawable.h13);
                    break;
                case s13:  view.setImageResource(R.drawable.s13);
                    break;
                case c13:  view.setImageResource(R.drawable.c13);
                    break;
                case c12:  view.setImageResource(R.drawable.c12);
                    break;
                case d12:  view.setImageResource(R.drawable.d12);
                    break;
                case h12:  view.setImageResource(R.drawable.h12);
                    break;
                case s12:  view.setImageResource(R.drawable.s12);
                    break;
                default:  view.setImageResource(R.drawable.front);
            }
        }
        catch (Exception e)
        {
            Toast.makeText(this,"Error.",Toast.LENGTH_LONG).show();
        }
    }
    private void update_ui()
    {
        String upper_card = game.get_face_up_card();
        mapImageWithCard(face_up, upper_card);
    }
}
