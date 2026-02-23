package com.example.documenpro.ui.fragments;

import static android.app.Activity.RESULT_OK;
import static com.example.documenpro.GlobalConstant.REQUEST_MANAGE_ALL_FILES_PERMISSION;
import static com.example.documenpro.GlobalConstant.REQUEST_STORAGE_PERMISSION;

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
import com.example.documenpro.GlobalConstant;
import com.example.documenpro.R;
import com.example.documenpro.adapter.ViewPagerAdapter;
import com.example.documenpro.model.Document;
import com.example.documenpro.ui.activities.MainActivity;
import com.example.documenpro.ui.activities.SelectActivity;
import com.example.documenpro.ui.customviews.FileTypeItem;
import com.example.documenpro.ui.customviews.smartrefresh.api.RefreshLayout;
import com.example.documenpro.ui.customviews.smartrefresh.header.ClassicsHeader;
import com.example.documenpro.ui.customviews.smartrefresh.listener.OnRefreshListener;
import com.example.documenpro.utils.Utils;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class FilesFragment extends Fragment implements View.OnClickListener {

    private MainActivity mActivity;
    private LinearLayout llPermissionContainer;
    private FileTypeItem btnTXT;
    private FileTypeItem btnPdf;
    private FileTypeItem btnWord;
    private FileTypeItem btnExcel;
    private FileTypeItem btnPpt;
    ArrayList<Document> txtFile;
    ArrayList<Document> excelFile;
    ArrayList<Document> pdfFile;
    ArrayList<Document> wordFile;
    ArrayList<Document> pptFile;
    private int mPosition;
    private final Executor executor = Executors.newSingleThreadExecutor();

    public FilesFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_files, container, false);
        initViews(view);
        initAction(view);
        checkPermission();
        getData();
        return view;
    }

    private void initAction(View view) {
        view.findViewById(R.id.tv_go_to_set).setOnClickListener(this);
        btnTXT.setOnClickListener(this);
        btnPdf.setOnClickListener(this);
        btnWord.setOnClickListener(this);
        btnExcel.setOnClickListener(this);
        btnPpt.setOnClickListener(this);
    }

    private void getData() {
        Intent intentData = mActivity.getIntent();
        if (intentData != null) {
            String st = intentData.getStringExtra(GlobalConstant.KEY_DATA_FROM_OUTSIDE);
            if (st != null) {
                Uri uri = Uri.parse(st);

                try {
                    if (uri.getScheme() != null) {
                        new SendData(mActivity, uri).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                    } else {
                        if (uri.getPath() != null) {
                            File file = new File(uri.getPath());
                            Utils.openFile(mActivity, file);
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
        llPermissionContainer = view.findViewById(R.id.llPermission_container);
        view.findViewById(R.id.btnSelect).setOnClickListener(this);
        TabLayout tabLayout = view.findViewById(R.id.tab_layout);
        ViewPager viewPager = view.findViewById(R.id.vp_content);
        ViewPagerAdapter adapter = new ViewPagerAdapter(mActivity.getSupportFragmentManager());
        adapter.addFrag(new RecentFragment2(mActivity), getResources().getString(R.string.str_recent));
        adapter.addFrag(new FavoriteFragment2(mActivity), getResources().getString(R.string.str_favorite));
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(3);
        tabLayout.setupWithViewPager(viewPager);
        viewPager.addOnPageChangeListener(mActivity);

        btnTXT = view.findViewById(R.id.btnTXT);
        btnPdf = view.findViewById(R.id.btnPdf);
        btnWord = view.findViewById(R.id.btnWord);
        btnExcel = view.findViewById(R.id.btnExcel);
        btnPpt = view.findViewById(R.id.btnPpt);
        RefreshLayout refreshLayout = view.findViewById(R.id.smartRefreshLayout);
        refreshLayout.setRefreshHeader(new ClassicsHeader(mActivity));
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                countFile();
                refreshLayout.finishRefresh(1);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_MANAGE_ALL_FILES_PERMISSION) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                if (Environment.isExternalStorageManager()) {
                    llPermissionContainer.setVisibility(View.GONE);
                    countFile();
                } else {
                    llPermissionContainer.setVisibility(View.VISIBLE);
                }
            }
        }
        if (requestCode == GlobalConstant.REQUEST_CODE_PICK_FILE) {
            if (resultCode == RESULT_OK && data != null) {
                Uri selectedUri = data.getData();
                new SendData(mActivity, selectedUri).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_STORAGE_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                llPermissionContainer.setVisibility(View.GONE);
                countFile();
            } else {
                llPermissionContainer.setVisibility(View.VISIBLE);
            }
        }
    }

    private void countFile() {
        executor.execute(() -> {
            txtFile = Utils.countFile(mActivity, GlobalConstant.COUNT_TXT_FILE);
            excelFile = Utils.countFile(mActivity, GlobalConstant.COUNT_EXCEL_FILE);
            pdfFile = Utils.countFile(mActivity, GlobalConstant.COUNT_PDF_FILE);
            wordFile = Utils.countFile(mActivity, GlobalConstant.COUNT_WORD_FILE);
            pptFile = Utils.countFile(mActivity, GlobalConstant.COUNT_PPT_FILE);
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
//                        fileViewModel.setAllFiles(allFile);
                    btnTXT.setTvCount(getResources().getString(R.string.str_files, String.valueOf(txtFile.size())));
                    btnPdf.setTvCount(getResources().getString(R.string.str_files, String.valueOf(pdfFile.size())));
                    btnExcel.setTvCount(getResources().getString(R.string.str_files, String.valueOf(excelFile.size())));
                    btnWord.setTvCount(getResources().getString(R.string.str_files, String.valueOf(wordFile.size())));
                    btnPpt.setTvCount(getResources().getString(R.string.str_files, String.valueOf(pptFile.size())));

                }
            });
        });
    }

    public FilesFragment(MainActivity mActivity) {
        this.mActivity = mActivity;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof Activity) {
            this.mActivity = (MainActivity) context;
        }
    }

    private void openListFile(int allFileType) {
        if (Utils.checkPermission(mActivity)) {
            Utils.openListFileActivity(mActivity, allFileType);
        } else {
            Toast.makeText(mActivity, getResources().getString(R.string.toast_no_permission), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (Utils.checkPermission(mActivity)) {
            llPermissionContainer.setVisibility(View.GONE);
            countFile();
        } else {
            llPermissionContainer.setVisibility(View.VISIBLE);
        }
    }

    private void checkPermission() {
        if (Utils.checkPermission(mActivity)) {
            llPermissionContainer.setVisibility(View.GONE);
            //LoadFile
            countFile();
        } else {
            Utils.showPermissionDialog(mActivity);

            llPermissionContainer.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(View v) {
        int idView = v.getId();
        if (idView == R.id.tv_go_to_set) {
            Utils.askPermission(mActivity);
        } else if (idView == R.id.btnSelect) {
            if (mPosition == 0) {
                Intent intentAll = new Intent(mActivity, SelectActivity.class);
                intentAll.putExtra(GlobalConstant.FILE_TYPE, GlobalConstant.RECENT_FILE_TYPE);
                startActivity(intentAll);
            } else if (mPosition == 1) {
                Intent intentRecent = new Intent(mActivity, SelectActivity.class);
                intentRecent.putExtra(GlobalConstant.FILE_TYPE, GlobalConstant.FAV_FILE_TYPE);
                startActivity(intentRecent);
            }
        } else if (idView == R.id.btnTXT) {
            openListFile(GlobalConstant.TXT_FILE_TYPE);
        } else if (idView == R.id.btnPdf) {
            openListFile(GlobalConstant.PDF_FILE_TYPE);
        } else if (idView == R.id.btnExcel) {
            openListFile(GlobalConstant.EXCEL_FILE_TYPE);
        } else if (idView == R.id.btnWord) {
            openListFile(GlobalConstant.WORD_FILE_TYPE);
        } else if (idView == R.id.btnPpt) {
            openListFile(GlobalConstant.PPT_FILE_TYPE);
        }
    }

    public static class SendData extends AsyncTask<Void, Void, Void> {
        WeakReference<MainActivity> weakReference;
        Intent intent;
        Uri intentData;


        public SendData(MainActivity activity, Uri uri) {
            this.weakReference = new WeakReference<>(activity);
            this.intentData = uri;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            String filename = Utils.getFileNameFromUri(intentData, weakReference.get().getContentResolver());
            File pathFolder = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Download/DocumentReader/");
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
