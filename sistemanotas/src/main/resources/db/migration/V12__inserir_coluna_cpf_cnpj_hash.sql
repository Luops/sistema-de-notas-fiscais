ALTER TABLE tb_cliente ADD COLUMN cpf_cnpj_hash VARCHAR(64);
CREATE UNIQUE INDEX idx_cpf_cnpj_hash ON tb_cliente(cpf_cnpj_hash);