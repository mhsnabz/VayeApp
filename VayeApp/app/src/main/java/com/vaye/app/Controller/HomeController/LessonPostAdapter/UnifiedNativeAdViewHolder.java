package com.vaye.app.Controller.HomeController.LessonPostAdapter;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.ads.formats.MediaView;
import com.google.android.gms.ads.formats.UnifiedNativeAdView;
import com.vaye.app.R;

public class UnifiedNativeAdViewHolder extends RecyclerView.ViewHolder{
    private UnifiedNativeAdView adView;
    public UnifiedNativeAdViewHolder(@NonNull View view) {
        super(view);
        adView = (UnifiedNativeAdView) view.findViewById(R.id.ad_view);
        // The MediaView will display a video asset if one is present in the ad, and the
        // first image asset otherwise.
        adView.setMediaView((MediaView) adView.findViewById(R.id.ad_media));

        // Register the view used for each individual asset.
        adView.setHeadlineView(adView.findViewById(R.id.ad_headline));
        adView.setBodyView(adView.findViewById(R.id.ad_body));
        adView.setCallToActionView(adView.findViewById(R.id.ad_call_to_action));
        adView.setIconView(adView.findViewById(R.id.ad_icon));
        adView.setPriceView(adView.findViewById(R.id.ad_price));
        adView.setStarRatingView(adView.findViewById(R.id.ad_stars));
        adView.setStoreView(adView.findViewById(R.id.ad_store));
        adView.setAdvertiserView(adView.findViewById(R.id.ad_advertiser));
    }

    public UnifiedNativeAdView getAdView() {
        return  adView;
    }
}
