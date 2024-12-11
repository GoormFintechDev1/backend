INSERT INTO pos (
    pos_id, br_num
) VALUES
      (2, '2068692418'),
      (3, '2768956781');


INSERT INTO account (
    account_id, account_num, bank_name, balance, br_num
) VALUES
      (2, '220-234-567890', '국민은행', 5000000, '2068692418'),
      (3, '330-345-678901', '우리은행', 5000000, '2768956781');


INSERT INTO prompt (month, type, contents) VALUES
('2024-11', 'issue', '지난 1년 동안 커피 수입물가가 78%나 올라서 26년 만에 최대치라고 해요. 고환율, 이상기후, 보호무역 같은 문제들 때문에 국제 커피 가격도 덩달아 올랐고, 로부스타 원두는 91%, 아라비카 원두는 62%나 비싸졌다고 하네요. 이런 상황 때문에 스타벅스 같은 프랜차이즈는 물론 저가 커피 브랜드들까지 음료 가격을 올렸고, 내년에도 이 흐름이 계속될 거라고 해요.'),
('2024-10', 'issue', '요즘 저가 커피가 남녀노소 모두에게 사랑받으면서 시장이 빠르게 성장하고 있어요. 특히 메가커피는 남성 소비자 비율이 높아지면서 저가 커피 브랜드 중에서도 돋보이고 있대요. 하지만 최근 위생 문제 사례가 많아져서 이런 급성장이 오래가지 않을 수도 있다는 우려도 나와요. 업계에서 위생 관리를 철저히 하지 않으면 소비자 신뢰를 잃을 가능성도 커 보이네요.'),
('2024-09', 'issue', '요즘 편의점에서 간편하게 즐길 수 있는 RTD 커피가 대세로 자리 잡으면서, 시장 규모가 2조 원에 이를 만큼 성장하고 있어요. 동시에 저가 커피와 초고가 커피 전문점이 인기를 얻으며, 커피 시장의 양극화도 뚜렷해지고 있어요. 특히 해외 프리미엄 커피 브랜드들이 잇따라 국내에 매장을 열면서 고가 커피 시장이 더욱 커지고 있어요.'),
('2024-11', 'trend', '요즘 저당 음료와 건강을 고려한 메뉴가 인기인 만큼, 알룰로스나 스테비아를 활용한 저당 음료나 저칼로리 디저트를 추가해 보면 어떨까요? 또, 스타벅스처럼 커피 구독 서비스를 도입해서 월 정액제로 매일 일정 시간대 할인 혜택을 제공하면 정기 고객을 유치하는 데 효과적일 것 같아요!'),
('2024-10', 'trend', '요즘 커피 트렌드는 건강을 강조한 ‘헬시 플레저’와 개인의 취향을 중시하는 ‘커스텀 소비’가 대세예요. 저당 시럽이나 저칼로리 음료를 추가하면 헬시 플레저 트렌드에 맞출 수 있고, 당도나 샷 추가 등 선택지를 늘려 커스텀 소비를 반영하면 고객 만족도가 높아질 것 같아요!'),
('2024-09', 'trend', '넷플릭스 요리 경연 프로그램 덕분에 밤을 활용한 메뉴가 요즘 주목받고 있어요! 밤 티라미수나 밤 라떼 같은 메뉴들이 인기몰이 중이에요. 또, 런치플레이션 영향으로 점심시간 직장인을 위한 할인 이벤트에 대한 수요가 커지고 있는데, 점심 할인 이벤트를 진행해 보면 어떨까요?');


INSERT INTO business_registration (
    business_registration_id, member_id, business_type, business_item, br_num,
    address, business_start_date, representative_name, company_name, pos_id, account_id
) VALUES
(2, NULL, '음식점업', '커피 전문점', '2068692418',
 '서울특별시 서대문구 연희동 96-9', '2020-03-15', '박수진', '프로토콜 커피', NULL, NULL),
(3, NULL, '음식점업', '커피 전문점', '2768956781',
 '서울특별시 서대문구 연희동 132-24', '2021-06-20', '이정훈', '유니크 커피로스터스', NULL, NULL);



-- 10월 데이터
INSERT INTO account_history (account_id, transaction_type, transaction_means, transaction_date, amount, category, note, fixed_EXPENSEs, store_name)
VALUES
    (2, 'EXPENSE', 'Cash', '2024-10-05 10:00:00', 3000000, '임대료', '임대료', true, '임차인'),
    (2, 'EXPENSE', 'Cash', '2024-10-12 11:00:00', 100000, '재료비', '커피 원두', true, '강릉 로스터리'),
    (2, 'EXPENSE', 'Cash', '2024-10-22 13:00:00', 60000, '재료비', '우유', true, '매일유업'),
    (2, 'EXPENSE', 'Cash', '2024-10-28 14:00:00', 70000, '공과금', '수도요금', true, '서울 아리수 본부'),
    (2, 'EXPENSE', 'Cash', '2024-10-30 16:00:00', 140000, '공과금', '전기요금', true, '한국전력공사'),

    (3, 'EXPENSE', 'Cash', '2024-10-05 09:00:00', 3500000, '임대료', '임대료', true, '임차인'),
    (3, 'EXPENSE', 'Cash', '2024-10-15 10:00:00', 150000, '재료비', '커피 원두', true, '강릉 로스터리'),
    (3, 'EXPENSE', 'Cash', '2024-10-23 15:00:00', 70000, '재료비', '우유', true, '매일유업'),
    (3, 'EXPENSE', 'Cash', '2024-10-27 16:00:00', 90000, '공과금', '수도요금', true, '서울 아리수 본부'),
    (3, 'EXPENSE', 'Cash', '2024-10-30 17:00:00', 160000, '공과금', '전기요금', true, '한국전력공사');

-- 11월 데이터
INSERT INTO account_history (account_id, transaction_type, transaction_means, transaction_date, amount, category, note, fixed_EXPENSEs, store_name)
VALUES
    (2, 'EXPENSE', 'Cash', '2024-11-05 10:00:00', 3000000, '임대료', '임대료', true, '임차인'),
    (2, 'EXPENSE', 'Cash', '2024-11-12 11:00:00', 115000, '재료비', '커피 원두', true, '강릉 로스터리'),
    (2, 'EXPENSE', 'Cash', '2024-11-22 13:00:00', 62000, '재료비', '우유', true, '매일유업'),
    (2, 'EXPENSE', 'Cash', '2024-11-28 14:00:00', 72000, '공과금', '수도요금', true, '서울 아리수 본부'),
    (2, 'EXPENSE', 'Cash', '2024-11-30 16:00:00', 142000, '공과금', '전기요금', true, '한국전력공사'),

    (3, 'EXPENSE', 'Cash', '2024-11-05 09:00:00', 3500000, '임대료', '임대료', true, '임차인'),
    (3, 'EXPENSE', 'Cash', '2024-11-15 10:00:00', 155000, '재료비', '커피 원두', true, '강릉 로스터리'),
    (3, 'EXPENSE', 'Cash', '2024-11-23 15:00:00', 75000, '재료비', '우유', true, '매일유업'),
    (3, 'EXPENSE', 'Cash', '2024-11-27 16:00:00', 92000, '공과금', '수도요금', true, '서울 아리수 본부'),
    (3, 'EXPENSE', 'Cash', '2024-11-30 17:00:00', 165000, '공과금', '전기요금', true, '한국전력공사');

-- 12월 데이터
INSERT INTO account_history (account_id, transaction_type, transaction_means, transaction_date, amount, category, note, fixed_EXPENSEs, store_name)
VALUES
    (2, 'EXPENSE', 'Cash', '2024-12-05 10:00:00', 3000000, '임대료', '임대료', true, '임차인'),
    (2, 'EXPENSE', 'Cash', '2024-12-12 11:00:00', 120000, '재료비', '커피 원두', true, '강릉 로스터리'),
    (2, 'EXPENSE', 'Cash', '2024-12-22 13:00:00', 64000, '재료비', '우유', true, '매일유업'),
    (2, 'EXPENSE', 'Cash', '2024-12-28 14:00:00', 74000, '공과금', '수도요금', true, '서울 아리수 본부'),
    (2, 'EXPENSE', 'Cash', '2024-12-30 16:00:00', 144000, '공과금', '전기요금', true, '한국전력공사'),

    (3, 'EXPENSE', 'Cash', '2024-12-05 09:00:00', 3500000, '임대료', '임대료', true, '임차인'),
    (3, 'EXPENSE', 'Cash', '2024-12-15 10:00:00', 160000, '재료비', '커피 원두', true, '강릉 로스터리'),
    (3, 'EXPENSE', 'Cash', '2024-12-23 15:00:00', 78000, '재료비', '우유', true, '매일유업'),
    (3, 'EXPENSE', 'Cash', '2024-12-27 16:00:00', 95000, '공과금', '수도요금', true, '서울 아리수 본부'),
    (3, 'EXPENSE', 'Cash', '2024-12-30 17:00:00', 168000, '공과금', '전기요금', true, '한국전력공사');
