
# 构建镜像
docker build -t agent-station-front:1.0-SNAPSHOT -f ./Dockerfile .

# 运行容器
docker compose  -f ./docker-compose.yml up -d

