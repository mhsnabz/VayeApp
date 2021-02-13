package com.vaye.app.Interfaces;

public interface Notifications {

    interface NotificationDescription{
        String  like_home = "Gönderini Beğendi";
        String  comment_home = "Gönderinize Yorum Yaptı";
        String  reply_comment = "Yorumunuza Cevap Verdi";
        String  comment_like = "Yorumunuzu Beğendi";
        String  comment_mention = "Bir Yorumda Sizden Bahsetti";
        String  following_you = "Sizi Takip Etmeye Başladı";
        String  home_new_post = "Yeni Bir Gönderi Paylaştı";
        String  new_ad = "Yeni Bir İlan Paylaştı";
        String  like_sell_buy = "Paylaştığın İlanı Beğendi";
        String  new_food_me = "Yeni Bir Yemek İlanı Paylaştı";
        String  like_food_me = "Paylaştığın Gönderiyi Beğendi";
        String  new_camping = "Yeni Bir Kamp Gönderisi Paylaştı";
        String  like_camping = "Gönderini Beğendi";
        String  notices_comment_like = "Yorumunuzu Beğendi";
        String  notices_post_like = "Gönderinizi Beğendi";
        String  notices_new_comment = "Gönderinize Yorum Yaptı";
        String  notices_replied_comment_like = "Yorumunuzu Beğendi";
        String  notice_mention_comment = "Bir Yorumda Sizden Bahsetti";
        String home_new_mentions_post = "Bir Gönderide Sizden Bahsetti";
    }
    interface  NotificationType{
        String  like_home = "like";
        String  comment_home = "comment_home";
        String  reply_comment = "reply_comment";
        String  comment_like = "comment_like";
        String  comment_mention = "comment_mention";
        String  following_you = "following_you";
        String  home_new_post = "home_new_post";
        String home_new_mentions_post = "home_new_mentions_post";
        String  new_ad = "new_ad";
        String  like_sell_buy = "like_sell_buy";
        String  new_food_me = "new_food_me";
        String  like_food_me = "like_food_me";
        String  new_camping = "new_camping";
        String  like_camping = "like_camping";
        String  notices_comment_like = "notices_comment_like";
        String  notices_post_like = "notices_post_like";
        String  notices_new_comment = "notices_new_comment";
        String  notices_replied_comment_like = "notices_replied_comment_like";
        String  notice_mention_comment = "notice_mention_comment";
    }

    interface LocalNotifications{
        String like = "like";
        String follow = "follow";
        String mention = "mention";
        String lessonNotices = "lessonNotices";
        String comment = "comment";
    }



}
