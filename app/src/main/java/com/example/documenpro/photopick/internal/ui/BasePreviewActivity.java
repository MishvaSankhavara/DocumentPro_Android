
package com.example.documenpro.photopick.internal.ui;

import static com.example.documenpro.photopick.ui.MatisseActivity.EXTRA_RESULT_SELECTION;
import static com.example.documenpro.photopick.ui.MatisseActivity.EXTRA_RESULT_SELECTION_PATH;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.viewpager.widget.ViewPager;

import com.example.documenpro.R;
import com.example.documenpro.photopick.internal.entity.IncapableCause;
import com.example.documenpro.photopick.internal.entity.Item;
import com.example.documenpro.photopick.internal.entity.SelectionSpec;
import com.example.documenpro.photopick.internal.model.SelectedItemCollection;
import com.example.documenpro.photopick.internal.ui.adapter.PreviewPagerAdapter;
import com.example.documenpro.photopick.internal.ui.widget.CheckView;
import com.example.documenpro.photopick.listener.OnFragmentInteractionListener;
import com.docpro.scanner.media.MediaSorterActivity;

import java.util.ArrayList;

public abstract class BasePreviewActivity extends AppCompatActivity implements View.OnClickListener,
        ViewPager.OnPageChangeListener, OnFragmentInteractionListener {

    public static final String EXTRA_DEFAULT_BUNDLE = "extra_default_bundle";
    public static final String EXTRA_RESULT_BUNDLE = "extra_result_bundle";
    public static final String EXTRA_RESULT_APPLY = "extra_result_apply";
    public static final String EXTRA_RESULT_ORIGINAL_ENABLE = "extra_result_original_enable";
    public static final String CHECK_STATE = "checkState";

    protected final SelectedItemCollection mSelectedCollection = new SelectedItemCollection(this);
    protected SelectionSpec mSpec;
    protected ViewPager mPager;

    protected LinearLayout btnSelect;
    protected PreviewPagerAdapter mAdapter;

    protected CheckView mCheckView;
    protected TextView mButtonApply;
    protected TextView tvNumberPhoto;
    protected int mPreviousPos = -1;
    protected boolean mOriginalEnable;
    protected AppCompatImageView btnBack;

    protected int totalPhoto;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setTheme(SelectionSpec.getInstance().themeId);
        super.onCreate(savedInstanceState);
        if (!SelectionSpec.getInstance().hasInited) {
            setResult(RESULT_CANCELED);
            finish();
            return;
        }
        setContentView(R.layout.act_media_preview);
        mSpec = SelectionSpec.getInstance();
        if (mSpec.needOrientationRestriction()) {
            setRequestedOrientation(mSpec.orientation);
        }

        if (savedInstanceState == null) {
            mSelectedCollection.onCreate(getIntent().getBundleExtra(EXTRA_DEFAULT_BUNDLE));
            mOriginalEnable = getIntent().getBooleanExtra(EXTRA_RESULT_ORIGINAL_ENABLE, false);
        } else {
            mSelectedCollection.onCreate(savedInstanceState);
            mOriginalEnable = savedInstanceState.getBoolean(CHECK_STATE);
        }
        mButtonApply = findViewById(R.id.button_apply);
        mButtonApply.setOnClickListener(this);
        btnSelect = findViewById(R.id.btnSelect);
        btnSelect.setOnClickListener(this);
        tvNumberPhoto = findViewById(R.id.tvNumberPhoto);
        btnBack = findViewById(R.id.act_picker_img_pre_back);
        btnBack.setOnClickListener(this);
        mPager = findViewById(R.id.pager);
        mPager.addOnPageChangeListener(this);
        mAdapter = new PreviewPagerAdapter(getSupportFragmentManager(), null);
        mPager.setAdapter(mAdapter);
        mCheckView = findViewById(R.id.check_view);
        mCheckView.setCountable(mSpec.countable);
        mCheckView.setOnClickListener(this);
        updateApplyButton();
        tvNumberPhoto.setText(mPager.getCurrentItem() + 1 + "/");

    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        mSelectedCollection.onSaveInstanceState(outState);
        outState.putBoolean("checkState", mOriginalEnable);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onBackPressed() {
        sendBackResult(false);
        super.onBackPressed();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.button_apply) {
            sendBackResult(true);
            finish();
        } else if (v.getId() == R.id.act_picker_img_pre_back) {
            onBackPressed();
        } else if (v.getId() == R.id.btnSelect || v.getId() == R.id.check_view) {
            Item item = mAdapter.getMediaItem(mPager.getCurrentItem());
            if (mSelectedCollection.isSelected(item)) {
                mSelectedCollection.remove(item);
                if (mSpec.countable) {
                    mCheckView.setCheckedNum(CheckView.UNCHECKED);
                } else {
                    mCheckView.setChecked(false);
                }
            } else {
                if (assertAddSelection(item)) {
                    mSelectedCollection.add(item);
                    if (mSpec.countable) {
                        mCheckView.setCheckedNum(mSelectedCollection.checkedNumOf(item));
                    } else {
                        mCheckView.setChecked(true);
                    }
                }
            }
            updateApplyButton();

            if (mSpec.onSelectedListener != null) {
                mSpec.onSelectedListener.onSelected(
                        mSelectedCollection.asListOfUri(), mSelectedCollection.asListOfString());
            }
        }
    }

    @Override
    public void onClick() {

    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        PreviewPagerAdapter adapter = (PreviewPagerAdapter) mPager.getAdapter();
        if (mPreviousPos != -1 && mPreviousPos != position) {
            ((PreviewItemFragment) adapter.instantiateItem(mPager, mPreviousPos)).resetView();

            Item item = adapter.getMediaItem(position);
            if (mSpec.countable) {
                int checkedNum = mSelectedCollection.checkedNumOf(item);
                mCheckView.setCheckedNum(checkedNum);
                if (checkedNum > 0) {
                    mCheckView.setEnabled(true);
                } else {
                    mCheckView.setEnabled(!mSelectedCollection.maxSelectableReached());
                }
            } else {
                boolean checked = mSelectedCollection.isSelected(item);
                mCheckView.setChecked(checked);
                if (checked) {
                    mCheckView.setEnabled(true);
                } else {
                    mCheckView.setEnabled(!mSelectedCollection.maxSelectableReached());
                }
            }
        }
        mPreviousPos = position;
        tvNumberPhoto.setText(mPager.getCurrentItem() + 1 + "/" + mAdapter.getCount());

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    private void updateApplyButton() {
        int selectedCount = mSelectedCollection.count();
        if (selectedCount == 0) {
            mButtonApply.setText(R.string.button_apply_default);
            mButtonApply.setEnabled(false);
        } else if (selectedCount == 1 && mSpec.singleSelectionModeEnabled()) {
            mButtonApply.setText(R.string.button_apply_default);
            mButtonApply.setEnabled(true);
        } else {
            mButtonApply.setEnabled(true);
            mButtonApply.setText(getString(R.string.button_apply, selectedCount));
        }

    }

    protected void sendBackResult(boolean apply) {
        // Intent intent = new Intent();
        // intent.putExtra(EXTRA_RESULT_BUNDLE,
        // mSelectedCollection.getDataWithBundle());
        // intent.putExtra(EXTRA_RESULT_APPLY, apply);
        // intent.putExtra(EXTRA_RESULT_ORIGINAL_ENABLE, mOriginalEnable);
        // setResult(Activity.RESULT_OK, intent);
        Intent result = new Intent(this, MediaSorterActivity.class);
        ArrayList<Uri> selectedUris = (ArrayList<Uri>) mSelectedCollection.asListOfUri();
        result.putParcelableArrayListExtra(EXTRA_RESULT_SELECTION, selectedUris);
        ArrayList<String> selectedPaths = (ArrayList<String>) mSelectedCollection.asListOfString();
        result.putStringArrayListExtra(EXTRA_RESULT_SELECTION_PATH, selectedPaths);
        result.putExtra(EXTRA_RESULT_ORIGINAL_ENABLE, mOriginalEnable);
        if (!selectedUris.isEmpty()) {
            startActivity(result);
            finish();
        } else {
            finish();
        }
    }

    private boolean assertAddSelection(Item item) {
        IncapableCause cause = mSelectedCollection.isAcceptable(item);
        IncapableCause.handleCause(this, cause);
        return cause == null;
    }
}
