create database if not exists orc;
use orc;

drop table if exists income_band;

create table income_band
stored as ORC
as select * from default.income_band;
