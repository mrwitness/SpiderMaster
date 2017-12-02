#!/bin/bash
#Usage:
# 1 modify conf/connection_benchmark.properties,set ip,port,concurrents
# 2 ./start_connect_benchmark.sh

java -XX:+PrintGCDetails -Xmx256m -Xmx128m -Xloggc:./connectionGclog.txt -cp lib/SpiderCommon.jar:SpiderMaster-1.0-jar-with-dependencies.jar wuxian.me.spidermaster.benchmark.CientConnectionBenchmark $@ 


