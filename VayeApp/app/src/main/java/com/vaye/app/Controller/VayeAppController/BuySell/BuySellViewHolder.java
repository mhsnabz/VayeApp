package com.vaye.app.Controller.VayeAppController.BuySell;

import android.content.Context;
import android.text.Html;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.ads.formats.UnifiedNativeAdView;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.GeoPoint;
import com.hendraanggrian.appcompat.widget.SocialTextView;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.vaye.app.Model.CurrentUser;
import com.vaye.app.R;
import com.vaye.app.Util.Helper;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class BuySellViewHolder extends RecyclerView.ViewHolder {

    public BuySellViewHolder(@NonNull View itemView) {
        super(itemView);
    }
    ImageButton locationButton = (ImageButton)itemView.findViewById(R.id.locationButton);
    CircleImageView profileImage = (CircleImageView)itemView.findViewById(R.id.profileImage);
    ProgressBar progressBar = (ProgressBar)itemView.findViewById(R.id.progress);
    TextView name = (TextView)itemView.findViewById(R.id.name);
    TextView userName = (TextView)itemView.findViewById(R.id.username);
    ImageButton more = (ImageButton)itemView.findViewById(R.id.setting);
    TextView lessonName = (TextView)itemView.findViewById(R.id.lessonName);
    public SocialTextView text  = (SocialTextView)itemView.findViewById(R.id.text);
    ImageButton like = (ImageButton)itemView.findViewById(R.id.like);
    TextView likeLbl = (TextView)itemView.findViewById(R.id.likeCount);

    ImageButton dislike = (ImageButton)itemView.findViewById(R.id.dislike);
    TextView dislikeLbl = (TextView)itemView.findViewById(R.id.dislikeCount);

   public ImageButton comment = (ImageButton)itemView.findViewById(R.id.comment);
    TextView commentLbl = (TextView)itemView.findViewById(R.id.commentCount);
    TextView value = (TextView)itemView.findViewById(R.id.value);

    TextView time = (TextView)itemView.findViewById(R.id.time);

    RelativeLayout one_image = (RelativeLayout)itemView.findViewById(R.id.one_image);
    RelativeLayout two_image = (RelativeLayout)itemView.findViewById(R.id.two_image);
    RelativeLayout there_image = (RelativeLayout)itemView.findViewById(R.id.there_image);
    RelativeLayout four_image = (RelativeLayout)itemView.findViewById(R.id.four_image);
    RelativeLayout transparentView = (RelativeLayout)itemView.findViewById(R.id.transparentView);
    TextView textImageCount = (TextView)itemView.findViewById(R.id.textImageCount);

    //TODO:-layer one images

    RoundedImageView one_image_1 = (RoundedImageView)itemView.findViewById(R.id.image_one_image1);
    ProgressBar one_progres_1 = (ProgressBar)itemView.findViewById(R.id.image_one_progres1);


    //TODO:-layer two images
    RoundedImageView two_image_1 = (RoundedImageView)itemView.findViewById(R.id.image_two_image1);
    RoundedImageView two_image_2 = (RoundedImageView)itemView.findViewById(R.id.image_two_image2);
    ProgressBar two_progres_1 = (ProgressBar)itemView.findViewById(R.id.image_two_progres1);
    ProgressBar two_progres_2 = (ProgressBar)itemView.findViewById(R.id.image_two_progres2);


    //TODO:-layer there images
    RoundedImageView there_image_1 = (RoundedImageView)itemView.findViewById(R.id.image_there_image1);
    RoundedImageView there_image_2 = (RoundedImageView)itemView.findViewById(R.id.image_there_image2);
    RoundedImageView there_image_3 = (RoundedImageView)itemView.findViewById(R.id.image_there_image3);
    ProgressBar there_progres_1 = (ProgressBar)itemView.findViewById(R.id.image_there_progres1);
    ProgressBar there_progres_2 = (ProgressBar)itemView.findViewById(R.id.image_there_progres2);
    ProgressBar there_progres_3 = (ProgressBar)itemView.findViewById(R.id.image_there_progres3);


    //TODO:-layer four images
    RoundedImageView four_image_1 = (RoundedImageView)itemView.findViewById(R.id.image_four_image1);
    RoundedImageView four_image_2 = (RoundedImageView)itemView.findViewById(R.id.image_four_image2);
    RoundedImageView four_image_3 = (RoundedImageView)itemView.findViewById(R.id.image_four_image3);
    RoundedImageView four_image_4 = (RoundedImageView)itemView.findViewById(R.id.image_four_image4);
    ProgressBar four_progres_1 = (ProgressBar)itemView.findViewById(R.id.image_four_progres1);
    ProgressBar four_progres_2 = (ProgressBar)itemView.findViewById(R.id.image_four_progres2);
    ProgressBar four_progres_3 = (ProgressBar)itemView.findViewById(R.id.image_four_progres3);

    //TODO-native ads layer

    UnifiedNativeAdView adView = (UnifiedNativeAdView) itemView.findViewById(R.id.native_ads);






    public void setProfileImage(String url){
        if (!url.isEmpty()){
            Picasso.get().load(url)
                    .centerCrop()
                    .resize(256,256).placeholder(android.R.color.darker_gray)
                    .into(profileImage, new Callback() {
                        @Override
                        public void onSuccess() {
                            progressBar.setVisibility(View.GONE);
                        }

                        @Override
                        public void onError(Exception e) {

                        }
                    });
        }else{
            profileImage.setImageResource(android.R.color.darker_gray);
            progressBar.setVisibility(View.GONE);
        }
    }

    public void setLessonName(String _lessonName){
        lessonName.setText(_lessonName);
    }
    public void setUserName(String _username){
        userName.setText(_username);
    }
    public void setName(String _name){
        name.setText(_name);
    }
    public void setText(String  _text){
        text.setText(_text);
    }

    public void setLikeLbl(ArrayList<String> likes){
        if (likes.isEmpty()){
            likeLbl.setText("0");
        }else{
            likeLbl.setText(String.valueOf(likes.size()));
        }
    }

    public void setDislikeLbl(ArrayList<String> disLikes){
        if (disLikes.isEmpty()){
            dislikeLbl.setText("0");
        }else{
            dislikeLbl.setText(String.valueOf(disLikes.size()));
        }
    }

    public void setCommentLbl(int size){
        commentLbl.setText(String.valueOf(size));
    }

    public void setDislike(ArrayList<String> dislikes , CurrentUser user , Context context){
        if (dislikes.contains(user.getUid())){
            like.setImageDrawable( ContextCompat.getDrawable(context, R.drawable.like_unselected_1));
            dislike.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.dislike_selected_2));
        }else{
            dislike.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.dislike_unselected));

        }
    }
    public void setLike(ArrayList<String> likes , CurrentUser user , Context context){
        if (likes.contains(user.getUid())){
            like.setImageDrawable( ContextCompat.getDrawable(context, R.drawable.like_1));
            dislike.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.dislike_unselected));
        }else{
            like.setImageDrawable( ContextCompat.getDrawable(context, R.drawable.like_unselected_1));
        }
    }

    public void setTime(Timestamp _time){
        time.setText(Html.fromHtml("&#8226;")+ Helper.shared().setTimeAgo(_time));
    }
    public void setLocationButton(GeoPoint location){
        if (location!=null){
            locationButton.setVisibility(View.VISIBLE);
        }else{
            locationButton.setVisibility(View.GONE);
        }
    }
    public void setImages(ArrayList<String> thumbImages )
    {
        if (thumbImages.size() == 1){
            one_image.setVisibility(View.VISIBLE);
            two_image.setVisibility(View.GONE);
            there_image.setVisibility(View.GONE);
            four_image.setVisibility(View.GONE);


            Picasso.get().load(thumbImages.get(0)).centerCrop().placeholder(android.R.color.darker_gray).resize(256,256).into(one_image_1, new Callback() {
                @Override
                public void onSuccess() {
                    one_progres_1.setVisibility(View.GONE);
                }

                @Override
                public void onError(Exception e) {

                }
            });



        }else if (thumbImages.size() == 2){
            one_image.setVisibility(View.GONE);
            two_image.setVisibility(View.VISIBLE);
            there_image.setVisibility(View.GONE);
            four_image.setVisibility(View.GONE);

            Picasso.get().load(thumbImages.get(0)).centerCrop().placeholder(android.R.color.darker_gray).resize(256,256).into(two_image_1, new Callback() {
                @Override
                public void onSuccess() {
                    two_progres_1.setVisibility(View.GONE);
                }

                @Override
                public void onError(Exception e) {

                }
            });
            Picasso.get().load(thumbImages.get(1)).centerCrop().placeholder(android.R.color.darker_gray).resize(256,256).into(two_image_2, new Callback() {
                @Override
                public void onSuccess() {
                    two_progres_2.setVisibility(View.GONE);
                }

                @Override
                public void onError(Exception e) {

                }
            });


        }else if (thumbImages.size() == 3){
            one_image.setVisibility(View.GONE);
            there_image.setVisibility(View.VISIBLE);
            two_image.setVisibility(View.GONE);
            four_image.setVisibility(View.GONE);
            Picasso.get().load(thumbImages.get(0)).centerCrop().placeholder(android.R.color.darker_gray).resize(256,256).into(there_image_1, new Callback() {
                @Override
                public void onSuccess() {
                    there_progres_1.setVisibility(View.GONE);
                }

                @Override
                public void onError(Exception e) {

                }
            });
            Picasso.get().load(thumbImages.get(1)).centerCrop().placeholder(android.R.color.darker_gray).resize(256,256).into(there_image_2, new Callback() {
                @Override
                public void onSuccess() {
                    there_progres_2.setVisibility(View.GONE);
                }

                @Override
                public void onError(Exception e) {

                }
            });
            Picasso.get().load(thumbImages.get(2)).centerCrop().placeholder(android.R.color.darker_gray).resize(256,256).into(there_image_3, new Callback() {
                @Override
                public void onSuccess() {
                    there_progres_3.setVisibility(View.GONE);
                }

                @Override
                public void onError(Exception e) {

                }
            });
        }else if (thumbImages.size() > 3){
            one_image.setVisibility(View.GONE);
            there_image.setVisibility(View.GONE);
            there_image.setVisibility(View.GONE);
            four_image.setVisibility(View.VISIBLE);
            if (thumbImages.size() == 4){
                textImageCount.setVisibility(View.GONE);
                transparentView.setVisibility(View.GONE);
            }else{
                textImageCount.setText( "+"+String.valueOf((thumbImages.size() + 1)-4));
            }
            Picasso.get().load(thumbImages.get(0)).centerCrop().placeholder(android.R.color.darker_gray).resize(256,256).into(four_image_1, new Callback() {
                @Override
                public void onSuccess() {
                    four_progres_1.setVisibility(View.GONE);
                }

                @Override
                public void onError(Exception e) {

                }
            });
            Picasso.get().load(thumbImages.get(1)).centerCrop().placeholder(android.R.color.darker_gray).resize(256,256).into(four_image_2, new Callback() {
                @Override
                public void onSuccess() {
                    four_progres_2.setVisibility(View.GONE);
                }

                @Override
                public void onError(Exception e) {

                }
            });
            Picasso.get().load(thumbImages.get(2)).centerCrop().placeholder(android.R.color.darker_gray).resize(256,256).into(four_image_3, new Callback() {
                @Override
                public void onSuccess() {
                    four_progres_3.setVisibility(View.GONE);
                }

                @Override
                public void onError(Exception e) {

                }
            });
            Picasso.get().load(thumbImages.get(3)).centerCrop().placeholder(android.R.color.darker_gray).resize(256,256).into(four_image_4, new Callback() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onError(Exception e) {

                }
            });
        }


    }
    public void setValue(String _value){

        if (_value != null && !_value.isEmpty()){
            value.setText(_value + " ₺");
        }else{
            value.setText("Fiyat Belirtilmemiş");
        }

    }

}
