Определяет, в каком состоянии в данный момент находится определенная заявка (номер заявки определяет пользователь). В результате данной операции, система должна ответить на вопросы ниже (формат вывода по своему усмотрению);
Маршрут заявки(Тест 4);

SELECT tb.step_id as step,tb.time_stamp as T_IN ,te.time_stamp as T_OUT ,te.step_result_num  as RESCODE, te.step_result as txt FROM T_BEGIN as tb LEFT JOIN T_END as te ON te.step_guid = tb.step_guid WHERE tb.app_num = :APP_NUM ORDER BY tb.time_stamp;
