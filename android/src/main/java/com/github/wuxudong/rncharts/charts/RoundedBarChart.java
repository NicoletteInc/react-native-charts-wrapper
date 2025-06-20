package com.github.wuxudong.rncharts.charts;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

import com.github.mikephil.charting.animation.ChartAnimator;
import com.github.mikephil.charting.buffer.BarBuffer;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.highlight.Range;
import com.github.mikephil.charting.interfaces.dataprovider.BarDataProvider;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.model.GradientColor;
import com.github.mikephil.charting.renderer.BarChartRenderer;
import com.github.mikephil.charting.utils.MPPointF;
import com.github.mikephil.charting.utils.Transformer;
import com.github.mikephil.charting.utils.Utils;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RoundedBarChart extends BarChart {
    public RoundedBarChart(Context context) {
        super(context);
    }

    public RoundedBarChart(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RoundedBarChart(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setRadius(int radius) {
        setRenderer(new RoundedBarChartRenderer(this, getAnimator(), getViewPortHandler(), radius));
    }

    private static class RoundedBarChartRenderer extends BarChartRenderer {
        private final int mRadius;
        private final RectF mBarShadowRectBuffer = new RectF();

        RoundedBarChartRenderer(BarDataProvider chart, ChartAnimator animator, ViewPortHandler viewPortHandler, int mRadius) {
            super(chart, animator, viewPortHandler);
            this.mRadius = mRadius;
        }

        @Override
        public void drawHighlighted(Canvas c, Highlight[] indices) {
            BarData barData = mChart.getBarData();

            for (Highlight high : indices) {

                IBarDataSet set = barData.getDataSetByIndex(high.getDataSetIndex());

                if (set == null || !set.isHighlightEnabled())
                    continue;

                BarEntry e = set.getEntryForXValue(high.getX(), high.getY());

                if (!isInBoundsX(e, set))
                    continue;

                Transformer trans = mChart.getTransformer(set.getAxisDependency());

                mHighlightPaint.setColor(set.getHighLightColor());
                mHighlightPaint.setAlpha(set.getHighLightAlpha());

                boolean isStack = high.getStackIndex() >= 0 && e.isStacked();

                final float y1;
                final float y2;

                if (isStack) {

                    if (mChart.isHighlightFullBarEnabled()) {

                        y1 = e.getPositiveSum();
                        y2 = -e.getNegativeSum();

                    } else {

                        Range range = e.getRanges()[high.getStackIndex()];

                        y1 = range.from;
                        y2 = range.to;
                    }

                } else {
                    y1 = e.getY();
                    y2 = 0.f;
                }

                prepareBarHighlight(e.getX(), y1, y2, barData.getBarWidth() / 2f, trans);

                setHighlightDrawPos(high, mBarRect);

                c.drawRoundRect(mBarRect, mRadius, mRadius, mHighlightPaint);
            }
        }


        @Override
        public void drawValues(Canvas c) {
            // if values are drawn
            if (isDrawingValuesAllowed(mChart)) {

                List<IBarDataSet> dataSets = mChart.getBarData().getDataSets();

                final float valueOffsetPlus = Utils.convertDpToPixel(4.5f);
                float posOffset = 0f;
                float negOffset = 0f;
                boolean drawValueAboveBar = mChart.isDrawValueAboveBarEnabled();

                for (int i = 0; i < mChart.getBarData().getDataSetCount(); i++) {

                    IBarDataSet dataSet = dataSets.get(i);

                    if (!shouldDrawValues(dataSet)) continue;

                    // apply the text-styling defined by the DataSet
                    applyValueTextStyle(dataSet);

                    boolean isInverted = mChart.isInverted(dataSet.getAxisDependency());

                    // calculate the correct offset depending on the draw position of
                    // the value
                    float valueTextHeight = Utils.calcTextHeight(mValuePaint, "8");
                    posOffset = (drawValueAboveBar ? -valueOffsetPlus : valueTextHeight + valueOffsetPlus);
                    negOffset = (drawValueAboveBar ? valueTextHeight + valueOffsetPlus : -valueOffsetPlus);

                    if (isInverted) {
                        posOffset = -posOffset - valueTextHeight;
                        negOffset = -negOffset - valueTextHeight;
                    }

                    // get the buffer
                    BarBuffer buffer = mBarBuffers[i];

                    final float phaseY = mAnimator.getPhaseY();

                    ValueFormatter formatter = dataSet.getValueFormatter();

                    MPPointF iconsOffset = MPPointF.getInstance(dataSet.getIconsOffset());
                    iconsOffset.x = Utils.convertDpToPixel(iconsOffset.x);
                    iconsOffset.y = Utils.convertDpToPixel(iconsOffset.y);

                    // if only single values are drawn (sum)
                    if (!dataSet.isStacked()) {

                        for (int j = 0; j < buffer.buffer.length * mAnimator.getPhaseX(); j += 4) {

                            float x = (buffer.buffer[j] + buffer.buffer[j + 2]) / 2f;

                            if (!mViewPortHandler.isInBoundsRight(x)) break;
                            if (!mViewPortHandler.isInBoundsLeft(x)) continue;

                            BarEntry entry = dataSet.getEntryForIndex(j / 4);
                            float val = entry.getY();
                            String stringValue = formatter.getBarLabel(entry);

                            if(entry.getData() != null && entry.getData() instanceof Map) {
                                Map<String, Object> data = (Map<String, Object>) entry.getData();
                                if (data.get("yLabel") != null && data.get("yLabel") instanceof String) {
                                    stringValue = (String) data.get("yLabel");
                                }
                                if (data.get("yLabelOffset") != null && data.get("yLabelOffset") != null && data.get("yLabelOffset") instanceof Integer) {
                                    int yLabelOffset = (Integer) data.get("yLabelOffset");
                                    posOffset = (drawValueAboveBar ? -(valueOffsetPlus + yLabelOffset) : valueTextHeight + valueOffsetPlus + yLabelOffset);
                                    negOffset = (drawValueAboveBar ? valueTextHeight + valueOffsetPlus + yLabelOffset: -(valueOffsetPlus + yLabelOffset));
                                }
                            }

                            // Patch: Always show label above bar for small bars (positive values only)
                            float barTop = buffer.buffer[j + 1];
                            float barBottom = buffer.buffer[j + 3];
                            float barHeight = Math.abs(barBottom - barTop);
                            float labelY;
                            if (val < 7.0f) {
                                // Draw label above the bar
                                labelY = barTop - valueTextHeight;
                            } else {
                                // Regular positioning
                                labelY = val >= 0 ? (barTop + posOffset) : (barBottom + negOffset);
                            }

                            if (dataSet.isDrawValuesEnabled()) {
                                drawValue(c, stringValue, x, labelY, dataSet.getValueTextColor(j / 4));
                            }

                            if (entry.getIcon() != null && dataSet.isDrawIconsEnabled()) {

                                Drawable icon = entry.getIcon();

                                float px = x;
                                float py = labelY;

                                px += iconsOffset.x;
                                py += iconsOffset.y;

                                Utils.drawImage(
                                        c,
                                        icon,
                                        (int)px,
                                        (int)py,
                                        icon.getIntrinsicWidth(),
                                        icon.getIntrinsicHeight());
                            }
                        }

                        // if we have stacks
                    } else {

                        Transformer trans = mChart.getTransformer(dataSet.getAxisDependency());

                        int bufferIndex = 0;
                        int index = 0;

                        while (index < dataSet.getEntryCount() * mAnimator.getPhaseX()) {

                            BarEntry entry = dataSet.getEntryForIndex(index);

                            float[] vals = entry.getYVals();
                            float x = (buffer.buffer[bufferIndex] + buffer.buffer[bufferIndex + 2]) / 2f;

                            int color = dataSet.getValueTextColor(index);

                            // we still draw stacked bars, but there is one
                            // non-stacked
                            // in between
                            if (vals == null) {

                               if (!mViewPortHandler.isInBoundsRight(x)) break;
                               if (!mViewPortHandler.isInBoundsLeft(x)) continue;

                                if (dataSet.isDrawValuesEnabled()) {
                                    drawValue(c, formatter.getBarLabel(entry), x, buffer.buffer[bufferIndex + 1] +
                                                    (entry.getY() >= 0 ? posOffset : negOffset),
                                            color);
                                }

                                if (entry.getIcon() != null && dataSet.isDrawIconsEnabled()) {

                                    Drawable icon = entry.getIcon();

                                    float px = x;
                                    float py = buffer.buffer[bufferIndex + 1] +
                                            (entry.getY() >= 0 ? posOffset : negOffset);

                                    px += iconsOffset.x;
                                    py += iconsOffset.y;

                                    Utils.drawImage(
                                            c,
                                            icon,
                                            (int)px,
                                            (int)py,
                                            icon.getIntrinsicWidth(),
                                            icon.getIntrinsicHeight());
                                }

                                // draw stack values
                            } else {

                                float[] transformed = new float[vals.length * 2];

                                float posY = 0f;
                                float negY = -entry.getNegativeSum();

                                for (int k = 0, idx = 0; k < transformed.length; k += 2, idx++) {

                                    float value = vals[idx];
                                    float y;

                                    if (value == 0.0f && (posY == 0.0f || negY == 0.0f)) {
                                        // Take care of the situation of a 0.0 value, which overlaps a non-zero bar
                                        y = value;
                                    } else if (value >= 0.0f) {
                                        posY += value;
                                        y = posY;
                                    } else {
                                        y = negY;
                                        negY -= value;
                                    }

                                    transformed[k + 1] = y * phaseY;
                                }

                                trans.pointValuesToPixel(transformed);

                                for (int k = 0; k < transformed.length; k += 2) {
                                    final int stackIndex = k / 2;
                                    final float val = vals[stackIndex];
                                    final boolean drawBelow = (val == 0.0f && negY == 0.0f && posY > 0.0f) || val < 0.0f;

                                    String stringValue = formatter.getBarStackedLabel(val, entry);

                                    if(entry.getData() != null && entry.getData() instanceof Map) {
                                        Map<String, Object> data = (Map<String, Object>) entry.getData();
                                        if (data.get("yLabel") != null && data.get("yLabel") instanceof List) {
                                            List yLabels = ((List) data.get("yLabel"));
                                            stringValue = yLabels.size() > stackIndex ? ((String)yLabels.get(stackIndex)) : stringValue;
                                        }
                                        if (data.get("yLabelOffset") != null && data.get("yLabelOffset") != null && data.get("yLabelOffset") instanceof Integer) {
                                            int yLabelOffset = (Integer) data.get("yLabelOffset");
                                            posOffset = (drawValueAboveBar ? -(valueOffsetPlus + yLabelOffset) : valueTextHeight + valueOffsetPlus + yLabelOffset);
                                            negOffset = (drawValueAboveBar ? valueTextHeight + valueOffsetPlus + yLabelOffset: -(valueOffsetPlus + yLabelOffset));
                                        }
                                    }

                                    int labelColor;
                                    float y;
                                    if (val < 7.0f) {
                                        // Draw label above the bar with dark pink color
                                        y = buffer.buffer[bufferIndex + 1] - valueTextHeight;
                                        labelColor = Color.parseColor("#C2185B");
                                    } else {
                                        // Regular positioning and color
                                        y = transformed[k + 1] + (drawBelow ? negOffset : posOffset);
                                        labelColor = dataSet.getValueTextColor(index);
                                    }

                                    if (!mViewPortHandler.isInBoundsRight(x))
                                        break;

                                    if (!mViewPortHandler.isInBoundsLeft(x))
                                        continue;

                                    if (dataSet.isDrawValuesEnabled()) {
                                        drawValue(c, stringValue, x, y, labelColor);
                                    }

                                    if (entry.getIcon() != null && dataSet.isDrawIconsEnabled()) {

                                        Drawable icon = entry.getIcon();

                                        Utils.drawImage(
                                                c,
                                                icon,
                                                (int)(x + iconsOffset.x),
                                                (int)(y + iconsOffset.y),
                                                icon.getIntrinsicWidth(),
                                                icon.getIntrinsicHeight());
                                    }
                                }
                            }

                            bufferIndex = vals == null ? bufferIndex + 4 : bufferIndex + 4 * vals.length;
                            index++;
                        }
                    }

                    MPPointF.recycleInstance(iconsOffset);
                }
            }
        }

        @Override
        protected void drawDataSet(Canvas c, IBarDataSet dataSet, int index) {

            final float MIN_BAR_HEIGHT_PX = 6f; // Minimum bar height in pixels
            Transformer trans = mChart.getTransformer(dataSet.getAxisDependency());

            mShadowPaint.setColor(dataSet.getBarShadowColor());

            float phaseX = mAnimator.getPhaseX();
            float phaseY = mAnimator.getPhaseY();

            // initialize the buffer
            BarBuffer buffer = mBarBuffers[index];
            buffer.setPhases(phaseX, phaseY);
            buffer.setDataSet(index);
            buffer.setInverted(mChart.isInverted(dataSet.getAxisDependency()));
            buffer.setBarWidth(mChart.getBarData().getBarWidth());

            buffer.feed(dataSet);

            trans.pointValuesToPixel(buffer.buffer);

            // if multiple colors
            if (dataSet.getColors().size() > 1) {
                for (int j = 0; j < buffer.size(); j += 4) {

                    if (!mViewPortHandler.isInBoundsLeft(buffer.buffer[j + 2]))
                        continue;

                    if (!mViewPortHandler.isInBoundsRight(buffer.buffer[j]))
                        break;

                    if (mChart.isDrawBarShadowEnabled()) {
                        if (mRadius > 0)
                            c.drawRoundRect(new RectF(buffer.buffer[j], mViewPortHandler.contentTop(),
                                    buffer.buffer[j + 2],
                                    mViewPortHandler.contentBottom()), mRadius, mRadius, mShadowPaint);
                        else
                            c.drawRect(buffer.buffer[j], mViewPortHandler.contentTop(),
                                    buffer.buffer[j + 2],
                                    mViewPortHandler.contentBottom(), mShadowPaint);
                    }

                    int _mRadius = mRadius;
                    int entryIndex = dataSet.isStacked() ? ( j / 4) / dataSet.getStackSize() :  j / 4;
                    int stackIndex = (j / 4) % dataSet.getStackSize();
                    BarEntry e = dataSet.getEntryForIndex(entryIndex);
                    float top = buffer.buffer[j + 1];
                    float bottom = buffer.buffer[j + 3];
                    float height = Math.abs(bottom - top);
                    // Only patch for positive values (bar goes up)
                    if (e.getY() > 0 && height < MIN_BAR_HEIGHT_PX) {
                        // For positive bars, top is above bottom (smaller y is higher on screen)
                        top = bottom - MIN_BAR_HEIGHT_PX;
                        buffer.buffer[j + 1] = top;
                    }
                    RectF rect = new RectF(buffer.buffer[j], buffer.buffer[j + 1], buffer.buffer[j + 2], buffer.buffer[j + 3]);

                    int drawIndex = -1;
                    if(e.getYVals() != null) {
                        for (float val : e.getYVals()) {
                            drawIndex = val > 0 ? drawIndex + 1 : drawIndex;
                        }
                        _mRadius = stackIndex >= drawIndex ? mRadius : 0;
                    }

                    mRenderPaint.setColor(dataSet.getColor(j / 4));
                    if (_mRadius > 0) {
                        Path path2 = roundRect(rect, mRadius, mRadius, true, true, false, false);
                        c.drawPath(path2, mRenderPaint);
                    }
                    else
                        c.drawRect(buffer.buffer[j], buffer.buffer[j + 1], buffer.buffer[j + 2],
                                buffer.buffer[j + 3], mRenderPaint);
                }
            } else {

                mRenderPaint.setColor(dataSet.getColor());
                for (int j = 0; j < buffer.size(); j += 4) {

                    if (!mViewPortHandler.isInBoundsLeft(buffer.buffer[j + 2]))
                        continue;

                    if (!mViewPortHandler.isInBoundsRight(buffer.buffer[j]))
                        break;

                    if (mChart.isDrawBarShadowEnabled()) {
                        if (mRadius > 0)
                            c.drawRoundRect(new RectF(buffer.buffer[j], mViewPortHandler.contentTop(),
                                    buffer.buffer[j + 2],
                                    mViewPortHandler.contentBottom()), mRadius, mRadius, mShadowPaint);
                        else
                            c.drawRect(buffer.buffer[j], buffer.buffer[j + 1], buffer.buffer[j + 2],
                                    buffer.buffer[j + 3], mRenderPaint);
                    }

                    float top = buffer.buffer[j + 1];
                    float bottom = buffer.buffer[j + 3];
                    float height = Math.abs(bottom - top);
                    // Only patch for positive values (bar goes up)
                    BarEntry e = dataSet.getEntryForIndex(j / 4);
                    if (e.getY() > 0 && height < MIN_BAR_HEIGHT_PX) {
                        top = bottom - MIN_BAR_HEIGHT_PX;
                        buffer.buffer[j + 1] = top;
                    }

                    if (mRadius > 0)
                        c.drawRoundRect(new RectF(buffer.buffer[j], buffer.buffer[j + 1], buffer.buffer[j + 2],
                                buffer.buffer[j + 3]), mRadius, mRadius, mRenderPaint);
                    else
                        c.drawRect(buffer.buffer[j], buffer.buffer[j + 1], buffer.buffer[j + 2],
                                buffer.buffer[j + 3], mRenderPaint);
                }
            }
        }

        private Path roundRect(RectF rect, float rx, float ry, boolean tl, boolean tr, boolean br, boolean bl) {
            float top = rect.top;
            float left = rect.left;
            float right = rect.right;
            float bottom = rect.bottom;
            Path path = new Path();
            if (rx < 0) rx = 0;
            if (ry < 0) ry = 0;
            float width = right - left;
            float height = bottom - top;
            if (rx > width / 2) rx = width / 2;
            if (ry > height / 2) ry = height / 2;
            float widthMinusCorners = (width - (2 * rx));
            float heightMinusCorners = (height - (2 * ry));

            path.moveTo(right, top + ry);
            if (tr)
                path.rQuadTo(0, -ry, -rx, -ry);//top-right corner
            else {
                path.rLineTo(0, -ry);
                path.rLineTo(-rx, 0);
            }
            path.rLineTo(-widthMinusCorners, 0);
            if (tl)
                path.rQuadTo(-rx, 0, -rx, ry); //top-left corner
            else {
                path.rLineTo(-rx, 0);
                path.rLineTo(0, ry);
            }
            path.rLineTo(0, heightMinusCorners);

            if (bl)
                path.rQuadTo(0, ry, rx, ry);//bottom-left corner
            else {
                path.rLineTo(0, ry);
                path.rLineTo(rx, 0);
            }

            path.rLineTo(widthMinusCorners, 0);
            if (br)
                path.rQuadTo(rx, 0, rx, -ry); //bottom-right corner
            else {
                path.rLineTo(rx, 0);
                path.rLineTo(0, -ry);
            }

            path.rLineTo(0, -heightMinusCorners);

            path.close();//Given close, last lineto can be removed.

            return path;
        }
    }
}