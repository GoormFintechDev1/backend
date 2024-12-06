from selenium import webdriver
from selenium.webdriver.chrome.service import Service
from selenium.webdriver.chrome.options import Options
import pandas as pd
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC
from selenium.webdriver.common.by import By
from selenium.common.exceptions import TimeoutException
import json

# Chrome 옵션 설정
chrome_options = Options()
chrome_options.add_argument("--headless")  # Headless 모드
chrome_options.add_argument("--no-sandbox")
chrome_options.add_argument("--disable-dev-shm-usage")
chrome_options.add_argument("--disable-gpu")

# ChromeDriver 경로 지정
# service = Service("/usr/local/bin/chromedriver")

# WebDriver 초기화
driver = webdriver.Chrome(options=chrome_options)

# url 접속 및 시작
url = 'https://www.card-gorilla.com/chart/top100?term=weekly'
driver.get(url)

# 데이터를 저장할 리스트 초기화
rankings = []
card_names = []
corp_names = []
benefits = []
detail_urls = []
image_urls = []

wait = WebDriverWait(driver, 10)

# ul.rk_lst > li 를 조회
winner_element = wait.until(EC.presence_of_element_located((By.XPATH, "//div[@class='num1_card']")))
cards_elements = wait.until(EC.presence_of_all_elements_located((By.XPATH, "//ul[@class='rk_lst']/li")))

for card in cards_elements:
    if "ad" not in card.get_attribute("class"):
        rank_element = card.find_element(By.XPATH, ".//div[@class='num']")
        
        card_name_element = card.find_element(By.XPATH, ".//p[@class='card_name']")
        card_name = card_name_element.text.split('\n')[0]
        corp_name = ' '.join(card.find_element(By.XPATH, ".//p[@class='corp_name']").text.split())
        
        # 1위 카드의 정보는 winner_element 에서 따로 꺼내온다
        if rank_element.text == "" and card_name == "" and corp_name == "":
            rank_element = winner_element.find_element(By.XPATH, ".//div[@class='winner']")

            card_name_element = winner_element.find_element(By.XPATH, ".//span[@class='card_name']")
            card_name = card_name_element.text.split('\n')[0]
            corp_name = ' '.join(winner_element.find_element(By.XPATH, ".//p[@class='corp_name']").text.split())

        # card 에서 href attribute 를 찾아서 값을 detail_url 에 대입
        detail_url = card.find_element(By.XPATH, ".//a").get_attribute('href')
        img_url = card.find_element(By.XPATH, ".//div[@class='card_img']//img").get_attribute('src')

        # 수집한 데이터 저장
        rankings.append(rank_element.text)
        card_names.append(card_name)
        corp_names.append(corp_name)
        detail_urls.append(detail_url)
        image_urls.append(img_url)

        benefits.append([]) 

# 상세 페이지로의 접근 및 혜택 정보 수집
for idx, url in enumerate(detail_urls):
    if not isinstance(url, str):  # URL이 문자열이 아니면 건너뛰기
        print(f"유효하지 않은 URL: {url}")
        continue

    driver.get(url)  
    try:
        benefit_elements_detail_page = wait.until(EC.presence_of_all_elements_located((By.XPATH, "//article[@class='cmd_con benefit']//div//dl")))
        benefit_texts_detail_page = [element.text for element in benefit_elements_detail_page]
        benefits[idx] = benefit_texts_detail_page
    except TimeoutException:
        print(f"혜택 정보를 얻지 못한 URL: {url}")

# 혜택 정보 정리
benefits_cleaned = []
for benefit_list in benefits:
    cleaned_list = []
    for benefit in benefit_list:
        cleaned_benefit = ' '.join(benefit.split())
        cleaned_list.append(cleaned_benefit)
    benefits_cleaned.append(cleaned_list)

# 수집한 데이터를 pandas DataFrame으로 변환
df = pd.DataFrame({
    'ranking': rankings,
    'cardName': card_names,
    'corporateName': corp_names,
    'benefits': benefits_cleaned,
    'imageURL': image_urls
})

# DataFrame을 리스트-딕셔너리 형태로 변환
json_data = df.to_dict(orient="records")  # orient="records"는 각 행을 딕셔너리로 변환

# Volume에 저장할 경로
output_path = "/shared/card_data.json"

# JSON 파일로 저장
with open(output_path, "w", encoding="utf-8") as json_file:
    json.dump(json_data, json_file, ensure_ascii=False, indent=4)

print(df)
driver.quit()