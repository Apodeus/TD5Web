package fr.ub.m2gl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.List;

@Path("/user")
public class UserResource {

    public static final String DB_NAME = "myBase";
    public static final String DB_COLLECTION = "myCollection";
    private final ObjectMapper mapper;

    public UserResource(){
        this.mapper = new ObjectMapper();
    }


    /*@GET
    @Path("/get")
    @Produces(MediaType.APPLICATION_JSON)
    public User convertUserToJSON(){
        return new User("Adrien", "Halnaut");
    }

    @POST
    @Path("/post")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response convertJSONToUser(User user){
        String result = "User saved : " + user;
        return Response.status(201).entity(result).build();
    }*/

    @GET
    @Path("/init")
    public String init(){
        User usr1 = new User("Adrien", "Halnaut");
        User usr2 = new User("Romain", "Ordonez");
        addUserToBDD(usr1);
        addUserToBDD(usr2);
        return "Yay !";
    }

    @GET
    @Path("/get")
    @Produces(MediaType.APPLICATION_JSON)
    public List<User> getAllUsers(){
        try (MongoClient mongoClient = new MongoClient()) {
            MongoDatabase database = mongoClient.getDatabase(DB_NAME);
            MongoCollection<Document> collection = database.getCollection(DB_COLLECTION);
            FindIterable<Document> documents = collection.find();
            List<User> allUsers = new ArrayList<>();
            for(Document doc : documents){
                User user = mapper.readValue(doc.toJson(), User.class);
                allUsers.add(user);
            }

            return allUsers;
        } catch(Exception e){
            throw new RuntimeException("Error : trying to add invalid user.");
        }
    }

    @POST
    @Path("/add")
    @Consumes(MediaType.APPLICATION_JSON)
    public String addUser(User user){
        return addUserToBDD(user);
    }

    private String addUserToBDD(User user) {
        try(MongoClient mongoClient = new MongoClient()) {
            MongoDatabase db = mongoClient.getDatabase(DB_NAME);
            MongoCollection<Document> collection = db.getCollection(DB_COLLECTION);
            String jsonString = mapper.writeValueAsString(user);
            Document doc = Document.parse(jsonString);
            collection.insertOne(doc);

            return "Utilisateur " + user.getFirstName() + " " + user.getLastName() + " added successfully.";
        } catch (Exception e) {
            throw new RuntimeException("Error : trying to add invalid user.");
        }
    }

}
