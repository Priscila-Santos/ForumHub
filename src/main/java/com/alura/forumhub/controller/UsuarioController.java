package com.alura.forumhub.controller;

import com.alura.forumhub.domain.usuario.*;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("/usuario")
public class UsuarioController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping
    public ResponseEntity<DadosDetalhamentoUsuario> cadastrar(
            @RequestBody @Valid DadosCadastroUsuario dados,
            UriComponentsBuilder uriBuilder) {

        if (usuarioRepository.existsByEmail(dados.email())) {
            return ResponseEntity
                    .badRequest()
                    .body(null);
        }

        var usuario = new Usuario();
        usuario.setNome(dados.nome());
        usuario.setEmail(dados.email());
        usuario.setSenha(passwordEncoder.encode(dados.senha()));
        usuarioRepository.save(usuario);

        var uri = uriBuilder
                .path("/usuario/{id}")
                .buildAndExpand(usuario.getId())
                .toUri();

        var responseBody = new DadosDetalhamentoUsuario(
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail()
        );

        return ResponseEntity
                .created(uri)
                .body(responseBody);
    }

    @GetMapping
    public ResponseEntity<Page<DadosListagemUsuario>> listar(Pageable pageable) {
        var page = usuarioRepository
                .findAll(pageable)
                .map(u -> new DadosListagemUsuario(
                        u.getId(),
                        u.getNome(),
                        u.getEmail()
                ));

        return ResponseEntity.ok(page);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DadosDetalhamentoUsuario> detalhar(@PathVariable Long id) {
        return usuarioRepository.findById(id)
                .map(u -> ResponseEntity.ok(
                        new DadosDetalhamentoUsuario(
                                u.getId(),
                                u.getNome(),
                                u.getEmail()
                        )
                ))
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<DadosDetalhamentoUsuario> atualizar(
            @PathVariable Long id,
            @RequestBody @Valid DadosAtualizacaoUsuario dados) {

        return usuarioRepository.findById(id)
                .map(u -> {
                    u.setNome(dados.nome());
                    u.setSenha(passwordEncoder.encode(dados.senha()));
                    usuarioRepository.save(u);
                    var dto = new DadosDetalhamentoUsuario(
                            u.getId(), u.getNome(), u.getEmail());
                    return ResponseEntity.ok(dto);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        if (!usuarioRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        usuarioRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
