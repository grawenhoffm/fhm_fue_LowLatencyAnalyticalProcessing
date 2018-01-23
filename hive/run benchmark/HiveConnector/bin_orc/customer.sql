create database if not exists orc;
use orc;

drop table if exists customer;

create table customer
stored as ORC
as select * from default.customer;
