package com.vaye.app.Controller.ChatController.FriendList;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.vaye.app.Controller.ChatController.ChatActivity;
import com.vaye.app.Model.CurrentUser;
import com.vaye.app.Model.FriendListModel;
import com.vaye.app.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class FriendListFragment extends Fragment {


    CurrentUser currentUser;
    RecyclerView list;
    View rootView;
    FriendListAdapter adapter;
    ArrayList<FriendListModel> friendList = new ArrayList<>();



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
        ChatActivity activity = (ChatActivity) getActivity();
        currentUser = activity.getIntent().getParcelableExtra("currentUser");
        configureUI();
        getFriendList();
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
        CollectionReference db = FirebaseFirestore.getInstance().collection("user")
                .document(currentUser.getUid())
                .collection("friend-list");
             db.addSnapshotListener((ChatActivity) getActivity(),new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (!value.isEmpty()){
                    for (DocumentChange item : value.getDocumentChanges()){
                        if (item.getType().equals(DocumentChange.Type.ADDED)){
                            if (friendList.isEmpty() || friendList.size() == 0 ){
                                friendList.add(item.getDocument().toObject(FriendListModel.class));
                                adapter.notifyDataSetChanged();
                            }else {
                                for (int i = 0 ; i < friendList.size() ; i++){
                                    if (!friendList.get(i).getUid().equals(item.getDocument().getString("uid"))){
                                            friendList.add(item.getDocument().toObject(FriendListModel.class));
                                            adapter.notifyDataSetChanged();
                                        }

                                }
                            }


                            Collections.sort(friendList, new Comparator<FriendListModel>() {
                                @Override
                                public int compare(FriendListModel obj1, FriendListModel obj2) {
                                        return obj2.getTarih().compareTo(obj1.getTarih());
                                }
                            });



                        }else if (item.getType().equals(DocumentChange.Type.REMOVED)){
                            for (int i = 0 ; i < friendList.size() ; i++){
                                if (friendList.get(i).getUid().equals(item.getDocument().getString("uid"))){
                                    friendList.remove(i);
                                    adapter.notifyItemRemoved(i);
                                }
                            }

                        }
                    }
                }
            }
        });
    }

}