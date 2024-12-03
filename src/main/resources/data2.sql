-- member
INSERT INTO member (member_id, login_id, business_registration_id, password, name, phone_number, identity_number, email, created_at, activity)
VALUES (1, 'hong123', 1, 'password123', '홍길동', '010-1234-5678', '800101-1234567', 'hong@gmail.com', '2022-01-01 10:00:00', 'ACTIVE');

INSERT INTO member (member_id, login_id, business_registration_id, password, name, phone_number, identity_number, email, created_at, activity)
VALUES (2, 'kim567', 2, 'password456', '김영희', '010-9876-5432', '850505-7654321', 'kim@gmail.com', '2023-03-15 15:30:00', 'ACTIVE');



-- BANK
INSERT INTO account (account_id, account_num, bank_name, balance, br_num)
VALUES (1, '123-456-789', '국민은행', 1000000, '1234567890');

INSERT INTO account (account_id, account_num, bank_name, balance, br_num)
VALUES (2, '987-654-321', '우리은행', 2000000, '0987654321');

-- POS
INSERT INTO pos (pos_id, br_num)
VALUES (1, '1234567890');

INSERT INTO pos (pos_id, br_num)
VALUES (2, '0987654321');

-- Business Registration
INSERT INTO business_registration (business_registration_id, br_num, business_type, business_item, business_start_date, representative_name, company_name, address, pos_id, account_id)
VALUES (1, '1234567890', '음식 및 음료', '카페', '2022-01-01', '홍길동', '연남동 카페', '서울특별시 마포구 연남동 1-1', 1, 1);

INSERT INTO business_registration (business_registration_id, br_num, business_type, business_item, business_start_date, representative_name, company_name, address, pos_id, account_id)
VALUES (2, '0987654321', '음식 및 음료', '카페', '2023-03-15', '김영희', '홍대 커피숍', '서울특별시 마포구 홍익로 15', 2, 2);



-- POS Sales
INSERT INTO pos_sales (pos_sales_id, pos_id, order_time, total_price, vat_amount, product_name, quantity, order_status, payment_type, payment_status)
VALUES (1, 1, '2024-01-01 12:30:00', 10000, 1000, '아메리카노', 2, 'COMPLETED', 'CARD', 'APPROVED');

INSERT INTO pos_sales (pos_sales_id, pos_id, order_time, total_price, vat_amount, product_name, quantity, order_status, payment_type, payment_status)
VALUES (2, 1, '2024-01-02 14:15:00', 15000, 1500, '카페라떼', 3, 'COMPLETED', 'CASH', 'APPROVED');

INSERT INTO pos_sales (pos_sales_id, pos_id, order_time, total_price, vat_amount, product_name, quantity, order_status, payment_type, payment_status)
VALUES (3, 2, '2024-01-03 10:45:00', 12000, 1200, '카푸치노', 2, 'COMPLETED', 'CARD', 'APPROVED');

-- Account History
INSERT INTO account_history (account_history_id, account_id, transaction_type, transaction_means, transaction_date, amount, category, note, fixed_expenses, store_name)
VALUES (1, 1, 'EXPENSE', 'CARD', '2024-01-01 10:00:00', 5000, '임대료', '월세', 1, '연남동 오피스');

INSERT INTO account_history (account_history_id, account_id, transaction_type, transaction_means, transaction_date, amount, category, note, fixed_expenses, store_name)
VALUES (2, 1, 'REVENUE', 'CASH', '2024-01-01 13:00:00', 20000, '매출', '아메리카노 판매', 0, '연남동 카페');

INSERT INTO account_history (account_history_id, account_id, transaction_type, transaction_means, transaction_date, amount, category, note, fixed_expenses, store_name)
VALUES (3, 2, 'EXPENSE', 'CARD', '2024-01-02 09:30:00', 8000, '재료비', '커피 원두 구매', 0, '마루 원두');

