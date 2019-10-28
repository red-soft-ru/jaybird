CREATE GLOBAL MAPPING MULTIFACTOR_AUTH USING PLUGIN MULTIFACTOR FROM ANY USER TO USER;
commit;
CREATE GLOBAL MAPPING GSS_AUTH USING PLUGIN GSS FROM ANY USER TO USER;
commit;
create user "TEST@RED-SOFT.RU" password 'q3rgu7Ah' using plugin Multifactor_Manager;
commit;
create user trusted_user password 'trusted' using plugin Multifactor_Manager;
commit;
