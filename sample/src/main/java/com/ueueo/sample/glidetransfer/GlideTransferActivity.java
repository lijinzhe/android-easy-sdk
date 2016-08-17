package com.ueueo.sample.glidetransfer;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.ueueo.glidetransfer.BlurTransfer;
import com.ueueo.glidetransfer.ColorFilterTransfer;
import com.ueueo.glidetransfer.CropCircleTransfer;
import com.ueueo.glidetransfer.CropRectangleTransfer;
import com.ueueo.glidetransfer.CropSquareTransfer;
import com.ueueo.glidetransfer.CropTransfer;
import com.ueueo.glidetransfer.GrayscaleTransfer;
import com.ueueo.glidetransfer.MaskTransfer;
import com.ueueo.glidetransfer.RoundCornerTransfer;
import com.ueueo.sample.R;
import com.ueueo.utils.UEDimenUtil;

public class GlideTransferActivity extends AppCompatActivity {

    private String[] mTransferTypes = new String[]{
            "Mask",
            "NinePatchMask",
            "CropTop",
            "CropCenter",
            "CropBottom",
            "CropSquare",
            "CropRectangle",
            "CropCircle",
            "ColorFilter",
            "Grayscale",
            "RoundedCorners",
            "Blur50",
            "Blur100"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        RecyclerView recyclerView = new RecyclerView(this);
        setContentView(recyclerView);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        recyclerView.setAdapter(new GlideTransferAdapter(this));
    }

    public class GlideTransferAdapter extends RecyclerView.Adapter<GlideTransferAdapter.ViewHolder> {

        private Context mContext;

        public GlideTransferAdapter(Context context) {
            mContext = context;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(mContext).inflate(R.layout.glidetransfer_list_item, parent, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            switch (mTransferTypes[position]) {
                case "Mask": {
                    int width = UEDimenUtil.dpToPx(mContext, 133);
                    int height = UEDimenUtil.dpToPx(mContext, 126);
                    Glide.with(mContext)
                            .load(R.drawable.glidetransfer_image)
                            .override(width, height)
                            .bitmapTransform(new CenterCrop(mContext),
                                    new MaskTransfer(mContext, R.drawable.glidetransfer_mask_starfish))
                            .into(holder.image);
                    break;
                }
                case "NinePatchMask": {
                    int width = UEDimenUtil.dpToPx(mContext, 150);
                    int height = UEDimenUtil.dpToPx(mContext, 100);
                    Glide.with(mContext)
                            .load(R.drawable.glidetransfer_image)
                            .override(width, height)
                            .bitmapTransform(new CenterCrop(mContext),
                                    new MaskTransfer(mContext, R.drawable.glidetransfer_mask_chat_right))
                            .into(holder.image);
                    break;
                }
                case "CropTop":
                    Glide.with(mContext)
                            .load(R.drawable.glidetransfer_image)
                            .bitmapTransform(
                                    new CropTransfer(mContext, 300, 100, CropTransfer.CropType.TOP))
                            .into(holder.image);
                    break;
                case "CropCenter":
                    Glide.with(mContext)
                            .load(R.drawable.glidetransfer_image)
                            .bitmapTransform(new CropTransfer(mContext, 300, 100))
                            .into(holder.image);
                    break;
                case "CropBottom":
                    Glide.with(mContext)
                            .load(R.drawable.glidetransfer_image)
                            .bitmapTransform(
                                    new CropTransfer(mContext, 300, 100, CropTransfer.CropType.BOTTOM))
                            .into(holder.image);

                    break;
                case "CropSquare":
                    Glide.with(mContext)
                            .load(R.drawable.glidetransfer_image)
                            .bitmapTransform(new CropSquareTransfer(mContext))
                            .into(holder.image);
                    break;
                case "CropRectangle":
                    Glide.with(mContext)
                            .load(R.drawable.glidetransfer_image)
                            .bitmapTransform(new CropRectangleTransfer(mContext, 0.5f))
                            .into(holder.image);
                    break;
                case "CropCircle":
                    Glide.with(mContext)
                            .load(R.drawable.glidetransfer_image)
                            .bitmapTransform(new CropCircleTransfer(mContext))
                            .into(holder.image);
                    break;
                case "ColorFilter":
                    Glide.with(mContext)
                            .load(R.drawable.glidetransfer_image)
                            .bitmapTransform(new ColorFilterTransfer(mContext, Color.argb(80, 255, 0, 0)))
                            .into(holder.image);
                    break;
                case "Grayscale":
                    Glide.with(mContext)
                            .load(R.drawable.glidetransfer_image)
                            .bitmapTransform(new GrayscaleTransfer(mContext))
                            .into(holder.image);
                    break;
                case "RoundedCorners":
                    Glide.with(mContext)
                            .load(R.drawable.glidetransfer_image)
                            .bitmapTransform(new RoundCornerTransfer(mContext, 40, 0,
                                    RoundCornerTransfer.CornerType.ALL))
                            .into(holder.image);
                    break;
                case "Blur50":
                    Glide.with(mContext)
                            .load(R.drawable.glidetransfer_image)
                            .bitmapTransform(new BlurTransfer(mContext, 50))
                            .into(holder.image);
                    break;
                case "Blur100":
                    Glide.with(mContext)
                            .load(R.drawable.glidetransfer_image)
                            .bitmapTransform(new BlurTransfer(mContext, 100))
                            .into(holder.image);
                    break;

            }
            holder.title.setText(mTransferTypes[position]);
        }

        @Override
        public int getItemCount() {
            return mTransferTypes.length;
        }

        class ViewHolder extends RecyclerView.ViewHolder {

            public ImageView image;
            public TextView title;

            ViewHolder(View itemView) {
                super(itemView);
                image = (ImageView) itemView.findViewById(R.id.image);
                title = (TextView) itemView.findViewById(R.id.title);
            }
        }
    }
}
