create database if not exists ${DB};
use ${DB};

drop table if exists catalog_page;

create table catalog_page
stored as ORC tblproperties ("compress" = "SNAPPY")
as select * from ${SOURCE}.catalog_page;
