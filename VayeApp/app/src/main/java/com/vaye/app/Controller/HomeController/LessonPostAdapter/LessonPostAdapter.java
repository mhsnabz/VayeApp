package com.vaye.app.Controller.HomeController.LessonPostAdapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.ads.nativetemplates.NativeTemplateStyle;
import com.google.android.ads.nativetemplates.TemplateView;
import com.google.android.gms.ads.formats.UnifiedNativeAd;
import com.google.firebase.Timestamp;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.vaye.app.Interfaces.TrueFalse;
import com.vaye.app.Model.CurrentUser;
import com.vaye.app.Model.LessonPostModel;
import com.vaye.app.R;
import com.vaye.app.Services.MajorPostService;
import com.vaye.app.Util.Helper;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class LessonPostAdapter extends RecyclerView.Adapter<LessonPostViewHolder> {
    ArrayList<LessonPostModel> post;
    CurrentUser currentUser;
    Context context;
    private static final int VIEW_TYPE_LESSON_POST = 1;
    private static final int VIEW_TYPE_LESSON_POST_DATA  = 2;
    private static final int VIEW_TYPE_ADS  = 3;
    private static final int VIEW_TYPE_EMPTY  = 4;
    public LessonPostAdapter(ArrayList<LessonPostModel> post, CurrentUser currentUser, Context context) {
        this.post = post;
        this.currentUser = currentUser;
        this.context = context;
    }

    @NonNull
    @Override
    public LessonPostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if (viewType == VIEW_TYPE_LESSON_POST){
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.single_lesson_post_item, parent, false);

            return new LessonPostViewHolder(itemView);
        }
        else if (viewType == VIEW_TYPE_LESSON_POST_DATA){
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.single_lesson_post_data_item, parent, false);

            return new LessonPostViewHolder(itemView);

        }else if (viewType == VIEW_TYPE_ADS){
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.navite_ads_layout, parent, false);

            return new LessonPostViewHolder(itemView);
        }
        else {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.empty_lesson_post, parent, false);

            return new LessonPostViewHolder(itemView);

        }


    }

    @Override
    public int getItemViewType(int position) {
        LessonPostModel model = (LessonPostModel)post.get(position);
        if (model.getType().equals("ads")) {
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
    public void onBindViewHolder(@NonNull LessonPostViewHolder holder, int i) {


        if (getItemViewType(i) == VIEW_TYPE_LESSON_POST_DATA){

            holder.setCommentLbl(post.get(i).getComment());
            holder.setName(post.get(i).getSenderName());
            holder.setUserName(post.get(i).getUsername());
            holder.setProfileImage(post.get(i).getThumb_image());
            holder.setLessonName(post.get(i).getLessonName());
            holder.setText(post.get(i).getText());
            holder.setDislikeLbl(post.get(i).getDislike());
            holder.setLikeLbl(post.get(i).getLikes());
            holder.setLike(post.get(i).getLikes(),currentUser , context);
            holder.setDislike(post.get(i).getDislike(),currentUser , context);
            holder.setFav(post.get(i).getFavori(),currentUser,context);
            holder.setTime(post.get(i).getPostTime());
            holder.itemView.findViewById(R.id.fav).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    MajorPostService.shared().setFav(currentUser, post.get(i), new TrueFalse<Boolean>() {
                        @Override
                        public void callBack(Boolean _value) {
                            if (_value){
                                holder.setFav(post.get(i).getFavori() , currentUser , context);
                                notifyDataSetChanged();
                            }
                        }
                    });
                }
            });

            holder.itemView.findViewById(R.id.like).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    MajorPostService.shared().setLike(currentUser, post.get(i), new TrueFalse<Boolean>() {
                        @Override
                        public void callBack(Boolean _value) {
                            if (_value){
                                holder.setLike(post.get(i).getLikes(),currentUser,context);
                                notifyDataSetChanged();
                            }
                        }
                    });
                }
            });

            holder.itemView.findViewById(R.id.dislike).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    MajorPostService.shared().setDislike(currentUser, post.get(i), new TrueFalse<Boolean>() {
                        @Override
                        public void callBack(Boolean _value) {
                            if (_value){
                                holder.setDislike(post.get(i).getDislike() , currentUser , context);
                                notifyDataSetChanged();
                            }
                        }
                    });
                }
            });

            holder.setImages(post.get(i).getThumbData());

           holder.itemView.findViewById(R.id.one_image).setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View view) {

               }
           });
            holder.itemView.findViewById(R.id.there_image).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(context,"3lü resim tıklandı",Toast.LENGTH_SHORT).show();
                }
            });

        }else if (getItemViewType(i) == VIEW_TYPE_LESSON_POST){
            holder.setCommentLbl(post.get(i).getComment());
            holder.setName(post.get(i).getSenderName());
            holder.setUserName(post.get(i).getUsername());
            holder.setProfileImage(post.get(i).getThumb_image());
            holder.setLessonName(post.get(i).getLessonName());
            holder.setText(post.get(i).getText());
            holder.setDislikeLbl(post.get(i).getDislike());
            holder.setLikeLbl(post.get(i).getLikes());
            holder.setLike(post.get(i).getLikes(),currentUser , context);
            holder.setDislike(post.get(i).getDislike(),currentUser , context);
            holder.setFav(post.get(i).getFavori(),currentUser,context);
            holder.setTime(post.get(i).getPostTime());
            holder.itemView.findViewById(R.id.fav).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    MajorPostService.shared().setFav(currentUser, post.get(i), new TrueFalse<Boolean>() {
                        @Override
                        public void callBack(Boolean _value) {
                            if (_value){
                                holder.setFav(post.get(i).getFavori() , currentUser , context);
                                notifyDataSetChanged();
                            }
                        }
                    });
                }
            });

            holder.itemView.findViewById(R.id.like).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    MajorPostService.shared().setLike(currentUser, post.get(i), new TrueFalse<Boolean>() {
                        @Override
                        public void callBack(Boolean _value) {
                            if (_value){
                                holder.setLike(post.get(i).getLikes(),currentUser,context);
                                notifyDataSetChanged();
                            }
                        }
                    });
                }
            });

            holder.itemView.findViewById(R.id.dislike).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    MajorPostService.shared().setDislike(currentUser, post.get(i), new TrueFalse<Boolean>() {
                        @Override
                        public void callBack(Boolean _value) {
                            if (_value){
                                holder.setDislike(post.get(i).getDislike() , currentUser , context);
                                notifyDataSetChanged();
                            }
                        }
                    });
                }
            });

        }else if (getItemViewType(i) == VIEW_TYPE_ADS){
            holder.setNative_ads(post.get(i).getNativeAd());
        }else {
            return;
        }



    }

    @Override
    public int getItemCount() {
        return post.size();
    }
}
 class LessonPostViewHolder extends RecyclerView.ViewHolder{

    public LessonPostViewHolder(@NonNull View itemView) {
        super(itemView);


    }

    CircleImageView profileImage = (CircleImageView)itemView.findViewById(R.id.profileImage);
    ProgressBar progressBar = (ProgressBar)itemView.findViewById(R.id.progress);
    TextView name = (TextView)itemView.findViewById(R.id.name);
    TextView userName = (TextView)itemView.findViewById(R.id.username);
    ImageButton more = (ImageButton)itemView.findViewById(R.id.setting);
    TextView lessonName = (TextView)itemView.findViewById(R.id.lessonName);
    TextView text  = (TextView)itemView.findViewById(R.id.text);
    ImageButton like = (ImageButton)itemView.findViewById(R.id.like);
    TextView likeLbl = (TextView)itemView.findViewById(R.id.likeCount);

    ImageButton dislike = (ImageButton)itemView.findViewById(R.id.dislike);
    TextView dislikeLbl = (TextView)itemView.findViewById(R.id.dislikeCount);

    ImageButton comment = (ImageButton)itemView.findViewById(R.id.comment);
    TextView commentLbl = (TextView)itemView.findViewById(R.id.commentCount);
    ImageButton fav = (ImageButton)itemView.findViewById(R.id.fav);

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
     TemplateView native_ads = (TemplateView)itemView.findViewById(R.id.native_ads);






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
        time.setText(Html.fromHtml("&#8226;")+Helper.shared().setTimeAgo(_time));
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


    public void setNative_ads(UnifiedNativeAd ads){
        NativeTemplateStyle style = new NativeTemplateStyle.Builder()
                .withMainBackgroundColor(new ColorDrawable(Color.parseColor("#ffffff"))).build();
        native_ads.setStyles(style);
        native_ads.setNativeAd(ads);
    }


}