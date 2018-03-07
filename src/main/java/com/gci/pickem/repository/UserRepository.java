package com.gci.pickem.repository;

import com.gci.pickem.data.User;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, Long> {
}
