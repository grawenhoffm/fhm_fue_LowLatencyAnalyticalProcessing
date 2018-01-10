create database if not exists ${DB};
use ${DB};

drop table if exists reason;

create table reason
stored as ORC tblproperties ("compress" = "ZLIB")
as select * from ${SOURCE}.reason;
