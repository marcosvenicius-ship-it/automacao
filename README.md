## Sobre o Projeto

Este é um projeto **Full-Stack real** desenvolvido para solucionar uma das maiores dores das equipes de pré-vendas (SDR/BDR) e times de marketing: a digitação manual de dados no CRM. 

A aplicação recebe históricos de conversas brutas de plataformas de chat (como WhatsApp), processa o contexto de forma assíncrona utilizando **Inteligência Artificial**, extrai as informações cruciais do cliente e preenche os campos estruturados da tela automaticamente.

## Arquitetura e Engenharia do Sistema

O projeto foi construído dividindo-se em pilares modernos de desenvolvimento web, garantindo segurança, escalabilidade e isolamento de código.

[Interface do Usuário] ➡️ (JSON) ➡️ [Backend Java no Render] ➡️ (Prompt) ➡️ [Google Gemini API]
│
[Campos Preenchidos] ⬅️ (JSON Limpo) ⬅️ [Backend Java no Render] ⬅️ ⬅️ ⬅️ ⬅️ ⬅️ ⬅️ ┘

### Back-end
Desenvolvido em **Java 17** com **Spring Boot 3**, o backend atua como uma API REST resiliente:
* **Encapsulamento Rígido:** Aplicação das melhores práticas de Orientação a Objetos. Todos os atributos das entidades são estritamente privados (`private`), blindando o estado do sistema contra manipulações externas diretas. O fluxo de dados é controlado estritamente por métodos acessores (`getters` e `setters`).
* **Segurança de Credenciais:** As chaves de acesso da Inteligência Artificial estão protegidas. O sistema utiliza injeção dinâmica via `application.properties` consumindo variáveis de ambiente da nuvem, impedindo o vazamento de chaves secretas no repositório público.
* **Resiliência a Falhas:** Implementação de tratamento de exceções robusto (`try-catch`) para interceptar erros de APIs de terceiros (como falhas de comunicação ou limites de requisições do Google), tratando as falhas com elegância sem derrubar o servidor.

### Front-end 
Hospedado de forma estática, focado em usabilidade e performance:
* **HTML5 & CSS3:** Interface responsiva, estilizada de forma limpa e intuitiva para o usuário final.
* **JavaScript Assíncrono:** Uso extensivo da **Fetch API** com o padrão `async/await`. O navegador dispara as requisições HTTP do tipo `POST` em segundo plano, evitando travamentos na tela enquanto aguarda o processamento dos dados comerciais pela IA.

### DevOps & Infraestrutura
* **Containerização com Docker:** Criação de um ambiente isolado via `Dockerfile`. O ecossistema Java, o gerenciador Maven e o pacote final `.jar` rodam de forma idêntica em qualquer máquina ou servidor do planeta.
* **Deploy e Nuvem:** O back-end containerizado está hospedado no servidor em nuvem da **Render** operando de forma independente, enquanto o front-end está publicado globalmente através do **GitHub Pages**.

---

## Fluxo de Integração com a Google Gemini API

Para garantir a extração cirúrgica das informações comerciais sem poluição de texto livre, o backend constrói uma árvore hierárquica de dados (Maps e Lists) e aplica técnicas de **Structured Prompting (Engenharia de Prompt)** para forçar o modelo **Gemini 2.5 Flash** a retornar estritamente um padrão estruturado no formato abaixo:

```json
{
  "razaoSocial": "Nome Oficial da Empresa",
  "cnpj": "00.000.000/0001-00",
  "nome": "Nome do Decisor/Contato",
  "cargo": "Cargo do Contato",
  "telefone": "Telefone Formatado",
  "email": "E-mail Corporativo",
  "segmento": "Setor de Atuação",
  "colaboradores": "Porte da Empresa",
  "faturamento": "Faturamento Estimado",
  "necessidade": "Dores identificadas no chat",
  "sistemaAtual": "Software/ERP utilizado atualmente"
}
