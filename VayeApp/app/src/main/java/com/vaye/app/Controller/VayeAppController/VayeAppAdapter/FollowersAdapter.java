package com.vaye.app.Controller.VayeAppController.VayeAppAdapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.ads.formats.NativeAd;
import com.google.android.gms.ads.formats.UnifiedNativeAd;
import com.google.android.gms.ads.formats.UnifiedNativeAdView;
import com.hendraanggrian.appcompat.widget.SocialView;
import com.kongzue.dialog.v3.TipDialog;
import com.kongzue.dialog.v3.WaitDialog;
import com.vaye.app.Controller.HomeController.LessonPostAdapter.MajorPostViewHolder;
import com.vaye.app.Controller.HomeController.LessonPostAdapter.UnifiedNativeAdViewHolder;
import com.vaye.app.Controller.HomeController.PagerAdapter.AllDatasActivity;
import com.vaye.app.Controller.Profile.CurrentUserProfile;
import com.vaye.app.Controller.Profile.OtherUserProfileActivity;
import com.vaye.app.Controller.VayeAppController.BuySell.BuySellViewHolder;
import com.vaye.app.Controller.VayeAppController.Camping.CampingViewHolder;
import com.vaye.app.Controller.VayeAppController.CommentController.MainPostCommentActivity;
import com.vaye.app.Controller.VayeAppController.FoodMe.FoodMeViewHolder;
import com.vaye.app.Interfaces.Notifications;
import com.vaye.app.Interfaces.OtherUserService;
import com.vaye.app.Interfaces.StringCompletion;
import com.vaye.app.Interfaces.TrueFalse;
import com.vaye.app.Model.CurrentUser;
import com.vaye.app.Model.LessonPostModel;
import com.vaye.app.Model.MainPostModel;
import com.vaye.app.Model.OtherUser;
import com.vaye.app.R;
import com.vaye.app.Services.MainPostService;
import com.vaye.app.Services.UserService;
import com.vaye.app.Util.Helper;

import java.util.ArrayList;

public class FollowersAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    ArrayList<MainPostModel> post;
    Context context;
    CurrentUser currentUser;
    Boolean istanceOfCurrentUserProfile = false;
    Boolean istanceOfOtherUserProfile = false;
    public FollowersAdapter(ArrayList<MainPostModel> post, Context context, CurrentUser currentUser) {
        this.post = post;
        this.context = context;
        this.currentUser = currentUser;
        if (context instanceof CurrentUserProfile){
            istanceOfCurrentUserProfile = true;
        }else{
            istanceOfCurrentUserProfile = false;
        }
        if (context instanceof  OtherUserProfileActivity){
            istanceOfOtherUserProfile= true;
        }else{
            istanceOfOtherUserProfile = false;
        }

    }
    ///FOOD ME İNİT
    private static final int FOODME = 1;
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

    private static final int VIEW_TYPE_ADS  = 3;
    private static final int VIEW_TYPE_EMPTY  = 4;

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == FOOD_ME_POST){
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

        else if (viewType == VIEW_TYPE_ADS){
            UnifiedNativeAdView view = (UnifiedNativeAdView) LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.native_ads_item,parent,false);
            return new UnifiedNativeAdViewHolder(view);
        }
        else {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.empty_lesson_post, parent, false);

            return new MajorPostViewHolder(itemView);

        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int i) {
        int viewType = getItemViewType(i);
        MainPostModel menuItemData = post.get(i);
        MainPostModel menuItem = post.get(i);

        switch (viewType){
            case  VIEW_TYPE_ADS:
                UnifiedNativeAd nativeAd = (UnifiedNativeAd) post.get(i).getNativeAd();
                populateNativeAdView(nativeAd, ((UnifiedNativeAdViewHolder) holder).getAdView());
                break;

            case VIEW_TYPE_EMPTY:
                break;
            case CAMPING__POST:
                CampingViewHolder caping_post_holder = (CampingViewHolder) holder;


                caping_post_holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(context , MainPostCommentActivity.class);
                        intent.putExtra("post",post.get(i));
                        intent.putExtra("currentUser",currentUser);
                        context.startActivity(intent);
                        Helper.shared().go((Activity) context);
                    }
                });

                caping_post_holder.setCommentLbl(menuItem.getComment());
                caping_post_holder.setName(menuItem.getSenderName());
                caping_post_holder.setUserName(menuItem.getUsername());
                caping_post_holder.setProfileImage(menuItem.getThumb_image());

                caping_post_holder.setText(menuItem.getText());
                caping_post_holder.setDislikeLbl(menuItem.getDislike());
                caping_post_holder.setLikeLbl(menuItem.getLikes());
                caping_post_holder.setLike(menuItem.getLikes(),currentUser , context);
                caping_post_holder.setDislike(menuItem.getDislike(),currentUser , context);

                caping_post_holder.setTime(menuItem.getPostTime());
                caping_post_holder.setLocationButton(menuItem.getGeoPoint());
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
                            if (menuItem.getSenderUid().equals(currentUser.getUid())){
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
                                }else{
                                    return;
                                }

                            }



                        }

                });
                caping_post_holder.itemView.findViewById(R.id.name).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {


                        if (menuItem.getSenderUid().equals(currentUser.getUid())){
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
                        MainPostService.shared().setPostLike(menuItem, currentUser, Notifications.NotificationType.like_food_me, Notifications.NotificationDescription.like_food_me, new TrueFalse<Boolean>() {
                            @Override
                            public void callBack(Boolean _value) {
                                caping_post_holder.setLike(menuItem.getLikes(),currentUser ,context);
                                notifyDataSetChanged();
                            }
                        });

                    }
                });

                caping_post_holder.itemView.findViewById(R.id.dislike).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        MainPostService.shared().setPostDislike(menuItem, currentUser, new TrueFalse<Boolean>() {
                            @Override
                            public void callBack(Boolean _value) {
                                caping_post_holder.setDislike(menuItem.getDislike(),currentUser,context);
                                notifyDataSetChanged();
                            }
                        });
                    }
                });
                caping_post_holder.itemView.findViewById(R.id.locationButton).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                    }
                });

                caping_post_holder.itemView.findViewById(R.id.setting).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (menuItem.getSenderUid().equals(currentUser.getUid())){
                            Helper.shared().VayeAppCurrentUserBottomSheetLauncher(post,(Activity) context, currentUser, menuItem, new TrueFalse<Boolean>() {
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
                                    Helper.shared().VayeAppOtherUserBottomSheetLauncher(post,(Activity) context, user, currentUser, menuItem, new TrueFalse<Boolean>() {
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
                camping_post_data_holder.setCommentLbl(menuItemData.getComment());
                camping_post_data_holder.setImages(menuItemData.getThumbData());
                camping_post_data_holder.setName(menuItemData.getSenderName());
                camping_post_data_holder.setUserName(menuItemData.getUsername());
                camping_post_data_holder.setProfileImage(menuItemData.getThumb_image());
                camping_post_data_holder.setText(menuItemData.getText());
                camping_post_data_holder.setDislikeLbl(menuItemData.getDislike());
                camping_post_data_holder.setLikeLbl(menuItemData.getLikes());
                camping_post_data_holder.setLike(menuItemData.getLikes(),currentUser , context);
                camping_post_data_holder.setDislike(menuItemData.getDislike(),currentUser , context);
                camping_post_data_holder.setTime(menuItemData.getPostTime());
                camping_post_data_holder.setLocationButton(menuItemData.getGeoPoint());
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


                        if (menuItemData.getSenderUid().equals(currentUser.getUid())){
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
                            }else{
                                return;
                            }

                        }


                    }
                });
                camping_post_data_holder.itemView.findViewById(R.id.name).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (menuItemData.getSenderUid().equals(currentUser.getUid())){
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

                    }
                });
                camping_post_data_holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(context , MainPostCommentActivity.class);
                        intent.putExtra("post",post.get(i));
                        intent.putExtra("currentUser",currentUser);
                        context.startActivity(intent);
                        Helper.shared().go((Activity) context);
                    }
                });

                camping_post_data_holder.itemView.findViewById(R.id.like).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        MainPostService.shared().setPostLike(menuItemData, currentUser, Notifications.NotificationType.like_food_me, Notifications.NotificationDescription.like_food_me, new TrueFalse<Boolean>() {
                            @Override
                            public void callBack(Boolean _value) {
                                camping_post_data_holder.setLike(menuItemData.getLikes(),currentUser ,context);
                                notifyDataSetChanged();
                            }
                        });
                    }
                });

                camping_post_data_holder.itemView.findViewById(R.id.dislike).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        MainPostService.shared().setPostDislike(menuItemData, currentUser, new TrueFalse<Boolean>() {
                            @Override
                            public void callBack(Boolean _value) {
                                if (_value){
                                    camping_post_data_holder.setDislike(menuItemData.getDislike(),currentUser,context);
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
                        i.putExtra("url",menuItemData.getData());
                        context.startActivity(i);
                        Helper.shared().go((Activity) context);
                    }
                });
                camping_post_data_holder.itemView.findViewById(R.id.two_image).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent i = new Intent(context , AllDatasActivity.class);
                        i.putExtra("url",menuItemData.getData());

                        context.startActivity(i);
                        Helper.shared().go((Activity) context);
                    }
                });
                camping_post_data_holder.itemView.findViewById(R.id.there_image).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent i = new Intent(context , AllDatasActivity.class);
                        i.putExtra("url",menuItemData.getData());

                        context.startActivity(i);
                        Helper.shared().go((Activity) context);
                    }
                });
                camping_post_data_holder.itemView.findViewById(R.id.four_image).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent i = new Intent(context , AllDatasActivity.class);
                        i.putExtra("url",menuItemData.getData());

                        context.startActivity(i);
                        Helper.shared().go((Activity) context);
                    }
                });

                camping_post_data_holder.itemView.findViewById(R.id.setting).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (menuItemData.getSenderUid().equals(currentUser.getUid())){
                            Helper.shared().VayeAppCurrentUserBottomSheetLauncher(post,(Activity) context, currentUser, menuItemData, new TrueFalse<Boolean>() {
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
                                    Helper.shared().VayeAppOtherUserBottomSheetLauncher(post,(Activity) context, user, currentUser, menuItemData, new TrueFalse<Boolean>() {
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

                foodme_post_data_holder.setCommentLbl(menuItemData.getComment());
                foodme_post_data_holder.setImages(menuItemData.getThumbData());
                foodme_post_data_holder.setName(menuItemData.getSenderName());
                foodme_post_data_holder.setUserName(menuItemData.getUsername());
                foodme_post_data_holder.setProfileImage(menuItemData.getThumb_image());
                foodme_post_data_holder.setText(menuItemData.getText());
                foodme_post_data_holder.setDislikeLbl(menuItemData.getDislike());
                foodme_post_data_holder.setLikeLbl(menuItemData.getLikes());
                foodme_post_data_holder.setLike(menuItemData.getLikes(),currentUser , context);
                foodme_post_data_holder.setDislike(menuItemData.getDislike(),currentUser , context);
                foodme_post_data_holder.setTime(menuItemData.getPostTime());
                foodme_post_data_holder.setLocationButton(menuItemData.getGeoPoint());
                foodme_post_data_holder.itemView.findViewById(R.id.profileLay).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {



                        if (menuItemData.getSenderUid().equals(currentUser.getUid())){
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
                            }else{
                                return;
                            }

                        }
                    }
                });
                foodme_post_data_holder.itemView.findViewById(R.id.name).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if (menuItemData.getSenderUid().equals(currentUser.getUid())){
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

                    }
                });
                foodme_post_data_holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(context , MainPostCommentActivity.class);
                        intent.putExtra("post",post.get(i));
                        intent.putExtra("currentUser",currentUser);
                        context.startActivity(intent);
                        Helper.shared().go((Activity) context);
                    }
                });

                foodme_post_data_holder.itemView.findViewById(R.id.like).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        MainPostService.shared().setPostLike(menuItemData, currentUser, Notifications.NotificationType.like_food_me, Notifications.NotificationDescription.like_food_me, new TrueFalse<Boolean>() {
                            @Override
                            public void callBack(Boolean _value) {
                                foodme_post_data_holder.setLike(menuItemData.getLikes(),currentUser ,context);
                                notifyDataSetChanged();
                            }
                        });
                    }
                });

                foodme_post_data_holder.itemView.findViewById(R.id.dislike).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        MainPostService.shared().setPostDislike(menuItemData, currentUser, new TrueFalse<Boolean>() {
                            @Override
                            public void callBack(Boolean _value) {
                                if (_value){
                                    foodme_post_data_holder.setDislike(menuItemData.getDislike(),currentUser,context);
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
                        i.putExtra("url",menuItemData.getData());
                        context.startActivity(i);
                        Helper.shared().go((Activity) context);
                    }
                });
                foodme_post_data_holder.itemView.findViewById(R.id.two_image).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent i = new Intent(context , AllDatasActivity.class);
                        i.putExtra("url",menuItemData.getData());

                        context.startActivity(i);
                        Helper.shared().go((Activity) context);
                    }
                });
                foodme_post_data_holder.itemView.findViewById(R.id.there_image).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent i = new Intent(context , AllDatasActivity.class);
                        i.putExtra("url",menuItemData.getData());

                        context.startActivity(i);
                        Helper.shared().go((Activity) context);
                    }
                });
                foodme_post_data_holder.itemView.findViewById(R.id.four_image).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent i = new Intent(context , AllDatasActivity.class);
                        i.putExtra("url",menuItemData.getData());

                        context.startActivity(i);
                        Helper.shared().go((Activity) context);
                    }
                });

                foodme_post_data_holder.itemView.findViewById(R.id.setting).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (menuItemData.getSenderUid().equals(currentUser.getUid())){
                            Helper.shared().VayeAppCurrentUserBottomSheetLauncher(post,(Activity) context, currentUser, menuItemData, new TrueFalse<Boolean>() {
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
                                    Helper.shared().VayeAppOtherUserBottomSheetLauncher(post,(Activity) context, user, currentUser, menuItemData, new TrueFalse<Boolean>() {
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

                foodme_post_holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(context , MainPostCommentActivity.class);
                        intent.putExtra("post",post.get(i));
                        intent.putExtra("currentUser",currentUser);
                        context.startActivity(intent);
                        Helper.shared().go((Activity) context);
                    }
                });

                foodme_post_holder.setCommentLbl(menuItem.getComment());
                foodme_post_holder.setName(menuItem.getSenderName());
                foodme_post_holder.setUserName(menuItem.getUsername());
                foodme_post_holder.setProfileImage(menuItem.getThumb_image());

                foodme_post_holder.setText(menuItem.getText());
                foodme_post_holder.setDislikeLbl(menuItem.getDislike());
                foodme_post_holder.setLikeLbl(menuItem.getLikes());
                foodme_post_holder.setLike(menuItem.getLikes(),currentUser , context);
                foodme_post_holder.setDislike(menuItem.getDislike(),currentUser , context);

                foodme_post_holder.setTime(menuItem.getPostTime());
                foodme_post_holder.setLocationButton(menuItem.getGeoPoint());
                foodme_post_holder.itemView.findViewById(R.id.profileLay).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (menuItem.getSenderUid().equals(currentUser.getUid())){
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
                            }else{
                                return;
                            }

                        }
                    }
                });
                foodme_post_holder.itemView.findViewById(R.id.name).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (menuItem.getSenderUid().equals(currentUser.getUid())){
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

                    }
                });
                foodme_post_holder.itemView.findViewById(R.id.like).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        MainPostService.shared().setPostLike(menuItem, currentUser, Notifications.NotificationType.like_food_me, Notifications.NotificationDescription.like_food_me, new TrueFalse<Boolean>() {
                            @Override
                            public void callBack(Boolean _value) {
                                foodme_post_holder.setLike(menuItem.getLikes(),currentUser ,context);
                                notifyDataSetChanged();
                            }
                        });

                    }
                });

                foodme_post_holder.itemView.findViewById(R.id.dislike).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        MainPostService.shared().setPostDislike(menuItem, currentUser, new TrueFalse<Boolean>() {
                            @Override
                            public void callBack(Boolean _value) {
                                foodme_post_holder.setDislike(menuItem.getDislike(),currentUser,context);
                                notifyDataSetChanged();
                            }
                        });
                    }
                });
                foodme_post_holder.itemView.findViewById(R.id.locationButton).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                    }
                });

                foodme_post_holder.itemView.findViewById(R.id.setting).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (menuItem.getSenderUid().equals(currentUser.getUid())){
                            Helper.shared().VayeAppCurrentUserBottomSheetLauncher(post,(Activity) context, currentUser, menuItem, new TrueFalse<Boolean>() {
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
                                    Helper.shared().VayeAppOtherUserBottomSheetLauncher(post,(Activity) context, user, currentUser, menuItem, new TrueFalse<Boolean>() {
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
            case BUYSELL__POST:
                BuySellViewHolder buy_sell_post_holder = (BuySellViewHolder) holder;

                buy_sell_post_holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(context , MainPostCommentActivity.class);
                        intent.putExtra("post",post.get(i));
                        intent.putExtra("currentUser",currentUser);
                        context.startActivity(intent);
                        Helper.shared().go((Activity) context);
                    }
                });

                buy_sell_post_holder.setCommentLbl(menuItem.getComment());
                buy_sell_post_holder.setName(menuItem.getSenderName());
                buy_sell_post_holder.setUserName(menuItem.getUsername());
                buy_sell_post_holder.setProfileImage(menuItem.getThumb_image());

                buy_sell_post_holder.setText(menuItem.getText());
                buy_sell_post_holder.setDislikeLbl(menuItem.getDislike());
                buy_sell_post_holder.setLikeLbl(menuItem.getLikes());
                buy_sell_post_holder.setLike(menuItem.getLikes(),currentUser , context);
                buy_sell_post_holder.setDislike(menuItem.getDislike(),currentUser , context);

                buy_sell_post_holder.setTime(menuItem.getPostTime());
                buy_sell_post_holder.setLocationButton(menuItem.getGeoPoint());

                buy_sell_post_holder.itemView.findViewById(R.id.profileLay).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (menuItem.getSenderUid().equals(currentUser.getUid())){
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
                            }else{
                                return;
                            }

                        }
                    }
                });
                buy_sell_post_holder.itemView.findViewById(R.id.name).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (menuItem.getSenderUid().equals(currentUser.getUid())){
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

                    }
                });
                buy_sell_post_holder.itemView.findViewById(R.id.like).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        MainPostService.shared().setPostLike(menuItem, currentUser, Notifications.NotificationType.like_food_me, Notifications.NotificationDescription.like_food_me, new TrueFalse<Boolean>() {
                            @Override
                            public void callBack(Boolean _value) {
                                buy_sell_post_holder.setLike(menuItem.getLikes(),currentUser ,context);
                                notifyDataSetChanged();
                            }
                        });

                    }
                });

                buy_sell_post_holder.itemView.findViewById(R.id.dislike).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        MainPostService.shared().setPostDislike(menuItem, currentUser, new TrueFalse<Boolean>() {
                            @Override
                            public void callBack(Boolean _value) {
                                buy_sell_post_holder.setDislike(menuItem.getDislike(),currentUser,context);
                                notifyDataSetChanged();
                            }
                        });
                    }
                });
                buy_sell_post_holder.itemView.findViewById(R.id.locationButton).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                    }
                });

                buy_sell_post_holder.itemView.findViewById(R.id.setting).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (menuItem.getSenderUid().equals(currentUser.getUid())){
                            Helper.shared().VayeAppCurrentUserBottomSheetLauncher(post,(Activity) context, currentUser, menuItem, new TrueFalse<Boolean>() {
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
                                    Helper.shared().VayeAppOtherUserBottomSheetLauncher(post,(Activity) context, user, currentUser, menuItem, new TrueFalse<Boolean>() {
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
                buy_sell_post_holder.setValue(menuItem.getValue());
                break;
            case BUYSELL_DATA_POST:
                BuySellViewHolder buy_sell_post_data_holder = (BuySellViewHolder) holder;

                buy_sell_post_data_holder.setValue(menuItemData.getValue());
                buy_sell_post_data_holder.setCommentLbl(menuItemData.getComment());
                buy_sell_post_data_holder.setImages(menuItemData.getThumbData());
                buy_sell_post_data_holder.setName(menuItemData.getSenderName());
                buy_sell_post_data_holder.setUserName(menuItemData.getUsername());
                buy_sell_post_data_holder.setProfileImage(menuItemData.getThumb_image());
                buy_sell_post_data_holder.setText(menuItemData.getText());
                buy_sell_post_data_holder.setDislikeLbl(menuItemData.getDislike());
                buy_sell_post_data_holder.setLikeLbl(menuItemData.getLikes());
                buy_sell_post_data_holder.setLike(menuItemData.getLikes(),currentUser , context);
                buy_sell_post_data_holder.setDislike(menuItemData.getDislike(),currentUser , context);
                buy_sell_post_data_holder.setTime(menuItemData.getPostTime());
                buy_sell_post_data_holder.itemView.findViewById(R.id.profileLay).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if (menuItemData.getSenderUid().equals(currentUser.getUid())){
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
                            }else{
                                return;
                            }

                        }
                    }
                });
                buy_sell_post_data_holder.itemView.findViewById(R.id.name).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if (menuItemData.getSenderUid().equals(currentUser.getUid())){
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
                            }else{
                                return;
                            }

                        }
                    }
                });
                buy_sell_post_data_holder.setLocationButton(menuItemData.getGeoPoint());
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

                    }
                });
                buy_sell_post_data_holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(context , MainPostCommentActivity.class);
                        intent.putExtra("post",post.get(i));
                        intent.putExtra("currentUser",currentUser);
                        context.startActivity(intent);
                        Helper.shared().go((Activity) context);
                    }
                });

                buy_sell_post_data_holder.itemView.findViewById(R.id.like).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        MainPostService.shared().setPostLike(menuItemData, currentUser, Notifications.NotificationType.like_food_me, Notifications.NotificationDescription.like_food_me, new TrueFalse<Boolean>() {
                            @Override
                            public void callBack(Boolean _value) {
                                buy_sell_post_data_holder.setLike(menuItemData.getLikes(),currentUser ,context);
                                notifyDataSetChanged();
                            }
                        });
                    }
                });

                buy_sell_post_data_holder.itemView.findViewById(R.id.dislike).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        MainPostService.shared().setPostDislike(menuItemData, currentUser, new TrueFalse<Boolean>() {
                            @Override
                            public void callBack(Boolean _value) {
                                if (_value){
                                    buy_sell_post_data_holder.setDislike(menuItemData.getDislike(),currentUser,context);
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
                        i.putExtra("url",menuItemData.getData());
                        context.startActivity(i);
                        Helper.shared().go((Activity) context);
                    }
                });
                buy_sell_post_data_holder.itemView.findViewById(R.id.two_image).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent i = new Intent(context , AllDatasActivity.class);
                        i.putExtra("url",menuItemData.getData());

                        context.startActivity(i);
                        Helper.shared().go((Activity) context);
                    }
                });
                buy_sell_post_data_holder.itemView.findViewById(R.id.there_image).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent i = new Intent(context , AllDatasActivity.class);
                        i.putExtra("url",menuItemData.getData());

                        context.startActivity(i);
                        Helper.shared().go((Activity) context);
                    }
                });
                buy_sell_post_data_holder.itemView.findViewById(R.id.four_image).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent i = new Intent(context , AllDatasActivity.class);
                        i.putExtra("url",menuItemData.getData());

                        context.startActivity(i);
                        Helper.shared().go((Activity) context);
                    }
                });

                buy_sell_post_data_holder.itemView.findViewById(R.id.setting).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (menuItemData.getSenderUid().equals(currentUser.getUid())){
                            Helper.shared().VayeAppCurrentUserBottomSheetLauncher(post,(Activity) context, currentUser, menuItemData, new TrueFalse<Boolean>() {
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
                                    Helper.shared().VayeAppOtherUserBottomSheetLauncher(post,(Activity) context, user, currentUser, menuItemData, new TrueFalse<Boolean>() {
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
        }
    }

    @Override
    public int getItemCount() {
        return post.size();
    }
    private void populateNativeAdView(UnifiedNativeAd nativeAd,
                                      UnifiedNativeAdView adView) {
        // Some assets are guaranteed to be in every UnifiedNativeAd.
        ((TextView) adView.getHeadlineView()).setText(nativeAd.getHeadline());
        ((TextView) adView.getBodyView()).setText(nativeAd.getBody());
        ((Button) adView.getCallToActionView()).setText(nativeAd.getCallToAction());

        // These assets aren't guaranteed to be in every UnifiedNativeAd, so it's important to
        // check before trying to display them.
        NativeAd.Image icon = nativeAd.getIcon();

        if (icon == null) {
            adView.getIconView().setVisibility(View.INVISIBLE);
        } else {
            ((ImageView) adView.getIconView()).setImageDrawable(icon.getDrawable());
            adView.getIconView().setVisibility(View.VISIBLE);
        }

        if (nativeAd.getPrice() == null) {
            adView.getPriceView().setVisibility(View.INVISIBLE);
        } else {
            adView.getPriceView().setVisibility(View.VISIBLE);
            ((TextView) adView.getPriceView()).setText(nativeAd.getPrice());
        }

        if (nativeAd.getStore() == null) {
            adView.getStoreView().setVisibility(View.INVISIBLE);
        } else {
            adView.getStoreView().setVisibility(View.VISIBLE);
            ((TextView) adView.getStoreView()).setText(nativeAd.getStore());
        }

        if (nativeAd.getStarRating() == null) {
            adView.getStarRatingView().setVisibility(View.INVISIBLE);
        } else {
            ((RatingBar) adView.getStarRatingView())
                    .setRating(nativeAd.getStarRating().floatValue());
            adView.getStarRatingView().setVisibility(View.VISIBLE);
        }

        if (nativeAd.getAdvertiser() == null) {
            adView.getAdvertiserView().setVisibility(View.INVISIBLE);
        } else {
            ((TextView) adView.getAdvertiserView()).setText(nativeAd.getAdvertiser());
            adView.getAdvertiserView().setVisibility(View.VISIBLE);
        }

        // Assign native ad object to the native view.
        adView.setNativeAd(nativeAd);
    }
    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        MainPostModel model = (MainPostModel)post.get(position);

        if (model.getPostType()!=null && model.getPostType().equals("food-me")){
            if (model.getType()!=null && model.getType().equals("post")){
                return FOOD_ME_POST;
            }else{
                return FOOD_ME_DATA_POST;
            }

        }else if (model.getPostType()!=null && model.getPostType().equals("sell-buy")){
            if (model.getType()!=null && model.getType().equals("post")){
                return BUYSELL__POST;
            }else{
                return BUYSELL_DATA_POST;
            }
        }else if (model.getPostType()!=null && model.getPostType().equals("camping")){
            if (model.getType()!=null && model.getType().equals("post")){
                return CAMPING__POST;
            }else if (model.getType()!=null && model.getType().equals("data")){
                return  CAMPING_DATA_POST;
            }

        }
        else if (model.getPostType()!=null&& model.getPostType().equals("ads")) {
            return  VIEW_TYPE_ADS;
        }else if (model.getEmpty().equals("empty")){
            return  VIEW_TYPE_EMPTY;
        }
        return super.getItemViewType(position);
    }
}
