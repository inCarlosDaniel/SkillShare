package com.SkillShare.SkillShare.controller;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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

import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.SkillShare.SkillShare.Modelo.Mensagem;
import com.SkillShare.SkillShare.Modelo.Usuario;
import com.SkillShare.SkillShare.service.MensagemService;
import com.SkillShare.SkillShare.service.UsuarioService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class MensagemController {

    private final MensagemService mensagemService;

    private final UsuarioService usuarioService;

    // Redireciona o acesso generico do chat para a lista de conversas
    @GetMapping("/chat")
    public String chatRedirect() {

        return "redirect:/mensagens-lista";
    }

    // Exibe a conversa com um usuario especifico
    @GetMapping("/chat/{idDestinatario}")
    public String chat(@PathVariable int idDestinatario, Model model, Authentication authentication) {




        Usuario usuarioAtual = usuarioService.buscarUsuarioAtual(authentication);

        Usuario destinatario = usuarioService.buscarPorId(idDestinatario);

        if (usuarioAtual == null) return "redirect:/login";

        if (destinatario == null) return "redirect:/mensagens-lista";

        model.addAttribute("currentUser", usuarioAtual);

        model.addAttribute("destinatario", destinatario);

        model.addAttribute("mensagens",
                mensagemService.listarMensagensPorConversa(usuarioAtual.getIdUsuario(), idDestinatario));


        var conexaoPendente = mensagemService.buscarConexaoPendente(
                destinatario.getIdUsuario(), usuarioAtual.getIdUsuario());

        model.addAttribute("conexaoPendente", conexaoPendente);

        return "Chat";
    }

    // Aceita um pedido de conexao recebido
    @PostMapping("/conexao/{idMensagem}/aceitar")
    public String aceitarConexao(@PathVariable int idMensagem, Authentication authentication) {



        Usuario usuarioAtual = usuarioService.buscarUsuarioAtual(authentication);

        if (usuarioAtual == null) return "redirect:/login";


        var msg = mensagemService.listarMensagens().stream()

                .filter(m -> m.getIdMensagem() != null && m.getIdMensagem() == idMensagem)

                .findFirst().orElse(null);

        if (msg == null || msg.getDestinatario() == null ||
                !msg.getDestinatario().getIdUsuario().equals(usuarioAtual.getIdUsuario())) {

            return "redirect:/mensagens-lista";
        }

        mensagemService.aceitarConexao(idMensagem);

        return "redirect:/chat/" + msg.getRemetente().getIdUsuario();
    }

    // Recusa um pedido de conexao recebido
    @PostMapping("/conexao/{idMensagem}/recusar")
    public String recusarConexao(@PathVariable int idMensagem, Authentication authentication) {



        Usuario usuarioAtual = usuarioService.buscarUsuarioAtual(authentication);

        if (usuarioAtual == null) return "redirect:/login";

        var msg = mensagemService.listarMensagens().stream()

                .filter(m -> m.getIdMensagem() != null && m.getIdMensagem() == idMensagem)

                .findFirst().orElse(null);

        if (msg == null || msg.getDestinatario() == null ||
                !msg.getDestinatario().getIdUsuario().equals(usuarioAtual.getIdUsuario())) {

            return "redirect:/mensagens-lista";
        }

        mensagemService.recusarConexao(idMensagem);

        return "redirect:/mensagens-lista";
    }


    // Envia uma mensagem pelo formulario do chat
    @PostMapping("/chat/{idDestinatario}")
    public String salvarMensagem(@PathVariable int idDestinatario,
            @RequestParam("texto") String texto,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        Usuario usuarioAtual = usuarioService.buscarUsuarioAtual(authentication);

        Usuario destinatario = usuarioService.buscarPorId(idDestinatario);

        if (usuarioAtual == null || destinatario == null) {

            redirectAttributes.addFlashAttribute("erro", "Conversa nao encontrada.");

            return "redirect:/mensagens-lista";
        }

        Mensagem mensagem = new Mensagem();

        mensagem.setTexto(texto);

        mensagem.setDataEnvio(LocalDateTime.now());

        mensagem.setLida(false);

        mensagem.setRemetente(usuarioAtual);

        mensagem.setDestinatario(destinatario);

        mensagemService.cadastrarMensagem(mensagem);

        return "redirect:/chat/" + idDestinatario;
    }

    // Exibe a lista de conversas e pedidos pendentes do usuario
    @GetMapping("/mensagens-lista")
    public String mensagensLista(Model model, Authentication authentication) {



        Usuario currentUser = usuarioService.buscarUsuarioAtual(authentication);

        model.addAttribute("currentUser", currentUser);

        if (currentUser != null && currentUser.getIdUsuario() != null) {

            List<Mensagem> conversas = mensagemService.listarConversas(currentUser.getIdUsuario());

            model.addAttribute("mensagens", conversas);


            var pendenteIds = mensagemService.listarMensagens().stream()

                    .filter(m -> "CONEXAO_PENDENTE".equals(m.getTipo()))

                    .filter(m -> m.getDestinatario() != null
                            && currentUser.getIdUsuario().equals(m.getDestinatario().getIdUsuario()))

                    .map(m -> m.getRemetente() != null ? m.getRemetente().getIdUsuario() : null)

                    .filter(id -> id != null)

                    .collect(java.util.stream.Collectors.toSet());

            model.addAttribute("pendenteIds", pendenteIds);



            List<Usuario> parceiros = conversas.stream()

                    .map(m -> currentUser.getIdUsuario().equals(m.getRemetente().getIdUsuario())

                            ? m.getDestinatario()

                            : m.getRemetente())

                    .filter(u -> u != null && u.getIdUsuario() != null)

                    .collect(Collectors.toList());

            model.addAttribute("parceiros", parceiros);
        } else {

            model.addAttribute("mensagens", new ArrayList<>());

            model.addAttribute("parceiros", new ArrayList<>());

            model.addAttribute("pendenteIds", java.util.Collections.emptySet());
        }

        return "MensagensLista";
    }

    // Exibe notificacoes separando pedidos de conexao e mensagens
    @GetMapping("/notificacoes")
    public String notificacoes(Model model, Authentication authentication) {



        Usuario usuarioAtual = usuarioService.buscarUsuarioAtual(authentication);

        model.addAttribute("currentUser", usuarioAtual);

        if (usuarioAtual == null) return "redirect:/login";

        var todasComConexoes = mensagemService.listarMensagens().stream()

                .filter(m -> m.getDestinatario() != null
                        && usuarioAtual.getIdUsuario().equals(m.getDestinatario().getIdUsuario()))

                .sorted(java.util.Comparator.comparing(
                        com.SkillShare.SkillShare.Modelo.Mensagem::getDataEnvio,
                        java.util.Comparator.nullsLast(java.util.Comparator.reverseOrder())))

                .collect(java.util.stream.Collectors.toList());


        var conexoes = todasComConexoes.stream()

                .filter(m -> "CONEXAO_PENDENTE".equals(m.getTipo()))

                .collect(java.util.stream.Collectors.toList());

        var mensagens = todasComConexoes.stream()

                .filter(m -> "MENSAGEM".equals(m.getTipo()))

                .collect(java.util.stream.Collectors.toList());

        model.addAttribute("conexoes", conexoes);

        model.addAttribute("mensagens", mensagens);

        return "Notificacoes";
    }

    // Retorna o historico de conversa em JSON para o chat
    @GetMapping("/api/chat-historico/{idDestinatario}")
    @ResponseBody
    public List<java.util.Map<String, Object>> chatHistorico(@PathVariable int idDestinatario,
            Authentication authentication) {



        Usuario usuarioAtual = usuarioService.buscarUsuarioAtual(authentication);

        if (usuarioAtual == null) return new ArrayList<>();

        var msgs = mensagemService.listarMensagensPorConversa(
                usuarioAtual.getIdUsuario(), idDestinatario);

        List<java.util.Map<String, Object>> resultado = new ArrayList<>();

        for (Mensagem m : msgs) {

            var map = new java.util.LinkedHashMap<String, Object>();

            map.put("texto", m.getTexto());

            map.put("proprio", m.getRemetente() != null
                    && m.getRemetente().getIdUsuario().equals(usuarioAtual.getIdUsuario()));

            map.put("nome", m.getRemetente() != null ? m.getRemetente().getNome() : "Usuário");

            map.put("hora", m.getDataEnvio() != null
                    ? m.getDataEnvio().format(java.time.format.DateTimeFormatter.ofPattern("HH:mm")) : "");

            resultado.add(map);
        }

        return resultado;
    }

    // Retorna as mensagens do usuario logado em JSON
    @GetMapping("/mensagens")
    @ResponseBody
    public ArrayList<Mensagem> listarMensagens(Authentication authentication) {

        Usuario usuarioAtual = usuarioService.buscarUsuarioAtual(authentication);

        if (usuarioAtual == null) return new ArrayList<>();

        return mensagemService.listarMensagens().stream()
                .filter(m -> m.getRemetente() != null && m.getDestinatario() != null
                        && (usuarioAtual.getIdUsuario().equals(m.getRemetente().getIdUsuario())
                         || usuarioAtual.getIdUsuario().equals(m.getDestinatario().getIdUsuario())))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    // Cadastra uma mensagem pela API
    @PostMapping("/mensagens")
    @ResponseBody
    public ResponseEntity<Void> cadastrarMensagem(@RequestBody Mensagem mensagem, Authentication authentication) {

        Usuario usuarioAtual = usuarioService.buscarUsuarioAtual(authentication);

        if (usuarioAtual == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        mensagem.setRemetente(usuarioAtual);

        mensagemService.cadastrarMensagem(mensagem);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    // Atualiza uma mensagem pela API se o usuario for o remetente
    @PutMapping("/mensagens/{id}")
    @ResponseBody
    public ResponseEntity<Void> alterarMensagem(@PathVariable int id, @RequestBody Mensagem mensagem,
            Authentication authentication) {

        Usuario usuarioAtual = usuarioService.buscarUsuarioAtual(authentication);

        if (usuarioAtual == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        Mensagem existente = mensagemService.listarMensagens().stream()
                .filter(m -> m.getIdMensagem() != null && m.getIdMensagem() == id)
                .findFirst().orElse(null);

        if (existente == null || existente.getRemetente() == null
                || !existente.getRemetente().getIdUsuario().equals(usuarioAtual.getIdUsuario()))
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();

        mensagem.setIdMensagem(id);

        mensagemService.alterarMensagem(mensagem);

        return ResponseEntity.ok().build();
    }

    // Exclui uma mensagem pela API
    @DeleteMapping("/mensagens/{id}")
    @ResponseBody
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void excluirMensagem(@PathVariable int id) {


        mensagemService.excluirMensagem(id);
    }
}
