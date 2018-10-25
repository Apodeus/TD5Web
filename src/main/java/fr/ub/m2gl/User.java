package fr.ub.m2gl;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.bson.Document;
import org.bson.types.ObjectId;

@JsonIgnoreProperties
public class User {
	private ObjectId _id;


    @JsonProperty("NamePerso")
    private String firstName;

    @JsonProperty("NamePerso2")
    private String lastName;

    public User(){
        _id = ObjectId.get();
    }

    public User(ObjectId id, String firstName, String lastName){
        this.firstName = firstName;
        this.lastName = lastName;
        this._id = id;
    }

    public User(String firstName, String lastName){
        this.firstName = firstName;
        this.lastName = lastName;
        this._id = ObjectId.get();
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

    public ObjectId get_id() {
        return _id;
    }

    public void set_id(ObjectId _id) {
        this._id = _id;
    }



}
