package valet.digikom.com.valetparking.domain;

/**
 * Created by DIGIKOM-EX4 on 3/22/2017.
 */

public class CheckoutData {

    private int remoteVthdId;
    private String jsonData;

    public CheckoutData() {
    }

    public int getRemoteVthdId() {
        return remoteVthdId;
    }

    public void setRemoteVthdId(int remoteVthdId) {
        this.remoteVthdId = remoteVthdId;
    }

    public String getJsonData() {
        return jsonData;
    }

    public void setJsonData(String jsonData) {
        this.jsonData = jsonData;
    }
}
