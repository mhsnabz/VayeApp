package com.vaye.app.Interfaces;

import com.google.firebase.firestore.GeoPoint;

public interface LocationCallback {
    void getLocation(GeoPoint location);
}
