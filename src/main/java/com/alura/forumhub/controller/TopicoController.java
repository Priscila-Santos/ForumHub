package com.alura.forumhub.controller;

import com.alura.forumhub.domain.topico.DadosCadastroTopico;
import com.alura.forumhub.domain.topico.DadosDetalhamentoTopico;
import com.alura.forumhub.domain.topico.Topico;
import com.alura.forumhub.domain.topico.TopicoRepository;
import com.alura.forumhub.domain.usuario.UsuarioRepository;
import com.alura.forumhub.domain.curso.CursoRepository;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("/topicos")
@SecurityRequirement(name = "bearer-key")
public class TopicoController {

    @Autowired
    private TopicoRepository topicoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private CursoRepository cursoRepository;

    @PostMapping
    @Transactional
    public ResponseEntity<?> cadastrar(@RequestBody @Valid DadosCadastroTopico dados, UriComponentsBuilder uriBuilder) {

        if (topicoRepository.existsByTituloAndMensagem(dados.titulo(), dados.mensagem())) {
            return ResponseEntity.badRequest().body("Já existe um tópico com esse título e mensagem.");
        }

        var autor = usuarioRepository.findById(dados.autorId())
                .orElseThrow(() -> new RuntimeException("Autor não encontrado."));
        var curso = cursoRepository.findById(dados.cursoId())
                .orElseThrow(() -> new RuntimeException("Curso não encontrado."));

        var topico = new Topico(dados, autor, curso);
        topicoRepository.save(topico);

        var uri = uriBuilder.path("/topicos/{id}").buildAndExpand(topico.getId()).toUri();

        return ResponseEntity.created(uri).body(new DadosDetalhamentoTopico(topico));
    }
}
