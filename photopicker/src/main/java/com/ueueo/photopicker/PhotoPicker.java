package com.ueueo.photopicker;

import android.app.Activity;

import com.easy.photopicker.base.PhotoItem;
import com.easy.photopicker.dialog.PhotoPickerDialog;

import java.util.List;

/**
 * Created by Lee on 16/7/26.
 */
public class PhotoPicker {

    public static void pickSingle(Activity activity, boolean showCamera, OnSinglePickListener listener) {
        PhotoPickerDialog dialog = new PhotoPickerDialog(activity);
        dialog.show();
    }

    public static void pickSingleAndCrop(Activity activity, boolean showCamera, int cropWidth, int cropHeight, OnSinglePickListener listener) {
        PhotoPickerDialog dialog = new PhotoPickerDialog(activity);
        dialog.show();
    }

    public static void pickMultiple(Activity activity, int maxCount, boolean showCamera, OnMultiplePickListener listener) {
        PhotoPickerDialog dialog = new PhotoPickerDialog(activity);
        dialog.show();
    }

    public interface OnSinglePickListener {
        void onSinglePickd(PhotoItem photoItem);
    }

    public interface OnMultiplePickListener {
        void onMultiplePickd(List<PhotoItem> photoItems);
    }
}
