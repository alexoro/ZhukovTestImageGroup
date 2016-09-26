package com.vk.imagesviewgroup;

import android.content.Context;
import android.graphics.Rect;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by a.sorokin@mail.vk.com on 16.05.2016.
 */
public class ImagesViewGroup extends ViewGroup {

    public static class ItemInfo {
        public int maxWidth;
        public int maxHeight;
        public Object tag;
        public ItemInfo(int maxWidth, int maxHeight, Object tag) {
            this.maxWidth = maxWidth;
            this.maxHeight = maxHeight;
            this.tag = tag;
        }
    }

    public static class EdgeView {
        public View topLeft;
        public View topRight;
        public View bottomRight;
        public View bottomLeft;
    }

    public interface ViewFactory {
        View onCreateView(ImagesViewGroup parent, Object tag);
        void onBindView(ImagesViewGroup parent, Object tag, View view);
        void onDestroyView(ImagesViewGroup parent, Object tag, View view);
        void onGroupLayoutDone(ImagesViewGroup parent);
    }

    private static final int MAX_ITEMS_COUNT = 10;

    private final Strategy.LayoutArgs mLayoutArgs;
    private final Strategy.LayoutResult mLayoutResult;

    private final SimpleObjectsPool<Size> mSizePool;
    private final SimpleObjectsPool<Rect> mLayoutPool;
    private final List<Object> mTags;
    private final List<View> mViews;

    private final StrategyFor1 mStrategyFor1;
    private final StrategyFor2 mStrategyFor2;
    private final StrategyFor3 mStrategyFor3;
    private final StrategyFor4 mStrategyFor4;
    private final StrategyFor5_10 mStrategyFor5_10;
    private boolean mInvokeOnGroupLayoutKnown;

    private int mMaxWidth;
    private int mMaxHeight;
    private ViewFactory mViewFactory;
    private int mSpace;


    public ImagesViewGroup(Context context) {
        super(context);

        mLayoutArgs = new Strategy.LayoutArgs();
        mLayoutArgs.containerWidthMeasureSpec = 0;
        mLayoutArgs.containerHeightMeasureSpec = 0;
        mLayoutArgs.containerMaxWidth = 0;
        mLayoutArgs.containerMaxHeight = 0;
        mLayoutArgs.itemsSpacing = 0;
        mLayoutArgs.itemsSizes = new ArrayList<>(MAX_ITEMS_COUNT);
        mSizePool = new SimpleObjectsPool<>(new SimpleObjectsPool.ObjectFactory<Size>() {
            @Override
            public Size newObject() {
                return new Size(-1, -1);
            }
            @Override
            public void reset(Size object) {
                object.width = -1;
                object.height = -1;
            }
        });

        mLayoutResult = new Strategy.LayoutResult();
        mLayoutResult.containerSize = new Size();
        mLayoutResult.itemsLayout = new ArrayList<>(MAX_ITEMS_COUNT);
        mLayoutPool = new SimpleObjectsPool<>(new SimpleObjectsPool.ObjectFactory<Rect>() {
            @Override
            public Rect newObject() {
                return new Rect();
            }
            @Override
            public void reset(Rect object) {
                object.setEmpty();
            }
        });

        mTags = new ArrayList<>(MAX_ITEMS_COUNT);
        mViews = new ArrayList<>(MAX_ITEMS_COUNT);

        mStrategyFor1 = new StrategyFor1();
        mStrategyFor2 = new StrategyFor2();
        mStrategyFor3 = new StrategyFor3();
        mStrategyFor4 = new StrategyFor4();
        mStrategyFor5_10 = new StrategyFor5_10();
        mInvokeOnGroupLayoutKnown = false;

        mMaxWidth = Integer.MAX_VALUE;
        mMaxHeight = Integer.MAX_VALUE;
        mViewFactory = null;
        mSpace = 0;
    }

    @Override
    public void addView(View view) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void removeView(View view) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void removeAllViews() {
        throw new UnsupportedOperationException();
    }


    // =======================================================

    public int getMaxWidth() {
        return mMaxWidth;
    }

    public void setMaxWidth(int maxWidth) {
        if (mMaxWidth != maxWidth) {
            mMaxWidth = maxWidth;
            requestLayout();
            invalidate();
        }
    }

    public int getMaxHeight() {
        return mMaxHeight;
    }

    public void setMaxHeight(int maxHeight) {
        if (mMaxHeight != maxHeight) {
            mMaxHeight = maxHeight;
            requestLayout();
            invalidate();
        }
    }

    public int getSpace() {
        return mSpace;
    }

    public void setSpace(int space) {
        if (mSpace != space) {
            mSpace = space;
            mInvokeOnGroupLayoutKnown = true;
            requestLayout();
            invalidate();
        }
    }

    public ViewFactory getViewFactory() {
        return mViewFactory;
    }

    public void setViewFactory(ViewFactory viewFactory) {
        mViewFactory = viewFactory;
        requestLayout();
        invalidate();
    }

    public void setItems(List<ItemInfo> items) {
        if (items == null) {
            throw new IllegalArgumentException("items is null");
        }
        if (items.size() > MAX_ITEMS_COUNT) {
            throw new IllegalArgumentException("items size must not be >= " + MAX_ITEMS_COUNT);
        }

        reset();

        for (int i = 0; i < items.size(); i++) {
            final ItemInfo itemInfo = items.get(i);

            final Size size = mSizePool.acquire();
            size.width = itemInfo.maxWidth;
            size.height = itemInfo.maxHeight;
            mLayoutArgs.itemsSizes.add(size);

            final Rect layout = mLayoutPool.acquire();
            mLayoutResult.itemsLayout.add(layout);

            final View child = mViewFactory.onCreateView(this, itemInfo.tag);
            mViewFactory.onBindView(this, itemInfo.tag, child);
            mViews.add(child);
            mTags.add(itemInfo.tag);
            super.addView(child);
        }

        mInvokeOnGroupLayoutKnown = true;
        requestLayout();
        invalidate();
    }

    private void reset() {
        for (int i = 0; i < mTags.size(); i++) {
            final Object tag = mTags.get(i);
            final View child = mViews.get(i);
            if (tag != null) {
                mViewFactory.onDestroyView(this, tag, child);
            }
        }
        mTags.clear();
        mViews.clear();
        super.removeAllViews();

        for (int i = 0; i < mLayoutArgs.itemsSizes.size(); i++) {
            final Size item = mLayoutArgs.itemsSizes.get(i);
            if (item != null) {
                mSizePool.release(item);
            }
        }
        mLayoutArgs.itemsSizes.clear();

        for (int i = 0; i < mLayoutResult.itemsLayout.size(); i++) {
            final Rect item = mLayoutResult.itemsLayout.get(i);
            if (item != null) {
                mLayoutPool.release(item);
            }
        }
        mLayoutResult.itemsLayout.clear();
    }


    // =======================================================

    public void findEdgeViews(EdgeView outEdgeView) {
        outEdgeView.topLeft = null;
        outEdgeView.topRight = null;
        outEdgeView.bottomRight = null;
        outEdgeView.bottomLeft = null;
        if (getChildCount() == 0) {
            return;
        }

        final int size = getChildCount();
        for (int i = 0; i < size; i++) {
            final View child = getChildAt(i);
            if (outEdgeView.topLeft == null
                    || child.getLeft() < outEdgeView.topLeft.getLeft()
                    || child.getTop() < outEdgeView.topLeft.getTop()) {
                outEdgeView.topLeft = child;
            }
            if (outEdgeView.topRight == null
                    || child.getRight() > outEdgeView.topRight.getRight()
                    || child.getTop() < outEdgeView.topRight.getTop()) {
                outEdgeView.topRight = child;
            }
            if (outEdgeView.bottomRight == null
                    || child.getRight() > outEdgeView.bottomRight.getRight()
                    || child.getBottom() > outEdgeView.bottomRight.getBottom()) {
                outEdgeView.bottomRight = child;
            }
            if (outEdgeView.bottomLeft == null
                    || child.getLeft() < outEdgeView.bottomLeft.getLeft()
                    || child.getBottom() > outEdgeView.bottomLeft.getBottom()) {
                outEdgeView.bottomLeft = child;
            }
        }
    }


    // =======================================================

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int itemsCount = getChildCount();
        if (itemsCount == 0) {
            final int finalWidth = getMeasurement(
                    widthMeasureSpec,
                    getSuggestedMinimumWidth(),
                    getMaxWidth(),
                    0);
            final int finalHeight = getMeasurement(
                    heightMeasureSpec,
                    getSuggestedMinimumHeight(),
                    getMaxHeight(),
                    0);
            setMeasuredDimension(finalWidth, finalHeight);
            return;
        }

        final Strategy strategy;
        if (itemsCount == 1) {
            strategy = mStrategyFor1;
        } else if (itemsCount == 2) {
            strategy = mStrategyFor2;
        } else if (itemsCount == 3) {
            strategy = mStrategyFor3;
        } else if (itemsCount == 4) {
            strategy = mStrategyFor4;
        } else if (itemsCount >= 5 && itemsCount <= 10) {
            strategy = mStrategyFor5_10;
        } else {
            throw new UnsupportedOperationException("No strategy to support " + itemsCount + " items");
        }

        final int usedWidth = getPaddingLeft() + getPaddingRight();
        final int usedHeight = getPaddingTop() + getPaddingBottom();

        final int availWidth = getSuggestedAvailableSizeFromSpec(
                widthMeasureSpec,
                getSuggestedMinimumWidth(),
                getMaxWidth(),
                usedWidth);
        final int availHeight = getSuggestedAvailableSizeFromSpec(
                heightMeasureSpec,
                getSuggestedMinimumHeight(),
                getMaxHeight(),
                usedHeight);

        mLayoutArgs.containerWidthMeasureSpec = MeasureSpec.makeMeasureSpec(
                availWidth,
                MeasureSpec.AT_MOST);
        mLayoutArgs.containerHeightMeasureSpec = MeasureSpec.makeMeasureSpec(
                availHeight,
                MeasureSpec.AT_MOST);
        mLayoutArgs.containerMaxWidth = getMaxWidth();
        mLayoutArgs.containerMaxHeight = getMaxHeight();
        mLayoutArgs.itemsSpacing = mSpace;

        strategy.layout(mLayoutArgs, mLayoutResult);

        final int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = getChildAt(i);
            final Rect layout = mLayoutResult.itemsLayout.get(i);
            final int childWidthMeasureSpec = View.MeasureSpec.makeMeasureSpec(
                    layout.width(),
                    View.MeasureSpec.EXACTLY);
            final int childHeightMeasureSpec = View.MeasureSpec.makeMeasureSpec(
                    layout.height(),
                    View.MeasureSpec.EXACTLY);
            child.measure(childWidthMeasureSpec, childHeightMeasureSpec);
        }

        final int rWidth = mLayoutResult.containerSize.width;
        final int rHeight = mLayoutResult.containerSize.height;

        final int finalWidth = getMeasurement(
                widthMeasureSpec,
                getSuggestedMinimumWidth(),
                getMaxWidth(),
                rWidth);
        final int finalHeight = getMeasurement(
                heightMeasureSpec,
                getSuggestedMinimumHeight(),
                getMaxHeight(),
                rHeight);
        setMeasuredDimension(finalWidth, finalHeight);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        final int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = getChildAt(i);
            final Rect layout = mLayoutResult.itemsLayout.get(i);
            child.layout(
                    left + layout.left + getPaddingLeft(),
                    top + layout.top + getPaddingBottom(),
                    left + layout.right + getPaddingLeft(),
                    top + layout.bottom + getPaddingBottom());
        }

        if (mInvokeOnGroupLayoutKnown) {
            mViewFactory.onGroupLayoutDone(this);
            mInvokeOnGroupLayoutKnown = false;
        }
    }


    // ================================================================

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
        final int specMode = View.MeasureSpec.getMode(measureSpec);
        final int specSize = View.MeasureSpec.getSize(measureSpec);
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

    public static int getMeasurement(int measureSpec,
                                     int minSize,
                                     int maxSize,
                                     int contentSize) {
        final int specMode = View.MeasureSpec.getMode(measureSpec);
        final int specSize = View.MeasureSpec.getSize(measureSpec);
        switch (specMode) {
            case View.MeasureSpec.EXACTLY:
                return specSize;
            case View.MeasureSpec.AT_MOST:
                if (specSize < minSize || specSize < contentSize) {
                    return specSize;
                } else {
                    return Math.max(
                            minSize,
                            Math.min(contentSize, maxSize));
                }
            case View.MeasureSpec.UNSPECIFIED:
                if (contentSize < minSize) {
                    return minSize;
                } else if (contentSize > maxSize) {
                    return maxSize;
                } else {
                    return contentSize;
                }
            default:
                throw new IllegalArgumentException("Unknown specMode: " + specMode);
        }
    }

    public static int getSuggestedAvailableSizeFromSize(int givenSize,
                                                        int minSize,
                                                        int maxSize,
                                                        int usedSize) {
        if (givenSize < minSize) {
            return Math.max(0, givenSize - usedSize);
        } else if (givenSize > maxSize) {
            return Math.max(0, maxSize - usedSize);
        } else {
            return Math.max(0, givenSize - usedSize);
        }
    }

    public static int getSuggestedAvailableSizeFromSpec(int spec,
                                                        int minSize,
                                                        int maxSize,
                                                        int usedSize) {
        final int specSize = View.MeasureSpec.getSize(spec);
        final int specMode = View.MeasureSpec.getMode(spec);

        if (specMode == View.MeasureSpec.EXACTLY
                || specMode == View.MeasureSpec.AT_MOST) {
            return getSuggestedAvailableSizeFromSize(specSize, minSize, maxSize, usedSize);
        } else {
            return Math.max(0, maxSize - usedSize);
        }
    }

}