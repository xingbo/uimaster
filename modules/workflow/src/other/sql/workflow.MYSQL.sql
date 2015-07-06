#---------------------------------------
-- Create SQL Generated               --
--                                    --
-- Please DO NOT modify !!            --
-- Generated on 2015-04-24 12:35:03   --
#---------------------------------------

USE <database name>;

-- WF_FLOWENTITY
CREATE TABLE WF_FLOWENTITY
(ID BIGINT(38) NOT NULL AUTO_INCREMENT,
 ENTITYNAME VARCHAR(255),
 CONTENT VARCHAR(255),
 _enable INT(2) DEFAULT 1,
 _version INT(2) DEFAULT 0,
 _starttime TIMESTAMP,
 _endtime TIMESTAMP,
 _optuserid BIGINT(38) DEFAULT 0,
 PRIMARY KEY(ID)
);

-- WF_UIFLOWS
CREATE TABLE WF_UIFLOWS
(ID BIGINT(38) NOT NULL AUTO_INCREMENT,
 NAME VARCHAR(255),
 FLOW VARCHAR(255),
 MODULEITEMID BIGINT(38),
 MODULETYPE INT(2),
 _enable INT(2) DEFAULT 1,
 PRIMARY KEY(ID)
);

