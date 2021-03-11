package com.vaye.app.Controller.NotificationService;

public interface MsgNotification {
    interface  type{
            String  new_msg ="new_msg";
        String new_rqst = "new_rqst";
        String new_image="new_image";
        String new_doc="new_doc";
        String new_location="new_location";
        String new_record="new_record";

    }
    interface  descp {
        String  new_msg ="Yeni Bir Mesaj Gönderdi";
        String new_rqst = "Size Mesaj Göndermek İstiyor";
        String new_image="Size Resim Gönderdi";
        String new_doc="Size Belge Gönderdi";
        String new_location="Sizinle Konum Paylaştı";
        String new_record="Size Yeni Bir Ses Dosyası Gönderdi";
    }
}
