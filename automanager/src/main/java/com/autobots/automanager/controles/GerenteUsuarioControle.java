package com.autobots.automanager.controles;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.autobots.automanager.entitades.Usuario;
import com.autobots.automanager.repositorios.RepositorioUsuario;
import com.autobots.automanager.servicos.UsuarioServico;

@RestController
@RequestMapping("/gerente")
@PreAuthorize("hasRole('GERENTE')")
public class GerenteUsuarioControle {

    @Autowired
    private RepositorioUsuario repositorio;

//    @Autowired
//    private UsuarioServico usuarioServico;

    // Ver usuários com perfis permitidos
    @GetMapping("/usuarios-gerenciaveis")
    public ResponseEntity<List<Usuario>> listarGerenciaveis() {
        List<Usuario> usuarios = repositorio.findAll().stream()
            .filter(u -> u.temPerfil("ROLE_CLIENTE") ||
                         u.temPerfil("ROLE_VENDEDOR") ||
                         u.temPerfil("ROLE_GERENTE"))
            .collect(Collectors.toList());

        return usuarios.isEmpty()
            ? new ResponseEntity<>(HttpStatus.NO_CONTENT)
            : new ResponseEntity<>(usuarios, HttpStatus.OK);
    }
    
    @PostMapping("/cadastrar-usuario")
    public ResponseEntity<String> cadastrarGerenciavel(@RequestBody Usuario novoUsuario) {
        boolean permitido = novoUsuario.getPerfis().stream().allMatch(p ->
            p.name().equals("ROLE_CLIENTE") ||
            p.name().equals("ROLE_VENDEDOR") ||
            p.name().equals("ROLE_GERENTE")
        );

        if (!permitido) {
            return new ResponseEntity<>("Gerente só pode cadastrar CLIENTE, VENDEDOR ou GERENTE", HttpStatus.FORBIDDEN);
        }

        repositorio.save(novoUsuario);
        return new ResponseEntity<>("Usuário cadastrado com sucesso", HttpStatus.CREATED);
    }

    // Atualizar nome de qualquer usuário permitido
    @PutMapping("/usuario/atualizar")
    public ResponseEntity<String> atualizarUsuario(@RequestBody Usuario atualizacao) {
        Usuario usuario = repositorio.findById(atualizacao.getId()).orElse(null);

        if (usuario == null) {
            return new ResponseEntity<>("Usuário não encontrado", HttpStatus.NOT_FOUND);
        }

        if (!(usuario.temPerfil("ROLE_CLIENTE") || usuario.temPerfil("ROLE_VENDEDOR") || usuario.temPerfil("ROLE_GERENTE"))) {
            return new ResponseEntity<>("Você não tem permissão para alterar este tipo de usuário", HttpStatus.FORBIDDEN);
        }

        usuario.setNome(atualizacao.getNome());
        repositorio.save(usuario);
        return new ResponseEntity<>("Usuário atualizado com sucesso", HttpStatus.OK);
    }

    // Excluir usuário permitido
    @DeleteMapping("/usuario/excluir/{id}")
    public ResponseEntity<String> excluirUsuario(@PathVariable Long id) {
        Usuario usuario = repositorio.findById(id).orElse(null);

        if (usuario == null) {
            return new ResponseEntity<>("Usuário não encontrado", HttpStatus.NOT_FOUND);
        }

        if (!(usuario.temPerfil("ROLE_CLIENTE") || usuario.temPerfil("ROLE_VENDEDOR") || usuario.temPerfil("ROLE_GERENTE"))) {
            return new ResponseEntity<>("Você não pode excluir este tipo de usuário", HttpStatus.FORBIDDEN);
        }

        repositorio.delete(usuario);
        return new ResponseEntity<>("Usuário excluído com sucesso", HttpStatus.OK);
    }
}
