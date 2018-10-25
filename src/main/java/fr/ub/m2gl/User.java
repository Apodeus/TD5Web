package fr.ub.m2gl;

import com.fasterxml.jackson.annotation.JsonProperty;

public class User {

    @JsonProperty("NamePerso")
    private String firstName;

    @JsonProperty("NamePerso2")
    private String lastName;

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
}
