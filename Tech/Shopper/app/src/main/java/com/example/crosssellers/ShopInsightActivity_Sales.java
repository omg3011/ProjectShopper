package com.example.crosssellers;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.LegendRenderer;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.DataPointInterface;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.OnDataPointTapListener;
import com.jjoe64.graphview.series.Series;

public class ShopInsightActivity_Sales extends AppCompatActivity {

    GraphView graph_top, graph_btm;
    Spinner spinner_top, spinner_btm_start, spinner_btm_end;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_insight_sales);

        //-- Init View
        spinner_top = findViewById(R.id.shop_insight_sales_top_spinner);
        spinner_btm_start = findViewById(R.id.shop_insight_sales_btm_start_spinner);
        spinner_btm_end = findViewById(R.id.shop_insight_sales_btm_end_spinner);

        //-- Init Spinner (Top)
        String[] items = getResources().getStringArray(R.array.shopInsight_sales_top_spinner);
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, items);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_top.setAdapter(dataAdapter);


        //-- Init Spinner (Btm Start)
        String[] items2 = getResources().getStringArray(R.array.shopInsight_sales_btm_start_spinner);
        ArrayAdapter<String> dataAdapter2 = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, items2);
        dataAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_btm_start.setAdapter(dataAdapter2);


        //-- Init Spinner (Btm end)
        String[] items3 = getResources().getStringArray(R.array.shopInsight_sales_btm_end_spinner);
        ArrayAdapter<String> dataAdapter3 = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, items3);
        dataAdapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_btm_end.setAdapter(dataAdapter3);

        //----------------------------------------------------------------------//
        // Action bar                                                           //
        //----------------------------------------------------------------------//
        //-- Add built-in "Actionbar" and it's "Actionbar"->title
        ActionBar actionbar = getSupportActionBar();
        actionbar.setTitle("Sales Insights");

        //-- Enable "Actionbar"->back button
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setDisplayShowHomeEnabled(true);

        // Init View
        graph_top = (GraphView) findViewById(R.id.shop_insight_sales_top_GV);
        graph_btm = findViewById(R.id.shop_insight_sales_btm_GV);

        SetupBarGraph_Top();
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
    }

    void SetupBarGraph_Top()
    {

        //--------------------------------------------------------------------------------------//
        //
        // Bar Graph
        //
        //--------------------------------------------------------------------------------------//
        BarGraphSeries<DataPoint> series = new BarGraphSeries<>(new DataPoint[] {
                new DataPoint(0, -2),
                new DataPoint(1, 5),
                new DataPoint(4, 6)
        });
        series.setColor(Color.BLUE);
        series.setTitle("foo");
        series.setAnimated(true);
        series.setDataWidth(1d);
        graph_top.addSeries(series);

        // Create Bar Graph
        BarGraphSeries<DataPoint> series2 = new BarGraphSeries<>(new DataPoint[] {
                new DataPoint(-10, -5),
                new DataPoint(1, 3),
                new DataPoint(4, 1)
        });
        series2.setColor(Color.RED);
        series2.setTitle("bar");
        series2.setAnimated(true);
        series2.setDataWidth(1d);
        graph_top.addSeries(series2);

        // Show Legend
        graph_top.getLegendRenderer().setVisible(true);
        graph_top.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.TOP);


        // set the viewport wider than the data, to have a nice view
        graph_top.getViewport().setMinX(-10);
        graph_top.getViewport().setMaxX(10);
        graph_top.getViewport().setMinY(-10);
        graph_top.getViewport().setMaxY(10);
        graph_top.getViewport().setXAxisBoundsManual(true);


        graph_top.getViewport().setScrollable(true); // enables horizontal scrolling
        graph_top.getViewport().setScrollableY(true); // enables vertical scrolling
        graph_top.getViewport().setScalable(true); // enables horizontal zooming and scrolling
        graph_top.getViewport().setScalableY(true); // enables vertical zooming and scrolling


        //-- Event Listener: On Click
        series.setOnDataPointTapListener(new OnDataPointTapListener() {
            @Override
            public void onTap(Series s, DataPointInterface dataPoint) {
                Toast.makeText(ShopInsightActivity_Sales.this, "(" + dataPoint.getX() + "," + dataPoint.getY() + ")", Toast.LENGTH_SHORT).show();
            }
        });
        series2.setOnDataPointTapListener(new OnDataPointTapListener() {
            @Override
            public void onTap(Series s, DataPointInterface dataPoint) {
                Toast.makeText(ShopInsightActivity_Sales.this, "(" + dataPoint.getX() + "," + dataPoint.getY() + ")", Toast.LENGTH_SHORT).show();
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
        Intent intent = new Intent(ShopInsightActivity_Sales.this, ShopInsightActivity_Home.class);
        startActivity(intent);
        finish();
    }
}
