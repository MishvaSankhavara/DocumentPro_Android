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
import com.example.documenpro.clickListener.DocClickListener;
import com.example.documenpro.model_reader.DocumentModel;
import com.example.documenpro.ui.activities.MainActivity;
import com.example.documenpro.ui.customviews.EmptyStateRecyclerView;
import com.example.documenpro.utils.Utils;
import com.example.documenpro.viewmodel.DataSingletonRecent;

import java.util.ArrayList;

public class FragmentRecent extends Fragment implements DocClickListener {
    private MainActivity activityContext;
    private EmptyStateRecyclerView recentRecyclerView;
    private TextView emptyDescriptionText;
    private ProgressBar loadingProgressBar;
    RecentFilesAdapter recentFilesAdapter;

    public FragmentRecent() {
    }

    public FragmentRecent(MainActivity mActivity) {
        this.activityContext = mActivity;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_recyclerview, container, false);
        initViews(view);
        DataSingletonRecent.getInstance().getRecentDocumentsLiveData().observe(getViewLifecycleOwner(), new Observer<ArrayList<DocumentModel>>() {
            @Override
            public void onChanged(ArrayList<DocumentModel> documents) {
                recentFilesAdapter = new RecentFilesAdapter(activityContext, FragmentRecent.this);
                recentRecyclerView.setAdapter(recentFilesAdapter);
            }
        });
        return view;
    }

    private void initViews(View view) {
        recentRecyclerView = view.findViewById(R.id.recyclerRecent);
        recentRecyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(activityContext);
        recentRecyclerView.setLayoutManager(layoutManager);
        recentRecyclerView.setEmptyView(view.findViewById(R.id.empty_layout));
        emptyDescriptionText = view.findViewById(R.id.tv_empty_desc);
        loadingProgressBar = view.findViewById(R.id.loadingView);
        emptyDescriptionText.setText(activityContext.getResources().getString(R.string.recent_files_appear_here));
        recentFilesAdapter = new RecentFilesAdapter(activityContext, this);
        recentRecyclerView.setAdapter(recentFilesAdapter);
        loadingProgressBar.setVisibility(View.GONE);
    }

    @Override
    public void onResume() {
        super.onResume();
        recentFilesAdapter = new RecentFilesAdapter(activityContext, this);
        recentRecyclerView.setAdapter(recentFilesAdapter);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof Activity) {
            this.activityContext = (MainActivity) context;
        }
    }

    @Override
    public void onDocClick(DocumentModel document) {
        Utils.openFile(activityContext, document);
    }
}
