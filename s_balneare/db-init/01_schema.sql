DROP database if exists stabilimenti;
CREATE database stabilimenti;
use stabilimenti;

create table user (
    id int primary key auto_increment,
    nome varchar(64) NOT NULL
);

create table beach (
    id int primary key auto_increment,
    nome varchar(64) NOT NULL
);

create table admin (
    id int primary key auto_increment,
    nome varchar(64) NOT NULL
)


