create database if not exists orc;
use orc;

drop table if exists promotion;

create table promotion
stored as ORC
as select * from default.promotion;
