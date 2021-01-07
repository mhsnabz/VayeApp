package com.vaye.app.Services;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.vaye.app.Interfaces.LessonPostModelCompletion;
import com.vaye.app.Interfaces.StringArrayListInterface;
import com.vaye.app.Model.CurrentUser;
import com.vaye.app.Model.LessonPostModel;

import java.util.ArrayList;
import java.util.concurrent.Semaphore;

public class MajorPostService {
    ArrayList<LessonPostModel> models;


    private static final MajorPostService instance = new MajorPostService();
    public static MajorPostService shared() {
        return instance;
    }
    private Semaphore sem = new Semaphore(1);














}
