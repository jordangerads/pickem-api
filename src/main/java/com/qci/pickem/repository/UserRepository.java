package com.qci.pickem.repository;

import com.qci.pickem.data.User;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, Long> {
}
