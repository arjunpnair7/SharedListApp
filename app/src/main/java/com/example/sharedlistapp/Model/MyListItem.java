package com.example.sharedlistapp.Model;

import java.util.Date;

public class MyListItem {

    private String Completed;
    private String Date;
    private String Genre;
    private String Title;


    public String getCompleted() {
        return Completed;
    }

    public void setCompleted(String completed) {
        Completed = completed;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public String getGenre() {
        return Genre;
    }

    public void setGenre(String genre) {
        Genre = genre;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public MyListItem(String completed, String date, String genre, String title) {
        Completed = completed;
        Date = date;
        Genre = genre;
        Title = title;
    }

    public MyListItem() {
    }
}
