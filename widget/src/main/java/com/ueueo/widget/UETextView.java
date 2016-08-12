package com.ueueo.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * 1.可以设置4个边Drawable的大小
 * 2.可以添加按下阴影效果
 * 3.可以直接设置不同位置文字的大小／颜色／字体
 *
 * Created by Lee on 16/8/11.
 */
public class UETextView extends TextView{
    public UETextView(Context context) {
        super(context);
    }

    public UETextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public UETextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
}
