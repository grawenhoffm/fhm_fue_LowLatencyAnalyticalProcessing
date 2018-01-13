create database if not exists ${DB};
use ${DB};

drop table if exists customer_demographics;

create table customer_demographics
stored as ORC tblproperties ("compress" = "ZLIB")
as select * from ${SOURCE}.customer_demographics;
