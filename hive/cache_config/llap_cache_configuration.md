Initial (Hortonworks recommendation)
```
hive --service llap --name llaptest --instances 2 --cache 98g --xmx 96g --size 200g --executors 24 --loglevel INFO --args " -XX:+UseG1GC -XX:+ResizeTLAB -XX:+UseNUMA  -XX:-ResizePLAB"
```

Setup1
```
hive --service llap --name llaptest --instances 1 --cache 60g --xmx 60g --size 120g --executors 24 --loglevel INFO --args " -XX:+UseG1GC -XX:+ResizeTLAB -XX:+UseNUMA  -XX:-ResizePLAB"
```

Setup2
```
hive --service llap --name llaptest --instances 1 --cache 42g --xmx 72g --size 120g --executors 24 --loglevel INFO --args " -XX:+UseG1GC -XX:+ResizeTLAB -XX:+UseNUMA  -XX:-ResizePLAB"
```

Setup3
```
hive --service llap --name llaptest --instances 1 --cache 24g --xmx 96g --size 120g --executors 24 --loglevel INFO --args " -XX:+UseG1GC -XX:+ResizeTLAB -XX:+UseNUMA  -XX:-ResizePLAB"
```

Setup4
```
hive --service llap --name llaptest --instances 1 --cache 18g --xmx 96g --size 120g --executors 24 --loglevel INFO --args " -XX:+UseG1GC -XX:+ResizeTLAB -XX:+UseNUMA  -XX:-ResizePLAB"
```

Setup5
```
hive --service llap --name llaptest --instances 1 --cache 25g --xmx 192g --size 223g --executors 48 --loglevel INFO --args " -XX:+UseG1GC -XX:+ResizeTLAB -XX:+UseNUMA  -XX:-ResizePLAB"
```

Setup6
```
hive --service llap --name llaptest --instances 1 --cache 15g --xmx 192g --size 213g --executors 48 --loglevel INFO --args " -XX:+UseG1GC -XX:+ResizeTLAB -XX:+UseNUMA  -XX:-ResizePLAB"
```

Setup7
```
hive --service llap --name llaptest --instances 1 --cache 25g --xmx 164g --size 195g --executors 41 --loglevel INFO --args " -XX:+UseG1GC -XX:+ResizeTLAB -XX:+UseNUMA  -XX:-ResizePLAB"
```



HW_opt
```
hive --service llap --name llaptest --instances 1 --cache 121g --xmx 96g --size 223g --executors 24 --loglevel INFO --args " -XX:+UseG1GC -XX:+ResizeTLAB -XX:+UseNUMA  -XX:-ResizePLAB"
```
