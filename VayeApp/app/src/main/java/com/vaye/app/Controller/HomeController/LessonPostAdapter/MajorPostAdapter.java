package com.vaye.app.Controller.HomeController.LessonPostAdapter;

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
import com.google.android.gms.common.SignInButton;
import com.vaye.app.Controller.HomeController.PagerAdapter.AllDatasActivity;
import com.vaye.app.Interfaces.TrueFalse;
import com.vaye.app.Model.CurrentUser;
import com.vaye.app.Model.LessonPostModel;
import com.vaye.app.R;
import com.vaye.app.Services.MajorPostService;
import com.vaye.app.Util.BottomSheetHelper.BottomSheetActionTarget;
import com.vaye.app.Util.BottomSheetHelper.BottomSheetModel;
import com.vaye.app.Util.BottomSheetHelper.BottomSheetTarget;
import com.vaye.app.Util.Helper;

import java.util.ArrayList;

public class MajorPostAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public MajorPostAdapter(ArrayList<LessonPostModel> post, CurrentUser currentUser, Context context) {
        this.post = post;
        this.currentUser = currentUser;
        this.context = context;
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



                postHolder.more.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (post.get(i).getSenderUid().equals(currentUser.getUid())){
                            ArrayList<String > items = new ArrayList<>();
                            items.add(BottomSheetActionTarget.gonderiyi_düzenle);
                            items.add(BottomSheetActionTarget.gonderiyi_sil);
                            ArrayList<Integer> res = new ArrayList<>();
                            res.add(R.drawable.edit);
                            res.add(R.drawable.trash);

                            MajorPostService.shared().check_currentUser_post_is_slient(currentUser, post.get(i), new TrueFalse<Boolean>() {
                                @Override
                                public void callBack(Boolean _value) {
                                    if (_value){
                                        items.add(BottomSheetActionTarget.gonderi_bildirimlerini_ac);
                                        res.add(R.drawable.slient_selected);
                                    }else {
                                        items.add(BottomSheetActionTarget.gonderiyi_sessize_al);
                                        res.add(R.drawable.slient);
                                    }
                                }
                            });
                            BottomSheetModel model = new BottomSheetModel(items,BottomSheetTarget.lesson_currentUser_target,res);

                            Helper.shared().BottomSheet_LessonCurrenUser_Dialog((Activity) context,post, BottomSheetTarget.lesson_currentUser_target, currentUser, model, post.get(i), new TrueFalse<Boolean>() {
                                @Override
                                public void callBack(Boolean _value) {
                                    if (_value){
                                        notifyDataSetChanged();
                                    }
                                }
                            });
                        }else {
                            ArrayList<String > items = new ArrayList<>();
                            items.add(BottomSheetActionTarget.bu_gonderiyi_sikayet_et);
                            items.add(BottomSheetActionTarget.bu_dersi_sessize_al);
                            items.add(BottomSheetActionTarget.bu_kullaniciyi_sessize_al);
                            items.add(BottomSheetActionTarget.bu_dersi_takip_etmeyi_birak);
                            items.add(BottomSheetActionTarget.bu_kullaniciyi_sikayet_et);
                            ArrayList<Integer> res = new ArrayList<>();
                            res.add(R.drawable.slient);
                            res.add(R.drawable.trash);
                            res.add(R.drawable.trash);
                            res.add(R.drawable.trash);
                            res.add(R.drawable.trash);
                            res.add(R.drawable.trash);
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
                break;
            case VIEW_TYPE_ADS:
                UnifiedNativeAd nativeAd = (UnifiedNativeAd) post.get(i).getNativeAd();
                populateNativeAdView(nativeAd, ((UnifiedNativeAdViewHolder) holder).getAdView());
                break;
            case VIEW_TYPE_LESSON_POST:
                MajorPostViewHolder itemHolder = (MajorPostViewHolder) holder;
                LessonPostModel menuItem = post.get(i);


                itemHolder.more.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {


                        if (post.get(i).getSenderUid().equals(currentUser.getUid())){
                            ArrayList<String > items = new ArrayList<>();
                            items.add(BottomSheetActionTarget.gonderiyi_düzenle);
                            items.add(BottomSheetActionTarget.gonderiyi_sil);
                            ArrayList<Integer> res = new ArrayList<>();
                            res.add(R.drawable.edit);
                            res.add(R.drawable.trash);

                            MajorPostService.shared().check_currentUser_post_is_slient(currentUser, post.get(i), new TrueFalse<Boolean>() {
                                @Override
                                public void callBack(Boolean _value) {
                                    if (_value){
                                        items.add(BottomSheetActionTarget.gonderi_bildirimlerini_ac);
                                        res.add(R.drawable.slient_selected);
                                    }else {
                                        items.add(BottomSheetActionTarget.gonderiyi_sessize_al);
                                        res.add(R.drawable.slient);
                                    }
                                }
                            });

                            BottomSheetModel model = new BottomSheetModel(items,BottomSheetTarget.lesson_currentUser_target,res);

                            Helper.shared().BottomSheet_LessonCurrenUser_Dialog((Activity) context,post, BottomSheetTarget.lesson_currentUser_target, currentUser, model, post.get(i), new TrueFalse<Boolean>() {
                                @Override
                                public void callBack(Boolean _value) {
                                    if (_value){
                                        notifyDataSetChanged();
                                    }
                                }
                            });
                        }else{

                        }



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
