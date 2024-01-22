package org.example.service;

import org.example.abstraction.annotation.Service;
import org.example.model.User;
import org.example.repository.UserRepository;

@Service
public class UserService extends CrudServiceImpl<User, Long> {
    public UserService(UserRepository repository) {
        super(repository);
    }
}