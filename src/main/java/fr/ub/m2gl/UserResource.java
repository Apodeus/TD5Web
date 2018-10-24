package fr.ub.m2gl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class UserResource {

    private final ObjectMapper mapper;

    public UserResource(){
        this.mapper = new ObjectMapper();
    }


    public String convertUserToJSON(User user){
        try {
            return mapper.writeValueAsString(user);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error during creating String JSON from User", e);
        }
    }

}
