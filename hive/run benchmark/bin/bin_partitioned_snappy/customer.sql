create database if not exists ${DB};
use ${DB};

drop table if exists customer;

create table customer
stored as ORC tblproperties ("compress" = "SNAPPY")
as select * from ${SOURCE}.customer;
