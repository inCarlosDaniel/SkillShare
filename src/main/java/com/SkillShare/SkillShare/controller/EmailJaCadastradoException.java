package com.SkillShare.SkillShare.controller;

import org.springframework.http.HttpStatus;

import org.springframework.web.bind.annotation.ResponseStatus;

// Retorna erro 409 quando alguem tenta cadastrar um e-mail ja existente
@ResponseStatus(HttpStatus.CONFLICT)
public class EmailJaCadastradoException extends RuntimeException {

    public EmailJaCadastradoException() {

        super("Email ja cadastrado");
    }

}
