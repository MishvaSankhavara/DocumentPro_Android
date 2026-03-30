package docreader.aidoc.pdfreader.ui.customviews.smartrefresh.impl;

import android.annotation.SuppressLint;
import android.view.View;

import docreader.aidoc.pdfreader.ui.customviews.smartrefresh.api.RefreshHeaderComponent;
import docreader.aidoc.pdfreader.ui.customviews.smartrefresh.internal.RefreshInternalAbstract;

@SuppressLint("ViewConstructor")
public class HeaderComponentWrapper extends RefreshInternalAbstract implements RefreshHeaderComponent/*, InvocationHandler*/ {
    public HeaderComponentWrapper(View wrapper) {
        super(wrapper);
    }
}
