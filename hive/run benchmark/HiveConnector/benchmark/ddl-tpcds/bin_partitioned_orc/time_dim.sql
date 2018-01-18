create database if not exists orc;
use orc;

drop table if exists time_dim;

create table time_dim
stored as ORC
as select * from default.time_dim;
