# v5.0.14

## Improvements

* Remove printing to `System.out` from trace manager. See RS-154172

## Ported from Jaybird

Fix FBStringField.fixPadding to work with codepoints instead of char. GH-770
Fixed: CallableStatement.getXXX(String) may return value from wrong column. GH-772
Update error messages from FB 5.0.0.1272
jaybird-fbclient 4.0.4.0

# v5.0.13

## Fixed

* Map time zone with GMT offset for V13 protocol only. See RS-150549


# v5.0.12

## Fixed

* Allow the use of parallel workers, starting with Red Database 3.0. See RS-150245


# v5.0.11

## Fixed

* Fix creation of temporary blobs. See RS-150077
* Fix memory release in native OO API implementation of database, statement and service. See RS-150127


# v5.0.10

## Improvements

* Add alias for encrypted password property. See RS-148217


# v5.0.9

## Fixed

* Fix working with time zone set in GMT format for 18 protocol version. See RS-145943


# v5.0.8

## Fixed

* Fix working with time zone set in GMT format. See RS-94039 RS-145787


# v5.0.7

## Improvements

* Add parallel workers option in statistics manager. See RS-98332


## Ported from Jaybird

* Add connection property parallelWorkers for isc_dpb_parallel_workers. GH-737
* Fix DatabaseConnectionProperties.setServerBatchBufferSize ignores serverBatchBufferSize parameter. GH-741
* Support Firebird 5.0 gfix ODS Upgrade option. GH-738
* Add Firebird 5.0 parallel support to BackupManager. GH-739
* Add Firebird 5.0 parallel sweep to MaintenanceManager. GH-740
* Support Firebird 3.0 gfix fix ICU option. GH-744
* Ensure option is only set when parallel workers are supported. GH-739
* Truncation handling of FBStatisticsManager. GH-747
* Mark classes/methods/constants removed in Jaybird 6 as deprecated. GH-759


# v5.0.6

## Fixed

* Do not duplicate connection properties when connecting using protocol v12 (RDB 2.6). See RS-98277


# v5.0.5

## Fixed

* Multifactor authentication with protocol below 13 (RDB 2.6). See RS-98277


# v5.0.4

## Improvements

* Use JDK 8 to build JDK 8 artifacts. See RS-96160


# v5.0.3

## Fixed

* Fix condition to continue authentication in `GostPasswordAuthenticationPlugin`. See RS-95205


# v5.0.2

## Fixed

* Frontported multithreaded backup/restore and sweep via services. See RS-95035
* Add ability to set property for non-encrypted password passing. See RS-95205
* Skip authentication in `GostPassword` plugin if no username and password are set. See RS-95205


# v5.0.1

## Improvements

* Add support for Java 1.8. RS-94949


## Ported from Jaybird

* Fix NPE in result set metadata of connectionless result set. GH-730


# v5.0.0

## Fixed

* Checking statement cursor before closing it. RS-89306
* Parameter buffer creation for service when connecting via native interfaces. RS-90026
* Release of statement when prepare call if it is not null. See RS-90056


## Improvements

* Add `Multifactor`, `GostPassword`, `Certificate` and `Gss` authentication plugins to default list. RS-89306
* Add descriptions of missing error codes. RS-87191
* Add message for `bad_trig_BLR` error code. RS-87191
* Simplify code in creating metadata for native OO API statement. RS-24827
* Implement support server-side batch updates for native OO API statement. See RS-24827


## Ported from Jaybird

* Create (minimal) package docs for each package. GH-725
* Review and fix character set usage in various ways to access BLOB SUB_TYPE TEXT. GH-723
* Remove warning about no connection character set. GH-717
* Get execution statistics. GH-716
* Add support for COLUMN_DEF of DatabaseMetaData.getProcedureColumns. GH-715
* DatabaseMetaData.getIdentifierQuoteString() should return " " (space) for connection dialect 1. GH-714
* Support statements longer than 64KB with native/embedded connections. GH-713
* Collapse FBWorkaroundStringField into FBStringField. GH-708
* Generate error messages and SQLstate from Firebird 5.0. GH-707
* Rework optimization of XID retrieval in FBManagedConnection. GH-704
* Wrong value returned from Statement::getUpdateCount, after Statement::getMoreResults call. GH-703
* Replace synchronized blocks with reentrant locks. GH-702
* Find, recover or forget Xids queries at most 10 records. GH-701
* Upgrade JNA to 5.12.1. GH-700
* Remove finalization from JDBC classes. GH-699
* Implement Firebird 4 batch support. GH-695
* Add support for server-side scrolling. GH-693
* Add support for isc_spb_expected_db for service attachments. GH-691
* Provide NativeResourceUnloadWebListener for jakarta.servlet. GH-684
* Replace statement parser for generated keys to remove dependency on ANTLR. GH-680
* Default to using stream blobs. GH-679
* Add support for Base 64 URL safe encoding for dbCryptConfig. GH-677
* Support multi-row RETURNING for getGeneratedKeys. GH-675
* Support nbackup fixup and preserve sequence option. GH-673
* Support database creation with force write disabled. GH-671
* When closing a statement, we may send DSQL_close before DSQL_drop [JDBC639]. GH-669
* Optimization of ResultSet.next() [JDBC633]. GH-663
* Add field type information (or the field class name) in TypeConversionExceptions [JDBC626]. GH-656
* Remove dependency on JCA [JDBC618]. GH-649
* Remove UDF support for JDBC escapes [JDBC617]. GH-648
* Remove constants, methods, etc deprecated for removal in Jaybird 5 [JDBC616]. GH-647
* Drop Java 7 support [JDBC614]. GH-645
* Add support for Chacha wire encryption plugin [JDBC613]. GH-644
* Improve cursor close handling [JDBC573]. GH-604
* Break up DatabaseMetaData implementation [JDBC558]. GH-591
