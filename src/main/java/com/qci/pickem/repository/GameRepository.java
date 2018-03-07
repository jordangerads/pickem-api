package com.qci.pickem.repository;

import com.qci.pickem.data.Game;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface GameRepository extends CrudRepository<Game, Long> {

    List<Game> findAllByWeek(Integer week);
}
