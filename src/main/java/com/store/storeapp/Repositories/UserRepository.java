package com.store.storeapp.Repositories;

import com.store.storeapp.Models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    @Query(
            "Select u from User u where u.username = :username or u.email = :email"
    )
    Optional<User> findByUsernameOrEmail(String username, String email);

    Optional<User> findById(Long id);

    Optional<User> getUserByUsername(String username);
}
