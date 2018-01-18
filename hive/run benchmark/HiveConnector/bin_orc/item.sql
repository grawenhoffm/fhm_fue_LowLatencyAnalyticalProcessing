create database if not exists orc;
use orc;

drop table if exists item;

create table item
stored as ORC
as select * from default.item;
