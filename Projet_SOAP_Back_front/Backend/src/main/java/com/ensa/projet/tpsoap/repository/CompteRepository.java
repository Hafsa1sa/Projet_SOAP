package com.ensa.projet.tpsoap.repository;

import com.ensa.projet.tpsoap.entities.Compte;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CompteRepository extends JpaRepository<Compte, Long> {
}
