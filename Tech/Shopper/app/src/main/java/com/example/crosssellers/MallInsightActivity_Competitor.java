package com.example.crosssellers;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.AxisBase;
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class MallInsightActivity_Competitor extends AppCompatActivity {

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
        setContentView(R.layout.activity_mall_insight_competitor);

        pChart = findViewById(R.id.mall_insight_competitor_PC);

        pChart.setUsePercentValues(true);

        List<PieEntry> value = new ArrayList<>();
        value.add(new PieEntry(60, "H&M"));
        value.add(new PieEntry(5, "CK"));
        value.add(new PieEntry(5, "GG"));
        value.add(new PieEntry(10, "OG"));
        value.add(new PieEntry(20, "CJ"));

        PieDataSet pieDataSet = new PieDataSet(value, "(% of Sales)");

        PieData pieData = new PieData(pieDataSet);
        pieData.setValueTextSize(15f);
        pieData.setHighlightEnabled(true);
        pChart.setData(pieData);
        //for rotating anti-clockwise
        pChart.spin( 500,0,-360f, Easing.EaseInOutQuad);
        pieDataSet.setColors(ColorTemplate.JOYFUL_COLORS);


        //----------------------------------------------------------------------//
        // Action bar                                                           //
        //----------------------------------------------------------------------//
        //-- Add built-in "Actionbar" and it's "Actionbar"->title
        ActionBar actionbar = getSupportActionBar();
        actionbar.setTitle("Competitor Insights");

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
        Intent intent = new Intent(MallInsightActivity_Competitor.this, MallInsightActivity_Home.class);
        startActivity(intent);
        finish();
    }
}