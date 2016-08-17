package com.ueueo.widget;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * 1.可以设置4个边Drawable的大小
 * 2.可以添加按下阴影效果
 * 3.可以直接设置不同位置文字的大小／颜色／字体
 * <p/>
 * Created by Lee on 16/8/11.
 */
public class UETextView extends TextView {

    private int[][] drawableSize = new int[4][2];

    public UETextView(Context context) {
        this(context, null);
    }

    public UETextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public UETextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.UETextView, defStyleAttr, 0);
        drawableSize[0] = new int[]{a.getDimensionPixelSize(R.styleable.UETextView_drawableLeftWidth, 0), a.getDimensionPixelSize(R.styleable.UETextView_drawableLeftHeight, 0)};
        drawableSize[1] = new int[]{a.getDimensionPixelSize(R.styleable.UETextView_drawableTopWidth, 0), a.getDimensionPixelSize(R.styleable.UETextView_drawableTopHeight, 0)};
        drawableSize[2] = new int[]{a.getDimensionPixelSize(R.styleable.UETextView_drawableRightWidth, 0), a.getDimensionPixelSize(R.styleable.UETextView_drawableRightHeight, 0)};
        drawableSize[3] = new int[]{a.getDimensionPixelSize(R.styleable.UETextView_drawableBottomWidth, 0), a.getDimensionPixelSize(R.styleable.UETextView_drawableBottomHeight, 0)};

        a.recycle();
        Drawable[] drawables = getCompoundDrawables();
        for (int i = 0; i < 4; i++) {
            setDrawableBounds(drawables[i], drawableSize[i][0], drawableSize[i][1]);
        }
        setCompoundDrawables(drawables[0], drawables[1], drawables[2], drawables[3]);
    }

    public void setCompoundDrawableLeft(Drawable drawable) {
        if (drawable != null) {
            setDrawableBounds(drawable, drawableSize[0][0], drawableSize[0][1]);
        }
        Drawable[] drawables = getCompoundDrawables();
        setCompoundDrawables(drawable, drawables[1], drawables[2], drawables[3]);
    }

    public void setCompoundDrawableTop(Drawable drawable) {
        if (drawable != null) {
            setDrawableBounds(drawable, drawableSize[1][0], drawableSize[1][1]);
        }
        Drawable[] drawables = getCompoundDrawables();
        setCompoundDrawables(drawables[0], drawable, drawables[2], drawables[3]);
    }

    public void setCompoundDrawableRight(Drawable drawable) {
        if (drawable != null) {
            setDrawableBounds(drawable, drawableSize[2][0], drawableSize[2][1]);
        }
        Drawable[] drawables = getCompoundDrawables();
        setCompoundDrawables(drawables[0], drawables[1], drawable, drawables[3]);
    }

    public void setCompoundDrawableBottom(Drawable drawable) {
        if (drawable != null) {
            setDrawableBounds(drawable, drawableSize[3][0], drawableSize[3][1]);
        }
        Drawable[] drawables = getCompoundDrawables();
        setCompoundDrawables(drawables[0], drawables[1], drawables[2], drawable);
    }

    public void setCompoundDrawables(int left, int top, int right, int bottom) {
        final Resources resources = getContext().getResources();
        Drawable drawableLeft = left != 0 ? resources.getDrawable(left) : null;
        Drawable drawableTop = top != 0 ? resources.getDrawable(top) : null;
        Drawable drawableRight = right != 0 ? resources.getDrawable(right) : null;
        Drawable drawableBottom = bottom != 0 ? resources.getDrawable(bottom) : null;
        if (drawableLeft != null) {
            setDrawableBounds(drawableLeft, drawableSize[0][0], drawableSize[0][1]);
        }
        if (drawableTop != null) {
            setDrawableBounds(drawableTop, drawableSize[1][0], drawableSize[1][1]);
        }
        if (drawableRight != null) {
            setDrawableBounds(drawableRight, drawableSize[2][0], drawableSize[2][1]);
        }
        if (drawableBottom != null) {
            setDrawableBounds(drawableBottom, drawableSize[3][0], drawableSize[3][1]);
        }
        // 解决三星s3手机，一个默认图按下，其他默认图也按下的bug
        setCompoundDrawables(drawableLeft != null ? drawableLeft.mutate() : null, drawableTop != null ? drawableTop.mutate() : null
                , drawableRight != null ? drawableRight.mutate() : null, drawableBottom != null ? drawableBottom.mutate() : null);
    }

    private void setDrawableBounds(Drawable drawable, int width, int height) {
        if (drawable != null) {
            if (width > 0 && height > 0) {
                drawable.setBounds(0, 0, width, height);
            } else {
                drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
            }
        }
    }

}
