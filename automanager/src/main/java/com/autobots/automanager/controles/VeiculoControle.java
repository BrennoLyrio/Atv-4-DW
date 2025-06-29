package com.autobots.automanager.controles;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.autobots.automanager.entitades.Usuario;
import com.autobots.automanager.entitades.Veiculo;
import com.autobots.automanager.modelo.AdicionadorLinkVeiculo;
import com.autobots.automanager.repositorios.RepositorioUsuario;
import com.autobots.automanager.repositorios.RepositorioVeiculo;

@RestController
@RequestMapping("/veiculo")
public class VeiculoControle {
	
	@Autowired
	private RepositorioVeiculo repositorio;
	
	@Autowired
	private RepositorioUsuario repositorioUsuario;
	
	@Autowired
	private AdicionadorLinkVeiculo adicionadorLink;
	
	@GetMapping("/veiculos")
	public ResponseEntity<List<Veiculo>> obterVeiculos() {
		List<Veiculo> veiculos = repositorio.findAll();
		if (veiculos.isEmpty()) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		} else {
			adicionadorLink.adicionarLink(veiculos);
			return new ResponseEntity<>(veiculos, HttpStatus.OK);
		}
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<Veiculo> obterVeiculo(@PathVariable Long id) {
		Veiculo veiculo = repositorio.findById(id).orElse(null);
		if (veiculo == null) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		} else {
			adicionadorLink.adicionarLink(veiculo);
			return new ResponseEntity<>(veiculo, HttpStatus.OK);
		}
	}
	
	@PostMapping("/cadastro/{usuarioId}")
	public ResponseEntity<?> cadastrarVeiculo(@PathVariable Long usuarioId, @RequestBody Veiculo veiculo) {
		Usuario usuario = repositorioUsuario.findById(usuarioId).orElse(null);
		if (usuario != null ) {
			veiculo.setProprietario(usuario);
			repositorio.save(veiculo);
			return new ResponseEntity<>(HttpStatus.CREATED);
		} else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}
	
	@PutMapping("/atualizar")
    public ResponseEntity<?> atualizarVeiculo(@RequestBody Veiculo atualizacao) {
        Veiculo veiculo = repositorio.findById(atualizacao.getId()).orElse(null);
        if (veiculo != null) {
            veiculo.setModelo(atualizacao.getModelo());
            veiculo.setPlaca(atualizacao.getPlaca());
            veiculo.setTipo(atualizacao.getTipo());
            repositorio.save(veiculo);
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @DeleteMapping("/excluir")
    public ResponseEntity<?> excluirVeiculo(@RequestBody Veiculo exclusao) {
        Veiculo veiculo = repositorio.findById(exclusao.getId()).orElse(null);
        if (veiculo != null) {
            repositorio.delete(veiculo);
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
	

}
