-- ============================================
-- FLYWAY MIGRATION V6
-- Inserir coluna frete na tabela tbNota
-- ============================================

ALTER TABLE tb_nota ADD COLUMN frete DECIMAL(10, 2) DEFAULT 0.00;
