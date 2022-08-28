use lp_master;
drop table originator;
drop table lender;
drop table data_template;
drop table relationship;
drop table deal;

CREATE TABLE originator ( 
  orig_seq SERIAL PRIMARY KEY,
  orig_code VARCHAR(6) NOT NULL,
  orig_name VARCHAR(100),
  orig_desc VARCHAR(255),
  active SMALLINT NOT NULL,
  created TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE lender ( 
  lender_seq SERIAL PRIMARY KEY,
  lender_code VARCHAR(6) NOT NULL,
  lender_name VARCHAR(100),
  lender_desc VARCHAR(255),
  active SMALLINT NOT NULL,
  created TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE data_template ( 
  dt_seq SERIAL PRIMARY KEY,
  lender_code VARCHAR(6) NOT NULL,
  template_code VARCHAR(6) NOT NULL,
  data_template JSON NOT NULL,
  active SMALLINT NOT NULL,
  created TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE relationship( 
  rel_seq SERIAL PRIMARY KEY,
  orig_code VARCHAR(6) NOT NULL,
  lender_code VARCHAR(6) NOT NULL,
  relation_code VARCHAR(6) NOT NULL,
  relation_db VARCHAR(12) NOT NULL,
  active SMALLINT NOT NULL,
  created TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE deal( 
  deal_seq SERIAL PRIMARY KEY,
  relation_code VARCHAR(6) NOT NULL,
  template_code VARCHAR(6) NOT NULL,
  deal_code VARCHAR(6) NOT NULL,  
  deal_desc VARCHAR(255),
  deal_config JSON NOT NULL,
  active SMALLINT NOT NULL,
  created TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);



