package com.vaye.app.Controller.VayeAppController.VayeAppAdapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.ads.formats.NativeAd;
import com.google.android.gms.ads.formats.UnifiedNativeAd;
import com.google.android.gms.ads.formats.UnifiedNativeAdView;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.kongzue.dialog.v3.WaitDialog;
import com.vaye.app.Controller.HomeController.LessonPostAdapter.MajorPostViewHolder;
import com.vaye.app.Controller.HomeController.LessonPostAdapter.UnifiedNativeAdViewHolder;
import com.vaye.app.Controller.HomeController.PagerAdapter.AllDatasActivity;
import com.vaye.app.Controller.HomeController.SinglePost.CommentActivity;
import com.vaye.app.Controller.VayeAppController.FoodMe.FoodMeViewHolder;
import com.vaye.app.Interfaces.OtherUserService;
import com.vaye.app.Interfaces.TrueFalse;
import com.vaye.app.Model.CurrentUser;
import com.vaye.app.Model.LessonPostModel;
import com.vaye.app.Model.MainPostModel;
import com.vaye.app.Model.OtherUser;
import com.vaye.app.R;
import com.vaye.app.Services.FoodMeService;
import com.vaye.app.Services.MajorPostService;
import com.vaye.app.Services.UserService;
import com.vaye.app.Util.BottomSheetHelper.BottomSheetActionTarget;
import com.vaye.app.Util.BottomSheetHelper.BottomSheetModel;
import com.vaye.app.Util.BottomSheetHelper.BottomSheetTarget;
import com.vaye.app.Util.Helper;

import java.util.ArrayList;

public class FoodMeAdapter  extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    ArrayList<MainPostModel> post;
    Context context;
    CurrentUser currentUser;
    private static final int VIEW_TYPE_FOODME_POST = 1;
    private static final int VIEW_TYPE_FOODME_POST_DATA  = 2;
    private static final int VIEW_TYPE_ADS  = 3;
    private static final int VIEW_TYPE_EMPTY  = 4;

    public FoodMeAdapter(ArrayList<MainPostModel> post, Context context, CurrentUser currentUser) {
        this.post = post;
        this.context = context;
        this.currentUser = currentUser;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if (viewType == VIEW_TYPE_FOODME_POST){
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.food_me_cell, parent, false);

            return new FoodMeViewHolder(itemView);
        }
        else if (viewType == VIEW_TYPE_FOODME_POST_DATA){
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.food_me_data_cell, parent, false);

            return new FoodMeViewHolder(itemView);

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
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int i) {
        int viewType = getItemViewType(i);
        switch (viewType) {
            case VIEW_TYPE_ADS:
                UnifiedNativeAd nativeAd = (UnifiedNativeAd) post.get(i).getNativeAd();
                populateNativeAdView(nativeAd, ((UnifiedNativeAdViewHolder) holder).getAdView());
                break;

            case VIEW_TYPE_EMPTY:
                break;
            case VIEW_TYPE_FOODME_POST:
                FoodMeViewHolder itemHolder = (FoodMeViewHolder) holder;
                MainPostModel menuItem = post.get(i);

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

                itemHolder.setCommentLbl(menuItem.getComment());
                itemHolder.setName(menuItem.getSenderName());
                itemHolder.setUserName(menuItem.getUsername());
                itemHolder.setProfileImage(menuItem.getThumb_image());

                itemHolder.setText(menuItem.getText());
                itemHolder.setDislikeLbl(menuItem.getDislike());
                itemHolder.setLikeLbl(menuItem.getLikes());
                itemHolder.setLike(menuItem.getLikes(),currentUser , context);
                itemHolder.setDislike(menuItem.getDislike(),currentUser , context);

                itemHolder.setTime(menuItem.getPostTime());
               itemHolder.setLocationButton(menuItem.getGeoPoint());
                itemHolder.itemView.findViewById(R.id.locationButton).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                    }
                });
                itemHolder.itemView.findViewById(R.id.like).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                       FoodMeService.shared().setLike(menuItem, currentUser, new TrueFalse<Boolean>() {
                           @Override
                           public void callBack(Boolean _value) {
                               itemHolder.setLike(menuItem.getLikes(),currentUser ,context);
                               notifyDataSetChanged();
                           }
                       });
                    }
                });

                itemHolder.itemView.findViewById(R.id.dislike).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                    FoodMeService.shared().setDislike(currentUser, menuItem, new TrueFalse<Boolean>() {
                        @Override
                        public void callBack(Boolean _value) {
                            itemHolder.setDislike(menuItem.getDislike(),currentUser,context);
                            notifyDataSetChanged();
                        }
                    });
                    }
                });
            itemHolder.itemView.findViewById(R.id.locationButton).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });
                break;

            case VIEW_TYPE_FOODME_POST_DATA:
                FoodMeViewHolder postHolder = (FoodMeViewHolder) holder;
                postHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                    }
                });




                MainPostModel menuItemData = post.get(i);

                postHolder.setCommentLbl(menuItemData.getComment());

                postHolder.setImages(menuItemData.getThumbData());
                postHolder.setName(menuItemData.getSenderName());
                postHolder.setUserName(menuItemData.getUsername());
                postHolder.setProfileImage(menuItemData.getThumb_image());

                postHolder.setText(menuItemData.getText());
                postHolder.setDislikeLbl(menuItemData.getDislike());
                postHolder.setLikeLbl(menuItemData.getLikes());
                postHolder.setLike(menuItemData.getLikes(),currentUser , context);
                postHolder.setDislike(menuItemData.getDislike(),currentUser , context);

                postHolder.setTime(menuItemData.getPostTime());
                postHolder.setLocationButton(menuItemData.getGeoPoint());
                postHolder.itemView.findViewById(R.id.locationButton).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                    }
                });
                postHolder.itemView.findViewById(R.id.like).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        FoodMeService.shared().setLike(menuItemData, currentUser, new TrueFalse<Boolean>() {
                            @Override
                            public void callBack(Boolean _value) {
                                postHolder.setLike(menuItemData.getLikes(),currentUser,context);
                                notifyDataSetChanged();
                            }
                        });
                    }
                });

                postHolder.itemView.findViewById(R.id.dislike).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        FoodMeService.shared().setDislike(currentUser, menuItemData, new TrueFalse<Boolean>() {
                            @Override
                            public void callBack(Boolean _value) {
                                postHolder.setDislike(menuItemData.getDislike(),currentUser,context);
                                notifyDataSetChanged();
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
        if (model.getType()!=null&& model.getType().equals("ads")) {
            return  VIEW_TYPE_ADS;
        }else if ( model.getType().equals("data")){
            return  VIEW_TYPE_FOODME_POST_DATA;
        }else if  ( model.getType().equals("post")){
            return   VIEW_TYPE_FOODME_POST;
        }else if (model.getEmpty().equals("empty")){
            return  VIEW_TYPE_EMPTY;
        }
        return super.getItemViewType(position);
    }
}
