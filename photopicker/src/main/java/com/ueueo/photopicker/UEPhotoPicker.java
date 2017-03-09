package com.ueueo.photopicker;

import android.content.Context;
import android.content.Intent;

import java.util.ArrayList;

/**
 * 照片选择
 * <p/>
 * 需要申请权限：
 * <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE"/>
 * <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"/>
 * <uses-permission android:name="android.permission.CAMERA"/>
 * <p/>
 * Created by Lee on 16/8/9.
 */
public class UEPhotoPicker {

    static OnSingleChoiceListener mOnSingleChoiceListener;
    static OnMultipleChoiceListener mOnMultipleChoiceListener;

    /**
     * 单张照片选择
     *
     * @param context
     * @param showCamera 是否显示拍照项
     * @param listener   选择成功回调接口
     */
    public static void singleChoice(Context context, boolean showCamera, OnSingleChoiceListener listener) {
        mOnSingleChoiceListener = listener;
        mOnMultipleChoiceListener = null;
        Intent intent = new Intent(context, PermissionActivity.class);
        intent.putExtra(PhotoPickerActivity.EXTRA_SHOW_CAMERA, showCamera);
        intent.putExtra(PhotoPickerActivity.EXTRA_SELECT_MODE, PhotoPickerActivity.MODE_SINGLE);
        context.startActivity(intent);
    }

    /**
     * 多张照片选择
     *
     * @param context
     * @param showCamera 是否显示拍照项
     * @param count      最多选择照片个数
     * @param listener   选择成功回调接口
     */
    public static void multipleChoice(Context context, boolean showCamera, int count, OnMultipleChoiceListener listener) {
        multipleChoice(context, showCamera, count, null, listener);
    }

    /**
     * 多张照片选择
     *
     * @param context
     * @param showCamera   是否显示拍照项
     * @param count        最多选择照片个数
     * @param chosedImages 当前已选中的照片
     * @param listener     选择成功回调接口
     */
    public static void multipleChoice(Context context, boolean showCamera, int count, ArrayList<String> chosedImages, OnMultipleChoiceListener listener) {
        mOnMultipleChoiceListener = listener;
        mOnSingleChoiceListener = null;
        Intent intent = new Intent(context, PermissionActivity.class);
        intent.putExtra(PhotoPickerActivity.EXTRA_SHOW_CAMERA, showCamera);
        intent.putExtra(PhotoPickerActivity.EXTRA_SELECT_COUNT, count);
        if (chosedImages != null) {
            intent.putStringArrayListExtra(PhotoPickerActivity.EXTRA_DEFAULT_SELECTED_LIST, chosedImages);
        }
        intent.putExtra(PhotoPickerActivity.EXTRA_SELECT_MODE, PhotoPickerActivity.MODE_MULTI);
        context.startActivity(intent);
    }

    public interface OnSingleChoiceListener {
        void onSingleChosed(String imagePath);
    }

    public interface OnMultipleChoiceListener {
        void onMultipleChosed(ArrayList<String> imagePaths);
    }
}
