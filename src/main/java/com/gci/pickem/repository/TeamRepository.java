package com.gci.pickem.repository;

import com.gci.pickem.data.Team;
import org.springframework.data.repository.CrudRepository;

public interface TeamRepository extends CrudRepository<Team, Long> {

    Team findByExternalId(Long externalId);
}
