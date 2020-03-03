package com.robypomper.josp.jcp.apis;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/apis/")
public class APIMethodsController {

    @GetMapping("/str")
    protected ResponseEntity<String> methodString() {
        return ResponseEntity.ok("String value");
    }

    @GetMapping("/int")
    protected ResponseEntity<Integer> methodInt() {
        return ResponseEntity.ok(42);
    }

}
