/*******************************************************************************
 * The John Cloud Platform is the set of infrastructure and software required to provide
 * the "cloud" to an IoT EcoSystem, like the John Operating System Platform one.
 * Copyright 2021 Roberto Pompermaier
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
 ******************************************************************************/

package com.robypomper.josp.jcp.jslwebbridge.controllers.internal.status;

import com.robypomper.josp.jcp.base.controllers.ControllerImpl;
import com.robypomper.josp.jcp.defs.jslwebbridge.internal.status.Params20;
import com.robypomper.josp.jcp.defs.jslwebbridge.internal.status.Paths20;
import com.robypomper.josp.jcp.jslwebbridge.services.JSLWebBridgeService;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * JCP JSL Web Bridge - Status 2.0
 */
@RestController(value = Paths20.API_NAME + " " + Paths20.DOCS_NAME)
@Api(tags = Paths20.DOCS_NAME, description = Paths20.DOCS_DESCR)
@Profile("jcp-jsl-web-bridge")
public class Controller20 extends ControllerImpl {

    // Internal vars

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(Controller20.class);
    @Autowired
    private JSLWebBridgeService jslWB;


    // Index methods

    @GetMapping(path = Paths20.FULL_PATH_STATUS, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = Paths20.FULL_PATH_STATUS)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "JCP JSL Web Bridge's status index", response = Params20.Index.class),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 403, message = "Only Admin user can access to this request"),
    })
    public ResponseEntity<Params20.Index> getIndex() {
        return ResponseEntity.ok(new Params20.Index());
    }

}
