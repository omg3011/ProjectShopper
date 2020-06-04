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

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
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

public class ShopInsightActivity_Sales extends AppCompatActivity {

    //-- DB(s)
    FirebaseUser fUser;
    CollectionReference dataReference_avgSales;

    //-- View(s)
    GraphView graph_top;
    Spinner spinner_top, spinner_btm_start, spinner_btm_end;

    //-- Variable(s)
    BarGraphSeries<DataPoint> barSeries;
    String[] items, items2, items3, itemHeading;

    BarChart barChart_btm;

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

        itemHeading = getResources().getStringArray(R.array.shopInsight_sales_hour);

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
        barChart_btm = findViewById(R.id.shop_insight_sales_btm_BC);

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
        spinner_top.setSelection(1);
        spinner_top.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                String select = (String)items[position];

                //-- Get today
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
                simpleDateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Singapore"));
                String today = simpleDateFormat.format(new Date());
                int currMonth = getMonth(today);

                // Past 1 month
                if(position == 0)
                {
                    SetupRetrieveDataForTopGraph(getFirstDayOfTheMonth(today, -1), getLastDayOfTheMonth(today, -1), 1, currMonth);
                }
                // Past 2 month
                else if(position == 1)
                {
                    SetupRetrieveDataForTopGraph(getFirstDayOfTheMonth(today, -2), getLastDayOfTheMonth(today, -1), 2, currMonth);
                }
                // Past 3 month
                else if(position == 2)
                {
                    SetupRetrieveDataForTopGraph(getFirstDayOfTheMonth(today, -3), getLastDayOfTheMonth(today, -1), 3, currMonth);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        spinner_btm_start.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                SetupBtmGraphFake(position, spinner_btm_end.getSelectedItemPosition());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinner_btm_end.setSelection(6);
        spinner_btm_end.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                SetupBtmGraphFake(spinner_btm_start.getSelectedItemPosition(), position);
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

    void SetupRetrieveDataForTopGraph(String startDateString, String endDateString, final int noOfMonths, final int currMonth)
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

        CalculateWeeklyRevenue(startDate, endDate, noOfMonths, currMonth);
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
        barSeries.setColor(Color.rgb(230, 0, 55));

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
        graph_top.getViewport().setMaxY(2500);

        graph_top.getViewport().setScrollable(true); // enables horizontal scrolling
        graph_top.getViewport().setScrollableY(true); // enables vertical scrolling
        graph_top.getViewport().setScalable(true); // enables horizontal zooming and scrolling
        graph_top.getViewport().setScalableY(true); // enables vertical zooming and scrolling
        //graph_top.getGridLabelRenderer().setLabelVerticalWidth(5);


        //-- Event Listener: On Click
        barSeries.setOnDataPointTapListener(new OnDataPointTapListener() {
            @Override
            public void onTap(Series s, DataPointInterface dataPoint) {
                Toast.makeText(ShopInsightActivity_Sales.this, "(" + dataPoint.getX() + "," + dataPoint.getY() + ")", Toast.LENGTH_SHORT).show();
            }
        });
    }

    void SetupBtmGraphFake(int leftIndex, int rightIndex)
    {
        // Create 13 slots
        List<Integer> hourList = new ArrayList<>();
        String[] labels = getResources().getStringArray(R.array.shopInsight_sales_hour);
        for(int i = 0; i < 12; ++i)
        {
            hourList.add(0);
        }

        // Assign value to each
        hourList.set(0, 30);
        hourList.set(1, 0);
        hourList.set(2, 35);
        hourList.set(3, 0);
        hourList.set(4, 65);
        hourList.set(5, 32);
        hourList.set(6, 34);
        hourList.set(7, 0);
        hourList.set(8, 37);
        hourList.set(9, 36);
        hourList.set(10, 90);
        hourList.set(11, 120);

        List<Integer> chooseHours = new ArrayList<>();
        List<String> chooseLabels = new ArrayList<>();

        for(int i = leftIndex; i < rightIndex; ++i)
        {
            chooseHours.add(hourList.get(i));
            chooseLabels.add(labels[i]);
        }

        InitBarGraph_Btm(chooseHours, chooseLabels);
    }

    void SetupRetrieveDataForBtmGraph(String startTimeString, String endTimeString)
    {
        Date startDate = null;
        Date endDate = null;

        //-- Get Start Date
        SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mmaa");
        try {
            startDate = dateFormat.parse(startTimeString);
        }
        catch (ParseException e) {
        }

        //-- Get End Date
        try {
            endDate = dateFormat.parse(endTimeString);
        }
        catch (ParseException e) {
        }

        CalculateAverageCustomerSales(startDate, endDate);
    }


    void InitBarGraph_Btm(List<Integer> hourList, List<String> labels)
    {
        List<BarEntry> entries = new ArrayList<>();

        for(int i = 0; i < hourList.size(); ++i)
        {
            entries.add(new BarEntry(Float.parseFloat(labels.get(i)), hourList.get(i)));
        }


        BarDataSet set = new BarDataSet(entries, "Time");

        set.setColor(Color.rgb(230, 0, 55));
        set.setValueTextSize(10f);
        BarData data = new BarData(set);
        data.setBarWidth(0.66f); // set custom bar width
        barChart_btm.setData(data);
        barChart_btm.setFitBars(true); // make the x-axis fit exactly all bars
        barChart_btm.invalidate();
    }


    //------------------------------------------------------------------------------------//
    //
    //  Average Customer Sales
    //
    //------------------------------------------------------------------------------------//
    void CalculateAverageCustomerSales(Date startTime, Date endtime)
    {
        final List<AverageSales_Model> modelList = new ArrayList<>();

        dataReference_avgSales.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot documentSnapshots)
            {
                //--------------------------------------------------------------------//
                // (1) Get Daily models
                //--------------------------------------------------------------------//
                for (DocumentSnapshot documentSnapshot : documentSnapshots) {
                    AverageSales_Model model = documentSnapshot.toObject(AverageSales_Model.class);

                    // (!model.getUid().equals(fUser.getUid()))
                    //    continue;

                    if (model.getDateSold().equals("21-3-2020")) {
                        modelList.add(model);
                    }
                }
                //--------------------------------------------------------------------//
                // (2) Get Weekly Cost
                //--------------------------------------------------------------------//
                List<Integer> salesByHourList = GetHourlySales(modelList);

                // There is 4 data here
                //InitBarGraph_Btm(salesByHourList);
            }
        });
    }


    List<Integer> GetHourlySales(List<AverageSales_Model> dailyModels)
    {
        List<Integer> hourList = new ArrayList<>();
        hourList.add(0);
        hourList.add(0);
        hourList.add(0);
        hourList.add(0);
        hourList.add(0);
        hourList.add(0);
        hourList.add(0);
        hourList.add(0);
        hourList.add(0);
        hourList.add(0);
        hourList.add(0);
        hourList.add(0);
        hourList.add(0);

        for(AverageSales_Model model : dailyModels)
        {
            for(int i = 0; i < model.getRevenue().size(); ++i)
            {
                hourList.set(i, Integer.parseInt(model.getRevenue().get(i)));
            }
        }
        return hourList;
    }

    //------------------------------------------------------------------------------------//
    //
    //  Past Month Sales
    //
    //------------------------------------------------------------------------------------//
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

    int getMonth(String date)
    {
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        Date dt= null;
        try {
            dt = formatter.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(dt);

        return calendar.get(Calendar.MONTH);
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

    List<Integer> GetWeeklySales(List<AverageSales_Model> dailyModels, int noOfMonths, int currMonth)
    {
        List<Integer> weekList = new ArrayList<>();
        for(int i = 0; i < noOfMonths; ++i)
        {
            weekList.add(0);
            weekList.add(0);
            weekList.add(0);
            weekList.add(0);
        }

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
                int month = c.get(Calendar.MONTH);

                // Set cost
                if(week < 0)
                    week = 0;

                // Which month
                int multiplier = currMonth - month - 1;
                if(multiplier < 0) multiplier = 0;
                week += multiplier * 4;
                if(week > weekList.size()-1)
                    week = weekList.size()-1;
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

    void CalculateWeeklyRevenue(final Date startDate, final Date endDate, final int noOfMonths, final int currMonth)
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

                    //if(!model.getUid().equals(fUser.getUid()))
                    //    continue;

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
                List<Integer> weeklyList = GetWeeklySales(modelList, noOfMonths, currMonth);

                // There is 4 data here
                InitBarGraph_Top(weeklyList);
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
