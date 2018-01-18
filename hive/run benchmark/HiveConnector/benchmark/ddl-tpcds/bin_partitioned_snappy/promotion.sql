create database if not exists ${DB};
use ${DB};

drop table if exists promotion;

create table promotion
stored as ORC tblproperties ("compress" = "SNAPPY")
as select * from ${SOURCE}.promotion;
