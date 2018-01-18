create database if not exists orc;
use orc;

drop table if exists customer_address;

create table customer_address
stored as ORC
as select * from default.customer_address;
