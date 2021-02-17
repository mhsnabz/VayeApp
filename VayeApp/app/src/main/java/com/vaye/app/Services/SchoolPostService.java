package com.vaye.app.Services;

import java.util.ArrayList;

public class SchoolPostService {
    private static final SchoolPostService instance = new SchoolPostService();
    public static SchoolPostService shared() {
        return instance;
    }

    String iste_hastag[] = {"Atatürkçü Düşünce Öğrenci Topluluğu","Bağımlılıkla Mücadele Öğrenci Topluluğu","Bilim Kadınları Öğrenci Topluluğu",
            "Bilim Ve Çocuk Öğrenci Topluluğu","Bilimsel Araştırmalar Öğrenci Topluluğu","Bireysel Sporlar Öğrenci Topluluğu"
            ,"Bisiklet Öğrenci Topluluğu","Çevre Öğrenci Topluluğu","Doğa Sporları Öğrenci Topluluğu","Edebiyat Öğrenci Topluluğu",
            "Ekonomi Öğrenci Topluluğu","Fotoğrafçılık Öğrenci Topluluğu","Gezi Öğrenci Topluluğu","Gönüllü Genç Sağlık Liderleri Öğrenci Topluluğu",
            "Güzel Sanatlar Öğrenci Topluluğu","Halk Dansları Öğrenci Topluluğu",
            "Havacılık Öğrenci Topluluğu","Hayvanları Koruma Öğrenci Topluluğu","Hidrojen Teknolojileri Öğrenci Topluluğu","İSTE-IEEE Öğrenci Topluluğu",
            "İSTE Endüstri Öğrenci Topluluğu","İSTE E-Spor Öğrenci Topluluğu",
            "İSTE Genç Tema Öğrenci Topluluğu","İSTE İzcilik Öğrenci Topluluğu","İSTE-Spe Öğrenci Topluluğu","İSTE-Engelsiz Öğrenci Topluluğu",
            "Karikatür Ve Mizah Öğrenci Topluluğu","Kültür Ve Sanat Öğrenci Topluluğu","Matematik Öğrenci Topluluğu","Mekatronik Öğrenci Topluluğu",
            "Metalurji Öğrenci Topluluğu","Müzik Öğrenci Topluluğu","Ombudsmanlık Öğrenci Topluluğu","Radyo Ve Televizyon Öğrenci Topluluğu",
            "Resim Öğrenci Topluluğu", "Robotik Öğrenci Topluluğu","Satranç Öğrenci Topluluğu","Savunma SanayiTeknolojileri Öğrenci Topluluğu",
            "Sinema Öğrenci Topluluğu","Sosyal Sorumluluk Öğrenci Topluluğu","Sualtı Öğrenci Topluluğ","Takım Sporları Öğrenci Topluluğu"," Tasarım Öğrenci Topluluğu"
            ,"Teknoloji Öğrenci Topluluğu","Tiyatro Öğrenci Topluluğu","Turizm Öğrenci Topluluğu","Türk Tarihi Araştırma Öğrenci Topluluğu","Uluslararası İlişkiler Öğrenci Topluluğu",
            "Uluslararası İlişkiler Öğrenci Topluluğu","Üniversite-Sanayi İşbirliği Öğrenci Topluluğu"
            ,"Üniversite-Sanayi İşbirliği Öğrenci Topluluğu","Yelken Öğrenci Topluluğu","Yenilikçilik Ve Girişimcilik Öğrenci Topluluğu"};


    public ArrayList<String> getHastah(String shortSchool){
        ArrayList<String> list = new ArrayList<>();
        if (short_school.İSTE.equals(shortSchool)){
            for (String i : iste_hastag){
                list.add(i);
            }
            return  list;
        }
        else {
            return  null;
        }
    }


    interface  short_school{
        String İSTE = "İSTE";
    }
}
