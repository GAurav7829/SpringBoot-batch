package com.grv.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.grv.model.User;

public interface UserRepository extends JpaRepository<User, Integer> {

}
