package com.alura.forumhub.controller;

import com.alura.forumhub.domain.usuario.DadosAtualizacaoUsuario;
import com.alura.forumhub.domain.usuario.DadosCadastroUsuario;
import com.alura.forumhub.domain.usuario.Usuario;
import com.alura.forumhub.domain.usuario.UsuarioRepository;
import com.alura.forumhub.infra.security.TokenService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = UsuarioController.class)
@AutoConfigureMockMvc(addFilters = false)
class UsuarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UsuarioRepository usuarioRepository;

    @MockitoBean
    private PasswordEncoder passwordEncoder;

    @MockitoBean
    private TokenService tokenService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    // --- helper para construir Usuario com segurança (evita construtores mal mapeados)
    private Usuario usuario(Long id, String nome, String email, String senha) {
        var u = new Usuario();
        u.setId(id);
        u.setNome(nome);
        u.setEmail(email);
        u.setSenha(senha);
        return u;
    }

    @Test
    @DisplayName("POST /usuario - sucesso na criacao")
    void deveCriarUsuario() throws Exception {
        var dados = new DadosCadastroUsuario("Alice", "alice@mail.com", "senha123");

        when(usuarioRepository.existsByEmail("alice@mail.com")).thenReturn(false);
        when(passwordEncoder.encode("senha123")).thenReturn("hashSenha");
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(inv -> {
            Usuario u = inv.getArgument(0);
            u.setId(10L);
            return u;
        });

        mockMvc.perform(post("/usuario")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dados)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/usuario/10"))
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.nome").value("Alice"))
                .andExpect(jsonPath("$.email").value("alice@mail.com"));
    }

    @Test
    @DisplayName("POST /usuario - falha quando email duplicado")
    void deveRetornarBadRequestAoCriarUsuarioDuplicado() throws Exception {
        var dados = new DadosCadastroUsuario("Bob", "bob@mail.com", "123");
        when(usuarioRepository.existsByEmail("bob@mail.com")).thenReturn(true);

        mockMvc.perform(post("/usuario")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dados)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /usuario - lista paginada")
    void deveListarUsuarios() throws Exception {
        var u = usuario(1L, "Carol", "carol@mail.com", "x");

        Page<Usuario> page = new PageImpl<>(
                List.of(u),
                PageRequest.of(0, 10),
                1
        );

        when(usuarioRepository.findAll(any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/usuario")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sort", "nome"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].nome").value("Carol"))
                .andExpect(jsonPath("$.content[0].email").value("carol@mail.com"))
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    @DisplayName("GET /usuario/{id} - detalhar usuario existente")
    void deveDetalharUsuario() throws Exception {
        var u = usuario(5L, "Dave", "dave@mail.com", "pwd");
        when(usuarioRepository.findById(5L)).thenReturn(Optional.of(u));

        mockMvc.perform(get("/usuario/5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(5))
                .andExpect(jsonPath("$.nome").value("Dave"))
                .andExpect(jsonPath("$.email").value("dave@mail.com"));
    }

    @Test
    @DisplayName("GET /usuario/{id} - retorna 404 se nao encontrado")
    void deveRetornar404AoDetalharUsuarioInexistente() throws Exception {
        when(usuarioRepository.findById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/usuario/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("PUT /usuario/{id} - atualiza usuario existente")
    void deveAtualizarUsuario() throws Exception {
        var u = usuario(8L, "Eve", "eve@mail.com", "old");
        var dadosAtualizacao = new DadosAtualizacaoUsuario("Eve Nova", "novaSenha");

        when(usuarioRepository.findById(8L)).thenReturn(Optional.of(u));
        when(passwordEncoder.encode("novaSenha")).thenReturn("hashNova");
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(inv -> inv.getArgument(0));

        mockMvc.perform(put("/usuario/8")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dadosAtualizacao)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(8))
                .andExpect(jsonPath("$.nome").value("Eve Nova"))
                .andExpect(jsonPath("$.email").value("eve@mail.com")); // email permanece o mesmo
    }

    @Test
    @DisplayName("PUT /usuario/{id} - retorna 404 se nao encontrado")
    void deveRetornar404AoAtualizarUsuarioInexistente() throws Exception {
        var dadosAtualizacao = new DadosAtualizacaoUsuario("Nome", "Senha");
        when(usuarioRepository.findById(123L)).thenReturn(Optional.empty());

        mockMvc.perform(put("/usuario/123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dadosAtualizacao)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("DELETE /usuario/{id} - exclui usuario existente")
    void deveExcluirUsuario() throws Exception {
        when(usuarioRepository.existsById(50L)).thenReturn(true);

        mockMvc.perform(delete("/usuario/50"))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("DELETE /usuario/{id} - retorna 404 se nao encontrado")
    void deveRetornar404AoExcluirUsuarioInexistente() throws Exception {
        when(usuarioRepository.existsById(51L)).thenReturn(false);

        mockMvc.perform(delete("/usuario/51"))
                .andExpect(status().isNotFound());
    }
}
