-- ============================================
-- FLYWAY MIGRATION V8
-- ============================================

-- Tabela de CFOPs
CREATE TABLE tb_cfop (
                         id BIGSERIAL PRIMARY KEY,
                         codigo VARCHAR(4) NOT NULL UNIQUE,
                         descricao VARCHAR(500) NOT NULL,
                         aplicacao TEXT,
                         tipo VARCHAR(20) NOT NULL, -- ENTRADA, SAIDA
                         natureza VARCHAR(50) NOT NULL, -- VENDA, COMPRA, TRANSFERENCIA, DEVOLUCAO, etc
                         operacao VARCHAR(50) NOT NULL, -- DENTRO_ESTADO, FORA_ESTADO, IMPORTACAO, EXPORTACAO
                         ativo BOOLEAN DEFAULT TRUE,
                         created_at TIMESTAMP DEFAULT NOW(),
                         updated_at TIMESTAMP DEFAULT NOW()
);

-- Índices
CREATE INDEX idx_cfop_codigo ON tb_cfop(codigo);
CREATE INDEX idx_cfop_tipo ON tb_cfop(tipo);
CREATE INDEX idx_cfop_operacao ON tb_cfop(operacao);

-- Inserir CFOPs mais comuns
INSERT INTO tb_cfop (codigo, descricao, tipo, natureza, operacao, aplicacao) VALUES
-- VENDAS DENTRO DO ESTADO
('5101', 'Venda de produção do estabelecimento', 'SAIDA', 'VENDA', 'DENTRO_ESTADO', 'Venda de produto industrializado ou produzido pelo próprio estabelecimento em operação com contribuinte do ICMS, dentro do estado.'),
('5102', 'Venda de mercadoria adquirida ou recebida de terceiros', 'SAIDA', 'VENDA', 'DENTRO_ESTADO', 'Venda de produto adquirido ou recebido de terceiros para revenda, em operação com contribuinte do ICMS, dentro do estado.'),
('5103', 'Venda de produção do estabelecimento efetuada fora do estabelecimento', 'SAIDA', 'VENDA', 'DENTRO_ESTADO', 'Venda efetuada fora do estabelecimento, inclusive por meio de veículo.'),
('5104', 'Venda de mercadoria adquirida ou recebida de terceiros, efetuada fora do estabelecimento', 'SAIDA', 'VENDA', 'DENTRO_ESTADO', 'Venda de mercadoria adquirida ou recebida de terceiros efetuada fora do estabelecimento.'),
('5105', 'Venda de energia elétrica', 'SAIDA', 'VENDA', 'DENTRO_ESTADO', 'Venda de energia elétrica para o consumidor final.'),

-- VENDAS FORA DO ESTADO
('6101', 'Venda de produção do estabelecimento', 'SAIDA', 'VENDA', 'FORA_ESTADO', 'Venda de produto industrializado ou produzido pelo próprio estabelecimento em operação com contribuinte do ICMS, fora do estado.'),
('6102', 'Venda de mercadoria adquirida ou recebida de terceiros', 'SAIDA', 'VENDA', 'FORA_ESTADO', 'Venda de produto adquirido ou recebido de terceiros para revenda, em operação com contribuinte do ICMS, fora do estado.'),
('6107', 'Venda de produção do estabelecimento destinada à Zona Franca de Manaus', 'SAIDA', 'VENDA', 'FORA_ESTADO', 'Venda de produção do estabelecimento destinada à Zona Franca de Manaus ou Áreas de Livre Comércio.'),
('6108', 'Venda de mercadoria adquirida ou recebida de terceiros destinada à Zona Franca', 'SAIDA', 'VENDA', 'FORA_ESTADO', 'Venda de mercadoria adquirida ou recebida de terceiros destinada à Zona Franca de Manaus ou Áreas de Livre Comércio.'),

-- DEVOLUÇÕES
('5201', 'Devolução de compra para industrialização ou produção rural', 'SAIDA', 'DEVOLUCAO', 'DENTRO_ESTADO', 'Devolução de mercadoria adquirida para ser utilizada em processo de industrialização ou produção rural.'),
('5202', 'Devolução de compra para comercialização', 'SAIDA', 'DEVOLUCAO', 'DENTRO_ESTADO', 'Devolução de mercadoria adquirida para ser comercializada.'),
('6201', 'Devolução de compra para industrialização ou produção rural', 'SAIDA', 'DEVOLUCAO', 'FORA_ESTADO', 'Devolução de mercadoria adquirida para ser utilizada em processo de industrialização ou produção rural.'),
('6202', 'Devolução de compra para comercialização', 'SAIDA', 'DEVOLUCAO', 'FORA_ESTADO', 'Devolução de mercadoria adquirida para ser comercializada.'),

-- TRANSFERÊNCIAS
('5151', 'Transferência de produção do estabelecimento', 'SAIDA', 'TRANSFERENCIA', 'DENTRO_ESTADO', 'Transferência de mercadoria de produção própria para outro estabelecimento da mesma empresa.'),
('5152', 'Transferência de mercadoria adquirida ou recebida de terceiros', 'SAIDA', 'TRANSFERENCIA', 'DENTRO_ESTADO', 'Transferência de mercadoria adquirida ou recebida de terceiros para outro estabelecimento da mesma empresa.'),
('6151', 'Transferência de produção do estabelecimento', 'SAIDA', 'TRANSFERENCIA', 'FORA_ESTADO', 'Transferência de mercadoria de produção própria para outro estabelecimento da mesma empresa.'),
('6152', 'Transferência de mercadoria adquirida ou recebida de terceiros', 'SAIDA', 'TRANSFERENCIA', 'FORA_ESTADO', 'Transferência de mercadoria adquirida ou recebida de terceiros para outro estabelecimento da mesma empresa.'),

-- COMPRAS DENTRO DO ESTADO
('1101', 'Compra para industrialização ou produção rural', 'ENTRADA', 'COMPRA', 'DENTRO_ESTADO', 'Compra de mercadoria para ser utilizada em processo de industrialização ou produção rural.'),
('1102', 'Compra para comercialização', 'ENTRADA', 'COMPRA', 'DENTRO_ESTADO', 'Compra de mercadoria para ser comercializada.'),
('1113', 'Compra para comercialização, de mercadoria recebida anteriormente em consignação mercantil', 'ENTRADA', 'COMPRA', 'DENTRO_ESTADO', 'Compra efetiva de mercadoria recebida anteriormente a título de consignação mercantil.'),

-- COMPRAS FORA DO ESTADO
('2101', 'Compra para industrialização ou produção rural', 'ENTRADA', 'COMPRA', 'FORA_ESTADO', 'Compra de mercadoria para ser utilizada em processo de industrialização ou produção rural.'),
('2102', 'Compra para comercialização', 'ENTRADA', 'COMPRA', 'FORA_ESTADO', 'Compra de mercadoria para ser comercializada.'),

-- DEVOLUÇÃO DE VENDAS
('1201', 'Devolução de venda de produção do estabelecimento', 'ENTRADA', 'DEVOLUCAO', 'DENTRO_ESTADO', 'Devolução de mercadoria vendida, cuja saída tenha sido classificada como venda de produção.'),
('1202', 'Devolução de venda de mercadoria adquirida ou recebida de terceiros', 'ENTRADA', 'DEVOLUCAO', 'DENTRO_ESTADO', 'Devolução de mercadoria vendida, cuja saída tenha sido classificada como venda de mercadoria adquirida ou recebida de terceiros.'),
('2201', 'Devolução de venda de produção do estabelecimento', 'ENTRADA', 'DEVOLUCAO', 'FORA_ESTADO', 'Devolução de mercadoria vendida, cuja saída tenha sido classificada como venda de produção.'),
('2202', 'Devolução de venda de mercadoria adquirida ou recebida de terceiros', 'ENTRADA', 'DEVOLUCAO', 'FORA_ESTADO', 'Devolução de mercadoria vendida, cuja saída tenha sido classificada como venda de mercadoria adquirida ou recebida de terceiros.'),

-- IMPORTAÇÃO/EXPORTAÇÃO
('3101', 'Compra para industrialização - Importação Direta', 'ENTRADA', 'IMPORTACAO', 'IMPORTACAO', 'Importação de mercadoria para ser utilizada em processo de industrialização.'),
('3102', 'Compra para comercialização - Importação Direta', 'ENTRADA', 'IMPORTACAO', 'IMPORTACAO', 'Importação de mercadoria para ser comercializada.'),
('7101', 'Venda de produção do estabelecimento - Exportação Direta', 'SAIDA', 'EXPORTACAO', 'EXPORTACAO', 'Exportação de mercadoria de produção própria.'),
('7102', 'Venda de mercadoria adquirida ou recebida de terceiros - Exportação Direta', 'SAIDA', 'EXPORTACAO', 'EXPORTACAO', 'Exportação de mercadoria adquirida ou recebida de terceiros.'),

-- OUTRAS OPERAÇÕES
('5949', 'Outra saída de mercadoria ou prestação de serviço não especificado', 'SAIDA', 'OUTRAS', 'DENTRO_ESTADO', 'Outras saídas de mercadorias ou prestações de serviços que não se enquadrem nos códigos específicos.'),
('6949', 'Outra saída de mercadoria ou prestação de serviço não especificado', 'SAIDA', 'OUTRAS', 'FORA_ESTADO', 'Outras saídas de mercadorias ou prestações de serviços que não se enquadrem nos códigos específicos.');
