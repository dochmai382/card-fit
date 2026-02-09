-- ===========================================
-- 시드 데이터 (개발/테스트용)
-- 출처: 카드고릴라 (https://card-gorilla.com)
-- 작성일: 2026-02-09
-- 주의: TRUNCATE 포함 - 실행 시 기존 데이터 삭제됨
-- ===========================================

-- 기존 데이터 삭제 (순서 중요: FK 제약조건)
TRUNCATE TABLE card_benefits, cards, categories RESTART IDENTITY CASCADE;

-- 카테고리
INSERT INTO categories (id, name, keywords) VALUES
(1, '커피/카페', '스타벅스,투썸,이디야,커피빈,메가커피,컴포즈'),
(2, '외식/배달', '배달의민족,요기요,쿠팡이츠,배민'),
(3, '대중교통/택시', '버스,지하철,택시,카카오T'),
(4, '온라인 쇼핑', '쿠팡,11번가,G마켓,네이버쇼핑,마켓컬리'),
(5, '마트/편의점', 'GS25,CU,세븐일레븐,이마트,롯데마트'),
(6, '주유', '주유소,SK에너지,GS칼텍스'),
(7, '문화/구독', '넷플릭스,유튜브,멜론,CGV'),
(8, '의료/병원', '병원,약국,의원'),
(9, '교육/학원', '학원,교육,인강'),
(10, '공과금/생활비', '전기,가스,수도,관리비'),
(11, '해외/여행', '해외결제,면세점,항공'),
(12, '항공마일리지', '대한항공,아시아나,마일리지'),
(13, '통신', '휴대폰,SKT,KT,LGU+'),
(0, '기타', '기타');

-- 카드 (신용카드)
INSERT INTO cards (id, issuer, name, card_type, annual_fee, total_limit, min_performance, card_image_url, status)
VALUES (1, '신한카드', '신한카드 Mr.Life', 'CREDIT', 15000, 50000, 300000,
        'https://api.card-gorilla.com:8080/storage/card/24/card_img/20562/24card.png', 'ACTIVE'),
       (2, '삼성카드', '삼성카드 taptap O', 'CREDIT', 10000, 30000, 300000,
        'https://api.card-gorilla.com:8080/storage/card/48/card_img/18/48card.png', 'ACTIVE'),
       (3, '삼성카드', '삼성카드 & MILEAGE PLATINUM', 'CREDIT', 49000, NULL, 0,
        'https://api.card-gorilla.com:8080/storage/card/51/card_img/21/51card.png', 'ACTIVE'),
       (4, 'KB국민카드', 'KB국민 My WE:SH 카드', 'CREDIT', 15000, 50000, 400000,
        'https://api.card-gorilla.com:8080/storage/card/2418/card_img/25895/2418card.png', 'ACTIVE'),
       (5, '신한카드', '신한카드 Deep Oil', 'CREDIT', 10000, 50000, 300000,
        'https://api.card-gorilla.com:8080/storage/card/179/card_img/149/179card.png', 'ACTIVE'),
       (6, '현대카드', '현대카드ZERO Edition3(할인형)', 'CREDIT', 15000, NULL, 0,
        'https://api.card-gorilla.com:8080/storage/card/2712/card_img/29548/2712card.png', 'ACTIVE'),
       (7, 'NH농협카드', '올바른 FLEX 카드', 'CREDIT', 10000, 32000, 300000,
        'https://api.card-gorilla.com:8080/storage/card/1814/card_img/21576/1814card.png', 'ACTIVE'),
       (8, '롯데카드', 'LOCA LIKIT 1.2', 'CREDIT', 10000, NULL, 0,
        'https://api.card-gorilla.com:8080/storage/card/2242/card_img/24050/2242card.png', 'ACTIVE'),
       (9, '우리카드', '카드의정석 EVERY 1', 'CREDIT', 12000, NULL, 0,
        'https://api.card-gorilla.com:8080/storage/card/2619/card_img/28435/2619card.png', 'ACTIVE'),
       (10, '신한카드', '신한카드 처음', 'CREDIT', 15000, 40000, 300000,
        'https://api.card-gorilla.com:8080/storage/card/2807/card_img/30596/2807card.png', 'ACTIVE');

INSERT INTO cards (id, issuer, name, card_type, annual_fee, total_limit, min_performance, card_image_url, status)
VALUES (11, '신한카드', '신한카드 Deep Dream', 'CREDIT', 8000, NULL, 0,
        'https://api.card-gorilla.com:8080/storage/card/154/card_img/20563/154card.png', 'ACTIVE'),
       (12, '현대카드', '현대카드 M Edition3', 'CREDIT', 30000, NULL, 500000,
        'https://api.card-gorilla.com:8080/storage/card/611/card_img/605/611card.png', 'ACTIVE'),
       (13, '우리BC카드', 'BC 바로 클리어 플러스', 'CREDIT', 5000, 10000, 300000,
        'https://api.card-gorilla.com:8080/storage/card/2221/card_img/23869/2221card.png', 'ACTIVE'),
       (14, 'KB국민카드', '다담 카드', 'CREDIT', 15000, NULL, 300000,
        'https://api.card-gorilla.com:8080/storage/card/42/card_img/14/42card.png', 'ACTIVE'),
       (15, '삼성카드', '삼성 ID ON 카드', 'CREDIT', 20000, 40000, 300000,
        'https://api.card-gorilla.com:8080/storage/card/2329/card_img/24838/2329card.png', 'ACTIVE'),
       (16, '하나카드', '하나 Multi Any(멀티 애니) 카드', 'CREDIT', 12000, NULL, 0,
        'https://api.card-gorilla.com:8080/storage/card/2070/card_img/22409/2070card.png', 'ACTIVE'),
       (17, '롯데카드', 'LOCA 365 카드', 'CREDIT', 20000, 35000, 500000,
        'https://api.card-gorilla.com:8080/storage/card/2407/card_img/25732/2407card.png', 'ACTIVE'),
       (18, '신한카드', '신한카드 Air One', 'CREDIT', 49000, NULL, 0,
        'https://api.card-gorilla.com:8080/storage/card/516/card_img/497/516card.png', 'ACTIVE'),
       (19, '하나카드', '하나 JADE Prime', 'CREDIT', 300000, NULL, 500000,
        'https://api.card-gorilla.com:8080/storage/card/2816/card_img/30671/2816card.png', 'ACTIVE'),
       (20, 'IBK기업은행', 'IBK 무민카드(신용)', 'CREDIT', 10000, 40000, 500000,
        'https://api.card-gorilla.com:8080/storage/card/1954/card_img/21264/1954card.png', 'ACTIVE');

-- 카드 (체크카드)
INSERT INTO cards (id, issuer, name, card_type, annual_fee, total_limit, min_performance, card_image_url, status)
VALUES (21, '케이뱅크', 'ONE 체크카드', 'CHECK', 0, NULL, 0,
        'https://api.card-gorilla.com:8080/storage/card/2789/card_img/30286/2789card.png', 'ACTIVE'),
       (22, 'KB국민카드', '노리2 체크카드(KB Pay)', 'CHECK', 0, 50000, 200000,
        'https://api.card-gorilla.com:8080/storage/card/2401/card_img/25691/2401card.png', 'ACTIVE'),
       (23, '토스뱅크', '토스뱅크 체크카드', 'CHECK', 0, NULL, 0,
        'https://api.card-gorilla.com:8080/storage/card/2126/card_img/23049/2126card.png', 'ACTIVE'),
       (24, '신한카드', '신한카드 SOL트래블 체크', 'CHECK', 0, NULL, 300000,
        'https://api.card-gorilla.com:8080/storage/card/2811/card_img/30623/2811card.png', 'ACTIVE'),
       (25, 'NH농협카드', 'NH20 해봄 체크카드', 'CHECK', 0, 35000, 200000,
        'https://api.card-gorilla.com:8080/storage/card/141/card_img/111/141card.png', 'ACTIVE'),
       (26, '우리카드', '카드의정석 오하쳌(오늘하루체크)', 'CHECK', 0, 30000, 200000,
        'https://api.card-gorilla.com:8080/storage/card/2103/card_img/22811/2103card.png', 'ACTIVE'),
       (27, 'KB국민카드', '노리체크카드', 'CHECK', 0, 50000, 200000,
        'https://api.card-gorilla.com:8080/storage/card/56/card_img/26/56card.png', 'ACTIVE'),
       (28, '신한카드', '신한카드 Deep Dream 체크', 'CHECK', 0, NULL, 0,
        'https://api.card-gorilla.com:8080/storage/card/159/card_img/130/159card.png', 'ACTIVE'),
       (29, '우리BC카드', 'BC 바로 페이백 체크', 'CHECK', 0, NULL, 0,
        'https://api.card-gorilla.com:8080/storage/card/2702/card_img/29424/2702card.png', 'ACTIVE'),
       (30, 'KB국민카드', 'KB Youth Club 체크카드', 'CHECK', 0, 20000, 200000,
        'https://api.card-gorilla.com:8080/storage/card/2884/card_img/31608/2884card.png', 'ACTIVE'),
       (31, '우리BC카드', 'K-패스 체크카드', 'CHECK', 0, NULL, 200000,
        'https://api.card-gorilla.com:8080/storage/card/2836/card_img/30894/2836card.png', 'ACTIVE'),
       (32, '신한카드', '신한카드 처음 체크', 'CHECK', 0, 20000, 200000,
        'https://api.card-gorilla.com:8080/storage/card/2847/card_img/31046/2847card.png', 'ACTIVE'),
       (33, '하나카드', '트래블로그 체크카드', 'CHECK', 0, NULL, 0,
        'https://api.card-gorilla.com:8080/storage/card/2403/card_img/25695/2403card.png', 'ACTIVE'),
       (34, 'KB국민카드', '트래블러스 체크카드', 'CHECK', 0, NULL, 0,
        'https://api.card-gorilla.com:8080/storage/card/2827/card_img/30800/2827card.png', 'ACTIVE'),
       (35, '하나카드', '비바 G 플래티늄 체크카드', 'CHECK', 0, NULL, 200000,
        'https://api.card-gorilla.com:8080/storage/card/75/card_img/45/75card.png', 'ACTIVE'),
       (36, '우리BC카드', 'BC 바로 앤(&) 체크', 'CHECK', 0, NULL, 0,
        'https://api.card-gorilla.com:8080/storage/card/2372/card_img/25333/2372card.png', 'ACTIVE'),
       (37, 'IBK기업은행', 'I-알뜰교통플러스 체크카드', 'CHECK', 0, NULL, 200000,
        'https://api.card-gorilla.com:8080/storage/card/2569/card_img/27917/2569card.png', 'ACTIVE'),
       (38, '신한카드', '카카오페이 신한 체크카드', 'CHECK', 0, 15000, 200000,
        'https://api.card-gorilla.com:8080/storage/card/147/card_img/118/147card.png', 'ACTIVE'),
       (39, '롯데카드', 'LOCA LIKIT 체크', 'CHECK', 0, 10000, 200000,
        'https://api.card-gorilla.com:8080/storage/card/2246/card_img/24056/2246card.png', 'ACTIVE'),
       (40, '하나카드', '하나 네이버페이 머니 체크카드', 'CHECK', 0, NULL, 0,
        'https://api.card-gorilla.com:8080/storage/card/2668/card_img/28994/2668card.png', 'ACTIVE');

-- 혜택 (신용카드 1-10)
INSERT INTO card_benefits (card_id, category_id, discount_type, discount_value, monthly_limit, priority)
VALUES
-- 1. 신한카드 Mr.Life
(1, 10, 'RATE', 10.0, 10000, 1),
(1, 2, 'RATE', 10.0, 10000, 2),
(1, 5, 'RATE', 10.0, 10000, 3),

-- 2. 삼성카드 taptap O
(2, 1, 'RATE', 50.0, 10000, 1),
(2, 4, 'RATE', 7.0, 5000, 2),
(2, 3, 'RATE', 10.0, 5000, 3),

-- 3. 삼성카드 & MILEAGE PLATINUM
(3, 12, 'AMOUNT', 1.0, NULL, 1),

-- 4. KB국민 My WE:SH 카드
(4, 2, 'RATE', 10.0, 5000, 1),
(4, 4, 'RATE', 10.0, 5000, 2),
(4, 7, 'RATE', 30.0, 5000, 3),

-- 5. 신한카드 Deep Oil
(5, 6, 'RATE', 10.0, 15000, 1),
(5, 5, 'RATE', 5.0, 5000, 2),

-- 6. 현대카드 ZERO Edition3
(6, 0, 'RATE', 0.8, NULL, 1),

-- 7. 올바른 FLEX 카드
(7, 1, 'RATE', 50.0, 10000, 1),
(7, 4, 'RATE', 5.0, 5000, 2),

-- 8. LOCA LIKIT 1.2
(8, 0, 'RATE', 1.2, NULL, 1),

-- 9. 카드의정석 EVERY 1
(9, 0, 'RATE', 1.0, NULL, 1),

-- 10. 신한카드 처음
(10, 2, 'RATE', 5.0, 10000, 1),
(10, 4, 'RATE', 5.0, 10000, 2);

-- 혜택 (신용카드 11-20)
INSERT INTO card_benefits (card_id, category_id, discount_type, discount_value, monthly_limit, priority)
VALUES
-- 11. 신한카드 Deep Dream
(11, 0, 'RATE', 0.7, NULL, 1),
(11, 5, 'RATE', 2.1, NULL, 2),

-- 12. 현대카드 M Edition3
(12, 0, 'RATE', 1.5, NULL, 1),

-- 13. BC 바로 클리어 플러스
(13, 2, 'RATE', 7.0, 10000, 1),
(13, 3, 'RATE', 7.0, 10000, 2),

-- 14. KB국민 다담 카드
(14, 3, 'RATE', 10.0, 5000, 1),
(14, 13, 'RATE', 10.0, 5000, 2),

-- 15. 삼성 ID ON 카드
(15, 2, 'RATE', 10.0, 10000, 1),
(15, 4, 'RATE', 3.0, 10000, 2),

-- 16. 하나 Multi Any
(16, 4, 'RATE', 1.0, NULL, 1),
(16, 5, 'RATE', 2.0, NULL, 2),

-- 17. LOCA 365 카드
(17, 10, 'AMOUNT', 5000, 5000, 1),
(17, 10, 'AMOUNT', 5000, 5000, 2),

-- 18. 신한카드 Air One
(18, 12, 'AMOUNT', 1.0, NULL, 1),

-- 19. 하나 JADE Prime
(19, 0, 'RATE', 1.0, NULL, 1),
(19, 6, 'RATE', 5.0, 20000, 2),

-- 20. IBK 무민카드
(20, 1, 'RATE', 20.0, 10000, 1),
(20, 7, 'RATE', 20.0, 10000, 2);

-- 혜택 (체크카드 21-40)
INSERT INTO card_benefits (card_id, category_id, discount_type, discount_value, monthly_limit, priority)
VALUES
-- 21. 케이뱅크 ONE 체크카드
(21, 0, 'RATE', 1.1, NULL, 1),

-- 22. 노리2 체크카드(KB Pay)
(22, 1, 'RATE', 10.0, 3000, 1),
(22, 2, 'AMOUNT', 1000, 5000, 2),

-- 23. 토스뱅크 체크카드
(23, 1, 'AMOUNT', 500, NULL, 1),
(23, 3, 'AMOUNT', 100, NULL, 2),

-- 24. 신한카드 SOL트래블 체크
(24, 11, 'RATE', 1.0, NULL, 1),
(24, 5, 'RATE', 5.0, 3000, 2),

-- 25. NH20 해봄 체크카드
(25, 4, 'RATE', 5.0, 5000, 1),
(25, 9, 'RATE', 5.0, 5000, 2),

-- 26. 카드의정석 오하쳌
(26, 4, 'RATE', 5.0, 3000, 1),
(26, 7, 'RATE', 5.0, 3000, 2),

-- 27. 노리체크카드
(27, 3, 'RATE', 10.0, 5000, 1),
(27, 1, 'RATE', 20.0, 5000, 2),

-- 28. 신한카드 Deep Dream 체크
(28, 0, 'RATE', 0.2, NULL, 1),
(28, 5, 'RATE', 0.6, NULL, 2),

-- 29. BC 바로 페이백 체크
(29, 0, 'RATE', 0.2, NULL, 1),

-- 30. KB Youth Club 체크카드
(30, 7, 'RATE', 50.0, 5000, 1),
(30, 4, 'RATE', 30.0, 5000, 2),

-- 31. 우리 K-패스 체크카드
(31, 3, 'RATE', 10.0, 3000, 1),

-- 32. 신한카드 처음 체크
(32, 2, 'RATE', 5.0, 5000, 1),
(32, 5, 'RATE', 5.0, 5000, 2),

-- 33. 트래블로그 체크카드
(33, 11, 'RATE', 100.0, NULL, 1),

-- 34. 트래블러스 체크카드
(34, 11, 'RATE', 1.0, NULL, 1),

-- 35. 비바 G 플래티늄 체크카드
(35, 11, 'RATE', 1.5, NULL, 1),

-- 36. BC 바로 앤(&) 체크
(36, 0, 'RATE', 0.3, NULL, 1),

-- 37. I-알뜰교통플러스 체크카드
(37, 3, 'AMOUNT', 250, NULL, 1),

-- 38. 카카오페이 신한 체크카드
(38, 3, 'RATE', 3.0, 5000, 1),
(38, 13, 'RATE', 3.0, 5000, 2),

-- 39. LOCA LIKIT 체크
(39, 1, 'RATE', 10.0, 3000, 1),
(39, 4, 'RATE', 5.0, 3000, 2),

-- 40. 하나 네이버페이 머니 체크카드
(40, 0, 'RATE', 1.2, 10000, 1);
