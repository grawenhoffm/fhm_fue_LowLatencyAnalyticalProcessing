create database if not exists orc;
use orc;

drop table if exists ship_mode;

create table ship_mode
stored as ORC
as select * from default.ship_mode;
