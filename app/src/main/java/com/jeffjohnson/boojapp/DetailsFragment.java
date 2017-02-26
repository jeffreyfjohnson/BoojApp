package com.jeffjohnson.boojapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;

/**
 * Created by jeffreyjohnson on 2/25/17.
 */

public class DetailsFragment extends Fragment {
    private static int IMAGE_WIDTH_PX;

    private Realtor realtor;
    private int position;
    private ImageView imageView;
    private TextView nameText;
    private TextView companyText;
    private TextView phoneText;

    public void setRealtor(Realtor realtor) {
        this.realtor = realtor;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //get the width in pixels of the full image
        IMAGE_WIDTH_PX = (int)ViewUtils.getPxFromDp(250, getContext());

        BroadcastReceiver imageReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                int position = intent.getIntExtra(DetailPictureDownloadService.EXTRA_PIC_POSITION, -1);
                if (position == DetailsFragment.this.position){
                    setImage();
                }
            }
        };

        LocalBroadcastManager.getInstance(getContext()).registerReceiver(imageReceiver,
                new IntentFilter(DetailPictureDownloadService.INTENT_DETAIL_PIC));
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_details, container, false);

        imageView = (ImageView) root.findViewById(R.id.realtor_details_image);
        nameText = (TextView) root.findViewById(R.id.name_value);
        companyText = (TextView) root.findViewById(R.id.company_value);
        phoneText = (TextView) root.findViewById(R.id.phone_value);

        nameText.setText(realtor.getFirstName() + " " + realtor.getLastName());
        companyText.setText(realtor.getOffice());
        phoneText.setText(FormatUtils.formatPhone(realtor.getPhoneNumber()));

        setImage();

        return root;
    }

    public void setImage(){
        //this ViewHolder has become visible, check if we have already downloaded it
        Uri pictureUri = Uri.parse(realtor.getPhotoUrl()).buildUpon()
                .appendEncodedPath("width")
                .appendEncodedPath(Integer.toString(IMAGE_WIDTH_PX))
                .build();
        //check if the image is already downloaded
        Bitmap bitmap = PictureCache.getInstance().get(pictureUri.toString());
        if (bitmap != null) {
            //if it is, set the image bitmap
            imageView.setImageBitmap(bitmap);
        } else {
            Context context = getContext();
            if (context != null) {
                //otherwise, clear the image, since these views are recycled
                imageView.setImageDrawable(ContextCompat.getDrawable(context, R.mipmap.ic_launcher));
                //and then download the image
                Intent downloadIntent = new Intent(getContext(), DetailPictureDownloadService.class);
                downloadIntent.setData(pictureUri);
                downloadIntent.putExtra(DetailPictureDownloadService.EXTRA_PIC_POSITION, position);
                getContext().startService(downloadIntent);
            }
        }
    }
}
