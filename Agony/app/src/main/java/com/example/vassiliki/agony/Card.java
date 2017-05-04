package com.example.vassiliki.agony;

import android.content.Context;
import android.widget.ImageView;

/**
 * Created by Vassiliki on 4/5/2016.
 */
public class Card extends ImageView{
    public String id;

    public Card(Context context,String card_id) {
        super(context);
        this.id = card_id;
    }
}
