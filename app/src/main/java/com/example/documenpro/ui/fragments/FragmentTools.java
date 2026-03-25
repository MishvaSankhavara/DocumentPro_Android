package com.example.documenpro.ui.fragments;

import com.docpro.scanner.result.ResultViewerActivity;
import com.docpro.scanner.selector.DocPickerActivity;
import java.util.ArrayList;

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

import com.example.documenpro.AppGlobalConstants;
import com.example.documenpro.DocumentMyApplication;
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
        ArrayList<ToolsModel> allTools = AppGlobalConstants.setToolsList();

        view.findViewById(R.id.iv_back).setOnClickListener(v -> {
            if (activityContext != null) {
                activityContext.onBackPressed();
            }
        });

        ArrayList<ToolsModel> pdfToolsList = new ArrayList<>();
        ArrayList<ToolsModel> securityToolsList = new ArrayList<>();
        ArrayList<ToolsModel> convertEditorList = new ArrayList<>();

        for (ToolsModel tool : allTools) {
            int type = tool.getToolType_toolModel();
            if (type == AppGlobalConstants.TOOL_YOUR_PDF ||
                    type == AppGlobalConstants.TOOL_ID_MERGE ||
                    type == AppGlobalConstants.TOOL_ID_COMPRESS ||
                    type == AppGlobalConstants.TOOL_ID_SPLIT ||
                    type == AppGlobalConstants.TOOL_ID_PRINT) {
                pdfToolsList.add(tool);
            } else if (type == AppGlobalConstants.TOOL_ID_LOCK_PDF ||
                    type == AppGlobalConstants.TOOL_ID_UNLOCK_PDF) {
                securityToolsList.add(tool);
            } else if (type == AppGlobalConstants.TOOL_ID_PHOTO_TO_PDF ||
                    type == AppGlobalConstants.TOOL_PDF_TO_PHOTO) {
                convertEditorList.add(tool);
            }
        }

        setupRecyclerView(view.findViewById(R.id.rv_pdf_tools), pdfToolsList);
        setupRecyclerView(view.findViewById(R.id.rv_security), securityToolsList);
        setupRecyclerView(view.findViewById(R.id.rv_convert_editor), convertEditorList);
    }

    private void setupRecyclerView(RecyclerView recyclerView, ArrayList<ToolsModel> toolsList) {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 3, RecyclerView.VERTICAL, false));
        DocumentToolAdapter adapter = new DocumentToolAdapter(requireActivity(), toolsList, new OnToolTapListener() {
            @Override
            public void onToolTap(ToolsModel toolType) {
                selectedTool = toolType;
                if (activityContext == null)
                    return;
                if (Utils.checkPermission(activityContext)) {
                    executeTool(selectedTool.getToolType_toolModel());
                } else {
                    Utils.showPermissionDialog(activityContext);
                }
            }
        });
        recyclerView.setAdapter(adapter);
    }

    private void executeTool(int toolType) {
        if (toolType == AppGlobalConstants.TOOL_YOUR_PDF) {
            startActivity(new Intent(activityContext, ResultViewerActivity.class));
        } else if (toolType == AppGlobalConstants.TOOL_ID_MERGE) {
            startActivity(new Intent(activityContext, MergeSelectFileActivity.class));
            DocumentMyApplication.getInstance().clearArrayListMerge();
        } else if (toolType == AppGlobalConstants.TOOL_ID_COMPRESS) {

            Intent intentCompress = new Intent(activityContext, DocPickerActivity.class);
            intentCompress.putExtra(AppGlobalConstants.EXTRA_TOOL_TYPE, AppGlobalConstants.TOOL_ID_COMPRESS);
            startActivity(intentCompress);

        } else if (toolType == AppGlobalConstants.TOOL_ID_SPLIT) {
            Intent intentSplit = new Intent(activityContext, DocPickerActivity.class);
            intentSplit.putExtra(AppGlobalConstants.EXTRA_TOOL_TYPE, AppGlobalConstants.TOOL_ID_SPLIT);
            startActivity(intentSplit);
            DocumentMyApplication.getInstance().clearArrayListSplit();
        } else if (toolType == AppGlobalConstants.TOOL_ID_BROWSE_PDF) {
            Utils.chooseFileManager(activityContext);
        } else if (toolType == AppGlobalConstants.TOOL_ID_PRINT) {
            Intent intentPrint = new Intent(activityContext, DocPickerActivity.class);
            intentPrint.putExtra(AppGlobalConstants.EXTRA_TOOL_TYPE, AppGlobalConstants.TOOL_ID_PRINT);
            startActivity(intentPrint);
        } else if (toolType == AppGlobalConstants.TOOL_ID_PHOTO_TO_PDF) {
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

        } else if (toolType == AppGlobalConstants.TOOL_PDF_TO_PHOTO) {
            Intent intentPDF = new Intent(activityContext, DocPickerActivity.class);
            intentPDF.putExtra(AppGlobalConstants.EXTRA_TOOL_TYPE, AppGlobalConstants.TOOL_PDF_TO_PHOTO);
            startActivity(intentPDF);
        } else if (toolType == AppGlobalConstants.TOOL_ID_LOCK_PDF) {

            Intent intentLock = new Intent(activityContext, DocPickerActivity.class);
            intentLock.putExtra(AppGlobalConstants.EXTRA_TOOL_TYPE, AppGlobalConstants.TOOL_ID_LOCK_PDF);
            startActivity(intentLock);

        } else if (toolType == AppGlobalConstants.TOOL_ID_UNLOCK_PDF) {
            Intent intentLock = new Intent(activityContext, DocPickerActivity.class);
            intentLock.putExtra(AppGlobalConstants.EXTRA_TOOL_TYPE, AppGlobalConstants.TOOL_ID_UNLOCK_PDF);
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
