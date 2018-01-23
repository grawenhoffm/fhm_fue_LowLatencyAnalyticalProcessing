create database if not exists orc;
use orc;

drop table if exists web_page;

create table web_page
stored as ORC
as select * from default.web_page;
