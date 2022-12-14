Определяет пять самых популярных продуктов по заявкам, поступившим в определенный временной интервал (интервал определяется пользователем);
Самые популярные N продуктов за интервал(Тест 2);

SELECT PRODUCT,CNT FROM (
SELECT ROW_NUMBER() OVER () AS R,TR.* FROM 
(
SELECT product,COUNT(*) as cnt FROM T_START st   
 WHERE time_stamp > TIMESTAMP(:BEGIN_DATE,'00:01:01')  AND 
 time_stamp < TIMESTAMP(:END_DATE,'00:01:01') GROUP BY product 
 ORDER by CNT DESC
) as TR ) as TR1 WHERE TR1.R <= :N_CNT ;
