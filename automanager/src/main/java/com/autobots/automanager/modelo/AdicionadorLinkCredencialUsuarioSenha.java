package com.autobots.automanager.modelo;

import java.util.List;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.stereotype.Component;

import com.autobots.automanager.controles.CredencialUsuarioSenhaControle;
import com.autobots.automanager.entitades.CredencialUsuarioSenha;

@Component
public class AdicionadorLinkCredencialUsuarioSenha implements AdicionadorLink<CredencialUsuarioSenha> {

    @Override
    public void adicionarLink(List<CredencialUsuarioSenha> lista) {
        for (CredencialUsuarioSenha cred : lista) {
            long id = cred.getId();
            Link linkProprio = WebMvcLinkBuilder
                .linkTo(WebMvcLinkBuilder
                    .methodOn(CredencialUsuarioSenhaControle.class)
                    .obterCredencial(id))
                .withSelfRel();
            cred.add(linkProprio);
        }
    }

    @Override
    public void adicionarLink(CredencialUsuarioSenha objeto) {
        Link linkLista = WebMvcLinkBuilder
            .linkTo(WebMvcLinkBuilder
                .methodOn(CredencialUsuarioSenhaControle.class)
                .obterCredenciais())
            .withRel("credenciais");
        objeto.add(linkLista);
    }
}
