package de.lernspiel.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import de.lernspiel.auth.entity.*;
import java.util.Optional;

/*
 * Repositoryschicht:
 * Das Interface JpaRepository stellt wichtige Crud methoden für das DB Handling bereit. 
 * Spezifischere Methoden können zusätzlich definiert werden und werden durch einhalten von Namenskonventionen generiert.
 */

public interface UserRepository extends JpaRepository<User, Integer> {
}
