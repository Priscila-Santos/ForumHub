package com.alura.forumhub.domain.topico;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface TopicoRepository extends JpaRepository<Topico, Long> {

    boolean existsByTituloAndMensagem(String titulo, String mensagem);

    Page<Topico> findByAtivoTrue(Pageable pageable);

    Optional<Topico> findByIdAndAtivoTrue(Long id);

    Page<Topico> findByCursoNomeAndDataCriacaoBetweenAndAtivoTrue(
            String cursoNome,
            LocalDateTime inicio,
            LocalDateTime fim,
            Pageable pageable
    );

    long countByCursoCategoria(String categoria);
    long countByCursoCategoriaAndDataCriacaoAfter(String categoria, LocalDateTime dataLimite);
}


