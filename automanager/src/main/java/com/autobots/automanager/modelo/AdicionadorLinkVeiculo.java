package com.autobots.automanager.modelo;

import java.util.List;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.stereotype.Component;

import com.autobots.automanager.controles.VeiculoControle;
import com.autobots.automanager.entitades.Veiculo;

@Component
public class AdicionadorLinkVeiculo implements AdicionadorLink<Veiculo>{
	
	@Override
	public void adicionarLink(List<Veiculo> lista) {
		for (Veiculo veiculo : lista) {
			Long id = veiculo.getId();
			Link link = WebMvcLinkBuilder
					.linkTo(WebMvcLinkBuilder
							.methodOn(VeiculoControle.class)
							.obterVeiculo(id))
					.withSelfRel();
			veiculo.add(link);
		}
	}
	
	@Override
    public void adicionarLink(Veiculo objeto) {
        Link link = WebMvcLinkBuilder
                .linkTo(WebMvcLinkBuilder.
                		methodOn(VeiculoControle.class)
                		.obterVeiculos())
                .withRel("veiculos");
        objeto.add(link);
    }

}
