# 设置基础镜像
FROM nginx

# 安装常用命令
RUN apt-get update
RUN apt-get install -y wget       # 安装wget
RUN apt-get install vim -y        # 安装vim
RUN apt-get install -y psmisc     # 安装ps

# 设置工作目录
RUN mkdir /storage
WORKDIR /storage

# 安装java8环境
RUN mkdir /usr/local/java
# 方式一：下载jdk并解压到指定目录（适用于网速快的情况，需要提前安装wget）
RUN wget https://mirrors.huaweicloud.com/java/jdk/8u202-b08/jdk-8u202-linux-x64.tar.gz
RUN tar zxvf jdk-8u202-linux-x64.tar.gz -C /usr/local/java && rm -f jdk-8u202-linux-x64.tar.gz
# 方式二：将本地jdk复制到内部目录并自动解压（适用于网速慢的情况，提前下载好）
# ADD jdk-8u202-linux-x64.tar.gz /usr/local/java
# RUN rm -f jdk-8u202-linux-x64.tar.gz
RUN ln -s /usr/local/java/jdk1.8.0_202 /usr/local/java/jdk
ENV JAVA_HOME /usr/local/java/jdk
ENV JRE_HOME ${JAVA_HOME}/jre
ENV CLASSPATH .:${JAVA_HOME}/lib:${JRE_HOME}/lib
ENV PATH ${JAVA_HOME}/bin:$PATH

# 放置前端代码（含文档）及nginx配置
ADD dist/ /storage/web_code
ADD dist_doc/ /storage/web_doc
COPY nginx.conf /etc/nginx/nginx.conf
COPY yoyo_web.conf /etc/nginx/conf.d/yoyo_web.conf
COPY yoyo_web_doc.conf /etc/nginx/conf.d/yoyo_web_doc.conf
COPY proxy.conf /etc/nginx

# 放置后端代码及包外配置
COPY web_manage-0.0.1.jar /storage
COPY config /storage

# 放置启动脚本并授予权限
COPY start_web.sh /storage/start_web.sh
RUN chmod u+x /storage/start_web.sh

# 容器服务自启
ENTRYPOINT ["/storage/start_web.sh"]