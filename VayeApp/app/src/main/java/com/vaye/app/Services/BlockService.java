package com.vaye.app.Services;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.database.core.utilities.Utilities;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.auth.User;
import com.kongzue.dialog.v3.CustomDialog;
import com.kongzue.dialog.v3.TipDialog;
import com.kongzue.dialog.v3.WaitDialog;
import com.vaye.app.Interfaces.BlockOptionSelect;
import com.vaye.app.Interfaces.CallBackCount;
import com.vaye.app.Interfaces.CurrentUserService;
import com.vaye.app.Interfaces.TrueFalse;
import com.vaye.app.Model.CurrentUser;
import com.vaye.app.Model.OtherUser;
import com.vaye.app.R;
import com.vaye.app.SplashScreen.SplashScreen;
import com.vaye.app.Util.BottomSheetHelper.BlockServiceAdapter;
import com.vaye.app.Util.Helper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class BlockService {
   public interface  BlockReason{
        String fake_account = "Sahte Hesap";
        String nudity = "Çıplaklık , Rahatsız Edici İçerik";
        String swear = "Küfür , Argo, Siber Zorbalık";
        String other = "Diğer";

    }

    private static final BlockService instance = new BlockService();

    public static BlockService shared() {  return instance;}

    public void reportReasonDialog(Activity activity , CurrentUser currentUser, OtherUser otherUser , BlockOptionSelect optionSelect){
        RecyclerView recyclerView;
        Button cancel;
        View view = LayoutInflater.from(activity.getApplicationContext())
                .inflate(R.layout.block_reason_dialog,(RelativeLayout)activity.findViewById(R.id.dialog));
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(activity,R.style.BottomSheetDialogTheme);
        ArrayList<String > items = new ArrayList<>();
        items.add(BlockReason.fake_account);
        items.add(BlockReason.nudity);
        items.add(BlockReason.swear);
        items.add(BlockReason.other);

        BlockServiceAdapter adapter = new BlockServiceAdapter(items,currentUser,otherUser , bottomSheetDialog,optionSelect);
        recyclerView = (RecyclerView)view.findViewById(R.id.optionList);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(activity));
        adapter.notifyDataSetChanged();


        cancel = (Button)view.findViewById(R.id.dismis);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                bottomSheetDialog.dismiss();
            }
        });

        bottomSheetDialog.setContentView(view);
        bottomSheetDialog.show();
    }

    public void showDialog(Context context , TrueFalse<Boolean> callback ){
        CustomDialog.show((AppCompatActivity) context, R.layout.block_dialog, new CustomDialog.OnBindView() {
            @Override
            public void onBind(CustomDialog dialog, View v) {
                dialog.setCancelable(true);

                Button okey = (Button)v.findViewById(R.id.okey);
                Button cancel = (Button)v.findViewById(R.id.cancel);

                okey.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        callback.callBack(true);
                        dialog.doDismiss();
                    }
                });
                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        callback.callBack(false);
                        dialog.doDismiss();
                    }
                });
            }
        });
    }

    public void report(Activity activity,String reportType , CurrentUser currentUser , OtherUser otherUser , TrueFalse<Boolean> callback){
        showDialog(activity, new TrueFalse<Boolean>() {
            @Override
            public void callBack(Boolean _value) {
                if (_value){
                    WaitDialog.show((AppCompatActivity) activity, "Lütfen Bekleyin");
                    ReportService.shared().setBlockReport(reportType, reportType, currentUser.getUid(), otherUser.getUid(), new TrueFalse<Boolean>() {
                        @Override
                        public void callBack(Boolean _value) {
                            if (_value){
                                addOnBlockList(currentUser, otherUser, new TrueFalse<Boolean>() {
                                    @Override
                                    public void callBack(Boolean _value) {
                                        if (_value){
                                            WaitDialog.dismiss();
                                            TipDialog.show((AppCompatActivity) activity, "Kullanıcı Engellendi", TipDialog.TYPE.SUCCESS);
                                            TipDialog.dismiss(2000);
                                            callback.callBack(true);
                                            UserService.shared().removeFromFirendList(currentUser.getUid(), otherUser.getUid(), new TrueFalse<Boolean>() {
                                                @Override
                                                public void callBack(Boolean _value) {
                                                    UserService.shared().removeFromMsgList(currentUser.getUid(), otherUser.getUid(), new TrueFalse<Boolean>() {
                                                        @Override
                                                        public void callBack(Boolean _value) {

                                                        }
                                                    });
                                                }
                                            });
                                        }
                                    }
                                });
                            }
                        }
                    });
                    UserService.shared().removeRequestBadgeCount(currentUser, otherUser, new TrueFalse<Boolean>() {
                        @Override
                        public void callBack(Boolean _value) {

                        }
                    });
                    DocumentReference ref = FirebaseFirestore.getInstance().collection("user").document(otherUser.getUid())
                            .collection("fallowers").document(currentUser.getUid());
                    ref.delete();
                    DocumentReference ref1 = FirebaseFirestore.getInstance().collection("user").document(otherUser.getUid())
                            .collection("following").document(currentUser.getUid());
                    ref1.delete();

                    DocumentReference reference = FirebaseFirestore.getInstance().collection("user").document(currentUser.getUid())
                            .collection("following").document(otherUser.getUid());
                    reference.delete();
                    DocumentReference reference1 = FirebaseFirestore.getInstance().collection("user").document(currentUser.getUid())
                            .collection("fallowers").document(otherUser.getUid());
                    reference1.delete();
                }else{

                }
            }
        });

    }
    private void addOnBlockList(CurrentUser currentUser ,OtherUser otherUser , TrueFalse<Boolean> callback){
        DocumentReference ref = FirebaseFirestore.getInstance().collection("user")
                .document(currentUser.getUid());
        Map<String  , Object> map = new HashMap<>();
        map.put("blockList", FieldValue.arrayUnion(otherUser.getUid()));
        ref.set(map, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    DocumentReference dbc = FirebaseFirestore.getInstance().collection("user")
                    .document(otherUser.getUid());
                    Map<String , Object> map1 = new HashMap<>();
                    map1.put("blockByOtherUser",FieldValue.arrayUnion(currentUser.getUid()));
                    dbc.set(map1,SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                callback.callBack(true);
                            }
                        }
                    });
                }
            }
        });
    }
    public  void removeOnBlockList(CurrentUser currentUser , OtherUser otherUser ,  TrueFalse<Boolean> callback){
        DocumentReference reference = FirebaseFirestore.getInstance().collection("user")
                .document(currentUser.getUid());
        Map<String , Object> map = new HashMap<>();
        map.put("blockList",FieldValue.arrayRemove(otherUser.getUid()));
        reference.set(map,SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                DocumentReference db = FirebaseFirestore.getInstance().collection("user")
                .document(otherUser.getUid());
                Map<String , Object> objectMap = new HashMap<>();
                map.put("blockByOtherUser",FieldValue.arrayRemove(currentUser.getUid()));
                db.set(objectMap,SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                callback.callBack(true);
                            }else{
                                callback.callBack(false);
                            }
                    }
                });
                }
            }
        });
    }

}
