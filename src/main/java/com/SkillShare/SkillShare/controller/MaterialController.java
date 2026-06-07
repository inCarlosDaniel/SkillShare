package com.SkillShare.SkillShare.controller;

import java.util.ArrayList;
import java.util.UUID;
import javax.sql.rowset.serial.SerialBlob;
import jakarta.servlet.http.HttpSession;

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

import com.SkillShare.SkillShare.Modelo.Material;
import com.SkillShare.SkillShare.Modelo.Usuario;
import com.SkillShare.SkillShare.service.MaterialService;
import com.SkillShare.SkillShare.service.UsuarioService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;



@Slf4j
@Controller
@RequiredArgsConstructor
public class MaterialController {

    private static final long TAMANHO_MAXIMO_ARQUIVO = 50L * 1024 * 1024;

    private static final String MATERIAL_UPLOAD_TOKEN = "materialUploadToken";

    private final MaterialService materialService;

    private final UsuarioService usuarioService;

    // Exibe o repositorio de materiais e prepara o token de upload
    @GetMapping("/materiais-repositorio")

    public String materiais(Model model, Authentication authentication, HttpSession session) {

        if (session.getAttribute(MATERIAL_UPLOAD_TOKEN) == null) {

            session.setAttribute(MATERIAL_UPLOAD_TOKEN, UUID.randomUUID().toString());
        }

        model.addAttribute("currentUser", usuarioService.buscarUsuarioAtual(authentication));

        model.addAttribute("materiais", materialService.listarMateriais());

        model.addAttribute("uploadToken", session.getAttribute(MATERIAL_UPLOAD_TOKEN));

        return "Materiais";
    }



    // Salva um novo material enviado pelo usuario logado
    @PostMapping("/materiais-repositorio")

    public String salvarMaterial(@RequestParam("titulo") String titulo,
            @RequestParam("categoria") String categoria,
            @RequestParam("arquivo") MultipartFile arquivo,
            @RequestParam("uploadToken") String uploadToken,
            Authentication authentication,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        if (!consumirUploadToken(session, uploadToken)) {

            return "redirect:/materiais-repositorio";
        }

        Usuario usuarioAtual = usuarioService.buscarUsuarioAtual(authentication);

        if (usuarioAtual == null) {

            redirectAttributes.addFlashAttribute("erro", "Cadastre um usuario antes de enviar materiais.");

            return "redirect:/materiais-repositorio";
        }

        if (arquivo.isEmpty()) {

            redirectAttributes.addFlashAttribute("erro", "Escolha um arquivo antes de enviar.");

            return "redirect:/materiais-repositorio";
        }

        if (arquivo.getSize() > TAMANHO_MAXIMO_ARQUIVO) {

            redirectAttributes.addFlashAttribute("erro", "Envie um arquivo de ate 50MB.");

            return "redirect:/materiais-repositorio";
        }

        try {

            Material material = new Material();

            material.setTitulo(titulo);

            material.setCategoria(categoria);

            material.setAutor(usuarioAtual);

            material.setArquivo(new SerialBlob(arquivo.getBytes()));

            materialService.cadastrarMaterial(material);

            redirectAttributes.addFlashAttribute("sucesso", "Material enviado com sucesso.");

            return "redirect:/materiais-repositorio";
        } catch (Exception e) {

            log.error("Erro ao salvar material: {}", e.getMessage(), e);

            redirectAttributes.addFlashAttribute("erro", mensagemErroUpload(e));

            return "redirect:/materiais-repositorio";
        }
    }

    // Valida e consome o token usado para evitar upload duplicado
    private boolean consumirUploadToken(HttpSession session, String uploadToken) {

        synchronized (session) {

            Object tokenSessao = session.getAttribute(MATERIAL_UPLOAD_TOKEN);

            if (tokenSessao == null || uploadToken == null || !tokenSessao.equals(uploadToken)) {

                return false;
            }
            session.removeAttribute(MATERIAL_UPLOAD_TOKEN);

            return true;
        }
    }

    // Baixa o arquivo de um material do repositorio
    @GetMapping("/materiais-repositorio/download/{id}")

    public ResponseEntity<byte[]> baixarMaterial(@PathVariable int id) throws Exception {

        Material material = materialService.buscarMaterialPorId(id);

        if (material == null || material.getArquivo() == null) {

            return ResponseEntity.notFound().build();
        }

        byte[] arquivo = material.getArquivo().getBytes(1, (int) material.getArquivo().length());

        String nomeArquivo = material.getTitulo() == null
                ? "material"
                : material.getTitulo().replaceAll("[^a-zA-Z0-9._-]", "_");

        return ResponseEntity.ok()

                .contentType(MediaType.APPLICATION_OCTET_STREAM)

                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + nomeArquivo + "\"")

                .body(arquivo);
    }

    // Retorna todos os materiais em JSON
    @GetMapping("/materiais")

    @ResponseBody
    public ArrayList<Material> listarMateriais() {

        return materialService.listarMateriais();
    }

    // Exclui um material do repositorio pela tela
    @PostMapping("/materiais-repositorio/{id}/excluir")

    public String excluirMaterialRepositorio(@PathVariable int id,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        Material existente = materialService.buscarMaterialPorId(id);

        Usuario usuarioAtual = usuarioService.buscarUsuarioAtual(authentication);

        if (existente == null) {

            redirectAttributes.addFlashAttribute("erro", "Material nao encontrado.");

            return "redirect:/materiais-repositorio";
        }
        if (!usuarioPodeAlterarMaterial(existente, usuarioAtual)) {

            redirectAttributes.addFlashAttribute("erro", "Voce so pode excluir materiais enviados por voce.");

            return "redirect:/materiais-repositorio";
        }

        materialService.excluirMaterial(id);

        redirectAttributes.addFlashAttribute("sucesso", "Material excluido com sucesso.");

        return "redirect:/materiais-repositorio";
    }

    // Cadastra um material pela API
    @PostMapping("/materiais")

    @ResponseBody
    @ResponseStatus(HttpStatus.CREATED)

    public void cadastrarMaterial(@RequestBody Material material) {

        materialService.cadastrarMaterial(material);
    }

    // Atualiza um material pela API se o usuario for o autor
    @PutMapping("/materiais/{id}")

    @ResponseBody
    public ResponseEntity<Void> alterarMaterial(@PathVariable int id,
            @RequestBody Material material,
            Authentication authentication) {

        Material existente = materialService.buscarMaterialPorId(id);

        Usuario usuarioAtual = usuarioService.buscarUsuarioAtual(authentication);

        if (existente == null) {

            return ResponseEntity.notFound().build();
        }
        if (!usuarioPodeAlterarMaterial(existente, usuarioAtual)) {

            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        material.setidMaterial(id);

        materialService.alterarMaterial(material);

        return ResponseEntity.ok().build();
    }

    // Exclui um material pela API se o usuario for o autor
    @DeleteMapping("/materiais/{id}")

    @ResponseBody
    public ResponseEntity<Void> excluirMaterial(@PathVariable int id, Authentication authentication) {

        Material existente = materialService.buscarMaterialPorId(id);

        Usuario usuarioAtual = usuarioService.buscarUsuarioAtual(authentication);

        if (existente == null) {

            return ResponseEntity.notFound().build();
        }
        if (!usuarioPodeAlterarMaterial(existente, usuarioAtual)) {

            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        materialService.excluirMaterial(id);

        return ResponseEntity.noContent().build();
    }

    // Verifica se o usuario logado pode editar ou excluir o material
    private boolean usuarioPodeAlterarMaterial(Material material, Usuario usuario) {

        return usuario != null
                && material != null
                && material.getAutor() != null
                && material.getAutor().getIdUsuario() != null
                && material.getAutor().getIdUsuario().equals(usuario.getIdUsuario());
    }

    // Monta uma mensagem detalhada quando o upload falha
    private String mensagemErroUpload(Exception erro) {

        Throwable causa = erro;

        StringBuilder detalhes = new StringBuilder();

        while (causa != null) {

            detalhes.append(" | ").append(causa.getClass().getSimpleName()).append(": ").append(causa.getMessage());

            causa = causa.getCause();
        }
        return "Erro ao salvar material: " + detalhes;
    }
}
