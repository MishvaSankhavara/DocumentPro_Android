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
import com.example.documenpro.adapter_reader.FavoriteItemsAdapter;
import com.example.documenpro.advertisement.AdManager;
import com.example.documenpro.clickListener.DocClickListener;
import com.example.documenpro.model_reader.DocumentModel;
import com.example.documenpro.ui.activities.MainActivity;
import com.example.documenpro.ui.customviews.EmptyStateRecyclerView;
import com.example.documenpro.utils.Utils;
import com.example.documenpro.viewmodel.DataSingletonFavorite;

import java.util.ArrayList;

public class Fragment2Favorite extends Fragment implements DocClickListener {
    private MainActivity activityContext;
    private TextView emptyDescriptionText;
    private ProgressBar loadingProgressBar;
    private EmptyStateRecyclerView favoriteRecyclerView;

    FavoriteItemsAdapter favoriteAdapter;

    public Fragment2Favorite() {
    }

    public Fragment2Favorite(MainActivity mActivity) {
        this.activityContext = mActivity;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recyclerview, container, false);
        initViews(view);

        DataSingletonFavorite.getInstance().getFavoriteDocumentsLiveData().observe(getViewLifecycleOwner(), new Observer<ArrayList<DocumentModel>>() {
            @Override
            public void onChanged(ArrayList<DocumentModel> documents) {

                favoriteAdapter = new FavoriteItemsAdapter(activityContext, Fragment2Favorite.this);
                favoriteRecyclerView.setAdapter(favoriteAdapter);
            }
        });
        return view;
    }

    private void initViews(View view) {
        favoriteRecyclerView = view.findViewById(R.id.recyclerRecent);
        favoriteRecyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(activityContext);
        favoriteRecyclerView.setLayoutManager(layoutManager);
        favoriteRecyclerView.setEmptyView(view.findViewById(R.id.empty_layout));
        emptyDescriptionText = view.findViewById(R.id.tv_empty_desc);
        loadingProgressBar = view.findViewById(R.id.loadingView);
        emptyDescriptionText.setText(activityContext.getResources().getString(R.string.img_add_to_favorites));
        favoriteAdapter = new FavoriteItemsAdapter(activityContext, this);
        favoriteRecyclerView.setAdapter(favoriteAdapter);
        loadingProgressBar.setVisibility(View.GONE);
    }

    @Override
    public void onResume() {
        super.onResume();
        favoriteAdapter = new FavoriteItemsAdapter(activityContext, this);
        favoriteRecyclerView.setAdapter(favoriteAdapter);
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
        AdManager.showAds_AdManager(activityContext, () -> Utils.openFile(activityContext, document));

    }

}
