
package com.arkay.gkinhindi.photoPickReader.filterReader;

import android.content.Context;

import com.arkay.gkinhindi.photopick.internal.entity.IncapableCause;
import com.arkay.gkinhindi.photopick.internal.entity.Item;

public abstract class FilterEngine {
    public abstract IncapableCause filter_FilterEngine(Context context_FilterEngine, Item item_FilterEngine);

}
