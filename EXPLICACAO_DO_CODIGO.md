# Explicação do Código — Trabalho de Android Parte 3

Este documento explica, ponto a ponto, como o app **Assessor Pessoal** (`MyPersonalOrganizerV2`) foi construído e **onde cada requisito do arquivo `Trabalho de Android - 3.pdf` está implementado**.

> Todos os caminhos abaixo são relativos a
> `app/src/main/java/com/example/mypersonalorganizerv2/`

---

## Sumário

1. [Requisitos do PDF → onde estão no código](#1-requisitos-do-pdf--onde-estão-no-código)
2. [Arquitetura MVVM do projeto](#2-arquitetura-mvvm-do-projeto)
3. [Camada Model](#3-camada-model)
4. [Camada de Dados (SQLite)](#4-camada-de-dados-sqlite)
5. [Camada ViewModel](#5-camada-viewmodel)
6. [Camada View (telas em Compose)](#6-camada-view-telas-em-compose)
7. [Navegação e controle do menu](#7-navegação-e-controle-do-menu)
8. [Fluxos completos passo a passo](#8-fluxos-completos-passo-a-passo)
9. [Onde fica o arquivo do banco](#9-onde-fica-o-arquivo-do-banco)
10. [Perguntas prováveis na defesa](#10-perguntas-prováveis-na-defesa)

---

## 1. Requisitos do PDF → onde estão no código

### Implementação de Banco de Dados

| Requisito do PDF | Onde está | Linha(s) |
|---|---|---|
| **SQLiteOpenHelper** | `data/DatabaseHelper.kt` — a classe `DatabaseHelper` herda de `SQLiteOpenHelper` | 7–8 |
| Criação das tabelas | `DatabaseHelper.onCreate()` — cria `users`, `tasks` e `appointments` | 10–55 |
| Atualização de versão | `DatabaseHelper.onUpgrade()` | 57–62 |
| **writableDatabase** | Em todo método que **grava**: `insert`, `update`, `delete` | `TaskDao.kt` 11, 44, 55 · `AppointmentDao.kt` 11, 44, 55 · `UserDao.kt` 10, 23 |
| **readableDatabase** | Em todo método que **lê**: `getAllByUser`, `getById`, `findBy...` | `TaskDao.kt` 17, 32 · `AppointmentDao.kt` 17, 32 · `UserDao.kt` 36, 47, 58 |
| **model** | Pasta `model/` — `User.kt`, `Task.kt`, `Appointment.kt` | linha 3 de cada |
| **CRUD 1 — Tarefas** (Criar, Listar, Alterar, Deletar) | `data/TaskDao.kt` + `viewmodel/TaskViewModel.kt` + `ui/screens/task/` | ver seção 4.3 |
| **CRUD 2 — Compromissos** (Criar, Listar, Alterar, Deletar) | `data/AppointmentDao.kt` + `viewmodel/AppointmentViewModel.kt` + `ui/screens/appointment/` | ver seção 4.4 |
| **LaunchedEffect** | `TaskListScreen.kt` 39 · `TaskFormScreen.kt` 35 · `AppointmentListScreen.kt` 39 · `AppointmentFormScreen.kt` 37 · `ProfileScreen.kt` 43 · `LoginScreen.kt` 39 | — |
| **LazyColumn** | `TaskListScreen.kt` 60 · `AppointmentListScreen.kt` 60 | — |

### Sistema de Login Funcional

| Requisito do PDF | Onde está | Linha(s) |
|---|---|---|
| **Login** | `AuthViewModel.login()` → `UserDao.findByEmailAndPassword()` | `AuthViewModel.kt` 29 · `UserDao.kt` 57 |
| **Cadastro de usuário** | `AuthViewModel.register()` → `UserDao.insert()` | `AuthViewModel.kt` 46 · `UserDao.kt` 9 |
| **Controle de sessão** | `data/SessionManager.kt` — salva / lê / apaga o id do usuário logado | 10, 16, 18 |
| Sessão mantida ao reabrir o app | `LoginScreen.kt` — `LaunchedEffect` verifica `isLoggedIn()` | 39–43 |
| Logout | `NavGraph.kt` — `onLogout` chama `authViewModel.logout()` e limpa a pilha de telas | 33–38 |
| **Sem menu nas telas de login e cadastro** | `LoginScreen.kt` e `RegisterScreen.kt` **não usam** o `MainScaffold` (que é quem desenha o menu). Todas as outras telas usam. | `MainScaffold.kt` 17 |

### Restrições

| Restrição | Como foi atendida |
|---|---|
| **Padrão MVVM** | Separação em `model/` (dados), `data/` (acesso ao banco), `viewmodel/` (lógica e estado) e `ui/screens/` (telas). As telas **nunca** acessam o banco diretamente. |
| Só recursos vistos em aula | Usamos apenas o SDK do Android (`SQLiteOpenHelper`, `ContentValues`, `Cursor`, `SharedPreferences`) e as bibliotecas Jetpack que já estavam no projeto (Compose, Navigation, ViewModel). Nenhuma biblioteca de terceiros (sem Room, sem Retrofit, sem Hilt). |

---

## 2. Arquitetura MVVM do projeto

```
mypersonalorganizerv2/
├── MainActivity.kt              → ponto de entrada; chama AppNavigation()
│
├── model/                       ── M (Model) ──────────────────────────
│   ├── User.kt                  → representa uma linha da tabela users
│   ├── Task.kt                  → representa uma linha da tabela tasks
│   └── Appointment.kt           → representa uma linha da tabela appointments
│
├── data/                        ── Acesso a dados (parte do Model) ─────
│   ├── DatabaseHelper.kt        → SQLiteOpenHelper: cria o banco e as tabelas
│   ├── UserDao.kt               → SQL da tabela users
│   ├── TaskDao.kt               → SQL da tabela tasks       (CRUD 1)
│   ├── AppointmentDao.kt        → SQL da tabela appointments (CRUD 2)
│   └── SessionManager.kt        → guarda quem está logado
│
├── viewmodel/                   ── VM (ViewModel) ─────────────────────
│   ├── AuthViewModel.kt         → login, cadastro, logout
│   ├── TaskViewModel.kt         → lista + formulário de tarefas
│   ├── AppointmentViewModel.kt  → lista + formulário de compromissos
│   └── ProfileViewModel.kt      → dados do perfil
│
├── navigation/
│   ├── Screen.kt                → nomes das rotas
│   └── NavGraph.kt              → liga rotas às telas
│
└── ui/                          ── V (View) ───────────────────────────
    ├── components/              → peças reutilizáveis (menu, botões, campos)
    └── screens/
        ├── login/               → LoginScreen
        ├── register/            → RegisterScreen
        ├── task/                → TaskListScreen, TaskFormScreen
        ├── appointment/         → AppointmentListScreen, AppointmentFormScreen
        └── profile/             → ProfileScreen
```

**Como as camadas conversam** (a seta indica "quem chama quem"):

```
  View (Tela Compose)
     │  lê o estado (viewModel.tasks, viewModel.title...)
     │  chama ações (viewModel.saveTask(), viewModel.deleteTask()...)
     ▼
  ViewModel
     │  valida os dados, guarda o estado da tela
     │  chama o DAO
     ▼
  DAO  ──── writableDatabase / readableDatabase ───▶  DatabaseHelper (SQLiteOpenHelper)
     │                                                       │
     │  converte Cursor ⇄ Model                              ▼
     ▼                                              personal_organizer.db
  Model (User / Task / Appointment)
```

A tela **nunca** fala com o banco. Ela só conhece o ViewModel. Isso é o coração do MVVM.

---

## 3. Camada Model

Pasta `model/`. São `data class` simples do Kotlin: cada propriedade corresponde a uma coluna da tabela.

### `model/Task.kt`
```kotlin
data class Task(
    val id: Long = 0,          // coluna id (gerada pelo banco)
    val userId: Long,          // coluna user_id (dono da tarefa)
    val title: String,
    val category: String,
    val priority: String,
    val deliveryDate: String,
    val notes: String,
    val done: Boolean = false  // no banco é INTEGER (0 ou 1)
)
```

- `id = 0` por padrão: quando criamos uma tarefa nova ainda não sabemos o id — o SQLite gera com `AUTOINCREMENT`.
- `userId` liga a tarefa ao usuário logado, assim cada usuário vê só as suas.
- `done` é `Boolean` no Kotlin, mas o SQLite não tem tipo booleano, então salvamos `1` ou `0`.

`User.kt` e `Appointment.kt` seguem a mesma ideia.

---

## 4. Camada de Dados (SQLite)

### 4.1 `data/DatabaseHelper.kt` — o **SQLiteOpenHelper**

```kotlin
class DatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
```

- Herda de `SQLiteOpenHelper` (**requisito do PDF**).
- `DATABASE_NAME = "personal_organizer.db"` → nome do arquivo do banco.
- `DATABASE_VERSION = 1` → se mudar a estrutura das tabelas, aumentamos esse número e o Android chama `onUpgrade`.

**`onCreate(db)`** — executado **uma única vez**, na primeira vez que o app abre o banco. Cria as 3 tabelas com `db.execSQL("CREATE TABLE ...")`:

| Tabela | Colunas |
|---|---|
| `users` | id, name, email (**UNIQUE**), phone, birth_date, address, password |
| `tasks` | id, user_id (FK → users), title, category, priority, delivery_date, notes, done |
| `appointments` | id, user_id (FK → users), service_type, date, time, location, observations |

**`onUpgrade(db, old, new)`** — apaga as tabelas e chama `onCreate` de novo. Só roda se `DATABASE_VERSION` aumentar.

**`companion object`** — guarda constantes (nome do banco e das tabelas) para não repetir texto solto pelo código.

### 4.2 Padrão usado em todos os DAOs

DAO = *Data Access Object*: uma classe que concentra todo o SQL de uma tabela.

**Gravar → `writableDatabase` + `ContentValues`**
```kotlin
fun insert(task: Task): Long {
    val db = dbHelper.writableDatabase                 // abre o banco para ESCRITA
    return db.insert(DatabaseHelper.TABLE_TASKS, null, toContentValues(task))
}
```
`ContentValues` funciona como um mapa `coluna → valor`. O `insert` retorna o id gerado (ou `-1` se der erro).

**Ler → `readableDatabase` + `Cursor`**
```kotlin
fun getAllByUser(userId: Long): List<Task> {
    val db = dbHelper.readableDatabase                 // abre o banco para LEITURA
    val cursor = db.rawQuery(
        "SELECT * FROM tasks WHERE user_id = ? ORDER BY done ASC, id DESC",
        arrayOf(userId.toString())
    )
    val tasks = mutableListOf<Task>()
    while (cursor.moveToNext()) {                      // percorre linha por linha
        tasks.add(cursorToTask(cursor))                // converte a linha em Model
    }
    cursor.close()                                     // sempre fechar o Cursor
    return tasks
}
```
- O `?` na SQL é preenchido pelo `arrayOf(...)`. Isso evita **SQL Injection** (nunca concatenamos texto do usuário dentro da SQL).
- `cursorToTask()` lê cada coluna (`getColumnIndexOrThrow("title")`) e monta um objeto `Task`.

### 4.3 CRUD 1 — Tarefas (`data/TaskDao.kt`)

| Operação CRUD | Método | Banco usado | Linha |
|---|---|---|---|
| **C**riar | `insert(task)` | `writableDatabase` | 10 |
| **L**istar | `getAllByUser(userId)` | `readableDatabase` | 16 |
| Ler um (para editar) | `getById(id)` | `readableDatabase` | 31 |
| **A**lterar | `update(task)` | `writableDatabase` | 43 |
| **D**eletar | `delete(id)` | `writableDatabase` | 54 |

### 4.4 CRUD 2 — Compromissos (`data/AppointmentDao.kt`)

| Operação CRUD | Método | Banco usado | Linha |
|---|---|---|---|
| **C**riar | `insert(appointment)` | `writableDatabase` | 10 |
| **L**istar | `getAllByUser(userId)` | `readableDatabase` | 16 |
| Ler um (para editar) | `getById(id)` | `readableDatabase` | 31 |
| **A**lterar | `update(appointment)` | `writableDatabase` | 43 |
| **D**eletar | `delete(id)` | `writableDatabase` | 54 |

### 4.5 Usuários (`data/UserDao.kt`)

| Método | Usado em | Banco |
|---|---|---|
| `insert(user)` | Cadastro | `writableDatabase` |
| `update(user)` | Salvar perfil | `writableDatabase` |
| `findById(id)` | Carregar perfil | `readableDatabase` |
| `findByEmail(email)` | Impedir e-mail duplicado | `readableDatabase` |
| `findByEmailAndPassword(email, senha)` | **Login** | `readableDatabase` |

### 4.6 `data/SessionManager.kt` — **controle de sessão**

```kotlin
fun saveUserId(userId: Long)   // chamado no login → guarda o id do usuário
fun getUserId(): Long          // usado pelos ViewModels para saber "de quem" são os dados
fun isLoggedIn(): Boolean      // true se existe um id salvo
fun logout()                   // apaga o id salvo
```

Usa `SharedPreferences`, que é um armazenamento chave-valor nativo do Android. Como fica gravado no aparelho, **o usuário continua logado mesmo se fechar e reabrir o app**, até clicar em "Sair".

---

## 5. Camada ViewModel

Todos os ViewModels herdam de `AndroidViewModel(application)`. Usamos `AndroidViewModel` (e não `ViewModel`) porque precisamos de um `Context` para criar o `DatabaseHelper` e o `SessionManager`.

O estado da tela fica em variáveis `mutableStateOf(...)` / `mutableStateListOf(...)`. Quando uma delas muda, o Compose **redesenha a tela automaticamente**.

### 5.1 `viewmodel/AuthViewModel.kt` — Login, cadastro e logout

| Membro | O que faz |
|---|---|
| `name, email, phone, birthDate, password` | Estado dos campos digitados |
| `errorMessage` | Mensagem de erro mostrada na tela (`private set`: só o ViewModel altera) |
| `isLoggedIn()` (l. 27) | Pergunta ao `SessionManager` se já há sessão |
| `login()` (l. 29) | Valida campos → busca no banco com `findByEmailAndPassword` → se achou, `sessionManager.saveUserId(user.id)` e retorna `true` |
| `register()` (l. 46) | Valida nome, e-mail, senha (mín. 4) e e-mail duplicado → `userDao.insert(...)` |
| `logout()` (l. 74) | `sessionManager.logout()` |

O e-mail é sempre salvo e buscado em minúsculas (`lowercase()`), para `Joao@x.com` e `joao@x.com` serem o mesmo usuário.

### 5.2 `viewmodel/TaskViewModel.kt` — CRUD 1

| Membro | Operação | O que faz |
|---|---|---|
| `tasks` | — | `mutableStateListOf<Task>()` — lista mostrada na `LazyColumn` |
| `title, category, priority, deliveryDate, notes` | — | Campos do formulário |
| `editingTask` | — | `null` = criando nova · preenchido = editando |
| `loadTasks()` (l. 37) | **Listar** | `tasks.clear()` + `tasks.addAll(taskDao.getAllByUser(userId))` |
| `loadTask(id)` (l. 42) | Ler um | Se `id > 0`, busca no banco e preenche os campos (modo edição) |
| `saveTask()` (l. 57) | **Criar / Alterar** | Valida o título. Se `editingTask == null` → `insert`; senão → `update` com `copy(...)` |
| `toggleDone(task)` (l. 90) | **Alterar** | Marca/desmarca como concluída (`update`) e recarrega a lista |
| `deleteTask(task)` (l. 95) | **Deletar** | `taskDao.delete(id)` e recarrega a lista |

O `formLoaded` evita que o formulário seja recarregado do banco (perdendo o que o usuário digitou) se a tela for recomposta, por exemplo ao girar o celular.

### 5.3 `viewmodel/AppointmentViewModel.kt` — CRUD 2

Mesma estrutura do `TaskViewModel`:

| Método | Operação |
|---|---|
| `loadAppointments()` (l. 37) | **Listar** |
| `loadAppointment(id)` (l. 42) | Ler um para editar |
| `saveAppointment()` (l. 57) | **Criar / Alterar** (exige data e horário) |
| `deleteAppointment(a)` (l. 90) | **Deletar** |

### 5.4 `viewmodel/ProfileViewModel.kt`

- `loadUser()` — busca o usuário logado (`findById(sessionManager.getUserId())`) e preenche os campos.
- `saveUser()` — valida e chama `userDao.update(...)`. Impede trocar para um e-mail que já pertence a outra conta.

---

## 6. Camada View (telas em Compose)

Cada tela recebe seu ViewModel assim:
```kotlin
viewModel: TaskViewModel = viewModel()
```
A função `viewModel()` cria o ViewModel na primeira vez e **reaproveita o mesmo** enquanto a tela existir (inclusive ao girar o celular).

### 6.1 `ui/screens/login/LoginScreen.kt`

- **Não usa `MainScaffold` → não tem menu** (requisito do PDF).
- **`LaunchedEffect(Unit)`** (l. 39): roda uma vez ao abrir a tela. Se já há sessão salva, pula direto para as tarefas.
- Botão "Entrar": `if (viewModel.login()) onLoginSuccess()`.
- Mostra `viewModel.errorMessage` em vermelho quando o login falha.

### 6.2 `ui/screens/register/RegisterScreen.kt`

- **Também não usa `MainScaffold` → sem menu.**
- Botão "Cadastrar": `if (viewModel.register()) onRegisterSuccess()` → volta para o login.

### 6.3 `ui/screens/task/TaskListScreen.kt` — Listar / Deletar tarefas

```kotlin
LaunchedEffect(Unit) {          // (l. 39) busca no banco ao abrir a tela
    viewModel.loadTasks()
}
...
LazyColumn(...) {               // (l. 60) lista com rolagem eficiente
    items(viewModel.tasks, key = { it.id }) { task ->
        TaskItem(task, onToggleDone, onEdit, onDelete)
    }
}
```

- **`LaunchedEffect`**: executa um código quando a tela entra na composição. Como a navegação recria a tela ao voltar do formulário, a lista é **recarregada do banco toda vez** — por isso a tarefa recém-criada aparece.
- **`LazyColumn`**: diferente de `Column`, só desenha os itens visíveis na tela. Ideal para listas que podem crescer.
- `key = { it.id }` ajuda o Compose a identificar cada item ao atualizar a lista.
- Cada `TaskItem` tem: checkbox (concluída), título, detalhes, botão **Editar** e botão **Excluir**.
- Excluir abre o `DeleteConfirmDialog` antes de apagar.
- Botão flutuante **+** abre o formulário vazio (criar).
- Se a lista estiver vazia, mostra `EmptyState` ("Nenhuma tarefa cadastrada").

### 6.4 `ui/screens/task/TaskFormScreen.kt` — Criar / Alterar tarefa

```kotlin
LaunchedEffect(taskId) {        // (l. 35)
    viewModel.loadTask(taskId)  // -1 = nova; id real = edição
}
```

- A mesma tela serve para **criar** e **editar**: o título muda entre "Nova tarefa" e "Editar tarefa" conforme `viewModel.editingTask`.
- Campos ligados direto ao ViewModel: `value = viewModel.title`, `onValueChange = { viewModel.title = it }`.
- Botão "Salvar tarefa": `if (viewModel.saveTask()) onSaved()` → volta para a lista.

### 6.5 `ui/screens/appointment/AppointmentListScreen.kt` e `AppointmentFormScreen.kt`

Mesma lógica das tarefas:
- Lista: `LaunchedEffect` (l. 39) + `LazyColumn` (l. 60) + editar + excluir com confirmação.
- Formulário: `LaunchedEffect(appointmentId)` (l. 37) carrega para edição.

### 6.6 `ui/screens/profile/ProfileScreen.kt`

- `LaunchedEffect(Unit)` (l. 43) → `viewModel.loadUser()`.
- "Salvar alterações" → `viewModel.saveUser()` e mostra mensagem de sucesso/erro.

### 6.7 Componentes reutilizáveis (`ui/components/`)

| Arquivo | Para que serve |
|---|---|
| `MainScaffold.kt` | Barra superior com **o menu** (Tarefas, Compromissos, Perfil, Sair). Só as telas internas usam. |
| `CommonComponents.kt` | `SelectField` (lista suspensa), `DeleteConfirmDialog` (confirmação de exclusão), `EmptyState` (lista vazia) |
| `CustomTextField.kt` | Campo de texto com rótulo e ícone |
| `PrimaryButton.kt` | Botão azul padrão |
| `PriorityOption.kt` | Radio button de prioridade |

---

## 7. Navegação e controle do menu

### `navigation/Screen.kt`
Define as rotas. As rotas de formulário recebem um id:
```kotlin
object TaskForm : Screen("task_form/{taskId}") {
    fun createRoute(taskId: Long) = "task_form/$taskId"
}
```
`createRoute(-1)` = nova tarefa · `createRoute(5)` = editar a tarefa de id 5.

### `navigation/NavGraph.kt`

- `startDestination = Screen.Login.route` (l. 42): o app sempre começa pelo login (que pula sozinho se já houver sessão).
- `onNavigate` (l. 26): usado pelo menu; `launchSingleTop = true` evita abrir a mesma tela duas vezes.
- `onLogout` (l. 33): apaga a sessão e navega para o login com `popUpTo(navController.graph.id) { inclusive = true }`, que **limpa toda a pilha de telas** — assim o botão "voltar" do celular não retorna para dentro do app depois de sair.
- O argumento `taskId` / `appointmentId` é lido com `backStackEntry.arguments?.getLong(...)` e repassado ao formulário.

### Por que o menu não aparece no login e cadastro?
O menu está **dentro** do `MainScaffold`. `LoginScreen` e `RegisterScreen` desenham apenas uma `Column`, sem `MainScaffold`. Todas as outras telas são envolvidas por `MainScaffold(...)`.

---

## 8. Fluxos completos passo a passo

### 8.1 Cadastro → Login
1. Usuário toca em **Criar conta** → `NavGraph` navega para `RegisterScreen`.
2. Preenche os campos → cada tecla atualiza `viewModel.name`, `viewModel.email`...
3. Toca **Cadastrar** → `AuthViewModel.register()`:
   - valida os campos;
   - `userDao.findByEmail()` (**readableDatabase**) verifica se o e-mail já existe;
   - `userDao.insert()` (**writableDatabase**) grava na tabela `users`.
4. Volta para o login.
5. Toca **Entrar** → `AuthViewModel.login()`:
   - `userDao.findByEmailAndPassword()` (**readableDatabase**);
   - achou → `sessionManager.saveUserId(id)` → **sessão iniciada**;
   - navega para `TaskListScreen` removendo o login da pilha.

### 8.2 Criar uma tarefa
1. Na lista, toca **+** → navega para `task_form/-1`.
2. `LaunchedEffect` chama `loadTask(-1)` → nada a carregar, formulário vazio.
3. Preenche e toca **Salvar** → `saveTask()` → `editingTask == null` → `taskDao.insert()` (**writableDatabase**).
4. `popBackStack()` volta para a lista.
5. A lista é recriada → `LaunchedEffect` → `loadTasks()` → `getAllByUser()` (**readableDatabase**) → a `LazyColumn` mostra a nova tarefa.

### 8.3 Editar uma tarefa
1. Toca no card ou no lápis → navega para `task_form/{id}`.
2. `LaunchedEffect(taskId)` → `loadTask(id)` → `getById()` (**readableDatabase**) → preenche os campos.
3. **Salvar** → `editingTask != null` → `taskDao.update()` (**writableDatabase**).

### 8.4 Excluir uma tarefa
1. Toca na lixeira → abre `DeleteConfirmDialog`.
2. Confirma → `deleteTask(task)` → `taskDao.delete(id)` (**writableDatabase**) → `loadTasks()` atualiza a lista.

### 8.5 Sair
1. Toca no ícone **Sair** do menu → `onLogout` → `authViewModel.logout()` → `SessionManager` apaga o id.
2. Navega para o login e limpa a pilha de telas.

---

## 9. Onde fica o arquivo do banco

O `SQLiteOpenHelper` cria o arquivo **no aparelho**, não na pasta do projeto:

```
/data/data/com.example.mypersonalorganizerv2/databases/personal_organizer.db
```

Para ver no Android Studio:
- **Database Inspector**: *View → Tool Windows → App Inspection* (com o app rodando). Mostra as tabelas e os dados ao vivo.
- **Device Explorer**: *View → Tool Windows → Device Explorer* → navegue até o caminho acima → botão direito → *Save As*.

---

## 10. Perguntas prováveis na defesa

**Por que usar `writableDatabase` e `readableDatabase` separados?**
`writableDatabase` abre o banco permitindo alterações (INSERT, UPDATE, DELETE). `readableDatabase` é usado só para consultas (SELECT). Deixa claro no código quando estamos lendo e quando estamos gravando.

**Quando o `onCreate` do `DatabaseHelper` é chamado?**
Só na primeira vez que o app acessa o banco (quando o arquivo `.db` ainda não existe). Depois disso, só o `onUpgrade` é chamado, e apenas se a `DATABASE_VERSION` aumentar.

**Para que serve o `?` nas consultas SQL?**
É um parâmetro. O valor vem do `arrayOf(...)` e o SQLite trata como dado, não como comando — isso impede SQL Injection.

**Por que fechar o `Cursor`?**
O `Cursor` ocupa memória e mantém recursos do banco abertos. Fechar com `cursor.close()` evita vazamento de memória.

**Qual a diferença entre `LazyColumn` e `Column`?**
`Column` desenha todos os itens de uma vez. `LazyColumn` desenha só os visíveis e reaproveita conforme rola — muito mais eficiente para listas.

**Para que serve o `LaunchedEffect`?**
Executa um bloco de código quando a tela aparece (ou quando a chave muda, como `taskId`). Usamos para buscar dados no banco ao abrir a tela, sem repetir a busca a cada recomposição.

**Por que `AndroidViewModel` em vez de `ViewModel`?**
Porque precisamos de um `Context` (a `Application`) para criar o `DatabaseHelper` e o `SessionManager`.

**Onde está o MVVM?**
- **Model**: `model/` + `data/` (dados e acesso ao banco).
- **View**: `ui/screens/` (só desenha e repassa ações).
- **ViewModel**: `viewmodel/` (estado da tela + regras + chama os DAOs).
A View nunca acessa o banco diretamente.

**Como funciona a sessão?**
No login, o id do usuário é salvo no `SharedPreferences` pelo `SessionManager`. Os ViewModels usam esse id para buscar só os dados daquele usuário. Ao reabrir o app, o `LaunchedEffect` do login vê que há sessão e pula a tela. No "Sair", o id é apagado.

**Cada usuário vê só seus dados? Como?**
Sim. As tabelas `tasks` e `appointments` têm a coluna `user_id`, e as consultas usam `WHERE user_id = ?` com o id da sessão.

**A senha é criptografada?**
Não — fica em texto puro, por ser um projeto acadêmico. Em produção o correto seria salvar um *hash* da senha.
