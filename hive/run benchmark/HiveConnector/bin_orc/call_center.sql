create database if not exists orc;
use orc;

drop table if exists call_center;

create table call_center
stored as ORC
as select * from default.call_center;
