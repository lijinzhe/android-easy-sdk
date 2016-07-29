package com.ueueo.photopicker.dialog;

import android.app.Dialog;
import android.content.Context;

import com.ueueo.photopicker.R;

/**
 * Created by Lee on 16/7/26.
 */
public class PhotoPickerDialog extends Dialog {

    public PhotoPickerDialog(Context context) {
        super(context, R.style.Transparent);
        setContentView(R.layout.activity_images_grid);
    }
}
