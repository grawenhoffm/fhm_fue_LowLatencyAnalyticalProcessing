create database if not exists ${DB};
use ${DB};

drop table if exists warehouse;

create table warehouse
stored as ORC tblproperties ("compress" = "ZLIB")
as select * from ${SOURCE}.warehouse;
