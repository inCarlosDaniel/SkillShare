package com.SkillShare.SkillShare.controller; 

import java.time.LocalDateTime; 
import java.util.ArrayList; 
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
import org.springframework.web.bind.annotation.PatchMapping; 
import org.springframework.web.bind.annotation.PathVariable; 
import org.springframework.web.bind.annotation.PostMapping; 
import org.springframework.web.bind.annotation.PutMapping; 
import org.springframework.web.bind.annotation.RequestBody; 
import org.springframework.web.bind.annotation.RequestParam; 
import org.springframework.web.bind.annotation.ResponseBody; 
import org.springframework.web.bind.annotation.ResponseStatus; 
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes; 
import jakarta.servlet.http.HttpServletRequest; 

import com.SkillShare.SkillShare.Modelo.Material; 
import com.SkillShare.SkillShare.Modelo.Mensagem; 
import com.SkillShare.SkillShare.Modelo.Usuario; 
import com.SkillShare.SkillShare.service.GrupoEstudoService; 
import com.SkillShare.SkillShare.service.MatchService; 
import com.SkillShare.SkillShare.service.MaterialService; 
import com.SkillShare.SkillShare.service.MensagemService; 
import com.SkillShare.SkillShare.service.PostagemService; 
import com.SkillShare.SkillShare.service.UsuarioService; 
import com.SkillShare.SkillShare.service.VerificacaoService; 

import jakarta.servlet.ServletException; 
import jakarta.servlet.http.HttpSession; 
import jakarta.validation.Valid; 
import jakarta.validation.constraints.Size; 
import lombok.RequiredArgsConstructor; 
import lombok.extern.slf4j.Slf4j; 

@Slf4j
@Controller
@RequiredArgsConstructor 
public class UsuarioController {

    private final UsuarioService usuarioService;

    private final GrupoEstudoService grupoEstudoService; 

    private final MaterialService materialService; 

    private final PostagemService postagemService; 

    private final VerificacaoService verificacaoService; 

    private final MatchService matchService; 

    private final MensagemService mensagemService; 


    
    // Exibe o perfil publico e informa se ele ja esta conectado ao usuario logado
    @GetMapping("/usuario/{id}")
    public String usuarioPublico(@PathVariable int id, Model model, Authentication authentication) {

        Usuario usuarioAtual = usuarioService.buscarUsuarioAtual(authentication);

        model.addAttribute("currentUser", usuarioAtual); 

        model.addAttribute("usuario", usuarioService.buscarPorId(id)); 

        if (usuarioAtual != null) {

            var conectados = mensagemService.listarConectadosIds(usuarioAtual.getIdUsuario()); 

            model.addAttribute("estaConectado", conectados.contains(id)); 
        } else {

            model.addAttribute("estaConectado", false); 
        }

        return "UsuarioPublico"; 

    }



    // Envia um pedido de conexao para outro usuario, se ainda nao existir conexao
    @PostMapping("/match/conectar/{idDestinatario}")
    public String conectarMatch(@PathVariable int idDestinatario, Authentication authentication) {


        Usuario atual = usuarioService.buscarUsuarioAtual(authentication); 

        Usuario destino = usuarioService.buscarPorId(idDestinatario); 

        if (atual == null)
            return "redirect:/login"; 

        if (destino == null)
            return "redirect:/match";

        var conectados = mensagemService.listarConectadosIds(atual.getIdUsuario()); 

        if (!conectados.contains(idDestinatario)) {

            Mensagem msg = new Mensagem();

            msg.setTexto(""); 

            msg.setDataEnvio(LocalDateTime.now()); 

            msg.setLida(false); 

            msg.setTipo("CONEXAO_PENDENTE");

            msg.setRemetente(atual); 

            msg.setDestinatario(destino); 

            mensagemService.cadastrarMensagem(msg); 
        }

        return "redirect:/chat/" + idDestinatario;
    }



    // Exibe a pagina de perfil do usuario logado
    @GetMapping("/perfil")
    public String perfil(Model model, Authentication authentication) {
        

        model.addAttribute("currentUser", usuarioService.buscarUsuarioAtual(authentication)); 

        return "Perfil"; 
    }



    // Exibe a pagina de edicao do perfil de aprendizado
    @GetMapping("/perfil-aprendizado")
    public String perfilAprendizado(Model model, Authentication authentication) {

        Usuario usuarioAtual = usuarioService.buscarUsuarioAtual(authentication);

        if (usuarioAtual == null)
            return "redirect:/login";

        model.addAttribute("currentUser", usuarioAtual);

        return "PerfilAprendizado";
    }

    // Exibe a pagina de configuracoes da conta
    @GetMapping("/configuracao-conta")
    public String configuracaoConta(Model model, Authentication authentication) {

        model.addAttribute("currentUser", usuarioService.buscarUsuarioAtual(authentication));

        return "ConfiguracaoConta";
    }

    // Salva alteracoes de nome e e-mail do usuario logado
    @PostMapping("/usuarios/alterar-dados")
    public String alterarDados(@RequestParam("nome") String nome, @RequestParam("email") String email,
            Authentication authentication, RedirectAttributes redirectAttributes) {

        Usuario usuarioAtual = usuarioService.buscarUsuarioAtual(authentication);

        if (usuarioAtual == null)
            return "redirect:/login";

        usuarioService.alterarNomeEmail(usuarioAtual, nome.trim(), email.trim());

        redirectAttributes.addFlashAttribute("sucesso", "Dados atualizados com sucesso.");

        return "redirect:/configuracao-conta";
    }

    // Processa a troca de senha do usuario logado
    @PostMapping("/usuarios/alterar-senha")
    public String alterarSenha(@RequestParam("senhaAtual") String senhaAtual,

            @RequestParam("novaSenha") String novaSenha, @RequestParam("confirmarSenha") String confirmarSenha,

            Authentication authentication, RedirectAttributes redirectAttributes) {

        Usuario usuarioAtual = usuarioService.buscarUsuarioAtual(authentication);

        if (usuarioAtual == null)

            return "redirect:/login";

        if (!novaSenha.equals(confirmarSenha)) {

            redirectAttributes.addFlashAttribute("erroSenha", "As senhas nao conferem.");

            return "redirect:/configuracao-conta#seguranca";
        }
        if (novaSenha.length() < 6) {

            redirectAttributes.addFlashAttribute("erroSenha", "A nova senha deve ter pelo menos 6 caracteres.");

            return "redirect:/configuracao-conta#seguranca";
        }

        boolean ok = usuarioService.alterarSenha(usuarioAtual, senhaAtual, novaSenha);

        redirectAttributes.addFlashAttribute(

                ok ? "sucessoSenha" : "erroSenha",
                ok ? "Senha alterada com sucesso." : "Senha atual incorreta.");

        return "redirect:/configuracao-conta#seguranca";

    }

    // Exibe sugestoes de usuarios compativeis para conexao
    @GetMapping("/match")
    public String match(Model model, Authentication authentication) {

        Usuario usuarioAtual = usuarioService.buscarUsuarioAtual(authentication);

        model.addAttribute("currentUser", usuarioAtual);

        model.addAttribute("resultadosMatch", usuarioAtual != null
                ? matchService.calcularMatches(usuarioAtual, usuarioService.listarUsuarios())

                : new ArrayList<>());

        if (usuarioAtual != null) {

            model.addAttribute("conectadosIds", mensagemService.listarConectadosIds(usuarioAtual.getIdUsuario()));
        } else {

            model.addAttribute("conectadosIds", java.util.Collections.emptySet());
        }

        return "Match";
    }

    // Exibe resultados de busca por usuarios, grupos, materiais e postagens
    @GetMapping("/busca")
    public String busca(@RequestParam(value = "q", defaultValue = "") String q,
            @RequestParam(value = "tipo", defaultValue = "todos") String tipo,
            Model model, Authentication authentication) {

        Usuario usuarioAtual = usuarioService.buscarUsuarioAtual(authentication);

        model.addAttribute("currentUser", usuarioAtual);

        model.addAttribute("termoBusca", q);

        model.addAttribute("tipoAtivo", tipo);

        String termo = q.toLowerCase().trim();

        boolean vazio = termo.isEmpty();

        boolean todos = tipo.equals("todos");

        if (usuarioAtual != null) {

            model.addAttribute("conectadosIds", mensagemService.listarConectadosIds(usuarioAtual.getIdUsuario()));
        } else {

            model.addAttribute("conectadosIds", java.util.Collections.emptySet());
        }

        if (todos || tipo.equals("membros")) {

            var lista = usuarioService.listarUsuarios();

            model.addAttribute("usuarios", vazio ? lista
                    : lista.stream()

                            .filter(u -> u.getNome() != null && u.getNome().toLowerCase().contains(termo))

                            .collect(Collectors.toCollection(ArrayList::new)));
        }
        if (todos || tipo.equals("grupos")) {

            model.addAttribute("grupos", vazio
                    ? grupoEstudoService.listarGruposEstudo()

                    : grupoEstudoService.buscarGruposPorNome(q));
        }
        if (todos || tipo.equals("materiais")) {

            var lista = materialService.listarMateriais();

            model.addAttribute("materiais", vazio ? lista
                    : lista.stream()

                            .filter(m -> m.getTitulo() != null && m.getTitulo().toLowerCase().contains(termo))

                            .collect(Collectors.toCollection(ArrayList::new)));
        }
        if (todos || tipo.equals("postagens")) {

            var lista = postagemService.listarPostagens();

            model.addAttribute("postagens", vazio ? lista
                    : lista.stream()

                            .filter(p -> p.getConteudo() != null && p.getConteudo().toLowerCase().contains(termo))

                            .collect(Collectors.toCollection(ArrayList::new)));
        }
        return "Busca";
    }

    // Retorna a foto de perfil de um usuario especifico
    @GetMapping("/usuarios/{id}/foto")
    @ResponseBody
    public ResponseEntity<byte[]> getFotoUsuario(@PathVariable int id) {

        return servirFoto(materialService.buscarFotoPerfilPorUsuario(id));
    }

    // Retorna todos os usuarios cadastrados 
    @GetMapping("/usuarios")
    @ResponseBody
    public ArrayList<Usuario> listarUsuarios() {

        return usuarioService.listarUsuarios();
    }

    // Retorna os dados do usuario logado
    @GetMapping("/usuario")
    @ResponseBody
    public ResponseEntity<Usuario> obterUsuarioAtual(Authentication authentication) {

        Usuario usuarioAtual = usuarioService.buscarUsuarioAtual(authentication);

        if (usuarioAtual == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        return ResponseEntity.ok(usuarioAtual);
    }

    // Inicia o cadastro e envia o codigo de verificacao por e-mail
    @PostMapping("/usuarios")
    @ResponseBody
    @ResponseStatus(HttpStatus.CREATED)
    public void cadastrarUsuario(@Valid @RequestBody Usuario usuario) {

        usuarioService.verificarEmailDisponivel(usuario.getEmail());

        verificacaoService.enviar(usuario);
    }

    // Confirma o codigo enviado por e-mail e conclui o cadastro
    @PostMapping("/usuarios/verificacao/confirmar")
    @ResponseBody
    public ResponseEntity<Void> confirmarVerificacao(@RequestBody CodigoVerificacaoRequest request,
            HttpServletRequest httpRequest) {

        return switch (verificacaoService.confirmar(request.email(), request.codigo())) {

            case OK -> {

                Usuario pendente = verificacaoService.extrairPendente(request.email());

                if (pendente != null) {

                    String senhaRaw = pendente.getSenha();

                    usuarioService.cadastrarUsuario(pendente);

                    try {

                        HttpSession sessaoAnterior = httpRequest.getSession(false);

                        if (sessaoAnterior != null)
                            sessaoAnterior.invalidate();

                        httpRequest.login(request.email(), senhaRaw);
                    } catch (ServletException e) {

                        log.warn("Login automatico falhou para {}: {}", request.email(), e.getMessage());

                        yield ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
                    }
                }
                yield ResponseEntity.ok().build();
            }
            case BLOQUEADO -> ResponseEntity.status(429).build();

            case INVALIDO -> ResponseEntity.badRequest().build();
        };
    }

    // Atualiza dados de um usuario, permitindo apenas o proprio usuario logado
    @PutMapping("/usuarios/{id}")
    @ResponseBody
    public ResponseEntity<Void> alterarUsuario(@PathVariable int id,
            @Valid @RequestBody Usuario usuario, Authentication authentication) {

        Usuario usuarioAtual = usuarioService.buscarUsuarioAtual(authentication);

        if (usuarioAtual == null || !usuarioAtual.getIdUsuario().equals(id))

            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();

        usuario.setIdUsuario(id);

        usuarioService.alterarUsuario(usuario);

        return ResponseEntity.ok().build();
    }

    // Atualiza habilidades, interesses e dificuldades do perfil de aprendizado
    @PatchMapping("/usuarios/perfil-aprendizado")
    @ResponseBody
    public ResponseEntity<Usuario> alterarPerfilAprendizado(@Valid @RequestBody PerfilAprendizadoRequest req,
            Authentication authentication) {

        Usuario usuarioAtual = usuarioService.buscarUsuarioAtual(authentication);

        if (usuarioAtual == null)
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();

        Usuario atualizado = usuarioService.alterarPerfilAprendizado(
                usuarioAtual, req.habilidades(), req.interesses(), req.dificuldades());

        return ResponseEntity.ok(atualizado);
    }

    // Faz upload da foto de perfil do usuario logado
    @PostMapping(value = "/usuarios/foto", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseBody
    public ResponseEntity<Void> uploadFotoPerfil(@RequestParam("arquivo") MultipartFile arquivo,
            Authentication authentication) {

        Usuario usuarioAtual = usuarioService.buscarUsuarioAtual(authentication);

        if (usuarioAtual == null)
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();

        if (arquivo.isEmpty())
            return ResponseEntity.badRequest().build();

        try {

            materialService.salvarFotoPerfil(usuarioAtual.getIdUsuario(),
                    arquivo.getContentType(), arquivo.getBytes());

            return ResponseEntity.ok().build();
        } catch (Exception e) {

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Retorna a foto de perfil do usuario logado
    @GetMapping("/usuarios/foto")
    @ResponseBody
    public ResponseEntity<byte[]> getFotoPerfil(Authentication authentication) {

        Usuario usuarioAtual = usuarioService.buscarUsuarioAtual(authentication);

        if (usuarioAtual == null)
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();

        return servirFoto(materialService.buscarFotoPerfilPorUsuario(usuarioAtual.getIdUsuario()));
    }

    // Remove a foto de perfil do usuario logado
    @PostMapping("/usuarios/foto/excluir")
    @ResponseBody
    public ResponseEntity<Void> excluirFotoPerfil(Authentication authentication) {

        Usuario usuarioAtual = usuarioService.buscarUsuarioAtual(authentication);

        if (usuarioAtual == null)
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();

        materialService.excluirFotoPerfil(usuarioAtual.getIdUsuario());

        return ResponseEntity.noContent().build();
    }

    // Exclui a conta do usuario logado e encerra a sessao
    @DeleteMapping("/usuarios/{id}")
    public String excluirUsuario(@PathVariable int id, Authentication authentication,
                                 HttpServletRequest request) {

        Usuario usuarioAtual = usuarioService.buscarUsuarioAtual(authentication);

        if (usuarioAtual == null || !usuarioAtual.getIdUsuario().equals(id))
            return "redirect:/configuracao-conta";

        usuarioService.excluirUsuario(id);

        var session = request.getSession(false);
        if (session != null) session.invalidate();

        return "redirect:/login?contaExcluida";
    }

    // Monta a resposta HTTP para exibir uma foto salva como Material
    private ResponseEntity<byte[]> servirFoto(Material foto) {

        if (foto == null || foto.getArquivo() == null)
            return ResponseEntity.notFound().build();

        try {

            byte[] bytes = foto.getArquivo().getBytes(1, (int) foto.getArquivo().length());

            String mime = foto.getTitulo() != null ? foto.getTitulo() : "image/jpeg";

            return ResponseEntity.ok()

                    .contentType(MediaType.parseMediaType(mime))

                    .header(HttpHeaders.CACHE_CONTROL, "no-cache, no-store, must-revalidate")

                    .body(bytes);
        } catch (Exception ignored) {

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    record CodigoVerificacaoRequest(String email, String codigo) {
    }

    record PerfilAprendizadoRequest(
            @Size(max = 500) String habilidades,
            @Size(max = 500) String interesses,
            @Size(max = 500) String dificuldades) {
    }
}
