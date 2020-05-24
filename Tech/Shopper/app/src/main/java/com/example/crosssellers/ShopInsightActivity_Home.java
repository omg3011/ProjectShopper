package com.example.crosssellers;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

public class ShopInsightActivity_Home extends AppCompatActivity {

    RelativeLayout RL_salesInsight, RL_customerInsight, RL_productInsight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_insight_home);

        //-- Init View
        RL_salesInsight = findViewById(R.id.shop_insight_home_saleClick_RL);
        RL_customerInsight = findViewById(R.id.shop_insight_home_customerClick_RL);
        RL_productInsight = findViewById(R.id.shop_insight_home_productClick_RL);


        //----------------------------------------------------------------------//
        // Action bar                                                           //
        //----------------------------------------------------------------------//
        //-- Add built-in "Actionbar" and it's "Actionbar"->title
        ActionBar actionbar = getSupportActionBar();
        actionbar.setTitle("Shop Insights");

        //-- Enable "Actionbar"->back button
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setDisplayShowHomeEnabled(true);

        //-- Register button listener
        RL_salesInsight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ShopInsightActivity_Home.this, ShopInsightActivity_Sales.class));
                finish();
            }
        });
        RL_customerInsight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ShopInsightActivity_Home.this, ShopInsightActivity_Customer.class));
                finish();
            }
        });
        RL_productInsight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ShopInsightActivity_Home.this, ShopInsightActivity_Product.class));
                finish();
            }
        });


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
        Intent intent = new Intent(ShopInsightActivity_Home.this, DashboardActivity.class);
        startActivity(intent);
        finish();
    }

}
