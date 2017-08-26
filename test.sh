#!/usr/bin/env bash


rm result.txt

#./highloadcup_tester -addr http://127.0.0.1:8080 -hlcupdocs /Users/volyx/Projects/hlcupdocs/data/FULL -test -phase 1 > result.txt

for p in {1..3}; do
   ./highloadcup_tester -addr http://127.0.0.1:8080 -hlcupdocs /Users/volyx/Projects/hlcupdocs/data/FULL -test -phase  $p > result$p.txt
 done

# ./tester -addr http://127.0.0.1:8081 -hlcupdocs /path/to/hlcupdocs/FULL/ -concurrent 2 -time 30s -phase 3