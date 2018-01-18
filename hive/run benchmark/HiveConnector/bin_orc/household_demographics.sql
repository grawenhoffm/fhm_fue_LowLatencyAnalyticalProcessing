create database if not exists orc;
use orc;

drop table if exists household_demographics;

create table household_demographics
stored as ORC
as select * from default.household_demographics;
