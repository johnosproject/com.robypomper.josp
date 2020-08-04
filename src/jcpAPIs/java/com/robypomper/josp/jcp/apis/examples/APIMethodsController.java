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
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST API class for method examples.
 */
//@RestController
//@RequestMapping(JCPAPIsGroups.PATH_EXMPL)
//@Api(tags = {JCPAPIsGroups.API_EXMPL_SG_METHODS_NAME})
@RestController
@RequestMapping(ExampleAPIs.FULL_METHODS_GENERIC)
@Api(tags = {ExampleAPIs.SubGroupMethods.NAME})
public class APIMethodsController {

    @GetMapping("/str")
    @ApiOperation("Example method that return a string value")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Method worked successfully", response = String.class)
    })
    protected ResponseEntity<String> methodString() {
        return ResponseEntity.ok("String value");
    }

    @GetMapping("/int")
    @ApiOperation("Example method that return a int value")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Method worked successfully", response = Integer.class)
    })
    protected ResponseEntity<Integer> methodInt() {
        return ResponseEntity.ok(42);
    }

}
