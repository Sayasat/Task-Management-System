package com.programmingtechie.taskmanagementsystem.repository;

import com.programmingtechie.taskmanagementsystem.model.UserImpl;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserImpl, Long> {
    Optional<UserImpl> findByUsername(String username);
}
