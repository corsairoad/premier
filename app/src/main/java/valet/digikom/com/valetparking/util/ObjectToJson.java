package valet.digikom.com.valetparking.util;

import com.google.gson.Gson;

/**
 * Created by DIGIKOM-EX4 on 2/20/2017.
 */

public class ObjectToJson {

    public static String getJson(Object object) {
        Gson gson = new Gson();
        String json = gson.toJson(object);
        return json;
    }
}
