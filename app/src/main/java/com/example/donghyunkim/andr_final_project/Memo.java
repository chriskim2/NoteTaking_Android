package com.example.donghyunkim.andr_final_project;

/**
 * Created by Donghyun Kim on 2016-12-16.
 */

public class Memo {
    int id;
    String title;
    String imgUri;
    String note;
    double lat;
    double lng;

    public Memo(int id, String title, String imgUri, String note, double lat, double lng) {
        this.id = id;
        this.title = title;
        this.imgUri = imgUri;
        this.note = note;
        this.lat = lat;
        this.lng = lng;
    }

    public String getTitle() {
        return title;
    }

    public String getNote() { return note; }

    public String getImgUri() { return imgUri;}

    public int getId() {return id;}

    public double getLat() {return lat;}

    public double getLng() {return lng;}
}
