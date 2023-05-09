package com.example.musicapplication.Model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.Timestamp;

import java.util.Date;

public class Song implements Parcelable {
    String id, duration, image, link, title, lyric,idAlbum, idSinger, idBanner;
    int like;
    Timestamp release;

    public Song(String id, String duration, String image,  String link, String title, String lyric, int like, Timestamp release, String idAlbum, String idSinger, String idBanner) {
        this.id = id;
        this.duration = duration;
        this.image = image;
        this.link = link;
        this.title = title;
        this.lyric = lyric;
        this.like = like;
        this.release = release;
        this.idAlbum = idAlbum;
        this.idSinger = idSinger;
        this.idBanner = idBanner;
    }

    protected Song(Parcel in) {

        id = in.readString();
        duration = in.readString();
        image = in.readString();
        link = in.readString();
        title = in.readString();
        lyric = in.readString();
        like = in.readInt();
        release = new Timestamp(new Date(in.readLong()));
        idAlbum = in.readString();
        idSinger = in.readString();
        idBanner = in.readString();
    }

    public static final Creator<Song> CREATOR = new Creator<Song>() {
        @Override
        public Song createFromParcel(Parcel in) {
            return new Song(in);
        }

        @Override
        public Song[] newArray(int size) {
            return new Song[size];
        }
    };

    public String getId() {
        return id;
    }

    public String getDuration() {
        return duration;
    }

    public String getImage() {
        return image;
    }

    public String getLink() {
        return link;
    }

    public String getTitle() {
        return title;
    }

    public String getLyric() {
        return lyric;
    }

    public String getIdAlbum() {
        return idAlbum;
    }

    public String getIdSinger() {
        return idSinger;
    }


    public int getLike() {
        return like;
    }

    public String getIdBanner() {
        return idBanner;
    }

    public Timestamp getRelease() {
        return release;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {


        dest.writeString(id);
        dest.writeString(duration);
        dest.writeString(image);
        dest.writeString(link);
        dest.writeString(title);
        dest.writeString(lyric);
        dest.writeInt(like);
        dest.writeLong(release.toDate().getTime());
        dest.writeString(idAlbum);
        dest.writeString(idSinger);
        dest.writeString(idBanner);
    }
}
