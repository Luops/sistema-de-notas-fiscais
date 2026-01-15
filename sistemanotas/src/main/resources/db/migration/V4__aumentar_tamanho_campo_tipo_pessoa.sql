-- ============================================
-- FLYWAY MIGRATION V4
-- Aumentar tamanho da coluna tipo_pessoa
-- ============================================

ALTER TABLE tb_cliente
ALTER COLUMN tipo_pessoa TYPE VARCHAR(40);