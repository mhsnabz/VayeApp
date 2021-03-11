package com.vaye.app.Controller.HomeController.LessonPostAdapter;

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
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.rpc.Help;
import com.hendraanggrian.appcompat.widget.SocialView;
import com.kongzue.dialog.v3.TipDialog;
import com.kongzue.dialog.v3.WaitDialog;
import com.vaye.app.Controller.HomeController.PagerAdapter.AllDatasActivity;
import com.vaye.app.Controller.HomeController.SinglePost.CommentActivity;
import com.vaye.app.Controller.Profile.CurrentUserProfile;
import com.vaye.app.Controller.Profile.OtherUserProfileActivity;
import com.vaye.app.Interfaces.OtherUserOptionsCompletion;
import com.vaye.app.Interfaces.OtherUserService;
import com.vaye.app.Interfaces.StringCompletion;
import com.vaye.app.Interfaces.TrueFalse;
import com.vaye.app.Model.CurrentUser;
import com.vaye.app.Model.LessonPostModel;
import com.vaye.app.Model.OtherUser;
import com.vaye.app.R;
import com.vaye.app.Services.MajorPostService;
import com.vaye.app.Services.UserService;
import com.vaye.app.Util.BottomSheetHelper.BottomSheetActionTarget;
import com.vaye.app.Util.BottomSheetHelper.BottomSheetModel;
import com.vaye.app.Util.BottomSheetHelper.BottomSheetTarget;
import com.vaye.app.Util.Helper;

import java.util.ArrayList;
import java.util.Calendar;

public class MajorPostAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = "MajorPostAdapter";
    Boolean istanceOfCurrentUserProfile = false;
    Boolean istanceOfOtherUserProfile = false;
    public MajorPostAdapter(ArrayList<LessonPostModel> post, CurrentUser currentUser, Context context) {
        this.post = post;
        this.currentUser = currentUser;
        this.context = context;
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

    ArrayList<LessonPostModel> post;
    CurrentUser currentUser;
    Context context;

    private static final int VIEW_TYPE_LESSON_POST = 1;
    private static final int VIEW_TYPE_LESSON_POST_DATA  = 2;
    private static final int VIEW_TYPE_ADS  = 3;
    private static final int VIEW_TYPE_EMPTY  = 4;

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

        }else if (viewType == VIEW_TYPE_ADS){
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
    public long getItemId(int position) {
        return position;
    }
    @Override
    public int getItemViewType(int position) {
        LessonPostModel model = (LessonPostModel)post.get(position);
        Log.d(TAG, "getItemViewType: " + model.getPostId());

        if (model.getType()!=null&& model.getType().equals("ads")) {
            return  VIEW_TYPE_ADS;
        }else if ( model.getType().equals("data")){
            return  VIEW_TYPE_LESSON_POST_DATA;
        }else if  ( model.getType().equals("post")){
            return   VIEW_TYPE_LESSON_POST;
        }else if (model.getEmpty().equals("empty")){
            return  VIEW_TYPE_EMPTY;
        }
        return super.getItemViewType(position);
    }
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int i) {
        int viewType = getItemViewType(i);
        switch (viewType) {
            case VIEW_TYPE_LESSON_POST_DATA:
                MajorPostViewHolder postHolder = (MajorPostViewHolder) holder;
                postHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(context , CommentActivity.class);
                        intent.putExtra("post",post.get(i));
                        intent.putExtra("currentUser",currentUser);
                        context.startActivity(intent);
                        Helper.shared().go((Activity) context);
                    }
                });


                postHolder.more.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (post.get(i).getSenderUid().equals(currentUser.getUid())){
                                Helper.shared().MajorPostCurrentUserBottomSheetLaunher((Activity) context, post, currentUser, post.get(i), new TrueFalse<Boolean>() {
                                    @Override
                                    public void callBack(Boolean _value) {
                                        if (_value){
                                            notifyDataSetChanged();
                                        }
                                    }
                                });
                        }else {

                            UserService.shared().getOtherUser((Activity) context, post.get(i).getSenderUid(), new OtherUserService() {
                                @Override
                                public void callback(OtherUser user) {
                                    Helper.shared().MajorPostOtherUserBottomSheetLaunher((Activity) context, post, user, currentUser, post.get(i), new TrueFalse<Boolean>() {
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
                LessonPostModel menuItemData = post.get(i);
                postHolder.setCommentLbl(menuItemData.getComment());

                postHolder.setImages(menuItemData.getThumbData());
                postHolder.setName(menuItemData.getSenderName());
                postHolder.setUserName(menuItemData.getUsername());
                postHolder.setProfileImage(menuItemData.getThumb_image());
                postHolder.setLessonName(menuItemData.getLessonName());
                postHolder.setText(menuItemData.getText());
                postHolder.setDislikeLbl(menuItemData.getDislike());
                postHolder.setLikeLbl(menuItemData.getLikes());
                postHolder.setLike(menuItemData.getLikes(),currentUser , context);
                postHolder.setDislike(menuItemData.getDislike(),currentUser , context);
                postHolder.setFav(menuItemData.getFavori(),currentUser,context);
                postHolder.setTime(menuItemData.getPostTime());
                postHolder.setLinkButton(menuItemData.getLink());
                postHolder.linkButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(menuItemData.getLink()));
                        context.startActivity(browserIntent);
                    }
                });
                postHolder.itemView.findViewById(R.id.like).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        MajorPostService.shared().setLike(currentUser, menuItemData, new TrueFalse<Boolean>() {
                            @Override
                            public void callBack(Boolean _value) {
                                if (_value){
                                    postHolder.setLike(menuItemData.getLikes(),currentUser,context);
                                    notifyDataSetChanged();
                                }
                            }
                        });
                    }
                });

                postHolder.itemView.findViewById(R.id.dislike).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        MajorPostService.shared().setDislike(currentUser, menuItemData, new TrueFalse<Boolean>() {
                            @Override
                            public void callBack(Boolean _value) {
                                if (_value)
                                {
                                    postHolder.setDislike(menuItemData.getDislike(),currentUser,context);
                                    notifyDataSetChanged();
                                }
                            }
                        });
                    }
                });

                postHolder.itemView.findViewById(R.id.fav).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        MajorPostService.shared().setFav(currentUser, menuItemData, new TrueFalse<Boolean>() {
                            @Override
                            public void callBack(Boolean _value) {
                                if (_value){
                                    postHolder.setFav(menuItemData.getFavori(),currentUser,context);
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
                        i.putExtra("url",menuItemData.getData());
                        context.startActivity(i);
                        Helper.shared().go((Activity) context);
                    }
                });
                postHolder.itemView.findViewById(R.id.two_image).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent i = new Intent(context , AllDatasActivity.class);
                        i.putExtra("url",menuItemData.getData());

                        context.startActivity(i);
                        Helper.shared().go((Activity) context);
                    }
                });
                postHolder.itemView.findViewById(R.id.there_image).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent i = new Intent(context , AllDatasActivity.class);
                        i.putExtra("url",menuItemData.getData());

                        context.startActivity(i);
                        Helper.shared().go((Activity) context);
                    }
                });
                postHolder.itemView.findViewById(R.id.four_image).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent i = new Intent(context , AllDatasActivity.class);
                        i.putExtra("url",menuItemData.getData());

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
                        if (post.get(i).getSenderUid().equals(currentUser.getUid())){
                            if (!istanceOfCurrentUserProfile){
                                Intent i = new Intent(context , CurrentUserProfile.class);
                                i.putExtra("currentUser",currentUser);
                                context.startActivity(i);
                                Helper.shared().go((Activity)context);
                            }
                        }else{
                            if (!istanceOfOtherUserProfile){
                                UserService.shared().getOtherUser((Activity) context, post.get(i).getSenderUid(), new OtherUserService() {
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
                        if (post.get(i).getSenderUid().equals(currentUser.getUid())){
                            if (!istanceOfCurrentUserProfile){
                                Intent i = new Intent(context , CurrentUserProfile.class);
                                i.putExtra("currentUser",currentUser);
                                context.startActivity(i);
                                Helper.shared().go((Activity)context);
                            }
                        }else{
                            if (!istanceOfOtherUserProfile){
                                UserService.shared().getOtherUser((Activity) context, post.get(i).getSenderUid(), new OtherUserService() {
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
            case VIEW_TYPE_ADS:
                UnifiedNativeAd nativeAd = (UnifiedNativeAd) post.get(i).getNativeAd();
                populateNativeAdView(nativeAd, ((UnifiedNativeAdViewHolder) holder).getAdView());
                break;
            case VIEW_TYPE_LESSON_POST:
                MajorPostViewHolder itemHolder = (MajorPostViewHolder) holder;
                LessonPostModel menuItem = post.get(i);

                itemHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context , CommentActivity.class);
                    intent.putExtra("post",post.get(i));
                    intent.putExtra("currentUser",currentUser);
                    context.startActivity(intent);
                    Helper.shared().go((Activity) context);
                }
            });
                itemHolder.profileImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (post.get(i).getSenderUid().equals(currentUser.getUid())){
                            if (!istanceOfCurrentUserProfile){
                                Intent i = new Intent(context , CurrentUserProfile.class);
                                i.putExtra("currentUser",currentUser);
                                context.startActivity(i);
                                Helper.shared().go((Activity)context);
                            }
                        }else{
                            if (!istanceOfOtherUserProfile){
                                UserService.shared().getOtherUser((Activity) context, post.get(i).getSenderUid(), new OtherUserService() {
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
                itemHolder.name.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (post.get(i).getSenderUid().equals(currentUser.getUid())){
                            if (!istanceOfCurrentUserProfile){
                                Intent i = new Intent(context , CurrentUserProfile.class);
                                i.putExtra("currentUser",currentUser);
                                context.startActivity(i);
                                Helper.shared().go((Activity)context);
                            }
                        }else{
                            if (!istanceOfOtherUserProfile){
                                UserService.shared().getOtherUser((Activity) context, post.get(i).getSenderUid(), new OtherUserService() {
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
                itemHolder.more.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {


                        if (post.get(i).getSenderUid().equals(currentUser.getUid())){
                            Helper.shared().MajorPostCurrentUserBottomSheetLaunher((Activity) context, post, currentUser, post.get(i), new TrueFalse<Boolean>() {
                                @Override
                                public void callBack(Boolean _value) {
                                    if (_value){
                                        notifyDataSetChanged();
                                    }
                                }
                            });
                        }else{
                            UserService.shared().getOtherUser((Activity) context, post.get(i).getSenderUid(), new OtherUserService() {
                                @Override
                                public void callback(OtherUser user) {
                                    Helper.shared().MajorPostOtherUserBottomSheetLaunher((Activity) context, post, user, currentUser, post.get(i), new TrueFalse<Boolean>() {
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
                itemHolder.text.setOnHyperlinkClickListener(new SocialView.OnClickListener() {
                @Override
                public void onClick(@NonNull SocialView view, @NonNull CharSequence text) {
                    String url = text.toString();
                    if (!url.startsWith("http://") && !url.startsWith("https://"))
                        url = "http://" + url;
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    context.startActivity(browserIntent);
                }
            });
                itemHolder.setCommentLbl(menuItem.getComment());
                itemHolder.setName(menuItem.getSenderName());
                itemHolder.setUserName(menuItem.getUsername());
                itemHolder.setProfileImage(menuItem.getThumb_image());
                itemHolder.setLessonName(menuItem.getLessonName());
                itemHolder.setText(menuItem.getText());
                itemHolder.setDislikeLbl(menuItem.getDislike());
                itemHolder.setLikeLbl(menuItem.getLikes());
                itemHolder.setLike(menuItem.getLikes(),currentUser , context);
                itemHolder.setDislike(menuItem.getDislike(),currentUser , context);
                itemHolder.setFav(menuItem.getFavori(),currentUser,context);
                itemHolder.setTime(menuItem.getPostTime());
                itemHolder.setLinkButton(menuItem.getLink());

                itemHolder.text.setOnMentionClickListener(new SocialView.OnClickListener() {
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

                itemHolder.linkButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(menuItem.getLink()));
                    context.startActivity(browserIntent);
                }
            });
                itemHolder.itemView.findViewById(R.id.like).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        MajorPostService.shared().setLike(currentUser, menuItem, new TrueFalse<Boolean>() {
                            @Override
                            public void callBack(Boolean _value) {
                                if (_value){
                                    itemHolder.setLike(menuItem.getLikes(),currentUser,context);
                                    notifyDataSetChanged();
                                }
                            }
                        });
                    }
                });

                itemHolder.itemView.findViewById(R.id.dislike).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                    MajorPostService.shared().setDislike(currentUser, menuItem, new TrueFalse<Boolean>() {
                        @Override
                        public void callBack(Boolean _value) {
                            if (_value)
                            {
                                itemHolder.setDislike(menuItem.getDislike(),currentUser,context);
                                notifyDataSetChanged();
                            }
                        }
                    });
                    }
                });

                itemHolder.itemView.findViewById(R.id.fav).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        MajorPostService.shared().setFav(currentUser, menuItem, new TrueFalse<Boolean>() {
                            @Override
                            public void callBack(Boolean _value) {
                                if (_value){
                                    itemHolder.setFav(menuItem.getFavori(),currentUser,context);
                                    notifyDataSetChanged();
                                }
                            }
                        });
                    }
                });
                break;


            case VIEW_TYPE_EMPTY:

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


}
