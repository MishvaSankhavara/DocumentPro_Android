package docreader.aidoc.pdfreader.ui.fragments.yourpdf;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.graphics.drawable.ColorDrawable;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.airbnb.lottie.LottieAnimationView;
import docreader.aidoc.pdfreader.AppGlobalConstants;
import docreader.aidoc.pdfreader.R;
import docreader.aidoc.pdfreader.adapter_reader.CompactFileListAdapter;
import docreader.aidoc.pdfreader.clickListener.OnPdfTapListener;
import docreader.aidoc.pdfreader.clickListener.PasswordClickListener;
import docreader.aidoc.pdfreader.model_reader.PDFReaderModel;
import com.docpro.scanner.result.ResultViewerActivity;
import docreader.aidoc.pdfreader.ui.customviews.EmptyStateRecyclerView;
import docreader.aidoc.pdfreader.ui.dialog.AppLoadingDialog;
import docreader.aidoc.pdfreader.ui.dialog.PasswordSetupDialog;
import docreader.aidoc.pdfreader.utils.Utils;

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
        if (activityContext == null) {
            return;
        }

        final PasswordSetupDialog[] dialogHolder = new PasswordSetupDialog[1];
        dialogHolder[0] = new PasswordSetupDialog(activityContext, new PasswordClickListener() {
            @Override
            public void onOkClickListener(String password) {
                AppLoadingDialog loadingDialog = new AppLoadingDialog(activityContext);
                Window window = loadingDialog.getWindow();
                if (window != null) {
                    window.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                    window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                }
                loadingDialog.show();

                AsyncTask.THREAD_POOL_EXECUTOR.execute(() -> {
                    try {
                        PDFReaderModel unlockedModel = Utils.unlockPdfFile(pdfModel, password);
                        activityContext.runOnUiThread(() -> {
                            loadingDialog.dismiss();
                            if (unlockedModel != null) {
                                if (dialogHolder[0] != null) dialogHolder[0].dismiss();
                                File unlockedFile = new File(unlockedModel.getAbsolutePath_PDFModel());
                                Utils.openFile(activityContext, unlockedFile);
                            } else {
                                if (dialogHolder[0] != null) dialogHolder[0].showError(activityContext.getString(R.string.pwd_error));
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                        activityContext.runOnUiThread(() -> {
                            loadingDialog.dismiss();
                            if (dialogHolder[0] != null) dialogHolder[0].showError(activityContext.getString(R.string.pwd_error));
                        });
                    }
                });
            }
        });
        dialogHolder[0].setDialogMode(true);
        Window window = dialogHolder[0].getWindow();
        if (window != null) {
            window.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        dialogHolder[0].show();
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
