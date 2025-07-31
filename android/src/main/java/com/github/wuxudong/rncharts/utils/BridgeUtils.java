package com.github.wuxudong.rncharts.utils;

import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.ReadableType;

public class BridgeUtils {

    public static boolean validate(ReadableMap map, ReadableType propType, String propName) {
        return map.hasKey(propName) && propType.equals(map.getType(propName));
    }

    public static int[] convertToIntArray(ReadableArray readableArray) {
        int[] array = new int[readableArray.size()];

        for (int i = 0; i < readableArray.size(); i++) {
            if (ReadableType.Number.equals(readableArray.getType(i))) {
                 array[i] = readableArray.getInt(i);
            }
            else {
                // If it's not a number default to 0 to prevent crashes
                String type = readableArray.getType(i).name();
                array[i] = 0;
            }
        }

        return array;
    }

    public static float[] convertToFloatArray(ReadableArray readableArray) {
        float[] array = new float[readableArray.size()];

        for (int i = 0; i < readableArray.size(); i++) {
            if (ReadableType.Number.equals(readableArray.getType(i))) {
                array[i] = (float) readableArray.getDouble(i);
            }
            else {
                // If it's not a number default to 0 to prevent crashes
                array[i] = 0f;
            }
        }

        return array;
    }

    public static String[] convertToStringArray(ReadableArray readableArray) {
        String[] array = new String[readableArray.size()];

        for (int i = 0; i < readableArray.size(); i++) {
            if (!ReadableType.String.equals(readableArray.getType(i))) {
                throw new IllegalArgumentException("Expecting array of strings");
            }
            array[i] = readableArray.getString(i);
        }

        return array;
    }

}
