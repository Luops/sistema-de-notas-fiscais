-- V15_inserir_empresa_id_no_tb_tipoProduto.sql

-- Inserir empresa teste no banco de dados

INSERT INTO
    tb_empresa (
        razao_social,
        nome_fantasia,
        cnpj,
        estado,
        email,
        is_ativo,
        created_at,
        updated_at
    )
VALUES
    (
        'Ellyon Tecnologia Limitada',
        'Ellyon',
        '03987123000112',
        'RS',
        'ellyon@mail.com',
        true,
        NOW(),
        NOW()
    );

-- 1. Adicionar coluna empresa_id (permitindo NULL temporariamente)
ALTER TABLE
    tb_tipo_produto
ADD
    COLUMN empresa_id BIGINT;

-- 2. Associar produtos existentes à primeira empresa disponível
-- (Ajuste o ID da empresa conforme necessário)
UPDATE
    tb_tipo_produto
SET
    empresa_id = (
        SELECT
            MIN(id)
        FROM
            tb_empresa
        LIMIT
            1
    )
WHERE
    empresa_id IS NULL;

ALTER TABLE
    tb_tipo_produto
ALTER COLUMN
    empresa_id
SET
    NOT NULL;

-- 4. Adicionar constraint de foreign key
ALTER TABLE
    tb_tipo_produto
ADD
    CONSTRAINT fk_tipo_produto_empresa FOREIGN KEY (empresa_id) REFERENCES tb_empresa(id);

-- 5. Criar índice para performance
CREATE INDEX idx_tipo_produto_empresa_id ON tb_tipo_produto(empresa_id);