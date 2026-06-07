package com.SkillShare.SkillShare.service;

import java.util.ArrayList; 
import java.util.Arrays; 
import java.util.HashSet; 
import java.util.List;
import java.util.Set; 
import java.util.stream.Collectors; 

import org.springframework.stereotype.Service; 

import com.SkillShare.SkillShare.Modelo.Usuario; 

@Service
public class MatchService {

    // Guarda o resultado calculado para um candidato: o usuario sugerido, a pontuacao
    // total e os motivos que explicam por que ele apareceu no match.
    public static class ResultadoMatch {

        public final Usuario usuario; 

        public final int pontuacao; 

        public final String podeTeEnsinar; 

        public final String voceEnsinaEle; 

        public final String interessesComuns; 

        public ResultadoMatch(Usuario usuario, int pontuacao,
                              String podeTeEnsinar, String voceEnsinaEle,
                              String interessesComuns) {

            this.usuario          = usuario;

            this.pontuacao        = pontuacao;

            this.podeTeEnsinar    = podeTeEnsinar;

            this.voceEnsinaEle    = voceEnsinaEle;

            this.interessesComuns = interessesComuns;
        }

        // Indica se houve pelo menos uma coincidencia real entre habilidades, objetivos ou interesses.
        public boolean temCompatibilidade() {

            return pontuacao > 0;

        }

    }

    // Calcula os matches do usuario atual comparando seu perfil de aprendizado com todos os outros usuarios.
    // A ideia central e cruzar "o que eu quero aprender" com "o que o outro sabe ensinar" e tambem o inverso.
    public List<ResultadoMatch> calcularMatches(Usuario usuarioAtual, ArrayList<Usuario> todosUsuarios) {

        // Normaliza os campos do usuario atual para conjuntos em minusculo, sem espacos e sem repeticoes.
        Set<String> minhasHabilidades  = termos(usuarioAtual.getHabilidades());

        Set<String> meusInteresses     = termos(usuarioAtual.getInteresses());

        Set<String> minhasDificuldades = termos(usuarioAtual.getDificuldades());


        // Objetivos representam tudo que o usuario quer aprender ou precisa de ajuda.
        Set<String> meusObjetivos      = unir(meusInteresses, minhasDificuldades);

        List<ResultadoMatch> resultados = new ArrayList<>();


        // Cada usuario cadastrado vira um candidato, exceto o proprio usuario atual.
        for (Usuario outro : todosUsuarios) {

            if (outro.getIdUsuario().equals(usuarioAtual.getIdUsuario())) continue;

            Set<String> habilidadesOutro  = termos(outro.getHabilidades());

            Set<String> interessesOutro   = termos(outro.getInteresses());

            Set<String> dificuldadesOutro = termos(outro.getDificuldades());

            Set<String> objetivosOutro    = unir(interessesOutro, dificuldadesOutro);

            // O outro ajuda voce quando as habilidades dele aparecem nos seus objetivos.
            Set<String> podeTeEnsinarSet    = intersecao(habilidadesOutro, meusObjetivos);

            // Voce ajuda o outro quando suas habilidades aparecem nos objetivos dele.
            Set<String> voceEnsinaEleSet    = intersecao(minhasHabilidades, objetivosOutro);

            // Interesses em comum nao indicam ensino direto, mas aumentam afinidade entre os perfis.
            Set<String> interessesComunsSet = intersecao(meusInteresses, interessesOutro);



            // Peso do algoritmo:
            // - ensinar/aprender vale 2 pontos, porque gera troca direta de conhecimento;
            // - interesse comum vale 1 ponto, porque e afinidade, mas nao necessariamente ajuda direta.
            int pontuacao =
                    podeTeEnsinarSet.size()    * 2 +
                    voceEnsinaEleSet.size()    * 2 +
                    interessesComunsSet.size() * 1;

            resultados.add(new ResultadoMatch(
                    outro,
                    pontuacao,
                    formatar(podeTeEnsinarSet),
                    formatar(voceEnsinaEleSet),
                    formatar(interessesComunsSet)


            ));
        }

        // Maior pontuacao aparece primeiro na tela de match.
        resultados.sort((a, b) -> b.pontuacao - a.pontuacao);

        return resultados;

    }
    

    // Transforma um campo de texto em conjunto de termos comparaveis.
    // Aceita virgula, ponto-e-virgula e quebra de linha para facilitar o preenchimento no perfil.
    private Set<String> termos(String campo) {

        if (campo == null || campo.isBlank()) return new HashSet<>();

        return Arrays.stream(campo.split("[,;\\n]"))

                .map(String::trim)

                .map(String::toLowerCase)

                .filter(s -> !s.isEmpty())

                .collect(Collectors.toCollection(HashSet::new));

    }


    // Junta dois conjuntos sem duplicar termos; usado para formar os objetivos de aprendizado.
    private Set<String> unir(Set<String> a, Set<String> b) {

        Set<String> r = new HashSet<>(a);

        r.addAll(b);

        return r;
    }


    // Retorna apenas os termos que existem nos dois conjuntos; e o coracao da comparacao de perfis.
    private Set<String> intersecao(Set<String> a, Set<String> b) {

        return a.stream()

                .filter(b::contains)

                .collect(Collectors.toCollection(HashSet::new));
    }


    // Converte o conjunto calculado em texto legivel para mostrar o motivo do match na interface.
    private String formatar(Set<String> conjunto) {

        return conjunto.stream()

                .sorted()

                .map(s -> Character.toUpperCase(s.charAt(0)) + s.substring(1))

                .collect(Collectors.joining(", "));
    }
}
