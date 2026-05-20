# InovaGAB - Documentação Técnica do Projeto

Este documento fornece uma visão geral completa da arquitetura, funcionalidades e estrutura do aplicativo InovaGAB, uma plataforma de inovação corporativa desenvolvida para o Grupo Águia Branca.

## 🚀 Visão Geral
O InovaGAB é um aplicativo Android desenvolvido com **Jetpack Compose** que visa digitalizar e centralizar o processo de inovação. Ele permite que colaboradores de diferentes níveis (Operador, Gestor, Líder) contribuam com ideias, acompanhem o progresso de projetos e visualizem o impacto estratégico das inovações.

---

## 🏗️ Arquitetura e Tecnologias

### Tecnologias Principais
- **Linguagem:** Kotlin
- **UI Framework:** Jetpack Compose (Modern Toolkit para Android)
- **Arquitetura:** MVVM (Model-View-ViewModel)
- **Backend:** 
  - **Firebase Auth:** Autenticação de usuários.
  - **Firebase Firestore:** Banco de dados NoSQL para perfis e dados.
- **Navegação:** Jetpack Compose Navigation

### Estrutura de Pastas (`app/src/main/java/br/com/fiap/`)
- `model/`: Definições de dados (ex: `UserProfile`).
- `viewmodel/`: Lógica de negócio e estado da UI (ex: `AuthViewModel`).
- `ui/navigation/`: Configuração de rotas e navegação do app.
- `ui/theme/`: Definições de cores, tipografia e temas globais.
- `ui/components/`: Componentes reutilizáveis (ex: Bottom Bars específicas por cargo).
- `ui/screens/`: Telas divididas por responsabilidade (Login, SignUp, Operador, Gestor, Líder).

---

## 🔐 Fluxo de Autenticação e Perfis

O app utiliza um sistema rigoroso de **Role-Based Access Control (RBAC)**.

1.  **Cadastro (`SignUpScreen`):** Novos usuários criam conta com e-mail/senha. Por padrão, são registrados com o cargo `OPERADOR` no Firestore.
2.  **Login (`LoginScreen`):**
    - O usuário deve selecionar explicitamente seu perfil (Operador, Gestor ou Líder).
    - O `AuthViewModel` valida se o cargo selecionado coincide com o cargo registrado no documento do usuário no Firestore.
    - Se houver divergência, o acesso é negado por segurança.

---

## 📱 Aplicações e Telas por Perfil

### 1. Perfil: Operador (Foco em Execução)
*Destinado a colaboradores da linha de frente (ex: motoristas, mecânicos).*
- **OperadorHomeScreen:** Dashboard pessoal com o "Meu Impacto" (número de ideias), ações rápidas e visão da estratégia do grupo.
- **NovaIdeiaScreen:** Formulário para registrar novos problemas ou sugestões.
- **MinhasIdeiasScreen:** Acompanhamento do status das ideias submetidas.

### 2. Perfil: Gestor (Foco em Curadoria)
*Destinado a supervisores e gerentes de área.*
- **GestorHomeScreen:** Central de curadoria. Permite filtrar ideias pendentes, aprovadas e recusadas.
- **Curadoria de Ideias:** O gestor pode "Aprovar" ou "Recusar" ideias, visualizar prioridades (Alta, Média) e o impacto esperado.
- **GestorProjetosScreen:** Visualização e gestão dos projetos em execução na sua área.

### 3. Perfil: Líder (Foco Estratégico)
*Destinado à diretoria e executivos.*
- **LiderHomeScreen:** Dashboard executivo com métricas de alto nível (ROI Total, Engajamento, Ideas Totais).
- **Resultados por Projeto:** Visualização detalhada do retorno sobre investimento de cada iniciativa.
- **EstrategiaScreen:** Gestão (CRUD) dos objetivos estratégicos do Grupo Águia Branca para o ano/ciclo.

---

## 🧭 Navegação (`AppNavigation.kt`)

A navegação é centralizada e utiliza rotas dinâmicas:
- `/login` e `/signup`: Portas de entrada.
- `/operador_home`, `/gestor_home`, `/lider_home`: Dashboards específicos.
- `/estrategia/{role}`: Tela de estratégia que adapta suas permissões baseada no cargo passado como parâmetro.

---

## 🎨 Identidade Visual (`ui.theme`)

O design segue a identidade do **Grupo Águia Branca**:
- **Cores Principais:** Azul Marinho (`BackgroundBlue`) e Azul Secundário (`BlueSecondary`).
- **Componentes:** Uso extensivo de `Cards` com cantos arredondados, gradientes lineares para destaque e ícones do Material Design.
- **Feedback Visual:** Uso de cores semânticas (Verde para aprovação, Vermelho/Laranja para prioridades e alertas).

---

## 🛠️ Manutenção e Expansão
- **Adicionar Novo Cargo:** Atualizar o enum `UserProfile`, criar a `BottomBar` correspondente e adicionar as rotas no `AppNavigation`.
- **Novas Funcionalidades:** Seguir o padrão MVVM, criando um novo `ViewModel` para cada fluxo complexo para manter a separação de responsabilidades.

---
*Documento gerado automaticamente para auxílio técnico e onboarding no projeto InovaGAB.*
