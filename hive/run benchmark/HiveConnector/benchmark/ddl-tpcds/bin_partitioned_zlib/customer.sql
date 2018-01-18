create database if not exists ${DB};
use ${DB};

drop table if exists customer;

create table customer
stored as ORC tblproperties ("compress" = "ZLIB")
as select * from ${SOURCE}.customer;
