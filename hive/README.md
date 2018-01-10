# Hive LLAP Benchmark

1. Load Data

    load data into hive tables
```
./generate_hive_tables.sh
```
2. Format Data

    load data into hive with the following command (orc or orc_zlib formatted)
```
./generate_orc.sh orc
```

3. Run Benchmark 

    use benchmark tool(IP, Port, DB-Name, Start-Point)
```
java -jar HiveConnector-1.0.jar wi-cluster01 10000  orc_50 100 &
```
Results are saved in ./testFile.txt
