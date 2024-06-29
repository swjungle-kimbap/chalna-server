#!/usr/bin/env bash

<<<<<<< Updated upstream
=======

>>>>>>> Stashed changes
# 프로젝트 루트 디렉토리 설정
PROJECT_ROOT="/home/ubuntu"
JAR_FILE="$PROJECT_ROOT/chalna-server-0.0.1-SNAPSHOT.jar"

# 로그 파일 설정
APP_LOG="$PROJECT_ROOT/application.log"
ERROR_LOG="$PROJECT_ROOT/error.log"
DEPLOY_LOG="$PROJECT_ROOT/deploy.log"

# 현재 시간
TIME_NOW=$(date +%c)

## 프로젝트 디렉토리 생성 (이미 존재하는 경우 무시)
#mkdir -p $PROJECT_ROOT
#
## 빌드 디렉토리 생성 (이미 존재하는 경우 무시)
#mkdir -p $BUILD_DIR

# 프로젝트 빌드 (예: Gradle 사용)
#cd $PROJECT_ROOT
#./gradlew build

# 빌드된 파일 복사 (이미 빌드된 파일이 해당 경로에 존재한다고 가정)
#echo "$TIME_NOW > $JAR_FILE 파일 복사" >> $DEPLOY_LOG
#cp $BUILD_DIR/chalna-server-0.0.1-SNAPSHOT.jar $PROJECT_ROOT/

# 환경 변수 설정 (선택 사항)
export JAVA_OPTS="-Xms512m -Xmx1024m"

# jar 파일 실행
echo "$TIME_NOW > $JAR_FILE 파일 실행" >> $DEPLOY_LOG
nohup java -jar $PROJECT_ROOT/chalna-server-0.0.1-SNAPSHOT.jar > $APP_LOG 2> $ERROR_LOG &

# 실행된 프로세스 ID 확인
CURRENT_PID=$(pgrep -f $PROJECT_ROOT/chalna-server-0.0.1-SNAPSHOT.jar)
echo "$TIME_NOW > 실행된 프로세스 아이디 $CURRENT_PID 입니다." >> $DEPLOY_LOG
