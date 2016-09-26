package com.vk.imagesviewgroup;

import android.graphics.Rect;

import java.util.List;

/**
 * Created by a.sorokin@mail.vk.com on 28.07.2015.
 */
interface Strategy {

    class LayoutArgs {
        public int containerWidthMeasureSpec;
        public int containerHeightMeasureSpec;
        public int containerMaxWidth;
        public int containerMaxHeight;
        public int itemsSpacing;
        public List<Size> itemsSizes;
    }

    class LayoutResult {
        public Size containerSize;
        public List<Rect> itemsLayout;
    }

    void layout(LayoutArgs args,
                LayoutResult outLayoutResult);

}