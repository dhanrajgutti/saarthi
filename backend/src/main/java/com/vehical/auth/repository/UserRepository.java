package com.vehical.auth.repository;

import com.vehical.auth.model.User;
import com.vehical.auth.model.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    Optional<User> findByUsernameAndRole(String username, UserRole role);

    Optional<User> findByEmail(String email);

    List<User> findByRole(UserRole role);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);
}
