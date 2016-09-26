package com.vk.imagesviewgroup;

import android.graphics.Rect;
import android.view.View;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;

/**
 * Created by a.sorokin@mail.vk.com on 28.07.2015.
 */
class StrategyFor5_10 implements Strategy {

    private static class LineInfo {
        public int lines;
        public int[] lineItemsCount;
        public float[] lineHeight;
        public LineInfo() {
            lines = 0;
            lineItemsCount = new int[3];
            lineHeight = new float[3];
        }
    }


    private int mCount;
    private final float[] mRatioOrig;
    private final float[] mRatioCropped;
    private final int[] mItemMeasuredWidth;
    private final int[] mItemMeasuredHeight;

    private final List<LineInfo> mLinesInfoForCollection;
    private LineInfo mBestLineInfo;

    private final Stack<LineInfo> mLinesInfoPool;


    public StrategyFor5_10() {
        mCount = 0;
        mRatioOrig = new float[10];
        mRatioCropped = new float[10];
        mItemMeasuredWidth = new int[10];
        mItemMeasuredHeight = new int[10];
        mLinesInfoForCollection = new ArrayList<>();
        mBestLineInfo = null;
        mLinesInfoPool = new Stack<>();
    }

    private void invalidateState() {
        mCount = 0;
        Arrays.fill(mRatioOrig, 0f);
        Arrays.fill(mRatioCropped, 0f);
        Arrays.fill(mItemMeasuredWidth, 0);
        Arrays.fill(mItemMeasuredHeight, 0);
        if (mBestLineInfo != null) {
            returnToPool(mBestLineInfo);
            mBestLineInfo = null;
        }
    }

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

        if (itemsSizes.size() < 5 || itemsSizes.size() > 10) {
            throw new UnsupportedOperationException("Strategy supports only [5,10] items layout logic");
        }
        if (widthMode != View.MeasureSpec.AT_MOST || heightMode != View.MeasureSpec.AT_MOST) {
            throw new UnsupportedOperationException("Only 'AT_MOST' mode is supported for both width and height");
        }

        invalidateState();


        // ======================================================================

        // Size of images in collection.
        // This var is used in calculations with local arrays
        mCount = itemsSizes.size();

        // Calculate ratios
        float avg_ratio = Utils.calculateAverageRatio(itemsSizes);
        for (int i = 0; i < itemsSizes.size(); i++) {
            mRatioOrig[i] = Utils.calculateRatio(itemsSizes.get(i));
        }

        // Modify ratios for best suite
        if (avg_ratio > 1.1f) {
            for (int i = 0; i < itemsSizes.size(); i++) {
                mRatioCropped[i] = Math.max(1.0f, mRatioOrig[i]);
            }
        } else {
            for (int i = 0; i < itemsSizes.size(); i++) {
                mRatioCropped[i] = Math.min(1.0f, mRatioOrig[i]);
            }
        }

        mLinesInfoForCollection.clear();

        // One line
        LineInfo oneLine = getOrCreateLineInfoFromPoolAndReset();
        oneLine.lines = 1;
        oneLine.lineItemsCount[0] = mCount;
        oneLine.lineHeight[0] = calculateMultiThumbsHeight(mRatioCropped, 0, mCount, widthSize, itemsSpacing);
        mLinesInfoForCollection.add(oneLine);

        // Two lines
        for (int first_line = 1; first_line <= mCount - 1; first_line++) {
            LineInfo line = getOrCreateLineInfoFromPoolAndReset();
            line.lines = 2;
            line.lineItemsCount[0] = first_line;
            line.lineItemsCount[1] = mCount - first_line;
            line.lineHeight[0] = calculateMultiThumbsHeight(mRatioCropped, 0, first_line, widthSize, itemsSpacing);
            line.lineHeight[1] = calculateMultiThumbsHeight(mRatioCropped, first_line, mCount, widthSize, itemsSpacing);
            mLinesInfoForCollection.add(line);
        }

        // Three lines
        for (int first_line = 1; first_line <= mCount - 2; first_line++) {
            for (int second_line = 1; second_line <= mCount - first_line - 1; second_line++) {
                LineInfo line = getOrCreateLineInfoFromPoolAndReset();
                line.lines = 3;
                line.lineItemsCount[0] = first_line;
                line.lineItemsCount[1] = second_line;
                line.lineItemsCount[2] = mCount - first_line - second_line;
                line.lineHeight[0] = calculateMultiThumbsHeight(mRatioCropped, 0, first_line, widthSize, itemsSpacing);
                line.lineHeight[1] = calculateMultiThumbsHeight(mRatioCropped, first_line, first_line + second_line, widthSize, itemsSpacing);
                line.lineHeight[2] = calculateMultiThumbsHeight(mRatioCropped, first_line + second_line, mCount, widthSize, itemsSpacing);
                mLinesInfoForCollection.add(line);
            }
        }

        // Looking for minimum difference between thumbs block height and max_h (may probably be little over)
        if (mBestLineInfo != null) {
            returnToPool(mBestLineInfo);
            mBestLineInfo = null;
        }
        float bestDiffHeightFromMax = 0f;
        for (int i = 0; i < mLinesInfoForCollection.size(); i++) {
            LineInfo line = mLinesInfoForCollection.get(i);
            float height = 0f;
            height += itemsSpacing * (line.lines - 1);
            for (int lineHeightOffset = 0; lineHeightOffset < line.lines; lineHeightOffset++) {
                height += line.lineHeight[lineHeightOffset];
            }
            float diffHeightFromMax = Math.abs(height - heightSize);

            boolean nextLineHasFewerItems = false;
            if (line.lines == 2) {
                nextLineHasFewerItems = line.lineItemsCount[0] > line.lineItemsCount[1];
            }
            if (line.lines == 3) {
                nextLineHasFewerItems = line.lineItemsCount[1] > line.lineItemsCount[2];
            }
            if (nextLineHasFewerItems) {
                diffHeightFromMax *= 1.1f;
            }

            if (mBestLineInfo == null || diffHeightFromMax < bestDiffHeightFromMax) {
                mBestLineInfo = line;
                bestDiffHeightFromMax = diffHeightFromMax;
            }
        }

        for (int i = 0; i < mLinesInfoForCollection.size(); i++) {
            LineInfo lineInfo = mLinesInfoForCollection.get(i);
            if (lineInfo != mBestLineInfo) {
                returnToPool(lineInfo);
            }
        }
        mLinesInfoForCollection.clear();

        if (mBestLineInfo == null) {
            outLayoutResult.containerSize.width = 0;
            outLayoutResult.containerSize.height = 0;
            return;
        }

        int itemPosition = 0;
        for (int line = 0; line < mBestLineInfo.lines; line++) {
            float lineHeight = mBestLineInfo.lineHeight[line];
            for (int lineItem = 0; lineItem < mBestLineInfo.lineItemsCount[line]; lineItem++) {
                int targetWidth = (int)(mRatioCropped[itemPosition] * lineHeight);
                if (targetWidth > widthSize) {
                    targetWidth = widthSize;
                }
                int targetHeight = (int) lineHeight;
                mItemMeasuredWidth[itemPosition] = targetWidth;
                mItemMeasuredHeight[itemPosition] = targetHeight;
                itemPosition++;
            }
        }

        int rWidth = 0;
        for (int i = 0; i < mBestLineInfo.lineItemsCount[0]; i++) {
            rWidth += mItemMeasuredWidth[i];
        }
        rWidth += itemsSpacing * (mBestLineInfo.lineItemsCount[0] - 1);

        int rHeight = 0;
        rHeight += itemsSpacing * (mBestLineInfo.lines - 1);
        for (int lineHeightOffset = 0; lineHeightOffset < mBestLineInfo.lines; lineHeightOffset++) {
            rHeight += mBestLineInfo.lineHeight[lineHeightOffset];
        }

        outLayoutResult.containerSize.width = rWidth;
        outLayoutResult.containerSize.height = rHeight;


        // ======================================================================

        int offsetTop = 0;
        int itemLayoutPosition = 0;
        Rect layout;
        Rect layoutPrev;
        for (int line = 0; line < mBestLineInfo.lines; line++) {
            for (int lineItem = 0; lineItem < mBestLineInfo.lineItemsCount[line]; lineItem++) {
                layout = outLayoutResult.itemsLayout.get(itemLayoutPosition);
                if (lineItem == 0) {
                    layout.left = 0;
                    layout.top = offsetTop;
                    layout.right = Math.min(
                            outLayoutResult.containerSize.width,
                            layout.left + mItemMeasuredWidth[itemLayoutPosition]);
                    layout.bottom = Math.min(
                            outLayoutResult.containerSize.height,
                            layout.top + mItemMeasuredHeight[itemLayoutPosition]);
                } else {
                    layoutPrev = outLayoutResult.itemsLayout.get(itemLayoutPosition - 1);
                    layout.left = layoutPrev.right + itemsSpacing;
                    layout.top = offsetTop;
                    layout.right = Math.min(
                            outLayoutResult.containerSize.width,
                            layout.left + mItemMeasuredWidth[itemLayoutPosition]);
                    layout.bottom = Math.min(
                            outLayoutResult.containerSize.height,
                            layout.top + mItemMeasuredHeight[itemLayoutPosition]);
                }
                itemLayoutPosition++;
            }
            layoutPrev = outLayoutResult.itemsLayout.get(itemLayoutPosition - 1);
            offsetTop = layoutPrev.bottom + itemsSpacing;
        }
    }


    private float calculateMultiThumbsHeight(float[] ratios,
                                             int startIncluding,
                                             int endExcluding,
                                             int maxWidth,
                                             int dividerSize) {
        float sum = 0f;
        for (int i = startIncluding; i < endExcluding; i++) {
            sum += ratios[i];
        }
        return (maxWidth - (endExcluding - startIncluding - 1) * dividerSize) / sum;
    }

    private LineInfo getOrCreateLineInfoFromPoolAndReset() {
        if (!mLinesInfoPool.isEmpty()) {
            LineInfo sample = mLinesInfoPool.pop();
            sample.lines = 0;
            Arrays.fill(sample.lineItemsCount, 0);
            Arrays.fill(sample.lineHeight, 0f);
            return sample;
        } else {
            return new LineInfo();
        }
    }

    private void returnToPool(LineInfo lineInfo) {
        mLinesInfoPool.push(lineInfo);
    }

}