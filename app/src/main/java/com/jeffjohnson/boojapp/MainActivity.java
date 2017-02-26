package com.jeffjohnson.boojapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import java.util.List;

/**
 * Created by jeffreyjohnson on 2/24/17.
 */

public class MainActivity extends AppCompatActivity
        implements ListFragment.NavigationDelegate, PagerFragment.ToastShowDelegate {
    FragmentManager fragmentManager;
    boolean hasShownSwipeToast = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        fragmentManager = getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentById(R.id.fragment_frame);
        if (fragment == null){
            ListFragment listFragment = new ListFragment();
            listFragment.setDelegate(this);
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.add(R.id.fragment_frame, listFragment).commit();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopService(new Intent(this, DetailPictureDownloadService.class));
        stopService(new Intent(this, ListPictureDownloadService.class));
    }

    @Override
    public boolean hasShownToast() {
        return hasShownSwipeToast;
    }

    @Override
    public void toastShown() {
        hasShownSwipeToast = true;
    }

    @Override
    public void navigateToDetails(List<Realtor> realtorList, int index) {
        PagerFragment pagerFragment = new PagerFragment();
        pagerFragment.setCurrentRealtorIndex(index);
        pagerFragment.setRealtors(realtorList);
        pagerFragment.setDelegate(this);
        fragmentManager.beginTransaction()
                .addToBackStack(null).replace(R.id.fragment_frame, pagerFragment)
                .commit();
    }
}
