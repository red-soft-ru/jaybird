Общие сведения
==================

Логирование 
------------------

``Jaybird`` записывает в журнал различную информацию во время своей работы.

Для ведения журнала ``jaybird`` использует следующие уровни логирования:

* ``trace`` - низкоуровневая отладочная информация;
* ``debug`` - отладочная информация;
* ``info`` - информационные сообщения;
* ``warn`` - предупреждения;
* ``error`` - ошибки;
* ``fatal`` - серьезные/фатальные ошибки (хотя в основном вместо фатальной ошибки будет использоваться уровень ``error``).

Java Platform Logging API
~~~~~~~~~~~~~~~~~~~~~~~~~~~~

Начиная с ``Jaybird 6``, используется ``Java Platform Logging API (JEP 264)``.

По умолчанию ``Java Platform Logging API`` ведет журнал в ``java.util.logging``, но можно подключить различные платформы для ведения журнала. 
Например, ``Log4j`` обеспечивает зависимость ``log4j-jpl``, которая заменит стандартную зависимость от ``java.util.logging`` на зависимость от ``Log4j``.

java.util.logging
~~~~~~~~~~~~~~~~~~~~~~~

По умолчанию ``Jaybird`` использует ``java.util.logging``.

``Jaybird`` применяет следующее сопоставление для уровней логирования:

.. tabularcolumns:: |>{\ttfamily\arraybackslash}\X{6}{14}|>{\ttfamily\arraybackslash}\X{8}{14}|
.. list-table:: 
   :class: longtable
   :header-rows: 1

   * - Уровень логирования Jaybird
     - Уровень журнала jul
   * - Logger.trace
     - Level.FINER
   * - Logger.debug
     - Level.FINE
   * - Logger.info
     - Level.INFO
   * - Logger.warn
     - Level.WARNING
   * - Logger.error
     - Level.SEVERE
   * - Logger.fatal
     - Level.SEVERE

Отключение логирования
~~~~~~~~~~~~~~~~~~~~~~~~~~~~

Чтобы отключить логирование, необходимо настроить библиотеку логирования.
Например, при использовании ``java.util.logging`` необходимо использовать соответствующий файл со свойствами 
(заданный с помощью параметра ``-Djava.util.logging.config.file=<file>``), включающий:

.. code-block::

    org.firebirdsql.level = OFF

Начиная с ``Jaybird 6`` для отключения логирования нужно указать для свойства ``org.firebirdsql.jdbc.disableLogging`` значение ``true``.

Ведение журнала в консоли
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

Начиная с ``Jaybird 6`` можно записывать журнал вывода в консоль. Для этого нужно настроить библиотеку логирования.
Например, при использовании ``java.util.logging`` необходимо использовать соответствующий файл со свойствами 
(заданный с помощью ``-Djava.util.logging.config.file=<file>``), включающий:

.. code-block::

    handlers = java.util.logging.ConsoleHandler

В отличие от решения, доступного в ``Jaybird 5`` и более ранних версиях, ``java.util.logging`` 
не предоставляет стандартного способа ведения журнала в ``System.out`` (его ``ConsoleHandler`` ведет журнал в ``System.err``), 
для этого потребуется реализация собственного обработчика.

Собственная реализация логирования
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

Например, при использовании ``java.util.logging`` необходимо использовать соответствующий файл со свойствами (заданный с помощью параметра ``-Djava.util.logging.config.file=<file>``), включающий:
Реализация должна быть публичной и иметь публичный конструктор с единственным аргументом ``String`` для имени логгера. 
Необходимо установить для системного свойства ``org.firebirdsql.jdbc.loggerImplementation`` полное имя собственной реализации.

Интерфейс ``org.firebirdsql.logging.Logger`` следует считать нестабильным, он может меняться в минорных релизах.

Например:

.. code-block::

    package org.example.jaybird.logging;

    public class CustomLogger implements org.firebirdsql.logging.Logger {
        public CustomLogger(String name) {
            // create the logger
        }
        // implementation of org.firebirdsql.logging.Logger interface
    }

В командной строке ``Java`` необходимо указать:

.. code-block::

    -Dorg.firebirdsql.jdbc.loggerImplementation=org.example.jaybird.logging.CustomLogger

Чтобы реализовать собственный логгер в ``Jaybird 6``, необходимо создать реализацию ``java.lang.System.Logger`` и ``java.lang.System.LoggerFinder``, а также определить загрузчик сервисов.