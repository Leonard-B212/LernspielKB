package de.lernspiel.auth.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/debug")
public class DebugController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @GetMapping("/dbinfo")
    public Map<String, Object> dbInfo() {

        return jdbcTemplate.queryForMap(
            "SELECT DATABASE() as db, USER() as user, @@hostname as host, @@port as port, @@version as version"
        );
    }
}