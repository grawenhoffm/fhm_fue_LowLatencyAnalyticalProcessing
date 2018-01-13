create database if not exists ${DB};
use ${DB};

drop table if exists ship_mode;

create table ship_mode
stored as ORC tblproperties ("compress" = "ZLIB")
as select * from ${SOURCE}.ship_mode;
