create database if not exists orc;
use orc;

drop table if exists customer_demographics;

create table customer_demographics
stored as ORC
as select * from default.customer_demographics;
