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
