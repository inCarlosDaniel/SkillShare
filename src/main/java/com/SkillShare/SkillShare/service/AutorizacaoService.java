package com.SkillShare.SkillShare.service; 

import java.sql.SQLException; 
import java.util.Collections; 

import org.springframework.security.core.userdetails.User; 
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService; 
import org.springframework.security.core.userdetails.UsernameNotFoundException; 

import org.springframework.stereotype.Service; 

import com.SkillShare.SkillShare.Modelo.Usuario; 
import com.SkillShare.SkillShare.Persistencia.UsuarioDAO; 

@Service
public class AutorizacaoService implements UserDetailsService {

    // Metodo chamado pelo Spring Security durante o login.
    // Ele busca o usuario no banco por e-mail ou nome e monta um UserDetails para autenticacao.
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        try {

            Usuario usuario = UsuarioDAO.buscarPorEmailOuNome(username);

            if (usuario == null) {

                throw new UsernameNotFoundException("Usuario nao encontrado");
            }

            // O Spring Security usa esse objeto para comparar a senha digitada com a senha salva.
          
            return User

                    .withUsername(usuario.getEmail())

                    .password(usuario.getSenha())

                    .authorities(Collections.emptyList())

                    .build();
                    
        } catch (SQLException e) {

            throw new UsernameNotFoundException("Erro ao buscar usuario", e);

        }

    }

}
