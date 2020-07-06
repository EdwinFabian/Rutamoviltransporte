package com.example.rutamoviltransporte;

import android.app.AlertDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class Report extends Fragment {
    private View ReporView;
    private PieChart mChart;

    //float rainfall[] = {98.8f, 123.8f, 161.6f, 24.2f };
    //String monthNames[] = {"Enero", "Febrero", "Marzo", "Abril"};

    public Report() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        ReporView =  inflater.inflate(R.layout.fragment_report, container, false);
        setupPieChart();
        //setupPieChart();
        return ReporView;
    }

    private void setupPieChart() {
        mChart = ReporView.findViewById(R.id.chart);
        mChart.setBackgroundColor(Color.WHITE);
        moveoffscreen();

        mChart.setUsePercentValues(true);
        mChart.getDescription().setEnabled(false);
        mChart.setDrawHoleEnabled(true);
        //mChart.setMaxAngle(180);
        //mChart.setRotationAngle(180);
        mChart.setCenterTextOffset(0, -20);
        mChart.animateY(1000, Easing.EaseInOutCubic);
        setData(4, 100);

        Legend l = mChart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(false);
        l.setYOffset(70f);

        mChart.setEntryLabelColor(Color.WHITE);
        mChart.setEntryLabelTextSize(12f);
    }
    String[] semanas  = new String[]{"Lunes", "Martes", "Miercoles", "Jueves", "Viernes", "Sabado"};
    private  void setData(int count, int range){
        ArrayList<PieEntry> values = new ArrayList<>();
        for (int i = 0; i< count; i++){
            float val = (float)((Math.random()*range)*range/5);
            values.add(new PieEntry(val,semanas[i]));
        }
        PieDataSet dataSet = new PieDataSet(values, "Semanas");
        dataSet.setSelectionShift(5f);
        dataSet.setSliceSpace(3f);
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);

        PieData data = new PieData(dataSet);
        data.setValueFormatter(new PercentFormatter());
        data.setValueTextSize(10f);
        data.setValueTextColor(Color.WHITE);

        mChart.setData(data);
        mChart.invalidate();
    }
    private void moveoffscreen(){
        Display display = getActivity().getWindowManager().getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int height = metrics.heightPixels;
        int offset = (int) (height*0.1);
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)mChart.getLayoutParams();
        params.setMargins(0,0,0,-offset);
        mChart.setLayoutParams(params);
    }



/*
    private void setupPieChart() {
        List<PieEntry> pieEntries = new ArrayList<>();
        for (int i = 0; i < rainfall.length; i++){
            pieEntries.add(new PieEntry(rainfall[i], monthNames[i]));

        }
        PieDataSet dataSet = new PieDataSet(pieEntries, "Rainfall for vancouber");
        dataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        PieData data = new PieData(dataSet);


        PieChart chart = ReporView.findViewById(R.id.chart);
        chart.setData(data);
        chart.animateY(1000);
        chart.invalidate();
    }*/
}
