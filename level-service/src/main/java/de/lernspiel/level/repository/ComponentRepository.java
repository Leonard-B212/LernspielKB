package de.lernspiel.level.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import de.lernspiel.common.code.CodeType;
import de.lernspiel.level.entity.Component;

public interface ComponentRepository extends JpaRepository<Component, Integer> {

    Optional<Component> findByComponentType(CodeType componentType);
}