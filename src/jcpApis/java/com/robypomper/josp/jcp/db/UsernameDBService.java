package com.robypomper.josp.jcp.db;

import com.robypomper.josp.jcp.db.entities.UserName;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UsernameDBService {

    private final UsernameRepository usernames;

    public UsernameDBService(UsernameRepository usernames) {
        this.usernames = usernames;
    }

    public List<UserName> findAll() {
        return usernames.findAll();
    }

    public Optional<UserName> findById(Long id) {
        return usernames.findById(id);
    }

    public UserName save(UserName stock) throws DataIntegrityViolationException {
        return usernames.save(stock);
    }

    public void deleteById(Long id) {
        usernames.deleteById(id);
    }
}
