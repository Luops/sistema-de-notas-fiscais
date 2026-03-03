-- ============================================
-- FLYWAY MIGRATION V10
-- ============================================
-- Corrigir constraint tb_nota_status_check para incluir novos status
-- Adicionar colunas faltantes na tabela tb_nota

-- Remover o constraint antigo
ALTER TABLE tb_nota DROP CONSTRAINT tb_nota_status_check;

-- Adicionar o novo constraint com todos os status suportados
ALTER TABLE tb_nota ADD CONSTRAINT tb_nota_status_check
    CHECK (status IN ('RASCUNHO', 'EMITIDA', 'CANCELADA', 'AUTORIZADA', 'REJEITADA', 'ERRO'));

-- Atualizar o comentário da coluna status
COMMENT ON COLUMN tb_nota.status IS 'Status da nota: RASCUNHO, EMITIDA, CANCELADA, AUTORIZADA, REJEITADA, ERRO';