package com.SkillShare.SkillShare.service;

import java.nio.ByteBuffer;
import java.security.SecureRandom;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

// Classe utilitaria para cifrar e decifrar textos usando AES/GCM.
public final class CriptografiaUtil {

    private static final String ALGORITMO = "AES/GCM/NoPadding";

    private static final int IV_LENGTH   = 12;

    private static final int TAG_LENGTH  = 128;

    private CriptografiaUtil() {}


    // Cifra um texto usando uma chave AES em Base64 e retorna o resultado tambem em Base64.
    public static String cifrar(String texto, String chaveBase64) {

        try {

            SecretKey chave = chaveDeBase64(chaveBase64); // Converte a chave Base64 para uma chave AES utilizavel pelo Cipher

            byte[] iv = new byte[IV_LENGTH]; // Cria o IV, que funciona como um valor unico para cada criptografia

            new SecureRandom().nextBytes(iv); // Preenche o IV com bytes aleatorios para deixar a cifragem mais segura

            Cipher cipher = Cipher.getInstance(ALGORITMO); // Cria o objeto responsavel por executar o AES/GCM

            cipher.init(Cipher.ENCRYPT_MODE, chave, new GCMParameterSpec(TAG_LENGTH, iv)); // Inicializa o Cipher em modo de criptografia

            byte[] criptografado = cipher.doFinal(texto.getBytes("UTF-8")); // Criptografa o texto convertido para bytes UTF-8

            ByteBuffer buf = ByteBuffer.allocate(IV_LENGTH + criptografado.length); // Reserva espaco para guardar IV + texto criptografado

            buf.put(iv); // Coloca o IV no comeco para ele poder ser recuperado na decifragem

            buf.put(criptografado); // Coloca os bytes criptografados logo depois do IV

            return Base64.getEncoder().encodeToString(buf.array()); // Retorna tudo em Base64 para salvar/enviar como String

        } catch (Exception e) {

            return texto;
        }
    }

    
    // Decifra um texto cifrado em Base64 usando a chave AES informada.
    // Se o texto for antigo, nao cifrado ou invalido, devolve o proprio valor para nao quebrar a leitura.
    public static String decifrar(String cifrado, String chaveBase64) {

        if (cifrado == null) return null;

        try {

            SecretKey chave = chaveDeBase64(chaveBase64); // Converte a chave Base64 para uma chave AES utilizavel pelo Cipher

            byte[] dados = Base64.getDecoder().decode(cifrado); // Transforma o texto Base64 salvo de volta em bytes

            if (dados.length < IV_LENGTH) return cifrado; // Se nao houver tamanho suficiente para o IV, trata como texto antigo nao cifrado

            ByteBuffer buf = ByteBuffer.wrap(dados); // Prepara os bytes para ler primeiro o IV e depois o conteudo criptografado

            byte[] iv = new byte[IV_LENGTH];
            buf.get(iv); // Recupera o IV que foi salvo no inicio durante a criptografia

            byte[] criptografado = new byte[buf.remaining()];
            buf.get(criptografado); // Recupera o restante dos bytes, que representam o texto criptografado

            Cipher cipher = Cipher.getInstance(ALGORITMO); // Cria o objeto responsavel por executar o AES/GCM
            cipher.init(Cipher.DECRYPT_MODE, chave, new GCMParameterSpec(TAG_LENGTH, iv)); // Inicializa o Cipher em modo de decifragem

            return new String(cipher.doFinal(criptografado), "UTF-8"); // Decifra os bytes e devolve o texto original em UTF-8

        } catch (Exception e) {

            return cifrado;
        }
    }



    // Converte a chave salva em Base64 para o formato SecretKey usado pelo AES.
    private static SecretKey chaveDeBase64(String base64) {

        byte[] bytes = Base64.getDecoder().decode(base64); // Decodifica a chave Base64 para recuperar os bytes originais
        return new SecretKeySpec(bytes, "AES"); // Monta a chave AES que sera usada para cifrar ou decifrar
        
    }



}
