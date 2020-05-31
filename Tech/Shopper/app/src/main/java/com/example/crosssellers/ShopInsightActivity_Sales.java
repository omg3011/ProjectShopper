package com.example.crosssellers;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.LegendRenderer;
import com.jjoe64.graphview.ValueDependentColor;
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

public class ShopInsightActivity_Sales extends AppCompatActivity {

    //-- DB(s)
    FirebaseUser fUser;
    CollectionReference dataReference_avgSales;

    //-- View(s)
    GraphView graph_top, graph_btm;
    Spinner spinner_top, spinner_btm_start, spinner_btm_end;

    //-- Variable(s)
    String[] items, items2, items3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_insight_sales);

        //-- Init DB
        fUser = FirebaseAuth.getInstance().getCurrentUser();
        dataReference_avgSales = FirebaseFirestore.getInstance().collection("AvgDailySales");

        //-- Init View
        spinner_top = findViewById(R.id.shop_insight_sales_top_spinner);
        spinner_btm_start = findViewById(R.id.shop_insight_sales_btm_start_spinner);
        spinner_btm_end = findViewById(R.id.shop_insight_sales_btm_end_spinner);

        //-- Init Spinner (Top)
        items = getResources().getStringArray(R.array.shopInsight_sales_top_spinner);
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, items);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_top.setAdapter(dataAdapter);


        //-- Init Spinner (Btm Start)
        items2 = getResources().getStringArray(R.array.shopInsight_sales_btm_start_spinner);
        ArrayAdapter<String> dataAdapter2 = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, items2);
        dataAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_btm_start.setAdapter(dataAdapter2);


        //-- Init Spinner (Btm end)
        items3 = getResources().getStringArray(R.array.shopInsight_sales_btm_end_spinner);
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

        //-- Hardcode fill in dummy data to db
/*
        FillDummyData("21-5-2020");
        FillDummyData("22-5-2020");
        FillDummyData("10-5-2020");
        FillDummyData("11-5-2020");
        FillDummyData("01-5-2020");
        FillDummyData("02-5-2020");
        */


        //----------------------------------------------------------------------//
        // Spinner Listener                                                     //
        //----------------------------------------------------------------------//
        spinner_top.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                String select = (String)items[position];

                //-- Get today
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
                simpleDateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Singapore"));
                String today = simpleDateFormat.format(new Date());

                // Past 1 month
                if(position == 0)
                {
                    SetupRetrieveDataForTopGraph(getFirstDayOfTheMonth(today, -1), getLastDayOfTheMonth(today, -1));
                }
                // Past 2 month
                else if(position == 1)
                {
                    //SetupBarGraph_Top();
                }
                // Past 3 month
                else if(position == 2)
                {
                    //SetupBarGraph_Top();
                }

                Log.d("Test", getLastDayOfTheMonth("05-05-2020", -2));
                Log.d("Test", getFirstDayOfTheMonth("06-05-2020", -2));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        //SetupBarGraph_Top();
        //SetupLineGraph_Btm();


    }

    void FillDummyData(String date)
    {
        List<String> rList1 = new ArrayList<>();
        rList1.add("100");
        rList1.add("200");
        List<String> tList1 = new ArrayList<>();
        tList1.add("11AM");
        tList1.add("12PM");
        AverageSales_Model avg = new AverageSales_Model(date, rList1, tList1, "YZllyzxb88RuRlXVcsCR2ppcDmP2");
        dataReference_avgSales.document().set(avg);
    }

    String getLastDayOfTheMonth(String date, int offsetMonth) {
        String lastDayOfTheMonth = "";

        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        try{
            java.util.Date dt= formatter.parse(date);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(dt);

            calendar.add(Calendar.MONTH, 1 + offsetMonth);
            calendar.set(Calendar.DAY_OF_MONTH, 1);
            calendar.add(Calendar.DATE, -1);


            java.util.Date lastDay = calendar.getTime();

            lastDayOfTheMonth = formatter.format(lastDay);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return lastDayOfTheMonth;
    }


    String getFirstDayOfTheMonth(String date, int offsetMonth) {
        String firstDayOfTheMonth = "";

        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        try{
            java.util.Date dt= formatter.parse(date);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(dt);

            calendar.add(Calendar.MONTH, offsetMonth);
            calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMinimum(Calendar.DAY_OF_MONTH));

            java.util.Date firstDay = calendar.getTime();

            firstDayOfTheMonth = formatter.format(firstDay);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return firstDayOfTheMonth;
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

    void CalculateWeeklyRevenue(final Date startDate, final Date endDate)
    {
        final List<AverageSales_Model> modelList = new ArrayList<>();

        dataReference_avgSales.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot documentSnapshots)
            {
                //--------------------------------------------------------------------//
                // (1) Get Daily models
                //--------------------------------------------------------------------//
                for (DocumentSnapshot documentSnapshot : documentSnapshots)
                {
                    AverageSales_Model model = documentSnapshot.toObject(AverageSales_Model.class);

                    if(!model.getUid().equals(fUser.getUid()))
                        continue;

                    Date date;
                    SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
                    try {
                        date = format.parse(model.getDateSold());

                        if(WithinDateRange(date, startDate, endDate))
                        {
                            modelList.add(model);
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }


                //--------------------------------------------------------------------//
                // (2) Get Weekly Cost
                //--------------------------------------------------------------------//
                List<Integer> weeklyList = GetWeeklySales(modelList);

                // There is 4 data here
                InitBarGraph_Top(weeklyList);
            }
        });
    }

    int GetTotalSum(List<String> list)
    {
        int sum = 0;
        for(String x : list)
        {
            int price = Integer.parseInt(x);
            sum += price;
        }

        return sum;
    }

    List<Integer> GetWeeklySales(List<AverageSales_Model> dailyModels)
    {
        List<Integer> weekList = new ArrayList<>();
        weekList.add(0);
        weekList.add(0);
        weekList.add(0);
        weekList.add(0);
        Log.d("Test", "List Size: " + Integer.toString(weekList.size()));

        for(AverageSales_Model model : dailyModels)
        {
            Date date;
            SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
            try {
                // Get Date
                date = format.parse(model.getDateSold());

                // Get the cost
                int totalCost = GetTotalSum(model.getRevenue());

                // Get Week
                Calendar c = Calendar.getInstance();
                c.setTime(date);
                int week = c.get(Calendar.WEEK_OF_MONTH);
                week -= 1;

                // Set cost
                weekList.set(week, weekList.get(week) + totalCost);

            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return weekList;
    }

    boolean WithinDateRange(Date testDate, Date start, Date end)
    {
        return testDate.after(start) && testDate.before(end);
    }

    void SetupRetrieveDataForTopGraph(String startDateString, String endDateString)
    {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        dateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Singapore"));
        Date startDate = null;
        try {
            startDate = dateFormat.parse(startDateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Date endDate = null;
        try {
            endDate = dateFormat.parse(endDateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        CalculateWeeklyRevenue(startDate, endDate);
    }

    void InitBarGraph_Top(List<Integer> weekList)
    {
        //--------------------------------------------------------------------------------------//
        //
        // Bar Graph
        //
        //--------------------------------------------------------------------------------------//
        BarGraphSeries<DataPoint> series = new BarGraphSeries<>();


        for(int i = 0; i < weekList.size(); ++i) {
            series.appendData(new DataPoint(i + 1, weekList.get(i)), false, weekList.size());
        }
        series.setColor(Color.BLUE);

        series.setTitle("foo");
        //series.setAnimated(true);
        series.setDataWidth(0.75d);
        series.setSpacing(10);
        series.setDrawValuesOnTop(true);
        series.setValuesOnTopColor(Color.RED);
        graph_top.addSeries(series);


        // Show Legend
        graph_top.getLegendRenderer().setVisible(true);
        graph_top.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.TOP);


        // set the viewport wider than the data, to have a nice view
        graph_top.getViewport().setMinX(0);
        graph_top.getViewport().setMaxX(weekList.size() + 1);
        graph_top.getViewport().setMinY(0);
        graph_top.getViewport().setMaxY(1000);


        //graph_top.getViewport().setScrollable(true); // enables horizontal scrolling
        //graph_top.getViewport().setScrollableY(true); // enables vertical scrolling
        graph_top.getViewport().setScalable(true); // enables horizontal zooming and scrolling
        //graph_top.getViewport().setScalableY(true); // enables vertical zooming and scrolling


        //-- Event Listener: On Click
        series.setOnDataPointTapListener(new OnDataPointTapListener() {
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
