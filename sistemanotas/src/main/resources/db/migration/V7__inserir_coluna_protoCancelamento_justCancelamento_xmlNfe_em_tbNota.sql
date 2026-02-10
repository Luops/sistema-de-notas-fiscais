-- ============================================
-- FLYWAY MIGRATION V7
-- ============================================

ALTER TABLE tb_nota ADD COLUMN protocolo_cancelamento varchar(15);
ALTER TABLE tb_nota ADD COLUMN justificativa_cancelamento varchar(255);;
ALTER TABLE tb_nota ADD COLUMN xml_nfe TEXT;

