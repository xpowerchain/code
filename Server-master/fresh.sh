#!/bin/bash
git pull

mv ./*.lock /tmp
mv tmp/*.lock /tmp

ps -ef|grep 'node index.js'|grep -v grep|awk '{print $2}'|xargs kill -9
nohup node index.js >>debug.log &

ps -ef|grep 'node schedule.js'|grep -v grep|awk '{print $2}'|xargs kill -9
nohup node schedule.js >>schedule.log &

ps -ef|grep 'node schedule_1.js'|grep -v grep|awk '{print $2}'|xargs kill -9
nohup node schedule_1.js >>schedule_1.log &

ps -ef|grep 'node schedule_2.js'|grep -v grep|awk '{print $2}'|xargs kill -9
nohup node schedule_2.js >>schedule_2.log &

ps -ef|grep 'node schedule_3.js'|grep -v grep|awk '{print $2}'|xargs kill -9
nohup node schedule_3.js >>schedule_3.log &

ps -ef|grep 'node schedule_peer_check.js'|grep -v grep|awk '{print $2}'|xargs kill -9
nohup node schedule_peer_check.js >>schedule_peer_check.log &

ps -ef|grep 'node schedule_balance.js'|grep -v grep|awk '{print $2}'|xargs kill -9
nohup node schedule_balance.js >>schedule_balance.log &