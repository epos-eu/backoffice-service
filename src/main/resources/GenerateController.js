const fs = require('fs');
///Users/andreaorfino/Documents/dbapiTest/db-api/src/test/resources
const base_path = "/Users/andreaorfino/Documents/comper/docker-compose/backoffice-service/src/main/java/org/epos/api/controller/";
const base_path_test = '/Users/andreaorfino/Documents/dbapiTest/db-api/src/test/java/org/epos/eposdatamodel/';
//console.log(process.argv[2])
//const class_name = process.argv[2]


function capitalizeFirstLetter(string) {
    return string.charAt(0).toUpperCase() + string.slice(1);
}
//, "Contract", "DataProductImplementationStatus", "ServiceImplementationStatus", "Publication"
const names = ["DataProduct", "Distribution", "Equipment", "Facility",
    "Operation", "Organization", "Person", "Service", "ContactPoint",
    "SoftwareApplication", "SoftwareSourceCode", "WebService"];

names.forEach(x => generate(x))

function generate(className) {

    const all_lower = className.toLowerCase();

    let interface_class =
        'package org.epos.api.controller;\n' +
        '\n' +
        'import io.swagger.v3.oas.annotations.Operation;\n' +
        'import io.swagger.v3.oas.annotations.Parameter;\n' +
        'import io.swagger.v3.oas.annotations.enums.ParameterIn;\n' +
        'import io.swagger.v3.oas.annotations.media.Content;\n' +
        'import io.swagger.v3.oas.annotations.media.Schema;\n' +
        'import io.swagger.v3.oas.annotations.responses.ApiResponse;\n' +
        'import io.swagger.v3.oas.annotations.responses.ApiResponses;\n';

    if(className !== 'Operation')
        interface_class = interface_class + 'import org.epos.eposdatamodel_backoffice.' + className + ';\n';

    interface_class = interface_class +
        'import org.epos.eposdatamodel.State;\n' +
        'import org.springframework.http.ResponseEntity;\n' +
        'import org.springframework.validation.annotation.Validated;\n' +
        'import org.springframework.web.bind.annotation.RequestBody;\n' +
        'import org.springframework.web.bind.annotation.RequestMapping;\n' +
        'import org.springframework.web.bind.annotation.RequestMethod;\n' +
        'import org.springframework.web.bind.annotation.RequestParam;\n' +
        '\n' +
        'import javax.validation.Valid;\n' +
        'import javax.validation.constraints.NotNull;\n' +
        'import java.util.List;\n' +
        'import java.util.Map;\n\n' +
        '@Validated\n' +
        'public interface ' + className + 'Api {\n' +
        '\n' +
        '\n' +
        '    @Operation(summary = "Retrive ' + className + 's instance", description = "", tags={ "V2 - ' + className + '" })\n' +
        '    @ApiResponses(value = {\n' +
        '            @ApiResponse(responseCode = "200", description = "ok.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ' + (className !== 'Operation' ? className : 'org.epos.eposdatamodel_backoffice.Operation') + '.class))),\n' +
        '\n' +
        '            @ApiResponse(responseCode = "204", description = "No DataProduct found."),\n' +
        '\n' +
        '            @ApiResponse(responseCode = "301", description = "Moved Permanently."),\n' +
        '\n' +
        '            @ApiResponse(responseCode = "400", description = "Bad request."),\n' +
        '\n' +
        '            @ApiResponse(responseCode = "401", description = "Token is missing or invalid"),\n' +
        '\n' +
        '            @ApiResponse(responseCode = "403", description = "Forbidden"),\n' +
        '\n' +
        '            @ApiResponse(responseCode = "404", description = "Not Found"),\n' +
        '\n' +
        '            @ApiResponse(responseCode = "405", description = "Invalid input"),\n' +
        '\n' +
        '            @ApiResponse(responseCode = "500", description = "Internal Server Error"),\n' +
        '\n' +
        '            @ApiResponse(responseCode = "501", description = "Not Implemented") })\n' +
        '    @RequestMapping(value = "/' + all_lower + '",\n' +
        '            produces = { "application/json" },\n' +
        '            method = RequestMethod.GET)\n' +
        '    public ResponseEntity<'+(className !== 'Operation' ? className : 'org.epos.eposdatamodel_backoffice.Operation')+'> '+className+'GetInstance(@RequestParam Map<String,String> allRequestParams);\n' +
        '\n' +
        '}\n';

    let controller_class =
        'package org.epos.api.controller;\n' +
        '\n' +
        'import com.fasterxml.jackson.core.JsonProcessingException;\n' +
        'import com.fasterxml.jackson.databind.ObjectMapper;\n' +
        'import io.swagger.annotations.Api;\n' +
        'import io.swagger.v3.oas.annotations.Parameter;\n' +
        'import io.swagger.v3.oas.annotations.enums.ParameterIn;\n' +
        'import io.swagger.v3.oas.annotations.media.Schema;\n' +
        'import org.epos.eposdatamodel_backoffice.' + className + ';\n' +
        'import org.epos.eposdatamodel.State;\n' +
        'import org.epos.api.model.User;\n' +
        'import org.epos.exception.RouterException;\n' +
        'import org.slf4j.Logger;\n' +
        'import org.slf4j.LoggerFactory;\n' +
        'import org.springframework.http.HttpStatus;\n' +
        'import org.springframework.http.ResponseEntity;\n' +
        'import org.epos.handler.dbapi.EPOSDataModelDBAPI;\n' +
        'import org.springframework.web.bind.annotation.RequestBody;\n' +
        'import org.springframework.web.bind.annotation.RequestParam;\n' +
        'import org.springframework.web.bind.annotation.RestController;\n' +
        '\n' +
        'import javax.servlet.http.HttpServletRequest;\n' +
        'import javax.validation.Valid;\n' +
        'import javax.validation.constraints.NotNull;\n' +
        'import java.util.List;\n' +
        'import java.util.Map;\n\n' +
        '@RestController\n' +
        '@Api(tags={ "V2 - ' + className + '" })\n' +
        'public class ' + className + 'ApiController extends AbstractController implements ' + className + 'Api {\n' +
        '\n' +
        '    private static final Logger log = LoggerFactory.getLogger(' + className + 'ApiController.class);\n' +
        '    protected final ObjectMapper objectMapper;\n' +
        '    private final HttpServletRequest request;\n' +
        '\n' +
        '    @org.springframework.beans.factory.annotation.Autowired\n' +
        '    public ' + className + 'ApiController(ObjectMapper objectMapper, HttpServletRequest request) {\n' +
        '        this.objectMapper = objectMapper;\n' +
        '        this.request = request;\n' +
        '    }\n' +
        '\n' +
        '\n' +
        '    @Override\n' +
        '    public ResponseEntity<'+(className !== 'Operation' ? className : 'org.epos.eposdatamodel_backoffice.Operation')+'> '+className+'GetInstance(@RequestParam Map<String,String> allRequestParams) {\n' +
        '        String accept = request.getHeader("Accept");\n' +
        '        String contentType = request.getHeader("Content-Type");\n' +
        '\n' +
        '        User user;\n' +
        '        try {\n' +
        '            user = buildUser(allRequestParams);\n' +
        '        } catch (NullPointerException e){\n' +
        '            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);\n' +
        '        }\n' +
        '\n' +
        '        user.signUp();\n' +
        '\n' +
        '        if(!allRequestParams.containsKey("instanceid"))\n' +
        '            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);\n' +
        '\n' +
        '        '+(className !== 'Operation' ? className : 'org.epos.eposdatamodel_backoffice.Operation')+' object = ('+(className !== 'Operation' ? className : 'org.epos.eposdatamodel_backoffice.Operation')+') EPOSDataModelDBAPI.getDBAPIByEDMname("'+className+'").getByInstanceId(allRequestParams.get("instanceid"));\n' +
        '\n' +
        '\n' +
        '        if (object == null) new ResponseEntity<>(HttpStatus.NOT_FOUND);\n' +
        '        return new ResponseEntity<>(object,HttpStatus.OK);'+
        '\n}\n}';


    fs.writeFileSync(base_path + className + "Api.java", interface_class);
    fs.writeFileSync(base_path + className + "ApiController.java", controller_class);

}

