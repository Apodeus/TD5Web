package fr.ub.m2gl;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
public class User {

    private String _id;

    private String firstName;

    private String lastName;

    public User(){
        _id = UUID.randomUUID().toString();
    }

    public User(String firstName, String lastName){
        this.firstName = firstName;
        this.lastName = lastName;
        _id = UUID.randomUUID().toString();
    }

    public User(String _id, String firstName, String lastName){
        this.firstName = firstName;
        this.lastName = lastName;
        this._id = _id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }
}
