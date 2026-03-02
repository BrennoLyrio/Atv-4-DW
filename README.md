# AutoManager — Atividade IV: Segurança com JWT e Spring Security

Evolução do sistema AutoManager com implementação de autenticação e autorização utilizando **JWT (JSON Web Token)** e **Spring Security**, protegendo todos os endpoints da API.

## 📋 Sobre o Projeto

Quarta atividade da série AutoManager (disciplina de Desenvolvimento Web — FATEC São José dos Campos). Com base nas atividades anteriores (CRUD, HATEOAS e expansão de domínios), esta versão adiciona uma camada completa de **segurança**, com login via JWT, controle de acesso por perfis (roles) e filtragem de requisições autenticadas.

## 🔧 Tecnologias

- Java 17+
- Spring Boot
- Spring Security
- JWT (JSON Web Token)
- Spring Data JPA
- Spring HATEOAS
- Hibernate
- Maven
- H2 / MySQL

## 🏗️ Estrutura do Projeto

```
automanager/
└── src/main/java/com/autobots/automanager/
    ├── adaptadores/        # Adaptadores de segurança (UserDetails)
    ├── configuracao/       # Configuração do Spring Security
    ├── controles/          # Controllers REST
    ├── entitades/          # Entidades JPA
    ├── enumeracoes/        # Enumerações (roles, tipos)
    ├── filtros/            # Filtros JWT para requisições
    ├── jwt/                # Utilitarios JWT (geração/validação de tokens)
    ├── modelo/             # Classes de modelo/negócio
    ├── repositorios/       # Repositórios Spring Data
    ├── servicos/           # Serviços de autenticação
    └── AutomanagerApplication.java
```

## 🔐 Segurança e Autenticação

### Fluxo de Autenticação

1. O usuário envia credenciais (usuário/senha) para `/login`
2. A API valida as credenciais e retorna um **token JWT**
3. O cliente inclui o token no header `Authorization: Bearer <token>` em todas as requisições
4. O filtro JWT intercepta e valida o token a cada requisição
5. O acesso é concedido ou negado com base no perfil do usuário

### Perfis de Acesso (Roles)
- **ADMIN** — Acesso total ao sistema
- **GERENTE** — Acesso gerencial
- **VENDEDOR** — Acesso a operações de vendas

## 🌐 Endpoints Principais

### Autenticação
| Método | Endpoint | Descrição |
|--------|----------|-----------|
| POST | `/login` | Autentica usuário e retorna token JWT |

### Recursos Protegidos
Todos os endpoints abaixo requerem token JWT válido:

| Recurso | Endpoint Base |
|---------|---------------|
| Usuários | `/usuarios`, `/usuario/{id}` |
| Empresas | `/empresas`, `/empresa/{id}` |
| Veículos | `/veiculos`, `/veiculo/{id}` |
| Mercadorias | `/mercadorias`, `/mercadoria/{id}` |
| Serviços | `/servicos`, `/servico/{id}` |
| Vendas | `/vendas`, `/venda/{id}` |
| Documentos | `/documentos`, `/documento/{id}` |
| Telefones | `/telefones`, `/telefone/{id}` |
| Endereços | `/enderecos`, `/endereco/{id}` |

## ▶️ Como Executar

```bash
git clone https://github.com/BrennoLyrio/Atv-4-DW.git
cd Atv-4-DW/automanager
./mvnw spring-boot:run
```

API disponível em `http://localhost:8080`

### Exemplo de Login
```bash
curl -X POST http://localhost:8080/login \
  -H "Content-Type: application/json" \
  -d '{"username": "admin", "password": "senha123"}'
```

### Exemplo de Requisição Autenticada
```bash
curl http://localhost:8080/usuarios \
  -H "Authorization: Bearer <seu_token_jwt>"
```

## 👨‍💻 Autor

**Brenno Lyrio** — [GitHub](https://github.com/BrennoLyrio)
