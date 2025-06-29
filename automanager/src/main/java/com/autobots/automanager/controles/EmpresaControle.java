package com.autobots.automanager.controles;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.autobots.automanager.entitades.Empresa;
import com.autobots.automanager.modelo.AdicionadorLinkEmpresa;
import com.autobots.automanager.repositorios.RepositorioEmpresa;

@RestController
@RequestMapping("/empresa")
public class EmpresaControle {
	@Autowired
	private RepositorioEmpresa repositorio;
	
	@Autowired
	private AdicionadorLinkEmpresa adicionadorLink;
	
	@GetMapping("/empresas/teste")
	public ResponseEntity<List<Map<String, Object>>> teste() {
	    List<Empresa> empresas = repositorio.findAll();
	    List<Map<String, Object>> lista = new ArrayList<>();
	    for (Empresa emp : empresas) {
	        Map<String, Object> e = new HashMap<>();
	        e.put("id", emp.getId());
	        e.put("razaoSocial", emp.getRazaoSocial());
	        lista.add(e);
	    }
	    return ResponseEntity.ok(lista);
	}
	
	@GetMapping("/empresas")
	public ResponseEntity<List<Empresa>> obterEmpresas() {
	    List<Empresa> empresas = repositorio.findAll();
	    if (empresas.isEmpty()) {
	        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	    } else {
	    	adicionadorLink.adicionarLink(empresas);
	        return new ResponseEntity<>(empresas, HttpStatus.OK); // CORRETO
	    }
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<Empresa> obterEmpresa(@PathVariable Long id) {
		Empresa empresa = repositorio.findById(id).orElse(null);
		if (empresa == null) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else {
            adicionadorLink.adicionarLink(empresa);
            return new ResponseEntity<>(empresa, HttpStatus.OK);
		}
	}
	
	@PostMapping("/cadastro") 
	public ResponseEntity<?> cadastrarEmpresa(@RequestBody Empresa empresa) {
		if (empresa.getId() == null) {
			repositorio.save(empresa);
			return new ResponseEntity<>(HttpStatus.CREATED);
		} else {
			return new ResponseEntity<>(HttpStatus.CONFLICT);
		}
	}
	
	@PutMapping("/atualizar")
	public ResponseEntity<?> atualizarEmpresa(@RequestBody Empresa atualizacao) {
		Empresa empresa = repositorio.findById(atualizacao.getId()).orElse(null);
		if (empresa != null) {
			empresa.setRazaoSocial(atualizacao.getRazaoSocial());
			empresa.setNomeFantasia(atualizacao.getNomeFantasia());
			repositorio.save(empresa);
			return new ResponseEntity<>(HttpStatus.OK);
		} else {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	}
	
	@DeleteMapping("/excluir")
	public ResponseEntity<?> excluirEmpresa(@RequestBody Empresa exclusao) {
		Empresa empresa = repositorio.findById(exclusao.getId()).orElse(null);
		if (empresa != null) {
			repositorio.delete(empresa);
			return new ResponseEntity<>(HttpStatus.OK);
		} else {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	}
}
