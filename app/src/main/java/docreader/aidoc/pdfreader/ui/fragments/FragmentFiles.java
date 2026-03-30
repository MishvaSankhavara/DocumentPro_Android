package docreader.aidoc.pdfreader.ui.fragments;

import static android.app.Activity.RESULT_OK;
import static docreader.aidoc.pdfreader.AppGlobalConstants.REQUEST_CODE_MANAGE_ALL_FILES;
import static docreader.aidoc.pdfreader.AppGlobalConstants.REQUEST_CODE_STORAGE_PERMISSION;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import docreader.aidoc.pdfreader.AppGlobalConstants;
import docreader.aidoc.pdfreader.R;
import docreader.aidoc.pdfreader.adapter_reader.PagerViewAdapter;
import docreader.aidoc.pdfreader.model_reader.DocumentModel;
import docreader.aidoc.pdfreader.ui.activities.MainActivity;
import docreader.aidoc.pdfreader.ui.activities.SelectDocumentActivity;
import docreader.aidoc.pdfreader.ui.customviews.DocumentTypeItemView;
import docreader.aidoc.pdfreader.ui.customviews.smartrefresh.api.SmartRefreshLayout;
import docreader.aidoc.pdfreader.ui.customviews.smartrefresh.header.ClassicRefreshHeaderView;
import docreader.aidoc.pdfreader.ui.customviews.smartrefresh.listener.RefreshListener;
import docreader.aidoc.pdfreader.utils.Utils;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class FragmentFiles extends Fragment implements View.OnClickListener {

    // Context
    private MainActivity activityContext;

    private LinearLayout permissionContainer;
    private DocumentTypeItemView pdfButton;
    private DocumentTypeItemView wordButton;
    private DocumentTypeItemView excelButton;
    private DocumentTypeItemView pptButton;
    ArrayList<DocumentModel> excelFile;
    ArrayList<DocumentModel> pdfFile;
    ArrayList<DocumentModel> wordFile;
    ArrayList<DocumentModel> pptFile;
    private int selectedTabPosition;
    private final Executor executor = Executors.newSingleThreadExecutor();

    public FragmentFiles() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_files, container, false);
        initViews(view);
        initAction(view);
        checkPermission();
        getData();
        return view;
    }

    private void initAction(View view) {
        view.findViewById(R.id.tv_go_to_set).setOnClickListener(this);
        pdfButton.setOnClickListener(this);
        wordButton.setOnClickListener(this);
        excelButton.setOnClickListener(this);
        pptButton.setOnClickListener(this);
        view.findViewById(R.id.cv_search_bar).setOnClickListener(this);
    }

    private void getData() {
        Intent intentData = activityContext.getIntent();
        if (intentData != null) {
            String st = intentData.getStringExtra(AppGlobalConstants.EXTRA_DATA_FROM_OUTSIDE);
            if (st != null) {
                Uri uri = Uri.parse(st);

                try {
                    if (uri.getScheme() != null) {
                        new SendIncomingDataTask(activityContext, uri)
                                .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                    } else {
                        if (uri.getPath() != null) {
                            File file = new File(uri.getPath());
                            Utils.openFile(activityContext, file);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } catch (Throwable throwable) {
                    throwable.addSuppressed(throwable);
                }

            }
        }
    }

    private void initViews(View view) {
        permissionContainer = view.findViewById(R.id.llPermission_container);
        view.findViewById(R.id.btnSelect).setOnClickListener(this);
        TabLayout tabLayout = view.findViewById(R.id.tab_layout);
        ViewPager viewPager = view.findViewById(R.id.vp_content);
        PagerViewAdapter adapter = new PagerViewAdapter(getChildFragmentManager());
        adapter.addFrag(new Fragment2Recent(activityContext), getResources().getString(R.string.app_recent));
        adapter.addFrag(new Fragment2Favorite(activityContext), getResources().getString(R.string.app_favorite));
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(3);
        tabLayout.setupWithViewPager(viewPager);
        viewPager.addOnPageChangeListener(activityContext);

        pdfButton = view.findViewById(R.id.btnPdf);
        wordButton = view.findViewById(R.id.btnWord);
        excelButton = view.findViewById(R.id.btnExcel);
        pptButton = view.findViewById(R.id.btnPpt);
        SmartRefreshLayout refreshLayout = view.findViewById(R.id.smartRefreshLayout);
        refreshLayout.setHeaderRefresh(new ClassicRefreshHeaderView(activityContext));
        refreshLayout.setRefreshListener(new RefreshListener() {
            @Override
            public void refresh(@NonNull SmartRefreshLayout refreshLayout) {
                countFiles();
                refreshLayout.completeRefresh(1);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_MANAGE_ALL_FILES) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                if (Environment.isExternalStorageManager()) {
                    permissionContainer.setVisibility(View.GONE);
                    countFiles();
                } else {
                    permissionContainer.setVisibility(View.VISIBLE);
                }
            }
        }
        if (requestCode == AppGlobalConstants.REQUEST_CODE_FILE_PICKER) {
            if (resultCode == RESULT_OK && data != null) {
                Uri selectedUri = data.getData();
                new SendIncomingDataTask(activityContext, selectedUri)
                        .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
            @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_STORAGE_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                permissionContainer.setVisibility(View.GONE);
                countFiles();
            } else {
                permissionContainer.setVisibility(View.VISIBLE);
            }
        }
    }

    private void countFiles() {
        executor.execute(() -> {
            excelFile = Utils.countFile(activityContext, AppGlobalConstants.QUERY_EXCEL_FILES);
            pdfFile = Utils.countFile(activityContext, AppGlobalConstants.QUERY_PDF_FILES);
            wordFile = Utils.countFile(activityContext, AppGlobalConstants.QUERY_WORD_FILES);
            pptFile = Utils.countFile(activityContext, AppGlobalConstants.QUERY_PPT_FILES);
            activityContext.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (isAdded() && getContext() != null) {
                        pdfButton.setTvCount(
                                getResources().getString(R.string.label_file_count, String.valueOf(pdfFile.size())));
                        excelButton.setTvCount(
                                getResources().getString(R.string.label_file_count, String.valueOf(excelFile.size())));
                        wordButton.setTvCount(
                                getResources().getString(R.string.label_file_count, String.valueOf(wordFile.size())));
                        pptButton.setTvCount(
                                getResources().getString(R.string.label_file_count, String.valueOf(pptFile.size())));
                    }
                }
            });
        });
    }

    public FragmentFiles(MainActivity activityContext) {
        this.activityContext = activityContext;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof Activity) {
            this.activityContext = (MainActivity) context;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (Utils.checkPermission(activityContext)) {
            permissionContainer.setVisibility(View.GONE);
            countFiles();
        } else {
            permissionContainer.setVisibility(View.VISIBLE);
        }
    }

    private void openFileList(int allFileType) {
        if (Utils.checkPermission(activityContext)) {
            Utils.openListFileActivity(activityContext, allFileType);
        } else {
            Toast.makeText(activityContext, getResources().getString(R.string.toast_permission_required),
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void checkPermission() {
        if (Utils.checkPermission(activityContext)) {
            permissionContainer.setVisibility(View.GONE);
            // LoadFile
            countFiles();
        } else {
            Utils.showPermissionDialog(activityContext);

            permissionContainer.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(View v) {
        int idView = v.getId();
        if (idView == R.id.tv_go_to_set) {
            Utils.askPermission(activityContext);
        } else if (idView == R.id.btnSelect) {
            if (selectedTabPosition == 0) {
                Intent intentAll = new Intent(activityContext, SelectDocumentActivity.class);
                intentAll.putExtra(AppGlobalConstants.EXTRA_FILE_TYPE, AppGlobalConstants.FILE_TYPE_RECENT);
                startActivity(intentAll);
            } else if (selectedTabPosition == 1) {
                Intent intentRecent = new Intent(activityContext, SelectDocumentActivity.class);
                intentRecent.putExtra(AppGlobalConstants.EXTRA_FILE_TYPE, AppGlobalConstants.FILE_TYPE_FAVORITE);
                startActivity(intentRecent);
            }
        } else if (idView == R.id.btnPdf) {
            openFileList(AppGlobalConstants.FILE_TYPE_PDF);
        } else if (idView == R.id.btnExcel) {
            openFileList(AppGlobalConstants.FILE_TYPE_EXCEL);
        } else if (idView == R.id.btnWord) {
            openFileList(AppGlobalConstants.FILE_TYPE_WORD);
        } else if (idView == R.id.btnPpt) {
            openFileList(AppGlobalConstants.FILE_TYPE_PPT);
        } else if (idView == R.id.cv_search_bar) {
            if (Utils.checkPermission(activityContext)) {
                Intent intentSearch = new Intent(activityContext,
                        docreader.aidoc.pdfreader.ui.activities.SearchDocumentActivity.class);
                startActivity(intentSearch);
            } else {
                Toast.makeText(activityContext, getResources().getString(R.string.toast_permission_required),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    public static class SendIncomingDataTask extends AsyncTask<Void, Void, Void> {
        WeakReference<MainActivity> weakReference;
        Intent intent;
        Uri intentData;

        public SendIncomingDataTask(MainActivity activity, Uri uri) {
            this.weakReference = new WeakReference<>(activity);
            this.intentData = uri;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            String filename = Utils.getFileNameFromUri(intentData, weakReference.get().getContentResolver());
            File pathFolder = new File(
                    Environment.getExternalStorageDirectory().getAbsolutePath() + "/Download/DocumentReader/");
            if (!pathFolder.exists()) {
                pathFolder.mkdirs();
            }
            String pathCopy = pathFolder + "/" + filename;
            Utils.copy(weakReference.get(), intentData, pathCopy);
            File file = new File(pathCopy);

            Utils.openFile(weakReference.get(), file);

            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
        }
    }

}
