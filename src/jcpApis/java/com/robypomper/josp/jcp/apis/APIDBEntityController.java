package com.robypomper.josp.jcp.apis;

import com.robypomper.josp.jcp.db.db.UsernameDBService;
import com.robypomper.josp.jcp.db.entities.Username;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/apis/db")
public class APIDBEntityController {

    private final UsernameDBService usernamesService;

    public APIDBEntityController(UsernameDBService usernamesService) {
        this.usernamesService = usernamesService;
    }

    @GetMapping
    public ResponseEntity<List<Username>> findAll() {
        return ResponseEntity.ok(usernamesService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Username> findById(@Valid @PathVariable Long id) {
        Optional<Username> optUsername = usernamesService.findById(id);
        if (!optUsername.isPresent())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Request id ('%s') not found.");
        return ResponseEntity.ok(optUsername.get());
    }

    @GetMapping("/add")
    public ResponseEntity<Username> add(@Valid String username) {
        Username usernameEntity = new Username();
        usernameEntity.setUserName(username);
        return ResponseEntity.ok(usernamesService.save(usernameEntity));
    }

    @GetMapping("/{id}/username")
    public ResponseEntity<String> getUsername(@Valid @PathVariable Long id) {
        return ResponseEntity.ok(findById(id).getBody().getUserName());
    }

}
