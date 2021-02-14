package com.vaye.app.Controller.VayeAppController.VayeAppAdapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.ads.formats.UnifiedNativeAdView;
import com.vaye.app.Controller.HomeController.LessonPostAdapter.MajorPostViewHolder;
import com.vaye.app.Controller.HomeController.LessonPostAdapter.UnifiedNativeAdViewHolder;
import com.vaye.app.Controller.VayeAppController.BuySell.BuySellViewHolder;
import com.vaye.app.Controller.VayeAppController.FoodMe.FoodMeViewHolder;
import com.vaye.app.Model.CurrentUser;
import com.vaye.app.Model.LessonPostModel;
import com.vaye.app.Model.MainPostModel;
import com.vaye.app.R;

import java.util.ArrayList;

public class FollowersAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    ArrayList<MainPostModel> post;
    Context context;
    CurrentUser currentUser;

    public FollowersAdapter(ArrayList<MainPostModel> post, Context context, CurrentUser currentUser) {
        this.post = post;
        this.context = context;
        this.currentUser = currentUser;
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
                    .inflate(R.layout.food_me_data_cell, parent, false);

            return new FoodMeViewHolder(itemView);

        }else if (viewType == CAMPING_DATA_POST){
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.food_me_data_cell, parent, false);

            return new FoodMeViewHolder(itemView);

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
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
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
            }else{
                return CAMPING_DATA_POST;
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
