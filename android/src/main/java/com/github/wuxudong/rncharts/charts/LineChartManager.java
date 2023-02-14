package com.github.wuxudong.rncharts.charts;


import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.ReadableType;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.annotations.ReactProp;
import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.wuxudong.rncharts.data.DataExtract;
import com.github.wuxudong.rncharts.data.LineDataExtract;
import com.github.wuxudong.rncharts.listener.RNOnChartValueSelectedListener;
import com.github.wuxudong.rncharts.listener.RNOnChartGestureListener;
import com.github.wuxudong.rncharts.utils.BridgeUtils;

public class LineChartManager extends BarLineChartBaseManager<LineChart, Entry> {

    @Override
    public String getName() {
        return "RNLineChart";
    }

    @Override
    protected LineChart createViewInstance(ThemedReactContext reactContext) {
        LineChart lineChart =  new LineChartCustom(reactContext);
        lineChart.setOnChartValueSelectedListener(new RNOnChartValueSelectedListener(lineChart));
        lineChart.setOnChartGestureListener(new RNOnChartGestureListener(lineChart));
        return lineChart;
    }

    @Override
    DataExtract getDataExtract() {
        return new LineDataExtract();
    }

    /**
     * xAxis config details: https://github.com/PhilJay/MPAndroidChart/wiki/XAxis
     */
    @ReactProp(name = "secondXAxis")
    public void setSecondXAxis(Chart chart, ReadableMap propMap) {
        if(chart instanceof LineChartCustom){
            XAxis axis = ((LineChartCustom) chart).getSecondXAxis();

            setCommonAxisConfig(chart, axis, propMap);

            if (BridgeUtils.validate(propMap, ReadableType.Number, "labelRotationAngle")) {
                axis.setLabelRotationAngle((float) propMap.getDouble("labelRotationAngle"));
            }
            if (BridgeUtils.validate(propMap, ReadableType.Boolean, "avoidFirstLastClipping")) {
                axis.setAvoidFirstLastClipping(propMap.getBoolean("avoidFirstLastClipping"));
            }
            if (BridgeUtils.validate(propMap, ReadableType.String, "position")) {
                axis.setPosition(XAxis.XAxisPosition.valueOf(propMap.getString("position")));
            }
        }
    }

}
