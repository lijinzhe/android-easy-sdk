package com.ueueo.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * 1.可设置宽高比，正方形
 * 2.可设置自定义形状，包括圆形等
 * 3.可设置圆边以及圆边的角度
 * 4.可设置按下阴影效果
 *
 * Created by Lee on 16/8/11.
 */
public class UEImageView extends ImageView{

    public UEImageView(Context context) {
        super(context);
    }

    public UEImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public UEImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
}
