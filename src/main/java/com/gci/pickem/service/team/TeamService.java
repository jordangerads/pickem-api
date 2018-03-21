package com.gci.pickem.service.team;

import com.gci.pickem.data.Team;

public interface TeamService {

    void createTeam(Team team);

    Team findByExternalId(Long externalId);

    Team findById(Long teamId);
}
