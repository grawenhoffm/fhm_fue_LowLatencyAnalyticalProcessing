create database if not exists orc;
use orc;

drop table if exists warehouse;

create table warehouse
stored as ORC
as select * from default.warehouse;
