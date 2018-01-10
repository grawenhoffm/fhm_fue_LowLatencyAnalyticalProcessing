create database if not exists ${DB};
use ${DB};

drop table if exists date_dim;

create table date_dim
stored as ORC tblproperties ("compress" = "SNAPPY")
as select * from ${SOURCE}.date_dim;
