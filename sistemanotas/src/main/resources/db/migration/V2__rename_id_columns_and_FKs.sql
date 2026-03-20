-- V2__rename_id_columns_and_FKs.sql
-- ============================================
-- FLYWAY MIGRATION V2
-- Padronização de IDs e Foreign Keys
-- ============================================

-- =====================================================
-- 1. REMOVER FOREIGN KEYS
-- =====================================================

ALTER TABLE tb_produto DROP CONSTRAINT fk_produto_tipo;

ALTER TABLE tb_empresa_usuario DROP CONSTRAINT fk_empresa_usuario_empresa;
ALTER TABLE tb_empresa_usuario DROP CONSTRAINT fk_empresa_usuario_usuario;

ALTER TABLE tb_nota DROP CONSTRAINT fk_nota_empresa;
ALTER TABLE tb_nota DROP CONSTRAINT fk_nota_cliente;
ALTER TABLE tb_nota DROP CONSTRAINT fk_nota_usuario;

ALTER TABLE tb_item_nota DROP CONSTRAINT fk_item_nota_nota;
ALTER TABLE tb_item_nota DROP CONSTRAINT fk_item_nota_produto;

-- =====================================================
-- 2. RENOMEAR PRIMARY KEYS PARA "id"
-- =====================================================

ALTER TABLE tb_tipo_produto RENAME COLUMN id_tipo_produto TO id;
ALTER TABLE tb_empresa RENAME COLUMN id_empresa TO id;
ALTER TABLE tb_cliente RENAME COLUMN id_cliente TO id;
ALTER TABLE tb_produto RENAME COLUMN id_produto TO id;
ALTER TABLE tb_usuario RENAME COLUMN id_usuario TO id;
ALTER TABLE tb_nota RENAME COLUMN id_nota TO id;
ALTER TABLE tb_item_nota RENAME COLUMN id_item_nota TO id;
ALTER TABLE tb_empresa_usuario RENAME COLUMN id_empresa_usuario TO id;

-- =====================================================
-- 3. RENOMEAR COLUNAS FK
-- =====================================================

ALTER TABLE tb_empresa_usuario RENAME COLUMN id_empresa TO empresa_id;
ALTER TABLE tb_empresa_usuario RENAME COLUMN id_usuario TO usuario_id;

ALTER TABLE tb_nota RENAME COLUMN id_empresa TO empresa_id;
ALTER TABLE tb_nota RENAME COLUMN id_cliente TO cliente_id;

ALTER TABLE tb_item_nota RENAME COLUMN id_nota TO nota_id;
ALTER TABLE tb_item_nota RENAME COLUMN id_produto TO produto_id;

-- =====================================================
-- 4. RECRIAR FOREIGN KEYS
-- =====================================================

-- PRODUTO -> TIPO_PRODUTO
ALTER TABLE tb_produto
    ADD CONSTRAINT fk_produto_tipo_produto
    FOREIGN KEY (tipo_produto)
    REFERENCES tb_tipo_produto(id);

-- EMPRESA_USUARIO
ALTER TABLE tb_empresa_usuario
    ADD CONSTRAINT fk_empresa_usuario_empresa
    FOREIGN KEY (empresa_id)
    REFERENCES tb_empresa(id);

ALTER TABLE tb_empresa_usuario
    ADD CONSTRAINT fk_empresa_usuario_usuario
    FOREIGN KEY (usuario_id)
    REFERENCES tb_usuario(id);

-- NOTA
ALTER TABLE tb_nota
    ADD CONSTRAINT fk_nota_empresa
    FOREIGN KEY (empresa_id)
    REFERENCES tb_empresa(id);

ALTER TABLE tb_nota
    ADD CONSTRAINT fk_nota_cliente
    FOREIGN KEY (cliente_id)
    REFERENCES tb_cliente(id);

ALTER TABLE tb_nota
    ADD CONSTRAINT fk_nota_usuario
    FOREIGN KEY (created_by_user_id)
    REFERENCES tb_usuario(id);

-- ITEM_NOTA
ALTER TABLE tb_item_nota
    ADD CONSTRAINT fk_item_nota_nota
    FOREIGN KEY (nota_id)
    REFERENCES tb_nota(id)
    ON DELETE CASCADE;

ALTER TABLE tb_item_nota
    ADD CONSTRAINT fk_item_nota_produto
    FOREIGN KEY (produto_id)
    REFERENCES tb_produto(id);

-- ============================================
-- FIM DA MIGRATION V2
-- ============================================
