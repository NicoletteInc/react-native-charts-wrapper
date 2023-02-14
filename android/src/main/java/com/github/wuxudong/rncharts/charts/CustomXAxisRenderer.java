package com.github.wuxudong.rncharts.charts;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.renderer.XAxisRenderer;
import com.github.mikephil.charting.utils.MPPointF;
import com.github.mikephil.charting.utils.Transformer;
import com.github.mikephil.charting.utils.Utils;
import com.github.mikephil.charting.utils.ViewPortHandler;

public class CustomXAxisRenderer extends XAxisRenderer {

    private final Paint mAxisBackgroundLabelPaint;

    public CustomXAxisRenderer(ViewPortHandler viewPortHandler, XAxis xAxis, Transformer trans) {
        super(viewPortHandler, xAxis, trans);
        mAxisBackgroundLabelPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mAxisBackgroundLabelPaint.setColor(Color.WHITE);
        mAxisBackgroundLabelPaint.setStyle(Paint.Style.FILL);
    }

    @Override
    protected void drawLabels(Canvas c, float pos, MPPointF anchor) {

        final float labelRotationAngleDegrees = mXAxis.getLabelRotationAngle();
        boolean centeringEnabled = mXAxis.isCenterAxisLabelsEnabled();

        float[] positions = new float[mXAxis.mEntryCount * 2];

        for (int i = 0; i < positions.length; i += 2) {

            // only fill x values
            if (centeringEnabled) {
                positions[i] = mXAxis.mCenteredEntries[i / 2];
            } else {
                positions[i] = mXAxis.mEntries[i / 2];
            }
        }

        mTrans.pointValuesToPixel(positions);

        for (int i = 0; i < positions.length; i += 2) {

            float x = positions[i];

            if (mViewPortHandler.isInBoundsX(x)) {

                float tickIndex = mXAxis.mEntries[i / 2];
                float distance = mXAxis.mEntries.length > 1 ? mXAxis.mEntries[1] - mXAxis.mEntries[0] : 1;

                String textBefore = mXAxis.getValueFormatter().getAxisLabel(tickIndex - distance, mXAxis);
                String textCurrent = mXAxis.getValueFormatter().getAxisLabel(tickIndex, mXAxis);

                boolean show = (textCurrent != null && !textCurrent.isEmpty() && !textCurrent.equals(textBefore));

                if (!show) {
                    continue;
                }

                float width = Utils.calcTextWidth(mAxisLabelPaint, textCurrent);

                if (mXAxis.isAvoidFirstLastClippingEnabled()) {

                    // avoid clipping of the last
                    if (i / 2 == mXAxis.mEntryCount - 1 && mXAxis.mEntryCount > 1) {
                        if (width > mViewPortHandler.offsetRight() * 2
                                && x + width > mViewPortHandler.getChartWidth())
                            x -= width / 2;

                        // avoid clipping of the first
                    } else if (i == 0) {
                        x += width / 2;
                    }
                }

                c.drawRect(x - width / 2, pos, x + width / 2, pos + 30, mAxisBackgroundLabelPaint);
                drawLabel(c, textCurrent, x, pos, anchor, labelRotationAngleDegrees);
            }
        }
    }

    @Override
    protected void drawLabel(Canvas c, String formattedLabel, float x, float y, MPPointF anchor, float angleDegrees) {
        Utils.drawXAxisValue(c, formattedLabel, x, y, mAxisLabelPaint, anchor, angleDegrees);
    }
}
