Определяет номера заявок на этапе принятия решения, которые находятся на нем более указанного количества суток (количество суток определяется пользователем);
Номера заявок на этапе принятия решения более N суток(Тест 3);

SELECT tb.app_num,tb.time_stamp FROM T_BEGIN tb LEFT JOIN T_END te ON tb.step_guid=te.step_guid
 WHERE  tb.step_id='DSC' AND te.id IS NULL AND {fn TIMESTAMPDIFF( SQL_TSI_HOUR, tb.time_stamp, CURRENT_TIMESTAMP) } > :NUM_DAY*24;
