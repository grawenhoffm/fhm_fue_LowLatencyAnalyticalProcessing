create database if not exists ${DB};
use ${DB};

drop table if exists warehouse;

create table warehouse
stored as ORC tblproperties ("compress" = "SNAPPY")
as select * from ${SOURCE}.warehouse;
