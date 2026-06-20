-- V16__add_usuario_address_phone_columns.sql

ALTER TABLE tb_usuario
    ADD COLUMN telefone VARCHAR(20),
    ADD COLUMN cidade VARCHAR(100),
    ADD COLUMN endereco VARCHAR(255),
    ADD COLUMN cep VARCHAR(20),
    ADD COLUMN numero_endereco VARCHAR(20);

COMMENT ON COLUMN tb_usuario.telefone IS 'Telefone do usuário';
COMMENT ON COLUMN tb_usuario.cidade IS 'Cidade do usuário';
COMMENT ON COLUMN tb_usuario.endereco IS 'Endereço completo do usuário';
COMMENT ON COLUMN tb_usuario.numero_endereco IS 'Número do endereço';