
package docreader.aidoc.pdfreader.photoPickReader.filterReader;

import android.content.Context;

import docreader.aidoc.pdfreader.photopick.internal.entity.IncapableCause;
import docreader.aidoc.pdfreader.photopick.internal.entity.Item;

public abstract class FilterEngine {
    public abstract IncapableCause filter_FilterEngine(Context context_FilterEngine, Item item_FilterEngine);

}
