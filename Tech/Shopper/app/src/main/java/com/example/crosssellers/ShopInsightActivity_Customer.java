package com.example.crosssellers;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.helper.StaticLabelsFormatter;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.DataPointInterface;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.OnDataPointTapListener;
import com.jjoe64.graphview.series.Series;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import Models.AverageSales_Model;

public class ShopInsightActivity_Customer extends AppCompatActivity {

    //-- DB(s)
    FirebaseUser fUser;

    //-- View(s)
    GraphView graph_top, graph_btm;
    Spinner spinner_top, spinner_btm;

    //-- Variable(s)
    BarGraphSeries<DataPoint> barSeries;
    String[] items, items2;

    BarGraphSeries<DataPoint> barSeriesBtm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_insight_customer);

        //-- Init DB
        fUser = FirebaseAuth.getInstance().getCurrentUser();

        //-- Init View
        spinner_top = findViewById(R.id.shop_insight_customer_top_spinner);
        spinner_btm = findViewById(R.id.shop_insight_customer_btm_spinner);

        //-- Init Spinner (Top)
        items = getResources().getStringArray(R.array.shopInsight_customer_top_spinner);
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, items);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_top.setAdapter(dataAdapter);


        //-- Init Spinner (Btm Start)
        items2 = getResources().getStringArray(R.array.shopInsight_customer_btm_spinner);
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
        graph_top = (GraphView) findViewById(R.id.shop_insight_customer_top_GV);
        graph_btm = findViewById(R.id.shop_insight_customer_btm_GV);

        //-- Hardcode fill in dummy data to db
        /*
        FillDummyData("21-1-2020");
        FillDummyData("22-1-2020");
        FillDummyData("10-1-2020");
        FillDummyData("11-1-2020");
        FillDummyData("01-1-2020");
        FillDummyData("02-1-2020");
        */


        //----------------------------------------------------------------------//
        // Spinner Listener                                                     //
        //----------------------------------------------------------------------//
        spinner_top.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                SetupRetrieveDataForTopGraph(position + 1);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        spinner_btm.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                SetupRetrieveDataForBtmGraph(position + 1);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


    }


    //------------------------------------------------------------------------------------//
    //
    //  Init Function(s)
    //
    //------------------------------------------------------------------------------------//

    //  Average Spending of Customer / Past 3 months
    void SetupRetrieveDataForTopGraph(final int noOfMonths)
    {
        // Create the slots
        List<Integer> weekList = new ArrayList<>();
        for(int i = 0; i < noOfMonths; ++i)
        {
            weekList.add(0);
            weekList.add(0);
            weekList.add(0);
            weekList.add(0);
        }

        // Fill in the data
        weekList.set(0, 102);
        weekList.set(1, 121);
        weekList.set(2, 103);
        weekList.set(3, 446);

        if(noOfMonths >= 2)
        {
            weekList.set(4, 535);
            weekList.set(5, 141);
            weekList.set(6, 33);
            weekList.set(7, 10);
        }

        if(noOfMonths >= 3)
        {
            weekList.set(8, 105);
            weekList.set(9, 225);
            weekList.set(10, 178);
            weekList.set(11, 611);
        }

        InitBarGraph_Top(weekList);
    }

    void InitBarGraph_Top(List<Integer> weekList)
    {
        if(barSeries != null)
            graph_top.removeSeries(barSeries);

        //--------------------------------------------------------------------------------------//
        //
        // Bar Graph
        //
        //--------------------------------------------------------------------------------------//
        barSeries = new BarGraphSeries<>();


        for(int i = 0; i < weekList.size(); ++i) {
            barSeries.appendData(new DataPoint(i + 1, weekList.get(i)), true, weekList.size());
        }
        barSeries.setColor(Color.BLUE);

        barSeries.setDataWidth(0.75d);
        barSeries.setSpacing(10);
        barSeries.setDrawValuesOnTop(true);
        barSeries.setValuesOnTopColor(Color.RED);
        graph_top.addSeries(barSeries);
        graph_top.getGridLabelRenderer().setHorizontalAxisTitle("Week");
        graph_top.getGridLabelRenderer().setVerticalAxisTitle("Sales(SGD)");

        //StaticLabelsFormatter staticLabelsFormatter = new StaticLabelsFormatter(graph_top);
        //staticLabelsFormatter.setHorizontalLabels(new String[]{"a", "x", "y", "z", "b"});
        //graph_top.getGridLabelRenderer().setLabelFormatter(staticLabelsFormatter);


        // Show Legend
        //graph_top.getLegendRenderer().setVisible(true);
        //graph_top.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.TOP);


        // set the viewport wider than the data, to have a nice view
        graph_top.getViewport().setYAxisBoundsManual(true);
        graph_top.getViewport().setXAxisBoundsManual(true);
        graph_top.getViewport().setMinX(0);
        graph_top.getViewport().setMaxX(weekList.size() + 1);
        graph_top.getViewport().setMinY(0);
        graph_top.getViewport().setMaxY(1000);

        graph_top.getViewport().setScrollable(true); // enables horizontal scrolling
        graph_top.getViewport().setScrollableY(true); // enables vertical scrolling
        graph_top.getViewport().setScalable(true); // enables horizontal zooming and scrolling
        graph_top.getViewport().setScalableY(true); // enables vertical zooming and scrolling
        //graph_top.getGridLabelRenderer().setLabelVerticalWidth(5);


        //-- Event Listener: On Click
        barSeries.setOnDataPointTapListener(new OnDataPointTapListener() {
            @Override
            public void onTap(Series s, DataPointInterface dataPoint) {
                Toast.makeText(ShopInsightActivity_Customer.this, "(" + dataPoint.getX() + "," + dataPoint.getY() + ")", Toast.LENGTH_SHORT).show();
            }
        });
    }




    //  No Of Customer / Past 3 months
    void SetupRetrieveDataForBtmGraph(final int noOfMonths)
    {
        // Create the slots
        List<Integer> weekList = new ArrayList<>();
        for(int i = 0; i < noOfMonths; ++i)
        {
            weekList.add(0);
            weekList.add(0);
            weekList.add(0);
            weekList.add(0);
        }

        // Fill in the data
        weekList.set(0, 10);
        weekList.set(1, 12);
        weekList.set(2, 10);
        weekList.set(3, 46);

        if(noOfMonths >= 2)
        {
            weekList.set(4, 53);
            weekList.set(5, 14);
            weekList.set(6, 33);
            weekList.set(7, 10);
        }

        if(noOfMonths >= 3)
        {
            weekList.set(8, 10);
            weekList.set(9, 22);
            weekList.set(10, 18);
            weekList.set(11, 61);
        }

        InitBarGraph_Btm(weekList);
    }

    void InitBarGraph_Btm(List<Integer> weekList)
    {
        if(barSeriesBtm != null)
            graph_btm.removeSeries(barSeriesBtm);

        //--------------------------------------------------------------------------------------//
        //
        // Bar Graph
        //
        //--------------------------------------------------------------------------------------//
        barSeriesBtm = new BarGraphSeries<>();


        for(int i = 0; i < weekList.size(); ++i) {
            barSeriesBtm.appendData(new DataPoint(i + 1, weekList.get(i)), true, weekList.size());
        }
        barSeriesBtm.setColor(Color.BLUE);

        barSeriesBtm.setDataWidth(0.75d);
        barSeriesBtm.setSpacing(10);
        barSeriesBtm.setDrawValuesOnTop(true);
        barSeriesBtm.setValuesOnTopColor(Color.RED);
        graph_btm.addSeries(barSeriesBtm);
        graph_btm.getGridLabelRenderer().setHorizontalAxisTitle("Week");
        graph_btm.getGridLabelRenderer().setVerticalAxisTitle("No. Of Customer(s)");

        //StaticLabelsFormatter staticLabelsFormatter = new StaticLabelsFormatter(graph_top);
        //staticLabelsFormatter.setHorizontalLabels(new String[]{"a", "x", "y", "z", "b"});
        //graph_top.getGridLabelRenderer().setLabelFormatter(staticLabelsFormatter);


        // Show Legend
        //graph_top.getLegendRenderer().setVisible(true);
        //graph_top.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.TOP);


        // set the viewport wider than the data, to have a nice view
        graph_btm.getViewport().setYAxisBoundsManual(true);
        graph_btm.getViewport().setXAxisBoundsManual(true);
        graph_btm.getViewport().setMinX(0);
        graph_btm.getViewport().setMaxX(weekList.size() + 1);
        graph_btm.getViewport().setMinY(0);
        graph_btm.getViewport().setMaxY(200);

        graph_btm.getViewport().setScrollable(true); // enables horizontal scrolling
        graph_btm.getViewport().setScrollableY(true); // enables vertical scrolling
        graph_btm.getViewport().setScalable(true); // enables horizontal zooming and scrolling
        graph_btm.getViewport().setScalableY(true); // enables vertical zooming and scrolling
        //graph_top.getGridLabelRenderer().setLabelVerticalWidth(5);


        //-- Event Listener: On Click
        barSeriesBtm.setOnDataPointTapListener(new OnDataPointTapListener() {
            @Override
            public void onTap(Series s, DataPointInterface dataPoint) {
                Toast.makeText(ShopInsightActivity_Customer.this, "(" + dataPoint.getX() + "," + dataPoint.getY() + ")", Toast.LENGTH_SHORT).show();
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
        Intent intent = new Intent(ShopInsightActivity_Customer.this, ShopInsightActivity_Home.class);
        startActivity(intent);
        finish();
    }
}