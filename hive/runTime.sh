hiveserver2 > hiveO 2> hiveE &
slider start llaptest
sleep 1m
hive -e "SET hive.optimize.ppd=true;"
hive -e "SET hive.optimize.ppd.storage=true"
