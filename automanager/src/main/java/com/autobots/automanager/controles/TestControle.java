package com.autobots.automanager.controles;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/teste")
public class TestControle {
    
    @GetMapping("/hello")
    public String hello() {
        return "API está funcionando!";
    }
    
    @GetMapping("/status")
    public String status() {
        return "Servidor rodando na porta 8080";
    }
}