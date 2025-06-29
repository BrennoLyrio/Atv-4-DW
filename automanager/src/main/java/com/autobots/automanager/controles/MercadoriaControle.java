package com.autobots.automanager.controles;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.autobots.automanager.entitades.Mercadoria;
import com.autobots.automanager.modelo.AdicionadorLinkMercadoria;
import com.autobots.automanager.repositorios.RepositorioMercadoria;


@RestController
@RequestMapping("/mercadoria")
public class MercadoriaControle {

    @Autowired
    private RepositorioMercadoria repositorio;

    @Autowired
    private AdicionadorLinkMercadoria adicionadorLink;

    @GetMapping("/mercadorias")
    public ResponseEntity<List<Mercadoria>> obterMercadorias() {
        List<Mercadoria> mercadorias = repositorio.findAll();
        if (mercadorias.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else {
            adicionadorLink.adicionarLink(mercadorias);
            return new ResponseEntity<>(mercadorias, HttpStatus.OK);
        }
    }
    
    @PreAuthorize("hasAnyRole('GERENTE', 'ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<Mercadoria> obterMercadoria(@PathVariable Long id) {
        Mercadoria mercadoria = repositorio.findById(id).orElse(null);
        if (mercadoria == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else {
            adicionadorLink.adicionarLink(mercadoria);
            return new ResponseEntity<>(mercadoria, HttpStatus.OK);
        }
    }
    
    @PreAuthorize("hasAnyRole('GERENTE', 'ADMIN')")
    @PostMapping("/cadastro")
    public ResponseEntity<?> cadastrarMercadoria(@RequestBody Mercadoria mercadoria) {
        if (mercadoria.getId() == null) {
            repositorio.save(mercadoria);
            return new ResponseEntity<>(HttpStatus.CREATED);
        }
        return new ResponseEntity<>(HttpStatus.CONFLICT);
    }
    
    @PreAuthorize("hasAnyRole('GERENTE', 'ADMIN')")
    @PutMapping("/atualizar")
    public ResponseEntity<?> atualizarMercadoria(@RequestBody Mercadoria atualizacao) {
        Mercadoria mercadoria = repositorio.findById(atualizacao.getId()).orElse(null);
        if (mercadoria != null) {
            mercadoria.setNome(atualizacao.getNome());
            mercadoria.setDescricao(atualizacao.getDescricao());
            mercadoria.setValor(atualizacao.getValor());
            mercadoria.setQuantidade(atualizacao.getQuantidade());
            mercadoria.setCadastro(atualizacao.getCadastro());
            mercadoria.setFabricao(atualizacao.getFabricao());
            mercadoria.setValidade(atualizacao.getValidade());
            repositorio.save(mercadoria);
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
    
    @PreAuthorize("hasAnyRole('GERENTE', 'ADMIN')")
    @DeleteMapping("/excluir")
    public ResponseEntity<?> excluirMercadoria(@RequestBody Mercadoria exclusao) {
        Mercadoria mercadoria = repositorio.findById(exclusao.getId()).orElse(null);
        if (mercadoria != null) {
            repositorio.delete(mercadoria);
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
}
