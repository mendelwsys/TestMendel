Удаление дубликатов;
Удаление дубликатов;
DELETE from T_START WHERE id IN 
( select id from T_START AS S,
( select app_num app_n ,min(id) mn,count(*) cnt from T_START group by app_num HAVING count(*)>1 ) AS A 
 WHERE app_n = app_num and id<>mn) ;