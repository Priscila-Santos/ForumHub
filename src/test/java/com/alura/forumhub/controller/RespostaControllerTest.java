package com.alura.forumhub.controller;

import com.alura.forumhub.domain.resposta.*;
import com.alura.forumhub.domain.topico.Topico;
import com.alura.forumhub.domain.topico.TopicoRepository;
import com.alura.forumhub.domain.usuario.Usuario;
import com.alura.forumhub.domain.usuario.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class RespostaControllerTest {

    @Mock
    private RespostaRepository respostaRepository;

    @Mock
    private TopicoRepository topicoRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private RespostaController respostaController;

    private Usuario autor;
    private Topico topico;
    private Resposta resposta;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        autor = new Usuario();
        autor.setId(1L);
        autor.setNome("Autor Teste");

        topico = new Topico();
        topico.setId(1L);
        topico.setTitulo("Título do Tópico");

        resposta = Resposta.builder()
                .id(1L)
                .mensagem("Mensagem teste")
                .autor(autor)
                .topico(topico)
                .solucao(false)
                .dataCriacao(LocalDateTime.now())
                .build();
    }

    @Test
    void deveCadastrarRespostaComSucesso() {
        DadosCadastroResposta dados = new DadosCadastroResposta(
                "Mensagem teste",
                topico.getId(),
                autor.getId()
        );

        when(topicoRepository.findById(topico.getId())).thenReturn(Optional.of(topico));
        when(usuarioRepository.findById(autor.getId())).thenReturn(Optional.of(autor));
        when(respostaRepository.save(any(Resposta.class))).thenReturn(resposta);

        UriComponentsBuilder uriBuilder = UriComponentsBuilder.newInstance();

        ResponseEntity<DadosDetalhamentoResposta> response =
                respostaController.cadastrar(dados, uriBuilder);

        assertEquals(201, response.getStatusCodeValue());
        assertEquals("Mensagem teste", response.getBody().mensagem());
        verify(respostaRepository, times(1)).save(any(Resposta.class));
    }

    @Test
    void deveListarRespostas() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Resposta> page = new PageImpl<>(List.of(resposta));

        when(respostaRepository.findAll(pageable)).thenReturn(page);

        ResponseEntity<Page<DadosListagemResposta>> response =
                respostaController.listar(pageable);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(1, response.getBody().getTotalElements());
    }

    @Test
    void deveDetalharResposta() {
        when(respostaRepository.findById(1L)).thenReturn(Optional.of(resposta));

        ResponseEntity<DadosDetalhamentoResposta> response =
                respostaController.detalhar(1L);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Mensagem teste", response.getBody().mensagem());
    }

    @Test
    void deveAtualizarResposta() {
        DadosAtualizacaoResposta dados = new DadosAtualizacaoResposta("Nova mensagem", true);

        when(respostaRepository.findById(1L)).thenReturn(Optional.of(resposta));

        ResponseEntity<DadosDetalhamentoResposta> response =
                respostaController.atualizar(1L, dados);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Nova mensagem", response.getBody().mensagem());
        assertTrue(response.getBody().solucao());
    }

    @Test
    void deveExcluirRespostaExistente() {
        when(respostaRepository.existsById(1L)).thenReturn(true);

        ResponseEntity<Void> response = respostaController.excluir(1L);

        assertEquals(204, response.getStatusCodeValue());
        verify(respostaRepository, times(1)).deleteById(1L);
    }

    @Test
    void naoDeveExcluirRespostaInexistente() {
        when(respostaRepository.existsById(1L)).thenReturn(false);

        ResponseEntity<Void> response = respostaController.excluir(1L);

        assertEquals(404, response.getStatusCodeValue());
        verify(respostaRepository, never()).deleteById(anyLong());
    }
}
