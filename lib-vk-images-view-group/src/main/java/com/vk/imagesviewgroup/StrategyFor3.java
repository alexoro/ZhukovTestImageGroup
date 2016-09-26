package com.vk.imagesviewgroup;

import android.graphics.Rect;
import android.view.View;

import java.util.List;

/**
 * Created by a.sorokin@mail.vk.com on 28.07.2015.
 */
class StrategyFor3 implements Strategy {

    public static final int TYPE_UNKNOWN = -1;

    /**
     * <pre>
     +-----------+
     |     1     |   // x0.618
     +-----+-----+
     |  2  |  3  |   // x0.382
     +-----+-----+
     </pre>
     */
    public static final int TYPE_ALL_WIDE_TWO_LINES = 1;

    /**
     * <pre>
     +-----+-----+
     |     |  2  |
     |  1  +-----+
     |     |  3  |
     +-----+-----+
     </pre>
     */
    public static final int TYPE_ETC = 2;


    @Override
    public void layout(LayoutArgs args, LayoutResult outLayoutResult) {
        final int widthMeasureSpec = args.containerWidthMeasureSpec;
        final int heightMeasureSpec = args.containerHeightMeasureSpec;
        final int widthMode = View.MeasureSpec.getMode(args.containerWidthMeasureSpec);
        final int widthSize = View.MeasureSpec.getSize(args.containerWidthMeasureSpec);
        final int heightMode = View.MeasureSpec.getMode(args.containerHeightMeasureSpec);
        final int heightSize = View.MeasureSpec.getSize(args.containerHeightMeasureSpec);
        final int maxWidth = args.containerMaxWidth;
        final int maxHeight = args.containerMaxHeight;
        final int itemsSpacing = args.itemsSpacing;
        final List<Size> itemsSizes = args.itemsSizes;

        if (itemsSizes.size() != 3) {
            throw new UnsupportedOperationException("Strategy supports only 3 items layout logic");
        }
        if (widthMode != View.MeasureSpec.AT_MOST || heightMode != View.MeasureSpec.AT_MOST) {
            throw new UnsupportedOperationException("Only 'AT_MOST' mode is supported for both width and height");
        }


        final int ratioFlags = Utils.calculateRatioFlags(itemsSizes, 1.2f, 0.8f);
        final float ratio0 = Utils.calculateRatio(itemsSizes.get(0));
        final float ratio1 = Utils.calculateRatio(itemsSizes.get(1));
        final float ratio2 = Utils.calculateRatio(itemsSizes.get(2));
        final int type;

        if (Utils.isPresentedByOneFlag(ratioFlags, Utils.FLAG_WIDE)) {
            type = TYPE_ALL_WIDE_TWO_LINES;
        } else {
            type = TYPE_ETC;
        }

        if (type == TYPE_ALL_WIDE_TWO_LINES) {
            final int w_cover = widthSize;
            final int h_cover = (int) Math.min(
                    w_cover / ratio0,
                    (widthSize - itemsSpacing) * 0.666f);

            final int w = ((widthSize - itemsSpacing) / 2);
            final int h = (int) Math.min(
                    widthSize - h_cover - itemsSpacing,
                    Math.min(w / ratio1, w / ratio2));

            final int rWidth = widthSize;
            final int rHeight = h_cover + itemsSpacing + h;

            int itemWidth;
            int itemHeight;
            Rect layout;

            itemWidth = w_cover;
            itemHeight = h_cover;
            layout = outLayoutResult.itemsLayout.get(0);
            layout.left = 0;
            layout.top = 0;
            layout.right = layout.left + itemWidth;
            layout.bottom = layout.top + itemHeight;

            itemWidth = w;
            itemHeight = h;
            layout = outLayoutResult.itemsLayout.get(1);
            layout.left = 0;
            layout.top = rHeight - itemHeight;
            layout.right = layout.left + itemWidth;
            layout.bottom = layout.top + itemHeight;

            itemWidth = w;
            itemHeight = h;
            layout = outLayoutResult.itemsLayout.get(2);
            layout.left = rWidth - itemWidth;
            layout.top = rHeight - itemHeight;
            layout.right = layout.left + itemWidth;
            layout.bottom = layout.top + itemHeight;

            outLayoutResult.containerSize.width = rWidth;
            outLayoutResult.containerSize.height = rHeight;
        }

        if (type == TYPE_ETC) {
            final int h_cover = heightSize;
            final int w_cover = (int) Math.min(
                    h_cover * ratio0,
                    (widthSize - itemsSpacing) * 0.75);
            final float h1 = (ratio1 * (heightSize - itemsSpacing) / (ratio1 + ratio2));
            final float h0 = (heightSize - h1 - itemsSpacing);
            final float w = Math.min(
                    widthSize - w_cover - itemsSpacing,
                    Math.min(h1 * ratio2, h0 * ratio1));

            final int rWidth = w_cover + itemsSpacing + (int) w;
            final int rHeight = h_cover;

            int itemWidth;
            int itemHeight;
            Rect layout;

            itemWidth = w_cover;
            itemHeight = h_cover;
            layout = outLayoutResult.itemsLayout.get(0);
            layout.left = 0;
            layout.top = 0;
            layout.right = layout.left + itemWidth;
            layout.bottom = layout.top + itemHeight;

            itemWidth = (int) w;
            itemHeight = (int) h0;
            layout = outLayoutResult.itemsLayout.get(1);
            layout.left = rWidth - itemWidth;
            layout.top = 0;
            layout.right = layout.left + itemWidth;
            layout.bottom = layout.top + itemHeight;

            itemWidth = (int) w;
            itemHeight = (int) h1;
            layout = outLayoutResult.itemsLayout.get(2);
            layout.left = rWidth - itemWidth;
            layout.top = rHeight - itemHeight;
            layout.right = layout.left + itemWidth;
            layout.bottom = layout.top + itemHeight;

            outLayoutResult.containerSize.width = rWidth;
            outLayoutResult.containerSize.height = rHeight;
        }
    }

}