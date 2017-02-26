package com.jeffjohnson.boojapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by jeffreyjohnson on 2/25/17.
 */

public class RealtorAdapter extends RecyclerView.Adapter<RealtorAdapter.RealtorHolder> {
    public static final int DEFAULT_IMAGE_WIDTH = 175;
    List<Realtor> realtorList;
    private ItemClickListener clickListener;
    private Context context;

    public RealtorAdapter(List<Realtor> realtorList, Context context) {
        this.realtorList = realtorList;
        this.context = context;
    }

    public void setClickListener(ItemClickListener clickListener) {
        this.clickListener = clickListener;
    }

    @Override
    public RealtorHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.realtor_item, parent, false);
        return new RealtorHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RealtorHolder holder, int position) {
        Realtor realtor = realtorList.get(position);
        holder.bindRealtor(realtor, position);
    }

    @Override
    public int getItemCount() {
        return realtorList == null ? 0 : realtorList.size();
    }

    public class RealtorHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private ImageView realtorItemImage;
        private TextView realtorItemName;
        private TextView realtorItemPhone;
        private int position;

        public RealtorHolder(View itemView) {
            super(itemView);
            realtorItemImage = (ImageView) itemView.findViewById(R.id.realtor_item_image);
            realtorItemName = (TextView) itemView.findViewById(R.id.realtor_item_name);
            realtorItemPhone = (TextView) itemView.findViewById(R.id.realtor_item_phone);
            itemView.setOnClickListener(this);
        }

        public void bindRealtor(Realtor realtor, int position) {
            this.position = position;

            int width = realtorItemImage.getWidth();
            //this will be called before the ImageView is finished laying out, so we should
            // have a default because we want images to download ASAP
            if (width <= 0) width = DEFAULT_IMAGE_WIDTH;
            //this ViewHolder has become visible, check if we have already downloaded it
            Uri pictureUri = Uri.parse(realtor.getPhotoUrl()).buildUpon()
                    .appendEncodedPath("width")
                    .appendEncodedPath(Integer.toString(width))
                    .build();
            //check if the image is already downloaded
            Bitmap bitmap = PictureCache.getInstance().get(pictureUri.toString());
            if (bitmap != null) {
                //if it is, set the image bitmap
                realtorItemImage.setImageBitmap(bitmap);
            } else {
                //otherwise, clear the image, since these views are recycled
                realtorItemImage.setImageDrawable(ContextCompat.getDrawable(context,R.mipmap.ic_launcher));
                //and then download the image
                Intent downloadIntent = new Intent(context, ListPictureDownloadService.class);
                downloadIntent.setData(pictureUri);
                downloadIntent.putExtra(ListPictureDownloadService.EXTRA_PIC_POSITION, position);
                context.startService(downloadIntent);
            }

            realtorItemName.setText(realtor.getFirstName() + " " + realtor.getLastName());
            realtorItemPhone.setText(FormatUtils.formatPhone(realtor.getPhoneNumber()));
        }

        @Override
        public void onClick(View view) {
            clickListener.onItemClicked(position);
        }
    }

    public interface ItemClickListener{
        void onItemClicked(int position);
    }
}
