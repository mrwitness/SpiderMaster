#!/bin/bash
#Usage: ./start_server_benchmark.sh listenPort threadNum

java -XX:+PrintGCDetails -Xmx256m -Xmx128m -Xloggc:./serverGclog.txt -cp lib/SpiderCommon.jar:SpiderMaster-1.0-jar-with-dependencies.jar wuxian.me.spidermaster.benchmark.ServerBenchmark 1>&1 2>&1 $@ 



