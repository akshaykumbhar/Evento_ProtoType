package com.evento.evento;

/**
 * Created by kumbh on 10/6/2018.
 */

public class Events {
    String name;
    String sub;
    String contact;
    String eventno;
    String imgurl;

    public Events() {
    }

    public Events(String name, String sub, String contact, String eventno, String imgurl) {
        this.name = name;
        this.sub = sub;
        this.contact = contact;
        this.eventno = eventno;
        this.imgurl = imgurl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSub() {
        return sub;
    }

    public void setSub(String sub) {
        this.sub = sub;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getEventno() {
        return eventno;
    }

    public void setEventno(String eventno) {
        this.eventno = eventno;
    }

    public String getImgurl() {
        return imgurl;
    }

    public void setImgurl(String imgurl) {
        this.imgurl = imgurl;
    }
}

