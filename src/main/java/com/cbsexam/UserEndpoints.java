package com.cbsexam;

import com.google.gson.Gson;
import controllers.UserController;
import java.util.ArrayList;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import model.User;
import utils.Encryption;
import utils.Log;
import cache.UserCache;

//Astrids notes: All endpoints from the userclass uses the "user" path.
@Path("user")
public class UserEndpoints {
  //Astrids notes:
  private static UserCache userCache;
  private static UserController userController;

  public UserEndpoints() {
    this.userCache = new UserCache();
    this.userController = new UserController();
  }

  /**
   * @param idUser
   * @return Responses
   */
  // Astrids notes: Using the user/iduser path
  @GET
  @Path("/{idUser}")
  public Response getUser(@PathParam("idUser") int idUser) {

    // Use the ID to get the user from the controller.
    User user = UserController.getUser(idUser);

    // TODO: Add Encryption to JSON : FIXED
    // Convert the user object to json in order to return the object
    String json = new Gson().toJson(user);
    json = Encryption.encryptDecryptXOR(json);

    /*Astrid notes: Calling the method in utils Encryption and encrypting a the String of Json
     json = Encryption.encryptDecryptXOR(json); */


    // TODO: What should happen if something breaks down? - FIXED
    //Astrid changes: creating an if statement to check method getUser
      if (user!=null) {
        // Return the user with the status code 200
        return Response.status(200).type(MediaType.APPLICATION_JSON_TYPE).entity(json).build();

      } else {
          //Astrid Changes: HTTP message 400 ocurs, as this means there is an error
          return Response.status(400).entity("Could not get user").build();
        }
      }
  /** @return Responses */
  @GET
  @Path("/")
  public Response getUsers() {

    // Write to log that we are here
    Log.writeLog(this.getClass().getName(), this, "Get all users", 0);

    // Get a list of users
    ArrayList<User> users = userCache.getUsers(false);

    // TODO: Add Encryption to JSON - FIXED

    // Transfer users to json in order to return it to the user
    String json = new Gson().toJson(users);
    json = Encryption.encryptDecryptXOR(json);

    // Return the users with the status code 200
    return Response.status(200).type(MediaType.APPLICATION_JSON).entity(json).build();
  }

  @POST
  @Path("/")
  @Consumes(MediaType.APPLICATION_JSON)
  public Response createUser(String body) {

    // Read the json from body and transfer it to a user class
    User newUser = new Gson().fromJson(body, User.class);

    // Use the controller to add the user
    User createUser = UserController.createUser(newUser);

    // Get the user back with the added ID and return it to the user
    String json = new Gson().toJson(createUser);

    // Return the data to the user
    if (createUser != null) {
      // Return a response with status 200 and JSON as type
      return Response.status(200).type(MediaType.APPLICATION_JSON_TYPE).entity(json).build();
    } else {
      return Response.status(400).entity("Could not create user").build();
    }
  }

  // TODO: Make the system able to login users and assign them a token to use throughout the system. - FIXED
  @POST
  @Path("/login")
  @Consumes(MediaType.APPLICATION_JSON)
  public Response loginUser(String body) {

    User user = new Gson().fromJson(body, User.class);

    String token = userController.loginUser(user);

    try{

    if (token != null) {
      return Response.status(200).type(MediaType.APPLICATION_JSON_TYPE).entity(token).build();
    } else {
      // Return a response with status 200 and JSON as type
      return Response.status(400).entity("Brugeren kunne ikke oprettes").build();
    }
    }catch(Exception x) {
        System.out.println(x.getMessage());
      }
      return null;
    }

  // TODO: Make the system able to delete users - FIXED

  @POST
  @Path("/delete")
  @Consumes(MediaType.APPLICATION_JSON)

  public Response deleteUser(String body) {

    User user = new Gson().fromJson(body, User.class);

    String token = UserController.getTokenVerifier(user);

    if(token !=null){
      UserController.deleteUser(user);

      return Response.status(200).type(MediaType.APPLICATION_JSON_TYPE).entity("The user has been deleted").build();
    }else
      // Return a response with status 200 and JSON as type
      return Response.status(400).entity("Something went wrong").build();
  }


  // TODO: Make the system able to update users - FIXED
  @POST
  @Path("/update")
  @Consumes(MediaType.APPLICATION_JSON)

  public Response updateUser(String body) {

    User user = new Gson().fromJson(body, User.class);
    String token = UserController.getTokenVerifier(user);

    //Astrid notes: skal denne være i en try-catch??

    if(token != ""){
      UserController.update(user);
      return Response.status(200).type(MediaType.APPLICATION_JSON_TYPE).entity("The user has been updated").build();
    } else
      return Response.status(400).entity("Something went wrong").build();

  }
}

