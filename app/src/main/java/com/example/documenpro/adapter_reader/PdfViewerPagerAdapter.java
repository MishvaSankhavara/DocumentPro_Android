package com.example.documenpro.adapter_reader;

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

public class PdfViewerPagerAdapter extends FragmentStateAdapter {

    ResultViewerActivity mActivity_PdfViewerPager;

    public PdfViewerPagerAdapter(
            @NonNull FragmentActivity fragmentActivity_PdfViewerPager,
            ResultViewerActivity activity_PdfViewerPager) {

        super(fragmentActivity_PdfViewerPager);
        this.mActivity_PdfViewerPager = activity_PdfViewerPager;
    }

    @Override
    public int getItemCount() {
        return 6;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position_PdfViewerPager) {

        switch (position_PdfViewerPager) {

            case 5:
                return new ImageFragment(mActivity_PdfViewerPager);

            case 4:
                return new LockFragment(mActivity_PdfViewerPager);

            case 3:
                return new ImageToPdfFragment(mActivity_PdfViewerPager);

            case 2:
                return new MergeFragment(mActivity_PdfViewerPager);

            case 1:
                return new SplitFragment(mActivity_PdfViewerPager);

            case 0:
            default:
                return new CompressFragment(mActivity_PdfViewerPager);
        }
    }
}