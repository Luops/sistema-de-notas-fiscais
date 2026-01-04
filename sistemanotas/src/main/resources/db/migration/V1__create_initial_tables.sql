-- V1_create_initial.tables.sql
-- ============================================
-- FLYWAY MIGRATION V1
-- Sistema de Notas Fiscais
-- Data: 02-01-2026
-- ============================================

-- ============================================
-- 1. TIPO_PRODUTO
-- ============================================
CREATE TABLE tb_tipo_produto (
    id_tipo_produto BIGSERIAL PRIMARY KEY,
    nome VARCHAR(100) NOT NULL UNIQUE,
    is_ativo BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE tb_tipo_produto IS 'Categorias de produtos (Eletrônicos, Alimentos, etc)';
COMMENT ON COLUMN tb_tipo_produto.nome IS 'Nome do tipo de produto (único)';

-- ============================================
-- 2. EMPRESA
-- ============================================
CREATE TABLE tb_empresa (
    id_empresa BIGSERIAL PRIMARY KEY,
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

COMMENT ON TABLE tb_empresa IS 'Empresas emissoras de notas fiscais (multi-empresa)';
COMMENT ON COLUMN tb_empresa.cnpj IS 'CNPJ único da empresa';
COMMENT ON COLUMN tb_empresa.estado IS 'Sigla do estado (UF)';

-- ============================================
-- 3. CLIENTE
-- ============================================
CREATE TABLE tb_cliente (
    id_cliente BIGSERIAL PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    tipo_pessoa VARCHAR(20) NOT NULL CHECK (tipo_pessoa IN ('FISICA', 'JURIDICA', 'CONSUMIDOR_FINAL')),
    cpf_cnpj VARCHAR(18) UNIQUE,
    inscricao_estadual VARCHAR(20),
    email VARCHAR(255) UNIQUE,
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

COMMENT ON TABLE tb_cliente IS 'Clientes (PF, PJ ou Consumidor Final)';
COMMENT ON COLUMN tb_cliente.tipo_pessoa IS 'Tipo: FISICA, JURIDICA ou CONSUMIDOR_FINAL';
COMMENT ON COLUMN tb_cliente.cpf_cnpj IS 'CPF ou CNPJ (pode ser NULL para consumidor final)';

-- ============================================
-- 4. PRODUTO
-- ============================================
CREATE TABLE tb_produto (
    id_produto BIGSERIAL PRIMARY KEY,
    tipo_produto BIGINT NOT NULL,
    codigo VARCHAR(50) NOT NULL UNIQUE,
    nome VARCHAR(255) NOT NULL,
    descricao TEXT,
    unidade VARCHAR(10) NOT NULL CHECK (unidade IN ('UN', 'KG', 'L', 'M', 'CX', 'PC')),
    preco_venda DECIMAL(15, 2) NOT NULL CHECK (preco_venda > 0),
    ncm VARCHAR(8),
    cfop_padrao VARCHAR(4),
    aliquota_icms_padrao DECIMAL(5, 2) CHECK (aliquota_icms_padrao >= 0 AND aliquota_icms_padrao <= 100),
    aliquota_pis_padrao DECIMAL(5, 2) CHECK (aliquota_pis_padrao >= 0 AND aliquota_pis_padrao <= 100),
    aliquota_cofins_padrao DECIMAL(5, 2) CHECK (aliquota_cofins_padrao >= 0 AND aliquota_cofins_padrao <= 100),
    is_ativo BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_produto_tipo FOREIGN KEY (tipo_produto) REFERENCES tb_tipo_produto(id_tipo_produto)
);

COMMENT ON TABLE tb_produto IS 'Produtos comercializados';
COMMENT ON COLUMN tb_produto.codigo IS 'Código único do produto';
COMMENT ON COLUMN tb_produto.ncm IS 'Nomenclatura Comum do Mercosul (8 dígitos)';
COMMENT ON COLUMN tb_produto.cfop_padrao IS 'Código Fiscal de Operações e Prestações';

-- Índices para performance
CREATE INDEX idx_produto_tipo ON tb_produto(tipo_produto);
CREATE INDEX idx_produto_ativo ON tb_produto(is_ativo);
CREATE INDEX idx_produto_nome ON tb_produto(nome);

-- ============================================
-- 5. USUARIO
-- ============================================
CREATE TABLE tb_usuario (
    id_usuario BIGSERIAL PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    senha VARCHAR(255) NOT NULL,
    is_ativo BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE tb_usuario IS 'Usuários do sistema';
COMMENT ON COLUMN tb_usuario.senha IS 'Senha criptografada (BCrypt)';

-- ============================================
-- 6. EMPRESA_USUARIO (Relacionamento N:N)
-- ============================================
CREATE TABLE tb_empresa_usuario (
    id_empresa_usuario BIGSERIAL PRIMARY KEY,
    id_empresa BIGINT NOT NULL,
    id_usuario BIGINT NOT NULL,
    perfil VARCHAR(20) NOT NULL CHECK (perfil IN ('ADMIN', 'VENDEDOR', 'VISUALIZADOR')),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_empresa_usuario_empresa FOREIGN KEY (id_empresa) REFERENCES tb_empresa(id_empresa),
    CONSTRAINT fk_empresa_usuario_usuario FOREIGN KEY (id_usuario) REFERENCES tb_usuario(id_usuario),
    CONSTRAINT uk_empresa_usuario UNIQUE (id_empresa, id_usuario)
);

COMMENT ON TABLE tb_empresa_usuario IS 'Relacionamento N:N entre Empresa e Usuario com perfil';
COMMENT ON COLUMN tb_empresa_usuario.perfil IS 'Perfil do usuário na empresa: ADMIN, VENDEDOR ou VISUALIZADOR';

-- Índices
CREATE INDEX idx_empresa_usuario_empresa ON tb_empresa_usuario(id_empresa);
CREATE INDEX idx_empresa_usuario_usuario ON tb_empresa_usuario(id_usuario);

-- ============================================
-- 7. NOTA
-- ============================================
CREATE TABLE tb_nota (
    id_nota BIGSERIAL PRIMARY KEY,
    numero VARCHAR(20) NOT NULL,
    serie VARCHAR(10) DEFAULT '1',
    tipo VARCHAR(20) NOT NULL DEFAULT 'SAIDA' CHECK (tipo IN ('SAIDA', 'ENTRADA', 'NFE', 'NFCE', 'NFSE')),
    status VARCHAR(20) NOT NULL DEFAULT 'RASCUNHO' CHECK (status IN ('RASCUNHO', 'EMITIDA', 'CANCELADA')),
    id_empresa BIGINT NOT NULL,
    id_cliente BIGINT,
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
    CONSTRAINT fk_nota_empresa FOREIGN KEY (id_empresa) REFERENCES tb_empresa(id_empresa),
    CONSTRAINT fk_nota_cliente FOREIGN KEY (id_cliente) REFERENCES tb_cliente(id_cliente),
    CONSTRAINT fk_nota_usuario FOREIGN KEY (created_by_user_id) REFERENCES tb_usuario(id_usuario),
    CONSTRAINT uk_nota_numero_empresa UNIQUE (numero, id_empresa)
);

COMMENT ON TABLE tb_nota IS 'Notas fiscais (documento principal)';
COMMENT ON COLUMN tb_nota.numero IS 'Número sequencial único por empresa';
COMMENT ON COLUMN tb_nota.tipo IS 'v1 usa apenas SAIDA';
COMMENT ON COLUMN tb_nota.status IS 'RASCUNHO → EMITIDA → CANCELADA';
COMMENT ON COLUMN tb_nota.id_cliente IS 'Pode ser NULL (consumidor final)';
COMMENT ON COLUMN tb_nota.chave_acesso IS 'Para integração NFe (v2)';

-- Índices para performance
CREATE INDEX idx_nota_empresa ON tb_nota(id_empresa);
CREATE INDEX idx_nota_cliente ON tb_nota(id_cliente);
CREATE INDEX idx_nota_status ON tb_nota(status);
CREATE INDEX idx_nota_tipo ON tb_nota(tipo);
CREATE INDEX idx_nota_data_emissao ON tb_nota(data_emissao);
CREATE INDEX idx_nota_numero ON tb_nota(numero);
CREATE INDEX idx_nota_created_by ON tb_nota(created_by_user_id);

-- ============================================
-- 8. ITEM_NOTA
-- ============================================
CREATE TABLE tb_item_nota (
    id_item_nota BIGSERIAL PRIMARY KEY,
    id_nota BIGINT NOT NULL,
    id_produto BIGINT NOT NULL,
    codigo_produto VARCHAR(50) NOT NULL,
    descricao_produto VARCHAR(255) NOT NULL,
    quantidade DECIMAL(15, 3) NOT NULL,
    unidade VARCHAR(10) NOT NULL,
    preco_unitario DECIMAL(15, 2) NOT NULL CHECK (preco_unitario > 0),
    sub_total DECIMAL(15, 2) NOT NULL,
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
    CONSTRAINT fk_item_nota_nota FOREIGN KEY (id_nota) REFERENCES tb_nota(id_nota) ON DELETE CASCADE,
    CONSTRAINT fk_item_nota_produto FOREIGN KEY (id_produto) REFERENCES tb_produto(id_produto)
);

COMMENT ON TABLE tb_item_nota IS 'Itens das notas fiscais';
COMMENT ON COLUMN tb_item_nota.codigo_produto IS 'Snapshot do código do produto (histórico)';
COMMENT ON COLUMN tb_item_nota.descricao_produto IS 'Snapshot da descrição (histórico)';
COMMENT ON COLUMN tb_item_nota.sub_total IS 'quantidade × preco_unitario';
COMMENT ON COLUMN tb_item_nota.valor_total_item IS 'subtotal + impostos';

-- Índices
CREATE INDEX idx_item_nota_nota ON tb_item_nota(id_nota);
CREATE INDEX idx_item_nota_produto ON tb_item_nota(id_produto);

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
CREATE TRIGGER update_tb_tipo_produto_updated_at BEFORE UPDATE ON tb_tipo_produto
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_tb_empresa_updated_at BEFORE UPDATE ON tb_empresa
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_tb_cliente_updated_at BEFORE UPDATE ON tb_cliente
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_tb_produto_updated_at BEFORE UPDATE ON tb_produto
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_tb_usuario_updated_at BEFORE UPDATE ON tb_usuario
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_tb_empresa_usuario_updated_at BEFORE UPDATE ON tb_empresa_usuario
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_tb_nota_updated_at BEFORE UPDATE ON tb_nota
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_tb_item_nota_updated_at BEFORE UPDATE ON tb_item_nota
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- ============================================
-- DADOS INICIAIS (SEED) - OPCIONAL
-- ============================================

-- Tipos de Produto padrão
INSERT INTO tb_tipo_produto (nome, is_ativo) VALUES
('Eletrônicos', TRUE),
('Alimentos', TRUE),
('Vestuário', TRUE),
('Móveis', TRUE),
('Livros', TRUE);

-- ============================================
-- FIM DA MIGRATION V1
-- ============================================