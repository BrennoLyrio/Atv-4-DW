package com.autobots.automanager.modelo;

import java.util.List;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.stereotype.Component;

import com.autobots.automanager.controles.MercadoriaControle;
import com.autobots.automanager.entitades.Mercadoria;

@Component
public class AdicionadorLinkMercadoria implements AdicionadorLink<Mercadoria> {
	
	@Override
	public void adicionarLink(List<Mercadoria> lista) {
		for (Mercadoria mercadoria : lista ) {
			long id = mercadoria.getId();
			Link link = WebMvcLinkBuilder
					.linkTo(WebMvcLinkBuilder.methodOn(MercadoriaControle.class)
			                .obterMercadoria(id))
	                .withSelfRel();
	            mercadoria.add(link);
		}
	}
	
	@Override
    public void adicionarLink(Mercadoria objeto) {
        Link link = WebMvcLinkBuilder
            .linkTo(WebMvcLinkBuilder.methodOn(MercadoriaControle.class)
            .obterMercadorias())
            .withRel("mercadorias");
        objeto.add(link);
    }

}
