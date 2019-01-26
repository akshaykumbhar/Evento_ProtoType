package com.evento.evento;

/**
 * Created by kumbh on 9/25/2018.
 */

public class User {

    String name;
    String email;
    String col;
    String phone;
    String prouri;

    public User() {
    }

    public User(String name, String email, String col, String phone,String prouri) {
        this.name = name;
        this.email = email;
        this.col = col;
        this.phone = phone;
        this.prouri = prouri;
    }

    public String getName() {
        return name;
    }

    public String getProuri() {
        return prouri;
    }

    public void setProuri(String prouri) {
        this.prouri = prouri;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCol() {
        return col;
    }

    public void setCol(String col) {
        this.col = col;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
