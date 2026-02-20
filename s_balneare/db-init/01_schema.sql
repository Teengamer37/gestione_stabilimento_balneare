DROP TABLE if exists prova;

CREATE TABLE if not exists prova(
                                    id INT PRIMARY KEY AUTO_INCREMENT,
                                    nome VARCHAR(100) NOT NULL
    );

INSERT INTO prova (nome)
VALUES
    ('Mario'),
    ('Luigi');
