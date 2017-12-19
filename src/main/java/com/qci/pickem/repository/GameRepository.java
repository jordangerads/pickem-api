package com.qci.pickem.repository;

import com.qci.pickem.data.Game;
import org.springframework.data.repository.CrudRepository;

public interface GameRepository extends CrudRepository<Game, Long> {
}
