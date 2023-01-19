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
