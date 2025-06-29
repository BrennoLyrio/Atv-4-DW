package com.autobots.automanager.controles;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import com.autobots.automanager.entitades.Endereco;
import com.autobots.automanager.modelo.AdicionadorLinkEndereco;
import com.autobots.automanager.repositorios.RepositorioEndereco;

@RestController
@RequestMapping("/endereco")
public class EnderecoControle {

    @Autowired
    private RepositorioEndereco repositorio;

    @Autowired
    private AdicionadorLinkEndereco adicionadorLink;

    @GetMapping("/enderecos")
    public ResponseEntity<List<Endereco>> obterEnderecos() {
        List<Endereco> enderecos = repositorio.findAll();
        if (enderecos.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else {
            adicionadorLink.adicionarLink(enderecos);
            return new ResponseEntity<>(enderecos, HttpStatus.OK);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Endereco> obterEndereco(@PathVariable Long id) {
        Endereco endereco = repositorio.findById(id).orElse(null);
        if (endereco == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else {
            adicionadorLink.adicionarLink(endereco);
            return new ResponseEntity<>(endereco, HttpStatus.OK);
        }
    }

    @PostMapping("/cadastro")
    public ResponseEntity<?> cadastrarEndereco(@RequestBody Endereco endereco) {
        if (endereco.getId() == null) {
            repositorio.save(endereco);
            return new ResponseEntity<>(HttpStatus.CREATED);
        }
        return new ResponseEntity<>(HttpStatus.CONFLICT);
    }

    @PutMapping("/atualizar")
    public ResponseEntity<?> atualizarEndereco(@RequestBody Endereco atualizacao) {
        Endereco endereco = repositorio.findById(atualizacao.getId()).orElse(null);
        if (endereco != null) {
            endereco.setEstado(atualizacao.getEstado());
            endereco.setCidade(atualizacao.getCidade());
            endereco.setBairro(atualizacao.getBairro());
            endereco.setRua(atualizacao.getRua());
            endereco.setNumero(atualizacao.getNumero());
            endereco.setCodigoPostal(atualizacao.getCodigoPostal());
            repositorio.save(endereco);
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @DeleteMapping("/excluir")
    public ResponseEntity<?> excluirEndereco(@RequestBody Endereco exclusao) {
        Endereco endereco = repositorio.findById(exclusao.getId()).orElse(null);
        if (endereco != null) {
            repositorio.delete(endereco);
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
}
