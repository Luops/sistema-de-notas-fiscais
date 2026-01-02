# Sistema de Notas Fiscais - v1.0

Sistema para emissão e gerenciamento de notas fiscais.

## Tecnologias
- Java 17
- Spring Boot 4.0.1
- PostgreSQL
- Flyway
- Lombok

## Como Rodar
1. Criar banco de dados: `createdb notas_fiscais`
2. Configurar application.yml
3. Executar: `./mvnw spring-boot:run`

## Equipe
- Fabrício Lopes
- Gabriel Francisco

🎯 Ordem de Prioridade
Críticas (Fazer primeiro):

Setup (#1-3)
TipoProduto (#4-9)
Entidades base (#10-27)
Produto (#28-33)
Nota/ItemNota (#39-49) ⭐

Altas (Fazer depois):

Autenticação (#50-53)
Testes finais (#58)

Médias (Se der tempo):

EmpresaUsuario (#34-38)
Swagger (#54)
Paginação (#55)
Erros (#56)

Baixas (Opcional v1):

Validações avançadas (#57)
# 📄 Sistema de Notas Fiscais - v1.0

Sistema completo para emissão e gerenciamento de notas fiscais desenvolvido em Java com Spring Boot.

## 👥 Equipe

- **Desenvolvedores:** Fabrício Lopes e Gabriel Francisco
- **Responsáveis:** Fabrício Lopes e Gabriel Francisco

## 🚀 Tecnologias

- **Java** 17
- **Spring Boot** 4.0.1
- **Spring Data JPA** (Hibernate)
- **PostgreSQL** 16
- **Flyway** (Migrations)
- **Lombok**
- **Bean Validation**
- **Maven**

## 📋 Pré-requisitos

- Java 17 ou superior
- PostgreSQL 12 ou superior
- Maven 3.8+ (ou usar o wrapper `./mvnw`)
- Git

## ⚙️ Configuração do Ambiente

### 1. Clonar o Repositório
```bash
git clone <url-do-repositorio>
cd sistemanotas
```

### 2. Criar Banco de Dados
```bash
# Conectar no PostgreSQL
psql -U postgres

# Criar banco
CREATE DATABASE notas_fiscais;

# Sair
\q
```

### 3. Configurar application.yml

O arquivo `src/main/resources/application.yml` já está configurado com valores padrão:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/notas_fiscais
    username: postgres
    password: postgres
```

**Se suas credenciais forem diferentes, ajuste o arquivo!**

### 4. Executar Migrations (Flyway)

As migrations serão executadas automaticamente ao iniciar a aplicação.

O arquivo de migration está em: `src/main/resources/db/migration/V1__create_initial_tables.sql`

## 🏃 Como Executar

### Usando Maven Wrapper (Recomendado):
```bash
# Compilar
./mvnw clean compile

# Executar
./mvnw spring-boot:run
```

### Usando Maven instalado:
```bash
# Compilar
mvn clean compile

# Executar
mvn spring-boot:run
```

### Usando IDE (IntelliJ/Eclipse):
1. Importar projeto como Maven
2. Executar a classe `SistemanotasApplication.java`

A aplicação estará disponível em: **http://localhost:8080**

## 📁 Estrutura do Projeto

```
src/main/java/dev/ellyon/sistemanotas/
├── entity/              # Entidades JPA
├── repository/          # Repositórios Spring Data
├── service/             # Lógica de negócio
├── controller/          # REST Controllers
├── dto/
│   ├── request/         # DTOs de entrada
│   └── response/        # DTOs de saída
├── exception/           # Exceções customizadas
├── config/              # Configurações
└── SistemanotasApplication.java

src/main/resources/
├── application.yml      # Configurações
└── db/
    └── migration/       # Scripts Flyway
        └── V1__create_initial_tables.sql
```

## 📊 Modelo de Dados

O sistema possui as seguintes entidades:

- **TipoProduto** - Categorias de produtos
- **Produto** - Produtos comercializados
- **Empresa** - Empresas emissoras (multi-empresa)
- **Cliente** - Clientes (PF, PJ, Consumidor Final)
- **Usuario** - Usuários do sistema
- **EmpresaUsuario** - Relacionamento N:N com perfis
- **Nota** - Notas fiscais (documento principal)
- **ItemNota** - Itens das notas

## 🔗 Endpoints da API

### Base URL
```
http://localhost:8080/api/v1
```

### Endpoints Disponíveis

#### TipoProduto
- `GET    /tipos-produto` - Listar todos
- `GET    /tipos-produto/{id}` - Buscar por ID
- `POST   /tipos-produto` - Criar novo
- `PUT    /tipos-produto/{id}` - Atualizar
- `DELETE /tipos-produto/{id}` - Deletar
- `PATCH  /tipos-produto/{id}/ativar` - Ativar
- `PATCH  /tipos-produto/{id}/desativar` - Desativar

#### Produto
- `GET    /produtos` - Listar com filtros
- `GET    /produtos/{id}` - Buscar por ID
- `POST   /produtos` - Criar novo
- `PUT    /produtos/{id}` - Atualizar
- `DELETE /produtos/{id}` - Deletar

#### Empresa, Cliente, Usuario
- Seguem o mesmo padrão CRUD

#### Nota Fiscal (Fluxo Completo)
- `POST   /notas` - Criar nota vazia (RASCUNHO)
- `POST   /notas/{id}/itens` - Adicionar produto
- `PUT    /notas/{id}/itens/{itemId}` - Editar item
- `DELETE /notas/{id}/itens/{itemId}` - Remover item
- `POST   /notas/{id}/emitir` - Emitir nota
- `POST   /notas/{id}/cancelar` - Cancelar nota
- `GET    /notas` - Listar com filtros
- `GET    /notas/{id}` - Buscar completa

## 🧪 Testando a API

### Importar Collection do Postman

Importe o arquivo `postman_collection.json` no Postman para ter todos os endpoints prontos.

### Exemplo de Request

**Criar TipoProduto:**
```bash
POST http://localhost:8080/api/v1/tipos-produto
Content-Type: application/json

{
  "nome": "Eletrônicos"
}
```

**Criar Produto:**
```bash
POST http://localhost:8080/api/v1/produtos
Content-Type: application/json

{
  "codigo": "PROD001",
  "nome": "Mouse Gamer RGB",
  "descricao": "Mouse gamer com iluminação RGB",
  "tipoProdutoId": 1,
  "unidade": "UN",
  "precoVenda": 150.00,
  "ncm": "84716069",
  "cfopPadrao": "5102",
  "aliquotaIcmsPadrao": 18.00,
  "aliquotaPisPadrao": 1.65,
  "aliquotaCofinsPadrao": 7.60
}
```

## 🐛 Troubleshooting

### Erro: "Connection refused" ao conectar no PostgreSQL
```bash
# Verificar se PostgreSQL está rodando
sudo service postgresql status

# Iniciar PostgreSQL (Linux)
sudo service postgresql start

# Iniciar PostgreSQL (Mac)
brew services start postgresql

# Windows: Iniciar pelo Services
```

### Erro: "Flyway migration failed"
```bash
# Limpar banco e recriar
psql -U postgres
DROP DATABASE notas_fiscais;
CREATE DATABASE notas_fiscais;
\q

# Executar novamente
./mvnw spring-boot:run
```

### Erro: "Port 8080 already in use"
```bash
# Alterar porta no application.yml
server:
  port: 8081
```

### Logs do Hibernate não aparecem
Verifique o nível de log em `application.yml`:
```yaml
logging:
  level:
    org.hibernate.SQL: DEBUG
```

## 📝 Desenvolvimento

### Branches

- `main` - Código estável (produção)
- `develop` - Integração de features
- `feature/*` - Desenvolvimento de funcionalidades
- `release/*` - Preparação de releases
- `hotfix/*` - Correções urgentes

### Workflow

```bash
# Criar nova feature
git checkout develop
git checkout -b feature/nome-da-feature

# Após concluir
git add .
git commit -m "feat: descrição da feature"
git push origin feature/nome-da-feature

# Criar PR para develop
```

## 📚 Documentação Adicional

- [Documentação Completa](docs/Doc.Projeto-Notas-Fiscais-v01.00.md)
- [História do Usuário](docs/historia-usuario.md)
- [Diagrama UML](docs/uml-diagram.md)
- [Diagrama ER](docs/er-diagram.md)
- [Backlog Sprint 2](docs/sprint2-backlog.md)

## 🔐 Segurança

- Senhas são criptografadas com BCrypt
- JWT será implementado na Sprint 2.5
- CORS configurado para ambientes permitidos

## 📈 Roadmap

### v1.0 (Atual)
- ✅ CRUD completo de entidades base
- ✅ Emissão de notas fiscais (SAIDA)
- ⏳ Autenticação JWT (Sprint 2.5)
- ⏳ Documentação Swagger (Sprint 2.5)

### v2.0 (Futuro)
- ⏳ Integração com SEFAZ (NFe)
- ⏳ Controle de estoque
- ⏳ Relatórios e dashboards
- ⏳ Exportação de nota em PDF
- ⏳ Cálculo automático de impostos interestaduais

## 📞 Contato

- **Fabrício Lopes** - [GitHub](https://github.com/Luops)
- **Gabriel Francisco** - [GitHub](https://github.com/gabeFrancisco)

## 📄 Licença

Este projeto está sob a licença MIT.

---

**Desenvolvido com ❤️ por Fabrício Lopes e Gabriel Francisco**
