HW_opt_2
```
hive --service llap --name llaptest --instances 1 --cache 121g --xmx 96g --size 223g --executors 24 --loglevel INFO --args " -XX:+UseG1GC -XX:+ResizeTLAB -XX:+UseNUMA  -XX:-ResizePLAB"
```

HW_opt_3
```
hive --service llap --name llaptest --instances 1 --cache 121g --xmx 96g --size 223g --executors 24 --loglevel INFO --args " -XX:+UseG1GC -XX:+ResizeTLAB -XX:+UseNUMA  -XX:-ResizePLAB"
```
