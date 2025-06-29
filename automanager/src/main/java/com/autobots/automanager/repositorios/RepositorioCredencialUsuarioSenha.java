package com.autobots.automanager.repositorios;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.autobots.automanager.entitades.CredencialUsuarioSenha;

@Repository
public interface RepositorioCredencialUsuarioSenha extends JpaRepository<CredencialUsuarioSenha, Long> {}

