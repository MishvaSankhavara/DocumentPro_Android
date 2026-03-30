package docreader.aidoc.pdfreader.ui.fragments;

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

import docreader.aidoc.pdfreader.R;
import docreader.aidoc.pdfreader.adapter_reader.RecentFilesAdapter;

import docreader.aidoc.pdfreader.clickListener.DocClickListener;
import docreader.aidoc.pdfreader.model_reader.DocumentModel;
import docreader.aidoc.pdfreader.ui.activities.MainActivity;
import docreader.aidoc.pdfreader.ui.customviews.EmptyStateRecyclerView;
import docreader.aidoc.pdfreader.utils.Utils;
import docreader.aidoc.pdfreader.viewmodel.DataSingletonRecent;

import java.util.ArrayList;

public class Fragment2Recent extends Fragment implements DocClickListener {
    private MainActivity activityContext;
    private EmptyStateRecyclerView recentRecyclerView;
    private TextView emptyDescriptionText;
    private ProgressBar loadingProgressBar;
    RecentFilesAdapter recentFilesAdapter;

    public Fragment2Recent() {
    }

    public Fragment2Recent(MainActivity mActivity) {
        this.activityContext = mActivity;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_recyclerview, container, false);
        initViews(view);
        DataSingletonRecent.getInstance().getRecentDocumentsLiveData().observe(getViewLifecycleOwner(),
                new Observer<ArrayList<DocumentModel>>() {
                    @Override
                    public void onChanged(ArrayList<DocumentModel> documents) {
                        if (recentFilesAdapter == null) {
                            recentFilesAdapter = new RecentFilesAdapter(activityContext, Fragment2Recent.this);
                            recentRecyclerView.setAdapter(recentFilesAdapter);
                        } else {
                            recentFilesAdapter.refreshData();
                        }
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
        emptyDescriptionText.setText(activityContext.getResources().getString(R.string.message_recent_files_empty));
        recentFilesAdapter = new RecentFilesAdapter(activityContext, this);
        recentRecyclerView.setAdapter(recentFilesAdapter);
        loadingProgressBar.setVisibility(View.GONE);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (recentFilesAdapter != null) {
            recentFilesAdapter.refreshData();
        }
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