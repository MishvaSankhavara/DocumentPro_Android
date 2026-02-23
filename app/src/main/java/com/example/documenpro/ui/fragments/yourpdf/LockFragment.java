package com.example.documenpro.ui.fragments.yourpdf;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.airbnb.lottie.LottieAnimationView;
import com.example.documenpro.GlobalConstant;
import com.example.documenpro.R;
import com.example.documenpro.adapter_reader.CompactFileListAdapter;
import com.example.documenpro.clickListener.OnPdfTapListener;
import com.example.documenpro.model.PDFModel;
import com.docpro.scanner.result.ResultViewerActivity;
import com.example.documenpro.ui.customviews.EmptyRecyclerView;
import com.example.documenpro.utils.Utils;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class LockFragment extends Fragment implements OnPdfTapListener {
    private ResultViewerActivity mActivity;
    private LottieAnimationView loadingView;
    private ArrayList<PDFModel> arrayList;
    EmptyRecyclerView recyclerView;
    CompactFileListAdapter adapter;

    public LockFragment() {
    }

    public LockFragment(ResultViewerActivity mActivity) {
        this.mActivity = mActivity;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_created_pdf, container, false);

        initViews(view);

        new LoadFile(this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        return view;
    }

    private void initViews(View view) {
        loadingView = view.findViewById(R.id.loadingView);

        recyclerView = view.findViewById(R.id.recycler);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mActivity);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setEmptyView(view.findViewById(R.id.empty_layout));
        arrayList = new ArrayList<>();

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof ResultViewerActivity) {
            this.mActivity = (ResultViewerActivity) context;
        }
    }

    @Override
    public void onPdfTap(PDFModel pdfModel) {
        // PdfUtils.openPDF(mActivity, pdfModel);
        File file = new File(pdfModel.getAbsolutePath());
        Utils.openFile(mActivity, file);
    }

    private static class LoadFile extends AsyncTask<Void, Void, Void> {
        WeakReference<LockFragment> weakReference;

        public LoadFile(LockFragment lockFragment) {
            this.weakReference = new WeakReference<>(lockFragment);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            this.weakReference.get().arrayList = Utils.getCreatedPdf(GlobalConstant.RootDirectoryLockSaved);
            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
            weakReference.get().adapter = new CompactFileListAdapter(weakReference.get().mActivity,
                    weakReference.get().arrayList, weakReference.get());
            weakReference.get().recyclerView.setAdapter(weakReference.get().adapter);
            weakReference.get().loadingView.setVisibility(View.GONE);
        }
    }
}
