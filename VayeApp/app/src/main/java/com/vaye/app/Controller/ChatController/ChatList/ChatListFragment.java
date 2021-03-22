package com.vaye.app.Controller.ChatController.ChatList;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.vaye.app.Model.ChatListModel;
import com.vaye.app.Model.CurrentUser;
import com.vaye.app.R;

import java.util.ArrayList;


public class ChatListFragment extends Fragment {

    CurrentUser currentUser;
    View rootView;
    RecyclerView list;
    ArrayList<ChatListModel> chatListModels = new ArrayList<>();
    ChatListAdapter adapter;
    public ChatListFragment(CurrentUser currentUser) {
        this.currentUser = currentUser;
    }

    public ChatListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_chat_list, container, false);
        configureUI();
        return rootView;
    }

    public void getMsgList(){
        CollectionReference db = (CollectionReference) FirebaseFirestore.getInstance().collection("user")
                .document(currentUser.getUid())
                .collection("msg-list").orderBy("time", Query.Direction.DESCENDING);
                db.addSnapshotListener(getActivity(), new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (!value.isEmpty()){
                           for (DocumentChange item : value.getDocumentChanges()){
                               if (item.getType().equals(DocumentChange.Type.ADDED)){
                                    chatListModels.add(item.getDocument().toObject(ChatListModel.class));
                               }else if (item.getType().equals(DocumentChange.Type.MODIFIED)){

                               }else {

                               }
                           }
                        }
                    }
                });
    }

    private void configureUI(){
        list = (RecyclerView)rootView.findViewById(R.id.list);
        list.setHasFixedSize(true);
        list.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ChatListAdapter(chatListModels,getContext(),currentUser);
        list.setAdapter(adapter);

    }
}