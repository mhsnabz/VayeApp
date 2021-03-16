package com.vaye.app.Controller.HomeController.LessonPostAdapter;

import android.content.Context;
import android.text.Html;
import android.util.Log;
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
import com.hendraanggrian.appcompat.widget.SocialTextView;
import com.kongzue.dialog.v3.WaitDialog;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.vaye.app.Interfaces.DriveLinkNames;
import com.vaye.app.Model.CurrentUser;
import com.vaye.app.R;
import com.vaye.app.Services.MajorPostService;
import com.vaye.app.Util.Helper;

import java.net.URISyntaxException;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class MajorPostViewHolder extends RecyclerView.ViewHolder{
    public MajorPostViewHolder(@NonNull View itemView) {
        super(itemView);
    }


   public ImageButton linkButton = (ImageButton)itemView.findViewById(R.id.linkButton);
    public CircleImageView profileImage = (CircleImageView)itemView.findViewById(R.id.profileImage);
    public ProgressBar progressBar = (ProgressBar)itemView.findViewById(R.id.progress);
    public TextView name = (TextView)itemView.findViewById(R.id.name);
    public TextView userName = (TextView)itemView.findViewById(R.id.username);
    public ImageButton more = (ImageButton)itemView.findViewById(R.id.setting);
    public TextView lessonName = (TextView)itemView.findViewById(R.id.lessonName);
    public SocialTextView text  = (SocialTextView)itemView.findViewById(R.id.text);
    public ImageButton like = (ImageButton)itemView.findViewById(R.id.like);
    public TextView likeLbl = (TextView)itemView.findViewById(R.id.likeCount);

    public ImageButton dislike = (ImageButton)itemView.findViewById(R.id.dislike);
    public TextView dislikeLbl = (TextView)itemView.findViewById(R.id.dislikeCount);

   public ImageButton comment = (ImageButton)itemView.findViewById(R.id.comment);
    public TextView commentLbl = (TextView)itemView.findViewById(R.id.commentCount);
    public ImageButton fav = (ImageButton)itemView.findViewById(R.id.fav);

    public TextView time = (TextView)itemView.findViewById(R.id.time);

    public RelativeLayout one_image = (RelativeLayout)itemView.findViewById(R.id.one_image);
    public RelativeLayout two_image = (RelativeLayout)itemView.findViewById(R.id.two_image);
    public RelativeLayout there_image = (RelativeLayout)itemView.findViewById(R.id.there_image);
    public RelativeLayout four_image = (RelativeLayout)itemView.findViewById(R.id.four_image);
    public RelativeLayout transparentView = (RelativeLayout)itemView.findViewById(R.id.transparentView);
    public TextView textImageCount = (TextView)itemView.findViewById(R.id.textImageCount);

    //TODO:-layer one images

    public RoundedImageView one_image_1 = (RoundedImageView)itemView.findViewById(R.id.image_one_image1);
    public ProgressBar one_progres_1 = (ProgressBar)itemView.findViewById(R.id.image_one_progres1);


    //TODO:-layer two images
    public RoundedImageView two_image_1 = (RoundedImageView)itemView.findViewById(R.id.image_two_image1);
    public RoundedImageView two_image_2 = (RoundedImageView)itemView.findViewById(R.id.image_two_image2);
    public ProgressBar two_progres_1 = (ProgressBar)itemView.findViewById(R.id.image_two_progres1);
    public ProgressBar two_progres_2 = (ProgressBar)itemView.findViewById(R.id.image_two_progres2);


    //TODO:-layer there images
    public  RoundedImageView there_image_1 = (RoundedImageView)itemView.findViewById(R.id.image_there_image1);
    public  RoundedImageView there_image_2 = (RoundedImageView)itemView.findViewById(R.id.image_there_image2);
    public RoundedImageView there_image_3 = (RoundedImageView)itemView.findViewById(R.id.image_there_image3);
    public ProgressBar there_progres_1 = (ProgressBar)itemView.findViewById(R.id.image_there_progres1);
    public ProgressBar there_progres_2 = (ProgressBar)itemView.findViewById(R.id.image_there_progres2);
    public ProgressBar there_progres_3 = (ProgressBar)itemView.findViewById(R.id.image_there_progres3);


    //TODO:-layer four images
    public RoundedImageView four_image_1 = (RoundedImageView)itemView.findViewById(R.id.image_four_image1);
    public RoundedImageView four_image_2 = (RoundedImageView)itemView.findViewById(R.id.image_four_image2);
    public RoundedImageView four_image_3 = (RoundedImageView)itemView.findViewById(R.id.image_four_image3);
    public RoundedImageView four_image_4 = (RoundedImageView)itemView.findViewById(R.id.image_four_image4);
    public ProgressBar four_progres_1 = (ProgressBar)itemView.findViewById(R.id.image_four_progres1);
    public ProgressBar four_progres_2 = (ProgressBar)itemView.findViewById(R.id.image_four_progres2);
    public ProgressBar four_progres_3 = (ProgressBar)itemView.findViewById(R.id.image_four_progres3);

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
            like.setImageDrawable( ContextCompat.getDrawable(context, R.drawable.like_unselected));
            dislike.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.dislike_selected));
        }else{
            dislike.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.dislike));

        }
    }
    public void setLike(ArrayList<String> likes , CurrentUser user , Context context){
        if (likes.contains(user.getUid())){
            like.setImageDrawable( ContextCompat.getDrawable(context, R.drawable.like));
            dislike.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.dislike));
        }else{
            like.setImageDrawable( ContextCompat.getDrawable(context, R.drawable.like_unselected));
        }
    }
    public void setFav(ArrayList<String> favs , CurrentUser user , Context context){
        if (favs.contains(user.getUid())){
            fav.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.star_selected));
        }else{
            fav.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.star));
        }
    }

    public void setTime(Timestamp _time){
        time.setText(Html.fromHtml("&#8226;")+ Helper.shared().setTimeAgo(_time));
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

    public void setLinkButton(String link){
        try {
            if(MajorPostService.shared().getLink(link) .equals("drive.google.com")
                    || MajorPostService.shared().getLink(link).equals("www.drive.google.com")){
                WaitDialog.dismiss();

                linkButton.setImageResource(R.drawable.google_drive);

            }else if ( MajorPostService.shared().getLink(link).equals("onedrive.live.com" )
                    || MajorPostService.shared().getLink(link).equals("www.onedrive.live.com")|| link.equals("1drv.ms")){

                linkButton.setImageResource(R.drawable.onedrive);


            }else if ( MajorPostService.shared().getLink(link).equals("dropbox.com")
                    ||  MajorPostService.shared().getLink(link).equals("www.dropbox.com")){

                linkButton.setImageResource(R.drawable.dropbox);


            }else if ( MajorPostService.shared().getLink(link).equals("icloud.com")
                    ||  MajorPostService.shared().getLink(link).equals("www.icloud.com")){

                linkButton.setImageResource(R.drawable.icloud);


            }else if ( MajorPostService.shared().getLink(link).equals("disk.yandex.com.tr")
                    ||  MajorPostService.shared().getLink(link).equals("disk.yandex.com") || MajorPostService.shared().getLink(link).equals("yadi.sk")){

                linkButton.setImageResource(R.drawable.yandex);


            }else if ( MajorPostService.shared().getLink(link).equals("mega.nz")
                    ||  MajorPostService.shared().getLink(link).equals("www.mega.nz")){

                linkButton.setImageResource(R.drawable.mega);

            }else{
                linkButton.setVisibility(View.GONE);

            }
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }
}
