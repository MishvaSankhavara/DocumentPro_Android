package com.example.documenpro.ui.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.documenpro.R;
import com.example.documenpro.adapter_reader.RecentFilesAdapter;
import com.example.documenpro.advertisement.AdManager;
import com.example.documenpro.clickListener.DocClickListener;
import com.example.documenpro.model_reader.DocumentModel;
import com.example.documenpro.ui.activities.MainActivity;
import com.example.documenpro.ui.customviews.EmptyRecyclerView;
import com.example.documenpro.utils.Utils;
import com.example.documenpro.viewmodel.RecentDataSingleton;

import java.util.ArrayList;

public class RecentFragment2 extends Fragment implements DocClickListener {
    private MainActivity mActivity;
    private EmptyRecyclerView recyclerView;
    private TextView tvDesEmpty;
    private ProgressBar loadingView;
    RecentFilesAdapter adapter;

    public RecentFragment2() {
    }

    public RecentFragment2(MainActivity mActivity) {
        this.mActivity = mActivity;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recyclerview, container, false);
        initViews(view);
        RecentDataSingleton.getInstance().getRecentLiveData().observe(getViewLifecycleOwner(), new Observer<ArrayList<DocumentModel>>() {
            @Override
            public void onChanged(ArrayList<DocumentModel> documents) {
                // Update RecyclerView with new favorite list
//                adapter.notifyDataSetChanged();
                adapter = new RecentFilesAdapter(mActivity, RecentFragment2.this);
                recyclerView.setAdapter(adapter);
            }
        });
        return view;
    }

    private void initViews(View view) {
        recyclerView = view.findViewById(R.id.recyclerRecent);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mActivity);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setEmptyView(view.findViewById(R.id.empty_layout));
        tvDesEmpty = view.findViewById(R.id.tv_empty_desc);
        loadingView = view.findViewById(R.id.loadingView);
        tvDesEmpty.setText(mActivity.getResources().getString(R.string.recent_files_appear_here));
        adapter = new RecentFilesAdapter(mActivity, this);
        recyclerView.setAdapter(adapter);
        loadingView.setVisibility(View.GONE);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof Activity) {
            this.mActivity = (MainActivity) context;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        adapter = new RecentFilesAdapter(mActivity, this);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onDocClick(DocumentModel document) {
//        Utils.openFile(mActivity, document);
//        Utils.openFileWithAds(mActivity, document, 2);
        AdManager.showAds_AdManager(mActivity, () -> Utils.openFile(mActivity, document));

    }


}