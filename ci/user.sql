CREATE GLOBAL MAPPING MULTIFACTOR_AUTH USING PLUGIN MULTIFACTOR FROM ANY USER TO USER;
commit;
create user "SYSDBA" password 'masterkey';
commit;
create user "ARTYOM.SMIRNOV@RED-SOFT.RU" password 'q3rgu7Ah' using plugin Multifactor_Manager;
commit;
