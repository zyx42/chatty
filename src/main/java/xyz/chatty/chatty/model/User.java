package xyz.chatty.chatty.model;

import java.security.Principal;

public class User implements Principal {

    String name;

    public User() {
        this.name = "default_user";
    }

    public User(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
