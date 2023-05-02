package com.example.musicapplication.Model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.lang.reflect.Array;
import java.util.List;

public class Singer implements Parcelable {
    String id,name,image;
    List<String> idAlbum;

    public Singer(String id, String name, String image, List<String> idAlbum) {
        this.id = id;
        this.name = name;
        this.image = image;
        this.idAlbum = idAlbum;
    }

    protected Singer(Parcel in) {
        id = in.readString();
        name = in.readString();
        image = in.readString();
        idAlbum = in.createStringArrayList();
    }

    public static final Creator<Singer> CREATOR = new Creator<Singer>() {
        @Override
        public Singer createFromParcel(Parcel in) {
            return new Singer(in);
        }

        @Override
        public Singer[] newArray(int size) {
            return new Singer[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public List<String> getIdAlbum() {
        return idAlbum;
    }

    public void setIdAlbum(List<String> idAlbum) {
        this.idAlbum = idAlbum;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(name);
        parcel.writeString(image);
        parcel.writeStringList(idAlbum);
    }
}
