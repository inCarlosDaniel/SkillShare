package com.SkillShare.SkillShare.service;

import java.sql.SQLException;
import java.util.ArrayList;

import org.springframework.stereotype.Service;

import com.SkillShare.SkillShare.Modelo.Anexo;
import com.SkillShare.SkillShare.Persistencia.AnexoDAO;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class AnexoService {

    // Lista todos os anexos cadastrados no banco.
    public ArrayList<Anexo> listarAnexos() {

        try {

            return AnexoDAO.listarAnexos();
        } catch (SQLException e) {

            log.error("Erro ao listar anexos: {}", e.getMessage());

            throw new RuntimeException("Erro ao listar anexos", e);
        }
    }

    // Cadastra um anexo e vincula suas informacoes a uma postagem.
    public void cadastrarAnexo(Anexo anexo) {

        try {

            AnexoDAO.inserirAnexo(anexo);
        } catch (SQLException e) {

            log.error("Erro ao cadastrar anexo: {}", e.getMessage());

            throw new RuntimeException("Erro ao cadastrar anexo", e);
        }
    }

    // Atualiza os dados de um anexo existente, como nome, tipo, URL ou postagem.
    public void alterarAnexo(Anexo anexo) {

        try {

            AnexoDAO.alterarAnexo(anexo);
        } catch (SQLException e) {

            log.error("Erro ao alterar anexo: {}", e.getMessage());

            throw new RuntimeException("Erro ao alterar anexo", e);
        }
    }

    // Exclui um anexo pelo ID.
    public void excluirAnexo(int id) {

        try {

            AnexoDAO.excluirAnexo(id);
        } catch (SQLException e) {

            log.error("Erro ao excluir anexo {}: {}", id, e.getMessage());

            throw new RuntimeException("Erro ao excluir anexo", e);
        }
    }
}
