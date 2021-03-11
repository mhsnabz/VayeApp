package com.vaye.app.Controller.NotificationService;

public interface NoticesPostNotification {
    interface descp{
        String replied_comment_like = "Yorumunuzu Beğendi";
        String post_like = "Gönderinizi Beğendi";
        String comment_like = "Yorumunuzu Beğendi";
        String new_post = "Yeni Bir Gönderi Paylaştı";

        String new_comment ="Gönderinize Yorum Yaptı";
        String new_mentioned_post = "Bir Gönderide Sizden Bahsetti";
        String new_mentioned_comment = "Bir Yorumda Sizden Bahsetti";
        String new_replied_comment = "Yorumunuza Cevap Verdi";
        String new_replied_mentioned_comment = "Bir Yorumda Sizden Bahsetti";
    }
    interface type{
        String replied_comment_like = "replied_comment_like";
        String post_like = "post_like";
        String comment_like = "comment_like";
        String new_post = "new_post";
        String new_comment ="new_comment";
        String new_mentioned_post = "new_mentioned_post";
        String new_mentioned_comment = "new_mentioned_comment";
        String new_replied_comment = "new_replied_comment";
        String new_replied_mentioned_comment = "new_replied_mentioned_comment";
    }
}

