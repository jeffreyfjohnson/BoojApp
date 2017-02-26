package com.jeffjohnson.boojapp;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.jeffjohnson.boojapp.Downloader.RealtorListCallback;

import java.util.List;

/**
 * Created by jeffreyjohnson on 2/25/17.
 */

public class ListFragment extends Fragment implements RealtorAdapter.ItemClickListener {

    private List<Realtor> realtors;

    private RecyclerView realtorRecyclerView;
    private RealtorAdapter adapter;

    private BroadcastReceiver pictureBroadcastReceiver;
    private NavigationDelegate delegate;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pictureBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                int position = intent.getIntExtra(ListPictureDownloadService.EXTRA_PIC_POSITION, -1);
                if (position >= 0 && adapter != null) {
                    adapter.notifyItemChanged(position);
                }
            }
        };
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(
                pictureBroadcastReceiver,
                new IntentFilter(ListPictureDownloadService.INTENT_LIST_PIC)
        );
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_list, container, false);
        root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (realtors == null || realtors.size() < 1) {
                    downloadList();
                }
            }
        });

        realtorRecyclerView = (RecyclerView) root.findViewById(R.id.realtor_recycler_view);
        realtorRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        if (realtors == null){
            downloadList();
            realtorRecyclerView.setVisibility(View.GONE);
        }
        else{
            setAdapter();
        }


        return root;
    }

    public void setDelegate(NavigationDelegate delegate) {
        this.delegate = delegate;
    }

    private void downloadList(){
        final ProgressDialog progressDialog = ProgressDialog.show(
                getContext(),
                getString(R.string.downloading),
                getString(R.string.please_wait),
                true
        );
        Downloader.getInstance().getRealtorList(new RealtorListCallback() {
            @Override
            public void onRealtorListDownloaded(List<Realtor> realtorList) {
                realtors = realtorList;
                setAdapter();
                progressDialog.dismiss();
            }

            @Override
            public void onException() {
                progressDialog.dismiss();
                Toast.makeText(
                        getContext(),
                        getString(R.string.error),
                        Toast.LENGTH_LONG
                ).show();
            }
        });
    }

    private void setAdapter(){
        adapter = new RealtorAdapter(realtors, getContext());
        adapter.setClickListener(this);
        realtorRecyclerView.setAdapter(adapter);
        realtorRecyclerView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onItemClicked(int position) {
        delegate.navigateToDetails(realtors, position);
    }

    public interface NavigationDelegate{
        void navigateToDetails(List<Realtor> realtorList, int index);
    }
}
