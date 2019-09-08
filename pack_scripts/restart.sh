#! /bin/bash
#进程名字可修改

cd /home/ansersion/bcServer
CMD="/usr/bin/java -jar bc_server.jar &> /dev/null &"
LOG_FILE="restart_log.log"
while true ; do
    # NUM=`netstat -tnul | grep -w 8025 | grep -v grep |wc -l`
    NUM=`ps -ef|grep java|grep bc_server.jar|wc -l`;
    # echo $NUM
    #少于1，重启进程
    if [ "${NUM}" -lt "1" ];then
        echo "`date` to restart" >> $LOG_FILE
        /usr/bin/java $BC_SERVER_JVM_PARA -jar bc_server.jar &> /dev/null &
        # echo "cmd end"
    fi  
    sleep 60s 
done

exit 0

