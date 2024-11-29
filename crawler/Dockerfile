# 1단계: 빌드 환경
FROM python:3.13-alpine AS builder

# 필요한 패키지 설치
RUN apk add --no-cache \
    bash \
    gcc \
    musl-dev \
    libffi-dev \
    openssl-dev \
    && pip install --no-cache-dir selenium pandas

# 2단계: 경량 실행 환경
FROM python:3.13-alpine

# 크롬 및 드라이버 설치
RUN apk add --no-cache chromium chromium-chromedriver

# 환경 변수 설정
ENV PYTHONUNBUFFERED=1 \
    CHROME_BIN=/usr/bin/chromium-browser \
    CHROMEDRIVER_PATH=/usr/bin/chromedriver

# builder 단계에서 필요한 파일 복사
COPY --from=builder /usr/local/lib/python3.13 /usr/local/lib/python3.13
COPY --from=builder /usr/local/bin /usr/local/bin

# 작업 디렉토리 설정
WORKDIR /app

# 스크립트 복사
COPY card_crawler.py .

# 실행 명령어 설정
CMD ["python", "card_crawler.py"]
