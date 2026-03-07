package com.example.documenpro.ui.fragments.yourpdf;

import com.docpro.scanner.result.ResultViewerActivity;

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
import com.example.documenpro.ui.customviews.EmptyStateRecyclerView;
import com.example.documenpro.utils.Utils;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class FragmentSplit extends Fragment implements OnPdfTapListener {

    private ResultViewerActivity activityContext;
    private LottieAnimationView loadingAnimationView;
    private ArrayList<PDFReaderModel> arrayList;
    EmptyStateRecyclerView splitPdfRecyclerView;
    CompactFileListAdapter splitPdfAdapter;

    public FragmentSplit() {
    }

    public FragmentSplit(ResultViewerActivity mActivity) {
        this.activityContext = mActivity;
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
        loadingAnimationView = view.findViewById(R.id.loadingView);

        splitPdfRecyclerView = view.findViewById(R.id.recycler);
        splitPdfRecyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(activityContext);
        splitPdfRecyclerView.setLayoutManager(layoutManager);
        splitPdfRecyclerView.setEmptyView(view.findViewById(R.id.empty_layout));
        arrayList = new ArrayList<>();

    }

    @Override
    public void onPdfTap(PDFReaderModel pdfModel) {
        // PdfUtils.openPDF(mActivity, pdfModel);
        File file = new File(pdfModel.getAbsolutePath_PDFModel());
        Utils.openFile(activityContext, file);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof ResultViewerActivity) {
            this.activityContext = (ResultViewerActivity) context;
        }
    }

    public static class LoadFile extends AsyncTask<Void, Void, Void> {
        WeakReference<FragmentSplit> weakReference;

        public LoadFile(FragmentSplit compressFragment) {
            this.weakReference = new WeakReference<>(compressFragment);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            this.weakReference.get().arrayList = Utils.getCreatedPdf(AppGlobalConstants.DIRECTORY_SPLIT_PDF_FILE);
            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
            weakReference.get().splitPdfAdapter = new CompactFileListAdapter(weakReference.get().activityContext,
                    weakReference.get().arrayList, weakReference.get());
            weakReference.get().splitPdfRecyclerView.setAdapter(weakReference.get().splitPdfAdapter);
            weakReference.get().loadingAnimationView.setVisibility(View.GONE);
        }
    }
}
