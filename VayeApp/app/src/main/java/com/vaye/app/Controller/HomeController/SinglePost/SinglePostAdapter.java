package com.vaye.app.Controller.HomeController.SinglePost;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.hendraanggrian.appcompat.widget.SocialView;
import com.kongzue.dialog.v3.TipDialog;
import com.kongzue.dialog.v3.WaitDialog;
import com.vaye.app.Controller.CommentController.CommentActivity;
import com.vaye.app.Controller.HomeController.LessonPostAdapter.MajorPostViewHolder;
import com.vaye.app.Controller.HomeController.PagerAdapter.AllDatasActivity;
import com.vaye.app.Controller.HomeController.SchoolPostAdapter.SchoolPostViewHolder;
import com.vaye.app.Controller.Profile.CurrentUserProfile;
import com.vaye.app.Controller.Profile.OtherUserProfileActivity;
import com.vaye.app.Controller.VayeAppController.BuySell.BuySellViewHolder;
import com.vaye.app.Controller.VayeAppController.Camping.CampingViewHolder;
import com.vaye.app.Controller.VayeAppController.FoodMe.FoodMeViewHolder;
import com.vaye.app.Interfaces.BlockOptionSelect;
import com.vaye.app.Interfaces.Notifications;
import com.vaye.app.Interfaces.OtherUserService;
import com.vaye.app.Interfaces.StringCompletion;
import com.vaye.app.Interfaces.TrueFalse;
import com.vaye.app.Model.CurrentUser;
import com.vaye.app.Model.LessonPostModel;
import com.vaye.app.Model.MainPostModel;
import com.vaye.app.Model.NoticesMainModel;
import com.vaye.app.Model.OtherUser;
import com.vaye.app.R;
import com.vaye.app.Services.MainPostService;
import com.vaye.app.Services.MajorPostService;
import com.vaye.app.Services.SchoolPostService;
import com.vaye.app.Services.UserService;
import com.vaye.app.Util.Helper;

import java.util.ArrayList;

public class SinglePostAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    LessonPostModel lessonPostModel;
    NoticesMainModel noticesMainModel;
    MainPostModel mainPostModel;
    CurrentUser currentUser;
    Context context;
    BlockOptionSelect optionSelect;
    private static final int VIEW_TYPE_LESSON_POST = 1;
    private static final String TAG = "SinglePostAdapter";
    private static final int VIEW_TYPE_LESSON_POST_DATA  = 2;

    private static final int VIEW_TYPE_SCHOOL_POST = 3;
    private static final int VIEW_TYPE_SCHOOL_POST_DATA  = 4;


    private static final int FOOD_ME_POST = 11;
    private static final int FOOD_ME_DATA_POST = 12;
    //BUY SELL INIT
    private static final int BUYSELL = 2;
    private static final int BUYSELL__POST = 21;
    private static final int BUYSELL_DATA_POST = 22;


    ///CAMPING INIT
    private static final int CAMPING = 3;
    private static final int CAMPING__POST = 31;
    private static final int CAMPING_DATA_POST = 32;


    Boolean istanceOfCurrentUserProfile = false;
    Boolean istanceOfOtherUserProfile = false;
    public SinglePostAdapter(LessonPostModel lessonPostModel, CurrentUser currentUser, Context context , BlockOptionSelect optionSelect) {
        this.lessonPostModel = lessonPostModel;
        this.currentUser = currentUser;
        this.context = context;
        this.optionSelect = optionSelect;
        if (context instanceof CurrentUserProfile){
            Log.d("FollowersAdapter", "FollowersAdapter: " + "instanceof CurrentUserProfile");
            istanceOfCurrentUserProfile = true;

        }else{

            Log.d("FollowersAdapter", "FollowersAdapter: " + "not instanceof CurrentUserProfile");
            istanceOfCurrentUserProfile = false;
        }

        if (context instanceof  OtherUserProfileActivity){
            istanceOfOtherUserProfile= true;
        }else{
            istanceOfOtherUserProfile = false;
        }
    }

    public SinglePostAdapter(NoticesMainModel noticesMainModel, CurrentUser currentUser, Context context , BlockOptionSelect optionSelect) {
        this.noticesMainModel = noticesMainModel;
        this.currentUser = currentUser;
        this.context = context;
        this.optionSelect = optionSelect;
        if (context instanceof CurrentUserProfile){
            Log.d("FollowersAdapter", "FollowersAdapter: " + "instanceof CurrentUserProfile");
            istanceOfCurrentUserProfile = true;

        }else{

            Log.d("FollowersAdapter", "FollowersAdapter: " + "not instanceof CurrentUserProfile");
            istanceOfCurrentUserProfile = false;
        }

        if (context instanceof  OtherUserProfileActivity){
            istanceOfOtherUserProfile= true;
        }else{
            istanceOfOtherUserProfile = false;
        }
    }

    public SinglePostAdapter(MainPostModel mainPostModel, CurrentUser currentUser, Context context , BlockOptionSelect optionSelect) {
        Log.d(TAG, "SinglePostAdapter: " + mainPostModel.getType());
        Log.d(TAG, "SinglePostAdapter: " + mainPostModel.getPostType());
        Log.d(TAG, "SinglePostAdapter: " + mainPostModel.getId());

        this.mainPostModel = mainPostModel;
        this.currentUser = currentUser;
        this.context = context;
        this.optionSelect = optionSelect;
        if (context instanceof CurrentUserProfile){
            Log.d("FollowersAdapter", "FollowersAdapter: " + "instanceof CurrentUserProfile");
            istanceOfCurrentUserProfile = true;

        }else{

            Log.d("FollowersAdapter", "FollowersAdapter: " + "not instanceof CurrentUserProfile");
            istanceOfCurrentUserProfile = false;
        }

        if (context instanceof  OtherUserProfileActivity){
            istanceOfOtherUserProfile= true;
        }else{
            istanceOfOtherUserProfile = false;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_LESSON_POST){
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.single_lesson_post_item, parent, false);

            return new MajorPostViewHolder(itemView);
        }
        else if (viewType == VIEW_TYPE_LESSON_POST_DATA){
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.single_lesson_post_data_item, parent, false);

            return new MajorPostViewHolder(itemView);

        }else  if (viewType == VIEW_TYPE_SCHOOL_POST){
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.school_post_cell, parent, false);

            return new SchoolPostViewHolder(itemView);
        }
        else if (viewType == VIEW_TYPE_SCHOOL_POST_DATA){
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.school_data_post_cell, parent, false);

            return new SchoolPostViewHolder(itemView);

        }else if (viewType == FOOD_ME_POST){
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.food_me_cell, parent, false);

            return new FoodMeViewHolder(itemView);
        }
        else if (viewType == FOOD_ME_DATA_POST){
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.food_me_data_cell, parent, false);

            return new FoodMeViewHolder(itemView);

        }else if (viewType == CAMPING__POST){
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.food_me_cell, parent, false);

            return new CampingViewHolder(itemView);

        }else if (viewType == CAMPING_DATA_POST){
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.food_me_data_cell, parent, false);

            return new CampingViewHolder(itemView);

        }else if (viewType == BUYSELL__POST){
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.buy_sell_cell, parent, false);

            return new BuySellViewHolder(itemView);
        }else if (viewType == BUYSELL_DATA_POST){
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.buy_sell_data_cell, parent, false);

            return new BuySellViewHolder(itemView);
        }
        return  null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int i) {
        int viewType = getItemViewType(i);
        switch (viewType) {

            case BUYSELL__POST:
                BuySellViewHolder buy_sell_post_holder = (BuySellViewHolder) holder;
                    MainPostModel buy_sell_item = mainPostModel;
                buy_sell_post_holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(context , CommentActivity.class);
                        intent.putExtra("mainPost",buy_sell_item);
                        intent.putExtra("currentUser",currentUser);
                        context.startActivity(intent);
                        Helper.shared().go((Activity) context);
                    }
                });
                buy_sell_post_holder.comment.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(context , CommentActivity.class);
                        intent.putExtra("mainPost",buy_sell_item);
                        intent.putExtra("currentUser",currentUser);
                        context.startActivity(intent);
                        Helper.shared().go((Activity) context);
                    }
                });
                buy_sell_post_holder.setCommentLbl(buy_sell_item.getComment());
                buy_sell_post_holder.setName(buy_sell_item.getSenderName());
                buy_sell_post_holder.setUserName(buy_sell_item.getUsername());
                buy_sell_post_holder.setProfileImage(buy_sell_item.getThumb_image());

                buy_sell_post_holder.setText(buy_sell_item.getText());
                buy_sell_post_holder.setDislikeLbl(buy_sell_item.getDislike());
                buy_sell_post_holder.setLikeLbl(buy_sell_item.getLikes());
                buy_sell_post_holder.setLike(buy_sell_item.getLikes(),currentUser , context);
                buy_sell_post_holder.setDislike(buy_sell_item.getDislike(),currentUser , context);

                buy_sell_post_holder.setTime(buy_sell_item.getPostTime());
                buy_sell_post_holder.setLocationButton(buy_sell_item.getGeoPoint());

                buy_sell_post_holder.itemView.findViewById(R.id.profileLay).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (buy_sell_item.getSenderUid().equals(currentUser.getUid())){
                            if (!istanceOfCurrentUserProfile){
                                Intent i = new Intent(context , CurrentUserProfile.class);
                                i.putExtra("currentUser",currentUser);
                                context.startActivity(i);
                                Helper.shared().go((Activity) context);
                            }else{
                                return;
                            }

                        }else{
                            if (!istanceOfOtherUserProfile){
                                UserService.shared().getOtherUser((Activity) context, buy_sell_item.getSenderUid(), new OtherUserService() {
                                    @Override
                                    public void callback(OtherUser user) {
                                        Intent i  = new Intent(context , OtherUserProfileActivity.class);
                                        i.putExtra("otherUser",user);
                                        i.putExtra("currentUser",currentUser);
                                        context.startActivity(i);
                                        Helper.shared().go((Activity) context);
                                        WaitDialog.dismiss();
                                    }
                                });
                            }else{
                                return;
                            }

                        }
                    }
                });
                buy_sell_post_holder.itemView.findViewById(R.id.name).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (buy_sell_item.getSenderUid().equals(currentUser.getUid())){
                            if (!istanceOfCurrentUserProfile){
                                Intent i = new Intent(context , CurrentUserProfile.class);
                                i.putExtra("currentUser",currentUser);
                                context.startActivity(i);
                                Helper.shared().go((Activity) context);
                            }else{
                                return;
                            }

                        }else{
                            if (!istanceOfOtherUserProfile){
                                UserService.shared().getOtherUser((Activity) context, buy_sell_item.getSenderUid(), new OtherUserService() {
                                    @Override
                                    public void callback(OtherUser user) {
                                        Intent i  = new Intent(context , OtherUserProfileActivity.class);
                                        i.putExtra("otherUser",user);
                                        i.putExtra("currentUser",currentUser);
                                        context.startActivity(i);
                                        Helper.shared().go((Activity) context);
                                        WaitDialog.dismiss();
                                    }
                                });
                            }else{
                                return;
                            }

                        }

                    }
                });
                buy_sell_post_holder.text.setOnHyperlinkClickListener(new SocialView.OnClickListener() {
                    @Override
                    public void onClick(@NonNull SocialView view, @NonNull CharSequence text) {
                        String url = text.toString();
                        if (!url.startsWith("http://") && !url.startsWith("https://"))
                            url = "http://" + url;
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        context.startActivity(browserIntent);
                    }
                });
                buy_sell_post_holder.text.setOnMentionClickListener(new SocialView.OnClickListener() {
                    @Override
                    public void onClick(@NonNull SocialView view, @NonNull CharSequence username) {

                        String _username = "@"+username.toString();
                        if (_username.equals(currentUser.getUsername())){
                            if (!istanceOfCurrentUserProfile){
                                Intent i = new Intent(context , CurrentUserProfile.class);
                                i.putExtra("currentUser",currentUser);
                                context.startActivity(i);
                                Helper.shared().go((Activity) context);
                            }

                        }else{
                            if (!istanceOfOtherUserProfile){
                                UserService.shared().getOthUserIdByMention("@"+username.toString(), new StringCompletion() {
                                    @Override
                                    public void getString(String otherUserId) {
                                        if (otherUserId!=null){
                                            UserService.shared().getOtherUser((Activity) context, otherUserId, new OtherUserService() {
                                                @Override
                                                public void callback(OtherUser user) {
                                                    if (user!=null){
                                                        WaitDialog.dismiss();
                                                        Intent i = new Intent(context , OtherUserProfileActivity.class);
                                                        i.putExtra("currentUser",currentUser);
                                                        i.putExtra("otherUser",user);
                                                        context.startActivity(i);
                                                        Helper.shared().go((Activity) context);
                                                    }
                                                }
                                            });
                                        }else{
                                            TipDialog.show((AppCompatActivity) context, "Böyle Bir Kullanıcı Bulunmuyor", TipDialog.TYPE.ERROR);
                                            TipDialog.dismiss(1000);
                                        }

                                    }
                                });
                            }


                        }

                    }
                });
                buy_sell_post_holder.itemView.findViewById(R.id.locationButton).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String uri = "http://maps.google.com/maps?saddr=" + buy_sell_item.getGeoPoint().getLatitude() + "," + buy_sell_item.getGeoPoint().getLongitude()  + "&daddr=" ;
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                        context.startActivity(intent);
                    }
                });
                buy_sell_post_holder.itemView.findViewById(R.id.like).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        MainPostService.shared().setPostLike(buy_sell_item, currentUser, Notifications.NotificationType.like_food_me, Notifications.NotificationDescription.like_food_me, new TrueFalse<Boolean>() {
                            @Override
                            public void callBack(Boolean _value) {
                                buy_sell_post_holder.setLike(buy_sell_item.getLikes(),currentUser ,context);
                                notifyDataSetChanged();
                            }
                        });

                    }
                });

                buy_sell_post_holder.itemView.findViewById(R.id.dislike).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        MainPostService.shared().setPostDislike(buy_sell_item, currentUser, new TrueFalse<Boolean>() {
                            @Override
                            public void callBack(Boolean _value) {
                                buy_sell_post_holder.setDislike(buy_sell_item.getDislike(),currentUser,context);
                                notifyDataSetChanged();
                            }
                        });
                    }
                });
                buy_sell_post_holder.itemView.findViewById(R.id.locationButton).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String uri = "http://maps.google.com/maps?saddr=" + buy_sell_item.getGeoPoint().getLatitude() + "," + buy_sell_item.getGeoPoint().getLongitude()  + "&daddr=" ;
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                        context.startActivity(intent);
                    }
                });

                buy_sell_post_holder.itemView.findViewById(R.id.setting).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (buy_sell_item.getSenderUid().equals(currentUser.getUid())){
                            ArrayList<MainPostModel> mainPostModels = new ArrayList<>();
                            mainPostModels.add(mainPostModel);
                            Helper.shared().VayeAppCurrentUserBottomSheetLauncher(mainPostModels,(Activity) context, currentUser, buy_sell_item, new TrueFalse<Boolean>() {
                                @Override
                                public void callBack(Boolean _value) {
                                    if (_value){
                                        notifyDataSetChanged();
                                    }
                                }
                            });
                        }else{
                            UserService.shared().getOtherUser((Activity) context, buy_sell_item.getSenderUid(), new OtherUserService() {
                                @Override
                                public void callback(OtherUser user) {
                                    ArrayList<MainPostModel> mainPostModels = new ArrayList<>();
                                    mainPostModels.add(mainPostModel);
                                    Helper.shared().VayeAppOtherUserBottomSheetLauncher(mainPostModels,(Activity) context, user, currentUser, buy_sell_item, new TrueFalse<Boolean>() {
                                        @Override
                                        public void callBack(Boolean _value) {
                                            if (_value){
                                                notifyDataSetChanged();
                                            }
                                        }
                                    });
                                    WaitDialog.dismiss();
                                }
                            });
                        }
                    }
                });
                buy_sell_post_holder.setValue(buy_sell_item.getValue());
                break;
            case BUYSELL_DATA_POST:
                BuySellViewHolder buy_sell_post_data_holder = (BuySellViewHolder) holder;
                MainPostModel buy_sell_item_data = mainPostModel;
                buy_sell_post_data_holder.setValue(buy_sell_item_data.getValue());
                buy_sell_post_data_holder.setCommentLbl(buy_sell_item_data.getComment());
                buy_sell_post_data_holder.setImages(buy_sell_item_data.getThumbData());
                buy_sell_post_data_holder.setName(buy_sell_item_data.getSenderName());
                buy_sell_post_data_holder.setUserName(buy_sell_item_data.getUsername());
                buy_sell_post_data_holder.setProfileImage(buy_sell_item_data.getThumb_image());
                buy_sell_post_data_holder.setText(buy_sell_item_data.getText());
                buy_sell_post_data_holder.setDislikeLbl(buy_sell_item_data.getDislike());
                buy_sell_post_data_holder.setLikeLbl(buy_sell_item_data.getLikes());
                buy_sell_post_data_holder.setLike(buy_sell_item_data.getLikes(),currentUser , context);
                buy_sell_post_data_holder.setDislike(buy_sell_item_data.getDislike(),currentUser , context);
                buy_sell_post_data_holder.setTime(buy_sell_item_data.getPostTime());
                buy_sell_post_data_holder.itemView.findViewById(R.id.profileLay).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if (buy_sell_item_data.getSenderUid().equals(currentUser.getUid())){
                            if (!istanceOfCurrentUserProfile){
                                Intent i = new Intent(context , CurrentUserProfile.class);
                                i.putExtra("currentUser",currentUser);
                                context.startActivity(i);
                                Helper.shared().go((Activity) context);
                            }else{
                                return;
                            }

                        }else{
                            if (!istanceOfOtherUserProfile){
                                UserService.shared().getOtherUser((Activity) context, buy_sell_item_data.getSenderUid(), new OtherUserService() {
                                    @Override
                                    public void callback(OtherUser user) {
                                        Intent i  = new Intent(context , OtherUserProfileActivity.class);
                                        i.putExtra("otherUser",user);
                                        i.putExtra("currentUser",currentUser);
                                        context.startActivity(i);
                                        Helper.shared().go((Activity) context);
                                        WaitDialog.dismiss();
                                    }
                                });
                            }else{
                                return;
                            }

                        }
                    }
                });
                buy_sell_post_data_holder.itemView.findViewById(R.id.name).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if (buy_sell_item_data.getSenderUid().equals(currentUser.getUid())){
                            if (!istanceOfCurrentUserProfile){
                                Intent i = new Intent(context , CurrentUserProfile.class);
                                i.putExtra("currentUser",currentUser);
                                context.startActivity(i);
                                Helper.shared().go((Activity) context);
                            }else{
                                return;
                            }

                        }else{
                            if (!istanceOfOtherUserProfile){
                                UserService.shared().getOtherUser((Activity) context, buy_sell_item_data.getSenderUid(), new OtherUserService() {
                                    @Override
                                    public void callback(OtherUser user) {
                                        Intent i  = new Intent(context , OtherUserProfileActivity.class);
                                        i.putExtra("otherUser",user);
                                        i.putExtra("currentUser",currentUser);
                                        context.startActivity(i);
                                        Helper.shared().go((Activity) context);
                                        WaitDialog.dismiss();
                                    }
                                });
                            }else{
                                return;
                            }

                        }
                    }
                });
                buy_sell_post_data_holder.setLocationButton(buy_sell_item_data.getGeoPoint());
                buy_sell_post_data_holder.text.setOnHyperlinkClickListener(new SocialView.OnClickListener() {
                    @Override
                    public void onClick(@NonNull SocialView view, @NonNull CharSequence text) {
                        String url = text.toString();
                        if (!url.startsWith("http://") && !url.startsWith("https://"))
                            url = "http://" + url;
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        context.startActivity(browserIntent);
                    }
                });
                buy_sell_post_data_holder.text.setOnMentionClickListener(new SocialView.OnClickListener() {
                    @Override
                    public void onClick(@NonNull SocialView view, @NonNull CharSequence username) {

                        String _username = "@"+username.toString();
                        if (_username.equals(currentUser.getUsername())){
                            if (!istanceOfCurrentUserProfile){
                                Intent i = new Intent(context , CurrentUserProfile.class);
                                i.putExtra("currentUser",currentUser);
                                context.startActivity(i);
                                Helper.shared().go((Activity) context);
                            }

                        }else{
                            if (!istanceOfOtherUserProfile){
                                UserService.shared().getOthUserIdByMention("@"+username.toString(), new StringCompletion() {
                                    @Override
                                    public void getString(String otherUserId) {
                                        if (otherUserId!=null){
                                            UserService.shared().getOtherUser((Activity) context, otherUserId, new OtherUserService() {
                                                @Override
                                                public void callback(OtherUser user) {
                                                    if (user!=null){
                                                        WaitDialog.dismiss();
                                                        Intent i = new Intent(context , OtherUserProfileActivity.class);
                                                        i.putExtra("currentUser",currentUser);
                                                        i.putExtra("otherUser",user);
                                                        context.startActivity(i);
                                                        Helper.shared().go((Activity) context);
                                                    }
                                                }
                                            });
                                        }else{
                                            TipDialog.show((AppCompatActivity) context, "Böyle Bir Kullanıcı Bulunmuyor", TipDialog.TYPE.ERROR);
                                            TipDialog.dismiss(1000);
                                        }

                                    }
                                });

                            }

                        }
                    }

                });
                buy_sell_post_data_holder.itemView.findViewById(R.id.text).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                    }
                });
                buy_sell_post_data_holder.itemView.findViewById(R.id.locationButton).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String uri = "http://maps.google.com/maps?saddr=" + buy_sell_item_data.getGeoPoint().getLatitude() + "," + buy_sell_item_data.getGeoPoint().getLongitude()  + "&daddr=" ;
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                        context.startActivity(intent);
                    }
                });
                buy_sell_post_data_holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(context , CommentActivity.class);
                        intent.putExtra("mainPost",buy_sell_item_data);
                        intent.putExtra("currentUser",currentUser);
                        context.startActivity(intent);
                        Helper.shared().go((Activity) context);
                    }
                });
                buy_sell_post_data_holder.comment.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(context , CommentActivity.class);
                        intent.putExtra("mainPost",buy_sell_item_data);
                        intent.putExtra("currentUser",currentUser);
                        context.startActivity(intent);
                        Helper.shared().go((Activity) context);
                    }
                });
                buy_sell_post_data_holder.itemView.findViewById(R.id.like).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        MainPostService.shared().setPostLike(buy_sell_item_data, currentUser, Notifications.NotificationType.like_food_me, Notifications.NotificationDescription.like_food_me, new TrueFalse<Boolean>() {
                            @Override
                            public void callBack(Boolean _value) {
                                buy_sell_post_data_holder.setLike(buy_sell_item_data.getLikes(),currentUser ,context);
                                notifyDataSetChanged();
                            }
                        });
                    }
                });

                buy_sell_post_data_holder.itemView.findViewById(R.id.dislike).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        MainPostService.shared().setPostDislike(buy_sell_item_data, currentUser, new TrueFalse<Boolean>() {
                            @Override
                            public void callBack(Boolean _value) {
                                if (_value){
                                    buy_sell_post_data_holder.setDislike(buy_sell_item_data.getDislike(),currentUser,context);
                                    notifyDataSetChanged();
                                }
                            }
                        });
                    }
                });


                buy_sell_post_data_holder.itemView.findViewById(R.id.one_image).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent i = new Intent(context , AllDatasActivity.class);
                        i.putExtra("url",buy_sell_item_data.getData());
                        context.startActivity(i);
                        Helper.shared().go((Activity) context);
                    }
                });
                buy_sell_post_data_holder.itemView.findViewById(R.id.two_image).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent i = new Intent(context , AllDatasActivity.class);
                        i.putExtra("url",buy_sell_item_data.getData());

                        context.startActivity(i);
                        Helper.shared().go((Activity) context);
                    }
                });
                buy_sell_post_data_holder.itemView.findViewById(R.id.there_image).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent i = new Intent(context , AllDatasActivity.class);
                        i.putExtra("url",buy_sell_item_data.getData());

                        context.startActivity(i);
                        Helper.shared().go((Activity) context);
                    }
                });
                buy_sell_post_data_holder.itemView.findViewById(R.id.four_image).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent i = new Intent(context , AllDatasActivity.class);
                        i.putExtra("url",buy_sell_item_data.getData());

                        context.startActivity(i);
                        Helper.shared().go((Activity) context);
                    }
                });

                buy_sell_post_data_holder.itemView.findViewById(R.id.setting).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (buy_sell_item_data.getSenderUid().equals(currentUser.getUid())){
                            ArrayList<MainPostModel> mainPostModels = new ArrayList<>();
                            mainPostModels.add(mainPostModel);
                            Helper.shared().VayeAppCurrentUserBottomSheetLauncher(mainPostModels,(Activity) context, currentUser, buy_sell_item_data, new TrueFalse<Boolean>() {
                                @Override
                                public void callBack(Boolean _value) {
                                    if (_value){
                                        notifyDataSetChanged();
                                    }
                                }
                            });
                        }else{
                            UserService.shared().getOtherUser((Activity) context, buy_sell_item_data.getSenderUid(), new OtherUserService() {
                                @Override
                                public void callback(OtherUser user) {
                                    ArrayList<MainPostModel> mainPostModels = new ArrayList<>();
                                    mainPostModels.add(mainPostModel);
                                    Helper.shared().VayeAppOtherUserBottomSheetLauncher(mainPostModels,(Activity) context, user, currentUser, buy_sell_item_data, new TrueFalse<Boolean>() {
                                        @Override
                                        public void callBack(Boolean _value) {
                                            if (_value){
                                                notifyDataSetChanged();
                                            }
                                        }
                                    });
                                    WaitDialog.dismiss();
                                }
                            });
                        }
                    }
                });
                break;

            case FOOD_ME_DATA_POST:
                FoodMeViewHolder foodme_post_data_holder = (FoodMeViewHolder) holder;
                MainPostModel food_item_data = mainPostModel;
                foodme_post_data_holder.setCommentLbl(food_item_data.getComment());
                foodme_post_data_holder.setImages(food_item_data.getThumbData());
                foodme_post_data_holder.setName(food_item_data.getSenderName());
                foodme_post_data_holder.setUserName(food_item_data.getUsername());
                foodme_post_data_holder.setProfileImage(food_item_data.getThumb_image());
                foodme_post_data_holder.setText(food_item_data.getText());
                foodme_post_data_holder.setDislikeLbl(food_item_data.getDislike());
                foodme_post_data_holder.setLikeLbl(food_item_data.getLikes());
                foodme_post_data_holder.setLike(food_item_data.getLikes(),currentUser , context);
                foodme_post_data_holder.setDislike(food_item_data.getDislike(),currentUser , context);
                foodme_post_data_holder.setTime(food_item_data.getPostTime());
                foodme_post_data_holder.setLocationButton(food_item_data.getGeoPoint());
                foodme_post_data_holder.itemView.findViewById(R.id.profileLay).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {



                        if (food_item_data.getSenderUid().equals(currentUser.getUid())){
                            if (!istanceOfCurrentUserProfile){
                                Intent i = new Intent(context , CurrentUserProfile.class);
                                i.putExtra("currentUser",currentUser);
                                context.startActivity(i);
                                Helper.shared().go((Activity) context);
                            }else{
                                return;
                            }

                        }else{
                            if (!istanceOfOtherUserProfile){
                                UserService.shared().getOtherUser((Activity) context, food_item_data.getSenderUid(), new OtherUserService() {
                                    @Override
                                    public void callback(OtherUser user) {
                                        Intent i  = new Intent(context , OtherUserProfileActivity.class);
                                        i.putExtra("otherUser",user);
                                        i.putExtra("currentUser",currentUser);
                                        context.startActivity(i);
                                        Helper.shared().go((Activity) context);
                                        WaitDialog.dismiss();
                                    }
                                });
                            }else{
                                return;
                            }

                        }
                    }
                });
                foodme_post_data_holder.itemView.findViewById(R.id.name).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if (food_item_data.getSenderUid().equals(currentUser.getUid())){
                            if (!istanceOfCurrentUserProfile){
                                Intent i = new Intent(context , CurrentUserProfile.class);
                                i.putExtra("currentUser",currentUser);
                                context.startActivity(i);
                                Helper.shared().go((Activity) context);
                            }else{
                                return;
                            }

                        }else{
                            if (!istanceOfOtherUserProfile){
                                UserService.shared().getOtherUser((Activity) context, food_item_data.getSenderUid(), new OtherUserService() {
                                    @Override
                                    public void callback(OtherUser user) {
                                        Intent i  = new Intent(context , OtherUserProfileActivity.class);
                                        i.putExtra("otherUser",user);
                                        i.putExtra("currentUser",currentUser);
                                        context.startActivity(i);
                                        Helper.shared().go((Activity) context);
                                        WaitDialog.dismiss();
                                    }
                                });
                            }else{
                                return;
                            }

                        }

                    }
                });
                foodme_post_data_holder.text.setOnHyperlinkClickListener(new SocialView.OnClickListener() {
                    @Override
                    public void onClick(@NonNull SocialView view, @NonNull CharSequence text) {
                        String url = text.toString();
                        if (!url.startsWith("http://") && !url.startsWith("https://"))
                            url = "http://" + url;
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        context.startActivity(browserIntent);
                    }
                });
                foodme_post_data_holder.text.setOnMentionClickListener(new SocialView.OnClickListener() {
                    @Override
                    public void onClick(@NonNull SocialView view, @NonNull CharSequence username) {

                        String _username = "@"+username.toString();
                        if (_username.equals(currentUser.getUsername())){
                            if (!istanceOfCurrentUserProfile){
                                Intent i = new Intent(context , CurrentUserProfile.class);
                                i.putExtra("currentUser",currentUser);
                                context.startActivity(i);
                                Helper.shared().go((Activity) context);
                            }

                        }else{
                            if (!istanceOfOtherUserProfile){
                                UserService.shared().getOthUserIdByMention("@"+username.toString(), new StringCompletion() {
                                    @Override
                                    public void getString(String otherUserId) {
                                        if (otherUserId!=null){
                                            UserService.shared().getOtherUser((Activity) context, otherUserId, new OtherUserService() {
                                                @Override
                                                public void callback(OtherUser user) {
                                                    if (user!=null){
                                                        WaitDialog.dismiss();
                                                        Intent i = new Intent(context , OtherUserProfileActivity.class);
                                                        i.putExtra("currentUser",currentUser);
                                                        i.putExtra("otherUser",user);
                                                        context.startActivity(i);
                                                        Helper.shared().go((Activity) context);
                                                    }
                                                }
                                            });
                                        }else{
                                            TipDialog.show((AppCompatActivity) context, "Böyle Bir Kullanıcı Bulunmuyor", TipDialog.TYPE.ERROR);
                                            TipDialog.dismiss(1000);
                                        }

                                    }
                                });
                            }


                        }
                    }

                });
                foodme_post_data_holder.itemView.findViewById(R.id.text).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                    }
                });
                foodme_post_data_holder.itemView.findViewById(R.id.locationButton).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String uri = "http://maps.google.com/maps?saddr=" + food_item_data.getGeoPoint().getLatitude() + "," + food_item_data.getGeoPoint().getLongitude()  + "&daddr=" ;
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                        context.startActivity(intent);
                    }
                });
                foodme_post_data_holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(context , CommentActivity.class);
                        intent.putExtra("mainPost",food_item_data);
                        intent.putExtra("currentUser",currentUser);
                        context.startActivity(intent);
                        Helper.shared().go((Activity) context);
                    }
                });
                foodme_post_data_holder.comment.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(context , CommentActivity.class);
                        intent.putExtra("mainPost",food_item_data);
                        intent.putExtra("currentUser",currentUser);
                        context.startActivity(intent);
                        Helper.shared().go((Activity) context);
                    }
                });

                foodme_post_data_holder.itemView.findViewById(R.id.like).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        MainPostService.shared().setPostLike(food_item_data, currentUser, Notifications.NotificationType.like_food_me, Notifications.NotificationDescription.like_food_me, new TrueFalse<Boolean>() {
                            @Override
                            public void callBack(Boolean _value) {
                                foodme_post_data_holder.setLike(food_item_data.getLikes(),currentUser ,context);
                                notifyDataSetChanged();
                            }
                        });
                    }
                });

                foodme_post_data_holder.itemView.findViewById(R.id.dislike).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        MainPostService.shared().setPostDislike(food_item_data, currentUser, new TrueFalse<Boolean>() {
                            @Override
                            public void callBack(Boolean _value) {
                                if (_value){
                                    foodme_post_data_holder.setDislike(food_item_data.getDislike(),currentUser,context);
                                    notifyDataSetChanged();
                                }
                            }
                        });
                    }
                });


                foodme_post_data_holder.itemView.findViewById(R.id.one_image).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent i = new Intent(context , AllDatasActivity.class);
                        i.putExtra("url",food_item_data.getData());
                        context.startActivity(i);
                        Helper.shared().go((Activity) context);
                    }
                });
                foodme_post_data_holder.itemView.findViewById(R.id.two_image).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent i = new Intent(context , AllDatasActivity.class);
                        i.putExtra("url",food_item_data.getData());

                        context.startActivity(i);
                        Helper.shared().go((Activity) context);
                    }
                });
                foodme_post_data_holder.itemView.findViewById(R.id.there_image).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent i = new Intent(context , AllDatasActivity.class);
                        i.putExtra("url",food_item_data.getData());

                        context.startActivity(i);
                        Helper.shared().go((Activity) context);
                    }
                });
                foodme_post_data_holder.itemView.findViewById(R.id.four_image).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent i = new Intent(context , AllDatasActivity.class);
                        i.putExtra("url",food_item_data.getData());

                        context.startActivity(i);
                        Helper.shared().go((Activity) context);
                    }
                });

                foodme_post_data_holder.itemView.findViewById(R.id.setting).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (food_item_data.getSenderUid().equals(currentUser.getUid())){
                            ArrayList<MainPostModel> mainPostModels = new ArrayList<>();
                            mainPostModels.add(mainPostModel);
                            Helper.shared().VayeAppCurrentUserBottomSheetLauncher(mainPostModels,(Activity) context, currentUser, food_item_data, new TrueFalse<Boolean>() {
                                @Override
                                public void callBack(Boolean _value) {
                                    if (_value){
                                        notifyDataSetChanged();
                                    }
                                }
                            });
                        }else{
                            UserService.shared().getOtherUser((Activity) context, food_item_data.getSenderUid(), new OtherUserService() {
                                @Override
                                public void callback(OtherUser user) {
                                    ArrayList<MainPostModel> mainPostModels = new ArrayList<>();
                                    mainPostModels.add(mainPostModel);
                                    Helper.shared().VayeAppOtherUserBottomSheetLauncher(mainPostModels,(Activity) context, user, currentUser, food_item_data, new TrueFalse<Boolean>() {
                                        @Override
                                        public void callBack(Boolean _value) {
                                            if (_value){
                                                notifyDataSetChanged();
                                            }
                                        }
                                    });
                                    WaitDialog.dismiss();
                                }
                            });
                        }
                    }
                });
                break;

            case FOOD_ME_POST:
                FoodMeViewHolder foodme_post_holder = (FoodMeViewHolder) holder;
                MainPostModel food_me_item = mainPostModel;
                foodme_post_holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(context , CommentActivity.class);
                        intent.putExtra("mainPost",food_me_item);
                        intent.putExtra("currentUser",currentUser);
                        context.startActivity(intent);
                        Helper.shared().go((Activity) context);
                    }
                });
                foodme_post_holder.comment.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(context , CommentActivity.class);
                        intent.putExtra("mainPost",food_me_item);
                        intent.putExtra("currentUser",currentUser);
                        context.startActivity(intent);
                        Helper.shared().go((Activity) context);
                    }
                });
                foodme_post_holder.setCommentLbl(food_me_item.getComment());
                foodme_post_holder.setName(food_me_item.getSenderName());
                foodme_post_holder.setUserName(food_me_item.getUsername());
                foodme_post_holder.setProfileImage(food_me_item.getThumb_image());

                foodme_post_holder.setText(food_me_item.getText());
                foodme_post_holder.setDislikeLbl(food_me_item.getDislike());
                foodme_post_holder.setLikeLbl(food_me_item.getLikes());
                foodme_post_holder.setLike(food_me_item.getLikes(),currentUser , context);
                foodme_post_holder.setDislike(food_me_item.getDislike(),currentUser , context);

                foodme_post_holder.setTime(food_me_item.getPostTime());
                foodme_post_holder.setLocationButton(food_me_item.getGeoPoint());
                foodme_post_holder.itemView.findViewById(R.id.profileLay).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (food_me_item.getSenderUid().equals(currentUser.getUid())){
                            if (!istanceOfCurrentUserProfile){
                                Intent i = new Intent(context , CurrentUserProfile.class);
                                i.putExtra("currentUser",currentUser);
                                context.startActivity(i);
                                Helper.shared().go((Activity) context);
                            }else{
                                return;
                            }

                        }else{
                            if (!istanceOfOtherUserProfile){
                                UserService.shared().getOtherUser((Activity) context, food_me_item.getSenderUid(), new OtherUserService() {
                                    @Override
                                    public void callback(OtherUser user) {
                                        Intent i  = new Intent(context , OtherUserProfileActivity.class);
                                        i.putExtra("otherUser",user);
                                        i.putExtra("currentUser",currentUser);
                                        context.startActivity(i);
                                        Helper.shared().go((Activity) context);
                                        WaitDialog.dismiss();
                                    }
                                });
                            }else{
                                return;
                            }

                        }
                    }
                });
                foodme_post_holder.itemView.findViewById(R.id.name).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (food_me_item.getSenderUid().equals(currentUser.getUid())){
                            if (!istanceOfCurrentUserProfile){
                                Intent i = new Intent(context , CurrentUserProfile.class);
                                i.putExtra("currentUser",currentUser);
                                context.startActivity(i);
                                Helper.shared().go((Activity) context);
                            }else{
                                return;
                            }

                        }else{
                            if (!istanceOfOtherUserProfile){
                                UserService.shared().getOtherUser((Activity) context, food_me_item.getSenderUid(), new OtherUserService() {
                                    @Override
                                    public void callback(OtherUser user) {
                                        Intent i  = new Intent(context , OtherUserProfileActivity.class);
                                        i.putExtra("otherUser",user);
                                        i.putExtra("currentUser",currentUser);
                                        context.startActivity(i);
                                        Helper.shared().go((Activity) context);
                                        WaitDialog.dismiss();
                                    }
                                });
                            }else{
                                return;
                            }

                        }

                    }
                });
                foodme_post_holder .text.setOnHyperlinkClickListener(new SocialView.OnClickListener() {
                    @Override
                    public void onClick(@NonNull SocialView view, @NonNull CharSequence text) {
                        String url = text.toString();
                        if (!url.startsWith("http://") && !url.startsWith("https://"))
                            url = "http://" + url;
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        context.startActivity(browserIntent);
                    }
                });
                foodme_post_holder.text.setOnMentionClickListener(new SocialView.OnClickListener() {
                    @Override
                    public void onClick(@NonNull SocialView view, @NonNull CharSequence username) {

                        String _username = "@"+username.toString();
                        if (_username.equals(currentUser.getUsername())){
                            if (!istanceOfCurrentUserProfile){
                                Intent i = new Intent(context , CurrentUserProfile.class);
                                i.putExtra("currentUser",currentUser);
                                context.startActivity(i);
                                Helper.shared().go((Activity) context);
                            }

                        }else{
                            if (!istanceOfOtherUserProfile){
                                UserService.shared().getOthUserIdByMention("@"+username.toString(), new StringCompletion() {
                                    @Override
                                    public void getString(String otherUserId) {
                                        if (otherUserId!=null){
                                            UserService.shared().getOtherUser((Activity) context, otherUserId, new OtherUserService() {
                                                @Override
                                                public void callback(OtherUser user) {
                                                    if (user!=null){
                                                        WaitDialog.dismiss();
                                                        Intent i = new Intent(context , OtherUserProfileActivity.class);
                                                        i.putExtra("currentUser",currentUser);
                                                        i.putExtra("otherUser",user);
                                                        context.startActivity(i);
                                                        Helper.shared().go((Activity) context);
                                                    }
                                                }
                                            });
                                        }else{
                                            TipDialog.show((AppCompatActivity) context, "Böyle Bir Kullanıcı Bulunmuyor", TipDialog.TYPE.ERROR);
                                            TipDialog.dismiss(1000);
                                        }

                                    }
                                });
                            }


                        }

                    }
                });
                foodme_post_holder.itemView.findViewById(R.id.locationButton).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String uri = "http://maps.google.com/maps?saddr=" + food_me_item.getGeoPoint().getLatitude() + "," + food_me_item.getGeoPoint().getLongitude()  + "&daddr=" ;
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                        context.startActivity(intent);
                    }
                });
                foodme_post_holder.itemView.findViewById(R.id.like).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        MainPostService.shared().setPostLike(food_me_item, currentUser, Notifications.NotificationType.like_food_me, Notifications.NotificationDescription.like_food_me, new TrueFalse<Boolean>() {
                            @Override
                            public void callBack(Boolean _value) {
                                foodme_post_holder.setLike(food_me_item.getLikes(),currentUser ,context);
                                notifyDataSetChanged();
                            }
                        });

                    }
                });

                foodme_post_holder.itemView.findViewById(R.id.dislike).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        MainPostService.shared().setPostDislike(food_me_item, currentUser, new TrueFalse<Boolean>() {
                            @Override
                            public void callBack(Boolean _value) {
                                foodme_post_holder.setDislike(food_me_item.getDislike(),currentUser,context);
                                notifyDataSetChanged();
                            }
                        });
                    }
                });
                foodme_post_holder.itemView.findViewById(R.id.locationButton).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String uri = "http://maps.google.com/maps?saddr=" + food_me_item.getGeoPoint().getLatitude() + "," + food_me_item.getGeoPoint().getLongitude()  + "&daddr=" ;
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                        context.startActivity(intent);
                    }
                });

                foodme_post_holder.itemView.findViewById(R.id.setting).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (food_me_item.getSenderUid().equals(currentUser.getUid())){
                            ArrayList<MainPostModel> mainPostModels = new ArrayList<>();
                            mainPostModels.add(mainPostModel);
                            Helper.shared().VayeAppCurrentUserBottomSheetLauncher(mainPostModels,(Activity) context, currentUser, food_me_item, new TrueFalse<Boolean>() {
                                @Override
                                public void callBack(Boolean _value) {
                                    if (_value){
                                        notifyDataSetChanged();
                                    }
                                }
                            });
                        }else{
                            UserService.shared().getOtherUser((Activity) context, food_me_item.getSenderUid(), new OtherUserService() {
                                @Override
                                public void callback(OtherUser user) {
                                    ArrayList<MainPostModel> mainPostModels = new ArrayList<>();
                                    mainPostModels.add(mainPostModel);
                                    Helper.shared().VayeAppOtherUserBottomSheetLauncher(mainPostModels,(Activity) context, user, currentUser, food_me_item, new TrueFalse<Boolean>() {
                                        @Override
                                        public void callBack(Boolean _value) {
                                            if (_value){
                                                notifyDataSetChanged();
                                            }
                                        }
                                    });
                                    WaitDialog.dismiss();
                                }
                            });
                        }
                    }
                });
                break;
            case CAMPING__POST:
                CampingViewHolder caping_post_holder = (CampingViewHolder) holder;
                MainPostModel camping_item = mainPostModel;

                caping_post_holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(context , CommentActivity.class);
                        intent.putExtra("mainPost",camping_item);
                        intent.putExtra("currentUser",currentUser);
                        context.startActivity(intent);
                        Helper.shared().go((Activity) context);
                    }
                });
                caping_post_holder.comment.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(context , CommentActivity.class);
                        intent.putExtra("mainPost",camping_item);
                        intent.putExtra("currentUser",currentUser);
                        context.startActivity(intent);
                        Helper.shared().go((Activity) context);
                    }
                });
                caping_post_holder.setCommentLbl(camping_item.getComment());
                caping_post_holder.setName(camping_item.getSenderName());
                caping_post_holder.setUserName(camping_item.getUsername());
                caping_post_holder.setProfileImage(camping_item.getThumb_image());

                caping_post_holder.setText(camping_item.getText());
                caping_post_holder.setDislikeLbl(camping_item.getDislike());
                caping_post_holder.setLikeLbl(camping_item.getLikes());
                caping_post_holder.setLike(camping_item.getLikes(),currentUser , context);
                caping_post_holder.setDislike(camping_item.getDislike(),currentUser , context);

                caping_post_holder.setTime(camping_item.getPostTime());
                caping_post_holder.setLocationButton(camping_item.getGeoPoint());
                caping_post_holder.text.setOnHyperlinkClickListener(new SocialView.OnClickListener() {
                    @Override
                    public void onClick(@NonNull SocialView view, @NonNull CharSequence text) {
                        String url = text.toString();
                        if (!url.startsWith("http://") && !url.startsWith("https://"))
                            url = "http://" + url;
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        context.startActivity(browserIntent);
                    }
                });

                caping_post_holder.itemView.findViewById(R.id.profileLay).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Log.d("context context", "onClick: " + "true");
                        if (camping_item.getSenderUid().equals(currentUser.getUid())){
                            if (!istanceOfCurrentUserProfile){
                                Intent i = new Intent(context , CurrentUserProfile.class);
                                i.putExtra("currentUser",currentUser);
                                context.startActivity(i);
                                Helper.shared().go((Activity) context);
                            }else{
                                return;
                            }

                        }else{
                            if (!istanceOfOtherUserProfile){
                                UserService.shared().getOtherUser((Activity) context, camping_item.getSenderUid(), new OtherUserService() {
                                    @Override
                                    public void callback(OtherUser user) {
                                        Intent i  = new Intent(context , OtherUserProfileActivity.class);
                                        i.putExtra("otherUser",user);
                                        i.putExtra("currentUser",currentUser);
                                        context.startActivity(i);
                                        Helper.shared().go((Activity) context);
                                        WaitDialog.dismiss();
                                    }
                                });
                            }else{
                                return;
                            }

                        }



                    }

                });
                caping_post_holder.itemView.findViewById(R.id.name).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {


                        if (camping_item.getSenderUid().equals(currentUser.getUid())){
                            if (!istanceOfCurrentUserProfile){
                                Intent i = new Intent(context , CurrentUserProfile.class);
                                i.putExtra("currentUser",currentUser);
                                context.startActivity(i);
                                Helper.shared().go((Activity) context);
                            }else{
                                return;
                            }

                        }else{
                            if (!istanceOfOtherUserProfile){
                                UserService.shared().getOtherUser((Activity) context, camping_item.getSenderUid(), new OtherUserService() {
                                    @Override
                                    public void callback(OtherUser user) {
                                        Intent i  = new Intent(context , OtherUserProfileActivity.class);
                                        i.putExtra("otherUser",user);
                                        i.putExtra("currentUser",currentUser);
                                        context.startActivity(i);
                                        Helper.shared().go((Activity) context);
                                        WaitDialog.dismiss();
                                    }
                                });
                            }else{
                                return;
                            }

                        }



                    }
                });

                caping_post_holder.text.setOnMentionClickListener(new SocialView.OnClickListener() {
                    @Override
                    public void onClick(@NonNull SocialView view, @NonNull CharSequence username) {

                        String _username = "@"+username.toString();
                        if (_username.equals(currentUser.getUsername())){
                            if (!istanceOfCurrentUserProfile){
                                Intent i = new Intent(context , CurrentUserProfile.class);
                                i.putExtra("currentUser",currentUser);
                                context.startActivity(i);
                                Helper.shared().go((Activity) context);
                            }

                        }else{
                            if (!istanceOfOtherUserProfile){
                                UserService.shared().getOthUserIdByMention("@"+username.toString(), new StringCompletion() {
                                    @Override
                                    public void getString(String otherUserId) {
                                        if (otherUserId!=null){

                                            UserService.shared().getOtherUser((Activity) context, otherUserId, new OtherUserService() {
                                                @Override
                                                public void callback(OtherUser user) {
                                                    if (user!=null){
                                                        WaitDialog.dismiss();
                                                        Intent i = new Intent(context , OtherUserProfileActivity.class);
                                                        i.putExtra("currentUser",currentUser);
                                                        i.putExtra("otherUser",user);
                                                        context.startActivity(i);
                                                        Helper.shared().go((Activity) context);
                                                    }
                                                }
                                            });
                                        }else{
                                            TipDialog.show((AppCompatActivity) context, "Böyle Bir Kullanıcı Bulunmuyor", TipDialog.TYPE.ERROR);
                                            TipDialog.dismiss(1000);
                                        }

                                    }
                                });
                            }


                        }

                    }
                });


                caping_post_holder.itemView.findViewById(R.id.like).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        MainPostService.shared().setPostLike(camping_item, currentUser, Notifications.NotificationType.like_food_me, Notifications.NotificationDescription.like_food_me, new TrueFalse<Boolean>() {
                            @Override
                            public void callBack(Boolean _value) {
                                caping_post_holder.setLike(camping_item.getLikes(),currentUser ,context);
                                notifyDataSetChanged();
                            }
                        });

                    }
                });

                caping_post_holder.itemView.findViewById(R.id.dislike).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        MainPostService.shared().setPostDislike(camping_item, currentUser, new TrueFalse<Boolean>() {
                            @Override
                            public void callBack(Boolean _value) {
                                caping_post_holder.setDislike(camping_item.getDislike(),currentUser,context);
                                notifyDataSetChanged();
                            }
                        });
                    }
                });
                caping_post_holder.itemView.findViewById(R.id.locationButton).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String uri = "http://maps.google.com/maps?saddr=" + camping_item.getGeoPoint().getLatitude() + "," + camping_item.getGeoPoint().getLongitude()  + "&daddr=" ;
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                        context.startActivity(intent);
                    }
                });

                caping_post_holder.itemView.findViewById(R.id.setting).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (camping_item.getSenderUid().equals(currentUser.getUid())){
                            ArrayList<MainPostModel> mainPostModels = new ArrayList<>();
                            mainPostModels.add(mainPostModel);
                            Helper.shared().VayeAppCurrentUserBottomSheetLauncher(mainPostModels,(Activity) context, currentUser, camping_item, new TrueFalse<Boolean>() {
                                @Override
                                public void callBack(Boolean _value) {
                                    if (_value){
                                        notifyDataSetChanged();
                                    }
                                }
                            });
                        }else{
                            UserService.shared().getOtherUser((Activity) context, camping_item.getSenderUid(), new OtherUserService() {
                                @Override
                                public void callback(OtherUser user) {
                                    ArrayList<MainPostModel> mainPostModels = new ArrayList<>();
                                    mainPostModels.add(mainPostModel);
                                    Helper.shared().VayeAppOtherUserBottomSheetLauncher(mainPostModels,(Activity) context, user, currentUser, camping_item, new TrueFalse<Boolean>() {
                                        @Override
                                        public void callBack(Boolean _value) {
                                            if (_value){
                                                notifyDataSetChanged();
                                            }
                                        }
                                    });
                                    WaitDialog.dismiss();
                                }
                            });
                        }
                    }
                });
                break;
            case CAMPING_DATA_POST:
                CampingViewHolder camping_post_data_holder = (CampingViewHolder) holder;
                MainPostModel camping_item_data = mainPostModel;
                camping_post_data_holder.setCommentLbl(camping_item_data.getComment());
                camping_post_data_holder.setImages(camping_item_data.getThumbData());
                camping_post_data_holder.setName(camping_item_data.getSenderName());
                camping_post_data_holder.setUserName(camping_item_data.getUsername());
                camping_post_data_holder.setProfileImage(camping_item_data.getThumb_image());
                camping_post_data_holder.setText(camping_item_data.getText());
                camping_post_data_holder.setDislikeLbl(camping_item_data.getDislike());
                camping_post_data_holder.setLikeLbl(camping_item_data.getLikes());
                camping_post_data_holder.setLike(camping_item_data.getLikes(),currentUser , context);
                camping_post_data_holder.setDislike(camping_item_data.getDislike(),currentUser , context);
                camping_post_data_holder.setTime(camping_item_data.getPostTime());
                camping_post_data_holder.setLocationButton(camping_item_data.getGeoPoint());
                camping_post_data_holder.text.setOnHyperlinkClickListener(new SocialView.OnClickListener() {
                    @Override
                    public void onClick(@NonNull SocialView view, @NonNull CharSequence text) {
                        String url = text.toString();
                        if (!url.startsWith("http://") && !url.startsWith("https://"))
                            url = "http://" + url;
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        context.startActivity(browserIntent);
                    }
                });
                camping_post_data_holder.itemView.findViewById(R.id.profileLay).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {


                        if (camping_item_data.getSenderUid().equals(currentUser.getUid())){
                            if (!istanceOfCurrentUserProfile){
                                Intent i = new Intent(context , CurrentUserProfile.class);
                                i.putExtra("currentUser",currentUser);
                                context.startActivity(i);
                                Helper.shared().go((Activity) context);
                            }else{
                                return;
                            }

                        }else{
                            if (!istanceOfOtherUserProfile){
                                UserService.shared().getOtherUser((Activity) context, camping_item_data.getSenderUid(), new OtherUserService() {
                                    @Override
                                    public void callback(OtherUser user) {
                                        Intent i  = new Intent(context , OtherUserProfileActivity.class);
                                        i.putExtra("otherUser",user);
                                        i.putExtra("currentUser",currentUser);
                                        context.startActivity(i);
                                        Helper.shared().go((Activity) context);
                                        WaitDialog.dismiss();
                                    }
                                });
                            }else{
                                return;
                            }

                        }


                    }
                });
                camping_post_data_holder.itemView.findViewById(R.id.name).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (camping_item_data.getSenderUid().equals(currentUser.getUid())){
                            if (!istanceOfCurrentUserProfile){
                                Intent i = new Intent(context , CurrentUserProfile.class);
                                i.putExtra("currentUser",currentUser);
                                context.startActivity(i);
                                Helper.shared().go((Activity) context);
                            }else{
                                return;
                            }

                        }else{
                            if (!istanceOfOtherUserProfile){
                                UserService.shared().getOtherUser((Activity) context, camping_item_data.getSenderUid(), new OtherUserService() {
                                    @Override
                                    public void callback(OtherUser user) {
                                        Intent i  = new Intent(context , OtherUserProfileActivity.class);
                                        i.putExtra("otherUser",user);
                                        i.putExtra("currentUser",currentUser);
                                        context.startActivity(i);
                                        Helper.shared().go((Activity) context);
                                        WaitDialog.dismiss();
                                    }
                                });
                            }else{
                                return;
                            }

                        }

                    }
                });
                camping_post_data_holder.text.setOnMentionClickListener(new SocialView.OnClickListener() {
                    @Override
                    public void onClick(@NonNull SocialView view, @NonNull CharSequence username) {

                        String _username = "@"+username.toString();
                        if (_username.equals(currentUser.getUsername())){
                            if (!istanceOfCurrentUserProfile){
                                Intent i = new Intent(context , CurrentUserProfile.class);
                                i.putExtra("currentUser",currentUser);
                                context.startActivity(i);
                                Helper.shared().go((Activity) context);
                            }

                        }else{
                            if (!istanceOfOtherUserProfile){
                                UserService.shared().getOthUserIdByMention("@"+username.toString(), new StringCompletion() {
                                    @Override
                                    public void getString(String otherUserId) {
                                        if (otherUserId!=null){
                                            UserService.shared().getOtherUser((Activity) context, otherUserId, new OtherUserService() {
                                                @Override
                                                public void callback(OtherUser user) {
                                                    if (user!=null){
                                                        WaitDialog.dismiss();
                                                        Intent i = new Intent(context , OtherUserProfileActivity.class);
                                                        i.putExtra("currentUser",currentUser);
                                                        i.putExtra("otherUser",user);
                                                        context.startActivity(i);
                                                        Helper.shared().go((Activity) context);
                                                    }
                                                }
                                            });
                                        }else{
                                            TipDialog.show((AppCompatActivity) context, "Böyle Bir Kullanıcı Bulunmuyor", TipDialog.TYPE.ERROR);
                                            TipDialog.dismiss(1000);
                                        }

                                    }
                                });
                            }


                        }
                    }

                });
                camping_post_data_holder.itemView.findViewById(R.id.text).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                    }
                });
                camping_post_data_holder.itemView.findViewById(R.id.locationButton).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String uri = "http://maps.google.com/maps?saddr=" + camping_item_data.getGeoPoint().getLatitude() + "," + camping_item_data.getGeoPoint().getLongitude()  + "&daddr=" ;
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                        context.startActivity(intent);
                    }
                });
                camping_post_data_holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(context , CommentActivity.class);
                        intent.putExtra("mainPost",camping_item_data);
                        intent.putExtra("currentUser",currentUser);
                        context.startActivity(intent);
                        Helper.shared().go((Activity) context);
                    }
                });
                camping_post_data_holder.comment.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(context , CommentActivity.class);
                        intent.putExtra("mainPost",camping_item_data);
                        intent.putExtra("currentUser",currentUser);
                        context.startActivity(intent);
                        Helper.shared().go((Activity) context);
                    }
                });

                camping_post_data_holder.itemView.findViewById(R.id.like).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        MainPostService.shared().setPostLike(camping_item_data, currentUser, Notifications.NotificationType.like_food_me, Notifications.NotificationDescription.like_food_me, new TrueFalse<Boolean>() {
                            @Override
                            public void callBack(Boolean _value) {
                                camping_post_data_holder.setLike(camping_item_data.getLikes(),currentUser ,context);
                                notifyDataSetChanged();
                            }
                        });
                    }
                });

                camping_post_data_holder.itemView.findViewById(R.id.dislike).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        MainPostService.shared().setPostDislike(camping_item_data, currentUser, new TrueFalse<Boolean>() {
                            @Override
                            public void callBack(Boolean _value) {
                                if (_value){
                                    camping_post_data_holder.setDislike(camping_item_data.getDislike(),currentUser,context);
                                    notifyDataSetChanged();
                                }
                            }
                        });
                    }
                });


                camping_post_data_holder.itemView.findViewById(R.id.one_image).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent i = new Intent(context , AllDatasActivity.class);
                        i.putExtra("url",camping_item_data.getData());
                        context.startActivity(i);
                        Helper.shared().go((Activity) context);
                    }
                });
                camping_post_data_holder.itemView.findViewById(R.id.two_image).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent i = new Intent(context , AllDatasActivity.class);
                        i.putExtra("url",camping_item_data.getData());

                        context.startActivity(i);
                        Helper.shared().go((Activity) context);
                    }
                });
                camping_post_data_holder.itemView.findViewById(R.id.there_image).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent i = new Intent(context , AllDatasActivity.class);
                        i.putExtra("url",camping_item_data.getData());

                        context.startActivity(i);
                        Helper.shared().go((Activity) context);
                    }
                });
                camping_post_data_holder.itemView.findViewById(R.id.four_image).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent i = new Intent(context , AllDatasActivity.class);
                        i.putExtra("url",camping_item_data.getData());

                        context.startActivity(i);
                        Helper.shared().go((Activity) context);
                    }
                });

                camping_post_data_holder.itemView.findViewById(R.id.setting).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (camping_item_data.getSenderUid().equals(currentUser.getUid())){
                            ArrayList<MainPostModel> mainPostModels = new ArrayList<>();
                            mainPostModels.add(mainPostModel);
                            Helper.shared().VayeAppCurrentUserBottomSheetLauncher(mainPostModels,(Activity) context, currentUser, camping_item_data, new TrueFalse<Boolean>() {
                                @Override
                                public void callBack(Boolean _value) {
                                    if (_value){
                                        notifyDataSetChanged();
                                    }
                                }
                            });
                        }else{
                            UserService.shared().getOtherUser((Activity) context, camping_item_data.getSenderUid(), new OtherUserService() {
                                @Override
                                public void callback(OtherUser user) {
                                    ArrayList<MainPostModel> mainPostModels = new ArrayList<>();
                                    mainPostModels.add(mainPostModel);
                                    Helper.shared().VayeAppOtherUserBottomSheetLauncher(mainPostModels,(Activity) context, user, currentUser, camping_item_data, new TrueFalse<Boolean>() {
                                        @Override
                                        public void callBack(Boolean _value) {
                                            if (_value){
                                                notifyDataSetChanged();
                                            }
                                        }
                                    });
                                    WaitDialog.dismiss();
                                }
                            });
                        }
                    }
                });
                break;


            case VIEW_TYPE_SCHOOL_POST:
            SchoolPostViewHolder school_itemHolder = (SchoolPostViewHolder) holder;
            NoticesMainModel menuItem = noticesMainModel;

            school_itemHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context , CommentActivity.class);
                    intent.putExtra("noticesPost",menuItem);
                    intent.putExtra("currentUser",currentUser);
                    context.startActivity(intent);
                    Helper.shared().go((Activity) context);
                }
            });
            school_itemHolder.comment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context , CommentActivity.class);
                    intent.putExtra("noticesPost",menuItem);
                    intent.putExtra("currentUser",currentUser);
                    context.startActivity(intent);
                    Helper.shared().go((Activity) context);
                }
            });
            school_itemHolder.text.setOnMentionClickListener(new SocialView.OnClickListener() {
                @Override
                public void onClick(@NonNull SocialView view, @NonNull CharSequence username) {
                    String _username = "@"+username.toString();
                    if (_username.equals(currentUser.getUsername())){
                        if (!istanceOfCurrentUserProfile){
                            Intent i = new Intent(context , CurrentUserProfile.class);
                            i.putExtra("currentUser",currentUser);
                            context.startActivity(i);
                            Helper.shared().go((Activity) context);
                        }

                    }else{
                        UserService.shared().getOthUserIdByMention("@"+username.toString(), new StringCompletion() {
                            @Override
                            public void getString(String otherUserId) {
                                if (otherUserId!=null){
                                    if (!istanceOfOtherUserProfile){
                                        UserService.shared().getOtherUser((Activity) context, otherUserId, new OtherUserService() {
                                            @Override
                                            public void callback(OtherUser user) {
                                                if (user!=null){
                                                    WaitDialog.dismiss();
                                                    Intent i = new Intent(context , OtherUserProfileActivity.class);
                                                    i.putExtra("currentUser",currentUser);
                                                    i.putExtra("otherUser",user);
                                                    context.startActivity(i);
                                                    Helper.shared().go((Activity) context);
                                                }
                                            }
                                        });
                                    }

                                }else{
                                    TipDialog.show((AppCompatActivity) context, "Böyle Bir Kullanıcı Bulunmuyor", TipDialog.TYPE.ERROR);
                                    TipDialog.dismiss(1000);
                                }

                            }
                        });

                    }
                }
            });
            school_itemHolder.more.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (menuItem.getSenderUid().equals(currentUser.getUid())){
                        ArrayList<NoticesMainModel> noticesMainModels = new ArrayList<>();
                        noticesMainModels.add(noticesMainModel);
                        Helper.shared().SchoolPostCurrentUserBottomSheetLauncher(noticesMainModels, (Activity) context, currentUser, menuItem, new TrueFalse<Boolean>() {
                            @Override
                            public void callBack(Boolean _value) {
                                if (_value){
                                    notifyDataSetChanged();
                                }
                            }
                        });
                    }else{
                        UserService.shared().getOtherUser((Activity) context, menuItem.getSenderUid(), new OtherUserService() {
                            @Override
                            public void callback(OtherUser user) {
                                ArrayList<NoticesMainModel> noticesMainModels = new ArrayList<>();
                                noticesMainModels.add(noticesMainModel);
                                Helper.shared().SchoolPostOtherUserBottomSheetLauncher(noticesMainModels,(Activity) context, user, currentUser, menuItem, new TrueFalse<Boolean>() {
                                    @Override
                                    public void callBack(Boolean _value) {
                                        if (_value){
                                            notifyDataSetChanged();
                                        }
                                    }
                                });
                                WaitDialog.dismiss();
                            }
                        });
                    }


                }
            });

            school_itemHolder.setCommentLbl(menuItem.getComment());
            school_itemHolder.setName(menuItem.getSenderName());
            school_itemHolder.setUserName(menuItem.getUsername());
            school_itemHolder.setProfileImage(menuItem.getThumb_image());
            school_itemHolder.setClupName(menuItem.getClupName());
            school_itemHolder.setText(menuItem.getText());
            school_itemHolder.setDislikeLbl(menuItem.getDislike());
            school_itemHolder.setLikeLbl(menuItem.getLikes());
            school_itemHolder.setLike(menuItem.getLikes(),currentUser , context);
            school_itemHolder.setDislike(menuItem.getDislike(),currentUser , context);

            school_itemHolder.setTime(menuItem.getPostTime());



            school_itemHolder.itemView.findViewById(R.id.like).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    SchoolPostService.shared().setLike(currentUser, menuItem, new TrueFalse<Boolean>() {
                        @Override
                        public void callBack(Boolean _value) {
                            school_itemHolder.setLike(menuItem.getLikes(),currentUser,context);
                            notifyDataSetChanged();
                        }
                    });
                }
            });

            school_itemHolder.itemView.findViewById(R.id.dislike).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    SchoolPostService.shared().setDislike(currentUser, menuItem, new TrueFalse<Boolean>() {
                        @Override
                        public void callBack(Boolean _value) {
                            school_itemHolder.setDislike(menuItem.getDislike(),currentUser,context);
                            notifyDataSetChanged();
                        }
                    });
                }
            });


            school_itemHolder.itemView.findViewById(R.id.profileLay).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (menuItem.getSenderUid().equals(currentUser.getUid())){

                        if (!istanceOfCurrentUserProfile){
                            Intent i = new Intent(context , CurrentUserProfile.class);
                            i.putExtra("currentUser",currentUser);
                            context.startActivity(i);
                            Helper.shared().go((Activity) context);
                        }

                    }else{
                        if (!istanceOfOtherUserProfile){
                            UserService.shared().getOtherUser((Activity) context, menuItem.getSenderUid(), new OtherUserService() {
                                @Override
                                public void callback(OtherUser user) {
                                    Intent i  = new Intent(context , OtherUserProfileActivity.class);
                                    i.putExtra("otherUser",user);
                                    i.putExtra("currentUser",currentUser);
                                    context.startActivity(i);
                                    Helper.shared().go((Activity) context);
                                    WaitDialog.dismiss();
                                }
                            });

                        }
                    }
                }
            });

            school_itemHolder.itemView.findViewById(R.id.name).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (menuItem.getSenderUid().equals(currentUser.getUid())){
                        if (!istanceOfCurrentUserProfile){
                            Intent i = new Intent(context , CurrentUserProfile.class);
                            i.putExtra("currentUser",currentUser);
                            context.startActivity(i);
                            Helper.shared().go((Activity) context);
                        }

                    }else{
                        if (!istanceOfOtherUserProfile){
                            UserService.shared().getOtherUser((Activity) context, menuItem.getSenderUid(), new OtherUserService() {
                                @Override
                                public void callback(OtherUser user) {
                                    Intent i  = new Intent(context , OtherUserProfileActivity.class);
                                    i.putExtra("otherUser",user);
                                    i.putExtra("currentUser",currentUser);
                                    context.startActivity(i);
                                    Helper.shared().go((Activity) context);
                                    WaitDialog.dismiss();
                                }
                            });
                        }

                    }

                }
            });
            break;
            case VIEW_TYPE_SCHOOL_POST_DATA:
                NoticesMainModel menuItemData = noticesMainModel;
                SchoolPostViewHolder school_postHolder = (SchoolPostViewHolder) holder;
                school_postHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(context , CommentActivity.class);
                        intent.putExtra("noticesPost",menuItemData);
                        intent.putExtra("currentUser",currentUser);
                        context.startActivity(intent);
                        Helper.shared().go((Activity) context);
                    }
                });
                school_postHolder.comment.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(context , CommentActivity.class);
                        intent.putExtra("noticesPost",menuItemData);
                        intent.putExtra("currentUser",currentUser);
                        context.startActivity(intent);
                        Helper.shared().go((Activity) context);
                    }
                });
                school_postHolder.itemView.findViewById(R.id.profileLay).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (menuItemData.getSenderUid().equals(currentUser.getUid())){
                            if (!istanceOfCurrentUserProfile){
                                Intent i = new Intent(context , CurrentUserProfile.class);
                                i.putExtra("currentUser",currentUser);
                                context.startActivity(i);
                                Helper.shared().go((Activity) context);
                            }

                        }else{
                            if (!istanceOfOtherUserProfile){
                                UserService.shared().getOtherUser((Activity) context, menuItemData.getSenderUid(), new OtherUserService() {
                                    @Override
                                    public void callback(OtherUser user) {
                                        Intent i  = new Intent(context , OtherUserProfileActivity.class);
                                        i.putExtra("otherUser",user);
                                        i.putExtra("currentUser",currentUser);
                                        context.startActivity(i);
                                        Helper.shared().go((Activity) context);
                                        WaitDialog.dismiss();
                                    }
                                });
                            }

                        }
                    }
                });
                school_postHolder.itemView.findViewById(R.id.name).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (menuItemData.getSenderUid().equals(currentUser.getUid())){
                            if (!istanceOfCurrentUserProfile){
                                Intent i = new Intent(context , CurrentUserProfile.class);
                                i.putExtra("currentUser",currentUser);
                                context.startActivity(i);
                                Helper.shared().go((Activity) context);
                            }

                        }else{
                            if (!istanceOfOtherUserProfile){
                                UserService.shared().getOtherUser((Activity) context, menuItemData.getSenderUid(), new OtherUserService() {
                                    @Override
                                    public void callback(OtherUser user) {
                                        Intent i  = new Intent(context , OtherUserProfileActivity.class);
                                        i.putExtra("otherUser",user);
                                        i.putExtra("currentUser",currentUser);
                                        context.startActivity(i);
                                        Helper.shared().go((Activity) context);
                                        WaitDialog.dismiss();
                                    }
                                });
                            }

                        }

                    }
                });
                school_postHolder.text.setOnMentionClickListener(new SocialView.OnClickListener() {
                    @Override
                    public void onClick(@NonNull SocialView view, @NonNull CharSequence username) {

                        String _username = "@"+username.toString();
                        if (_username.equals(currentUser.getUsername())){
                            if (!istanceOfCurrentUserProfile){
                                Intent i = new Intent(context , CurrentUserProfile.class);
                                i.putExtra("currentUser",currentUser);
                                context.startActivity(i);
                                Helper.shared().go((Activity) context);
                            }

                        }else{
                            UserService.shared().getOthUserIdByMention("@"+username.toString(), new StringCompletion() {
                                @Override
                                public void getString(String otherUserId) {
                                    if (otherUserId!=null){
                                        if (!istanceOfOtherUserProfile){
                                            UserService.shared().getOtherUser((Activity) context, otherUserId, new OtherUserService() {
                                                @Override
                                                public void callback(OtherUser user) {
                                                    if (user!=null){
                                                        WaitDialog.dismiss();
                                                        Intent i = new Intent(context , OtherUserProfileActivity.class);
                                                        i.putExtra("currentUser",currentUser);
                                                        i.putExtra("otherUser",user);
                                                        context.startActivity(i);
                                                        Helper.shared().go((Activity) context);
                                                    }
                                                }
                                            });
                                        }

                                    }else{
                                        TipDialog.show((AppCompatActivity) context, "Böyle Bir Kullanıcı Bulunmuyor", TipDialog.TYPE.ERROR);
                                        TipDialog.dismiss(1000);
                                    }

                                }
                            });

                        }
                    }
                });
                school_postHolder.more.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (menuItemData.getSenderUid().equals(currentUser.getUid())){
                            ArrayList<NoticesMainModel> noticesMainModels = new ArrayList<>();
                            noticesMainModels.add(noticesMainModel);
                            Helper.shared().SchoolPostCurrentUserBottomSheetLauncher(noticesMainModels, (Activity) context, currentUser, menuItemData, new TrueFalse<Boolean>() {
                                @Override
                                public void callBack(Boolean _value) {
                                    if (_value){
                                        notifyDataSetChanged();
                                    }
                                }
                            });
                        }else{
                            UserService.shared().getOtherUser((Activity) context, menuItemData.getSenderUid(), new OtherUserService() {
                                @Override
                                public void callback(OtherUser user) {
                                    ArrayList<NoticesMainModel> noticesMainModels = new ArrayList<>();
                                    noticesMainModels.add(noticesMainModel);
                                    Helper.shared().SchoolPostOtherUserBottomSheetLauncher(noticesMainModels,(Activity) context, user, currentUser, menuItemData, new TrueFalse<Boolean>() {
                                        @Override
                                        public void callBack(Boolean _value) {
                                            if (_value){
                                                notifyDataSetChanged();
                                            }
                                        }
                                    });
                                    WaitDialog.dismiss();
                                }
                            });
                        }
                    }
                });




                school_postHolder.setCommentLbl(menuItemData.getComment());

                school_postHolder.setImages(menuItemData.getThumbData());
                school_postHolder.setName(menuItemData.getSenderName());
                school_postHolder.setUserName(menuItemData.getUsername());
                school_postHolder.setProfileImage(menuItemData.getThumb_image());
                school_postHolder.setClupName(menuItemData.getClupName());
                school_postHolder.setText(menuItemData.getText());
                school_postHolder.setDislikeLbl(menuItemData.getDislike());
                school_postHolder.setLikeLbl(menuItemData.getLikes());
                school_postHolder.setLike(menuItemData.getLikes(),currentUser , context);
                school_postHolder.setDislike(menuItemData.getDislike(),currentUser , context);

                school_postHolder.setTime(menuItemData.getPostTime());

                school_postHolder.itemView.findViewById(R.id.like).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        SchoolPostService.shared().setLike(currentUser, menuItemData, new TrueFalse<Boolean>() {
                            @Override
                            public void callBack(Boolean _value) {
                                school_postHolder.setLike(menuItemData.getLikes(),currentUser,context);
                                notifyDataSetChanged();
                            }
                        });
                    }
                });

                school_postHolder.itemView.findViewById(R.id.dislike).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        SchoolPostService.shared().setDislike(currentUser, menuItemData, new TrueFalse<Boolean>() {
                            @Override
                            public void callBack(Boolean _value) {
                                school_postHolder.setDislike(menuItemData.getDislike(),currentUser , context);
                                notifyDataSetChanged();
                            }
                        });
                    }
                });



                school_postHolder.itemView.findViewById(R.id.one_image).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent i = new Intent(context , AllDatasActivity.class);
                        i.putExtra("url",menuItemData.getData());
                        context.startActivity(i);
                        Helper.shared().go((Activity) context);
                    }
                });
                school_postHolder.itemView.findViewById(R.id.two_image).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent i = new Intent(context , AllDatasActivity.class);
                        i.putExtra("url",menuItemData.getData());

                        context.startActivity(i);
                        Helper.shared().go((Activity) context);
                    }
                });
                school_postHolder.itemView.findViewById(R.id.there_image).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent i = new Intent(context , AllDatasActivity.class);
                        i.putExtra("url",menuItemData.getData());

                        context.startActivity(i);
                        Helper.shared().go((Activity) context);
                    }
                });
                school_postHolder.itemView.findViewById(R.id.four_image).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent i = new Intent(context , AllDatasActivity.class);
                        i.putExtra("url",menuItemData.getData());

                        context.startActivity(i);
                        Helper.shared().go((Activity) context);
                    }
                });
                break;




            case VIEW_TYPE_LESSON_POST_DATA:
                MajorPostViewHolder postHolder = (MajorPostViewHolder) holder;
                postHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(context , CommentActivity.class);
                        intent.putExtra("lessonPost",lessonPostModel);
                        intent.putExtra("currentUser",currentUser);
                        context.startActivity(intent);
                        Helper.shared().go((Activity) context);
                    }
                });

                postHolder.comment.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(context , CommentActivity.class);
                        intent.putExtra("lessonPost",lessonPostModel);
                        intent.putExtra("currentUser",currentUser);
                        context.startActivity(intent);
                        Helper.shared().go((Activity) context);
                    }
                });

                postHolder.more.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (lessonPostModel.getSenderUid().equals(currentUser.getUid())){
                            ArrayList<LessonPostModel> lessonPostModels = new ArrayList<>();
                            lessonPostModels.add(lessonPostModel);
                            Helper.shared().MajorPostCurrentUserBottomSheetLaunher((Activity) context, lessonPostModels, currentUser, lessonPostModel, new TrueFalse<Boolean>() {
                                @Override
                                public void callBack(Boolean _value) {
                                    if (_value){
                                        notifyDataSetChanged();
                                    }
                                }
                            });
                        }else {

                            UserService.shared().getOtherUser((Activity) context, lessonPostModel.getSenderUid(), new OtherUserService() {
                                @Override
                                public void callback(OtherUser user) {
                                    ArrayList<LessonPostModel> lessonPostModels = new ArrayList<>();
                                    lessonPostModels.add(lessonPostModel);
                                    Helper.shared().MajorPostOtherUserBottomSheetLaunher((Activity) context, lessonPostModels, user, currentUser, lessonPostModel, optionSelect,new TrueFalse<Boolean>() {
                                        @Override
                                        public void callBack(Boolean _value) {
                                            if (_value){
                                                notifyDataSetChanged();
                                            }
                                        }
                                    });
                                }
                            });
                            WaitDialog.dismiss();
                        }
                    }
                });
                LessonPostModel major_menuItemData = lessonPostModel;
                postHolder.setCommentLbl(major_menuItemData.getComment());

                postHolder.setImages(major_menuItemData.getThumbData());
                postHolder.setName(major_menuItemData.getSenderName());
                postHolder.setUserName(major_menuItemData.getUsername());
                postHolder.setProfileImage(major_menuItemData.getThumb_image());
                postHolder.setLessonName(major_menuItemData.getLessonName());
                postHolder.setText(major_menuItemData.getText());
                postHolder.setDislikeLbl(major_menuItemData.getDislike());
                postHolder.setLikeLbl(major_menuItemData.getLikes());
                postHolder.setLike(major_menuItemData.getLikes(),currentUser , context);
                postHolder.setDislike(major_menuItemData.getDislike(),currentUser , context);
                postHolder.setFav(major_menuItemData.getFavori(),currentUser,context);
                postHolder.setTime(major_menuItemData.getPostTime());
                postHolder.setLinkButton(major_menuItemData.getLink());
                postHolder.linkButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(major_menuItemData.getLink()));
                        context.startActivity(browserIntent);
                    }
                });
                postHolder.itemView.findViewById(R.id.like).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        MajorPostService.shared().setLike(currentUser, major_menuItemData, new TrueFalse<Boolean>() {
                            @Override
                            public void callBack(Boolean _value) {
                                if (_value){
                                    postHolder.setLike(major_menuItemData.getLikes(),currentUser,context);
                                    notifyDataSetChanged();
                                }
                            }
                        });
                    }
                });

                postHolder.itemView.findViewById(R.id.dislike).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        MajorPostService.shared().setDislike(currentUser, major_menuItemData, new TrueFalse<Boolean>() {
                            @Override
                            public void callBack(Boolean _value) {
                                if (_value)
                                {
                                    postHolder.setDislike(major_menuItemData.getDislike(),currentUser,context);
                                    notifyDataSetChanged();
                                }
                            }
                        });
                    }
                });

                postHolder.itemView.findViewById(R.id.fav).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        MajorPostService.shared().setFav(currentUser, major_menuItemData, new TrueFalse<Boolean>() {
                            @Override
                            public void callBack(Boolean _value) {
                                if (_value){
                                    postHolder.setFav(major_menuItemData.getFavori(),currentUser,context);
                                    notifyDataSetChanged();
                                }
                            }
                        });
                    }
                });

                postHolder.itemView.findViewById(R.id.one_image).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent i = new Intent(context , AllDatasActivity.class);
                        i.putExtra("url",major_menuItemData.getData());
                        context.startActivity(i);
                        Helper.shared().go((Activity) context);
                    }
                });
                postHolder.itemView.findViewById(R.id.two_image).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent i = new Intent(context , AllDatasActivity.class);
                        i.putExtra("url",major_menuItemData.getData());

                        context.startActivity(i);
                        Helper.shared().go((Activity) context);
                    }
                });
                postHolder.itemView.findViewById(R.id.there_image).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent i = new Intent(context , AllDatasActivity.class);
                        i.putExtra("url",major_menuItemData.getData());

                        context.startActivity(i);
                        Helper.shared().go((Activity) context);
                    }
                });
                postHolder.itemView.findViewById(R.id.four_image).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent i = new Intent(context , AllDatasActivity.class);
                        i.putExtra("url",major_menuItemData.getData());

                        context.startActivity(i);
                        Helper.shared().go((Activity) context);
                    }
                });
                postHolder.text.setOnHyperlinkClickListener(new SocialView.OnClickListener() {
                    @Override
                    public void onClick(@NonNull SocialView view, @NonNull CharSequence text) {
                        String url = text.toString();
                        if (!url.startsWith("http://") && !url.startsWith("https://"))
                            url = "http://" + url;
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        context.startActivity(browserIntent);
                    }
                });
                postHolder.profileImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (lessonPostModel.getSenderUid().equals(currentUser.getUid())){
                            if (!istanceOfCurrentUserProfile){
                                Intent i = new Intent(context , CurrentUserProfile.class);
                                i.putExtra("currentUser",currentUser);
                                context.startActivity(i);
                                Helper.shared().go((Activity)context);
                            }
                        }else{
                            if (!istanceOfOtherUserProfile){

                                UserService.shared().getOtherUser((Activity) context, lessonPostModel.getSenderUid(), new OtherUserService() {
                                    @Override
                                    public void callback(OtherUser user) {
                                        Intent i  = new Intent(context , OtherUserProfileActivity.class);
                                        i.putExtra("otherUser",user);
                                        i.putExtra("currentUser",currentUser);
                                        context.startActivity(i);
                                        Helper.shared().go((Activity) context);
                                        WaitDialog.dismiss();
                                    }
                                });

                            }
                        }
                    }
                });
                postHolder.name.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (lessonPostModel.getSenderUid().equals(currentUser.getUid())){
                            if (!istanceOfCurrentUserProfile){
                                Intent i = new Intent(context , CurrentUserProfile.class);
                                i.putExtra("currentUser",currentUser);
                                context.startActivity(i);
                                Helper.shared().go((Activity)context);
                            }
                        }else{
                            if (!istanceOfOtherUserProfile){
                                UserService.shared().getOtherUser((Activity) context, lessonPostModel.getSenderUid(), new OtherUserService() {
                                    @Override
                                    public void callback(OtherUser user) {
                                        Intent i  = new Intent(context , OtherUserProfileActivity.class);
                                        i.putExtra("otherUser",user);
                                        i.putExtra("currentUser",currentUser);
                                        context.startActivity(i);
                                        Helper.shared().go((Activity) context);
                                        WaitDialog.dismiss();
                                    }
                                });

                            }
                        }
                    }
                });
                postHolder.text.setOnMentionClickListener(new SocialView.OnClickListener() {
                    @Override
                    public void onClick(@NonNull SocialView view, @NonNull CharSequence username) {
                        String _username = "@"+username.toString();
                        if (_username.equals(currentUser.getUsername())){
                            if (!istanceOfCurrentUserProfile){
                                Intent i = new Intent(context , CurrentUserProfile.class);
                                i.putExtra("currentUser",currentUser);
                                context.startActivity(i);
                                Helper.shared().go((Activity) context);
                            }

                        }else{
                            UserService.shared().getOthUserIdByMention("@"+username.toString(), new StringCompletion() {
                                @Override
                                public void getString(String otherUserId) {
                                    if (otherUserId!=null){
                                        if (!istanceOfOtherUserProfile){
                                            UserService.shared().getOtherUser((Activity) context, otherUserId, new OtherUserService() {
                                                @Override
                                                public void callback(OtherUser user) {
                                                    if (user!=null){
                                                        WaitDialog.dismiss();
                                                        Intent i = new Intent(context , OtherUserProfileActivity.class);
                                                        i.putExtra("currentUser",currentUser);
                                                        i.putExtra("otherUser",user);
                                                        context.startActivity(i);
                                                        Helper.shared().go((Activity) context);
                                                    }
                                                }
                                            });
                                        }

                                    }else{
                                        TipDialog.show((AppCompatActivity) context, "Böyle Bir Kullanıcı Bulunmuyor", TipDialog.TYPE.ERROR);
                                        TipDialog.dismiss(1000);
                                    }

                                }
                            });

                        }
                    }
                });
                break;


            case VIEW_TYPE_LESSON_POST:
                MajorPostViewHolder majorPostViewHolder = (MajorPostViewHolder) holder;
                LessonPostModel major_menuItem = lessonPostModel;
                majorPostViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(context , CommentActivity.class);
                        intent.putExtra("lessonPost",major_menuItem);
                        intent.putExtra("currentUser",currentUser);
                        context.startActivity(intent);
                        Helper.shared().go((Activity) context);
                    }
                });

                majorPostViewHolder.comment.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(context , CommentActivity.class);
                        intent.putExtra("lessonPost",major_menuItem);
                        intent.putExtra("currentUser",currentUser);
                        context.startActivity(intent);
                        Helper.shared().go((Activity) context);
                    }
                });

                majorPostViewHolder.profileImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (lessonPostModel.getSenderUid().equals(currentUser.getUid())){
                            if (!istanceOfCurrentUserProfile){
                                Intent i = new Intent(context , CurrentUserProfile.class);
                                i.putExtra("currentUser",currentUser);
                                context.startActivity(i);
                                Helper.shared().go((Activity)context);
                            }
                        }else{
                            if (!istanceOfOtherUserProfile){
                                UserService.shared().getOtherUser((Activity) context, lessonPostModel.getSenderUid(), new OtherUserService() {
                                    @Override
                                    public void callback(OtherUser user) {
                                        Intent i  = new Intent(context , OtherUserProfileActivity.class);
                                        i.putExtra("otherUser",user);
                                        i.putExtra("currentUser",currentUser);
                                        context.startActivity(i);
                                        Helper.shared().go((Activity) context);
                                        WaitDialog.dismiss();
                                    }
                                });

                            }
                        }
                    }
                });
                majorPostViewHolder.name.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (lessonPostModel.getSenderUid().equals(currentUser.getUid())){
                            if (!istanceOfCurrentUserProfile){
                                Intent i = new Intent(context , CurrentUserProfile.class);
                                i.putExtra("currentUser",currentUser);
                                context.startActivity(i);
                                Helper.shared().go((Activity)context);
                            }
                        }else{
                            if (!istanceOfOtherUserProfile){
                                UserService.shared().getOtherUser((Activity) context, lessonPostModel.getSenderUid(), new OtherUserService() {
                                    @Override
                                    public void callback(OtherUser user) {
                                        Intent i  = new Intent(context , OtherUserProfileActivity.class);
                                        i.putExtra("otherUser",user);
                                        i.putExtra("currentUser",currentUser);
                                        context.startActivity(i);
                                        Helper.shared().go((Activity) context);
                                        WaitDialog.dismiss();
                                    }
                                });

                            }
                        }
                    }
                });
                majorPostViewHolder.more.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {


                        if (lessonPostModel.getSenderUid().equals(currentUser.getUid())){
                            ArrayList<LessonPostModel> lessonPostModels = new ArrayList<>();
                            lessonPostModels.add(lessonPostModel);
                            Helper.shared().MajorPostCurrentUserBottomSheetLaunher((Activity) context, lessonPostModels, currentUser, lessonPostModel, new TrueFalse<Boolean>() {
                                @Override
                                public void callBack(Boolean _value) {
                                    if (_value){
                                        notifyDataSetChanged();
                                    }
                                }
                            });
                        }else{
                            UserService.shared().getOtherUser((Activity) context, lessonPostModel.getSenderUid(), new OtherUserService() {
                                @Override
                                public void callback(OtherUser user) {
                                    ArrayList<LessonPostModel> lessonPostModels = new ArrayList<>();
                                    lessonPostModels.add(lessonPostModel);
                                    Helper.shared().MajorPostOtherUserBottomSheetLaunher((Activity) context, lessonPostModels, user, currentUser, lessonPostModel, optionSelect,new TrueFalse<Boolean>() {
                                        @Override
                                        public void callBack(Boolean _value) {
                                            if (_value){
                                                notifyDataSetChanged();
                                            }
                                        }
                                    });
                                }
                            });
                            WaitDialog.dismiss();

                        }



                    }
                });
                majorPostViewHolder.text.setOnHyperlinkClickListener(new SocialView.OnClickListener() {
                    @Override
                    public void onClick(@NonNull SocialView view, @NonNull CharSequence text) {
                        String url = text.toString();
                        if (!url.startsWith("http://") && !url.startsWith("https://"))
                            url = "http://" + url;
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        context.startActivity(browserIntent);
                    }
                });
                majorPostViewHolder.setCommentLbl(major_menuItem.getComment());
                majorPostViewHolder.setName(major_menuItem.getSenderName());
                majorPostViewHolder.setUserName(major_menuItem.getUsername());
                majorPostViewHolder.setProfileImage(major_menuItem.getThumb_image());
                majorPostViewHolder.setLessonName(major_menuItem.getLessonName());
                majorPostViewHolder.setText(major_menuItem.getText());
                majorPostViewHolder.setDislikeLbl(major_menuItem.getDislike());
                majorPostViewHolder.setLikeLbl(major_menuItem.getLikes());
                majorPostViewHolder.setLike(major_menuItem.getLikes(),currentUser , context);
                majorPostViewHolder.setDislike(major_menuItem.getDislike(),currentUser , context);
                majorPostViewHolder.setFav(major_menuItem.getFavori(),currentUser,context);
                majorPostViewHolder.setTime(major_menuItem.getPostTime());
                majorPostViewHolder.setLinkButton(major_menuItem.getLink());

                majorPostViewHolder.text.setOnMentionClickListener(new SocialView.OnClickListener() {
                    @Override
                    public void onClick(@NonNull SocialView view, @NonNull CharSequence username) {
                        String _username = "@"+username.toString();
                        if (_username.equals(currentUser.getUsername())){
                            if (!istanceOfCurrentUserProfile){
                                Intent i = new Intent(context , CurrentUserProfile.class);
                                i.putExtra("currentUser",currentUser);
                                context.startActivity(i);
                                Helper.shared().go((Activity) context);
                            }

                        }else{
                            UserService.shared().getOthUserIdByMention("@"+username.toString(), new StringCompletion() {
                                @Override
                                public void getString(String otherUserId) {
                                    if (otherUserId!=null){
                                        if (!istanceOfOtherUserProfile){
                                            UserService.shared().getOtherUser((Activity) context, otherUserId, new OtherUserService() {
                                                @Override
                                                public void callback(OtherUser user) {
                                                    if (user!=null){
                                                        WaitDialog.dismiss();
                                                        Intent i = new Intent(context , OtherUserProfileActivity.class);
                                                        i.putExtra("currentUser",currentUser);
                                                        i.putExtra("otherUser",user);
                                                        context.startActivity(i);
                                                        Helper.shared().go((Activity) context);
                                                    }
                                                }
                                            });
                                        }

                                    }else{
                                        TipDialog.show((AppCompatActivity) context, "Böyle Bir Kullanıcı Bulunmuyor", TipDialog.TYPE.ERROR);
                                        TipDialog.dismiss(1000);
                                    }

                                }
                            });

                        }
                    }
                });

                majorPostViewHolder.linkButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(major_menuItem.getLink()));
                        context.startActivity(browserIntent);
                    }
                });
                majorPostViewHolder.itemView.findViewById(R.id.like).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        MajorPostService.shared().setLike(currentUser, major_menuItem, new TrueFalse<Boolean>() {
                            @Override
                            public void callBack(Boolean _value) {
                                if (_value){
                                    majorPostViewHolder.setLike(major_menuItem.getLikes(),currentUser,context);
                                    notifyDataSetChanged();
                                }
                            }
                        });
                    }
                });

                majorPostViewHolder.itemView.findViewById(R.id.dislike).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        MajorPostService.shared().setDislike(currentUser, major_menuItem, new TrueFalse<Boolean>() {
                            @Override
                            public void callBack(Boolean _value) {
                                if (_value)
                                {
                                    majorPostViewHolder.setDislike(major_menuItem.getDislike(),currentUser,context);
                                    notifyDataSetChanged();
                                }
                            }
                        });
                    }
                });

                majorPostViewHolder.itemView.findViewById(R.id.fav).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        MajorPostService.shared().setFav(currentUser, major_menuItem, new TrueFalse<Boolean>() {
                            @Override
                            public void callBack(Boolean _value) {
                                if (_value){
                                    majorPostViewHolder.setFav(major_menuItem.getFavori(),currentUser,context);
                                    notifyDataSetChanged();
                                }
                            }
                        });
                    }
                });
                break;

        }
    }

    @Override
    public int getItemViewType(int position) {

        if (lessonPostModel != null){
            if (lessonPostModel.getData().isEmpty()){
                return VIEW_TYPE_LESSON_POST;
            }else{
                return  VIEW_TYPE_LESSON_POST_DATA;
            }
        }else if (noticesMainModel!=null){
            if (noticesMainModel.getData().isEmpty()){
                return VIEW_TYPE_SCHOOL_POST;
            }else{
                return  VIEW_TYPE_SCHOOL_POST_DATA;
            }
        }
        else if (mainPostModel != null){
            if (mainPostModel.getPostType()!=null && mainPostModel.getPostType().equals("food-me")){
                if (mainPostModel.getData().isEmpty()){
                    return FOOD_ME_POST;
                }else{
                    return FOOD_ME_DATA_POST;
                }

            }else if (mainPostModel.getPostType()!=null && mainPostModel.getPostType().equals("sell-buy")){
                if (mainPostModel.getData().isEmpty()){
                    return BUYSELL__POST;
                }else{
                    return BUYSELL_DATA_POST;
                }
            }else if (mainPostModel.getPostType()!=null && mainPostModel.getPostType().equals("camping")){
                if (mainPostModel.getData().isEmpty()){
                    return CAMPING__POST;
                }else {
                    return  CAMPING_DATA_POST;
                }

            }
        }

        return super.getItemViewType(position);

    }

    @Override
    public int getItemCount() {
        return 1;
    }
}
