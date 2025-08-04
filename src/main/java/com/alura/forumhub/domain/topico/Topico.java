package com.alura.forumhub.domain.topico;

import com.alura.forumhub.domain.curso.Curso;
import com.alura.forumhub.domain.resposta.Resposta;
import com.alura.forumhub.domain.usuario.Usuario;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Table(name = "topicos")
@Entity(name = "Topico")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Topico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String titulo;

    @NotBlank
    @Column(columnDefinition = "TEXT")
    private String mensagem;

    @Column(name = "data_criacao", nullable = false)
    private LocalDateTime dataCriacao = LocalDateTime.now();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusTopico status = StatusTopico.NAO_RESPONDIDO;

    @ManyToOne
    @JoinColumn(name = "autor_id", nullable = false)
    private Usuario autor;

    @ManyToOne
    @JoinColumn(name = "curso_id", nullable = false)
    private Curso curso;

    @Getter
    @OneToMany(mappedBy = "topico", cascade = CascadeType.ALL)
    private List<Resposta> respostas;

    private Boolean ativo;

    public Topico(DadosCadastroTopico dados, Usuario autor, Curso curso) {
        this.ativo = true;
        this.titulo = dados.titulo();
        this.mensagem = dados.mensagem();
        this.dataCriacao = LocalDateTime.now();
        this.status = StatusTopico.NAO_RESPONDIDO;
        this.autor = autor;
        this.curso = curso;
    }

    public void excluir() {
        this.ativo = false;
    }

    public void atualizar(String novoTitulo, String novaMensagem) {
        if (novoTitulo != null) {
            this.titulo = novoTitulo;
        }
        if (novaMensagem != null) {
            this.mensagem = novaMensagem;
        }
    }
}
