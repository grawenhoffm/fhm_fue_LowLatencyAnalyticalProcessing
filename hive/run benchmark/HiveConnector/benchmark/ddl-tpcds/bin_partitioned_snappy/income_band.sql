create database if not exists ${DB};
use ${DB};

drop table if exists income_band;

create table income_band
stored as ORC tblproperties ("compress" = "SNAPPY")
as select * from ${SOURCE}.income_band;
