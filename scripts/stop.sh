#!/usr/bin/env bash

PROJECT_NAME="app/chalna-server"
PROJECT_ROOT="/home/ubuntu/${PROJECT_NAME}"
JAR_FILE="$PROJECT_ROOT/chalna-server-0.0.1-SNAPSHOT.jar"

STOP_LOG="$PROJECT_ROOT/stop.log"
TIME_NOW=$(date +%c)

# 현재 구동 중인 애플리케이션 pid 확인 (프로세스 이름과 함께 실행 경로 확인)
CURRENT_PID=$(pgrep -f "$JAR_FILE")

# 프로세스가 켜져 있으면 종료
if [ -z "$CURRENT_PID" ]; then
  echo "$TIME_NOW > 현재 실행 중인 애플리케이션이 없습니다." >> $STOP_LOG
else
  echo "$TIME_NOW > 실행 중인 애플리케이션 (PID: $CURRENT_PID) 종료 시도" >> $STOP_LOG
  kill -15 $CURRENT_PID
  # 종료 확인 (일정 시간 대기 후 다시 확인)
  sleep 5
  if ps -p $CURRENT_PID > /dev/null; thens
    echo "$TIME_NOW > 애플리케이션 종료 실패 (PID: $CURRENT_PID). 강제 종료 시도" >> $STOP_LOG
    kill -9 $CURRENT_PID
  else
    echo "$TIME_NOW > 애플리케이션 종료 성공 (PID: $CURRENT_PID)" >> $STOP_LOG
  fi
fi
