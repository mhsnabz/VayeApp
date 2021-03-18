package com.vaye.app.Model;

import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;

public class SchoolModel  implements Parcelable {
      String  name ,shortName;
      Drawable logo ;


    public SchoolModel(String name, String shortName, Drawable logo) {
        this.name = name;
        this.shortName = shortName;
        this.logo = logo;
    }

    protected SchoolModel(Parcel in) {
        name = in.readString();
        shortName = in.readString();
    }

    public static final Creator<SchoolModel> CREATOR = new Creator<SchoolModel>() {
        @Override
        public SchoolModel createFromParcel(Parcel in) {
            return new SchoolModel(in);
        }

        @Override
        public SchoolModel[] newArray(int size) {
            return new SchoolModel[size];
        }
    };

    public String getName() {
        return name;
    }

    public String getShortName() {
        return shortName;
    }

    public Drawable getLogo() {
        return logo;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeString(shortName);
    }
}
