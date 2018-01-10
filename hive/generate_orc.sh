#!/bin/bash

SCALE=50
FORMAT="ORC"
BUCKETS=1 #13
RETURN_BUCKETS=1 #13
SCRIPS="bin_partitioned"

if [$1 = "orc"]; then
 SCRIPTS="bin_partitioned"
fi
if [$1 = "zlib"]; then
 SCRIPTS="bin_partitioned_zlib"
fi

# Tables in the TPC-DS schema.
DIMS="date_dim time_dim item customer customer_demographics household_demographics customer_address store promotion warehouse ship_mode reason income_band call_center web_page catalog_page web_site"
FACTS="store_sales store_returns web_sales web_returns catalog_sales catalog_returns inventory"



i=1
total=24
LOAD_FILE="load_orc_${SCALE}.mk"
MAX_REDUCERS=2500 # maximum number of useful reducers for any scale
REDUCERS=$((test ${SCALE} -gt ${MAX_REDUCERS} && echo ${MAX_REDUCERS}) || echo ${SCALE})
SOURCE_DB="default"
TARGET_DB="orc_${SCALE}"

echo -e "all: ${DIMS} ${FACTS}" >> $LOAD_FILE

# Populate the smaller tables.
for t in ${DIMS}
do
	COMMAND="hive -i settings/load-partitioned.sql -f bin/$SCRIPTS/${t}.sql \
	    -d DB=${TARGET_DB} -d SOURCE=${SOURCE_DB}  \
            -d SCALE=${SCALE} \
	    -d REDUCERS=${REDUCERS} \
	    -d FILE=${FORMAT}"
	echo -e "${t}:\n\t@$COMMAND $SILENCE && echo 'Optimizing table $t ($i/$total).'" >> $LOAD_FILE
	i=`expr $i + 1`

done

for t in ${FACTS}
do
	COMMAND="hive -i settings/load-partitioned.sql -f bin/$SCRIPTS/${t}.sql \
	    -d DB=${TARGET_DB} \
            -d SCALE=${SCALE} \
	    -d SOURCE=${SOURCE_DB} -d BUCKETS=${BUCKETS} \
	    -d RETURN_BUCKETS=${RETURN_BUCKETS} -d REDUCERS=${REDUCERS} -d FILE=${FORMAT}"
	echo -e "${t}:\n\t@$COMMAND $SILENCE && echo 'Optimizing table $t ($i/$total).'" >> $LOAD_FILE
	i=`expr $i + 1`
 echo -e "${SOURCE}"
done

make -j 2 -f $LOAD_FILE

echo "Data loaded into database ${TARGET_DB}."
