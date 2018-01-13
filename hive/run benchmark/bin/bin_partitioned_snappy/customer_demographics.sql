create database if not exists ${DB};
use ${DB};

drop table if exists customer_demographics;

create table customer_demographics
stored as ORC tblproperties ("compress" = "SNAPPY")
as select * from ${SOURCE}.customer_demographics;
