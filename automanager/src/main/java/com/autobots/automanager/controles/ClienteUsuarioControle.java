package com.autobots.automanager.controles;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.autobots.automanager.entitades.CredencialUsuarioSenha;
import com.autobots.automanager.entitades.Usuario;
import com.autobots.automanager.entitades.Venda;
import com.autobots.automanager.modelo.AdicionadorLinkUsuario;
import com.autobots.automanager.repositorios.RepositorioUsuario;
import com.autobots.automanager.repositorios.RepositorioVenda;

@RestController
@RequestMapping("/cliente")
@PreAuthorize("hasRole('CLIENTE')")
public class ClienteUsuarioControle {

    @Autowired
    private RepositorioUsuario repositorio;

    @Autowired
    private AdicionadorLinkUsuario adicionadorLink;

    @Autowired
    private RepositorioVenda vendaRepositorio;

    // Ver o próprio cadastro
    @GetMapping("/meu-cadastro")
    public ResponseEntity<Usuario> verCadastro(Authentication auth) {
        String nomeUsuario = auth.getName();

        Usuario usuario = repositorio.findAll().stream()
            .filter(u -> u.getCredenciais().stream()
                .anyMatch(c -> c instanceof CredencialUsuarioSenha &&
                               ((CredencialUsuarioSenha) c).getNomeUsuario().equals(nomeUsuario)))
            .findFirst().orElse(null);

        if (usuario == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        adicionadorLink.adicionarLink(usuario);
        return ResponseEntity.ok(usuario);
    }

    // Atualizar o próprio nome
    @PutMapping("/atualizar")
    public ResponseEntity<?> atualizarCadastro(@RequestBody Usuario atualizacao, Authentication auth) {
        String nomeUsuario = auth.getName();
        Usuario usuario = repositorio.findById(atualizacao.getId()).orElse(null);

        if (usuario == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        boolean ehProprioCadastro = usuario.getCredenciais().stream()
            .anyMatch(c -> c instanceof CredencialUsuarioSenha &&
                           ((CredencialUsuarioSenha) c).getNomeUsuario().equals(nomeUsuario));

        if (!ehProprioCadastro) {
            return new ResponseEntity<>("Você só pode atualizar seu próprio cadastro.", HttpStatus.FORBIDDEN);
        }

        usuario.setNome(atualizacao.getNome());
        repositorio.save(usuario);
        return new ResponseEntity<>("Cadastro atualizado com sucesso.", HttpStatus.OK);
    }

    // Ver as próprias vendas
    @GetMapping("/minhas-vendas")
    public ResponseEntity<List<Venda>> minhasVendas(Authentication auth) {
        String nomeUsuario = auth.getName();

        Usuario cliente = repositorio.findAll().stream()
            .filter(u -> u.getCredenciais().stream()
                .anyMatch(c -> c instanceof CredencialUsuarioSenha &&
                               ((CredencialUsuarioSenha) c).getNomeUsuario().equals(nomeUsuario)))
            .findFirst().orElse(null);

        if (cliente == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        List<Venda> vendas = vendaRepositorio.findByCliente(cliente);
        return ResponseEntity.ok(vendas);
    }
}
