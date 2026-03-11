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

public class FragmentLock extends Fragment implements OnPdfTapListener {
    private ResultViewerActivity activityContext;
    private LottieAnimationView loadingAnimationView;
    EmptyStateRecyclerView pdfRecyclerView;
    CompactFileListAdapter pdfAdapter;
    private ArrayList<PDFReaderModel> arrayList;

    public FragmentLock() {
    }

    public FragmentLock(ResultViewerActivity mActivity) {
        this.activityContext = mActivity;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_created_pdf, container, false);

        initViews(view);

        new LoadLockedPdfFilesTask(this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        return view;
    }

    private void initViews(View view) {
        loadingAnimationView = view.findViewById(R.id.loadingView);
        pdfRecyclerView = view.findViewById(R.id.recycler);
        pdfRecyclerView.setHasFixedSize(true);
        pdfRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        pdfRecyclerView.setEmptyView(view.findViewById(R.id.empty_layout));
        arrayList = new ArrayList<>();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof ResultViewerActivity) {
            this.activityContext = (ResultViewerActivity) context;
        }
    }

    @Override
    public void onPdfTap(PDFReaderModel pdfModel) {
        // PdfUtils.openPDF(mActivity, pdfModel);
        File file = new File(pdfModel.getAbsolutePath_PDFModel());
        Utils.openFile(activityContext, file);
    }

    private static class LoadLockedPdfFilesTask extends AsyncTask<Void, Void, Void> {
        WeakReference<FragmentLock> weakReference;

        public LoadLockedPdfFilesTask(FragmentLock lockFragment) {
            this.weakReference = new WeakReference<>(lockFragment);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            this.weakReference.get().arrayList = Utils.getCreatedPdf(AppGlobalConstants.DIRECTORY_LOCKED_PDF_FILE);
            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
            FragmentLock f = weakReference.get();
            if (f == null || !f.isAdded() || f.activityContext == null)
                return;
            f.pdfAdapter = new CompactFileListAdapter(f.activityContext, f.arrayList, f);
            f.pdfRecyclerView.setAdapter(f.pdfAdapter);
            f.loadingAnimationView.setVisibility(View.GONE);
        }
    }
}
