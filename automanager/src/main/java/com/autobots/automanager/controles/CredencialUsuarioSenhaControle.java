package com.autobots.automanager.controles;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import com.autobots.automanager.entitades.CredencialUsuarioSenha;
import com.autobots.automanager.modelo.AdicionadorLinkCredencialUsuarioSenha;
import com.autobots.automanager.repositorios.RepositorioCredencialUsuarioSenha;

@RestController
@RequestMapping("/credencial-usuario")
public class CredencialUsuarioSenhaControle {

    @Autowired
    private RepositorioCredencialUsuarioSenha repositorio;

    @Autowired
    private AdicionadorLinkCredencialUsuarioSenha adicionadorLink;
    
    @Autowired
    private BCryptPasswordEncoder encoder;

    @GetMapping("/credenciais")
    public ResponseEntity<List<CredencialUsuarioSenha>> obterCredenciais() {
        List<CredencialUsuarioSenha> credenciais = repositorio.findAll();
        if (credenciais.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else {
            adicionadorLink.adicionarLink(credenciais);
            return new ResponseEntity<>(credenciais, HttpStatus.OK);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<CredencialUsuarioSenha> obterCredencial(@PathVariable Long id) {
        CredencialUsuarioSenha credencial = repositorio.findById(id).orElse(null);
        if (credencial == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else {
            adicionadorLink.adicionarLink(credencial);
            return new ResponseEntity<>(credencial, HttpStatus.OK);
        }
    }

    @PostMapping("/cadastro")
    public ResponseEntity<?> cadastrarCredencial(@RequestBody CredencialUsuarioSenha credencial) {
        if (credencial.getId() == null) {
            credencial.setSenha(encoder.encode(credencial.getSenha()));
            repositorio.save(credencial);
            return new ResponseEntity<>(HttpStatus.CREATED);
        }
        return new ResponseEntity<>(HttpStatus.CONFLICT);
    }

    @PutMapping("/atualizar")
    public ResponseEntity<?> atualizarCredencial(@RequestBody CredencialUsuarioSenha atualizacao) {
        CredencialUsuarioSenha credencial = repositorio.findById(atualizacao.getId()).orElse(null);
        if (credencial != null) {
            credencial.setNomeUsuario(atualizacao.getNomeUsuario());
            credencial.setSenha(atualizacao.getSenha());
            credencial.setUltimoAcesso(atualizacao.getUltimoAcesso());
            credencial.setInativo(atualizacao.isInativo());
            repositorio.save(credencial);
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @DeleteMapping("/excluir")
    public ResponseEntity<?> excluirCredencial(@RequestBody CredencialUsuarioSenha exclusao) {
        CredencialUsuarioSenha credencial = repositorio.findById(exclusao.getId()).orElse(null);
        if (credencial != null) {
            repositorio.delete(credencial);
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
}
