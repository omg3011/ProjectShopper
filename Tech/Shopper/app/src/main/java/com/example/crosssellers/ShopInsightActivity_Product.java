package com.example.crosssellers;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ShopInsightActivity_Product extends AppCompatActivity {

    //-- View(s)
    HorizontalBarChart mChart;
    PieChart pChart;

    public class CategoryBarChartXaxisFormatter extends ValueFormatter implements IAxisValueFormatter {

        ArrayList<String> mValues;

        public CategoryBarChartXaxisFormatter(ArrayList<String> values) {
            this.mValues = values;
        }

        @Override
        public String getFormattedValue(float value, AxisBase axis) {

            int val = (int) value;
            String label = "";
            if (val >= 0 && val < mValues.size()) {
                label = mValues.get(val);
            } else {
                label = "";
            }
            return label;
        }
    }

    public class IndexAxisValueFormatter extends ValueFormatter
    {
        private String[] mValues = new String[] {};
        private int mValueCount = 0;

        /**
         * An empty constructor.
         * Use `setValues` to set the axis labels.
         */
        public IndexAxisValueFormatter() {
        }

        /**
         * Constructor that specifies axis labels.
         *
         * @param values The values string array
         */
        public IndexAxisValueFormatter(String[] values) {
            if (values != null)
                setValues(values);
        }

        /**
         * Constructor that specifies axis labels.
         *
         * @param values The values string array
         */
        public IndexAxisValueFormatter(Collection<String> values) {
            if (values != null)
                setValues(values.toArray(new String[values.size()]));
        }

        @Override
        public String getFormattedValue(float value, AxisBase axisBase) {
            int index = Math.round(value);

            if (index < 0 || index >= mValueCount || index != (int)value)
                return "";

            return mValues[index];
        }

        public String[] getValues()
        {
            return mValues;
        }

        public void setValues(String[] values)
        {
            if (values == null)
                values = new String[] {};

            this.mValues = values;
            this.mValueCount = values.length;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_insight_product);

        pChart = findViewById(R.id.shop_insight_product_PC);
        mChart = findViewById(R.id.shop_insight_product_HBC);
        ArrayList<String> labels = new ArrayList<>();
        labels.add("January");
        labels.add("February");
        labels.add("March");
        labels.add("April");
        labels.add("May");
        labels.add("June");

        mChart = (HorizontalBarChart) findViewById(R.id.shop_insight_product_HBC);
        mChart.setDrawBarShadow(false);
        mChart.setDrawValueAboveBar(true);
        mChart.getDescription().setEnabled(false);
        mChart.setPinchZoom(false);
        mChart.setDrawGridBackground(false);


        XAxis xl = mChart.getXAxis();
       // xl.setPosition(XAxis.XAxisPosition.BOTTOM);
        xl.setPosition(XAxis.XAxisPosition.BOTTOM);
        xl.setDrawAxisLine(true);
        xl.setDrawGridLines(false);
        //CategoryBarChartXaxisFormatter xaxisFormatter = new CategoryBarChartXaxisFormatter(labels);
        //xl.setValueFormatter(xaxisFormatter);
        xl.setGranularity(1);
        //..
        xl.setDrawLabels(false);

        YAxis yl = mChart.getAxisLeft();
        yl.setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART);
        yl.setDrawGridLines(false);
        yl.setEnabled(false);
        yl.setAxisMinimum(0f);


        YAxis yr = mChart.getAxisRight();
        yr.setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART);
        yr.setDrawGridLines(false);
        yr.setAxisMinimum(0f);
        yr.setAxisMaximum(100);
        yr.setDrawLabels(false);

        ArrayList<BarEntry> yVals1 = new ArrayList<BarEntry>();
        yVals1.add(new BarEntry(0, 5));
        yVals1.add(new BarEntry(1, 12));
        yVals1.add(new BarEntry(2, 15));
        yVals1.add(new BarEntry(3, 18));
        yVals1.add(new BarEntry(4, 60));


        BarDataSet set1;
        set1 = new BarDataSet(yVals1, "DataSet 1");
        ArrayList<IBarDataSet> dataSets = new ArrayList<IBarDataSet>();
        dataSets.add(set1);
        BarData data = new BarData(dataSets);
        data.setValueTextSize(10f);
        data.setBarWidth(.9f);
        mChart.setData(data);
        mChart.getLegend().setEnabled(false);

        /*
        final String[] weekdays = {"Sun", "Mon", "Tue", "Wed", "Thu"};

            // Set the value formatter
        YAxis yAxis = mChart.getAxisLeft();
        yAxis.setValueFormatter(new IndexAxisValueFormatter(weekdays));
        yAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);

         */

        pChart.setUsePercentValues(false);

        List<PieEntry> value = new ArrayList<>();
        value.add(new PieEntry(600, "BST"));
        value.add(new PieEntry(180, "YST"));
        value.add(new PieEntry(150, "RST"));
        value.add(new PieEntry(120, "GST"));
        value.add(new PieEntry(50, "OST"));

        PieDataSet pieDataSet = new PieDataSet(value, "(Top 5 Products)");

        PieData pieData = new PieData(pieDataSet);
        pChart.setData(pieData);
        pieDataSet.setColors(ColorTemplate.JOYFUL_COLORS);


        //----------------------------------------------------------------------//
        // Action bar                                                           //
        //----------------------------------------------------------------------//
        //-- Add built-in "Actionbar" and it's "Actionbar"->title
        ActionBar actionbar = getSupportActionBar();
        actionbar.setTitle("Product/Service Insights");

        //-- Enable "Actionbar"->back button
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setDisplayShowHomeEnabled(true);
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
        Intent intent = new Intent(ShopInsightActivity_Product.this, ShopInsightActivity_Home.class);
        startActivity(intent);
        finish();
    }
}

