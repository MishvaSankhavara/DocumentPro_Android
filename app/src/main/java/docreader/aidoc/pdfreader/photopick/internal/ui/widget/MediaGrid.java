
package docreader.aidoc.pdfreader.photopick.internal.ui.widget;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.format.DateUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;

import docreader.aidoc.pdfreader.R;
import docreader.aidoc.pdfreader.photopick.internal.entity.Item;
import docreader.aidoc.pdfreader.photopick.internal.entity.SelectionSpec;

public class MediaGrid extends SquareFrameLayout implements View.OnClickListener {
    private ImageView mThumbnail;
    private AppCompatImageView mBtnShowPreview;
    private CheckView mCheckView;
    private ImageView mGifTag;
    private TextView mVideoDuration;
    private Item mMedia;
    private PreBindInfo mPreBindInfo;
    private OnMediaGridClickListener mListener;

    public MediaGrid(Context context) {
        super(context);
        init(context);
    }

    public MediaGrid(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.media_grid_container, this, true);

        mThumbnail = findViewById(R.id.media_thumbnail);
        mCheckView = findViewById(R.id.check_view);
        mGifTag = findViewById(R.id.gif);
        mVideoDuration = findViewById(R.id.video_duration);
        mBtnShowPreview = findViewById(R.id.pdf_item_pic_big);

        mThumbnail.setOnClickListener(this);
        mBtnShowPreview.setOnClickListener(this);
        mCheckView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (mListener != null) {
            if (v == mBtnShowPreview) {
                mListener.onThumbnailClicked(mThumbnail, mMedia, mPreBindInfo.mViewHolder);
            } else if (v == mCheckView) {
                mListener.onCheckViewClicked(mCheckView, mMedia, mPreBindInfo.mViewHolder);
            } else if (v == mThumbnail) {
                mListener.onItemSelected(mThumbnail, mMedia, mPreBindInfo.mViewHolder);
            }
        }
    }

    public void preBindMedia(PreBindInfo info) {
        mPreBindInfo = info;
    }

    public void bindMedia(Item item) {
        mMedia = item;
        setGifTag();
        initCheckView();
        setImage();
        setVideoDuration();
    }

    public Item getMedia() {
        return mMedia;
    }

    private void setGifTag() {
        mGifTag.setVisibility(mMedia.isGif() ? View.VISIBLE : View.GONE);
    }

    private void initCheckView() {
        mCheckView.setCountable(mPreBindInfo.mCheckViewCountable);
    }

    public void setCheckEnabled(boolean enabled) {
        mCheckView.setEnabled(enabled);
    }

    public void setCheckedNum(int checkedNum) {
        mCheckView.setCheckedNum(checkedNum);
    }

    public void setChecked(boolean checked) {
        mCheckView.setChecked(checked);
    }

    private void setImage() {
        if (mMedia.isGif()) {
            SelectionSpec.getInstance().imageEngine.loadGifThumbnail_ImageEngine(getContext(), mPreBindInfo.mResize,
                    mPreBindInfo.mPlaceholder, mThumbnail, mMedia.getContentUri());
//            SelectionSpec.getInstance().imageEngine.loadGifImage(getContext(), mAvailableWidth, mAvailableWidth, mThumbnail, mMedia.getContentUri());
        } else {
            SelectionSpec.getInstance().imageEngine.loadThumbnail_ImageEngine(getContext(), mPreBindInfo.mResize,
                    mPreBindInfo.mPlaceholder, mThumbnail, mMedia.getContentUri());
        }
    }

    private void setVideoDuration() {
        if (mMedia.isVideo()) {
            mVideoDuration.setVisibility(VISIBLE);
            mVideoDuration.setText(DateUtils.formatElapsedTime(mMedia.duration / 1000));
        } else {
            mVideoDuration.setVisibility(GONE);
        }
    }

    public void setOnMediaGridClickListener(OnMediaGridClickListener listener) {
        mListener = listener;
    }

    public interface OnMediaGridClickListener {

        void onThumbnailClicked(ImageView thumbnail, Item item, RecyclerView.ViewHolder holder);

        void onCheckViewClicked(CheckView checkView, Item item, RecyclerView.ViewHolder holder);

        void onItemSelected(ImageView thumbnail, Item item, RecyclerView.ViewHolder holder);

    }

    public static class PreBindInfo {
        int mResize;
        Drawable mPlaceholder;
        boolean mCheckViewCountable;
        RecyclerView.ViewHolder mViewHolder;
        int mSpanCount;

        public PreBindInfo(int resize, Drawable placeholder, boolean checkViewCountable,
                           RecyclerView.ViewHolder viewHolder, int spanCount) {
            mResize = resize;
            mPlaceholder = placeholder;
            mCheckViewCountable = checkViewCountable;
            mViewHolder = viewHolder;
            mSpanCount = spanCount;
        }
    }

}
