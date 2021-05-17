package com.vaye.app.Util.BottomSheetHelper;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.kongzue.dialog.v3.WaitDialog;
import com.vaye.app.Controller.HomeController.HomeActivity;
import com.vaye.app.Controller.HomeController.LessonPostEdit.EditPostActivity;
import com.vaye.app.Controller.ReportController.ReportActivity;
import com.vaye.app.Interfaces.BlockOptionSelect;
import com.vaye.app.Interfaces.OtherUserService;
import com.vaye.app.Interfaces.Report;
import com.vaye.app.Interfaces.TrueFalse;
import com.vaye.app.Model.CurrentUser;
import com.vaye.app.Model.LessonModel;
import com.vaye.app.Model.LessonPostModel;
import com.vaye.app.Model.MainPostModel;
import com.vaye.app.Model.OtherUser;
import com.vaye.app.R;
import com.vaye.app.Services.BlockService;
import com.vaye.app.Services.LessonSettingService;
import com.vaye.app.Services.MainPostService;
import com.vaye.app.Services.MajorPostService;
import com.vaye.app.Services.UserService;
import com.vaye.app.Util.Helper;

import java.util.ArrayList;

public class MajorPostBottomAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    static final int current_user_options= 0;
    static final int other_user_options= 1;
    LessonPostModel post;
    CurrentUser currentUser;
    BottomSheetModel model;
    BottomSheetDialog dialog;
    OtherUser otherUser;
    Context context;
    ArrayList<LessonPostModel> allPost;
    BlockOptionSelect optionSelect;
    public MajorPostBottomAdapter(ArrayList<LessonPostModel> allPost,LessonPostModel post, CurrentUser currentUser, BottomSheetModel model, BottomSheetDialog dialog, OtherUser otherUser, Context context,BlockOptionSelect optionSelect) {
        this.post = post;
        this.currentUser = currentUser;
        this.model = model;
        this.dialog = dialog;
        this.otherUser = otherUser;
        this.context = context;
        this.allPost = allPost;
        this.optionSelect = optionSelect;
    }

    /**
     * current user consctructur
     * */
    public MajorPostBottomAdapter(ArrayList<LessonPostModel> allPost,LessonPostModel post, CurrentUser currentUser, BottomSheetModel model, BottomSheetDialog dialog, Context context) {
        this.post = post;
        this.currentUser = currentUser;
        this.model = model;
        this.dialog = dialog;
        this.context = context;
        this.allPost = allPost;
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType){
            case current_user_options :
                View itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.action_sheet_single_item, parent, false);

                return new VayeAppBottomSheetLauncherViewHolder(itemView);

            case  other_user_options :
                View itemView1 = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.action_sheet_single_item, parent, false);

                return new VayeAppBottomSheetLauncherViewHolder(itemView1);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int i) {
        int viewType = getItemViewType(i);
        switch (viewType){
            case current_user_options:
              VayeAppBottomSheetLauncherViewHolder currentUser_holder = (VayeAppBottomSheetLauncherViewHolder) holder;
                currentUser_holder.setTitle(model.getItems().get(i));
                currentUser_holder.setImageOne(model.getImagesHolder().get(i));
                currentUser_holder.title.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (model.getItems().get(i).equals(BottomSheetActionTarget.gonderiyi_düzenle)){

                            Intent i = new Intent(context , EditPostActivity.class);
                            i.putExtra("currentUser",currentUser);
                            i.putExtra("post",post);
                            context.startActivity(i);
                            Helper.shared().go((Activity) context);
                            dialog.dismiss();
                        }
                        else if (model.getItems().get(i).equals(BottomSheetActionTarget.gonderiyi_sil))
                        {
                            MajorPostService.shared().deleteCurrentUserPost((Activity) context, currentUser, post, new TrueFalse<Boolean>() {
                                @Override
                                public void callBack(Boolean _value) {
                                    if (_value){
                                        WaitDialog.dismiss();
                                        allPost.remove(post);

                                        dialog.dismiss();
                                    }
                                }
                            });

                        }else if (model.getItems().get(i).equals(BottomSheetActionTarget.gonderiyi_sessize_al)){
                            MajorPostService.shared().setCurrentUserPostSlient((Activity) context, currentUser, post, new TrueFalse<Boolean>() {
                                @Override
                                public void callBack(Boolean _value) {
                                    if (_value){
                                        dialog.dismiss();;

                                    }
                                }
                            });
                        }else if (model.getItems().get(i).equals(BottomSheetActionTarget.gonderi_bildirimlerini_ac)){
                            MajorPostService.shared().setCurrentUserPostNotSilent((Activity) context, currentUser, post, new TrueFalse<Boolean>() {
                                @Override
                                public void callBack(Boolean _value) {
                                    if (_value)
                                        dialog.dismiss();

                                }
                            });

                        }
                    }
                });
                break;
            case other_user_options:
                VayeAppBottomSheetLauncherViewHolder otheruser_holder = (VayeAppBottomSheetLauncherViewHolder) holder;
                otheruser_holder.setTitle(model.getItems().get(i));
                otheruser_holder.setImageOne(model.getImagesHolder().get(i));
               otheruser_holder.title.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (model.getItems().get(i).equals(BottomSheetActionTarget.bu_gonderiyi_sikayet_et)){
                        Intent i = new Intent(context , ReportActivity.class);
                        i.putExtra("otherUser",post.getSenderUid());
                        i.putExtra("postId",post.getPostId());
                        i.putExtra("target", Report.ReportTarget.homePost);
                        i.putExtra("reportType", Report.ReportType.reportPost);
                        i.putExtra("currentUser",currentUser);
                        context.startActivity(i);
                        Helper.shared().go((Activity) context);
                        dialog.dismiss();
                    }else if (model.getItems().get(i).equals(BottomSheetActionTarget.bu_kullaniciyi_sessize_al)){
                        MainPostService.shared().setUserSlient(currentUser, otherUser, new TrueFalse<Boolean>() {
                            @Override
                            public void callBack(Boolean _value) {

                                dialog.dismiss();
                            }
                        });
                    }
                    else if (model.getItems().get(i).equals(BottomSheetActionTarget.bu_kullaniciyi_sessiden_al)){
                        MainPostService.shared().setUserNotSlient(currentUser, otherUser, new TrueFalse<Boolean>() {
                            @Override
                            public void callBack(Boolean _value) {
                                dialog.dismiss();
                            }
                        });
                    }
                    else if (model.getItems().get(i).equals(BottomSheetActionTarget.bu_dersi_takip_etmeyi_birak)){
                        ///İSTE/lesson/Bilgisayar Mühendisliği/Bilgisayar Ağları
                        DocumentReference ref = FirebaseFirestore.getInstance().collection(currentUser.getShort_school())
                                .document("lesson").collection(currentUser.getBolum()).document(post.getLessonName());
                        ref.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()){
                                    LessonModel model = task.getResult().toObject(LessonModel.class);
                                    LessonSettingService.shared().removeLesson(model,currentUser,(Activity)context);
                                    dialog.dismiss();
                                }
                            }
                        });
                    }
                    else if (model.getItems().get(i).equals(BottomSheetActionTarget.bu_kullaniciyi_sikayet_et)){
                        Intent i = new Intent(context , ReportActivity.class);
                        i.putExtra("otherUser",post.getSenderUid());
                        i.putExtra("postId",post.getPostId());
                        i.putExtra("target", Report.ReportTarget.homePost);
                        i.putExtra("reportType", Report.ReportType.reportUser);
                        i.putExtra("currentUser",currentUser);
                        context.startActivity(i);
                        Helper.shared().go((Activity) context);
                        dialog.dismiss();
                    }else if (model.getItems().get(i).equals(BottomSheetActionTarget.getBu_kullaniciyi_engelle)){
                        WaitDialog.show((AppCompatActivity)context,null );
                        UserService.shared().getOtherUserById(post.getSenderUid(), new OtherUserService() {
                            @Override
                            public void callback(OtherUser user) {
                                if (user!=null){

                                    BlockService.shared().reportReasonDialog((Activity)context,currentUser,user,optionSelect);
                                    WaitDialog.dismiss();
                                    dialog.dismiss();
                                }
                            }
                        });
                    }

                }
            });
                break;

        }
    }
    @Override
    public int getItemViewType(int position) {
        if (model.getTarget().equals(BottomSheetTarget.major_currentUser_launcher)){
            return current_user_options;
        }else if (model.getTarget().equals(BottomSheetTarget.major_OtherUser_launcher)){
            return other_user_options;
        }
        return super.getItemViewType(position);
    }
    @Override
    public int getItemCount() {
        return model.getImagesHolder().size();
    }
}
