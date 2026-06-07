package com.SkillShare.SkillShare.service; 

import java.sql.SQLException; 
import java.util.ArrayList; 
import java.util.UUID; 

import org.springframework.security.core.Authentication; 
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken; 
import org.springframework.security.crypto.password.PasswordEncoder; 
import org.springframework.stereotype.Service; 

import com.SkillShare.SkillShare.Modelo.Usuario; 
import com.SkillShare.SkillShare.Persistencia.UsuarioDAO; 
import com.SkillShare.SkillShare.controller.EmailJaCadastradoException; 

import lombok.RequiredArgsConstructor; 
import lombok.extern.slf4j.Slf4j; 

@Slf4j 
@Service // Registra esta classe como serviço no contexto Spring
@RequiredArgsConstructor 
public class UsuarioService {

    private final PasswordEncoder passwordEncoder; 

    public ArrayList<Usuario> listarUsuarios() { // Método que retorna todos os usuários cadastrados no banco

        try { // Tenta executar a consulta ao banco de dados

            return UsuarioDAO.listarUsuarios(); // Delega a consulta ao DAO e retorna a lista completa de usuários

        } catch (SQLException e) { // Captura erros 

            log.error("Erro ao listar usuarios: {}", e.getMessage()); // Registra o erro de listagem no log

            throw new RuntimeException("Erro ao listar usuarios", e); // Relança como exceção não verificada para o controller tratar
        }
    }

    public Usuario buscarPorId(int id) { 

        return listarUsuarios().stream() 

                .filter(u -> u.getIdUsuario() != null && u.getIdUsuario() == id) // Filtra o usuário cujo ID corresponde ao buscado

                .findFirst() // Retorna o primeiro resultado encontrado 

                .orElse(null); // Retorna nulo se nenhum usuário corresponder ao ID
    }

    public Usuario buscarPorEmailOuNome(String identificador) { 

        try { // Tenta executar a consulta ao banco

            return UsuarioDAO.buscarPorEmailOuNome(identificador); // Delega a busca ao DAO e retorna o usuário encontrado
        } catch (SQLException e) { // Captura erros de SQL durante a busca

            throw new RuntimeException("Erro ao buscar usuario", e); // Relança como exceção não verificada
        }
    }

    public Usuario buscarUsuarioAtual(Authentication authentication) { // Método que resolve o usuário logado a partir do objeto de autenticação do Spring Security

        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getName())) { // Verifica se a autenticação é válida e o usuário não é anônimo

            return null; // Retorna null para sessões não autenticadas ou anônimas
        }

        if (authentication instanceof OAuth2AuthenticationToken oauthToken) { // Verifica se a autenticação é via OAuth2 (Google, GitHub etc.)

            String email = oauthToken.getPrincipal().getAttribute("email"); // Extrai o e-mail do usuário a partir dos atributos do principal OAuth2

            if (email == null || email.isBlank()) { // Verifica se o e-mail foi obtido com sucesso

                return null; // Retorna null se o e-mail do OAuth2 estiver ausente
            }

            return buscarPorEmailOuNome(email); // Busca o usuário no banco pelo e-mail obtido do OAuth2
        }

        return buscarPorEmailOuNome(authentication.getName()); // Para autenticação padrão, busca pelo nome de usuário (e-mail/login)
    }

    public Usuario buscarPrimeiroDestinatario(Usuario usuarioAtual) { // Método que retorna o primeiro usuário diferente do usuário atual para iniciar uma conversa

        ArrayList<Usuario> usuarios = listarUsuarios(); // Carrega a lista completa de usuários cadastrados

        for (Usuario usuario : usuarios) { // Itera cada usuário da lista

            if (usuarioAtual == null || !usuario.getIdUsuario().equals(usuarioAtual.getIdUsuario())) { // Verifica se o usuário da iteração é diferente do usuário atual

                return usuario; // Retorna o primeiro usuário encontrado que não seja o usuário atual
            }
        }
        return null; // Retorna null se não houver outros usuários cadastrados
    }

    public void verificarEmailDisponivel(String email) { // Método que lança exceção se o e-mail já estiver cadastrado no sistema

        try { // Tenta consultar o banco para verificar a existência do e-mail

            if (UsuarioDAO.buscarPorEmail(email) != null) { // Verifica se já existe um usuário com o e-mail informado

                throw new EmailJaCadastradoException(); // Lança exceção customizada indicando que o e-mail já está em uso
            }
        } catch (SQLException e) { // Captura erros de SQL durante a verificação

            throw new RuntimeException("Erro ao verificar email", e); // Relança como exceção não verificada
        }
    }

    public void cadastrarUsuario(Usuario usuario) { // Método que persiste um novo usuário no banco com a senha criptografada

        try { // Tenta realizar o cadastro do usuário

            usuario.setSenha(passwordEncoder.encode(usuario.getSenha())); // Codifica a senha em texto puro para armazenamento seguro com hash BCrypt

            UsuarioDAO.inserirUsuario(usuario); // Delega a inserção do usuário ao DAO

            log.info("Usuario '{}' cadastrado com sucesso", usuario.getNome()); // Registra o sucesso do cadastro no log informativo
        } catch (SQLException e) { // Captura erros de SQL durante o cadastro

            log.error("Erro ao cadastrar usuario '{}': {}", usuario.getNome(), e.getMessage()); // Registra o nome do usuário e o erro no log

            throw new RuntimeException("Erro ao cadastrar usuario", e); // Relança como exceção não verificada
        }
    }

    public boolean cadastrarUsuarioOAuthSeNaoExistir(String nome, String email) { // Método que cadastra automaticamente usuários autenticados via OAuth2 caso ainda não existam

        try { // Tenta verificar a existência do usuário e cadastrá-lo se necessário

            if (UsuarioDAO.buscarPorEmail(email) != null) { // Verifica se já existe um usuário cadastrado com o e-mail OAuth2

                return false; // Retorna false indicando que o usuário já existia (não foi criado)
            }
            Usuario usuario = new Usuario(); // Cria uma nova instância de Usuario para o cadastro via OAuth2

            usuario.setNome(nome); // Define o nome obtido do provedor OAuth2

            usuario.setEmail(email); // Define o e-mail obtido do provedor OAuth2

            usuario.setSenha(passwordEncoder.encode(UUID.randomUUID().toString())); // Gera uma senha aleatória segura pois o login será sempre via OAuth2

            usuario.setHabilidades(""); // Inicializa o campo de habilidades como vazio

            usuario.setInteresses(""); // Inicializa o campo de interesses como vazio

            usuario.setDificuldades(""); // Inicializa o campo de dificuldades como vazio

            UsuarioDAO.inserirUsuario(usuario); // Persiste o novo usuário no banco de dados

            return true; // Retorna true indicando que um novo usuário foi criado com sucesso
        } catch (SQLException e) { // Captura erros de SQL durante o processo

            throw new RuntimeException("Erro ao cadastrar usuario com Google", e); // Relança como exceção não verificada
        }
    }

    public void alterarUsuario(Usuario usuario) { // Método que atualiza os dados de um usuário existente com senha re-criptografada

        try { // Tenta executar a atualização no banco

            String senha = usuario.getSenha();
            if (senha != null && !senha.startsWith("$2")) {
                usuario.setSenha(passwordEncoder.encode(senha));
            }

            UsuarioDAO.alterarUsuario(usuario); // Delega a operação de UPDATE ao DAO
        } catch (SQLException e) { // Captura erros de SQL durante a alteração

            throw new RuntimeException("Erro ao alterar usuario", e); // Relança como exceção não verificada
        }
    }

    public Usuario alterarPerfilAprendizado(Usuario usuarioAtual, String habilidades, String interesses,
            String dificuldades) { // Método que atualiza as informações de perfil de aprendizado do usuário e retorna o usuário atualizado

        try { // Tenta executar a atualização do perfil de aprendizado no banco

            UsuarioDAO.alterarPerfilAprendizado(
                    usuarioAtual.getIdUsuario(), // ID do usuário cujo perfil será atualizado
                    normalizarCampoPerfil(habilidades), // Habilidades normalizadas (sem espaços extras)
                    normalizarCampoPerfil(interesses), // Interesses normalizados (sem espaços extras)
                    normalizarCampoPerfil(dificuldades)); // Dificuldades normalizadas (sem espaços extras)

            return UsuarioDAO.buscarPorEmail(usuarioAtual.getEmail()); // Retorna o usuário atualizado buscando os dados frescos do banco
        } catch (SQLException e) { // Captura erros de SQL durante a atualização

            throw new RuntimeException("Erro ao alterar perfil de aprendizado", e); // Relança como exceção não verificada
        }
    }

    private String normalizarCampoPerfil(String valor) { // Método auxiliar que sanitiza campos de perfil retirando espaços e tratando nulos

        return valor == null ? "" : valor.trim(); // Retorna string vazia para nulos ou o valor sem espaços nas extremidades
    }

    public boolean alterarSenha(Usuario usuarioAtual, String senhaAtual, String novaSenha) { // Método que altera a senha do usuário após validar a senha atual

        if (!passwordEncoder.matches(senhaAtual, usuarioAtual.getSenha())) { // Verifica se a senha atual informada corresponde ao hash armazenado

            return false; // Retorna false indicando que a senha atual está incorreta
        }
        try { // Tenta persistir a nova senha no banco

            UsuarioDAO.alterarSenha(usuarioAtual.getIdUsuario(), passwordEncoder.encode(novaSenha)); // Atualiza a senha no banco com o hash da nova senha

            return true; // Retorna true indicando que a senha foi alterada com sucesso
        } catch (SQLException e) { // Captura erros de SQL durante a alteração da senha

            log.error("Erro ao alterar senha do usuario '{}': {}", usuarioAtual.getNome(), e.getMessage()); // Registra o nome do usuário e o erro no log

            throw new RuntimeException("Erro ao alterar senha", e); // Relança como exceção não verificada
        }
    }

    public void alterarNomeEmail(Usuario usuarioAtual, String nome, String email) { // Método que atualiza o nome e e-mail de um usuário existente

        try { // Tenta executar a atualização no banco

            UsuarioDAO.alterarNomeEmail(usuarioAtual.getIdUsuario(), nome, email); // Delega a atualização de nome e e-mail ao DAO passando o ID do usuário
        } catch (SQLException e) { // Captura erros de SQL durante a alteração

            throw new RuntimeException("Erro ao alterar nome/email", e); // Relança como exceção não verificada
        }
    }

    public void excluirUsuario(int id) { // Método que remove um usuário do banco pelo ID

        try { // Tenta executar a exclusão no banco

            UsuarioDAO.excluirUsuario(id); // Delega a operação de DELETE ao DAO passando o ID do usuário
        } catch (SQLException e) { // Captura erros de SQL durante a exclusão

            throw new RuntimeException("Erro ao excluir usuario", e); // Relança como exceção não verificada
        }
    }
}
