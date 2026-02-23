package com.example.documenpro.ui.fragments.yourpdf;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.example.documenpro.GlobalConstant;
import com.example.documenpro.R;
import com.example.documenpro.adapter.ImageAdapter;
import com.example.documenpro.ui.customviews.EmptyRecyclerView;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
public class ImageFragment extends Fragment {
    private Activity mActivity;
    private LottieAnimationView loadingView;
    private ArrayList<File> arrayList;
    EmptyRecyclerView recyclerView;
    ImageAdapter adapter;

    public ImageFragment() {
    }

    public ImageFragment(Activity mActivity) {
        this.mActivity = mActivity;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_created_pdf, container, false);
        initViews(view);
        new LoadImage(this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        return view;
    }

    private void initViews(View view) {
        loadingView = view.findViewById(R.id.loadingView);
        recyclerView = view.findViewById(R.id.recycler);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(mActivity, 2, RecyclerView.VERTICAL, false);
        recyclerView.setLayoutManager(mLayoutManager);
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

    public static class LoadImage extends AsyncTask<Void, Void, Void> {
        WeakReference<ImageFragment> weakReference;

        public LoadImage(ImageFragment imageFragment) {
            this.weakReference = new WeakReference<>(imageFragment);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            File[] files = GlobalConstant.RootDirectoryImageSaved.listFiles();
            if (files != null) {
                Collections.addAll(weakReference.get().arrayList, files);
                weakReference.get().adapter = new ImageAdapter(weakReference.get().mActivity, weakReference.get().arrayList);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
            weakReference.get().recyclerView.setAdapter(weakReference.get().adapter);
            weakReference.get().loadingView.setVisibility(View.GONE);
        }
    }
}
