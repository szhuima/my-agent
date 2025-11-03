mvn clean package -DskipTests
echo "Maven 打包完成"

sh ./build.sh
echo "Docker 镜像构建完成"

docker compose -f ./docker-compose.yml up -d
echo "Docker 容器已创建成功并启动"