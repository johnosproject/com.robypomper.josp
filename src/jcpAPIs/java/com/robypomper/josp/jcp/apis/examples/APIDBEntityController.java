/* *****************************************************************************
 * The John Cloud Platform set of infrastructure and software required to provide
 * the "cloud" to an IoT EcoSystem, like the John Operating System Platform one.
 * Copyright 2020 Roberto Pompermaier
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 **************************************************************************** */

package com.robypomper.josp.jcp.apis.examples;

import com.robypomper.josp.jcp.apis.paths.ExampleAPIs;
import com.robypomper.josp.jcp.db.UsernameDBService;
import com.robypomper.josp.jcp.db.entities.UserName;
import io.swagger.annotations.*;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

//@RestController
//@RequestMapping(JCPAPIsGroups.PATH_EXMPL + "/db")
//@Api(tags = {JCPAPIsGroups.API_EXMPL_SG_DB_NAME})
@RestController
//@RequestMapping("/apis/examples/" + JCPAPIsGroups.VER_TEST_2_0 + "/db")
@RequestMapping(ExampleAPIs.FULL_DB_DB)
@Api(tags = {ExampleAPIs.SubGroupDB.NAME})
public class APIDBEntityController {

    private final UsernameDBService usernamesService;

    public APIDBEntityController(UsernameDBService usernamesService) {
        this.usernamesService = usernamesService;
    }

    @GetMapping
    @ApiOperation("Return all usernames registered in the database")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Full usernames list", response = UserName.class, responseContainer = "List")
    })
    public ResponseEntity<List<UserName>> findAll() {
        return ResponseEntity.ok(usernamesService.findAll());
    }

    @GetMapping("/{id}")
    @ApiOperation("Look for username of specified Id in the database")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Username founded successfully", response = UserName.class),
            @ApiResponse(code = 404, message = "Username with specified id not found")
    })
    public ResponseEntity<UserName> findById(
            @Valid
            @PathVariable
            @ApiParam("The <code>id</code> of the username to be obtained. Cannot be empty.")
                    Long id
    ) {
        Optional<UserName> optUsername = usernamesService.findById(id);
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
    public ResponseEntity<String> getUsername(
            @Valid
            @PathVariable
                    Long id
    ) {
        UserName username = findById(id).getBody();
        if (username == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("Request id ('%s') not found.", id));

        return ResponseEntity.ok(username.getUserName());
    }

    @GetMapping("/add")
    @ApiOperation("Add given username to the database")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Username added successfully", response = UserName.class),
            @ApiResponse(code = 409, message = "Given username not unique")
    })
    public ResponseEntity<UserName> add(
            @Valid
            @RequestParam
                    String username
    ) {
        try {
            UserName usernameEntity = new UserName();
            usernameEntity.setUserName(username);
            return ResponseEntity.ok(usernamesService.save(usernameEntity));
        } catch (DataIntegrityViolationException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMostSpecificCause().getMessage());
        }
    }

    @PostMapping("/add")
    @ApiOperation("Add given username to the database")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Username added successfully", response = UserName.class),
            @ApiResponse(code = 409, message = "Given username not unique")
    })
    public ResponseEntity<UserName> postAdd(
            @Valid
            @RequestBody
                    String username
    ) {
        return add(username);
    }

}
