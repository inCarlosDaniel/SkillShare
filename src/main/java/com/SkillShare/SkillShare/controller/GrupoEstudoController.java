package com.SkillShare.SkillShare.controller;

import java.util.ArrayList;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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

import com.SkillShare.SkillShare.Modelo.GrupoEstudo;
import com.SkillShare.SkillShare.Modelo.Material;
import com.SkillShare.SkillShare.Modelo.Usuario;
import com.SkillShare.SkillShare.service.AnexoService;
import com.SkillShare.SkillShare.service.GrupoEstudoService;
import com.SkillShare.SkillShare.service.MaterialService;
import com.SkillShare.SkillShare.service.UsuarioService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequiredArgsConstructor
public class GrupoEstudoController {

    private final GrupoEstudoService grupoEstudoService;

    private final UsuarioService usuarioService;

    private final MaterialService materialService;

    private final AnexoService anexoService;

    // Exibe os grupos do usuario e sugestoes para descobrir
    @GetMapping("/grupos")

    public String grupos(Model model, Authentication authentication) {

        Usuario usuarioAtual = usuarioService.buscarUsuarioAtual(authentication);

        ArrayList<GrupoEstudo> meusGrupos = usuarioAtual != null

                ? grupoEstudoService.listarGruposPorUsuario(usuarioAtual.getIdUsuario())

                : new ArrayList<>();

        model.addAttribute("currentUser", usuarioAtual);

        model.addAttribute("grupos", meusGrupos);

        model.addAttribute("gruposDescobrir", calcularGruposDescobrir(meusGrupos));

        return "GrupoEstudos";
    }

    // Busca grupos por nome e mantem os grupos do usuario na tela
    @GetMapping("/grupos/buscar")

    public String buscarGrupos(@RequestParam(value = "q", defaultValue = "") String q,
            Model model, Authentication authentication) {

        Usuario usuarioAtual = usuarioService.buscarUsuarioAtual(authentication);

        ArrayList<GrupoEstudo> meusGrupos = usuarioAtual != null
                ? grupoEstudoService.listarGruposPorUsuario(usuarioAtual.getIdUsuario())

                : new ArrayList<>();

        model.addAttribute("currentUser", usuarioAtual);

        model.addAttribute("termoBusca", q);

        model.addAttribute("gruposResultado",
                q.isBlank() ? new ArrayList<>() : grupoEstudoService.buscarGruposPorNome(q));

        model.addAttribute("grupos", meusGrupos);

        model.addAttribute("gruposDescobrir", calcularGruposDescobrir(meusGrupos));

        return "GrupoEstudos";
    }

    // Calcula quais grupos o usuario ainda nao participa
    private ArrayList<GrupoEstudo> calcularGruposDescobrir(ArrayList<GrupoEstudo> meusGrupos) {

        Set<Integer> meusIds = meusGrupos.stream()

                .map(GrupoEstudo::getidGrupo)

                .collect(Collectors.toSet());

        return grupoEstudoService.listarGruposEstudo().stream()

                .filter(g -> !meusIds.contains(g.getidGrupo()))

                .collect(Collectors.toCollection(ArrayList::new));
    }

    // Cria um novo grupo e adiciona o usuario logado como criador
    @PostMapping("/grupos")

    public String salvarGrupo(@RequestParam("nome") String nome,
            @RequestParam("materia") String materia,
            @RequestParam("exameAlvo") String exameAlvo,
            @RequestParam(value = "foto", required = false) MultipartFile foto,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        Usuario usuarioAtual = usuarioService.buscarUsuarioAtual(authentication);

        if (usuarioAtual == null) {

            redirectAttributes.addFlashAttribute("erro", "Cadastre um usuario antes de criar grupos.");

            return "redirect:/grupos";
        }

        GrupoEstudo grupo = new GrupoEstudo();

        grupo.setNome(nome);

        grupo.setMateria(materia);

        grupo.setExameAlvo(exameAlvo);

        grupo.setCriador(usuarioAtual);

        grupo.setMembros(new ArrayList<>());
        grupo.getMembros().add(usuarioAtual);

        try {

            grupoEstudoService.cadastrarGrupoEstudo(grupo);

        } catch (Exception e) {

            log.error("Erro ao criar grupo '{}': {}", nome, e.getMessage());

            String msg = e.getMessage() != null ? e.getMessage() : "";

            if (msg.contains("'nome'")) {
                redirectAttributes.addFlashAttribute("erroNome", "Máximo de 100 caracteres atingido.");
            } else if (msg.contains("'materia'")) {
                redirectAttributes.addFlashAttribute("erroMateria", "Máximo de 255 caracteres atingido.");
            } else if (msg.contains("'exameAlvo'")) {
                redirectAttributes.addFlashAttribute("erroExameAlvo", "Máximo de 255 caracteres atingido.");
            } else {
                redirectAttributes.addFlashAttribute("erro", "Não foi possível criar o grupo. Tente novamente.");
            }

            return "redirect:/grupos";
        }

        if (foto != null && !foto.isEmpty()) {

            try {

                materialService.salvarFotoGrupo(grupo.getidGrupo(), usuarioAtual.getIdUsuario(),
                        foto.getContentType(), foto.getBytes());
            } catch (Exception e) {

                log.warn("Nao foi possivel salvar foto do grupo: {}", e.getMessage());
            }
        }

        return "redirect:/grupos";
    }

    // Remove um material compartilhado dentro de um grupo
    @PostMapping("/grupos/{id}/materiais/{idAnexo}/excluir")

    public String excluirMaterialGrupo(@PathVariable int id, @PathVariable int idAnexo,
            Authentication authentication, RedirectAttributes redirectAttributes) {

        if (!isCriadorGrupo(id, authentication)) {

            redirectAttributes.addFlashAttribute("erro", "Apenas o criador pode excluir materiais do grupo.");

            return "redirect:/grupos/" + id;
        }
        anexoService.excluirAnexo(idAnexo);

        redirectAttributes.addFlashAttribute("sucesso", "Material removido do grupo.");

        return "redirect:/grupos/" + id;
    }

    // Atualiza a foto de capa de um grupo
    @PostMapping(value = "/grupos/{id}/foto", consumes = "multipart/form-data")

    public String atualizarFotoGrupo(@PathVariable int id,
            @RequestParam("foto") MultipartFile foto,
            Authentication authentication, RedirectAttributes redirectAttributes) {

        if (!isCriadorGrupo(id, authentication)) {

            redirectAttributes.addFlashAttribute("erro", "Apenas o criador pode alterar a foto do grupo.");

            return "redirect:/grupos/" + id;
        }
        if (foto == null || foto.isEmpty()) {

            redirectAttributes.addFlashAttribute("erro", "Escolha uma imagem antes de enviar.");

            return "redirect:/grupos/" + id;
        }
        try {

            materialService.salvarFotoGrupo(id,
                    usuarioService.buscarUsuarioAtual(authentication).getIdUsuario(),
                    foto.getContentType(), foto.getBytes());

            redirectAttributes.addFlashAttribute("sucesso", "Foto do grupo atualizada.");
        } catch (Exception e) {

            redirectAttributes.addFlashAttribute("erro", "Nao foi possivel salvar a foto.");
        }
        return "redirect:/grupos/" + id;
    }

    // Retorna a foto de capa de um grupo
    @GetMapping("/grupos/{id}/foto")

    @ResponseBody
    public ResponseEntity<byte[]> getFotoGrupo(@PathVariable int id) {

        Material fotoMaterial = materialService.buscarFotoGrupoPorGrupo(id);

        if (fotoMaterial == null || fotoMaterial.getArquivo() == null) {

            return ResponseEntity.notFound().build();
        }
        try {

            byte[] bytes = fotoMaterial.getArquivo().getBytes(1, (int) fotoMaterial.getArquivo().length());

            String mimeType = fotoMaterial.getTitulo() != null ? fotoMaterial.getTitulo() : "image/jpeg";

            return ResponseEntity.ok()

                    .contentType(MediaType.parseMediaType(mimeType))

                    .header(HttpHeaders.CACHE_CONTROL, "no-store, no-cache, must-revalidate, max-age=0")

                    .header(HttpHeaders.PRAGMA, "no-cache")

                    .header(HttpHeaders.EXPIRES, "0")

                    .body(bytes);
        } catch (Exception e) {

            log.error("Erro ao retornar foto do grupo {}: {}", id, e.getMessage());

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Adiciona o usuario logado como membro de um grupo
    @PostMapping("/grupos/{id}/entrar")

    public String entrarNoGrupo(@PathVariable int id, Authentication authentication,
            RedirectAttributes redirectAttributes) {

        Usuario usuarioAtual = usuarioService.buscarUsuarioAtual(authentication);

        if (usuarioAtual == null) {

            return "redirect:/login";
        }

        grupoEstudoService.entrarNoGrupo(id, usuarioAtual.getIdUsuario());

        redirectAttributes.addFlashAttribute("sucesso", "Você entrou no grupo!");

        return "redirect:/grupos/" + id;
    }

    // Exclui um grupo quando o usuario logado e o criador
    @PostMapping("/grupos/{id}/excluir")

    public String excluirGrupo(@PathVariable int id, Authentication authentication,
            RedirectAttributes redirectAttributes) {

        if (!isCriadorGrupo(id, authentication)) {

            redirectAttributes.addFlashAttribute("erro", "Apenas o criador pode excluir o grupo.");

            return "redirect:/grupos";
        }

        grupoEstudoService.excluirGrupoEstudo(id);

        return "redirect:/grupos";
    }

    // Retorna todos os grupos de estudo em JSON
    @GetMapping("/grupos-estudo")

    @ResponseBody
    public ArrayList<GrupoEstudo> listarGruposEstudo() {

        return grupoEstudoService.listarGruposEstudo();
    }

    // Cadastra um grupo de estudo pela API
    @PostMapping("/grupos-estudo")

    @ResponseBody
    public ResponseEntity<Void> cadastrarGrupoEstudo(@RequestBody GrupoEstudo grupoEstudo,
            Authentication authentication) {

        Usuario usuarioAtual = usuarioService.buscarUsuarioAtual(authentication);

        if (usuarioAtual == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        grupoEstudo.setCriador(usuarioAtual);

        grupoEstudoService.cadastrarGrupoEstudo(grupoEstudo);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    // Atualiza um grupo de estudo pela API
    @PutMapping("/grupos-estudo/{id}")

    @ResponseBody
    public ResponseEntity<Void> alterarGrupoEstudo(@PathVariable int id,
            @RequestBody GrupoEstudo grupoEstudo, Authentication authentication) {

        if (!isCriadorGrupo(id, authentication))
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();

        grupoEstudo.setidGrupo(id);

        grupoEstudoService.alterarGrupoEstudo(grupoEstudo);

        return ResponseEntity.ok().build();
    }

    // Exclui um grupo de estudo pela API se o usuario for o criador
    @DeleteMapping("/grupos-estudo/{id}")

    @ResponseBody
    public ResponseEntity<Void> excluirGrupoEstudo(@PathVariable int id, Authentication authentication) {

        if (!isCriadorGrupo(id, authentication))
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();

        grupoEstudoService.excluirGrupoEstudo(id);

        return ResponseEntity.noContent().build();
    }

    // Verifica se o usuario logado e o criador do grupo
    private boolean isCriadorGrupo(int idGrupo, Authentication authentication) {

        Usuario usuarioAtual = usuarioService.buscarUsuarioAtual(authentication);

        if (usuarioAtual == null)
            return false;

        GrupoEstudo grupo = grupoEstudoService.buscarGrupoEstudoPorId(idGrupo);

        if (grupo == null || grupo.getCriador() == null)
            return false;

        return grupo.getCriador().getIdUsuario().equals(usuarioAtual.getIdUsuario());
    }
}
