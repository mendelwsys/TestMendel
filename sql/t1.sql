Определяет, сколько заявок определенного субпродукта не дошло до шага верификации в текущий момент (субпродукт определяется пользователем);
Субпродуктов данной категории не дошло до шага верификации в текущий момент(Тест 1);

SELECT T_TL.total - T_SUB.cnt as res FROM  
(SELECT count(*) as total FROM T_START WHERE subproduct=:SUBPRODUCT ) as T_TL , 
(SELECT count(*) as cnt FROM T_START st INNER JOIN T_BEGIN tb ON 
st.app_num=tb.app_num AND st.subproduct=:SUBPRODUCT AND step_id='VRF') as T_SUB;
