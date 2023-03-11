package com.netro.troxrider.util;

import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class LinearRecyclerDecoration extends RecyclerView.ItemDecoration {
    int topPadding,bottomPadding, sidePadding;

    public LinearRecyclerDecoration(int topPadding, int bottomPadding, int sidePadding) {
        this.topPadding = topPadding;
        this.bottomPadding = bottomPadding;
        this.sidePadding = sidePadding;
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);

        int itemCount = state.getItemCount();

        if (itemCount > 0 && parent.getChildAdapterPosition(view) == itemCount - 1) {
            outRect.bottom = bottomPadding;
        }
        outRect.top = topPadding;
        outRect.right = sidePadding;
        outRect.left = sidePadding;
    }
}