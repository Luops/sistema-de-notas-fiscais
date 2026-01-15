-- ============================================
-- FLYWAY MIGRATION V3
-- Alteração do tipo_pessoa em tb_cliente
-- ============================================

-- 1. REMOVER QUALQUER CONSTRAINT ANTIGA
ALTER TABLE tb_cliente
DROP CONSTRAINT IF EXISTS tb_cliente_tipo_pessoa_check;

-- 2. NORMALIZAR DADOS EXISTENTES
UPDATE tb_cliente
SET tipo_pessoa = 'CONSUMIDOR FINAL'
WHERE tipo_pessoa IN ('CONSUMIDOR_FINAL', 'CONSUMIDOR FINAL ');

-- 3. GARANTIR QUE NÃO EXISTE LIXO
UPDATE tb_cliente
SET tipo_pessoa = TRIM(tipo_pessoa);

-- 4. RECRIAR CONSTRAINT CORRETA
ALTER TABLE tb_cliente
ADD CONSTRAINT tb_cliente_tipo_pessoa_check
CHECK (
    tipo_pessoa IN (
        'FISICA',
        'JURIDICA',
        'CONSUMIDOR FINAL',
        'CONSUMIDOR NAO IDENTIFICADO'
    )
);

-- 5. COMENTÁRIO
COMMENT ON COLUMN tb_cliente.tipo_pessoa IS
'Tipo: FISICA, JURIDICA, CONSUMIDOR FINAL ou CONSUMIDOR NAO IDENTIFICADO';