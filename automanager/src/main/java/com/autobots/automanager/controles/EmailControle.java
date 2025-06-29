package com.autobots.automanager.controles;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import com.autobots.automanager.entitades.Email;
import com.autobots.automanager.modelo.AdicionadorLinkEmail;
import com.autobots.automanager.repositorios.RepositorioEmail;

@RestController
@RequestMapping("/email")
public class EmailControle {

    @Autowired
    private RepositorioEmail repositorio;

    @Autowired
    private AdicionadorLinkEmail adicionadorLink;

    @GetMapping("/emails")
    public ResponseEntity<List<Email>> obterEmails() {
        List<Email> emails = repositorio.findAll();
        if (emails.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else {
            adicionadorLink.adicionarLink(emails);
            return new ResponseEntity<>(emails, HttpStatus.OK);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Email> obterEmail(@PathVariable Long id) {
        Email email = repositorio.findById(id).orElse(null);
        if (email == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else {
            adicionadorLink.adicionarLink(email);
            return new ResponseEntity<>(email, HttpStatus.OK);
        }
    }

    @PostMapping("/cadastro")
    public ResponseEntity<?> cadastrarEmail(@RequestBody Email email) {
        if (email.getId() == null) {
            repositorio.save(email);
            return new ResponseEntity<>(HttpStatus.CREATED);
        }
        return new ResponseEntity<>(HttpStatus.CONFLICT);
    }

    @PutMapping("/atualizar")
    public ResponseEntity<?> atualizarEmail(@RequestBody Email atualizacao) {
        Email email = repositorio.findById(atualizacao.getId()).orElse(null);
        if (email != null) {
            email.setEndereco(atualizacao.getEndereco());
            repositorio.save(email);
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @DeleteMapping("/excluir")
    public ResponseEntity<?> excluirEmail(@RequestBody Email exclusao) {
        Email email = repositorio.findById(exclusao.getId()).orElse(null);
        if (email != null) {
            repositorio.delete(email);
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
}
