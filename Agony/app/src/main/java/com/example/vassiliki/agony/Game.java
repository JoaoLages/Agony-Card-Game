package com.example.vassiliki.agony;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Stack;

/**
 * Created by Vassiliki on 15/4/2016.
 */
public class Game implements Serializable{
    public List<String> players; /**a list with the names of the players*/
    public String winner = null;
    public List<Integer> num_of_players_cards; /**a list with the numbers of the players cards*/
    public int numberOfPlayers = 2; /**the number of the players*/
    public int turn; /**the list index of the player who's turn is it*/
    private String[] card_set = {"c1","c2","c3","c4","c5","c6","c7","c8","c9","c10","c11","c12","c13","d1","d2","d3","d4","d5","d6","d7","d8","d9","d10","d11","d12","d13","h1","h2","h3","h4","h5","h6","h7","h8","h9","h10","h11","h12","h13","s1","s2","s3","s4","s5","s6","s7","s8","s9","s10","s11","s12","s13"};
    public Stack<String> face_up_pack;
    private String upper_card;
    public Stack<String> face_down_pack;
    private String previous_players_card;
    public int aux_prior;
    private String upper_card_suit;/**the suit that players are supposed to take into account in order to play.*/
    /**is equal to the upper card's suit,except if an ace is being played,so it can change.*/

    /*public Game(Game g)
    {
        this.players = g.players;
        this.numberOfPlayers = g.numberOfPlayers;
        this.num_of_players_cards = new ArrayList<>();
        for (int i = 0; i < numberOfPlayers; i++)
        {
            num_of_players_cards.add(i,7);
        }
        this.turn = g.turn;
        Stack<?> copiedStack = (Stack<?>)g.face_up_pack.clone();
        this.face_up_pack = (Stack<String>)copiedStack;
        Stack<?> copiedStack2 = (Stack<?>)g.face_down_pack.clone();
        this.face_down_pack = (Stack<String>)copiedStack2;
        this.upper_card = g.upper_card;
        this.upper_card_suit = this.upper_card.substring(0, 1);
        this.previous_players_card = this.upper_card;*//**a variable to show what the previous player played.At the beggining of a game, is equal to the upper card.*//*
    }*/
    public void set_numberOfPlayers(int num)
    {
        numberOfPlayers = num;
        for (int i = 0;i < numberOfPlayers; i++)
        {/**proxeiro*/
            players.add(i,"player_"+i);
        }
        for (int i = 0; i < numberOfPlayers; i++)
        {
            num_of_players_cards.add(i, 7);
        }
    }
    public void set_prior(int p)
    {
        this.aux_prior = p;
    }
    public Game(List pl,int num)
    {/**constructor-initializes a new game*/
        this.players = pl;
        this.numberOfPlayers = num;
        this.turn = 0;
        this.num_of_players_cards = new ArrayList<>();
        for (int i = 0; i < numberOfPlayers; i++)
        {
            num_of_players_cards.add(i,7);
        }
        face_down_pack = new Stack<>();
        for (int i = 0; i <= 52; i++)
        {/**filling the face down card pack witha full card set*/
            face_down_pack.push(card_set[i]);
        }
        long seed = System.nanoTime();
        Random r = new Random(seed);
        /**suffle randomizes the face down card pack using seed*/
        Collections.shuffle(face_down_pack, r);
        face_up_pack = new Stack<>();
        face_up_pack.push(face_down_pack.pop());/**picks the upper card of the face down pale and places in the common face up pale*/
        /****************don't want to start with 9**********************/
        String number = face_up_pack.get(0).substring(1);
        List<String> helper = new ArrayList<>();
        while (number.equals("9"))
        {
            helper.add(face_up_pack.pop());
            face_up_pack.push(face_down_pack.pop());/**take the upper from the face down pale*/
            number = face_up_pack.peek().substring(1);
        }
        for (int k = 0; k < helper.size(); k++)
        {
            face_down_pack.push(helper.get(k));
        }
        //////////////don't want to start with 9*//////////////////////
        upper_card = face_up_pack.peek();
        this.upper_card_suit = this.upper_card.substring(0, 1);
        this.previous_players_card = this.upper_card;/**a variable to show what the previous player played.At the beggining of a game, is equal to the upper card.*/
    }
    public Game()
    {
        this.players = new ArrayList<>();/**proxeiro*/
        this.numberOfPlayers = 2;
        this.num_of_players_cards = new ArrayList<>();
        this.turn = 0;

        face_down_pack = new Stack<>();
        for (int i = 0; i < 52; i++)
        {/**filling the face down card pack witha full card set*/
            face_down_pack.push(card_set[i]);
        }
        /**the face down card must be shuffled*/
        shuffling(face_down_pack);
        face_up_pack = new Stack<>();
        /*//test code must be removed!!
        face_up_pack.add("s7");
        //test code end*/
        face_up_pack.push(face_down_pack.pop());/**picks the upper card of the face down pale and places in the common face up pale*/
        upper_card = face_up_pack.peek();
        this.upper_card_suit = this.upper_card.substring(0, 1);
        this.previous_players_card = this.upper_card;/**a variable to show what the previous player played.At the beggining of a game, is equal to the upper card.*/
    }
    public void set_upper_suit(String suit)
    {
        this.upper_card_suit = suit;
    }
    public String getUpper_card_suit()
    {
        return this.upper_card_suit;
    }
    public int get_turn()
    {
        return this.turn;
    }
    public void set_next_turn()
    {
        this.turn++;
        this.turn = this.turn % this.numberOfPlayers;
    }
    public void setPrevious_players_card(String new_c)
    {
        this.previous_players_card = new_c;
    }
    public String getPrevious_players_card()
    {
        return this.previous_players_card;
    }
    public String get_face_up_card()
    {
        return upper_card;
    }
    public void add_to_face_up_pack(String new_upper)
    {
        face_up_pack.push(new_upper);/**add the new card at the top of the pale*/
        upper_card = face_up_pack.peek();/**update uppercard variable*/
    }
    public String remove_upper_downpale()
    {/**removes the upper card from the face down pale and returns it (to a player's personal set of cards*/
        if (face_down_pack.size() > 0)/**if true,the pale still has cards*/
            return face_down_pack.pop();
        else
        {/**if this condition is true,the pale has run out of cards*/
            if (face_up_pack.size() <= 1)
            {/**in this case, the game is over. to be implemented*/
                return "";
            }
            else
            {/**if this condition is true,the pale has run out of cards and must be recreated by the faceup pale*/
            /**add an animation to show the resuffling*/
                String temp_upper = face_up_pack.pop();/**remove the upper card from the faceup pale, and store it*/
                face_down_pack = face_up_pack;/**assign to the facedown pale the content of the face up*/
                shuffling(face_down_pack);/**shuffle the newly created face dowm pale*/
                face_up_pack = new Stack<>();/**recreate the face up pale*/
                face_up_pack.push(temp_upper);/**add to it the previously stored upper card*/
                return face_down_pack.pop();
            }
        }
    }
    private void shuffling(List a_pale)
    {
        long seed = System.nanoTime();
        Random r = new Random(seed);
        /**suffle randomizes the pack using seed*/
        Collections.shuffle(a_pale, r);
    }
    public List<String> dealCards(int turn)
    {/**this function will deal cards to each player.*/
        /**Must be called by the group owner,after creating the Game object,once for each player*/
        /**test version*/
        String heplper = " ";
        for (int i = 0; i < turn; i++)
        {
            for (int j = 0;j < 7; j++)
            {/**removes the 7 next cards, because they were dealt to another player*/
                heplper = face_down_pack.pop();
            }
        }
        List<String> a_players_set = new ArrayList<>();
        for (int i = 0; i < 7; i++)
        {
            a_players_set.add(face_down_pack.pop());
        }
        for (int i = turn + 1; i < numberOfPlayers; i++)
        {
            for (int j = 0;j < 7; j++)
            {/**removes the rest 7-packs of cards, because they are gonne be dealt to next players*/
                heplper = face_down_pack.pop();
            }
        }
        return a_players_set;
    }
}
