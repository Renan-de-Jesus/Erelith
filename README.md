# RPG Manager — Aplicativo Android

Aplicativo mobile para gerenciamento de grupos e personagens de RPG de mesa.
O Mestre pode criar grupos, cadastrar personagens e adicionar/editar atributos customizados
em tempo real durante as sessões.

---

## Tecnologias utilizadas

| Tecnologia | Finalidade |
|---|---|
| Kotlin | Linguagem principal |
| Room (SQLite) | Persistência local de dados |
| ViewModel + LiveData | Arquitetura MVVM |
| Navigation Component | Navegação entre telas |
| ViewBinding | Acesso seguro às views |
| Glide | Carregamento de imagens |
| Material Design 3 | Componentes de interface |
| Coroutines | Operações assíncronas |

---

## Estrutura do projeto

```
app/src/main/java/com/rpgmanager/
├── data/
│   ├── model/
│   │   ├── Group.kt           → Entidade de grupo (tabela: groups)
│   │   ├── Character.kt       → Entidade de personagem (tabela: characters)
│   │   └── Attribute.kt       → Entidade de atributo (tabela: attributes)
│   ├── dao/
│   │   ├── GroupDao.kt
│   │   ├── CharacterDao.kt
│   │   └── AttributeDao.kt
│   └── database/
│       └── RPGDatabase.kt     → Room Database (3 tabelas)
├── repository/
│   ├── GroupRepository.kt
│   ├── CharacterRepository.kt
│   └── AttributeRepository.kt
├── viewmodel/
│   ├── GroupViewModel.kt
│   ├── CharacterViewModel.kt
│   └── AttributeViewModel.kt
└── ui/
    ├── MainActivity.kt        → DrawerLayout + BottomNav + NavHost
    ├── groups/
    │   ├── GroupListFragment.kt
    │   ├── GroupFormFragment.kt
    │   ├── GroupAdapter.kt
    │   └── DeleteConfirmDialog.kt
    ├── characters/
    │   ├── CharacterListFragment.kt
    │   ├── CharacterFormFragment.kt
    │   ├── CharacterDetailFragment.kt
    │   └── CharacterAdapter.kt
    └── attributes/
        ├── AttributeFormFragment.kt
        └── AttributeAdapter.kt
```

---

## Banco de dados — 3 tabelas

### `groups`
| Campo | Tipo | Descrição |
|---|---|---|
| id | Long (PK) | Auto-gerado |
| name | String | Nome do grupo |
| masterName | String | Nome do Mestre |
| description | String | Descrição da campanha |
| imagePath | String? | Caminho da imagem |
| isActive | Boolean | Grupo ativo ou encerrado |
| createdAt | Long | Timestamp de criação |

### `characters`
| Campo | Tipo | Descrição |
|---|---|---|
| id | Long (PK) | Auto-gerado |
| groupId | Long (FK) | Referência ao grupo |
| name | String | Nome do personagem |
| playerName | String | Nome do jogador |
| race | String | Raça do personagem |
| characterClass | String | Classe do personagem |
| level | Int | Nível atual |
| backstory | String | História do personagem |
| imagePath | String? | Imagem do personagem |
| isAlive | Boolean | Vivo ou morto |
| createdAt | Long | Timestamp |

### `attributes`
| Campo | Tipo | Descrição |
|---|---|---|
| id | Long (PK) | Auto-gerado |
| characterId | Long (FK) | Referência ao personagem |
| name | String | Nome do atributo (ex: "Força") |
| value | String | Valor atual |
| attributeType | String | "NUMBER", "TEXT" ou "BOOLEAN" |
| note | String | Nota do Mestre |
| updatedAt | Long | Timestamp da última edição |

---

## Fluxo de navegação

```
GroupListFragment  ──────────────────────────────────────
    │  (FAB +)                                           │
    ▼                                                    │ (clique no grupo)
GroupFormFragment                               CharacterListFragment
(criar / editar grupo)                               │         │
                                               (FAB +)   (clique no personagem)
                                                   ▼              ▼
                                        CharacterFormFragment   CharacterDetailFragment
                                        (criar/editar)               │
                                                                 (FAB +)
                                                                     ▼
                                                           AttributeFormFragment
                                                           (criar/editar atributo)
```

---

## Componentes de interface utilizados

| Componente | Onde é usado |
|---|---|
| TextView (Label) | Em todos os layouts para rótulos e informações |
| EditText (TextInputEditText) | Formulários de grupo, personagem e atributo |
| Button | Botões Salvar/Cancelar em todos os formulários |
| CheckBox | "Grupo ativo" no formulário de grupo; "Ativado" em atributo booleano |
| RadioButton / RadioGroup | Seleção Vivo/Morto no personagem; Tipo de atributo (Número/Texto/Booleano) |
| ImageView | Foto de grupo e personagem (com Glide) |
| RecyclerView | Listas de grupos, personagens e atributos |
| Toast | Confirmação após criar/editar/excluir |
| GridLayout | Grade 2 colunas para campos lado a lado e botões |
| Navigation Component | DrawerLayout (Flyout), BottomNavigationView, NavHostFragment |
| MaterialCardView | Cards dos itens nas listas |
| FloatingActionButton | Adicionar grupo, personagem ou atributo |
| SearchView (Toolbar) | Busca em tempo real em todos os lists |
| AlertDialog | Confirmação antes de excluir qualquer registro |
| Snackbar | Pode ser usado em substituição ao Toast conforme necessidade |
| Chip | Indicador de status (Ativo/Inativo, Vivo/Morto) |

---

## Como executar

### Pré-requisitos
- Android Studio Hedgehog (2023.1.1) ou superior
- JDK 8+
- Android SDK 24+
- Dispositivo ou emulador com Android 7.0+

### Passos

1. **Clone o repositório:**
   ```bash
   git clone <url-do-repositorio>
   cd RPGManager
   ```

2. **Abra no Android Studio:**
   - File → Open → selecione a pasta `RPGManager`
   - Aguarde o sync do Gradle

3. **Execute:**
   - Conecte um dispositivo físico (USB debugging ativado) **ou** inicie um AVD (emulador)
   - Clique no botão ▶ Run (Shift+F10)

4. **Build manual (opcional):**
   ```bash
   ./gradlew assembleDebug
   # APK gerado em: app/build/outputs/apk/debug/app-debug.apk
   ```

---

## Funcionalidades CRUD implementadas

| Operação | Grupos | Personagens | Atributos |
|---|---|---|---|
| Create | ✅ FAB + formulário | ✅ FAB + formulário | ✅ FAB + formulário |
| Read | ✅ Lista com busca | ✅ Lista com busca | ✅ Lista na ficha |
| Update | ✅ Botão editar | ✅ Botão editar + Level Up | ✅ Botão editar |
| Delete | ✅ Confirmação | ✅ Confirmação | ✅ Confirmação |

---

## Arquitetura MVVM

```
View (Fragment/Activity)
    │  observa LiveData
    ▼
ViewModel  ←→  Repository  ←→  DAO  ←→  Room (SQLite)
    │
    └── viewModelScope (Coroutines para operações assíncronas)
```
