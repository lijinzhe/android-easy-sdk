package com.ueueo.sample.photopicker;

import android.os.Bundle;
import android.widget.Toast;

import com.ueueo.photopicker.UEPhotoPicker;
import com.ueueo.sample.AbsListActivity;

import java.util.ArrayList;

public class PhotoPickerActivity extends AbsListActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void initItemDatas() {
        addItemData(new ItemObject("选择单张照片") {
            @Override
            public void onItemClick() {
                UEPhotoPicker.singleChoice(PhotoPickerActivity.this, true, new UEPhotoPicker.OnSingleChoiceListener() {
                    @Override
                    public void onSingleChosed(String imagePath) {
                        Toast.makeText(PhotoPickerActivity.this, imagePath, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        addItemData(new ItemObject("选择多张照片") {
            @Override
            public void onItemClick() {
                UEPhotoPicker.multipleChoice(PhotoPickerActivity.this, true, 3, new UEPhotoPicker.OnMultipleChoiceListener() {
                    @Override
                    public void onMultipleChosed(ArrayList<String> imagePaths) {
                        StringBuilder sb = new StringBuilder();
                        for(String s:imagePaths){
                            sb.append(s).append("\n");
                        }
                        Toast.makeText(PhotoPickerActivity.this, sb.toString(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

}
