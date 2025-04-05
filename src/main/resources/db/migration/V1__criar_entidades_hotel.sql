CREATE TABLE quarto (
    id INTEGER AUTO_INCREMENT PRIMARY KEY,
    numero INTEGER NOT NULL,
    tipo_quarto VARCHAR(50) NOT NULL,
    valor DECIMAL(10,2) NOT NULL,
    disponibilidade BOOLEAN NOT NULL
);

CREATE TABLE hospede (
    id INTEGER AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    cpf VARCHAR(11) UNIQUE NOT NULL,
    data_nascimento DATE NOT NULL,
    telefone VARCHAR(50) NOT NULL
);

CREATE TABLE reservas (
    id INTEGER AUTO_INCREMENT PRIMARY KEY,
    checkin DATE NOT NULL,
    checkout DATE NOT NULL,
    hospede_id INTEGER NOT NULL,
    quarto_id INTEGER NOT NULL,
    situacao BOOLEAN NOT NULL,
    FOREIGN KEY (hospede_id) REFERENCES hospede(id),
    FOREIGN KEY (quarto_id) REFERENCES quarto(id)
);