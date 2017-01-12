package valet.digikom.com.valetparking.util;

import android.graphics.Bitmap;
import android.util.Base64;

import java.io.ByteArrayOutputStream;

/**
 * Created by DIGIKOM-EX4 on 1/12/2017.
 */

public class BitmapToString {

    public static String create(Bitmap bitmap) {
        String base64Image;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] b = baos.toByteArray();
        base64Image = Base64.encodeToString(b, Base64.DEFAULT);
        return base64Image;
    }
}
