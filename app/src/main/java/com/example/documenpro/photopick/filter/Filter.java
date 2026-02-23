
package com.example.documenpro.photopick.filter;

import android.content.Context;

import com.example.documenpro.photopick.MimeType;
import com.example.documenpro.photopick.internal.entity.IncapableCause;
import com.example.documenpro.photopick.internal.entity.Item;

import java.util.Set;
public abstract class Filter {
    protected abstract Set<MimeType> constraintTypes();
    public abstract IncapableCause filter(Context context, Item item);
    protected boolean needFiltering(Context context, Item item) {
        for (MimeType type : constraintTypes()) {
            if (type.checkType(context.getContentResolver(), item.getContentUri())) {
                return true;
            }
        }
        return false;
    }
}
