package com.example.vassiliki.agony;

import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class Winner extends AppCompatActivity {
    public String the_winner;
    public String this_player;
    private LinearLayout linearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_winner);

        linearLayout = (LinearLayout)findViewById(R.id.rating);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            the_winner = extras.getString("winner");
            this_player = extras.getString("thisPlayer");

            if (!this_player.equals(the_winner))
            {
                TextView t = (TextView)findViewById(R.id.textView1);
                t.setText("LOSER!!");
                t.setTextColor(Color.RED);
            }
            TextView tx = new TextView(this);
            tx.setText(the_winner);
            tx.setTextColor(Color.RED);
            tx.setTypeface(null, Typeface.BOLD);
            TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,TableRow.LayoutParams.WRAP_CONTENT);
            lp.setMargins(0,10,10,5);
            tx.setLayoutParams(lp);
            tx.setLayoutParams(lp);/**peritto*/
            linearLayout.addView(tx);
        }
    }
}
