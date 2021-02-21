package com.vaye.app.Util.BottomSheetHelper;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.kongzue.dialog.v3.CustomDialog;
import com.kongzue.dialog.v3.TipDialog;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.vaye.app.Interfaces.TrueFalse;
import com.vaye.app.Model.CurrentUser;
import com.vaye.app.Model.LessonPostModel;
import com.vaye.app.Model.MainPostModel;
import com.vaye.app.Model.OtherUser;
import com.vaye.app.R;
import com.vaye.app.Services.MajorPostService;

import java.net.URISyntaxException;
import java.util.ArrayList;

public class BottomSheetLinkAdapter  extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    static final int view_add_link_on_post = 1;
    CurrentUser currentUser;
    BottomSheetModel model;
    BottomSheetDialog dialog;
    LessonPostModel post;
    Context context;
    String target;

    public BottomSheetLinkAdapter(CurrentUser currentUser, BottomSheetModel model, BottomSheetDialog dialog, LessonPostModel post, Context context, String target) {
        this.currentUser = currentUser;
        this.model = model;
        this.dialog = dialog;
        this.post = post;
        this.context = context;
        this.target = target;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType){
            case view_add_link_on_post:
                View itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.action_sheet_single_item, parent, false);

                return new VayeAppBottomSheetLauncherViewHolder(itemView);
        }
        return null;

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int i) {
        VayeAppBottomSheetLauncherViewHolder currentUser_holder = (VayeAppBottomSheetLauncherViewHolder) holder;
        currentUser_holder.setTitle(model.getItems().get(i));
        currentUser_holder.setImageOne(model.getImagesHolder().get(i));
        currentUser_holder.title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CustomDialog.show((AppCompatActivity) context, R.layout.input_link_layout, new CustomDialog.OnBindView() {
                    @Override
                    public void onBind(CustomDialog _dialog, View v) {
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
                                }else{
                                    goDrive.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {

                                            Intent browse = new Intent( Intent.ACTION_VIEW , Uri.parse( currentUser_holder.getLinkTarget(model.getItems().get(i)) ) );

                                            context.startActivity( browse );
                                        }
                                    });
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
                    }
                });
            }
        });
    }
    @Override
    public int getItemViewType(int position) {
        if (model.getTarget().equals(BottomSheetTarget.add_link)){
            return view_add_link_on_post;
        }

        return super.getItemViewType(position);
    }
    @Override
    public int getItemCount() {
        return 0;
    }
}
