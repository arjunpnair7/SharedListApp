package com.example.sharedlistapp.Model;

import java.util.Date;

public class MyListItem {

    private String Date;
    private String Genre;
    private String Title;

    public MyListItem(String date, String title, String genre) {
        Date = date;
        Genre = genre;
        Title = title;

    }

    public MyListItem() {
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getGenre() {
        return Genre;
    }

    public void setGenre(String genre) {
        Genre = genre;
    }
}
