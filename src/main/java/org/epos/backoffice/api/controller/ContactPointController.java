package org.epos.backoffice.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.epos.eposdatamodel.ContactPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping(
        value = "/contactpoint",
        produces = {"application/json"}
)
public class ContactPointController extends MetadataAbstractController<ContactPoint> implements ApiDocTag{


    private static final Logger log = LoggerFactory.getLogger(ContactPointController.class);

    @org.springframework.beans.factory.annotation.Autowired
    public ContactPointController(ObjectMapper objectMapper, HttpServletRequest request) {
        super(objectMapper, request, ContactPoint.class);
    }

    @RequestMapping(value = "/{meta_id}/{instance_id}",
            produces = {"application/json"},
            method = RequestMethod.GET)
    @ResponseBody
    @Operation(summary = "Get ContactPoints instances", description = "You can use this endpoint to retrieve all the ContactPoint instances (using \"all\" as path parameter instead of instanceId) or a specific instance the endpoint will return only the instances which the user doing the request have access (more information into the BackOffice repository documentation)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "The ContactPoints instances are correctly retrieved", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "400", description = "Bad request."),
            @ApiResponse(responseCode = "401", description = "Token is missing or invalid"),
            @ApiResponse(responseCode = "404", description = "Not found"),
            @ApiResponse(responseCode = "415", description = "Wrong media type"),
            @ApiResponse(responseCode = "500", description = "Error executing the request, the error may be, either in the gateway or the backoffice-service")
    })
    public ResponseEntity<?> get(
            @PathVariable String meta_id,
            @PathVariable String instance_id
    ) {
        return getMethod(meta_id, instance_id,null);
    }
    
    @RequestMapping(value = "/{meta_id}",
            produces = {"application/json"},
            method = RequestMethod.GET)
    @ResponseBody
    @Operation(summary = "Get ContactPoints instances", description = "You can use this endpoint to retrieve all the ContactPoint instances (using \"all\" as path parameter instead of metaId) or a specific instance the endpoint will return only the instances which the user doing the request have access (more information into the BackOffice repository documentation)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "The ContactPoints instances are correctly retrieved", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "400", description = "Bad request."),
            @ApiResponse(responseCode = "401", description = "Token is missing or invalid"),
            @ApiResponse(responseCode = "404", description = "Not found"),
            @ApiResponse(responseCode = "415", description = "Wrong media type"),
            @ApiResponse(responseCode = "500", description = "Error executing the request, the error may be, either in the gateway or the backoffice-service")
    })
    public ResponseEntity<?> get(
            @PathVariable String meta_id
    ) {
        return getMethod(meta_id, null,null);
    }


    @RequestMapping(
            value = "",
            method = RequestMethod.POST
    )
    @ResponseBody
    @Operation(summary = "create ContactPoint instance", description = "You can use this endpoint to create a ContactPoint instance (more information about which fields are required and how to use it are into the BackOffice repository documentation)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "The ContactPoint instance is correctly created.", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "400", description = "Bad request."),
            @ApiResponse(responseCode = "401", description = "Token is missing or invalid"),
            @ApiResponse(responseCode = "404", description = "Not found"),
            @ApiResponse(responseCode = "415", description = "Wrong media type"),
            @ApiResponse(responseCode = "500", description = "Error executing the request, the error may be, either in the gateway or the backoffice-service")
    })
    public ResponseEntity<?> post(
            @RequestBody ContactPoint body
    ) {
        return postMethod(body, false);
    }

    @RequestMapping(
            value = "",
            method = RequestMethod.PUT
    )
    @ResponseBody
    @Operation(summary = "update ContactPoint instance", description = "You can use this endpoint to update a ContactPoint instance (more information about which fields are required, who has the permission and how to use it are into the BackOffice repository documentation)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "The ContactPoint instance is correctly updated.", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "400", description = "Bad request."),
            @ApiResponse(responseCode = "401", description = "Token is missing or invalid"),
            @ApiResponse(responseCode = "404", description = "Not found"),
            @ApiResponse(responseCode = "415", description = "Wrong media type"),
            @ApiResponse(responseCode = "500", description = "Error executing the request, the error may be, either in the gateway or the backoffice-service")
    })
    public ResponseEntity<?> put(
            @RequestBody ContactPoint body
    ) {
        return updateMethod(body, false);
    }

    @RequestMapping(value = "/{instance_id}",
            method = RequestMethod.DELETE)
    @ResponseBody
    @Operation(summary = "delete ContactPoint instance", description = "You can use this endpoint to delete a ContactPoint instance (more information about which fields are required, who has the permission and how to use it are into the BackOffice repository documentation)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "The ContactPoint instance is correctly deleted.", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "400", description = "Bad request."),
            @ApiResponse(responseCode = "401", description = "Token is missing or invalid"),
            @ApiResponse(responseCode = "404", description = "Not found"),
            @ApiResponse(responseCode = "415", description = "Wrong media type"),
            @ApiResponse(responseCode = "500", description = "Error executing the request, the error may be, either in the gateway or the backoffice-service")
    })
    public ResponseEntity<?> delete(
            @PathVariable String instance_id
    ) {
        return deleteMethod(instance_id);
    }

}