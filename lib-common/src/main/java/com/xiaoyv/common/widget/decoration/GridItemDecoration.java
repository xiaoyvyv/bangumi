package com.xiaoyv.common.widget.decoration;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;

import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

/**
 * 描述:  自定义网格ItemDecoration
 * 作者:  LemZ
 */
public class GridItemDecoration extends RecyclerView.ItemDecoration {
    private final Drawable mDivider;
    private final boolean mShowLastLine;
    private final int mHorizonSpan;
    private final int mVerticalSpan;

    private GridItemDecoration(int horizonSpan, int verticalSpan, int color, boolean showLastLine) {
        this.mHorizonSpan = horizonSpan;
        this.mShowLastLine = showLastLine;
        this.mVerticalSpan = verticalSpan;
        mDivider = new ColorDrawable(color);
    }

    @Override
    public void onDrawOver(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        drawHorizontal(c, parent);
        drawVertical(c, parent);
    }

    private void drawHorizontal(Canvas c, RecyclerView parent) {
        int childCount = parent.getChildCount();

        for (int i = 0; i < childCount; i++) {
            View child = parent.getChildAt(i);

            // 最后一行底部横线不绘制
            if (isLastRaw(parent, i, getSpanCount(parent), childCount) && !mShowLastLine) {
                continue;
            }
            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
            final int left = child.getLeft() - params.leftMargin;
            final int right = child.getRight() + params.rightMargin;
            final int top = child.getBottom() + params.bottomMargin;
            final int bottom = top + mHorizonSpan;

            mDivider.setBounds(left, top, right, bottom);
            mDivider.draw(c);
        }
    }

    private void drawVertical(Canvas c, RecyclerView parent) {
        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = parent.getChildAt(i);
            if ((parent.getChildViewHolder(child).getAdapterPosition() + 1) % getSpanCount(parent) == 0) {
                continue;
            }
            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
            final int top = child.getTop() - params.topMargin;
            final int bottom = child.getBottom() + params.bottomMargin + mHorizonSpan;
            final int left = child.getRight() + params.rightMargin;
            int right = left + mVerticalSpan;
            // 满足条件( 最后一行 && 不绘制 ) 将vertical多出的一部分去掉;
            if (i == childCount - 1) {
                right -= mVerticalSpan;
            }
            mDivider.setBounds(left, top, right, bottom);
            mDivider.draw(c);
        }
    }

    /**
     * 计算偏移量
     */
    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        int spanCount = getSpanCount(parent);
        int childCount = 0;
        if (parent.getAdapter() != null) {
            childCount = parent.getAdapter().getItemCount();
        }
        int itemPosition = ((RecyclerView.LayoutParams) view.getLayoutParams()).getViewLayoutPosition();

        if (itemPosition < 0) {
            return;
        }

        int column = itemPosition % spanCount;
        int bottom;

        int left = column * mVerticalSpan / spanCount;
        int right = mVerticalSpan - (column + 1) * mVerticalSpan / spanCount;

        if (isLastRaw(parent, itemPosition, spanCount, childCount)) {
            if (mShowLastLine) {
                bottom = mHorizonSpan;
            } else {
                bottom = 0;
            }
        } else {
            bottom = mHorizonSpan;
        }
        outRect.set(left, 0, right, bottom);
    }

    /**
     * 获取列数
     */
    private int getSpanCount(RecyclerView parent) {
        // 列数
        int mSpanCount = -1;
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {
            mSpanCount = ((GridLayoutManager) layoutManager).getSpanCount();
        } else if (layoutManager instanceof StaggeredGridLayoutManager) {
            mSpanCount = ((StaggeredGridLayoutManager) layoutManager).getSpanCount();
        }
        return mSpanCount;
    }

    /**
     * 是否最后一行
     *
     * @param parent     RecyclerView
     * @param pos        当前item的位置
     * @param spanCount  每行显示的item个数
     * @param childCount child个数
     */
    private boolean isLastRaw(RecyclerView parent, int pos, int spanCount, int childCount) {
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();

        if (layoutManager instanceof GridLayoutManager) {
            return getResult(pos, spanCount, childCount);
        } else if (layoutManager instanceof StaggeredGridLayoutManager) {
            int orientation = ((StaggeredGridLayoutManager) layoutManager).getOrientation();
            if (orientation == StaggeredGridLayoutManager.VERTICAL) {
                // StaggeredGridLayoutManager 且纵向滚动
                return getResult(pos, spanCount, childCount);
            } else {
                // StaggeredGridLayoutManager 且横向滚动
                return (pos + 1) % spanCount == 0;
            }
        }
        return false;
    }

    private boolean getResult(int pos, int spanCount, int childCount) {
        int remainCount = childCount % spanCount;// 获取余数
        // 如果正好最后一行完整;
        if (remainCount == 0) {
            return pos >= childCount - spanCount; // 最后一行全部不绘制;
        } else {
            return pos >= childCount - childCount % spanCount;
        }
    }

    /**
     * 使用Builder构造
     */
    public static class Builder {
        private final Context mContext;
        private final Resources mResources;
        private boolean mShowLastLine;
        private int mHorizonSpan;
        private int mVerticalSpan;
        private int mColor;

        public Builder(Context context) {
            mContext = context;
            mResources = context.getResources();
            mShowLastLine = true;
            mHorizonSpan = 0;
            mVerticalSpan = 0;
            mColor = Color.WHITE;
        }

        /**
         * 通过资源文件设置分隔线颜色
         */
        public Builder setColorResource(@ColorRes int resource) {
            setColor(ContextCompat.getColor(mContext, resource));
            return this;
        }

        /**
         * 设置颜色
         */
        public Builder setColor(@ColorInt int color) {
            mColor = color;
            return this;
        }


        /**
         * 通过 px 设置垂直间距
         */
        public Builder setVerticalSpan(int mVertical) {
            this.mVerticalSpan = mVertical;
            return this;
        }

        /**
         * 通过 px 设置水平间距
         */
        public Builder setHorizontalSpan(int horizontal) {
            this.mHorizonSpan = horizontal;
            return this;
        }

        /**
         * 是否最后一条显示分割线
         */
        public Builder setShowLastLine(boolean show) {
            mShowLastLine = show;
            return this;
        }

        public GridItemDecoration build() {
            return new GridItemDecoration(mHorizonSpan, mVerticalSpan, mColor, mShowLastLine);
        }
    }
}
