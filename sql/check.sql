Дубликаты номеров заявок;
Дубликаты номеров заявок;

select app_num, count(*) from T_START group by app_num HAVING count(*)>1;