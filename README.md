# SkillShare

Rede social acadêmica desenvolvida para conectar estudantes através da troca de conhecimentos, monitorias informais e colaboração entre usuários com diferentes habilidades. O projeto busca facilitar o aprendizado coletivo por meio de uma plataforma web moderna, dinâmica e responsiva.

Projeto desenvolvido como Trabalho de Conclusão de Curso (TCC) do Curso Técnico de Informática da Escola Técnica de Brasília (ETB). :contentReference[oaicite:0]{index=0}

---

## Objetivo do Projeto

O SkillShare tem como objetivo democratizar o acesso ao conhecimento técnico através da colaboração entre estudantes, permitindo que usuários compartilhem habilidades, materiais acadêmicos e experiências de aprendizado. :contentReference[oaicite:1]{index=1}

A plataforma foi criada para solucionar problemas como:

- Fragmentação do conhecimento entre alunos;
- Falta de canais para monitoria informal;
- Dificuldade em encontrar parceiros de estudo compatíveis. :contentReference[oaicite:2]{index=2}

---

## Principais Funcionalidades

### Match Inteligente
Sistema de recomendação que conecta estudantes com base nas dificuldades de um usuário e nas habilidades de outro. :contentReference[oaicite:3]{index=3}

### Feed Acadêmico
Área para publicação de dúvidas, conteúdos informativos e materiais acadêmicos com suporte para anexos em PDF e imagens. :contentReference[oaicite:4]{index=4}

### Chat em Tempo Real
Comunicação direta entre usuários para troca de mensagens, mentorias e compartilhamento de arquivos. :contentReference[oaicite:5]{index=5}

### Sistema de Gamificação
Sistema de pontuação e upvotes para incentivar contribuições dentro da comunidade acadêmica. :contentReference[oaicite:6]{index=6}

### Gestão de Perfil
Perfis personalizados baseados em competências, habilidades e áreas de interesse acadêmico. :contentReference[oaicite:7]{index=7}

---

## Tecnologias Utilizadas

### Back-end
- Java
- Spring Boot
- Servlet
- WebSocket

### Front-end
- HTML5
- CSS3
- JavaScript

### Banco de Dados
- MySQL

### Ferramentas
- GitHub
- VS Code
- NetBeans

O Spring Boot será utilizado no processamento das requisições HTTP, autenticação, login, cadastro, postagem e comunicação em tempo real, utilizando internamente o DispatcherServlet para gerenciamento das requisições web. :contentReference[oaicite:8]{index=8}

---

## Requisitos do Sistema

- Interface responsiva para desktop, tablet e smartphone;
- Carregamento rápido do feed acadêmico;
- Proteção de dados e segurança baseada na LGPD;
- Sistema escalável para múltiplos usuários simultâneos. :contentReference[oaicite:9]{index=9}

---

## Estrutura do Projeto

```bash
SkillShare/
│
├── backend/
├── frontend/
├── database/
├── docs/
└── README.md
