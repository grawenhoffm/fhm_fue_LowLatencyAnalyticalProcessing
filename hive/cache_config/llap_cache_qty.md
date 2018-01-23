Setup6.2
```
hive --service llap --name llaptest --instances 2 --cache 15g --xmx 192g --size 213g --executors 48 --loglevel INFO --args " -XX:+UseG1GC -XX:+ResizeTLAB -XX:+UseNUMA  -XX:-ResizePLAB"
```

Setup6.3
```
hive --service llap --name llaptest --instances 3 --cache 15g --xmx 192g --size 213g --executors 48 --loglevel INFO --args " -XX:+UseG1GC -XX:+ResizeTLAB -XX:+UseNUMA  -XX:-ResizePLAB"
```

Setup6.4
```
hive --service llap --name llaptest --instances 4 --cache 15g --xmx 192g --size 213g --executors 48 --loglevel INFO --args " -XX:+UseG1GC -XX:+ResizeTLAB -XX:+UseNUMA  -XX:-ResizePLAB"
```

Setup4.2
```
hive --service llap --name llaptest --instances 2 --cache 18g --xmx 96g --size 120g --executors 24 --loglevel INFO --args " -XX:+UseG1GC -XX:+ResizeTLAB -XX:+UseNUMA  -XX:-ResizePLAB"
```

Setup4.3
```
hive --service llap --name llaptest --instances 3 --cache 18g --xmx 96g --size 120g --executors 24 --loglevel INFO --args " -XX:+UseG1GC -XX:+ResizeTLAB -XX:+UseNUMA  -XX:-ResizePLAB"
```

Setup4.4
```
hive --service llap --name llaptest --instances 4 --cache 18g --xmx 96g --size 120g --executors 24 --loglevel INFO --args " -XX:+UseG1GC -XX:+ResizeTLAB -XX:+UseNUMA  -XX:-ResizePLAB"
```
