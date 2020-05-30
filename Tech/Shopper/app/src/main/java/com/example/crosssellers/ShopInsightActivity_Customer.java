package com.example.crosssellers;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

public class ShopInsightActivity_Customer extends AppCompatActivity {

    GraphView graph_top, graph_btm;
    Spinner spinner_top, spinner_btm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_insight_customer);

        //-- Init View
        spinner_top = findViewById(R.id.shop_insight_customer_top_spinner);
        spinner_btm = findViewById(R.id.shop_insight_customer_btm_spinner);

        //-- Init Spinner (Top)
        String[] items = getResources().getStringArray(R.array.shopInsight_customer_top_spinner);
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, items);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_top.setAdapter(dataAdapter);


        //-- Init Spinner (Btm Start)
        String[] items2 = getResources().getStringArray(R.array.shopInsight_customer_btm_spinner);
        ArrayAdapter<String> dataAdapter2 = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, items2);
        dataAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_btm.setAdapter(dataAdapter2);


        //----------------------------------------------------------------------//
        // Action bar                                                           //
        //----------------------------------------------------------------------//
        //-- Add built-in "Actionbar" and it's "Actionbar"->title
        ActionBar actionbar = getSupportActionBar();
        actionbar.setTitle("Customer Insights");

        //-- Enable "Actionbar"->back button
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setDisplayShowHomeEnabled(true);

        // Init View
        graph_top = findViewById(R.id.shop_insight_customer_top_GV);
        graph_btm = findViewById(R.id.shop_insight_customer_btm_GV);

        SetupLineGraph_Top();
        SetupLineGraph_Btm();


    }

    void SetupLineGraph_Btm()
    {
        LineGraphSeries<DataPoint> series = new LineGraphSeries<>(new DataPoint[] {
                new DataPoint(0, 1),
                new DataPoint(1, 5),
                new DataPoint(2, 3),
                new DataPoint(3, 2),
                new DataPoint(4, 8)
        });
        graph_btm.addSeries(series);

        LineGraphSeries<DataPoint> series2 = new LineGraphSeries<>(new DataPoint[] {
                new DataPoint(0, 1),
                new DataPoint(1, 5),
                new DataPoint(2, 3),
                new DataPoint(3, 2),
                new DataPoint(4, 6)
        });

        // set second scale
        graph_btm.getSecondScale().addSeries(series2);
        // the y bounds are always manual for second scale
        graph_btm.getSecondScale().setMinY(0);
        graph_btm.getSecondScale().setMaxY(100);
        series2.setColor(Color.RED);
        graph_btm.getGridLabelRenderer().setVerticalLabelsSecondScaleColor(Color.RED);
        // Write Description below
        graph_top.getGridLabelRenderer().setHorizontalAxisTitle("Line-Graph: No. Of Customer");
    }


    void SetupLineGraph_Top()
    {
        LineGraphSeries<DataPoint> series = new LineGraphSeries<>(new DataPoint[] {
                new DataPoint(0, 1),
                new DataPoint(1, 5),
                new DataPoint(2, 3),
                new DataPoint(3, 2),
                new DataPoint(4, 8)
        });
        graph_top.addSeries(series);

        LineGraphSeries<DataPoint> series2 = new LineGraphSeries<>(new DataPoint[] {
                new DataPoint(0, 1),
                new DataPoint(1, 5),
                new DataPoint(2, 3),
                new DataPoint(3, 2),
                new DataPoint(4, 6)
        });

        // set second scale
        graph_top.getSecondScale().addSeries(series2);
        // the y bounds are always manual for second scale
        graph_top.getSecondScale().setMinY(0);
        graph_top.getSecondScale().setMaxY(100);
        series2.setColor(Color.RED);
        graph_top.getGridLabelRenderer().setVerticalLabelsSecondScaleColor(Color.RED);
        // Write Description below
        graph_top.getGridLabelRenderer().setHorizontalAxisTitle("Line-Graph: Avg Spending / Customer");
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
        Intent intent = new Intent(ShopInsightActivity_Customer.this, ShopInsightActivity_Home.class);
        startActivity(intent);
        finish();
    }
}
