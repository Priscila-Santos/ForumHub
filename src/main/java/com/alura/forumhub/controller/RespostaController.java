package com.alura.forumhub.controller;

import com.alura.forumhub.domain.resposta.*;
import com.alura.forumhub.domain.topico.TopicoRepository;
import com.alura.forumhub.domain.usuario.UsuarioRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/respostas")
public class RespostaController {

    @Autowired
    private RespostaRepository respostaRepository;

    @Autowired
    private TopicoRepository topicoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @PostMapping
    @Transactional
    public ResponseEntity<DadosDetalhamentoResposta> cadastrar(
            @RequestBody @Valid DadosCadastroResposta dados,
            UriComponentsBuilder uriBuilder) {

        var topico = topicoRepository.findById(dados.topicoId())
                .orElseThrow(() -> new RuntimeException("Tópico não encontrado."));
        var autor = usuarioRepository.findById(dados.autorId())
                .orElseThrow(() -> new RuntimeException("Autor não encontrado."));

        var resposta = Resposta.builder()
                .mensagem(dados.mensagem())
                .topico(topico)
                .autor(autor)
                .dataCriacao(LocalDateTime.now())
                .solucao(false)
                .build();

        respostaRepository.save(resposta);

        var uri = uriBuilder.path("/respostas/{id}")
                .buildAndExpand(resposta.getId())
                .toUri();

        var dto = new DadosDetalhamentoResposta(
                resposta.getId(),
                resposta.getMensagem(),
                autor.getNome(),
                topico.getTitulo(),
                resposta.getSolucao(),
                resposta.getDataCriacao().toString()
        );

        return ResponseEntity.created(uri).body(dto);
    }

    @GetMapping
    public ResponseEntity<Page<DadosListagemResposta>> listar(Pageable pageable) {
        var page = respostaRepository.findAll(pageable)
                .map(r -> new DadosListagemResposta(
                        r.getId(),
                        r.getMensagem(),
                        r.getAutor().getNome(),
                        r.getSolucao()
                ));
        return ResponseEntity.ok(page);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DadosDetalhamentoResposta> detalhar(@PathVariable Long id) {
        var resposta = respostaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Resposta não encontrada."));

        var dto = new DadosDetalhamentoResposta(
                resposta.getId(),
                resposta.getMensagem(),
                resposta.getAutor().getNome(),
                resposta.getTopico().getTitulo(),
                resposta.getSolucao(),
                resposta.getDataCriacao().toString()
        );

        return ResponseEntity.ok(dto);
    }

    @PutMapping("/{id}")
    @Transactional
    public ResponseEntity<DadosDetalhamentoResposta> atualizar(
            @PathVariable Long id,
            @RequestBody @Valid DadosAtualizacaoResposta dados) {

        var resposta = respostaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Resposta não encontrada."));

        resposta.setMensagem(dados.mensagem());
        if (dados.solucao() != null) {
            resposta.setSolucao(dados.solucao());
        }

        var dto = new DadosDetalhamentoResposta(
                resposta.getId(),
                resposta.getMensagem(),
                resposta.getAutor().getNome(),
                resposta.getTopico().getTitulo(),
                resposta.getSolucao(),
                resposta.getDataCriacao().toString()
        );

        return ResponseEntity.ok(dto);
    }

    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        if (!respostaRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        respostaRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}