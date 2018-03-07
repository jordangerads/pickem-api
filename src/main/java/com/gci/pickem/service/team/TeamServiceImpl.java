package com.gci.pickem.service.team;

import com.gci.pickem.repository.TeamRepository;
import com.gci.pickem.data.Team;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TeamServiceImpl implements TeamService {

    private TeamRepository teamRepository;

    @Autowired
    TeamServiceImpl(
        TeamRepository teamRepository
    ) {
        this.teamRepository = teamRepository;
    }

    @Override
    @Transactional
    public void createTeam(Team team) {
        teamRepository.save(team);
    }

    @Override
    public Team findByExternalId(Long externalId) {
        return teamRepository.findByExternalId(externalId);
    }
}
