#!/bin/bash
#author: Sebastian
#Bashscript zu Steuerung des Elasticserach Clusters auf Basis von Docker-Compose
case $1 in
 start)
  sshpass -p "WEloveES!" ssh -t elastic@10.42.13.203 "echo WEloveES! | sudo -S docker-compose -f es611/docker-compose611.yml start"
  sshpass -p "WEloveES!" ssh -t elastic@10.42.13.204 "echo WEloveES! | sudo -S docker-compose -f es611/docker-compose611.yml start"
  sshpass -p "WEloveES!" ssh -t elastic@10.42.13.205 "echo WEloveES! | sudo -S docker-compose -f es611/docker-compose611.yml start"
  sshpass -p "WEloveES!" ssh -t elastic@10.42.13.206 "echo WEloveES! | sudo -S docker-compose -f es611/docker-compose611.yml start"

  echo "6.1.1 Cluster should be started. Kibana @ :5605"
 ;;

 stop)
  sshpass -p "WEloveES!" ssh -t elastic@10.42.13.204 "echo WEloveES! | sudo -S docker-compose -f es611/docker-compose611.yml stop"
  sshpass -p "WEloveES!" ssh -t elastic@10.42.13.205 "echo WEloveES! | sudo -S docker-compose -f es611/docker-compose611.yml stop"
  sshpass -p "WEloveES!" ssh -t elastic@10.42.13.206 "echo WEloveES! | sudo -S docker-compose -f es611/docker-compose611.yml stop"
  sshpass -p "WEloveES!" ssh -t elastic@10.42.13.203 "echo WEloveES! | sudo -S docker-compose -f es611/docker-compose611.yml stop"
  echo "6.1.1 Cluster should be stopped"
 ;;

 up)
  sshpass -p "WEloveES!" ssh -t elastic@10.42.13.204 "echo WEloveES! | sudo -S docker-compose -f es611/docker-compose611.yml up -d"
  sshpass -p "WEloveES!" ssh -t elastic@10.42.13.205 "echo WEloveES! | sudo -S docker-compose -f es611/docker-compose611.yml up -d"
  sshpass -p "WEloveES!" ssh -t elastic@10.42.13.206 "echo WEloveES! | sudo -S docker-compose -f es611/docker-compose611.yml up -d"
  sshpass -p "WEloveES!" ssh -t elastic@10.42.13.203 "echo WEloveES! | sudo -S docker-compose -f es611/docker-compose611.yml up -d"
  echo "6.1.1 Cluster should be up"
 ;;

 down)
  sshpass -p "WEloveES!" ssh -t elastic@10.42.13.204 "echo WEloveES! | sudo -S docker-compose -f es611/docker-compose611.yml down"
  sshpass -p "WEloveES!" ssh -t elastic@10.42.13.205 "echo WEloveES! | sudo -S docker-compose -f es611/docker-compose611.yml down"
  sshpass -p "WEloveES!" ssh -t elastic@10.42.13.206 "echo WEloveES! | sudo -S docker-compose -f es611/docker-compose611.yml down"
  sshpass -p "WEloveES!" ssh -t elastic@10.42.13.203 "echo WEloveES! | sudo -S docker-compose -f es611/docker-compose611.yml down"
  echo "6.1.1 Cluster should be down"
 ;;

 after_restart)
  sshpass -p "WEloveES!" ssh -t elastic@10.42.13.204 "echo WEloveES! | sudo -S mount -t cifs  //wi-cluster02.fh-muenster.de/ES_Backup /mnt/es_snap"
  sshpass -p "WEloveES!" ssh -t elastic@10.42.13.205 "echo WEloveES! | sudo -S mount -t cifs  //wi-cluster02.fh-muenster.de/ES_Backup /mnt/es_snap"
  sshpass -p "WEloveES!" ssh -t elastic@10.42.13.206 "echo WEloveES! | sudo -S mount -t cifs  //wi-cluster02.fh-muenster.de/ES_Backup /mnt/es_snap"
  sshpass -p "WEloveES!" ssh -t elastic@10.42.13.203 "echo WEloveES! | sudo -S mount -t cifs  //wi-cluster02.fh-muenster.de/ES_Backup /mnt/es_snap"
  sshpass -p "WEloveES!" ssh -t elastic@10.42.13.204 "echo WEloveES! | sudo -S swapoff -a"
  sshpass -p "WEloveES!" ssh -t elastic@10.42.13.205 "echo WEloveES! | sudo -S swapoff -a"
  sshpass -p "WEloveES!" ssh -t elastic@10.42.13.206 "echo WEloveES! | sudo -S swapoff -a"
  sshpass -p "WEloveES!" ssh -t elastic@10.42.13.203 "echo WEloveES! | sudo -S swapoff -a"
  sshpass -p "WEloveES!" ssh -t elastic@10.42.13.204 "echo WEloveES! | sudo -S sysctl -w vm.max_map_count=262144"
  sshpass -p "WEloveES!" ssh -t elastic@10.42.13.205 "echo WEloveES! | sudo -S sysctl -w vm.max_map_count=262144"
  sshpass -p "WEloveES!" ssh -t elastic@10.42.13.206 "echo WEloveES! | sudo -S sysctl -w vm.max_map_count=262144"
  sshpass -p "WEloveES!" ssh -t elastic@10.42.13.203 "echo WEloveES! | sudo -S sysctl -w vm.max_map_count=262144"
 ;;

 to_ssd)
  for i in {3..6}
  do
   echo -e "\n\e[1;31mConnected to 10.42.13.20$i\e[0m"
   echo -e "\e[1;31mmoving to /var/lib/docker/volumes/\e[0m"
   sshpass -p "WEloveES!" ssh -t -q elastic@10.42.13.20$i<< zzz
   echo -e "\e[1;31mmoving es611_esdata1...\e[0m"
   echo WEloveES! | sudo -S mv /data/elasticsearch/temp_save/es611_esdata1 /var/lib/docker/volumes/ &> /dev/null
   echo -e "\e[1;31mmoving es611_esdata2...\e[0m"
   echo WEloveES! | sudo -S mv /data/elasticsearch/temp_save/es611_esdata2 /var/lib/docker/volumes/ &> /dev/null
   echo -e "\e[1;31mmoving es611_esdata3...\e[0m"
   echo WEloveES! | sudo -S mv /data/elasticsearch/temp_save/es611_esdata3 /var/lib/docker/volumes/ &> /dev/null
   echo -e "\e[1;31mmoving es611_esdata4...\e[0m"
   echo WEloveES! | sudo -S mv /data/elasticsearch/temp_save/es611_esdata4 /var/lib/docker/volumes/ &> /dev/null
   echo WEloveES! | sudo -S ls /var/lib/docker/volumes/
zzz
  done
  echo -e "\n\e[1;31mMoved 611 Volumes from SAN to SSD\e[0m"
  exit 0
 ;;

 to_san)
  bash /home/elastic/sm/611.sh stop
  for i in {3..6}
  do
   echo -e "\n\e[1;31mConnected to 10.42.13.20$i\e[0m"
   echo -e "\e[1;31mmoving to /data/elasticsearch/temp_save/\e[0m"
   sshpass -p "WEloveES!" ssh -t -q elastic@10.42.13.20$i<< yyy
   echo -e "\e[1;31mmoving es611_esdata1...\e[0m"
   echo WEloveES! | sudo -S mv /var/lib/docker/volumes/es611_esdata1 /data/elasticsearch/temp_save/ &> /dev/null
   echo -e "\e[1;31mmoving es611_esdata2...\e[0m"
   echo WEloveES! | sudo -S mv /var/lib/docker/volumes/es611_esdata2 /data/elasticsearch/temp_save/ &> /dev/null
   echo -e "\e[1;31mmoving es611_esdata3...\e[0m"
   echo WEloveES! | sudo -S mv /var/lib/docker/volumes/es611_esdata3 /data/elasticsearch/temp_save/ &> /dev/null
   echo -e "\e[1;31mmoving es611_esdata4...\e[0m"
   echo WEloveES! | sudo -S mv /var/lib/docker/volumes/es611_esdata4 /data/elasticsearch/temp_save/ &> /dev/null
   echo WEloveES! | sudo -S ls /data/elasticsearch/temp_save/
yyy
  done
  echo -e "\n\e[1;31mMoved 611 Volumes from SSD to SAN\e[0m"
 ;;

 *)
  echo don\'t know
 ;;
esac

exit 0

