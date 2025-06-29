package com.autobots.automanager.controles;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.autobots.automanager.entitades.*;
import com.autobots.automanager.modelo.AdicionadorLinkVenda;
import com.autobots.automanager.repositorios.*;

@RestController
@RequestMapping("/venda")
public class VendaControle {

    @Autowired
    private RepositorioVenda repositorio;

    @Autowired
    private RepositorioMercadoria mercadoriaRepo;

    @Autowired
    private RepositorioServico servicoRepo;

    @Autowired
    private RepositorioVeiculo veiculoRepo;

    @Autowired
    private AdicionadorLinkVenda adicionadorLink;
    
    @PreAuthorize("hasAnyRole('GERENTE', 'ADMIN')")
    @GetMapping("/vendas")
    public ResponseEntity<List<Venda>> obterVendas() {
        List<Venda> vendas = repositorio.findAll();
        if (vendas.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else {
            adicionadorLink.adicionarLink(vendas);
            return new ResponseEntity<>(vendas, HttpStatus.OK);
        }
    }
    
    @PreAuthorize("hasAnyRole('GERENTE', 'ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<Venda> obterVenda(@PathVariable Long id) {
        Venda venda = repositorio.findById(id).orElse(null);
        if (venda == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else {
            adicionadorLink.adicionarLink(venda);
            return new ResponseEntity<>(venda, HttpStatus.OK);
        }
    }
    
    @PreAuthorize("hasAnyRole('GERENTE', 'ADMIN')")
    @PostMapping("/cadastro")
    public ResponseEntity<?> cadastrarVenda(@RequestBody Venda venda) {
        // Reassociar veiculo
        if (venda.getVeiculo() != null && venda.getVeiculo().getId() != null) {
            Veiculo veiculo = veiculoRepo.findById(venda.getVeiculo().getId()).orElse(null);
            venda.setVeiculo(veiculo);
        }

        // Reassociar mercadorias
        Set<Mercadoria> mercadoriasCorrigidas = new HashSet<>();
        if (venda.getMercadorias() != null) {
            for (Mercadoria m : venda.getMercadorias()) {
                Mercadoria encontrada = mercadoriaRepo.findById(m.getId()).orElse(null);
                if (encontrada != null) {
                    mercadoriasCorrigidas.add(encontrada);
                }
            }
            venda.setMercadorias(mercadoriasCorrigidas);
        }

        // Reassociar serviços
        Set<Servico> servicosCorrigidos = new HashSet<>();
        if (venda.getServicos() != null) {
            for (Servico s : venda.getServicos()) {
                Servico encontrado = servicoRepo.findById(s.getId()).orElse(null);
                if (encontrado != null) {
                    servicosCorrigidos.add(encontrado);
                }
            }
            venda.setServicos(servicosCorrigidos);
        }

        repositorio.save(venda);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
    
    @PreAuthorize("hasAnyRole('GERENTE', 'ADMIN')")
    @PutMapping("/atualizar")
    public ResponseEntity<?> atualizarVenda(@RequestBody Venda atualizacao) {
        Venda venda = repositorio.findById(atualizacao.getId()).orElse(null);
        if (venda != null) {
            venda.setIdentificacao(atualizacao.getIdentificacao());
            venda.setCadastro(atualizacao.getCadastro());

            // Reaproveita a lógica do POST para reassociar corretamente
            if (atualizacao.getVeiculo() != null && atualizacao.getVeiculo().getId() != null) {
                Veiculo veiculo = veiculoRepo.findById(atualizacao.getVeiculo().getId()).orElse(null);
                venda.setVeiculo(veiculo);
            }

            Set<Mercadoria> mercadoriasCorrigidas = new HashSet<>();
            if (atualizacao.getMercadorias() != null) {
                for (Mercadoria m : atualizacao.getMercadorias()) {
                    Mercadoria encontrada = mercadoriaRepo.findById(m.getId()).orElse(null);
                    if (encontrada != null) {
                        mercadoriasCorrigidas.add(encontrada);
                    }
                }
                venda.setMercadorias(mercadoriasCorrigidas);
            }

            Set<Servico> servicosCorrigidos = new HashSet<>();
            if (atualizacao.getServicos() != null) {
                for (Servico s : atualizacao.getServicos()) {
                    Servico encontrado = servicoRepo.findById(s.getId()).orElse(null);
                    if (encontrado != null) {
                        servicosCorrigidos.add(encontrado);
                    }
                }
                venda.setServicos(servicosCorrigidos);
            }

            repositorio.save(venda);
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
    
    @PreAuthorize("hasAnyRole('GERENTE', 'ADMIN')")
    @DeleteMapping("/excluir")
    public ResponseEntity<?> excluirVenda(@RequestBody Venda exclusao) {
        Venda venda = repositorio.findById(exclusao.getId()).orElse(null);
        if (venda != null) {
            repositorio.delete(venda);
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
}
