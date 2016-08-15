package com.ueueo.glidetransfer;

/**
 * Copyright (C) 2015 Wasabeef
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.content.Context;
import android.graphics.Bitmap;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.Transformation;
import com.bumptech.glide.load.engine.Resource;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapResource;

public class CropSquareTransfer implements Transformation<Bitmap> {

    private BitmapPool mBitmapPool;

    private int mSize;

    public CropSquareTransfer(Context context) {
        this(Glide.get(context).getBitmapPool());
    }

    public CropSquareTransfer(BitmapPool pool) {
        this.mBitmapPool = pool;
    }

    @Override
    public Resource<Bitmap> transform(Resource<Bitmap> resource, int outWidth, int outHeight) {
        Bitmap source = resource.get();
        mSize = Math.min(source.getWidth(), source.getHeight());

        int offsetX = (source.getWidth() - mSize) / 2;
        int offsetY = (source.getHeight() - mSize) / 2;

        Bitmap.Config config = source.getConfig() != null ? source.getConfig() : Bitmap.Config.ARGB_8888;
//        Bitmap bitmap = mBitmapPool.get(mSize, mSize, config);
//        if (bitmap == null) {
        Bitmap  bitmap = Bitmap.createBitmap(source, offsetX, offsetY, mSize, mSize);
//        }

        return BitmapResource.obtain(bitmap, mBitmapPool);
    }

    @Override
    public String getId() {
        return "CropSquareTransformation(width=" + mSize + ", height=" + mSize + ")";
    }
}
