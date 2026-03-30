package docreader.aidoc.pdfreader.ui.fragments.yourpdf;

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
import docreader.aidoc.pdfreader.AppGlobalConstants;
import docreader.aidoc.pdfreader.R;
import docreader.aidoc.pdfreader.adapter_reader.ImageItemAdapter;
import docreader.aidoc.pdfreader.ui.customviews.EmptyStateRecyclerView;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;

public class FragmentImage extends Fragment {
    private Activity activityContext;
    private LottieAnimationView loadingAnimationView;
    private ArrayList<File> arrayList;
    EmptyStateRecyclerView imageRecyclerView;
    ImageItemAdapter adapter;

    public FragmentImage() {
    }

    public FragmentImage(Activity mActivity) {
        this.activityContext = mActivity;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_created_pdf, container, false);
        initViews(view);
        new LoadImagesTask(this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        return view;
    }

    private void initViews(View view) {
        loadingAnimationView = view.findViewById(R.id.loadingView);
        imageRecyclerView = view.findViewById(R.id.recycler);
        imageRecyclerView.setHasFixedSize(true);
        imageRecyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 2, RecyclerView.VERTICAL, false));
        imageRecyclerView.setEmptyView(view.findViewById(R.id.empty_layout));
        arrayList = new ArrayList<>();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof Activity) {
            this.activityContext = (Activity) context;
        }
    }

    public static class LoadImagesTask extends AsyncTask<Void, Void, Void> {
        WeakReference<FragmentImage> weakReference;

        public LoadImagesTask(FragmentImage imageFragment) {
            this.weakReference = new WeakReference<>(imageFragment);
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
            FragmentImage f = weakReference.get();
            if (f == null || !f.isAdded())
                return;
            if (f.adapter != null) {
                f.imageRecyclerView.setAdapter(f.adapter);
            }
            f.loadingAnimationView.setVisibility(View.GONE);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            File[] files = AppGlobalConstants.DIRECTORY_SAVED_IMAGES.listFiles();
            if (files != null) {
                Collections.addAll(weakReference.get().arrayList, files);
                weakReference.get().adapter = new ImageItemAdapter(weakReference.get().activityContext,
                        weakReference.get().arrayList);
            }
            return null;
        }
    }
}
