package com.vaye.app.Controller.ChatController.ChatList;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.vaye.app.Model.ChatListModel;
import com.vaye.app.Model.CurrentUser;
import com.vaye.app.Model.LessonPostModel;
import com.vaye.app.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.ListIterator;


public class ChatListFragment extends Fragment {
    String  TAG = "ChatListFragment";
    CurrentUser currentUser;
    View rootView;
    RecyclerView list;
    ArrayList<ChatListModel> chatListModels;
    ChatListAdapter adapter;
    private ListenerRegistration registration;
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
        getMsgList();
        return rootView;
    }

    public void getMsgList(){
        Query db =  FirebaseFirestore.getInstance().collection("user")
                .document(currentUser.getUid())
                .collection("msg-list").orderBy("time", Query.Direction.ASCENDING);
        registration =  db.addSnapshotListener( new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (!value.isEmpty()){
                           for (DocumentChange item : value.getDocumentChanges()){
                               if (item.getType().equals(DocumentChange.Type.ADDED)){
                                    chatListModels.add(item.getDocument().toObject(ChatListModel.class));
                                   Collections.sort(chatListModels, new Comparator<ChatListModel>(){
                                       public int compare(ChatListModel obj1, ChatListModel obj2) {
                                           return obj2.getTime().compareTo(obj1.getTime());
                                       }

                                   });
                                    adapter.notifyDataSetChanged();
                               }else if (item.getType().equals(DocumentChange.Type.MODIFIED)){

                                   ListIterator<ChatListModel> iterator = chatListModels.listIterator();
                                   while (iterator.hasNext()) {
                                       String next = iterator.next().getUid();
                                       Log.d(TAG, "onEvent: "+ next);
                                       Log.d(TAG, "onEvent: "+ item.getDocument().getString("uid"));
                                       if (next.equals(item.getDocument().getString("uid"))) {
                                           iterator.set(item.getDocument().toObject(ChatListModel.class));
                                       }
                                   }
                                   Collections.sort(chatListModels, new Comparator<ChatListModel>(){
                                       public int compare(ChatListModel obj1, ChatListModel obj2) {
                                           return obj2.getTime().compareTo(obj1.getTime());
                                       }

                                   });

                                   adapter.notifyDataSetChanged();

                               }else {
                                   Collections.sort(chatListModels, new Comparator<ChatListModel>(){
                                       public int compare(ChatListModel obj1, ChatListModel obj2) {
                                           return obj2.getTime().compareTo(obj1.getTime());
                                       }

                                   });
                                    chatListModels.remove(chatListModels.indexOf(item.getDocument().getString("uid")));
                                    adapter.notifyItemRemoved(chatListModels.indexOf(item.getDocument().getString("uid")));
                               }
                           }
                        }
                    }
                });
    }

    private void configureUI(){
        chatListModels = new ArrayList<>();
        list = (RecyclerView)rootView.findViewById(R.id.list);
        list.setHasFixedSize(true);
        list.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ChatListAdapter(chatListModels,getContext(),currentUser);
        list.setAdapter(adapter);

    }

    @Override
    public void onStop() {
        super.onStop();
        registration.remove();
    }

    @Override
    public void onPause() {
        super.onPause();
        registration.remove();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        registration.remove();

    }
}