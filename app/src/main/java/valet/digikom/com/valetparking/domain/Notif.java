package valet.digikom.com.valetparking.domain;

/**
 * Created by DIGIKOM-EX4 on 12/30/2016.
 */

public class Notif {

    private int id;
    private String msg;

    public Notif(int id, String msg) {
        this.id = id;
        this.msg = msg;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
