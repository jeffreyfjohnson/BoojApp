package com.jeffjohnson.boojapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.List;

/**
 * Created by jeffreyjohnson on 2/25/17.
 */

public class PagerFragment extends Fragment {

    private List<Realtor> realtors;
    private Integer currentRealtorIndex = null;

    private ViewPager pager;
    private DetailsFragmentAdapter adapter;
    private ToastShowDelegate delegate;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_pager, container, false);

        pager = (ViewPager) root.findViewById(R.id.pager);
        adapter = new DetailsFragmentAdapter(getFragmentManager());
        pager.setAdapter(adapter);

        if (currentRealtorIndex != null){
            pager.setCurrentItem(currentRealtorIndex, true);
        }

        if (!delegate.hasShownToast()){
            Toast.makeText(
                    getContext(),
                    R.string.swipe_message,
                    Toast.LENGTH_SHORT
            ).show();
            delegate.toastShown();
        }

        return root;
    }

    public void setRealtors(List<Realtor> realtors) {
        this.realtors = realtors;
    }

    public void setCurrentRealtorIndex(Integer currentRealtorIndex) {
        this.currentRealtorIndex = currentRealtorIndex;
    }

    public void setDelegate(ToastShowDelegate delegate) {
        this.delegate = delegate;
    }

    private class DetailsFragmentAdapter extends FragmentStatePagerAdapter{

        public DetailsFragmentAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            DetailsFragment detailsFragment = new DetailsFragment();
            detailsFragment.setRealtor(realtors.get(position));
            detailsFragment.setPosition(position);
            return detailsFragment;
        }

        @Override
        public int getCount() {
            return realtors == null ? 0 : realtors.size();
        }
    }

    public interface ToastShowDelegate{
        boolean hasShownToast();
        void toastShown();
    }
}
