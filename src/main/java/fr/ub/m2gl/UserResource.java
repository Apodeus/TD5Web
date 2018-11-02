package fr.ub.m2gl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import org.bson.Document;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.List;

import static java.text.MessageFormat.format;

@Path("/user")
public class UserResource {

    public static final String DB_NAME = "myBase";
    public static final String DB_COLLECTION = "myCollection";
    private final ObjectMapper mapper;

    public UserResource() {
        this.mapper = new ObjectMapper();
    }

    @GET
    @Path("/init")
    public String init() {
        add(new User("Adrien", "Halnaut"));
        add(new User("Romain", "Ordonez"));
        return "Yay !";
    }

    @GET
    @Path("/list")
    @Produces(MediaType.APPLICATION_JSON)
    public List<User> getAllUsers() {
        try (MongoClient mongoClient = new MongoClient()) {

            MongoDatabase database = mongoClient.getDatabase(DB_NAME);
            MongoCollection<Document> collection = database.getCollection(DB_COLLECTION);

            FindIterable<Document> documents = collection.find();
            List<User> allUsers = new ArrayList<>();
            for (Document doc : documents) {
                System.out.println(doc.toJson());
                User user = mapper.readValue(doc.toJson(), User.class);
                allUsers.add(user);
            }

            return allUsers;
        } catch (Exception e) {
            throw new RuntimeException("Error : trying to add invalid user.", e);
        }
    }

    @GET
    @Path("/getUser")
    @Produces(MediaType.APPLICATION_JSON)
    public List<User> getUser(@QueryParam("firstName") @DefaultValue("") String fname, @QueryParam("lastName") @DefaultValue("") String lname, @QueryParam("_id") @DefaultValue("") String id) {
        try (MongoClient mongoClient = new MongoClient()) {

            MongoDatabase database = mongoClient.getDatabase(DB_NAME);
            MongoCollection<Document> collection = database.getCollection(DB_COLLECTION);
            FindIterable<Document> documents = collection.find(Filters.eq("firstName", fname));
            List<User> allUsers = new ArrayList<>();
            for (Document doc : documents) {
                System.out.println(doc.toJson());
                User user = mapper.readValue(doc.toJson(), User.class);
                allUsers.add(user);
            }

            return allUsers;
        } catch (Exception e) {
            throw new RuntimeException("Error : trying to add invalid user.", e);
        }
    }

    @GET
    @Path("/delete")
    public String getUser(@QueryParam("_id") @DefaultValue("") String id) {
        try (MongoClient mongoClient = new MongoClient()) {

            MongoDatabase database = mongoClient.getDatabase(DB_NAME);
            MongoCollection<Document> collection = database.getCollection(DB_COLLECTION);
            FindIterable<Document> documents = collection.find(Filters.eq("_id", id));
            Document userDocument = null;
            User user = null;
            for (Document doc : documents) {
                user = mapper.readValue(doc.toJson(), User.class);
                userDocument = doc;
            }

            if(user == null){
                return "There is no user in database with this id : " + id;
            }

            String firstname = user.getFirstName();
            String lastname = user.getLastName();

            collection.deleteOne(userDocument);

            return format("User {0} {1} has been removed from database !", firstname, lastname);
        } catch (Exception e) {
            throw new RuntimeException("Error : trying to add invalid user.", e);
        }
    }

    /**
     * Save an User in MongoDB
     * @param user
     * @return a message to display
     */
    @POST
    @Path("/add")
    @Consumes(MediaType.APPLICATION_JSON)
    public String add(User user) {
        try (MongoClient mongoClient = new MongoClient()) {
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

    /*
    @POST
    @Path("/add")
    //@Consumes(MediaType.APPLICATION_JSON)
    public String addUser(@FormParam("fname") String fname, @FormParam("lname") String lname){
        User u = new User(fname, lname);
        return saveUserToDB(u);
    }
    */
}
