package com.autobots.automanager.controles;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.autobots.automanager.entitades.CredencialCodigoBarra;
import com.autobots.automanager.entitades.CredencialUsuarioSenha;
import com.autobots.automanager.entitades.Empresa;
import com.autobots.automanager.entitades.Usuario;
import com.autobots.automanager.modelo.AdicionadorLinkUsuario;
import com.autobots.automanager.repositorios.RepositorioCredencialCodigoBarra;
import com.autobots.automanager.repositorios.RepositorioCredencialUsuarioSenha;
import com.autobots.automanager.repositorios.RepositorioEmpresa;
import com.autobots.automanager.repositorios.RepositorioUsuario;

//Acesso pleno do Administrador

@RestController
@RequestMapping("/usuario")
public class UsuarioControle {
	@Autowired
	private RepositorioUsuario repositorio;
	
	@Autowired
	private RepositorioEmpresa repositorioEmpresa;
	
	@Autowired
	private AdicionadorLinkUsuario adicionadorLink;
	
	@Autowired
	private RepositorioCredencialUsuarioSenha repoCredencialSenha;

	@Autowired
	private RepositorioCredencialCodigoBarra repoCredencialCodigo;
	
	@Autowired
	private BCryptPasswordEncoder encoder;
	
	//PRIMEIRO ACESSO PARA CADASTRAR COMO ADM
	@PostMapping("/publico/cadastro")
	public ResponseEntity<?> cadastroPublico(@RequestBody Usuario usuario) {
	    // Codifica a senha caso tenha credencial de usuário e senha
	    usuario.getCredenciais().forEach(credencial -> {
	        if (credencial instanceof CredencialUsuarioSenha) {
	            CredencialUsuarioSenha cs = (CredencialUsuarioSenha) credencial;
	            cs.setSenha(encoder.encode(cs.getSenha()));
	        }
	    });

	    repositorio.save(usuario);
	    return new ResponseEntity<>("Usuário cadastrado com sucesso.", HttpStatus.CREATED);
	}
	

	@PreAuthorize("hasAnyRole('ADMIN')")
	@GetMapping("/usuarios")
	public ResponseEntity<List<Usuario>> obterUsuarios() {
		List<Usuario> usuarios = repositorio.findAll(); 
		if (usuarios.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else {
            adicionadorLink.adicionarLink(usuarios);
            return new ResponseEntity<>(usuarios, HttpStatus.OK);
        }
	}
	
	@PreAuthorize("hasAnyRole('ADMIN')")
	@GetMapping("/{id}")
	public ResponseEntity<Usuario> obterUsuario(@PathVariable Long id) {
		Usuario usuario = repositorio.findById(id).orElse(null);
		if (usuario == null) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		} else {
			adicionadorLink.adicionarLink(usuario);
			return new ResponseEntity<>(usuario, HttpStatus.OK);
		}
	}
	
	@PreAuthorize("hasAnyRole('ADMIN')")
	@PostMapping("/cadastro/{empresaId}")
    public ResponseEntity<?> cadastrarUsuario(@PathVariable Long empresaId, @RequestBody Usuario usuario) {
        Empresa empresa = repositorioEmpresa.findById(empresaId).orElse(null);
        if (empresa != null) {
//            usuario.setEmpresa(empresa);
//            repositorio.save(usuario);
//            return new ResponseEntity<>(HttpStatus.CREATED);
        	
        	// Associa a empresa
            usuario.setEmpresa(empresa);

            // Codifica a senha se houver CredencialUsuarioSenha
            usuario.getCredenciais().forEach(credencial -> {
                if (credencial instanceof CredencialUsuarioSenha) {
                    CredencialUsuarioSenha cs = (CredencialUsuarioSenha) credencial;
                    cs.setSenha(encoder.encode(cs.getSenha()));
                }
            });

            repositorio.save(usuario);
            return new ResponseEntity<>(HttpStatus.CREATED);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
	
	@PreAuthorize("hasAnyRole('ADMIN')")
	@PutMapping("/atualizar")
    public ResponseEntity<?> atualizarUsuario(@RequestBody Usuario atualizacao) {
        Usuario usuario = repositorio.findById(atualizacao.getId()).orElse(null);
        if (usuario != null) {
            usuario.setNome(atualizacao.getNome());
            repositorio.save(usuario);
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
	
	@PreAuthorize("hasAnyRole('ADMIN')")
	@DeleteMapping("/excluir")
    public ResponseEntity<?> excluirUsuario(@RequestBody Usuario exclusao) {
        Usuario usuario = repositorio.findById(exclusao.getId()).orElse(null);
        if (usuario != null) {
            repositorio.delete(usuario);
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
	
	//Para associar um Usuario a sua credencial, criar coisas separadas e depois são associadas
	
	@PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
	@PostMapping("/{id}/adicionar/credencial-senha")
	public ResponseEntity<?> adicionarCredencialSenha(@PathVariable Long id, @RequestBody CredencialUsuarioSenha credencial) {
	    Usuario usuario = repositorio.findById(id).orElse(null);
	    if (usuario == null) {
	        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	    }

	    CredencialUsuarioSenha credExistente = repoCredencialSenha.findById(credencial.getId()).orElse(null);
	    if (credExistente == null) {
	        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	    }

	    usuario.getCredenciais().add(credExistente);
	    repositorio.save(usuario);
	    return new ResponseEntity<>(HttpStatus.OK);
	}
	
	@PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
	@PostMapping("/{id}/adicionar/credencial-codigo")
	public ResponseEntity<?> adicionarCredencialCodigo(@PathVariable Long id, @RequestBody CredencialCodigoBarra credencial) {
	    Usuario usuario = repositorio.findById(id).orElse(null);
	    if (usuario == null) {
	        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	    }

	    CredencialCodigoBarra credExistente = repoCredencialCodigo.findById(credencial.getId()).orElse(null);
	    if (credExistente == null) {
	        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	    }

	    usuario.getCredenciais().add(credExistente);
	    repositorio.save(usuario);
	    return new ResponseEntity<>(HttpStatus.OK);
	}
}


