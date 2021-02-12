package com.vaye.app.Services;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.vaye.app.Interfaces.Notifications;
import com.vaye.app.Interfaces.TrueFalse;
import com.vaye.app.Model.CurrentUser;
import com.vaye.app.Model.MainPostModel;

import java.util.HashMap;
import java.util.Map;

public class FoodMeService {
    private static final FoodMeService instance = new FoodMeService();
    public static FoodMeService shared() {
        return instance;
    }








}
