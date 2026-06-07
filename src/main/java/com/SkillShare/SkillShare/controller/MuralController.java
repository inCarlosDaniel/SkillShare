package com.SkillShare.SkillShare.controller;

import java.time.LocalDateTime;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.SkillShare.SkillShare.Modelo.Anexo;
import com.SkillShare.SkillShare.Modelo.GrupoEstudo;
import com.SkillShare.SkillShare.Modelo.Material;
import com.SkillShare.SkillShare.Modelo.Postagem;
import com.SkillShare.SkillShare.Modelo.Usuario;
import com.SkillShare.SkillShare.service.AnexoService;
import com.SkillShare.SkillShare.service.GrupoEstudoService;
import com.SkillShare.SkillShare.service.MaterialService;
import com.SkillShare.SkillShare.service.PostagemService;
import com.SkillShare.SkillShare.service.UsuarioService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequiredArgsConstructor
public class MuralController {

    private final GrupoEstudoService grupoEstudoService;

    private final PostagemService postagemService;

    private final MaterialService materialService;

    private final UsuarioService usuarioService;

    private final AnexoService anexoService;

    // Exibe os detalhes, postagens e materiais de um grupo
    @GetMapping("/grupos/{id}")
    public String detalheGrupo(@PathVariable int id, Model model, Authentication authentication) {




        Usuario usuarioAtual = usuarioService.buscarUsuarioAtual(authentication);

        GrupoEstudo grupo = grupoEstudoService.buscarGrupoEstudoPorId(id);

        if (grupo == null) {

            return "redirect:/grupos";
        }

        boolean isMembro = false;

        boolean isCriador = false;

        if (usuarioAtual != null) {

            if (grupo.getCriador() != null && grupo.getCriador().getIdUsuario().equals(usuarioAtual.getIdUsuario())) {

                isCriador = true;

                isMembro = true;
            } else if (grupo.getMembros() != null) {

                isMembro = grupo.getMembros().stream()

                        .anyMatch(m -> m.getIdUsuario().equals(usuarioAtual.getIdUsuario()));
            }
        }

        model.addAttribute("currentUser", usuarioAtual);

        model.addAttribute("grupo", grupo);

        model.addAttribute("fotoGrupoVersao", System.currentTimeMillis());

        model.addAttribute("isMembro", isMembro);

        model.addAttribute("isCriador", isCriador);

        if (isMembro) {

            model.addAttribute("posts", postagemService.listarPostagensPorGrupo(id));

            model.addAttribute("materiaisGrupo", materialService.listarMateriaisPorGrupo(id));

            model.addAttribute("todosMateriais", materialService.listarMateriais());
        }

        return "GrupoDetalhe";
    }

    // Publica uma postagem de texto no mural do grupo
    @PostMapping("/grupos/{id}/mural")
    public String postarTexto(@PathVariable int id,
            @RequestParam("conteudo") String conteudo,
            Authentication authentication) {



        Usuario usuarioAtual = usuarioService.buscarUsuarioAtual(authentication);

        if (usuarioAtual == null) {

            return "redirect:/login";
        }

        Postagem post = new Postagem();

        post.setAutor(usuarioAtual);

        post.setConteudo(conteudo);

        post.setTipo("MURAL");

        post.setIdReferencia(id);

        post.setDataPublicacao(LocalDateTime.now());

        postagemService.cadastrarPostagem(post);

        return "redirect:/grupos/" + id;
    }

    // Publica um quiz no mural do grupo
    @PostMapping("/grupos/{id}/mural/quiz")
    public String postarQuiz(@PathVariable int id,
            @RequestParam("conteudo") String conteudo,
            Authentication authentication) {



        Usuario usuarioAtual = usuarioService.buscarUsuarioAtual(authentication);

        if (usuarioAtual == null) {

            return "redirect:/login";
        }

        Postagem post = new Postagem();

        post.setAutor(usuarioAtual);

        post.setConteudo(conteudo);

        post.setTipo("QUIZ");

        post.setIdReferencia(id);

        post.setDataPublicacao(LocalDateTime.now());

        postagemService.cadastrarPostagem(post);

        return "redirect:/grupos/" + id;
    }

    // Compartilha um material existente no mural do grupo
    @PostMapping("/grupos/{id}/mural/material")
    public String compartilharMaterial(@PathVariable int id,
            @RequestParam("idMaterial") int idMaterial,
            Authentication authentication) {



        Usuario usuarioAtual = usuarioService.buscarUsuarioAtual(authentication);

        if (usuarioAtual == null) return "redirect:/login";

        Material material = materialService.buscarMaterialPorId(idMaterial);

        if (material != null) {

            Postagem post = new Postagem();

            post.setAutor(usuarioAtual);

            post.setConteudo("Compartilhou o material: " + material.getTitulo());

            post.setTipo("MURAL");

            post.setIdReferencia(id);

            post.setDataPublicacao(LocalDateTime.now());

            postagemService.cadastrarPostagem(post);

            Anexo anexo = new Anexo();

            anexo.setNomeArquivo(material.getTitulo());

            anexo.setTipo("mural-" + id);

            anexo.setUrl("/materiais-repositorio/download/" + idMaterial);

            anexo.setPostagem(post);

            anexoService.cadastrarAnexo(anexo);
        }

        return "redirect:/grupos/" + id;
    }

    // Envia um arquivo novo e publica no mural do grupo
    @PostMapping(value = "/grupos/{id}/mural/arquivo", consumes = "multipart/form-data")
    public String uploadArquivoGrupo(@PathVariable int id,
            @RequestParam("arquivo") MultipartFile arquivo,
            @RequestParam(value = "titulo", required = false) String titulo,
            Authentication authentication) {



        Usuario usuarioAtual = usuarioService.buscarUsuarioAtual(authentication);

        if (usuarioAtual == null) return "redirect:/login";

        if (arquivo == null || arquivo.isEmpty()) return "redirect:/grupos/" + id;

        try {

            String nome = (titulo != null && !titulo.isBlank()) ? titulo : arquivo.getOriginalFilename();

            int idMaterial = materialService.salvarArquivo(
                    usuarioAtual.getIdUsuario(), nome, arquivo.getContentType(), arquivo.getBytes());

            Postagem post = new Postagem();

            post.setAutor(usuarioAtual);

            post.setConteudo("Publicou o arquivo: " + nome);

            post.setTipo("MURAL");

            post.setIdReferencia(id);

            post.setDataPublicacao(LocalDateTime.now());

            postagemService.cadastrarPostagem(post);

            Anexo anexo = new Anexo();

            anexo.setNomeArquivo(nome);

            anexo.setTipo("mural-" + id);

            anexo.setUrl("/materiais-repositorio/download/" + idMaterial);

            anexo.setPostagem(post);

            anexoService.cadastrarAnexo(anexo);
        } catch (Exception e) {

            log.error("Erro ao fazer upload no grupo {}: {}", id, e.getMessage());
        }

        return "redirect:/grupos/" + id;
    }
}
