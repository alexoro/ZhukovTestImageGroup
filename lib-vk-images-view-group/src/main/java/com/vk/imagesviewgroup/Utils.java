package com.vk.imagesviewgroup;

import android.view.View;

import java.util.List;

/**
 * Created by a.sorokin@mail.vk.com on 16.05.2016.
 */
class Utils {

    public static final int FLAG_WIDE = 1;
    public static final int FLAG_NARROW = 2;
    public static final int FLAG_SQUARE = 4;

    public static int calculateRatioFlags(List<Size> items,
                                          float ratioAboveIsWide,
                                          float ratioBelowIsNarrow) {
        int flags = 0;
        for (int i = 0; i < items.size(); i++) {
            final float ratio = calculateRatio(items.get(i));
            if (ratio >= ratioAboveIsWide) {
                flags |= FLAG_WIDE;
            } else if (ratio <= ratioBelowIsNarrow) {
                flags |= FLAG_NARROW;
            } else {
                flags |= FLAG_SQUARE;
            }
        }
        return flags;
    }

    public static float calculateAverageRatio(List<Size> itemsSizes) {
        float sum = 0f;
        for (int i = 0; i < itemsSizes.size(); i++) {
            sum += calculateRatio(itemsSizes.get(i));
        }
        return sum / itemsSizes.size();
    }

    public static boolean isContainsFlag(int flags, int targetFlagValue) {
        return (flags & targetFlagValue) != 0;
    }

    public static boolean isPresentedByOneFlag(int flags, int targetFlagValue) {
        return flags == targetFlagValue;
    }

    public static float calculateRatio(Size size) {
        return (float) size.width / size.height;
    }

    public static int calculateWidthFromRatio(int knownHeight, float ratio) {
        return Math.round(knownHeight * ratio);
    }

    public static int calculateHeightFromRatio(int knownWidth, float ratio) {
        return Math.round(knownWidth / ratio);
    }


    /**
     * Utility to return a view's standard measurement. Uses the
     * supplied size when constraints are given. Attempts to
     * hold to the desired size unless it conflicts with provided
     * constraints.
     *
     * @param measureSpec Constraints imposed by the parent
     * @param contentSize Desired size for the view
     * @return The size the view should be.
     */
    public static int getMeasurement(int measureSpec, int contentSize) {
        int specMode = View.MeasureSpec.getMode(measureSpec);
        int specSize = View.MeasureSpec.getSize(measureSpec);
        switch (specMode) {
            case View.MeasureSpec.UNSPECIFIED:
                //Big as we want to be
                return contentSize;
            case View.MeasureSpec.AT_MOST:
                //Big as we want to be, up to the spec
                return Math.min(contentSize, specSize);
            case View.MeasureSpec.EXACTLY:
                //Must be the spec size
                return specSize;
            default:
                return 0;
        }
    }

}