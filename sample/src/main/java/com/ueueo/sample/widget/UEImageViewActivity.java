package com.ueueo.sample.widget;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.ueueo.sample.R;
import com.ueueo.widget.UEImageView;

public class UEImageViewActivity extends AppCompatActivity {
    UEImageView imageView = null;
    int i = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ueimageview);
        imageView =  (UEImageView)findViewById(R.id.image);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                i++;
                if(i%2 == 0){
                    imageView.setAspectRatio(3);
                    imageView.setShape(UEImageView.SHAPE_NORMAL);
                }else{
                    imageView.setShape(UEImageView.SHAPE_CIRCLE);
                }

//                imageView.setBorderWidth(i++);
            }
        });
    }

}
