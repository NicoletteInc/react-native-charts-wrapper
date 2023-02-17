package com.github.wuxudong.rncharts.utils;

import android.graphics.Typeface;

import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.ReadableType;
import com.facebook.react.views.text.ReactFontManager;
import com.github.mikephil.charting.charts.Chart;

/**
 * Created by wuxudong on 2018/7/25.
 */

public class TypefaceUtils {
    public static Typeface getTypeface(Chart chart, ReadableMap propMap) {
        String fontFamily = propMap.getString("fontFamily");
        boolean italic = false;
        boolean bold = false;
        int weight = 0;

        int style = Typeface.NORMAL;
        if (BridgeUtils.validate(propMap, ReadableType.String, "fontStyle")) {
            italic = "italic".equals(propMap.getString("fontStyle"));
        }
        if (BridgeUtils.validate(propMap, ReadableType.String, "fontWeight")) {
            bold = "bold".equals(propMap.getString("fontWeight"));
            Integer fontWeight = parseInteger(propMap.getString("fontWeight"));
            if(fontWeight != null){
                weight = fontWeight;
            }
        }

        if (italic && bold) {
            style = Typeface.BOLD_ITALIC;
        } else if (italic) {
            style = Typeface.ITALIC;
        } else if (bold) {
            style = Typeface.BOLD;
        }

        if(weight > 0){
           return ReactFontManager.getInstance().getTypeface(
                    fontFamily,
                    style,
                    weight,
                    chart.getContext().getAssets()
            );
        }

        return getTypeface(chart, fontFamily, style);
    }

    public static Typeface getTypeface(Chart chart, String fontFamily) {
        return getTypeface(chart, fontFamily, Typeface.NORMAL);
    }

    private static Typeface getTypeface(Chart chart, String fontFamily, int style) {
        return ReactFontManager.getInstance().getTypeface(fontFamily,
                style,
                chart.getContext().getAssets());
    }

    public static Integer parseInteger(String input ) {
        try {
            return Integer.parseInt(input);
        }
        catch(Exception e) {
            return null;
        }
    }
}
