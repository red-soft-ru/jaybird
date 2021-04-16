# Jaybird
Jaybird is a JDBC driver suite to connect to Firebird/Red Database servers.

[![image](https://img.shields.io/badge/red--soft--bintray-jaybird--releases-brightgreen)](https://bintray.com/beta/#/red-soft-ru/releases)

## Reporting bugs or improvements

For security vulnerabilities, see [Security Policy](https://github.com/FirebirdSQL/jaybird/security/policy).

For bugs or improvement, go to our [Issue tracker](http://tracker.firebirdsql.org/browse/JDBC).

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

[![image](https://img.shields.io/badge/red--soft--nexus-releases-brightgreen)](http://nexus.red-soft.ru/repository/jaybird/ru/red-soft/jdbc/jaybird-parent/maven-metadata.xml)

## Resources

- [Downloads](https://www.firebirdsql.org/en/jdbc-driver/)
- [Issue tracker](http://tracker.firebirdsql.org/browse/JDBC)
- [Wiki](https://github.com/FirebirdSQL/jaybird/wiki)
- [FAQ](src/documentation/faq.md)
- [Release notes](src/documentation/release_notes.md)
- [How to contribute](CONTRIBUTING.md)
- [Red Soft](https://www.red-soft.ru)
- [Red Database](https://reddatabase.ru)

## License

Jaybird is licensed under LGPL 2.1 or later, with extension interfaces licensed
under the BSD 3-clause. See source headers for the specific license.

`SPDX-License-Identifier: LGPL-2.1-or-later AND BSD-3-Clause`

## Support

Looking for professional support of Jaybird? Jaybird is now part of the [Tidelift subscription](https://tidelift.com/subscription/pkg/maven-org-firebirdsql-jdbc-jaybird?utm_source=maven-org-firebirdsql-jdbc-jaybird&utm_medium=referral&utm_campaign=readme).

## Sponsors

The Firebird JDBC team would like to thank YourKit for providing licenses to their [YourKit Java Profiler](https://www.yourkit.com/java/profiler/). 
![YourKit](https://www.yourkit.com/images/yklogo.png)
