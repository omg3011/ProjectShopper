package com.example.crosssellers;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class MallInsightActivity_Floorfall extends AppCompatActivity {

    LinearLayout image_LL;
    Button btn0, btn1, btn2, btn3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mall_insight_floorfall);

        image_LL = findViewById(R.id.mall_insight_floorfall_LL);
        btn0 = findViewById(R.id.mall_insight_floorfall_btn0);
        btn1 = findViewById(R.id.mall_insight_floorfall_btn1);
        btn2 = findViewById(R.id.mall_insight_floorfall_btn2);
        btn3 = findViewById(R.id.mall_insight_floorfall_btn3);

        SetFloorImage(0);

        btn0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SetFloorImage(0);
            }
        });

        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SetFloorImage(1);
            }
        });

        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SetFloorImage(2);
            }
        });

        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SetFloorImage(3);
            }
        });

        //----------------------------------------------------------------------//
        // Action bar                                                           //
        //----------------------------------------------------------------------//
        //-- Add built-in "Actionbar" and it's "Actionbar"->title
        ActionBar actionbar = getSupportActionBar();
        actionbar.setTitle("Footfall Insights");

        //-- Enable "Actionbar"->back button
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setDisplayShowHomeEnabled(true);
    }

    void SetFloorImage(int floor)
    {
        // Turn off all floor
        for(int i = 0; i < image_LL.getChildCount(); ++i)
        {
            ImageView iv = (ImageView)image_LL.getChildAt(i);
            iv.setVisibility(View.GONE);
        }

        // Turn on floor
        ImageView iv2 = (ImageView)image_LL.getChildAt(floor);
        iv2.setVisibility(View.VISIBLE);

    }

    //------------------------------------------------------------------------//
    // Function: To allow back button
    //------------------------------------------------------------------------//
    @Override
    public boolean onSupportNavigateUp() {
        //-- Go to previous activity
        onBackPressed(); // Built-in function

        return super.onSupportNavigateUp();
    }


    @Override
    public void onBackPressed() {
        Intent intent = new Intent(MallInsightActivity_Floorfall.this, MallInsightActivity_Home.class);
        startActivity(intent);
        finish();
    }
}