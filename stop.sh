ps -ef |grep java |grep aiphone-api.jar |grep -v 'grep'|awk '{print $2}'  | xargs kill -9
