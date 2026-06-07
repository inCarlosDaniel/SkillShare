package com.SkillShare.SkillShare.service; 

import java.sql.SQLException; 
import java.util.ArrayList; 

import org.springframework.stereotype.Service; 

import com.SkillShare.SkillShare.Modelo.GrupoEstudo; 
import com.SkillShare.SkillShare.Persistencia.GrupoEstudoDAO; 

import lombok.extern.slf4j.Slf4j; 

@Slf4j 
@Service
public class GrupoEstudoService {

    // Lista todos os grupos cadastrados na plataforma
    public ArrayList<GrupoEstudo> listarGruposEstudo() {

        try { 

            return GrupoEstudoDAO.listarGruposEstudo();
            
        } catch (SQLException e) { 

            log.error("Erro ao listar grupos: {}", e.getMessage());

            throw new RuntimeException("Erro ao listar grupos de estudo", e); 
        }

    }



    // Cadastra um novo grupo de estudo no banco
    public void cadastrarGrupoEstudo(GrupoEstudo grupoEstudo) {

        try {

            GrupoEstudoDAO.inserirGrupoEstudo(grupoEstudo);

            log.info("Grupo '{}' cadastrado com sucesso (id={})", grupoEstudo.getNome(), grupoEstudo.getidGrupo());
        } catch (SQLException e) {

            log.error("Erro ao cadastrar grupo '{}': {} — causa: {}", grupoEstudo.getNome(), e.getMessage(), e.getCause() != null ? e.getCause().getMessage() : "n/a");

            throw new RuntimeException("Erro ao cadastrar grupo de estudo: " + e.getMessage(), e);
        }

    }



    // Atualiza os dados de um grupo existente
    public void alterarGrupoEstudo(GrupoEstudo grupoEstudo) {

        try {

            GrupoEstudoDAO.alterarGrupoEstudo(grupoEstudo);
        } catch (SQLException e) {

            throw new RuntimeException("Erro ao alterar grupo de estudo", e);
        }

    }



    // Busca um grupo especifico pelo ID
    public GrupoEstudo buscarGrupoEstudoPorId(int id) {

        try {

            return GrupoEstudoDAO.buscarGrupoEstudoPorId(id);
        } catch (SQLException e) {

            throw new RuntimeException("Erro ao buscar grupo de estudo", e);
        }

    }



    // Exclui um grupo pelo ID
    public void excluirGrupoEstudo(int id) {

        try {

            GrupoEstudoDAO.excluirGrupoEstudo(id);
        } catch (SQLException e) {

            throw new RuntimeException("Erro ao excluir grupo de estudo", e);
        }

    }



    // Lista os grupos dos quais um usuario participa
    public ArrayList<GrupoEstudo> listarGruposPorUsuario(int idUsuario) {

        try {

            return GrupoEstudoDAO.listarGruposPorUsuario(idUsuario);
        } catch (SQLException e) {

            throw new RuntimeException("Erro ao listar grupos do usuario", e);
        }

    }



    // Busca grupos pelo nome informado na barra de pesquisa
    public ArrayList<GrupoEstudo> buscarGruposPorNome(String nome) {

        try {

            return GrupoEstudoDAO.buscarGruposPorNome(nome);
        } catch (SQLException e) {

            throw new RuntimeException("Erro ao buscar grupos por nome", e);
        }

    }



    // Adiciona um usuario como membro de um grupo
    public void entrarNoGrupo(int idGrupo, int idUsuario) {

        try {

            GrupoEstudoDAO.entrarNoGrupo(idGrupo, idUsuario);

            log.info("Usuario {} entrou no grupo {}", idUsuario, idGrupo);
        } catch (SQLException e) {

            log.error("Erro ao entrar no grupo {}: {}", idGrupo, e.getMessage());

            throw new RuntimeException("Erro ao entrar no grupo", e);
        }
    }



}
