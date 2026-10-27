# Explicação Simples do App — Versão 2

Este é um resumo **fácil de entender** de como o app funciona e onde está cada coisa que o professor pediu.

> Os arquivos ficam em: `app/src/main/java/com/example/mypersonalorganizerv2/`

---

## 1. O que o app faz?

É um **organizador pessoal**. A pessoa:

1. **Cria uma conta** e **entra** com e-mail e senha.
2. Cadastra suas **tarefas** (ex.: "Estudar para a prova").
3. Cadastra seus **compromissos** (ex.: "Consulta no dentista, 10/11 às 14h").
4. Pode **ver, editar e apagar** tudo isso.
5. Pode **sair** da conta.

Tudo fica salvo no celular, em um **banco de dados SQLite**.

---

## 2. A ideia geral: um restaurante 🍽️

O projeto segue o padrão **MVVM**. Pense em um restaurante:

| No restaurante | No app | Pasta |
|---|---|---|
| 🧑 **Cliente** — vê o cardápio e faz o pedido | **View** (as telas) | `ui/screens/` |
| 🧑‍🍳 **Garçom** — recebe o pedido, confere e leva para a cozinha | **ViewModel** | `viewmodel/` |
| 🥫 **Despensa** — onde os ingredientes ficam guardados | **Banco de dados** | `data/` |
| 📋 **Ficha do prato** — descreve o que tem em cada prato | **Model** | `model/` |

👉 **Regra de ouro:** o cliente (tela) **nunca** entra na despensa (banco). Ele sempre pede ao garçom (ViewModel).

```
Tela  ──pede──▶  ViewModel  ──busca/guarda──▶  Banco de dados
Tela  ◀─mostra─  ViewModel  ◀────devolve─────  Banco de dados
```

---

## 3. Os requisitos do professor e onde estão

### ✅ Banco de dados

| O professor pediu | O que é, em palavras simples | Onde está |
|---|---|---|
| **SQLiteOpenHelper** | É quem **cria o banco** e as tabelas na primeira vez que o app abre | `data/DatabaseHelper.kt` |
| **writableDatabase** | Abre o banco para **escrever** (salvar, alterar, apagar) | Nos arquivos `data/...Dao.kt`, métodos `insert`, `update`, `delete` |
| **readableDatabase** | Abre o banco só para **ler** (buscar) | Nos arquivos `data/...Dao.kt`, métodos `getAllByUser`, `getById`, `findBy...` |
| **model** | Uma "ficha" que diz quais informações cada coisa tem | `model/User.kt`, `model/Task.kt`, `model/Appointment.kt` |
| **2 CRUDs** | Criar, Listar, Alterar e Deletar | **Tarefas** e **Compromissos** (ver seção 5) |
| **LaunchedEffect** | "Quando a tela abrir, faça isso" — usamos para **buscar os dados no banco** | Em todas as telas de lista, formulário, perfil e login |
| **LazyColumn** | Uma **lista com rolagem** que só desenha o que aparece na tela | `TaskListScreen.kt` e `AppointmentListScreen.kt` |

### ✅ Login

| O professor pediu | Onde está |
|---|---|
| **Login** | `LoginScreen.kt` (tela) + `AuthViewModel.kt` → função `login()` |
| **Cadastro** | `RegisterScreen.kt` (tela) + `AuthViewModel.kt` → função `register()` |
| **Controle de sessão** | `data/SessionManager.kt` — lembra quem está logado |
| **Sem menu no login e cadastro** | Essas duas telas **não usam** o `MainScaffold` (o componente que desenha o menu) |

### ✅ Restrições

| Regra | Como cumprimos |
|---|---|
| Padrão **MVVM** | Pastas separadas: `model`, `data`, `viewmodel`, `ui` |
| Sem bibliotecas de fora | Só usamos o que já vem no Android e no projeto (nada de Room, Firebase etc.) |

---

## 4. O banco de dados (SQLite)

### O que é?
Um **arquivo** dentro do celular (`personal_organizer.db`) que funciona como uma **planilha do Excel** com várias abas. Cada aba é uma **tabela**.

### Nossas 3 tabelas

**`users`** — as pessoas cadastradas
| id | name | email | password | ... |
|---|---|---|---|---|
| 1 | Maria | maria@email.com | 1234 | ... |

**`tasks`** — as tarefas
| id | user_id | title | priority | done |
|---|---|---|---|---|
| 1 | 1 | Estudar Android | Alta | 0 |

**`appointments`** — os compromissos
| id | user_id | service_type | date | time |
|---|---|---|---|---|
| 1 | 1 | Consulta | 10/11 | 14:00 |

👉 A coluna **`user_id`** diz **de quem** é aquela tarefa/compromisso. Assim cada pessoa só vê o que é dela.

### Quem faz o quê

- **`DatabaseHelper.kt`** → o **construtor**: cria o arquivo e as tabelas (no método `onCreate`). Isso acontece **uma vez só**.
- **`UserDao.kt`, `TaskDao.kt`, `AppointmentDao.kt`** → os **funcionários do estoque**: cada um cuida de uma tabela e sabe salvar, buscar, alterar e apagar.

### Exemplo simples: salvar uma tarefa
```kotlin
fun insert(task: Task): Long {
    val db = dbHelper.writableDatabase   // abre o banco para ESCREVER
    return db.insert("tasks", null, valores)  // salva na tabela tasks
}
```

### Exemplo simples: buscar as tarefas
```kotlin
fun getAllByUser(userId: Long): List<Task> {
    val db = dbHelper.readableDatabase   // abre o banco para LER
    val cursor = db.rawQuery("SELECT * FROM tasks WHERE user_id = ?", ...)
    // percorre o resultado linha por linha e monta a lista
}
```

---

## 5. Os 2 CRUDs

**CRUD** = as 4 ações básicas com dados:

| Letra | Significa | Em português |
|---|---|---|
| **C** | Create | **Criar** |
| **R** | Read | **Listar / Ler** |
| **U** | Update | **Alterar** |
| **D** | Delete | **Deletar** |

### CRUD 1 — Tarefas 📝

| Ação | O que o usuário faz | Função no ViewModel | Função no banco |
|---|---|---|---|
| **Criar** | Toca no **+** e preenche | `saveTask()` | `insert()` |
| **Listar** | Abre a tela de tarefas | `loadTasks()` | `getAllByUser()` |
| **Alterar** | Toca no **lápis** ✏️ ou marca como feita ☑️ | `saveTask()` / `toggleDone()` | `update()` |
| **Deletar** | Toca na **lixeira** 🗑️ e confirma | `deleteTask()` | `delete()` |

Arquivos: `TaskListScreen.kt`, `TaskFormScreen.kt`, `TaskViewModel.kt`, `TaskDao.kt`

### CRUD 2 — Compromissos 📅

| Ação | O que o usuário faz | Função no ViewModel | Função no banco |
|---|---|---|---|
| **Criar** | Toca no **+** e preenche | `saveAppointment()` | `insert()` |
| **Listar** | Abre a tela de compromissos | `loadAppointments()` | `getAllByUser()` |
| **Alterar** | Toca no **lápis** ✏️ | `saveAppointment()` | `update()` |
| **Deletar** | Toca na **lixeira** 🗑️ e confirma | `deleteAppointment()` | `delete()` |

Arquivos: `AppointmentListScreen.kt`, `AppointmentFormScreen.kt`, `AppointmentViewModel.kt`, `AppointmentDao.kt`

💡 **Curiosidade:** a mesma tela de formulário serve para **criar** e para **editar**. Se ela recebe o número `-1`, é uma tarefa nova. Se recebe um número de verdade (ex.: `5`), carrega a tarefa 5 para editar.

---

## 6. LaunchedEffect e LazyColumn

### LaunchedEffect = "assim que a tela abrir, faça isso"
```kotlin
LaunchedEffect(Unit) {
    viewModel.loadTasks()   // vai no banco buscar as tarefas
}
```
É por isso que, ao salvar uma tarefa e voltar para a lista, **ela já aparece**: a lista abre de novo e busca tudo atualizado.

Também usamos no **login**: se a pessoa já estava logada, o `LaunchedEffect` pula a tela de login sozinho.

### LazyColumn = lista inteligente
```kotlin
LazyColumn {
    items(viewModel.tasks) { task ->
        TaskItem(task)   // desenha um cartão para cada tarefa
    }
}
```
"Lazy" = **preguiçosa**: ela só desenha os itens que **aparecem na tela**. Se você tiver 1.000 tarefas, ela não desenha as 1.000 de uma vez — economiza memória.

---

## 7. Login e sessão

### Como funciona o cadastro
1. A pessoa preenche nome, e-mail e senha.
2. O `AuthViewModel` confere: nome preenchido? e-mail válido? senha com 4+ caracteres? e-mail já existe?
3. Se estiver tudo certo, salva na tabela `users`.

### Como funciona o login
1. A pessoa digita e-mail e senha.
2. O `AuthViewModel` procura no banco alguém com **esse e-mail E essa senha**.
3. Achou? ✅ Guarda o **id** da pessoa no `SessionManager` e abre o app.
4. Não achou? ❌ Mostra "E-mail ou senha incorretos".

### O que é a "sessão"?
É o app **lembrar quem está logado**, como uma **pulseira de festa** 🎟️:
- Ao **entrar**, a pessoa ganha a pulseira (o id dela fica salvo).
- Enquanto estiver com a pulseira, pode circular pelo app, mesmo se fechar e abrir de novo.
- Ao tocar em **Sair**, a pulseira é retirada (o id é apagado) e volta para o login.

Arquivo: `data/SessionManager.kt`

### Por que não aparece menu no login e cadastro?
O **menu** fica dentro de um componente chamado `MainScaffold`.
- Telas internas (Tarefas, Compromissos, Perfil) → **usam** o `MainScaffold` → **têm menu**.
- Login e Cadastro → **não usam** → **sem menu**. ✅

---

## 8. Caminho completo: criando uma tarefa

```
1. 👆 Usuário toca no "+"
        ↓
2. 📱 Abre a tela de formulário (TaskFormScreen)
        ↓
3. ✍️ Usuário digita o título e toca em "Salvar"
        ↓
4. 🧑‍🍳 TaskViewModel.saveTask() confere se tem título
        ↓
5. 🥫 TaskDao.insert() → writableDatabase → salva no SQLite
        ↓
6. ⬅️ Volta para a lista
        ↓
7. 🔄 LaunchedEffect → loadTasks() → readableDatabase → busca tudo
        ↓
8. 📋 LazyColumn mostra a nova tarefa na tela
```

---

## 9. Resumo dos arquivos

| Pasta | Arquivo | Em uma frase |
|---|---|---|
| `model/` | `User.kt` | Ficha de um usuário |
| `model/` | `Task.kt` | Ficha de uma tarefa |
| `model/` | `Appointment.kt` | Ficha de um compromisso |
| `data/` | `DatabaseHelper.kt` | Cria o banco e as tabelas |
| `data/` | `UserDao.kt` | Salva e busca usuários |
| `data/` | `TaskDao.kt` | Salva, busca, altera e apaga tarefas |
| `data/` | `AppointmentDao.kt` | Salva, busca, altera e apaga compromissos |
| `data/` | `SessionManager.kt` | Lembra quem está logado |
| `viewmodel/` | `AuthViewModel.kt` | Regras de login, cadastro e sair |
| `viewmodel/` | `TaskViewModel.kt` | Regras das tarefas |
| `viewmodel/` | `AppointmentViewModel.kt` | Regras dos compromissos |
| `viewmodel/` | `ProfileViewModel.kt` | Regras do perfil |
| `ui/screens/` | `LoginScreen.kt` | Tela de entrar |
| `ui/screens/` | `RegisterScreen.kt` | Tela de criar conta |
| `ui/screens/` | `TaskListScreen.kt` | Lista de tarefas |
| `ui/screens/` | `TaskFormScreen.kt` | Formulário de tarefa |
| `ui/screens/` | `AppointmentListScreen.kt` | Lista de compromissos |
| `ui/screens/` | `AppointmentFormScreen.kt` | Formulário de compromisso |
| `ui/screens/` | `ProfileScreen.kt` | Tela de perfil |
| `ui/components/` | `MainScaffold.kt` | O menu de cima |
| `navigation/` | `NavGraph.kt` | O "mapa" que liga uma tela à outra |

---

## 10. Cola rápida para a defesa 🎯

- **O que é SQLiteOpenHelper?** → A classe que cria e gerencia o banco. A nossa é a `DatabaseHelper`.
- **Diferença entre writable e readable?** → `writable` para salvar/alterar/apagar; `readable` só para ler.
- **O que é o Model?** → A "ficha" com os campos de cada coisa (usuário, tarefa, compromisso).
- **Quais são os 2 CRUDs?** → Tarefas e Compromissos.
- **Para que serve o LaunchedEffect?** → Rodar algo quando a tela abre — usamos para buscar os dados no banco.
- **Para que serve o LazyColumn?** → Mostrar listas com rolagem de forma econômica.
- **O que é MVVM?** → Separar tela (View), regras (ViewModel) e dados (Model). A tela nunca fala direto com o banco.
- **Como funciona a sessão?** → O id do usuário fica salvo ao entrar e é apagado ao sair.
- **Cada usuário vê só seus dados?** → Sim, pela coluna `user_id` nas tabelas.
- **Onde fica o banco?** → Dentro do celular, no arquivo `personal_organizer.db`. Dá para ver no Android Studio em *App Inspection → Database Inspector*.
