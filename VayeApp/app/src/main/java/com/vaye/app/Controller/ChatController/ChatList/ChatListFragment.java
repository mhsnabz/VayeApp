package com.vaye.app.Controller.ChatController.ChatList;

import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.vaye.app.Controller.ChatController.ChatActivity;
import com.vaye.app.Controller.HomeController.HomeActivity;
import com.vaye.app.Model.ChatListModel;
import com.vaye.app.Model.CurrentUser;
import com.vaye.app.R;

import java.util.ArrayList;
import java.util.Calendar;
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

    Activity activity;


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
        ChatActivity activity = (ChatActivity) getActivity();
        currentUser = activity.getIntent().getParcelableExtra("currentUser");
        configureUI();

        getMsgList();
        return rootView;
    }

    public void getMsgList(){

        Query db =  FirebaseFirestore.getInstance().collection("user")
                .document(currentUser.getUid())
                .collection("msg-list");
           db.addSnapshotListener((ChatActivity)getActivity(), new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (!value.isEmpty()){
                           for (DocumentChange item : value.getDocumentChanges()){
                               if (item.getType().equals(DocumentChange.Type.ADDED)){
                                    chatListModels.add(item.getDocument().toObject(ChatListModel.class));
                                   Collections.sort(chatListModels, new Comparator<ChatListModel>(){
                                       public int compare(ChatListModel obj1, ChatListModel obj2) {
                                           if (obj1.getTime() != null && obj2.getTime()!=null){
                                               return obj2.getTime().compareTo(obj1.getTime());
                                           }else{
                                              obj1.setTime(Timestamp.now());
                                              obj2.setTime(Timestamp.now());
                                              adapter.notifyDataSetChanged();
                                              return obj2.getTime().compareTo(obj1.getTime());
                                           }

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
                                           if (obj2.getTime()==null){
                                               return Calendar.getInstance().getTime().compareTo(obj1.getTime().toDate());
                                           }else{
                                               return obj2.getTime().compareTo(obj1.getTime());
                                           }

                                       }
                                   });

                                   adapter.notifyDataSetChanged();

                               }else {
                                   Collections.sort(chatListModels, new Comparator<ChatListModel>(){
                                       public int compare(ChatListModel obj1, ChatListModel obj2) {
                                           return obj2.getTime().compareTo(obj1.getTime());
                                       }
                                   });

                                   for (int i = 0 ; i < chatListModels.size() ; i++){
                                       if (chatListModels.get(i).getUid().equals(item.getDocument().getString("uid"))){
                                           chatListModels.remove(i);
                                           adapter.notifyItemRemoved(i);
                                       }
                                   }

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
    public void onDestroy() {
        super.onDestroy();

        Log.d(TAG, "onDestroy: " + "on destroy");

    }

    @Override
    public void onStart() {
        super.onStart();

    }
}