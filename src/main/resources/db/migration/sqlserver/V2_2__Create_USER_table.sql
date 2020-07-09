create table JENKINS_USER
(
    ID        VARCHAR(40) identity (1,1) PRIMARY KEY,
    NAME      VARCHAR(20),
    LOGINNAME VARCHAR(20),
    API_TOKEN VARCHAR(20)
);
