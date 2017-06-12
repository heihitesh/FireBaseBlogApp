package com.itshiteshverma.firebaseblogapp.Helper_Classes;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Wilmar Africa Ltd on 23-05-17.
 */

public class Blog_GetterSetter {

    private String title;
    private String image;
    private String name;
    private String timestamp;

    public Blog_GetterSetter(){

    }

    public Blog_GetterSetter(String title, String image) {
        this.title = title;
        this.image = image;
    }



    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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

    public String getTimestamp() {
        DateFormat fromFormat = new SimpleDateFormat("dd-M-yyyy hh:mm:ss");
        fromFormat.setLenient(false);
        DateFormat toFormat = new SimpleDateFormat("dd MMMM");
        toFormat.setLenient(false);
        String dateStr = timestamp;
        Date date = null;
        try {
            date = fromFormat.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return toFormat.format(date);
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
