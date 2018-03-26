package com.sattlerio.microservices.api_gateway.objects;

public class User {

    private String firstname;
    private String lastname;
    private String email;
    private String uuid;
    private String user_id;

    public User(String firstname, String lastname, String email, String uuid, String user_id) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
        this.uuid = uuid;
        this.user_id = user_id;
    }

    public String getFirstname() {
        return firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public String getEmail() {
        return email;
    }

    public String getUuid() {
        return uuid;
    }

    public String getUser_id() {
        return user_id;
    }
}
