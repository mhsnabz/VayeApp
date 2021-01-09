package com.vaye.app.Util.AdsHelper;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.formats.UnifiedNativeAd;

public abstract  class AdUnifiedListeningg extends AdListener implements UnifiedNativeAd.OnUnifiedNativeAdLoadedListener {

    private AdLoader adLoader;

    public AdLoader getAdLoader() {
        return adLoader;
    }

    public void setAdLoader(AdLoader adLoader) {
        this.adLoader = adLoader;
    }
}
