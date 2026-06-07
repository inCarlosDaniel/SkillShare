package com.SkillShare.SkillShare.service;

import java.sql.SQLException;
import java.util.ArrayList;

import org.springframework.stereotype.Service;

import com.SkillShare.SkillShare.Modelo.Material;
import com.SkillShare.SkillShare.Persistencia.MaterialDAO;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class MaterialService {

    // Busca todos os materiais cadastrados no sistema e transforma erros de banco em RuntimeException.
    public ArrayList<Material> listarMateriais() {

        try {

            return MaterialDAO.listarMateriais();
        } catch (SQLException e) {

            log.error("Erro ao listar materiais: {}", e.getMessage());

            throw new RuntimeException("Erro ao listar materiais", e);
        }
    }



    // Procura um material pelo ID dentro da lista geral de materiais.
    public Material buscarMaterialPorId(int id) {

        return listarMateriais().stream()

                .filter(m -> m.getidMaterial() != null && m.getidMaterial() == id)

                .findFirst()

                .orElse(null);
    }



    // Cadastra um novo material no banco de dados.
    public void cadastrarMaterial(Material material) {

        try {

            MaterialDAO.inserirMaterial(material);
        } catch (SQLException e) {

            log.error("Erro ao cadastrar material: {}", e.getMessage());

            throw new RuntimeException("Erro ao cadastrar material", e);
        }
    }



    // Lista apenas os materiais ligados a um grupo de estudo especifico.
    public ArrayList<Material> listarMateriaisPorGrupo(int idGrupo) {

        try {

            return MaterialDAO.listarMateriaisPorGrupo(idGrupo);
        } catch (SQLException e) {

            log.error("Erro ao listar materiais do grupo {}: {}", idGrupo, e.getMessage());

            throw new RuntimeException("Erro ao listar materiais do grupo", e);
        }
    }



    // Atualiza as informacoes de um material ja existente.
    public void alterarMaterial(Material material) {

        try {

            MaterialDAO.alterarMaterial(material);
        } catch (SQLException e) {

            log.error("Erro ao alterar material: {}", e.getMessage());

            throw new RuntimeException("Erro ao alterar material", e);
        }
    }



    // Remove um material do banco usando o ID recebido.
    public void excluirMaterial(int id) {

        try {

            MaterialDAO.excluirMaterial(id);
        } catch (SQLException e) {

            log.error("Erro ao excluir material {}: {}", id, e.getMessage());

            throw new RuntimeException("Erro ao excluir material", e);
        }
    }



    // Busca o material usado como foto de perfil de um usuario.
    public Material buscarFotoPerfilPorUsuario(int idUsuario) {

        try {

            return MaterialDAO.buscarFotoPerfilPorUsuario(idUsuario);
        } catch (SQLException e) {

            log.error("Erro ao buscar foto de perfil do usuario {}: {}", idUsuario, e.getMessage());

            throw new RuntimeException("Erro ao buscar foto de perfil", e);
        }
    }



    // Salva a imagem de um grupo com o tipo do arquivo e os bytes enviados pelo upload.
    public void salvarFotoGrupo(int idGrupo, int idCriador, String mimeType, byte[] bytes) {

        try {

            MaterialDAO.inserirFotoGrupo(idGrupo, idCriador, mimeType, bytes);
        } catch (SQLException e) {

            log.error("Erro ao salvar foto do grupo {}: {}", idGrupo, e.getMessage());

            throw new RuntimeException("Erro ao salvar foto do grupo", e);
        }
    }



    // Busca a foto/capa cadastrada para um grupo de estudo.
    public Material buscarFotoGrupoPorGrupo(int idGrupo) {

        try {

            return MaterialDAO.buscarFotoGrupoPorGrupo(idGrupo);
        } catch (SQLException e) {

            log.error("Erro ao buscar foto do grupo {}: {}", idGrupo, e.getMessage());

            throw new RuntimeException("Erro ao buscar foto do grupo", e);
        }
    }



    // Salva um arquivo enviado por usuario e retorna o ID gerado para esse material.
    public int salvarArquivo(int idUsuario, String titulo, String categoria, byte[] bytes) {

        try {

            return MaterialDAO.inserirArquivo(idUsuario, titulo, categoria, bytes);
        } catch (SQLException e) {

            log.error("Erro ao salvar arquivo '{}': {}", titulo, e.getMessage());

            throw new RuntimeException("Erro ao salvar arquivo", e);
        }
    }



    // Exclui a foto de perfil atual de um usuario.
    public void excluirFotoPerfil(int idUsuario) {

        try {

            MaterialDAO.excluirFotoPerfilPorUsuario(idUsuario);
        } catch (SQLException e) {

            log.error("Erro ao excluir foto de perfil do usuario {}: {}", idUsuario, e.getMessage());

            throw new RuntimeException("Erro ao excluir foto de perfil", e);
        }
    }



    // Substitui a foto de perfil: remove a antiga e grava a nova imagem enviada.
    public void salvarFotoPerfil(int idUsuario, String mimeType, byte[] bytes) {

        try {

            MaterialDAO.excluirFotoPerfilPorUsuario(idUsuario);

            MaterialDAO.inserirFotoPerfil(idUsuario, mimeType, bytes);
        } catch (SQLException e) {

            log.error("Erro ao salvar foto de perfil do usuario {}: {}", idUsuario, e.getMessage());

            throw new RuntimeException("Erro ao salvar foto de perfil", e);
        }
    }


    
}

