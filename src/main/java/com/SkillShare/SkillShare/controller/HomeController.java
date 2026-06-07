package com.SkillShare.SkillShare.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.SkillShare.SkillShare.Modelo.Usuario;
import com.SkillShare.SkillShare.service.GrupoEstudoService;
import com.SkillShare.SkillShare.service.MaterialService;
import com.SkillShare.SkillShare.service.MensagemService;
import com.SkillShare.SkillShare.service.PostagemService;
import com.SkillShare.SkillShare.service.UsuarioService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final UsuarioService usuarioService;

    private final PostagemService postagemService;

    private final MaterialService materialService;

    private final GrupoEstudoService grupoEstudoService;

    private final MensagemService mensagemService;

    // Exibe a pagina inicial com feed, materiais, grupos e sugestoes
    @GetMapping("/")

    public String index(Model model, Authentication authentication) {

        Usuario usuarioAtual = usuarioService.buscarUsuarioAtual(authentication);

        model.addAttribute("currentUser", usuarioAtual);

        model.addAttribute("postagens", postagemService.listarPostagens());

        model.addAttribute("materiais", materialService.listarMateriais());

        model.addAttribute("grupos", grupoEstudoService.listarGruposEstudo());

        model.addAttribute("mensagens", mensagemService.listarMensagensRecebidas(usuarioAtual));

        model.addAttribute("usuarios", usuarioService.listarUsuarios());

        if (usuarioAtual != null) {

            model.addAttribute("conectadosIds", mensagemService.listarConectadosIds(usuarioAtual.getIdUsuario()));
        } else {

            model.addAttribute("conectadosIds", java.util.Collections.emptySet());
        }

        return "index";
    }

    // Exibe a pagina de login
    @GetMapping("/login")

    public String login() {

        return "Login";
    }

    // Exibe a pagina de cadastro
    @GetMapping("/cadastro")

    public String cadastro() {

        return "Cadastro";
    }

    // Exibe a tela intermediaria de validacao do cadastro
    @GetMapping("/validando-informacoes")

    public String validandoInformacoes() {

        return "ValidandoInformacoes";
    }

    // Exibe a pagina de acessibilidade
    @GetMapping("/acessibilidade")

    public String acessibilidade(Model model, Authentication authentication) {

        model.addAttribute("currentUser", usuarioService.buscarUsuarioAtual(authentication));

        return "Acessibilidade";
    }
}
