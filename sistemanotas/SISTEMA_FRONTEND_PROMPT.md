# 📋 SISTEMA NOTAS - Documentação Completa de Serviços e Rotas

## 📌 Informações do Projeto

**Nome:** Sistema Notas  
**Descrição:** Sistema para emissão de notas fiscais eletrônicas (NF-e)  
**Versão:** 0.0.1-SNAPSHOT  
**Framework:** Spring Boot 4.0.1  
**Linguagem:** Java 17  
**Banco de Dados:** PostgreSQL (porta 5433)  
**Ambiente:** Homologação SEFAZ (NF-e)

---

## 🏗️ Arquitetura do Projeto

### Camadas do Sistema
- **Controllers**: APIs REST que expõem as funcionalidades
- **Services**: Lógica de negócio
- **Repositories**: Acesso a dados (JPA)
- **DTOs**: Transferência de dados entre camadas
- **Models**: Entidades do banco de dados
- **Security**: Autenticação e autorização com JWT

### Padrão de Resposta
Todas as respostas seguem o padrão:
```json
{
  "status": 200,
  "mensagem": "Descrição da operação",
  "data": {}
}
```

---

## 👥 MÓDULO: CLIENTE

### Base Path: `/api/v1/cliente`

#### 1. Criar Cliente
- **Rota:** `POST /create`
- **Autenticação:** Requerida (ADMIN, VENDEDOR)
- **Request Body:** ClienteRequestDTO
- **Response:** ClienteResponseDTO
- **Status:** 201 Created

#### 2. Atualizar Cliente
- **Rota:** `PUT /update/{id}`
- **Autenticação:** Requerida (ADMIN, VENDEDOR)
- **Path Variables:** id (Long)
- **Request Body:** ClienteRequestDTO
- **Response:** ClienteResponseDTO
- **Status:** 200 OK

#### 3. Deletar Cliente (Hard Delete)
- **Rota:** `DELETE /delete/{id}`
- **Autenticação:** Requerida (ADMIN)
- **Path Variables:** id (Long)
- **Response:** null
- **Status:** 200 OK

#### 4. Desativar Cliente (Soft Delete)
- **Rota:** `PUT /update/softDelete/{id}`
- **Autenticação:** Requerida (ADMIN, VENDEDOR)
- **Path Variables:** id (Long)
- **Response:** null
- **Status:** 200 OK

#### 5. Ativar Cliente
- **Rota:** `PUT /update/activate/{id}`
- **Autenticação:** Requerida (ADMIN, VENDEDOR)
- **Path Variables:** id (Long)
- **Response:** null
- **Status:** 200 OK

#### 6. Buscar Cliente por ID
- **Rota:** `GET /findById/{id}`
- **Autenticação:** Requerida (ADMIN, VENDEDOR)
- **Path Variables:** id (Long)
- **Response:** ClienteResponseDTO
- **Status:** 200 OK

#### 7. Listar Todos os Clientes
- **Rota:** `GET /findAll`
- **Autenticação:** Requerida (ADMIN, VENDEDOR)
- **Response:** Array[ClienteListResponseDTO]
- **Status:** 200 OK ou 204 No Content

#### 8. Listar Clientes com Paginação
- **Rota:** `GET /paginated`
- **Autenticação:** Requerida (ADMIN, VENDEDOR)
- **Query Parameters:**
  - `page` (int, default: 0)
  - `size` (int, default: 10)
  - `sort` (String, default: "id")
- **Response:** Page[ClienteListResponseDTO]
- **Status:** 200 OK ou 204 No Content

#### 9. Buscar Cliente por CPF/CNPJ
- **Rota:** `GET /findByCpfCnpj/{cpfCnpj}`
- **Autenticação:** Requerida (ADMIN, VENDEDOR)
- **Path Variables:** cpfCnpj (String)
- **Response:** ClienteResponseDTO
- **Status:** 200 OK

---

## 🏢 MÓDULO: EMPRESA

### Base Path: `/api/v1/empresa`

#### 1. Criar Empresa
- **Rota:** `POST /create`
- **Autenticação:** Requerida
- **Request Body:** EmpresaRequestDTO
- **Response:** EmpresaResponseDTO
- **Status:** 201 Created

#### 2. Atualizar Empresa
- **Rota:** `PUT /update/{id}`
- **Autenticação:** Requerida (ADMIN)
- **Path Variables:** id (Long)
- **Request Body:** EmpresaRequestDTO
- **Response:** EmpresaResponseDTO
- **Status:** 200 OK

#### 3. Deletar Empresa
- **Rota:** `DELETE /delete/{id}`
- **Autenticação:** Requerida (ADMIN)
- **Path Variables:** id (Long)
- **Response:** null
- **Status:** 200 OK

#### 4. Desativar Empresa (Soft Delete)
- **Rota:** `PUT /update/softDelete/{id}`
- **Autenticação:** Requerida (ADMIN)
- **Path Variables:** id (Long)
- **Response:** null
- **Status:** 200 OK

#### 5. Ativar Empresa
- **Rota:** `PUT /update/activate/{id}`
- **Autenticação:** Requerida (ADMIN)
- **Path Variables:** id (Long)
- **Response:** null
- **Status:** 200 OK

#### 6. Upload de Certificado Digital
- **Rota:** `POST /{empresaId}/certificado/upload`
- **Autenticação:** Requerida (ADMIN)
- **Content-Type:** multipart/form-data
- **Path Variables:** empresaId (Long)
- **Request Body:** CertificadoUploadDTO (arquivo .pfx, senha)
- **Response:** CertificadoResponseDTO
- **Status:** 200 OK

#### 7. Buscar Informações do Certificado
- **Rota:** `GET /{empresaId}/certificado`
- **Autenticação:** Requerida (ADMIN)
- **Path Variables:** empresaId (Long)
- **Response:** CertificadoResponseDTO
- **Status:** 200 OK

#### 8. Remover Certificado
- **Rota:** `DELETE /{empresaId}/certificado`
- **Autenticação:** Requerida (ADMIN)
- **Path Variables:** empresaId (Long)
- **Response:** null
- **Status:** 200 OK

#### 9. Buscar Empresa por ID
- **Rota:** `GET /findById/{id}`
- **Autenticação:** Requerida (ADMIN)
- **Path Variables:** id (Long)
- **Response:** EmpresaResponseDTO
- **Status:** 200 OK

---

## 👤 MÓDULO: EMPRESA-USUÁRIO

### Base Path: `/api/v1/empresa-usuario`

#### 1. Associar Empresa e Usuário
- **Rota:** `POST /associar`
- **Autenticação:** Requerida (ADMIN)
- **Request Body:** EmpresaUsuarioRequestDTO (empresaId, usuarioId, perfil)
- **Response:** EmpresaUsuarioResponseDTO
- **Status:** 201 Created

#### 2. Alterar Perfil de Usuário em Empresa
- **Rota:** `POST /update-perfil`
- **Autenticação:** Requerida (ADMIN)
- **Request Body:** EmpresaUsuarioRequestDTO
- **Response:** EmpresaUsuarioResponseDTO
- **Status:** 200 OK

#### 3. Buscar Usuários por Empresa
- **Rota:** `GET /findByEmpresaId/{empresaId}`
- **Autenticação:** Requerida (ADMIN)
- **Path Variables:** empresaId (Long)
- **Response:** Array[EmpresaUsuarioResponseDTO]
- **Status:** 200 OK

#### 4. Buscar Usuários por Perfil
- **Rota:** `GET /findByPerfil/{perfil}`
- **Autenticação:** Requerida (ADMIN)
- **Path Variables:** perfil (String)
- **Response:** Array[EmpresaUsuarioResponseDTO]
- **Status:** 200 OK

#### 5. Buscar Empresas por Usuário
- **Rota:** `GET /findByUsuarioId/{usuarioId}`
- **Autenticação:** Requerida (ADMIN, VENDEDOR)
- **Path Variables:** usuarioId (Long)
- **Response:** Array[EmpresaUsuarioResponseDTO]
- **Status:** 200 OK

#### 6. Buscar Vínculo Específico Empresa-Usuário
- **Rota:** `GET /vinculo`
- **Autenticação:** Requerida (ADMIN, VENDEDOR)
- **Query Parameters:** empresaId (Long), usuarioId (Long)
- **Response:** EmpresaUsuarioResponseDTO
- **Status:** 200 OK

---

## 📦 MÓDULO: PRODUTO

### Base Path: `/api/v1/produto`

#### 1. Criar Produto
- **Rota:** `POST /create`
- **Autenticação:** Requerida (ADMIN, VENDEDOR)
- **Request Body:** ProdutoRequestDTO
- **Response:** ProdutoResponseDTO
- **Status:** 201 Created

#### 2. Deletar Produto
- **Rota:** `DELETE /delete/{id}`
- **Autenticação:** Requerida (ADMIN)
- **Path Variables:** id (Long)
- **Response:** null
- **Status:** 200 OK

#### 3. Atualizar Produto
- **Rota:** `PUT /update/{id}`
- **Autenticação:** Requerida (ADMIN, VENDEDOR)
- **Path Variables:** id (Long)
- **Request Body:** ProdutoRequestDTO
- **Response:** null
- **Status:** 200 OK

#### 4. Desativar Produto (Soft Delete)
- **Rota:** `PUT /update/softDelete/{id}`
- **Autenticação:** Requerida (ADMIN, VENDEDOR)
- **Path Variables:** id (Long)
- **Response:** null
- **Status:** 200 OK

#### 5. Ativar Produto
- **Rota:** `PUT /update/activate/{id}`
- **Autenticação:** Requerida (ADMIN, VENDEDOR)
- **Path Variables:** id (Long)
- **Response:** null
- **Status:** 200 OK

#### 6. Buscar Produto por ID
- **Rota:** `GET /findById/{id}`
- **Autenticação:** Requerida (ADMIN, VENDEDOR)
- **Path Variables:** id (Long)
- **Response:** ProdutoResponseDTO
- **Status:** 200 OK

#### 7. Listar Todos os Produtos
- **Rota:** `GET /findAll`
- **Autenticação:** Requerida (ADMIN, VENDEDOR)
- **Response:** Array[ProdutoListResponseDTO]
- **Status:** 200 OK

#### 8. Buscar Produtos por Tipo (ID)
- **Rota:** `GET /findByTipoProdutoId/{tipoProdutoId}`
- **Autenticação:** Requerida (ADMIN, VENDEDOR)
- **Path Variables:** tipoProdutoId (Long)
- **Response:** Array[ProdutoListResponseDTO]
- **Status:** 200 OK

#### 9. Buscar Produtos por Tipo (Nome)
- **Rota:** `GET /findByTipoProdutoNome/{tipoProdutoNome}`
- **Autenticação:** Requerida (ADMIN, VENDEDOR)
- **Path Variables:** tipoProdutoNome (String)
- **Response:** Array[ProdutoListResponseDTO]
- **Status:** 200 OK

#### 10. Buscar Produtos por Status (Ativo/Inativo)
- **Rota:** `GET /findByAtivoInativo/{ativo}`
- **Autenticação:** Requerida (ADMIN, VENDEDOR)
- **Path Variables:** ativo (Boolean)
- **Response:** Array[ProdutoListResponseDTO]
- **Status:** 200 OK

#### 11. Buscar Produtos por Nome
- **Rota:** `GET /findByNome/{nome}`
- **Autenticação:** Requerida (ADMIN, VENDEDOR)
- **Path Variables:** nome (String)
- **Response:** Array[ProdutoListResponseDTO]
- **Status:** 200 OK

#### 12. Buscar Produtos por Código
- **Rota:** `GET /findByCodigoProduto/{codigoProduto}`
- **Autenticação:** Requerida (ADMIN, VENDEDOR)
- **Path Variables:** codigoProduto (String)
- **Response:** Array[ProdutoListResponseDTO]
- **Status:** 200 OK

---

## 📂 MÓDULO: TIPO PRODUTO

### Base Path: `/api/v1/tipoProduto`

#### 1. Criar Tipo de Produto
- **Rota:** `POST /create`
- **Autenticação:** Requerida (ADMIN, VENDEDOR)
- **Request Body:** TipoProdutoRequestDTO
- **Response:** TipoProdutoResponseDTO
- **Status:** 201 Created

#### 2. Atualizar Tipo de Produto
- **Rota:** `PUT /update/{id}`
- **Autenticação:** Requerida (ADMIN, VENDEDOR)
- **Path Variables:** id (Long)
- **Request Body:** TipoProdutoRequestDTO
- **Response:** TipoProdutoResponseDTO
- **Status:** 200 OK

#### 3. Deletar Tipo de Produto
- **Rota:** `DELETE /delete/{id}`
- **Autenticação:** Requerida (ADMIN)
- **Path Variables:** id (Long)
- **Response:** null
- **Status:** 200 OK

#### 4. Desativar Tipo de Produto
- **Rota:** `PUT /update/softDelete/{id}`
- **Autenticação:** Requerida (ADMIN, VENDEDOR)
- **Path Variables:** id (Long)
- **Response:** null
- **Status:** 200 OK

#### 5. Ativar Tipo de Produto
- **Rota:** `PUT /update/activate/{id}`
- **Autenticação:** Requerida (ADMIN, VENDEDOR)
- **Path Variables:** id (Long)
- **Response:** null
- **Status:** 200 OK

#### 6. Buscar Tipo de Produto por ID
- **Rota:** `GET /findById/{id}`
- **Autenticação:** Requerida (ADMIN, VENDEDOR)
- **Path Variables:** id (Long)
- **Response:** TipoProdutoResponseDTO
- **Status:** 200 OK

#### 7. Listar Todos os Tipos de Produto
- **Rota:** `GET /findAll`
- **Autenticação:** Requerida (ADMIN, VENDEDOR)
- **Response:** Array[TipoProdutoResponseDTO]
- **Status:** 200 OK

#### 8. Buscar Tipos de Produto por Status (Ativo/Inativo)
- **Rota:** `GET /findByAtivoInativo/{ativo}`
- **Autenticação:** Requerida (ADMIN, VENDEDOR)
- **Path Variables:** ativo (Boolean)
- **Response:** Array[TipoProdutoResponseDTO]
- **Status:** 200 OK

#### 9. Buscar Tipos de Produto por Nome
- **Rota:** `GET /findByNome/{nome}`
- **Autenticação:** Requerida (ADMIN, VENDEDOR)
- **Path Variables:** nome (String)
- **Response:** Array[TipoProdutoResponseDTO]
- **Status:** 200 OK

#### 10. Buscar Tipos de Produto por Data de Criação
- **Rota:** `GET /findByCreatedAtBetween`
- **Autenticação:** Requerida (ADMIN, VENDEDOR)
- **Query Parameters:**
  - `dataInicio` (LocalDate - ISO format)
  - `dataFim` (LocalDate - ISO format)
- **Response:** Array[TipoProdutoResponseDTO]
- **Status:** 200 OK

---

## 📄 MÓDULO: NOTA (NF-e)

### Base Path: `/api/v1/notas`

#### 1. Criar Nota (Rascunho)
- **Rota:** `POST /create`
- **Autenticação:** Requerida (ADMIN, VENDEDOR)
- **Request Body:** NotaRequestDTO
- **Response:** NotaResponseDTO
- **Status:** 201 Created

#### 2. Adicionar Item à Nota
- **Rota:** `POST /{notaId}/add-item`
- **Autenticação:** Requerida (ADMIN, VENDEDOR)
- **Path Variables:** notaId (Long)
- **Request Body:** ItemNotaRequestDTO
- **Response:** NotaResponseDTO
- **Status:** 200 OK

#### 3. Atualizar Item da Nota
- **Rota:** `PUT /{notaId}/update-item/{itemId}`
- **Autenticação:** Requerida (ADMIN, VENDEDOR)
- **Path Variables:** notaId (Long), itemId (Long)
- **Request Body:** ItemNotaRequestDTO
- **Response:** NotaResponseDTO
- **Status:** 200 OK

#### 4. Remover Item da Nota
- **Rota:** `DELETE /{notaId}/remove-item/{itemId}`
- **Autenticação:** Requerida (ADMIN, VENDEDOR)
- **Path Variables:** notaId (Long), itemId (Long)
- **Response:** NotaResponseDTO
- **Status:** 200 OK

#### 5. Emitir Nota (Submeter ao SEFAZ)
- **Rota:** `POST /{notaId}/emitir`
- **Autenticação:** Requerida (ADMIN, VENDEDOR)
- **Path Variables:** notaId (Long)
- **Response:** NotaResponseDTO
- **Status:** 200 OK

#### 6. Atualizar Dados da Nota
- **Rota:** `PUT /update/{notaId}`
- **Autenticação:** Requerida (ADMIN, VENDEDOR)
- **Path Variables:** notaId (Long)
- **Request Body:** NotaRequestDTO
- **Response:** NotaResponseDTO
- **Status:** 200 OK

#### 7. Cancelar Nota
- **Rota:** `PUT /cancel/{notaId}`
- **Autenticação:** Requerida (ADMIN)
- **Path Variables:** notaId (Long)
- **Response:** null
- **Status:** 200 OK

#### 8. Buscar Nota por ID
- **Rota:** `GET /findById/{notaId}`
- **Autenticação:** Requerida (ADMIN, VENDEDOR)
- **Path Variables:** notaId (Long)
- **Response:** NotaResponseDTO
- **Status:** 200 OK

#### 9. Listar Todas as Notas
- **Rota:** `GET /findAll`
- **Autenticação:** Requerida (ADMIN, VENDEDOR)
- **Response:** Array[NotaListResponseDTO]
- **Status:** 200 OK

#### 10. Listar Notas com Paginação
- **Rota:** `GET /paginated`
- **Autenticação:** Requerida (ADMIN, VENDEDOR)
- **Query Parameters:**
  - `page` (int, default: 0)
  - `size` (int, default: 10)
  - `sort` (String, default: "id")
- **Response:** Page[NotaListResponseDTO]
- **Status:** 200 OK

#### 11. Buscar Nota por Número e Empresa
- **Rota:** `GET /find-by-numero-and-empresa`
- **Autenticação:** Requerida (ADMIN, VENDEDOR)
- **Query Parameters:** empresaId (Long), numero (String)
- **Response:** NotaResponseDTO
- **Status:** 200 OK

---

## 🔐 MÓDULO: AUTENTICAÇÃO E USUÁRIO

### Base Path: `/api/v1/usuario`

#### 1. Criar Usuário
- **Rota:** `POST /create`
- **Autenticação:** Não requerida
- **Request Body:** UsuarioRequestDTO (nome, email, senha, etc.)
- **Response:** UsuarioResponseDTO
- **Status:** 201 Created

#### 2. Login (Autenticação)
- **Rota:** `POST /auth/login`
- **Autenticação:** Não requerida
- **Request Body:** LoginRequestDTO (email, senha)
- **Response:** LoginResponseDTO (token JWT)
- **Status:** 200 OK

#### 3. Atualizar Usuário
- **Rota:** `PUT /update/{id}`
- **Autenticação:** Requerida (ADMIN ou próprio perfil)
- **Path Variables:** id (Long)
- **Request Body:** UsuarioUpdateRequestDTO
- **Response:** UsuarioResponseDTO
- **Status:** 200 OK

#### 4. Deletar Usuário
- **Rota:** `DELETE /delete/{id}`
- **Autenticação:** Requerida (ADMIN)
- **Path Variables:** id (Long)
- **Response:** null
- **Status:** 200 OK

#### 5. Desativar Usuário (Soft Delete)
- **Rota:** `PUT /update/soft-delete/{id}`
- **Autenticação:** Requerida (ADMIN)
- **Path Variables:** id (Long)
- **Response:** null
- **Status:** 200 OK

#### 6. Ativar Usuário
- **Rota:** `PUT /update/activate/{id}`
- **Autenticação:** Requerida (ADMIN)
- **Path Variables:** id (Long)
- **Response:** null
- **Status:** 200 OK

#### 7. Buscar Usuário por ID
- **Rota:** `GET /findById/{id}`
- **Autenticação:** Requerida (ADMIN ou próprio perfil)
- **Path Variables:** id (Long)
- **Response:** UsuarioResponseDTO
- **Status:** 200 OK

#### 8. Listar Todos os Usuários
- **Rota:** `GET /findAll`
- **Autenticação:** Requerida (ADMIN)
- **Response:** Array[UsuarioResponseDTO]
- **Status:** 200 OK

#### 9. Buscar Usuários por Email
- **Rota:** `GET /findByEmail/{email}`
- **Autenticação:** Requerida (ADMIN ou proprietário)
- **Path Variables:** email (String)
- **Response:** Array[UsuarioResponseDTO]
- **Status:** 200 OK

#### 10. Buscar Usuários por Nome
- **Rota:** `GET /findByNome/{nome}`
- **Autenticação:** Requerida
- **Path Variables:** nome (String)
- **Response:** Array[UsuarioResponseDTO]
- **Status:** 200 OK

#### 11. Buscar Usuários por Status (Ativo/Inativo)
- **Rota:** `GET /findByAtivo/{ativo}`
- **Autenticação:** Requerida
- **Path Variables:** ativo (Boolean)
- **Response:** Array[UsuarioResponseDTO]
- **Status:** 200 OK

---

## 📌 MÓDULO: NCM (NOMENCLATURA COMUM DO MERCOSUL)

### Base Path: `/api/v1/ncm`

#### 1. Consultar Dados de um NCM
- **Rota:** `GET /consultar/{ncm}`
- **Autenticação:** Não requerida
- **Path Variables:** ncm (String - 8 dígitos)
- **Response:** Map com dados do NCM
- **Status:** 200 OK ou 400 Bad Request

#### 2. Buscar Alíquotas Sugeridas para um NCM
- **Rota:** `GET /aliquotas/{ncm}`
- **Autenticação:** Não requerida
- **Path Variables:** ncm (String - 8 dígitos)
- **Response:** Map<String, BigDecimal> com alíquotas (ICMS, PIS, COFINS, etc.)
- **Status:** 200 OK

---

## 🧾 MÓDULO: NF-e (NOTA FISCAL ELETRÔNICA)

### Base Path: `/api/v1/nfe`

#### 1. Consultar Status do Serviço SEFAZ
- **Rota:** `GET /status-servico`
- **Autenticação:** Não requerida
- **Response:** NFeStatusDTO (online: boolean, versao: String, etc.)
- **Status:** 200 OK

#### 2. Emitir NF-e
- **Rota:** `POST /emitir/{notaId}`
- **Autenticação:** Requerida (ADMIN, VENDEDOR)
- **Path Variables:** notaId (Long)
- **Response:** NFeResponseDTO (chaveAcesso, codigoStatus, protocolo, mensagem)
- **Status:** 200 OK

#### 3. Cancelar NF-e
- **Rota:** `POST /cancelar/{notaId}`
- **Autenticação:** Requerida (ADMIN)
- **Path Variables:** notaId (Long)
- **Request Body:** CancelamentoNFeDTORequest (justificativa)
- **Response:** NFeResponseDTO
- **Status:** 200 OK

#### 4. Gerar DANFE (PDF)
- **Rota:** `GET /{notaId}/danfe`
- **Autenticação:** Não requerida
- **Path Variables:** notaId (Long)
- **Response:** File (application/pdf) - download
- **Status:** 200 OK

#### 5. Visualizar DANFE (PDF)
- **Rota:** `GET /{notaId}/danfe/visualizar`
- **Autenticação:** Não requerida
- **Path Variables:** notaId (Long)
- **Response:** File (application/pdf) - inline (visualização no navegador)
- **Status:** 200 OK

---

## 🗄️ MODELO DE DADOS

### Entidades Principais

#### 1. **Usuario**
Armazena informações dos usuários do sistema.
- Campos: id, nome, email, senha (criptografada), telefone, cpf, dataCriacao, ativo

#### 2. **Empresa**
Armazena dados das empresas emissoras de NF-e.
- Campos: id, razaoSocial, nomeFantasia, cnpj, inscricaoEstadual, endereco, telefone, email, certificadoCaminho, certificadoSenha, dataCriacao, ativo

#### 3. **EmpresaUsuario**
Associa usuários a empresas com diferentes perfis.
- Campos: id, usuarioId, empresaId, perfil (ADMIN, GERENTE, VENDEDOR, OBSERVADOR), dataCriacao

#### 4. **Cliente**
Armazena dados dos clientes/destinatários das NF-e.
- Campos: id, empresaId, nome, email, telefone, cpfCnpj, cpfCnpjHash, tipoPessoa (PF ou PJ), endereco, numero, complemento, bairro, cep, municipio, uf, dataCriacao, ativo

#### 5. **TipoProduto**
Categorias de produtos (Ex: Eletrônicos, Vestuário, etc.).
- Campos: id, empresaId, nome, descricao, dataCriacao, ativo

#### 6. **Produto**
Armazena produtos que podem ser vendidos.
- Campos: id, empresaId, tipoProdutoId, nome, descricao, codigoProduto, codigoInterno, ncm, precoUnitario, estoque, unidadeMedida, dataCriacao, ativo

#### 7. **Nota**
Armazena notas fiscais (rascunho ou emitidas).
- Campos: id, empresaId, clienteId, numero, serieNota, status (RASCUNHO, EMITIDA, CANCELADA), dataEmissao, chaveAcesso, protoCancelamento, justificativaCancelamento, xmlNfe, frete, dataCriacao

#### 8. **ItemNota**
Itens de uma nota fiscal.
- Campos: id, notaId, produtoId, quantidade, precoUnitario, aliquotaIcms, aliquotaPis, aliquotaCofins, desconto, subtotal, dataCriacao

#### 9. **Cfop**
Código Fiscal de Operação e Prestação (classificação fiscal).
- Campos: id, codigo, descricao

---

## 🔑 AUTENTICAÇÃO E AUTORIZAÇÃO

### JWT Token
- **Secret:** 3cfa76ef14937c1c0ea519f8fc057a80fcd04a7420f8e8bcd0a7567c272e007b
- **Expiration:** 86400000ms (24 horas)
- **Header:** Authorization: Bearer {token}

### Papéis (Roles)
- **ADMIN**: Acesso total ao sistema
- **VENDEDOR**: Pode criar/editar clientes, produtos e notas
- **GERENTE**: Pode visualizar relatórios e gerenciar vendedores
- **OBSERVADOR**: Apenas leitura de dados

---

## 🔧 CONFIGURAÇÕES IMPORTANTES

### NF-e (Nota Fiscal Eletrônica)
- **Ambiente:** Homologação (2)
- **Tipo de Certificado:** A1 (arquivo .pfx)
- **Estado:** RS (Rio Grande do Sul)
- **Código Município:** 4305108 (Cachoeirinha/RS)
- **SEFAZ URLs:** Configuradas para homologação RS
- **Mock SEFAZ:** Habilitado (true) para testes

### Banco de Dados
- **Driver:** PostgreSQL
- **Host:** localhost
- **Porta:** 5433
- **Database:** db_sistema_notas
- **User:** postgres
- **Password:** postgres

### Flyway Migrations
- Automático na inicialização
- Localização: `/db/migration`
- Versões: V1 até V15

---

## 📊 FLUXO TÍPICO DE USO

### 1. Fluxo de Criar e Emitir NF-e
```
1. Login do Usuário (POST /usuario/auth/login)
2. Criar Cliente (POST /cliente/create)
3. Criar Produtos (POST /produto/create)
4. Criar Nota (POST /notas/create)
5. Adicionar Itens à Nota (POST /notas/{id}/add-item)
6. Consultar NCM se necessário (GET /ncm/consultar/{ncm})
7. Verificar Status SEFAZ (GET /nfe/status-servico)
8. Emitir Nota (POST /notas/{id}/emitir)
9. A emissão chama internamente (POST /nfe/emitir/{notaId})
10. Gerar DANFE (GET /nfe/{notaId}/danfe)
```

### 2. Fluxo de Gerenciamento de Empresas
```
1. Criar Empresa (POST /empresa/create)
2. Upload de Certificado (POST /empresa/{id}/certificado/upload)
3. Associar Usuários à Empresa (POST /empresa-usuario/associar)
4. Designar Perfis aos Usuários (POST /empresa-usuario/update-perfil)
```

### 3. Fluxo de Cancelamento
```
1. Buscar Nota (GET /notas/findById/{id})
2. Cancelar Nota (PUT /notas/cancel/{id})
3. Cancelar NF-e no SEFAZ (POST /nfe/cancelar/{notaId})
```

---

## 🎯 RECOMENDAÇÕES PARA FRONTEND

### Componentes Principais a Desenvolver

#### Dashboard
- Exibir resumo de notas emitidas
- Número de clientes cadastrados
- Produtos em estoque
- Status do SEFAZ

#### Cadastros (CRUD)
- **Clientes:** Listagem paginada, busca por CPF/CNPJ, formulário de criação/edição
- **Produtos:** Listagem com filtros, associação a tipos
- **Tipos de Produtos:** Gerenciamento de categorias
- **Usuários:** Listagem, atribuição de roles, ativação/desativação
- **Empresas:** Gerenciamento e upload de certificados

#### Notas Fiscais
- Listagem com filtros e paginação
- Editor visual de notas (adicionar/remover itens)
- Buscador de NCM com autocomplete
- Preview do XML antes de emitir
- Geração de DANFE (PDF)
- Histórico de cancelamentos

#### Autenticação
- Tela de login
- Persistência de token JWT
- Refresh token handling
- Logout

#### Integração com SEFAZ
- Monitor de status do serviço SEFAZ
- Exibição de chave de acesso
- Exibição de protocolo de autorização
- Logs de tentativas de emissão

### Padrões de Integração

#### Chamadas HTTP
```typescript
// GET
GET /api/v1/cliente/findAll
Authorization: Bearer {token}

// POST
POST /api/v1/cliente/create
Authorization: Bearer {token}
Content-Type: application/json
{
  "nome": "João Silva",
  "email": "joao@email.com",
  "cpfCnpj": "12345678901",
  ...
}

// PUT
PUT /api/v1/cliente/update/123
Authorization: Bearer {token}
Content-Type: application/json
{...}

// DELETE
DELETE /api/v1/cliente/delete/123
Authorization: Bearer {token}
```

#### Tratamento de Erros
```json
{
  "status": 400,
  "mensagem": "Descrição do erro",
  "data": {
    "campo1": "Erro de validação",
    "campo2": "Outro erro"
  }
}
```

#### Paginação
```typescript
// Request
GET /api/v1/cliente/paginated?page=0&size=10&sort=id,desc

// Response
{
  "content": [...],
  "pageable": {
    "pageNumber": 0,
    "pageSize": 10,
    "sort": {...}
  },
  "totalElements": 100,
  "totalPages": 10,
  "last": false,
  "first": true,
  ...
}
```

---

## 📋 CHECKLIST DE ENDPOINTS A IMPLEMENTAR

### Autenticação
- [ ] Login
- [ ] Logout
- [ ] Refresh Token
- [ ] Validar Token

### Usuários
- [ ] Create
- [ ] Read (findById, findAll, findByEmail, findByNome, findByAtivo)
- [ ] Update
- [ ] Delete (hard e soft)
- [ ] Activate

### Clientes
- [ ] Create
- [ ] Read (findById, findAll, findByCpfCnpj, paginated)
- [ ] Update
- [ ] Delete (hard e soft)
- [ ] Activate

### Empresas
- [ ] Create
- [ ] Read (findById)
- [ ] Update
- [ ] Delete (hard e soft)
- [ ] Activate
- [ ] Upload Certificado
- [ ] Buscar Certificado
- [ ] Remover Certificado

### Empresa-Usuário
- [ ] Associar
- [ ] Alterar Perfil
- [ ] findByEmpresaId
- [ ] findByPerfil
- [ ] findByUsuarioId
- [ ] findByUsuarioIdAndEmpresaId

### Produtos
- [ ] Create
- [ ] Read (findById, findAll, findByTipoProdutoId, findByTipoProdutoNome, findByAtivoInativo, findByNome, findByCodigoProduto)
- [ ] Update
- [ ] Delete (hard e soft)
- [ ] Activate

### Tipos de Produtos
- [ ] Create
- [ ] Read (findById, findAll, findByAtivoInativo, findByNome, findByCreatedAtBetween)
- [ ] Update
- [ ] Delete (hard e soft)
- [ ] Activate

### Notas Fiscais
- [ ] Create
- [ ] Add Item
- [ ] Update Item
- [ ] Remove Item
- [ ] Read (findById, findAll, paginated, findByNumeroAndEmpresa)
- [ ] Update
- [ ] Emitir
- [ ] Cancel

### NF-e
- [ ] Status SEFAZ
- [ ] Emitir NF-e
- [ ] Cancelar NF-e
- [ ] Gerar DANFE (PDF download)
- [ ] Visualizar DANFE (PDF inline)

### NCM
- [ ] Consultar NCM
- [ ] Buscar Alíquotas

---

## 🚀 PRÓXIMOS PASSOS

1. **Criar serviços HTTP** no frontend para cada módulo
2. **Implementar interceptor** de JWT para adicionar token em todas requisições
3. **Criar estado global** (Redux/Context API) para autenticação e dados
4. **Validação de formulários** client-side
5. **Tratamento de erros** e exibição de notificações
6. **Testes unitários** para os serviços HTTP
7. **Cache inteligente** para dados que não mudam frequentemente
8. **Sincronização otimista** de dados

---

**Documento gerado em:** 2026-03-20  
**Versão do Sistema:** 0.0.1-SNAPSHOT  
**Status:** Análise Completa

