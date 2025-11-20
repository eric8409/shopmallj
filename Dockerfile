# ===============================================
# 第一階段：建構應用程式 (Build Stage)
# ===============================================
# 使用 Maven 3.8.1 和 OpenJDK 17 作為建構環境
FROM maven:3.8.1-openjdk-17 AS build

# 設定容器內的工作目錄
WORKDIR /app

# 複製 Maven 的 pom.xml 檔案，並先下載依賴
# 這個步驟的好處是：如果 pom.xml 沒變，Docker 就會快取依賴下載結果，加速後續建構
COPY pom.xml .
RUN mvn dependency:go-offline

# 複製剩餘的原始碼
COPY src ./src
# 執行 Maven package 命令，產生可執行的 JAR 檔（跳過測試以節省時間）
RUN mvn clean package -DskipTests


# ===============================================
# 第二階段：運行應用程式 (Runtime Stage)
# ===============================================
# 使用一個極小且安全的映像檔，僅包含運行所需的 JRE
FROM eclipse-temurin:17-alpine

# 設定工作目錄
WORKDIR /app

# 將第一階段建構好的 JAR 檔複製到當前工作目錄
# 請確認 target/ 後面的 JAR 檔名符合您的專案實際名稱（通常是 artifactId-version.jar）
COPY --from=build /app/target/*.jar app.jar

# 宣告容器將監聽 8080 Port
EXPOSE 8080

# 定義容器啟動時執行的主命令
ENTRYPOINT ["java", "-jar", "app.jar"]