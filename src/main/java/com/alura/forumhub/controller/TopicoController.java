package com.alura.forumhub.controller;

import com.alura.forumhub.domain.curso.CursoRepository;
import com.alura.forumhub.domain.resposta.RespostaRepository;
import com.alura.forumhub.domain.topico.*;
import com.alura.forumhub.domain.usuario.UsuarioRepository;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/topicos")
@SecurityRequirement(name = "bearer-key")
@CrossOrigin(origins = "http://localhost:5173") // libera só para o frontend
public class TopicoController {

    @Autowired
    private TopicoRepository topicoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private CursoRepository cursoRepository;

    @Autowired
    private RespostaRepository respostaRepository;

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

    @GetMapping
    public ResponseEntity<Page<DadosListagemTopico>> listar(
            @RequestParam(required = false) String curso,
            @RequestParam(required = false) Integer ano,
            @PageableDefault(size = 10, sort = "dataCriacao") Pageable paginacao
    ) {
        Page<Topico> topicos;

        if (curso != null && ano != null) {
            LocalDateTime inicio = LocalDateTime.of(ano, 1, 1, 0, 0);
            LocalDateTime fim = LocalDateTime.of(ano, 12, 31, 23, 59);
            topicos = topicoRepository.findByCursoNomeAndDataCriacaoBetweenAndAtivoTrue(curso, inicio, fim, paginacao);
        } else {
            topicos = topicoRepository.findByAtivoTrue(paginacao);
        }

        var page = topicos.map(DadosListagemTopico::new);
        return ResponseEntity.ok(page);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DadosDetalhamentoTopico> detalhar(@PathVariable Long id) {
        var topico = topicoRepository.findByIdAndAtivoTrue(id)
                .orElseThrow(() -> new RuntimeException("Tópico não encontrado ou foi excluído."));

        return ResponseEntity.ok(new DadosDetalhamentoTopico(topico));
    }

    @PutMapping("/{id}")
    @Transactional
    public ResponseEntity<DadosDetalhamentoTopico> atualizar(
            @PathVariable Long id,
            @RequestBody @Valid DadosAtualizacaoTopico dados
    ) {
        var topico = topicoRepository.findByIdAndAtivoTrue(id)
                .orElseThrow(() -> new RuntimeException("Tópico não encontrado ou foi excluído."));

        topico.atualizarInformacoes(dados);
        return ResponseEntity.ok(new DadosDetalhamentoTopico(topico));
    }

    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity excluir(@PathVariable Long id) {
        var topico = topicoRepository.findByIdAndAtivoTrue(id)
                .orElseThrow(() -> new RuntimeException("Tópico não encontrado ou já foi excluído."));

        topico.excluir();
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/stats")
    public List<DadosEstatisticosTopicos> stats() {
        LocalDateTime semanaPassada = LocalDateTime.now().minusWeeks(1);
        List<DadosEstatisticosTopicos> lista = new ArrayList<>();

        for (String curso : List.of("Mobile", "BackEnd", "FrontEnd", "UX & Design")) {
            long total     = topicoRepository.countByCursoCategoria(curso);
            long ultSemana = topicoRepository
                    .countByCursoCategoriaAndDataCriacaoAfter(curso, semanaPassada);
            long respostas = respostaRepository.countByTopicoCursoCategoria(curso);

            lista.add(new DadosEstatisticosTopicos(curso, total, ultSemana, respostas));
        }

        return lista;
    }


}
