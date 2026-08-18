package de.lernspiel.level.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import de.lernspiel.level.entity.ProgrammingLanguage;

public interface ProgrammingLanguageRepository
        extends JpaRepository<ProgrammingLanguage, Integer> {

    Optional<ProgrammingLanguage> findByLanguageName(String languageName);
}