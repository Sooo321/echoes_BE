package org.example.echoes_be.repository;

import org.example.echoes_be.domain.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<Users, Long> {
    //Users 뒤에 있는 Long은 엔티티의 Primary Key(기본 키, ID)의 타입을 지정
    // JpaRepository<Users, Long>는 Spring Data JPA에서 제공하는 제네릭 인터페이스임.
    //이 인터페이스를 상속받으면, 기본적인 CRUD 메서드들이 자동으로 제공됨. (save(), findById(), delete() 등)

    Optional<Users> findByEmail(String email);
    Optional<Users> findById(Long id);
}
