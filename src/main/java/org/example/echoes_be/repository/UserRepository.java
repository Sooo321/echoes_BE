package org.example.echoes_be.repository;

import org.example.echoes_be.domain.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<Users, Long> {


    Optional<Users> findByEmail(String email);
}
