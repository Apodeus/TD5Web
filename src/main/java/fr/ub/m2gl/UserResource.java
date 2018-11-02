package fr.ub.m2gl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.BasicDBObject;
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

//    @GET
//    @Path("/init")
//    public String init() {
//        add(new User("Adrien", "Halnaut"));
//        add(new User("Romain", "Ordonez"));
//        return "Yay !";
//    }


	/**
	 * Return all of the users stored in the database. Can be called using GET method on "/users".
	 * @return users stored on the database.
	 */
    @GET
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
            throw new RuntimeException("Error while trying to fetch user list.", e);
        }
    }

	/**
	 * Search for specific users using their fields. Can be called using GET method on "/users/find?[params]".
	 * @param fname firstName field.
	 * @param lname lastName field.
	 * @param id _id field.
	 * @return the users matching the request.
	 */
    @GET
	@Path("/find")
    @Produces(MediaType.APPLICATION_JSON)
    public List<User> find(@QueryParam("firstName") @DefaultValue("") String fname, @QueryParam("lastName") @DefaultValue("") String lname, @QueryParam("_id") @DefaultValue("") String id) {
        try (MongoClient mongoClient = new MongoClient()) {
            MongoDatabase database = mongoClient.getDatabase(DB_NAME);
            MongoCollection<Document> collection = database.getCollection(DB_COLLECTION);
			BasicDBObject query = new BasicDBObject();

			if (!fname.isEmpty())
				query.append("firstName", fname);
			if (!lname.isEmpty())
				query.append("lastName", lname);
			if (!id.isEmpty())
				query.append("_id", id);
            FindIterable<Document> documents = collection.find(query);
            List<User> allUsers = new ArrayList<>();
            for (Document doc : documents) {
                User user = mapper.readValue(doc.toJson(), User.class);
                allUsers.add(user);
            }

            return allUsers;
        } catch (Exception e) {
            throw new RuntimeException("Error while searching an user.", e);
        }
    }

	/**
	 * Delete an user from a given id. Can be called using the DELETE method on "/users/[id]".
	 * @param id the id of the user to remove.
	 * @return a String reporting if the operation was successful or not.
	 */
	@DELETE
    @Path("/{id}")
    public String deleteUser(@PathParam("id") @DefaultValue("") String id) {
        try (MongoClient mongoClient = new MongoClient()) {

            MongoDatabase database = mongoClient.getDatabase(DB_NAME);
            MongoCollection<Document> collection = database.getCollection(DB_COLLECTION);
            FindIterable<Document> documents = collection.find(Filters.eq("_id", id));
            if (!documents.iterator().hasNext()) {
				return "There is no user in database with id " + id;
			}

			Document userDocument = documents.first();
			User user = mapper.readValue(userDocument.toJson(), User.class);

            String firstname = user.getFirstName();
            String lastname = user.getLastName();

            collection.deleteOne(userDocument);

            return format("User {0} {1} has been removed from database.", firstname, lastname);
        } catch (Exception e) {
            throw new RuntimeException("Error while removing an user.", e);
        }
    }

	/**
	 * Update user information stored in the database from a given dummy user object. The _id field cannot be changed though.
	 * Can be called using PUT method on "/users/[id]".
	 * @param id The id of the user to update.
	 * @param user The dummy object containing the new information. _id field can be omitted on this one.
	 * @return a String reporting if the operation was successful or not.
	 */
	@PUT
	@Path("/{id}")
	@Consumes(MediaType.APPLICATION_JSON)
	public String update(@PathParam("id") String id, User user){
		try (MongoClient mongoClient = new MongoClient()) {

			MongoDatabase database = mongoClient.getDatabase(DB_NAME);
			MongoCollection<Document> collection = database.getCollection(DB_COLLECTION);
			BasicDBObject updatedValues = new BasicDBObject();
			updatedValues.append("$set", new BasicDBObject().append("firstName", user.getFirstName()).append("lastName", user.getLastName()));
			collection.updateOne(Filters.eq("_id", id), updatedValues);
			return collection.find(Filters.eq("_id", id)).first().toJson();
		} catch(Exception e){
			throw new RuntimeException("Error while updating user data.", e);
		}
	}

	/**
	 * Get a user from a given id.
	 * @param id the id of the user to get.
	 * @return the user matching the id. null if no one is matching.
	 */
	@GET
	@Path("/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public User get(@PathParam("id") String id){
		try (MongoClient mongoClient = new MongoClient()) {

			MongoDatabase database = mongoClient.getDatabase(DB_NAME);
			MongoCollection<Document> collection = database.getCollection(DB_COLLECTION);
			FindIterable<Document> docs = collection.find(Filters.eq("_id", id));
			if (!docs.iterator().hasNext())
				return null;

			return mapper.readValue(docs.first().toJson(), User.class);
		} catch(Exception e){
			throw new RuntimeException("Error while looking for user.", e);
		}
	}

	/**
	 * Add a new user in the database.
	 * @param user the user to insert in the database.
	 * @return a String reporting if the operation was successful or not.
	 */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public String add(User user) {
			try(MongoClient mongoClient = new MongoClient()) {
            MongoDatabase db = mongoClient.getDatabase(DB_NAME);
            MongoCollection<Document> collection = db.getCollection(DB_COLLECTION);
            String jsonString = mapper.writeValueAsString(user);
            Document doc = Document.parse(jsonString);
            collection.insertOne(doc);
            return "User " + user.getFirstName() + " " + user.getLastName() + " added successfully.";
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
