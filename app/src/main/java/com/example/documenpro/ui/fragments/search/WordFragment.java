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

import com.example.documenpro.GlobalConstant;
import com.example.documenpro.R;
import com.example.documenpro.adapter_reader.FileListAdapter;
import com.example.documenpro.clickListener.DocClickListener;
import com.example.documenpro.model.Document;
import com.example.documenpro.ui.customviews.EmptyRecyclerView;
import com.example.documenpro.utils.Utils;
import com.example.documenpro.viewmodel.SearchViewModel;

import java.util.ArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class WordFragment extends Fragment implements DocClickListener {
    private Activity mActivity;
    private EmptyRecyclerView recyclerView;
    private FileListAdapter adapter;
    private ArrayList<Document> arrayList;
    private SearchViewModel searchViewModel;
    private ProgressBar progressBar;
    private final Executor executor = Executors.newSingleThreadExecutor();

    public WordFragment(Activity mActivity) {
        this.mActivity = mActivity;
    }

    public WordFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recyclerview, container, false);
        initViews(view);
        searchViewModel = new ViewModelProvider(requireActivity()).get(SearchViewModel.class);
        listFile();
        return view;
    }

    private void listFile() {
//        arrayList = WordFileSingleton.getInstance().getWordFileList();
//        adapter = new ListFileAdapter(mActivity, arrayList, this);
//        recyclerView.setAdapter(adapter);
//        recyclerView.setVisibility(View.VISIBLE);
//        progressBar.setVisibility(View.GONE);
        executor.execute(new Runnable() {
            @Override
            public void run() {

                arrayList = Utils.countFile(mActivity, GlobalConstant.COUNT_WORD_FILE);

                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter = new FileListAdapter(mActivity, arrayList, WordFragment.this);
                        recyclerView.setAdapter(adapter);
                        recyclerView.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.GONE);
                    }
                });
            }
        });
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        searchViewModel.getQuery().observe(requireActivity(), s -> Utils.searchDocument(s, arrayList, adapter));
    }

    private void initViews(View view) {
        progressBar = view.findViewById(R.id.loadingView);
        recyclerView = view.findViewById(R.id.recyclerRecent);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mActivity);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setEmptyView(view.findViewById(R.id.empty_layout));
        arrayList = new ArrayList<>();


    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof Activity) {
            this.mActivity = (Activity) context;
        }
    }

    @Override
    public void onDocClick(Document document) {
        Utils.openFile(mActivity, document);
//        Utils.openFileWithAds(mActivity, document, 3);
//        FullAds.showAds(mActivity, () -> Utils.openFile(mActivity, document));

    }
}
