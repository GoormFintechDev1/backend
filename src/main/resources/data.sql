INSERT INTO business_category (business_category_id, type, field) VALUES (1, '음식점', '커피전문점');

INSERT INTO business_registration (business_registration_id, member_id, business_category_id, br_num, business_start_date, representative_name, company_name, address) VALUES (1, NULL, 1, 1234567890, '2021-02-15', '김철수', '카페 블리스', '서울시 강남구 테헤란로 123');
INSERT INTO business_registration (business_registration_id, member_id, business_category_id, br_num, business_start_date, representative_name, company_name, address) VALUES (2, NULL, 1, 2345678901, '2022-05-20', '박영희', '커피 코너', '서울시 마포구 홍익로 456');
INSERT INTO business_registration (business_registration_id, member_id, business_category_id, br_num, business_start_date, representative_name, company_name, address) VALUES (3, NULL, 1, 3456789012, '2020-07-10', '이민수', '더 비너리', '부산시 해운대구 해변로 789');
INSERT INTO business_registration (business_registration_id, member_id, business_category_id, br_num, business_start_date, representative_name, company_name, address) VALUES (4, NULL, 1, 4567890123, '2021-11-30', '최수정', '에스프레소 익스프레스', '대구시 중구 중앙대로 101');
INSERT INTO business_registration (business_registration_id, member_id, business_category_id, br_num, business_start_date, representative_name, company_name, address) VALUES (5, NULL, 1, 5678901234, '2019-01-25', '장민호', '카페 센트럴', '서울시 강동구 올림픽로 202');
INSERT INTO business_registration (business_registration_id, member_id, business_category_id, br_num, business_start_date, representative_name, company_name, address) VALUES (6, NULL, 1, 6789012345, '2018-09-12', '윤정희', '브루드 어웨이크닝', '광주시 서구 치평로 303');
INSERT INTO business_registration (business_registration_id, member_id, business_category_id, br_num, business_start_date, representative_name, company_name, address) VALUES (7, NULL, 1, 7890123456, '2020-03-08', '정우성', '자바 정션', '인천시 연수구 컨벤시아대로 404');
INSERT INTO business_registration (business_registration_id, member_id, business_category_id, br_num, business_start_date, representative_name, company_name, address) VALUES (8, NULL, 1, 8901234567, '2022-06-18', '이지은', '더 커피 스팟', '대전시 유성구 대학로 505');
INSERT INTO business_registration (business_registration_id, member_id, business_category_id, br_num, business_start_date, representative_name, company_name, address) VALUES (9, NULL, 1, 9012345678, '2021-04-22', '김하늘', '컵 오브 조', '울산시 남구 삼산로 606');
INSERT INTO business_registration (business_registration_id, member_id, business_category_id, br_num, business_start_date, representative_name, company_name, address) VALUES (10, NULL, 1, 1234567891, '2023-01-05', '한지민', '카페 딜라이트', '경기도 성남시 분당구 정자일로 707');

INSERT INTO account (account_id, business_id, account_num, bank_name, balance) VALUES (1, 1, '110-1234-5678-9012', '국민은행', 300000);
INSERT INTO account (account_id, business_id, account_num, bank_name, balance) VALUES (2, 2, '220-2345-6789-0123', '신한은행', 450000);
INSERT INTO account (account_id, business_id, account_num, bank_name, balance) VALUES (3, 3, '330-3456-7890-1234', '우리은행', 150000);
INSERT INTO account (account_id, business_id, account_num, bank_name, balance) VALUES (4, 4, '440-4567-8901-2345', '하나은행', 500000);
INSERT INTO account (account_id, business_id, account_num, bank_name, balance) VALUES (5, 5, '550-5678-9012-3456', '카카오뱅크', 200000);

INSERT INTO account_history (history_id, account_id, transaction_type, transaction_means, transaction_date, amount, balance_after, category, note, fixed_expenses, store_name) VALUES (1, 1, 'Revenue', 'Card', '2024-11-01 10:30:00', 15000, 315000, '음료 판매', '아메리카노 3잔 판매', FALSE, '카페 블리스');
INSERT INTO account_history (history_id, account_id, transaction_type, transaction_means, transaction_date, amount, balance_after, category, note, fixed_expenses, store_name) VALUES (2, 1, 'Expense', 'Card', '2024-11-02 14:20:00', 50000, 265000, '재료비', '원두 구입', TRUE, '블루빈 커피 원두 공급');
INSERT INTO account_history (history_id, account_id, transaction_type, transaction_means, transaction_date, amount, balance_after, category, note, fixed_expenses, store_name) VALUES (3, 1, 'Revenue', 'Cash', '2024-11-03 12:45:00', 10000, 275000, '음료 판매', '카푸치노 2잔 판매', FALSE, '카페 블리스');
INSERT INTO account_history (history_id, account_id, transaction_type, transaction_means, transaction_date, amount, balance_after, category, note, fixed_expenses, store_name) VALUES (4, 1, 'Expense', 'Cash', '2024-11-04 09:10:00', 20000, 255000, '임대료', '월 임대료 납부', TRUE, '해피타운 건물 관리');
INSERT INTO account_history (history_id, account_id, transaction_type, transaction_means, transaction_date, amount, balance_after, category, note, fixed_expenses, store_name) VALUES (5, 1, 'Revenue', 'Card', '2024-11-05 17:00:00', 22000, 277000, '디저트 판매', '크루아상 4개 판매', FALSE, '카페 블리스');

INSERT INTO pos (pos_id, business_id, account_id, income) VALUES (1, 1, 1, 5000000);
INSERT INTO pos (pos_id, business_id, account_id, income) VALUES (2, 1, 1, 2500000);
INSERT INTO pos (pos_id, business_id, account_id, income) VALUES (3, 1, 1, 3200000);
INSERT INTO pos (pos_id, business_id, account_id, income) VALUES (4, 1, 1, 1500000);
INSERT INTO pos (pos_id, business_id, account_id, income) VALUES (5, 1, 1, 1000000);

INSERT INTO pos (pos_id, business_id, account_id, income) VALUES (6, 2, 1, 1000000);


INSERT INTO pos_sale (sale_id, pos_id, sale_date, sale_time, payment_type, total_amount, vat_amount, card_company, approval_number) VALUES (1, 1, '2024-11-07 10:00:00', '2024-11-07 10:00:00', 'CARD', 100000, 10000, '신한카드', 'SH12345678');
INSERT INTO pos_sale (sale_id, pos_id, sale_date, sale_time, payment_type, total_amount, vat_amount, card_company, approval_number) VALUES (2, 2, '2024-11-07 11:00:00', '2024-11-07 11:00:00', 'CASH', 50000, 0, NULL, NULL);
INSERT INTO pos_sale (sale_id, pos_id, sale_date, sale_time, payment_type, total_amount, vat_amount, card_company, approval_number) VALUES (3, 3, '2024-11-07 12:00:00', '2024-11-07 12:00:00', 'CARD', 200000, 20000, 'KB국민카드', 'KB98765432');
INSERT INTO pos_sale (sale_id, pos_id, sale_date, sale_time, payment_type, total_amount, vat_amount, card_company, approval_number) VALUES (4, 4, '2024-11-07 13:00:00', '2024-11-07 13:00:00', 'CARD', 150000, 15000, '삼성카드', 'SP11223344');
INSERT INTO pos_sale (sale_id, pos_id, sale_date, sale_time, payment_type, total_amount, vat_amount, card_company, approval_number) VALUES (5, 5, '2024-11-07 14:00:00', '2024-11-07 14:00:00', 'CARD', 120000, 12000, '현대카드', 'HY55667788');

INSERT INTO pos_sale (sale_id, pos_id, sale_date, sale_time, payment_type, total_amount, vat_amount, card_company, approval_number) VALUES (5, 5, '2024-11-07 14:00:00', '2024-11-07 14:00:00', 'CARD', 120000, 12000, '현대카드', 'HY55667788');