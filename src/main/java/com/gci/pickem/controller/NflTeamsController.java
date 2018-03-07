package com.gci.pickem.controller;

import com.gci.pickem.model.NflTeams;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class NflTeamsController {

    @GetMapping("api/v1/team/{code}")
    public NflTeams getTeamByCode(@PathVariable("code") String code) {
        return NflTeams.getTeamByCode(code);
    }
}
