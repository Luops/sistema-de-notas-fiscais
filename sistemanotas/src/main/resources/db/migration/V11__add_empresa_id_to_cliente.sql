-- V11__add_empresa_id_to_cliente.sql

-- 1. Adicionar coluna empresa_id (permitindo NULL temporariamente)
ALTER TABLE tb_cliente
    ADD COLUMN empresa_id BIGINT;

-- 2. Associar clientes existentes à primeira empresa disponível
-- (Ajuste o ID da empresa conforme necessário)
UPDATE tb_cliente
SET empresa_id = (SELECT MIN(id) FROM tb_empresa LIMIT 1)
WHERE empresa_id IS NULL;

-- ⚠️ Se não houver nenhuma empresa, você precisa criar uma primeiro!
-- Opção alternativa: Deletar clientes órfãos
-- DELETE FROM tb_cliente WHERE empresa_id IS NULL;

-- 3. Agora sim, tornar NOT NULL (depois de preencher os valores)
ALTER TABLE tb_cliente
    ALTER COLUMN empresa_id SET NOT NULL;

-- 4. Adicionar constraint de foreign key
ALTER TABLE tb_cliente
    ADD CONSTRAINT fk_cliente_empresa
        FOREIGN KEY (empresa_id) REFERENCES tb_empresa(id);

-- 5. Criar índice para performance
CREATE INDEX idx_cliente_empresa_id ON tb_cliente(empresa_id);