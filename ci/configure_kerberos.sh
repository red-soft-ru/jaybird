#!/bin/bash
set -e
set -x

cat << EOF > /var/kerberos/krb5kdc/kdc.conf
[kdcdefaults]
 kdc_ports = 88
 kdc_tcp_ports = 88

[realms]
 RDB.EXAMPLE.COM = {
  master_key_type = aes128-cts
  acl_file = /var/kerberos/krb5kdc/kadm5.acl
  dict_file = /usr/share/dict/words
  admin_keytab = /var/kerberos/krb5kdc/kadm5.keytab
  #supported_enctypes = aes256-cts:normal aes128-cts:normal des3-hmac-sha1:normal arcfour-hmac:normal des-hmac-sha1:normal des-cbc-md5:normal des-cbc-crc:normal
  supported_enctypes = aes128-cts:normal des3-hmac-sha1:normal arcfour-hmac:normal des-hmac-sha1:normal des-cbc-md5:normal des-cbc-crc:normal
 }
EOF

cat << EOF > /etc/krb5.conf
[logging]
 default = FILE:/var/log/krb5libs.log
 kdc = FILE:/var/log/krb5kdc.log
 admin_server = FILE:/var/log/kadmind.log

[libdefaults]
 default_realm = RDB.EXAMPLE.COM
 dns_lookup_realm = false
 dns_lookup_kdc = false
 ticket_lifetime = 24h
 renew_lifetime = 7d
 forwardable = true

[realms]
 RDB.EXAMPLE.COM = {
  kdc = localhost
  admin_server = localhost
 }

[domain_realm]
 .example.com = RDB.EXAMPLE.COM
 example.com = RDB.EXAMPLE.COM
EOF

echo '*/admin@RDB.EXAMPLE.COM *' > /var/kerberos/krb5kdc/kadm5.acl

cat << EOF > /tmp/expect
#!/usr/bin/expect -f
spawn kdb5_util create -r RDB.EXAMPLE.COM -s
expect "Enter KDC database master key: "
send -- "12345\n"
expect "Re-enter KDC database master key to verify: "
send -- "12345\n"
expect eof
EOF

chmod +x /tmp/expect
/tmp/expect

cat << EOF > /tmp/expect
#!/usr/bin/expect -f
spawn kadmin.local addprinc rdb_server/admin
expect "Enter password for principal \"rdb_server/admin@RDB.EXAMPLE.COM\": "
send -- "12345\n"
expect "Re-enter password for principal \"rdb_server/admin@RDB.EXAMPLE.COM\":"
send -- "12345\n"
expect eof
EOF
chmod +x /tmp/expect
/tmp/expect

cat << EOF > /tmp/expect
#!/usr/bin/expect -f
spawn kadmin.local addprinc rdb_server/localhost
expect "Enter password for principal \"rdb_server/localhost@RDB.EXAMPLE.COM\": "
send -- "12345\n"
expect "Re-enter password for principal \"rdb_server/localhost@RDB.EXAMPLE.COM\":"
send -- "12345\n"
expect eof
EOF
chmod +x /tmp/expect
/tmp/expect

kadmin.local ktadd -k /var/kerberos/krb5kdc/kadm5.keytab kadmin/admin kadmin/changepw

krb5kdc
kadmind
sleep 5

cat /var/log/krb5kdc.log
cat /var/log/kadmind.log

kadmin.local ktadd -norandkey -k /etc/krb5.keytab rdb_server/localhost

kinit -kt /etc/krb5.keytab rdb_server/localhost@RDB.EXAMPLE.COM
klist
kdestroy
