create database if not exists orc;
use orc;

drop table if exists catalog_page;

create table catalog_page
stored as ORC
as select * from default.catalog_page;
