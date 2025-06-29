package com.autobots.automanager.controles;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import com.autobots.automanager.entitades.Documento;
import com.autobots.automanager.modelo.AdicionadorLinkDocumento;
import com.autobots.automanager.repositorios.RepositorioDocumento;

@RestController
@RequestMapping("/documento")
public class DocumentoControle {

    @Autowired
    private RepositorioDocumento repositorio;

    @Autowired
    private AdicionadorLinkDocumento adicionadorLink;

    @GetMapping("/documentos")
    public ResponseEntity<List<Documento>> obterDocumentos() {
        List<Documento> documentos = repositorio.findAll();
        if (documentos.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else {
            adicionadorLink.adicionarLink(documentos);
            return new ResponseEntity<>(documentos, HttpStatus.OK);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Documento> obterDocumento(@PathVariable Long id) {
        Documento documento = repositorio.findById(id).orElse(null);
        if (documento == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else {
            adicionadorLink.adicionarLink(documento);
            return new ResponseEntity<>(documento, HttpStatus.OK);
        }
    }

    @PostMapping("/cadastro")
    public ResponseEntity<?> cadastrarDocumento(@RequestBody Documento documento) {
        if (documento.getId() == null) {
            repositorio.save(documento);
            return new ResponseEntity<>(HttpStatus.CREATED);
        }
        return new ResponseEntity<>(HttpStatus.CONFLICT);
    }

    @PutMapping("/atualizar")
    public ResponseEntity<?> atualizarDocumento(@RequestBody Documento atualizacao) {
        Documento documento = repositorio.findById(atualizacao.getId()).orElse(null);
        if (documento != null) {
            documento.setNumero(atualizacao.getNumero());
            documento.setTipo(atualizacao.getTipo());
            documento.setDataEmissao(atualizacao.getDataEmissao());
            repositorio.save(documento);
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @DeleteMapping("/excluir")
    public ResponseEntity<?> excluirDocumento(@RequestBody Documento exclusao) {
        Documento documento = repositorio.findById(exclusao.getId()).orElse(null);
        if (documento != null) {
            repositorio.delete(documento);
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
}
