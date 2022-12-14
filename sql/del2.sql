Удаление дубликатов;
Удаление дубликатов: просто, но медленно,для сравнения с del1.sql;
DELETE FROM T_START AS DL WHERE DL.id NOT IN (select min(id) as mn from T_START group by app_num);