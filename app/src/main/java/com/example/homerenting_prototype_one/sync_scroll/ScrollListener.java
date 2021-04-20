package com.example.homerenting_prototype_one.sync_scroll;

import android.view.View;

public interface ScrollListener {
    void onScrollChange(View syncedStrollView, int l, int t, int oldl, int oldt);
}
