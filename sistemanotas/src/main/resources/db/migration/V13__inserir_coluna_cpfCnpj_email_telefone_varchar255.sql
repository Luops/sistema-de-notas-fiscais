-- V13__increase_encrypted_columns_size.sql

ALTER TABLE tb_cliente
ALTER COLUMN cpf_cnpj TYPE VARCHAR(255);

ALTER TABLE tb_cliente
ALTER COLUMN email TYPE VARCHAR(255);

ALTER TABLE tb_cliente
ALTER COLUMN telefone TYPE VARCHAR(255);