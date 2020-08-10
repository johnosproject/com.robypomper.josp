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

package com.robypomper.josp.jcpfe;

import com.robypomper.java.JavaSSLIgnoreChecks;
import com.robypomper.josp.jsl.objs.JSLRemoteObject;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * JCP API's service main class.
 * <p>
 * This class start a Spring Boot application that provide JCP API services.
 */
@SpringBootApplication
@Configuration
@ComponentScan
public class ServiceApplication {

    private static final boolean DISABLE_SSL_CHECKS = true;

    public static void main(String[] args) {
        JSLRemoteObject r;
        if (DISABLE_SSL_CHECKS)
            try {
                JavaSSLIgnoreChecks.disableSSLChecks(JavaSSLIgnoreChecks.LOCALHOST);
            } catch (JavaSSLIgnoreChecks.JavaSSLIgnoreChecksException e) {
                System.out.println("Can't disable SSL checks for localhost communications, exit.");
                System.exit(-1);
            }

        SpringApplication.run(ServiceApplication.class, args);
    }

}
