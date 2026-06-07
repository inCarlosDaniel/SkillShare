package com.SkillShare.SkillShare.controller;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.SkillShare.SkillShare.Modelo.Anexo;
import com.SkillShare.SkillShare.Modelo.Postagem;
import com.SkillShare.SkillShare.Modelo.Usuario;
import com.SkillShare.SkillShare.service.AnexoService;
import com.SkillShare.SkillShare.service.MaterialService;
import com.SkillShare.SkillShare.service.PostagemService;
import com.SkillShare.SkillShare.service.UsuarioService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;



@Slf4j
@Controller
@RequiredArgsConstructor
public class PostagemController {

    private final PostagemService postagemService;

    private final UsuarioService usuarioService;

    private final MaterialService materialService;

    private final AnexoService anexoService;

    // Exibe o feed geral ou apenas as postagens do usuario logado
    @GetMapping("/feed")
    public String feed(@RequestParam(value = "minhas", defaultValue = "false") boolean minhas,

            Model model, Authentication authentication) {



        Usuario usuarioAtual = usuarioService.buscarUsuarioAtual(authentication);

        model.addAttribute("currentUser", usuarioAtual);

        model.addAttribute("abaMinhas", minhas);

        if (minhas && usuarioAtual != null) {

            model.addAttribute("postagens", postagemService.listarPostagensPorUsuario(usuarioAtual.getIdUsuario()));
        } else {

            model.addAttribute("postagens", postagemService.listarPostagens());
        }

        return "Feed";
    }

    private static final long TAMANHO_MAXIMO = 50L * 1024 * 1024;

    // Cria uma postagem no feed com anexos opcionais
    @PostMapping("/feed")
    public String salvarPostagem(@RequestParam("conteudo") String conteudo,

            @RequestParam(value = "arquivo", required = false) MultipartFile arquivo,

            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        Usuario usuarioAtual = usuarioService.buscarUsuarioAtual(authentication);

        if (usuarioAtual == null) {

            redirectAttributes.addFlashAttribute("erro", "Cadastre um usuario antes de criar postagens.");

            return "redirect:/feed";
        }

        if (conteudo == null || conteudo.isBlank()) {

            redirectAttributes.addFlashAttribute("erro", "Escreva algo antes de publicar.");

            return "redirect:/feed";
        }

        byte[] bytesAnexo = null;
        if (arquivo != null && !arquivo.isEmpty()) {

            if (arquivo.getSize() > TAMANHO_MAXIMO) {

                redirectAttributes.addFlashAttribute("erroAnexo", "O arquivo excede o limite de 50 MB.");

                return "redirect:/feed";
            }
            try {

                bytesAnexo = arquivo.getBytes();
            } catch (Exception e) {

                log.error("Erro ao ler anexo da postagem: {}", e.getMessage());

                redirectAttributes.addFlashAttribute("erroAnexo", "Erro ao ler o arquivo: " + e.getMessage());

                return "redirect:/feed";
            }
        }

        Postagem postagem = new Postagem();

        postagem.setAutor(usuarioAtual);

        postagem.setConteudo(conteudo.trim());

        postagem.setDataPublicacao(LocalDateTime.now());

        postagemService.cadastrarPostagem(postagem);

        if (bytesAnexo != null) {

            try {

                int idMaterial = materialService.salvarArquivo(
                        usuarioAtual.getIdUsuario(),
                        arquivo.getOriginalFilename(),
                        arquivo.getContentType(),
                        bytesAnexo);

                if (idMaterial > 0) {

                    Anexo anexo = new Anexo();

                    anexo.setNomeArquivo(arquivo.getOriginalFilename());

                    anexo.setTipo(arquivo.getContentType());

                    anexo.setUrl("/materiais-repositorio/download/" + idMaterial);

                    anexo.setPostagem(postagem);

                    anexoService.cadastrarAnexo(anexo);
                } else {

                    redirectAttributes.addFlashAttribute("erroAnexo", "Nao foi possivel salvar o arquivo. Tente novamente.");
                }
            } catch (Exception e) {

                log.error("Erro ao salvar anexo da postagem: {}", e.getMessage());

                redirectAttributes.addFlashAttribute("erroAnexo", "Erro ao salvar o arquivo: " + e.getMessage());
            }
        }

        return "redirect:/feed";
    }



    // Adiciona upvote em uma postagem
    @PostMapping("/postagens/{id}/upvote")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> upvotar(@PathVariable int id, Authentication authentication) {



        Usuario usuarioAtual = usuarioService.buscarUsuarioAtual(authentication);

        if (usuarioAtual == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        postagemService.upvotar(id, usuarioAtual);

        return ResponseEntity.ok(Map.of(
                "upvotes", postagemService.contarUpvotes(id),
                "upvotou", true));
    }

    // Remove o upvote do usuario em uma postagem
    @DeleteMapping("/postagens/{id}/upvote")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> removerUpvote(@PathVariable int id, Authentication authentication) {



        Usuario usuarioAtual = usuarioService.buscarUsuarioAtual(authentication);

        if (usuarioAtual == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        postagemService.removerUpvote(id, usuarioAtual.getIdUsuario());

        return ResponseEntity.ok(Map.of(
                "upvotes", postagemService.contarUpvotes(id),
                "upvotou", false));
    }

    // Retorna a quantidade de upvotes e se o usuario ja votou
    @GetMapping("/postagens/{id}/upvotes")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> upvotes(@PathVariable int id, Authentication authentication) {



        Usuario usuarioAtual = usuarioService.buscarUsuarioAtual(authentication);

        boolean upvotou = usuarioAtual != null && postagemService.usuarioUpvotou(id, usuarioAtual.getIdUsuario());

        return ResponseEntity.ok(Map.of(
                "upvotes", postagemService.contarUpvotes(id),
                "upvotou", upvotou));
    }



    // Adiciona um comentario em uma postagem
    @PostMapping("/postagens/{id}/comentar")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> comentar(
            @PathVariable int id,
            @RequestBody Map<String, String> body,
            Authentication authentication) {

        Usuario usuarioAtual = usuarioService.buscarUsuarioAtual(authentication);

        if (usuarioAtual == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        String texto = body.getOrDefault("texto", "").trim();

        if (texto.isEmpty()) return ResponseEntity.badRequest().build();

        postagemService.comentar(id, texto, usuarioAtual);

        List<Postagem> comentarios = postagemService.listarComentarios(id);

        Postagem ultimo = comentarios.isEmpty() ? null : comentarios.get(comentarios.size() - 1);

        return ResponseEntity.ok(Map.of(
                "total", comentarios.size(),
                "autor", usuarioAtual.getNome(),
                "texto", texto,
                "hora", ultimo != null && ultimo.getDataPublicacao() != null
                        ? ultimo.getDataPublicacao().toString() : ""));
    }

    // Lista os comentarios de uma postagem
    @GetMapping("/postagens/{id}/comentarios")
    @ResponseBody
    public List<Map<String, Object>> listarComentarios(@PathVariable int id) {


        return postagemService.listarComentarios(id).stream()

                .map(c -> Map.<String, Object>of(
                        "autor", c.getAutor() != null ? c.getAutor().getNome() : "Usuario",
                        "texto", c.getConteudo() != null ? c.getConteudo() : "",
                        "hora", c.getDataPublicacao() != null ? c.getDataPublicacao().toString() : ""))

                .toList();
    }



    // Retorna todas as postagens em JSON
    @GetMapping("/postagens")
    @ResponseBody
    public ArrayList<Postagem> listarPostagens() {

        return postagemService.listarPostagens();
    }

    // Cadastra uma postagem pela API
    @PostMapping("/postagens")
    @ResponseBody
    public ResponseEntity<Void> cadastrarPostagem(@RequestBody Postagem postagem, Authentication authentication) {

        Usuario usuarioAtual = usuarioService.buscarUsuarioAtual(authentication);

        if (usuarioAtual == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        postagem.setAutor(usuarioAtual);

        postagemService.cadastrarPostagem(postagem);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    // Atualiza uma postagem pela API se o usuario for o autor
    @PutMapping("/postagens/{id}")
    @ResponseBody
    public ResponseEntity<Void> alterarPostagem(@PathVariable int id,
            @RequestBody Postagem postagem,
            Authentication authentication) {

        if (!isDonoPostagem(id, authentication)) {

            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        postagem.setidPostagem(id);

        postagemService.alterarPostagem(postagem);

        return ResponseEntity.ok().build();
    }

    // Exclui uma postagem pela API se o usuario for o autor
    @DeleteMapping("/postagens/{id}")
    @ResponseBody
    public ResponseEntity<Void> excluirPostagem(@PathVariable int id, Authentication authentication) {



        if (!isDonoPostagem(id, authentication)) {

            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        postagemService.excluirPostagem(id);

        return ResponseEntity.noContent().build();
    }

    // Verifica se o usuario logado e o autor da postagem
    private boolean isDonoPostagem(int idPostagem, Authentication authentication) {


        Usuario usuarioAtual = usuarioService.buscarUsuarioAtual(authentication);

        if (usuarioAtual == null) return false;

        Postagem postagem = postagemService.buscarPostagemPorId(idPostagem);

        if (postagem == null || postagem.getAutor() == null) return false;

        return postagem.getAutor().getIdUsuario().equals(usuarioAtual.getIdUsuario());
    }
}
