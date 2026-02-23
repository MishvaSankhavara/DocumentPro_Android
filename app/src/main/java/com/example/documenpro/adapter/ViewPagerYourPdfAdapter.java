package com.example.documenpro.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.docpro.scanner.result.ResultViewerActivity;
import com.example.documenpro.ui.fragments.yourpdf.CompressFragment;
import com.example.documenpro.ui.fragments.yourpdf.ImageFragment;
import com.example.documenpro.ui.fragments.yourpdf.ImageToPdfFragment;
import com.example.documenpro.ui.fragments.yourpdf.LockFragment;
import com.example.documenpro.ui.fragments.yourpdf.MergeFragment;
import com.example.documenpro.ui.fragments.yourpdf.SplitFragment;

public class ViewPagerYourPdfAdapter extends FragmentStateAdapter {
    ResultViewerActivity mActivity;

    public ViewPagerYourPdfAdapter(@NonNull FragmentActivity fragmentActivity, ResultViewerActivity activity) {
        super(fragmentActivity);
        this.mActivity = activity;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new CompressFragment(mActivity);
            case 1:
                return new SplitFragment(mActivity);
            case 2:
                return new MergeFragment(mActivity);
            case 3:
                return new ImageToPdfFragment(mActivity);
            case 4:
                return new LockFragment(mActivity);
            case 5:
                return new ImageFragment(mActivity);
            default:
                return new CompressFragment(mActivity);

        }
    }

    @Override
    public int getItemCount() {
        return 6;
    }

}