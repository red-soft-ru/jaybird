.. raw:: latex

  \appcount

Таблица преобразования типов данных
=========================================

Соответствие между типами JDBC, Firebird и Java
-----------------------------------------------------

В таблице ниже описано отображение типов данных ``JDBC``, определенных в классе ``java.sql.Types``, на типы данных ``Firebird``. 
Также для каждого типа данных ``JDBC`` приведен экземпляр класса, который возвращается методом ``ResultSet.getObject``.

.. tabularcolumns:: |>{\ttfamily\arraybackslash}\X{6}{18}|>{\ttfamily\arraybackslash}\X{6}{18}|>{\ttfamily\arraybackslash}\X{6}{18}|
.. list-table::
   :class: longtable
   :header-rows: 1

   * - Тип JDBC 
     - Тип Firebird 
     - Java Object
   * - CHAR
     - CHAR
     - String
   * - VARCHAR
     - VARCHAR
     - String
   * - LONGVARCHAR
     - BLOB SUB_TYPE TEXT
     - String
   * - NUMERIC
     - NUMERIC
       
       INT128
     - java.math.BigDecimal
   * - DECIMAL
     - DECIMAL
     - java.math.BigDecimal
   * - SMALLINT
     - SMALLINT
     - Integer
   * - INTEGER
     - INTEGER
     - Integer
   * - BIGINT
     - BIGINT
     - Long
   * - REAL
     - [30]
     - 
   * - FLOAT
     - FLOAT
     - Double
   * - DOUBLE
     - DOUBLE PRECISION
     - Double
   * - BINARY
     - CHAR CHARACTER SET OCTETS BINARY
     - byte[]
   * - VARBINARY
     - VARCHAR CHARACTER SET OCTETS VARBINARY
     - byte[]
   * - LONGVARBINARY
     - BLOB SUB_TYPE BINARY
     - byte[]
   * - DATE
     - DATE
     - java.sql.Date
   * - TIME
     - TIME
     - java.sql.Time
   * - TIME_WITH_TIMEZONE
     - TIME WITH TIME ZONE
     - java.time.OffsetTime
   * - TIMESTAMP
     - TIMESTAMP
     - java.sql.Timestamp
   * - TIMESTAMP_WITH_TIMEZONE
     - TIMESTAMP WITH TIME ZONE
     - java.time.OffsetDateTime
   * - BLOB
     - BLOB SUB_TYPE < 0
     - java.sql.Blob
   * - BOOLEAN
     - BOOLEAN
     - Boolean
   * - JaybirdTypeCodes.DECFLOAT
     - DECFLOAT
     - java.math.BigDecimal

