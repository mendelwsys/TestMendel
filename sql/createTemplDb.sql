Tables creating;

DROP TABLE T_END;
DROP TABLE T_START;
DROP TABLE T_BEGIN;

CREATE TABLE  T_START (
  id          INTEGER NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1), 
  time_stamp  TIMESTAMP,
  app_num     VARCHAR(15), 
  product     VARCHAR(11), 
  subproduct  VARCHAR(10),
  PRIMARY KEY (id)

);


CREATE TABLE  T_BEGIN (
  id          INTEGER NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1), 
  time_stamp  TIMESTAMP,
  app_num     VARCHAR(20), 
  subproduct  VARCHAR(10), 
  step_id     VARCHAR(5),  
  step_guid   VARCHAR(20),
  PRIMARY KEY (id),
  UNIQUE (step_guid)
);


CREATE TABLE  T_END (
  id          INTEGER NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1), 
  time_stamp        TIMESTAMP,
  step_guid         VARCHAR(20),
  step_result_num   INTEGER,      
  step_result       VARCHAR(200), 
  PRIMARY KEY (id),
  FOREIGN KEY (step_guid)
  REFERENCES T_BEGIN (step_guid)
);



