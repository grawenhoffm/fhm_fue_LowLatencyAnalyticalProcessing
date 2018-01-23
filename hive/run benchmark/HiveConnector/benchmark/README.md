# Generate TPCDS Data
## orc
load data into default db with Hiveconnector
run generate_orc.sh tool the used scale factor as param
for example the following for 10 gb partitioned data

```
./generate_orc.sh 10
```

## orc snappy compressed
## orc zlib compressed
