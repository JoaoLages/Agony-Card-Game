package com.example.vassiliki.agony;

import java.util.List;

/**
 * Created by Vassiliki on 25/4/2016.
 */
public class PlayerState {
    private List<String> cards; /**the personal card set of each player*/
    private boolean my_turn;/**should be true only whenever this player plays*/
    public int priority_number; //should be assigned when the game object is broadcasted for the first time from group owner
    //in each turn,the player at the end should send to everybody the next player's number. If this number is equal to priority_number
    //then the player knows that it is their turn.

    public PlayerState(Game game, int priority,List<String> myCards)
    {/**to be removed from here (the dealing)*/
        this.priority_number = priority;
        this.cards = myCards;
        /*//test code
        cards.add("d7");
        //test code*/
        //priority_number = 0; /**to be removed: for test reasons*/
    }
    public List<String> getPlayersCards()
    {
        return cards;
    }
    public boolean getMy_turn()
    {
        return my_turn;
    }
    public void setMy_turn(boolean value)
    {
        my_turn = value;
    }
    public void addToPlayersCards(String c)
    {
        cards.add(c);
    }
    public void removeFromPlayersCards(String c)
    {
        cards.remove(c);
    }
    public int getNumberOfCards()
    {
        return cards.size();
    }
    public int searchFor7InCards()
    {
        int answer = 0;
        String card_num;
        for (int i = 0;i < cards.size();i++)
        {
            card_num = cards.get(i).substring(1);
            if (card_num.equals("7"))
                answer++;
        }
        return answer;
    }
}
