//package com.alura.forumhub.infra.config;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.stereotype.Component;
//
//import jakarta.annotation.PostConstruct;
//
//@Component
//public class GeradorDeSenha {
//
//    @Autowired
//    private PasswordEncoder passwordEncoder;
//
//    @PostConstruct
//    public void gerarSenha() {
//        String senhaCriptografada = passwordEncoder.encode("123456");
//        System.out.println("Senha criptografada: " + senhaCriptografada);
//    }
//}
