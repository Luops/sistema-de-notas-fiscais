-- ============================================
-- FLYWAY MIGRATION V9
-- ============================================
-- Adicionar campos de certificado na tabela empresa
ALTER TABLE tb_empresa
    ADD COLUMN certificado_digital BYTEA,  -- Armazena o arquivo .pfx
ADD COLUMN certificado_senha_criptografada VARCHAR(255),  -- Senha criptografada
ADD COLUMN certificado_tipo VARCHAR(10) DEFAULT 'A1',  -- A1 ou A3
ADD COLUMN certificado_validade DATE,  -- Data de vencimento
ADD COLUMN certificado_cnpj VARCHAR(14),  -- CNPJ do certificado (para validação)
ADD COLUMN certificado_ativo BOOLEAN DEFAULT FALSE,  -- Se está configurado
ADD COLUMN certificado_upload_date TIMESTAMP;  -- Quando foi feito upload

-- Índice para buscar empresas com certificado
CREATE INDEX idx_empresa_certificado_ativo ON tb_empresa(certificado_ativo);

COMMENT ON COLUMN tb_empresa.certificado_digital IS 'Arquivo do certificado digital em formato PKCS12 (.pfx)';
COMMENT ON COLUMN tb_empresa.certificado_senha_criptografada IS 'Senha do certificado criptografada com BCrypt';