package valet.digikom.com.valetparking.util;

/**
 * Created by fadlymunandar on 8/5/17.
 */

public interface Syncing {

    void cancelRequest();
    void sendProgress(int progress);
    void setProgressTitle(String title);
    void setProgressMessage(String message);
}
