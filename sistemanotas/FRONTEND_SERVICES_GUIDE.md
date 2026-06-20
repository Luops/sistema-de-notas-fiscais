# 🌐 GUIA DE IMPLEMENTAÇÃO - SERVIÇOS HTTP FRONTEND

## 📐 Arquitetura de Serviços Sugerida para o Frontend

### Estrutura de Pastas Recomendada
```
src/
├── services/
│   ├── api.service.ts          // Configuração base de HTTP
│   ├── auth.service.ts         // Login e autenticação
│   ├── cliente.service.ts      // CRUD de clientes
│   ├── empresa.service.ts      // CRUD de empresas
│   ├── usuario.service.ts      // CRUD de usuários
│   ├── produto.service.ts      // CRUD de produtos
│   ├── tipoProduto.service.ts  // CRUD de tipos
│   ├── nota.service.ts         // CRUD de notas
│   ├── nfe.service.ts          // Emissão de NF-e
│   ├── ncm.service.ts          // Consultas NCM
│   └── empresaUsuario.service.ts // Associação
├── models/
│   ├── usuario.model.ts
│   ├── cliente.model.ts
│   ├── empresa.model.ts
│   ├── produto.model.ts
│   ├── nota.model.ts
│   └── ...
├── interceptors/
│   └── jwt.interceptor.ts      // Adiciona token em requisições
├── guards/
│   └── auth.guard.ts           // Proteção de rotas
└── state/
    └── [Redux/Context/Zustand] // Estado global
```

---

## 🔧 EXEMPLO: Base Service (api.service.ts)

```typescript
import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface SuccessResponseDTO {
  status: number;
  mensagem: string;
  data: any;
}

export interface PaginationParams {
  page?: number;
  size?: number;
  sort?: string;
}

@Injectable({
  providedIn: 'root'
})
export class ApiService {
  private readonly API_BASE_URL = 'http://localhost:8080/api/v1';
  private readonly JWT_TOKEN_KEY = 'auth_token';

  constructor(private http: HttpClient) {}

  /**
   * Extrai a resposta data do padrão SuccessResponseDTO
   */
  private extractData<T>(response: SuccessResponseDTO): T {
    return response.data as T;
  }

  /**
   * GET genérico
   */
  get<T>(endpoint: string, params?: any): Observable<SuccessResponseDTO> {
    let httpParams = new HttpParams();
    if (params) {
      Object.keys(params).forEach(key => {
        httpParams = httpParams.set(key, params[key]);
      });
    }
    return this.http.get<SuccessResponseDTO>(
      `${this.API_BASE_URL}${endpoint}`,
      { params: httpParams }
    );
  }

  /**
   * POST genérico
   */
  post<T>(endpoint: string, body: any): Observable<SuccessResponseDTO> {
    return this.http.post<SuccessResponseDTO>(
      `${this.API_BASE_URL}${endpoint}`,
      body
    );
  }

  /**
   * PUT genérico
   */
  put<T>(endpoint: string, body: any): Observable<SuccessResponseDTO> {
    return this.http.put<SuccessResponseDTO>(
      `${this.API_BASE_URL}${endpoint}`,
      body
    );
  }

  /**
   * DELETE genérico
   */
  delete<T>(endpoint: string): Observable<SuccessResponseDTO> {
    return this.http.delete<SuccessResponseDTO>(
      `${this.API_BASE_URL}${endpoint}`
    );
  }

  /**
   * Upload de arquivo (multipart/form-data)
   */
  uploadFile(endpoint: string, formData: FormData): Observable<SuccessResponseDTO> {
    return this.http.post<SuccessResponseDTO>(
      `${this.API_BASE_URL}${endpoint}`,
      formData
    );
  }

  /**
   * Download de arquivo (ex: DANFE PDF)
   */
  downloadFile(endpoint: string, filename: string): void {
    this.http.get(`${this.API_BASE_URL}${endpoint}`, {
      responseType: 'blob'
    }).subscribe({
      next: (blob) => {
        const url = window.URL.createObjectURL(blob);
        const link = document.createElement('a');
        link.href = url;
        link.download = filename;
        link.click();
        window.URL.revokeObjectURL(url);
      },
      error: (err) => {
        console.error('Erro ao baixar arquivo', err);
      }
    });
  }

  /**
   * Salva o token JWT no localStorage
   */
  setToken(token: string): void {
    localStorage.setItem(this.JWT_TOKEN_KEY, token);
  }

  /**
   * Obtém o token JWT do localStorage
   */
  getToken(): string | null {
    return localStorage.getItem(this.JWT_TOKEN_KEY);
  }

  /**
   * Remove o token JWT
   */
  clearToken(): void {
    localStorage.removeItem(this.JWT_TOKEN_KEY);
  }

  /**
   * Verifica se está autenticado
   */
  isAuthenticated(): boolean {
    return !!this.getToken();
  }
}
```

---

## 🔐 EXEMPLO: Auth Service (auth.service.ts)

```typescript
import { Injectable } from '@angular/core';
import { Observable, BehaviorSubject } from 'rxjs';
import { map, tap } from 'rxjs/operators';
import { ApiService, SuccessResponseDTO } from './api.service';

export interface LoginRequest {
  email: string;
  senha: string;
}

export interface LoginResponse {
  token: string;
  usuario: {
    id: number;
    nome: string;
    email: string;
    roles: string[];
  };
}

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private currentUser$ = new BehaviorSubject<any>(null);
  private isAuthenticated$ = new BehaviorSubject<boolean>(false);

  constructor(private apiService: ApiService) {
    this.checkAuthStatus();
  }

  /**
   * Realiza login
   */
  login(email: string, senha: string): Observable<LoginResponse> {
    const payload: LoginRequest = { email, senha };

    return this.apiService.post<SuccessResponseDTO>(
      '/usuario/auth/login',
      payload
    ).pipe(
      map(response => response.data as LoginResponse),
      tap(response => {
        this.apiService.setToken(response.token);
        this.isAuthenticated$.next(true);
        this.currentUser$.next(response.usuario);
      })
    );
  }

  /**
   * Realiza logout
   */
  logout(): void {
    this.apiService.clearToken();
    this.isAuthenticated$.next(false);
    this.currentUser$.next(null);
  }

  /**
   * Retorna observable do usuário atual
   */
  getCurrentUser(): Observable<any> {
    return this.currentUser$.asObservable();
  }

  /**
   * Retorna observable do status de autenticação
   */
  getIsAuthenticated(): Observable<boolean> {
    return this.isAuthenticated$.asObservable();
  }

  /**
   * Verifica se tem um papel específico
   */
  hasRole(role: string): boolean {
    const user = this.currentUser$.value;
    return user && user.roles && user.roles.includes(role);
  }

  /**
   * Verifica se está autenticado
   */
  private checkAuthStatus(): void {
    const token = this.apiService.getToken();
    if (token) {
      this.isAuthenticated$.next(true);
      // Opcionalmente, decodificar JWT para obter usuário
    }
  }
}
```

---

## 👥 EXEMPLO: Cliente Service (cliente.service.ts)

```typescript
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { ApiService, SuccessResponseDTO, PaginationParams } from './api.service';

export interface ClienteRequestDTO {
  nome: string;
  email: string;
  telefone: string;
  cpfCnpj: string;
  tipoPessoa: 'PF' | 'PJ';
  endereco: string;
  numero: string;
  complemento?: string;
  bairro: string;
  cep: string;
  municipio: string;
  uf: string;
  empresaId: number;
}

export interface ClienteResponseDTO extends ClienteRequestDTO {
  id: number;
  dataCriacao: string;
  ativo: boolean;
}

@Injectable({
  providedIn: 'root'
})
export class ClienteService {
  private readonly ENDPOINT = '/cliente';

  constructor(private apiService: ApiService) {}

  /**
   * Criar novo cliente
   */
  create(cliente: ClienteRequestDTO): Observable<ClienteResponseDTO> {
    return this.apiService.post<SuccessResponseDTO>(
      `${this.ENDPOINT}/create`,
      cliente
    ).pipe(
      map(response => response.data as ClienteResponseDTO)
    );
  }

  /**
   * Atualizar cliente
   */
  update(id: number, cliente: ClienteRequestDTO): Observable<ClienteResponseDTO> {
    return this.apiService.put<SuccessResponseDTO>(
      `${this.ENDPOINT}/update/${id}`,
      cliente
    ).pipe(
      map(response => response.data as ClienteResponseDTO)
    );
  }

  /**
   * Deletar cliente (hard delete)
   */
  delete(id: number): Observable<void> {
    return this.apiService.delete<SuccessResponseDTO>(
      `${this.ENDPOINT}/delete/${id}`
    ).pipe(
      map(() => undefined)
    );
  }

  /**
   * Soft delete (desativar)
   */
  softDelete(id: number): Observable<void> {
    return this.apiService.put<SuccessResponseDTO>(
      `${this.ENDPOINT}/update/softDelete/${id}`,
      {}
    ).pipe(
      map(() => undefined)
    );
  }

  /**
   * Ativar cliente
   */
  activate(id: number): Observable<void> {
    return this.apiService.put<SuccessResponseDTO>(
      `${this.ENDPOINT}/update/activate/${id}`,
      {}
    ).pipe(
      map(() => undefined)
    );
  }

  /**
   * Buscar cliente por ID
   */
  findById(id: number): Observable<ClienteResponseDTO> {
    return this.apiService.get<SuccessResponseDTO>(
      `${this.ENDPOINT}/findById/${id}`
    ).pipe(
      map(response => response.data as ClienteResponseDTO)
    );
  }

  /**
   * Listar todos os clientes
   */
  findAll(): Observable<ClienteResponseDTO[]> {
    return this.apiService.get<SuccessResponseDTO>(
      `${this.ENDPOINT}/findAll`
    ).pipe(
      map(response => response.data as ClienteResponseDTO[])
    );
  }

  /**
   * Listar clientes com paginação
   */
  findAllPaged(params: PaginationParams): Observable<any> {
    return this.apiService.get<SuccessResponseDTO>(
      `${this.ENDPOINT}/paginated`,
      params
    ).pipe(
      map(response => response.data)
    );
  }

  /**
   * Buscar cliente por CPF/CNPJ
   */
  findByCpfCnpj(cpfCnpj: string): Observable<ClienteResponseDTO> {
    return this.apiService.get<SuccessResponseDTO>(
      `${this.ENDPOINT}/findByCpfCnpj/${cpfCnpj}`
    ).pipe(
      map(response => response.data as ClienteResponseDTO)
    );
  }
}
```

---

## 📦 EXEMPLO: Produto Service (produto.service.ts)

```typescript
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { ApiService, SuccessResponseDTO } from './api.service';

export interface ProdutoRequestDTO {
  nome: string;
  descricao: string;
  codigoProduto: string;
  codigoInterno: string;
  ncm: string;
  precoUnitario: number;
  estoque: number;
  unidadeMedida: string;
  tipoProdutoId: number;
  empresaId: number;
}

export interface ProdutoResponseDTO extends ProdutoRequestDTO {
  id: number;
  dataCriacao: string;
  ativo: boolean;
}

@Injectable({
  providedIn: 'root'
})
export class ProdutoService {
  private readonly ENDPOINT = '/produto';

  constructor(private apiService: ApiService) {}

  create(produto: ProdutoRequestDTO): Observable<ProdutoResponseDTO> {
    return this.apiService.post<SuccessResponseDTO>(
      `${this.ENDPOINT}/create`,
      produto
    ).pipe(
      map(response => response.data as ProdutoResponseDTO)
    );
  }

  update(id: number, produto: ProdutoRequestDTO): Observable<void> {
    return this.apiService.put<SuccessResponseDTO>(
      `${this.ENDPOINT}/update/${id}`,
      produto
    ).pipe(
      map(() => undefined)
    );
  }

  delete(id: number): Observable<void> {
    return this.apiService.delete<SuccessResponseDTO>(
      `${this.ENDPOINT}/delete/${id}`
    ).pipe(
      map(() => undefined)
    );
  }

  softDelete(id: number): Observable<void> {
    return this.apiService.put<SuccessResponseDTO>(
      `${this.ENDPOINT}/update/softDelete/${id}`,
      {}
    ).pipe(
      map(() => undefined)
    );
  }

  activate(id: number): Observable<void> {
    return this.apiService.put<SuccessResponseDTO>(
      `${this.ENDPOINT}/update/activate/${id}`,
      {}
    ).pipe(
      map(() => undefined)
    );
  }

  findById(id: number): Observable<ProdutoResponseDTO> {
    return this.apiService.get<SuccessResponseDTO>(
      `${this.ENDPOINT}/findById/${id}`
    ).pipe(
      map(response => response.data as ProdutoResponseDTO)
    );
  }

  findAll(): Observable<ProdutoResponseDTO[]> {
    return this.apiService.get<SuccessResponseDTO>(
      `${this.ENDPOINT}/findAll`
    ).pipe(
      map(response => response.data as ProdutoResponseDTO[])
    );
  }

  findByTipoProdutoId(tipoProdutoId: number): Observable<ProdutoResponseDTO[]> {
    return this.apiService.get<SuccessResponseDTO>(
      `${this.ENDPOINT}/findByTipoProdutoId/${tipoProdutoId}`
    ).pipe(
      map(response => response.data as ProdutoResponseDTO[])
    );
  }

  findByNome(nome: string): Observable<ProdutoResponseDTO[]> {
    return this.apiService.get<SuccessResponseDTO>(
      `${this.ENDPOINT}/findByNome/${nome}`
    ).pipe(
      map(response => response.data as ProdutoResponseDTO[])
    );
  }

  findByAtivoInativo(ativo: boolean): Observable<ProdutoResponseDTO[]> {
    return this.apiService.get<SuccessResponseDTO>(
      `${this.ENDPOINT}/findByAtivoInativo/${ativo}`
    ).pipe(
      map(response => response.data as ProdutoResponseDTO[])
    );
  }
}
```

---

## 📄 EXEMPLO: Nota Service (nota.service.ts)

```typescript
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { ApiService, SuccessResponseDTO } from './api.service';

export interface ItemNotaRequestDTO {
  produtoId: number;
  quantidade: number;
  precoUnitario: number;
  aliquotaIcms: number;
  aliquotaPis: number;
  aliquotaCofins: number;
  desconto?: number;
}

export interface NotaRequestDTO {
  clienteId: number;
  empresaId: number;
  numero: string;
  serieNota: string;
}

export interface NotaResponseDTO extends NotaRequestDTO {
  id: number;
  status: 'RASCUNHO' | 'EMITIDA' | 'CANCELADA';
  chaveAcesso?: string;
  dataCriacao: string;
  items: ItemNotaRequestDTO[];
}

@Injectable({
  providedIn: 'root'
})
export class NotaService {
  private readonly ENDPOINT = '/notas';

  constructor(private apiService: ApiService) {}

  /**
   * Criar nova nota (rascunho)
   */
  create(nota: NotaRequestDTO): Observable<NotaResponseDTO> {
    return this.apiService.post<SuccessResponseDTO>(
      `${this.ENDPOINT}/create`,
      nota
    ).pipe(
      map(response => response.data as NotaResponseDTO)
    );
  }

  /**
   * Adicionar item à nota
   */
  addItem(notaId: number, item: ItemNotaRequestDTO): Observable<NotaResponseDTO> {
    return this.apiService.post<SuccessResponseDTO>(
      `${this.ENDPOINT}/${notaId}/add-item`,
      item
    ).pipe(
      map(response => response.data as NotaResponseDTO)
    );
  }

  /**
   * Atualizar item da nota
   */
  updateItem(notaId: number, itemId: number, item: ItemNotaRequestDTO): Observable<NotaResponseDTO> {
    return this.apiService.put<SuccessResponseDTO>(
      `${this.ENDPOINT}/${notaId}/update-item/${itemId}`,
      item
    ).pipe(
      map(response => response.data as NotaResponseDTO)
    );
  }

  /**
   * Remover item da nota
   */
  removeItem(notaId: number, itemId: number): Observable<NotaResponseDTO> {
    return this.apiService.delete<SuccessResponseDTO>(
      `${this.ENDPOINT}/${notaId}/remove-item/${itemId}`
    ).pipe(
      map(response => response.data as NotaResponseDTO)
    );
  }

  /**
   * Emitir nota (submeter ao SEFAZ)
   */
  emitir(notaId: number): Observable<NotaResponseDTO> {
    return this.apiService.post<SuccessResponseDTO>(
      `${this.ENDPOINT}/${notaId}/emitir`,
      {}
    ).pipe(
      map(response => response.data as NotaResponseDTO)
    );
  }

  /**
   * Atualizar dados da nota
   */
  update(notaId: number, nota: NotaRequestDTO): Observable<NotaResponseDTO> {
    return this.apiService.put<SuccessResponseDTO>(
      `${this.ENDPOINT}/update/${notaId}`,
      nota
    ).pipe(
      map(response => response.data as NotaResponseDTO)
    );
  }

  /**
   * Cancelar nota
   */
  cancel(notaId: number): Observable<void> {
    return this.apiService.put<SuccessResponseDTO>(
      `${this.ENDPOINT}/cancel/${notaId}`,
      {}
    ).pipe(
      map(() => undefined)
    );
  }

  /**
   * Buscar nota por ID
   */
  findById(notaId: number): Observable<NotaResponseDTO> {
    return this.apiService.get<SuccessResponseDTO>(
      `${this.ENDPOINT}/findById/${notaId}`
    ).pipe(
      map(response => response.data as NotaResponseDTO)
    );
  }

  /**
   * Listar todas as notas
   */
  findAll(): Observable<NotaResponseDTO[]> {
    return this.apiService.get<SuccessResponseDTO>(
      `${this.ENDPOINT}/findAll`
    ).pipe(
      map(response => response.data as NotaResponseDTO[])
    );
  }

  /**
   * Buscar notas com paginação
   */
  findAllPaged(page: number, size: number): Observable<any> {
    return this.apiService.get<SuccessResponseDTO>(
      `${this.ENDPOINT}/paginated`,
      { page, size }
    ).pipe(
      map(response => response.data)
    );
  }

  /**
   * Buscar nota por número e empresa
   */
  findByNumeroAndEmpresa(empresaId: number, numero: string): Observable<NotaResponseDTO> {
    return this.apiService.get<SuccessResponseDTO>(
      `${this.ENDPOINT}/find-by-numero-and-empresa`,
      { empresaId, numero }
    ).pipe(
      map(response => response.data as NotaResponseDTO)
    );
  }
}
```

---

## 🧾 EXEMPLO: NF-e Service (nfe.service.ts)

```typescript
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { ApiService, SuccessResponseDTO } from './api.service';

export interface NFeStatusDTO {
  online: boolean;
  versao: string;
  motivo: string;
  tempoMedio: string;
}

export interface NFeResponseDTO {
  chaveAcesso: string;
  codigoStatus: string;
  protocolo: string;
  mensagem: string;
}

export interface CancelamentoNFeDTORequest {
  justificativa: string;
}

@Injectable({
  providedIn: 'root'
})
export class NFeService {
  private readonly ENDPOINT = '/nfe';

  constructor(private apiService: ApiService) {}

  /**
   * Consultar status do serviço SEFAZ
   */
  consultarStatusServico(): Observable<NFeStatusDTO> {
    return this.apiService.get<SuccessResponseDTO>(
      `${this.ENDPOINT}/status-servico`
    ).pipe(
      map(response => response.data as NFeStatusDTO)
    );
  }

  /**
   * Emitir NF-e
   */
  emitir(notaId: number): Observable<NFeResponseDTO> {
    return this.apiService.post<SuccessResponseDTO>(
      `${this.ENDPOINT}/emitir/${notaId}`,
      {}
    ).pipe(
      map(response => response.data as NFeResponseDTO)
    );
  }

  /**
   * Cancelar NF-e
   */
  cancelar(notaId: number, justificativa: string): Observable<NFeResponseDTO> {
    const payload: CancelamentoNFeDTORequest = { justificativa };
    return this.apiService.post<SuccessResponseDTO>(
      `${this.ENDPOINT}/cancelar/${notaId}`,
      payload
    ).pipe(
      map(response => response.data as NFeResponseDTO)
    );
  }

  /**
   * Gerar DANFE (Download PDF)
   */
  downloadDanfe(notaId: number): void {
    this.apiService.downloadFile(
      `${this.ENDPOINT}/${notaId}/danfe`,
      `DANFE_${notaId}.pdf`
    );
  }

  /**
   * Visualizar DANFE (Inline PDF)
   */
  visualizarDanfe(notaId: number): Observable<Blob> {
    return this.apiService.get<any>(
      `${this.ENDPOINT}/${notaId}/danfe/visualizar`,
      {}
    ).pipe(
      map(response => new Blob([response], { type: 'application/pdf' }))
    );
  }
}
```

---

## 📌 EXEMPLO: NCM Service (ncm.service.ts)

```typescript
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { ApiService, SuccessResponseDTO } from './api.service';

export interface NcmData {
  codigo: string;
  descricao: string;
  aliquota: number;
}

@Injectable({
  providedIn: 'root'
})
export class NcmService {
  private readonly ENDPOINT = '/ncm';

  constructor(private apiService: ApiService) {}

  /**
   * Consultar dados de um NCM
   */
  consultar(ncm: string): Observable<NcmData> {
    return this.apiService.get<SuccessResponseDTO>(
      `${this.ENDPOINT}/consultar/${ncm}`
    ).pipe(
      map(response => response.data as NcmData)
    );
  }

  /**
   * Buscar alíquotas sugeridas para um NCM
   */
  buscarAliquotas(ncm: string): Observable<any> {
    return this.apiService.get<SuccessResponseDTO>(
      `${this.ENDPOINT}/aliquotas/${ncm}`
    ).pipe(
      map(response => response.data)
    );
  }
}
```

---

## 🔐 EXEMPLO: JWT Interceptor (jwt.interceptor.ts)

```typescript
import { Injectable } from '@angular/core';
import {
  HttpRequest,
  HttpHandler,
  HttpEvent,
  HttpInterceptor
} from '@angular/common/http';
import { Observable } from 'rxjs';
import { ApiService } from '../services/api.service';

@Injectable()
export class JwtInterceptor implements HttpInterceptor {
  constructor(private apiService: ApiService) {}

  intercept(request: HttpRequest<unknown>, next: HttpHandler): Observable<HttpEvent<unknown>> {
    // Obter token do serviço
    const token = this.apiService.getToken();

    // Se houver token, adicionar header de autenticação
    if (token) {
      request = request.clone({
        setHeaders: {
          Authorization: `Bearer ${token}`
        }
      });
    }

    return next.handle(request);
  }
}
```

---

## 🛡️ EXEMPLO: Auth Guard (auth.guard.ts)

```typescript
import { Injectable } from '@angular/core';
import {
  ActivatedRouteSnapshot,
  CanActivate,
  RouterStateSnapshot,
  UrlTree,
  Router
} from '@angular/router';
import { Observable } from 'rxjs';
import { AuthService } from '../services/auth.service';

@Injectable({
  providedIn: 'root'
})
export class AuthGuard implements CanActivate {
  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  canActivate(
    route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot
  ): Observable<boolean | UrlTree> | Promise<boolean | UrlTree> | boolean | UrlTree {
    if (this.authService.getToken()) {
      return true;
    }

    // Redirecionar para login
    this.router.navigate(['/login']);
    return false;
  }
}
```

---

## 📋 EXEMPLO: Uso em Componente

```typescript
import { Component, OnInit, OnDestroy } from '@angular/core';
import { ClienteService, ClienteResponseDTO } from '../services/cliente.service';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';

@Component({
  selector: 'app-cliente-list',
  template: `
    <div class="container">
      <h2>Clientes</h2>
      
      <button (click)="criarNovoCliente()">Novo Cliente</button>
      
      <div *ngIf="carregando" class="spinner">
        Carregando...
      </div>
      
      <table *ngIf="!carregando && clientes.length > 0">
        <thead>
          <tr>
            <th>ID</th>
            <th>Nome</th>
            <th>Email</th>
            <th>CPF/CNPJ</th>
            <th>Ações</th>
          </tr>
        </thead>
        <tbody>
          <tr *ngFor="let cliente of clientes">
            <td>{{ cliente.id }}</td>
            <td>{{ cliente.nome }}</td>
            <td>{{ cliente.email }}</td>
            <td>{{ cliente.cpfCnpj }}</td>
            <td>
              <button (click)="editar(cliente.id)">Editar</button>
              <button (click)="deletar(cliente.id)">Deletar</button>
            </td>
          </tr>
        </tbody>
      </table>
      
      <p *ngIf="!carregando && clientes.length === 0">
        Nenhum cliente encontrado
      </p>
    </div>
  `
})
export class ClienteListComponent implements OnInit, OnDestroy {
  clientes: ClienteResponseDTO[] = [];
  carregando = false;
  private destroy$ = new Subject<void>();

  constructor(private clienteService: ClienteService) {}

  ngOnInit(): void {
    this.carregarClientes();
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  carregarClientes(): void {
    this.carregando = true;
    this.clienteService.findAll()
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (clientes) => {
          this.clientes = clientes;
          this.carregando = false;
        },
        error: (err) => {
          console.error('Erro ao carregar clientes', err);
          this.carregando = false;
        }
      });
  }

  criarNovoCliente(): void {
    // Navegar para formulário de criação
  }

  editar(id: number): void {
    // Navegar para formulário de edição
  }

  deletar(id: number): void {
    if (confirm('Tem certeza que deseja deletar?')) {
      this.clienteService.delete(id)
        .pipe(takeUntil(this.destroy$))
        .subscribe({
          next: () => {
            this.carregarClientes();
          },
          error: (err) => {
            console.error('Erro ao deletar cliente', err);
          }
        });
    }
  }
}
```

---

## 🗂️ APP MODULE - Registrar Interceptor

```typescript
import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { HttpClientModule, HTTP_INTERCEPTORS } from '@angular/common/http';
import { JwtInterceptor } from './interceptors/jwt.interceptor';

@NgModule({
  declarations: [],
  imports: [
    BrowserModule,
    HttpClientModule
  ],
  providers: [
    {
      provide: HTTP_INTERCEPTORS,
      useClass: JwtInterceptor,
      multi: true
    }
  ],
  bootstrap: []
})
export class AppModule { }
```

---

## 📱 PADRÕES RECOMENDADOS

### Tratamento de Erros Centralizado
```typescript
// Criar um error handler service
@Injectable({
  providedIn: 'root'
})
export class ErrorHandlerService {
  private error$ = new Subject<string>();

  getError$(): Observable<string> {
    return this.error$.asObservable();
  }

  handleError(error: any): void {
    let mensagem = 'Erro desconhecido';

    if (error.error instanceof ErrorEvent) {
      // Erro do cliente
      mensagem = error.error.message;
    } else {
      // Erro do servidor
      mensagem = error.error?.mensagem || error.statusText;
    }

    this.error$.next(mensagem);
  }
}
```

### State Management (Redux/Ngrx Pattern)
```typescript
// Exemplo de estado para clientes
export interface ClienteState {
  clientes: ClienteResponseDTO[];
  carregando: boolean;
  erro: string | null;
}

// Actions
export const carregarClientes = createAction('[Cliente] Carregar Clientes');
export const carregarClientesSuccess = createAction(
  '[Cliente] Carregar Clientes Success',
  props<{ clientes: ClienteResponseDTO[] }>()
);
```

### Reactividade com RxJS
```typescript
// Combinar múltiplas observables
combineLatest([
  this.clienteService.findAll(),
  this.produtoService.findAll()
]).subscribe(([clientes, produtos]) => {
  // Usar ambos os dados
});

// Busca com debounce
this.searchTerm$
  .pipe(
    debounceTime(300),
    distinctUntilChanged(),
    switchMap(term => this.clienteService.findByNome(term))
  )
  .subscribe(clientes => {
    // Atualizar lista
  });
```

---

**Versão:** 1.0  
**Última atualização:** 2026-03-20

