package valet.intan.com.valetparking.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import java.io.ByteArrayOutputStream;

/**
 * Created by DIGIKOM-EX4 on 1/12/2017.
 */

public class BitmapToString {

    public static String create(Bitmap bitmap) {
        if (bitmap != null) {
            String base64Image;
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
            byte[] b = baos.toByteArray();
            base64Image = Base64.encodeToString(b, Base64.DEFAULT);
            return base64Image;
        }
        return null;
    }

    public static Bitmap reverse(String encodedImage) {
        if (encodedImage == null) {
            return null;
        }

        byte[] decodedString = Base64.decode(encodedImage, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
    }
}
