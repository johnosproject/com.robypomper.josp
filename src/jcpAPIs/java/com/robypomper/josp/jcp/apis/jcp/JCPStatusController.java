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

package com.robypomper.josp.jcp.apis.jcp;

import com.robypomper.java.JavaVersionUtils;
import com.robypomper.josp.paths.APIJCPStatus;
import com.robypomper.josp.info.JCPAPIsVersions;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.util.Date;

@Deprecated     // for CloudStatusController
@RestController
@Api(tags = {APIJCPStatus.SubGroupStatus.NAME})
public class JCPStatusController {

    public JCPStatusController() {
        System.out.println(JavaVersionUtils.buildJavaVersionStr("John Cloud Platform", JCPAPIsVersions.VER_JCP_APIs_2_0));
    }

    @GetMapping(path = APIJCPStatus.FULL_PATH_STATUS_PUBLIC)
    @ApiOperation("Public accessible method")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Method worked successfully", response = String.class)
    })
    public String getJCPStatusPublic() {
        return new SimpleDateFormat().format(new Date());
    }

    @GetMapping(path = APIJCPStatus.FULL_PATH_STATUS_FULL)
    @ResponseBody
    @ApiOperation("Public accessible method")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Method worked successfully", response = String.class)
    })
    public String getJCPStatusFull() {
        return JavaVersionUtils.buildJavaVersionStr("John Cloud Platform", JCPAPIsVersions.VER_JCP_APIs_2_0);
    }

}
