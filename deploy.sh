#!/bin/bash
set -e

# ===== 환경변수 로드 =====
source .env.deploy

# ===== 설정 =====
IMAGE_NAME=java-app:latest
TAR_NAME=java.tar
CONTAINER_NAME=java.upinnn.com
LOCAL_PORT=8080
REMOTE_PORT=8080
JAR_PATH=build/libs/javaspring-0.0.1-SNAPSHOT.jar

# ===== 0. Gradle 빌드 =====
echo "Gradle bootJar 실행"
./gradlew bootJar

# ===== 1. jar 파일 확인 =====
if [ ! -f "$JAR_PATH" ]; then
  echo "JAR 파일이 생성되지 않았습니다: $JAR_PATH"
  exit 1
fi

# ===== 2. Docker 이미지 빌드 =====
echo "Docker 이미지 생성"
docker buildx build \
  --no-cache \
  --platform linux/amd64 \
  -t $IMAGE_NAME \
  --output type=docker \
  .

# ===== 3. 이미지 저장 =====
echo "이미지 저장: $TAR_NAME"
docker save $IMAGE_NAME -o $TAR_NAME

# ===== 4. 서버로 전송 =====
echo "tar 파일 서버 업로드"
scp $TAR_NAME $REMOTE_USER@$REMOTE_IP:$REMOTE_DIR/

# ===== 5. 로컬 정리 =====
echo "로컬 tar 및 이미지 삭제"
rm -f "$TAR_NAME"
docker rmi -f $IMAGE_NAME || true
docker builder prune -af || true

# ===== 6. 서버 배포 =====
echo "서버 컨테이너 재배포"
ssh $REMOTE_USER@$REMOTE_IP << EOF
  set -e
  echo "기존 컨테이너 중지 및 제거"
  docker stop $CONTAINER_NAME || true
  docker rm $CONTAINER_NAME || true

  echo "이미지 로드"
  docker load < $REMOTE_DIR/$TAR_NAME

  echo "새 컨테이너 실행"
  docker run -d \
    --name $CONTAINER_NAME \
    --network router \
    -p $REMOTE_PORT:$LOCAL_PORT \
    --env-file /home/docker-manager/project_envs/$CONTAINER_NAME.env \
    $IMAGE_NAME
EOF

# ===== 7. 완료 =====
echo "✅ 배포 완료"