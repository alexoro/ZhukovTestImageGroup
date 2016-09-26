package com.vk.imagesviewgroup;

import android.view.View;

import java.util.List;

/**
 * Created by a.sorokin@mail.vk.com on 28.07.2015.
 */
class StrategyFor1 implements Strategy {

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

        if (itemsSizes.size() != 1) {
            throw new UnsupportedOperationException("Strategy supports only 1 item layout logic");
        }
        if (widthMode == View.MeasureSpec.UNSPECIFIED) {
            throw new UnsupportedOperationException("'UNSPECIFIED' is not supported for width measure spec");
        }

        final Size itemSize = itemsSizes.get(0);
        final float ratio = (float) itemSize.width / itemSize.height;


        final int rWidth;
        final int rHeight;
        if (ratio > 1) {
            rWidth = Utils.getMeasurement(widthMeasureSpec, maxWidth);
            rHeight = Utils.getMeasurement(
                    heightMeasureSpec,
                    Utils.calculateHeightFromRatio(rWidth, ratio));
        } else {
            rHeight = Utils.getMeasurement(heightMeasureSpec, maxHeight);
            rWidth = Utils.getMeasurement(
                    widthMeasureSpec,
                    Utils.calculateWidthFromRatio(rHeight, ratio));
        }

        outLayoutResult.itemsLayout.get(0).set(
                0,
                0,
                rWidth,
                rHeight);
        outLayoutResult.containerSize.width = rWidth;
        outLayoutResult.containerSize.height = rHeight;
    }

}