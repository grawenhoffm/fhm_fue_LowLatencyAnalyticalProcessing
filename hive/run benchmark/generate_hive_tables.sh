#!/bin/bash
hive -f queries_create/tpcds_source.sql
hive -f queries_create/tpcds.sql
hive -f queries_create/copyData.sql
hive -f queries_create/createBigTable.sql
