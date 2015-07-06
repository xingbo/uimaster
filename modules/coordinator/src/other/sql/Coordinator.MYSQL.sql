#---------------------------------------
-- Create SQL Generated               --
--                                    --
-- Please DO NOT modify !!            --
-- Generated on 2015-06-04 10:37:14   --
#---------------------------------------

USE <database name>;

-- NOTI_TASKS
CREATE TABLE NOTI_TASKS
(ID BIGINT(38) NOT NULL AUTO_INCREMENT,
 PARTYID BIGINT(38),
 SUBJECT VARCHAR(255),
 DESCRIPTION VARCHAR(255),
 TRIGGERTIME DATETIME,
 SENDSMS TINYINT(1),
 SENDEMAIL TINYINT(1),
 STATUS INT(2),
 COMPLETERATE INT(3),
 PRIORITY INT(2),
 _enable INT(2) DEFAULT 1,
 PRIMARY KEY(ID)
);

