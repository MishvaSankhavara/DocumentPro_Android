
package com.example.documenpro.photoPickReader.filterReader;

import android.content.Context;

import com.example.documenpro.photopick.internal.entity.IncapableCause;
import com.example.documenpro.photopick.internal.entity.Item;

public abstract class FilterEngine {
    public abstract IncapableCause filter_FilterEngine(Context context_FilterEngine, Item item_FilterEngine);

}
