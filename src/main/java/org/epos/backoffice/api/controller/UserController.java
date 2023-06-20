package org.epos.backoffice.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.epos.backoffice.api.exception.ApiResponseMessage;
import org.epos.backoffice.bean.BackofficeOperationType;
import org.epos.backoffice.bean.ComputePermissionAbstract;
import org.epos.backoffice.bean.RoleEnum;
import org.epos.backoffice.bean.User;
import org.epos.backoffice.service.ComputePermissionNoGroup;
import org.epos.eposdatamodel.Person;
import org.epos.handler.dbapi.DBAPIClient;
import org.epos.handler.dbapi.dbapiimplementation.PersonDBAPI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.epos.backoffice.bean.EntityTypeEnum.USER;
import static org.epos.backoffice.bean.OperationTypeEnum.GET_ALL;
import static org.epos.backoffice.bean.OperationTypeEnum.GET_SINGLE;
import static org.epos.backoffice.bean.RoleEnum.ADMIN;
import static org.epos.backoffice.bean.RoleEnum.VIEWER;

@RestController
public class UserController extends BackofficeAbstractController<Person> implements ApiDocTag {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    @org.springframework.beans.factory.annotation.Autowired
    public UserController(ObjectMapper objectMapper, HttpServletRequest request) {
        super(objectMapper, request, Person.class);
    }

    @RequestMapping(value = "/user",
            produces = {"application/json"},
            method = RequestMethod.PUT)
    @ResponseBody
    @Operation(summary = "Update User", description = "You can use this endpoint to update a User (more information about which fields are required, who has the permission and how to use it are into the BackOffice repository documentation)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "The User is correctly updated.", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "400", description = "Bad request."),
            @ApiResponse(responseCode = "401", description = "Token is missing or invalid"),
            @ApiResponse(responseCode = "404", description = "Not found"),
            @ApiResponse(responseCode = "415", description = "Wrong media type"),
            @ApiResponse(responseCode = "500", description = "Error executing the request, the error may be, either in the gateway or the backoffice-service")
    })
    public ResponseEntity<?> put(
            @RequestBody User body
    ) {
        User user = getUserFromSession();

        if (body.getInstanceId() == null)
            return ResponseEntity.status(400).body(new ApiResponseMessage(1, "missing instanceId in the body"));

        //check admissibility of the operation
        DBAPIClient.GetQuery query = new DBAPIClient.GetQuery().instanceId(body.getInstanceId());
        List<Person> people = dbapi.retrieve(Person.class, query);

        if (people.isEmpty()) {
            return ResponseEntity.status(404).body(new ApiResponseMessage(1, "user not found"));
        }
        if (body.getEduPersonUniqueId() != null && !people.get(0).getAuthIdentifier().equals(body.getEduPersonUniqueId()))
            return ResponseEntity.status(400).body(new ApiResponseMessage(1, "The user instanceId and authIdentifier doesn't correspond"));


        if (!user.getRole().equals(ADMIN)) {
            if (user.getEduPersonUniqueId().equals(body.getEduPersonUniqueId())) {
                if (body.getRole() == null || user.getRole().equals(body.getRole())) {
                    body.setEduPersonUniqueId(people.get(0).getAuthIdentifier());
                    body.update();
                    return ResponseEntity.status(200).body(new ApiResponseMessage(4, "User successfully modified"));
                } else {
                    return ResponseEntity.status(403).body(new ApiResponseMessage(1, "You can't update your role"));
                }
            } else {
                return ResponseEntity.status(403).body(new ApiResponseMessage(1, "You can't update other user"));
            }
        } else {
            body.update();
            return ResponseEntity.status(200).body(new ApiResponseMessage(4, "User successfully modified"));
        }
    }

    @RequestMapping(value = "/user",
            produces = {"application/json"},
            method = RequestMethod.POST)
    @ResponseBody
    @Operation(summary = "Create an User", description = "You can use this endpoint to create a User (more information about which fields are required, who has the permission and how to use it are into the BackOffice repository documentation)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "The User is correctly created.", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "400", description = "Bad request."),
            @ApiResponse(responseCode = "401", description = "Token is missing or invalid"),
            @ApiResponse(responseCode = "404", description = "Not found"),
            @ApiResponse(responseCode = "415", description = "Wrong media type"),
            @ApiResponse(responseCode = "500", description = "Error executing the request, the error may be, either in the gateway or the backoffice-service")
    })
    public ResponseEntity<?> post(
            @RequestBody User body
    ) {
        User user = getUserFromSession();
        body.setEduPersonUniqueId(body.getEduPersonUniqueId() == null ? user.getEduPersonUniqueId() : body.getEduPersonUniqueId());

        if (body.isRegistered()) {
            return ResponseEntity.status(200).body(new ApiResponseMessage(2, "User already registered"));
        }

        if (user.isRegistered()) {
            user.signIn();
            if (user.getRole().equals(ADMIN)) {
                body.signUp();
                return ResponseEntity.status(201).body(new ApiResponseMessage(4, "User successfully registered"));
            }
            return ResponseEntity.status(403).body(new ApiResponseMessage(1, "You can't register other user"));
        }


        if (user.getEduPersonUniqueId().equals(body.getEduPersonUniqueId())) {
            try {
                Objects.requireNonNull(body.getEmail(), "missing email");
                Objects.requireNonNull(body.getFirstName(), "missing first name");
                Objects.requireNonNull(body.getLastName(), "missing last name");
            } catch (NullPointerException e) {
                return ResponseEntity.status(400).body(new ApiResponseMessage(1, "Error during the user registration: " + e.getMessage()));
            }
            body.setRole(VIEWER);
            body.signUp();
            return ResponseEntity.status(201).body(new ApiResponseMessage(4, "User successfully registered"));
        }

        return ResponseEntity.status(403).body(new ApiResponseMessage(1, "You can't register other user"));
    }

    @RequestMapping(value = "/user/{instance_id}",
            produces = {"application/json"},
            method = RequestMethod.GET)
    @ResponseBody
    @Operation(summary = "Get User information", description = "You can use this endpoint to retrieve an User (more information about which fields are required, who has the permission and how to use it are into the BackOffice repository documentation)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "The User is correctly retrieved.", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "400", description = "Bad request."),
            @ApiResponse(responseCode = "401", description = "Token is missing or invalid"),
            @ApiResponse(responseCode = "404", description = "Not found"),
            @ApiResponse(responseCode = "415", description = "Wrong media type"),
            @ApiResponse(responseCode = "500", description = "Error executing the request, the error may be, either in the gateway or the backoffice-service")
    })
    public ResponseEntity<?> get(
            @PathVariable String instance_id,
            @RequestParam(required = false, defaultValue = "false") Boolean available_section
    ) {

        if (instance_id == null)
            return ResponseEntity
                    .status(400)
                    .body(new ApiResponseMessage(1, "The [instance_id] field can't be left blank"));

        User user = getUserFromSession();

        List<Person> personList;
        if (!instance_id.equals("self")) {

            BackofficeOperationType operationType = new BackofficeOperationType()
                    .operationType(instance_id.equals("all") ? GET_ALL : GET_SINGLE)
                    .entityType(USER)
                    .userRole(user.getRole());


            ComputePermissionAbstract computePermission = new ComputePermissionNoGroup(operationType);
            if (!computePermission.isAuthorized())
                return ResponseEntity
                        .status(403)
                        .body(new ApiResponseMessage(1, computePermission.generateErrorMessage()));


            if (instance_id.equals("all")) {
                personList = dbapi.retrieve(Person.class, new DBAPIClient.GetQuery());
            } else {
                personList = dbapi.retrieve(Person.class, new DBAPIClient.GetQuery().instanceId(instance_id));
            }
        } else {
            PersonDBAPI personDBAPI = new PersonDBAPI();
            personDBAPI.setMetadataMode(false);
            personList = Collections.singletonList(personDBAPI.getByAuthId(user.getEduPersonUniqueId()));
        }

        List<User> userStream = personList.stream()
                .filter(this::onlyUser)
                .map(this::mapFromPersonToUser).collect(Collectors.toList());

        if (available_section) userStream.forEach(User::generateAccessibleSection);

        return ResponseEntity
                .status(200)
                .body(userStream);
    }

    @RequestMapping(value = "/user/{instance_id}",
            produces = {"application/json"},
            method = RequestMethod.DELETE)
    @ResponseBody
    @Operation(summary = "Delete a User", description = "You can use this endpoint to delete a User (more information about which fields are required, who has the permission and how to use it are into the BackOffice repository documentation)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "The User is correctly deleted.", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "400", description = "Bad request."),
            @ApiResponse(responseCode = "401", description = "Token is missing or invalid"),
            @ApiResponse(responseCode = "404", description = "Not found"),
            @ApiResponse(responseCode = "415", description = "Wrong media type"),
            @ApiResponse(responseCode = "500", description = "Error executing the request, the error may be, either in the gateway or the backoffice-service")
    })
    public ResponseEntity<?> delete(
            @PathVariable String instance_id
    ) {
        User user = getUserFromSession();

        if (!user.getRole().equals(ADMIN)) {
            return ResponseEntity.status(403).body(new ApiResponseMessage(1, "You can't delete a user"));
        }

        dbapi.delete(Person.class, new DBAPIClient.DeleteQuery().instanceId(instance_id));

        return ResponseEntity.status(200).body(new ApiResponseMessage(4, "User successfully deleted"));
    }


    private User mapFromPersonToUser(Person person) {
        User u = new User();
        u.setEduPersonUniqueId(person.getAuthIdentifier());
        u.setLastName(person.getFamilyName());
        u.setFirstName(person.getGivenName());
        u.setEmail(person.getEmail() != null && !person.getEmail().isEmpty() ? person.getEmail().get(0) : null);
        u.setMetaId(person.getMetaId());
        u.setInstanceId(person.getInstanceId());
        u.setRole(RoleEnum.valueOf(person.getRole().toString()));
        return u;
    }

    private boolean onlyUser(Person x) {
        return x.getAuthIdentifier() != null && !x.getAuthIdentifier().isEmpty();
    }


}
