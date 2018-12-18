package com.example.a47420.camerademo.util;

import android.hardware.Camera.Size;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class CamParaUtil {

    public static Size getSize(List<Size> list, int th,Size defaultSize) {
        if(null == list || list.isEmpty()) return defaultSize;
        Collections.sort(list, new Comparator<Size>(){
            public int compare(Size lhs, Size rhs) {//作升序排序
                if (lhs.width == rhs.width) {
                    return 0;
                } else if (lhs.width > rhs.width) {
                    return 1;
                } else {
                    return -1;
                }
            }
        });
        int i = 0;
        for (Size s : list) {
            if ((s.width > th) ) {//&& equalRate(s, rate)
                break;
            }
            i++;
        }
        if (i == list.size()) {
            return list.get(i-1);
        } else {
            return list.get(i);
        }
    }



    public static boolean isSupportedFocusMode(List<String> focusList, String focusMode) {
        for (int i = 0; i < focusList.size(); i++) {
            if (focusMode.equals(focusList.get(i))) {
                return true;
            }
        }
        return false;
    }

    public static boolean isSupportedFormats(List<Integer> supportedFormats, int jpeg) {
        for (int i = 0; i < supportedFormats.size(); i++) {
            if (jpeg == supportedFormats.get(i)) {
                return true;
            }
        }
        return false;
    }
}