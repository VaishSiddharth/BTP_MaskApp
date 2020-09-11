package com.btp.maskapp.Utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Base64;
import android.util.Log;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import static com.btp.maskapp.Utils.PreferenceManager.PROFILE_PIC_LOCAL;


public class CompressBitmap {



    public static File compress(Context context, Bitmap bitmap){

        File f = new File( context.getCacheDir(), "zpi");
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            Bitmap original=bitmap;
            //PreferenceManager.setImage(PROFILE_PIC_LOCAL, original);
            original.compress(Bitmap.CompressFormat.JPEG, 50, out);
            byte[] bitmapData = out.toByteArray();
            String imageEncoded = Base64.encodeToString(bitmapData, Base64.DEFAULT);
            Log.i("imagebase64",imageEncoded);
            PreferenceManager.setStringValue(PROFILE_PIC_LOCAL, imageEncoded);
            FileOutputStream fos = new FileOutputStream(f);
            fos.write(bitmapData);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return f;
    }

    public static void compressandsave(Context context, Bitmap bitmap){


            ByteArrayOutputStream out = new ByteArrayOutputStream();
            Bitmap original=bitmap;
            //PreferenceManager.setImage(PROFILE_PIC_LOCAL, original);
            original.compress(Bitmap.CompressFormat.JPEG, 50, out);
            byte[] bitmapData = out.toByteArray();
            String imageEncoded = Base64.encodeToString(bitmapData, Base64.DEFAULT);
            Log.i("imagebase64",imageEncoded);
            PreferenceManager.setStringValue(PROFILE_PIC_LOCAL, imageEncoded);


    }
}
