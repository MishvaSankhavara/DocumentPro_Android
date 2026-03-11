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
import androidx.recyclerview.widget.LinearLayoutManager;

import com.airbnb.lottie.LottieAnimationView;
import com.example.documenpro.AppGlobalConstants;
import com.example.documenpro.R;
import com.example.documenpro.adapter_reader.CompactFileListAdapter;
import com.example.documenpro.clickListener.OnPdfTapListener;
import com.example.documenpro.model_reader.PDFReaderModel;
import com.docpro.scanner.result.ResultViewerActivity;
import com.example.documenpro.ui.customviews.EmptyStateRecyclerView;
import com.example.documenpro.utils.Utils;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class FragmentMerge extends Fragment implements OnPdfTapListener {

    private ResultViewerActivity activityContext;

    private LottieAnimationView loadingAnimationView;
    private ArrayList<PDFReaderModel> arrayList;
    EmptyStateRecyclerView mergedPdfRecyclerView;
    CompactFileListAdapter mergedPdfAdapter;

    public FragmentMerge() {
    }

    public FragmentMerge(ResultViewerActivity mActivity) {
        this.activityContext = mActivity;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_created_pdf, container, false);
        initViews(view);
        new LoadMergedPdfFilesTask(this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        return view;
    }

    private void initViews(View view) {
        loadingAnimationView = view.findViewById(R.id.loadingView);
        mergedPdfRecyclerView = view.findViewById(R.id.recycler);
        mergedPdfRecyclerView.setHasFixedSize(true);
        mergedPdfRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        mergedPdfRecyclerView.setEmptyView(view.findViewById(R.id.empty_layout));
        arrayList = new ArrayList<>();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof Activity) {
            this.activityContext = (ResultViewerActivity) context;
        }
    }

    @Override
    public void onPdfTap(PDFReaderModel pdfModel) {
        // PdfUtils.openPDF(mActivity, pdfModel);
        File file = new File(pdfModel.getAbsolutePath_PDFModel());
        Utils.openFile(activityContext, file);
    }

    private static class LoadMergedPdfFilesTask extends AsyncTask<Void, Void, Void> {
        WeakReference<FragmentMerge> weakReference;

        public LoadMergedPdfFilesTask(FragmentMerge mergeFragment) {
            this.weakReference = new WeakReference<>(mergeFragment);

        }

        @Override
        protected Void doInBackground(Void... voids) {
            this.weakReference.get().arrayList = Utils.getCreatedPdf(AppGlobalConstants.DIRECTORY_MERGED_PDF_FILE);

            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
            FragmentMerge f = weakReference.get();
            if (f == null || !f.isAdded() || f.activityContext == null)
                return;
            f.mergedPdfAdapter = new CompactFileListAdapter(f.activityContext, f.arrayList, f);
            f.mergedPdfRecyclerView.setAdapter(f.mergedPdfAdapter);
            f.loadingAnimationView.setVisibility(View.GONE);
        }
    }
}
