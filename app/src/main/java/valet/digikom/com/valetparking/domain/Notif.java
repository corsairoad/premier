package valet.digikom.com.valetparking.domain;

/**
 * Created by DIGIKOM-EX4 on 12/30/2016.
 */

public class Notif {

    private int id;
    private String msg;
    private String subject;


    public Notif(int id, String msg, String subject) {
        this.id = id;
        this.msg = msg;
        this.subject = subject;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
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

    @Override
    public String toString() {
<<<<<<< HEAD
        return "Message : \nid: " + getId() + "\nmsg: " + getMsg() + "\n";
=======
        return "Message : \nid: " + getId() + "\nmsg: " + getMsg() + "\nsubject: " + getSubject() + "\n";
        // adfadfad;
>>>>>>> cbd4ed75eeecd822ec254fe600d0afb9a28b0843
    }
}
