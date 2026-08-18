package com.arkay.gkinhindi.ui.fragments;

import static android.app.Activity.RESULT_OK;
import static com.arkay.gkinhindi.Constants.REQUEST_CODE_MANAGE_ALL_FILES;
import static com.arkay.gkinhindi.Constants.REQUEST_CODE_STORAGE_PERMISSION;

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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.arkay.gkinhindi.BuildConfig;
import com.arkay.gkinhindi.Constants;
import com.arkay.gkinhindi.R;
import com.arkay.gkinhindi.clickListener.DocClickListener;
import com.arkay.gkinhindi.model_reader.DocumentModel;
import com.arkay.gkinhindi.ui.activities.MainActivity;
import com.arkay.gkinhindi.ui.activities.SelectDocumentActivity;
import com.arkay.gkinhindi.ui.customviews.DocumentTypeItemView;
import com.arkay.gkinhindi.ui.customviews.smartrefresh.api.SmartRefreshLayout;
import com.arkay.gkinhindi.ui.customviews.smartrefresh.header.ClassicRefreshHeaderView;
import com.arkay.gkinhindi.ui.customviews.smartrefresh.listener.RefreshListener;
import com.arkay.gkinhindi.adapter_reader.RecentFilesAdapter;
import com.arkay.gkinhindi.adapter_reader.FavoriteCardItemsAdapter;
import com.arkay.gkinhindi.utils.AdsUtils;
import com.arkay.gkinhindi.viewmodel.DataSingletonRecent;
import com.arkay.gkinhindi.viewmodel.DataSingletonFavorite;
import com.arkay.gkinhindi.utils.Utils;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class FragmentFiles extends Fragment implements View.OnClickListener {

    // Context
    private MainActivity activityContext;

    private LinearLayout permissionContainer;
    private DocumentTypeItemView allButton;
    private DocumentTypeItemView pdfButton;
    private DocumentTypeItemView wordButton;
    private DocumentTypeItemView excelButton;
    private DocumentTypeItemView pptButton;
    private DocumentTypeItemView txtButton;
    
    private RecyclerView recentRecyclerView;
    private RecyclerView favoriteRecyclerView;
    private RecentFilesAdapter recentFilesAdapter;
    private FavoriteCardItemsAdapter favoriteCardAdapter;
    
    ArrayList<DocumentModel> excelFile;
    ArrayList<DocumentModel> pdfFile;
    ArrayList<DocumentModel> wordFile;
    ArrayList<DocumentModel> pptFile;
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
        allButton.setOnClickListener(this);
        pdfButton.setOnClickListener(this);
        wordButton.setOnClickListener(this);
        excelButton.setOnClickListener(this);
        pptButton.setOnClickListener(this);
        txtButton.setOnClickListener(this);
        view.findViewById(R.id.cv_search_bar).setOnClickListener(this);
        
        view.findViewById(R.id.btn_recent_view_all).setOnClickListener(this);
        view.findViewById(R.id.btn_favorite_view_all).setOnClickListener(this);
    }

    private void getData() {
        Intent intentData = activityContext.getIntent();
        if (intentData != null) {
            String st = intentData.getStringExtra(Constants.EXTRA_DATA_FROM_OUTSIDE);
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
        
        recentRecyclerView = view.findViewById(R.id.recyclerRecentHome);
        recentRecyclerView.setLayoutManager(new LinearLayoutManager(activityContext) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        });
        recentRecyclerView.setHasFixedSize(true);
        recentFilesAdapter = new RecentFilesAdapter(activityContext, new DocClickListener() {
            @Override
            public void onDocClick(DocumentModel document) {
                AdsUtils.showInterstitialAdFunction(
                        activityContext,
                        BuildConfig.interstitial_function,
                        BuildConfig.interstitial_function_2ID,
                        Constants.interstitial_function,
                        Constants.interstitial_function_2ID,
                        () -> Utils.openFile(activityContext, document)
                );
            }
        }, true);
        recentRecyclerView.setAdapter(recentFilesAdapter);

        favoriteRecyclerView = view.findViewById(R.id.recyclerFavoriteHome);
        favoriteRecyclerView.setLayoutManager(new LinearLayoutManager(activityContext, LinearLayoutManager.HORIZONTAL, false) {
            @Override
            public boolean canScrollHorizontally() {
                return true;
            }
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        });
        favoriteRecyclerView.setHasFixedSize(false);
        favoriteCardAdapter = new FavoriteCardItemsAdapter(activityContext, new DocClickListener() {
            @Override
            public void onDocClick(DocumentModel document) {
                AdsUtils.showInterstitialAdFunction(
                        activityContext,
                        BuildConfig.interstitial_function,
                        BuildConfig.interstitial_function_2ID,
                        Constants.interstitial_function,
                        Constants.interstitial_function_2ID,
                        () -> Utils.openFile(activityContext, document)
                );
            }
        });
        favoriteRecyclerView.setAdapter(favoriteCardAdapter);

        DataSingletonRecent.getInstance().getRecentDocumentsLiveData().observe(getViewLifecycleOwner(),
                documents -> {
                    if (recentFilesAdapter != null) {
                        recentFilesAdapter.refreshData();
                        View recentCard = view.findViewById(R.id.card_recent_container);
                        View recentEmpty = view.findViewById(R.id.recent_empty_layout);
                        if (documents == null || documents.isEmpty()) {
                            recentCard.setVisibility(View.GONE);
                            recentEmpty.setVisibility(View.VISIBLE);
                        } else {
                            recentCard.setVisibility(View.VISIBLE);
                            recentEmpty.setVisibility(View.GONE);
                        }
                    }
                });

        DataSingletonFavorite.getInstance().getFavoriteDocumentsLiveData().observe(getViewLifecycleOwner(),
                documents -> {
                    if (favoriteCardAdapter != null) {
                        favoriteCardAdapter.refreshData();
                        View favoriteEmpty = view.findViewById(R.id.favorite_empty_layout);
                        if (documents == null || documents.isEmpty()) {
                            favoriteRecyclerView.setVisibility(View.GONE);
                            favoriteEmpty.setVisibility(View.VISIBLE);
                        } else {
                            favoriteRecyclerView.setVisibility(View.VISIBLE);
                            favoriteEmpty.setVisibility(View.GONE);
                        }
                    }
                });

        allButton = view.findViewById(R.id.btnAll);
        pdfButton = view.findViewById(R.id.btnPdf);
        wordButton = view.findViewById(R.id.btnWord);
        excelButton = view.findViewById(R.id.btnExcel);
        pptButton = view.findViewById(R.id.btnPpt);
        txtButton = view.findViewById(R.id.btnTxt);
        
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
        if (requestCode == Constants.REQUEST_CODE_FILE_PICKER) {
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
            excelFile = Utils.countFile(activityContext, Constants.QUERY_EXCEL_FILES);
            pdfFile = Utils.countFile(activityContext, Constants.QUERY_PDF_FILES);
            wordFile = Utils.countFile(activityContext, Constants.QUERY_WORD_FILES);
            pptFile = Utils.countFile(activityContext, Constants.QUERY_PPT_FILES);
            ArrayList<DocumentModel> txtFile = Utils.countFile(activityContext, Constants.QUERY_TEXT_FILES);
            
            int excelCount = (excelFile != null) ? excelFile.size() : 0;
            int pdfCount = (pdfFile != null) ? pdfFile.size() : 0;
            int wordCount = (wordFile != null) ? wordFile.size() : 0;
            int pptCount = (pptFile != null) ? pptFile.size() : 0;
            int txtCount = (txtFile != null) ? txtFile.size() : 0;
            int allCount = excelCount + pdfCount + wordCount + pptCount + txtCount;
            
            activityContext.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (isAdded() && getContext() != null) {
                        allButton.setTvCount(String.valueOf(allCount));
                        pdfButton.setTvCount(String.valueOf(pdfCount));
                        excelButton.setTvCount(String.valueOf(excelCount));
                        wordButton.setTvCount(String.valueOf(wordCount));
                        pptButton.setTvCount(String.valueOf(pptCount));
                        txtButton.setTvCount(String.valueOf(txtCount));
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
            Utils.dismissPermissionDialog();
            permissionContainer.setVisibility(View.GONE);
            countFiles();
            if (recentFilesAdapter != null) {
                recentFilesAdapter.refreshData();
            }
            if (favoriteCardAdapter != null) {
                favoriteCardAdapter.refreshData();
            }
        } else {
            permissionContainer.setVisibility(View.VISIBLE);
        }
    }

    private void openFileList(int allFileType) {
        if (Utils.checkPermission(activityContext)) {
            AdsUtils.showInterstitialAdFunction(
                    activityContext,
                    BuildConfig.interstitial_function,
                    BuildConfig.interstitial_function_2ID,
                    Constants.interstitial_function,
                    Constants.interstitial_function_2ID,
                    () -> Utils.openListFileActivity(activityContext, allFileType)
            );
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
        } else if (idView == R.id.btn_recent_view_all) {
            if (Utils.checkPermission(activityContext)) {
                AdsUtils.showInterstitialAdFunction(
                        activityContext,
                        BuildConfig.interstitial_function,
                        BuildConfig.interstitial_function_2ID,
                        Constants.interstitial_function,
                        Constants.interstitial_function_2ID,
                        () -> {
                            Intent intentRecent = new Intent(activityContext, SelectDocumentActivity.class);
                            intentRecent.putExtra(Constants.EXTRA_FILE_TYPE, Constants.FILE_TYPE_RECENT);
                            startActivity(intentRecent);
                        }
                );
            } else {
                Toast.makeText(activityContext, getResources().getString(R.string.toast_permission_required),
                        Toast.LENGTH_SHORT).show();
            }
        } else if (idView == R.id.btn_favorite_view_all) {
            if (Utils.checkPermission(activityContext)) {
                AdsUtils.showInterstitialAdFunction(
                        activityContext,
                        BuildConfig.interstitial_function,
                        BuildConfig.interstitial_function_2ID,
                        Constants.interstitial_function,
                        Constants.interstitial_function_2ID,
                        () -> {
                            Intent intentFavorite = new Intent(activityContext, SelectDocumentActivity.class);
                            intentFavorite.putExtra(Constants.EXTRA_FILE_TYPE, Constants.FILE_TYPE_FAVORITE);
                            startActivity(intentFavorite);
                        }
                );
            } else {
                Toast.makeText(activityContext, getResources().getString(R.string.toast_permission_required),
                        Toast.LENGTH_SHORT).show();
            }
        } else if (idView == R.id.btnAll) {
            openFileList(Constants.FILE_TYPE_ALL);
        } else if (idView == R.id.btnPdf) {
            openFileList(Constants.FILE_TYPE_PDF);
        } else if (idView == R.id.btnExcel) {
            openFileList(Constants.FILE_TYPE_EXCEL);
        } else if (idView == R.id.btnWord) {
            openFileList(Constants.FILE_TYPE_WORD);
        } else if (idView == R.id.btnPpt) {
            openFileList(Constants.FILE_TYPE_PPT);
        } else if (idView == R.id.btnTxt) {
            openFileList(Constants.FILE_TYPE_TEXT);
        } else if (idView == R.id.cv_search_bar) {
            if (Utils.checkPermission(activityContext)) {
                Intent intentSearch = new Intent(activityContext,
                        com.arkay.gkinhindi.ui.activities.SearchDocumentActivity.class);
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
