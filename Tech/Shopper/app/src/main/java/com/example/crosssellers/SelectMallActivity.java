package com.example.crosssellers;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class SelectMallActivity extends AppCompatActivity {

    //-- Cache Reference(s)
    private GridLayout gridLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_mall);

        //-- Add built-in "Actionbar" and it's "Actionbar"->title
        ActionBar actionbar = getSupportActionBar();
        actionbar.setTitle("Select Mall");

        //-- Init View
        gridLayout = findViewById(R.id.sm_GL);

        //-- Setup EventListener for clicking each element in the scrolling List
        for(int i = 0; i < gridLayout.getChildCount(); ++i)
        {
            final CardView cardView = (CardView) gridLayout.getChildAt(i);
            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    LinearLayout layout = (LinearLayout) cardView.getChildAt(0);
                    TextView TV_mall = (TextView) layout.getChildAt(1);

                    Intent intent = new Intent(SelectMallActivity.this, MainActivity.class);
                    intent.putExtra("mall", TV_mall.getText());
                    startActivity(intent);
                    finish();
                }
            });
        }
    }
}
