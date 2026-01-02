-- V1_create_initial.tables.sql
-- ============================================
-- FLYWAY MIGRATION V1
-- Sistema de Notas Fiscais
-- Data: 02-01-2026
-- ============================================

-- ============================================
-- 1. TIPO_PRODUTO
-- ============================================
CREATE TABLE tipo_produto (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(100) NOT NULL UNIQUE,
    is_ativo BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE tipo_produto IS 'Categorias de produtos (Eletrônicos, Alimentos, etc)';
COMMENT ON COLUMN tipo_produto.nome IS 'Nome do tipo de produto (único)';

-- ============================================
-- 2. EMPRESA
-- ============================================
CREATE TABLE empresa (
    id BIGSERIAL PRIMARY KEY,
    razao_social VARCHAR(255) NOT NULL,
    nome_fantasia VARCHAR(255),
    cnpj VARCHAR(18) NOT NULL UNIQUE,
    inscricao_estadual VARCHAR(20),
    endereco_completo VARCHAR(500),
    cidade VARCHAR(100),
    estado VARCHAR(2) NOT NULL,
    cep VARCHAR(10),
    telefone VARCHAR(20),
    email VARCHAR(255) NOT NULL,
    logo_url VARCHAR(500),
    is_ativo BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE empresa IS 'Empresas emissoras de notas fiscais (multi-empresa)';
COMMENT ON COLUMN empresa.cnpj IS 'CNPJ único da empresa';
COMMENT ON COLUMN empresa.estado IS 'Sigla do estado (UF)';

-- ============================================
-- 3. CLIENTE
-- ============================================
CREATE TABLE cliente (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    tipo_pessoa VARCHAR(20) NOT NULL CHECK (tipo_pessoa IN ('FISICA', 'JURIDICA', 'CONSUMIDOR_FINAL')),
    cpf_cnpj VARCHAR(18) UNIQUE,
    inscricao_estadual VARCHAR(20),
    email VARCHAR(255),
    telefone VARCHAR(20),
    endereco_completo VARCHAR(500),
    cidade VARCHAR(100),
    estado VARCHAR(2),
    cep VARCHAR(10),
    bairro VARCHAR(100),
    is_ativo BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE cliente IS 'Clientes (PF, PJ ou Consumidor Final)';
COMMENT ON COLUMN cliente.tipo_pessoa IS 'Tipo: FISICA, JURIDICA ou CONSUMIDOR_FINAL';
COMMENT ON COLUMN cliente.cpf_cnpj IS 'CPF ou CNPJ (pode ser NULL para consumidor final)';

-- ============================================
-- 4. PRODUTO
-- ============================================
CREATE TABLE produto (
    id BIGSERIAL PRIMARY KEY,
    tipo_produto_id BIGINT NOT NULL,
    codigo VARCHAR(50) NOT NULL UNIQUE,
    nome VARCHAR(255) NOT NULL,
    descricao TEXT,
    unidade VARCHAR(10) NOT NULL CHECK (unidade IN ('UN', 'KG', 'LT', 'M', 'CX', 'PC')),
    preco_venda DECIMAL(15, 2) NOT NULL CHECK (preco_venda > 0),
    ncm VARCHAR(8),
    cfop_padrao VARCHAR(4),
    aliquota_icms_padrao DECIMAL(5, 2) CHECK (aliquota_icms_padrao >= 0 AND aliquota_icms_padrao <= 100),
    aliquota_pis_padrao DECIMAL(5, 2) CHECK (aliquota_pis_padrao >= 0 AND aliquota_pis_padrao <= 100),
    aliquota_cofins_padrao DECIMAL(5, 2) CHECK (aliquota_cofins_padrao >= 0 AND aliquota_cofins_padrao <= 100),
    is_ativo BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_produto_tipo FOREIGN KEY (tipo_produto_id) REFERENCES tipo_produto(id)
);

COMMENT ON TABLE produto IS 'Produtos comercializados';
COMMENT ON COLUMN produto.codigo IS 'Código único do produto';
COMMENT ON COLUMN produto.ncm IS 'Nomenclatura Comum do Mercosul (8 dígitos)';
COMMENT ON COLUMN produto.cfop_padrao IS 'Código Fiscal de Operações e Prestações';

-- Índices para performance
CREATE INDEX idx_produto_tipo ON produto(tipo_produto_id);
CREATE INDEX idx_produto_ativo ON produto(is_ativo);
CREATE INDEX idx_produto_nome ON produto(nome);

-- ============================================
-- 5. USUARIO
-- ============================================
CREATE TABLE usuario (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    senha VARCHAR(255) NOT NULL,
    is_ativo BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE usuario IS 'Usuários do sistema';
COMMENT ON COLUMN usuario.senha IS 'Senha criptografada (BCrypt)';

-- ============================================
-- 6. EMPRESA_USUARIO (Relacionamento N:N)
-- ============================================
CREATE TABLE empresa_usuario (
    id BIGSERIAL PRIMARY KEY,
    empresa_id BIGINT NOT NULL,
    usuario_id BIGINT NOT NULL,
    perfil VARCHAR(20) NOT NULL CHECK (perfil IN ('ADMIN', 'VENDEDOR', 'VISUALIZADOR')),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_empresa_usuario_empresa FOREIGN KEY (empresa_id) REFERENCES empresa(id),
    CONSTRAINT fk_empresa_usuario_usuario FOREIGN KEY (usuario_id) REFERENCES usuario(id),
    CONSTRAINT uk_empresa_usuario UNIQUE (empresa_id, usuario_id)
);

COMMENT ON TABLE empresa_usuario IS 'Relacionamento N:N entre Empresa e Usuario com perfil';
COMMENT ON COLUMN empresa_usuario.perfil IS 'Perfil do usuário na empresa: ADMIN, VENDEDOR ou VISUALIZADOR';

-- Índices
CREATE INDEX idx_empresa_usuario_empresa ON empresa_usuario(empresa_id);
CREATE INDEX idx_empresa_usuario_usuario ON empresa_usuario(usuario_id);

-- ============================================
-- 7. NOTA
-- ============================================
CREATE TABLE nota (
    id BIGSERIAL PRIMARY KEY,
    numero VARCHAR(20) NOT NULL,
    serie VARCHAR(10) DEFAULT '1',
    tipo VARCHAR(20) NOT NULL DEFAULT 'SAIDA' CHECK (tipo IN ('SAIDA', 'ENTRADA', 'NFE', 'NFCE', 'NFSE')),
    status VARCHAR(20) NOT NULL DEFAULT 'RASCUNHO' CHECK (status IN ('RASCUNHO', 'EMITIDA', 'CANCELADA')),
    empresa_id BIGINT NOT NULL,
    cliente_id BIGINT,
    data_emissao TIMESTAMP,
    data_cancelamento TIMESTAMP,
    valor_produtos DECIMAL(15, 2) NOT NULL DEFAULT 0,
    valor_impostos_total DECIMAL(15, 2) NOT NULL DEFAULT 0,
    valor_total DECIMAL(15, 2) NOT NULL DEFAULT 0,
    observacoes TEXT,
    chave_acesso VARCHAR(44),
    protocolo_autorizacao VARCHAR(50),
    created_by_user_id BIGINT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_nota_empresa FOREIGN KEY (empresa_id) REFERENCES empresa(id),
    CONSTRAINT fk_nota_cliente FOREIGN KEY (cliente_id) REFERENCES cliente(id),
    CONSTRAINT fk_nota_usuario FOREIGN KEY (created_by_user_id) REFERENCES usuario(id),
    CONSTRAINT uk_nota_numero_empresa UNIQUE (numero, empresa_id)
);

COMMENT ON TABLE nota IS 'Notas fiscais (documento principal)';
COMMENT ON COLUMN nota.numero IS 'Número sequencial único por empresa';
COMMENT ON COLUMN nota.tipo IS 'v1 usa apenas SAIDA';
COMMENT ON COLUMN nota.status IS 'RASCUNHO → EMITIDA → CANCELADA';
COMMENT ON COLUMN nota.cliente_id IS 'Pode ser NULL (consumidor final)';
COMMENT ON COLUMN nota.chave_acesso IS 'Para integração NFe (v2)';

-- Índices para performance
CREATE INDEX idx_nota_empresa ON nota(empresa_id);
CREATE INDEX idx_nota_cliente ON nota(cliente_id);
CREATE INDEX idx_nota_status ON nota(status);
CREATE INDEX idx_nota_tipo ON nota(tipo);
CREATE INDEX idx_nota_data_emissao ON nota(data_emissao);
CREATE INDEX idx_nota_numero ON nota(numero);
CREATE INDEX idx_nota_created_by ON nota(created_by_user_id);

-- ============================================
-- 8. ITEM_NOTA
-- ============================================
CREATE TABLE item_nota (
    id BIGSERIAL PRIMARY KEY,
    nota_id BIGINT NOT NULL,
    produto_id BIGINT NOT NULL,
    codigo_produto VARCHAR(50) NOT NULL,
    descricao_produto VARCHAR(255) NOT NULL,
    quantidade DECIMAL(15, 3) NOT NULL CHECK (quantidade > 0),
    unidade VARCHAR(10) NOT NULL,
    preco_unitario DECIMAL(15, 2) NOT NULL CHECK (preco_unitario > 0),
    subtotal DECIMAL(15, 2) NOT NULL,
    ncm VARCHAR(8),
    cfop VARCHAR(4),
    aliquota_icms DECIMAL(5, 2) NOT NULL DEFAULT 0,
    valor_icms DECIMAL(15, 2) NOT NULL DEFAULT 0,
    aliquota_pis DECIMAL(5, 2) NOT NULL DEFAULT 0,
    valor_pis DECIMAL(15, 2) NOT NULL DEFAULT 0,
    aliquota_cofins DECIMAL(5, 2) NOT NULL DEFAULT 0,
    valor_cofins DECIMAL(15, 2) NOT NULL DEFAULT 0,
    valor_total_item DECIMAL(15, 2) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_item_nota_nota FOREIGN KEY (nota_id) REFERENCES nota(id) ON DELETE CASCADE,
    CONSTRAINT fk_item_nota_produto FOREIGN KEY (produto_id) REFERENCES produto(id)
);

COMMENT ON TABLE item_nota IS 'Itens das notas fiscais';
COMMENT ON COLUMN item_nota.codigo_produto IS 'Snapshot do código do produto (histórico)';
COMMENT ON COLUMN item_nota.descricao_produto IS 'Snapshot da descrição (histórico)';
COMMENT ON COLUMN item_nota.subtotal IS 'quantidade × preco_unitario';
COMMENT ON COLUMN item_nota.valor_total_item IS 'subtotal + impostos';

-- Índices
CREATE INDEX idx_item_nota_nota ON item_nota(nota_id);
CREATE INDEX idx_item_nota_produto ON item_nota(produto_id);

-- ============================================
-- TRIGGERS PARA UPDATED_AT (Atualiza automaticamente)
-- ============================================

-- Função genérica para atualizar updated_at
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Aplicar trigger em todas as tabelas
CREATE TRIGGER update_tipo_produto_updated_at BEFORE UPDATE ON tipo_produto
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_empresa_updated_at BEFORE UPDATE ON empresa
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_cliente_updated_at BEFORE UPDATE ON cliente
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_produto_updated_at BEFORE UPDATE ON produto
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_usuario_updated_at BEFORE UPDATE ON usuario
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_empresa_usuario_updated_at BEFORE UPDATE ON empresa_usuario
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_nota_updated_at BEFORE UPDATE ON nota
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_item_nota_updated_at BEFORE UPDATE ON item_nota
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- ============================================
-- DADOS INICIAIS (SEED) - OPCIONAL
-- ============================================

-- Tipos de Produto padrão
INSERT INTO tipo_produto (nome, is_ativo) VALUES
('Eletrônicos', TRUE),
('Alimentos', TRUE),
('Vestuário', TRUE),
('Móveis', TRUE),
('Livros', TRUE);

-- ============================================
-- FIM DA MIGRATION V1
-- ============================================