package com.autobots.automanager.controles;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.autobots.automanager.entitades.Servico;
import com.autobots.automanager.modelo.AdicionadorLinkServico;
import com.autobots.automanager.repositorios.RepositorioServico;

@RestController
@RequestMapping("/servico")
public class ServicoControle {

    @Autowired
    private RepositorioServico repositorio;

    @Autowired
    private AdicionadorLinkServico adicionadorLink;
    
    @PreAuthorize("hasAnyRole('GERENTE', 'ADMIN')")
    @GetMapping("/servicos")
    public ResponseEntity<List<Servico>> obterServicos() {
        List<Servico> servicos = repositorio.findAll();
        if (servicos.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else {
            adicionadorLink.adicionarLink(servicos);
            return new ResponseEntity<>(servicos, HttpStatus.OK);
        }
    }
    
    @PreAuthorize("hasAnyRole('GERENTE', 'ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<Servico> obterServico(@PathVariable Long id) {
        Servico servico = repositorio.findById(id).orElse(null);
        if (servico == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else {
            adicionadorLink.adicionarLink(servico);
            return new ResponseEntity<>(servico, HttpStatus.OK);
        }
    }
    
    @PreAuthorize("hasAnyRole('GERENTE', 'ADMIN')")
    @PostMapping("/cadastro")
    public ResponseEntity<?> cadastrarServico(@RequestBody Servico servico) {
        if (servico.getId() == null) {
            repositorio.save(servico);
            return new ResponseEntity<>(HttpStatus.CREATED);
        }
        return new ResponseEntity<>(HttpStatus.CONFLICT);
    }
    
    @PreAuthorize("hasAnyRole('GERENTE', 'ADMIN')")
    @PutMapping("/atualizar")
    public ResponseEntity<?> atualizarServico(@RequestBody Servico atualizacao) {
        Servico servico = repositorio.findById(atualizacao.getId()).orElse(null);
        if (servico != null) {
            servico.setNome(atualizacao.getNome());
            servico.setDescricao(atualizacao.getDescricao());
            servico.setValor(atualizacao.getValor());
            repositorio.save(servico);
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
    
    @PreAuthorize("hasAnyRole('GERENTE', 'ADMIN')")
    @DeleteMapping("/excluir")
    public ResponseEntity<?> excluirServico(@RequestBody Servico exclusao) {
        Servico servico = repositorio.findById(exclusao.getId()).orElse(null);
        if (servico != null) {
            repositorio.delete(servico);
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
}
