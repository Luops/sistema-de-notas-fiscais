-- V15_inserir_empresa_id_no_tb_tipoProduto.sql

-- 1. Adicionar coluna empresa_id (permitindo NULL temporariamente)
ALTER TABLE tb_tipo_produto
    ADD COLUMN empresa_id BIGINT;

-- 2. Associar produtos existentes à primeira empresa disponível
-- (Ajuste o ID da empresa conforme necessário)
UPDATE tb_tipo_produto
SET empresa_id = (SELECT MIN(id) FROM tb_empresa LIMIT 1)
WHERE empresa_id IS NULL;

ALTER TABLE tb_tipo_produto
    ALTER COLUMN empresa_id SET NOT NULL;

-- 4. Adicionar constraint de foreign key
ALTER TABLE tb_tipo_produto
    ADD CONSTRAINT fk_tipo_produto_empresa
        FOREIGN KEY (empresa_id) REFERENCES tb_empresa(id);

-- 5. Criar índice para performance
CREATE INDEX idx_tipo_produto_empresa_id ON tb_tipo_produto(empresa_id);