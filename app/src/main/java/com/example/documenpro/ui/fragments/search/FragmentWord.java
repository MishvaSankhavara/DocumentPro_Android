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

public class FragmentWord extends Fragment implements DocClickListener {
    private Activity activityContext;
    private EmptyStateRecyclerView wordRecyclerView;
    private FileListAdapter wordAdapter;
    private ArrayList<DocumentModel> arrayList;
    private ViewModelSearch searchViewModel;
    private ProgressBar loadingProgressBar;
    private final Executor backgroundExecutor = Executors.newSingleThreadExecutor();

    public FragmentWord(Activity mActivity) {
        this.activityContext = mActivity;
    }

    public FragmentWord() {
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
//        arrayList = WordFileSingleton.getInstance().getWordFileList();
//        adapter = new ListFileAdapter(mActivity, arrayList, this);
//        recyclerView.setAdapter(adapter);
//        recyclerView.setVisibility(View.VISIBLE);
//        progressBar.setVisibility(View.GONE);
        backgroundExecutor.execute(new Runnable() {
            @Override
            public void run() {

                arrayList = Utils.countFile(activityContext, AppGlobalConstants.QUERY_WORD_FILES);

                activityContext.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        wordAdapter = new FileListAdapter(activityContext, arrayList, FragmentWord.this);
                        wordRecyclerView.setAdapter(wordAdapter);
                        wordRecyclerView.setVisibility(View.VISIBLE);
                        loadingProgressBar.setVisibility(View.GONE);
                    }
                });
            }
        });
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        searchViewModel.getSearchQueryLiveData().observe(requireActivity(), s -> Utils.searchDocument(s, arrayList, wordAdapter));
    }

    private void initViews(View view) {
        loadingProgressBar = view.findViewById(R.id.loadingView);
        wordRecyclerView = view.findViewById(R.id.recyclerRecent);
        wordRecyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(activityContext);
        wordRecyclerView.setLayoutManager(layoutManager);
        wordRecyclerView.setEmptyView(view.findViewById(R.id.empty_layout));
        arrayList = new ArrayList<>();


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
//        Utils.openFileWithAds(mActivity, document, 3);
//        FullAds.showAds(mActivity, () -> Utils.openFile(mActivity, document));

    }
}
