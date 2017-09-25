#!/usr/bin/env bash
#! /usr/bin/expect

pass="mininet"

if [ "$#" -ne 1 ];then
 echo "Invalid number of arguments"
 exit 1
fi

expect "mn0@192.168.1.241's password: "
send "$pass"
rsync -rav ssh \
/mnt/Exec/code/java/of-Dwoxy/openflow-scripts/controller-component/l2_all_to_controller.py \
"mn0@192.168.1.2$1:/home/mn0/proxy/ext/"


rsync -rav ssh \
/mnt/Exec/code/java/of-Dwoxy/openflow-scripts/mininet-topology/*.py \
"mn0@192.168.1.2$1:/home/mn0/px/mininet-topology/"
expect "mn0@192.168.1.2$1's password: "
send "$pass"