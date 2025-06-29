package com.autobots.automanager.modelo;

import java.util.List;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.stereotype.Component;

import com.autobots.automanager.controles.DocumentoControle;
import com.autobots.automanager.entitades.Documento;

@Component
public class AdicionadorLinkDocumento implements AdicionadorLink<Documento> {

    @Override
    public void adicionarLink(List<Documento> lista) {
        for (Documento doc : lista) {
            long id = doc.getId();
            Link linkProprio = WebMvcLinkBuilder
                .linkTo(WebMvcLinkBuilder
                    .methodOn(DocumentoControle.class)
                    .obterDocumento(id))
                .withSelfRel();
            doc.add(linkProprio);
        }
    }

    @Override
    public void adicionarLink(Documento objeto) {
        Link linkLista = WebMvcLinkBuilder
            .linkTo(WebMvcLinkBuilder
                .methodOn(DocumentoControle.class)
                .obterDocumentos())
            .withRel("documentos");
        objeto.add(linkLista);
    }
}
