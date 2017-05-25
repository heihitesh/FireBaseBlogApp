package com.itshiteshverma.firebaseblogapp.Helper_Classes;

/**
 * Created by Wilmar Africa Ltd on 23-05-17.
 */

public class Blog_GetterSetter {

    private String title;
    private String desc;
    private String image;
    private String name;

    public Blog_GetterSetter(){

    }

    public Blog_GetterSetter(String title, String desc, String image) {
        this.title = title;
        this.desc = desc;
        this.image = image;
    }



    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
