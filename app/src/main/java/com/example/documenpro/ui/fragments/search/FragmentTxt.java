package com.example.documenpro.ui.fragments.search;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.documenpro.AppGlobalConstants;
import com.example.documenpro.R;
import com.example.documenpro.adapter_reader.FileListAdapter;
import com.example.documenpro.clickListener.DocClickListener;
import com.example.documenpro.model_reader.DocumentModel;
import com.example.documenpro.ui.customviews.EmptyStateRecyclerView;
import com.example.documenpro.utils.Utils;
import com.example.documenpro.viewmodel.ViewModelSearch;

import java.util.ArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class FragmentTxt extends Fragment implements DocClickListener {

    private Activity activityContext;
    private ViewModelSearch searchViewModel;
    private EmptyStateRecyclerView txtRecyclerView;
    private ProgressBar loadingProgressBar;
    private FileListAdapter txtAdapter;
    private ArrayList<DocumentModel> arrayList;
    private final Executor backgroundExecutor = Executors.newSingleThreadExecutor();

    public FragmentTxt(Activity mActivity) {
        this.activityContext = mActivity;
    }

    public FragmentTxt() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recyclerview, container, false);
        initViews(view);
        searchViewModel = new ViewModelProvider(requireActivity()).get(ViewModelSearch.class);
        listFile();
        return view;
    }
    private void listFile() {

        backgroundExecutor.execute(new Runnable() {
            @Override
            public void run() {

                arrayList = Utils.countFile(activityContext, AppGlobalConstants.QUERY_TEXT_FILES);

                activityContext.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        txtAdapter = new FileListAdapter(activityContext, arrayList, FragmentTxt.this);
                        txtRecyclerView.setAdapter(txtAdapter);
                        txtRecyclerView.setVisibility(View.VISIBLE);
                        loadingProgressBar.setVisibility(View.GONE);
                    }
                });
            }
        });
    }
    private void initViews(View view) {
        loadingProgressBar = view.findViewById(R.id.loadingView);
        txtRecyclerView = view.findViewById(R.id.recyclerRecent);
        txtRecyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(activityContext);
        txtRecyclerView.setLayoutManager(layoutManager);
        txtRecyclerView.setEmptyView(view.findViewById(R.id.empty_layout));
        arrayList = new ArrayList<>();


    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        searchViewModel.getSearchQueryLiveData().observe(requireActivity(), s -> Utils.searchDocument(s, arrayList, txtAdapter));
    }
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof Activity) {
            this.activityContext = (Activity) context;
        }
    }

    @Override
    public void onDocClick(DocumentModel document) {
        Utils.openFile(activityContext, document);

    }
}
