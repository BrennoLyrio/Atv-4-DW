package com.autobots.automanager.servicos;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.autobots.automanager.entitades.Credencial;
import com.autobots.automanager.entitades.CredencialUsuarioSenha;
import com.autobots.automanager.entitades.Usuario;
import com.autobots.automanager.repositorios.RepositorioUsuario;

@Service
public class UsuarioServico {

    @Autowired
    private RepositorioUsuario repositorio;

    public Usuario buscarPorNomeDeUsuario(String nomeUsuario) {
        Optional<Usuario> usuario = repositorio.findAll().stream()
            .filter(u -> u.getCredenciais().stream()
                .anyMatch(c -> c instanceof CredencialUsuarioSenha &&
                               ((CredencialUsuarioSenha) c).getNomeUsuario().equals(nomeUsuario)))
            .findFirst();

        return usuario.orElse(null);
    }
}
