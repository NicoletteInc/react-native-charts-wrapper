package com.github.wuxudong.rncharts.charts;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.renderer.XAxisRenderer;

public class LineChartCustom extends LineChart {

    protected XAxisRenderer mSecondXAxisRenderer;
    protected XAxis mSecondXAxis;

    public LineChartCustom(Context context) {
        super(context);
    }

    public LineChartCustom(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LineChartCustom(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void init() {
        super.init();
        mSecondXAxis = new XAxis();
        mSecondXAxisRenderer = new CustomXAxisRenderer(mViewPortHandler, mSecondXAxis, mLeftAxisTransformer);
    }

    public XAxis getSecondXAxis() {
        return mSecondXAxis;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        LineData data = this.getData();

        if(data == null) {
            return;
        }

        if(mSecondXAxis.isEnabled()){
            mSecondXAxis.calculate(data.getXMin(), data.getXMax());
            mSecondXAxisRenderer.computeAxis(mSecondXAxis.getAxisMinimum(), mSecondXAxis.getAxisMaximum(), false);
            mSecondXAxisRenderer.renderAxisLine(canvas);
            mSecondXAxisRenderer.renderAxisLabels(canvas);
        }
    }

}
