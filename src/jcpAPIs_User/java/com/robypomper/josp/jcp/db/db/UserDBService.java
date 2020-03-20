package com.robypomper.josp.jcp.db.db;

import com.robypomper.josp.jcp.db.entities.User;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.SessionScope;

import java.util.Optional;


@Service
@SessionScope
public class UserDBService {

    // Internal vars

    private final UserRepository users;

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    private Optional<User> user = Optional.empty();


    // Constructor

    public UserDBService(UserRepository users) {
        this.users = users;
    }


    // Access methods

    public Optional<User> get(String usrId) {
        if (!user.isPresent() || user.get().getUsrId().compareTo(usrId) != 0)
            user = users.findById(usrId);
        return user;
    }

    public User add(User stock) throws DataIntegrityViolationException {
        return users.save(stock);
    }

}
