package personal.aiwei.lastfirstlayout;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 线性布局，末尾的子控件会优先被测量和分配空间
 * <p>
 * Created by Ai Wei on 2018/11/28
 */
public class LastFirstLayout extends ViewGroup {
    private static final int DEFAULT_CHILD_GRAVITY = Gravity.TOP | Gravity.START;

    @IntDef({HORIZONTAL, VERTICAL})
    @Retention(RetentionPolicy.SOURCE)
    public @interface OrientationMode {
    }

    public static final int HORIZONTAL = 0;
    public static final int VERTICAL = 1;

    private int mOrientation = HORIZONTAL;

    public LastFirstLayout(Context context) {
        this(context, null);
    }

    public LastFirstLayout(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LastFirstLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.LastFirstLayout, defStyleAttr, 0);
        mOrientation = typedArray.getInt(R.styleable.LastFirstLayout_android_orientation, HORIZONTAL);
        typedArray.recycle();

        init(context);
    }

    private void init(Context context) {
    }

    public void setOrientation(@OrientationMode int orientation) {
        if (this.mOrientation != orientation) {
            this.mOrientation = orientation;
            requestLayout();
        }
    }

    @OrientationMode
    public int getOrientation() {
        return mOrientation;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (mOrientation == VERTICAL) {
            measureVertical(widthMeasureSpec, heightMeasureSpec);
        } else {
            measureHorizontal(widthMeasureSpec, heightMeasureSpec);
        }
    }

    private void measureVertical(int widthMeasureSpec, int heightMeasureSpec) {
        int desiredWidth = 0;
        int desiredHeight = 0;

        final int count = getChildCount();
        int widthUsed = 0;
        int heightUsed = 0;
        for (int i = count - 1; i >= 0; i--) {
            final View child = getChildAt(i);
            if (child.getVisibility() != GONE) {
                measureChildWithMargins(child, widthMeasureSpec, widthUsed, heightMeasureSpec, heightUsed);
                final MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();
                heightUsed += (lp.topMargin + child.getMeasuredHeight() + lp.bottomMargin);

                desiredWidth = Math.max(desiredWidth, lp.leftMargin + child.getMeasuredWidth() + lp.rightMargin);
                desiredHeight += (lp.topMargin + child.getMeasuredHeight() + lp.bottomMargin);
            }
        }

        desiredWidth += (getPaddingLeft() + getPaddingRight());
        desiredHeight += (getPaddingTop() + getPaddingBottom());

        setMeasuredDimension(resolveSize(desiredWidth, widthMeasureSpec), resolveSize(desiredHeight, heightMeasureSpec));
    }

    private void measureHorizontal(int widthMeasureSpec, int heightMeasureSpec) {
        int desiredWidth = 0;
        int desiredHeight = 0;

        final int count = getChildCount();
        int widthUsed = 0;
        int heightUsed = 0;
        for (int i = count - 1; i >= 0; i--) {
            final View child = getChildAt(i);
            if (child.getVisibility() != GONE) {
                measureChildWithMargins(child, widthMeasureSpec, widthUsed, heightMeasureSpec, heightUsed);
                final MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();
                widthUsed += (lp.leftMargin + child.getMeasuredWidth() + lp.rightMargin);

                desiredWidth += (lp.leftMargin + child.getMeasuredWidth() + lp.rightMargin);
                desiredHeight = Math.max(desiredHeight, lp.topMargin + child.getMeasuredHeight() + lp.bottomMargin);
            }
        }

        desiredWidth += (getPaddingLeft() + getPaddingRight());
        desiredHeight += (getPaddingTop() + getPaddingBottom());

        setMeasuredDimension(resolveSize(desiredWidth, widthMeasureSpec), resolveSize(desiredHeight, heightMeasureSpec));
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (mOrientation == VERTICAL) {
            layoutVertical(changed, l, t, r, b);
        } else {
            layoutHorizontal(changed, l, t, r, b);
        }
    }

    private void layoutVertical(boolean changed, int l, int t, int r, int b) {
        final int count = getChildCount();

        final int parentLeft = getPaddingLeft();
        final int parentRight = r - l - getPaddingRight();

        final int parentTop = getPaddingTop();
        final int parentBottom = b - t - getPaddingBottom();

        int layoutTop = parentTop;

        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);

            if (child.getVisibility() != GONE) {
                final LayoutParams lp = (LayoutParams) child.getLayoutParams();

                final int width = child.getMeasuredWidth();
                final int height = child.getMeasuredHeight();

                int childLeft;
                int childTop;

                int gravity = lp.gravity;
                if (gravity == -1) {
                    gravity = DEFAULT_CHILD_GRAVITY;
                }

                final int horizontalGravity = gravity & Gravity.HORIZONTAL_GRAVITY_MASK;
                childTop = layoutTop + lp.topMargin;
                switch (horizontalGravity) {
                    case Gravity.CENTER_HORIZONTAL:
                        childLeft = parentLeft + (parentRight - parentLeft - width) / 2;
                        break;
                    case Gravity.RIGHT:
                        childLeft = parentRight - lp.rightMargin - width;
                        break;
                    default:
                        childLeft = parentLeft + lp.leftMargin;
                }

                child.layout(childLeft, childTop, childLeft + width, childTop + height);

                layoutTop = childTop + height + lp.bottomMargin;
            }
        }
    }

    private void layoutHorizontal(boolean changed, int l, int t, int r, int b) {
        final int count = getChildCount();

        final int parentLeft = getPaddingLeft();
        final int parentRight = r - l - getPaddingRight();

        final int parentTop = getPaddingTop();
        final int parentBottom = b - t - getPaddingBottom();

        int layoutLeft = parentLeft;

        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);

            if (child.getVisibility() != GONE) {
                final LayoutParams lp = (LayoutParams) child.getLayoutParams();

                final int width = child.getMeasuredWidth();
                final int height = child.getMeasuredHeight();

                int childLeft;
                int childTop;

                int gravity = lp.gravity;
                if (gravity == -1) {
                    gravity = DEFAULT_CHILD_GRAVITY;
                }

                final int verticalGravity = gravity & Gravity.VERTICAL_GRAVITY_MASK;
                childLeft = layoutLeft + lp.leftMargin;
                switch (verticalGravity) {
                    case Gravity.CENTER_VERTICAL:
                        childTop = parentTop + (parentBottom - parentTop - height) / 2;
                        break;
                    case Gravity.BOTTOM:
                        childTop = parentBottom - lp.bottomMargin - height;
                        break;
                    default:
                        childTop = parentTop + lp.topMargin;
                }

                child.layout(childLeft, childTop, childLeft + width, childTop + height);

                layoutLeft = childLeft + width + lp.rightMargin;
            }
        }
    }

    @Override
    protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
        return p instanceof LastFirstLayout.LayoutParams;
    }

    @Override
    protected ViewGroup.LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
        return new LastFirstLayout.LayoutParams(p);
    }

    @Override
    public ViewGroup.LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LastFirstLayout.LayoutParams(getContext(), attrs);
    }

    public static class LayoutParams extends MarginLayoutParams {
        public static final int UNSPECIFIED_GRAVITY = -1;

        public int gravity = UNSPECIFIED_GRAVITY;

        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);

            final TypedArray a = c.obtainStyledAttributes(attrs, R.styleable.LastFirstLayout_Layout);
            gravity = a.getInt(R.styleable.LastFirstLayout_Layout_android_layout_gravity, UNSPECIFIED_GRAVITY);
            a.recycle();
        }

        public LayoutParams(int width, int height) {
            super(width, height);
        }

        public LayoutParams(MarginLayoutParams source) {
            super(source);
        }

        public LayoutParams(ViewGroup.LayoutParams source) {
            super(source);
        }
    }
}