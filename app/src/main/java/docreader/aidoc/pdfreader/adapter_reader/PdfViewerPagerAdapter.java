package docreader.aidoc.pdfreader.adapter_reader;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.docpro.scanner.result.ResultViewerActivity;
import docreader.aidoc.pdfreader.ui.fragments.yourpdf.FragmentCompress;
import docreader.aidoc.pdfreader.ui.fragments.yourpdf.FragmentImage;
import docreader.aidoc.pdfreader.ui.fragments.yourpdf.FragmentImageToPdf;
import docreader.aidoc.pdfreader.ui.fragments.yourpdf.FragmentLock;
import docreader.aidoc.pdfreader.ui.fragments.yourpdf.FragmentMerge;
import docreader.aidoc.pdfreader.ui.fragments.yourpdf.FragmentSplit;

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
                return new FragmentImage(mActivity_PdfViewerPager);

            case 4:
                return new FragmentLock(mActivity_PdfViewerPager);

            case 3:
                return new FragmentImageToPdf(mActivity_PdfViewerPager);

            case 2:
                return new FragmentMerge(mActivity_PdfViewerPager);

            case 1:
                return new FragmentSplit(mActivity_PdfViewerPager);

            case 0:
            default:
                return new FragmentCompress(mActivity_PdfViewerPager);
        }
    }
}