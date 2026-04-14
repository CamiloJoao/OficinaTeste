CREATE TYPE tipo_usuario_enum AS ENUM ('atendente', 'adm');
CREATE TYPE tipo_servico_enum AS ENUM ('montagem', 'manutencao');
CREATE TYPE status_enum AS ENUM ('em_andamento', 'encerrado');
CREATE TYPE tipo_peca_enum AS ENUM ('placa_mae', 'cpu', 'memoria_ram', 'gpu', 'hd', 'ssd', 'fonte' );

CREATE TABLE cliente (
    id_cliente SERIAL PRIMARY KEY,
    nome_cliente VARCHAR(100) NOT NULL,
    telefone_cliente VARCHAR(20) UNIQUE,
    email_cliente VARCHAR(100) NOT NULL UNIQUE
);

CREATE TABLE usuario (
    id_usuario SERIAL PRIMARY KEY,
    nome_usuario VARCHAR(45) NOT NULL,
    email_usuario VARCHAR(45) NOT NULL UNIQUE,
    senha VARCHAR(255) NOT NULL,
    tipo_usuario tipo_usuario_enum NOT NULL
);

CREATE TABLE peca (
    id_peca SERIAL PRIMARY KEY,
    nome_peca VARCHAR(100) NOT NULL,
    tipo_peca tipo_peca_enum NOT NULL,
    preco DECIMAL(10,2) NOT NULL,
    compatibilidade JSON
);

CREATE TABLE servico (
    id_servico SERIAL PRIMARY KEY,
    id_cliente INT NOT NULL REFERENCES cliente(id_cliente),
    tipo_servico tipo_servico_enum NOT NULL,
    descricao_problema TEXT, 
    pecas_cadastradas TEXT,
    orcamento_inicial DECIMAL(10,2),
    orcamento_final DECIMAL(10,2), 
    status status_enum NOT NULL,
    data_prevista_conclusao DATE
);

CREATE TABLE servico_peca (
    id SERIAL PRIMARY KEY,
    id_servico INT REFERENCES servico(id_servico),
    nome_peca VARCHAR(100),
    valor DECIMAL(10,2)
    id_referencia INT,
    tipo VARCHAR(50)
);


