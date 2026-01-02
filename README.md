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
