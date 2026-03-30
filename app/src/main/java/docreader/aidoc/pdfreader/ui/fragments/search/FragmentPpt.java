package docreader.aidoc.pdfreader.ui.fragments.search;

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

import docreader.aidoc.pdfreader.AppGlobalConstants;
import docreader.aidoc.pdfreader.R;
import docreader.aidoc.pdfreader.adapter_reader.FileListAdapter;
import docreader.aidoc.pdfreader.clickListener.DocClickListener;
import docreader.aidoc.pdfreader.model_reader.DocumentModel;
import docreader.aidoc.pdfreader.ui.customviews.EmptyStateRecyclerView;
import docreader.aidoc.pdfreader.utils.Utils;
import docreader.aidoc.pdfreader.viewmodel.ViewModelSearch;

import java.util.ArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class FragmentPpt extends Fragment implements DocClickListener {

    private Activity activityContext;
    private ViewModelSearch searchViewModel;
    private EmptyStateRecyclerView pptRecyclerView;
    private ProgressBar loadingProgressBar;
    private FileListAdapter pptAdapter;
    private ArrayList<DocumentModel> arrayList;
    private final Executor backgroundExecutor = Executors.newSingleThreadExecutor();

    public FragmentPpt(Activity mActivity) {
        this.activityContext = mActivity;
    }

    public FragmentPpt() {
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        searchViewModel.getSearchQueryLiveData().observe(requireActivity(), s -> Utils.searchDocument(s, arrayList, pptAdapter));
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_recyclerview, container, false);
        initViews(view);
        searchViewModel = new ViewModelProvider(requireActivity()).get(ViewModelSearch.class);
        listFile();
        return view;
    }

    private void initViews(View view) {
        loadingProgressBar = view.findViewById(R.id.loadingView);
        pptRecyclerView = view.findViewById(R.id.recyclerRecent);
        pptRecyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(activityContext);
        pptRecyclerView.setLayoutManager(layoutManager);
        pptRecyclerView.setEmptyView(view.findViewById(R.id.empty_layout));
        arrayList = new ArrayList<>();


    }

    private void listFile() {
        backgroundExecutor.execute(new Runnable() {
            @Override
            public void run() {

                arrayList = Utils.countFile(activityContext, AppGlobalConstants.QUERY_PPT_FILES);

                activityContext.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        pptAdapter = new FileListAdapter(activityContext, arrayList, FragmentPpt.this);
                        pptRecyclerView.setAdapter(pptAdapter);
                        pptRecyclerView.setVisibility(View.VISIBLE);
                        loadingProgressBar.setVisibility(View.GONE);
                    }
                });
            }
        });
    }

    @Override
    public void onDocClick(DocumentModel document) {
        Utils.openFile(activityContext, document);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof Activity) {
            this.activityContext = (Activity) context;
        }
    }
}
