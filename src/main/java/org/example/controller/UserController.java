package org.example.controller;

import org.example.abstraction.annotation.*;
import org.example.model.User;
import org.example.service.UserService;

@RestController
@RequestMapping("/users")
public class UserController extends CrudController<User, Long> {
    public UserController(UserService service) {
        super(service);
    }
}