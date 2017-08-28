#!/usr/bin/env bash


#rm result.txt

#./highloadcup_tester -addr http://127.0.0.1:8080 -hlcupdocs /Users/volyx/Projects/hlcupdocs/data/TRAIN -test -phase 2 > result.txt

for p in {1..3}; do
   ./highloadcup_tester -addr http://127.0.0.1:8080 -hlcupdocs /Users/volyx/Projects/hlcupdocs/data/TRAIN -test -phase  $p
 done

# ./tester -addr http://127.0.0.1:8081 -hlcupdocs /path/to/hlcupdocs/FULL/ -concurrent 2 -time 30s -phase 3