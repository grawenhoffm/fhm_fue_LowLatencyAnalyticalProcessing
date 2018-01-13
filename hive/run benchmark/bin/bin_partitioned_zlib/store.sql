create database if not exists ${DB};
use ${DB};

drop table if exists store;

create table store
stored as ORC tblproperties ("compress" = "ZLIB")
as select * from ${SOURCE}.store;
