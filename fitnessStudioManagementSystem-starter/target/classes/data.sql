INSERT INTO member (name,
                    gender,
                    birth_date,
                    email,
                    membership_tier,
                    created_at)
VALUES ('Ishaan Kapoor', 'MALE', '1991-04-14', 'ishaan.kapoor@example.com', 'PREMIUM', CURRENT_TIMESTAMP),
       ('Meera Nambiar', 'FEMALE', '1996-09-03', 'meera.nambiar@example.com', 'BASIC', CURRENT_TIMESTAMP),
       ('Arjun Deshmukh', 'MALE', '1987-01-22', 'arjun.deshmukh@example.com', 'VIP', CURRENT_TIMESTAMP),
       ('Pooja Banerjee', 'FEMALE', '1993-10-27', 'pooja.banerjee@example.com', 'PREMIUM', CURRENT_TIMESTAMP),
       ('Yash Malhotra', 'MALE', '1998-06-08', 'yash.malhotra@example.com', 'BASIC', CURRENT_TIMESTAMP),
       ('Riya Choudhary', 'FEMALE', '1994-12-16', 'riya.choudhary@example.com', 'VIP', CURRENT_TIMESTAMP),
       ('Kunal Bhatia', 'MALE', '1989-08-05', 'kunal.bhatia@example.com', 'PREMIUM', CURRENT_TIMESTAMP),
       ('Tanya Krishnan', 'FEMALE', '1997-03-30', 'tanya.krishnan@example.com', 'BASIC', CURRENT_TIMESTAMP);

INSERT INTO trainer (name, specialization, email)
VALUES ('Coach Ravi Shetty', 'Strength Training', 'ravi.shetty@example.com'),
       ('Coach Anika Rao', 'Yoga', 'anika.rao@example.com'),
       ('Coach Dev Menon', 'HIIT', 'dev.menon@example.com');
