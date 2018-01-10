create database if not exists ${DB};
use ${DB};

drop table if exists item;

create table item
stored as ORC tblproperties ("compress" = "ZLIB")
as select * from ${SOURCE}.item;
