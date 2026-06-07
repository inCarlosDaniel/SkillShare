package com.SkillShare.SkillShare.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    // Trata erros gerais e decide entre resposta JSON ou pagina de erro
    public Object handleException(Exception ex, HttpServletRequest request) {

        log.error("Erro nao tratado em {}: {}", request.getRequestURI(), ex.getMessage(), ex);

        String mensagem = mensagemAmigavel(ex);

        if (isAjax(request)) {

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Map.of("erro", mensagem));
        }

        String voltarUrl = request.getHeader("Referer");

        if (voltarUrl == null || voltarUrl.isBlank()) {

            voltarUrl = "/";
        }

        ModelAndView mav = new ModelAndView("Erro");

        mav.addObject("mensagem", mensagem);

        mav.addObject("voltarUrl", voltarUrl);

        mav.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);

        return mav;
    }

    // Identifica se a requisicao espera uma resposta JSON
    private boolean isAjax(HttpServletRequest request) {

        String accept = request.getHeader("Accept");

        String contentType = request.getContentType();

        String xRequested = request.getHeader("X-Requested-With");

        return "XMLHttpRequest".equals(xRequested)
                || (accept != null && accept.contains(MediaType.APPLICATION_JSON_VALUE))
                || (contentType != null && contentType.contains(MediaType.APPLICATION_JSON_VALUE));
    }

    // Converte excecoes conhecidas em mensagens mais claras para o usuario
    private String mensagemAmigavel(Exception ex) {

        String msg = ex.getMessage();

        if (msg == null) return "Ocorreu um erro inesperado. Tente novamente.";

        if (msg.contains("Data too long")) return "Um dos campos ultrapassa o limite de caracteres permitido.";

        if (msg.contains("Duplicate entry")) return "Esse registro já existe.";

        if (msg.contains("foreign key constraint")) return "Operação não permitida: registro vinculado a outros dados.";

        if (msg.contains("Connection") || msg.contains("comunicar")) return "Não foi possível conectar ao banco de dados. Tente novamente.";

        return "Ocorreu um erro inesperado. Tente novamente.";
    }
}
