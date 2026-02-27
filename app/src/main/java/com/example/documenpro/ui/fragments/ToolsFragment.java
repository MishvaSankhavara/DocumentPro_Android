package com.example.documenpro.ui.fragments;

import com.docpro.scanner.result.ResultViewerActivity;
import com.docpro.scanner.selector.DocPickerActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.documenpro.GlobalConstant;
import com.example.documenpro.MyApplication;
import com.example.documenpro.R;
import com.example.documenpro.adapter_reader.ViewItemDecoration;
import com.example.documenpro.adapter_reader.DocumentToolAdapter;
import com.example.documenpro.clickListener.OnToolTapListener;
import com.example.documenpro.model_reader.ToolsModel;
import com.example.documenpro.photopick.Matisse;
import com.example.documenpro.photopick.MimeType;
import com.example.documenpro.photoPickReader.engineReader.GlideEngineManager;
import com.example.documenpro.ui.activities.MergeSelectFileActivity;
import com.example.documenpro.utils.Utils;

public class ToolsFragment extends Fragment {

    private Activity mActivity;
    ToolsModel mToolType;

    public ToolsFragment() {
    }

    public ToolsFragment(Activity mActivity) {
        this.mActivity = mActivity;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tools, container, false);
        initViews(view);

        return view;
    }

    private void initViews(View view) {
        RecyclerView recyclerView = view.findViewById(R.id.tools_rv);
        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen._1sdp);
        recyclerView.addItemDecoration(new ViewItemDecoration(spacingInPixels));
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(mActivity, 4, RecyclerView.VERTICAL, false));
        DocumentToolAdapter adapter = new DocumentToolAdapter(mActivity, new OnToolTapListener() {
            @Override
            public void onToolTap(ToolsModel toolType) {
                mToolType = toolType;

                if (Utils.checkPermission(mActivity)) {
                    executeTool(mToolType.getToolType_toolModel());

                } else {
                    Utils.showPermissionDialog(mActivity);
                }
            }
        });
        recyclerView.setAdapter(adapter);
    }

    private void executeTool(int toolType) {
        if (toolType == GlobalConstant.TOOL_YOUR_PDF) {
            startActivity(new Intent(mActivity, ResultViewerActivity.class));
        } else if (toolType == GlobalConstant.TOOL_MERGE) {
            startActivity(new Intent(mActivity, MergeSelectFileActivity.class));
            MyApplication.getInstance().clearArrayListMerge();
        } else if (toolType == GlobalConstant.TOOL_COMPRESS) {

            Intent intentCompress = new Intent(mActivity, DocPickerActivity.class);
            intentCompress.putExtra(GlobalConstant.TOOL_TYPE, GlobalConstant.TOOL_COMPRESS);
            startActivity(intentCompress);

        } else if (toolType == GlobalConstant.TOOL_SPLIT) {
            Intent intentSplit = new Intent(mActivity, DocPickerActivity.class);
            intentSplit.putExtra(GlobalConstant.TOOL_TYPE, GlobalConstant.TOOL_SPLIT);
            startActivity(intentSplit);
            MyApplication.getInstance().clearArrayListSplit();
        } else if (toolType == GlobalConstant.TOOL_BROWSE_PDF) {
            Utils.chooseFileManager(mActivity);
        } else if (toolType == GlobalConstant.TOOL_PRINT) {
            Intent intentPrint = new Intent(mActivity, DocPickerActivity.class);
            intentPrint.putExtra(GlobalConstant.TOOL_TYPE, GlobalConstant.TOOL_PRINT);
            startActivity(intentPrint);
        } else if (toolType == GlobalConstant.TOOL_PHOTO_TO_PDF) {
            Matisse.from(mActivity)
                    .choose(MimeType.ofImage(), false)
                    .countable(true)
                    .maxSelectable(9)
                    .showSingleMediaType(true)
                    .gridExpectedSize(getResources().getDimensionPixelSize(R.dimen.grid_expected_size))
                    .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
                    .thumbnailScale(0.85f)
                    .imageEngine(new GlideEngineManager())
                    .showPreview(false) // Default is `true`
                    .forResult(11);

        } else if (toolType == GlobalConstant.TOOL_PDF_TO_PHOTO) {
            Intent intentPDF = new Intent(mActivity, DocPickerActivity.class);
            intentPDF.putExtra(GlobalConstant.TOOL_TYPE, GlobalConstant.TOOL_PDF_TO_PHOTO);
            startActivity(intentPDF);
        } else if (toolType == GlobalConstant.TOOL_LOCK_PDF) {

            Intent intentLock = new Intent(mActivity, DocPickerActivity.class);
            intentLock.putExtra(GlobalConstant.TOOL_TYPE, GlobalConstant.TOOL_LOCK_PDF);
            startActivity(intentLock);

        } else if (toolType == GlobalConstant.TOOL_UNLOCK_PDF) {
            Intent intentLock = new Intent(mActivity, DocPickerActivity.class);
            intentLock.putExtra(GlobalConstant.TOOL_TYPE, GlobalConstant.TOOL_UNLOCK_PDF);
            startActivity(intentLock);
        }

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof Activity) {
            this.mActivity = (Activity) context;
        }
    }
}
