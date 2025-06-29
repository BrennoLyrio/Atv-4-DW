package com.autobots.automanager.controles;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import com.autobots.automanager.entitades.CredencialCodigoBarra;
import com.autobots.automanager.modelo.AdicionadorLinkCredencialCodigoBarra;
import com.autobots.automanager.repositorios.RepositorioCredencialCodigoBarra;

@RestController
@RequestMapping("/credencial-codigo")
public class CredencialCodigoBarraControle {

    @Autowired
    private RepositorioCredencialCodigoBarra repositorio;

    @Autowired
    private AdicionadorLinkCredencialCodigoBarra adicionadorLink;

    @GetMapping("/credenciais")
    public ResponseEntity<List<CredencialCodigoBarra>> obterCredenciais() {
        List<CredencialCodigoBarra> credenciais = repositorio.findAll();
        if (credenciais.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else {
            adicionadorLink.adicionarLink(credenciais);
            return new ResponseEntity<>(credenciais, HttpStatus.OK);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<CredencialCodigoBarra> obterCredencial(@PathVariable Long id) {
        CredencialCodigoBarra credencial = repositorio.findById(id).orElse(null);
        if (credencial == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else {
            adicionadorLink.adicionarLink(credencial);
            return new ResponseEntity<>(credencial, HttpStatus.OK);
        }
    }

    @PostMapping("/cadastro")
    public ResponseEntity<?> cadastrarCredencial(@RequestBody CredencialCodigoBarra credencial) {
        if (credencial.getId() == null) {
            repositorio.save(credencial);
            return new ResponseEntity<>(HttpStatus.CREATED);
        }
        return new ResponseEntity<>(HttpStatus.CONFLICT);
    }

    @PutMapping("/atualizar")
    public ResponseEntity<?> atualizarCredencial(@RequestBody CredencialCodigoBarra atualizacao) {
        CredencialCodigoBarra credencial = repositorio.findById(atualizacao.getId()).orElse(null);
        if (credencial != null) {
            credencial.setCodigo(atualizacao.getCodigo());
            credencial.setUltimoAcesso(atualizacao.getUltimoAcesso());
            credencial.setInativo(atualizacao.isInativo());
            repositorio.save(credencial);
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @DeleteMapping("/excluir")
    public ResponseEntity<?> excluirCredencial(@RequestBody CredencialCodigoBarra exclusao) {
        CredencialCodigoBarra credencial = repositorio.findById(exclusao.getId()).orElse(null);
        if (credencial != null) {
            repositorio.delete(credencial);
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
}
