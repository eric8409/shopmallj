# ===============================================
# 第一階段：建構應用程式 (Build Stage)
# ===============================================
# 修正：標籤改為 3.8-eclipse-temurin-17 (去掉 .1) 或 3.9-eclipse-temurin-17
FROM maven:3.8-eclipse-temurin-17 AS build

WORKDIR /app

# 複製 pom.xml 並下載依賴
COPY pom.xml .
# 下載依賴階段
RUN mvn dependency:go-offline

# 複製原始碼並打包
COPY src ./src
RUN mvn clean package -DskipTests


# ===============================================
# 第二階段：運行應用程式 (Runtime Stage)
# ===============================================
# 使用穩定的 Temurin JRE 17
FROM eclipse-temurin:17-jre

WORKDIR /app

# 從建構階段複製 JAR 檔
COPY --from=build /app/target/*.jar app.jar

# 宣告監聽 8080 Port
EXPOSE 8080

# 設定 JVM 參數，針對 Render 免費版建議調整為 384m 避免壓線 512m 限制
ENTRYPOINT ["java", "-Xmx384m", "-jar", "app.jar"]
