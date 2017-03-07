package com.ueueo.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * 1.可设置宽高比
 * 2.可设置圆形，正方形
 * 3.可设置边框（颜色，宽度）
 * 4.可设置按下阴影效果
 * <p/>
 * Created by Lee on 16/8/11.
 */
public class UEImageView extends ImageView {
    public static final int SHAPE_NORMAL = 0;//正常ImageView，没有设置形状
    public static final int SHAPE_CIRCLE = 1;//圆形
    public static final int SHAPE_SQUARE = 2;//方形

    /**
     * Imageview的形状
     */
    private int mShape = SHAPE_NORMAL;

    /**
     * 按下时颜色
     */
    private int mPressedColor = 0xFFCCCCCC;
    /**
     * 按下时是否颜色变暗
     */
    private boolean mPressedColorEnable = false;
    /**
     * 宽高比，当mShape等于SHAPE_RECTANGLE（矩形）时有效
     */
    private float mAspectRatio = 1;
    /**
     * 边框的颜色
     */
    private int mBorderColor;
    /**
     * 边框的宽度，默认为0，即没有圆边
     */
    private int mBorderWidth = 0;
    /**
     * 边框圆角大小
     */
    private int mBorderRadius;

    private Bitmap mBitmap;
    private BitmapShader mBitmapShader;

    private int mBitmapWidth;
    private int mBitmapHeight;

    private RectF mDrawableRect = new RectF();
    private RectF mBorderRect = new RectF();
    private Matrix mShaderMatrix = new Matrix();
    private Paint mBitmapPaint = new Paint();
    private Paint mBorderPaint = new Paint();

    public UEImageView(Context context) {
        this(context, null);
    }

    public UEImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public UEImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.UEImageView);
        mPressedColor = a.getColor(R.styleable.UEImageView_pressedColor, 0xFFCCCCCC);
        mAspectRatio = a.getFloat(R.styleable.UEImageView_aspectRatio, 0);
        mPressedColorEnable = a.getBoolean(R.styleable.UEImageView_pressedColorEnable, false);

        mShape = a.getInt(R.styleable.UEImageView_shape, SHAPE_NORMAL);
        mBorderWidth = a.getDimensionPixelSize(R.styleable.UEImageView_borderWidth, 0);
        mBorderRadius = a.getDimensionPixelSize(R.styleable.UEImageView_borderRadius, 0);
        mBorderColor = a.getColor(R.styleable.UEImageView_borderColor, Color.BLACK);
        a.recycle();

        if (mShape == SHAPE_CIRCLE || mShape == SHAPE_SQUARE) {
            mAspectRatio = 1;
        }
        if (mShape == SHAPE_CIRCLE || mShape == SHAPE_SQUARE || mBorderWidth > 0 || mBorderRadius > 0) {
            setScaleType(ScaleType.CENTER_CROP);
        }
        initializeBitmap();
    }

    public void setAspectRatio(float aspectRatio) {
        this.mAspectRatio = aspectRatio;
        requestLayout();
    }

    public void setPressedColor(int pressedColor) {
        this.mPressedColor = pressedColor;
        refreshDrawableState();
    }

    public void setPressedColorEnable(boolean enable) {
        this.mPressedColorEnable = enable;
        refreshDrawableState();
    }

    /**
     * 设置形状
     * <p/>
     * 此方法必须在setImageResource()等设置图片的方法前调用，否则无效
     *
     * @param shape
     */
    public void setShape(int shape) {
        mShape = shape;
        if (mShape == SHAPE_CIRCLE || mShape == SHAPE_SQUARE) {
            mAspectRatio = 1;
        }
        if (mShape == SHAPE_CIRCLE || mShape == SHAPE_SQUARE || mBorderWidth > 0 || mBorderRadius > 0) {
            setScaleType(ScaleType.CENTER_CROP);
        }
    }

    @Override
    public ScaleType getScaleType() {
        if (mShape == SHAPE_CIRCLE || mShape == SHAPE_SQUARE || mBorderWidth > 0 || mBorderRadius > 0) {
            return ScaleType.CENTER_CROP;
        } else {
            return super.getScaleType();
        }
    }

    @Override
    public void setScaleType(ScaleType scaleType) {
        if (mShape == SHAPE_CIRCLE || mShape == SHAPE_SQUARE || mBorderWidth > 0 || mBorderRadius > 0) {
            if (scaleType != ScaleType.CENTER_CROP) {
                throw new IllegalArgumentException(String.format("ScaleType %s not supported.", scaleType));
            }
        } else {
            super.setScaleType(scaleType);
        }
    }

    @Override
    public void setAdjustViewBounds(boolean adjustViewBounds) {
        if (mShape == SHAPE_CIRCLE || mShape == SHAPE_SQUARE || mBorderWidth >= 0 || mBorderRadius > 0) {
            if (adjustViewBounds) {
                throw new IllegalArgumentException("adjustViewBounds not supported.");
            }
        } else {
            super.setAdjustViewBounds(adjustViewBounds);
        }
    }

    @Override
    protected void drawableStateChanged() {
        super.drawableStateChanged();
        if (android.os.Build.VERSION.SDK_INT >= 14) {
            final Drawable background = getBackground();
            final Drawable drawable = getDrawable();
            if (mPressedColorEnable && mPressedColor != 0 && isPressed()) {
                PorterDuffColorFilter colorFilter = new PorterDuffColorFilter(mPressedColor, PorterDuff.Mode.MULTIPLY);
                if (background != null) {
                    background.setColorFilter(colorFilter);
                }
                if (drawable != null) {
                    drawable.setColorFilter(colorFilter);
                }
            } else {
                if (background != null) {
                    background.clearColorFilter();
                }
                if (drawable != null) {
                    drawable.clearColorFilter();
                }
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        //根据宽高比计算View的高度
        if (widthMode == MeasureSpec.EXACTLY && heightMode != MeasureSpec.EXACTLY) {
            if (mAspectRatio > 0) {
                int paddingLeft = getPaddingLeft();
                int paddingTop = getPaddingTop();
                int paddingRight = getPaddingRight();
                int paddingBottom = getPaddingBottom();
                heightSize = (int) ((widthSize - paddingLeft - paddingRight) / mAspectRatio + paddingTop + paddingBottom);
            }
        }
        setMeasuredDimension(widthSize, heightSize);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mShape == SHAPE_CIRCLE || mShape == SHAPE_SQUARE || mBorderWidth > 0 || mBorderRadius > 0) {
            if (mBitmap == null) {
                return;
            }
            if (mPressedColorEnable && mPressedColor != 0 && isPressed()) {
                PorterDuffColorFilter colorFilter = new PorterDuffColorFilter(mPressedColor, PorterDuff.Mode.MULTIPLY);
                mBitmapPaint.setColorFilter(colorFilter);
                mBorderPaint.setColorFilter(colorFilter);
            } else {
                mBitmapPaint.setColorFilter(null);
                mBorderPaint.setColorFilter(null);
            }
            if (mShape == SHAPE_CIRCLE) {
                canvas.drawCircle(mDrawableRect.centerX(), mDrawableRect.centerY(), mDrawableRect.width() / 2.0f, mBitmapPaint);
                if (mBorderWidth > 0) {
                    canvas.drawCircle(mBorderRect.centerX(), mBorderRect.centerY(), mDrawableRect.width() / 2.0f, mBorderPaint);
                }
            } else {
                canvas.drawRoundRect(mDrawableRect, mBorderRadius, mBorderRadius, mBitmapPaint);
                if (mBorderWidth > 0) {
                    canvas.drawRoundRect(mBorderRect, mBorderRadius, mBorderRadius, mBorderPaint);
                }
            }
        } else {
            super.onDraw(canvas);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        initializeBitmapPaint();
    }

    @Override
    public void setPadding(int left, int top, int right, int bottom) {
        super.setPadding(left, top, right, bottom);
        initializeBitmapPaint();
    }

    @Override
    public void setPaddingRelative(int start, int top, int end, int bottom) {
        super.setPaddingRelative(start, top, end, bottom);
        initializeBitmapPaint();
    }

    public int getBorderColor() {
        return mBorderColor;
    }

    public void setBorderColor(@ColorInt int borderColor) {
        if (borderColor == mBorderColor) {
            return;
        }

        mBorderColor = borderColor;
        mBorderPaint.setColor(mBorderColor);
        invalidate();
    }

    public int getBorderWidth() {
        return mBorderWidth;
    }

    public void setBorderWidth(int borderWidth) {
        if (borderWidth == mBorderWidth) {
            return;
        }

        mBorderWidth = borderWidth;
        initializeBitmapPaint();
    }

    @Override
    public void setImageDrawable(Drawable drawable) {
        super.setImageDrawable(drawable);
        initializeBitmap();
    }

    @Override
    public void setImageResource(@DrawableRes int resId) {
        super.setImageResource(resId);
        initializeBitmap();
    }

    @Override
    public void setImageURI(Uri uri) {
        super.setImageURI(uri);
        initializeBitmap();
    }

    private void initializeBitmap() {
        mBitmap = null;
        if (mShape == SHAPE_CIRCLE || mShape == SHAPE_SQUARE || mBorderWidth > 0 || mBorderRadius > 0) {
            Drawable drawable = getDrawable();
            if (drawable != null) {
                if (drawable instanceof BitmapDrawable) {
                    mBitmap = ((BitmapDrawable) drawable).getBitmap();
                } else {
                    try {
                        Bitmap bitmap;

                        if (drawable instanceof ColorDrawable) {
                            bitmap = Bitmap.createBitmap(2, 2, Bitmap.Config.ARGB_8888);
                        } else {
                            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
                        }

                        Canvas canvas = new Canvas(bitmap);
                        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
                        drawable.draw(canvas);
                        mBitmap = bitmap;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        initializeBitmapPaint();
    }

    private void initializeBitmapPaint() {
        if (mShape == SHAPE_NORMAL && mBorderWidth <= 0 && mBorderRadius <= 0) {
            return;
        }

        if (mBitmap == null) {
            invalidate();
            return;
        }

        mBitmapShader = new BitmapShader(mBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        mBitmapPaint.setAntiAlias(true);
        mBitmapPaint.setShader(mBitmapShader);

        mBitmapHeight = mBitmap.getHeight();
        mBitmapWidth = mBitmap.getWidth();

        mBorderPaint.setStyle(Paint.Style.STROKE);
        mBorderPaint.setAntiAlias(true);
        mBorderPaint.setColor(mBorderColor);
        mBorderPaint.setStrokeWidth(mBorderWidth);
        mBorderRect.set(calculateBounds());
        //如果不加inset画边框时会存在截取的问题
        int inset = (int) (mBorderWidth / 2f + 0.5f);
        mBorderRect.inset(inset, inset);
        mDrawableRect.set(mBorderRect);

        updateShaderMatrix();
        invalidate();
    }

    private RectF calculateBounds() {
        int availableWidth = getWidth() - getPaddingLeft() - getPaddingRight();
        int availableHeight = getHeight() - getPaddingTop() - getPaddingBottom();

        int sideWidth = 0;
        int sideHeight = 0;

        if (mShape == SHAPE_CIRCLE || mShape == SHAPE_SQUARE) {
            sideWidth = sideHeight = Math.min(availableWidth, availableHeight);
        } else if (mAspectRatio > 0) {
            float curRatio = (float) availableWidth / (float) availableHeight;
            if (curRatio > mAspectRatio) {
                //截取宽
                sideWidth = (int) (availableHeight * mAspectRatio);
                sideHeight = availableHeight;
            } else {
                //截取高
                sideHeight = (int) (availableWidth / mAspectRatio);
                sideWidth = availableWidth;
            }
        } else {
            sideWidth = availableWidth;
            sideHeight = availableHeight;
        }

        float left = getPaddingLeft() + (availableWidth - sideWidth) / 2f;
        float top = getPaddingTop() + (availableHeight - sideHeight) / 2f;

        return new RectF(left, top, left + sideWidth, top + sideHeight);
    }

    private void updateShaderMatrix() {
        float scale;
        float dx = 0;
        float dy = 0;

        mShaderMatrix.set(null);

        if (mBitmapWidth * mDrawableRect.height() > mDrawableRect.width() * mBitmapHeight) {
            scale = mDrawableRect.height() / (float) mBitmapHeight;
            dx = (mDrawableRect.width() - mBitmapWidth * scale) * 0.5f;
        } else {
            scale = mDrawableRect.width() / (float) mBitmapWidth;
            dy = (mDrawableRect.height() - mBitmapHeight * scale) * 0.5f;
        }

        mShaderMatrix.setScale(scale, scale);
        mShaderMatrix.postTranslate((int) (dx + 0.5f) + mDrawableRect.left, (int) (dy + 0.5f) + mDrawableRect.top);

        mBitmapShader.setLocalMatrix(mShaderMatrix);
    }

}
