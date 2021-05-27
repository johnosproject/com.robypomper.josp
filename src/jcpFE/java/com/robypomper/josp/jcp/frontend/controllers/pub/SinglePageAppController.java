package com.robypomper.josp.jcp.frontend.controllers.pub;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.FileNotFoundException;
import java.io.InputStream;

@RestController
public class SinglePageAppController {

    @RequestMapping(value = {
            "/objects/**",
            "/service/**",
            "/jcp/**",
            "/stats/**"})
    public ResponseEntity<StreamingResponseBody> export() throws FileNotFoundException {
        InputStream indexPageStream = this.getClass().getClassLoader().getResourceAsStream("public/index.html");

        StreamingResponseBody responseBody = outputStream -> {
            int numberOfBytesToWrite;
            byte[] data = new byte[1024];
            while ((numberOfBytesToWrite = indexPageStream.read(data, 0, data.length)) != -1)
                outputStream.write(data, 0, numberOfBytesToWrite);

            indexPageStream.close();
        };

        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_HTML)
                .body(responseBody);
    }

}