package docreader.aidoc.pdfreader.photopick.internal.ui.adapter;

import android.content.Context;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import docreader.aidoc.pdfreader.R;
import docreader.aidoc.pdfreader.photopick.internal.entity.Album;
import docreader.aidoc.pdfreader.photopick.internal.entity.SelectionSpec;

public class AlbumsAdapter extends CursorAdapter {

    private final Drawable mPlaceholder;

    public AlbumsAdapter(Context context, Cursor c, boolean autoRequery) {
        super(context, c, autoRequery);

        TypedArray ta = context.getTheme().obtainStyledAttributes(
                new int[]{R.attr.album_thumbnail_placeholder});
        mPlaceholder = ta.getDrawable(0);
        ta.recycle();
    }

    public AlbumsAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);

        TypedArray ta = context.getTheme().obtainStyledAttributes(
                new int[]{R.attr.album_thumbnail_placeholder});
        mPlaceholder = ta.getDrawable(0);
        ta.recycle();
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.album_item_list, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        Album album = Album.valueOf(cursor);
        String displayName = album.getDisplayName(context);
        ((TextView) view.findViewById(R.id.album_name)).setText(displayName);
        ((TextView) view.findViewById(R.id.album_media_count)).setText(context.getString(R.string.label_file_count, String.valueOf(album.getCount())));

        docreader.aidoc.pdfreader.photopick.internal.ui.widget.RoundedRectangleImageView coverView = view.findViewById(R.id.album_cover);
        
        if (displayName.toLowerCase().contains("pdf")) {
            coverView.setImageResource(R.drawable.ic_album_pdf);
        } else if (displayName.toLowerCase().contains("word") || displayName.toLowerCase().contains("doc")) {
            coverView.setImageResource(R.drawable.ic_album_doc);
        } else if (displayName.toLowerCase().contains("excel") || displayName.toLowerCase().contains("xls")) {
            coverView.setImageResource(R.drawable.ic_album_xls);
        } else if (displayName.toLowerCase().contains("ppt") || displayName.toLowerCase().contains("powerpoint")) {
            coverView.setImageResource(R.drawable.ic_album_ppt);
        } else {
            SelectionSpec.getInstance().imageEngine.loadThumbnail_ImageEngine(context, context.getResources().getDimensionPixelSize(R
                            .dimen.media_grid_size), mPlaceholder,
                    coverView, album.getCoverUri());
        }
    }
}
