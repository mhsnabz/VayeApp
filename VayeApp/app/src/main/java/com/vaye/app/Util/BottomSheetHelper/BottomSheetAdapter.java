package com.vaye.app.Util.BottomSheetHelper;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.kongzue.dialog.util.DialogSettings;
import com.kongzue.dialog.util.InputInfo;
import com.kongzue.dialog.util.TextInfo;
import com.kongzue.dialog.v3.CustomDialog;
import com.kongzue.dialog.v3.InputDialog;
import com.kongzue.dialog.v3.TipDialog;
import com.kongzue.dialog.v3.WaitDialog;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.vaye.app.Controller.HomeController.LessonPostAdapter.MajorPostViewHolder;
import com.vaye.app.Controller.HomeController.LessonPostEdit.EditPostActivity;
import com.vaye.app.Controller.ReportController.ReportActivity;
import com.vaye.app.Interfaces.Report;
import com.vaye.app.Interfaces.TrueFalse;
import com.vaye.app.Model.CurrentUser;
import com.vaye.app.Model.LessonModel;
import com.vaye.app.Model.LessonPostModel;
import com.vaye.app.R;
import com.vaye.app.Services.LessonSettingService;
import com.vaye.app.Services.MajorPostService;
import com.vaye.app.Util.Helper;

import java.net.URISyntaxException;
import java.util.ArrayList;

import io.perfmark.Link;

public class BottomSheetAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
{

    static final int view_lesson = 0;
    static final int view_currentUser = 1;
    static final int view_add_link = 2;
    static final int view_add_link_on_post = 3;
    static final int otheruser_options_target = 4;
//target ,currentUser ,activity ,model , bottomSheetDialog , lessonModel)
    public BottomSheetAdapter(String target,String link , CurrentUser currentUser, Context context, BottomSheetModel model, BottomSheetDialog dialog ) {
        this.target = target;
        this.currentUser = currentUser;
        this.context = context;
        this.model = model;
        this.dialog = dialog;
        this.selectedLink = link;
    }

    String target;
    CurrentUser currentUser;
    Context context;
    BottomSheetModel model;
    BottomSheetDialog dialog;
    LessonModel lessonModel;
    String selectedLink;
    LessonPostModel post;
    ArrayList<LessonPostModel> postModels;
//target ,currentUser ,activity ,model , bottomSheetDialog , post,lessonPostModels


    public BottomSheetAdapter(String target, CurrentUser currentUser, Context context, BottomSheetModel model, BottomSheetDialog dialog, LessonPostModel post, ArrayList<LessonPostModel> postModels) {
        this.target = target;
        this.currentUser = currentUser;
        this.context = context;
        this.model = model;
        this.dialog = dialog;
        this.post = post;
        this.postModels = postModels;
    }

    public BottomSheetAdapter(String target, CurrentUser currentUser, Context context, BottomSheetModel model, BottomSheetDialog dialog, LessonModel lessonModel) {
        this.target = target;
        this.currentUser = currentUser;
        this.context = context;
        this.model = model;
        this.dialog = dialog;
        this.lessonModel = lessonModel;
    }

    public BottomSheetAdapter(String target, CurrentUser currentUser, Context context, BottomSheetModel model, BottomSheetDialog dialog, LessonPostModel post) {
        this.target = target;
        this.currentUser = currentUser;
        this.context = context;
        this.dialog = dialog;
        this.post = post;
        this.model = model;
    }



    public BottomSheetAdapter(String target, CurrentUser currentUser, Context context, BottomSheetModel model, BottomSheetDialog dialog, LessonModel lessonModel, ArrayList<LessonPostModel> lessonPostModels ) {
        this.target = target;
        this.currentUser = currentUser;
        this.context = context;
        this.model = model;
        this.dialog = dialog;
        this.lessonModel = lessonModel;
        this.postModels = lessonPostModels;
    }

    

    @Override
    public int getItemViewType(int position) {
        if (model.getTarget().equals(BottomSheetTarget.lessonTarget)){
            return view_lesson;
        }else if (model.getTarget().equals(BottomSheetTarget.lesson_currentUser_target)){
            return view_currentUser;
        }else if (model.getTarget().equals(BottomSheetTarget.post_add_link_target)){
            return view_add_link;
        }else if (model.getTarget().equals(BottomSheetTarget.new_post_add_link_target)){
            return  view_add_link_on_post;
        }else if (model.getTarget().equals(BottomSheetTarget.otheruser_options_target)){
            return otheruser_options_target;
        }
        return super.getItemViewType(position);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        switch (viewType){
            case view_lesson:
                View itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.action_sheet_single_item, parent, false);

                return new BottomSheetLessonViewHolder(itemView);

            case view_currentUser:
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.action_sheet_single_item, parent, false);

                return new BottomSheetLessonCurrentUserViewHolder(view);

            case view_add_link:
                View view_link = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.action_sheet_single_item, parent, false);

                return new BottomSheetLessonCurrentUserViewHolder(view_link);

            case view_add_link_on_post:
                View new_view_link = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.action_sheet_single_item, parent, false);

                return new BottomSheetLessonCurrentUserViewHolder(new_view_link);

            case otheruser_options_target :
                View otherUser_view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.action_sheet_single_item, parent, false);

                return new BottomSheetLessonViewHolder(otherUser_view);


        }

        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int i)
    {
        int viewType = getItemViewType(i);
        if (viewType == view_lesson){

            BottomSheetLessonViewHolder lessonViewHolder = (BottomSheetLessonViewHolder) holder;
            Log.d("TAG", "onBindViewHolder: " + i);
             lessonViewHolder.setImageOne(model.getImagesHolder().get(i));
            lessonViewHolder.setTitle(model.getItems().get(i));
            lessonViewHolder.title.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (model.getItems().get(i).equals(BottomSheetActionTarget.dersi_takip_et)){

                        LessonSettingService.shared().addLesson(lessonModel, currentUser, (Activity) context, new TrueFalse<Boolean>() {
                            @Override
                            public void callBack(Boolean _value) {
                                if (_value){
                                    dialog.dismiss();
                                }
                            }
                        });

                    }else if (model.getItems().get(i).equals(BottomSheetActionTarget.ders_hakkında)){
                        Toast.makeText(context,"About Lesson",Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                    else if (model.getItems().get(i).equals(BottomSheetActionTarget.problem_bildir)){
                        Toast.makeText(context,"Report Lesson",Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                }
            });


        }
        else if (viewType == view_currentUser){
            BottomSheetLessonCurrentUserViewHolder currentUser_holder = (BottomSheetLessonCurrentUserViewHolder) holder;
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
                                    postModels.remove(post);

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
        }
        else if (viewType == view_add_link){

            BottomSheetLessonCurrentUserViewHolder currentUser_holder = (BottomSheetLessonCurrentUserViewHolder) holder;
            currentUser_holder.setTitle(model.getItems().get(i));
            currentUser_holder.setImageOne(model.getImagesHolder().get(i));
            currentUser_holder.title.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    CustomDialog.show((AppCompatActivity) context, R.layout.input_link_layout, new CustomDialog.OnBindView() {
                        @Override
                        public void onBind(final CustomDialog _dialog, View v) {
                        Button addLink = (Button)v.findViewById(R.id.addLink);
                        Button goDrive = (Button)v.findViewById(R.id.goDrive);
                        MaterialEditText url = (MaterialEditText)v.findViewById(R.id.link);
                        goDrive.setText(model.getItems().get(i)+ " git") ;
                        ImageButton dismiss = (ImageButton)v.findViewById(R.id.dismis);
                        dismiss.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                _dialog.doDismiss();
                            }
                        });

                        addLink.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                            String link = url.getText().toString();
                            if (link.isEmpty()){
                                _dialog.doDismiss();
                            }
                            else{
                                try {
                                    String linkName =  MajorPostService.shared().getLink(link);

                                    if (linkName.equals(LinkNames.dropbox))
                                    {
                                        MajorPostService.shared().addLink(post, link, currentUser, (Activity) context, new TrueFalse<Boolean>() {
                                            @Override
                                            public void callBack(Boolean _value) {
                                                if (_value){
                                                    post.setLink(link);
                                                    _dialog.doDismiss();
                                                    dialog.dismiss();

                                                }
                                            }
                                        });
                                    }else if (linkName.equals(LinkNames.yandex) || linkName.equals(LinkNames.yandex_tr) || linkName.equals(LinkNames.yandex_sk)){
                                        MajorPostService.shared().addLink(post, link, currentUser, (Activity) context, new TrueFalse<Boolean>() {
                                            @Override
                                            public void callBack(Boolean _value) {
                                                if (_value){
                                                    post.setLink(link);
                                                    _dialog.doDismiss();
                                                    dialog.dismiss();

                                                }
                                            }
                                        });

                                    }else if (linkName.equals(LinkNames.icloud)){
                                        MajorPostService.shared().addLink(post, link, currentUser, (Activity) context, new TrueFalse<Boolean>() {
                                            @Override
                                            public void callBack(Boolean _value) {
                                                if (_value){
                                                    post.setLink(link);
                                                    _dialog.doDismiss();
                                                    dialog.dismiss();

                                                }
                                            }
                                        });
                                    }else if (linkName.equals(LinkNames.google_drive)){
                                        MajorPostService.shared().addLink(post, link, currentUser, (Activity) context, new TrueFalse<Boolean>() {
                                            @Override
                                            public void callBack(Boolean _value) {
                                                if (_value){
                                                    post.setLink(link);
                                                    _dialog.doDismiss();
                                                    dialog.dismiss();

                                                }
                                            }
                                        });
                                    }else if (linkName.equals(LinkNames.one_drive)){
                                        MajorPostService.shared().addLink(post, link, currentUser, (Activity) context, new TrueFalse<Boolean>() {
                                            @Override
                                            public void callBack(Boolean _value) {
                                                if (_value){
                                                    post.setLink(link);
                                                    _dialog.doDismiss();
                                                    dialog.dismiss();

                                                }
                                            }
                                        });
                                    }else if (linkName.equals(LinkNames.mega)){
                                        MajorPostService.shared().addLink(post, link, currentUser, (Activity) context, new TrueFalse<Boolean>() {
                                            @Override
                                            public void callBack(Boolean _value) {
                                                if (_value){
                                                    post.setLink(link);
                                                    _dialog.doDismiss();
                                                    dialog.dismiss();

                                                }
                                            }
                                        });
                                    }else{
                                        _dialog.doDismiss();
                                        TipDialog.show((AppCompatActivity) context, "Bu Bağlantıyı Tanıyamadık" , TipDialog.TYPE.ERROR);
                                    }


                                } catch (URISyntaxException e) {
                                    e.printStackTrace();
                                }

                            }
                            }
                        });

                        goDrive.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                Intent browse = new Intent( Intent.ACTION_VIEW , Uri.parse( currentUser_holder.getLinkTarget(model.getItems().get(i)) ) );

                                context.startActivity( browse );
                            }
                        });
                        }
                    });

                }
            });

        }
        else if (viewType == view_add_link_on_post){
            BottomSheetLessonCurrentUserViewHolder currentUser_holder = (BottomSheetLessonCurrentUserViewHolder) holder;
            currentUser_holder.setTitle(model.getItems().get(i));
            currentUser_holder.setImageOne(model.getImagesHolder().get(i));
            currentUser_holder.title.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    CustomDialog.show((AppCompatActivity) context, R.layout.input_link_layout, new CustomDialog.OnBindView() {
                        @Override
                        public void onBind(final CustomDialog _dialog, View v) {
                            Button addLink = (Button)v.findViewById(R.id.addLink);
                            Button goDrive = (Button)v.findViewById(R.id.goDrive);
                            MaterialEditText url = (MaterialEditText)v.findViewById(R.id.link);
                            goDrive.setText(model.getItems().get(i)+ " git") ;
                            ImageButton dismiss = (ImageButton)v.findViewById(R.id.dismis);
                            dismiss.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    _dialog.doDismiss();
                                    selectedLink = "";
                                }
                            });

                            addLink.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    String link = url.getText().toString();
                                    if (link.isEmpty()){
                                        _dialog.doDismiss();
                                        selectedLink = "";
                                        dialog.dismiss();
                                    }
                                    else{
                                        try {
                                            String linkName =  MajorPostService.shared().getLink(link);
                                            if (MajorPostService.shared().getLink(link).equals("")){
                                                _dialog.doDismiss();
                                                selectedLink = "";
                                                dialog.dismiss();
                                                TipDialog.show((AppCompatActivity) context, "Bu Bağlantıyı Tanıyamadık" , TipDialog.TYPE.ERROR);
                                                TipDialog.dismiss(1500);
                                                return;
                                            }else if (linkName.equals(LinkNames.dropbox))
                                            {
                                                MajorPostService.shared().setLinkOnSavedTask(currentUser, link, new TrueFalse<Boolean>() {
                                                    @Override
                                                    public void callBack(Boolean _value) {
                                                        _dialog.doDismiss();
                                                        dialog.dismiss();
                                                    }
                                                });
                                            }else if (linkName.equals(LinkNames.yandex) || linkName.equals(LinkNames.yandex_tr) || linkName.equals(LinkNames.yandex_sk)){
                                                MajorPostService.shared().setLinkOnSavedTask(currentUser, link, new TrueFalse<Boolean>() {
                                                    @Override
                                                    public void callBack(Boolean _value) {
                                                        _dialog.doDismiss();
                                                        dialog.dismiss();
                                                    }
                                                });
                                            }else if (linkName.equals(LinkNames.icloud)){
                                                MajorPostService.shared().setLinkOnSavedTask(currentUser, link, new TrueFalse<Boolean>() {
                                                    @Override
                                                    public void callBack(Boolean _value) {
                                                        _dialog.doDismiss();
                                                        dialog.dismiss();
                                                    }
                                                });
                                            }else if (linkName.equals(LinkNames.google_drive)){
                                                MajorPostService.shared().setLinkOnSavedTask(currentUser, link, new TrueFalse<Boolean>() {
                                                    @Override
                                                    public void callBack(Boolean _value) {
                                                        _dialog.doDismiss();
                                                        dialog.dismiss();
                                                    }
                                                });
                                            }else if (linkName.equals(LinkNames.one_drive)){
                                                MajorPostService.shared().setLinkOnSavedTask(currentUser, link, new TrueFalse<Boolean>() {
                                                    @Override
                                                    public void callBack(Boolean _value) {
                                                        _dialog.doDismiss();
                                                        dialog.dismiss();
                                                    }
                                                });
                                            }else if (linkName.equals(LinkNames.mega)){
                                                MajorPostService.shared().setLinkOnSavedTask(currentUser, link, new TrueFalse<Boolean>() {
                                                    @Override
                                                    public void callBack(Boolean _value) {
                                                        _dialog.doDismiss();
                                                        dialog.dismiss();
                                                    }
                                                });
                                            }else{
                                                _dialog.doDismiss();
                                                selectedLink = "";
                                                dialog.dismiss();
                                                TipDialog.show((AppCompatActivity) context, "Bu Bağlantıyı Tanıyamadık" , TipDialog.TYPE.ERROR);
                                                TipDialog.dismiss(1500);
                                            }


                                        } catch (URISyntaxException e) {
                                            e.printStackTrace();
                                        }

                                    }
                                }
                            });

                            goDrive.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                    Intent browse = new Intent( Intent.ACTION_VIEW , Uri.parse( currentUser_holder.getLinkTarget(model.getItems().get(i)) ) );

                                    context.startActivity( browse );
                                }
                            });
                        }
                    });

                }
            });
        }
        else if (viewType == otheruser_options_target){
            BottomSheetLessonViewHolder otheruser_holder = (BottomSheetLessonViewHolder) holder;
            otheruser_holder.setTitle(model.getItems().get(i));
            otheruser_holder.setImageOne(model.getImagesHolder().get(i));
            otheruser_holder.setImageOne(model.getImagesHolder().get(i));
            otheruser_holder.setTitle(model.getItems().get(i));
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
                    }else if (model.getItems().get(i).equals(BottomSheetActionTarget.bu_dersi_sessize_al)){
                        Toast.makeText(context , BottomSheetActionTarget.bu_dersi_sessize_al , Toast.LENGTH_SHORT).show();

                    }else if (model.getItems().get(i).equals(BottomSheetActionTarget.bu_dersi_sessizden_al)){
                        Toast.makeText(context , BottomSheetActionTarget.bu_dersi_sessizden_al , Toast.LENGTH_SHORT).show();
                    }else if (model.getItems().get(i).equals(BottomSheetActionTarget.bu_kullaniciyi_sessize_al)){
                        Toast.makeText(context , BottomSheetActionTarget.bu_kullaniciyi_sessize_al , Toast.LENGTH_SHORT).show();
                    }
                    else if (model.getItems().get(i).equals(BottomSheetActionTarget.bu_dersi_takip_etmeyi_birak)){
                        Toast.makeText(context , BottomSheetActionTarget.bu_dersi_takip_etmeyi_birak , Toast.LENGTH_SHORT).show();
                    }
                    else if (model.getItems().get(i).equals(BottomSheetActionTarget.bu_kullaniciyi_sikayet_et)){
                        Toast.makeText(context , BottomSheetActionTarget.bu_kullaniciyi_sikayet_et , Toast.LENGTH_SHORT).show();
                        Intent i = new Intent(context , ReportActivity.class);
                        i.putExtra("otherUser",post.getSenderUid());
                        i.putExtra("postId",post.getPostId());
                        i.putExtra("target", Report.ReportTarget.homePost);
                        i.putExtra("reportType", Report.ReportType.reportUser);
                        i.putExtra("currentUser",currentUser);
                        context.startActivity(i);
                        Helper.shared().go((Activity) context);
                        dialog.dismiss();
                    }

                }
            });
        }
    }



    @Override
    public int getItemCount() {
        return model.getImagesHolder().size();
    }



    public class BottomSheetLessonViewHolder extends RecyclerView.ViewHolder {
        public BottomSheetLessonViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        ImageButton imageView = (ImageButton)itemView.findViewById(R.id.image);
        TextView title = (TextView) itemView.findViewById(R.id.title);
        void setImageOne(int _res){
                imageView.setImageResource(_res);
        }
        void setTitle(String text){
            title.setText(text);
        }

    }

    class BottomSheetLessonCurrentUserViewHolder extends  RecyclerView.ViewHolder{

        public BottomSheetLessonCurrentUserViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        ImageButton imageView = (ImageButton)itemView.findViewById(R.id.image);
        TextView title = (TextView) itemView.findViewById(R.id.title);
        void setImageOne(int _res){
            imageView.setImageResource(_res);
        }
        void setTitle(String text){
            title.setText(text);
        }

        public String getLinkTarget(String item){
            if (item.equals(BottomSheetActionTarget.dropbox))
                return  LinkTarget.dropbox;
            else if (item.equals(BottomSheetActionTarget.google_drive))
                return  LinkTarget.google_drive;
            else if (item.equals(BottomSheetActionTarget.yandex_disk))
                return LinkTarget.yandex;
            else if (item.equals(BottomSheetActionTarget.iClould))
                return LinkTarget.iClould;
            else if (item.equals(BottomSheetActionTarget.mega))
                return LinkTarget.mega;
            else if (item.equals(BottomSheetActionTarget.one_drive))
                return LinkTarget.oneDrive;
            return  "";
        }
    }


}
