FROM openjdk:21-jdk-slim

# 作者
LABEL maintainer="szhuima"

# 配置
ENV PARAMS=""

# 时区
ENV TZ=PRC
ENV SPRING_PROFILES_ACTIVE=prod
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone
# 安装 Node.js 环境
RUN apt-get update && \
    apt-get install -y curl && \
    curl -fsSL https://deb.nodesource.com/setup_20.x | bash - && \
    apt-get install -y nodejs && \
    npm install -g npm@latest && \
    apt-get purge -y --auto-remove curl && \
    rm -rf /var/lib/apt/lists/*

# 添加应用
ADD agent-station-app/target/agent-station-app.jar /agent-station-app.jar

ENTRYPOINT ["sh","-c","java -jar $JAVA_OPTS /agent-station-app.jar $PARAMS"]