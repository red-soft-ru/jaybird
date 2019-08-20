CREATE GLOBAL MAPPING GSS_AUTH USING PLUGIN GSS FROM ANY USER TO USER;
commit;

create user "ARTYOM.SMIRNOV@RED-SOFT.RU" password '1q2w3e4r' using plugin GostPassword_Manager;
commit;

create user "RDB_SERVER/LOCALHOST" password '1q2w3e4r';
commit;

create user effective_user password 'effective';
commit;

create user trusted_user password 'trusted' using plugin GostPassword_Manager;
commit;

create user UserWithGostPassword password 'password' using plugin GostPassword_Manager;
commit;

create policy TestPolicy AS
AUTH_FACTORS = (certificate, gostpassword),
PSWD_NEED_CHAR = 6,
PSWD_NEED_DIGIT = 2,
PSWD_MIN_LEN = 8,
PSWD_NEED_DIFF_CASE = true,
PSWD_VALID_DAYS = 15,
PSWD_UNIQUE_COUNT = 5,
MAX_FAILED_COUNT = 5,
MAX_SESSIONS = 10,
MAX_IDLE_TIME = 1800,
MAX_UNUSED_DAYS = 45;
commit;

grant policy "DEFAULT" to "RDB_SERVER/LOCALHOST";
commit;

grant policy "DEFAULT" to SYSDBA;
commit;

grant policy "DEFAULT" to trusted_user;
commit;

grant policy "DEFAULT" to UserWithGostPassword;
commit;

grant policy "DEFAULT" to effective_user;
commit;

grant policy TestPolicy to "ARTYOM.SMIRNOV@RED-SOFT.RU";
commit;

alter user "ARTYOM.SMIRNOV@RED-SOFT.RU" set password 'q3rgu7Ah' using plugin GostPassword_Manager;
commit;
