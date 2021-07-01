package com.example.sharedlistapp.Model;

public class MyLists {

    private String listName;
    private String listCategory;

    public MyLists() {
    }

    public MyLists(String listName, String listCategory) {
        this.listName = listName;
        this.listCategory = listCategory;
    }

    public String getListName() {
        return listName;
    }

    public void setListName(String listName) {
        this.listName = listName;
    }

    public String getListCategory() {
        return listCategory;
    }

    public void setListCategory(String listCategory) {
        this.listCategory = listCategory;
    }
    
}
