package com.contactsview;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/**
 * Authored by vedhavyas.singareddi on 24-12-2014.
 */
public class Utils {

    public static Bitmap getBitmapFromBlob(byte[] blob) {
        Bitmap bitmap = null;
        if (blob != null) {
            bitmap = BitmapFactory.decodeByteArray(blob, 0, blob.length);
        }
        return bitmap;
    }

}
