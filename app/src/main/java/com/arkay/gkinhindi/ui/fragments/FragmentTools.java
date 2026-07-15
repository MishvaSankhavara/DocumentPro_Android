package com.arkay.gkinhindi.ui.fragments;

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

import com.arkay.gkinhindi.Constants;
import com.arkay.gkinhindi.MyApplication;
import com.arkay.gkinhindi.R;
import com.arkay.gkinhindi.model_reader.ToolsModel;
import com.arkay.gkinhindi.photopick.Matisse;
import com.arkay.gkinhindi.photopick.MimeType;
import com.arkay.gkinhindi.photoPickReader.engineReader.GlideEngineManager;
import com.arkay.gkinhindi.ui.activities.MergeSelectFileActivity;
import com.arkay.gkinhindi.utils.Utils;

public class FragmentTools extends Fragment {

    ToolsModel selectedTool;
    private Activity activityContext;

    public FragmentTools() {
    }

    public FragmentTools(Activity mActivity) {
        this.activityContext = mActivity;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_tools, container, false);
        initViews(view);

        return view;
    }

    private void initViews(View view) {
        view.findViewById(R.id.iv_back).setOnClickListener(v -> {
            if (activityContext != null) {
                activityContext.onBackPressed();
            }
        });

        view.findViewById(R.id.card_view_pdf).setOnClickListener(v -> executeToolWithPermissionCheck(Constants.TOOL_YOUR_PDF));
        view.findViewById(R.id.card_merge_pdf).setOnClickListener(v -> executeToolWithPermissionCheck(Constants.TOOL_ID_MERGE));
        view.findViewById(R.id.card_compress_pdf).setOnClickListener(v -> executeToolWithPermissionCheck(Constants.TOOL_ID_COMPRESS));
        view.findViewById(R.id.card_print_pdf).setOnClickListener(v -> executeToolWithPermissionCheck(Constants.TOOL_ID_PRINT));
        view.findViewById(R.id.card_split_pdf).setOnClickListener(v -> executeToolWithPermissionCheck(Constants.TOOL_ID_SPLIT));
        view.findViewById(R.id.card_lock_pdf).setOnClickListener(v -> executeToolWithPermissionCheck(Constants.TOOL_ID_LOCK_PDF));
        view.findViewById(R.id.card_unlock_pdf).setOnClickListener(v -> executeToolWithPermissionCheck(Constants.TOOL_ID_UNLOCK_PDF));
        view.findViewById(R.id.card_image_to_pdf).setOnClickListener(v -> executeToolWithPermissionCheck(Constants.TOOL_ID_PHOTO_TO_PDF));
        view.findViewById(R.id.card_save_as_image).setOnClickListener(v -> executeToolWithPermissionCheck(Constants.TOOL_PDF_TO_PHOTO));
    }

    private void executeToolWithPermissionCheck(int toolType) {
        if (activityContext == null) return;
        if (Utils.checkPermission(activityContext)) {
            executeTool(toolType);
        } else {
            Utils.showPermissionDialog(activityContext);
        }
    }

    private void executeTool(int toolType) {
        if (toolType == Constants.TOOL_YOUR_PDF) {
            startActivity(new Intent(activityContext, ResultViewerActivity.class));
        } else if (toolType == Constants.TOOL_ID_MERGE) {
            startActivity(new Intent(activityContext, MergeSelectFileActivity.class));
            MyApplication.getInstance().clearArrayListMerge();
        } else if (toolType == Constants.TOOL_ID_COMPRESS) {

            Intent intentCompress = new Intent(activityContext, DocPickerActivity.class);
            intentCompress.putExtra(Constants.EXTRA_TOOL_TYPE, Constants.TOOL_ID_COMPRESS);
            startActivity(intentCompress);

        } else if (toolType == Constants.TOOL_ID_SPLIT) {
            Intent intentSplit = new Intent(activityContext, DocPickerActivity.class);
            intentSplit.putExtra(Constants.EXTRA_TOOL_TYPE, Constants.TOOL_ID_SPLIT);
            startActivity(intentSplit);
            MyApplication.getInstance().clearArrayListSplit();
        } else if (toolType == Constants.TOOL_ID_BROWSE_PDF) {
            Utils.chooseFileManager(activityContext);
        } else if (toolType == Constants.TOOL_ID_PRINT) {
            Intent intentPrint = new Intent(activityContext, DocPickerActivity.class);
            intentPrint.putExtra(Constants.EXTRA_TOOL_TYPE, Constants.TOOL_ID_PRINT);
            startActivity(intentPrint);
        } else if (toolType == Constants.TOOL_ID_PHOTO_TO_PDF) {
            Matisse.from(activityContext)
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

        } else if (toolType == Constants.TOOL_PDF_TO_PHOTO) {
            Intent intentPDF = new Intent(activityContext, DocPickerActivity.class);
            intentPDF.putExtra(Constants.EXTRA_TOOL_TYPE, Constants.TOOL_PDF_TO_PHOTO);
            startActivity(intentPDF);
        } else if (toolType == Constants.TOOL_ID_LOCK_PDF) {

            Intent intentLock = new Intent(activityContext, DocPickerActivity.class);
            intentLock.putExtra(Constants.EXTRA_TOOL_TYPE, Constants.TOOL_ID_LOCK_PDF);
            startActivity(intentLock);

        } else if (toolType == Constants.TOOL_ID_UNLOCK_PDF) {
            Intent intentLock = new Intent(activityContext, DocPickerActivity.class);
            intentLock.putExtra(Constants.EXTRA_TOOL_TYPE, Constants.TOOL_ID_UNLOCK_PDF);
            startActivity(intentLock);
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof Activity) {
            this.activityContext = (Activity) context;
        }
    }
}
