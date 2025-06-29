package com.autobots.automanager.controles;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.autobots.automanager.entitades.Usuario;
import com.autobots.automanager.entitades.Venda;
import com.autobots.automanager.repositorios.RepositorioUsuario;
import com.autobots.automanager.repositorios.RepositorioVenda;
import com.autobots.automanager.servicos.UsuarioServico;

@RestController
@RequestMapping("/vendedor")
@PreAuthorize("hasRole('VENDEDOR')")
public class VendedorUsuarioControle {

    @Autowired
    private RepositorioUsuario repositorio;

    @Autowired
    private RepositorioVenda vendaRepositorio;

    @Autowired
    private UsuarioServico usuarioServico;

    // Ver clientes (usuários com perfil CLIENTE)
    @GetMapping("/clientes")
    public ResponseEntity<List<Usuario>> listarClientes() {
        List<Usuario> clientes = repositorio.findAll().stream()
            .filter(u -> u.temPerfil("ROLE_CLIENTE"))
            .toList();

        if (clientes.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<>(clientes, HttpStatus.OK);
    }
    
    @PostMapping("/cadastrar-cliente")
    public ResponseEntity<String> cadastrarCliente(@RequestBody Usuario novoUsuario) {
        boolean apenasCliente = novoUsuario.getPerfis().stream()
            .allMatch(p -> p.name().equals("ROLE_CLIENTE"));

        if (!apenasCliente) {
            return new ResponseEntity<>("Vendedor só pode cadastrar clientes", HttpStatus.FORBIDDEN);
        }

        repositorio.save(novoUsuario);
        return new ResponseEntity<>("Cliente cadastrado com sucesso", HttpStatus.CREATED);
    }

    // Criar uma venda (associada ao vendedor logado)
    @PostMapping("/vendas")
    public ResponseEntity<String> criarVenda(@RequestBody Venda venda, Authentication auth) {
        Usuario vendedor = usuarioServico.buscarPorNomeDeUsuario(auth.getName());

        if (vendedor == null) {
            return new ResponseEntity<>("Vendedor não encontrado", HttpStatus.NOT_FOUND);
        }

        venda.setUsuario(vendedor); // Define o "responsável pela venda"
        vendaRepositorio.save(venda);
        return new ResponseEntity<>("Venda registrada com sucesso", HttpStatus.CREATED);
    }

    // Ver vendas feitas por ele mesmo
    @GetMapping("/minhas-vendas")
    public ResponseEntity<List<Venda>> minhasVendas(Authentication auth) {
        Usuario vendedor = usuarioServico.buscarPorNomeDeUsuario(auth.getName());

        if (vendedor == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        List<Venda> vendas = vendaRepositorio.findAll().stream()
            .filter(v -> v.getUsuario() != null && v.getUsuario().getId().equals(vendedor.getId()))
            .toList();

        return ResponseEntity.ok(vendas);
    }
}
