package com.pu.facebook.util;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Random;

public class BitmapUtil {
    public static Bitmap loadBitmap(Context context, String path) {
        try {
            ContextWrapper cw = new ContextWrapper(context);
            // path to /data/data/yourapp/app_data/imageDir
            File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
            File f = new File(directory.getAbsolutePath(), path);
            return BitmapFactory.decodeStream(new FileInputStream(f));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String saveToInternalStorage(Context context, Bitmap bitmapImage) {
        ContextWrapper cw = new ContextWrapper(context);

        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        // Create imageDir
        String imageName = randomString() + ".jpg";
        File mypath = new File(directory, imageName);

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return imageName;
    }

    private static String randomString() {
        String DATA = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        Random RANDOM = new Random();
        StringBuilder sb = new StringBuilder(5);

        for (int i = 0; i < 5; i++) {
            sb.append(DATA.charAt(RANDOM.nextInt(DATA.length())));
        }

        return sb.toString();
    }
}
