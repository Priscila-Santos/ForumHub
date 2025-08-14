package com.alura.forumhub.controller;

import com.alura.forumhub.domain.curso.Curso;
import com.alura.forumhub.domain.curso.CursoRepository;
import com.alura.forumhub.domain.topico.*;
import com.alura.forumhub.domain.usuario.Usuario;
import com.alura.forumhub.domain.usuario.UsuarioRepository;
import com.alura.forumhub.infra.security.TokenService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.*;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = TopicoController.class)
@AutoConfigureMockMvc(addFilters = false)
class TopicoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TopicoRepository topicoRepository;

    @MockitoBean
    private UsuarioRepository usuarioRepository;

    @MockitoBean
    private CursoRepository cursoRepository;

    @MockitoBean
    private TokenService tokenService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @DisplayName("POST /topicos - sucesso na criacao")
    void deveCriarTopico() throws Exception {
        var dados = new DadosCadastroTopico("Java 101", "Intro a Java", 1L, 2L);
        var autor = new Usuario(1L, "Alice", "alice@mail.com", "senha");
        var curso = new Curso(2L, "Spring Boot", "Backend");

        when(topicoRepository.existsByTituloAndMensagem(dados.titulo(), dados.mensagem()))
                .thenReturn(false);
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(autor));
        when(cursoRepository.findById(2L)).thenReturn(Optional.of(curso));
        when(topicoRepository.save(any(Topico.class))).thenAnswer(inv -> {
            var t = inv.getArgument(0, Topico.class);
            t.setId(100L);
            return t;
        });

        mockMvc.perform(post("/topicos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dados)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/topicos/100"))
                .andExpect(jsonPath("$.id").value(100))
                .andExpect(jsonPath("$.titulo").value("Java 101"));
    }

    @Test
    @DisplayName("POST /topicos - falha quando duplicado")
    void deveRetornarBadRequestAoCriarTopicoDuplicado() throws Exception {
        var dados = new DadosCadastroTopico("X", "Y", 1L, 2L);
        when(topicoRepository.existsByTituloAndMensagem("X", "Y")).thenReturn(true);

        mockMvc.perform(post("/topicos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dados)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Já existe um tópico com esse título e mensagem."));
    }

    @Test
    @DisplayName("GET /topicos - lista ativos sem filtros")
    void deveListarTopicosAtivos() throws Exception {
        var autor = new Usuario(1L, "Bob", "bob@mail.com", "123");
        var curso = new Curso(1L, "Java", "Backend");
        var topico = new Topico(new DadosCadastroTopico("T", "M", 1L, 1L), autor, curso);
        topico.setId(5L);

        Page<Topico> page = new PageImpl<>(
                List.of(topico),
                PageRequest.of(0, 10, Sort.by("dataCriacao")),
                1
        );
        when(topicoRepository.findByAtivoTrue(PageRequest.of(0, 10, Sort.by("dataCriacao"))))
                .thenReturn(page);

        mockMvc.perform(get("/topicos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].titulo").value("T"))
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    @DisplayName("GET /topicos/{id} - detalha tópico existente")
    void deveDetalharTopico() throws Exception {
        var autor = new Usuario(2L, "Carol", "carol@mail.com", "abc");
        var curso = new Curso(3L, "Docker", "Infra");
        var topico = new Topico(new DadosCadastroTopico("D", "Desc", 2L, 3L), autor, curso);
        topico.setId(77L);

        when(topicoRepository.findByIdAndAtivoTrue(77L))
                .thenReturn(Optional.of(topico));

        mockMvc.perform(get("/topicos/77"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(77))
                .andExpect(jsonPath("$.titulo").value("D"));
    }

    @Test
    @DisplayName("PUT /topicos/{id} - atualiza informacoes")
    void deveAtualizarTopico() throws Exception {
        var autor = new Usuario(5L, "Dave", "dave@mail.com", "pwd");
        var curso = new Curso(5L, "Kubernetes", "Infra");
        var topico = new Topico(new DadosCadastroTopico("Old", "Msg", 5L, 5L), autor, curso);
        topico.setId(500L);

        var dadosAtualizacao = new DadosAtualizacaoTopico("New Title", "New Message");
        when(topicoRepository.findByIdAndAtivoTrue(500L))
                .thenReturn(Optional.of(topico));

        mockMvc.perform(put("/topicos/500")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dadosAtualizacao)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.titulo").value("New Title"))
                .andExpect(jsonPath("$.mensagem").value("New Message"));
    }

    @Test
    @DisplayName("DELETE /topicos/{id} - exclusao logica")
    void deveExcluirTopico() throws Exception {
        var autor = new Usuario(9L, "Eve", "eve@mail.com", "zzz");
        var curso = new Curso(9L, "Python", "Backend");
        var topico = new Topico(new DadosCadastroTopico("Q", "W", 9L, 9L), autor, curso);
        topico.setId(900L);

        when(topicoRepository.findByIdAndAtivoTrue(900L))
                .thenReturn(Optional.of(topico));

        mockMvc.perform(delete("/topicos/900"))
                .andExpect(status().isNoContent());
    }
}