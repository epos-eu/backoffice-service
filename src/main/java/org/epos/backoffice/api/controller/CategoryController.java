package org.epos.backoffice.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import org.epos.backoffice.api.controller.AbstractController.LocalDateAdapter;
import org.epos.backoffice.api.util.CategoryManager;
import org.epos.backoffice.bean.User;
import org.epos.eposdatamodel.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;


@RestController
@RequestMapping(
        value = "/categories",
        produces = {"application/json"}
)
public class CategoryController implements ApiDocTag{

	protected final ObjectMapper objectMapper;
	private final HttpServletRequest request;

	protected Gson gson = new GsonBuilder()
	        .setPrettyPrinting()
	        .registerTypeAdapter(LocalDateTime.class, new LocalDateAdapter())
	        .create();

	@org.springframework.beans.factory.annotation.Autowired
	public CategoryController(ObjectMapper objectMapper, HttpServletRequest request) {
		this.objectMapper = objectMapper;
		this.request = request;
	}

	protected User getUserFromSession() {
		return (User) request.getSession().getAttribute("user");
	}
	class LocalDateAdapter implements JsonSerializer<LocalDateTime> {

	    public JsonElement serialize(LocalDateTime date, Type typeOfSrc, JsonSerializationContext context) {
	        return new JsonPrimitive(date.format(DateTimeFormatter.ISO_DATE_TIME)); // "yyyy-mm-dd"
	    }
	}

    private static final Logger log = LoggerFactory.getLogger(CategoryController.class);

    @RequestMapping(value = "/{meta_id}/{instance_id}",
            method = RequestMethod.GET)
    @ResponseBody
    @io.swagger.v3.oas.annotations.Operation(summary = "Get Category instances", description = "You can use this endpoint to retrieve all the Category instances (using \"all\" as path parameter instead of instanceId) or a specific instance the endpoint will return only the instances which the user doing the request have access (more information into the BackOffice repository documentation)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "The Category instances are correctly retrieved", content = @Content(mediaType = "application/json")),
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

		List<Category> list = new ArrayList<Category>();
		list = (List<Category>) CategoryManager.getCategories("all", instance_id, null).getListOfEntities();
		if (list.isEmpty())
			return ResponseEntity.status(404).body("[]");

		return ResponseEntity
				.status(200)
				.body(list);
    }
    
    @RequestMapping(value = "/{meta_id}",
            method = RequestMethod.GET)
    @ResponseBody
    @io.swagger.v3.oas.annotations.Operation(summary = "Get Category instances", description = "You can use this endpoint to retrieve all the Category instances (using \"all\" as path parameter instead of metaId) or a specific instance the endpoint will return only the instances which the user doing the request have access (more information into the BackOffice repository documentation)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "The Category instances are correctly retrieved", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "400", description = "Bad request."),
            @ApiResponse(responseCode = "401", description = "Token is missing or invalid"),
            @ApiResponse(responseCode = "404", description = "Not found"),
            @ApiResponse(responseCode = "415", description = "Wrong media type"),
            @ApiResponse(responseCode = "500", description = "Error executing the request, the error may be, either in the gateway or the backoffice-service")
    })
    public ResponseEntity<?> get(
            @PathVariable String meta_id
    ) {
    	List<Category> list = new ArrayList<Category>();
		list = (List<Category>) CategoryManager.getCategories("all", null, null).getListOfEntities();

		if (list.isEmpty())
			return ResponseEntity.status(404).body("[]");

		return ResponseEntity
				.status(200)
				.body(list);
    }

    @RequestMapping(
            value = "",
            method = RequestMethod.POST
    )
    @ResponseBody
    @io.swagger.v3.oas.annotations.Operation(summary = "Create Category instance", description = "You can use this endpoint to create a Category instance (more information about which fields are required and how to use it are into the BackOffice repository documentation)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "The Category instance is correctly created.", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "400", description = "Bad request."),
            @ApiResponse(responseCode = "401", description = "Token is missing or invalid"),
            @ApiResponse(responseCode = "404", description = "Not found"),
            @ApiResponse(responseCode = "415", description = "Wrong media type"),
            @ApiResponse(responseCode = "500", description = "Error executing the request, the error may be, either in the gateway or the backoffice-service")
    })
    public ResponseEntity<?> post(
            @RequestBody Category body
    ) {
    	List<Category> list = new ArrayList<Category>();
		list = (List<Category>) CategoryManager.createCategory(body, null, false,false).getListOfEntities();

		if (list.isEmpty())
			return ResponseEntity.status(404).body("[]");

		return ResponseEntity
				.status(200)
				.body(list);
    }

    @RequestMapping(
            value = "",
            method = RequestMethod.PUT
    )
    @ResponseBody
    @io.swagger.v3.oas.annotations.Operation(summary = "Update Category instance", description = "You can use this endpoint to update a Category instance (more information about which fields are required, who has the permission and how to use it are into the BackOffice repository documentation)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "The Category instance is correctly updated.", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "400", description = "Bad request."),
            @ApiResponse(responseCode = "401", description = "Token is missing or invalid"),
            @ApiResponse(responseCode = "404", description = "Not found"),
            @ApiResponse(responseCode = "415", description = "Wrong media type"),
            @ApiResponse(responseCode = "500", description = "Error executing the request, the error may be, either in the gateway or the backoffice-service")
    })
    public ResponseEntity<?> put(
            @RequestBody Category body
    ) {
    	List<Category> list = new ArrayList<Category>();
		list = (List<Category>) CategoryManager.updateCategory(body, null, false,false).getListOfEntities();

		if (list.isEmpty())
			return ResponseEntity.status(404).body("[]");

		return ResponseEntity
				.status(200)
				.body(list);
    }


    @RequestMapping(value = "/{instance_id}",
            method = RequestMethod.DELETE)
    @ResponseBody
    @io.swagger.v3.oas.annotations.Operation(summary = "Delete Category instance", description = "You can use this endpoint to delete a Category instance (more information about which fields are required, who has the permission and how to use it are into the BackOffice repository documentation)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "The Category instance is correctly deleted.", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "400", description = "Bad request."),
            @ApiResponse(responseCode = "401", description = "Token is missing or invalid"),
            @ApiResponse(responseCode = "404", description = "Not found"),
            @ApiResponse(responseCode = "415", description = "Wrong media type"),
            @ApiResponse(responseCode = "500", description = "Error executing the request, the error may be, either in the gateway or the backoffice-service")
    })
    public ResponseEntity<?> delete(
            @PathVariable String instance_id
    ) {
    	if(!CategoryManager.deleteCategory(instance_id, null)) {
    		return ResponseEntity.status(400).body("[]");
    	} else {
    		return ResponseEntity
    				.status(200)
    				.body("[]");
    	}
    }

}