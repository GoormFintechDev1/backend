from selenium import webdriver
from selenium.webdriver.chrome.service import Service
import pandas as pd
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC
from selenium.webdriver.common.by import By
from selenium.common.exceptions import TimeoutException
import json

# 웹드라이버 시작
driver = webdriver.Chrome()
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
cards_elements = wait.until(EC.presence_of_all_elements_located((By.XPATH, "//ul[@class='rk_lst']/li")))

for card in cards_elements[:10]:
    if "ad" not in card.get_attribute("class"):
        rank_element = card.find_element(By.XPATH, ".//div[@class='num']")
        
        card_name_element = card.find_element(By.XPATH, ".//p[@class='card_name']")
        card_name = card_name_element.text.split('\n')[0]
        corp_name = ' '.join(card.find_element(By.XPATH, ".//p[@class='corp_name']").text.split())

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
    'Ranking': rankings,
    'Card Name': card_names,
    'Corporate Name': corp_names,
    'Benefits': benefits_cleaned,
    'Image URLs': image_urls
})

# DataFrame을 리스트-딕셔너리 형태로 변환
json_data = df.to_dict(orient="records")  # orient="records"는 각 행을 딕셔너리로 변환

# JSON 파일로 저장
with open("card_data.json", "w", encoding="utf-8") as json_file:
    json.dump(json_data, json_file, ensure_ascii=False, indent=4)

print(df)
driver.quit()