package com.ueueo.glidetransfer;

import android.content.Context;
import android.graphics.Bitmap;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.Transformation;
import com.bumptech.glide.load.engine.Resource;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapResource;

public class CropRectangleTransfer implements Transformation<Bitmap> {

    private BitmapPool mBitmapPool;

    /**
     * 宽高比(已知宽，求高)
     */
    private float mAspectRatio = 0;

    public CropRectangleTransfer(Context context) {
        this(Glide.get(context).getBitmapPool(), 0);
    }

    public CropRectangleTransfer(Context context, float aspectRatio) {
        this(Glide.get(context).getBitmapPool(), aspectRatio);

    }

    public CropRectangleTransfer(BitmapPool pool, float aspectRatio) {
        this.mBitmapPool = pool;
        this.mAspectRatio = aspectRatio;
    }

    @Override
    public Resource<Bitmap> transform(Resource<Bitmap> resource, int outWidth, int outHeight) {
        if (mAspectRatio <= 0) {
            return resource;
        }

        Bitmap source = resource.get();
        int width = source.getWidth();
        int height = source.getHeight();
        float curRatio = (float) width / (float) height;
        int offsetX = 0;
        int offsetY = 0;
        if(curRatio > mAspectRatio){
            //截取宽
            width = (int)(height * mAspectRatio);
            offsetX = (source.getWidth() - width)/2;
        }else{
            //截取高
            height = (int)(width/mAspectRatio);
            offsetY = (source.getHeight() - height) / 2;
        }
//        Bitmap.Config config = source.getConfig() != null ? source.getConfig() : Bitmap.Config.ARGB_8888;
//        Bitmap bitmap = mBitmapPool.get(width, height, config);
//        if (bitmap == null) {
        Bitmap  bitmap = Bitmap.createBitmap(source, offsetX, offsetY, width, height);
//        }

        return BitmapResource.obtain(bitmap, mBitmapPool);
    }

    @Override
    public String getId() {
        return "CropRectangleTransfer(aspectRatio=" + mAspectRatio + ")";
    }
}
