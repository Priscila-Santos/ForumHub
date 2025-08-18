package com.alura.forumhub.controller;

import com.alura.forumhub.domain.usuario.DadosAutenticacao;
import com.alura.forumhub.domain.usuario.Usuario;
import com.alura.forumhub.infra.security.DadosTokenJWT;
import com.alura.forumhub.infra.security.TokenService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/login")
@CrossOrigin(origins = "http://localhost:5173") // libera só para o frontend
public class AutenticacaoController {

    @Autowired
    private AuthenticationManager manager;

    @Autowired
    private TokenService tokenService;

    @PostMapping
    public ResponseEntity efetuarLogin(@RequestBody @Valid DadosAutenticacao dados) {
        try {
            var authToken = new UsernamePasswordAuthenticationToken(dados.email(), dados.senha());
            var authentication = manager.authenticate(authToken);
            var tokenJWT = tokenService.gerarToken((Usuario) authentication.getPrincipal());
            return ResponseEntity.ok(new DadosTokenJWT(tokenJWT));
        } catch (Exception e) {
            return ResponseEntity.status(403).body("Erro: " + e.getMessage());
        }
    }

}
