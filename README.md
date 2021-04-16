# Jaybird
Jaybird is a JCA/JDBC driver suite to connect to Firebird/Red Database servers.

This driver is based on both the JCA standard for application server connections to enterprise information systems and the well-known JDBC standard.

The JCA standard specifies an architecture in which an application server can cooperate with a driver so that the application server manages transactions, security, and resource pooling, and the driver supplies only the connection functionality. While similar to the JDBC XADataSource concept, the JCA specification is considerably clearer on the division of responsibility between the application server and driver.

## Key Features

The main difference between Red Soft Jaybird driver and the original driver is full Red Database server support. Improved performance compared to the original Jaybird driver for specific tasks. Key supported features:
- External stored procedures;
- Full text search;
- Improved security subsystem.

The driver supports all types of authentication provided by Red Database server. The following authentication methods are supported:
- Secure Password Authentication (Srp);
- Traditional password (Legacy_Auth) authentication;
- Multifactor authentication (Password, Certificate) using security policies;
- Trusted Authentication through the GSSAPI (Gss) Mechanism.

Support for multifactor authentication is implemented using the cryptographic provider CryptoPro.

[![MavenCentral](https://maven-badges.herokuapp.com/maven-central/org.firebirdsql.jdbc/jaybird-jdk18/badge.svg)](https://maven-badges.herokuapp.com/maven-central/org.firebirdsql.jdbc/jaybird-jdk18/) [![image](https://img.shields.io/badge/red--soft--nexus-releases-brightgreen)](http://nexus.red-soft.ru/repository/jaybird/ru/red-soft/jdbc/jaybird-parent/maven-metadata.xml)

## Resources

- [Downloads](http://www.firebirdsql.org/en/jdbc-driver/)
- [Issue tracker](http://tracker.firebirdsql.org/browse/JDBC)
- [Wiki](https://github.com/FirebirdSQL/jaybird/wiki)
- [FAQ](src/documentation/faq.md)
- [Release notes](src/documentation/release_notes.md)
- [How to contribute](CONTRIBUTING.md)
- [Red Soft](https://www.red-soft.ru)
- [Red Database](https://reddatabase.ru)
