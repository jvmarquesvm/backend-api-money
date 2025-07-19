create table pessoa (
	codigo bigint(20) primary key auto_increment,
	nome varchar(80) not null,
	ativo boolean not null,
	logradouro varchar(80),
	numero varchar(6),
	complemento varchar(80),
	bairro varchar(80),
	cep varchar(15),
	cidade varchar(80),
	estado varchar(2)
) engine=InnoDB default charset=utf8;

insert into pessoa (nome, logradouro, numero, complemento, bairro, cep, cidade, estado, ativo) values ('João Silva', 'Rua do Abacaxi', '10', null, 'Brasil', '38.400-12', 'Uberlândia', 'MG', true);
insert into pessoa (nome, logradouro, numero, complemento, bairro, cep, cidade, estado, ativo) values ('Maria Rita', 'Rua do Sabiá', '110', 'Apto 101', 'Colina', '11.400-12', 'Ribeirão Preto', 'SP', true);
insert into pessoa (nome, logradouro, numero, complemento, bairro, cep, cidade, estado, ativo) values ('Pedro Santos', 'Rua da Bateria', '23', null, 'Morumbi', '54.212-12', 'Goiânia', 'GO', true);
   