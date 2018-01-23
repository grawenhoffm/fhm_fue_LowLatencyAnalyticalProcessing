create database if not exists orc;
use orc;

drop table if exists date_dim;

create table date_dim
stored as ORC
as select * from default.date_dim;
