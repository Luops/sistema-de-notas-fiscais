# 🚀 PROMPT PRONTO PARA ANTIGRAVITY - GERAÇÃO DE FRONTEND

## 📋 PROMPT COMPLETO PARA GERAR FRONTEND NO ANTIGRAVITY

Copie e cole todo este texto no AntiGravity para gerar o frontend:

---

### PROMPT ANTIGRAVITY:

```
Você é um desenvolvedor frontend especializado em Angular e TypeScript. 

Preciso que você gere um frontend completo para um sistema de emissão de notas fiscais eletrônicas (NF-e).

## 📊 VISÃO GERAL DO SISTEMA

Sistema Backend desenvolvido em Spring Boot 4.0.1 com Java 17.
- Base de dados: PostgreSQL (porta 5433, database: db_sistema_notas)
- API REST em http://localhost:8080/api/v1
- Autenticação: JWT Token com 24 horas de expiração
- Papéis: ADMIN, VENDEDOR, GERENTE, OBSERVADOR

## 🏗️ ESTRUTURA DE MÓDULOS

O sistema é composto pelos seguintes módulos:

### 1. AUTENTICAÇÃO (Usuario)
- Login: POST /usuario/auth/login (email, senha)
- Retorna: Token JWT + dados do usuário
- CRUD de usuários com soft delete

### 2. CLIENTES
- CRUD completo
- Busca por CPF/CNPJ
- Paginação
- Soft delete

### 3. EMPRESAS
- CRUD completo
- Upload de certificado digital (arquivo .pfx)
- Gerenciamento de certificados

### 4. EMPRESA-USUÁRIO
- Associação de usuários a empresas
- Atribuição de papéis (perfis)
- Busca por empresa, usuário, perfil

### 5. PRODUTOS
- CRUD completo
- Associação a tipos de produtos
- Controle de estoque
- Busca por NCM

### 6. TIPOS DE PRODUTO
- CRUD completo
- Categorização de produtos

### 7. NOTAS FISCAIS
- Criar nota (rascunho)
- Adicionar/editar/remover itens
- Emitir nota (submeter ao SEFAZ)
- Cancelar nota
- Listar com paginação
- Status: RASCUNHO, EMITIDA, CANCELADA

### 8. NF-e (INTEGRAÇÃO SEFAZ)
- Verificar status do serviço SEFAZ
- Emitir NF-e (retorna chave de acesso e protocolo)
- Cancelar NF-e
- Gerar DANFE (PDF)

### 9. NCM
- Consultar dados de NCM
- Buscar alíquotas sugeridas

## 🎯 REQUISITOS FUNCIONAIS

### Autenticação
- Página de login com email e senha
- Armazenar JWT em localStorage
- Refresh automático de token
- Logout
- Proteção de rotas com AuthGuard

### Dashboard
- Resumo de notas emitidas
- Quantidade de clientes
- Produtos em estoque
- Status do SEFAZ (online/offline)
- Últimas notas emitidas

### Gerenciamento de Clientes
- Listagem com paginação
- Filtro por nome, CPF/CNPJ, status (ativo/inativo)
- Criar novo cliente
- Editar cliente
- Ativar/Desativar cliente
- Deletar cliente
- Modal de confirmação para ações destrutivas

### Gerenciamento de Produtos
- Listagem com paginação
- Filtro por tipo, nome, status
- Criar novo produto
- Editar produto
- Ativar/Desativar produto
- Deletar produto

### Gerenciamento de Tipos de Produtos
- CRUD simples
- Listagem com filtros

### Gerenciamento de Empresas
- Listagem de empresas
- Criar empresa
- Editar empresa
- Upload de certificado digital (modal de upload)
- Visualizar informações do certificado
- Remover certificado

### Criação de Notas Fiscais
- Wizard de 5 passos:
  1. Seleção de empresa e cliente
  2. Adição de itens (produto, quantidade, preços)
  3. Revisão de dados
  4. Preview do XML (se possível)
  5. Emissão
- Busca de NCM com autocomplete
- Cálculo automático de impostos (ICMS, PIS, COFINS)
- Tabela dinâmica de itens com edição inline
- Total da nota calculado automaticamente

### Listagem de Notas Fiscais
- Tabela com paginação
- Filtro por empresa, status, data, cliente
- Ações: visualizar, editar (se rascunho), emitir, cancelar, gerar DANFE
- Status visual com cores/badges

### Emissão de NF-e
- Botão "Emitir" na listagem
- Modal de confirmação com validações
- Exibição de status durante emissão (loading)
- Sucesso: mostrar chave de acesso e protocolo
- Erro: mostrar mensagem de erro detalhada
- Gerar DANFE automático após sucesso

### Cancelamento de Notas
- Modal com campo de justificativa
- Validação obrigatória
- Confirmação antes de cancelar
- Sucesso/erro feedback

### Download de DANFE
- Link "Baixar DANFE" em cada nota
- PDF com nome: DANFE_{numeroNota}.pdf
- Visualização inline no navegador também disponível

### Gerenciamento de Usuários
- Listagem com paginação
- Criar usuário
- Editar usuário
- Ativar/Desativar usuário
- Deletar usuário
- Atribuição de papéis

### Associação Empresa-Usuário
- Atribuir usuário a empresa
- Definir perfil/papel do usuário
- Listar usuários por empresa
- Alterar perfil de usuário

## 🎨 DESIGN REQUIREMENTS

- Framework: Angular (última versão estável)
- UI Library: Recomendo Angular Material ou PrimeNG
- Responsivo: Desktop e tablet (mobile opcional)
- Paleta de cores: Profissional (azuis, cinzas, verde para sucesso, vermelho para erro)
- Tema claro e escuro (opcional)
- Ícones: Material Icons ou Font Awesome

## 🔧 ESTRUTURA TÉCNICA

### Arquitetura
- Components: Para cada página/funcionalidade
- Services: Para integração com API
- Guards: AuthGuard para proteção de rotas
- Interceptors: JwtInterceptor para adicionar token
- Models/Interfaces: DTOs do backend
- Pipes: Para formatação de dados
- Directives: Para validações customizadas

### State Management (escolher um)
- RxJS + BehaviorSubject (simples)
- NgRx (enterprise)
- Akita (alternativa)

### Validação
- Validadores do Angular Reactive Forms
- Validação client-side
- Exibição de erros em tempo real
- Desabilitar botão Submit se form inválido

### HTTP
- HttpClient do Angular
- Interceptor para JWT
- Tratamento de erros centralizado
- Toast/Snackbar para notificações
- Loading spinners durante requisições

### Roteamento
- Lazy loading de módulos
- CanActivate guards
- Resolver para pré-carregar dados
- Redirects para home se logout

## 📋 LISTA DE PÁGINAS/COMPONENTES

### Públicas
- LoginComponent
- ForgotPasswordComponent (opcional)

### Privadas (autenticadas)
- DashboardComponent
- ClienteListComponent
- ClienteFormComponent (create/edit)
- ProdutoListComponent
- ProdutoFormComponent
- TipoProdutoListComponent
- TipoProdutoFormComponent
- EmpresaListComponent
- EmpresaFormComponent
- EmpresaCertificadoComponent
- NotaListComponent
- NotaFormComponent (wizard)
- NotaDetailComponent
- UsuarioListComponent
- UsuarioFormComponent
- EmpresaUsuarioComponent

## 🔌 INTEGRAÇÃO COM BACKEND

### Endpoints a Consumir

#### Autenticação
- POST /usuario/auth/login

#### Cliente
- GET /cliente/findAll
- GET /cliente/paginated
- GET /cliente/findById/{id}
- GET /cliente/findByCpfCnpj/{cpfCnpj}
- POST /cliente/create
- PUT /cliente/update/{id}
- PUT /cliente/update/softDelete/{id}
- PUT /cliente/update/activate/{id}
- DELETE /cliente/delete/{id}

#### Empresa
- GET /empresa/findById/{id}
- POST /empresa/create
- PUT /empresa/update/{id}
- PUT /empresa/update/softDelete/{id}
- PUT /empresa/update/activate/{id}
- DELETE /empresa/delete/{id}
- POST /empresa/{id}/certificado/upload
- GET /empresa/{id}/certificado
- DELETE /empresa/{id}/certificado

#### Produto
- GET /produto/findAll
- GET /produto/findById/{id}
- GET /produto/findByTipoProdutoId/{id}
- GET /produto/findByNome/{nome}
- POST /produto/create
- PUT /produto/update/{id}
- PUT /produto/update/softDelete/{id}
- PUT /produto/update/activate/{id}
- DELETE /produto/delete/{id}

#### Tipo Produto
- GET /tipoProduto/findAll
- GET /tipoProduto/findById/{id}
- GET /tipoProduto/findByNome/{nome}
- POST /tipoProduto/create
- PUT /tipoProduto/update/{id}
- PUT /tipoProduto/update/softDelete/{id}
- PUT /tipoProduto/update/activate/{id}
- DELETE /tipoProduto/delete/{id}

#### Nota Fiscal
- GET /notas/findAll
- GET /notas/paginated
- GET /notas/findById/{id}
- GET /notas/find-by-numero-and-empresa?empresaId={id}&numero={numero}
- POST /notas/create
- POST /notas/{id}/add-item
- PUT /notas/{id}/update-item/{itemId}
- DELETE /notas/{id}/remove-item/{itemId}
- PUT /notas/update/{id}
- POST /notas/{id}/emitir
- PUT /notas/cancel/{id}

#### NF-e
- GET /nfe/status-servico
- POST /nfe/emitir/{notaId}
- POST /nfe/cancelar/{notaId}
- GET /nfe/{notaId}/danfe
- GET /nfe/{notaId}/danfe/visualizar

#### NCM
- GET /ncm/consultar/{ncm}
- GET /ncm/aliquotas/{ncm}

#### Usuário
- POST /usuario/create
- GET /usuario/findAll
- GET /usuario/findById/{id}
- GET /usuario/findByEmail/{email}
- GET /usuario/findByNome/{nome}
- GET /usuario/findByAtivo/{ativo}
- PUT /usuario/update/{id}
- PUT /usuario/update/soft-delete/{id}
- PUT /usuario/update/activate/{id}
- DELETE /usuario/delete/{id}

#### Empresa-Usuário
- POST /empresa-usuario/associar
- POST /empresa-usuario/update-perfil
- GET /empresa-usuario/findByEmpresaId/{id}
- GET /empresa-usuario/findByPerfil/{perfil}
- GET /empresa-usuario/findByUsuarioId/{id}
- GET /empresa-usuario/vinculo?empresaId={id}&usuarioId={id}

## 🚀 INSTRUÇÕES FINAIS

1. Criar novo projeto Angular
2. Instalar dependências (Angular Material / PrimeNG, etc)
3. Gerar componentes e serviços
4. Implementar autenticação e guards
5. Criar layout base (header, sidebar, main)
6. Implementar dashboard
7. Implementar cada módulo (clientes, produtos, etc)
8. Implementar gerenciamento de notas com wizard
9. Integrar download de DANFE
10. Testes e ajustes finais

Prioridade de desenvolvimento:
1. Autenticação
2. Dashboard
3. Clientes e Produtos (base)
4. Notas Fiscais (principal funcionalidade)
5. NF-e e DANFE
6. Usuários e Empresas (admin)

Comece a gerar!
```

---

## 💾 VERSÃO SIMPLIFICADA (PARA COLAR DIRETAMENTE)

Se preferir uma versão mais concisa:

```
Gere um frontend em Angular para um sistema de emissão de notas fiscais eletrônicas com os seguintes módulos:

1. AUTENTICAÇÃO: Login com email/senha, JWT token
2. DASHBOARD: Resumo de notas, clientes, produtos
3. CLIENTES: CRUD, paginação, filtros
4. PRODUTOS: CRUD, tipos, NCM
5. NOTAS: Criar (wizard), listar, editar, emitir, cancelar
6. NF-e: Emitir ao SEFAZ, gerar DANFE PDF, cancelar
7. USUÁRIOS: CRUD, atribuição de papéis
8. EMPRESAS: CRUD, upload de certificado

API Backend: http://localhost:8080/api/v1
Autenticação: JWT Bearer Token

Use:
- Angular 17+
- Angular Material ou PrimeNG
- Reactive Forms
- RxJS
- TypeScript

Implemente:
- Lazy loading
- Guards de autenticação
- Interceptor JWT
- Validação de formulários
- Tratamento de erros
- Loading states
- Toast/notifications

Estruture em:
- Components (pages, shared)
- Services (API integration)
- Guards (auth)
- Interceptors (JWT)
- Models/Interfaces
- Pipes e directives

Comece!
```

---

## 🎯 DICAS PARA USAR NO ANTIGRAVITY

1. **Copie todo o prompt acima**
2. **Cole na interface do AntiGravity**
3. **Clique em "Generate" ou "Create"**
4. **Aguarde a geração do projeto**
5. **O código será gerado em blocos de componentes e serviços**
6. **Vá salvando cada arquivo em sua estrutura local**

### Fluxo Esperado:
```
AntiGravity vai gerar:
├── app.module.ts (ou app.config.ts se standalone)
├── app.component.ts/html/css
├── services/
│   ├── api.service.ts
│   ├── auth.service.ts
│   ├── cliente.service.ts
│   ├── produto.service.ts
│   ├── nota.service.ts
│   └── ... outros serviços
├── components/
│   ├── login/
│   ├── dashboard/
│   ├── cliente-list/
│   ├── cliente-form/
│   ├── nota-list/
│   ├── nota-form/ (com wizard)
│   └── ... outros componentes
├── guards/
│   └── auth.guard.ts
├── interceptors/
│   └── jwt.interceptor.ts
└── models/
    └── interfaces do backend
```

---

## 📌 PRÓXIMOS PASSOS APÓS GERAR

1. **Instale as dependências:**
   ```bash
   npm install
   ```

2. **Configure o ambiente:**
   - Verifique a URL do backend em `environment.ts`
   - Configure JWT_TOKEN_KEY se necessário

3. **Inicie o servidor Angular:**
   ```bash
   ng serve
   ```

4. **Acesse:**
   ```
   http://localhost:4200
   ```

5. **Teste o login:**
   - Use as credenciais criadas no backend

6. **Ajustes finais:**
   - Estilos CSS/SCSS
   - Validações adicionais
   - Temas e cores
   - Responsividade

---

## 🔍 CHECKLIST PÓS-GERAÇÃO

- [ ] Projeto Angular criado com sucesso
- [ ] Node modules instalados
- [ ] Servidor inicia sem erros (ng serve)
- [ ] Página de login carrega
- [ ] Autenticação funciona
- [ ] Dashboard carrega após login
- [ ] CRUD de clientes funciona
- [ ] CRUD de produtos funciona
- [ ] Listagem de notas carrega
- [ ] Formulário de nota com wizard abre
- [ ] Emissão de NF-e funciona
- [ ] Download de DANFE funciona
- [ ] Responsividade em tablet/mobile
- [ ] Validações de formulários funcionam
- [ ] Tratamento de erros exibe mensagens
- [ ] Loading states funcionam
- [ ] Logout limpa token e redireciona
- [ ] Guards protegem rotas

---

**Pronto! Agora você tem tudo para gerar um frontend profissional no AntiGravity! 🚀**

