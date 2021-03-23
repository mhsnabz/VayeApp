package com.vaye.app.Controller.ChatController.FriendList;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.vaye.app.Model.CurrentUser;
import com.vaye.app.Model.FriendListModel;
import com.vaye.app.R;

import java.util.ArrayList;

public class FriendListFragment extends Fragment {


    CurrentUser currentUser;
    RecyclerView list;
    View rootView;
    FriendListAdapter adapter;
    ArrayList<FriendListModel> friendList = new ArrayList<>();
    ListenerRegistration registration;
    public FriendListFragment(CurrentUser currentUser) {
        this.currentUser = currentUser;
    }

    public FriendListFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_friend_list, container, false);
        configureUI();
        return rootView;
    }

    private void configureUI(){
        list = (RecyclerView)rootView.findViewById(R.id.list);
        list.setHasFixedSize(true);
        list.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new FriendListAdapter(friendList,getContext(),currentUser);
        list.setAdapter(adapter);

    }

    private void getFriendList(){
        Query db = FirebaseFirestore.getInstance().collection("user")
                .document(currentUser.getUid())
                .collection("friend-list").orderBy("tarih", Query.Direction.ASCENDING);
      registration =   db.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (!value.isEmpty()){
                    for (DocumentChange item : value.getDocumentChanges()){
                        if (item.getType().equals(DocumentChange.Type.ADDED)){
                            friendList.add(item.getDocument().toObject(FriendListModel.class));
                            adapter.notifyDataSetChanged();
                        }else if (item.getType().equals(DocumentChange.Type.REMOVED)){
                            friendList.remove(friendList.indexOf(item.getDocument().toObject(FriendListModel.class)));
                            adapter.notifyItemRemoved(friendList.indexOf(item.getDocument().toObject(FriendListModel.class)));
                        }
                    }
                }
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        getFriendList();
    }

    @Override
    public void onStop() {
        super.onStop();
        registration.remove();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        registration.remove();
    }
}