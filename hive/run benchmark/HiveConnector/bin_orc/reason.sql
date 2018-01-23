create database if not exists orc;
use orc;

drop table if exists reason;

create table reason
stored as ORC
as select * from orc.reason;
