create database if not exists orc;
use orc;

drop table if exists store;

create table store
stored as ORC
as select * from default.store;
