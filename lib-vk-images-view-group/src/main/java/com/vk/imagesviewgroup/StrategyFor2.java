package com.vk.imagesviewgroup;

import android.graphics.Rect;
import android.view.View;

import java.util.List;

/**
 * Created by a.sorokin@mail.vk.com on 28.07.2015.
 */
class StrategyFor2 implements Strategy {

    public static final int TYPE_UNKNOWN = -1;

    /**<pre>
     +-----------+
     |     1     |
     +-----+-----+
     |     2     |
     +-----------+
     </pre>
     */
    public static final int TYPE_TWO_WIDE_TWO_ROWS = 1;

    /**<pre>
     +---------+---------+
     |         |         |
     |    1    |    2    |
     |         |         |
     +---------+---------+
     </pre>
     */
    public static final int TYPE_WIDE_OR_SQUARE_NEAR_ONE_LINE = 2;

    /** Size may vary in 1 or 2
     * <pre>
     +-------------+-------+
     |             |       |
     |      1      |   2   |
     |             |       |
     +-------------+-------+
     </pre>
     */
    public static final int TYPE_VARY_WIDTH_ONE_LINE = 3;


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

        if (itemsSizes.size() != 2) {
            throw new UnsupportedOperationException("Strategy supports only 2 items layout logic");
        }
        if (widthMode != View.MeasureSpec.AT_MOST || heightMode != View.MeasureSpec.AT_MOST) {
            throw new UnsupportedOperationException("Only 'AT_MOST' mode is supported for both width and height");
        }

        final int ratioFlags = Utils.calculateRatioFlags(itemsSizes, 1.2f, 0.8f);
        final float ratio0 = Utils.calculateRatio(itemsSizes.get(0));
        final float ratio1 = Utils.calculateRatio(itemsSizes.get(1));
        final float ratioDiff = Math.abs(ratio0 - ratio1);
        final int type;

        if (Utils.isPresentedByOneFlag(ratioFlags, Utils.FLAG_WIDE) && ratioDiff < 0.2) {
            type = TYPE_TWO_WIDE_TWO_ROWS;
        } else if (Utils.isPresentedByOneFlag(ratioFlags, Utils.FLAG_WIDE)
                || Utils.isPresentedByOneFlag(ratioFlags, Utils.FLAG_SQUARE)) {
            type = TYPE_WIDE_OR_SQUARE_NEAR_ONE_LINE;
        } else {
            type = TYPE_VARY_WIDTH_ONE_LINE;
        }


        if (type == TYPE_TWO_WIDE_TWO_ROWS) {
            final float w = widthSize;
            final float h = Math.min(
                    w / ratio0,
                    Math.min(w / ratio1, (widthSize - itemsSpacing) / 2.0f));
            final int rWidth = (int) w;
            final int rHeight = (int) (h * 2 + itemsSpacing);

            Rect layout;

            layout = outLayoutResult.itemsLayout.get(0);
            layout.left = 0;
            layout.top = 0;
            layout.right = (int) w;
            layout.bottom = (int) h;

            layout = outLayoutResult.itemsLayout.get(1);
            layout.left = 0;
            layout.top = rHeight - (int) h;
            layout.right = (int) w;
            layout.bottom = rHeight;

            outLayoutResult.containerSize.width = rWidth;
            outLayoutResult.containerSize.height = rHeight;

            return;
        }

        if (type == TYPE_WIDE_OR_SQUARE_NEAR_ONE_LINE) {
            final float w = ((widthSize - itemsSpacing) / 2);
            final float h = Math.min(w / ratio0, Math.min(w / ratio1, heightSize));
            final int rWidth = (int) (w * 2 + itemsSpacing);
            final int rHeight = (int) h;

            Rect layout;

            layout = outLayoutResult.itemsLayout.get(0);
            layout.left = 0;
            layout.top = 0;
            layout.right = (int) w;
            layout.bottom = (int) h;

            layout = outLayoutResult.itemsLayout.get(1);
            layout.left = rWidth - (int) w;
            layout.top = 0;
            layout.right = rWidth;
            layout.bottom = (int) h;

            outLayoutResult.containerSize.width = rWidth;
            outLayoutResult.containerSize.height = rHeight;

            return;
        }

        if (type == TYPE_VARY_WIDTH_ONE_LINE) {
            final float w0 = ((widthSize - itemsSpacing) / ratio1 / (1 / ratio0 + 1 / ratio1));
            final float w1 = (widthSize - w0 - itemsSpacing);
            final float h = Math.min(heightSize, Math.min(w0 / ratio0, w1 / ratio1));
            final int rWidth = (int)(w0 + itemsSpacing + w1);
            final int rHeight = (int) h;

            Rect layout;

            layout = outLayoutResult.itemsLayout.get(0);
            layout.left = 0;
            layout.top = 0;
            layout.right = (int) w0;
            layout.bottom = (int) h;

            layout = outLayoutResult.itemsLayout.get(1);
            layout.left = rWidth - (int) w1;
            layout.top = 0;
            layout.right = rWidth;
            layout.bottom = (int) h;

            outLayoutResult.containerSize.width = rWidth;
            outLayoutResult.containerSize.height = rHeight;

            return;
        }

    }

}