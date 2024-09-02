Установка соединения
========================

``Jaybird`` представляет собой ``JDBC-драйвер`` для ``Firebird`` и Ред Базы Данных. 
Поддерживает два способа подключения: через базовый сервис для управления набором драйверов ``java.sql.DriverManager`` и через интерфейс ``javax.sql.DataSource``.

Установка соединения c помощью DriverManager
-------------------------------------------------

Первым способом установки соединений в ``Java`` является ``java.sql.DriverManager``. Он основан на ``JDBC`` ``URL-строке``,
определяющей базу данных, к которой нужно подключиться.
Затем менеджер драйверов проверяет, какие драйверы (если таковые имеются) могут установить соединение.

Также есть возможность указывать дополнительные параметры соединения, такие как имя пользователя и пароль.

Строка подключения состоит из трёх частей:

.. code-block:: redstatement

	jdbc:<драйвер>:<строка подключения>

Где:

* ``jdbc`` - ``JDBC`` протокол;

* ``драйвер`` - ``firebird`` или ``firebirdsql``- подпротокол ``JDBC``, определяющий драйвер, который необходимо использовать;

* ``строка подключения`` - часть, определяющая базу данных для подключения в формате: ``//<хост>:<порт>/<путь к бд>``.

Пример строки подключения:

.. code-block::

	jdbc:firebird://localhost:3050/c:/database/example.fdb


Пример установки ``JDBC-подключения`` с базой данных ``Firebird``:

.. code-block::

	package hello;

	import java.sql.*;

	public class HelloServer {

		public static void main(String[] args) throws Exception {

			Class.forName("org.firebirdsql.jdbc.FBDriver");

			Connection connection = DriverManager.getConnection(
				"jdbc:firebird://localhost:3050/c:/db/employee.fdb",
				"SYSDBA", "masterkey");

			// do something here
		}
	}

Первая строка этого кода сообщает ``Java`` о необходимости загрузить ``JDBC-драйвер`` ``Jaybird``.
На этом этапе драйвер регистрирует себя в ``java.sql.DriverManager``.

Второй оператор в примере говорит ``java.sql.DriverManager`` открыть соединение базы данных, расположенной по пути ``c:/database/employee.fdb``,
с сервером ``Firebird``, работающим на ``localhost``.

Начиная с ``Java 6`` (``JDBC 4``), явная загрузка драйвера с помощью ``Class.forName("org.firebirdsql.jdbc.FBDriver")`` больше не требуется,
за исключением случаев, когда драйвер не находится по пути системных классов. Примером, когда может потребоваться явная загрузка драйвера,
являются веб-приложения, загружающие драйвер динамически в процессе работы.
В этом случае драйвер не находится по пути системного класса, поэтому его нужно загрузить явно.

В следующих примерах будет использоваться ``Class.forName``, они будут работать благодаря автоматической загрузке драйвера.

.. rubric:: Регистрация JDBC-драйвера

Есть несколько способов регистрации ``JDBC-драйвера``:

#. ``DriverManager`` загружает драйверы из системного каталога классов. Это происходит автоматически.
#. Приложение явно загружает класс драйвера. Это необходимо только в том случае, если автоматическая загрузка недоступна, например из-за того,
   что драйвер загружается динамически.

   Спецификация ``JDBC`` требует, чтобы во время инициализации класса драйвер зарегистрировал себя в ``DriverManager``.

   .. code-block::

	 Class.forName("org.firebirdsql.jdbc.FBDriver");

#. Имя драйвера ``JDBC`` указывается в системном свойстве ``jdbc.drivers``. Несколько драйверов разделяются двоеточием.
   Значение для этого свойства можно при запуске ``JVM``:

   .. code-block::

	 java\
	    -Djdbc.drivers=org.firebirdsql.jdbc.FBDriver\
		-classpath jaybird-jdk11-5.0.1.jar;C:/myproject/classes\
		my.company.SomeJavaExample

Спецификация соединения состоит из хоста сервера базы данных, опционально можно указать порт (по умолчанию используется порт 3050).
В качестве хоста можно указать либо его ``DNS-имя`` (например, ``fb-server.mycompany.com`` или просто ``fb-server``),
либо его ``IP-адрес`` (например, ``192.168.0.5`` или ``[1080::8:800:200C:417A]`` для ``IPv6-адресов``).

После имени сервера и порта указывается псевдоним или путь к базе данных. Рекомендуется указывать псевдоним базы данных вместо абсолютного пути к ней.

Формат пути зависит от платформы, на которой работает сервер.

В ``Windows`` путь должен включать диск и путь, например: ``c:/database/employee.fdb``.

В ``Unix`` путь должен включать корень, поскольку в противном случае путь интерпретируется относительно папки сервера.
Необходимость включать корень приводит к тому, что для базы данных в каталоге ``/var/firebird/employee.fdb``
нужно использовать ``//`` после имени хоста (и порта) в строке подключения: ``jdbc:firebird://localhost//var/firebird/employee.fdb``.

``Java`` поддерживает либо ``/``, либо ``\`` (экранированный как ``\\\``) в качестве разделителя в пути на ``Windows``.
В ``Unix`` и ``Linux`` в качестве разделителя можно использовать только ``/``.

Дополнительные параметры соединения
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

``JDBC API`` предоставляет метод, с помощью которого можно указать дополнительные параметры соединения.

Пример подключения с дополнительными параметрами:

.. code-block::

	package hello;

	import java.sql.*;
	import java.util.*;

	public class HelloServerWithEncoding {

		public static void main(String[] args) throws Exception {
			Properties props = new Properties();

			props.setProperty("user", "SYSDBA");
			props.setProperty("password", "masterkey");
			props.setProperty("encoding", "UTF8");

			try (Connection connection = DriverManager.getConnection(
					"jdbc:firebird://localhost:3050/C:/db/employee.fdb",
					props)) {

				// do something here

			}
		}
	}

Параметры ``user`` и ``password`` определены в спецификации ``JDBC``. Все остальные параметры являются специфичными для каждого драйвера.

Дополнительные параметры подключения можно добавить, дописав их в объект ``Properties``.
Список параметров, доступных для ``Jaybird``, можно найти в разделе :ref:`Extended connection properties`.

``Jaybird`` также позволяет указать дополнительные параметры в ``JDBC URL``. Синтаксис строки подключения с дополнительными параметрами:

.. code-block:: redstatement

	jdbc:firebird://<хост>[:<порт>]/<путь к бд>?<параметры подключения>

	<параметры подключения> ::= <свойство>[{& | ;}<свойство>]

	<свойство>   ::= <имя>[=<значение>]

В этом случае дополнительные параметры передаются использованием ``HTTP-подобной`` схемы передачи параметров:
сначала идет основная часть ``URL``, затем ``?``, затем пары ``имя-значение``, разделенные символами ``&`` или ``;``.

Пример определения дополнительных параметров в строке подключения:

.. code-block::

	import java.sql.*;

	...

	Connection connection = DriverManager.getConnection(
		"jdbc:firebird://localhost:3050/C:/db/employee.fdb?encoding=UTF8",
		"SYSDBA",
		"masterkey");

Кодировка в части запроса JDBC URL
""""""""""""""""""""""""""""""""""""""""""

Значения (и ключи) в кодировке ``UTF-8`` можно использовать в части запроса ``JDBC URL``.

В результате этого изменения следующие ранее не поддерживаемые символы могут быть использованы в значении свойства соединения, если они экранированы:

* Символ ``;`` экранируется как ``%3B``;
* Символ ``&`` экранируется как ``%26``.

Следующие символы также должны быть экранированы:

* Знак ``+`` в части запроса указывается как пробел (``0x20``) и должен быть экранирован как ``%2B``;
  необходимо убедиться, что это сделано для значений ``dbCryptConfig`` в кодировке ``base64`` (можно использовать кодировку ``base64url`` вместо этого);

* Символ ``%`` в части запроса теперь представляет собой ``escape-символ``, поэтому ``%`` нужно экранировать как ``%25``;

Кодировка также может быть использована для любого символа юникода. ``Jaybird`` всегда будет использовать ``UTF-8`` для декодирования.

Некорректные значения в кодировке ``URL`` будут вызывать исключение ``SQLNonTransientConnectionException``.

Поддержка кодировки ``URL`` применяется только к части ``JDBC URL`` после первого ``?``.
Кодировка ``URL`` не должна применяться для параметров соединения, заданных через ``java.util.Properties или на javax.sql.DataSource``.

Установка соединения с помощью javax.sql.DataSource
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

Интерфейс ``javax.sql.DataSource`` описывает простой ``API`` для создания объектов ``java.sql.Connection``.
Источники данных могут быть созданы и настроены с помощью самоанализа кода или компонента, найдены в ``JNDI`` или внедрены с помощью ``CDI`` или ``Spring``.

Сам ``Jaybird`` предоставляет одну реализацию ``javax.sql.DataSource`` - ``org.firebirdsql.ds.FBSimpleDataSource``, которая представляет собой простую фабрику соединений,
без пула соединений.

Пример создания источника данных и получения соединения через объект ``DataSource``:

.. code-block::

	package hello;

	import java.sql.*;
	import org.firebirdsql.ds.*;

	public class HelloServerDataSource {

		public static void main(String[] args) throws Exception {
			var ds = new FBSimpleDataSource();
			ds.setUser("SYSDBA");
			ds.setPassword("masterkey");
			// in a single property
			ds.setDatabaseName("//localhost:3050/C:/database/employee.fdb");
			// or split out over serverName, portNumber and databaseName
			ds.setServerName("localhost");
			ds.setPortNumber(3050);
			ds.setDatabaseName("C:/database/employee.fdb");

			try (Connection connection = ds.getConnection()) {
			// do something here...
			}
		}
	}

Использование ``JNDI`` для поиска источника данных ``javax.sql.DataSource``
"""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""

В спецификации ``JDBC 2.0`` появился механизм получения соединений с базами данных, не требующий от приложения знания специфики основного драйвера ``JDBC``.
Приложению достаточно знать логическое имя, чтобы найти экземпляр интерфейса ``javax.sql.DataSource`` с помощью ``Java Naming and Directory Interface (JNDI)``.
Это был распространенный способ получения соединений в веб-серверах и серверах приложений до появления ``CDI``.

Этот код предполагает, что свойства ``JNDI`` настроены правильно. Для получения дополнительной информации о настройке ``JNDI`` обратитесь к документации,
поставляемой с веб-сервером или сервером приложений.

Типичный способ установки ``JDBC-соединения`` через ``JNDI``:

.. code-block::

	package hello;

	import java.sql.*;
	import javax.sql.*;
	import javax.naming.*;

	public class HelloServerJNDI {

		public static void main(String[] args) throws Exception {
			var ctx = new InitialContext();
			DataSource ds = (DataSource) ctx.lookup("jdbc/SomeDB");

			try (Connection connection = ds.getConnection()) {
			// do something here...
			}
		}
	}

Обычно связь между объектом ``DataSource`` и его ``JNDI-именем`` происходит в конфигурации веб-сервера или сервера приложений.
Однако при некоторых обстоятельствах (например, при разработке собственного сервера приложений/фреймворка с поддержкой ``JNDI``)
может потребоваться сделать это самостоятельно. Для этого можно использовать данный фрагмент кода:

Программный способ инстанцирования реализации ``javax.sql.DataSource``:

.. code-block::

	import javax.naming.*;
	import org.firebirdsql.ds.*;
	...
	var ds = new FBSimpleDataSource();

	ds.setDatabaseName("//localhost:3050/C:/database/employee.fdb");
	ds.setUser("SYSDBA");
	ds.setPassword("masterkey");

	var ctx = new InitialContext();

	ctx.bind("jdbc/SomeDB", ds);

``DataSource`` поддерживает все параметры подключения, доступные интерфейсу ``DriverManager``.

Типы драйверов
------------------

``Jaybird`` поддерживает несколько реализаций ``GDS API``. Дистрибутив ``Jaybird`` по умолчанию содержит две категории реализаций:
чистая ``Java-реализация`` протокола ``Firebird`` и ``JNA-прокси``, который может использовать библиотеку ``fbclient``.

В следующих разделах представлено описание этих типов и их конфигурации с соответствующими ``JDBC URL``,
которые следует использовать для установки соединения нужного типа. Тип ``JDBC-драйвера`` для ``javax.sql.DataSource`` настраивается через соответствующий параметр в строке подключения. Типы и параметры описаны ниже.

Тип PURE_JAVA
~~~~~~~~~~~~~~~~~~~~~~

Тип ``PURE_JAVA`` (``JDBC Type 4``) использует чистую ``Java-реализацию`` протокола передачи данных ``Firebird``.
Этот тип рекомендуется для подключения к удаленному серверу баз данных с помощью ``TCP/IP``.
Установка не требуется, кроме добавления драйвера ``JDBC`` в путь загрузчика классов.
Этот тип драйвера обеспечивает наилучшую производительность при подключении к удаленному серверу.

Для установки соединения с помощью драйвера типа ``PURE_JAVA`` необходимо использовать ``JDBC URL``, как показано в разделе `Установка соединения c помощью DriverManager`_.

Поддерживается следующий синтаксис ``JDBC URL`` (``serverName`` стало необязательным в ``Jaybird 5``):

.. code-block:: redstatement

	<pure-java-url> ::=
	    jdbc:firebird[sql]:[java:]<database-coordinates>

	<database-coordinates> ::=
	    //[serverName[:portNumber]]/databaseName
	  | <legacy-url>

	<legacy-url> ::=
	    [serverName[/portNumber]:]databaseName

Если ``serverName`` не указан, по умолчанию используется ``localhost``.
Если ``portNumber`` не указан, по умолчанию используется значение 3050.

При использовании ``javax.sql.DataSource`` тип ``PURE_JAVA`` используется по умолчанию.

Примеры использования ``PURE_JAVA``:

.. code-block::

	// Connect to db alias employee on localhost, port 3050
	jdbc:firebird://localhost/employee
	jdbc:firebird://localhost:3050/employee
	jdbc:firebird:///employee

	// Same using the legacy URL format
	jdbc:firebird:localhost:employee
	jdbc:firebird:localhost/3050:employee
	jdbc:firebird:employee

Типы NATIVE и LOCAL
~~~~~~~~~~~~~~~~~~~~~~~~~~

Типы ``NATIVE`` и ``LOCAL`` (``JDBC Type 2``) используют ``JNA-прокси`` для доступа к клиентской библиотеке ``Firebird`` и требуют установки клиента ``Firebird``.
Драйвер ``NATIVE`` используется для доступа к удаленному серверу базы данных, ``LOCAL`` (только для ``Windows``) получает доступ к серверу базы данных,
работающему на том же хосте, посредством ``IPC`` (``Inter-Process Communication``). Производительность драйвера ``NATIVE`` примерно на 10% ниже по сравнению с драйвером ``PURE_JAVA``,
но производительность типа ``LOCAL`` до 30% выше по сравнению с ``PURE_JAVA`` при подключении к серверу на том же хосте. В основном это связано с тем,
что в этом режиме не задействован стек ``TCP/IP``.

Чтобы создать соединение с помощью драйвера ``NATIVE`` для подключения к удаленному серверу, необходимо использовать следующий ``JDBC URL`` с подпротоколом ``native``.

Поддерживается следующий синтаксис ``JDBC URL``:

.. code-block:: redstatement

	<native-url> ::=
	  jdbc:firebird[sql]:native:<database-coordinates>

	<database-coordinates> ::=
	  //[serverName[:portNumber]]/databaseName
	| <fbclient-url>

	<fbclient-url>
	  inet://serverName[:portNumber]/databaseName
	| inet4://serverName[:portNumber]/databaseName
	| inet6://serverName[:portNumber]/databaseName
	| wnet://[serverName[:portNumber]/]databaseName
	| xnet://databaseName
	| [serverName[/portNumber]:]databaseName

Начиная с ``Jaybird 5``, можно использовать все ``URL``, поддерживаемые ``fbclient``. Поддерживаемые ``URL`` зависят от версии ``fbclient`` и ОС
(например, ``XNET`` и ``WNET`` поддерживаются только ``Windows``, а поддержка ``WNET`` удалена в ``Firebird 5``).

При подключении к локальному серверу баз данных с помощью драйвера ``LOCAL`` следует использовать следующее:

.. code-block:: redstatement

	jdbc:firebird:local:<абсолютный путь к бд>

Помимо ``Jaybird``, для этого требуется собственная клиентская библиотека ``Firebird``, а библиотека ``JNA 5.12+`` должна быть добавлена в путь загрузчика классов.

Протокол ``LOCAL`` был удален в ``Jaybird 5``, и теперь это просто псевдоним для ``NATIVE``. 
Чтобы обеспечить локальный доступ, необходимо использовать строку соединения с ``XNET`` (только для ``Windows``!):


.. code-block:: redstatement

	jdbc:firebird:native:xnet://<путь к бд>

Поддержка такого типа ``URL`` была введена в ``Jaybird 5``, поэтому этот синтаксис не может быть использован в более ранних версиях.

Поскольку ``XNET`` работает только в ``Windows``, на других платформах вместо него необходимо использовать ``EMBEDDED-соединение``.

Примеры ``URL`` с использованием ``NATIVE`` подключения:

.. code-block::

	// Connect to db alias employee on localhost, port 3050
	jdbc:firebird:native://localhost/employee
	jdbc:firebird:native://localhost:3050/employee
	jdbc:firebird:native:///employee

	jdbc:firebird:native:inet://localhost/employee
	// Require IPv4
	jdbc:firebird:native:inet4://localhost/employee
	// Require IPv6
	jdbc:firebird:native:inet6://localhost/employee
	// Using WNET
	jdbc:firebird:native:wnet://localhost/employee
	// Using XNET
	jdbc:firebird:native:xnet://employee

	// Same using the legacy URL format
	jdbc:firebird:native:localhost:employee
	jdbc:firebird:native:localhost/3050:employee
	// May use XNET, INET or embedded access
	jdbc:firebird:native:employee

Windows
""""""""""""

В ``Windows`` необходимо убедиться, что ``fbclient.dll`` находится в переменной окружения ``PATH``. В качестве альтернативы можно указать каталог,
содержащий эту ``DLL``, в системном свойстве ``jna.library.path``.

Например, если поместить копию ``fbclient.dll`` в текущую директорию, то для запуска Java-приложения нужно будет использовать следующую команду:

.. code-block::

	java -cp <relevant claspath> -Djna.library.path=. com.mycompany.MyClass

Если установлена 32-битная ``Java``, нужна 32-битная ``fbclient.dll``, для 64-битной ``Java`` - 64-битная ``fbclient.dll``.

Linux
""""""""""""

В ``Linux`` нужно убедиться, что ``libfbclient.so`` доступен через переменную окружения ``LD_PATH``.

Обычно общие библиотеки хранятся в каталоге ``/usr/lib/``; однако для установки библиотеки туда понадобятся права ``root``.
В некоторых дистрибутивах есть только, например, ``libfbclient.so.2.5``. В этом случае может потребоваться добавить символьную ссылку для ``libfbclient.so`` в операционную систему.

В качестве альтернативы можно указать каталог, содержащий библиотеку, в системном свойстве ``Java`` ``jna.library.path``.

Ограничения
""""""""""""""

Старые версии клиентской библиотеки ``Firebird`` могут быть небезопасны при подключении к локальному серверу баз данных с помощью ``IPC``.
По умолчанию ``Jaybird`` не обеспечивает синхронизацию, но ее можно включить с помощью системного свойства ``org.firebirdsql.jna.syncWrapNativeLibrary``, установленного в ``true``.
Однако эта синхронизация является локальной для загрузчика классов, который загрузил классы ``Jaybird``.

Чтобы обеспечить правильную синхронизацию, драйвер ``Jaybird`` должен быть загружен самым верхним загрузчиком классов.
Например, при использовании драйвера ``JDBC`` с веб-сервером или сервером приложений необходимо добавить классы ``Jaybird`` в основной ``classpath``
(например, в каталог ``lib/`` веб-сервера или сервера приложений), но не в веб-приложение или приложение ``Jave EE/Jakarta EE``, например, в каталог ``WEB-INF/lib``.

Тип EMBEDDED
~~~~~~~~~~~~~~~~~~

Драйвер ``Embedded`` - это драйвер ``JDBC`` типа 2, который вместо использования клиентской библиотеки ``Firebird`` загружает встроенную библиотеку сервера.
Это самый высокопроизводительный тип ``JDBC-драйвера`` для доступа к локальным базам данных, так как ``Java-код`` обращается непосредственно к файлу базы данных.

Поддерживается следующий синтаксис ``JDBC URL``:

.. code-block:: redstatement

	<embedded-url> ::=
	  jdbc:firebird[sql]:embedded:_dbname-or-alias_

На практике ``URL`` принимает те же значения ``<fbclient-url>``, которые описаны для ``NATIVE``.
То есть встроенный сервер выступает в роли клиентской библиотеки (т.е. такое же поведение, как и при использовании ``native``).

Этот драйвер пытается загрузить ``fbembed.dll/libfbembed.so`` (в ``Firebird 2.5`` и более ранних версиях) и ``fbclient.dll/libfbclient.so``.

При использовании ``Firebird 3.0`` и старше нужно  убедиться, что необходимые плагины, такие как ``engineNN.dll/libengineNN.so``
(``NN`` 12 для ``Firebird 3.0``, 13 для ``Firebird 4.0`` и ``Firebird 5.0``), доступны для клиентской библиотеки.

Ограничения
""""""""""""""

Старые версии встроенного сервера ``Firebird 2.1`` и ниже для ``Linux`` не являются потокобезопасными.
``Jaybird`` может обеспечить необходимую синхронизацию в коде ``Java``, как описано для типа ``NATIVE``.
Это подразумевает те же ограничения на загрузчик классов, который будет загружать классы ``Jaybird``.

По умолчанию встроенная библиотека ``Firebird`` открывает базы данных в эксклюзивном режиме.
Это означает, что данная конкретная база данных доступна только для одной виртуальной машины ``Java``.
Это можно изменить с помощью параметра ``ServerMode`` в файле ``firebird.conf``.

Пул соединений
--------------------

Каждый раз, когда соединение устанавливается через ``DriverManager``, открывается новое физическое соединение с сервером. 
Физическое соединение закрывается, когда закрывается ``java-соединение``.

Начиная с ``Jaybird 3`` пул соединений больше не поддерживается.
Если необходима реализация интерфейса ``javax.sql.DataSource``, обеспечивающая пул соединений, 
необходимо использовать пул соединений сервера приложения, либо использовать ``HikariCP``, ``DBCP`` или ``c3p0``.

.. Пример HikariCP
.. """"""""""""""""""

.. В этом примере показано, как настроить ``HikariCP`` для подключения к ``Firebird``:

..
	.. code-block::

.. 	package example;

.. 	import com.zaxxer.hikari.HikariConfig;
.. 	import com.zaxxer.hikari.HikariDataSource;
.. 	import org.firebirdsql.ds.FBSimpleDataSource;

.. 	import java.sql.Connection;
.. 	import java.sql.SQLException;

.. 	public class HikariConnectExample {

.. 		public static void main(String[] args) {
.. 			HikariDataSource hikariDataSource = initDataSource();

.. 			try (Connection connection = hikariDataSource.getConnection()) {
.. 			// use connection
.. 			} catch (SQLException e) {
.. 			System.getLogger("HikariConnectExample")
.. 				.log(System.Logger.Level.ERROR, "Could not connect", e);
.. 			}

.. 			hikariDataSource.close();
.. 		}

.. 		private static HikariDataSource initDataSource() {
.. 			var firebirdDataSource = new FBSimpleDataSource();
.. 			firebirdDataSource.setServerName("localhost");
.. 			firebirdDataSource.setDatabaseName("employee");
.. 			firebirdDataSource.setUser("sysdba");
.. 			firebirdDataSource.setPassword("masterkey");
.. 			firebirdDataSource.setCharSet("utf-8");

.. 			var config = new HikariConfig();
.. 			config.setDataSource(firebirdDataSource);
.. 			return new HikariDataSource(config);
.. 		}
.. 	}

.. ``HikariCP`` предоставляет множество способов настройки соединения. Некоторые примеры:

.. Косвенное использование ``FBSimpleDataSource``:

..
	.. code-block::

.. 	private static HikariDataSource initDataSourceAlternative1() {
.. 		var config = new HikariConfig();
.. 		config.setDataSourceClassName("org.firebirdsql.ds.FBSimpleDataSource");
.. 		config.setUsername("sysdba");
.. 		config.setPassword("masterkey");
.. 		config.addDataSourceProperty("serverName", "localhost");
.. 		config.addDataSourceProperty("databaseName", "employee");
.. 		config.addDataSourceProperty("charSet", "utf-8");
.. 		return new HikariDataSource(config);
.. 	}

.. Использование ``JDBC-драйвера`` ``Jaybird`` вместо ``DataSource``:

..
	.. code-block::

.. 	private static HikariDataSource initDataSourceAlternative2() {
.. 		var config = new HikariConfig();
.. 		config.setDriverClassName("org.firebirdsql.jdbc.FBDriver");
.. 		config.setJdbcUrl("jdbc:firebird://localhost/employee");
.. 		config.setUsername("sysdba");
.. 		config.setPassword("masterkey");
.. 		config.addDataSourceProperty("charSet", "utf-8");
.. 		return new HikariDataSource(config);
.. 	}

Реализация javax.sql.DataSource
---------------------------------------

Реализации пула соединений, предоставляемые сервером приложений ``Java EE/Jakarta EE`` или сторонними библиотеками, представлены в виде интерфейса ``javax.sql.DataSource``.

Наиболее важным методом, предоставляемым этим интерфейсом, является ``getConnection()``, который возвращает соединение, основанное на конфигурации источника данных.
Для обычного (не объединяемого в пул) источника данных это приведет к созданию физического соединения.
Для пула соединений это приведет к созданию логического соединения, которое включает в себя физическое соединение из пула.

Когда приложение завершает работу с соединением, оно должно вызвать ``close()`` для него.
Соединение, источник данных которого не добавлен в пул, будет закрыто.
Для логического соединения из пула соединений функция ``close()`` отменит логическое соединение (что сделает его похожим на закрытое соединение)
и вернет физическое соединение в пул соединений, где оно будет либо сохранено для повторного использования, либо закрыто.

.. note::

	Лучше использовать соединение в течение минимального периода времени, необходимого для корректной работы.
	Необходимо закрыть соединение сразу после завершения работы. При использовании пула соединений это дает дополнительное преимущество:
	всего несколько подключений могут удовлетворить потребности приложения.

Реализация javax.sql.ConnectionPoolDataSource
--------------------------------------------------

Интерфейс ``javax.sql.ConnectionPoolDataSource`` позволяет создать объекты ``PooledConnection`` для использования пулом соединений.
Например, серверы приложений поддерживают использование ``ConnectionPoolDataSource`` для наполнения пула соединений.

Объект ``PooledConnection`` представляет физическое соединение с базой данных и является источником логических соединений, которые пул соединений может раздавать приложениям.
Закрытие логического соединения возвращает физическое соединение обратно в пул.

.. warning::

	``ConnectionPoolDataSource`` не является пулом соединений! Это фабрика физических соединений, которые могут быть использованы пулом соединений.

``Jaybird`` предоставляет ``org.firebirdsql.ds.FBConnectionPoolDataSource`` в виде интерфейса ``javax.sql.ConnectionPoolDataSource``.

Реализация javax.sql.XADataSource
--------------------------------------

В спецификации ``JDBC 2.0`` появился интерфейс ``javax.sql.XADataSource``, который используется для доступа к соединениям,
способным выполнять распределенные транзакции с ``JTA-совместимым`` координатором транзакций.
Это дает приложениям возможность использовать двухфазную фиксацию для синхронизации нескольких менеджеров ресурсов.

Как и ``javax.sql.ConnectionPoolDataSource``, приложения обычно не обращаются к ``XADataSource`` напрямую,
вместо этого он используется как фабрика соединений для источника данных с поддержкой ``XA``.
Для приложения он обычно отображается как ``javax.sql.DataSource``.

``Jaybird`` предоставляет ``org.firebirdsql.ds.FBXADataSource`` в виде интерфейса ``javax.sql.XADataSource``.
