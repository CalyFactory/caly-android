package com.cooltechworks.views.shimmer;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.shimmer.ShimmerFrameLayout;

import io.caly.calyandroid.R;

/**
 * Created by sharish on 22/11/16.
 */

public class ShimmerViewHolder extends RecyclerView.ViewHolder {

    public ShimmerViewHolder(LayoutInflater inflater, ViewGroup parent, int innerViewResId) {
        super(inflater.inflate(R.layout.viewholder_shimmer, parent, false));
        ShimmerFrameLayout layout = (ShimmerFrameLayout) itemView;

        View innerView = inflater.inflate(innerViewResId, layout, false);
        layout.addView(innerView);
        layout.setAutoStart(false);
    }

    /**
     * Binds the view
     */
    public void bind() {

        ShimmerFrameLayout layout = (ShimmerFrameLayout) itemView;
        layout.startShimmerAnimation();
    }
}
