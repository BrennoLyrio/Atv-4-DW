package com.autobots.automanager.modelo;

import java.util.List;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.stereotype.Component;

import com.autobots.automanager.controles.CredencialCodigoBarraControle;
import com.autobots.automanager.entitades.CredencialCodigoBarra;

@Component
public class AdicionadorLinkCredencialCodigoBarra implements AdicionadorLink<CredencialCodigoBarra> {

    @Override
    public void adicionarLink(List<CredencialCodigoBarra> lista) {
        for (CredencialCodigoBarra cred : lista) {
            long id = cred.getId();
            Link linkProprio = WebMvcLinkBuilder
                .linkTo(WebMvcLinkBuilder
                    .methodOn(CredencialCodigoBarraControle.class)
                    .obterCredencial(id))
                .withSelfRel();
            cred.add(linkProprio);
        }
    }

    @Override
    public void adicionarLink(CredencialCodigoBarra objeto) {
        Link linkLista = WebMvcLinkBuilder
            .linkTo(WebMvcLinkBuilder
                .methodOn(CredencialCodigoBarraControle.class)
                .obterCredenciais())
            .withRel("credenciais");
        objeto.add(linkLista);
    }
}
