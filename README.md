# Cartolitos CF

<p align="center">
  <img src="relatorio/Telas/Iniciais/LogoCartolitos.png" alt="Logo Cartolitos CF" width="200"/>
</p>

## Sobre o projeto

O **Cartolitos CF** é um sistema de gerenciamento de ligas de futebol virtual, inspirado no famoso Cartola FC e no Rei do Pitaco. O sistema permite que usuários criem e gerenciem seus times, escalem jogadores, participem de ligas e simulem partidas, tudo isso com uma interface amigável e intuitiva.

O projeto foi desenvolvido em Java, utilizando JavaFX para a interface gráfica e PostgreSQL para persistência dos dados. A arquitetura orientada a objetos facilita a manutenção e expansão das funcionalidades.

## Funcionalidades

-  Cadastro e login de usuários
-  Criação e gerenciamento de ligas
-  Montagem e escalação de times
-  Mercado de jogadores
-  Simulação de partidas entre clubes
-  Ranking das ligas
-  Interface gráfica moderna e intuitiva
-  Gerenciamento administrativo (admin)

## Tecnologias utilizadas

-  Java 17
-  JavaFX
-  PostgreSQL
-  JDBC
-  Maven

## Estrutura do projeto

```
├── src/
│   └── main/java/
│       ├── app/           # Inicialização e execução
│       ├── controller/    # Lógica das telas e controle de fluxo
│       ├── dao/           # Acesso a dados (banco)
│       ├── database/      # Configuração do banco
│       ├── model/         # Classes de domínio (Usuario, Liga, Clube, etc)
│       └── resources/     # Arquivos de recursos (imagens, CSS, FXML)
├── relatorio/             # Relatório LaTeX e imagens das telas
├── lib/                   # Dependências externas (.jar)
├── pom.xml                # Gerenciador Maven
```

## Como executar o projeto

1. **Clone o repositório:**
   ```bash
   git clone <url-do-repositorio>
   ```
2. **Instale as dependências:**
   -  Certifique-se de ter o Java 17 e o Maven instalados.
   -  Baixe as dependências externas (JavaFX, PostgreSQL JDBC) ou utilize os arquivos em `lib/`.
3. **Configure o banco de dados:**
   -  Crie um banco PostgreSQL e ajuste as configurações em `src/main/java/database/`.
4. **Compile e execute:**
   ```bash
   mvn clean install
   mvn javafx:run
   ```

## Integrantes

-  Élvis Miranda
-  Gustavo Alves
-  Pedro Marcinoni

## Licença

Este projeto é acadêmico e não possui fins comerciais.
