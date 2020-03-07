package com.robypomper.josp.jcp.db.db;

import com.robypomper.josp.jcp.db.entities.Username;
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

    public List<Username> findAll() {
        return usernames.findAll();
    }

    public Optional<Username> findById(Long id) {
        return usernames.findById(id);
    }

    public Username save(Username stock) throws DataIntegrityViolationException {
        return usernames.save(stock);
    }

    public void deleteById(Long id) {
        usernames.deleteById(id);
    }
}
