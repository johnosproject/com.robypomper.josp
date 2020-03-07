package com.robypomper.josp.jcp.apis;

import com.robypomper.josp.jcp.db.db.UsernameDBService;
import com.robypomper.josp.jcp.db.entities.Username;
import com.robypomper.josp.jcp.info.JCPAPIsGroups;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(JCPAPIsGroups.PATH_EXMPL + "db/")
@Api(tags = {JCPAPIsGroups.API_EXMPL_SG_DB_NAME})
public class APIDBEntityController {

    private final UsernameDBService usernamesService;

    public APIDBEntityController(UsernameDBService usernamesService) {
        this.usernamesService = usernamesService;
    }

    @GetMapping
    @ApiOperation("Return all usernames registered in the database")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Full usernames list", response = Username.class, responseContainer = "List")
    })
    public ResponseEntity<List<Username>> findAll () {
        return ResponseEntity.ok(usernamesService.findAll());
    }

    @GetMapping("/{id}")
    @ApiOperation("Look for username of specified Id in the database")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Username founded successfully", response = Username.class),
            @ApiResponse(code = 404, message = "Username with specified id not found")
    })
    public ResponseEntity<Username> findById (
            @Valid
            @PathVariable
            @ApiParam("The <code>id</code> of the username to be obtained. Cannot be empty.")
                    Long id
    ) {
        Optional<Username> optUsername = usernamesService.findById(id);
        if (!optUsername.isPresent())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("Request id ('%s') not found.", id));

        return ResponseEntity.ok(optUsername.get());
    }

    @GetMapping("/{id}/username")
    @ApiOperation("Return the username string of username object of specified Id")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Username founded successfully", response = String.class),
            @ApiResponse(code = 404, message = "Username with specified id not found")
    })
    public ResponseEntity<String> getUsername (
            @Valid
            @PathVariable
                    Long id
    ) {
        Username username = findById(id).getBody();
        if (username==null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("Request id ('%s') not found.", id));

        return ResponseEntity.ok(username.getUserName());
    }

    @GetMapping("/add")
    @ApiOperation("Add given username to the database")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Username added successfully", response = Username.class),
            @ApiResponse(code = 409, message = "Given username not unique")
    })
    public ResponseEntity<Username> add (
            @Valid
            @RequestParam
                    String username
    ) {
        try {
            Username usernameEntity = new Username();
            usernameEntity.setUserName(username);
            return ResponseEntity.ok(usernamesService.save(usernameEntity));
        } catch (DataIntegrityViolationException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMostSpecificCause().getMessage());
        }
    }

    @PostMapping("/add")
    @ApiOperation("Add given username to the database")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Username added successfully", response = Username.class),
            @ApiResponse(code = 409, message = "Given username not unique")
    })
    public ResponseEntity<Username> postAdd (
            @Valid
            @RequestBody
                    String username
    ) {
        return add(username);
    }

}
