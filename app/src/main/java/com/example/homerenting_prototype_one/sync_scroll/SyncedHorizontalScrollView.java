package com.example.homerenting_prototype_one.sync_scroll;

import android.content.Context;
import android.widget.HorizontalScrollView;

public class SyncedHorizontalScrollView extends HorizontalScrollView implements ScrollNotifier {
    private ScrollListener scrollListener = null;

    public SyncedHorizontalScrollView(Context context) {
        super(context);
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if(scrollListener != null)
            scrollListener.onScrollChange(this, l, t, oldl, oldt);
    }

    @Override
    public void setScrollListener(ScrollListener scrollListener) {
        this.scrollListener = scrollListener;
    }

    @Override
    public ScrollListener getScrollListener() {
        return scrollListener;
    }
}
