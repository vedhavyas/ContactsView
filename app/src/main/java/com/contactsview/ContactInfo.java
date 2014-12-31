package com.contactsview;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

/**
 * Authored by vedhavyas.singareddi on 23-12-2014.
 */
public class ContactInfo {

    private String name;
    private String ID;
    private Bitmap image;
    private String email;

    public void setImage(byte[] data) {
        this.image = Utils.getBitmapFromBlob(data);
    }

    public ContactInfo(String ID, String title, Drawable drawable) {
        this.ID = ID;
        this.name = title;
        this.image = ((BitmapDrawable)drawable).getBitmap();
    }

    public String getName() {
        return name;
    }

    public Bitmap getImage(){
        return image;
    }
    public String getID(){
        return ID;
    }

    public String getEmail() {
        return email;
    }

    public void addEmail(String email){
        if(this.email == null){
            this.email = email;
        }else {
            if(!isEmailExist(email)) {
                this.email += ":" + email;
            }
        }
    }

    public String[] getEmails(){
        return email.split(":");
    }

    private boolean isEmailExist(String data){
        String[] emails = this.email.split(":");
        for(String email : emails){
            if(email.equalsIgnoreCase(data)){
                return true;
            }
        }

        return false;
    }
}
