package de.lernspiel.level.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import de.lernspiel.level.entity.LevelComponent;

public interface LevelComponentRepository extends JpaRepository<LevelComponent, Integer> {

    List<LevelComponent> findByLevelLevelID(Integer levelID);
}