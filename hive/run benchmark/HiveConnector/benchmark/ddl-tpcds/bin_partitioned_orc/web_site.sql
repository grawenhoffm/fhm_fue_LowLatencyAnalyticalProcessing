create database if not exists orc;
use orc;

drop table if exists web_site;

create table web_site
stored as ORC
as select * from default.web_site;
