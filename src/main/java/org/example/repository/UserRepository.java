package org.example.repository;

import lombok.extern.slf4j.Slf4j;
import org.example.abstraction.annotation.Repository;
import org.example.model.Document;
import org.example.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Repository
public class UserRepository implements CrudRepository<User, Long> {
    List<User> users = new ArrayList<>();

    public UserRepository() {
        users.add(User.builder().id(1L).name("Aarohan").email("aarohan.nakarmi@gmail.com").build());
        users.add(User.builder().id(2L).name("Aaron").email("aarohn@gmail.com").build());
        users.add(User.builder().id(3L).name("Guest").email("guest@gmail.com").build());
    }

    @Override
    public List<User> findAll() {
        log.info("Finding all users.");
        return users;
    }

    @Override
    public User findById(Long id) {
        log.info("Finding user by ID: {}", id);
        return users.stream().filter(user -> Objects.equals(user.getId(), id)).findFirst().orElse(null);
    }

    @Override
    public User save(User user) {
        log.info("Adding new document");
        users.add(user);
        return findById(user.getId());
    }

    @Override
    public User update(User updatedUser, Long id) {
        log.info("Updating user");
        Optional<User> user = users.stream()
                .filter(usr -> usr.getId().equals(id))
                .findFirst();

        user.ifPresent(existingDocument -> {
            existingDocument.setName(updatedUser.getName());
            existingDocument.setEmail(updatedUser.getEmail());
        });

        if (user.isEmpty()) {
            log.error("User with ID {} not found for update.", id);
        }

        return user.orElse(null);
    }

    @Override
    public void deleteById(Long id) {
        users.removeIf(user -> user.getId().equals(id));
    }
}