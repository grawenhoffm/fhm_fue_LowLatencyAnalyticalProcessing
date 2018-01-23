create database if not exists ${DB};
use ${DB};

drop table if exists time_dim;

create table time_dim
stored as ORC tblproperties ("compress" = "ZLIB")
as select * from ${SOURCE}.time_dim;
