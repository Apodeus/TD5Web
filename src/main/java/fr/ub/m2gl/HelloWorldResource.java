package fr.ub.m2gl;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Path("/hello")
public class HelloWorldResource {

    public final UserResource userResource;

    public HelloWorldResource(){
        this.userResource = new UserResource();
    }

    @GET
    @Produces("text/plain")
    public String getHelloWorld() {
        User user = new User();
        user.setFirstName("Romain");
        user.setLastName("Ordo");

        String result = userResource.convertUserToJSON(user);
//        User nu = userResource.convertJSONToUser(result);
//        result = result + nu.getFirstName() + nu.getLastName();
        return result;
    }
}
