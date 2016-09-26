package com.vk.imagesviewgroup;

import android.graphics.Rect;
import android.view.View;

import java.util.List;

/**
 * Created by a.sorokin@mail.vk.com on 28.07.2015.
 */
class StrategyFor4 implements Strategy {

    public static final int TYPE_UNKNOWN = -1;

    public static final int TYPE_ALL_WIDE_TWO_LINES = 1;
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

        if (itemsSizes.size() != 4) {
            throw new UnsupportedOperationException("Strategy supports only 4 items layout logic");
        }
        if (widthMode != View.MeasureSpec.AT_MOST || heightMode != View.MeasureSpec.AT_MOST) {
            throw new UnsupportedOperationException("Only 'AT_MOST' mode is supported for both width and height");
        }

        final int ratioFlags = Utils.calculateRatioFlags(itemsSizes, 1.2f, 0.8f);
        final float ratio0 = Utils.calculateRatio(itemsSizes.get(0));
        final float ratio1 = Utils.calculateRatio(itemsSizes.get(1));
        final float ratio2 = Utils.calculateRatio(itemsSizes.get(2));
        final float ratio3 = Utils.calculateRatio(itemsSizes.get(3));

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
                    (heightSize - itemsSpacing) * 0.66);

            int h = (int)((widthSize - 2 * itemsSpacing) / (ratio1 + ratio2 + ratio3));
            final int w1 = (int)(h * ratio1);
            final int w2 = (int)(h * ratio2);
            final int w3 = (int)(h * ratio3);
            h = (int) Math.min(heightSize - h_cover - itemsSpacing, h);

            final int rWidth = w_cover;
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

            itemWidth = w1;
            itemHeight = h;
            layout = outLayoutResult.itemsLayout.get(1);
            layout.left = 0;
            layout.top = rHeight - itemHeight;
            layout.right = layout.left + itemWidth;
            layout.bottom = layout.top + itemHeight;

            itemWidth = w3;
            itemHeight = h;
            layout = outLayoutResult.itemsLayout.get(3);
            layout.left = rWidth - itemWidth;
            layout.top = rHeight - itemHeight;
            layout.right = layout.left + itemWidth;
            layout.bottom = layout.top + itemHeight;

            itemWidth = w2;
            itemHeight = h;
            layout = outLayoutResult.itemsLayout.get(2);
            layout.left = outLayoutResult.itemsLayout.get(1).right + itemsSpacing;
            layout.top = rHeight - itemHeight;
            layout.right = outLayoutResult.itemsLayout.get(3).left - itemsSpacing;
            layout.bottom = layout.top + itemHeight;

            outLayoutResult.containerSize.width = rWidth;
            outLayoutResult.containerSize.height = rHeight;
        }


        if (type == TYPE_ETC) {
            final int h_cover = heightSize;
            final int w_cover = (int) Math.min(
                    h_cover * ratio0,
                    (widthSize - itemsSpacing) * 0.66);
            int w = (int) ((heightSize - 2 * itemsSpacing) / (1 / ratio1 + 1 / ratio2 + 1 / ratio3));
            final int h1 = (int)(w / ratio1);
            final int h2 = (int)(w / ratio2);
            final int h3 = (int)(w / ratio3);
            w = Math.min(widthSize - w_cover - itemsSpacing, w);

            final int rWidth = w_cover + itemsSpacing + w;
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

            itemWidth = w;
            itemHeight = h1;
            layout = outLayoutResult.itemsLayout.get(1);
            layout.left = rWidth - itemWidth;
            layout.top = 0;
            layout.right = layout.left + itemWidth;
            layout.bottom = layout.top + itemHeight;

            itemWidth = w;
            itemHeight = h3;
            layout = outLayoutResult.itemsLayout.get(3);
            layout.left = rWidth - itemWidth;
            layout.top = rHeight - itemHeight;
            layout.right = layout.left + itemWidth;
            layout.bottom = layout.top + itemHeight;

            itemWidth = w;
            itemHeight = h2;
            layout = outLayoutResult.itemsLayout.get(2);
            layout.left = rWidth - itemWidth;
            layout.top = outLayoutResult.itemsLayout.get(1).bottom + itemsSpacing;
            layout.right = layout.left + itemWidth;
            layout.bottom = outLayoutResult.itemsLayout.get(3).top - itemsSpacing;

            outLayoutResult.containerSize.width = rWidth;
            outLayoutResult.containerSize.height = rHeight;
        }

    }

}