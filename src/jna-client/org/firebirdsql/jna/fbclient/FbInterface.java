package org.firebirdsql.jna.fbclient;

import org.firebirdsql.gds.ng.jna.FbException;

/**
 * JNA Wrapper for library implementing <b>interface.h</b>.
 *
 * @author <a href="mailto:vasiliy.yashkov@red-soft.ru">Vasiliy Yashkov</a>
 * @since 4.0
 */
public interface FbInterface extends FbClientLibrary {

    public static interface IVersionedIntf
    {
    }

    public static interface IReferenceCountedIntf extends IVersionedIntf
    {
        public void addRef();
        public int release();
    }

    public static interface IDisposableIntf extends IVersionedIntf
    {
        public void dispose();
    }

    public static interface IStatusIntf extends IDisposableIntf
    {
        public static int STATE_WARNINGS = 1;
        public static int STATE_ERRORS = 2;
        public static int RESULT_ERROR = -1;
        public static int RESULT_OK = 0;
        public static int RESULT_NO_DATA = 1;
        public static int RESULT_SEGMENT = 2;

        public void init();
        public int getState();
        public void setErrors2(int length, com.sun.jna.Pointer[] value);
        public void setWarnings2(int length, com.sun.jna.Pointer[] value);
        public void setErrors(com.sun.jna.Pointer[] value);
        public void setWarnings(com.sun.jna.Pointer[] value);
        public com.sun.jna.Pointer getErrors();
        public com.sun.jna.Pointer getWarnings();
        public IStatus clone();
    }

    public static interface IMasterIntf extends IVersionedIntf
    {
        public IStatus getStatus();
        public IProvider getDispatcher();
        public IPluginManager getPluginManager();
        public ITimerControl getTimerControl();
        public IDtc getDtc();
        public IAttachment registerAttachment(IProvider provider, IAttachment attachment);
        public ITransaction registerTransaction(IAttachment attachment, ITransaction transaction);
        public IMetadataBuilder getMetadataBuilder(IStatus status, int fieldCount) throws FbException;
        public int serverMode(int mode);
        public IUtil getUtilInterface();
        public IConfigManager getConfigManager();
        public boolean getProcessExiting();
    }

    public static interface IPluginBaseIntf extends IReferenceCountedIntf
    {
        public void setOwner(IReferenceCounted r);
        public IReferenceCounted getOwner();
    }

    public static interface IPluginSetIntf extends IReferenceCountedIntf
    {
        public String getName();
        public String getModuleName();
        public IPluginBase getPlugin(IStatus status) throws FbException;
        public void next(IStatus status) throws FbException;
        public void set(IStatus status, String s) throws FbException;
    }

    public static interface IConfigEntryIntf extends IReferenceCountedIntf
    {
        public String getName();
        public String getValue();
        public long getIntValue();
        public boolean getBoolValue();
        public IConfig getSubConfig(IStatus status) throws FbException;
    }

    public static interface IConfigIntf extends IReferenceCountedIntf
    {
        public IConfigEntry find(IStatus status, String name) throws FbException;
        public IConfigEntry findValue(IStatus status, String name, String value) throws FbException;
        public IConfigEntry findPos(IStatus status, String name, int pos) throws FbException;
    }

    public static interface IFirebirdConfIntf extends IReferenceCountedIntf
    {
        public int getKey(String name);
        public long asInteger(int key);
        public String asString(int key);
        public boolean asBoolean(int key);
    }

    public static interface IPluginConfigIntf extends IReferenceCountedIntf
    {
        public String getConfigFileName();
        public IConfig getDefaultConfig(IStatus status) throws FbException;
        public IFirebirdConf getFirebirdConf(IStatus status) throws FbException;
        public void setReleaseDelay(IStatus status, long microSeconds) throws FbException;
    }

    public static interface IPluginFactoryIntf extends IVersionedIntf
    {
        public IPluginBase createPlugin(IStatus status, IPluginConfig factoryParameter) throws FbException;
    }

    public static interface IPluginModuleIntf extends IVersionedIntf
    {
        public void doClean();
    }

    public static interface IPluginManagerIntf extends IVersionedIntf
    {
        public static int TYPE_PROVIDER = 1;
        public static int TYPE_FIRST_NON_LIB = 2;
        public static int TYPE_AUTH_SERVER = 3;
        public static int TYPE_AUTH_CLIENT = 4;
        public static int TYPE_AUTH_USER_MANAGEMENT = 5;
        public static int TYPE_EXTERNAL_ENGINE = 6;
        public static int TYPE_TRACE = 7;
        public static int TYPE_WIRE_CRYPT = 8;
        public static int TYPE_DB_CRYPT = 9;
        public static int TYPE_KEY_HOLDER = 10;
        public static int TYPE_CRYPTO_API = 11;
        public static int TYPE_LDAP = 12;
        public static int TYPE_COUNT = 13;

        public void registerPluginFactory(int pluginType, String defaultName, IPluginFactory factory);
        public void registerModule(IPluginModule cleanup);
        public void unregisterModule(IPluginModule cleanup);
        public IPluginSet getPlugins(IStatus status, int pluginType, String namesList, IFirebirdConf firebirdConf) throws FbException;
        public IConfig getConfig(IStatus status, String filename) throws FbException;
        public void releasePlugin(IPluginBase plugin);
    }

    public static interface ICryptKeyIntf extends IVersionedIntf
    {
        public void setSymmetric(IStatus status, String type, int keyLength, com.sun.jna.Pointer key) throws FbException;
        public void setAsymmetric(IStatus status, String type, int encryptKeyLength, com.sun.jna.Pointer encryptKey, int decryptKeyLength, com.sun.jna.Pointer decryptKey) throws FbException;
        public com.sun.jna.Pointer getEncryptKey(int[] length);
        public com.sun.jna.Pointer getDecryptKey(int[] length);
    }

    public static interface IConfigManagerIntf extends IVersionedIntf
    {
        public static int DIR_BIN = 0;
        public static int DIR_SBIN = 1;
        public static int DIR_CONF = 2;
        public static int DIR_LIB = 3;
        public static int DIR_INC = 4;
        public static int DIR_DOC = 5;
        public static int DIR_UDF = 6;
        public static int DIR_SAMPLE = 7;
        public static int DIR_SAMPLEDB = 8;
        public static int DIR_HELP = 9;
        public static int DIR_INTL = 10;
        public static int DIR_MISC = 11;
        public static int DIR_SECDB = 12;
        public static int DIR_MSG = 13;
        public static int DIR_LOG = 14;
        public static int DIR_GUARD = 15;
        public static int DIR_PLUGINS = 16;
        public static int DIR_COUNT = 17;

        public String getDirectory(int code);
        public IFirebirdConf getFirebirdConf();
        public IFirebirdConf getDatabaseConf(String dbName);
        public IConfig getPluginConfig(String configuredPlugin);
        public String getInstallDirectory();
        public String getRootDirectory();
    }

    public static interface IEventCallbackIntf extends IReferenceCountedIntf
    {
        public void eventCallbackFunction(int length, byte[] events);
    }

    public static interface IBlobIntf extends IReferenceCountedIntf
    {
        public void getInfo(IStatus status, int itemsLength, byte[] items, int bufferLength, byte[] buffer) throws FbException;
        public int getSegment(IStatus status, int bufferLength, com.sun.jna.Pointer buffer, int[] segmentLength) throws FbException;
        public void putSegment(IStatus status, int length, com.sun.jna.Pointer buffer) throws FbException;
        public void cancel(IStatus status) throws FbException;
        public void close(IStatus status) throws FbException;
        public int seek(IStatus status, int mode, int offset) throws FbException;
    }

    public static interface ITransactionIntf extends IReferenceCountedIntf
    {
        public void getInfo(IStatus status, int itemsLength, byte[] items, int bufferLength, byte[] buffer) throws FbException;
        public void prepare(IStatus status, int msgLength, byte[] message) throws FbException;
        public void commit(IStatus status) throws FbException;
        public void commitRetaining(IStatus status) throws FbException;
        public void rollback(IStatus status) throws FbException;
        public void rollbackRetaining(IStatus status) throws FbException;
        public void disconnect(IStatus status) throws FbException;
        public ITransaction join(IStatus status, ITransaction transaction) throws FbException;
        public ITransaction validate(IStatus status, IAttachment attachment) throws FbException;
        public ITransaction enterDtc(IStatus status) throws FbException;
    }

    public static interface IMessageMetadataIntf extends IReferenceCountedIntf
    {
        public int getCount(IStatus status) throws FbException;
        public String getField(IStatus status, int index) throws FbException;
        public String getRelation(IStatus status, int index) throws FbException;
        public String getOwner(IStatus status, int index) throws FbException;
        public String getAlias(IStatus status, int index) throws FbException;
        public int getType(IStatus status, int index) throws FbException;
        public boolean isNullable(IStatus status, int index) throws FbException;
        public int getSubType(IStatus status, int index) throws FbException;
        public int getLength(IStatus status, int index) throws FbException;
        public int getScale(IStatus status, int index) throws FbException;
        public int getCharSet(IStatus status, int index) throws FbException;
        public int getOffset(IStatus status, int index) throws FbException;
        public int getNullOffset(IStatus status, int index) throws FbException;
        public IMetadataBuilder getBuilder(IStatus status) throws FbException;
        public int getMessageLength(IStatus status) throws FbException;
    }

    public static interface IMetadataBuilderIntf extends IReferenceCountedIntf
    {
        public void setType(IStatus status, int index, int type) throws FbException;
        public void setSubType(IStatus status, int index, int subType) throws FbException;
        public void setLength(IStatus status, int index, int length) throws FbException;
        public void setCharSet(IStatus status, int index, int charSet) throws FbException;
        public void setScale(IStatus status, int index, int scale) throws FbException;
        public void truncate(IStatus status, int count) throws FbException;
        public void moveNameToIndex(IStatus status, String name, int index) throws FbException;
        public void remove(IStatus status, int index) throws FbException;
        public int addField(IStatus status) throws FbException;
        public IMessageMetadata getMetadata(IStatus status) throws FbException;
    }

    public static interface IResultSetIntf extends IReferenceCountedIntf
    {
        public int fetchNext(IStatus status, com.sun.jna.Pointer message) throws FbException;
        public int fetchPrior(IStatus status, com.sun.jna.Pointer message) throws FbException;
        public int fetchFirst(IStatus status, com.sun.jna.Pointer message) throws FbException;
        public int fetchLast(IStatus status, com.sun.jna.Pointer message) throws FbException;
        public int fetchAbsolute(IStatus status, int position, com.sun.jna.Pointer message) throws FbException;
        public int fetchRelative(IStatus status, int offset, com.sun.jna.Pointer message) throws FbException;
        public boolean isEof(IStatus status) throws FbException;
        public boolean isBof(IStatus status) throws FbException;
        public IMessageMetadata getMetadata(IStatus status) throws FbException;
        public void close(IStatus status) throws FbException;
        public void setDelayedOutputFormat(IStatus status, IMessageMetadata format) throws FbException;
    }

    public static interface IStatementIntf extends IReferenceCountedIntf
    {
        public static int PREPARE_PREFETCH_NONE = 0;
        public static int PREPARE_PREFETCH_TYPE = 1;
        public static int PREPARE_PREFETCH_INPUT_PARAMETERS = 2;
        public static int PREPARE_PREFETCH_OUTPUT_PARAMETERS = 4;
        public static int PREPARE_PREFETCH_LEGACY_PLAN = 8;
        public static int PREPARE_PREFETCH_DETAILED_PLAN = 16;
        public static int PREPARE_PREFETCH_AFFECTED_RECORDS = 32;
        public static int PREPARE_PREFETCH_FLAGS = 64;
        public static int PREPARE_PREFETCH_METADATA = IStatementIntf.PREPARE_PREFETCH_TYPE | IStatementIntf.PREPARE_PREFETCH_FLAGS | IStatementIntf.PREPARE_PREFETCH_INPUT_PARAMETERS | IStatementIntf.PREPARE_PREFETCH_OUTPUT_PARAMETERS;
        public static int PREPARE_PREFETCH_ALL = IStatementIntf.PREPARE_PREFETCH_METADATA | IStatementIntf.PREPARE_PREFETCH_LEGACY_PLAN | IStatementIntf.PREPARE_PREFETCH_DETAILED_PLAN | IStatementIntf.PREPARE_PREFETCH_AFFECTED_RECORDS;
        public static int FLAG_HAS_CURSOR = 1;
        public static int FLAG_REPEAT_EXECUTE = 2;
        public static int CURSOR_TYPE_SCROLLABLE = 1;

        public void getInfo(IStatus status, int itemsLength, byte[] items, int bufferLength, byte[] buffer) throws FbException;
        public int getType(IStatus status) throws FbException;
        public String getPlan(IStatus status, boolean detailed) throws FbException;
        public long getAffectedRecords(IStatus status) throws FbException;
        public IMessageMetadata getInputMetadata(IStatus status) throws FbException;
        public IMessageMetadata getOutputMetadata(IStatus status) throws FbException;
        public ITransaction execute(IStatus status, ITransaction transaction, IMessageMetadata inMetadata, com.sun.jna.Pointer inBuffer, IMessageMetadata outMetadata, com.sun.jna.Pointer outBuffer) throws FbException;
        public IResultSet openCursor(IStatus status, ITransaction transaction, IMessageMetadata inMetadata, com.sun.jna.Pointer inBuffer, IMessageMetadata outMetadata, int flags) throws FbException;
        public void setCursorName(IStatus status, String name) throws FbException;
        public void free(IStatus status) throws FbException;
        public int getFlags(IStatus status) throws FbException;
    }

    public static interface IRequestIntf extends IReferenceCountedIntf
    {
        public void receive(IStatus status, int level, int msgType, int length, byte[] message) throws FbException;
        public void send(IStatus status, int level, int msgType, int length, byte[] message) throws FbException;
        public void getInfo(IStatus status, int level, int itemsLength, byte[] items, int bufferLength, byte[] buffer) throws FbException;
        public void start(IStatus status, ITransaction tra, int level) throws FbException;
        public void startAndSend(IStatus status, ITransaction tra, int level, int msgType, int length, byte[] message) throws FbException;
        public void unwind(IStatus status, int level) throws FbException;
        public void free(IStatus status) throws FbException;
    }

    public static interface IEventsIntf extends IReferenceCountedIntf
    {
        public void cancel(IStatus status) throws FbException;
    }

    public static interface IAttachmentIntf extends IReferenceCountedIntf
    {
        public void getInfo(IStatus status, int itemsLength, byte[] items, int bufferLength, byte[] buffer) throws FbException;
        public ITransaction startTransaction(IStatus status, int tpbLength, byte[] tpb) throws FbException;
        public ITransaction reconnectTransaction(IStatus status, int length, byte[] id) throws FbException;
        public IRequest compileRequest(IStatus status, int blrLength, byte[] blr) throws FbException;
        public void transactRequest(IStatus status, ITransaction transaction, int blrLength, byte[] blr, int inMsgLength, byte[] inMsg, int outMsgLength, byte[] outMsg) throws FbException;
        public IBlob createBlob(IStatus status, ITransaction transaction, ISC_QUAD[] id, int bpbLength, byte[] bpb) throws FbException;
        public IBlob openBlob(IStatus status, ITransaction transaction, ISC_QUAD[] id, int bpbLength, byte[] bpb) throws FbException;
        public int getSlice(IStatus status, ITransaction transaction, ISC_QUAD[] id, int sdlLength, byte[] sdl, int paramLength, byte[] param, int sliceLength, byte[] slice) throws FbException;
        public void putSlice(IStatus status, ITransaction transaction, ISC_QUAD[] id, int sdlLength, byte[] sdl, int paramLength, byte[] param, int sliceLength, byte[] slice) throws FbException;
        public void executeDyn(IStatus status, ITransaction transaction, int length, byte[] dyn) throws FbException;
        public IStatement prepare(IStatus status, ITransaction tra, int stmtLength, String sqlStmt, int dialect, int flags) throws FbException;
        public ITransaction execute(IStatus status, ITransaction transaction, int stmtLength, String sqlStmt, int dialect, IMessageMetadata inMetadata, com.sun.jna.Pointer inBuffer, IMessageMetadata outMetadata, com.sun.jna.Pointer outBuffer) throws FbException;
        public IResultSet openCursor(IStatus status, ITransaction transaction, int stmtLength, String sqlStmt, int dialect, IMessageMetadata inMetadata, com.sun.jna.Pointer inBuffer, IMessageMetadata outMetadata, String cursorName, int cursorFlags) throws FbException;
        public IEvents queEvents(IStatus status, IEventCallback callback, int length, byte[] events) throws FbException;
        public void cancelOperation(IStatus status, int option) throws FbException;
        public void ping(IStatus status) throws FbException;
        public void detach(IStatus status) throws FbException;
        public void dropDatabase(IStatus status) throws FbException;
        public void replicate(IStatus status, int length, byte[] data) throws FbException;
    }

    public static interface IServiceIntf extends IReferenceCountedIntf
    {
        public void detach(IStatus status) throws FbException;
        public void query(IStatus status, int sendLength, byte[] sendItems, int receiveLength, byte[] receiveItems, int bufferLength, byte[] buffer) throws FbException;
        public void start(IStatus status, int spbLength, byte[] spb) throws FbException;
    }

    public static interface IProviderIntf extends IPluginBaseIntf
    {
        public IAttachment attachDatabase(IStatus status, String fileName, int dpbLength, byte[] dpb) throws FbException;
        public IAttachment createDatabase(IStatus status, String fileName, int dpbLength, byte[] dpb) throws FbException;
        public IService attachServiceManager(IStatus status, String service, int spbLength, byte[] spb) throws FbException;
        public void shutdown(IStatus status, int timeout, int reason) throws FbException;
        public void setDbCryptCallback(IStatus status, ICryptKeyCallback cryptCallback) throws FbException;
    }

    public static interface IDtcStartIntf extends IDisposableIntf
    {
        public void addAttachment(IStatus status, IAttachment att) throws FbException;
        public void addWithTpb(IStatus status, IAttachment att, int length, byte[] tpb) throws FbException;
        public ITransaction start(IStatus status) throws FbException;
    }

    public static interface IDtcIntf extends IVersionedIntf
    {
        public ITransaction join(IStatus status, ITransaction one, ITransaction two) throws FbException;
        public IDtcStart startBuilder(IStatus status) throws FbException;
    }

    public static interface IAuthIntf extends IPluginBaseIntf
    {
        public static int AUTH_FAILED = -1;
        public static int AUTH_SUCCESS = 0;
        public static int AUTH_MORE_DATA = 1;
        public static int AUTH_CONTINUE = 2;

    }

    public static interface IWriterIntf extends IVersionedIntf
    {
        public void reset();
        public void add(IStatus status, String name) throws FbException;
        public void setType(IStatus status, String value) throws FbException;
        public void setDb(IStatus status, String value) throws FbException;
    }

    public static interface IServerBlockIntf extends IVersionedIntf
    {
        public String getLogin();
        public com.sun.jna.Pointer getData(int[] length);
        public void putData(IStatus status, int length, com.sun.jna.Pointer data) throws FbException;
        public ICryptKey newKey(IStatus status) throws FbException;
    }

    public static interface IClientBlockIntf extends IReferenceCountedIntf
    {
        public String getLogin();
        public String getPassword();
        public String getCertificate();
        public String getRepositoryPin();
        public boolean getVerifyServer();
        public com.sun.jna.Pointer getData(int[] length);
        public void putData(IStatus status, int length, com.sun.jna.Pointer data) throws FbException;
        public ICryptKey newKey(IStatus status) throws FbException;
    }

    public static interface IServerIntf extends IAuthIntf
    {
        public int authenticate(IStatus status, IServerBlock sBlock, IWriter writerInterface) throws FbException;
        public void setDbCryptCallback(IStatus status, ICryptKeyCallback cryptCallback) throws FbException;
    }

    public static interface IClientIntf extends IAuthIntf
    {
        public int authenticate(IStatus status, IClientBlock cBlock) throws FbException;
    }

    public static interface IUserFieldIntf extends IVersionedIntf
    {
        public int entered();
        public int specified();
        public void setEntered(IStatus status, int newValue) throws FbException;
    }

    public static interface ICharUserFieldIntf extends IUserFieldIntf
    {
        public String get();
        public void set(IStatus status, String newValue) throws FbException;
    }

    public static interface IIntUserFieldIntf extends IUserFieldIntf
    {
        public int get();
        public void set(IStatus status, int newValue) throws FbException;
    }

    public static interface IUserIntf extends IVersionedIntf
    {
        public static int OP_USER_ADD = 1;
        public static int OP_USER_MODIFY = 2;
        public static int OP_USER_DELETE = 3;
        public static int OP_USER_DISPLAY = 4;
        public static int OP_USER_SET_MAP = 5;
        public static int OP_USER_DROP_MAP = 6;
        public static int OP_USER_RESET = 100;

        public int operation();
        public ICharUserField userName();
        public ICharUserField password();
        public ICharUserField firstName();
        public ICharUserField lastName();
        public ICharUserField middleName();
        public ICharUserField comment();
        public ICharUserField attributes();
        public IIntUserField active();
        public IIntUserField admin();
        public void clear(IStatus status) throws FbException;
    }

    public static interface IListUsersIntf extends IVersionedIntf
    {
        public void list(IStatus status, IUser user) throws FbException;
    }

    public static interface ILogonInfoIntf extends IVersionedIntf
    {
        public String name();
        public String role();
        public String networkProtocol();
        public String remoteAddress();
        public com.sun.jna.Pointer authBlock(int[] length);
    }

    public static interface IManagementIntf extends IPluginBaseIntf
    {
        public void start(IStatus status, ILogonInfo logonInfo) throws FbException;
        public int execute(IStatus status, IUser user, IListUsers callback) throws FbException;
        public void commit(IStatus status) throws FbException;
        public void rollback(IStatus status) throws FbException;
    }

    public static interface IWireCryptPluginIntf extends IPluginBaseIntf
    {
        public String getKnownTypes(IStatus status) throws FbException;
        public void setKey(IStatus status, ICryptKey key) throws FbException;
        public void encrypt(IStatus status, int length, com.sun.jna.Pointer from, com.sun.jna.Pointer to) throws FbException;
        public void decrypt(IStatus status, int length, com.sun.jna.Pointer from, com.sun.jna.Pointer to) throws FbException;
    }

    public static interface ICryptKeyCallbackIntf extends IVersionedIntf
    {
        public int callback(int dataLength, com.sun.jna.Pointer data, int bufferLength, com.sun.jna.Pointer buffer);
    }

    public static interface IKeyHolderPluginIntf extends IPluginBaseIntf
    {
        public int keyCallback(IStatus status, ICryptKeyCallback callback) throws FbException;
        public ICryptKeyCallback keyHandle(IStatus status, String keyName) throws FbException;
        public boolean useOnlyOwnKeys(IStatus status) throws FbException;
        public ICryptKeyCallback chainHandle(IStatus status) throws FbException;
    }

    public static interface IDbCryptInfoIntf extends IReferenceCountedIntf
    {
        public String getDatabaseFullPath(IStatus status) throws FbException;
    }

    public static interface IDbCryptPluginIntf extends IPluginBaseIntf
    {
        public void setKey(IStatus status, int length, IKeyHolderPlugin[] sources, String keyName) throws FbException;
        public void encrypt(IStatus status, int length, com.sun.jna.Pointer from, com.sun.jna.Pointer to) throws FbException;
        public void decrypt(IStatus status, int length, com.sun.jna.Pointer from, com.sun.jna.Pointer to) throws FbException;
        public void setInfo(IStatus status, IDbCryptInfo info) throws FbException;
    }

    public static interface IExternalContextIntf extends IVersionedIntf
    {
        public IMaster getMaster();
        public IExternalEngine getEngine(IStatus status) throws FbException;
        public IAttachment getAttachment(IStatus status) throws FbException;
        public ITransaction getTransaction(IStatus status) throws FbException;
        public String getUserName();
        public String getDatabaseName();
        public String getClientCharSet();
        public int obtainInfoCode();
        public com.sun.jna.Pointer getInfo(int code);
        public com.sun.jna.Pointer setInfo(int code, com.sun.jna.Pointer value);
    }

    public static interface IExternalResultSetIntf extends IDisposableIntf
    {
        public boolean fetch(IStatus status) throws FbException;
    }

    public static interface IExternalFunctionIntf extends IDisposableIntf
    {
        public void getCharSet(IStatus status, IExternalContext context, com.sun.jna.Pointer name, int nameSize) throws FbException;
        public void execute(IStatus status, IExternalContext context, com.sun.jna.Pointer inMsg, com.sun.jna.Pointer outMsg) throws FbException;
    }

    public static interface IExternalProcedureIntf extends IDisposableIntf
    {
        public void getCharSet(IStatus status, IExternalContext context, com.sun.jna.Pointer name, int nameSize) throws FbException;
        public IExternalResultSet open(IStatus status, IExternalContext context, com.sun.jna.Pointer inMsg, com.sun.jna.Pointer outMsg) throws FbException;
    }

    public static interface IExternalTriggerIntf extends IDisposableIntf
    {
        public static int TYPE_BEFORE = 1;
        public static int TYPE_AFTER = 2;
        public static int TYPE_DATABASE = 3;
        public static int ACTION_INSERT = 1;
        public static int ACTION_UPDATE = 2;
        public static int ACTION_DELETE = 3;
        public static int ACTION_CONNECT = 4;
        public static int ACTION_DISCONNECT = 5;
        public static int ACTION_TRANS_START = 6;
        public static int ACTION_TRANS_COMMIT = 7;
        public static int ACTION_TRANS_ROLLBACK = 8;
        public static int ACTION_DDL = 9;

        public void getCharSet(IStatus status, IExternalContext context, com.sun.jna.Pointer name, int nameSize) throws FbException;
        public void execute(IStatus status, IExternalContext context, int action, com.sun.jna.Pointer oldMsg, com.sun.jna.Pointer newMsg, com.sun.jna.Pointer oldDbKey, com.sun.jna.Pointer newDbKey) throws FbException;
    }

    public static interface IRoutineMetadataIntf extends IVersionedIntf
    {
        public String getPackage(IStatus status) throws FbException;
        public String getName(IStatus status) throws FbException;
        public String getEntryPoint(IStatus status) throws FbException;
        public String getBody(IStatus status) throws FbException;
        public IMessageMetadata getInputMetadata(IStatus status) throws FbException;
        public IMessageMetadata getOutputMetadata(IStatus status) throws FbException;
        public IMessageMetadata getTriggerMetadata(IStatus status) throws FbException;
        public String getTriggerTable(IStatus status) throws FbException;
        public int getTriggerType(IStatus status) throws FbException;
    }

    public static interface IExternalEngineIntf extends IPluginBaseIntf
    {
        public void open(IStatus status, IExternalContext context, com.sun.jna.Pointer charSet, int charSetSize) throws FbException;
        public void openAttachment(IStatus status, IExternalContext context) throws FbException;
        public void closeAttachment(IStatus status, IExternalContext context) throws FbException;
        public IExternalFunction makeFunction(IStatus status, IExternalContext context, IRoutineMetadata metadata, IMetadataBuilder inBuilder, IMetadataBuilder outBuilder) throws FbException;
        public IExternalProcedure makeProcedure(IStatus status, IExternalContext context, IRoutineMetadata metadata, IMetadataBuilder inBuilder, IMetadataBuilder outBuilder) throws FbException;
        public IExternalTrigger makeTrigger(IStatus status, IExternalContext context, IRoutineMetadata metadata, IMetadataBuilder fieldsBuilder) throws FbException;
    }

    public static interface ITimerIntf extends IReferenceCountedIntf
    {
        public void handler();
    }

    public static interface ITimerControlIntf extends IVersionedIntf
    {
        public void start(IStatus status, ITimer timer, long microSeconds) throws FbException;
        public void stop(IStatus status, ITimer timer) throws FbException;
    }

    public static interface IVersionCallbackIntf extends IVersionedIntf
    {
        public void callback(IStatus status, String text) throws FbException;
    }

    public static interface IUtilIntf extends IVersionedIntf
    {
        public void getFbVersion(IStatus status, IAttachment att, IVersionCallback callback) throws FbException;
        public void loadBlob(IStatus status, ISC_QUAD[] blobId, IAttachment att, ITransaction tra, String file, boolean txt) throws FbException;
        public void dumpBlob(IStatus status, ISC_QUAD[] blobId, IAttachment att, ITransaction tra, String file, boolean txt) throws FbException;
        public void getPerfCounters(IStatus status, IAttachment att, String countersSet, long[] counters) throws FbException;
        public IAttachment executeCreateDatabase(IStatus status, int stmtLength, String creatDBstatement, int dialect, boolean[] stmtIsCreateDb) throws FbException;
        public void decodeDate(ISC_DATE date, int[] year, int[] month, int[] day);
        public void decodeTime(ISC_TIME time, int[] hours, int[] minutes, int[] seconds, int[] fractions);
        public ISC_DATE encodeDate(int year, int month, int day);
        public ISC_TIME encodeTime(int hours, int minutes, int seconds, int fractions);
        public int formatStatus(com.sun.jna.Pointer buffer, int bufferSize, IStatus status);
        public int getClientVersion();
        public IXpbBuilder getXpbBuilder(IStatus status, int kind, byte[] buf, int len) throws FbException;
        public int setOffsets(IStatus status, IMessageMetadata metadata, IOffsetsCallback callback) throws FbException;
    }

    public static interface IOffsetsCallbackIntf extends IVersionedIntf
    {
        public void setOffset(IStatus status, int index, int offset, int nullOffset) throws FbException;
    }

    public static interface IXpbBuilderIntf extends IDisposableIntf
    {
        public static int DPB = 1;
        public static int SPB_ATTACH = 2;
        public static int SPB_START = 3;
        public static int TPB = 4;

        public void clear(IStatus status) throws FbException;
        public void removeCurrent(IStatus status) throws FbException;
        public void insertInt(IStatus status, byte tag, int value) throws FbException;
        public void insertBigInt(IStatus status, byte tag, long value) throws FbException;
        public void insertBytes(IStatus status, byte tag, com.sun.jna.Pointer bytes, int length) throws FbException;
        public void insertString(IStatus status, byte tag, String str) throws FbException;
        public void insertTag(IStatus status, byte tag) throws FbException;
        public boolean isEof(IStatus status) throws FbException;
        public void moveNext(IStatus status) throws FbException;
        public void rewind(IStatus status) throws FbException;
        public boolean findFirst(IStatus status, byte tag) throws FbException;
        public boolean findNext(IStatus status) throws FbException;
        public byte getTag(IStatus status) throws FbException;
        public int getLength(IStatus status) throws FbException;
        public int getInt(IStatus status) throws FbException;
        public long getBigInt(IStatus status) throws FbException;
        public String getString(IStatus status) throws FbException;
        public com.sun.jna.Pointer getBytes(IStatus status) throws FbException;
        public int getBufferLength(IStatus status) throws FbException;
        public com.sun.jna.Pointer getBuffer(IStatus status) throws FbException;
    }

    public static interface ITraceConnectionIntf extends IVersionedIntf
    {
        public static int KIND_DATABASE = 1;
        public static int KIND_SERVICE = 2;

        public int getKind();
        public int getProcessID();
        public String getUserName();
        public String getRoleName();
        public String getCharSet();
        public String getRemoteProtocol();
        public String getRemoteAddress();
        public String getRemoteHwAddress();
        public int getRemoteProcessID();
        public String getRemoteProcessName();
    }

    public static interface ITraceDatabaseConnectionIntf extends ITraceConnectionIntf
    {
        public long getConnectionID();
        public String getDatabaseName();
    }

    public static interface ITraceTransactionIntf extends IVersionedIntf
    {
        public static int ISOLATION_CONSISTENCY = 1;
        public static int ISOLATION_CONCURRENCY = 2;
        public static int ISOLATION_READ_COMMITTED_RECVER = 3;
        public static int ISOLATION_READ_COMMITTED_NORECVER = 4;

        public long getTransactionID();
        public boolean getReadOnly();
        public int getWait();
        public int getIsolation();
        public com.sun.jna.Pointer getPerf();
    }

    public static interface ITraceParamsIntf extends IVersionedIntf
    {
        public int getCount();
        public com.sun.jna.Pointer getParam(int idx);
        public String getTextUTF8(IStatus status, int idx) throws FbException;
    }

    public static interface ITraceStatementIntf extends IVersionedIntf
    {
        public long getStmtID();
        public com.sun.jna.Pointer getPerf();
    }

    public static interface ITraceSQLStatementIntf extends ITraceStatementIntf
    {
        public String getText();
        public String getPlan();
        public ITraceParams getInputs();
        public String getTextUTF8();
        public String getExplainedPlan();
    }

    public static interface ITraceBLRStatementIntf extends ITraceStatementIntf
    {
        public com.sun.jna.Pointer getData();
        public int getDataLength();
        public String getText();
    }

    public static interface ITraceDYNRequestIntf extends IVersionedIntf
    {
        public com.sun.jna.Pointer getData();
        public int getDataLength();
        public String getText();
    }

    public static interface ITraceContextVariableIntf extends IVersionedIntf
    {
        public String getNameSpace();
        public String getVarName();
        public String getVarValue();
    }

    public static interface ITraceProcedureIntf extends IVersionedIntf
    {
        public String getProcName();
        public ITraceParams getInputs();
        public com.sun.jna.Pointer getPerf();
    }

    public static interface ITraceFunctionIntf extends IVersionedIntf
    {
        public String getFuncName();
        public ITraceParams getInputs();
        public ITraceParams getResult();
        public com.sun.jna.Pointer getPerf();
    }

    public static interface ITraceTriggerIntf extends IVersionedIntf
    {
        public static int TYPE_ALL = 0;
        public static int TYPE_BEFORE = 1;
        public static int TYPE_AFTER = 2;

        public String getTriggerName();
        public String getRelationName();
        public int getAction();
        public int getWhich();
        public com.sun.jna.Pointer getPerf();
    }

    public static interface ITraceServiceConnectionIntf extends ITraceConnectionIntf
    {
        public com.sun.jna.Pointer getServiceID();
        public String getServiceMgr();
        public String getServiceName();
    }

    public static interface ITraceStatusVectorIntf extends IVersionedIntf
    {
        public boolean hasError();
        public boolean hasWarning();
        public IStatus getStatus();
        public String getText();
    }

    public static interface ITraceSweepInfoIntf extends IVersionedIntf
    {
        public long getOIT();
        public long getOST();
        public long getOAT();
        public long getNext();
        public com.sun.jna.Pointer getPerf();
    }

    public static interface ITraceLogWriterIntf extends IReferenceCountedIntf
    {
        public int write(com.sun.jna.Pointer buf, int size);
    }

    public static interface ITraceInitInfoIntf extends IVersionedIntf
    {
        public String getConfigText();
        public int getTraceSessionID();
        public String getTraceSessionName();
        public int getTraceSessionFlags();
        public String getFirebirdRootDirectory();
        public String getDatabaseName();
        public ITraceDatabaseConnection getConnection();
        public ITraceServiceConnection getService();
        public ITraceLogWriter getLogWriter();
    }

    public static interface ITracePluginIntf extends IReferenceCountedIntf
    {
        public static int RESULT_SUCCESS = 0;
        public static int RESULT_FAILED = 1;
        public static int RESULT_UNAUTHORIZED = 2;
        public static int SWEEP_STATE_STARTED = 1;
        public static int SWEEP_STATE_FINISHED = 2;
        public static int SWEEP_STATE_FAILED = 3;
        public static int SWEEP_STATE_PROGRESS = 4;

        public String trace_get_error();
        public boolean trace_attach(ITraceDatabaseConnection connection, boolean create_db, int dpb_length, byte[] dpb, int att_result);
        public boolean trace_detach(ITraceDatabaseConnection connection, boolean drop_db);
        public boolean trace_transaction_start(ITraceDatabaseConnection connection, ITraceTransaction transaction, int tpb_length, byte[] tpb, int tra_result);
        public boolean trace_transaction_end(ITraceDatabaseConnection connection, ITraceTransaction transaction, boolean commit, boolean retain_context, int tra_result);
        public boolean trace_proc_execute(ITraceDatabaseConnection connection, ITraceTransaction transaction, ITraceProcedure procedure, boolean started, int proc_result);
        public boolean trace_trigger_execute(ITraceDatabaseConnection connection, ITraceTransaction transaction, ITraceTrigger trigger, boolean started, int trig_result);
        public boolean trace_set_context(ITraceDatabaseConnection connection, ITraceTransaction transaction, ITraceContextVariable variable);
        public boolean trace_dsql_prepare(ITraceDatabaseConnection connection, ITraceTransaction transaction, ITraceSQLStatement statement, long time_millis, int req_result);
        public boolean trace_dsql_free(ITraceDatabaseConnection connection, ITraceSQLStatement statement, int option);
        public boolean trace_dsql_execute(ITraceDatabaseConnection connection, ITraceTransaction transaction, ITraceSQLStatement statement, boolean started, int req_result);
        public boolean trace_blr_compile(ITraceDatabaseConnection connection, ITraceTransaction transaction, ITraceBLRStatement statement, long time_millis, int req_result);
        public boolean trace_blr_execute(ITraceDatabaseConnection connection, ITraceTransaction transaction, ITraceBLRStatement statement, int req_result);
        public boolean trace_dyn_execute(ITraceDatabaseConnection connection, ITraceTransaction transaction, ITraceDYNRequest request, long time_millis, int req_result);
        public boolean trace_service_attach(ITraceServiceConnection service, int spb_length, byte[] spb, int att_result);
        public boolean trace_service_start(ITraceServiceConnection service, int switches_length, String switches, int start_result);
        public boolean trace_service_query(ITraceServiceConnection service, int send_item_length, byte[] send_items, int recv_item_length, byte[] recv_items, int query_result);
        public boolean trace_service_detach(ITraceServiceConnection service, int detach_result);
        public boolean trace_event_error(ITraceConnection connection, ITraceStatusVector status, String function);
        public boolean trace_event_sweep(ITraceDatabaseConnection connection, ITraceSweepInfo sweep, int sweep_state);
        public boolean trace_func_execute(ITraceDatabaseConnection connection, ITraceTransaction transaction, ITraceFunction function, boolean started, int func_result);
        public boolean trace_privilege_change(ITraceDatabaseConnection connection, ITraceTransaction transaction, String executor, String grantor, boolean is_grant, String object_name, String field_name, String user_name, String privileges, int options, int change_result);
    }

    public static interface ITraceFactoryIntf extends IPluginBaseIntf
    {
        public static int TRACE_EVENT_ATTACH = 0;
        public static int TRACE_EVENT_DETACH = 1;
        public static int TRACE_EVENT_TRANSACTION_START = 2;
        public static int TRACE_EVENT_TRANSACTION_END = 3;
        public static int TRACE_EVENT_SET_CONTEXT = 4;
        public static int TRACE_EVENT_PROC_EXECUTE = 5;
        public static int TRACE_EVENT_TRIGGER_EXECUTE = 6;
        public static int TRACE_EVENT_DSQL_PREPARE = 7;
        public static int TRACE_EVENT_DSQL_FREE = 8;
        public static int TRACE_EVENT_DSQL_EXECUTE = 9;
        public static int TRACE_EVENT_BLR_COMPILE = 10;
        public static int TRACE_EVENT_BLR_EXECUTE = 11;
        public static int TRACE_EVENT_DYN_EXECUTE = 12;
        public static int TRACE_EVENT_SERVICE_ATTACH = 13;
        public static int TRACE_EVENT_SERVICE_START = 14;
        public static int TRACE_EVENT_SERVICE_QUERY = 15;
        public static int TRACE_EVENT_SERVICE_DETACH = 16;
        public static int TRACE_EVENT_ERROR = 17;
        public static int TRACE_EVENT_SWEEP = 18;
        public static int TRACE_EVENT_FUNC_EXECUTE = 19;
        public static int TRACE_EVENT_PRIVILEGE_CHANGE = 20;
        public static int TRACE_EVENT_MAX = 21;

        public long trace_needs();
        public ITracePlugin trace_create(IStatus status, ITraceInitInfo init_info) throws FbException;
    }

    public static interface IUdrFunctionFactoryIntf extends IDisposableIntf
    {
        public void setup(IStatus status, IExternalContext context, IRoutineMetadata metadata, IMetadataBuilder inBuilder, IMetadataBuilder outBuilder) throws FbException;
        public IExternalFunction newItem(IStatus status, IExternalContext context, IRoutineMetadata metadata) throws FbException;
    }

    public static interface IUdrProcedureFactoryIntf extends IDisposableIntf
    {
        public void setup(IStatus status, IExternalContext context, IRoutineMetadata metadata, IMetadataBuilder inBuilder, IMetadataBuilder outBuilder) throws FbException;
        public IExternalProcedure newItem(IStatus status, IExternalContext context, IRoutineMetadata metadata) throws FbException;
    }

    public static interface IUdrTriggerFactoryIntf extends IDisposableIntf
    {
        public void setup(IStatus status, IExternalContext context, IRoutineMetadata metadata, IMetadataBuilder fieldsBuilder) throws FbException;
        public IExternalTrigger newItem(IStatus status, IExternalContext context, IRoutineMetadata metadata) throws FbException;
    }

    public static interface IUdrPluginIntf extends IVersionedIntf
    {
        public IMaster getMaster();
        public void registerFunction(IStatus status, String name, IUdrFunctionFactory factory) throws FbException;
        public void registerProcedure(IStatus status, String name, IUdrProcedureFactory factory) throws FbException;
        public void registerTrigger(IStatus status, String name, IUdrTriggerFactory factory) throws FbException;
    }

    public static interface ICryptoKeyIntf extends IVersionedIntf
    {
        public com.sun.jna.Pointer getObjectInfo();
        public int loadFromFile(String fileName);
        public int loadFromBuffer(com.sun.jna.Pointer buffer, int length);
        public int loadFromCurrentRepository();
        public int saveToFile(String fileName);
        public int saveToBuffer(com.sun.jna.Pointer buffer, int length, int[] realLength);
        public int saveToRepository(ICryptoRepository repository, String name);
        public int setAgreeKeyFromRepository(ICryptoRepository repository);
        public int setExchangeKey(ICryptoKey key);
        public int generateKey();
        public int getIV(byte[] iv, int length, int[] realLength);
        public int setIV(byte[] iv, int length);
        public int createFromBuffer(byte[] buffer, int length);
    }

    public static interface ICryptoKeyPairIntf extends IVersionedIntf
    {
        public com.sun.jna.Pointer getObjectInfo();
        public int loadFromFile(String fileName);
        public int loadFromBuffer(com.sun.jna.Pointer buffer, int length);
        public int loadFromRepository(ICryptoRepository repository, String name);
        public int loadFromCurrentRepository();
        public int saveToFile(String fileName);
        public int saveToBuffer(com.sun.jna.Pointer buffer, int length, int[] realLength);
        public int saveToRepository(ICryptoRepository repository, String name);
        public int setAgreeKeyFromRepository(ICryptoRepository repository);
        public int setExchangeKey(ICryptoKey key);
        public int generateKeyPair();
        public int getPublicKey(ICryptoKey key);
        public int createPublicKey(ICryptoKey[] key);
        public int deletePublicKey(ICryptoKey key);
    }

    public static interface ICryptoRandomFactoryIntf extends IVersionedIntf
    {
        public com.sun.jna.Pointer getObjectInfo();
        public int generateRandom(byte[] buffer, int length, ICryptoProvider providerName);
    }

    public static interface ICryptoHashFactoryIntf extends IVersionedIntf
    {
        public com.sun.jna.Pointer getObjectInfo();
        public int createHash(byte[] buffer, int bufferLength, byte[] hash, int hashLength, int[] realHashLength, boolean asString);
        public int setKeyForHash(ICryptoKey key);
    }

    public static interface ICryptoSymmetricFactoryIntf extends IVersionedIntf
    {
        public com.sun.jna.Pointer getObjectInfo();
        public int encrypt(byte[] data, int dataLength, byte[] cryptData, int cryptDataLength, int[] realCryptDataLength, ICryptoKey key);
        public int decrypt(byte[] cryptData, int cryptDataLength, byte[] data, int dataLength, int[] realDataLength, ICryptoKey key);
        public int createKey(ICryptoRepository repository, ICryptoKey[] key);
        public int deleteKey(ICryptoKey key);
    }

    public static interface ICryptoSignatureFactoryIntf extends IVersionedIntf
    {
        public com.sun.jna.Pointer getObjectInfo();
        public int createKeyPair(ICryptoRepository repository, ICryptoKeyPair[] key);
        public int deleteKeyPair(ICryptoKeyPair keyPair);
        public int sign(byte[] data, int dataLength, ICryptoSignature signature, ICryptoKeyPair privateKey);
        public int verifySign(byte[] data, int dataLength, ICryptoSignature signature, ICryptoKey publicKey);
        public int createSignature(ICryptoSignature[] signature);
        public int deleteSignature(ICryptoSignature signature);
    }

    public static interface ICryptoSignatureIntf extends IVersionedIntf
    {
        public com.sun.jna.Pointer getObjectInfo();
        public int saveToFile(String fileName);
        public int saveToBuffer(com.sun.jna.Pointer buffer, int length, int[] realLength);
        public int loadFromFile(String fileName);
        public int loadFromBuffer(com.sun.jna.Pointer buffer, int length);
    }

    public static interface ICryptoCertificateFactoryIntf extends IVersionedIntf
    {
        public com.sun.jna.Pointer getObjectInfo();
        public int createCertificate(ICryptoRepository repository, ICryptoCertificate[] certificate);
        public int deleteCertificate(ICryptoCertificate certificate);
        public int encrypt(byte[] data, int dataLength, byte[] cryptData, int cryptDataLength, int[] realCryptDataLength, ICryptoCertificate certificate);
        public int decrypt(byte[] cryptData, int cryptDataLength, byte[] data, int dataLength, int[] realDataLength, ICryptoRepository repository);
    }

    public static interface ICryptoCertificateIntf extends IVersionedIntf
    {
        public com.sun.jna.Pointer getObjectInfo();
        public int loadFromFile(String fileName);
        public int loadFromBuffer(com.sun.jna.Pointer buffer, int length);
        public int loadFromBinaryBuffer(com.sun.jna.Pointer buffer, int length);
        public int loadFromRepository(ICryptoRepository repository, String name);
        public int saveToFile(String fileName);
        public int saveToBuffer(com.sun.jna.Pointer buffer, int length, int[] realLength);
        public int saveToBinaryBuffer(com.sun.jna.Pointer buffer, int length, int[] realLength);
        public int verifyCertificate(ICryptoCertificate certificate);
        public int getId(byte[] id, int[] length);
        public int getIssuerName(byte[] issuerName, int[] length);
        public int getPublicKey(ICryptoKey key);
        public int getPublicKeyMethod(com.sun.jna.Pointer method, int[] length);
        public int createPublicKeyFromCertificate(ICryptoRepository repository, ICryptoKey[] key);
        public int deleteCertificatePublicKey(ICryptoKey key);
        public int getSerialNumber(byte[] serialNumber, int[] length);
        public int getOwnerName(com.sun.jna.Pointer ownerName, int[] length, String user_dn);
        public int getKeyContainer(com.sun.jna.Pointer container, int[] length);
    }

    public static interface ICryptoRepositoryIntf extends IVersionedIntf
    {
        public com.sun.jna.Pointer getObjectInfo();
        public int getRepositoryName(com.sun.jna.Pointer name, int length, int[] realLength);
        public int open(String path, int openMode, int repositoryLocation, int providerType);
        public int close();
        public int createPublicKey(int method, ICryptoKey[] key);
        public int getPublicKey(ICryptoKey key);
        public int deletePublicKey(ICryptoKey key);
        public boolean isOpened();
    }

    public static interface ICryptoProviderIntf extends IVersionedIntf
    {
        public com.sun.jna.Pointer getObjectInfo();
        public int createRepository(ICryptoRepository[] repository, int type, String pin);
        public int deleteRepository(ICryptoRepository repository);
    }

    public static interface IListCryptoObjectsIntf extends IVersionedIntf
    {
        public void list(CryptoObjectInfo[] objInfo);
    }

    public static interface ICryptoFactoryIntf extends IPluginBaseIntf
    {
        public void setTrace(boolean need);
        public ICryptoProvider getCryptoProvider(CryptoObjectInfo[] objInfo);
        public ICryptoRandomFactory getCryptoRandomFactory(CryptoObjectInfo[] objInfo);
        public ICryptoHashFactory getCryptoHashFactory(CryptoObjectInfo[] objInfo);
        public ICryptoSymmetricFactory getCryptoSymmetricFactory(CryptoObjectInfo[] objInfo);
        public ICryptoSignatureFactory getCryptoSignatureFactory(CryptoObjectInfo[] objInfo);
        public ICryptoCertificateFactory getCryptoCertificateFactory(CryptoObjectInfo[] objInfo);
        public int getCryptoObjects(int type, IListCryptoObjects callback);
    }

    public static interface ILdapPluginIntf extends IReferenceCountedIntf
    {
        public static int SYNC_RESULT_SUCCESS = 0;
        public static int SYNC_RESULT_NO_USER = 1;
        public static int SYNC_RESULT_ERROR = 2;
        public static int CERT_TEXT = 0;
        public static int CERT_BINARY = 1;
        public static int CERT_ERROR = 2;

        public void connect();
        public boolean is_connected();
        public boolean bind();
        public boolean bind_as(String user, String password);
        public boolean find_user(String name, com.sun.jna.Pointer password, com.sun.jna.Pointer mf_password, com.sun.jna.Pointer hash_alg);
        public boolean find_srp_user(String name, com.sun.jna.Pointer verifier, com.sun.jna.Pointer salt);
        public int get_certificate(String name, com.sun.jna.Pointer buffer, int[] buffer_length, String attr_name);
        public boolean get_user_attr(String name, String attr, com.sun.jna.Pointer value);
        public boolean get_policy(String name, com.sun.jna.Pointer policy, ISC_TIMESTAMP[] passwd_time, int[] failed_count, ISC_TIMESTAMP[] access_time);
        public boolean set_policy(String name, String policy, ISC_TIMESTAMP[] passwd_time, int[] failed_count, ISC_TIMESTAMP[] access_time);
        public boolean get_password_history(String name, com.sun.jna.Pointer buffer, int[] buffer_length);
        public void find_user_groups(com.sun.jna.Pointer userId);
        public int change_legacy_password(String name, String password);
        public int change_mf_password(String name, String password, com.sun.jna.Pointer hash);
        public int change_srp_password(String name, String password, com.sun.jna.Pointer verifier, com.sun.jna.Pointer salt);
    }

    public static interface ILdapFactoryIntf extends IPluginBaseIntf
    {
        public ILdapPlugin getLdapPlugin(IStatus status) throws FbException;
    }

    public static class IVersioned extends com.sun.jna.Structure implements IVersionedIntf
    {
        public static class VTable extends com.sun.jna.Structure implements com.sun.jna.Structure.ByReference
        {
            public com.sun.jna.Pointer cloopDummy;
            public com.sun.jna.Pointer version;

            public VTable(com.sun.jna.Pointer pointer)
            {
                super(pointer);
            }

            public VTable(final IVersionedIntf obj)
            {
            }

            public VTable()
            {
            }

            @Override
            protected java.util.List<String> getFieldOrder()
            {
                java.util.List<String> fields = new java.util.ArrayList<String>();
                fields.addAll(java.util.Arrays.asList("cloopDummy", "version"));
                return fields;
            }
        }

        public com.sun.jna.Pointer cloopDummy;
        public com.sun.jna.Pointer cloopVTable;
        protected volatile VTable vTable;

        @Override
        protected java.util.List<String> getFieldOrder()
        {
            java.util.List<String> fields = new java.util.ArrayList<String>();
            fields.addAll(java.util.Arrays.asList("cloopDummy", "cloopVTable"));
            return fields;
        }

        @SuppressWarnings("unchecked")
        public final <T extends VTable> T getVTable()
        {
            if (vTable == null)
            {
                synchronized (cloopVTable)
                {
                    if (vTable == null)
                    {
                        vTable = createVTable();
                        vTable.read();
                    }
                }
            }

            return (T) vTable;
        }

        public IVersioned()
        {
        }

        public IVersioned(IVersionedIntf obj)
        {
            vTable = new VTable(obj);
            vTable.write();
            cloopVTable = vTable.getPointer();
            write();
        }

        protected VTable createVTable()
        {
            return new VTable(cloopVTable);
        }
    }

    public static class IReferenceCounted extends IVersioned implements IReferenceCountedIntf
    {
        public static class VTable extends IVersioned.VTable
        {
            public static interface Callback_addRef extends com.sun.jna.Callback
            {
                public void invoke(IReferenceCounted self);
            }

            public static interface Callback_release extends com.sun.jna.Callback
            {
                public int invoke(IReferenceCounted self);
            }

            public VTable(com.sun.jna.Pointer pointer)
            {
                super(pointer);
            }

            public VTable(final IReferenceCountedIntf obj)
            {
                super(obj);

                addRef = new Callback_addRef() {
                    @Override
                    public void invoke(IReferenceCounted self)
                    {
                        obj.addRef();
                    }
                };

                release = new Callback_release() {
                    @Override
                    public int invoke(IReferenceCounted self)
                    {
                        return obj.release();
                    }
                };
            }

            public VTable()
            {
            }

            public Callback_addRef addRef;
            public Callback_release release;

            @Override
            protected java.util.List<String> getFieldOrder()
            {
                java.util.List<String> fields = super.getFieldOrder();
                fields.addAll(java.util.Arrays.asList("addRef", "release"));
                return fields;
            }
        }

        public IReferenceCounted()
        {
        }

        public IReferenceCounted(IReferenceCountedIntf obj)
        {
            vTable = new VTable(obj);
            vTable.write();
            cloopVTable = vTable.getPointer();
            write();
        }

        @Override
        protected VTable createVTable()
        {
            return new VTable(cloopVTable);
        }

        public void addRef()
        {
            VTable vTable = getVTable();
            vTable.addRef.invoke(this);
        }

        public int release()
        {
            VTable vTable = getVTable();
            int result = vTable.release.invoke(this);
            return result;
        }
    }

    public static class IDisposable extends IVersioned implements IDisposableIntf
    {
        public static class VTable extends IVersioned.VTable
        {
            public static interface Callback_dispose extends com.sun.jna.Callback
            {
                public void invoke(IDisposable self);
            }

            public VTable(com.sun.jna.Pointer pointer)
            {
                super(pointer);
            }

            public VTable(final IDisposableIntf obj)
            {
                super(obj);

                dispose = new Callback_dispose() {
                    @Override
                    public void invoke(IDisposable self)
                    {
                        obj.dispose();
                    }
                };
            }

            public VTable()
            {
            }

            public Callback_dispose dispose;

            @Override
            protected java.util.List<String> getFieldOrder()
            {
                java.util.List<String> fields = super.getFieldOrder();
                fields.addAll(java.util.Arrays.asList("dispose"));
                return fields;
            }
        }

        public IDisposable()
        {
        }

        public IDisposable(IDisposableIntf obj)
        {
            vTable = new VTable(obj);
            vTable.write();
            cloopVTable = vTable.getPointer();
            write();
        }

        @Override
        protected VTable createVTable()
        {
            return new VTable(cloopVTable);
        }

        public void dispose()
        {
            VTable vTable = getVTable();
            vTable.dispose.invoke(this);
        }
    }

    public static class IStatus extends IDisposable implements IStatusIntf
    {
        public static class VTable extends IDisposable.VTable
        {
            public static interface Callback_init extends com.sun.jna.Callback
            {
                public void invoke(IStatus self);
            }

            public static interface Callback_getState extends com.sun.jna.Callback
            {
                public int invoke(IStatus self);
            }

            public static interface Callback_setErrors2 extends com.sun.jna.Callback
            {
                public void invoke(IStatus self, int length, com.sun.jna.Pointer[] value);
            }

            public static interface Callback_setWarnings2 extends com.sun.jna.Callback
            {
                public void invoke(IStatus self, int length, com.sun.jna.Pointer[] value);
            }

            public static interface Callback_setErrors extends com.sun.jna.Callback
            {
                public void invoke(IStatus self, com.sun.jna.Pointer[] value);
            }

            public static interface Callback_setWarnings extends com.sun.jna.Callback
            {
                public void invoke(IStatus self, com.sun.jna.Pointer[] value);
            }

            public static interface Callback_getErrors extends com.sun.jna.Callback
            {
                public com.sun.jna.Pointer invoke(IStatus self);
            }

            public static interface Callback_getWarnings extends com.sun.jna.Callback
            {
                public com.sun.jna.Pointer invoke(IStatus self);
            }

            public static interface Callback_clone extends com.sun.jna.Callback
            {
                public IStatus invoke(IStatus self);
            }

            public VTable(com.sun.jna.Pointer pointer)
            {
                super(pointer);
            }

            public VTable(final IStatusIntf obj)
            {
                super(obj);

                init = new Callback_init() {
                    @Override
                    public void invoke(IStatus self)
                    {
                        obj.init();
                    }
                };

                getState = new Callback_getState() {
                    @Override
                    public int invoke(IStatus self)
                    {
                        return obj.getState();
                    }
                };

                setErrors2 = new Callback_setErrors2() {
                    @Override
                    public void invoke(IStatus self, int length, com.sun.jna.Pointer[] value)
                    {
                        obj.setErrors2(length, value);
                    }
                };

                setWarnings2 = new Callback_setWarnings2() {
                    @Override
                    public void invoke(IStatus self, int length, com.sun.jna.Pointer[] value)
                    {
                        obj.setWarnings2(length, value);
                    }
                };

                setErrors = new Callback_setErrors() {
                    @Override
                    public void invoke(IStatus self, com.sun.jna.Pointer[] value)
                    {
                        obj.setErrors(value);
                    }
                };

                setWarnings = new Callback_setWarnings() {
                    @Override
                    public void invoke(IStatus self, com.sun.jna.Pointer[] value)
                    {
                        obj.setWarnings(value);
                    }
                };

                getErrors = new Callback_getErrors() {
                    @Override
                    public com.sun.jna.Pointer invoke(IStatus self)
                    {
                        return obj.getErrors();
                    }
                };

                getWarnings = new Callback_getWarnings() {
                    @Override
                    public com.sun.jna.Pointer invoke(IStatus self)
                    {
                        return obj.getWarnings();
                    }
                };

                clone = new Callback_clone() {
                    @Override
                    public IStatus invoke(IStatus self)
                    {
                        return obj.clone();
                    }
                };
            }

            public VTable()
            {
            }

            public Callback_init init;
            public Callback_getState getState;
            public Callback_setErrors2 setErrors2;
            public Callback_setWarnings2 setWarnings2;
            public Callback_setErrors setErrors;
            public Callback_setWarnings setWarnings;
            public Callback_getErrors getErrors;
            public Callback_getWarnings getWarnings;
            public Callback_clone clone;

            @Override
            protected java.util.List<String> getFieldOrder()
            {
                java.util.List<String> fields = super.getFieldOrder();
                fields.addAll(java.util.Arrays.asList("init", "getState", "setErrors2", "setWarnings2", "setErrors", "setWarnings", "getErrors", "getWarnings", "clone"));
                return fields;
            }
        }

        public IStatus()
        {
        }

        public IStatus(IStatusIntf obj)
        {
            vTable = new VTable(obj);
            vTable.write();
            cloopVTable = vTable.getPointer();
            write();
        }

        @Override
        protected VTable createVTable()
        {
            return new VTable(cloopVTable);
        }

        public void init()
        {
            VTable vTable = getVTable();
            vTable.init.invoke(this);
        }

        public int getState()
        {
            VTable vTable = getVTable();
            int result = vTable.getState.invoke(this);
            return result;
        }

        public void setErrors2(int length, com.sun.jna.Pointer[] value)
        {
            VTable vTable = getVTable();
            vTable.setErrors2.invoke(this, length, value);
        }

        public void setWarnings2(int length, com.sun.jna.Pointer[] value)
        {
            VTable vTable = getVTable();
            vTable.setWarnings2.invoke(this, length, value);
        }

        public void setErrors(com.sun.jna.Pointer[] value)
        {
            VTable vTable = getVTable();
            vTable.setErrors.invoke(this, value);
        }

        public void setWarnings(com.sun.jna.Pointer[] value)
        {
            VTable vTable = getVTable();
            vTable.setWarnings.invoke(this, value);
        }

        public com.sun.jna.Pointer getErrors()
        {
            VTable vTable = getVTable();
            com.sun.jna.Pointer result = vTable.getErrors.invoke(this);
            return result;
        }

        public com.sun.jna.Pointer getWarnings()
        {
            VTable vTable = getVTable();
            com.sun.jna.Pointer result = vTable.getWarnings.invoke(this);
            return result;
        }

        public IStatus clone()
        {
            VTable vTable = getVTable();
            IStatus result = vTable.clone.invoke(this);
            return result;
        }
    }

    public static class IMaster extends IVersioned implements IMasterIntf
    {
        public static class VTable extends IVersioned.VTable
        {
            public static interface Callback_getStatus extends com.sun.jna.Callback
            {
                public IStatus invoke(IMaster self);
            }

            public static interface Callback_getDispatcher extends com.sun.jna.Callback
            {
                public IProvider invoke(IMaster self);
            }

            public static interface Callback_getPluginManager extends com.sun.jna.Callback
            {
                public IPluginManager invoke(IMaster self);
            }

            public static interface Callback_getTimerControl extends com.sun.jna.Callback
            {
                public ITimerControl invoke(IMaster self);
            }

            public static interface Callback_getDtc extends com.sun.jna.Callback
            {
                public IDtc invoke(IMaster self);
            }

            public static interface Callback_registerAttachment extends com.sun.jna.Callback
            {
                public IAttachment invoke(IMaster self, IProvider provider, IAttachment attachment);
            }

            public static interface Callback_registerTransaction extends com.sun.jna.Callback
            {
                public ITransaction invoke(IMaster self, IAttachment attachment, ITransaction transaction);
            }

            public static interface Callback_getMetadataBuilder extends com.sun.jna.Callback
            {
                public IMetadataBuilder invoke(IMaster self, IStatus status, int fieldCount);
            }

            public static interface Callback_serverMode extends com.sun.jna.Callback
            {
                public int invoke(IMaster self, int mode);
            }

            public static interface Callback_getUtilInterface extends com.sun.jna.Callback
            {
                public IUtil invoke(IMaster self);
            }

            public static interface Callback_getConfigManager extends com.sun.jna.Callback
            {
                public IConfigManager invoke(IMaster self);
            }

            public static interface Callback_getProcessExiting extends com.sun.jna.Callback
            {
                public boolean invoke(IMaster self);
            }

            public VTable(com.sun.jna.Pointer pointer)
            {
                super(pointer);
            }

            public VTable(final IMasterIntf obj)
            {
                super(obj);

                getStatus = new Callback_getStatus() {
                    @Override
                    public IStatus invoke(IMaster self)
                    {
                        return obj.getStatus();
                    }
                };

                getDispatcher = new Callback_getDispatcher() {
                    @Override
                    public IProvider invoke(IMaster self)
                    {
                        return obj.getDispatcher();
                    }
                };

                getPluginManager = new Callback_getPluginManager() {
                    @Override
                    public IPluginManager invoke(IMaster self)
                    {
                        return obj.getPluginManager();
                    }
                };

                getTimerControl = new Callback_getTimerControl() {
                    @Override
                    public ITimerControl invoke(IMaster self)
                    {
                        return obj.getTimerControl();
                    }
                };

                getDtc = new Callback_getDtc() {
                    @Override
                    public IDtc invoke(IMaster self)
                    {
                        return obj.getDtc();
                    }
                };

                registerAttachment = new Callback_registerAttachment() {
                    @Override
                    public IAttachment invoke(IMaster self, IProvider provider, IAttachment attachment)
                    {
                        return obj.registerAttachment(provider, attachment);
                    }
                };

                registerTransaction = new Callback_registerTransaction() {
                    @Override
                    public ITransaction invoke(IMaster self, IAttachment attachment, ITransaction transaction)
                    {
                        return obj.registerTransaction(attachment, transaction);
                    }
                };

                getMetadataBuilder = new Callback_getMetadataBuilder() {
                    @Override
                    public IMetadataBuilder invoke(IMaster self, IStatus status, int fieldCount)
                    {
                        try
                        {
                            return obj.getMetadataBuilder(status, fieldCount);
                        }
                        catch (Throwable t)
                        {
                            FbException.catchException(status, t);
                            return null;
                        }
                    }
                };

                serverMode = new Callback_serverMode() {
                    @Override
                    public int invoke(IMaster self, int mode)
                    {
                        return obj.serverMode(mode);
                    }
                };

                getUtilInterface = new Callback_getUtilInterface() {
                    @Override
                    public IUtil invoke(IMaster self)
                    {
                        return obj.getUtilInterface();
                    }
                };

                getConfigManager = new Callback_getConfigManager() {
                    @Override
                    public IConfigManager invoke(IMaster self)
                    {
                        return obj.getConfigManager();
                    }
                };

                getProcessExiting = new Callback_getProcessExiting() {
                    @Override
                    public boolean invoke(IMaster self)
                    {
                        return obj.getProcessExiting();
                    }
                };
            }

            public VTable()
            {
            }

            public Callback_getStatus getStatus;
            public Callback_getDispatcher getDispatcher;
            public Callback_getPluginManager getPluginManager;
            public Callback_getTimerControl getTimerControl;
            public Callback_getDtc getDtc;
            public Callback_registerAttachment registerAttachment;
            public Callback_registerTransaction registerTransaction;
            public Callback_getMetadataBuilder getMetadataBuilder;
            public Callback_serverMode serverMode;
            public Callback_getUtilInterface getUtilInterface;
            public Callback_getConfigManager getConfigManager;
            public Callback_getProcessExiting getProcessExiting;

            @Override
            protected java.util.List<String> getFieldOrder()
            {
                java.util.List<String> fields = super.getFieldOrder();
                fields.addAll(java.util.Arrays.asList("getStatus", "getDispatcher", "getPluginManager", "getTimerControl", "getDtc", "registerAttachment", "registerTransaction", "getMetadataBuilder", "serverMode", "getUtilInterface", "getConfigManager", "getProcessExiting"));
                return fields;
            }
        }

        public IMaster()
        {
        }

        public IMaster(IMasterIntf obj)
        {
            vTable = new VTable(obj);
            vTable.write();
            cloopVTable = vTable.getPointer();
            write();
        }

        @Override
        protected VTable createVTable()
        {
            return new VTable(cloopVTable);
        }

        public IStatus getStatus()
        {
            VTable vTable = getVTable();
            IStatus result = vTable.getStatus.invoke(this);
            return result;
        }

        public IProvider getDispatcher()
        {
            VTable vTable = getVTable();
            IProvider result = vTable.getDispatcher.invoke(this);
            return result;
        }

        public IPluginManager getPluginManager()
        {
            VTable vTable = getVTable();
            IPluginManager result = vTable.getPluginManager.invoke(this);
            return result;
        }

        public ITimerControl getTimerControl()
        {
            VTable vTable = getVTable();
            ITimerControl result = vTable.getTimerControl.invoke(this);
            return result;
        }

        public IDtc getDtc()
        {
            VTable vTable = getVTable();
            IDtc result = vTable.getDtc.invoke(this);
            return result;
        }

        public IAttachment registerAttachment(IProvider provider, IAttachment attachment)
        {
            VTable vTable = getVTable();
            IAttachment result = vTable.registerAttachment.invoke(this, provider, attachment);
            return result;
        }

        public ITransaction registerTransaction(IAttachment attachment, ITransaction transaction)
        {
            VTable vTable = getVTable();
            ITransaction result = vTable.registerTransaction.invoke(this, attachment, transaction);
            return result;
        }

        public IMetadataBuilder getMetadataBuilder(IStatus status, int fieldCount) throws FbException
        {
            VTable vTable = getVTable();
            IMetadataBuilder result = vTable.getMetadataBuilder.invoke(this, status, fieldCount);
            FbException.checkException(status);
            return result;
        }

        public int serverMode(int mode)
        {
            VTable vTable = getVTable();
            int result = vTable.serverMode.invoke(this, mode);
            return result;
        }

        public IUtil getUtilInterface()
        {
            VTable vTable = getVTable();
            IUtil result = vTable.getUtilInterface.invoke(this);
            return result;
        }

        public IConfigManager getConfigManager()
        {
            VTable vTable = getVTable();
            IConfigManager result = vTable.getConfigManager.invoke(this);
            return result;
        }

        public boolean getProcessExiting()
        {
            VTable vTable = getVTable();
            boolean result = vTable.getProcessExiting.invoke(this);
            return result;
        }
    }

    public static class IPluginBase extends IReferenceCounted implements IPluginBaseIntf
    {
        public static class VTable extends IReferenceCounted.VTable
        {
            public static interface Callback_setOwner extends com.sun.jna.Callback
            {
                public void invoke(IPluginBase self, IReferenceCounted r);
            }

            public static interface Callback_getOwner extends com.sun.jna.Callback
            {
                public IReferenceCounted invoke(IPluginBase self);
            }

            public VTable(com.sun.jna.Pointer pointer)
            {
                super(pointer);
            }

            public VTable(final IPluginBaseIntf obj)
            {
                super(obj);

                setOwner = new Callback_setOwner() {
                    @Override
                    public void invoke(IPluginBase self, IReferenceCounted r)
                    {
                        obj.setOwner(r);
                    }
                };

                getOwner = new Callback_getOwner() {
                    @Override
                    public IReferenceCounted invoke(IPluginBase self)
                    {
                        return obj.getOwner();
                    }
                };
            }

            public VTable()
            {
            }

            public Callback_setOwner setOwner;
            public Callback_getOwner getOwner;

            @Override
            protected java.util.List<String> getFieldOrder()
            {
                java.util.List<String> fields = super.getFieldOrder();
                fields.addAll(java.util.Arrays.asList("setOwner", "getOwner"));
                return fields;
            }
        }

        public IPluginBase()
        {
        }

        public IPluginBase(IPluginBaseIntf obj)
        {
            vTable = new VTable(obj);
            vTable.write();
            cloopVTable = vTable.getPointer();
            write();
        }

        @Override
        protected VTable createVTable()
        {
            return new VTable(cloopVTable);
        }

        public void setOwner(IReferenceCounted r)
        {
            VTable vTable = getVTable();
            vTable.setOwner.invoke(this, r);
        }

        public IReferenceCounted getOwner()
        {
            VTable vTable = getVTable();
            IReferenceCounted result = vTable.getOwner.invoke(this);
            return result;
        }
    }

    public static class IPluginSet extends IReferenceCounted implements IPluginSetIntf
    {
        public static class VTable extends IReferenceCounted.VTable
        {
            public static interface Callback_getName extends com.sun.jna.Callback
            {
                public String invoke(IPluginSet self);
            }

            public static interface Callback_getModuleName extends com.sun.jna.Callback
            {
                public String invoke(IPluginSet self);
            }

            public static interface Callback_getPlugin extends com.sun.jna.Callback
            {
                public IPluginBase invoke(IPluginSet self, IStatus status);
            }

            public static interface Callback_next extends com.sun.jna.Callback
            {
                public void invoke(IPluginSet self, IStatus status);
            }

            public static interface Callback_set extends com.sun.jna.Callback
            {
                public void invoke(IPluginSet self, IStatus status, String s);
            }

            public VTable(com.sun.jna.Pointer pointer)
            {
                super(pointer);
            }

            public VTable(final IPluginSetIntf obj)
            {
                super(obj);

                getName = new Callback_getName() {
                    @Override
                    public String invoke(IPluginSet self)
                    {
                        return obj.getName();
                    }
                };

                getModuleName = new Callback_getModuleName() {
                    @Override
                    public String invoke(IPluginSet self)
                    {
                        return obj.getModuleName();
                    }
                };

                getPlugin = new Callback_getPlugin() {
                    @Override
                    public IPluginBase invoke(IPluginSet self, IStatus status)
                    {
                        try
                        {
                            return obj.getPlugin(status);
                        }
                        catch (Throwable t)
                        {
                            FbException.catchException(status, t);
                            return null;
                        }
                    }
                };

                next = new Callback_next() {
                    @Override
                    public void invoke(IPluginSet self, IStatus status)
                    {
                        try
                        {
                            obj.next(status);
                        }
                        catch (Throwable t)
                        {
                            FbException.catchException(status, t);
                        }
                    }
                };

                set = new Callback_set() {
                    @Override
                    public void invoke(IPluginSet self, IStatus status, String s)
                    {
                        try
                        {
                            obj.set(status, s);
                        }
                        catch (Throwable t)
                        {
                            FbException.catchException(status, t);
                        }
                    }
                };
            }

            public VTable()
            {
            }

            public Callback_getName getName;
            public Callback_getModuleName getModuleName;
            public Callback_getPlugin getPlugin;
            public Callback_next next;
            public Callback_set set;

            @Override
            protected java.util.List<String> getFieldOrder()
            {
                java.util.List<String> fields = super.getFieldOrder();
                fields.addAll(java.util.Arrays.asList("getName", "getModuleName", "getPlugin", "next", "set"));
                return fields;
            }
        }

        public IPluginSet()
        {
        }

        public IPluginSet(IPluginSetIntf obj)
        {
            vTable = new VTable(obj);
            vTable.write();
            cloopVTable = vTable.getPointer();
            write();
        }

        @Override
        protected VTable createVTable()
        {
            return new VTable(cloopVTable);
        }

        public String getName()
        {
            VTable vTable = getVTable();
            String result = vTable.getName.invoke(this);
            return result;
        }

        public String getModuleName()
        {
            VTable vTable = getVTable();
            String result = vTable.getModuleName.invoke(this);
            return result;
        }

        public IPluginBase getPlugin(IStatus status) throws FbException
        {
            VTable vTable = getVTable();
            IPluginBase result = vTable.getPlugin.invoke(this, status);
            FbException.checkException(status);
            return result;
        }

        public void next(IStatus status) throws FbException
        {
            VTable vTable = getVTable();
            vTable.next.invoke(this, status);
            FbException.checkException(status);
        }

        public void set(IStatus status, String s) throws FbException
        {
            VTable vTable = getVTable();
            vTable.set.invoke(this, status, s);
            FbException.checkException(status);
        }
    }

    public static class IConfigEntry extends IReferenceCounted implements IConfigEntryIntf
    {
        public static class VTable extends IReferenceCounted.VTable
        {
            public static interface Callback_getName extends com.sun.jna.Callback
            {
                public String invoke(IConfigEntry self);
            }

            public static interface Callback_getValue extends com.sun.jna.Callback
            {
                public String invoke(IConfigEntry self);
            }

            public static interface Callback_getIntValue extends com.sun.jna.Callback
            {
                public long invoke(IConfigEntry self);
            }

            public static interface Callback_getBoolValue extends com.sun.jna.Callback
            {
                public boolean invoke(IConfigEntry self);
            }

            public static interface Callback_getSubConfig extends com.sun.jna.Callback
            {
                public IConfig invoke(IConfigEntry self, IStatus status);
            }

            public VTable(com.sun.jna.Pointer pointer)
            {
                super(pointer);
            }

            public VTable(final IConfigEntryIntf obj)
            {
                super(obj);

                getName = new Callback_getName() {
                    @Override
                    public String invoke(IConfigEntry self)
                    {
                        return obj.getName();
                    }
                };

                getValue = new Callback_getValue() {
                    @Override
                    public String invoke(IConfigEntry self)
                    {
                        return obj.getValue();
                    }
                };

                getIntValue = new Callback_getIntValue() {
                    @Override
                    public long invoke(IConfigEntry self)
                    {
                        return obj.getIntValue();
                    }
                };

                getBoolValue = new Callback_getBoolValue() {
                    @Override
                    public boolean invoke(IConfigEntry self)
                    {
                        return obj.getBoolValue();
                    }
                };

                getSubConfig = new Callback_getSubConfig() {
                    @Override
                    public IConfig invoke(IConfigEntry self, IStatus status)
                    {
                        try
                        {
                            return obj.getSubConfig(status);
                        }
                        catch (Throwable t)
                        {
                            FbException.catchException(status, t);
                            return null;
                        }
                    }
                };
            }

            public VTable()
            {
            }

            public Callback_getName getName;
            public Callback_getValue getValue;
            public Callback_getIntValue getIntValue;
            public Callback_getBoolValue getBoolValue;
            public Callback_getSubConfig getSubConfig;

            @Override
            protected java.util.List<String> getFieldOrder()
            {
                java.util.List<String> fields = super.getFieldOrder();
                fields.addAll(java.util.Arrays.asList("getName", "getValue", "getIntValue", "getBoolValue", "getSubConfig"));
                return fields;
            }
        }

        public IConfigEntry()
        {
        }

        public IConfigEntry(IConfigEntryIntf obj)
        {
            vTable = new VTable(obj);
            vTable.write();
            cloopVTable = vTable.getPointer();
            write();
        }

        @Override
        protected VTable createVTable()
        {
            return new VTable(cloopVTable);
        }

        public String getName()
        {
            VTable vTable = getVTable();
            String result = vTable.getName.invoke(this);
            return result;
        }

        public String getValue()
        {
            VTable vTable = getVTable();
            String result = vTable.getValue.invoke(this);
            return result;
        }

        public long getIntValue()
        {
            VTable vTable = getVTable();
            long result = vTable.getIntValue.invoke(this);
            return result;
        }

        public boolean getBoolValue()
        {
            VTable vTable = getVTable();
            boolean result = vTable.getBoolValue.invoke(this);
            return result;
        }

        public IConfig getSubConfig(IStatus status) throws FbException
        {
            VTable vTable = getVTable();
            IConfig result = vTable.getSubConfig.invoke(this, status);
            FbException.checkException(status);
            return result;
        }
    }

    public static class IConfig extends IReferenceCounted implements IConfigIntf
    {
        public static class VTable extends IReferenceCounted.VTable
        {
            public static interface Callback_find extends com.sun.jna.Callback
            {
                public IConfigEntry invoke(IConfig self, IStatus status, String name);
            }

            public static interface Callback_findValue extends com.sun.jna.Callback
            {
                public IConfigEntry invoke(IConfig self, IStatus status, String name, String value);
            }

            public static interface Callback_findPos extends com.sun.jna.Callback
            {
                public IConfigEntry invoke(IConfig self, IStatus status, String name, int pos);
            }

            public VTable(com.sun.jna.Pointer pointer)
            {
                super(pointer);
            }

            public VTable(final IConfigIntf obj)
            {
                super(obj);

                find = new Callback_find() {
                    @Override
                    public IConfigEntry invoke(IConfig self, IStatus status, String name)
                    {
                        try
                        {
                            return obj.find(status, name);
                        }
                        catch (Throwable t)
                        {
                            FbException.catchException(status, t);
                            return null;
                        }
                    }
                };

                findValue = new Callback_findValue() {
                    @Override
                    public IConfigEntry invoke(IConfig self, IStatus status, String name, String value)
                    {
                        try
                        {
                            return obj.findValue(status, name, value);
                        }
                        catch (Throwable t)
                        {
                            FbException.catchException(status, t);
                            return null;
                        }
                    }
                };

                findPos = new Callback_findPos() {
                    @Override
                    public IConfigEntry invoke(IConfig self, IStatus status, String name, int pos)
                    {
                        try
                        {
                            return obj.findPos(status, name, pos);
                        }
                        catch (Throwable t)
                        {
                            FbException.catchException(status, t);
                            return null;
                        }
                    }
                };
            }

            public VTable()
            {
            }

            public Callback_find find;
            public Callback_findValue findValue;
            public Callback_findPos findPos;

            @Override
            protected java.util.List<String> getFieldOrder()
            {
                java.util.List<String> fields = super.getFieldOrder();
                fields.addAll(java.util.Arrays.asList("find", "findValue", "findPos"));
                return fields;
            }
        }

        public IConfig()
        {
        }

        public IConfig(IConfigIntf obj)
        {
            vTable = new VTable(obj);
            vTable.write();
            cloopVTable = vTable.getPointer();
            write();
        }

        @Override
        protected VTable createVTable()
        {
            return new VTable(cloopVTable);
        }

        public IConfigEntry find(IStatus status, String name) throws FbException
        {
            VTable vTable = getVTable();
            IConfigEntry result = vTable.find.invoke(this, status, name);
            FbException.checkException(status);
            return result;
        }

        public IConfigEntry findValue(IStatus status, String name, String value) throws FbException
        {
            VTable vTable = getVTable();
            IConfigEntry result = vTable.findValue.invoke(this, status, name, value);
            FbException.checkException(status);
            return result;
        }

        public IConfigEntry findPos(IStatus status, String name, int pos) throws FbException
        {
            VTable vTable = getVTable();
            IConfigEntry result = vTable.findPos.invoke(this, status, name, pos);
            FbException.checkException(status);
            return result;
        }
    }

    public static class IFirebirdConf extends IReferenceCounted implements IFirebirdConfIntf
    {
        public static class VTable extends IReferenceCounted.VTable
        {
            public static interface Callback_getKey extends com.sun.jna.Callback
            {
                public int invoke(IFirebirdConf self, String name);
            }

            public static interface Callback_asInteger extends com.sun.jna.Callback
            {
                public long invoke(IFirebirdConf self, int key);
            }

            public static interface Callback_asString extends com.sun.jna.Callback
            {
                public String invoke(IFirebirdConf self, int key);
            }

            public static interface Callback_asBoolean extends com.sun.jna.Callback
            {
                public boolean invoke(IFirebirdConf self, int key);
            }

            public VTable(com.sun.jna.Pointer pointer)
            {
                super(pointer);
            }

            public VTable(final IFirebirdConfIntf obj)
            {
                super(obj);

                getKey = new Callback_getKey() {
                    @Override
                    public int invoke(IFirebirdConf self, String name)
                    {
                        return obj.getKey(name);
                    }
                };

                asInteger = new Callback_asInteger() {
                    @Override
                    public long invoke(IFirebirdConf self, int key)
                    {
                        return obj.asInteger(key);
                    }
                };

                asString = new Callback_asString() {
                    @Override
                    public String invoke(IFirebirdConf self, int key)
                    {
                        return obj.asString(key);
                    }
                };

                asBoolean = new Callback_asBoolean() {
                    @Override
                    public boolean invoke(IFirebirdConf self, int key)
                    {
                        return obj.asBoolean(key);
                    }
                };
            }

            public VTable()
            {
            }

            public Callback_getKey getKey;
            public Callback_asInteger asInteger;
            public Callback_asString asString;
            public Callback_asBoolean asBoolean;

            @Override
            protected java.util.List<String> getFieldOrder()
            {
                java.util.List<String> fields = super.getFieldOrder();
                fields.addAll(java.util.Arrays.asList("getKey", "asInteger", "asString", "asBoolean"));
                return fields;
            }
        }

        public IFirebirdConf()
        {
        }

        public IFirebirdConf(IFirebirdConfIntf obj)
        {
            vTable = new VTable(obj);
            vTable.write();
            cloopVTable = vTable.getPointer();
            write();
        }

        @Override
        protected VTable createVTable()
        {
            return new VTable(cloopVTable);
        }

        public int getKey(String name)
        {
            VTable vTable = getVTable();
            int result = vTable.getKey.invoke(this, name);
            return result;
        }

        public long asInteger(int key)
        {
            VTable vTable = getVTable();
            long result = vTable.asInteger.invoke(this, key);
            return result;
        }

        public String asString(int key)
        {
            VTable vTable = getVTable();
            String result = vTable.asString.invoke(this, key);
            return result;
        }

        public boolean asBoolean(int key)
        {
            VTable vTable = getVTable();
            boolean result = vTable.asBoolean.invoke(this, key);
            return result;
        }
    }

    public static class IPluginConfig extends IReferenceCounted implements IPluginConfigIntf
    {
        public static class VTable extends IReferenceCounted.VTable
        {
            public static interface Callback_getConfigFileName extends com.sun.jna.Callback
            {
                public String invoke(IPluginConfig self);
            }

            public static interface Callback_getDefaultConfig extends com.sun.jna.Callback
            {
                public IConfig invoke(IPluginConfig self, IStatus status);
            }

            public static interface Callback_getFirebirdConf extends com.sun.jna.Callback
            {
                public IFirebirdConf invoke(IPluginConfig self, IStatus status);
            }

            public static interface Callback_setReleaseDelay extends com.sun.jna.Callback
            {
                public void invoke(IPluginConfig self, IStatus status, long microSeconds);
            }

            public VTable(com.sun.jna.Pointer pointer)
            {
                super(pointer);
            }

            public VTable(final IPluginConfigIntf obj)
            {
                super(obj);

                getConfigFileName = new Callback_getConfigFileName() {
                    @Override
                    public String invoke(IPluginConfig self)
                    {
                        return obj.getConfigFileName();
                    }
                };

                getDefaultConfig = new Callback_getDefaultConfig() {
                    @Override
                    public IConfig invoke(IPluginConfig self, IStatus status)
                    {
                        try
                        {
                            return obj.getDefaultConfig(status);
                        }
                        catch (Throwable t)
                        {
                            FbException.catchException(status, t);
                            return null;
                        }
                    }
                };

                getFirebirdConf = new Callback_getFirebirdConf() {
                    @Override
                    public IFirebirdConf invoke(IPluginConfig self, IStatus status)
                    {
                        try
                        {
                            return obj.getFirebirdConf(status);
                        }
                        catch (Throwable t)
                        {
                            FbException.catchException(status, t);
                            return null;
                        }
                    }
                };

                setReleaseDelay = new Callback_setReleaseDelay() {
                    @Override
                    public void invoke(IPluginConfig self, IStatus status, long microSeconds)
                    {
                        try
                        {
                            obj.setReleaseDelay(status, microSeconds);
                        }
                        catch (Throwable t)
                        {
                            FbException.catchException(status, t);
                        }
                    }
                };
            }

            public VTable()
            {
            }

            public Callback_getConfigFileName getConfigFileName;
            public Callback_getDefaultConfig getDefaultConfig;
            public Callback_getFirebirdConf getFirebirdConf;
            public Callback_setReleaseDelay setReleaseDelay;

            @Override
            protected java.util.List<String> getFieldOrder()
            {
                java.util.List<String> fields = super.getFieldOrder();
                fields.addAll(java.util.Arrays.asList("getConfigFileName", "getDefaultConfig", "getFirebirdConf", "setReleaseDelay"));
                return fields;
            }
        }

        public IPluginConfig()
        {
        }

        public IPluginConfig(IPluginConfigIntf obj)
        {
            vTable = new VTable(obj);
            vTable.write();
            cloopVTable = vTable.getPointer();
            write();
        }

        @Override
        protected VTable createVTable()
        {
            return new VTable(cloopVTable);
        }

        public String getConfigFileName()
        {
            VTable vTable = getVTable();
            String result = vTable.getConfigFileName.invoke(this);
            return result;
        }

        public IConfig getDefaultConfig(IStatus status) throws FbException
        {
            VTable vTable = getVTable();
            IConfig result = vTable.getDefaultConfig.invoke(this, status);
            FbException.checkException(status);
            return result;
        }

        public IFirebirdConf getFirebirdConf(IStatus status) throws FbException
        {
            VTable vTable = getVTable();
            IFirebirdConf result = vTable.getFirebirdConf.invoke(this, status);
            FbException.checkException(status);
            return result;
        }

        public void setReleaseDelay(IStatus status, long microSeconds) throws FbException
        {
            VTable vTable = getVTable();
            vTable.setReleaseDelay.invoke(this, status, microSeconds);
            FbException.checkException(status);
        }
    }

    public static class IPluginFactory extends IVersioned implements IPluginFactoryIntf
    {
        public static class VTable extends IVersioned.VTable
        {
            public static interface Callback_createPlugin extends com.sun.jna.Callback
            {
                public IPluginBase invoke(IPluginFactory self, IStatus status, IPluginConfig factoryParameter);
            }

            public VTable(com.sun.jna.Pointer pointer)
            {
                super(pointer);
            }

            public VTable(final IPluginFactoryIntf obj)
            {
                super(obj);

                createPlugin = new Callback_createPlugin() {
                    @Override
                    public IPluginBase invoke(IPluginFactory self, IStatus status, IPluginConfig factoryParameter)
                    {
                        try
                        {
                            return obj.createPlugin(status, factoryParameter);
                        }
                        catch (Throwable t)
                        {
                            FbException.catchException(status, t);
                            return null;
                        }
                    }
                };
            }

            public VTable()
            {
            }

            public Callback_createPlugin createPlugin;

            @Override
            protected java.util.List<String> getFieldOrder()
            {
                java.util.List<String> fields = super.getFieldOrder();
                fields.addAll(java.util.Arrays.asList("createPlugin"));
                return fields;
            }
        }

        public IPluginFactory()
        {
        }

        public IPluginFactory(IPluginFactoryIntf obj)
        {
            vTable = new VTable(obj);
            vTable.write();
            cloopVTable = vTable.getPointer();
            write();
        }

        @Override
        protected VTable createVTable()
        {
            return new VTable(cloopVTable);
        }

        public IPluginBase createPlugin(IStatus status, IPluginConfig factoryParameter) throws FbException
        {
            VTable vTable = getVTable();
            IPluginBase result = vTable.createPlugin.invoke(this, status, factoryParameter);
            FbException.checkException(status);
            return result;
        }
    }

    public static class IPluginModule extends IVersioned implements IPluginModuleIntf
    {
        public static class VTable extends IVersioned.VTable
        {
            public static interface Callback_doClean extends com.sun.jna.Callback
            {
                public void invoke(IPluginModule self);
            }

            public VTable(com.sun.jna.Pointer pointer)
            {
                super(pointer);
            }

            public VTable(final IPluginModuleIntf obj)
            {
                super(obj);

                doClean = new Callback_doClean() {
                    @Override
                    public void invoke(IPluginModule self)
                    {
                        obj.doClean();
                    }
                };
            }

            public VTable()
            {
            }

            public Callback_doClean doClean;

            @Override
            protected java.util.List<String> getFieldOrder()
            {
                java.util.List<String> fields = super.getFieldOrder();
                fields.addAll(java.util.Arrays.asList("doClean"));
                return fields;
            }
        }

        public IPluginModule()
        {
        }

        public IPluginModule(IPluginModuleIntf obj)
        {
            vTable = new VTable(obj);
            vTable.write();
            cloopVTable = vTable.getPointer();
            write();
        }

        @Override
        protected VTable createVTable()
        {
            return new VTable(cloopVTable);
        }

        public void doClean()
        {
            VTable vTable = getVTable();
            vTable.doClean.invoke(this);
        }
    }

    public static class IPluginManager extends IVersioned implements IPluginManagerIntf
    {
        public static class VTable extends IVersioned.VTable
        {
            public static interface Callback_registerPluginFactory extends com.sun.jna.Callback
            {
                public void invoke(IPluginManager self, int pluginType, String defaultName, IPluginFactory factory);
            }

            public static interface Callback_registerModule extends com.sun.jna.Callback
            {
                public void invoke(IPluginManager self, IPluginModule cleanup);
            }

            public static interface Callback_unregisterModule extends com.sun.jna.Callback
            {
                public void invoke(IPluginManager self, IPluginModule cleanup);
            }

            public static interface Callback_getPlugins extends com.sun.jna.Callback
            {
                public IPluginSet invoke(IPluginManager self, IStatus status, int pluginType, String namesList, IFirebirdConf firebirdConf);
            }

            public static interface Callback_getConfig extends com.sun.jna.Callback
            {
                public IConfig invoke(IPluginManager self, IStatus status, String filename);
            }

            public static interface Callback_releasePlugin extends com.sun.jna.Callback
            {
                public void invoke(IPluginManager self, IPluginBase plugin);
            }

            public VTable(com.sun.jna.Pointer pointer)
            {
                super(pointer);
            }

            public VTable(final IPluginManagerIntf obj)
            {
                super(obj);

                registerPluginFactory = new Callback_registerPluginFactory() {
                    @Override
                    public void invoke(IPluginManager self, int pluginType, String defaultName, IPluginFactory factory)
                    {
                        obj.registerPluginFactory(pluginType, defaultName, factory);
                    }
                };

                registerModule = new Callback_registerModule() {
                    @Override
                    public void invoke(IPluginManager self, IPluginModule cleanup)
                    {
                        obj.registerModule(cleanup);
                    }
                };

                unregisterModule = new Callback_unregisterModule() {
                    @Override
                    public void invoke(IPluginManager self, IPluginModule cleanup)
                    {
                        obj.unregisterModule(cleanup);
                    }
                };

                getPlugins = new Callback_getPlugins() {
                    @Override
                    public IPluginSet invoke(IPluginManager self, IStatus status, int pluginType, String namesList, IFirebirdConf firebirdConf)
                    {
                        try
                        {
                            return obj.getPlugins(status, pluginType, namesList, firebirdConf);
                        }
                        catch (Throwable t)
                        {
                            FbException.catchException(status, t);
                            return null;
                        }
                    }
                };

                getConfig = new Callback_getConfig() {
                    @Override
                    public IConfig invoke(IPluginManager self, IStatus status, String filename)
                    {
                        try
                        {
                            return obj.getConfig(status, filename);
                        }
                        catch (Throwable t)
                        {
                            FbException.catchException(status, t);
                            return null;
                        }
                    }
                };

                releasePlugin = new Callback_releasePlugin() {
                    @Override
                    public void invoke(IPluginManager self, IPluginBase plugin)
                    {
                        obj.releasePlugin(plugin);
                    }
                };
            }

            public VTable()
            {
            }

            public Callback_registerPluginFactory registerPluginFactory;
            public Callback_registerModule registerModule;
            public Callback_unregisterModule unregisterModule;
            public Callback_getPlugins getPlugins;
            public Callback_getConfig getConfig;
            public Callback_releasePlugin releasePlugin;

            @Override
            protected java.util.List<String> getFieldOrder()
            {
                java.util.List<String> fields = super.getFieldOrder();
                fields.addAll(java.util.Arrays.asList("registerPluginFactory", "registerModule", "unregisterModule", "getPlugins", "getConfig", "releasePlugin"));
                return fields;
            }
        }

        public IPluginManager()
        {
        }

        public IPluginManager(IPluginManagerIntf obj)
        {
            vTable = new VTable(obj);
            vTable.write();
            cloopVTable = vTable.getPointer();
            write();
        }

        @Override
        protected VTable createVTable()
        {
            return new VTable(cloopVTable);
        }

        public void registerPluginFactory(int pluginType, String defaultName, IPluginFactory factory)
        {
            VTable vTable = getVTable();
            vTable.registerPluginFactory.invoke(this, pluginType, defaultName, factory);
        }

        public void registerModule(IPluginModule cleanup)
        {
            VTable vTable = getVTable();
            vTable.registerModule.invoke(this, cleanup);
        }

        public void unregisterModule(IPluginModule cleanup)
        {
            VTable vTable = getVTable();
            vTable.unregisterModule.invoke(this, cleanup);
        }

        public IPluginSet getPlugins(IStatus status, int pluginType, String namesList, IFirebirdConf firebirdConf) throws FbException
        {
            VTable vTable = getVTable();
            IPluginSet result = vTable.getPlugins.invoke(this, status, pluginType, namesList, firebirdConf);
            FbException.checkException(status);
            return result;
        }

        public IConfig getConfig(IStatus status, String filename) throws FbException
        {
            VTable vTable = getVTable();
            IConfig result = vTable.getConfig.invoke(this, status, filename);
            FbException.checkException(status);
            return result;
        }

        public void releasePlugin(IPluginBase plugin)
        {
            VTable vTable = getVTable();
            vTable.releasePlugin.invoke(this, plugin);
        }
    }

    public static class ICryptKey extends IVersioned implements ICryptKeyIntf
    {
        public static class VTable extends IVersioned.VTable
        {
            public static interface Callback_setSymmetric extends com.sun.jna.Callback
            {
                public void invoke(ICryptKey self, IStatus status, String type, int keyLength, com.sun.jna.Pointer key);
            }

            public static interface Callback_setAsymmetric extends com.sun.jna.Callback
            {
                public void invoke(ICryptKey self, IStatus status, String type, int encryptKeyLength, com.sun.jna.Pointer encryptKey, int decryptKeyLength, com.sun.jna.Pointer decryptKey);
            }

            public static interface Callback_getEncryptKey extends com.sun.jna.Callback
            {
                public com.sun.jna.Pointer invoke(ICryptKey self, int[] length);
            }

            public static interface Callback_getDecryptKey extends com.sun.jna.Callback
            {
                public com.sun.jna.Pointer invoke(ICryptKey self, int[] length);
            }

            public VTable(com.sun.jna.Pointer pointer)
            {
                super(pointer);
            }

            public VTable(final ICryptKeyIntf obj)
            {
                super(obj);

                setSymmetric = new Callback_setSymmetric() {
                    @Override
                    public void invoke(ICryptKey self, IStatus status, String type, int keyLength, com.sun.jna.Pointer key)
                    {
                        try
                        {
                            obj.setSymmetric(status, type, keyLength, key);
                        }
                        catch (Throwable t)
                        {
                            FbException.catchException(status, t);
                        }
                    }
                };

                setAsymmetric = new Callback_setAsymmetric() {
                    @Override
                    public void invoke(ICryptKey self, IStatus status, String type, int encryptKeyLength, com.sun.jna.Pointer encryptKey, int decryptKeyLength, com.sun.jna.Pointer decryptKey)
                    {
                        try
                        {
                            obj.setAsymmetric(status, type, encryptKeyLength, encryptKey, decryptKeyLength, decryptKey);
                        }
                        catch (Throwable t)
                        {
                            FbException.catchException(status, t);
                        }
                    }
                };

                getEncryptKey = new Callback_getEncryptKey() {
                    @Override
                    public com.sun.jna.Pointer invoke(ICryptKey self, int[] length)
                    {
                        return obj.getEncryptKey(length);
                    }
                };

                getDecryptKey = new Callback_getDecryptKey() {
                    @Override
                    public com.sun.jna.Pointer invoke(ICryptKey self, int[] length)
                    {
                        return obj.getDecryptKey(length);
                    }
                };
            }

            public VTable()
            {
            }

            public Callback_setSymmetric setSymmetric;
            public Callback_setAsymmetric setAsymmetric;
            public Callback_getEncryptKey getEncryptKey;
            public Callback_getDecryptKey getDecryptKey;

            @Override
            protected java.util.List<String> getFieldOrder()
            {
                java.util.List<String> fields = super.getFieldOrder();
                fields.addAll(java.util.Arrays.asList("setSymmetric", "setAsymmetric", "getEncryptKey", "getDecryptKey"));
                return fields;
            }
        }

        public ICryptKey()
        {
        }

        public ICryptKey(ICryptKeyIntf obj)
        {
            vTable = new VTable(obj);
            vTable.write();
            cloopVTable = vTable.getPointer();
            write();
        }

        @Override
        protected VTable createVTable()
        {
            return new VTable(cloopVTable);
        }

        public void setSymmetric(IStatus status, String type, int keyLength, com.sun.jna.Pointer key) throws FbException
        {
            VTable vTable = getVTable();
            vTable.setSymmetric.invoke(this, status, type, keyLength, key);
            FbException.checkException(status);
        }

        public void setAsymmetric(IStatus status, String type, int encryptKeyLength, com.sun.jna.Pointer encryptKey, int decryptKeyLength, com.sun.jna.Pointer decryptKey) throws FbException
        {
            VTable vTable = getVTable();
            vTable.setAsymmetric.invoke(this, status, type, encryptKeyLength, encryptKey, decryptKeyLength, decryptKey);
            FbException.checkException(status);
        }

        public com.sun.jna.Pointer getEncryptKey(int[] length)
        {
            VTable vTable = getVTable();
            com.sun.jna.Pointer result = vTable.getEncryptKey.invoke(this, length);
            return result;
        }

        public com.sun.jna.Pointer getDecryptKey(int[] length)
        {
            VTable vTable = getVTable();
            com.sun.jna.Pointer result = vTable.getDecryptKey.invoke(this, length);
            return result;
        }
    }

    public static class IConfigManager extends IVersioned implements IConfigManagerIntf
    {
        public static class VTable extends IVersioned.VTable
        {
            public static interface Callback_getDirectory extends com.sun.jna.Callback
            {
                public String invoke(IConfigManager self, int code);
            }

            public static interface Callback_getFirebirdConf extends com.sun.jna.Callback
            {
                public IFirebirdConf invoke(IConfigManager self);
            }

            public static interface Callback_getDatabaseConf extends com.sun.jna.Callback
            {
                public IFirebirdConf invoke(IConfigManager self, String dbName);
            }

            public static interface Callback_getPluginConfig extends com.sun.jna.Callback
            {
                public IConfig invoke(IConfigManager self, String configuredPlugin);
            }

            public static interface Callback_getInstallDirectory extends com.sun.jna.Callback
            {
                public String invoke(IConfigManager self);
            }

            public static interface Callback_getRootDirectory extends com.sun.jna.Callback
            {
                public String invoke(IConfigManager self);
            }

            public VTable(com.sun.jna.Pointer pointer)
            {
                super(pointer);
            }

            public VTable(final IConfigManagerIntf obj)
            {
                super(obj);

                getDirectory = new Callback_getDirectory() {
                    @Override
                    public String invoke(IConfigManager self, int code)
                    {
                        return obj.getDirectory(code);
                    }
                };

                getFirebirdConf = new Callback_getFirebirdConf() {
                    @Override
                    public IFirebirdConf invoke(IConfigManager self)
                    {
                        return obj.getFirebirdConf();
                    }
                };

                getDatabaseConf = new Callback_getDatabaseConf() {
                    @Override
                    public IFirebirdConf invoke(IConfigManager self, String dbName)
                    {
                        return obj.getDatabaseConf(dbName);
                    }
                };

                getPluginConfig = new Callback_getPluginConfig() {
                    @Override
                    public IConfig invoke(IConfigManager self, String configuredPlugin)
                    {
                        return obj.getPluginConfig(configuredPlugin);
                    }
                };

                getInstallDirectory = new Callback_getInstallDirectory() {
                    @Override
                    public String invoke(IConfigManager self)
                    {
                        return obj.getInstallDirectory();
                    }
                };

                getRootDirectory = new Callback_getRootDirectory() {
                    @Override
                    public String invoke(IConfigManager self)
                    {
                        return obj.getRootDirectory();
                    }
                };
            }

            public VTable()
            {
            }

            public Callback_getDirectory getDirectory;
            public Callback_getFirebirdConf getFirebirdConf;
            public Callback_getDatabaseConf getDatabaseConf;
            public Callback_getPluginConfig getPluginConfig;
            public Callback_getInstallDirectory getInstallDirectory;
            public Callback_getRootDirectory getRootDirectory;

            @Override
            protected java.util.List<String> getFieldOrder()
            {
                java.util.List<String> fields = super.getFieldOrder();
                fields.addAll(java.util.Arrays.asList("getDirectory", "getFirebirdConf", "getDatabaseConf", "getPluginConfig", "getInstallDirectory", "getRootDirectory"));
                return fields;
            }
        }

        public IConfigManager()
        {
        }

        public IConfigManager(IConfigManagerIntf obj)
        {
            vTable = new VTable(obj);
            vTable.write();
            cloopVTable = vTable.getPointer();
            write();
        }

        @Override
        protected VTable createVTable()
        {
            return new VTable(cloopVTable);
        }

        public String getDirectory(int code)
        {
            VTable vTable = getVTable();
            String result = vTable.getDirectory.invoke(this, code);
            return result;
        }

        public IFirebirdConf getFirebirdConf()
        {
            VTable vTable = getVTable();
            IFirebirdConf result = vTable.getFirebirdConf.invoke(this);
            return result;
        }

        public IFirebirdConf getDatabaseConf(String dbName)
        {
            VTable vTable = getVTable();
            IFirebirdConf result = vTable.getDatabaseConf.invoke(this, dbName);
            return result;
        }

        public IConfig getPluginConfig(String configuredPlugin)
        {
            VTable vTable = getVTable();
            IConfig result = vTable.getPluginConfig.invoke(this, configuredPlugin);
            return result;
        }

        public String getInstallDirectory()
        {
            VTable vTable = getVTable();
            String result = vTable.getInstallDirectory.invoke(this);
            return result;
        }

        public String getRootDirectory()
        {
            VTable vTable = getVTable();
            String result = vTable.getRootDirectory.invoke(this);
            return result;
        }
    }

    public static class IEventCallback extends IReferenceCounted implements IEventCallbackIntf
    {
        public static class VTable extends IReferenceCounted.VTable
        {
            public static interface Callback_eventCallbackFunction extends com.sun.jna.Callback
            {
                public void invoke(IEventCallback self, int length, byte[] events);
            }

            public VTable(com.sun.jna.Pointer pointer)
            {
                super(pointer);
            }

            public VTable(final IEventCallbackIntf obj)
            {
                super(obj);

                eventCallbackFunction = new Callback_eventCallbackFunction() {
                    @Override
                    public void invoke(IEventCallback self, int length, byte[] events)
                    {
                        obj.eventCallbackFunction(length, events);
                    }
                };
            }

            public VTable()
            {
            }

            public Callback_eventCallbackFunction eventCallbackFunction;

            @Override
            protected java.util.List<String> getFieldOrder()
            {
                java.util.List<String> fields = super.getFieldOrder();
                fields.addAll(java.util.Arrays.asList("eventCallbackFunction"));
                return fields;
            }
        }

        public IEventCallback()
        {
        }

        public IEventCallback(IEventCallbackIntf obj)
        {
            vTable = new VTable(obj);
            vTable.write();
            cloopVTable = vTable.getPointer();
            write();
        }

        @Override
        protected VTable createVTable()
        {
            return new VTable(cloopVTable);
        }

        public void eventCallbackFunction(int length, byte[] events)
        {
            VTable vTable = getVTable();
            vTable.eventCallbackFunction.invoke(this, length, events);
        }
    }

    public static class IBlob extends IReferenceCounted implements IBlobIntf
    {
        public static class VTable extends IReferenceCounted.VTable
        {
            public static interface Callback_getInfo extends com.sun.jna.Callback
            {
                public void invoke(IBlob self, IStatus status, int itemsLength, byte[] items, int bufferLength, byte[] buffer);
            }

            public static interface Callback_getSegment extends com.sun.jna.Callback
            {
                public int invoke(IBlob self, IStatus status, int bufferLength, com.sun.jna.Pointer buffer, int[] segmentLength);
            }

            public static interface Callback_putSegment extends com.sun.jna.Callback
            {
                public void invoke(IBlob self, IStatus status, int length, com.sun.jna.Pointer buffer);
            }

            public static interface Callback_cancel extends com.sun.jna.Callback
            {
                public void invoke(IBlob self, IStatus status);
            }

            public static interface Callback_close extends com.sun.jna.Callback
            {
                public void invoke(IBlob self, IStatus status);
            }

            public static interface Callback_seek extends com.sun.jna.Callback
            {
                public int invoke(IBlob self, IStatus status, int mode, int offset);
            }

            public VTable(com.sun.jna.Pointer pointer)
            {
                super(pointer);
            }

            public VTable(final IBlobIntf obj)
            {
                super(obj);

                getInfo = new Callback_getInfo() {
                    @Override
                    public void invoke(IBlob self, IStatus status, int itemsLength, byte[] items, int bufferLength, byte[] buffer)
                    {
                        try
                        {
                            obj.getInfo(status, itemsLength, items, bufferLength, buffer);
                        }
                        catch (Throwable t)
                        {
                            FbException.catchException(status, t);
                        }
                    }
                };

                getSegment = new Callback_getSegment() {
                    @Override
                    public int invoke(IBlob self, IStatus status, int bufferLength, com.sun.jna.Pointer buffer, int[] segmentLength)
                    {
                        try
                        {
                            return obj.getSegment(status, bufferLength, buffer, segmentLength);
                        }
                        catch (Throwable t)
                        {
                            FbException.catchException(status, t);
                            return 0;
                        }
                    }
                };

                putSegment = new Callback_putSegment() {
                    @Override
                    public void invoke(IBlob self, IStatus status, int length, com.sun.jna.Pointer buffer)
                    {
                        try
                        {
                            obj.putSegment(status, length, buffer);
                        }
                        catch (Throwable t)
                        {
                            FbException.catchException(status, t);
                        }
                    }
                };

                cancel = new Callback_cancel() {
                    @Override
                    public void invoke(IBlob self, IStatus status)
                    {
                        try
                        {
                            obj.cancel(status);
                        }
                        catch (Throwable t)
                        {
                            FbException.catchException(status, t);
                        }
                    }
                };

                close = new Callback_close() {
                    @Override
                    public void invoke(IBlob self, IStatus status)
                    {
                        try
                        {
                            obj.close(status);
                        }
                        catch (Throwable t)
                        {
                            FbException.catchException(status, t);
                        }
                    }
                };

                seek = new Callback_seek() {
                    @Override
                    public int invoke(IBlob self, IStatus status, int mode, int offset)
                    {
                        try
                        {
                            return obj.seek(status, mode, offset);
                        }
                        catch (Throwable t)
                        {
                            FbException.catchException(status, t);
                            return 0;
                        }
                    }
                };
            }

            public VTable()
            {
            }

            public Callback_getInfo getInfo;
            public Callback_getSegment getSegment;
            public Callback_putSegment putSegment;
            public Callback_cancel cancel;
            public Callback_close close;
            public Callback_seek seek;

            @Override
            protected java.util.List<String> getFieldOrder()
            {
                java.util.List<String> fields = super.getFieldOrder();
                fields.addAll(java.util.Arrays.asList("getInfo", "getSegment", "putSegment", "cancel", "close", "seek"));
                return fields;
            }
        }

        public IBlob()
        {
        }

        public IBlob(IBlobIntf obj)
        {
            vTable = new VTable(obj);
            vTable.write();
            cloopVTable = vTable.getPointer();
            write();
        }

        @Override
        protected VTable createVTable()
        {
            return new VTable(cloopVTable);
        }

        public void getInfo(IStatus status, int itemsLength, byte[] items, int bufferLength, byte[] buffer) throws FbException
        {
            VTable vTable = getVTable();
            vTable.getInfo.invoke(this, status, itemsLength, items, bufferLength, buffer);
            FbException.checkException(status);
        }

        public int getSegment(IStatus status, int bufferLength, com.sun.jna.Pointer buffer, int[] segmentLength) throws FbException
        {
            VTable vTable = getVTable();
            int result = vTable.getSegment.invoke(this, status, bufferLength, buffer, segmentLength);
            FbException.checkException(status);
            return result;
        }

        public void putSegment(IStatus status, int length, com.sun.jna.Pointer buffer) throws FbException
        {
            VTable vTable = getVTable();
            vTable.putSegment.invoke(this, status, length, buffer);
            FbException.checkException(status);
        }

        public void cancel(IStatus status) throws FbException
        {
            VTable vTable = getVTable();
            vTable.cancel.invoke(this, status);
            FbException.checkException(status);
        }

        public void close(IStatus status) throws FbException
        {
            VTable vTable = getVTable();
            vTable.close.invoke(this, status);
            FbException.checkException(status);
        }

        public int seek(IStatus status, int mode, int offset) throws FbException
        {
            VTable vTable = getVTable();
            int result = vTable.seek.invoke(this, status, mode, offset);
            FbException.checkException(status);
            return result;
        }
    }

    public static class ITransaction extends IReferenceCounted implements ITransactionIntf
    {
        public static class VTable extends IReferenceCounted.VTable
        {
            public static interface Callback_getInfo extends com.sun.jna.Callback
            {
                public void invoke(ITransaction self, IStatus status, int itemsLength, byte[] items, int bufferLength, byte[] buffer);
            }

            public static interface Callback_prepare extends com.sun.jna.Callback
            {
                public void invoke(ITransaction self, IStatus status, int msgLength, byte[] message);
            }

            public static interface Callback_commit extends com.sun.jna.Callback
            {
                public void invoke(ITransaction self, IStatus status);
            }

            public static interface Callback_commitRetaining extends com.sun.jna.Callback
            {
                public void invoke(ITransaction self, IStatus status);
            }

            public static interface Callback_rollback extends com.sun.jna.Callback
            {
                public void invoke(ITransaction self, IStatus status);
            }

            public static interface Callback_rollbackRetaining extends com.sun.jna.Callback
            {
                public void invoke(ITransaction self, IStatus status);
            }

            public static interface Callback_disconnect extends com.sun.jna.Callback
            {
                public void invoke(ITransaction self, IStatus status);
            }

            public static interface Callback_join extends com.sun.jna.Callback
            {
                public ITransaction invoke(ITransaction self, IStatus status, ITransaction transaction);
            }

            public static interface Callback_validate extends com.sun.jna.Callback
            {
                public ITransaction invoke(ITransaction self, IStatus status, IAttachment attachment);
            }

            public static interface Callback_enterDtc extends com.sun.jna.Callback
            {
                public ITransaction invoke(ITransaction self, IStatus status);
            }

            public VTable(com.sun.jna.Pointer pointer)
            {
                super(pointer);
            }

            public VTable(final ITransactionIntf obj)
            {
                super(obj);

                getInfo = new Callback_getInfo() {
                    @Override
                    public void invoke(ITransaction self, IStatus status, int itemsLength, byte[] items, int bufferLength, byte[] buffer)
                    {
                        try
                        {
                            obj.getInfo(status, itemsLength, items, bufferLength, buffer);
                        }
                        catch (Throwable t)
                        {
                            FbException.catchException(status, t);
                        }
                    }
                };

                prepare = new Callback_prepare() {
                    @Override
                    public void invoke(ITransaction self, IStatus status, int msgLength, byte[] message)
                    {
                        try
                        {
                            obj.prepare(status, msgLength, message);
                        }
                        catch (Throwable t)
                        {
                            FbException.catchException(status, t);
                        }
                    }
                };

                commit = new Callback_commit() {
                    @Override
                    public void invoke(ITransaction self, IStatus status)
                    {
                        try
                        {
                            obj.commit(status);
                        }
                        catch (Throwable t)
                        {
                            FbException.catchException(status, t);
                        }
                    }
                };

                commitRetaining = new Callback_commitRetaining() {
                    @Override
                    public void invoke(ITransaction self, IStatus status)
                    {
                        try
                        {
                            obj.commitRetaining(status);
                        }
                        catch (Throwable t)
                        {
                            FbException.catchException(status, t);
                        }
                    }
                };

                rollback = new Callback_rollback() {
                    @Override
                    public void invoke(ITransaction self, IStatus status)
                    {
                        try
                        {
                            obj.rollback(status);
                        }
                        catch (Throwable t)
                        {
                            FbException.catchException(status, t);
                        }
                    }
                };

                rollbackRetaining = new Callback_rollbackRetaining() {
                    @Override
                    public void invoke(ITransaction self, IStatus status)
                    {
                        try
                        {
                            obj.rollbackRetaining(status);
                        }
                        catch (Throwable t)
                        {
                            FbException.catchException(status, t);
                        }
                    }
                };

                disconnect = new Callback_disconnect() {
                    @Override
                    public void invoke(ITransaction self, IStatus status)
                    {
                        try
                        {
                            obj.disconnect(status);
                        }
                        catch (Throwable t)
                        {
                            FbException.catchException(status, t);
                        }
                    }
                };

                join = new Callback_join() {
                    @Override
                    public ITransaction invoke(ITransaction self, IStatus status, ITransaction transaction)
                    {
                        try
                        {
                            return obj.join(status, transaction);
                        }
                        catch (Throwable t)
                        {
                            FbException.catchException(status, t);
                            return null;
                        }
                    }
                };

                validate = new Callback_validate() {
                    @Override
                    public ITransaction invoke(ITransaction self, IStatus status, IAttachment attachment)
                    {
                        try
                        {
                            return obj.validate(status, attachment);
                        }
                        catch (Throwable t)
                        {
                            FbException.catchException(status, t);
                            return null;
                        }
                    }
                };

                enterDtc = new Callback_enterDtc() {
                    @Override
                    public ITransaction invoke(ITransaction self, IStatus status)
                    {
                        try
                        {
                            return obj.enterDtc(status);
                        }
                        catch (Throwable t)
                        {
                            FbException.catchException(status, t);
                            return null;
                        }
                    }
                };
            }

            public VTable()
            {
            }

            public Callback_getInfo getInfo;
            public Callback_prepare prepare;
            public Callback_commit commit;
            public Callback_commitRetaining commitRetaining;
            public Callback_rollback rollback;
            public Callback_rollbackRetaining rollbackRetaining;
            public Callback_disconnect disconnect;
            public Callback_join join;
            public Callback_validate validate;
            public Callback_enterDtc enterDtc;

            @Override
            protected java.util.List<String> getFieldOrder()
            {
                java.util.List<String> fields = super.getFieldOrder();
                fields.addAll(java.util.Arrays.asList("getInfo", "prepare", "commit", "commitRetaining", "rollback", "rollbackRetaining", "disconnect", "join", "validate", "enterDtc"));
                return fields;
            }
        }

        public ITransaction()
        {
        }

        public ITransaction(ITransactionIntf obj)
        {
            vTable = new VTable(obj);
            vTable.write();
            cloopVTable = vTable.getPointer();
            write();
        }

        @Override
        protected VTable createVTable()
        {
            return new VTable(cloopVTable);
        }

        public void getInfo(IStatus status, int itemsLength, byte[] items, int bufferLength, byte[] buffer) throws FbException
        {
            VTable vTable = getVTable();
            vTable.getInfo.invoke(this, status, itemsLength, items, bufferLength, buffer);
            FbException.checkException(status);
        }

        public void prepare(IStatus status, int msgLength, byte[] message) throws FbException
        {
            VTable vTable = getVTable();
            vTable.prepare.invoke(this, status, msgLength, message);
            FbException.checkException(status);
        }

        public void commit(IStatus status) throws FbException
        {
            VTable vTable = getVTable();
            vTable.commit.invoke(this, status);
            FbException.checkException(status);
        }

        public void commitRetaining(IStatus status) throws FbException
        {
            VTable vTable = getVTable();
            vTable.commitRetaining.invoke(this, status);
            FbException.checkException(status);
        }

        public void rollback(IStatus status) throws FbException
        {
            VTable vTable = getVTable();
            vTable.rollback.invoke(this, status);
            FbException.checkException(status);
        }

        public void rollbackRetaining(IStatus status) throws FbException
        {
            VTable vTable = getVTable();
            vTable.rollbackRetaining.invoke(this, status);
            FbException.checkException(status);
        }

        public void disconnect(IStatus status) throws FbException
        {
            VTable vTable = getVTable();
            vTable.disconnect.invoke(this, status);
            FbException.checkException(status);
        }

        public ITransaction join(IStatus status, ITransaction transaction) throws FbException
        {
            VTable vTable = getVTable();
            ITransaction result = vTable.join.invoke(this, status, transaction);
            FbException.checkException(status);
            return result;
        }

        public ITransaction validate(IStatus status, IAttachment attachment) throws FbException
        {
            VTable vTable = getVTable();
            ITransaction result = vTable.validate.invoke(this, status, attachment);
            FbException.checkException(status);
            return result;
        }

        public ITransaction enterDtc(IStatus status) throws FbException
        {
            VTable vTable = getVTable();
            ITransaction result = vTable.enterDtc.invoke(this, status);
            FbException.checkException(status);
            return result;
        }
    }

    public static class IMessageMetadata extends IReferenceCounted implements IMessageMetadataIntf
    {
        public static class VTable extends IReferenceCounted.VTable
        {
            public static interface Callback_getCount extends com.sun.jna.Callback
            {
                public int invoke(IMessageMetadata self, IStatus status);
            }

            public static interface Callback_getField extends com.sun.jna.Callback
            {
                public String invoke(IMessageMetadata self, IStatus status, int index);
            }

            public static interface Callback_getRelation extends com.sun.jna.Callback
            {
                public String invoke(IMessageMetadata self, IStatus status, int index);
            }

            public static interface Callback_getOwner extends com.sun.jna.Callback
            {
                public String invoke(IMessageMetadata self, IStatus status, int index);
            }

            public static interface Callback_getAlias extends com.sun.jna.Callback
            {
                public String invoke(IMessageMetadata self, IStatus status, int index);
            }

            public static interface Callback_getType extends com.sun.jna.Callback
            {
                public int invoke(IMessageMetadata self, IStatus status, int index);
            }

            public static interface Callback_isNullable extends com.sun.jna.Callback
            {
                public boolean invoke(IMessageMetadata self, IStatus status, int index);
            }

            public static interface Callback_getSubType extends com.sun.jna.Callback
            {
                public int invoke(IMessageMetadata self, IStatus status, int index);
            }

            public static interface Callback_getLength extends com.sun.jna.Callback
            {
                public int invoke(IMessageMetadata self, IStatus status, int index);
            }

            public static interface Callback_getScale extends com.sun.jna.Callback
            {
                public int invoke(IMessageMetadata self, IStatus status, int index);
            }

            public static interface Callback_getCharSet extends com.sun.jna.Callback
            {
                public int invoke(IMessageMetadata self, IStatus status, int index);
            }

            public static interface Callback_getOffset extends com.sun.jna.Callback
            {
                public int invoke(IMessageMetadata self, IStatus status, int index);
            }

            public static interface Callback_getNullOffset extends com.sun.jna.Callback
            {
                public int invoke(IMessageMetadata self, IStatus status, int index);
            }

            public static interface Callback_getBuilder extends com.sun.jna.Callback
            {
                public IMetadataBuilder invoke(IMessageMetadata self, IStatus status);
            }

            public static interface Callback_getMessageLength extends com.sun.jna.Callback
            {
                public int invoke(IMessageMetadata self, IStatus status);
            }

            public VTable(com.sun.jna.Pointer pointer)
            {
                super(pointer);
            }

            public VTable(final IMessageMetadataIntf obj)
            {
                super(obj);

                getCount = new Callback_getCount() {
                    @Override
                    public int invoke(IMessageMetadata self, IStatus status)
                    {
                        try
                        {
                            return obj.getCount(status);
                        }
                        catch (Throwable t)
                        {
                            FbException.catchException(status, t);
                            return 0;
                        }
                    }
                };

                getField = new Callback_getField() {
                    @Override
                    public String invoke(IMessageMetadata self, IStatus status, int index)
                    {
                        try
                        {
                            return obj.getField(status, index);
                        }
                        catch (Throwable t)
                        {
                            FbException.catchException(status, t);
                            return null;
                        }
                    }
                };

                getRelation = new Callback_getRelation() {
                    @Override
                    public String invoke(IMessageMetadata self, IStatus status, int index)
                    {
                        try
                        {
                            return obj.getRelation(status, index);
                        }
                        catch (Throwable t)
                        {
                            FbException.catchException(status, t);
                            return null;
                        }
                    }
                };

                getOwner = new Callback_getOwner() {
                    @Override
                    public String invoke(IMessageMetadata self, IStatus status, int index)
                    {
                        try
                        {
                            return obj.getOwner(status, index);
                        }
                        catch (Throwable t)
                        {
                            FbException.catchException(status, t);
                            return null;
                        }
                    }
                };

                getAlias = new Callback_getAlias() {
                    @Override
                    public String invoke(IMessageMetadata self, IStatus status, int index)
                    {
                        try
                        {
                            return obj.getAlias(status, index);
                        }
                        catch (Throwable t)
                        {
                            FbException.catchException(status, t);
                            return null;
                        }
                    }
                };

                getType = new Callback_getType() {
                    @Override
                    public int invoke(IMessageMetadata self, IStatus status, int index)
                    {
                        try
                        {
                            return obj.getType(status, index);
                        }
                        catch (Throwable t)
                        {
                            FbException.catchException(status, t);
                            return 0;
                        }
                    }
                };

                isNullable = new Callback_isNullable() {
                    @Override
                    public boolean invoke(IMessageMetadata self, IStatus status, int index)
                    {
                        try
                        {
                            return obj.isNullable(status, index);
                        }
                        catch (Throwable t)
                        {
                            FbException.catchException(status, t);
                            return false;
                        }
                    }
                };

                getSubType = new Callback_getSubType() {
                    @Override
                    public int invoke(IMessageMetadata self, IStatus status, int index)
                    {
                        try
                        {
                            return obj.getSubType(status, index);
                        }
                        catch (Throwable t)
                        {
                            FbException.catchException(status, t);
                            return 0;
                        }
                    }
                };

                getLength = new Callback_getLength() {
                    @Override
                    public int invoke(IMessageMetadata self, IStatus status, int index)
                    {
                        try
                        {
                            return obj.getLength(status, index);
                        }
                        catch (Throwable t)
                        {
                            FbException.catchException(status, t);
                            return 0;
                        }
                    }
                };

                getScale = new Callback_getScale() {
                    @Override
                    public int invoke(IMessageMetadata self, IStatus status, int index)
                    {
                        try
                        {
                            return obj.getScale(status, index);
                        }
                        catch (Throwable t)
                        {
                            FbException.catchException(status, t);
                            return 0;
                        }
                    }
                };

                getCharSet = new Callback_getCharSet() {
                    @Override
                    public int invoke(IMessageMetadata self, IStatus status, int index)
                    {
                        try
                        {
                            return obj.getCharSet(status, index);
                        }
                        catch (Throwable t)
                        {
                            FbException.catchException(status, t);
                            return 0;
                        }
                    }
                };

                getOffset = new Callback_getOffset() {
                    @Override
                    public int invoke(IMessageMetadata self, IStatus status, int index)
                    {
                        try
                        {
                            return obj.getOffset(status, index);
                        }
                        catch (Throwable t)
                        {
                            FbException.catchException(status, t);
                            return 0;
                        }
                    }
                };

                getNullOffset = new Callback_getNullOffset() {
                    @Override
                    public int invoke(IMessageMetadata self, IStatus status, int index)
                    {
                        try
                        {
                            return obj.getNullOffset(status, index);
                        }
                        catch (Throwable t)
                        {
                            FbException.catchException(status, t);
                            return 0;
                        }
                    }
                };

                getBuilder = new Callback_getBuilder() {
                    @Override
                    public IMetadataBuilder invoke(IMessageMetadata self, IStatus status)
                    {
                        try
                        {
                            return obj.getBuilder(status);
                        }
                        catch (Throwable t)
                        {
                            FbException.catchException(status, t);
                            return null;
                        }
                    }
                };

                getMessageLength = new Callback_getMessageLength() {
                    @Override
                    public int invoke(IMessageMetadata self, IStatus status)
                    {
                        try
                        {
                            return obj.getMessageLength(status);
                        }
                        catch (Throwable t)
                        {
                            FbException.catchException(status, t);
                            return 0;
                        }
                    }
                };
            }

            public VTable()
            {
            }

            public Callback_getCount getCount;
            public Callback_getField getField;
            public Callback_getRelation getRelation;
            public Callback_getOwner getOwner;
            public Callback_getAlias getAlias;
            public Callback_getType getType;
            public Callback_isNullable isNullable;
            public Callback_getSubType getSubType;
            public Callback_getLength getLength;
            public Callback_getScale getScale;
            public Callback_getCharSet getCharSet;
            public Callback_getOffset getOffset;
            public Callback_getNullOffset getNullOffset;
            public Callback_getBuilder getBuilder;
            public Callback_getMessageLength getMessageLength;

            @Override
            protected java.util.List<String> getFieldOrder()
            {
                java.util.List<String> fields = super.getFieldOrder();
                fields.addAll(java.util.Arrays.asList("getCount", "getField", "getRelation", "getOwner", "getAlias", "getType", "isNullable", "getSubType", "getLength", "getScale", "getCharSet", "getOffset", "getNullOffset", "getBuilder", "getMessageLength"));
                return fields;
            }
        }

        public IMessageMetadata()
        {
        }

        public IMessageMetadata(IMessageMetadataIntf obj)
        {
            vTable = new VTable(obj);
            vTable.write();
            cloopVTable = vTable.getPointer();
            write();
        }

        @Override
        protected VTable createVTable()
        {
            return new VTable(cloopVTable);
        }

        public int getCount(IStatus status) throws FbException
        {
            VTable vTable = getVTable();
            int result = vTable.getCount.invoke(this, status);
            FbException.checkException(status);
            return result;
        }

        public String getField(IStatus status, int index) throws FbException
        {
            VTable vTable = getVTable();
            String result = vTable.getField.invoke(this, status, index);
            FbException.checkException(status);
            return result;
        }

        public String getRelation(IStatus status, int index) throws FbException
        {
            VTable vTable = getVTable();
            String result = vTable.getRelation.invoke(this, status, index);
            FbException.checkException(status);
            return result;
        }

        public String getOwner(IStatus status, int index) throws FbException
        {
            VTable vTable = getVTable();
            String result = vTable.getOwner.invoke(this, status, index);
            FbException.checkException(status);
            return result;
        }

        public String getAlias(IStatus status, int index) throws FbException
        {
            VTable vTable = getVTable();
            String result = vTable.getAlias.invoke(this, status, index);
            FbException.checkException(status);
            return result;
        }

        public int getType(IStatus status, int index) throws FbException
        {
            VTable vTable = getVTable();
            int result = vTable.getType.invoke(this, status, index);
            FbException.checkException(status);
            return result;
        }

        public boolean isNullable(IStatus status, int index) throws FbException
        {
            VTable vTable = getVTable();
            boolean result = vTable.isNullable.invoke(this, status, index);
            FbException.checkException(status);
            return result;
        }

        public int getSubType(IStatus status, int index) throws FbException
        {
            VTable vTable = getVTable();
            int result = vTable.getSubType.invoke(this, status, index);
            FbException.checkException(status);
            return result;
        }

        public int getLength(IStatus status, int index) throws FbException
        {
            VTable vTable = getVTable();
            int result = vTable.getLength.invoke(this, status, index);
            FbException.checkException(status);
            return result;
        }

        public int getScale(IStatus status, int index) throws FbException
        {
            VTable vTable = getVTable();
            int result = vTable.getScale.invoke(this, status, index);
            FbException.checkException(status);
            return result;
        }

        public int getCharSet(IStatus status, int index) throws FbException
        {
            VTable vTable = getVTable();
            int result = vTable.getCharSet.invoke(this, status, index);
            FbException.checkException(status);
            return result;
        }

        public int getOffset(IStatus status, int index) throws FbException
        {
            VTable vTable = getVTable();
            int result = vTable.getOffset.invoke(this, status, index);
            FbException.checkException(status);
            return result;
        }

        public int getNullOffset(IStatus status, int index) throws FbException
        {
            VTable vTable = getVTable();
            int result = vTable.getNullOffset.invoke(this, status, index);
            FbException.checkException(status);
            return result;
        }

        public IMetadataBuilder getBuilder(IStatus status) throws FbException
        {
            VTable vTable = getVTable();
            IMetadataBuilder result = vTable.getBuilder.invoke(this, status);
            FbException.checkException(status);
            return result;
        }

        public int getMessageLength(IStatus status) throws FbException
        {
            VTable vTable = getVTable();
            int result = vTable.getMessageLength.invoke(this, status);
            FbException.checkException(status);
            return result;
        }
    }

    public static class IMetadataBuilder extends IReferenceCounted implements IMetadataBuilderIntf
    {
        public static class VTable extends IReferenceCounted.VTable
        {
            public static interface Callback_setType extends com.sun.jna.Callback
            {
                public void invoke(IMetadataBuilder self, IStatus status, int index, int type);
            }

            public static interface Callback_setSubType extends com.sun.jna.Callback
            {
                public void invoke(IMetadataBuilder self, IStatus status, int index, int subType);
            }

            public static interface Callback_setLength extends com.sun.jna.Callback
            {
                public void invoke(IMetadataBuilder self, IStatus status, int index, int length);
            }

            public static interface Callback_setCharSet extends com.sun.jna.Callback
            {
                public void invoke(IMetadataBuilder self, IStatus status, int index, int charSet);
            }

            public static interface Callback_setScale extends com.sun.jna.Callback
            {
                public void invoke(IMetadataBuilder self, IStatus status, int index, int scale);
            }

            public static interface Callback_truncate extends com.sun.jna.Callback
            {
                public void invoke(IMetadataBuilder self, IStatus status, int count);
            }

            public static interface Callback_moveNameToIndex extends com.sun.jna.Callback
            {
                public void invoke(IMetadataBuilder self, IStatus status, String name, int index);
            }

            public static interface Callback_remove extends com.sun.jna.Callback
            {
                public void invoke(IMetadataBuilder self, IStatus status, int index);
            }

            public static interface Callback_addField extends com.sun.jna.Callback
            {
                public int invoke(IMetadataBuilder self, IStatus status);
            }

            public static interface Callback_getMetadata extends com.sun.jna.Callback
            {
                public IMessageMetadata invoke(IMetadataBuilder self, IStatus status);
            }

            public VTable(com.sun.jna.Pointer pointer)
            {
                super(pointer);
            }

            public VTable(final IMetadataBuilderIntf obj)
            {
                super(obj);

                setType = new Callback_setType() {
                    @Override
                    public void invoke(IMetadataBuilder self, IStatus status, int index, int type)
                    {
                        try
                        {
                            obj.setType(status, index, type);
                        }
                        catch (Throwable t)
                        {
                            FbException.catchException(status, t);
                        }
                    }
                };

                setSubType = new Callback_setSubType() {
                    @Override
                    public void invoke(IMetadataBuilder self, IStatus status, int index, int subType)
                    {
                        try
                        {
                            obj.setSubType(status, index, subType);
                        }
                        catch (Throwable t)
                        {
                            FbException.catchException(status, t);
                        }
                    }
                };

                setLength = new Callback_setLength() {
                    @Override
                    public void invoke(IMetadataBuilder self, IStatus status, int index, int length)
                    {
                        try
                        {
                            obj.setLength(status, index, length);
                        }
                        catch (Throwable t)
                        {
                            FbException.catchException(status, t);
                        }
                    }
                };

                setCharSet = new Callback_setCharSet() {
                    @Override
                    public void invoke(IMetadataBuilder self, IStatus status, int index, int charSet)
                    {
                        try
                        {
                            obj.setCharSet(status, index, charSet);
                        }
                        catch (Throwable t)
                        {
                            FbException.catchException(status, t);
                        }
                    }
                };

                setScale = new Callback_setScale() {
                    @Override
                    public void invoke(IMetadataBuilder self, IStatus status, int index, int scale)
                    {
                        try
                        {
                            obj.setScale(status, index, scale);
                        }
                        catch (Throwable t)
                        {
                            FbException.catchException(status, t);
                        }
                    }
                };

                truncate = new Callback_truncate() {
                    @Override
                    public void invoke(IMetadataBuilder self, IStatus status, int count)
                    {
                        try
                        {
                            obj.truncate(status, count);
                        }
                        catch (Throwable t)
                        {
                            FbException.catchException(status, t);
                        }
                    }
                };

                moveNameToIndex = new Callback_moveNameToIndex() {
                    @Override
                    public void invoke(IMetadataBuilder self, IStatus status, String name, int index)
                    {
                        try
                        {
                            obj.moveNameToIndex(status, name, index);
                        }
                        catch (Throwable t)
                        {
                            FbException.catchException(status, t);
                        }
                    }
                };

                remove = new Callback_remove() {
                    @Override
                    public void invoke(IMetadataBuilder self, IStatus status, int index)
                    {
                        try
                        {
                            obj.remove(status, index);
                        }
                        catch (Throwable t)
                        {
                            FbException.catchException(status, t);
                        }
                    }
                };

                addField = new Callback_addField() {
                    @Override
                    public int invoke(IMetadataBuilder self, IStatus status)
                    {
                        try
                        {
                            return obj.addField(status);
                        }
                        catch (Throwable t)
                        {
                            FbException.catchException(status, t);
                            return 0;
                        }
                    }
                };

                getMetadata = new Callback_getMetadata() {
                    @Override
                    public IMessageMetadata invoke(IMetadataBuilder self, IStatus status)
                    {
                        try
                        {
                            return obj.getMetadata(status);
                        }
                        catch (Throwable t)
                        {
                            FbException.catchException(status, t);
                            return null;
                        }
                    }
                };
            }

            public VTable()
            {
            }

            public Callback_setType setType;
            public Callback_setSubType setSubType;
            public Callback_setLength setLength;
            public Callback_setCharSet setCharSet;
            public Callback_setScale setScale;
            public Callback_truncate truncate;
            public Callback_moveNameToIndex moveNameToIndex;
            public Callback_remove remove;
            public Callback_addField addField;
            public Callback_getMetadata getMetadata;

            @Override
            protected java.util.List<String> getFieldOrder()
            {
                java.util.List<String> fields = super.getFieldOrder();
                fields.addAll(java.util.Arrays.asList("setType", "setSubType", "setLength", "setCharSet", "setScale", "truncate", "moveNameToIndex", "remove", "addField", "getMetadata"));
                return fields;
            }
        }

        public IMetadataBuilder()
        {
        }

        public IMetadataBuilder(IMetadataBuilderIntf obj)
        {
            vTable = new VTable(obj);
            vTable.write();
            cloopVTable = vTable.getPointer();
            write();
        }

        @Override
        protected VTable createVTable()
        {
            return new VTable(cloopVTable);
        }

        public void setType(IStatus status, int index, int type) throws FbException
        {
            VTable vTable = getVTable();
            vTable.setType.invoke(this, status, index, type);
            FbException.checkException(status);
        }

        public void setSubType(IStatus status, int index, int subType) throws FbException
        {
            VTable vTable = getVTable();
            vTable.setSubType.invoke(this, status, index, subType);
            FbException.checkException(status);
        }

        public void setLength(IStatus status, int index, int length) throws FbException
        {
            VTable vTable = getVTable();
            vTable.setLength.invoke(this, status, index, length);
            FbException.checkException(status);
        }

        public void setCharSet(IStatus status, int index, int charSet) throws FbException
        {
            VTable vTable = getVTable();
            vTable.setCharSet.invoke(this, status, index, charSet);
            FbException.checkException(status);
        }

        public void setScale(IStatus status, int index, int scale) throws FbException
        {
            VTable vTable = getVTable();
            vTable.setScale.invoke(this, status, index, scale);
            FbException.checkException(status);
        }

        public void truncate(IStatus status, int count) throws FbException
        {
            VTable vTable = getVTable();
            vTable.truncate.invoke(this, status, count);
            FbException.checkException(status);
        }

        public void moveNameToIndex(IStatus status, String name, int index) throws FbException
        {
            VTable vTable = getVTable();
            vTable.moveNameToIndex.invoke(this, status, name, index);
            FbException.checkException(status);
        }

        public void remove(IStatus status, int index) throws FbException
        {
            VTable vTable = getVTable();
            vTable.remove.invoke(this, status, index);
            FbException.checkException(status);
        }

        public int addField(IStatus status) throws FbException
        {
            VTable vTable = getVTable();
            int result = vTable.addField.invoke(this, status);
            FbException.checkException(status);
            return result;
        }

        public IMessageMetadata getMetadata(IStatus status) throws FbException
        {
            VTable vTable = getVTable();
            IMessageMetadata result = vTable.getMetadata.invoke(this, status);
            FbException.checkException(status);
            return result;
        }
    }

    public static class IResultSet extends IReferenceCounted implements IResultSetIntf
    {
        public static class VTable extends IReferenceCounted.VTable
        {
            public static interface Callback_fetchNext extends com.sun.jna.Callback
            {
                public int invoke(IResultSet self, IStatus status, com.sun.jna.Pointer message);
            }

            public static interface Callback_fetchPrior extends com.sun.jna.Callback
            {
                public int invoke(IResultSet self, IStatus status, com.sun.jna.Pointer message);
            }

            public static interface Callback_fetchFirst extends com.sun.jna.Callback
            {
                public int invoke(IResultSet self, IStatus status, com.sun.jna.Pointer message);
            }

            public static interface Callback_fetchLast extends com.sun.jna.Callback
            {
                public int invoke(IResultSet self, IStatus status, com.sun.jna.Pointer message);
            }

            public static interface Callback_fetchAbsolute extends com.sun.jna.Callback
            {
                public int invoke(IResultSet self, IStatus status, int position, com.sun.jna.Pointer message);
            }

            public static interface Callback_fetchRelative extends com.sun.jna.Callback
            {
                public int invoke(IResultSet self, IStatus status, int offset, com.sun.jna.Pointer message);
            }

            public static interface Callback_isEof extends com.sun.jna.Callback
            {
                public boolean invoke(IResultSet self, IStatus status);
            }

            public static interface Callback_isBof extends com.sun.jna.Callback
            {
                public boolean invoke(IResultSet self, IStatus status);
            }

            public static interface Callback_getMetadata extends com.sun.jna.Callback
            {
                public IMessageMetadata invoke(IResultSet self, IStatus status);
            }

            public static interface Callback_close extends com.sun.jna.Callback
            {
                public void invoke(IResultSet self, IStatus status);
            }

            public static interface Callback_setDelayedOutputFormat extends com.sun.jna.Callback
            {
                public void invoke(IResultSet self, IStatus status, IMessageMetadata format);
            }

            public VTable(com.sun.jna.Pointer pointer)
            {
                super(pointer);
            }

            public VTable(final IResultSetIntf obj)
            {
                super(obj);

                fetchNext = new Callback_fetchNext() {
                    @Override
                    public int invoke(IResultSet self, IStatus status, com.sun.jna.Pointer message)
                    {
                        try
                        {
                            return obj.fetchNext(status, message);
                        }
                        catch (Throwable t)
                        {
                            FbException.catchException(status, t);
                            return 0;
                        }
                    }
                };

                fetchPrior = new Callback_fetchPrior() {
                    @Override
                    public int invoke(IResultSet self, IStatus status, com.sun.jna.Pointer message)
                    {
                        try
                        {
                            return obj.fetchPrior(status, message);
                        }
                        catch (Throwable t)
                        {
                            FbException.catchException(status, t);
                            return 0;
                        }
                    }
                };

                fetchFirst = new Callback_fetchFirst() {
                    @Override
                    public int invoke(IResultSet self, IStatus status, com.sun.jna.Pointer message)
                    {
                        try
                        {
                            return obj.fetchFirst(status, message);
                        }
                        catch (Throwable t)
                        {
                            FbException.catchException(status, t);
                            return 0;
                        }
                    }
                };

                fetchLast = new Callback_fetchLast() {
                    @Override
                    public int invoke(IResultSet self, IStatus status, com.sun.jna.Pointer message)
                    {
                        try
                        {
                            return obj.fetchLast(status, message);
                        }
                        catch (Throwable t)
                        {
                            FbException.catchException(status, t);
                            return 0;
                        }
                    }
                };

                fetchAbsolute = new Callback_fetchAbsolute() {
                    @Override
                    public int invoke(IResultSet self, IStatus status, int position, com.sun.jna.Pointer message)
                    {
                        try
                        {
                            return obj.fetchAbsolute(status, position, message);
                        }
                        catch (Throwable t)
                        {
                            FbException.catchException(status, t);
                            return 0;
                        }
                    }
                };

                fetchRelative = new Callback_fetchRelative() {
                    @Override
                    public int invoke(IResultSet self, IStatus status, int offset, com.sun.jna.Pointer message)
                    {
                        try
                        {
                            return obj.fetchRelative(status, offset, message);
                        }
                        catch (Throwable t)
                        {
                            FbException.catchException(status, t);
                            return 0;
                        }
                    }
                };

                isEof = new Callback_isEof() {
                    @Override
                    public boolean invoke(IResultSet self, IStatus status)
                    {
                        try
                        {
                            return obj.isEof(status);
                        }
                        catch (Throwable t)
                        {
                            FbException.catchException(status, t);
                            return false;
                        }
                    }
                };

                isBof = new Callback_isBof() {
                    @Override
                    public boolean invoke(IResultSet self, IStatus status)
                    {
                        try
                        {
                            return obj.isBof(status);
                        }
                        catch (Throwable t)
                        {
                            FbException.catchException(status, t);
                            return false;
                        }
                    }
                };

                getMetadata = new Callback_getMetadata() {
                    @Override
                    public IMessageMetadata invoke(IResultSet self, IStatus status)
                    {
                        try
                        {
                            return obj.getMetadata(status);
                        }
                        catch (Throwable t)
                        {
                            FbException.catchException(status, t);
                            return null;
                        }
                    }
                };

                close = new Callback_close() {
                    @Override
                    public void invoke(IResultSet self, IStatus status)
                    {
                        try
                        {
                            obj.close(status);
                        }
                        catch (Throwable t)
                        {
                            FbException.catchException(status, t);
                        }
                    }
                };

                setDelayedOutputFormat = new Callback_setDelayedOutputFormat() {
                    @Override
                    public void invoke(IResultSet self, IStatus status, IMessageMetadata format)
                    {
                        try
                        {
                            obj.setDelayedOutputFormat(status, format);
                        }
                        catch (Throwable t)
                        {
                            FbException.catchException(status, t);
                        }
                    }
                };
            }

            public VTable()
            {
            }

            public Callback_fetchNext fetchNext;
            public Callback_fetchPrior fetchPrior;
            public Callback_fetchFirst fetchFirst;
            public Callback_fetchLast fetchLast;
            public Callback_fetchAbsolute fetchAbsolute;
            public Callback_fetchRelative fetchRelative;
            public Callback_isEof isEof;
            public Callback_isBof isBof;
            public Callback_getMetadata getMetadata;
            public Callback_close close;
            public Callback_setDelayedOutputFormat setDelayedOutputFormat;

            @Override
            protected java.util.List<String> getFieldOrder()
            {
                java.util.List<String> fields = super.getFieldOrder();
                fields.addAll(java.util.Arrays.asList("fetchNext", "fetchPrior", "fetchFirst", "fetchLast", "fetchAbsolute", "fetchRelative", "isEof", "isBof", "getMetadata", "close", "setDelayedOutputFormat"));
                return fields;
            }
        }

        public IResultSet()
        {
        }

        public IResultSet(IResultSetIntf obj)
        {
            vTable = new VTable(obj);
            vTable.write();
            cloopVTable = vTable.getPointer();
            write();
        }

        @Override
        protected VTable createVTable()
        {
            return new VTable(cloopVTable);
        }

        public int fetchNext(IStatus status, com.sun.jna.Pointer message) throws FbException
        {
            VTable vTable = getVTable();
            int result = vTable.fetchNext.invoke(this, status, message);
            FbException.checkException(status);
            return result;
        }

        public int fetchPrior(IStatus status, com.sun.jna.Pointer message) throws FbException
        {
            VTable vTable = getVTable();
            int result = vTable.fetchPrior.invoke(this, status, message);
            FbException.checkException(status);
            return result;
        }

        public int fetchFirst(IStatus status, com.sun.jna.Pointer message) throws FbException
        {
            VTable vTable = getVTable();
            int result = vTable.fetchFirst.invoke(this, status, message);
            FbException.checkException(status);
            return result;
        }

        public int fetchLast(IStatus status, com.sun.jna.Pointer message) throws FbException
        {
            VTable vTable = getVTable();
            int result = vTable.fetchLast.invoke(this, status, message);
            FbException.checkException(status);
            return result;
        }

        public int fetchAbsolute(IStatus status, int position, com.sun.jna.Pointer message) throws FbException
        {
            VTable vTable = getVTable();
            int result = vTable.fetchAbsolute.invoke(this, status, position, message);
            FbException.checkException(status);
            return result;
        }

        public int fetchRelative(IStatus status, int offset, com.sun.jna.Pointer message) throws FbException
        {
            VTable vTable = getVTable();
            int result = vTable.fetchRelative.invoke(this, status, offset, message);
            FbException.checkException(status);
            return result;
        }

        public boolean isEof(IStatus status) throws FbException
        {
            VTable vTable = getVTable();
            boolean result = vTable.isEof.invoke(this, status);
            FbException.checkException(status);
            return result;
        }

        public boolean isBof(IStatus status) throws FbException
        {
            VTable vTable = getVTable();
            boolean result = vTable.isBof.invoke(this, status);
            FbException.checkException(status);
            return result;
        }

        public IMessageMetadata getMetadata(IStatus status) throws FbException
        {
            VTable vTable = getVTable();
            IMessageMetadata result = vTable.getMetadata.invoke(this, status);
            FbException.checkException(status);
            return result;
        }

        public void close(IStatus status) throws FbException
        {
            VTable vTable = getVTable();
            vTable.close.invoke(this, status);
            FbException.checkException(status);
        }

        public void setDelayedOutputFormat(IStatus status, IMessageMetadata format) throws FbException
        {
            VTable vTable = getVTable();
            vTable.setDelayedOutputFormat.invoke(this, status, format);
            FbException.checkException(status);
        }
    }

    public static class IStatement extends IReferenceCounted implements IStatementIntf
    {
        public static class VTable extends IReferenceCounted.VTable
        {
            public static interface Callback_getInfo extends com.sun.jna.Callback
            {
                public void invoke(IStatement self, IStatus status, int itemsLength, byte[] items, int bufferLength, byte[] buffer);
            }

            public static interface Callback_getType extends com.sun.jna.Callback
            {
                public int invoke(IStatement self, IStatus status);
            }

            public static interface Callback_getPlan extends com.sun.jna.Callback
            {
                public String invoke(IStatement self, IStatus status, boolean detailed);
            }

            public static interface Callback_getAffectedRecords extends com.sun.jna.Callback
            {
                public long invoke(IStatement self, IStatus status);
            }

            public static interface Callback_getInputMetadata extends com.sun.jna.Callback
            {
                public IMessageMetadata invoke(IStatement self, IStatus status);
            }

            public static interface Callback_getOutputMetadata extends com.sun.jna.Callback
            {
                public IMessageMetadata invoke(IStatement self, IStatus status);
            }

            public static interface Callback_execute extends com.sun.jna.Callback
            {
                public ITransaction invoke(IStatement self, IStatus status, ITransaction transaction, IMessageMetadata inMetadata, com.sun.jna.Pointer inBuffer, IMessageMetadata outMetadata, com.sun.jna.Pointer outBuffer);
            }

            public static interface Callback_openCursor extends com.sun.jna.Callback
            {
                public IResultSet invoke(IStatement self, IStatus status, ITransaction transaction, IMessageMetadata inMetadata, com.sun.jna.Pointer inBuffer, IMessageMetadata outMetadata, int flags);
            }

            public static interface Callback_setCursorName extends com.sun.jna.Callback
            {
                public void invoke(IStatement self, IStatus status, String name);
            }

            public static interface Callback_free extends com.sun.jna.Callback
            {
                public void invoke(IStatement self, IStatus status);
            }

            public static interface Callback_getFlags extends com.sun.jna.Callback
            {
                public int invoke(IStatement self, IStatus status);
            }

            public VTable(com.sun.jna.Pointer pointer)
            {
                super(pointer);
            }

            public VTable(final IStatementIntf obj)
            {
                super(obj);

                getInfo = new Callback_getInfo() {
                    @Override
                    public void invoke(IStatement self, IStatus status, int itemsLength, byte[] items, int bufferLength, byte[] buffer)
                    {
                        try
                        {
                            obj.getInfo(status, itemsLength, items, bufferLength, buffer);
                        }
                        catch (Throwable t)
                        {
                            FbException.catchException(status, t);
                        }
                    }
                };

                getType = new Callback_getType() {
                    @Override
                    public int invoke(IStatement self, IStatus status)
                    {
                        try
                        {
                            return obj.getType(status);
                        }
                        catch (Throwable t)
                        {
                            FbException.catchException(status, t);
                            return 0;
                        }
                    }
                };

                getPlan = new Callback_getPlan() {
                    @Override
                    public String invoke(IStatement self, IStatus status, boolean detailed)
                    {
                        try
                        {
                            return obj.getPlan(status, detailed);
                        }
                        catch (Throwable t)
                        {
                            FbException.catchException(status, t);
                            return null;
                        }
                    }
                };

                getAffectedRecords = new Callback_getAffectedRecords() {
                    @Override
                    public long invoke(IStatement self, IStatus status)
                    {
                        try
                        {
                            return obj.getAffectedRecords(status);
                        }
                        catch (Throwable t)
                        {
                            FbException.catchException(status, t);
                            return 0;
                        }
                    }
                };

                getInputMetadata = new Callback_getInputMetadata() {
                    @Override
                    public IMessageMetadata invoke(IStatement self, IStatus status)
                    {
                        try
                        {
                            return obj.getInputMetadata(status);
                        }
                        catch (Throwable t)
                        {
                            FbException.catchException(status, t);
                            return null;
                        }
                    }
                };

                getOutputMetadata = new Callback_getOutputMetadata() {
                    @Override
                    public IMessageMetadata invoke(IStatement self, IStatus status)
                    {
                        try
                        {
                            return obj.getOutputMetadata(status);
                        }
                        catch (Throwable t)
                        {
                            FbException.catchException(status, t);
                            return null;
                        }
                    }
                };

                execute = new Callback_execute() {
                    @Override
                    public ITransaction invoke(IStatement self, IStatus status, ITransaction transaction, IMessageMetadata inMetadata, com.sun.jna.Pointer inBuffer, IMessageMetadata outMetadata, com.sun.jna.Pointer outBuffer)
                    {
                        try
                        {
                            return obj.execute(status, transaction, inMetadata, inBuffer, outMetadata, outBuffer);
                        }
                        catch (Throwable t)
                        {
                            FbException.catchException(status, t);
                            return null;
                        }
                    }
                };

                openCursor = new Callback_openCursor() {
                    @Override
                    public IResultSet invoke(IStatement self, IStatus status, ITransaction transaction, IMessageMetadata inMetadata, com.sun.jna.Pointer inBuffer, IMessageMetadata outMetadata, int flags)
                    {
                        try
                        {
                            return obj.openCursor(status, transaction, inMetadata, inBuffer, outMetadata, flags);
                        }
                        catch (Throwable t)
                        {
                            FbException.catchException(status, t);
                            return null;
                        }
                    }
                };

                setCursorName = new Callback_setCursorName() {
                    @Override
                    public void invoke(IStatement self, IStatus status, String name)
                    {
                        try
                        {
                            obj.setCursorName(status, name);
                        }
                        catch (Throwable t)
                        {
                            FbException.catchException(status, t);
                        }
                    }
                };

                free = new Callback_free() {
                    @Override
                    public void invoke(IStatement self, IStatus status)
                    {
                        try
                        {
                            obj.free(status);
                        }
                        catch (Throwable t)
                        {
                            FbException.catchException(status, t);
                        }
                    }
                };

                getFlags = new Callback_getFlags() {
                    @Override
                    public int invoke(IStatement self, IStatus status)
                    {
                        try
                        {
                            return obj.getFlags(status);
                        }
                        catch (Throwable t)
                        {
                            FbException.catchException(status, t);
                            return 0;
                        }
                    }
                };
            }

            public VTable()
            {
            }

            public Callback_getInfo getInfo;
            public Callback_getType getType;
            public Callback_getPlan getPlan;
            public Callback_getAffectedRecords getAffectedRecords;
            public Callback_getInputMetadata getInputMetadata;
            public Callback_getOutputMetadata getOutputMetadata;
            public Callback_execute execute;
            public Callback_openCursor openCursor;
            public Callback_setCursorName setCursorName;
            public Callback_free free;
            public Callback_getFlags getFlags;

            @Override
            protected java.util.List<String> getFieldOrder()
            {
                java.util.List<String> fields = super.getFieldOrder();
                fields.addAll(java.util.Arrays.asList("getInfo", "getType", "getPlan", "getAffectedRecords", "getInputMetadata", "getOutputMetadata", "execute", "openCursor", "setCursorName", "free", "getFlags"));
                return fields;
            }
        }

        public IStatement()
        {
        }

        public IStatement(IStatementIntf obj)
        {
            vTable = new VTable(obj);
            vTable.write();
            cloopVTable = vTable.getPointer();
            write();
        }

        @Override
        protected VTable createVTable()
        {
            return new VTable(cloopVTable);
        }

        public void getInfo(IStatus status, int itemsLength, byte[] items, int bufferLength, byte[] buffer) throws FbException
        {
            VTable vTable = getVTable();
            vTable.getInfo.invoke(this, status, itemsLength, items, bufferLength, buffer);
            FbException.checkException(status);
        }

        public int getType(IStatus status) throws FbException
        {
            VTable vTable = getVTable();
            int result = vTable.getType.invoke(this, status);
            FbException.checkException(status);
            return result;
        }

        public String getPlan(IStatus status, boolean detailed) throws FbException
        {
            VTable vTable = getVTable();
            String result = vTable.getPlan.invoke(this, status, detailed);
            FbException.checkException(status);
            return result;
        }

        public long getAffectedRecords(IStatus status) throws FbException
        {
            VTable vTable = getVTable();
            long result = vTable.getAffectedRecords.invoke(this, status);
            FbException.checkException(status);
            return result;
        }

        public IMessageMetadata getInputMetadata(IStatus status) throws FbException
        {
            VTable vTable = getVTable();
            IMessageMetadata result = vTable.getInputMetadata.invoke(this, status);
            FbException.checkException(status);
            return result;
        }

        public IMessageMetadata getOutputMetadata(IStatus status) throws FbException
        {
            VTable vTable = getVTable();
            IMessageMetadata result = vTable.getOutputMetadata.invoke(this, status);
            FbException.checkException(status);
            return result;
        }

        public ITransaction execute(IStatus status, ITransaction transaction, IMessageMetadata inMetadata, com.sun.jna.Pointer inBuffer, IMessageMetadata outMetadata, com.sun.jna.Pointer outBuffer) throws FbException
        {
            VTable vTable = getVTable();
            ITransaction result = vTable.execute.invoke(this, status, transaction, inMetadata, inBuffer, outMetadata, outBuffer);
            FbException.checkException(status);
            return result;
        }

        public IResultSet openCursor(IStatus status, ITransaction transaction, IMessageMetadata inMetadata, com.sun.jna.Pointer inBuffer, IMessageMetadata outMetadata, int flags) throws FbException
        {
            VTable vTable = getVTable();
            IResultSet result = vTable.openCursor.invoke(this, status, transaction, inMetadata, inBuffer, outMetadata, flags);
            FbException.checkException(status);
            return result;
        }

        public void setCursorName(IStatus status, String name) throws FbException
        {
            VTable vTable = getVTable();
            vTable.setCursorName.invoke(this, status, name);
            FbException.checkException(status);
        }

        public void free(IStatus status) throws FbException
        {
            VTable vTable = getVTable();
            vTable.free.invoke(this, status);
            FbException.checkException(status);
        }

        public int getFlags(IStatus status) throws FbException
        {
            VTable vTable = getVTable();
            int result = vTable.getFlags.invoke(this, status);
            FbException.checkException(status);
            return result;
        }
    }

    public static class IRequest extends IReferenceCounted implements IRequestIntf
    {
        public static class VTable extends IReferenceCounted.VTable
        {
            public static interface Callback_receive extends com.sun.jna.Callback
            {
                public void invoke(IRequest self, IStatus status, int level, int msgType, int length, byte[] message);
            }

            public static interface Callback_send extends com.sun.jna.Callback
            {
                public void invoke(IRequest self, IStatus status, int level, int msgType, int length, byte[] message);
            }

            public static interface Callback_getInfo extends com.sun.jna.Callback
            {
                public void invoke(IRequest self, IStatus status, int level, int itemsLength, byte[] items, int bufferLength, byte[] buffer);
            }

            public static interface Callback_start extends com.sun.jna.Callback
            {
                public void invoke(IRequest self, IStatus status, ITransaction tra, int level);
            }

            public static interface Callback_startAndSend extends com.sun.jna.Callback
            {
                public void invoke(IRequest self, IStatus status, ITransaction tra, int level, int msgType, int length, byte[] message);
            }

            public static interface Callback_unwind extends com.sun.jna.Callback
            {
                public void invoke(IRequest self, IStatus status, int level);
            }

            public static interface Callback_free extends com.sun.jna.Callback
            {
                public void invoke(IRequest self, IStatus status);
            }

            public VTable(com.sun.jna.Pointer pointer)
            {
                super(pointer);
            }

            public VTable(final IRequestIntf obj)
            {
                super(obj);

                receive = new Callback_receive() {
                    @Override
                    public void invoke(IRequest self, IStatus status, int level, int msgType, int length, byte[] message)
                    {
                        try
                        {
                            obj.receive(status, level, msgType, length, message);
                        }
                        catch (Throwable t)
                        {
                            FbException.catchException(status, t);
                        }
                    }
                };

                send = new Callback_send() {
                    @Override
                    public void invoke(IRequest self, IStatus status, int level, int msgType, int length, byte[] message)
                    {
                        try
                        {
                            obj.send(status, level, msgType, length, message);
                        }
                        catch (Throwable t)
                        {
                            FbException.catchException(status, t);
                        }
                    }
                };

                getInfo = new Callback_getInfo() {
                    @Override
                    public void invoke(IRequest self, IStatus status, int level, int itemsLength, byte[] items, int bufferLength, byte[] buffer)
                    {
                        try
                        {
                            obj.getInfo(status, level, itemsLength, items, bufferLength, buffer);
                        }
                        catch (Throwable t)
                        {
                            FbException.catchException(status, t);
                        }
                    }
                };

                start = new Callback_start() {
                    @Override
                    public void invoke(IRequest self, IStatus status, ITransaction tra, int level)
                    {
                        try
                        {
                            obj.start(status, tra, level);
                        }
                        catch (Throwable t)
                        {
                            FbException.catchException(status, t);
                        }
                    }
                };

                startAndSend = new Callback_startAndSend() {
                    @Override
                    public void invoke(IRequest self, IStatus status, ITransaction tra, int level, int msgType, int length, byte[] message)
                    {
                        try
                        {
                            obj.startAndSend(status, tra, level, msgType, length, message);
                        }
                        catch (Throwable t)
                        {
                            FbException.catchException(status, t);
                        }
                    }
                };

                unwind = new Callback_unwind() {
                    @Override
                    public void invoke(IRequest self, IStatus status, int level)
                    {
                        try
                        {
                            obj.unwind(status, level);
                        }
                        catch (Throwable t)
                        {
                            FbException.catchException(status, t);
                        }
                    }
                };

                free = new Callback_free() {
                    @Override
                    public void invoke(IRequest self, IStatus status)
                    {
                        try
                        {
                            obj.free(status);
                        }
                        catch (Throwable t)
                        {
                            FbException.catchException(status, t);
                        }
                    }
                };
            }

            public VTable()
            {
            }

            public Callback_receive receive;
            public Callback_send send;
            public Callback_getInfo getInfo;
            public Callback_start start;
            public Callback_startAndSend startAndSend;
            public Callback_unwind unwind;
            public Callback_free free;

            @Override
            protected java.util.List<String> getFieldOrder()
            {
                java.util.List<String> fields = super.getFieldOrder();
                fields.addAll(java.util.Arrays.asList("receive", "send", "getInfo", "start", "startAndSend", "unwind", "free"));
                return fields;
            }
        }

        public IRequest()
        {
        }

        public IRequest(IRequestIntf obj)
        {
            vTable = new VTable(obj);
            vTable.write();
            cloopVTable = vTable.getPointer();
            write();
        }

        @Override
        protected VTable createVTable()
        {
            return new VTable(cloopVTable);
        }

        public void receive(IStatus status, int level, int msgType, int length, byte[] message) throws FbException
        {
            VTable vTable = getVTable();
            vTable.receive.invoke(this, status, level, msgType, length, message);
            FbException.checkException(status);
        }

        public void send(IStatus status, int level, int msgType, int length, byte[] message) throws FbException
        {
            VTable vTable = getVTable();
            vTable.send.invoke(this, status, level, msgType, length, message);
            FbException.checkException(status);
        }

        public void getInfo(IStatus status, int level, int itemsLength, byte[] items, int bufferLength, byte[] buffer) throws FbException
        {
            VTable vTable = getVTable();
            vTable.getInfo.invoke(this, status, level, itemsLength, items, bufferLength, buffer);
            FbException.checkException(status);
        }

        public void start(IStatus status, ITransaction tra, int level) throws FbException
        {
            VTable vTable = getVTable();
            vTable.start.invoke(this, status, tra, level);
            FbException.checkException(status);
        }

        public void startAndSend(IStatus status, ITransaction tra, int level, int msgType, int length, byte[] message) throws FbException
        {
            VTable vTable = getVTable();
            vTable.startAndSend.invoke(this, status, tra, level, msgType, length, message);
            FbException.checkException(status);
        }

        public void unwind(IStatus status, int level) throws FbException
        {
            VTable vTable = getVTable();
            vTable.unwind.invoke(this, status, level);
            FbException.checkException(status);
        }

        public void free(IStatus status) throws FbException
        {
            VTable vTable = getVTable();
            vTable.free.invoke(this, status);
            FbException.checkException(status);
        }
    }

    public static class IEvents extends IReferenceCounted implements IEventsIntf
    {
        public static class VTable extends IReferenceCounted.VTable
        {
            public static interface Callback_cancel extends com.sun.jna.Callback
            {
                public void invoke(IEvents self, IStatus status);
            }

            public VTable(com.sun.jna.Pointer pointer)
            {
                super(pointer);
            }

            public VTable(final IEventsIntf obj)
            {
                super(obj);

                cancel = new Callback_cancel() {
                    @Override
                    public void invoke(IEvents self, IStatus status)
                    {
                        try
                        {
                            obj.cancel(status);
                        }
                        catch (Throwable t)
                        {
                            FbException.catchException(status, t);
                        }
                    }
                };
            }

            public VTable()
            {
            }

            public Callback_cancel cancel;

            @Override
            protected java.util.List<String> getFieldOrder()
            {
                java.util.List<String> fields = super.getFieldOrder();
                fields.addAll(java.util.Arrays.asList("cancel"));
                return fields;
            }
        }

        public IEvents()
        {
        }

        public IEvents(IEventsIntf obj)
        {
            vTable = new VTable(obj);
            vTable.write();
            cloopVTable = vTable.getPointer();
            write();
        }

        @Override
        protected VTable createVTable()
        {
            return new VTable(cloopVTable);
        }

        public void cancel(IStatus status) throws FbException
        {
            VTable vTable = getVTable();
            vTable.cancel.invoke(this, status);
            FbException.checkException(status);
        }
    }

    public static class IAttachment extends IReferenceCounted implements IAttachmentIntf
    {
        public static class VTable extends IReferenceCounted.VTable
        {
            public static interface Callback_getInfo extends com.sun.jna.Callback
            {
                public void invoke(IAttachment self, IStatus status, int itemsLength, byte[] items, int bufferLength, byte[] buffer);
            }

            public static interface Callback_startTransaction extends com.sun.jna.Callback
            {
                public ITransaction invoke(IAttachment self, IStatus status, int tpbLength, byte[] tpb);
            }

            public static interface Callback_reconnectTransaction extends com.sun.jna.Callback
            {
                public ITransaction invoke(IAttachment self, IStatus status, int length, byte[] id);
            }

            public static interface Callback_compileRequest extends com.sun.jna.Callback
            {
                public IRequest invoke(IAttachment self, IStatus status, int blrLength, byte[] blr);
            }

            public static interface Callback_transactRequest extends com.sun.jna.Callback
            {
                public void invoke(IAttachment self, IStatus status, ITransaction transaction, int blrLength, byte[] blr, int inMsgLength, byte[] inMsg, int outMsgLength, byte[] outMsg);
            }

            public static interface Callback_createBlob extends com.sun.jna.Callback
            {
                public IBlob invoke(IAttachment self, IStatus status, ITransaction transaction, ISC_QUAD[] id, int bpbLength, byte[] bpb);
            }

            public static interface Callback_openBlob extends com.sun.jna.Callback
            {
                public IBlob invoke(IAttachment self, IStatus status, ITransaction transaction, ISC_QUAD[] id, int bpbLength, byte[] bpb);
            }

            public static interface Callback_getSlice extends com.sun.jna.Callback
            {
                public int invoke(IAttachment self, IStatus status, ITransaction transaction, ISC_QUAD[] id, int sdlLength, byte[] sdl, int paramLength, byte[] param, int sliceLength, byte[] slice);
            }

            public static interface Callback_putSlice extends com.sun.jna.Callback
            {
                public void invoke(IAttachment self, IStatus status, ITransaction transaction, ISC_QUAD[] id, int sdlLength, byte[] sdl, int paramLength, byte[] param, int sliceLength, byte[] slice);
            }

            public static interface Callback_executeDyn extends com.sun.jna.Callback
            {
                public void invoke(IAttachment self, IStatus status, ITransaction transaction, int length, byte[] dyn);
            }

            public static interface Callback_prepare extends com.sun.jna.Callback
            {
                public IStatement invoke(IAttachment self, IStatus status, ITransaction tra, int stmtLength, String sqlStmt, int dialect, int flags);
            }

            public static interface Callback_execute extends com.sun.jna.Callback
            {
                public ITransaction invoke(IAttachment self, IStatus status, ITransaction transaction, int stmtLength, String sqlStmt, int dialect, IMessageMetadata inMetadata, com.sun.jna.Pointer inBuffer, IMessageMetadata outMetadata, com.sun.jna.Pointer outBuffer);
            }

            public static interface Callback_openCursor extends com.sun.jna.Callback
            {
                public IResultSet invoke(IAttachment self, IStatus status, ITransaction transaction, int stmtLength, String sqlStmt, int dialect, IMessageMetadata inMetadata, com.sun.jna.Pointer inBuffer, IMessageMetadata outMetadata, String cursorName, int cursorFlags);
            }

            public static interface Callback_queEvents extends com.sun.jna.Callback
            {
                public IEvents invoke(IAttachment self, IStatus status, IEventCallback callback, int length, byte[] events);
            }

            public static interface Callback_cancelOperation extends com.sun.jna.Callback
            {
                public void invoke(IAttachment self, IStatus status, int option);
            }

            public static interface Callback_ping extends com.sun.jna.Callback
            {
                public void invoke(IAttachment self, IStatus status);
            }

            public static interface Callback_detach extends com.sun.jna.Callback
            {
                public void invoke(IAttachment self, IStatus status);
            }

            public static interface Callback_dropDatabase extends com.sun.jna.Callback
            {
                public void invoke(IAttachment self, IStatus status);
            }

            public static interface Callback_replicate extends com.sun.jna.Callback
            {
                public void invoke(IAttachment self, IStatus status, int length, byte[] data);
            }

            public VTable(com.sun.jna.Pointer pointer)
            {
                super(pointer);
            }

            public VTable(final IAttachmentIntf obj)
            {
                super(obj);

                getInfo = new Callback_getInfo() {
                    @Override
                    public void invoke(IAttachment self, IStatus status, int itemsLength, byte[] items, int bufferLength, byte[] buffer)
                    {
                        try
                        {
                            obj.getInfo(status, itemsLength, items, bufferLength, buffer);
                        }
                        catch (Throwable t)
                        {
                            FbException.catchException(status, t);
                        }
                    }
                };

                startTransaction = new Callback_startTransaction() {
                    @Override
                    public ITransaction invoke(IAttachment self, IStatus status, int tpbLength, byte[] tpb)
                    {
                        try
                        {
                            return obj.startTransaction(status, tpbLength, tpb);
                        }
                        catch (Throwable t)
                        {
                            FbException.catchException(status, t);
                            return null;
                        }
                    }
                };

                reconnectTransaction = new Callback_reconnectTransaction() {
                    @Override
                    public ITransaction invoke(IAttachment self, IStatus status, int length, byte[] id)
                    {
                        try
                        {
                            return obj.reconnectTransaction(status, length, id);
                        }
                        catch (Throwable t)
                        {
                            FbException.catchException(status, t);
                            return null;
                        }
                    }
                };

                compileRequest = new Callback_compileRequest() {
                    @Override
                    public IRequest invoke(IAttachment self, IStatus status, int blrLength, byte[] blr)
                    {
                        try
                        {
                            return obj.compileRequest(status, blrLength, blr);
                        }
                        catch (Throwable t)
                        {
                            FbException.catchException(status, t);
                            return null;
                        }
                    }
                };

                transactRequest = new Callback_transactRequest() {
                    @Override
                    public void invoke(IAttachment self, IStatus status, ITransaction transaction, int blrLength, byte[] blr, int inMsgLength, byte[] inMsg, int outMsgLength, byte[] outMsg)
                    {
                        try
                        {
                            obj.transactRequest(status, transaction, blrLength, blr, inMsgLength, inMsg, outMsgLength, outMsg);
                        }
                        catch (Throwable t)
                        {
                            FbException.catchException(status, t);
                        }
                    }
                };

                createBlob = new Callback_createBlob() {
                    @Override
                    public IBlob invoke(IAttachment self, IStatus status, ITransaction transaction, ISC_QUAD[] id, int bpbLength, byte[] bpb)
                    {
                        try
                        {
                            return obj.createBlob(status, transaction, id, bpbLength, bpb);
                        }
                        catch (Throwable t)
                        {
                            FbException.catchException(status, t);
                            return null;
                        }
                    }
                };

                openBlob = new Callback_openBlob() {
                    @Override
                    public IBlob invoke(IAttachment self, IStatus status, ITransaction transaction, ISC_QUAD[] id, int bpbLength, byte[] bpb)
                    {
                        try
                        {
                            return obj.openBlob(status, transaction, id, bpbLength, bpb);
                        }
                        catch (Throwable t)
                        {
                            FbException.catchException(status, t);
                            return null;
                        }
                    }
                };

                getSlice = new Callback_getSlice() {
                    @Override
                    public int invoke(IAttachment self, IStatus status, ITransaction transaction, ISC_QUAD[] id, int sdlLength, byte[] sdl, int paramLength, byte[] param, int sliceLength, byte[] slice)
                    {
                        try
                        {
                            return obj.getSlice(status, transaction, id, sdlLength, sdl, paramLength, param, sliceLength, slice);
                        }
                        catch (Throwable t)
                        {
                            FbException.catchException(status, t);
                            return 0;
                        }
                    }
                };

                putSlice = new Callback_putSlice() {
                    @Override
                    public void invoke(IAttachment self, IStatus status, ITransaction transaction, ISC_QUAD[] id, int sdlLength, byte[] sdl, int paramLength, byte[] param, int sliceLength, byte[] slice)
                    {
                        try
                        {
                            obj.putSlice(status, transaction, id, sdlLength, sdl, paramLength, param, sliceLength, slice);
                        }
                        catch (Throwable t)
                        {
                            FbException.catchException(status, t);
                        }
                    }
                };

                executeDyn = new Callback_executeDyn() {
                    @Override
                    public void invoke(IAttachment self, IStatus status, ITransaction transaction, int length, byte[] dyn)
                    {
                        try
                        {
                            obj.executeDyn(status, transaction, length, dyn);
                        }
                        catch (Throwable t)
                        {
                            FbException.catchException(status, t);
                        }
                    }
                };

                prepare = new Callback_prepare() {
                    @Override
                    public IStatement invoke(IAttachment self, IStatus status, ITransaction tra, int stmtLength, String sqlStmt, int dialect, int flags)
                    {
                        try
                        {
                            return obj.prepare(status, tra, stmtLength, sqlStmt, dialect, flags);
                        }
                        catch (Throwable t)
                        {
                            FbException.catchException(status, t);
                            return null;
                        }
                    }
                };

                execute = new Callback_execute() {
                    @Override
                    public ITransaction invoke(IAttachment self, IStatus status, ITransaction transaction, int stmtLength, String sqlStmt, int dialect, IMessageMetadata inMetadata, com.sun.jna.Pointer inBuffer, IMessageMetadata outMetadata, com.sun.jna.Pointer outBuffer)
                    {
                        try
                        {
                            return obj.execute(status, transaction, stmtLength, sqlStmt, dialect, inMetadata, inBuffer, outMetadata, outBuffer);
                        }
                        catch (Throwable t)
                        {
                            FbException.catchException(status, t);
                            return null;
                        }
                    }
                };

                openCursor = new Callback_openCursor() {
                    @Override
                    public IResultSet invoke(IAttachment self, IStatus status, ITransaction transaction, int stmtLength, String sqlStmt, int dialect, IMessageMetadata inMetadata, com.sun.jna.Pointer inBuffer, IMessageMetadata outMetadata, String cursorName, int cursorFlags)
                    {
                        try
                        {
                            return obj.openCursor(status, transaction, stmtLength, sqlStmt, dialect, inMetadata, inBuffer, outMetadata, cursorName, cursorFlags);
                        }
                        catch (Throwable t)
                        {
                            FbException.catchException(status, t);
                            return null;
                        }
                    }
                };

                queEvents = new Callback_queEvents() {
                    @Override
                    public IEvents invoke(IAttachment self, IStatus status, IEventCallback callback, int length, byte[] events)
                    {
                        try
                        {
                            return obj.queEvents(status, callback, length, events);
                        }
                        catch (Throwable t)
                        {
                            FbException.catchException(status, t);
                            return null;
                        }
                    }
                };

                cancelOperation = new Callback_cancelOperation() {
                    @Override
                    public void invoke(IAttachment self, IStatus status, int option)
                    {
                        try
                        {
                            obj.cancelOperation(status, option);
                        }
                        catch (Throwable t)
                        {
                            FbException.catchException(status, t);
                        }
                    }
                };

                ping = new Callback_ping() {
                    @Override
                    public void invoke(IAttachment self, IStatus status)
                    {
                        try
                        {
                            obj.ping(status);
                        }
                        catch (Throwable t)
                        {
                            FbException.catchException(status, t);
                        }
                    }
                };

                detach = new Callback_detach() {
                    @Override
                    public void invoke(IAttachment self, IStatus status)
                    {
                        try
                        {
                            obj.detach(status);
                        }
                        catch (Throwable t)
                        {
                            FbException.catchException(status, t);
                        }
                    }
                };

                dropDatabase = new Callback_dropDatabase() {
                    @Override
                    public void invoke(IAttachment self, IStatus status)
                    {
                        try
                        {
                            obj.dropDatabase(status);
                        }
                        catch (Throwable t)
                        {
                            FbException.catchException(status, t);
                        }
                    }
                };

                replicate = new Callback_replicate() {
                    @Override
                    public void invoke(IAttachment self, IStatus status, int length, byte[] data)
                    {
                        try
                        {
                            obj.replicate(status, length, data);
                        }
                        catch (Throwable t)
                        {
                            FbException.catchException(status, t);
                        }
                    }
                };
            }

            public VTable()
            {
            }

            public Callback_getInfo getInfo;
            public Callback_startTransaction startTransaction;
            public Callback_reconnectTransaction reconnectTransaction;
            public Callback_compileRequest compileRequest;
            public Callback_transactRequest transactRequest;
            public Callback_createBlob createBlob;
            public Callback_openBlob openBlob;
            public Callback_getSlice getSlice;
            public Callback_putSlice putSlice;
            public Callback_executeDyn executeDyn;
            public Callback_prepare prepare;
            public Callback_execute execute;
            public Callback_openCursor openCursor;
            public Callback_queEvents queEvents;
            public Callback_cancelOperation cancelOperation;
            public Callback_ping ping;
            public Callback_detach detach;
            public Callback_dropDatabase dropDatabase;
            public Callback_replicate replicate;

            @Override
            protected java.util.List<String> getFieldOrder()
            {
                java.util.List<String> fields = super.getFieldOrder();
                fields.addAll(java.util.Arrays.asList("getInfo", "startTransaction", "reconnectTransaction", "compileRequest", "transactRequest", "createBlob", "openBlob", "getSlice", "putSlice", "executeDyn", "prepare", "execute", "openCursor", "queEvents", "cancelOperation", "ping", "detach", "dropDatabase", "replicate"));
                return fields;
            }
        }

        public IAttachment()
        {
        }

        public IAttachment(IAttachmentIntf obj)
        {
            vTable = new VTable(obj);
            vTable.write();
            cloopVTable = vTable.getPointer();
            write();
        }

        @Override
        protected VTable createVTable()
        {
            return new VTable(cloopVTable);
        }

        public void getInfo(IStatus status, int itemsLength, byte[] items, int bufferLength, byte[] buffer) throws FbException
        {
            VTable vTable = getVTable();
            vTable.getInfo.invoke(this, status, itemsLength, items, bufferLength, buffer);
            FbException.checkException(status);
        }

        public ITransaction startTransaction(IStatus status, int tpbLength, byte[] tpb) throws FbException
        {
            VTable vTable = getVTable();
            ITransaction result = vTable.startTransaction.invoke(this, status, tpbLength, tpb);
            FbException.checkException(status);
            return result;
        }

        public ITransaction reconnectTransaction(IStatus status, int length, byte[] id) throws FbException
        {
            VTable vTable = getVTable();
            ITransaction result = vTable.reconnectTransaction.invoke(this, status, length, id);
            FbException.checkException(status);
            return result;
        }

        public IRequest compileRequest(IStatus status, int blrLength, byte[] blr) throws FbException
        {
            VTable vTable = getVTable();
            IRequest result = vTable.compileRequest.invoke(this, status, blrLength, blr);
            FbException.checkException(status);
            return result;
        }

        public void transactRequest(IStatus status, ITransaction transaction, int blrLength, byte[] blr, int inMsgLength, byte[] inMsg, int outMsgLength, byte[] outMsg) throws FbException
        {
            VTable vTable = getVTable();
            vTable.transactRequest.invoke(this, status, transaction, blrLength, blr, inMsgLength, inMsg, outMsgLength, outMsg);
            FbException.checkException(status);
        }

        public IBlob createBlob(IStatus status, ITransaction transaction, ISC_QUAD[] id, int bpbLength, byte[] bpb) throws FbException
        {
            VTable vTable = getVTable();
            IBlob result = vTable.createBlob.invoke(this, status, transaction, id, bpbLength, bpb);
            FbException.checkException(status);
            return result;
        }

        public IBlob openBlob(IStatus status, ITransaction transaction, ISC_QUAD[] id, int bpbLength, byte[] bpb) throws FbException
        {
            VTable vTable = getVTable();
            IBlob result = vTable.openBlob.invoke(this, status, transaction, id, bpbLength, bpb);
            FbException.checkException(status);
            return result;
        }

        public int getSlice(IStatus status, ITransaction transaction, ISC_QUAD[] id, int sdlLength, byte[] sdl, int paramLength, byte[] param, int sliceLength, byte[] slice) throws FbException
        {
            VTable vTable = getVTable();
            int result = vTable.getSlice.invoke(this, status, transaction, id, sdlLength, sdl, paramLength, param, sliceLength, slice);
            FbException.checkException(status);
            return result;
        }

        public void putSlice(IStatus status, ITransaction transaction, ISC_QUAD[] id, int sdlLength, byte[] sdl, int paramLength, byte[] param, int sliceLength, byte[] slice) throws FbException
        {
            VTable vTable = getVTable();
            vTable.putSlice.invoke(this, status, transaction, id, sdlLength, sdl, paramLength, param, sliceLength, slice);
            FbException.checkException(status);
        }

        public void executeDyn(IStatus status, ITransaction transaction, int length, byte[] dyn) throws FbException
        {
            VTable vTable = getVTable();
            vTable.executeDyn.invoke(this, status, transaction, length, dyn);
            FbException.checkException(status);
        }

        public IStatement prepare(IStatus status, ITransaction tra, int stmtLength, String sqlStmt, int dialect, int flags) throws FbException
        {
            VTable vTable = getVTable();
            IStatement result = vTable.prepare.invoke(this, status, tra, stmtLength, sqlStmt, dialect, flags);
            FbException.checkException(status);
            return result;
        }

        public ITransaction execute(IStatus status, ITransaction transaction, int stmtLength, String sqlStmt, int dialect, IMessageMetadata inMetadata, com.sun.jna.Pointer inBuffer, IMessageMetadata outMetadata, com.sun.jna.Pointer outBuffer) throws FbException
        {
            VTable vTable = getVTable();
            ITransaction result = vTable.execute.invoke(this, status, transaction, stmtLength, sqlStmt, dialect, inMetadata, inBuffer, outMetadata, outBuffer);
            FbException.checkException(status);
            return result;
        }

        public IResultSet openCursor(IStatus status, ITransaction transaction, int stmtLength, String sqlStmt, int dialect, IMessageMetadata inMetadata, com.sun.jna.Pointer inBuffer, IMessageMetadata outMetadata, String cursorName, int cursorFlags) throws FbException
        {
            VTable vTable = getVTable();
            IResultSet result = vTable.openCursor.invoke(this, status, transaction, stmtLength, sqlStmt, dialect, inMetadata, inBuffer, outMetadata, cursorName, cursorFlags);
            FbException.checkException(status);
            return result;
        }

        public IEvents queEvents(IStatus status, IEventCallback callback, int length, byte[] events) throws FbException
        {
            VTable vTable = getVTable();
            IEvents result = vTable.queEvents.invoke(this, status, callback, length, events);
            FbException.checkException(status);
            return result;
        }

        public void cancelOperation(IStatus status, int option) throws FbException
        {
            VTable vTable = getVTable();
            vTable.cancelOperation.invoke(this, status, option);
            FbException.checkException(status);
        }

        public void ping(IStatus status) throws FbException
        {
            VTable vTable = getVTable();
            vTable.ping.invoke(this, status);
            FbException.checkException(status);
        }

        public void detach(IStatus status) throws FbException
        {
            VTable vTable = getVTable();
            vTable.detach.invoke(this, status);
            FbException.checkException(status);
        }

        public void dropDatabase(IStatus status) throws FbException
        {
            VTable vTable = getVTable();
            vTable.dropDatabase.invoke(this, status);
            FbException.checkException(status);
        }

        public void replicate(IStatus status, int length, byte[] data) throws FbException
        {
            VTable vTable = getVTable();
            vTable.replicate.invoke(this, status, length, data);
            FbException.checkException(status);
        }
    }

    public static class IService extends IReferenceCounted implements IServiceIntf
    {
        public static class VTable extends IReferenceCounted.VTable
        {
            public static interface Callback_detach extends com.sun.jna.Callback
            {
                public void invoke(IService self, IStatus status);
            }

            public static interface Callback_query extends com.sun.jna.Callback
            {
                public void invoke(IService self, IStatus status, int sendLength, byte[] sendItems, int receiveLength, byte[] receiveItems, int bufferLength, byte[] buffer);
            }

            public static interface Callback_start extends com.sun.jna.Callback
            {
                public void invoke(IService self, IStatus status, int spbLength, byte[] spb);
            }

            public VTable(com.sun.jna.Pointer pointer)
            {
                super(pointer);
            }

            public VTable(final IServiceIntf obj)
            {
                super(obj);

                detach = new Callback_detach() {
                    @Override
                    public void invoke(IService self, IStatus status)
                    {
                        try
                        {
                            obj.detach(status);
                        }
                        catch (Throwable t)
                        {
                            FbException.catchException(status, t);
                        }
                    }
                };

                query = new Callback_query() {
                    @Override
                    public void invoke(IService self, IStatus status, int sendLength, byte[] sendItems, int receiveLength, byte[] receiveItems, int bufferLength, byte[] buffer)
                    {
                        try
                        {
                            obj.query(status, sendLength, sendItems, receiveLength, receiveItems, bufferLength, buffer);
                        }
                        catch (Throwable t)
                        {
                            FbException.catchException(status, t);
                        }
                    }
                };

                start = new Callback_start() {
                    @Override
                    public void invoke(IService self, IStatus status, int spbLength, byte[] spb)
                    {
                        try
                        {
                            obj.start(status, spbLength, spb);
                        }
                        catch (Throwable t)
                        {
                            FbException.catchException(status, t);
                        }
                    }
                };
            }

            public VTable()
            {
            }

            public Callback_detach detach;
            public Callback_query query;
            public Callback_start start;

            @Override
            protected java.util.List<String> getFieldOrder()
            {
                java.util.List<String> fields = super.getFieldOrder();
                fields.addAll(java.util.Arrays.asList("detach", "query", "start"));
                return fields;
            }
        }

        public IService()
        {
        }

        public IService(IServiceIntf obj)
        {
            vTable = new VTable(obj);
            vTable.write();
            cloopVTable = vTable.getPointer();
            write();
        }

        @Override
        protected VTable createVTable()
        {
            return new VTable(cloopVTable);
        }

        public void detach(IStatus status) throws FbException
        {
            VTable vTable = getVTable();
            vTable.detach.invoke(this, status);
            FbException.checkException(status);
        }

        public void query(IStatus status, int sendLength, byte[] sendItems, int receiveLength, byte[] receiveItems, int bufferLength, byte[] buffer) throws FbException
        {
            VTable vTable = getVTable();
            vTable.query.invoke(this, status, sendLength, sendItems, receiveLength, receiveItems, bufferLength, buffer);
            FbException.checkException(status);
        }

        public void start(IStatus status, int spbLength, byte[] spb) throws FbException
        {
            VTable vTable = getVTable();
            vTable.start.invoke(this, status, spbLength, spb);
            FbException.checkException(status);
        }
    }

    public static class IProvider extends IPluginBase implements IProviderIntf
    {
        public static class VTable extends IPluginBase.VTable
        {
            public static interface Callback_attachDatabase extends com.sun.jna.Callback
            {
                public IAttachment invoke(IProvider self, IStatus status, String fileName, int dpbLength, byte[] dpb);
            }

            public static interface Callback_createDatabase extends com.sun.jna.Callback
            {
                public IAttachment invoke(IProvider self, IStatus status, String fileName, int dpbLength, byte[] dpb);
            }

            public static interface Callback_attachServiceManager extends com.sun.jna.Callback
            {
                public IService invoke(IProvider self, IStatus status, String service, int spbLength, byte[] spb);
            }

            public static interface Callback_shutdown extends com.sun.jna.Callback
            {
                public void invoke(IProvider self, IStatus status, int timeout, int reason);
            }

            public static interface Callback_setDbCryptCallback extends com.sun.jna.Callback
            {
                public void invoke(IProvider self, IStatus status, ICryptKeyCallback cryptCallback);
            }

            public VTable(com.sun.jna.Pointer pointer)
            {
                super(pointer);
            }

            public VTable(final IProviderIntf obj)
            {
                super(obj);

                attachDatabase = new Callback_attachDatabase() {
                    @Override
                    public IAttachment invoke(IProvider self, IStatus status, String fileName, int dpbLength, byte[] dpb)
                    {
                        try
                        {
                            return obj.attachDatabase(status, fileName, dpbLength, dpb);
                        }
                        catch (Throwable t)
                        {
                            FbException.catchException(status, t);
                            return null;
                        }
                    }
                };

                createDatabase = new Callback_createDatabase() {
                    @Override
                    public IAttachment invoke(IProvider self, IStatus status, String fileName, int dpbLength, byte[] dpb)
                    {
                        try
                        {
                            return obj.createDatabase(status, fileName, dpbLength, dpb);
                        }
                        catch (Throwable t)
                        {
                            FbException.catchException(status, t);
                            return null;
                        }
                    }
                };

                attachServiceManager = new Callback_attachServiceManager() {
                    @Override
                    public IService invoke(IProvider self, IStatus status, String service, int spbLength, byte[] spb)
                    {
                        try
                        {
                            return obj.attachServiceManager(status, service, spbLength, spb);
                        }
                        catch (Throwable t)
                        {
                            FbException.catchException(status, t);
                            return null;
                        }
                    }
                };

                shutdown = new Callback_shutdown() {
                    @Override
                    public void invoke(IProvider self, IStatus status, int timeout, int reason)
                    {
                        try
                        {
                            obj.shutdown(status, timeout, reason);
                        }
                        catch (Throwable t)
                        {
                            FbException.catchException(status, t);
                        }
                    }
                };

                setDbCryptCallback = new Callback_setDbCryptCallback() {
                    @Override
                    public void invoke(IProvider self, IStatus status, ICryptKeyCallback cryptCallback)
                    {
                        try
                        {
                            obj.setDbCryptCallback(status, cryptCallback);
                        }
                        catch (Throwable t)
                        {
                            FbException.catchException(status, t);
                        }
                    }
                };
            }

            public VTable()
            {
            }

            public Callback_attachDatabase attachDatabase;
            public Callback_createDatabase createDatabase;
            public Callback_attachServiceManager attachServiceManager;
            public Callback_shutdown shutdown;
            public Callback_setDbCryptCallback setDbCryptCallback;

            @Override
            protected java.util.List<String> getFieldOrder()
            {
                java.util.List<String> fields = super.getFieldOrder();
                fields.addAll(java.util.Arrays.asList("attachDatabase", "createDatabase", "attachServiceManager", "shutdown", "setDbCryptCallback"));
                return fields;
            }
        }

        public IProvider()
        {
        }

        public IProvider(IProviderIntf obj)
        {
            vTable = new VTable(obj);
            vTable.write();
            cloopVTable = vTable.getPointer();
            write();
        }

        @Override
        protected VTable createVTable()
        {
            return new VTable(cloopVTable);
        }

        public IAttachment attachDatabase(IStatus status, String fileName, int dpbLength, byte[] dpb) throws FbException
        {
            VTable vTable = getVTable();
            IAttachment result = vTable.attachDatabase.invoke(this, status, fileName, dpbLength, dpb);
            FbException.checkException(status);
            return result;
        }

        public IAttachment createDatabase(IStatus status, String fileName, int dpbLength, byte[] dpb) throws FbException
        {
            VTable vTable = getVTable();
            IAttachment result = vTable.createDatabase.invoke(this, status, fileName, dpbLength, dpb);
            FbException.checkException(status);
            return result;
        }

        public IService attachServiceManager(IStatus status, String service, int spbLength, byte[] spb) throws FbException
        {
            VTable vTable = getVTable();
            IService result = vTable.attachServiceManager.invoke(this, status, service, spbLength, spb);
            FbException.checkException(status);
            return result;
        }

        public void shutdown(IStatus status, int timeout, int reason) throws FbException
        {
            VTable vTable = getVTable();
            vTable.shutdown.invoke(this, status, timeout, reason);
            FbException.checkException(status);
        }

        public void setDbCryptCallback(IStatus status, ICryptKeyCallback cryptCallback) throws FbException
        {
            VTable vTable = getVTable();
            vTable.setDbCryptCallback.invoke(this, status, cryptCallback);
            FbException.checkException(status);
        }
    }

    public static class IDtcStart extends IDisposable implements IDtcStartIntf
    {
        public static class VTable extends IDisposable.VTable
        {
            public static interface Callback_addAttachment extends com.sun.jna.Callback
            {
                public void invoke(IDtcStart self, IStatus status, IAttachment att);
            }

            public static interface Callback_addWithTpb extends com.sun.jna.Callback
            {
                public void invoke(IDtcStart self, IStatus status, IAttachment att, int length, byte[] tpb);
            }

            public static interface Callback_start extends com.sun.jna.Callback
            {
                public ITransaction invoke(IDtcStart self, IStatus status);
            }

            public VTable(com.sun.jna.Pointer pointer)
            {
                super(pointer);
            }

            public VTable(final IDtcStartIntf obj)
            {
                super(obj);

                addAttachment = new Callback_addAttachment() {
                    @Override
                    public void invoke(IDtcStart self, IStatus status, IAttachment att)
                    {
                        try
                        {
                            obj.addAttachment(status, att);
                        }
                        catch (Throwable t)
                        {
                            FbException.catchException(status, t);
                        }
                    }
                };

                addWithTpb = new Callback_addWithTpb() {
                    @Override
                    public void invoke(IDtcStart self, IStatus status, IAttachment att, int length, byte[] tpb)
                    {
                        try
                        {
                            obj.addWithTpb(status, att, length, tpb);
                        }
                        catch (Throwable t)
                        {
                            FbException.catchException(status, t);
                        }
                    }
                };

                start = new Callback_start() {
                    @Override
                    public ITransaction invoke(IDtcStart self, IStatus status)
                    {
                        try
                        {
                            return obj.start(status);
                        }
                        catch (Throwable t)
                        {
                            FbException.catchException(status, t);
                            return null;
                        }
                    }
                };
            }

            public VTable()
            {
            }

            public Callback_addAttachment addAttachment;
            public Callback_addWithTpb addWithTpb;
            public Callback_start start;

            @Override
            protected java.util.List<String> getFieldOrder()
            {
                java.util.List<String> fields = super.getFieldOrder();
                fields.addAll(java.util.Arrays.asList("addAttachment", "addWithTpb", "start"));
                return fields;
            }
        }

        public IDtcStart()
        {
        }

        public IDtcStart(IDtcStartIntf obj)
        {
            vTable = new VTable(obj);
            vTable.write();
            cloopVTable = vTable.getPointer();
            write();
        }

        @Override
        protected VTable createVTable()
        {
            return new VTable(cloopVTable);
        }

        public void addAttachment(IStatus status, IAttachment att) throws FbException
        {
            VTable vTable = getVTable();
            vTable.addAttachment.invoke(this, status, att);
            FbException.checkException(status);
        }

        public void addWithTpb(IStatus status, IAttachment att, int length, byte[] tpb) throws FbException
        {
            VTable vTable = getVTable();
            vTable.addWithTpb.invoke(this, status, att, length, tpb);
            FbException.checkException(status);
        }

        public ITransaction start(IStatus status) throws FbException
        {
            VTable vTable = getVTable();
            ITransaction result = vTable.start.invoke(this, status);
            FbException.checkException(status);
            return result;
        }
    }

    public static class IDtc extends IVersioned implements IDtcIntf
    {
        public static class VTable extends IVersioned.VTable
        {
            public static interface Callback_join extends com.sun.jna.Callback
            {
                public ITransaction invoke(IDtc self, IStatus status, ITransaction one, ITransaction two);
            }

            public static interface Callback_startBuilder extends com.sun.jna.Callback
            {
                public IDtcStart invoke(IDtc self, IStatus status);
            }

            public VTable(com.sun.jna.Pointer pointer)
            {
                super(pointer);
            }

            public VTable(final IDtcIntf obj)
            {
                super(obj);

                join = new Callback_join() {
                    @Override
                    public ITransaction invoke(IDtc self, IStatus status, ITransaction one, ITransaction two)
                    {
                        try
                        {
                            return obj.join(status, one, two);
                        }
                        catch (Throwable t)
                        {
                            FbException.catchException(status, t);
                            return null;
                        }
                    }
                };

                startBuilder = new Callback_startBuilder() {
                    @Override
                    public IDtcStart invoke(IDtc self, IStatus status)
                    {
                        try
                        {
                            return obj.startBuilder(status);
                        }
                        catch (Throwable t)
                        {
                            FbException.catchException(status, t);
                            return null;
                        }
                    }
                };
            }

            public VTable()
            {
            }

            public Callback_join join;
            public Callback_startBuilder startBuilder;

            @Override
            protected java.util.List<String> getFieldOrder()
            {
                java.util.List<String> fields = super.getFieldOrder();
                fields.addAll(java.util.Arrays.asList("join", "startBuilder"));
                return fields;
            }
        }

        public IDtc()
        {
        }

        public IDtc(IDtcIntf obj)
        {
            vTable = new VTable(obj);
            vTable.write();
            cloopVTable = vTable.getPointer();
            write();
        }

        @Override
        protected VTable createVTable()
        {
            return new VTable(cloopVTable);
        }

        public ITransaction join(IStatus status, ITransaction one, ITransaction two) throws FbException
        {
            VTable vTable = getVTable();
            ITransaction result = vTable.join.invoke(this, status, one, two);
            FbException.checkException(status);
            return result;
        }

        public IDtcStart startBuilder(IStatus status) throws FbException
        {
            VTable vTable = getVTable();
            IDtcStart result = vTable.startBuilder.invoke(this, status);
            FbException.checkException(status);
            return result;
        }
    }

    public static class IAuth extends IPluginBase implements IAuthIntf
    {
        public static class VTable extends IPluginBase.VTable
        {
            public VTable(com.sun.jna.Pointer pointer)
            {
                super(pointer);
            }

            public VTable(final IAuthIntf obj)
            {
                super(obj);

            }

            public VTable()
            {
            }

            @Override
            protected java.util.List<String> getFieldOrder()
            {
                java.util.List<String> fields = super.getFieldOrder();
                return fields;
            }
        }

        public IAuth()
        {
        }

        public IAuth(IAuthIntf obj)
        {
            vTable = new VTable(obj);
            vTable.write();
            cloopVTable = vTable.getPointer();
            write();
        }

        @Override
        protected VTable createVTable()
        {
            return new VTable(cloopVTable);
        }
    }

    public static class IWriter extends IVersioned implements IWriterIntf
    {
        public static class VTable extends IVersioned.VTable
        {
            public static interface Callback_reset extends com.sun.jna.Callback
            {
                public void invoke(IWriter self);
            }

            public static interface Callback_add extends com.sun.jna.Callback
            {
                public void invoke(IWriter self, IStatus status, String name);
            }

            public static interface Callback_setType extends com.sun.jna.Callback
            {
                public void invoke(IWriter self, IStatus status, String value);
            }

            public static interface Callback_setDb extends com.sun.jna.Callback
            {
                public void invoke(IWriter self, IStatus status, String value);
            }

            public VTable(com.sun.jna.Pointer pointer)
            {
                super(pointer);
            }

            public VTable(final IWriterIntf obj)
            {
                super(obj);

                reset = new Callback_reset() {
                    @Override
                    public void invoke(IWriter self)
                    {
                        obj.reset();
                    }
                };

                add = new Callback_add() {
                    @Override
                    public void invoke(IWriter self, IStatus status, String name)
                    {
                        try
                        {
                            obj.add(status, name);
                        }
                        catch (Throwable t)
                        {
                            FbException.catchException(status, t);
                        }
                    }
                };

                setType = new Callback_setType() {
                    @Override
                    public void invoke(IWriter self, IStatus status, String value)
                    {
                        try
                        {
                            obj.setType(status, value);
                        }
                        catch (Throwable t)
                        {
                            FbException.catchException(status, t);
                        }
                    }
                };

                setDb = new Callback_setDb() {
                    @Override
                    public void invoke(IWriter self, IStatus status, String value)
                    {
                        try
                        {
                            obj.setDb(status, value);
                        }
                        catch (Throwable t)
                        {
                            FbException.catchException(status, t);
                        }
                    }
                };
            }

            public VTable()
            {
            }

            public Callback_reset reset;
            public Callback_add add;
            public Callback_setType setType;
            public Callback_setDb setDb;

            @Override
            protected java.util.List<String> getFieldOrder()
            {
                java.util.List<String> fields = super.getFieldOrder();
                fields.addAll(java.util.Arrays.asList("reset", "add", "setType", "setDb"));
                return fields;
            }
        }

        public IWriter()
        {
        }

        public IWriter(IWriterIntf obj)
        {
            vTable = new VTable(obj);
            vTable.write();
            cloopVTable = vTable.getPointer();
            write();
        }

        @Override
        protected VTable createVTable()
        {
            return new VTable(cloopVTable);
        }

        public void reset()
        {
            VTable vTable = getVTable();
            vTable.reset.invoke(this);
        }

        public void add(IStatus status, String name) throws FbException
        {
            VTable vTable = getVTable();
            vTable.add.invoke(this, status, name);
            FbException.checkException(status);
        }

        public void setType(IStatus status, String value) throws FbException
        {
            VTable vTable = getVTable();
            vTable.setType.invoke(this, status, value);
            FbException.checkException(status);
        }

        public void setDb(IStatus status, String value) throws FbException
        {
            VTable vTable = getVTable();
            vTable.setDb.invoke(this, status, value);
            FbException.checkException(status);
        }
    }

    public static class IServerBlock extends IVersioned implements IServerBlockIntf
    {
        public static class VTable extends IVersioned.VTable
        {
            public static interface Callback_getLogin extends com.sun.jna.Callback
            {
                public String invoke(IServerBlock self);
            }

            public static interface Callback_getData extends com.sun.jna.Callback
            {
                public com.sun.jna.Pointer invoke(IServerBlock self, int[] length);
            }

            public static interface Callback_putData extends com.sun.jna.Callback
            {
                public void invoke(IServerBlock self, IStatus status, int length, com.sun.jna.Pointer data);
            }

            public static interface Callback_newKey extends com.sun.jna.Callback
            {
                public ICryptKey invoke(IServerBlock self, IStatus status);
            }

            public VTable(com.sun.jna.Pointer pointer)
            {
                super(pointer);
            }

            public VTable(final IServerBlockIntf obj)
            {
                super(obj);

                getLogin = new Callback_getLogin() {
                    @Override
                    public String invoke(IServerBlock self)
                    {
                        return obj.getLogin();
                    }
                };

                getData = new Callback_getData() {
                    @Override
                    public com.sun.jna.Pointer invoke(IServerBlock self, int[] length)
                    {
                        return obj.getData(length);
                    }
                };

                putData = new Callback_putData() {
                    @Override
                    public void invoke(IServerBlock self, IStatus status, int length, com.sun.jna.Pointer data)
                    {
                        try
                        {
                            obj.putData(status, length, data);
                        }
                        catch (Throwable t)
                        {
                            FbException.catchException(status, t);
                        }
                    }
                };

                newKey = new Callback_newKey() {
                    @Override
                    public ICryptKey invoke(IServerBlock self, IStatus status)
                    {
                        try
                        {
                            return obj.newKey(status);
                        }
                        catch (Throwable t)
                        {
                            FbException.catchException(status, t);
                            return null;
                        }
                    }
                };
            }

            public VTable()
            {
            }

            public Callback_getLogin getLogin;
            public Callback_getData getData;
            public Callback_putData putData;
            public Callback_newKey newKey;

            @Override
            protected java.util.List<String> getFieldOrder()
            {
                java.util.List<String> fields = super.getFieldOrder();
                fields.addAll(java.util.Arrays.asList("getLogin", "getData", "putData", "newKey"));
                return fields;
            }
        }

        public IServerBlock()
        {
        }

        public IServerBlock(IServerBlockIntf obj)
        {
            vTable = new VTable(obj);
            vTable.write();
            cloopVTable = vTable.getPointer();
            write();
        }

        @Override
        protected VTable createVTable()
        {
            return new VTable(cloopVTable);
        }

        public String getLogin()
        {
            VTable vTable = getVTable();
            String result = vTable.getLogin.invoke(this);
            return result;
        }

        public com.sun.jna.Pointer getData(int[] length)
        {
            VTable vTable = getVTable();
            com.sun.jna.Pointer result = vTable.getData.invoke(this, length);
            return result;
        }

        public void putData(IStatus status, int length, com.sun.jna.Pointer data) throws FbException
        {
            VTable vTable = getVTable();
            vTable.putData.invoke(this, status, length, data);
            FbException.checkException(status);
        }

        public ICryptKey newKey(IStatus status) throws FbException
        {
            VTable vTable = getVTable();
            ICryptKey result = vTable.newKey.invoke(this, status);
            FbException.checkException(status);
            return result;
        }
    }

    public static class IClientBlock extends IReferenceCounted implements IClientBlockIntf
    {
        public static class VTable extends IReferenceCounted.VTable
        {
            public static interface Callback_getLogin extends com.sun.jna.Callback
            {
                public String invoke(IClientBlock self);
            }

            public static interface Callback_getPassword extends com.sun.jna.Callback
            {
                public String invoke(IClientBlock self);
            }

            public static interface Callback_getCertificate extends com.sun.jna.Callback
            {
                public String invoke(IClientBlock self);
            }

            public static interface Callback_getRepositoryPin extends com.sun.jna.Callback
            {
                public String invoke(IClientBlock self);
            }

            public static interface Callback_getVerifyServer extends com.sun.jna.Callback
            {
                public boolean invoke(IClientBlock self);
            }

            public static interface Callback_getData extends com.sun.jna.Callback
            {
                public com.sun.jna.Pointer invoke(IClientBlock self, int[] length);
            }

            public static interface Callback_putData extends com.sun.jna.Callback
            {
                public void invoke(IClientBlock self, IStatus status, int length, com.sun.jna.Pointer data);
            }

            public static interface Callback_newKey extends com.sun.jna.Callback
            {
                public ICryptKey invoke(IClientBlock self, IStatus status);
            }

            public VTable(com.sun.jna.Pointer pointer)
            {
                super(pointer);
            }

            public VTable(final IClientBlockIntf obj)
            {
                super(obj);

                getLogin = new Callback_getLogin() {
                    @Override
                    public String invoke(IClientBlock self)
                    {
                        return obj.getLogin();
                    }
                };

                getPassword = new Callback_getPassword() {
                    @Override
                    public String invoke(IClientBlock self)
                    {
                        return obj.getPassword();
                    }
                };

                getCertificate = new Callback_getCertificate() {
                    @Override
                    public String invoke(IClientBlock self)
                    {
                        return obj.getCertificate();
                    }
                };

                getRepositoryPin = new Callback_getRepositoryPin() {
                    @Override
                    public String invoke(IClientBlock self)
                    {
                        return obj.getRepositoryPin();
                    }
                };

                getVerifyServer = new Callback_getVerifyServer() {
                    @Override
                    public boolean invoke(IClientBlock self)
                    {
                        return obj.getVerifyServer();
                    }
                };

                getData = new Callback_getData() {
                    @Override
                    public com.sun.jna.Pointer invoke(IClientBlock self, int[] length)
                    {
                        return obj.getData(length);
                    }
                };

                putData = new Callback_putData() {
                    @Override
                    public void invoke(IClientBlock self, IStatus status, int length, com.sun.jna.Pointer data)
                    {
                        try
                        {
                            obj.putData(status, length, data);
                        }
                        catch (Throwable t)
                        {
                            FbException.catchException(status, t);
                        }
                    }
                };

                newKey = new Callback_newKey() {
                    @Override
                    public ICryptKey invoke(IClientBlock self, IStatus status)
                    {
                        try
                        {
                            return obj.newKey(status);
                        }
                        catch (Throwable t)
                        {
                            FbException.catchException(status, t);
                            return null;
                        }
                    }
                };
            }

            public VTable()
            {
            }

            public Callback_getLogin getLogin;
            public Callback_getPassword getPassword;
            public Callback_getCertificate getCertificate;
            public Callback_getRepositoryPin getRepositoryPin;
            public Callback_getVerifyServer getVerifyServer;
            public Callback_getData getData;
            public Callback_putData putData;
            public Callback_newKey newKey;

            @Override
            protected java.util.List<String> getFieldOrder()
            {
                java.util.List<String> fields = super.getFieldOrder();
                fields.addAll(java.util.Arrays.asList("getLogin", "getPassword", "getCertificate", "getRepositoryPin", "getVerifyServer", "getData", "putData", "newKey"));
                return fields;
            }
        }

        public IClientBlock()
        {
        }

        public IClientBlock(IClientBlockIntf obj)
        {
            vTable = new VTable(obj);
            vTable.write();
            cloopVTable = vTable.getPointer();
            write();
        }

        @Override
        protected VTable createVTable()
        {
            return new VTable(cloopVTable);
        }

        public String getLogin()
        {
            VTable vTable = getVTable();
            String result = vTable.getLogin.invoke(this);
            return result;
        }

        public String getPassword()
        {
            VTable vTable = getVTable();
            String result = vTable.getPassword.invoke(this);
            return result;
        }

        public String getCertificate()
        {
            VTable vTable = getVTable();
            String result = vTable.getCertificate.invoke(this);
            return result;
        }

        public String getRepositoryPin()
        {
            VTable vTable = getVTable();
            String result = vTable.getRepositoryPin.invoke(this);
            return result;
        }

        public boolean getVerifyServer()
        {
            VTable vTable = getVTable();
            boolean result = vTable.getVerifyServer.invoke(this);
            return result;
        }

        public com.sun.jna.Pointer getData(int[] length)
        {
            VTable vTable = getVTable();
            com.sun.jna.Pointer result = vTable.getData.invoke(this, length);
            return result;
        }

        public void putData(IStatus status, int length, com.sun.jna.Pointer data) throws FbException
        {
            VTable vTable = getVTable();
            vTable.putData.invoke(this, status, length, data);
            FbException.checkException(status);
        }

        public ICryptKey newKey(IStatus status) throws FbException
        {
            VTable vTable = getVTable();
            ICryptKey result = vTable.newKey.invoke(this, status);
            FbException.checkException(status);
            return result;
        }
    }

    public static class IServer extends IAuth implements IServerIntf
    {
        public static class VTable extends IAuth.VTable
        {
            public static interface Callback_authenticate extends com.sun.jna.Callback
            {
                public int invoke(IServer self, IStatus status, IServerBlock sBlock, IWriter writerInterface);
            }

            public static interface Callback_setDbCryptCallback extends com.sun.jna.Callback
            {
                public void invoke(IServer self, IStatus status, ICryptKeyCallback cryptCallback);
            }

            public VTable(com.sun.jna.Pointer pointer)
            {
                super(pointer);
            }

            public VTable(final IServerIntf obj)
            {
                super(obj);

                authenticate = new Callback_authenticate() {
                    @Override
                    public int invoke(IServer self, IStatus status, IServerBlock sBlock, IWriter writerInterface)
                    {
                        try
                        {
                            return obj.authenticate(status, sBlock, writerInterface);
                        }
                        catch (Throwable t)
                        {
                            FbException.catchException(status, t);
                            return 0;
                        }
                    }
                };

                setDbCryptCallback = new Callback_setDbCryptCallback() {
                    @Override
                    public void invoke(IServer self, IStatus status, ICryptKeyCallback cryptCallback)
                    {
                        try
                        {
                            obj.setDbCryptCallback(status, cryptCallback);
                        }
                        catch (Throwable t)
                        {
                            FbException.catchException(status, t);
                        }
                    }
                };
            }

            public VTable()
            {
            }

            public Callback_authenticate authenticate;
            public Callback_setDbCryptCallback setDbCryptCallback;

            @Override
            protected java.util.List<String> getFieldOrder()
            {
                java.util.List<String> fields = super.getFieldOrder();
                fields.addAll(java.util.Arrays.asList("authenticate", "setDbCryptCallback"));
                return fields;
            }
        }

        public IServer()
        {
        }

        public IServer(IServerIntf obj)
        {
            vTable = new VTable(obj);
            vTable.write();
            cloopVTable = vTable.getPointer();
            write();
        }

        @Override
        protected VTable createVTable()
        {
            return new VTable(cloopVTable);
        }

        public int authenticate(IStatus status, IServerBlock sBlock, IWriter writerInterface) throws FbException
        {
            VTable vTable = getVTable();
            int result = vTable.authenticate.invoke(this, status, sBlock, writerInterface);
            FbException.checkException(status);
            return result;
        }

        public void setDbCryptCallback(IStatus status, ICryptKeyCallback cryptCallback) throws FbException
        {
            VTable vTable = getVTable();
            vTable.setDbCryptCallback.invoke(this, status, cryptCallback);
            FbException.checkException(status);
        }
    }

    public static class IClient extends IAuth implements IClientIntf
    {
        public static class VTable extends IAuth.VTable
        {
            public static interface Callback_authenticate extends com.sun.jna.Callback
            {
                public int invoke(IClient self, IStatus status, IClientBlock cBlock);
            }

            public VTable(com.sun.jna.Pointer pointer)
            {
                super(pointer);
            }

            public VTable(final IClientIntf obj)
            {
                super(obj);

                authenticate = new Callback_authenticate() {
                    @Override
                    public int invoke(IClient self, IStatus status, IClientBlock cBlock)
                    {
                        try
                        {
                            return obj.authenticate(status, cBlock);
                        }
                        catch (Throwable t)
                        {
                            FbException.catchException(status, t);
                            return 0;
                        }
                    }
                };
            }

            public VTable()
            {
            }

            public Callback_authenticate authenticate;

            @Override
            protected java.util.List<String> getFieldOrder()
            {
                java.util.List<String> fields = super.getFieldOrder();
                fields.addAll(java.util.Arrays.asList("authenticate"));
                return fields;
            }
        }

        public IClient()
        {
        }

        public IClient(IClientIntf obj)
        {
            vTable = new VTable(obj);
            vTable.write();
            cloopVTable = vTable.getPointer();
            write();
        }

        @Override
        protected VTable createVTable()
        {
            return new VTable(cloopVTable);
        }

        public int authenticate(IStatus status, IClientBlock cBlock) throws FbException
        {
            VTable vTable = getVTable();
            int result = vTable.authenticate.invoke(this, status, cBlock);
            FbException.checkException(status);
            return result;
        }
    }

    public static class IUserField extends IVersioned implements IUserFieldIntf
    {
        public static class VTable extends IVersioned.VTable
        {
            public static interface Callback_entered extends com.sun.jna.Callback
            {
                public int invoke(IUserField self);
            }

            public static interface Callback_specified extends com.sun.jna.Callback
            {
                public int invoke(IUserField self);
            }

            public static interface Callback_setEntered extends com.sun.jna.Callback
            {
                public void invoke(IUserField self, IStatus status, int newValue);
            }

            public VTable(com.sun.jna.Pointer pointer)
            {
                super(pointer);
            }

            public VTable(final IUserFieldIntf obj)
            {
                super(obj);

                entered = new Callback_entered() {
                    @Override
                    public int invoke(IUserField self)
                    {
                        return obj.entered();
                    }
                };

                specified = new Callback_specified() {
                    @Override
                    public int invoke(IUserField self)
                    {
                        return obj.specified();
                    }
                };

                setEntered = new Callback_setEntered() {
                    @Override
                    public void invoke(IUserField self, IStatus status, int newValue)
                    {
                        try
                        {
                            obj.setEntered(status, newValue);
                        }
                        catch (Throwable t)
                        {
                            FbException.catchException(status, t);
                        }
                    }
                };
            }

            public VTable()
            {
            }

            public Callback_entered entered;
            public Callback_specified specified;
            public Callback_setEntered setEntered;

            @Override
            protected java.util.List<String> getFieldOrder()
            {
                java.util.List<String> fields = super.getFieldOrder();
                fields.addAll(java.util.Arrays.asList("entered", "specified", "setEntered"));
                return fields;
            }
        }

        public IUserField()
        {
        }

        public IUserField(IUserFieldIntf obj)
        {
            vTable = new VTable(obj);
            vTable.write();
            cloopVTable = vTable.getPointer();
            write();
        }

        @Override
        protected VTable createVTable()
        {
            return new VTable(cloopVTable);
        }

        public int entered()
        {
            VTable vTable = getVTable();
            int result = vTable.entered.invoke(this);
            return result;
        }

        public int specified()
        {
            VTable vTable = getVTable();
            int result = vTable.specified.invoke(this);
            return result;
        }

        public void setEntered(IStatus status, int newValue) throws FbException
        {
            VTable vTable = getVTable();
            vTable.setEntered.invoke(this, status, newValue);
            FbException.checkException(status);
        }
    }

    public static class ICharUserField extends IUserField implements ICharUserFieldIntf
    {
        public static class VTable extends IUserField.VTable
        {
            public static interface Callback_get extends com.sun.jna.Callback
            {
                public String invoke(ICharUserField self);
            }

            public static interface Callback_set extends com.sun.jna.Callback
            {
                public void invoke(ICharUserField self, IStatus status, String newValue);
            }

            public VTable(com.sun.jna.Pointer pointer)
            {
                super(pointer);
            }

            public VTable(final ICharUserFieldIntf obj)
            {
                super(obj);

                get = new Callback_get() {
                    @Override
                    public String invoke(ICharUserField self)
                    {
                        return obj.get();
                    }
                };

                set = new Callback_set() {
                    @Override
                    public void invoke(ICharUserField self, IStatus status, String newValue)
                    {
                        try
                        {
                            obj.set(status, newValue);
                        }
                        catch (Throwable t)
                        {
                            FbException.catchException(status, t);
                        }
                    }
                };
            }

            public VTable()
            {
            }

            public Callback_get get;
            public Callback_set set;

            @Override
            protected java.util.List<String> getFieldOrder()
            {
                java.util.List<String> fields = super.getFieldOrder();
                fields.addAll(java.util.Arrays.asList("get", "set"));
                return fields;
            }
        }

        public ICharUserField()
        {
        }

        public ICharUserField(ICharUserFieldIntf obj)
        {
            vTable = new VTable(obj);
            vTable.write();
            cloopVTable = vTable.getPointer();
            write();
        }

        @Override
        protected VTable createVTable()
        {
            return new VTable(cloopVTable);
        }

        public String get()
        {
            VTable vTable = getVTable();
            String result = vTable.get.invoke(this);
            return result;
        }

        public void set(IStatus status, String newValue) throws FbException
        {
            VTable vTable = getVTable();
            vTable.set.invoke(this, status, newValue);
            FbException.checkException(status);
        }
    }

    public static class IIntUserField extends IUserField implements IIntUserFieldIntf
    {
        public static class VTable extends IUserField.VTable
        {
            public static interface Callback_get extends com.sun.jna.Callback
            {
                public int invoke(IIntUserField self);
            }

            public static interface Callback_set extends com.sun.jna.Callback
            {
                public void invoke(IIntUserField self, IStatus status, int newValue);
            }

            public VTable(com.sun.jna.Pointer pointer)
            {
                super(pointer);
            }

            public VTable(final IIntUserFieldIntf obj)
            {
                super(obj);

                get = new Callback_get() {
                    @Override
                    public int invoke(IIntUserField self)
                    {
                        return obj.get();
                    }
                };

                set = new Callback_set() {
                    @Override
                    public void invoke(IIntUserField self, IStatus status, int newValue)
                    {
                        try
                        {
                            obj.set(status, newValue);
                        }
                        catch (Throwable t)
                        {
                            FbException.catchException(status, t);
                        }
                    }
                };
            }

            public VTable()
            {
            }

            public Callback_get get;
            public Callback_set set;

            @Override
            protected java.util.List<String> getFieldOrder()
            {
                java.util.List<String> fields = super.getFieldOrder();
                fields.addAll(java.util.Arrays.asList("get", "set"));
                return fields;
            }
        }

        public IIntUserField()
        {
        }

        public IIntUserField(IIntUserFieldIntf obj)
        {
            vTable = new VTable(obj);
            vTable.write();
            cloopVTable = vTable.getPointer();
            write();
        }

        @Override
        protected VTable createVTable()
        {
            return new VTable(cloopVTable);
        }

        public int get()
        {
            VTable vTable = getVTable();
            int result = vTable.get.invoke(this);
            return result;
        }

        public void set(IStatus status, int newValue) throws FbException
        {
            VTable vTable = getVTable();
            vTable.set.invoke(this, status, newValue);
            FbException.checkException(status);
        }
    }

    public static class IUser extends IVersioned implements IUserIntf
    {
        public static class VTable extends IVersioned.VTable
        {
            public static interface Callback_operation extends com.sun.jna.Callback
            {
                public int invoke(IUser self);
            }

            public static interface Callback_userName extends com.sun.jna.Callback
            {
                public ICharUserField invoke(IUser self);
            }

            public static interface Callback_password extends com.sun.jna.Callback
            {
                public ICharUserField invoke(IUser self);
            }

            public static interface Callback_firstName extends com.sun.jna.Callback
            {
                public ICharUserField invoke(IUser self);
            }

            public static interface Callback_lastName extends com.sun.jna.Callback
            {
                public ICharUserField invoke(IUser self);
            }

            public static interface Callback_middleName extends com.sun.jna.Callback
            {
                public ICharUserField invoke(IUser self);
            }

            public static interface Callback_comment extends com.sun.jna.Callback
            {
                public ICharUserField invoke(IUser self);
            }

            public static interface Callback_attributes extends com.sun.jna.Callback
            {
                public ICharUserField invoke(IUser self);
            }

            public static interface Callback_active extends com.sun.jna.Callback
            {
                public IIntUserField invoke(IUser self);
            }

            public static interface Callback_admin extends com.sun.jna.Callback
            {
                public IIntUserField invoke(IUser self);
            }

            public static interface Callback_clear extends com.sun.jna.Callback
            {
                public void invoke(IUser self, IStatus status);
            }

            public VTable(com.sun.jna.Pointer pointer)
            {
                super(pointer);
            }

            public VTable(final IUserIntf obj)
            {
                super(obj);

                operation = new Callback_operation() {
                    @Override
                    public int invoke(IUser self)
                    {
                        return obj.operation();
                    }
                };

                userName = new Callback_userName() {
                    @Override
                    public ICharUserField invoke(IUser self)
                    {
                        return obj.userName();
                    }
                };

                password = new Callback_password() {
                    @Override
                    public ICharUserField invoke(IUser self)
                    {
                        return obj.password();
                    }
                };

                firstName = new Callback_firstName() {
                    @Override
                    public ICharUserField invoke(IUser self)
                    {
                        return obj.firstName();
                    }
                };

                lastName = new Callback_lastName() {
                    @Override
                    public ICharUserField invoke(IUser self)
                    {
                        return obj.lastName();
                    }
                };

                middleName = new Callback_middleName() {
                    @Override
                    public ICharUserField invoke(IUser self)
                    {
                        return obj.middleName();
                    }
                };

                comment = new Callback_comment() {
                    @Override
                    public ICharUserField invoke(IUser self)
                    {
                        return obj.comment();
                    }
                };

                attributes = new Callback_attributes() {
                    @Override
                    public ICharUserField invoke(IUser self)
                    {
                        return obj.attributes();
                    }
                };

                active = new Callback_active() {
                    @Override
                    public IIntUserField invoke(IUser self)
                    {
                        return obj.active();
                    }
                };

                admin = new Callback_admin() {
                    @Override
                    public IIntUserField invoke(IUser self)
                    {
                        return obj.admin();
                    }
                };

                clear = new Callback_clear() {
                    @Override
                    public void invoke(IUser self, IStatus status)
                    {
                        try
                        {
                            obj.clear(status);
                        }
                        catch (Throwable t)
                        {
                            FbException.catchException(status, t);
                        }
                    }
                };
            }

            public VTable()
            {
            }

            public Callback_operation operation;
            public Callback_userName userName;
            public Callback_password password;
            public Callback_firstName firstName;
            public Callback_lastName lastName;
            public Callback_middleName middleName;
            public Callback_comment comment;
            public Callback_attributes attributes;
            public Callback_active active;
            public Callback_admin admin;
            public Callback_clear clear;

            @Override
            protected java.util.List<String> getFieldOrder()
            {
                java.util.List<String> fields = super.getFieldOrder();
                fields.addAll(java.util.Arrays.asList("operation", "userName", "password", "firstName", "lastName", "middleName", "comment", "attributes", "active", "admin", "clear"));
                return fields;
            }
        }

        public IUser()
        {
        }

        public IUser(IUserIntf obj)
        {
            vTable = new VTable(obj);
            vTable.write();
            cloopVTable = vTable.getPointer();
            write();
        }

        @Override
        protected VTable createVTable()
        {
            return new VTable(cloopVTable);
        }

        public int operation()
        {
            VTable vTable = getVTable();
            int result = vTable.operation.invoke(this);
            return result;
        }

        public ICharUserField userName()
        {
            VTable vTable = getVTable();
            ICharUserField result = vTable.userName.invoke(this);
            return result;
        }

        public ICharUserField password()
        {
            VTable vTable = getVTable();
            ICharUserField result = vTable.password.invoke(this);
            return result;
        }

        public ICharUserField firstName()
        {
            VTable vTable = getVTable();
            ICharUserField result = vTable.firstName.invoke(this);
            return result;
        }

        public ICharUserField lastName()
        {
            VTable vTable = getVTable();
            ICharUserField result = vTable.lastName.invoke(this);
            return result;
        }

        public ICharUserField middleName()
        {
            VTable vTable = getVTable();
            ICharUserField result = vTable.middleName.invoke(this);
            return result;
        }

        public ICharUserField comment()
        {
            VTable vTable = getVTable();
            ICharUserField result = vTable.comment.invoke(this);
            return result;
        }

        public ICharUserField attributes()
        {
            VTable vTable = getVTable();
            ICharUserField result = vTable.attributes.invoke(this);
            return result;
        }

        public IIntUserField active()
        {
            VTable vTable = getVTable();
            IIntUserField result = vTable.active.invoke(this);
            return result;
        }

        public IIntUserField admin()
        {
            VTable vTable = getVTable();
            IIntUserField result = vTable.admin.invoke(this);
            return result;
        }

        public void clear(IStatus status) throws FbException
        {
            VTable vTable = getVTable();
            vTable.clear.invoke(this, status);
            FbException.checkException(status);
        }
    }

    public static class IListUsers extends IVersioned implements IListUsersIntf
    {
        public static class VTable extends IVersioned.VTable
        {
            public static interface Callback_list extends com.sun.jna.Callback
            {
                public void invoke(IListUsers self, IStatus status, IUser user);
            }

            public VTable(com.sun.jna.Pointer pointer)
            {
                super(pointer);
            }

            public VTable(final IListUsersIntf obj)
            {
                super(obj);

                list = new Callback_list() {
                    @Override
                    public void invoke(IListUsers self, IStatus status, IUser user)
                    {
                        try
                        {
                            obj.list(status, user);
                        }
                        catch (Throwable t)
                        {
                            FbException.catchException(status, t);
                        }
                    }
                };
            }

            public VTable()
            {
            }

            public Callback_list list;

            @Override
            protected java.util.List<String> getFieldOrder()
            {
                java.util.List<String> fields = super.getFieldOrder();
                fields.addAll(java.util.Arrays.asList("list"));
                return fields;
            }
        }

        public IListUsers()
        {
        }

        public IListUsers(IListUsersIntf obj)
        {
            vTable = new VTable(obj);
            vTable.write();
            cloopVTable = vTable.getPointer();
            write();
        }

        @Override
        protected VTable createVTable()
        {
            return new VTable(cloopVTable);
        }

        public void list(IStatus status, IUser user) throws FbException
        {
            VTable vTable = getVTable();
            vTable.list.invoke(this, status, user);
            FbException.checkException(status);
        }
    }

    public static class ILogonInfo extends IVersioned implements ILogonInfoIntf
    {
        public static class VTable extends IVersioned.VTable
        {
            public static interface Callback_name extends com.sun.jna.Callback
            {
                public String invoke(ILogonInfo self);
            }

            public static interface Callback_role extends com.sun.jna.Callback
            {
                public String invoke(ILogonInfo self);
            }

            public static interface Callback_networkProtocol extends com.sun.jna.Callback
            {
                public String invoke(ILogonInfo self);
            }

            public static interface Callback_remoteAddress extends com.sun.jna.Callback
            {
                public String invoke(ILogonInfo self);
            }

            public static interface Callback_authBlock extends com.sun.jna.Callback
            {
                public com.sun.jna.Pointer invoke(ILogonInfo self, int[] length);
            }

            public VTable(com.sun.jna.Pointer pointer)
            {
                super(pointer);
            }

            public VTable(final ILogonInfoIntf obj)
            {
                super(obj);

                name = new Callback_name() {
                    @Override
                    public String invoke(ILogonInfo self)
                    {
                        return obj.name();
                    }
                };

                role = new Callback_role() {
                    @Override
                    public String invoke(ILogonInfo self)
                    {
                        return obj.role();
                    }
                };

                networkProtocol = new Callback_networkProtocol() {
                    @Override
                    public String invoke(ILogonInfo self)
                    {
                        return obj.networkProtocol();
                    }
                };

                remoteAddress = new Callback_remoteAddress() {
                    @Override
                    public String invoke(ILogonInfo self)
                    {
                        return obj.remoteAddress();
                    }
                };

                authBlock = new Callback_authBlock() {
                    @Override
                    public com.sun.jna.Pointer invoke(ILogonInfo self, int[] length)
                    {
                        return obj.authBlock(length);
                    }
                };
            }

            public VTable()
            {
            }

            public Callback_name name;
            public Callback_role role;
            public Callback_networkProtocol networkProtocol;
            public Callback_remoteAddress remoteAddress;
            public Callback_authBlock authBlock;

            @Override
            protected java.util.List<String> getFieldOrder()
            {
                java.util.List<String> fields = super.getFieldOrder();
                fields.addAll(java.util.Arrays.asList("name", "role", "networkProtocol", "remoteAddress", "authBlock"));
                return fields;
            }
        }

        public ILogonInfo()
        {
        }

        public ILogonInfo(ILogonInfoIntf obj)
        {
            vTable = new VTable(obj);
            vTable.write();
            cloopVTable = vTable.getPointer();
            write();
        }

        @Override
        protected VTable createVTable()
        {
            return new VTable(cloopVTable);
        }

        public String name()
        {
            VTable vTable = getVTable();
            String result = vTable.name.invoke(this);
            return result;
        }

        public String role()
        {
            VTable vTable = getVTable();
            String result = vTable.role.invoke(this);
            return result;
        }

        public String networkProtocol()
        {
            VTable vTable = getVTable();
            String result = vTable.networkProtocol.invoke(this);
            return result;
        }

        public String remoteAddress()
        {
            VTable vTable = getVTable();
            String result = vTable.remoteAddress.invoke(this);
            return result;
        }

        public com.sun.jna.Pointer authBlock(int[] length)
        {
            VTable vTable = getVTable();
            com.sun.jna.Pointer result = vTable.authBlock.invoke(this, length);
            return result;
        }
    }

    public static class IManagement extends IPluginBase implements IManagementIntf
    {
        public static class VTable extends IPluginBase.VTable
        {
            public static interface Callback_start extends com.sun.jna.Callback
            {
                public void invoke(IManagement self, IStatus status, ILogonInfo logonInfo);
            }

            public static interface Callback_execute extends com.sun.jna.Callback
            {
                public int invoke(IManagement self, IStatus status, IUser user, IListUsers callback);
            }

            public static interface Callback_commit extends com.sun.jna.Callback
            {
                public void invoke(IManagement self, IStatus status);
            }

            public static interface Callback_rollback extends com.sun.jna.Callback
            {
                public void invoke(IManagement self, IStatus status);
            }

            public VTable(com.sun.jna.Pointer pointer)
            {
                super(pointer);
            }

            public VTable(final IManagementIntf obj)
            {
                super(obj);

                start = new Callback_start() {
                    @Override
                    public void invoke(IManagement self, IStatus status, ILogonInfo logonInfo)
                    {
                        try
                        {
                            obj.start(status, logonInfo);
                        }
                        catch (Throwable t)
                        {
                            FbException.catchException(status, t);
                        }
                    }
                };

                execute = new Callback_execute() {
                    @Override
                    public int invoke(IManagement self, IStatus status, IUser user, IListUsers callback)
                    {
                        try
                        {
                            return obj.execute(status, user, callback);
                        }
                        catch (Throwable t)
                        {
                            FbException.catchException(status, t);
                            return 0;
                        }
                    }
                };

                commit = new Callback_commit() {
                    @Override
                    public void invoke(IManagement self, IStatus status)
                    {
                        try
                        {
                            obj.commit(status);
                        }
                        catch (Throwable t)
                        {
                            FbException.catchException(status, t);
                        }
                    }
                };

                rollback = new Callback_rollback() {
                    @Override
                    public void invoke(IManagement self, IStatus status)
                    {
                        try
                        {
                            obj.rollback(status);
                        }
                        catch (Throwable t)
                        {
                            FbException.catchException(status, t);
                        }
                    }
                };
            }

            public VTable()
            {
            }

            public Callback_start start;
            public Callback_execute execute;
            public Callback_commit commit;
            public Callback_rollback rollback;

            @Override
            protected java.util.List<String> getFieldOrder()
            {
                java.util.List<String> fields = super.getFieldOrder();
                fields.addAll(java.util.Arrays.asList("start", "execute", "commit", "rollback"));
                return fields;
            }
        }

        public IManagement()
        {
        }

        public IManagement(IManagementIntf obj)
        {
            vTable = new VTable(obj);
            vTable.write();
            cloopVTable = vTable.getPointer();
            write();
        }

        @Override
        protected VTable createVTable()
        {
            return new VTable(cloopVTable);
        }

        public void start(IStatus status, ILogonInfo logonInfo) throws FbException
        {
            VTable vTable = getVTable();
            vTable.start.invoke(this, status, logonInfo);
            FbException.checkException(status);
        }

        public int execute(IStatus status, IUser user, IListUsers callback) throws FbException
        {
            VTable vTable = getVTable();
            int result = vTable.execute.invoke(this, status, user, callback);
            FbException.checkException(status);
            return result;
        }

        public void commit(IStatus status) throws FbException
        {
            VTable vTable = getVTable();
            vTable.commit.invoke(this, status);
            FbException.checkException(status);
        }

        public void rollback(IStatus status) throws FbException
        {
            VTable vTable = getVTable();
            vTable.rollback.invoke(this, status);
            FbException.checkException(status);
        }
    }

    public static class IWireCryptPlugin extends IPluginBase implements IWireCryptPluginIntf
    {
        public static class VTable extends IPluginBase.VTable
        {
            public static interface Callback_getKnownTypes extends com.sun.jna.Callback
            {
                public String invoke(IWireCryptPlugin self, IStatus status);
            }

            public static interface Callback_setKey extends com.sun.jna.Callback
            {
                public void invoke(IWireCryptPlugin self, IStatus status, ICryptKey key);
            }

            public static interface Callback_encrypt extends com.sun.jna.Callback
            {
                public void invoke(IWireCryptPlugin self, IStatus status, int length, com.sun.jna.Pointer from, com.sun.jna.Pointer to);
            }

            public static interface Callback_decrypt extends com.sun.jna.Callback
            {
                public void invoke(IWireCryptPlugin self, IStatus status, int length, com.sun.jna.Pointer from, com.sun.jna.Pointer to);
            }

            public VTable(com.sun.jna.Pointer pointer)
            {
                super(pointer);
            }

            public VTable(final IWireCryptPluginIntf obj)
            {
                super(obj);

                getKnownTypes = new Callback_getKnownTypes() {
                    @Override
                    public String invoke(IWireCryptPlugin self, IStatus status)
                    {
                        try
                        {
                            return obj.getKnownTypes(status);
                        }
                        catch (Throwable t)
                        {
                            FbException.catchException(status, t);
                            return null;
                        }
                    }
                };

                setKey = new Callback_setKey() {
                    @Override
                    public void invoke(IWireCryptPlugin self, IStatus status, ICryptKey key)
                    {
                        try
                        {
                            obj.setKey(status, key);
                        }
                        catch (Throwable t)
                        {
                            FbException.catchException(status, t);
                        }
                    }
                };

                encrypt = new Callback_encrypt() {
                    @Override
                    public void invoke(IWireCryptPlugin self, IStatus status, int length, com.sun.jna.Pointer from, com.sun.jna.Pointer to)
                    {
                        try
                        {
                            obj.encrypt(status, length, from, to);
                        }
                        catch (Throwable t)
                        {
                            FbException.catchException(status, t);
                        }
                    }
                };

                decrypt = new Callback_decrypt() {
                    @Override
                    public void invoke(IWireCryptPlugin self, IStatus status, int length, com.sun.jna.Pointer from, com.sun.jna.Pointer to)
                    {
                        try
                        {
                            obj.decrypt(status, length, from, to);
                        }
                        catch (Throwable t)
                        {
                            FbException.catchException(status, t);
                        }
                    }
                };
            }

            public VTable()
            {
            }

            public Callback_getKnownTypes getKnownTypes;
            public Callback_setKey setKey;
            public Callback_encrypt encrypt;
            public Callback_decrypt decrypt;

            @Override
            protected java.util.List<String> getFieldOrder()
            {
                java.util.List<String> fields = super.getFieldOrder();
                fields.addAll(java.util.Arrays.asList("getKnownTypes", "setKey", "encrypt", "decrypt"));
                return fields;
            }
        }

        public IWireCryptPlugin()
        {
        }

        public IWireCryptPlugin(IWireCryptPluginIntf obj)
        {
            vTable = new VTable(obj);
            vTable.write();
            cloopVTable = vTable.getPointer();
            write();
        }

        @Override
        protected VTable createVTable()
        {
            return new VTable(cloopVTable);
        }

        public String getKnownTypes(IStatus status) throws FbException
        {
            VTable vTable = getVTable();
            String result = vTable.getKnownTypes.invoke(this, status);
            FbException.checkException(status);
            return result;
        }

        public void setKey(IStatus status, ICryptKey key) throws FbException
        {
            VTable vTable = getVTable();
            vTable.setKey.invoke(this, status, key);
            FbException.checkException(status);
        }

        public void encrypt(IStatus status, int length, com.sun.jna.Pointer from, com.sun.jna.Pointer to) throws FbException
        {
            VTable vTable = getVTable();
            vTable.encrypt.invoke(this, status, length, from, to);
            FbException.checkException(status);
        }

        public void decrypt(IStatus status, int length, com.sun.jna.Pointer from, com.sun.jna.Pointer to) throws FbException
        {
            VTable vTable = getVTable();
            vTable.decrypt.invoke(this, status, length, from, to);
            FbException.checkException(status);
        }
    }

    public static class ICryptKeyCallback extends IVersioned implements ICryptKeyCallbackIntf
    {
        public static class VTable extends IVersioned.VTable
        {
            public static interface Callback_callback extends com.sun.jna.Callback
            {
                public int invoke(ICryptKeyCallback self, int dataLength, com.sun.jna.Pointer data, int bufferLength, com.sun.jna.Pointer buffer);
            }

            public VTable(com.sun.jna.Pointer pointer)
            {
                super(pointer);
            }

            public VTable(final ICryptKeyCallbackIntf obj)
            {
                super(obj);

                callback = new Callback_callback() {
                    @Override
                    public int invoke(ICryptKeyCallback self, int dataLength, com.sun.jna.Pointer data, int bufferLength, com.sun.jna.Pointer buffer)
                    {
                        return obj.callback(dataLength, data, bufferLength, buffer);
                    }
                };
            }

            public VTable()
            {
            }

            public Callback_callback callback;

            @Override
            protected java.util.List<String> getFieldOrder()
            {
                java.util.List<String> fields = super.getFieldOrder();
                fields.addAll(java.util.Arrays.asList("callback"));
                return fields;
            }
        }

        public ICryptKeyCallback()
        {
        }

        public ICryptKeyCallback(ICryptKeyCallbackIntf obj)
        {
            vTable = new VTable(obj);
            vTable.write();
            cloopVTable = vTable.getPointer();
            write();
        }

        @Override
        protected VTable createVTable()
        {
            return new VTable(cloopVTable);
        }

        public int callback(int dataLength, com.sun.jna.Pointer data, int bufferLength, com.sun.jna.Pointer buffer)
        {
            VTable vTable = getVTable();
            int result = vTable.callback.invoke(this, dataLength, data, bufferLength, buffer);
            return result;
        }
    }

    public static class IKeyHolderPlugin extends IPluginBase implements IKeyHolderPluginIntf
    {
        public static class VTable extends IPluginBase.VTable
        {
            public static interface Callback_keyCallback extends com.sun.jna.Callback
            {
                public int invoke(IKeyHolderPlugin self, IStatus status, ICryptKeyCallback callback);
            }

            public static interface Callback_keyHandle extends com.sun.jna.Callback
            {
                public ICryptKeyCallback invoke(IKeyHolderPlugin self, IStatus status, String keyName);
            }

            public static interface Callback_useOnlyOwnKeys extends com.sun.jna.Callback
            {
                public boolean invoke(IKeyHolderPlugin self, IStatus status);
            }

            public static interface Callback_chainHandle extends com.sun.jna.Callback
            {
                public ICryptKeyCallback invoke(IKeyHolderPlugin self, IStatus status);
            }

            public VTable(com.sun.jna.Pointer pointer)
            {
                super(pointer);
            }

            public VTable(final IKeyHolderPluginIntf obj)
            {
                super(obj);

                keyCallback = new Callback_keyCallback() {
                    @Override
                    public int invoke(IKeyHolderPlugin self, IStatus status, ICryptKeyCallback callback)
                    {
                        try
                        {
                            return obj.keyCallback(status, callback);
                        }
                        catch (Throwable t)
                        {
                            FbException.catchException(status, t);
                            return 0;
                        }
                    }
                };

                keyHandle = new Callback_keyHandle() {
                    @Override
                    public ICryptKeyCallback invoke(IKeyHolderPlugin self, IStatus status, String keyName)
                    {
                        try
                        {
                            return obj.keyHandle(status, keyName);
                        }
                        catch (Throwable t)
                        {
                            FbException.catchException(status, t);
                            return null;
                        }
                    }
                };

                useOnlyOwnKeys = new Callback_useOnlyOwnKeys() {
                    @Override
                    public boolean invoke(IKeyHolderPlugin self, IStatus status)
                    {
                        try
                        {
                            return obj.useOnlyOwnKeys(status);
                        }
                        catch (Throwable t)
                        {
                            FbException.catchException(status, t);
                            return false;
                        }
                    }
                };

                chainHandle = new Callback_chainHandle() {
                    @Override
                    public ICryptKeyCallback invoke(IKeyHolderPlugin self, IStatus status)
                    {
                        try
                        {
                            return obj.chainHandle(status);
                        }
                        catch (Throwable t)
                        {
                            FbException.catchException(status, t);
                            return null;
                        }
                    }
                };
            }

            public VTable()
            {
            }

            public Callback_keyCallback keyCallback;
            public Callback_keyHandle keyHandle;
            public Callback_useOnlyOwnKeys useOnlyOwnKeys;
            public Callback_chainHandle chainHandle;

            @Override
            protected java.util.List<String> getFieldOrder()
            {
                java.util.List<String> fields = super.getFieldOrder();
                fields.addAll(java.util.Arrays.asList("keyCallback", "keyHandle", "useOnlyOwnKeys", "chainHandle"));
                return fields;
            }
        }

        public IKeyHolderPlugin()
        {
        }

        public IKeyHolderPlugin(IKeyHolderPluginIntf obj)
        {
            vTable = new VTable(obj);
            vTable.write();
            cloopVTable = vTable.getPointer();
            write();
        }

        @Override
        protected VTable createVTable()
        {
            return new VTable(cloopVTable);
        }

        public int keyCallback(IStatus status, ICryptKeyCallback callback) throws FbException
        {
            VTable vTable = getVTable();
            int result = vTable.keyCallback.invoke(this, status, callback);
            FbException.checkException(status);
            return result;
        }

        public ICryptKeyCallback keyHandle(IStatus status, String keyName) throws FbException
        {
            VTable vTable = getVTable();
            ICryptKeyCallback result = vTable.keyHandle.invoke(this, status, keyName);
            FbException.checkException(status);
            return result;
        }

        public boolean useOnlyOwnKeys(IStatus status) throws FbException
        {
            VTable vTable = getVTable();
            boolean result = vTable.useOnlyOwnKeys.invoke(this, status);
            FbException.checkException(status);
            return result;
        }

        public ICryptKeyCallback chainHandle(IStatus status) throws FbException
        {
            VTable vTable = getVTable();
            ICryptKeyCallback result = vTable.chainHandle.invoke(this, status);
            FbException.checkException(status);
            return result;
        }
    }

    public static class IDbCryptInfo extends IReferenceCounted implements IDbCryptInfoIntf
    {
        public static class VTable extends IReferenceCounted.VTable
        {
            public static interface Callback_getDatabaseFullPath extends com.sun.jna.Callback
            {
                public String invoke(IDbCryptInfo self, IStatus status);
            }

            public VTable(com.sun.jna.Pointer pointer)
            {
                super(pointer);
            }

            public VTable(final IDbCryptInfoIntf obj)
            {
                super(obj);

                getDatabaseFullPath = new Callback_getDatabaseFullPath() {
                    @Override
                    public String invoke(IDbCryptInfo self, IStatus status)
                    {
                        try
                        {
                            return obj.getDatabaseFullPath(status);
                        }
                        catch (Throwable t)
                        {
                            FbException.catchException(status, t);
                            return null;
                        }
                    }
                };
            }

            public VTable()
            {
            }

            public Callback_getDatabaseFullPath getDatabaseFullPath;

            @Override
            protected java.util.List<String> getFieldOrder()
            {
                java.util.List<String> fields = super.getFieldOrder();
                fields.addAll(java.util.Arrays.asList("getDatabaseFullPath"));
                return fields;
            }
        }

        public IDbCryptInfo()
        {
        }

        public IDbCryptInfo(IDbCryptInfoIntf obj)
        {
            vTable = new VTable(obj);
            vTable.write();
            cloopVTable = vTable.getPointer();
            write();
        }

        @Override
        protected VTable createVTable()
        {
            return new VTable(cloopVTable);
        }

        public String getDatabaseFullPath(IStatus status) throws FbException
        {
            VTable vTable = getVTable();
            String result = vTable.getDatabaseFullPath.invoke(this, status);
            FbException.checkException(status);
            return result;
        }
    }

    public static class IDbCryptPlugin extends IPluginBase implements IDbCryptPluginIntf
    {
        public static class VTable extends IPluginBase.VTable
        {
            public static interface Callback_setKey extends com.sun.jna.Callback
            {
                public void invoke(IDbCryptPlugin self, IStatus status, int length, IKeyHolderPlugin[] sources, String keyName);
            }

            public static interface Callback_encrypt extends com.sun.jna.Callback
            {
                public void invoke(IDbCryptPlugin self, IStatus status, int length, com.sun.jna.Pointer from, com.sun.jna.Pointer to);
            }

            public static interface Callback_decrypt extends com.sun.jna.Callback
            {
                public void invoke(IDbCryptPlugin self, IStatus status, int length, com.sun.jna.Pointer from, com.sun.jna.Pointer to);
            }

            public static interface Callback_setInfo extends com.sun.jna.Callback
            {
                public void invoke(IDbCryptPlugin self, IStatus status, IDbCryptInfo info);
            }

            public VTable(com.sun.jna.Pointer pointer)
            {
                super(pointer);
            }

            public VTable(final IDbCryptPluginIntf obj)
            {
                super(obj);

                setKey = new Callback_setKey() {
                    @Override
                    public void invoke(IDbCryptPlugin self, IStatus status, int length, IKeyHolderPlugin[] sources, String keyName)
                    {
                        try
                        {
                            obj.setKey(status, length, sources, keyName);
                        }
                        catch (Throwable t)
                        {
                            FbException.catchException(status, t);
                        }
                    }
                };

                encrypt = new Callback_encrypt() {
                    @Override
                    public void invoke(IDbCryptPlugin self, IStatus status, int length, com.sun.jna.Pointer from, com.sun.jna.Pointer to)
                    {
                        try
                        {
                            obj.encrypt(status, length, from, to);
                        }
                        catch (Throwable t)
                        {
                            FbException.catchException(status, t);
                        }
                    }
                };

                decrypt = new Callback_decrypt() {
                    @Override
                    public void invoke(IDbCryptPlugin self, IStatus status, int length, com.sun.jna.Pointer from, com.sun.jna.Pointer to)
                    {
                        try
                        {
                            obj.decrypt(status, length, from, to);
                        }
                        catch (Throwable t)
                        {
                            FbException.catchException(status, t);
                        }
                    }
                };

                setInfo = new Callback_setInfo() {
                    @Override
                    public void invoke(IDbCryptPlugin self, IStatus status, IDbCryptInfo info)
                    {
                        try
                        {
                            obj.setInfo(status, info);
                        }
                        catch (Throwable t)
                        {
                            FbException.catchException(status, t);
                        }
                    }
                };
            }

            public VTable()
            {
            }

            public Callback_setKey setKey;
            public Callback_encrypt encrypt;
            public Callback_decrypt decrypt;
            public Callback_setInfo setInfo;

            @Override
            protected java.util.List<String> getFieldOrder()
            {
                java.util.List<String> fields = super.getFieldOrder();
                fields.addAll(java.util.Arrays.asList("setKey", "encrypt", "decrypt", "setInfo"));
                return fields;
            }
        }

        public IDbCryptPlugin()
        {
        }

        public IDbCryptPlugin(IDbCryptPluginIntf obj)
        {
            vTable = new VTable(obj);
            vTable.write();
            cloopVTable = vTable.getPointer();
            write();
        }

        @Override
        protected VTable createVTable()
        {
            return new VTable(cloopVTable);
        }

        public void setKey(IStatus status, int length, IKeyHolderPlugin[] sources, String keyName) throws FbException
        {
            VTable vTable = getVTable();
            vTable.setKey.invoke(this, status, length, sources, keyName);
            FbException.checkException(status);
        }

        public void encrypt(IStatus status, int length, com.sun.jna.Pointer from, com.sun.jna.Pointer to) throws FbException
        {
            VTable vTable = getVTable();
            vTable.encrypt.invoke(this, status, length, from, to);
            FbException.checkException(status);
        }

        public void decrypt(IStatus status, int length, com.sun.jna.Pointer from, com.sun.jna.Pointer to) throws FbException
        {
            VTable vTable = getVTable();
            vTable.decrypt.invoke(this, status, length, from, to);
            FbException.checkException(status);
        }

        public void setInfo(IStatus status, IDbCryptInfo info) throws FbException
        {
            VTable vTable = getVTable();
            vTable.setInfo.invoke(this, status, info);
            FbException.checkException(status);
        }
    }

    public static class IExternalContext extends IVersioned implements IExternalContextIntf
    {
        public static class VTable extends IVersioned.VTable
        {
            public static interface Callback_getMaster extends com.sun.jna.Callback
            {
                public IMaster invoke(IExternalContext self);
            }

            public static interface Callback_getEngine extends com.sun.jna.Callback
            {
                public IExternalEngine invoke(IExternalContext self, IStatus status);
            }

            public static interface Callback_getAttachment extends com.sun.jna.Callback
            {
                public IAttachment invoke(IExternalContext self, IStatus status);
            }

            public static interface Callback_getTransaction extends com.sun.jna.Callback
            {
                public ITransaction invoke(IExternalContext self, IStatus status);
            }

            public static interface Callback_getUserName extends com.sun.jna.Callback
            {
                public String invoke(IExternalContext self);
            }

            public static interface Callback_getDatabaseName extends com.sun.jna.Callback
            {
                public String invoke(IExternalContext self);
            }

            public static interface Callback_getClientCharSet extends com.sun.jna.Callback
            {
                public String invoke(IExternalContext self);
            }

            public static interface Callback_obtainInfoCode extends com.sun.jna.Callback
            {
                public int invoke(IExternalContext self);
            }

            public static interface Callback_getInfo extends com.sun.jna.Callback
            {
                public com.sun.jna.Pointer invoke(IExternalContext self, int code);
            }

            public static interface Callback_setInfo extends com.sun.jna.Callback
            {
                public com.sun.jna.Pointer invoke(IExternalContext self, int code, com.sun.jna.Pointer value);
            }

            public VTable(com.sun.jna.Pointer pointer)
            {
                super(pointer);
            }

            public VTable(final IExternalContextIntf obj)
            {
                super(obj);

                getMaster = new Callback_getMaster() {
                    @Override
                    public IMaster invoke(IExternalContext self)
                    {
                        return obj.getMaster();
                    }
                };

                getEngine = new Callback_getEngine() {
                    @Override
                    public IExternalEngine invoke(IExternalContext self, IStatus status)
                    {
                        try
                        {
                            return obj.getEngine(status);
                        }
                        catch (Throwable t)
                        {
                            FbException.catchException(status, t);
                            return null;
                        }
                    }
                };

                getAttachment = new Callback_getAttachment() {
                    @Override
                    public IAttachment invoke(IExternalContext self, IStatus status)
                    {
                        try
                        {
                            return obj.getAttachment(status);
                        }
                        catch (Throwable t)
                        {
                            FbException.catchException(status, t);
                            return null;
                        }
                    }
                };

                getTransaction = new Callback_getTransaction() {
                    @Override
                    public ITransaction invoke(IExternalContext self, IStatus status)
                    {
                        try
                        {
                            return obj.getTransaction(status);
                        }
                        catch (Throwable t)
                        {
                            FbException.catchException(status, t);
                            return null;
                        }
                    }
                };

                getUserName = new Callback_getUserName() {
                    @Override
                    public String invoke(IExternalContext self)
                    {
                        return obj.getUserName();
                    }
                };

                getDatabaseName = new Callback_getDatabaseName() {
                    @Override
                    public String invoke(IExternalContext self)
                    {
                        return obj.getDatabaseName();
                    }
                };

                getClientCharSet = new Callback_getClientCharSet() {
                    @Override
                    public String invoke(IExternalContext self)
                    {
                        return obj.getClientCharSet();
                    }
                };

                obtainInfoCode = new Callback_obtainInfoCode() {
                    @Override
                    public int invoke(IExternalContext self)
                    {
                        return obj.obtainInfoCode();
                    }
                };

                getInfo = new Callback_getInfo() {
                    @Override
                    public com.sun.jna.Pointer invoke(IExternalContext self, int code)
                    {
                        return obj.getInfo(code);
                    }
                };

                setInfo = new Callback_setInfo() {
                    @Override
                    public com.sun.jna.Pointer invoke(IExternalContext self, int code, com.sun.jna.Pointer value)
                    {
                        return obj.setInfo(code, value);
                    }
                };
            }

            public VTable()
            {
            }

            public Callback_getMaster getMaster;
            public Callback_getEngine getEngine;
            public Callback_getAttachment getAttachment;
            public Callback_getTransaction getTransaction;
            public Callback_getUserName getUserName;
            public Callback_getDatabaseName getDatabaseName;
            public Callback_getClientCharSet getClientCharSet;
            public Callback_obtainInfoCode obtainInfoCode;
            public Callback_getInfo getInfo;
            public Callback_setInfo setInfo;

            @Override
            protected java.util.List<String> getFieldOrder()
            {
                java.util.List<String> fields = super.getFieldOrder();
                fields.addAll(java.util.Arrays.asList("getMaster", "getEngine", "getAttachment", "getTransaction", "getUserName", "getDatabaseName", "getClientCharSet", "obtainInfoCode", "getInfo", "setInfo"));
                return fields;
            }
        }

        public IExternalContext()
        {
        }

        public IExternalContext(IExternalContextIntf obj)
        {
            vTable = new VTable(obj);
            vTable.write();
            cloopVTable = vTable.getPointer();
            write();
        }

        @Override
        protected VTable createVTable()
        {
            return new VTable(cloopVTable);
        }

        public IMaster getMaster()
        {
            VTable vTable = getVTable();
            IMaster result = vTable.getMaster.invoke(this);
            return result;
        }

        public IExternalEngine getEngine(IStatus status) throws FbException
        {
            VTable vTable = getVTable();
            IExternalEngine result = vTable.getEngine.invoke(this, status);
            FbException.checkException(status);
            return result;
        }

        public IAttachment getAttachment(IStatus status) throws FbException
        {
            VTable vTable = getVTable();
            IAttachment result = vTable.getAttachment.invoke(this, status);
            FbException.checkException(status);
            return result;
        }

        public ITransaction getTransaction(IStatus status) throws FbException
        {
            VTable vTable = getVTable();
            ITransaction result = vTable.getTransaction.invoke(this, status);
            FbException.checkException(status);
            return result;
        }

        public String getUserName()
        {
            VTable vTable = getVTable();
            String result = vTable.getUserName.invoke(this);
            return result;
        }

        public String getDatabaseName()
        {
            VTable vTable = getVTable();
            String result = vTable.getDatabaseName.invoke(this);
            return result;
        }

        public String getClientCharSet()
        {
            VTable vTable = getVTable();
            String result = vTable.getClientCharSet.invoke(this);
            return result;
        }

        public int obtainInfoCode()
        {
            VTable vTable = getVTable();
            int result = vTable.obtainInfoCode.invoke(this);
            return result;
        }

        public com.sun.jna.Pointer getInfo(int code)
        {
            VTable vTable = getVTable();
            com.sun.jna.Pointer result = vTable.getInfo.invoke(this, code);
            return result;
        }

        public com.sun.jna.Pointer setInfo(int code, com.sun.jna.Pointer value)
        {
            VTable vTable = getVTable();
            com.sun.jna.Pointer result = vTable.setInfo.invoke(this, code, value);
            return result;
        }
    }

    public static class IExternalResultSet extends IDisposable implements IExternalResultSetIntf
    {
        public static class VTable extends IDisposable.VTable
        {
            public static interface Callback_fetch extends com.sun.jna.Callback
            {
                public boolean invoke(IExternalResultSet self, IStatus status);
            }

            public VTable(com.sun.jna.Pointer pointer)
            {
                super(pointer);
            }

            public VTable(final IExternalResultSetIntf obj)
            {
                super(obj);

                fetch = new Callback_fetch() {
                    @Override
                    public boolean invoke(IExternalResultSet self, IStatus status)
                    {
                        try
                        {
                            return obj.fetch(status);
                        }
                        catch (Throwable t)
                        {
                            FbException.catchException(status, t);
                            return false;
                        }
                    }
                };
            }

            public VTable()
            {
            }

            public Callback_fetch fetch;

            @Override
            protected java.util.List<String> getFieldOrder()
            {
                java.util.List<String> fields = super.getFieldOrder();
                fields.addAll(java.util.Arrays.asList("fetch"));
                return fields;
            }
        }

        public IExternalResultSet()
        {
        }

        public IExternalResultSet(IExternalResultSetIntf obj)
        {
            vTable = new VTable(obj);
            vTable.write();
            cloopVTable = vTable.getPointer();
            write();
        }

        @Override
        protected VTable createVTable()
        {
            return new VTable(cloopVTable);
        }

        public boolean fetch(IStatus status) throws FbException
        {
            VTable vTable = getVTable();
            boolean result = vTable.fetch.invoke(this, status);
            FbException.checkException(status);
            return result;
        }
    }

    public static class IExternalFunction extends IDisposable implements IExternalFunctionIntf
    {
        public static class VTable extends IDisposable.VTable
        {
            public static interface Callback_getCharSet extends com.sun.jna.Callback
            {
                public void invoke(IExternalFunction self, IStatus status, IExternalContext context, com.sun.jna.Pointer name, int nameSize);
            }

            public static interface Callback_execute extends com.sun.jna.Callback
            {
                public void invoke(IExternalFunction self, IStatus status, IExternalContext context, com.sun.jna.Pointer inMsg, com.sun.jna.Pointer outMsg);
            }

            public VTable(com.sun.jna.Pointer pointer)
            {
                super(pointer);
            }

            public VTable(final IExternalFunctionIntf obj)
            {
                super(obj);

                getCharSet = new Callback_getCharSet() {
                    @Override
                    public void invoke(IExternalFunction self, IStatus status, IExternalContext context, com.sun.jna.Pointer name, int nameSize)
                    {
                        try
                        {
                            obj.getCharSet(status, context, name, nameSize);
                        }
                        catch (Throwable t)
                        {
                            FbException.catchException(status, t);
                        }
                    }
                };

                execute = new Callback_execute() {
                    @Override
                    public void invoke(IExternalFunction self, IStatus status, IExternalContext context, com.sun.jna.Pointer inMsg, com.sun.jna.Pointer outMsg)
                    {
                        try
                        {
                            obj.execute(status, context, inMsg, outMsg);
                        }
                        catch (Throwable t)
                        {
                            FbException.catchException(status, t);
                        }
                    }
                };
            }

            public VTable()
            {
            }

            public Callback_getCharSet getCharSet;
            public Callback_execute execute;

            @Override
            protected java.util.List<String> getFieldOrder()
            {
                java.util.List<String> fields = super.getFieldOrder();
                fields.addAll(java.util.Arrays.asList("getCharSet", "execute"));
                return fields;
            }
        }

        public IExternalFunction()
        {
        }

        public IExternalFunction(IExternalFunctionIntf obj)
        {
            vTable = new VTable(obj);
            vTable.write();
            cloopVTable = vTable.getPointer();
            write();
        }

        @Override
        protected VTable createVTable()
        {
            return new VTable(cloopVTable);
        }

        public void getCharSet(IStatus status, IExternalContext context, com.sun.jna.Pointer name, int nameSize) throws FbException
        {
            VTable vTable = getVTable();
            vTable.getCharSet.invoke(this, status, context, name, nameSize);
            FbException.checkException(status);
        }

        public void execute(IStatus status, IExternalContext context, com.sun.jna.Pointer inMsg, com.sun.jna.Pointer outMsg) throws FbException
        {
            VTable vTable = getVTable();
            vTable.execute.invoke(this, status, context, inMsg, outMsg);
            FbException.checkException(status);
        }
    }

    public static class IExternalProcedure extends IDisposable implements IExternalProcedureIntf
    {
        public static class VTable extends IDisposable.VTable
        {
            public static interface Callback_getCharSet extends com.sun.jna.Callback
            {
                public void invoke(IExternalProcedure self, IStatus status, IExternalContext context, com.sun.jna.Pointer name, int nameSize);
            }

            public static interface Callback_open extends com.sun.jna.Callback
            {
                public IExternalResultSet invoke(IExternalProcedure self, IStatus status, IExternalContext context, com.sun.jna.Pointer inMsg, com.sun.jna.Pointer outMsg);
            }

            public VTable(com.sun.jna.Pointer pointer)
            {
                super(pointer);
            }

            public VTable(final IExternalProcedureIntf obj)
            {
                super(obj);

                getCharSet = new Callback_getCharSet() {
                    @Override
                    public void invoke(IExternalProcedure self, IStatus status, IExternalContext context, com.sun.jna.Pointer name, int nameSize)
                    {
                        try
                        {
                            obj.getCharSet(status, context, name, nameSize);
                        }
                        catch (Throwable t)
                        {
                            FbException.catchException(status, t);
                        }
                    }
                };

                open = new Callback_open() {
                    @Override
                    public IExternalResultSet invoke(IExternalProcedure self, IStatus status, IExternalContext context, com.sun.jna.Pointer inMsg, com.sun.jna.Pointer outMsg)
                    {
                        try
                        {
                            return obj.open(status, context, inMsg, outMsg);
                        }
                        catch (Throwable t)
                        {
                            FbException.catchException(status, t);
                            return null;
                        }
                    }
                };
            }

            public VTable()
            {
            }

            public Callback_getCharSet getCharSet;
            public Callback_open open;

            @Override
            protected java.util.List<String> getFieldOrder()
            {
                java.util.List<String> fields = super.getFieldOrder();
                fields.addAll(java.util.Arrays.asList("getCharSet", "open"));
                return fields;
            }
        }

        public IExternalProcedure()
        {
        }

        public IExternalProcedure(IExternalProcedureIntf obj)
        {
            vTable = new VTable(obj);
            vTable.write();
            cloopVTable = vTable.getPointer();
            write();
        }

        @Override
        protected VTable createVTable()
        {
            return new VTable(cloopVTable);
        }

        public void getCharSet(IStatus status, IExternalContext context, com.sun.jna.Pointer name, int nameSize) throws FbException
        {
            VTable vTable = getVTable();
            vTable.getCharSet.invoke(this, status, context, name, nameSize);
            FbException.checkException(status);
        }

        public IExternalResultSet open(IStatus status, IExternalContext context, com.sun.jna.Pointer inMsg, com.sun.jna.Pointer outMsg) throws FbException
        {
            VTable vTable = getVTable();
            IExternalResultSet result = vTable.open.invoke(this, status, context, inMsg, outMsg);
            FbException.checkException(status);
            return result;
        }
    }

    public static class IExternalTrigger extends IDisposable implements IExternalTriggerIntf
    {
        public static class VTable extends IDisposable.VTable
        {
            public static interface Callback_getCharSet extends com.sun.jna.Callback
            {
                public void invoke(IExternalTrigger self, IStatus status, IExternalContext context, com.sun.jna.Pointer name, int nameSize);
            }

            public static interface Callback_execute extends com.sun.jna.Callback
            {
                public void invoke(IExternalTrigger self, IStatus status, IExternalContext context, int action, com.sun.jna.Pointer oldMsg, com.sun.jna.Pointer newMsg, com.sun.jna.Pointer oldDbKey, com.sun.jna.Pointer newDbKey);
            }

            public VTable(com.sun.jna.Pointer pointer)
            {
                super(pointer);
            }

            public VTable(final IExternalTriggerIntf obj)
            {
                super(obj);

                getCharSet = new Callback_getCharSet() {
                    @Override
                    public void invoke(IExternalTrigger self, IStatus status, IExternalContext context, com.sun.jna.Pointer name, int nameSize)
                    {
                        try
                        {
                            obj.getCharSet(status, context, name, nameSize);
                        }
                        catch (Throwable t)
                        {
                            FbException.catchException(status, t);
                        }
                    }
                };

                execute = new Callback_execute() {
                    @Override
                    public void invoke(IExternalTrigger self, IStatus status, IExternalContext context, int action, com.sun.jna.Pointer oldMsg, com.sun.jna.Pointer newMsg, com.sun.jna.Pointer oldDbKey, com.sun.jna.Pointer newDbKey)
                    {
                        try
                        {
                            obj.execute(status, context, action, oldMsg, newMsg, oldDbKey, newDbKey);
                        }
                        catch (Throwable t)
                        {
                            FbException.catchException(status, t);
                        }
                    }
                };
            }

            public VTable()
            {
            }

            public Callback_getCharSet getCharSet;
            public Callback_execute execute;

            @Override
            protected java.util.List<String> getFieldOrder()
            {
                java.util.List<String> fields = super.getFieldOrder();
                fields.addAll(java.util.Arrays.asList("getCharSet", "execute"));
                return fields;
            }
        }

        public IExternalTrigger()
        {
        }

        public IExternalTrigger(IExternalTriggerIntf obj)
        {
            vTable = new VTable(obj);
            vTable.write();
            cloopVTable = vTable.getPointer();
            write();
        }

        @Override
        protected VTable createVTable()
        {
            return new VTable(cloopVTable);
        }

        public void getCharSet(IStatus status, IExternalContext context, com.sun.jna.Pointer name, int nameSize) throws FbException
        {
            VTable vTable = getVTable();
            vTable.getCharSet.invoke(this, status, context, name, nameSize);
            FbException.checkException(status);
        }

        public void execute(IStatus status, IExternalContext context, int action, com.sun.jna.Pointer oldMsg, com.sun.jna.Pointer newMsg, com.sun.jna.Pointer oldDbKey, com.sun.jna.Pointer newDbKey) throws FbException
        {
            VTable vTable = getVTable();
            vTable.execute.invoke(this, status, context, action, oldMsg, newMsg, oldDbKey, newDbKey);
            FbException.checkException(status);
        }
    }

    public static class IRoutineMetadata extends IVersioned implements IRoutineMetadataIntf
    {
        public static class VTable extends IVersioned.VTable
        {
            public static interface Callback_getPackage extends com.sun.jna.Callback
            {
                public String invoke(IRoutineMetadata self, IStatus status);
            }

            public static interface Callback_getName extends com.sun.jna.Callback
            {
                public String invoke(IRoutineMetadata self, IStatus status);
            }

            public static interface Callback_getEntryPoint extends com.sun.jna.Callback
            {
                public String invoke(IRoutineMetadata self, IStatus status);
            }

            public static interface Callback_getBody extends com.sun.jna.Callback
            {
                public String invoke(IRoutineMetadata self, IStatus status);
            }

            public static interface Callback_getInputMetadata extends com.sun.jna.Callback
            {
                public IMessageMetadata invoke(IRoutineMetadata self, IStatus status);
            }

            public static interface Callback_getOutputMetadata extends com.sun.jna.Callback
            {
                public IMessageMetadata invoke(IRoutineMetadata self, IStatus status);
            }

            public static interface Callback_getTriggerMetadata extends com.sun.jna.Callback
            {
                public IMessageMetadata invoke(IRoutineMetadata self, IStatus status);
            }

            public static interface Callback_getTriggerTable extends com.sun.jna.Callback
            {
                public String invoke(IRoutineMetadata self, IStatus status);
            }

            public static interface Callback_getTriggerType extends com.sun.jna.Callback
            {
                public int invoke(IRoutineMetadata self, IStatus status);
            }

            public VTable(com.sun.jna.Pointer pointer)
            {
                super(pointer);
            }

            public VTable(final IRoutineMetadataIntf obj)
            {
                super(obj);

                getPackage = new Callback_getPackage() {
                    @Override
                    public String invoke(IRoutineMetadata self, IStatus status)
                    {
                        try
                        {
                            return obj.getPackage(status);
                        }
                        catch (Throwable t)
                        {
                            FbException.catchException(status, t);
                            return null;
                        }
                    }
                };

                getName = new Callback_getName() {
                    @Override
                    public String invoke(IRoutineMetadata self, IStatus status)
                    {
                        try
                        {
                            return obj.getName(status);
                        }
                        catch (Throwable t)
                        {
                            FbException.catchException(status, t);
                            return null;
                        }
                    }
                };

                getEntryPoint = new Callback_getEntryPoint() {
                    @Override
                    public String invoke(IRoutineMetadata self, IStatus status)
                    {
                        try
                        {
                            return obj.getEntryPoint(status);
                        }
                        catch (Throwable t)
                        {
                            FbException.catchException(status, t);
                            return null;
                        }
                    }
                };

                getBody = new Callback_getBody() {
                    @Override
                    public String invoke(IRoutineMetadata self, IStatus status)
                    {
                        try
                        {
                            return obj.getBody(status);
                        }
                        catch (Throwable t)
                        {
                            FbException.catchException(status, t);
                            return null;
                        }
                    }
                };

                getInputMetadata = new Callback_getInputMetadata() {
                    @Override
                    public IMessageMetadata invoke(IRoutineMetadata self, IStatus status)
                    {
                        try
                        {
                            return obj.getInputMetadata(status);
                        }
                        catch (Throwable t)
                        {
                            FbException.catchException(status, t);
                            return null;
                        }
                    }
                };

                getOutputMetadata = new Callback_getOutputMetadata() {
                    @Override
                    public IMessageMetadata invoke(IRoutineMetadata self, IStatus status)
                    {
                        try
                        {
                            return obj.getOutputMetadata(status);
                        }
                        catch (Throwable t)
                        {
                            FbException.catchException(status, t);
                            return null;
                        }
                    }
                };

                getTriggerMetadata = new Callback_getTriggerMetadata() {
                    @Override
                    public IMessageMetadata invoke(IRoutineMetadata self, IStatus status)
                    {
                        try
                        {
                            return obj.getTriggerMetadata(status);
                        }
                        catch (Throwable t)
                        {
                            FbException.catchException(status, t);
                            return null;
                        }
                    }
                };

                getTriggerTable = new Callback_getTriggerTable() {
                    @Override
                    public String invoke(IRoutineMetadata self, IStatus status)
                    {
                        try
                        {
                            return obj.getTriggerTable(status);
                        }
                        catch (Throwable t)
                        {
                            FbException.catchException(status, t);
                            return null;
                        }
                    }
                };

                getTriggerType = new Callback_getTriggerType() {
                    @Override
                    public int invoke(IRoutineMetadata self, IStatus status)
                    {
                        try
                        {
                            return obj.getTriggerType(status);
                        }
                        catch (Throwable t)
                        {
                            FbException.catchException(status, t);
                            return 0;
                        }
                    }
                };
            }

            public VTable()
            {
            }

            public Callback_getPackage getPackage;
            public Callback_getName getName;
            public Callback_getEntryPoint getEntryPoint;
            public Callback_getBody getBody;
            public Callback_getInputMetadata getInputMetadata;
            public Callback_getOutputMetadata getOutputMetadata;
            public Callback_getTriggerMetadata getTriggerMetadata;
            public Callback_getTriggerTable getTriggerTable;
            public Callback_getTriggerType getTriggerType;

            @Override
            protected java.util.List<String> getFieldOrder()
            {
                java.util.List<String> fields = super.getFieldOrder();
                fields.addAll(java.util.Arrays.asList("getPackage", "getName", "getEntryPoint", "getBody", "getInputMetadata", "getOutputMetadata", "getTriggerMetadata", "getTriggerTable", "getTriggerType"));
                return fields;
            }
        }

        public IRoutineMetadata()
        {
        }

        public IRoutineMetadata(IRoutineMetadataIntf obj)
        {
            vTable = new VTable(obj);
            vTable.write();
            cloopVTable = vTable.getPointer();
            write();
        }

        @Override
        protected VTable createVTable()
        {
            return new VTable(cloopVTable);
        }

        public String getPackage(IStatus status) throws FbException
        {
            VTable vTable = getVTable();
            String result = vTable.getPackage.invoke(this, status);
            FbException.checkException(status);
            return result;
        }

        public String getName(IStatus status) throws FbException
        {
            VTable vTable = getVTable();
            String result = vTable.getName.invoke(this, status);
            FbException.checkException(status);
            return result;
        }

        public String getEntryPoint(IStatus status) throws FbException
        {
            VTable vTable = getVTable();
            String result = vTable.getEntryPoint.invoke(this, status);
            FbException.checkException(status);
            return result;
        }

        public String getBody(IStatus status) throws FbException
        {
            VTable vTable = getVTable();
            String result = vTable.getBody.invoke(this, status);
            FbException.checkException(status);
            return result;
        }

        public IMessageMetadata getInputMetadata(IStatus status) throws FbException
        {
            VTable vTable = getVTable();
            IMessageMetadata result = vTable.getInputMetadata.invoke(this, status);
            FbException.checkException(status);
            return result;
        }

        public IMessageMetadata getOutputMetadata(IStatus status) throws FbException
        {
            VTable vTable = getVTable();
            IMessageMetadata result = vTable.getOutputMetadata.invoke(this, status);
            FbException.checkException(status);
            return result;
        }

        public IMessageMetadata getTriggerMetadata(IStatus status) throws FbException
        {
            VTable vTable = getVTable();
            IMessageMetadata result = vTable.getTriggerMetadata.invoke(this, status);
            FbException.checkException(status);
            return result;
        }

        public String getTriggerTable(IStatus status) throws FbException
        {
            VTable vTable = getVTable();
            String result = vTable.getTriggerTable.invoke(this, status);
            FbException.checkException(status);
            return result;
        }

        public int getTriggerType(IStatus status) throws FbException
        {
            VTable vTable = getVTable();
            int result = vTable.getTriggerType.invoke(this, status);
            FbException.checkException(status);
            return result;
        }
    }

    public static class IExternalEngine extends IPluginBase implements IExternalEngineIntf
    {
        public static class VTable extends IPluginBase.VTable
        {
            public static interface Callback_open extends com.sun.jna.Callback
            {
                public void invoke(IExternalEngine self, IStatus status, IExternalContext context, com.sun.jna.Pointer charSet, int charSetSize);
            }

            public static interface Callback_openAttachment extends com.sun.jna.Callback
            {
                public void invoke(IExternalEngine self, IStatus status, IExternalContext context);
            }

            public static interface Callback_closeAttachment extends com.sun.jna.Callback
            {
                public void invoke(IExternalEngine self, IStatus status, IExternalContext context);
            }

            public static interface Callback_makeFunction extends com.sun.jna.Callback
            {
                public IExternalFunction invoke(IExternalEngine self, IStatus status, IExternalContext context, IRoutineMetadata metadata, IMetadataBuilder inBuilder, IMetadataBuilder outBuilder);
            }

            public static interface Callback_makeProcedure extends com.sun.jna.Callback
            {
                public IExternalProcedure invoke(IExternalEngine self, IStatus status, IExternalContext context, IRoutineMetadata metadata, IMetadataBuilder inBuilder, IMetadataBuilder outBuilder);
            }

            public static interface Callback_makeTrigger extends com.sun.jna.Callback
            {
                public IExternalTrigger invoke(IExternalEngine self, IStatus status, IExternalContext context, IRoutineMetadata metadata, IMetadataBuilder fieldsBuilder);
            }

            public VTable(com.sun.jna.Pointer pointer)
            {
                super(pointer);
            }

            public VTable(final IExternalEngineIntf obj)
            {
                super(obj);

                open = new Callback_open() {
                    @Override
                    public void invoke(IExternalEngine self, IStatus status, IExternalContext context, com.sun.jna.Pointer charSet, int charSetSize)
                    {
                        try
                        {
                            obj.open(status, context, charSet, charSetSize);
                        }
                        catch (Throwable t)
                        {
                            FbException.catchException(status, t);
                        }
                    }
                };

                openAttachment = new Callback_openAttachment() {
                    @Override
                    public void invoke(IExternalEngine self, IStatus status, IExternalContext context)
                    {
                        try
                        {
                            obj.openAttachment(status, context);
                        }
                        catch (Throwable t)
                        {
                            FbException.catchException(status, t);
                        }
                    }
                };

                closeAttachment = new Callback_closeAttachment() {
                    @Override
                    public void invoke(IExternalEngine self, IStatus status, IExternalContext context)
                    {
                        try
                        {
                            obj.closeAttachment(status, context);
                        }
                        catch (Throwable t)
                        {
                            FbException.catchException(status, t);
                        }
                    }
                };

                makeFunction = new Callback_makeFunction() {
                    @Override
                    public IExternalFunction invoke(IExternalEngine self, IStatus status, IExternalContext context, IRoutineMetadata metadata, IMetadataBuilder inBuilder, IMetadataBuilder outBuilder)
                    {
                        try
                        {
                            return obj.makeFunction(status, context, metadata, inBuilder, outBuilder);
                        }
                        catch (Throwable t)
                        {
                            FbException.catchException(status, t);
                            return null;
                        }
                    }
                };

                makeProcedure = new Callback_makeProcedure() {
                    @Override
                    public IExternalProcedure invoke(IExternalEngine self, IStatus status, IExternalContext context, IRoutineMetadata metadata, IMetadataBuilder inBuilder, IMetadataBuilder outBuilder)
                    {
                        try
                        {
                            return obj.makeProcedure(status, context, metadata, inBuilder, outBuilder);
                        }
                        catch (Throwable t)
                        {
                            FbException.catchException(status, t);
                            return null;
                        }
                    }
                };

                makeTrigger = new Callback_makeTrigger() {
                    @Override
                    public IExternalTrigger invoke(IExternalEngine self, IStatus status, IExternalContext context, IRoutineMetadata metadata, IMetadataBuilder fieldsBuilder)
                    {
                        try
                        {
                            return obj.makeTrigger(status, context, metadata, fieldsBuilder);
                        }
                        catch (Throwable t)
                        {
                            FbException.catchException(status, t);
                            return null;
                        }
                    }
                };
            }

            public VTable()
            {
            }

            public Callback_open open;
            public Callback_openAttachment openAttachment;
            public Callback_closeAttachment closeAttachment;
            public Callback_makeFunction makeFunction;
            public Callback_makeProcedure makeProcedure;
            public Callback_makeTrigger makeTrigger;

            @Override
            protected java.util.List<String> getFieldOrder()
            {
                java.util.List<String> fields = super.getFieldOrder();
                fields.addAll(java.util.Arrays.asList("open", "openAttachment", "closeAttachment", "makeFunction", "makeProcedure", "makeTrigger"));
                return fields;
            }
        }

        public IExternalEngine()
        {
        }

        public IExternalEngine(IExternalEngineIntf obj)
        {
            vTable = new VTable(obj);
            vTable.write();
            cloopVTable = vTable.getPointer();
            write();
        }

        @Override
        protected VTable createVTable()
        {
            return new VTable(cloopVTable);
        }

        public void open(IStatus status, IExternalContext context, com.sun.jna.Pointer charSet, int charSetSize) throws FbException
        {
            VTable vTable = getVTable();
            vTable.open.invoke(this, status, context, charSet, charSetSize);
            FbException.checkException(status);
        }

        public void openAttachment(IStatus status, IExternalContext context) throws FbException
        {
            VTable vTable = getVTable();
            vTable.openAttachment.invoke(this, status, context);
            FbException.checkException(status);
        }

        public void closeAttachment(IStatus status, IExternalContext context) throws FbException
        {
            VTable vTable = getVTable();
            vTable.closeAttachment.invoke(this, status, context);
            FbException.checkException(status);
        }

        public IExternalFunction makeFunction(IStatus status, IExternalContext context, IRoutineMetadata metadata, IMetadataBuilder inBuilder, IMetadataBuilder outBuilder) throws FbException
        {
            VTable vTable = getVTable();
            IExternalFunction result = vTable.makeFunction.invoke(this, status, context, metadata, inBuilder, outBuilder);
            FbException.checkException(status);
            return result;
        }

        public IExternalProcedure makeProcedure(IStatus status, IExternalContext context, IRoutineMetadata metadata, IMetadataBuilder inBuilder, IMetadataBuilder outBuilder) throws FbException
        {
            VTable vTable = getVTable();
            IExternalProcedure result = vTable.makeProcedure.invoke(this, status, context, metadata, inBuilder, outBuilder);
            FbException.checkException(status);
            return result;
        }

        public IExternalTrigger makeTrigger(IStatus status, IExternalContext context, IRoutineMetadata metadata, IMetadataBuilder fieldsBuilder) throws FbException
        {
            VTable vTable = getVTable();
            IExternalTrigger result = vTable.makeTrigger.invoke(this, status, context, metadata, fieldsBuilder);
            FbException.checkException(status);
            return result;
        }
    }

    public static class ITimer extends IReferenceCounted implements ITimerIntf
    {
        public static class VTable extends IReferenceCounted.VTable
        {
            public static interface Callback_handler extends com.sun.jna.Callback
            {
                public void invoke(ITimer self);
            }

            public VTable(com.sun.jna.Pointer pointer)
            {
                super(pointer);
            }

            public VTable(final ITimerIntf obj)
            {
                super(obj);

                handler = new Callback_handler() {
                    @Override
                    public void invoke(ITimer self)
                    {
                        obj.handler();
                    }
                };
            }

            public VTable()
            {
            }

            public Callback_handler handler;

            @Override
            protected java.util.List<String> getFieldOrder()
            {
                java.util.List<String> fields = super.getFieldOrder();
                fields.addAll(java.util.Arrays.asList("handler"));
                return fields;
            }
        }

        public ITimer()
        {
        }

        public ITimer(ITimerIntf obj)
        {
            vTable = new VTable(obj);
            vTable.write();
            cloopVTable = vTable.getPointer();
            write();
        }

        @Override
        protected VTable createVTable()
        {
            return new VTable(cloopVTable);
        }

        public void handler()
        {
            VTable vTable = getVTable();
            vTable.handler.invoke(this);
        }
    }

    public static class ITimerControl extends IVersioned implements ITimerControlIntf
    {
        public static class VTable extends IVersioned.VTable
        {
            public static interface Callback_start extends com.sun.jna.Callback
            {
                public void invoke(ITimerControl self, IStatus status, ITimer timer, long microSeconds);
            }

            public static interface Callback_stop extends com.sun.jna.Callback
            {
                public void invoke(ITimerControl self, IStatus status, ITimer timer);
            }

            public VTable(com.sun.jna.Pointer pointer)
            {
                super(pointer);
            }

            public VTable(final ITimerControlIntf obj)
            {
                super(obj);

                start = new Callback_start() {
                    @Override
                    public void invoke(ITimerControl self, IStatus status, ITimer timer, long microSeconds)
                    {
                        try
                        {
                            obj.start(status, timer, microSeconds);
                        }
                        catch (Throwable t)
                        {
                            FbException.catchException(status, t);
                        }
                    }
                };

                stop = new Callback_stop() {
                    @Override
                    public void invoke(ITimerControl self, IStatus status, ITimer timer)
                    {
                        try
                        {
                            obj.stop(status, timer);
                        }
                        catch (Throwable t)
                        {
                            FbException.catchException(status, t);
                        }
                    }
                };
            }

            public VTable()
            {
            }

            public Callback_start start;
            public Callback_stop stop;

            @Override
            protected java.util.List<String> getFieldOrder()
            {
                java.util.List<String> fields = super.getFieldOrder();
                fields.addAll(java.util.Arrays.asList("start", "stop"));
                return fields;
            }
        }

        public ITimerControl()
        {
        }

        public ITimerControl(ITimerControlIntf obj)
        {
            vTable = new VTable(obj);
            vTable.write();
            cloopVTable = vTable.getPointer();
            write();
        }

        @Override
        protected VTable createVTable()
        {
            return new VTable(cloopVTable);
        }

        public void start(IStatus status, ITimer timer, long microSeconds) throws FbException
        {
            VTable vTable = getVTable();
            vTable.start.invoke(this, status, timer, microSeconds);
            FbException.checkException(status);
        }

        public void stop(IStatus status, ITimer timer) throws FbException
        {
            VTable vTable = getVTable();
            vTable.stop.invoke(this, status, timer);
            FbException.checkException(status);
        }
    }

    public static class IVersionCallback extends IVersioned implements IVersionCallbackIntf
    {
        public static class VTable extends IVersioned.VTable
        {
            public static interface Callback_callback extends com.sun.jna.Callback
            {
                public void invoke(IVersionCallback self, IStatus status, String text);
            }

            public VTable(com.sun.jna.Pointer pointer)
            {
                super(pointer);
            }

            public VTable(final IVersionCallbackIntf obj)
            {
                super(obj);

                callback = new Callback_callback() {
                    @Override
                    public void invoke(IVersionCallback self, IStatus status, String text)
                    {
                        try
                        {
                            obj.callback(status, text);
                        }
                        catch (Throwable t)
                        {
                            FbException.catchException(status, t);
                        }
                    }
                };
            }

            public VTable()
            {
            }

            public Callback_callback callback;

            @Override
            protected java.util.List<String> getFieldOrder()
            {
                java.util.List<String> fields = super.getFieldOrder();
                fields.addAll(java.util.Arrays.asList("callback"));
                return fields;
            }
        }

        public IVersionCallback()
        {
        }

        public IVersionCallback(IVersionCallbackIntf obj)
        {
            vTable = new VTable(obj);
            vTable.write();
            cloopVTable = vTable.getPointer();
            write();
        }

        @Override
        protected VTable createVTable()
        {
            return new VTable(cloopVTable);
        }

        public void callback(IStatus status, String text) throws FbException
        {
            VTable vTable = getVTable();
            vTable.callback.invoke(this, status, text);
            FbException.checkException(status);
        }
    }

    public static class IUtil extends IVersioned implements IUtilIntf
    {
        public static class VTable extends IVersioned.VTable
        {
            public static interface Callback_getFbVersion extends com.sun.jna.Callback
            {
                public void invoke(IUtil self, IStatus status, IAttachment att, IVersionCallback callback);
            }

            public static interface Callback_loadBlob extends com.sun.jna.Callback
            {
                public void invoke(IUtil self, IStatus status, ISC_QUAD[] blobId, IAttachment att, ITransaction tra, String file, boolean txt);
            }

            public static interface Callback_dumpBlob extends com.sun.jna.Callback
            {
                public void invoke(IUtil self, IStatus status, ISC_QUAD[] blobId, IAttachment att, ITransaction tra, String file, boolean txt);
            }

            public static interface Callback_getPerfCounters extends com.sun.jna.Callback
            {
                public void invoke(IUtil self, IStatus status, IAttachment att, String countersSet, long[] counters);
            }

            public static interface Callback_executeCreateDatabase extends com.sun.jna.Callback
            {
                public IAttachment invoke(IUtil self, IStatus status, int stmtLength, String creatDBstatement, int dialect, boolean[] stmtIsCreateDb);
            }

            public static interface Callback_decodeDate extends com.sun.jna.Callback
            {
                public void invoke(IUtil self, ISC_DATE date, int[] year, int[] month, int[] day);
            }

            public static interface Callback_decodeTime extends com.sun.jna.Callback
            {
                public void invoke(IUtil self, ISC_TIME time, int[] hours, int[] minutes, int[] seconds, int[] fractions);
            }

            public static interface Callback_encodeDate extends com.sun.jna.Callback
            {
                public ISC_DATE invoke(IUtil self, int year, int month, int day);
            }

            public static interface Callback_encodeTime extends com.sun.jna.Callback
            {
                public ISC_TIME invoke(IUtil self, int hours, int minutes, int seconds, int fractions);
            }

            public static interface Callback_formatStatus extends com.sun.jna.Callback
            {
                public int invoke(IUtil self, com.sun.jna.Pointer buffer, int bufferSize, IStatus status);
            }

            public static interface Callback_getClientVersion extends com.sun.jna.Callback
            {
                public int invoke(IUtil self);
            }

            public static interface Callback_getXpbBuilder extends com.sun.jna.Callback
            {
                public IXpbBuilder invoke(IUtil self, IStatus status, int kind, byte[] buf, int len);
            }

            public static interface Callback_setOffsets extends com.sun.jna.Callback
            {
                public int invoke(IUtil self, IStatus status, IMessageMetadata metadata, IOffsetsCallback callback);
            }

            public VTable(com.sun.jna.Pointer pointer)
            {
                super(pointer);
            }

            public VTable(final IUtilIntf obj)
            {
                super(obj);

                getFbVersion = new Callback_getFbVersion() {
                    @Override
                    public void invoke(IUtil self, IStatus status, IAttachment att, IVersionCallback callback)
                    {
                        try
                        {
                            obj.getFbVersion(status, att, callback);
                        }
                        catch (Throwable t)
                        {
                            FbException.catchException(status, t);
                        }
                    }
                };

                loadBlob = new Callback_loadBlob() {
                    @Override
                    public void invoke(IUtil self, IStatus status, ISC_QUAD[] blobId, IAttachment att, ITransaction tra, String file, boolean txt)
                    {
                        try
                        {
                            obj.loadBlob(status, blobId, att, tra, file, txt);
                        }
                        catch (Throwable t)
                        {
                            FbException.catchException(status, t);
                        }
                    }
                };

                dumpBlob = new Callback_dumpBlob() {
                    @Override
                    public void invoke(IUtil self, IStatus status, ISC_QUAD[] blobId, IAttachment att, ITransaction tra, String file, boolean txt)
                    {
                        try
                        {
                            obj.dumpBlob(status, blobId, att, tra, file, txt);
                        }
                        catch (Throwable t)
                        {
                            FbException.catchException(status, t);
                        }
                    }
                };

                getPerfCounters = new Callback_getPerfCounters() {
                    @Override
                    public void invoke(IUtil self, IStatus status, IAttachment att, String countersSet, long[] counters)
                    {
                        try
                        {
                            obj.getPerfCounters(status, att, countersSet, counters);
                        }
                        catch (Throwable t)
                        {
                            FbException.catchException(status, t);
                        }
                    }
                };

                executeCreateDatabase = new Callback_executeCreateDatabase() {
                    @Override
                    public IAttachment invoke(IUtil self, IStatus status, int stmtLength, String creatDBstatement, int dialect, boolean[] stmtIsCreateDb)
                    {
                        try
                        {
                            return obj.executeCreateDatabase(status, stmtLength, creatDBstatement, dialect, stmtIsCreateDb);
                        }
                        catch (Throwable t)
                        {
                            FbException.catchException(status, t);
                            return null;
                        }
                    }
                };

                decodeDate = new Callback_decodeDate() {
                    @Override
                    public void invoke(IUtil self, ISC_DATE date, int[] year, int[] month, int[] day)
                    {
                        obj.decodeDate(date, year, month, day);
                    }
                };

                decodeTime = new Callback_decodeTime() {
                    @Override
                    public void invoke(IUtil self, ISC_TIME time, int[] hours, int[] minutes, int[] seconds, int[] fractions)
                    {
                        obj.decodeTime(time, hours, minutes, seconds, fractions);
                    }
                };

                encodeDate = new Callback_encodeDate() {
                    @Override
                    public ISC_DATE invoke(IUtil self, int year, int month, int day)
                    {
                        return obj.encodeDate(year, month, day);
                    }
                };

                encodeTime = new Callback_encodeTime() {
                    @Override
                    public ISC_TIME invoke(IUtil self, int hours, int minutes, int seconds, int fractions)
                    {
                        return obj.encodeTime(hours, minutes, seconds, fractions);
                    }
                };

                formatStatus = new Callback_formatStatus() {
                    @Override
                    public int invoke(IUtil self, com.sun.jna.Pointer buffer, int bufferSize, IStatus status)
                    {
                        return obj.formatStatus(buffer, bufferSize, status);
                    }
                };

                getClientVersion = new Callback_getClientVersion() {
                    @Override
                    public int invoke(IUtil self)
                    {
                        return obj.getClientVersion();
                    }
                };

                getXpbBuilder = new Callback_getXpbBuilder() {
                    @Override
                    public IXpbBuilder invoke(IUtil self, IStatus status, int kind, byte[] buf, int len)
                    {
                        try
                        {
                            return obj.getXpbBuilder(status, kind, buf, len);
                        }
                        catch (Throwable t)
                        {
                            FbException.catchException(status, t);
                            return null;
                        }
                    }
                };

                setOffsets = new Callback_setOffsets() {
                    @Override
                    public int invoke(IUtil self, IStatus status, IMessageMetadata metadata, IOffsetsCallback callback)
                    {
                        try
                        {
                            return obj.setOffsets(status, metadata, callback);
                        }
                        catch (Throwable t)
                        {
                            FbException.catchException(status, t);
                            return 0;
                        }
                    }
                };
            }

            public VTable()
            {
            }

            public Callback_getFbVersion getFbVersion;
            public Callback_loadBlob loadBlob;
            public Callback_dumpBlob dumpBlob;
            public Callback_getPerfCounters getPerfCounters;
            public Callback_executeCreateDatabase executeCreateDatabase;
            public Callback_decodeDate decodeDate;
            public Callback_decodeTime decodeTime;
            public Callback_encodeDate encodeDate;
            public Callback_encodeTime encodeTime;
            public Callback_formatStatus formatStatus;
            public Callback_getClientVersion getClientVersion;
            public Callback_getXpbBuilder getXpbBuilder;
            public Callback_setOffsets setOffsets;

            @Override
            protected java.util.List<String> getFieldOrder()
            {
                java.util.List<String> fields = super.getFieldOrder();
                fields.addAll(java.util.Arrays.asList("getFbVersion", "loadBlob", "dumpBlob", "getPerfCounters", "executeCreateDatabase", "decodeDate", "decodeTime", "encodeDate", "encodeTime", "formatStatus", "getClientVersion", "getXpbBuilder", "setOffsets"));
                return fields;
            }
        }

        public IUtil()
        {
        }

        public IUtil(IUtilIntf obj)
        {
            vTable = new VTable(obj);
            vTable.write();
            cloopVTable = vTable.getPointer();
            write();
        }

        @Override
        protected VTable createVTable()
        {
            return new VTable(cloopVTable);
        }

        public void getFbVersion(IStatus status, IAttachment att, IVersionCallback callback) throws FbException
        {
            VTable vTable = getVTable();
            vTable.getFbVersion.invoke(this, status, att, callback);
            FbException.checkException(status);
        }

        public void loadBlob(IStatus status, ISC_QUAD[] blobId, IAttachment att, ITransaction tra, String file, boolean txt) throws FbException
        {
            VTable vTable = getVTable();
            vTable.loadBlob.invoke(this, status, blobId, att, tra, file, txt);
            FbException.checkException(status);
        }

        public void dumpBlob(IStatus status, ISC_QUAD[] blobId, IAttachment att, ITransaction tra, String file, boolean txt) throws FbException
        {
            VTable vTable = getVTable();
            vTable.dumpBlob.invoke(this, status, blobId, att, tra, file, txt);
            FbException.checkException(status);
        }

        public void getPerfCounters(IStatus status, IAttachment att, String countersSet, long[] counters) throws FbException
        {
            VTable vTable = getVTable();
            vTable.getPerfCounters.invoke(this, status, att, countersSet, counters);
            FbException.checkException(status);
        }

        public IAttachment executeCreateDatabase(IStatus status, int stmtLength, String creatDBstatement, int dialect, boolean[] stmtIsCreateDb) throws FbException
        {
            VTable vTable = getVTable();
            IAttachment result = vTable.executeCreateDatabase.invoke(this, status, stmtLength, creatDBstatement, dialect, stmtIsCreateDb);
            FbException.checkException(status);
            return result;
        }

        public void decodeDate(ISC_DATE date, int[] year, int[] month, int[] day)
        {
            VTable vTable = getVTable();
            vTable.decodeDate.invoke(this, date, year, month, day);
        }

        public void decodeTime(ISC_TIME time, int[] hours, int[] minutes, int[] seconds, int[] fractions)
        {
            VTable vTable = getVTable();
            vTable.decodeTime.invoke(this, time, hours, minutes, seconds, fractions);
        }

        public ISC_DATE encodeDate(int year, int month, int day)
        {
            VTable vTable = getVTable();
            ISC_DATE result = vTable.encodeDate.invoke(this, year, month, day);
            return result;
        }

        public ISC_TIME encodeTime(int hours, int minutes, int seconds, int fractions)
        {
            VTable vTable = getVTable();
            ISC_TIME result = vTable.encodeTime.invoke(this, hours, minutes, seconds, fractions);
            return result;
        }

        public int formatStatus(com.sun.jna.Pointer buffer, int bufferSize, IStatus status)
        {
            VTable vTable = getVTable();
            int result = vTable.formatStatus.invoke(this, buffer, bufferSize, status);
            return result;
        }

        public int getClientVersion()
        {
            VTable vTable = getVTable();
            int result = vTable.getClientVersion.invoke(this);
            return result;
        }

        public IXpbBuilder getXpbBuilder(IStatus status, int kind, byte[] buf, int len) throws FbException
        {
            VTable vTable = getVTable();
            IXpbBuilder result = vTable.getXpbBuilder.invoke(this, status, kind, buf, len);
            FbException.checkException(status);
            return result;
        }

        public int setOffsets(IStatus status, IMessageMetadata metadata, IOffsetsCallback callback) throws FbException
        {
            VTable vTable = getVTable();
            int result = vTable.setOffsets.invoke(this, status, metadata, callback);
            FbException.checkException(status);
            return result;
        }
    }

    public static class IOffsetsCallback extends IVersioned implements IOffsetsCallbackIntf
    {
        public static class VTable extends IVersioned.VTable
        {
            public static interface Callback_setOffset extends com.sun.jna.Callback
            {
                public void invoke(IOffsetsCallback self, IStatus status, int index, int offset, int nullOffset);
            }

            public VTable(com.sun.jna.Pointer pointer)
            {
                super(pointer);
            }

            public VTable(final IOffsetsCallbackIntf obj)
            {
                super(obj);

                setOffset = new Callback_setOffset() {
                    @Override
                    public void invoke(IOffsetsCallback self, IStatus status, int index, int offset, int nullOffset)
                    {
                        try
                        {
                            obj.setOffset(status, index, offset, nullOffset);
                        }
                        catch (Throwable t)
                        {
                            FbException.catchException(status, t);
                        }
                    }
                };
            }

            public VTable()
            {
            }

            public Callback_setOffset setOffset;

            @Override
            protected java.util.List<String> getFieldOrder()
            {
                java.util.List<String> fields = super.getFieldOrder();
                fields.addAll(java.util.Arrays.asList("setOffset"));
                return fields;
            }
        }

        public IOffsetsCallback()
        {
        }

        public IOffsetsCallback(IOffsetsCallbackIntf obj)
        {
            vTable = new VTable(obj);
            vTable.write();
            cloopVTable = vTable.getPointer();
            write();
        }

        @Override
        protected VTable createVTable()
        {
            return new VTable(cloopVTable);
        }

        public void setOffset(IStatus status, int index, int offset, int nullOffset) throws FbException
        {
            VTable vTable = getVTable();
            vTable.setOffset.invoke(this, status, index, offset, nullOffset);
            FbException.checkException(status);
        }
    }

    public static class IXpbBuilder extends IDisposable implements IXpbBuilderIntf
    {
        public static class VTable extends IDisposable.VTable
        {
            public static interface Callback_clear extends com.sun.jna.Callback
            {
                public void invoke(IXpbBuilder self, IStatus status);
            }

            public static interface Callback_removeCurrent extends com.sun.jna.Callback
            {
                public void invoke(IXpbBuilder self, IStatus status);
            }

            public static interface Callback_insertInt extends com.sun.jna.Callback
            {
                public void invoke(IXpbBuilder self, IStatus status, byte tag, int value);
            }

            public static interface Callback_insertBigInt extends com.sun.jna.Callback
            {
                public void invoke(IXpbBuilder self, IStatus status, byte tag, long value);
            }

            public static interface Callback_insertBytes extends com.sun.jna.Callback
            {
                public void invoke(IXpbBuilder self, IStatus status, byte tag, com.sun.jna.Pointer bytes, int length);
            }

            public static interface Callback_insertString extends com.sun.jna.Callback
            {
                public void invoke(IXpbBuilder self, IStatus status, byte tag, String str);
            }

            public static interface Callback_insertTag extends com.sun.jna.Callback
            {
                public void invoke(IXpbBuilder self, IStatus status, byte tag);
            }

            public static interface Callback_isEof extends com.sun.jna.Callback
            {
                public boolean invoke(IXpbBuilder self, IStatus status);
            }

            public static interface Callback_moveNext extends com.sun.jna.Callback
            {
                public void invoke(IXpbBuilder self, IStatus status);
            }

            public static interface Callback_rewind extends com.sun.jna.Callback
            {
                public void invoke(IXpbBuilder self, IStatus status);
            }

            public static interface Callback_findFirst extends com.sun.jna.Callback
            {
                public boolean invoke(IXpbBuilder self, IStatus status, byte tag);
            }

            public static interface Callback_findNext extends com.sun.jna.Callback
            {
                public boolean invoke(IXpbBuilder self, IStatus status);
            }

            public static interface Callback_getTag extends com.sun.jna.Callback
            {
                public byte invoke(IXpbBuilder self, IStatus status);
            }

            public static interface Callback_getLength extends com.sun.jna.Callback
            {
                public int invoke(IXpbBuilder self, IStatus status);
            }

            public static interface Callback_getInt extends com.sun.jna.Callback
            {
                public int invoke(IXpbBuilder self, IStatus status);
            }

            public static interface Callback_getBigInt extends com.sun.jna.Callback
            {
                public long invoke(IXpbBuilder self, IStatus status);
            }

            public static interface Callback_getString extends com.sun.jna.Callback
            {
                public String invoke(IXpbBuilder self, IStatus status);
            }

            public static interface Callback_getBytes extends com.sun.jna.Callback
            {
                public com.sun.jna.Pointer invoke(IXpbBuilder self, IStatus status);
            }

            public static interface Callback_getBufferLength extends com.sun.jna.Callback
            {
                public int invoke(IXpbBuilder self, IStatus status);
            }

            public static interface Callback_getBuffer extends com.sun.jna.Callback
            {
                public com.sun.jna.Pointer invoke(IXpbBuilder self, IStatus status);
            }

            public VTable(com.sun.jna.Pointer pointer)
            {
                super(pointer);
            }

            public VTable(final IXpbBuilderIntf obj)
            {
                super(obj);

                clear = new Callback_clear() {
                    @Override
                    public void invoke(IXpbBuilder self, IStatus status)
                    {
                        try
                        {
                            obj.clear(status);
                        }
                        catch (Throwable t)
                        {
                            FbException.catchException(status, t);
                        }
                    }
                };

                removeCurrent = new Callback_removeCurrent() {
                    @Override
                    public void invoke(IXpbBuilder self, IStatus status)
                    {
                        try
                        {
                            obj.removeCurrent(status);
                        }
                        catch (Throwable t)
                        {
                            FbException.catchException(status, t);
                        }
                    }
                };

                insertInt = new Callback_insertInt() {
                    @Override
                    public void invoke(IXpbBuilder self, IStatus status, byte tag, int value)
                    {
                        try
                        {
                            obj.insertInt(status, tag, value);
                        }
                        catch (Throwable t)
                        {
                            FbException.catchException(status, t);
                        }
                    }
                };

                insertBigInt = new Callback_insertBigInt() {
                    @Override
                    public void invoke(IXpbBuilder self, IStatus status, byte tag, long value)
                    {
                        try
                        {
                            obj.insertBigInt(status, tag, value);
                        }
                        catch (Throwable t)
                        {
                            FbException.catchException(status, t);
                        }
                    }
                };

                insertBytes = new Callback_insertBytes() {
                    @Override
                    public void invoke(IXpbBuilder self, IStatus status, byte tag, com.sun.jna.Pointer bytes, int length)
                    {
                        try
                        {
                            obj.insertBytes(status, tag, bytes, length);
                        }
                        catch (Throwable t)
                        {
                            FbException.catchException(status, t);
                        }
                    }
                };

                insertString = new Callback_insertString() {
                    @Override
                    public void invoke(IXpbBuilder self, IStatus status, byte tag, String str)
                    {
                        try
                        {
                            obj.insertString(status, tag, str);
                        }
                        catch (Throwable t)
                        {
                            FbException.catchException(status, t);
                        }
                    }
                };

                insertTag = new Callback_insertTag() {
                    @Override
                    public void invoke(IXpbBuilder self, IStatus status, byte tag)
                    {
                        try
                        {
                            obj.insertTag(status, tag);
                        }
                        catch (Throwable t)
                        {
                            FbException.catchException(status, t);
                        }
                    }
                };

                isEof = new Callback_isEof() {
                    @Override
                    public boolean invoke(IXpbBuilder self, IStatus status)
                    {
                        try
                        {
                            return obj.isEof(status);
                        }
                        catch (Throwable t)
                        {
                            FbException.catchException(status, t);
                            return false;
                        }
                    }
                };

                moveNext = new Callback_moveNext() {
                    @Override
                    public void invoke(IXpbBuilder self, IStatus status)
                    {
                        try
                        {
                            obj.moveNext(status);
                        }
                        catch (Throwable t)
                        {
                            FbException.catchException(status, t);
                        }
                    }
                };

                rewind = new Callback_rewind() {
                    @Override
                    public void invoke(IXpbBuilder self, IStatus status)
                    {
                        try
                        {
                            obj.rewind(status);
                        }
                        catch (Throwable t)
                        {
                            FbException.catchException(status, t);
                        }
                    }
                };

                findFirst = new Callback_findFirst() {
                    @Override
                    public boolean invoke(IXpbBuilder self, IStatus status, byte tag)
                    {
                        try
                        {
                            return obj.findFirst(status, tag);
                        }
                        catch (Throwable t)
                        {
                            FbException.catchException(status, t);
                            return false;
                        }
                    }
                };

                findNext = new Callback_findNext() {
                    @Override
                    public boolean invoke(IXpbBuilder self, IStatus status)
                    {
                        try
                        {
                            return obj.findNext(status);
                        }
                        catch (Throwable t)
                        {
                            FbException.catchException(status, t);
                            return false;
                        }
                    }
                };

                getTag = new Callback_getTag() {
                    @Override
                    public byte invoke(IXpbBuilder self, IStatus status)
                    {
                        try
                        {
                            return obj.getTag(status);
                        }
                        catch (Throwable t)
                        {
                            FbException.catchException(status, t);
                            return (byte) 0;
                        }
                    }
                };

                getLength = new Callback_getLength() {
                    @Override
                    public int invoke(IXpbBuilder self, IStatus status)
                    {
                        try
                        {
                            return obj.getLength(status);
                        }
                        catch (Throwable t)
                        {
                            FbException.catchException(status, t);
                            return 0;
                        }
                    }
                };

                getInt = new Callback_getInt() {
                    @Override
                    public int invoke(IXpbBuilder self, IStatus status)
                    {
                        try
                        {
                            return obj.getInt(status);
                        }
                        catch (Throwable t)
                        {
                            FbException.catchException(status, t);
                            return 0;
                        }
                    }
                };

                getBigInt = new Callback_getBigInt() {
                    @Override
                    public long invoke(IXpbBuilder self, IStatus status)
                    {
                        try
                        {
                            return obj.getBigInt(status);
                        }
                        catch (Throwable t)
                        {
                            FbException.catchException(status, t);
                            return 0;
                        }
                    }
                };

                getString = new Callback_getString() {
                    @Override
                    public String invoke(IXpbBuilder self, IStatus status)
                    {
                        try
                        {
                            return obj.getString(status);
                        }
                        catch (Throwable t)
                        {
                            FbException.catchException(status, t);
                            return null;
                        }
                    }
                };

                getBytes = new Callback_getBytes() {
                    @Override
                    public com.sun.jna.Pointer invoke(IXpbBuilder self, IStatus status)
                    {
                        try
                        {
                            return obj.getBytes(status);
                        }
                        catch (Throwable t)
                        {
                            FbException.catchException(status, t);
                            return null;
                        }
                    }
                };

                getBufferLength = new Callback_getBufferLength() {
                    @Override
                    public int invoke(IXpbBuilder self, IStatus status)
                    {
                        try
                        {
                            return obj.getBufferLength(status);
                        }
                        catch (Throwable t)
                        {
                            FbException.catchException(status, t);
                            return 0;
                        }
                    }
                };

                getBuffer = new Callback_getBuffer() {
                    @Override
                    public com.sun.jna.Pointer invoke(IXpbBuilder self, IStatus status)
                    {
                        try
                        {
                            return obj.getBuffer(status);
                        }
                        catch (Throwable t)
                        {
                            FbException.catchException(status, t);
                            return null;
                        }
                    }
                };
            }

            public VTable()
            {
            }

            public Callback_clear clear;
            public Callback_removeCurrent removeCurrent;
            public Callback_insertInt insertInt;
            public Callback_insertBigInt insertBigInt;
            public Callback_insertBytes insertBytes;
            public Callback_insertString insertString;
            public Callback_insertTag insertTag;
            public Callback_isEof isEof;
            public Callback_moveNext moveNext;
            public Callback_rewind rewind;
            public Callback_findFirst findFirst;
            public Callback_findNext findNext;
            public Callback_getTag getTag;
            public Callback_getLength getLength;
            public Callback_getInt getInt;
            public Callback_getBigInt getBigInt;
            public Callback_getString getString;
            public Callback_getBytes getBytes;
            public Callback_getBufferLength getBufferLength;
            public Callback_getBuffer getBuffer;

            @Override
            protected java.util.List<String> getFieldOrder()
            {
                java.util.List<String> fields = super.getFieldOrder();
                fields.addAll(java.util.Arrays.asList("clear", "removeCurrent", "insertInt", "insertBigInt", "insertBytes", "insertString", "insertTag", "isEof", "moveNext", "rewind", "findFirst", "findNext", "getTag", "getLength", "getInt", "getBigInt", "getString", "getBytes", "getBufferLength", "getBuffer"));
                return fields;
            }
        }

        public IXpbBuilder()
        {
        }

        public IXpbBuilder(IXpbBuilderIntf obj)
        {
            vTable = new VTable(obj);
            vTable.write();
            cloopVTable = vTable.getPointer();
            write();
        }

        @Override
        protected VTable createVTable()
        {
            return new VTable(cloopVTable);
        }

        public void clear(IStatus status) throws FbException
        {
            VTable vTable = getVTable();
            vTable.clear.invoke(this, status);
            FbException.checkException(status);
        }

        public void removeCurrent(IStatus status) throws FbException
        {
            VTable vTable = getVTable();
            vTable.removeCurrent.invoke(this, status);
            FbException.checkException(status);
        }

        public void insertInt(IStatus status, byte tag, int value) throws FbException
        {
            VTable vTable = getVTable();
            vTable.insertInt.invoke(this, status, tag, value);
            FbException.checkException(status);
        }

        public void insertBigInt(IStatus status, byte tag, long value) throws FbException
        {
            VTable vTable = getVTable();
            vTable.insertBigInt.invoke(this, status, tag, value);
            FbException.checkException(status);
        }

        public void insertBytes(IStatus status, byte tag, com.sun.jna.Pointer bytes, int length) throws FbException
        {
            VTable vTable = getVTable();
            vTable.insertBytes.invoke(this, status, tag, bytes, length);
            FbException.checkException(status);
        }

        public void insertString(IStatus status, byte tag, String str) throws FbException
        {
            VTable vTable = getVTable();
            vTable.insertString.invoke(this, status, tag, str);
            FbException.checkException(status);
        }

        public void insertTag(IStatus status, byte tag) throws FbException
        {
            VTable vTable = getVTable();
            vTable.insertTag.invoke(this, status, tag);
            FbException.checkException(status);
        }

        public boolean isEof(IStatus status) throws FbException
        {
            VTable vTable = getVTable();
            boolean result = vTable.isEof.invoke(this, status);
            FbException.checkException(status);
            return result;
        }

        public void moveNext(IStatus status) throws FbException
        {
            VTable vTable = getVTable();
            vTable.moveNext.invoke(this, status);
            FbException.checkException(status);
        }

        public void rewind(IStatus status) throws FbException
        {
            VTable vTable = getVTable();
            vTable.rewind.invoke(this, status);
            FbException.checkException(status);
        }

        public boolean findFirst(IStatus status, byte tag) throws FbException
        {
            VTable vTable = getVTable();
            boolean result = vTable.findFirst.invoke(this, status, tag);
            FbException.checkException(status);
            return result;
        }

        public boolean findNext(IStatus status) throws FbException
        {
            VTable vTable = getVTable();
            boolean result = vTable.findNext.invoke(this, status);
            FbException.checkException(status);
            return result;
        }

        public byte getTag(IStatus status) throws FbException
        {
            VTable vTable = getVTable();
            byte result = vTable.getTag.invoke(this, status);
            FbException.checkException(status);
            return result;
        }

        public int getLength(IStatus status) throws FbException
        {
            VTable vTable = getVTable();
            int result = vTable.getLength.invoke(this, status);
            FbException.checkException(status);
            return result;
        }

        public int getInt(IStatus status) throws FbException
        {
            VTable vTable = getVTable();
            int result = vTable.getInt.invoke(this, status);
            FbException.checkException(status);
            return result;
        }

        public long getBigInt(IStatus status) throws FbException
        {
            VTable vTable = getVTable();
            long result = vTable.getBigInt.invoke(this, status);
            FbException.checkException(status);
            return result;
        }

        public String getString(IStatus status) throws FbException
        {
            VTable vTable = getVTable();
            String result = vTable.getString.invoke(this, status);
            FbException.checkException(status);
            return result;
        }

        public com.sun.jna.Pointer getBytes(IStatus status) throws FbException
        {
            VTable vTable = getVTable();
            com.sun.jna.Pointer result = vTable.getBytes.invoke(this, status);
            FbException.checkException(status);
            return result;
        }

        public int getBufferLength(IStatus status) throws FbException
        {
            VTable vTable = getVTable();
            int result = vTable.getBufferLength.invoke(this, status);
            FbException.checkException(status);
            return result;
        }

        public com.sun.jna.Pointer getBuffer(IStatus status) throws FbException
        {
            VTable vTable = getVTable();
            com.sun.jna.Pointer result = vTable.getBuffer.invoke(this, status);
            FbException.checkException(status);
            return result;
        }
    }

    public static class ITraceConnection extends IVersioned implements ITraceConnectionIntf
    {
        public static class VTable extends IVersioned.VTable
        {
            public static interface Callback_getKind extends com.sun.jna.Callback
            {
                public int invoke(ITraceConnection self);
            }

            public static interface Callback_getProcessID extends com.sun.jna.Callback
            {
                public int invoke(ITraceConnection self);
            }

            public static interface Callback_getUserName extends com.sun.jna.Callback
            {
                public String invoke(ITraceConnection self);
            }

            public static interface Callback_getRoleName extends com.sun.jna.Callback
            {
                public String invoke(ITraceConnection self);
            }

            public static interface Callback_getCharSet extends com.sun.jna.Callback
            {
                public String invoke(ITraceConnection self);
            }

            public static interface Callback_getRemoteProtocol extends com.sun.jna.Callback
            {
                public String invoke(ITraceConnection self);
            }

            public static interface Callback_getRemoteAddress extends com.sun.jna.Callback
            {
                public String invoke(ITraceConnection self);
            }

            public static interface Callback_getRemoteHwAddress extends com.sun.jna.Callback
            {
                public String invoke(ITraceConnection self);
            }

            public static interface Callback_getRemoteProcessID extends com.sun.jna.Callback
            {
                public int invoke(ITraceConnection self);
            }

            public static interface Callback_getRemoteProcessName extends com.sun.jna.Callback
            {
                public String invoke(ITraceConnection self);
            }

            public VTable(com.sun.jna.Pointer pointer)
            {
                super(pointer);
            }

            public VTable(final ITraceConnectionIntf obj)
            {
                super(obj);

                getKind = new Callback_getKind() {
                    @Override
                    public int invoke(ITraceConnection self)
                    {
                        return obj.getKind();
                    }
                };

                getProcessID = new Callback_getProcessID() {
                    @Override
                    public int invoke(ITraceConnection self)
                    {
                        return obj.getProcessID();
                    }
                };

                getUserName = new Callback_getUserName() {
                    @Override
                    public String invoke(ITraceConnection self)
                    {
                        return obj.getUserName();
                    }
                };

                getRoleName = new Callback_getRoleName() {
                    @Override
                    public String invoke(ITraceConnection self)
                    {
                        return obj.getRoleName();
                    }
                };

                getCharSet = new Callback_getCharSet() {
                    @Override
                    public String invoke(ITraceConnection self)
                    {
                        return obj.getCharSet();
                    }
                };

                getRemoteProtocol = new Callback_getRemoteProtocol() {
                    @Override
                    public String invoke(ITraceConnection self)
                    {
                        return obj.getRemoteProtocol();
                    }
                };

                getRemoteAddress = new Callback_getRemoteAddress() {
                    @Override
                    public String invoke(ITraceConnection self)
                    {
                        return obj.getRemoteAddress();
                    }
                };

                getRemoteHwAddress = new Callback_getRemoteHwAddress() {
                    @Override
                    public String invoke(ITraceConnection self)
                    {
                        return obj.getRemoteHwAddress();
                    }
                };

                getRemoteProcessID = new Callback_getRemoteProcessID() {
                    @Override
                    public int invoke(ITraceConnection self)
                    {
                        return obj.getRemoteProcessID();
                    }
                };

                getRemoteProcessName = new Callback_getRemoteProcessName() {
                    @Override
                    public String invoke(ITraceConnection self)
                    {
                        return obj.getRemoteProcessName();
                    }
                };
            }

            public VTable()
            {
            }

            public Callback_getKind getKind;
            public Callback_getProcessID getProcessID;
            public Callback_getUserName getUserName;
            public Callback_getRoleName getRoleName;
            public Callback_getCharSet getCharSet;
            public Callback_getRemoteProtocol getRemoteProtocol;
            public Callback_getRemoteAddress getRemoteAddress;
            public Callback_getRemoteHwAddress getRemoteHwAddress;
            public Callback_getRemoteProcessID getRemoteProcessID;
            public Callback_getRemoteProcessName getRemoteProcessName;

            @Override
            protected java.util.List<String> getFieldOrder()
            {
                java.util.List<String> fields = super.getFieldOrder();
                fields.addAll(java.util.Arrays.asList("getKind", "getProcessID", "getUserName", "getRoleName", "getCharSet", "getRemoteProtocol", "getRemoteAddress", "getRemoteHwAddress", "getRemoteProcessID", "getRemoteProcessName"));
                return fields;
            }
        }

        public ITraceConnection()
        {
        }

        public ITraceConnection(ITraceConnectionIntf obj)
        {
            vTable = new VTable(obj);
            vTable.write();
            cloopVTable = vTable.getPointer();
            write();
        }

        @Override
        protected VTable createVTable()
        {
            return new VTable(cloopVTable);
        }

        public int getKind()
        {
            VTable vTable = getVTable();
            int result = vTable.getKind.invoke(this);
            return result;
        }

        public int getProcessID()
        {
            VTable vTable = getVTable();
            int result = vTable.getProcessID.invoke(this);
            return result;
        }

        public String getUserName()
        {
            VTable vTable = getVTable();
            String result = vTable.getUserName.invoke(this);
            return result;
        }

        public String getRoleName()
        {
            VTable vTable = getVTable();
            String result = vTable.getRoleName.invoke(this);
            return result;
        }

        public String getCharSet()
        {
            VTable vTable = getVTable();
            String result = vTable.getCharSet.invoke(this);
            return result;
        }

        public String getRemoteProtocol()
        {
            VTable vTable = getVTable();
            String result = vTable.getRemoteProtocol.invoke(this);
            return result;
        }

        public String getRemoteAddress()
        {
            VTable vTable = getVTable();
            String result = vTable.getRemoteAddress.invoke(this);
            return result;
        }

        public String getRemoteHwAddress()
        {
            VTable vTable = getVTable();
            String result = vTable.getRemoteHwAddress.invoke(this);
            return result;
        }

        public int getRemoteProcessID()
        {
            VTable vTable = getVTable();
            int result = vTable.getRemoteProcessID.invoke(this);
            return result;
        }

        public String getRemoteProcessName()
        {
            VTable vTable = getVTable();
            String result = vTable.getRemoteProcessName.invoke(this);
            return result;
        }
    }

    public static class ITraceDatabaseConnection extends ITraceConnection implements ITraceDatabaseConnectionIntf
    {
        public static class VTable extends ITraceConnection.VTable
        {
            public static interface Callback_getConnectionID extends com.sun.jna.Callback
            {
                public long invoke(ITraceDatabaseConnection self);
            }

            public static interface Callback_getDatabaseName extends com.sun.jna.Callback
            {
                public String invoke(ITraceDatabaseConnection self);
            }

            public VTable(com.sun.jna.Pointer pointer)
            {
                super(pointer);
            }

            public VTable(final ITraceDatabaseConnectionIntf obj)
            {
                super(obj);

                getConnectionID = new Callback_getConnectionID() {
                    @Override
                    public long invoke(ITraceDatabaseConnection self)
                    {
                        return obj.getConnectionID();
                    }
                };

                getDatabaseName = new Callback_getDatabaseName() {
                    @Override
                    public String invoke(ITraceDatabaseConnection self)
                    {
                        return obj.getDatabaseName();
                    }
                };
            }

            public VTable()
            {
            }

            public Callback_getConnectionID getConnectionID;
            public Callback_getDatabaseName getDatabaseName;

            @Override
            protected java.util.List<String> getFieldOrder()
            {
                java.util.List<String> fields = super.getFieldOrder();
                fields.addAll(java.util.Arrays.asList("getConnectionID", "getDatabaseName"));
                return fields;
            }
        }

        public ITraceDatabaseConnection()
        {
        }

        public ITraceDatabaseConnection(ITraceDatabaseConnectionIntf obj)
        {
            vTable = new VTable(obj);
            vTable.write();
            cloopVTable = vTable.getPointer();
            write();
        }

        @Override
        protected VTable createVTable()
        {
            return new VTable(cloopVTable);
        }

        public long getConnectionID()
        {
            VTable vTable = getVTable();
            long result = vTable.getConnectionID.invoke(this);
            return result;
        }

        public String getDatabaseName()
        {
            VTable vTable = getVTable();
            String result = vTable.getDatabaseName.invoke(this);
            return result;
        }
    }

    public static class ITraceTransaction extends IVersioned implements ITraceTransactionIntf
    {
        public static class VTable extends IVersioned.VTable
        {
            public static interface Callback_getTransactionID extends com.sun.jna.Callback
            {
                public long invoke(ITraceTransaction self);
            }

            public static interface Callback_getReadOnly extends com.sun.jna.Callback
            {
                public boolean invoke(ITraceTransaction self);
            }

            public static interface Callback_getWait extends com.sun.jna.Callback
            {
                public int invoke(ITraceTransaction self);
            }

            public static interface Callback_getIsolation extends com.sun.jna.Callback
            {
                public int invoke(ITraceTransaction self);
            }

            public static interface Callback_getPerf extends com.sun.jna.Callback
            {
                public com.sun.jna.Pointer invoke(ITraceTransaction self);
            }

            public VTable(com.sun.jna.Pointer pointer)
            {
                super(pointer);
            }

            public VTable(final ITraceTransactionIntf obj)
            {
                super(obj);

                getTransactionID = new Callback_getTransactionID() {
                    @Override
                    public long invoke(ITraceTransaction self)
                    {
                        return obj.getTransactionID();
                    }
                };

                getReadOnly = new Callback_getReadOnly() {
                    @Override
                    public boolean invoke(ITraceTransaction self)
                    {
                        return obj.getReadOnly();
                    }
                };

                getWait = new Callback_getWait() {
                    @Override
                    public int invoke(ITraceTransaction self)
                    {
                        return obj.getWait();
                    }
                };

                getIsolation = new Callback_getIsolation() {
                    @Override
                    public int invoke(ITraceTransaction self)
                    {
                        return obj.getIsolation();
                    }
                };

                getPerf = new Callback_getPerf() {
                    @Override
                    public com.sun.jna.Pointer invoke(ITraceTransaction self)
                    {
                        return obj.getPerf();
                    }
                };
            }

            public VTable()
            {
            }

            public Callback_getTransactionID getTransactionID;
            public Callback_getReadOnly getReadOnly;
            public Callback_getWait getWait;
            public Callback_getIsolation getIsolation;
            public Callback_getPerf getPerf;

            @Override
            protected java.util.List<String> getFieldOrder()
            {
                java.util.List<String> fields = super.getFieldOrder();
                fields.addAll(java.util.Arrays.asList("getTransactionID", "getReadOnly", "getWait", "getIsolation", "getPerf"));
                return fields;
            }
        }

        public ITraceTransaction()
        {
        }

        public ITraceTransaction(ITraceTransactionIntf obj)
        {
            vTable = new VTable(obj);
            vTable.write();
            cloopVTable = vTable.getPointer();
            write();
        }

        @Override
        protected VTable createVTable()
        {
            return new VTable(cloopVTable);
        }

        public long getTransactionID()
        {
            VTable vTable = getVTable();
            long result = vTable.getTransactionID.invoke(this);
            return result;
        }

        public boolean getReadOnly()
        {
            VTable vTable = getVTable();
            boolean result = vTable.getReadOnly.invoke(this);
            return result;
        }

        public int getWait()
        {
            VTable vTable = getVTable();
            int result = vTable.getWait.invoke(this);
            return result;
        }

        public int getIsolation()
        {
            VTable vTable = getVTable();
            int result = vTable.getIsolation.invoke(this);
            return result;
        }

        public com.sun.jna.Pointer getPerf()
        {
            VTable vTable = getVTable();
            com.sun.jna.Pointer result = vTable.getPerf.invoke(this);
            return result;
        }
    }

    public static class ITraceParams extends IVersioned implements ITraceParamsIntf
    {
        public static class VTable extends IVersioned.VTable
        {
            public static interface Callback_getCount extends com.sun.jna.Callback
            {
                public int invoke(ITraceParams self);
            }

            public static interface Callback_getParam extends com.sun.jna.Callback
            {
                public com.sun.jna.Pointer invoke(ITraceParams self, int idx);
            }

            public static interface Callback_getTextUTF8 extends com.sun.jna.Callback
            {
                public String invoke(ITraceParams self, IStatus status, int idx);
            }

            public VTable(com.sun.jna.Pointer pointer)
            {
                super(pointer);
            }

            public VTable(final ITraceParamsIntf obj)
            {
                super(obj);

                getCount = new Callback_getCount() {
                    @Override
                    public int invoke(ITraceParams self)
                    {
                        return obj.getCount();
                    }
                };

                getParam = new Callback_getParam() {
                    @Override
                    public com.sun.jna.Pointer invoke(ITraceParams self, int idx)
                    {
                        return obj.getParam(idx);
                    }
                };

                getTextUTF8 = new Callback_getTextUTF8() {
                    @Override
                    public String invoke(ITraceParams self, IStatus status, int idx)
                    {
                        try
                        {
                            return obj.getTextUTF8(status, idx);
                        }
                        catch (Throwable t)
                        {
                            FbException.catchException(status, t);
                            return null;
                        }
                    }
                };
            }

            public VTable()
            {
            }

            public Callback_getCount getCount;
            public Callback_getParam getParam;
            public Callback_getTextUTF8 getTextUTF8;

            @Override
            protected java.util.List<String> getFieldOrder()
            {
                java.util.List<String> fields = super.getFieldOrder();
                fields.addAll(java.util.Arrays.asList("getCount", "getParam", "getTextUTF8"));
                return fields;
            }
        }

        public ITraceParams()
        {
        }

        public ITraceParams(ITraceParamsIntf obj)
        {
            vTable = new VTable(obj);
            vTable.write();
            cloopVTable = vTable.getPointer();
            write();
        }

        @Override
        protected VTable createVTable()
        {
            return new VTable(cloopVTable);
        }

        public int getCount()
        {
            VTable vTable = getVTable();
            int result = vTable.getCount.invoke(this);
            return result;
        }

        public com.sun.jna.Pointer getParam(int idx)
        {
            VTable vTable = getVTable();
            com.sun.jna.Pointer result = vTable.getParam.invoke(this, idx);
            return result;
        }

        public String getTextUTF8(IStatus status, int idx) throws FbException
        {
            VTable vTable = getVTable();
            String result = vTable.getTextUTF8.invoke(this, status, idx);
            FbException.checkException(status);
            return result;
        }
    }

    public static class ITraceStatement extends IVersioned implements ITraceStatementIntf
    {
        public static class VTable extends IVersioned.VTable
        {
            public static interface Callback_getStmtID extends com.sun.jna.Callback
            {
                public long invoke(ITraceStatement self);
            }

            public static interface Callback_getPerf extends com.sun.jna.Callback
            {
                public com.sun.jna.Pointer invoke(ITraceStatement self);
            }

            public VTable(com.sun.jna.Pointer pointer)
            {
                super(pointer);
            }

            public VTable(final ITraceStatementIntf obj)
            {
                super(obj);

                getStmtID = new Callback_getStmtID() {
                    @Override
                    public long invoke(ITraceStatement self)
                    {
                        return obj.getStmtID();
                    }
                };

                getPerf = new Callback_getPerf() {
                    @Override
                    public com.sun.jna.Pointer invoke(ITraceStatement self)
                    {
                        return obj.getPerf();
                    }
                };
            }

            public VTable()
            {
            }

            public Callback_getStmtID getStmtID;
            public Callback_getPerf getPerf;

            @Override
            protected java.util.List<String> getFieldOrder()
            {
                java.util.List<String> fields = super.getFieldOrder();
                fields.addAll(java.util.Arrays.asList("getStmtID", "getPerf"));
                return fields;
            }
        }

        public ITraceStatement()
        {
        }

        public ITraceStatement(ITraceStatementIntf obj)
        {
            vTable = new VTable(obj);
            vTable.write();
            cloopVTable = vTable.getPointer();
            write();
        }

        @Override
        protected VTable createVTable()
        {
            return new VTable(cloopVTable);
        }

        public long getStmtID()
        {
            VTable vTable = getVTable();
            long result = vTable.getStmtID.invoke(this);
            return result;
        }

        public com.sun.jna.Pointer getPerf()
        {
            VTable vTable = getVTable();
            com.sun.jna.Pointer result = vTable.getPerf.invoke(this);
            return result;
        }
    }

    public static class ITraceSQLStatement extends ITraceStatement implements ITraceSQLStatementIntf
    {
        public static class VTable extends ITraceStatement.VTable
        {
            public static interface Callback_getText extends com.sun.jna.Callback
            {
                public String invoke(ITraceSQLStatement self);
            }

            public static interface Callback_getPlan extends com.sun.jna.Callback
            {
                public String invoke(ITraceSQLStatement self);
            }

            public static interface Callback_getInputs extends com.sun.jna.Callback
            {
                public ITraceParams invoke(ITraceSQLStatement self);
            }

            public static interface Callback_getTextUTF8 extends com.sun.jna.Callback
            {
                public String invoke(ITraceSQLStatement self);
            }

            public static interface Callback_getExplainedPlan extends com.sun.jna.Callback
            {
                public String invoke(ITraceSQLStatement self);
            }

            public VTable(com.sun.jna.Pointer pointer)
            {
                super(pointer);
            }

            public VTable(final ITraceSQLStatementIntf obj)
            {
                super(obj);

                getText = new Callback_getText() {
                    @Override
                    public String invoke(ITraceSQLStatement self)
                    {
                        return obj.getText();
                    }
                };

                getPlan = new Callback_getPlan() {
                    @Override
                    public String invoke(ITraceSQLStatement self)
                    {
                        return obj.getPlan();
                    }
                };

                getInputs = new Callback_getInputs() {
                    @Override
                    public ITraceParams invoke(ITraceSQLStatement self)
                    {
                        return obj.getInputs();
                    }
                };

                getTextUTF8 = new Callback_getTextUTF8() {
                    @Override
                    public String invoke(ITraceSQLStatement self)
                    {
                        return obj.getTextUTF8();
                    }
                };

                getExplainedPlan = new Callback_getExplainedPlan() {
                    @Override
                    public String invoke(ITraceSQLStatement self)
                    {
                        return obj.getExplainedPlan();
                    }
                };
            }

            public VTable()
            {
            }

            public Callback_getText getText;
            public Callback_getPlan getPlan;
            public Callback_getInputs getInputs;
            public Callback_getTextUTF8 getTextUTF8;
            public Callback_getExplainedPlan getExplainedPlan;

            @Override
            protected java.util.List<String> getFieldOrder()
            {
                java.util.List<String> fields = super.getFieldOrder();
                fields.addAll(java.util.Arrays.asList("getText", "getPlan", "getInputs", "getTextUTF8", "getExplainedPlan"));
                return fields;
            }
        }

        public ITraceSQLStatement()
        {
        }

        public ITraceSQLStatement(ITraceSQLStatementIntf obj)
        {
            vTable = new VTable(obj);
            vTable.write();
            cloopVTable = vTable.getPointer();
            write();
        }

        @Override
        protected VTable createVTable()
        {
            return new VTable(cloopVTable);
        }

        public String getText()
        {
            VTable vTable = getVTable();
            String result = vTable.getText.invoke(this);
            return result;
        }

        public String getPlan()
        {
            VTable vTable = getVTable();
            String result = vTable.getPlan.invoke(this);
            return result;
        }

        public ITraceParams getInputs()
        {
            VTable vTable = getVTable();
            ITraceParams result = vTable.getInputs.invoke(this);
            return result;
        }

        public String getTextUTF8()
        {
            VTable vTable = getVTable();
            String result = vTable.getTextUTF8.invoke(this);
            return result;
        }

        public String getExplainedPlan()
        {
            VTable vTable = getVTable();
            String result = vTable.getExplainedPlan.invoke(this);
            return result;
        }
    }

    public static class ITraceBLRStatement extends ITraceStatement implements ITraceBLRStatementIntf
    {
        public static class VTable extends ITraceStatement.VTable
        {
            public static interface Callback_getData extends com.sun.jna.Callback
            {
                public com.sun.jna.Pointer invoke(ITraceBLRStatement self);
            }

            public static interface Callback_getDataLength extends com.sun.jna.Callback
            {
                public int invoke(ITraceBLRStatement self);
            }

            public static interface Callback_getText extends com.sun.jna.Callback
            {
                public String invoke(ITraceBLRStatement self);
            }

            public VTable(com.sun.jna.Pointer pointer)
            {
                super(pointer);
            }

            public VTable(final ITraceBLRStatementIntf obj)
            {
                super(obj);

                getData = new Callback_getData() {
                    @Override
                    public com.sun.jna.Pointer invoke(ITraceBLRStatement self)
                    {
                        return obj.getData();
                    }
                };

                getDataLength = new Callback_getDataLength() {
                    @Override
                    public int invoke(ITraceBLRStatement self)
                    {
                        return obj.getDataLength();
                    }
                };

                getText = new Callback_getText() {
                    @Override
                    public String invoke(ITraceBLRStatement self)
                    {
                        return obj.getText();
                    }
                };
            }

            public VTable()
            {
            }

            public Callback_getData getData;
            public Callback_getDataLength getDataLength;
            public Callback_getText getText;

            @Override
            protected java.util.List<String> getFieldOrder()
            {
                java.util.List<String> fields = super.getFieldOrder();
                fields.addAll(java.util.Arrays.asList("getData", "getDataLength", "getText"));
                return fields;
            }
        }

        public ITraceBLRStatement()
        {
        }

        public ITraceBLRStatement(ITraceBLRStatementIntf obj)
        {
            vTable = new VTable(obj);
            vTable.write();
            cloopVTable = vTable.getPointer();
            write();
        }

        @Override
        protected VTable createVTable()
        {
            return new VTable(cloopVTable);
        }

        public com.sun.jna.Pointer getData()
        {
            VTable vTable = getVTable();
            com.sun.jna.Pointer result = vTable.getData.invoke(this);
            return result;
        }

        public int getDataLength()
        {
            VTable vTable = getVTable();
            int result = vTable.getDataLength.invoke(this);
            return result;
        }

        public String getText()
        {
            VTable vTable = getVTable();
            String result = vTable.getText.invoke(this);
            return result;
        }
    }

    public static class ITraceDYNRequest extends IVersioned implements ITraceDYNRequestIntf
    {
        public static class VTable extends IVersioned.VTable
        {
            public static interface Callback_getData extends com.sun.jna.Callback
            {
                public com.sun.jna.Pointer invoke(ITraceDYNRequest self);
            }

            public static interface Callback_getDataLength extends com.sun.jna.Callback
            {
                public int invoke(ITraceDYNRequest self);
            }

            public static interface Callback_getText extends com.sun.jna.Callback
            {
                public String invoke(ITraceDYNRequest self);
            }

            public VTable(com.sun.jna.Pointer pointer)
            {
                super(pointer);
            }

            public VTable(final ITraceDYNRequestIntf obj)
            {
                super(obj);

                getData = new Callback_getData() {
                    @Override
                    public com.sun.jna.Pointer invoke(ITraceDYNRequest self)
                    {
                        return obj.getData();
                    }
                };

                getDataLength = new Callback_getDataLength() {
                    @Override
                    public int invoke(ITraceDYNRequest self)
                    {
                        return obj.getDataLength();
                    }
                };

                getText = new Callback_getText() {
                    @Override
                    public String invoke(ITraceDYNRequest self)
                    {
                        return obj.getText();
                    }
                };
            }

            public VTable()
            {
            }

            public Callback_getData getData;
            public Callback_getDataLength getDataLength;
            public Callback_getText getText;

            @Override
            protected java.util.List<String> getFieldOrder()
            {
                java.util.List<String> fields = super.getFieldOrder();
                fields.addAll(java.util.Arrays.asList("getData", "getDataLength", "getText"));
                return fields;
            }
        }

        public ITraceDYNRequest()
        {
        }

        public ITraceDYNRequest(ITraceDYNRequestIntf obj)
        {
            vTable = new VTable(obj);
            vTable.write();
            cloopVTable = vTable.getPointer();
            write();
        }

        @Override
        protected VTable createVTable()
        {
            return new VTable(cloopVTable);
        }

        public com.sun.jna.Pointer getData()
        {
            VTable vTable = getVTable();
            com.sun.jna.Pointer result = vTable.getData.invoke(this);
            return result;
        }

        public int getDataLength()
        {
            VTable vTable = getVTable();
            int result = vTable.getDataLength.invoke(this);
            return result;
        }

        public String getText()
        {
            VTable vTable = getVTable();
            String result = vTable.getText.invoke(this);
            return result;
        }
    }

    public static class ITraceContextVariable extends IVersioned implements ITraceContextVariableIntf
    {
        public static class VTable extends IVersioned.VTable
        {
            public static interface Callback_getNameSpace extends com.sun.jna.Callback
            {
                public String invoke(ITraceContextVariable self);
            }

            public static interface Callback_getVarName extends com.sun.jna.Callback
            {
                public String invoke(ITraceContextVariable self);
            }

            public static interface Callback_getVarValue extends com.sun.jna.Callback
            {
                public String invoke(ITraceContextVariable self);
            }

            public VTable(com.sun.jna.Pointer pointer)
            {
                super(pointer);
            }

            public VTable(final ITraceContextVariableIntf obj)
            {
                super(obj);

                getNameSpace = new Callback_getNameSpace() {
                    @Override
                    public String invoke(ITraceContextVariable self)
                    {
                        return obj.getNameSpace();
                    }
                };

                getVarName = new Callback_getVarName() {
                    @Override
                    public String invoke(ITraceContextVariable self)
                    {
                        return obj.getVarName();
                    }
                };

                getVarValue = new Callback_getVarValue() {
                    @Override
                    public String invoke(ITraceContextVariable self)
                    {
                        return obj.getVarValue();
                    }
                };
            }

            public VTable()
            {
            }

            public Callback_getNameSpace getNameSpace;
            public Callback_getVarName getVarName;
            public Callback_getVarValue getVarValue;

            @Override
            protected java.util.List<String> getFieldOrder()
            {
                java.util.List<String> fields = super.getFieldOrder();
                fields.addAll(java.util.Arrays.asList("getNameSpace", "getVarName", "getVarValue"));
                return fields;
            }
        }

        public ITraceContextVariable()
        {
        }

        public ITraceContextVariable(ITraceContextVariableIntf obj)
        {
            vTable = new VTable(obj);
            vTable.write();
            cloopVTable = vTable.getPointer();
            write();
        }

        @Override
        protected VTable createVTable()
        {
            return new VTable(cloopVTable);
        }

        public String getNameSpace()
        {
            VTable vTable = getVTable();
            String result = vTable.getNameSpace.invoke(this);
            return result;
        }

        public String getVarName()
        {
            VTable vTable = getVTable();
            String result = vTable.getVarName.invoke(this);
            return result;
        }

        public String getVarValue()
        {
            VTable vTable = getVTable();
            String result = vTable.getVarValue.invoke(this);
            return result;
        }
    }

    public static class ITraceProcedure extends IVersioned implements ITraceProcedureIntf
    {
        public static class VTable extends IVersioned.VTable
        {
            public static interface Callback_getProcName extends com.sun.jna.Callback
            {
                public String invoke(ITraceProcedure self);
            }

            public static interface Callback_getInputs extends com.sun.jna.Callback
            {
                public ITraceParams invoke(ITraceProcedure self);
            }

            public static interface Callback_getPerf extends com.sun.jna.Callback
            {
                public com.sun.jna.Pointer invoke(ITraceProcedure self);
            }

            public VTable(com.sun.jna.Pointer pointer)
            {
                super(pointer);
            }

            public VTable(final ITraceProcedureIntf obj)
            {
                super(obj);

                getProcName = new Callback_getProcName() {
                    @Override
                    public String invoke(ITraceProcedure self)
                    {
                        return obj.getProcName();
                    }
                };

                getInputs = new Callback_getInputs() {
                    @Override
                    public ITraceParams invoke(ITraceProcedure self)
                    {
                        return obj.getInputs();
                    }
                };

                getPerf = new Callback_getPerf() {
                    @Override
                    public com.sun.jna.Pointer invoke(ITraceProcedure self)
                    {
                        return obj.getPerf();
                    }
                };
            }

            public VTable()
            {
            }

            public Callback_getProcName getProcName;
            public Callback_getInputs getInputs;
            public Callback_getPerf getPerf;

            @Override
            protected java.util.List<String> getFieldOrder()
            {
                java.util.List<String> fields = super.getFieldOrder();
                fields.addAll(java.util.Arrays.asList("getProcName", "getInputs", "getPerf"));
                return fields;
            }
        }

        public ITraceProcedure()
        {
        }

        public ITraceProcedure(ITraceProcedureIntf obj)
        {
            vTable = new VTable(obj);
            vTable.write();
            cloopVTable = vTable.getPointer();
            write();
        }

        @Override
        protected VTable createVTable()
        {
            return new VTable(cloopVTable);
        }

        public String getProcName()
        {
            VTable vTable = getVTable();
            String result = vTable.getProcName.invoke(this);
            return result;
        }

        public ITraceParams getInputs()
        {
            VTable vTable = getVTable();
            ITraceParams result = vTable.getInputs.invoke(this);
            return result;
        }

        public com.sun.jna.Pointer getPerf()
        {
            VTable vTable = getVTable();
            com.sun.jna.Pointer result = vTable.getPerf.invoke(this);
            return result;
        }
    }

    public static class ITraceFunction extends IVersioned implements ITraceFunctionIntf
    {
        public static class VTable extends IVersioned.VTable
        {
            public static interface Callback_getFuncName extends com.sun.jna.Callback
            {
                public String invoke(ITraceFunction self);
            }

            public static interface Callback_getInputs extends com.sun.jna.Callback
            {
                public ITraceParams invoke(ITraceFunction self);
            }

            public static interface Callback_getResult extends com.sun.jna.Callback
            {
                public ITraceParams invoke(ITraceFunction self);
            }

            public static interface Callback_getPerf extends com.sun.jna.Callback
            {
                public com.sun.jna.Pointer invoke(ITraceFunction self);
            }

            public VTable(com.sun.jna.Pointer pointer)
            {
                super(pointer);
            }

            public VTable(final ITraceFunctionIntf obj)
            {
                super(obj);

                getFuncName = new Callback_getFuncName() {
                    @Override
                    public String invoke(ITraceFunction self)
                    {
                        return obj.getFuncName();
                    }
                };

                getInputs = new Callback_getInputs() {
                    @Override
                    public ITraceParams invoke(ITraceFunction self)
                    {
                        return obj.getInputs();
                    }
                };

                getResult = new Callback_getResult() {
                    @Override
                    public ITraceParams invoke(ITraceFunction self)
                    {
                        return obj.getResult();
                    }
                };

                getPerf = new Callback_getPerf() {
                    @Override
                    public com.sun.jna.Pointer invoke(ITraceFunction self)
                    {
                        return obj.getPerf();
                    }
                };
            }

            public VTable()
            {
            }

            public Callback_getFuncName getFuncName;
            public Callback_getInputs getInputs;
            public Callback_getResult getResult;
            public Callback_getPerf getPerf;

            @Override
            protected java.util.List<String> getFieldOrder()
            {
                java.util.List<String> fields = super.getFieldOrder();
                fields.addAll(java.util.Arrays.asList("getFuncName", "getInputs", "getResult", "getPerf"));
                return fields;
            }
        }

        public ITraceFunction()
        {
        }

        public ITraceFunction(ITraceFunctionIntf obj)
        {
            vTable = new VTable(obj);
            vTable.write();
            cloopVTable = vTable.getPointer();
            write();
        }

        @Override
        protected VTable createVTable()
        {
            return new VTable(cloopVTable);
        }

        public String getFuncName()
        {
            VTable vTable = getVTable();
            String result = vTable.getFuncName.invoke(this);
            return result;
        }

        public ITraceParams getInputs()
        {
            VTable vTable = getVTable();
            ITraceParams result = vTable.getInputs.invoke(this);
            return result;
        }

        public ITraceParams getResult()
        {
            VTable vTable = getVTable();
            ITraceParams result = vTable.getResult.invoke(this);
            return result;
        }

        public com.sun.jna.Pointer getPerf()
        {
            VTable vTable = getVTable();
            com.sun.jna.Pointer result = vTable.getPerf.invoke(this);
            return result;
        }
    }

    public static class ITraceTrigger extends IVersioned implements ITraceTriggerIntf
    {
        public static class VTable extends IVersioned.VTable
        {
            public static interface Callback_getTriggerName extends com.sun.jna.Callback
            {
                public String invoke(ITraceTrigger self);
            }

            public static interface Callback_getRelationName extends com.sun.jna.Callback
            {
                public String invoke(ITraceTrigger self);
            }

            public static interface Callback_getAction extends com.sun.jna.Callback
            {
                public int invoke(ITraceTrigger self);
            }

            public static interface Callback_getWhich extends com.sun.jna.Callback
            {
                public int invoke(ITraceTrigger self);
            }

            public static interface Callback_getPerf extends com.sun.jna.Callback
            {
                public com.sun.jna.Pointer invoke(ITraceTrigger self);
            }

            public VTable(com.sun.jna.Pointer pointer)
            {
                super(pointer);
            }

            public VTable(final ITraceTriggerIntf obj)
            {
                super(obj);

                getTriggerName = new Callback_getTriggerName() {
                    @Override
                    public String invoke(ITraceTrigger self)
                    {
                        return obj.getTriggerName();
                    }
                };

                getRelationName = new Callback_getRelationName() {
                    @Override
                    public String invoke(ITraceTrigger self)
                    {
                        return obj.getRelationName();
                    }
                };

                getAction = new Callback_getAction() {
                    @Override
                    public int invoke(ITraceTrigger self)
                    {
                        return obj.getAction();
                    }
                };

                getWhich = new Callback_getWhich() {
                    @Override
                    public int invoke(ITraceTrigger self)
                    {
                        return obj.getWhich();
                    }
                };

                getPerf = new Callback_getPerf() {
                    @Override
                    public com.sun.jna.Pointer invoke(ITraceTrigger self)
                    {
                        return obj.getPerf();
                    }
                };
            }

            public VTable()
            {
            }

            public Callback_getTriggerName getTriggerName;
            public Callback_getRelationName getRelationName;
            public Callback_getAction getAction;
            public Callback_getWhich getWhich;
            public Callback_getPerf getPerf;

            @Override
            protected java.util.List<String> getFieldOrder()
            {
                java.util.List<String> fields = super.getFieldOrder();
                fields.addAll(java.util.Arrays.asList("getTriggerName", "getRelationName", "getAction", "getWhich", "getPerf"));
                return fields;
            }
        }

        public ITraceTrigger()
        {
        }

        public ITraceTrigger(ITraceTriggerIntf obj)
        {
            vTable = new VTable(obj);
            vTable.write();
            cloopVTable = vTable.getPointer();
            write();
        }

        @Override
        protected VTable createVTable()
        {
            return new VTable(cloopVTable);
        }

        public String getTriggerName()
        {
            VTable vTable = getVTable();
            String result = vTable.getTriggerName.invoke(this);
            return result;
        }

        public String getRelationName()
        {
            VTable vTable = getVTable();
            String result = vTable.getRelationName.invoke(this);
            return result;
        }

        public int getAction()
        {
            VTable vTable = getVTable();
            int result = vTable.getAction.invoke(this);
            return result;
        }

        public int getWhich()
        {
            VTable vTable = getVTable();
            int result = vTable.getWhich.invoke(this);
            return result;
        }

        public com.sun.jna.Pointer getPerf()
        {
            VTable vTable = getVTable();
            com.sun.jna.Pointer result = vTable.getPerf.invoke(this);
            return result;
        }
    }

    public static class ITraceServiceConnection extends ITraceConnection implements ITraceServiceConnectionIntf
    {
        public static class VTable extends ITraceConnection.VTable
        {
            public static interface Callback_getServiceID extends com.sun.jna.Callback
            {
                public com.sun.jna.Pointer invoke(ITraceServiceConnection self);
            }

            public static interface Callback_getServiceMgr extends com.sun.jna.Callback
            {
                public String invoke(ITraceServiceConnection self);
            }

            public static interface Callback_getServiceName extends com.sun.jna.Callback
            {
                public String invoke(ITraceServiceConnection self);
            }

            public VTable(com.sun.jna.Pointer pointer)
            {
                super(pointer);
            }

            public VTable(final ITraceServiceConnectionIntf obj)
            {
                super(obj);

                getServiceID = new Callback_getServiceID() {
                    @Override
                    public com.sun.jna.Pointer invoke(ITraceServiceConnection self)
                    {
                        return obj.getServiceID();
                    }
                };

                getServiceMgr = new Callback_getServiceMgr() {
                    @Override
                    public String invoke(ITraceServiceConnection self)
                    {
                        return obj.getServiceMgr();
                    }
                };

                getServiceName = new Callback_getServiceName() {
                    @Override
                    public String invoke(ITraceServiceConnection self)
                    {
                        return obj.getServiceName();
                    }
                };
            }

            public VTable()
            {
            }

            public Callback_getServiceID getServiceID;
            public Callback_getServiceMgr getServiceMgr;
            public Callback_getServiceName getServiceName;

            @Override
            protected java.util.List<String> getFieldOrder()
            {
                java.util.List<String> fields = super.getFieldOrder();
                fields.addAll(java.util.Arrays.asList("getServiceID", "getServiceMgr", "getServiceName"));
                return fields;
            }
        }

        public ITraceServiceConnection()
        {
        }

        public ITraceServiceConnection(ITraceServiceConnectionIntf obj)
        {
            vTable = new VTable(obj);
            vTable.write();
            cloopVTable = vTable.getPointer();
            write();
        }

        @Override
        protected VTable createVTable()
        {
            return new VTable(cloopVTable);
        }

        public com.sun.jna.Pointer getServiceID()
        {
            VTable vTable = getVTable();
            com.sun.jna.Pointer result = vTable.getServiceID.invoke(this);
            return result;
        }

        public String getServiceMgr()
        {
            VTable vTable = getVTable();
            String result = vTable.getServiceMgr.invoke(this);
            return result;
        }

        public String getServiceName()
        {
            VTable vTable = getVTable();
            String result = vTable.getServiceName.invoke(this);
            return result;
        }
    }

    public static class ITraceStatusVector extends IVersioned implements ITraceStatusVectorIntf
    {
        public static class VTable extends IVersioned.VTable
        {
            public static interface Callback_hasError extends com.sun.jna.Callback
            {
                public boolean invoke(ITraceStatusVector self);
            }

            public static interface Callback_hasWarning extends com.sun.jna.Callback
            {
                public boolean invoke(ITraceStatusVector self);
            }

            public static interface Callback_getStatus extends com.sun.jna.Callback
            {
                public IStatus invoke(ITraceStatusVector self);
            }

            public static interface Callback_getText extends com.sun.jna.Callback
            {
                public String invoke(ITraceStatusVector self);
            }

            public VTable(com.sun.jna.Pointer pointer)
            {
                super(pointer);
            }

            public VTable(final ITraceStatusVectorIntf obj)
            {
                super(obj);

                hasError = new Callback_hasError() {
                    @Override
                    public boolean invoke(ITraceStatusVector self)
                    {
                        return obj.hasError();
                    }
                };

                hasWarning = new Callback_hasWarning() {
                    @Override
                    public boolean invoke(ITraceStatusVector self)
                    {
                        return obj.hasWarning();
                    }
                };

                getStatus = new Callback_getStatus() {
                    @Override
                    public IStatus invoke(ITraceStatusVector self)
                    {
                        return obj.getStatus();
                    }
                };

                getText = new Callback_getText() {
                    @Override
                    public String invoke(ITraceStatusVector self)
                    {
                        return obj.getText();
                    }
                };
            }

            public VTable()
            {
            }

            public Callback_hasError hasError;
            public Callback_hasWarning hasWarning;
            public Callback_getStatus getStatus;
            public Callback_getText getText;

            @Override
            protected java.util.List<String> getFieldOrder()
            {
                java.util.List<String> fields = super.getFieldOrder();
                fields.addAll(java.util.Arrays.asList("hasError", "hasWarning", "getStatus", "getText"));
                return fields;
            }
        }

        public ITraceStatusVector()
        {
        }

        public ITraceStatusVector(ITraceStatusVectorIntf obj)
        {
            vTable = new VTable(obj);
            vTable.write();
            cloopVTable = vTable.getPointer();
            write();
        }

        @Override
        protected VTable createVTable()
        {
            return new VTable(cloopVTable);
        }

        public boolean hasError()
        {
            VTable vTable = getVTable();
            boolean result = vTable.hasError.invoke(this);
            return result;
        }

        public boolean hasWarning()
        {
            VTable vTable = getVTable();
            boolean result = vTable.hasWarning.invoke(this);
            return result;
        }

        public IStatus getStatus()
        {
            VTable vTable = getVTable();
            IStatus result = vTable.getStatus.invoke(this);
            return result;
        }

        public String getText()
        {
            VTable vTable = getVTable();
            String result = vTable.getText.invoke(this);
            return result;
        }
    }

    public static class ITraceSweepInfo extends IVersioned implements ITraceSweepInfoIntf
    {
        public static class VTable extends IVersioned.VTable
        {
            public static interface Callback_getOIT extends com.sun.jna.Callback
            {
                public long invoke(ITraceSweepInfo self);
            }

            public static interface Callback_getOST extends com.sun.jna.Callback
            {
                public long invoke(ITraceSweepInfo self);
            }

            public static interface Callback_getOAT extends com.sun.jna.Callback
            {
                public long invoke(ITraceSweepInfo self);
            }

            public static interface Callback_getNext extends com.sun.jna.Callback
            {
                public long invoke(ITraceSweepInfo self);
            }

            public static interface Callback_getPerf extends com.sun.jna.Callback
            {
                public com.sun.jna.Pointer invoke(ITraceSweepInfo self);
            }

            public VTable(com.sun.jna.Pointer pointer)
            {
                super(pointer);
            }

            public VTable(final ITraceSweepInfoIntf obj)
            {
                super(obj);

                getOIT = new Callback_getOIT() {
                    @Override
                    public long invoke(ITraceSweepInfo self)
                    {
                        return obj.getOIT();
                    }
                };

                getOST = new Callback_getOST() {
                    @Override
                    public long invoke(ITraceSweepInfo self)
                    {
                        return obj.getOST();
                    }
                };

                getOAT = new Callback_getOAT() {
                    @Override
                    public long invoke(ITraceSweepInfo self)
                    {
                        return obj.getOAT();
                    }
                };

                getNext = new Callback_getNext() {
                    @Override
                    public long invoke(ITraceSweepInfo self)
                    {
                        return obj.getNext();
                    }
                };

                getPerf = new Callback_getPerf() {
                    @Override
                    public com.sun.jna.Pointer invoke(ITraceSweepInfo self)
                    {
                        return obj.getPerf();
                    }
                };
            }

            public VTable()
            {
            }

            public Callback_getOIT getOIT;
            public Callback_getOST getOST;
            public Callback_getOAT getOAT;
            public Callback_getNext getNext;
            public Callback_getPerf getPerf;

            @Override
            protected java.util.List<String> getFieldOrder()
            {
                java.util.List<String> fields = super.getFieldOrder();
                fields.addAll(java.util.Arrays.asList("getOIT", "getOST", "getOAT", "getNext", "getPerf"));
                return fields;
            }
        }

        public ITraceSweepInfo()
        {
        }

        public ITraceSweepInfo(ITraceSweepInfoIntf obj)
        {
            vTable = new VTable(obj);
            vTable.write();
            cloopVTable = vTable.getPointer();
            write();
        }

        @Override
        protected VTable createVTable()
        {
            return new VTable(cloopVTable);
        }

        public long getOIT()
        {
            VTable vTable = getVTable();
            long result = vTable.getOIT.invoke(this);
            return result;
        }

        public long getOST()
        {
            VTable vTable = getVTable();
            long result = vTable.getOST.invoke(this);
            return result;
        }

        public long getOAT()
        {
            VTable vTable = getVTable();
            long result = vTable.getOAT.invoke(this);
            return result;
        }

        public long getNext()
        {
            VTable vTable = getVTable();
            long result = vTable.getNext.invoke(this);
            return result;
        }

        public com.sun.jna.Pointer getPerf()
        {
            VTable vTable = getVTable();
            com.sun.jna.Pointer result = vTable.getPerf.invoke(this);
            return result;
        }
    }

    public static class ITraceLogWriter extends IReferenceCounted implements ITraceLogWriterIntf
    {
        public static class VTable extends IReferenceCounted.VTable
        {
            public static interface Callback_write extends com.sun.jna.Callback
            {
                public int invoke(ITraceLogWriter self, com.sun.jna.Pointer buf, int size);
            }

            public VTable(com.sun.jna.Pointer pointer)
            {
                super(pointer);
            }

            public VTable(final ITraceLogWriterIntf obj)
            {
                super(obj);

                write = new Callback_write() {
                    @Override
                    public int invoke(ITraceLogWriter self, com.sun.jna.Pointer buf, int size)
                    {
                        return obj.write(buf, size);
                    }
                };
            }

            public VTable()
            {
            }

            public Callback_write write;

            @Override
            protected java.util.List<String> getFieldOrder()
            {
                java.util.List<String> fields = super.getFieldOrder();
                fields.addAll(java.util.Arrays.asList("write"));
                return fields;
            }
        }

        public ITraceLogWriter()
        {
        }

        public ITraceLogWriter(ITraceLogWriterIntf obj)
        {
            vTable = new VTable(obj);
            vTable.write();
            cloopVTable = vTable.getPointer();
            write();
        }

        @Override
        protected VTable createVTable()
        {
            return new VTable(cloopVTable);
        }

        public int write(com.sun.jna.Pointer buf, int size)
        {
            VTable vTable = getVTable();
            int result = vTable.write.invoke(this, buf, size);
            return result;
        }
    }

    public static class ITraceInitInfo extends IVersioned implements ITraceInitInfoIntf
    {
        public static class VTable extends IVersioned.VTable
        {
            public static interface Callback_getConfigText extends com.sun.jna.Callback
            {
                public String invoke(ITraceInitInfo self);
            }

            public static interface Callback_getTraceSessionID extends com.sun.jna.Callback
            {
                public int invoke(ITraceInitInfo self);
            }

            public static interface Callback_getTraceSessionName extends com.sun.jna.Callback
            {
                public String invoke(ITraceInitInfo self);
            }

            public static interface Callback_getTraceSessionFlags extends com.sun.jna.Callback
            {
                public int invoke(ITraceInitInfo self);
            }

            public static interface Callback_getFirebirdRootDirectory extends com.sun.jna.Callback
            {
                public String invoke(ITraceInitInfo self);
            }

            public static interface Callback_getDatabaseName extends com.sun.jna.Callback
            {
                public String invoke(ITraceInitInfo self);
            }

            public static interface Callback_getConnection extends com.sun.jna.Callback
            {
                public ITraceDatabaseConnection invoke(ITraceInitInfo self);
            }

            public static interface Callback_getService extends com.sun.jna.Callback
            {
                public ITraceServiceConnection invoke(ITraceInitInfo self);
            }

            public static interface Callback_getLogWriter extends com.sun.jna.Callback
            {
                public ITraceLogWriter invoke(ITraceInitInfo self);
            }

            public VTable(com.sun.jna.Pointer pointer)
            {
                super(pointer);
            }

            public VTable(final ITraceInitInfoIntf obj)
            {
                super(obj);

                getConfigText = new Callback_getConfigText() {
                    @Override
                    public String invoke(ITraceInitInfo self)
                    {
                        return obj.getConfigText();
                    }
                };

                getTraceSessionID = new Callback_getTraceSessionID() {
                    @Override
                    public int invoke(ITraceInitInfo self)
                    {
                        return obj.getTraceSessionID();
                    }
                };

                getTraceSessionName = new Callback_getTraceSessionName() {
                    @Override
                    public String invoke(ITraceInitInfo self)
                    {
                        return obj.getTraceSessionName();
                    }
                };

                getTraceSessionFlags = new Callback_getTraceSessionFlags() {
                    @Override
                    public int invoke(ITraceInitInfo self)
                    {
                        return obj.getTraceSessionFlags();
                    }
                };

                getFirebirdRootDirectory = new Callback_getFirebirdRootDirectory() {
                    @Override
                    public String invoke(ITraceInitInfo self)
                    {
                        return obj.getFirebirdRootDirectory();
                    }
                };

                getDatabaseName = new Callback_getDatabaseName() {
                    @Override
                    public String invoke(ITraceInitInfo self)
                    {
                        return obj.getDatabaseName();
                    }
                };

                getConnection = new Callback_getConnection() {
                    @Override
                    public ITraceDatabaseConnection invoke(ITraceInitInfo self)
                    {
                        return obj.getConnection();
                    }
                };

                getService = new Callback_getService() {
                    @Override
                    public ITraceServiceConnection invoke(ITraceInitInfo self)
                    {
                        return obj.getService();
                    }
                };

                getLogWriter = new Callback_getLogWriter() {
                    @Override
                    public ITraceLogWriter invoke(ITraceInitInfo self)
                    {
                        return obj.getLogWriter();
                    }
                };
            }

            public VTable()
            {
            }

            public Callback_getConfigText getConfigText;
            public Callback_getTraceSessionID getTraceSessionID;
            public Callback_getTraceSessionName getTraceSessionName;
            public Callback_getTraceSessionFlags getTraceSessionFlags;
            public Callback_getFirebirdRootDirectory getFirebirdRootDirectory;
            public Callback_getDatabaseName getDatabaseName;
            public Callback_getConnection getConnection;
            public Callback_getService getService;
            public Callback_getLogWriter getLogWriter;

            @Override
            protected java.util.List<String> getFieldOrder()
            {
                java.util.List<String> fields = super.getFieldOrder();
                fields.addAll(java.util.Arrays.asList("getConfigText", "getTraceSessionID", "getTraceSessionName", "getTraceSessionFlags", "getFirebirdRootDirectory", "getDatabaseName", "getConnection", "getService", "getLogWriter"));
                return fields;
            }
        }

        public ITraceInitInfo()
        {
        }

        public ITraceInitInfo(ITraceInitInfoIntf obj)
        {
            vTable = new VTable(obj);
            vTable.write();
            cloopVTable = vTable.getPointer();
            write();
        }

        @Override
        protected VTable createVTable()
        {
            return new VTable(cloopVTable);
        }

        public String getConfigText()
        {
            VTable vTable = getVTable();
            String result = vTable.getConfigText.invoke(this);
            return result;
        }

        public int getTraceSessionID()
        {
            VTable vTable = getVTable();
            int result = vTable.getTraceSessionID.invoke(this);
            return result;
        }

        public String getTraceSessionName()
        {
            VTable vTable = getVTable();
            String result = vTable.getTraceSessionName.invoke(this);
            return result;
        }

        public int getTraceSessionFlags()
        {
            VTable vTable = getVTable();
            int result = vTable.getTraceSessionFlags.invoke(this);
            return result;
        }

        public String getFirebirdRootDirectory()
        {
            VTable vTable = getVTable();
            String result = vTable.getFirebirdRootDirectory.invoke(this);
            return result;
        }

        public String getDatabaseName()
        {
            VTable vTable = getVTable();
            String result = vTable.getDatabaseName.invoke(this);
            return result;
        }

        public ITraceDatabaseConnection getConnection()
        {
            VTable vTable = getVTable();
            ITraceDatabaseConnection result = vTable.getConnection.invoke(this);
            return result;
        }

        public ITraceServiceConnection getService()
        {
            VTable vTable = getVTable();
            ITraceServiceConnection result = vTable.getService.invoke(this);
            return result;
        }

        public ITraceLogWriter getLogWriter()
        {
            VTable vTable = getVTable();
            ITraceLogWriter result = vTable.getLogWriter.invoke(this);
            return result;
        }
    }

    public static class ITracePlugin extends IReferenceCounted implements ITracePluginIntf
    {
        public static class VTable extends IReferenceCounted.VTable
        {
            public static interface Callback_trace_get_error extends com.sun.jna.Callback
            {
                public String invoke(ITracePlugin self);
            }

            public static interface Callback_trace_attach extends com.sun.jna.Callback
            {
                public boolean invoke(ITracePlugin self, ITraceDatabaseConnection connection, boolean create_db, int dpb_length, byte[] dpb, int att_result);
            }

            public static interface Callback_trace_detach extends com.sun.jna.Callback
            {
                public boolean invoke(ITracePlugin self, ITraceDatabaseConnection connection, boolean drop_db);
            }

            public static interface Callback_trace_transaction_start extends com.sun.jna.Callback
            {
                public boolean invoke(ITracePlugin self, ITraceDatabaseConnection connection, ITraceTransaction transaction, int tpb_length, byte[] tpb, int tra_result);
            }

            public static interface Callback_trace_transaction_end extends com.sun.jna.Callback
            {
                public boolean invoke(ITracePlugin self, ITraceDatabaseConnection connection, ITraceTransaction transaction, boolean commit, boolean retain_context, int tra_result);
            }

            public static interface Callback_trace_proc_execute extends com.sun.jna.Callback
            {
                public boolean invoke(ITracePlugin self, ITraceDatabaseConnection connection, ITraceTransaction transaction, ITraceProcedure procedure, boolean started, int proc_result);
            }

            public static interface Callback_trace_trigger_execute extends com.sun.jna.Callback
            {
                public boolean invoke(ITracePlugin self, ITraceDatabaseConnection connection, ITraceTransaction transaction, ITraceTrigger trigger, boolean started, int trig_result);
            }

            public static interface Callback_trace_set_context extends com.sun.jna.Callback
            {
                public boolean invoke(ITracePlugin self, ITraceDatabaseConnection connection, ITraceTransaction transaction, ITraceContextVariable variable);
            }

            public static interface Callback_trace_dsql_prepare extends com.sun.jna.Callback
            {
                public boolean invoke(ITracePlugin self, ITraceDatabaseConnection connection, ITraceTransaction transaction, ITraceSQLStatement statement, long time_millis, int req_result);
            }

            public static interface Callback_trace_dsql_free extends com.sun.jna.Callback
            {
                public boolean invoke(ITracePlugin self, ITraceDatabaseConnection connection, ITraceSQLStatement statement, int option);
            }

            public static interface Callback_trace_dsql_execute extends com.sun.jna.Callback
            {
                public boolean invoke(ITracePlugin self, ITraceDatabaseConnection connection, ITraceTransaction transaction, ITraceSQLStatement statement, boolean started, int req_result);
            }

            public static interface Callback_trace_blr_compile extends com.sun.jna.Callback
            {
                public boolean invoke(ITracePlugin self, ITraceDatabaseConnection connection, ITraceTransaction transaction, ITraceBLRStatement statement, long time_millis, int req_result);
            }

            public static interface Callback_trace_blr_execute extends com.sun.jna.Callback
            {
                public boolean invoke(ITracePlugin self, ITraceDatabaseConnection connection, ITraceTransaction transaction, ITraceBLRStatement statement, int req_result);
            }

            public static interface Callback_trace_dyn_execute extends com.sun.jna.Callback
            {
                public boolean invoke(ITracePlugin self, ITraceDatabaseConnection connection, ITraceTransaction transaction, ITraceDYNRequest request, long time_millis, int req_result);
            }

            public static interface Callback_trace_service_attach extends com.sun.jna.Callback
            {
                public boolean invoke(ITracePlugin self, ITraceServiceConnection service, int spb_length, byte[] spb, int att_result);
            }

            public static interface Callback_trace_service_start extends com.sun.jna.Callback
            {
                public boolean invoke(ITracePlugin self, ITraceServiceConnection service, int switches_length, String switches, int start_result);
            }

            public static interface Callback_trace_service_query extends com.sun.jna.Callback
            {
                public boolean invoke(ITracePlugin self, ITraceServiceConnection service, int send_item_length, byte[] send_items, int recv_item_length, byte[] recv_items, int query_result);
            }

            public static interface Callback_trace_service_detach extends com.sun.jna.Callback
            {
                public boolean invoke(ITracePlugin self, ITraceServiceConnection service, int detach_result);
            }

            public static interface Callback_trace_event_error extends com.sun.jna.Callback
            {
                public boolean invoke(ITracePlugin self, ITraceConnection connection, ITraceStatusVector status, String function);
            }

            public static interface Callback_trace_event_sweep extends com.sun.jna.Callback
            {
                public boolean invoke(ITracePlugin self, ITraceDatabaseConnection connection, ITraceSweepInfo sweep, int sweep_state);
            }

            public static interface Callback_trace_func_execute extends com.sun.jna.Callback
            {
                public boolean invoke(ITracePlugin self, ITraceDatabaseConnection connection, ITraceTransaction transaction, ITraceFunction function, boolean started, int func_result);
            }

            public static interface Callback_trace_privilege_change extends com.sun.jna.Callback
            {
                public boolean invoke(ITracePlugin self, ITraceDatabaseConnection connection, ITraceTransaction transaction, String executor, String grantor, boolean is_grant, String object_name, String field_name, String user_name, String privileges, int options, int change_result);
            }

            public VTable(com.sun.jna.Pointer pointer)
            {
                super(pointer);
            }

            public VTable(final ITracePluginIntf obj)
            {
                super(obj);

                trace_get_error = new Callback_trace_get_error() {
                    @Override
                    public String invoke(ITracePlugin self)
                    {
                        return obj.trace_get_error();
                    }
                };

                trace_attach = new Callback_trace_attach() {
                    @Override
                    public boolean invoke(ITracePlugin self, ITraceDatabaseConnection connection, boolean create_db, int dpb_length, byte[] dpb, int att_result)
                    {
                        return obj.trace_attach(connection, create_db, dpb_length, dpb, att_result);
                    }
                };

                trace_detach = new Callback_trace_detach() {
                    @Override
                    public boolean invoke(ITracePlugin self, ITraceDatabaseConnection connection, boolean drop_db)
                    {
                        return obj.trace_detach(connection, drop_db);
                    }
                };

                trace_transaction_start = new Callback_trace_transaction_start() {
                    @Override
                    public boolean invoke(ITracePlugin self, ITraceDatabaseConnection connection, ITraceTransaction transaction, int tpb_length, byte[] tpb, int tra_result)
                    {
                        return obj.trace_transaction_start(connection, transaction, tpb_length, tpb, tra_result);
                    }
                };

                trace_transaction_end = new Callback_trace_transaction_end() {
                    @Override
                    public boolean invoke(ITracePlugin self, ITraceDatabaseConnection connection, ITraceTransaction transaction, boolean commit, boolean retain_context, int tra_result)
                    {
                        return obj.trace_transaction_end(connection, transaction, commit, retain_context, tra_result);
                    }
                };

                trace_proc_execute = new Callback_trace_proc_execute() {
                    @Override
                    public boolean invoke(ITracePlugin self, ITraceDatabaseConnection connection, ITraceTransaction transaction, ITraceProcedure procedure, boolean started, int proc_result)
                    {
                        return obj.trace_proc_execute(connection, transaction, procedure, started, proc_result);
                    }
                };

                trace_trigger_execute = new Callback_trace_trigger_execute() {
                    @Override
                    public boolean invoke(ITracePlugin self, ITraceDatabaseConnection connection, ITraceTransaction transaction, ITraceTrigger trigger, boolean started, int trig_result)
                    {
                        return obj.trace_trigger_execute(connection, transaction, trigger, started, trig_result);
                    }
                };

                trace_set_context = new Callback_trace_set_context() {
                    @Override
                    public boolean invoke(ITracePlugin self, ITraceDatabaseConnection connection, ITraceTransaction transaction, ITraceContextVariable variable)
                    {
                        return obj.trace_set_context(connection, transaction, variable);
                    }
                };

                trace_dsql_prepare = new Callback_trace_dsql_prepare() {
                    @Override
                    public boolean invoke(ITracePlugin self, ITraceDatabaseConnection connection, ITraceTransaction transaction, ITraceSQLStatement statement, long time_millis, int req_result)
                    {
                        return obj.trace_dsql_prepare(connection, transaction, statement, time_millis, req_result);
                    }
                };

                trace_dsql_free = new Callback_trace_dsql_free() {
                    @Override
                    public boolean invoke(ITracePlugin self, ITraceDatabaseConnection connection, ITraceSQLStatement statement, int option)
                    {
                        return obj.trace_dsql_free(connection, statement, option);
                    }
                };

                trace_dsql_execute = new Callback_trace_dsql_execute() {
                    @Override
                    public boolean invoke(ITracePlugin self, ITraceDatabaseConnection connection, ITraceTransaction transaction, ITraceSQLStatement statement, boolean started, int req_result)
                    {
                        return obj.trace_dsql_execute(connection, transaction, statement, started, req_result);
                    }
                };

                trace_blr_compile = new Callback_trace_blr_compile() {
                    @Override
                    public boolean invoke(ITracePlugin self, ITraceDatabaseConnection connection, ITraceTransaction transaction, ITraceBLRStatement statement, long time_millis, int req_result)
                    {
                        return obj.trace_blr_compile(connection, transaction, statement, time_millis, req_result);
                    }
                };

                trace_blr_execute = new Callback_trace_blr_execute() {
                    @Override
                    public boolean invoke(ITracePlugin self, ITraceDatabaseConnection connection, ITraceTransaction transaction, ITraceBLRStatement statement, int req_result)
                    {
                        return obj.trace_blr_execute(connection, transaction, statement, req_result);
                    }
                };

                trace_dyn_execute = new Callback_trace_dyn_execute() {
                    @Override
                    public boolean invoke(ITracePlugin self, ITraceDatabaseConnection connection, ITraceTransaction transaction, ITraceDYNRequest request, long time_millis, int req_result)
                    {
                        return obj.trace_dyn_execute(connection, transaction, request, time_millis, req_result);
                    }
                };

                trace_service_attach = new Callback_trace_service_attach() {
                    @Override
                    public boolean invoke(ITracePlugin self, ITraceServiceConnection service, int spb_length, byte[] spb, int att_result)
                    {
                        return obj.trace_service_attach(service, spb_length, spb, att_result);
                    }
                };

                trace_service_start = new Callback_trace_service_start() {
                    @Override
                    public boolean invoke(ITracePlugin self, ITraceServiceConnection service, int switches_length, String switches, int start_result)
                    {
                        return obj.trace_service_start(service, switches_length, switches, start_result);
                    }
                };

                trace_service_query = new Callback_trace_service_query() {
                    @Override
                    public boolean invoke(ITracePlugin self, ITraceServiceConnection service, int send_item_length, byte[] send_items, int recv_item_length, byte[] recv_items, int query_result)
                    {
                        return obj.trace_service_query(service, send_item_length, send_items, recv_item_length, recv_items, query_result);
                    }
                };

                trace_service_detach = new Callback_trace_service_detach() {
                    @Override
                    public boolean invoke(ITracePlugin self, ITraceServiceConnection service, int detach_result)
                    {
                        return obj.trace_service_detach(service, detach_result);
                    }
                };

                trace_event_error = new Callback_trace_event_error() {
                    @Override
                    public boolean invoke(ITracePlugin self, ITraceConnection connection, ITraceStatusVector status, String function)
                    {
                        return obj.trace_event_error(connection, status, function);
                    }
                };

                trace_event_sweep = new Callback_trace_event_sweep() {
                    @Override
                    public boolean invoke(ITracePlugin self, ITraceDatabaseConnection connection, ITraceSweepInfo sweep, int sweep_state)
                    {
                        return obj.trace_event_sweep(connection, sweep, sweep_state);
                    }
                };

                trace_func_execute = new Callback_trace_func_execute() {
                    @Override
                    public boolean invoke(ITracePlugin self, ITraceDatabaseConnection connection, ITraceTransaction transaction, ITraceFunction function, boolean started, int func_result)
                    {
                        return obj.trace_func_execute(connection, transaction, function, started, func_result);
                    }
                };

                trace_privilege_change = new Callback_trace_privilege_change() {
                    @Override
                    public boolean invoke(ITracePlugin self, ITraceDatabaseConnection connection, ITraceTransaction transaction, String executor, String grantor, boolean is_grant, String object_name, String field_name, String user_name, String privileges, int options, int change_result)
                    {
                        return obj.trace_privilege_change(connection, transaction, executor, grantor, is_grant, object_name, field_name, user_name, privileges, options, change_result);
                    }
                };
            }

            public VTable()
            {
            }

            public Callback_trace_get_error trace_get_error;
            public Callback_trace_attach trace_attach;
            public Callback_trace_detach trace_detach;
            public Callback_trace_transaction_start trace_transaction_start;
            public Callback_trace_transaction_end trace_transaction_end;
            public Callback_trace_proc_execute trace_proc_execute;
            public Callback_trace_trigger_execute trace_trigger_execute;
            public Callback_trace_set_context trace_set_context;
            public Callback_trace_dsql_prepare trace_dsql_prepare;
            public Callback_trace_dsql_free trace_dsql_free;
            public Callback_trace_dsql_execute trace_dsql_execute;
            public Callback_trace_blr_compile trace_blr_compile;
            public Callback_trace_blr_execute trace_blr_execute;
            public Callback_trace_dyn_execute trace_dyn_execute;
            public Callback_trace_service_attach trace_service_attach;
            public Callback_trace_service_start trace_service_start;
            public Callback_trace_service_query trace_service_query;
            public Callback_trace_service_detach trace_service_detach;
            public Callback_trace_event_error trace_event_error;
            public Callback_trace_event_sweep trace_event_sweep;
            public Callback_trace_func_execute trace_func_execute;
            public Callback_trace_privilege_change trace_privilege_change;

            @Override
            protected java.util.List<String> getFieldOrder()
            {
                java.util.List<String> fields = super.getFieldOrder();
                fields.addAll(java.util.Arrays.asList("trace_get_error", "trace_attach", "trace_detach", "trace_transaction_start", "trace_transaction_end", "trace_proc_execute", "trace_trigger_execute", "trace_set_context", "trace_dsql_prepare", "trace_dsql_free", "trace_dsql_execute", "trace_blr_compile", "trace_blr_execute", "trace_dyn_execute", "trace_service_attach", "trace_service_start", "trace_service_query", "trace_service_detach", "trace_event_error", "trace_event_sweep", "trace_func_execute", "trace_privilege_change"));
                return fields;
            }
        }

        public ITracePlugin()
        {
        }

        public ITracePlugin(ITracePluginIntf obj)
        {
            vTable = new VTable(obj);
            vTable.write();
            cloopVTable = vTable.getPointer();
            write();
        }

        @Override
        protected VTable createVTable()
        {
            return new VTable(cloopVTable);
        }

        public String trace_get_error()
        {
            VTable vTable = getVTable();
            String result = vTable.trace_get_error.invoke(this);
            return result;
        }

        public boolean trace_attach(ITraceDatabaseConnection connection, boolean create_db, int dpb_length, byte[] dpb, int att_result)
        {
            VTable vTable = getVTable();
            boolean result = vTable.trace_attach.invoke(this, connection, create_db, dpb_length, dpb, att_result);
            return result;
        }

        public boolean trace_detach(ITraceDatabaseConnection connection, boolean drop_db)
        {
            VTable vTable = getVTable();
            boolean result = vTable.trace_detach.invoke(this, connection, drop_db);
            return result;
        }

        public boolean trace_transaction_start(ITraceDatabaseConnection connection, ITraceTransaction transaction, int tpb_length, byte[] tpb, int tra_result)
        {
            VTable vTable = getVTable();
            boolean result = vTable.trace_transaction_start.invoke(this, connection, transaction, tpb_length, tpb, tra_result);
            return result;
        }

        public boolean trace_transaction_end(ITraceDatabaseConnection connection, ITraceTransaction transaction, boolean commit, boolean retain_context, int tra_result)
        {
            VTable vTable = getVTable();
            boolean result = vTable.trace_transaction_end.invoke(this, connection, transaction, commit, retain_context, tra_result);
            return result;
        }

        public boolean trace_proc_execute(ITraceDatabaseConnection connection, ITraceTransaction transaction, ITraceProcedure procedure, boolean started, int proc_result)
        {
            VTable vTable = getVTable();
            boolean result = vTable.trace_proc_execute.invoke(this, connection, transaction, procedure, started, proc_result);
            return result;
        }

        public boolean trace_trigger_execute(ITraceDatabaseConnection connection, ITraceTransaction transaction, ITraceTrigger trigger, boolean started, int trig_result)
        {
            VTable vTable = getVTable();
            boolean result = vTable.trace_trigger_execute.invoke(this, connection, transaction, trigger, started, trig_result);
            return result;
        }

        public boolean trace_set_context(ITraceDatabaseConnection connection, ITraceTransaction transaction, ITraceContextVariable variable)
        {
            VTable vTable = getVTable();
            boolean result = vTable.trace_set_context.invoke(this, connection, transaction, variable);
            return result;
        }

        public boolean trace_dsql_prepare(ITraceDatabaseConnection connection, ITraceTransaction transaction, ITraceSQLStatement statement, long time_millis, int req_result)
        {
            VTable vTable = getVTable();
            boolean result = vTable.trace_dsql_prepare.invoke(this, connection, transaction, statement, time_millis, req_result);
            return result;
        }

        public boolean trace_dsql_free(ITraceDatabaseConnection connection, ITraceSQLStatement statement, int option)
        {
            VTable vTable = getVTable();
            boolean result = vTable.trace_dsql_free.invoke(this, connection, statement, option);
            return result;
        }

        public boolean trace_dsql_execute(ITraceDatabaseConnection connection, ITraceTransaction transaction, ITraceSQLStatement statement, boolean started, int req_result)
        {
            VTable vTable = getVTable();
            boolean result = vTable.trace_dsql_execute.invoke(this, connection, transaction, statement, started, req_result);
            return result;
        }

        public boolean trace_blr_compile(ITraceDatabaseConnection connection, ITraceTransaction transaction, ITraceBLRStatement statement, long time_millis, int req_result)
        {
            VTable vTable = getVTable();
            boolean result = vTable.trace_blr_compile.invoke(this, connection, transaction, statement, time_millis, req_result);
            return result;
        }

        public boolean trace_blr_execute(ITraceDatabaseConnection connection, ITraceTransaction transaction, ITraceBLRStatement statement, int req_result)
        {
            VTable vTable = getVTable();
            boolean result = vTable.trace_blr_execute.invoke(this, connection, transaction, statement, req_result);
            return result;
        }

        public boolean trace_dyn_execute(ITraceDatabaseConnection connection, ITraceTransaction transaction, ITraceDYNRequest request, long time_millis, int req_result)
        {
            VTable vTable = getVTable();
            boolean result = vTable.trace_dyn_execute.invoke(this, connection, transaction, request, time_millis, req_result);
            return result;
        }

        public boolean trace_service_attach(ITraceServiceConnection service, int spb_length, byte[] spb, int att_result)
        {
            VTable vTable = getVTable();
            boolean result = vTable.trace_service_attach.invoke(this, service, spb_length, spb, att_result);
            return result;
        }

        public boolean trace_service_start(ITraceServiceConnection service, int switches_length, String switches, int start_result)
        {
            VTable vTable = getVTable();
            boolean result = vTable.trace_service_start.invoke(this, service, switches_length, switches, start_result);
            return result;
        }

        public boolean trace_service_query(ITraceServiceConnection service, int send_item_length, byte[] send_items, int recv_item_length, byte[] recv_items, int query_result)
        {
            VTable vTable = getVTable();
            boolean result = vTable.trace_service_query.invoke(this, service, send_item_length, send_items, recv_item_length, recv_items, query_result);
            return result;
        }

        public boolean trace_service_detach(ITraceServiceConnection service, int detach_result)
        {
            VTable vTable = getVTable();
            boolean result = vTable.trace_service_detach.invoke(this, service, detach_result);
            return result;
        }

        public boolean trace_event_error(ITraceConnection connection, ITraceStatusVector status, String function)
        {
            VTable vTable = getVTable();
            boolean result = vTable.trace_event_error.invoke(this, connection, status, function);
            return result;
        }

        public boolean trace_event_sweep(ITraceDatabaseConnection connection, ITraceSweepInfo sweep, int sweep_state)
        {
            VTable vTable = getVTable();
            boolean result = vTable.trace_event_sweep.invoke(this, connection, sweep, sweep_state);
            return result;
        }

        public boolean trace_func_execute(ITraceDatabaseConnection connection, ITraceTransaction transaction, ITraceFunction function, boolean started, int func_result)
        {
            VTable vTable = getVTable();
            boolean result = vTable.trace_func_execute.invoke(this, connection, transaction, function, started, func_result);
            return result;
        }

        public boolean trace_privilege_change(ITraceDatabaseConnection connection, ITraceTransaction transaction, String executor, String grantor, boolean is_grant, String object_name, String field_name, String user_name, String privileges, int options, int change_result)
        {
            VTable vTable = getVTable();
            boolean result = vTable.trace_privilege_change.invoke(this, connection, transaction, executor, grantor, is_grant, object_name, field_name, user_name, privileges, options, change_result);
            return result;
        }
    }

    public static class ITraceFactory extends IPluginBase implements ITraceFactoryIntf
    {
        public static class VTable extends IPluginBase.VTable
        {
            public static interface Callback_trace_needs extends com.sun.jna.Callback
            {
                public long invoke(ITraceFactory self);
            }

            public static interface Callback_trace_create extends com.sun.jna.Callback
            {
                public ITracePlugin invoke(ITraceFactory self, IStatus status, ITraceInitInfo init_info);
            }

            public VTable(com.sun.jna.Pointer pointer)
            {
                super(pointer);
            }

            public VTable(final ITraceFactoryIntf obj)
            {
                super(obj);

                trace_needs = new Callback_trace_needs() {
                    @Override
                    public long invoke(ITraceFactory self)
                    {
                        return obj.trace_needs();
                    }
                };

                trace_create = new Callback_trace_create() {
                    @Override
                    public ITracePlugin invoke(ITraceFactory self, IStatus status, ITraceInitInfo init_info)
                    {
                        try
                        {
                            return obj.trace_create(status, init_info);
                        }
                        catch (Throwable t)
                        {
                            FbException.catchException(status, t);
                            return null;
                        }
                    }
                };
            }

            public VTable()
            {
            }

            public Callback_trace_needs trace_needs;
            public Callback_trace_create trace_create;

            @Override
            protected java.util.List<String> getFieldOrder()
            {
                java.util.List<String> fields = super.getFieldOrder();
                fields.addAll(java.util.Arrays.asList("trace_needs", "trace_create"));
                return fields;
            }
        }

        public ITraceFactory()
        {
        }

        public ITraceFactory(ITraceFactoryIntf obj)
        {
            vTable = new VTable(obj);
            vTable.write();
            cloopVTable = vTable.getPointer();
            write();
        }

        @Override
        protected VTable createVTable()
        {
            return new VTable(cloopVTable);
        }

        public long trace_needs()
        {
            VTable vTable = getVTable();
            long result = vTable.trace_needs.invoke(this);
            return result;
        }

        public ITracePlugin trace_create(IStatus status, ITraceInitInfo init_info) throws FbException
        {
            VTable vTable = getVTable();
            ITracePlugin result = vTable.trace_create.invoke(this, status, init_info);
            FbException.checkException(status);
            return result;
        }
    }

    public static class IUdrFunctionFactory extends IDisposable implements IUdrFunctionFactoryIntf
    {
        public static class VTable extends IDisposable.VTable
        {
            public static interface Callback_setup extends com.sun.jna.Callback
            {
                public void invoke(IUdrFunctionFactory self, IStatus status, IExternalContext context, IRoutineMetadata metadata, IMetadataBuilder inBuilder, IMetadataBuilder outBuilder);
            }

            public static interface Callback_newItem extends com.sun.jna.Callback
            {
                public IExternalFunction invoke(IUdrFunctionFactory self, IStatus status, IExternalContext context, IRoutineMetadata metadata);
            }

            public VTable(com.sun.jna.Pointer pointer)
            {
                super(pointer);
            }

            public VTable(final IUdrFunctionFactoryIntf obj)
            {
                super(obj);

                setup = new Callback_setup() {
                    @Override
                    public void invoke(IUdrFunctionFactory self, IStatus status, IExternalContext context, IRoutineMetadata metadata, IMetadataBuilder inBuilder, IMetadataBuilder outBuilder)
                    {
                        try
                        {
                            obj.setup(status, context, metadata, inBuilder, outBuilder);
                        }
                        catch (Throwable t)
                        {
                            FbException.catchException(status, t);
                        }
                    }
                };

                newItem = new Callback_newItem() {
                    @Override
                    public IExternalFunction invoke(IUdrFunctionFactory self, IStatus status, IExternalContext context, IRoutineMetadata metadata)
                    {
                        try
                        {
                            return obj.newItem(status, context, metadata);
                        }
                        catch (Throwable t)
                        {
                            FbException.catchException(status, t);
                            return null;
                        }
                    }
                };
            }

            public VTable()
            {
            }

            public Callback_setup setup;
            public Callback_newItem newItem;

            @Override
            protected java.util.List<String> getFieldOrder()
            {
                java.util.List<String> fields = super.getFieldOrder();
                fields.addAll(java.util.Arrays.asList("setup", "newItem"));
                return fields;
            }
        }

        public IUdrFunctionFactory()
        {
        }

        public IUdrFunctionFactory(IUdrFunctionFactoryIntf obj)
        {
            vTable = new VTable(obj);
            vTable.write();
            cloopVTable = vTable.getPointer();
            write();
        }

        @Override
        protected VTable createVTable()
        {
            return new VTable(cloopVTable);
        }

        public void setup(IStatus status, IExternalContext context, IRoutineMetadata metadata, IMetadataBuilder inBuilder, IMetadataBuilder outBuilder) throws FbException
        {
            VTable vTable = getVTable();
            vTable.setup.invoke(this, status, context, metadata, inBuilder, outBuilder);
            FbException.checkException(status);
        }

        public IExternalFunction newItem(IStatus status, IExternalContext context, IRoutineMetadata metadata) throws FbException
        {
            VTable vTable = getVTable();
            IExternalFunction result = vTable.newItem.invoke(this, status, context, metadata);
            FbException.checkException(status);
            return result;
        }
    }

    public static class IUdrProcedureFactory extends IDisposable implements IUdrProcedureFactoryIntf
    {
        public static class VTable extends IDisposable.VTable
        {
            public static interface Callback_setup extends com.sun.jna.Callback
            {
                public void invoke(IUdrProcedureFactory self, IStatus status, IExternalContext context, IRoutineMetadata metadata, IMetadataBuilder inBuilder, IMetadataBuilder outBuilder);
            }

            public static interface Callback_newItem extends com.sun.jna.Callback
            {
                public IExternalProcedure invoke(IUdrProcedureFactory self, IStatus status, IExternalContext context, IRoutineMetadata metadata);
            }

            public VTable(com.sun.jna.Pointer pointer)
            {
                super(pointer);
            }

            public VTable(final IUdrProcedureFactoryIntf obj)
            {
                super(obj);

                setup = new Callback_setup() {
                    @Override
                    public void invoke(IUdrProcedureFactory self, IStatus status, IExternalContext context, IRoutineMetadata metadata, IMetadataBuilder inBuilder, IMetadataBuilder outBuilder)
                    {
                        try
                        {
                            obj.setup(status, context, metadata, inBuilder, outBuilder);
                        }
                        catch (Throwable t)
                        {
                            FbException.catchException(status, t);
                        }
                    }
                };

                newItem = new Callback_newItem() {
                    @Override
                    public IExternalProcedure invoke(IUdrProcedureFactory self, IStatus status, IExternalContext context, IRoutineMetadata metadata)
                    {
                        try
                        {
                            return obj.newItem(status, context, metadata);
                        }
                        catch (Throwable t)
                        {
                            FbException.catchException(status, t);
                            return null;
                        }
                    }
                };
            }

            public VTable()
            {
            }

            public Callback_setup setup;
            public Callback_newItem newItem;

            @Override
            protected java.util.List<String> getFieldOrder()
            {
                java.util.List<String> fields = super.getFieldOrder();
                fields.addAll(java.util.Arrays.asList("setup", "newItem"));
                return fields;
            }
        }

        public IUdrProcedureFactory()
        {
        }

        public IUdrProcedureFactory(IUdrProcedureFactoryIntf obj)
        {
            vTable = new VTable(obj);
            vTable.write();
            cloopVTable = vTable.getPointer();
            write();
        }

        @Override
        protected VTable createVTable()
        {
            return new VTable(cloopVTable);
        }

        public void setup(IStatus status, IExternalContext context, IRoutineMetadata metadata, IMetadataBuilder inBuilder, IMetadataBuilder outBuilder) throws FbException
        {
            VTable vTable = getVTable();
            vTable.setup.invoke(this, status, context, metadata, inBuilder, outBuilder);
            FbException.checkException(status);
        }

        public IExternalProcedure newItem(IStatus status, IExternalContext context, IRoutineMetadata metadata) throws FbException
        {
            VTable vTable = getVTable();
            IExternalProcedure result = vTable.newItem.invoke(this, status, context, metadata);
            FbException.checkException(status);
            return result;
        }
    }

    public static class IUdrTriggerFactory extends IDisposable implements IUdrTriggerFactoryIntf
    {
        public static class VTable extends IDisposable.VTable
        {
            public static interface Callback_setup extends com.sun.jna.Callback
            {
                public void invoke(IUdrTriggerFactory self, IStatus status, IExternalContext context, IRoutineMetadata metadata, IMetadataBuilder fieldsBuilder);
            }

            public static interface Callback_newItem extends com.sun.jna.Callback
            {
                public IExternalTrigger invoke(IUdrTriggerFactory self, IStatus status, IExternalContext context, IRoutineMetadata metadata);
            }

            public VTable(com.sun.jna.Pointer pointer)
            {
                super(pointer);
            }

            public VTable(final IUdrTriggerFactoryIntf obj)
            {
                super(obj);

                setup = new Callback_setup() {
                    @Override
                    public void invoke(IUdrTriggerFactory self, IStatus status, IExternalContext context, IRoutineMetadata metadata, IMetadataBuilder fieldsBuilder)
                    {
                        try
                        {
                            obj.setup(status, context, metadata, fieldsBuilder);
                        }
                        catch (Throwable t)
                        {
                            FbException.catchException(status, t);
                        }
                    }
                };

                newItem = new Callback_newItem() {
                    @Override
                    public IExternalTrigger invoke(IUdrTriggerFactory self, IStatus status, IExternalContext context, IRoutineMetadata metadata)
                    {
                        try
                        {
                            return obj.newItem(status, context, metadata);
                        }
                        catch (Throwable t)
                        {
                            FbException.catchException(status, t);
                            return null;
                        }
                    }
                };
            }

            public VTable()
            {
            }

            public Callback_setup setup;
            public Callback_newItem newItem;

            @Override
            protected java.util.List<String> getFieldOrder()
            {
                java.util.List<String> fields = super.getFieldOrder();
                fields.addAll(java.util.Arrays.asList("setup", "newItem"));
                return fields;
            }
        }

        public IUdrTriggerFactory()
        {
        }

        public IUdrTriggerFactory(IUdrTriggerFactoryIntf obj)
        {
            vTable = new VTable(obj);
            vTable.write();
            cloopVTable = vTable.getPointer();
            write();
        }

        @Override
        protected VTable createVTable()
        {
            return new VTable(cloopVTable);
        }

        public void setup(IStatus status, IExternalContext context, IRoutineMetadata metadata, IMetadataBuilder fieldsBuilder) throws FbException
        {
            VTable vTable = getVTable();
            vTable.setup.invoke(this, status, context, metadata, fieldsBuilder);
            FbException.checkException(status);
        }

        public IExternalTrigger newItem(IStatus status, IExternalContext context, IRoutineMetadata metadata) throws FbException
        {
            VTable vTable = getVTable();
            IExternalTrigger result = vTable.newItem.invoke(this, status, context, metadata);
            FbException.checkException(status);
            return result;
        }
    }

    public static class IUdrPlugin extends IVersioned implements IUdrPluginIntf
    {
        public static class VTable extends IVersioned.VTable
        {
            public static interface Callback_getMaster extends com.sun.jna.Callback
            {
                public IMaster invoke(IUdrPlugin self);
            }

            public static interface Callback_registerFunction extends com.sun.jna.Callback
            {
                public void invoke(IUdrPlugin self, IStatus status, String name, IUdrFunctionFactory factory);
            }

            public static interface Callback_registerProcedure extends com.sun.jna.Callback
            {
                public void invoke(IUdrPlugin self, IStatus status, String name, IUdrProcedureFactory factory);
            }

            public static interface Callback_registerTrigger extends com.sun.jna.Callback
            {
                public void invoke(IUdrPlugin self, IStatus status, String name, IUdrTriggerFactory factory);
            }

            public VTable(com.sun.jna.Pointer pointer)
            {
                super(pointer);
            }

            public VTable(final IUdrPluginIntf obj)
            {
                super(obj);

                getMaster = new Callback_getMaster() {
                    @Override
                    public IMaster invoke(IUdrPlugin self)
                    {
                        return obj.getMaster();
                    }
                };

                registerFunction = new Callback_registerFunction() {
                    @Override
                    public void invoke(IUdrPlugin self, IStatus status, String name, IUdrFunctionFactory factory)
                    {
                        try
                        {
                            obj.registerFunction(status, name, factory);
                        }
                        catch (Throwable t)
                        {
                            FbException.catchException(status, t);
                        }
                    }
                };

                registerProcedure = new Callback_registerProcedure() {
                    @Override
                    public void invoke(IUdrPlugin self, IStatus status, String name, IUdrProcedureFactory factory)
                    {
                        try
                        {
                            obj.registerProcedure(status, name, factory);
                        }
                        catch (Throwable t)
                        {
                            FbException.catchException(status, t);
                        }
                    }
                };

                registerTrigger = new Callback_registerTrigger() {
                    @Override
                    public void invoke(IUdrPlugin self, IStatus status, String name, IUdrTriggerFactory factory)
                    {
                        try
                        {
                            obj.registerTrigger(status, name, factory);
                        }
                        catch (Throwable t)
                        {
                            FbException.catchException(status, t);
                        }
                    }
                };
            }

            public VTable()
            {
            }

            public Callback_getMaster getMaster;
            public Callback_registerFunction registerFunction;
            public Callback_registerProcedure registerProcedure;
            public Callback_registerTrigger registerTrigger;

            @Override
            protected java.util.List<String> getFieldOrder()
            {
                java.util.List<String> fields = super.getFieldOrder();
                fields.addAll(java.util.Arrays.asList("getMaster", "registerFunction", "registerProcedure", "registerTrigger"));
                return fields;
            }
        }

        public IUdrPlugin()
        {
        }

        public IUdrPlugin(IUdrPluginIntf obj)
        {
            vTable = new VTable(obj);
            vTable.write();
            cloopVTable = vTable.getPointer();
            write();
        }

        @Override
        protected VTable createVTable()
        {
            return new VTable(cloopVTable);
        }

        public IMaster getMaster()
        {
            VTable vTable = getVTable();
            IMaster result = vTable.getMaster.invoke(this);
            return result;
        }

        public void registerFunction(IStatus status, String name, IUdrFunctionFactory factory) throws FbException
        {
            VTable vTable = getVTable();
            vTable.registerFunction.invoke(this, status, name, factory);
            FbException.checkException(status);
        }

        public void registerProcedure(IStatus status, String name, IUdrProcedureFactory factory) throws FbException
        {
            VTable vTable = getVTable();
            vTable.registerProcedure.invoke(this, status, name, factory);
            FbException.checkException(status);
        }

        public void registerTrigger(IStatus status, String name, IUdrTriggerFactory factory) throws FbException
        {
            VTable vTable = getVTable();
            vTable.registerTrigger.invoke(this, status, name, factory);
            FbException.checkException(status);
        }
    }

    public static class ICryptoKey extends IVersioned implements ICryptoKeyIntf
    {
        public static class VTable extends IVersioned.VTable
        {
            public static interface Callback_getObjectInfo extends com.sun.jna.Callback
            {
                public com.sun.jna.Pointer invoke(ICryptoKey self);
            }

            public static interface Callback_loadFromFile extends com.sun.jna.Callback
            {
                public int invoke(ICryptoKey self, String fileName);
            }

            public static interface Callback_loadFromBuffer extends com.sun.jna.Callback
            {
                public int invoke(ICryptoKey self, com.sun.jna.Pointer buffer, int length);
            }

            public static interface Callback_loadFromCurrentRepository extends com.sun.jna.Callback
            {
                public int invoke(ICryptoKey self);
            }

            public static interface Callback_saveToFile extends com.sun.jna.Callback
            {
                public int invoke(ICryptoKey self, String fileName);
            }

            public static interface Callback_saveToBuffer extends com.sun.jna.Callback
            {
                public int invoke(ICryptoKey self, com.sun.jna.Pointer buffer, int length, int[] realLength);
            }

            public static interface Callback_saveToRepository extends com.sun.jna.Callback
            {
                public int invoke(ICryptoKey self, ICryptoRepository repository, String name);
            }

            public static interface Callback_setAgreeKeyFromRepository extends com.sun.jna.Callback
            {
                public int invoke(ICryptoKey self, ICryptoRepository repository);
            }

            public static interface Callback_setExchangeKey extends com.sun.jna.Callback
            {
                public int invoke(ICryptoKey self, ICryptoKey key);
            }

            public static interface Callback_generateKey extends com.sun.jna.Callback
            {
                public int invoke(ICryptoKey self);
            }

            public static interface Callback_getIV extends com.sun.jna.Callback
            {
                public int invoke(ICryptoKey self, byte[] iv, int length, int[] realLength);
            }

            public static interface Callback_setIV extends com.sun.jna.Callback
            {
                public int invoke(ICryptoKey self, byte[] iv, int length);
            }

            public static interface Callback_createFromBuffer extends com.sun.jna.Callback
            {
                public int invoke(ICryptoKey self, byte[] buffer, int length);
            }

            public VTable(com.sun.jna.Pointer pointer)
            {
                super(pointer);
            }

            public VTable(final ICryptoKeyIntf obj)
            {
                super(obj);

                getObjectInfo = new Callback_getObjectInfo() {
                    @Override
                    public com.sun.jna.Pointer invoke(ICryptoKey self)
                    {
                        return obj.getObjectInfo();
                    }
                };

                loadFromFile = new Callback_loadFromFile() {
                    @Override
                    public int invoke(ICryptoKey self, String fileName)
                    {
                        return obj.loadFromFile(fileName);
                    }
                };

                loadFromBuffer = new Callback_loadFromBuffer() {
                    @Override
                    public int invoke(ICryptoKey self, com.sun.jna.Pointer buffer, int length)
                    {
                        return obj.loadFromBuffer(buffer, length);
                    }
                };

                loadFromCurrentRepository = new Callback_loadFromCurrentRepository() {
                    @Override
                    public int invoke(ICryptoKey self)
                    {
                        return obj.loadFromCurrentRepository();
                    }
                };

                saveToFile = new Callback_saveToFile() {
                    @Override
                    public int invoke(ICryptoKey self, String fileName)
                    {
                        return obj.saveToFile(fileName);
                    }
                };

                saveToBuffer = new Callback_saveToBuffer() {
                    @Override
                    public int invoke(ICryptoKey self, com.sun.jna.Pointer buffer, int length, int[] realLength)
                    {
                        return obj.saveToBuffer(buffer, length, realLength);
                    }
                };

                saveToRepository = new Callback_saveToRepository() {
                    @Override
                    public int invoke(ICryptoKey self, ICryptoRepository repository, String name)
                    {
                        return obj.saveToRepository(repository, name);
                    }
                };

                setAgreeKeyFromRepository = new Callback_setAgreeKeyFromRepository() {
                    @Override
                    public int invoke(ICryptoKey self, ICryptoRepository repository)
                    {
                        return obj.setAgreeKeyFromRepository(repository);
                    }
                };

                setExchangeKey = new Callback_setExchangeKey() {
                    @Override
                    public int invoke(ICryptoKey self, ICryptoKey key)
                    {
                        return obj.setExchangeKey(key);
                    }
                };

                generateKey = new Callback_generateKey() {
                    @Override
                    public int invoke(ICryptoKey self)
                    {
                        return obj.generateKey();
                    }
                };

                getIV = new Callback_getIV() {
                    @Override
                    public int invoke(ICryptoKey self, byte[] iv, int length, int[] realLength)
                    {
                        return obj.getIV(iv, length, realLength);
                    }
                };

                setIV = new Callback_setIV() {
                    @Override
                    public int invoke(ICryptoKey self, byte[] iv, int length)
                    {
                        return obj.setIV(iv, length);
                    }
                };

                createFromBuffer = new Callback_createFromBuffer() {
                    @Override
                    public int invoke(ICryptoKey self, byte[] buffer, int length)
                    {
                        return obj.createFromBuffer(buffer, length);
                    }
                };
            }

            public VTable()
            {
            }

            public Callback_getObjectInfo getObjectInfo;
            public Callback_loadFromFile loadFromFile;
            public Callback_loadFromBuffer loadFromBuffer;
            public Callback_loadFromCurrentRepository loadFromCurrentRepository;
            public Callback_saveToFile saveToFile;
            public Callback_saveToBuffer saveToBuffer;
            public Callback_saveToRepository saveToRepository;
            public Callback_setAgreeKeyFromRepository setAgreeKeyFromRepository;
            public Callback_setExchangeKey setExchangeKey;
            public Callback_generateKey generateKey;
            public Callback_getIV getIV;
            public Callback_setIV setIV;
            public Callback_createFromBuffer createFromBuffer;

            @Override
            protected java.util.List<String> getFieldOrder()
            {
                java.util.List<String> fields = super.getFieldOrder();
                fields.addAll(java.util.Arrays.asList("getObjectInfo", "loadFromFile", "loadFromBuffer", "loadFromCurrentRepository", "saveToFile", "saveToBuffer", "saveToRepository", "setAgreeKeyFromRepository", "setExchangeKey", "generateKey", "getIV", "setIV", "createFromBuffer"));
                return fields;
            }
        }

        public ICryptoKey()
        {
        }

        public ICryptoKey(ICryptoKeyIntf obj)
        {
            vTable = new VTable(obj);
            vTable.write();
            cloopVTable = vTable.getPointer();
            write();
        }

        @Override
        protected VTable createVTable()
        {
            return new VTable(cloopVTable);
        }

        public com.sun.jna.Pointer getObjectInfo()
        {
            VTable vTable = getVTable();
            com.sun.jna.Pointer result = vTable.getObjectInfo.invoke(this);
            return result;
        }

        public int loadFromFile(String fileName)
        {
            VTable vTable = getVTable();
            int result = vTable.loadFromFile.invoke(this, fileName);
            return result;
        }

        public int loadFromBuffer(com.sun.jna.Pointer buffer, int length)
        {
            VTable vTable = getVTable();
            int result = vTable.loadFromBuffer.invoke(this, buffer, length);
            return result;
        }

        public int loadFromCurrentRepository()
        {
            VTable vTable = getVTable();
            int result = vTable.loadFromCurrentRepository.invoke(this);
            return result;
        }

        public int saveToFile(String fileName)
        {
            VTable vTable = getVTable();
            int result = vTable.saveToFile.invoke(this, fileName);
            return result;
        }

        public int saveToBuffer(com.sun.jna.Pointer buffer, int length, int[] realLength)
        {
            VTable vTable = getVTable();
            int result = vTable.saveToBuffer.invoke(this, buffer, length, realLength);
            return result;
        }

        public int saveToRepository(ICryptoRepository repository, String name)
        {
            VTable vTable = getVTable();
            int result = vTable.saveToRepository.invoke(this, repository, name);
            return result;
        }

        public int setAgreeKeyFromRepository(ICryptoRepository repository)
        {
            VTable vTable = getVTable();
            int result = vTable.setAgreeKeyFromRepository.invoke(this, repository);
            return result;
        }

        public int setExchangeKey(ICryptoKey key)
        {
            VTable vTable = getVTable();
            int result = vTable.setExchangeKey.invoke(this, key);
            return result;
        }

        public int generateKey()
        {
            VTable vTable = getVTable();
            int result = vTable.generateKey.invoke(this);
            return result;
        }

        public int getIV(byte[] iv, int length, int[] realLength)
        {
            VTable vTable = getVTable();
            int result = vTable.getIV.invoke(this, iv, length, realLength);
            return result;
        }

        public int setIV(byte[] iv, int length)
        {
            VTable vTable = getVTable();
            int result = vTable.setIV.invoke(this, iv, length);
            return result;
        }

        public int createFromBuffer(byte[] buffer, int length)
        {
            VTable vTable = getVTable();
            int result = vTable.createFromBuffer.invoke(this, buffer, length);
            return result;
        }
    }

    public static class ICryptoKeyPair extends IVersioned implements ICryptoKeyPairIntf
    {
        public static class VTable extends IVersioned.VTable
        {
            public static interface Callback_getObjectInfo extends com.sun.jna.Callback
            {
                public com.sun.jna.Pointer invoke(ICryptoKeyPair self);
            }

            public static interface Callback_loadFromFile extends com.sun.jna.Callback
            {
                public int invoke(ICryptoKeyPair self, String fileName);
            }

            public static interface Callback_loadFromBuffer extends com.sun.jna.Callback
            {
                public int invoke(ICryptoKeyPair self, com.sun.jna.Pointer buffer, int length);
            }

            public static interface Callback_loadFromRepository extends com.sun.jna.Callback
            {
                public int invoke(ICryptoKeyPair self, ICryptoRepository repository, String name);
            }

            public static interface Callback_loadFromCurrentRepository extends com.sun.jna.Callback
            {
                public int invoke(ICryptoKeyPair self);
            }

            public static interface Callback_saveToFile extends com.sun.jna.Callback
            {
                public int invoke(ICryptoKeyPair self, String fileName);
            }

            public static interface Callback_saveToBuffer extends com.sun.jna.Callback
            {
                public int invoke(ICryptoKeyPair self, com.sun.jna.Pointer buffer, int length, int[] realLength);
            }

            public static interface Callback_saveToRepository extends com.sun.jna.Callback
            {
                public int invoke(ICryptoKeyPair self, ICryptoRepository repository, String name);
            }

            public static interface Callback_setAgreeKeyFromRepository extends com.sun.jna.Callback
            {
                public int invoke(ICryptoKeyPair self, ICryptoRepository repository);
            }

            public static interface Callback_setExchangeKey extends com.sun.jna.Callback
            {
                public int invoke(ICryptoKeyPair self, ICryptoKey key);
            }

            public static interface Callback_generateKeyPair extends com.sun.jna.Callback
            {
                public int invoke(ICryptoKeyPair self);
            }

            public static interface Callback_getPublicKey extends com.sun.jna.Callback
            {
                public int invoke(ICryptoKeyPair self, ICryptoKey key);
            }

            public static interface Callback_createPublicKey extends com.sun.jna.Callback
            {
                public int invoke(ICryptoKeyPair self, ICryptoKey[] key);
            }

            public static interface Callback_deletePublicKey extends com.sun.jna.Callback
            {
                public int invoke(ICryptoKeyPair self, ICryptoKey key);
            }

            public VTable(com.sun.jna.Pointer pointer)
            {
                super(pointer);
            }

            public VTable(final ICryptoKeyPairIntf obj)
            {
                super(obj);

                getObjectInfo = new Callback_getObjectInfo() {
                    @Override
                    public com.sun.jna.Pointer invoke(ICryptoKeyPair self)
                    {
                        return obj.getObjectInfo();
                    }
                };

                loadFromFile = new Callback_loadFromFile() {
                    @Override
                    public int invoke(ICryptoKeyPair self, String fileName)
                    {
                        return obj.loadFromFile(fileName);
                    }
                };

                loadFromBuffer = new Callback_loadFromBuffer() {
                    @Override
                    public int invoke(ICryptoKeyPair self, com.sun.jna.Pointer buffer, int length)
                    {
                        return obj.loadFromBuffer(buffer, length);
                    }
                };

                loadFromRepository = new Callback_loadFromRepository() {
                    @Override
                    public int invoke(ICryptoKeyPair self, ICryptoRepository repository, String name)
                    {
                        return obj.loadFromRepository(repository, name);
                    }
                };

                loadFromCurrentRepository = new Callback_loadFromCurrentRepository() {
                    @Override
                    public int invoke(ICryptoKeyPair self)
                    {
                        return obj.loadFromCurrentRepository();
                    }
                };

                saveToFile = new Callback_saveToFile() {
                    @Override
                    public int invoke(ICryptoKeyPair self, String fileName)
                    {
                        return obj.saveToFile(fileName);
                    }
                };

                saveToBuffer = new Callback_saveToBuffer() {
                    @Override
                    public int invoke(ICryptoKeyPair self, com.sun.jna.Pointer buffer, int length, int[] realLength)
                    {
                        return obj.saveToBuffer(buffer, length, realLength);
                    }
                };

                saveToRepository = new Callback_saveToRepository() {
                    @Override
                    public int invoke(ICryptoKeyPair self, ICryptoRepository repository, String name)
                    {
                        return obj.saveToRepository(repository, name);
                    }
                };

                setAgreeKeyFromRepository = new Callback_setAgreeKeyFromRepository() {
                    @Override
                    public int invoke(ICryptoKeyPair self, ICryptoRepository repository)
                    {
                        return obj.setAgreeKeyFromRepository(repository);
                    }
                };

                setExchangeKey = new Callback_setExchangeKey() {
                    @Override
                    public int invoke(ICryptoKeyPair self, ICryptoKey key)
                    {
                        return obj.setExchangeKey(key);
                    }
                };

                generateKeyPair = new Callback_generateKeyPair() {
                    @Override
                    public int invoke(ICryptoKeyPair self)
                    {
                        return obj.generateKeyPair();
                    }
                };

                getPublicKey = new Callback_getPublicKey() {
                    @Override
                    public int invoke(ICryptoKeyPair self, ICryptoKey key)
                    {
                        return obj.getPublicKey(key);
                    }
                };

                createPublicKey = new Callback_createPublicKey() {
                    @Override
                    public int invoke(ICryptoKeyPair self, ICryptoKey[] key)
                    {
                        return obj.createPublicKey(key);
                    }
                };

                deletePublicKey = new Callback_deletePublicKey() {
                    @Override
                    public int invoke(ICryptoKeyPair self, ICryptoKey key)
                    {
                        return obj.deletePublicKey(key);
                    }
                };
            }

            public VTable()
            {
            }

            public Callback_getObjectInfo getObjectInfo;
            public Callback_loadFromFile loadFromFile;
            public Callback_loadFromBuffer loadFromBuffer;
            public Callback_loadFromRepository loadFromRepository;
            public Callback_loadFromCurrentRepository loadFromCurrentRepository;
            public Callback_saveToFile saveToFile;
            public Callback_saveToBuffer saveToBuffer;
            public Callback_saveToRepository saveToRepository;
            public Callback_setAgreeKeyFromRepository setAgreeKeyFromRepository;
            public Callback_setExchangeKey setExchangeKey;
            public Callback_generateKeyPair generateKeyPair;
            public Callback_getPublicKey getPublicKey;
            public Callback_createPublicKey createPublicKey;
            public Callback_deletePublicKey deletePublicKey;

            @Override
            protected java.util.List<String> getFieldOrder()
            {
                java.util.List<String> fields = super.getFieldOrder();
                fields.addAll(java.util.Arrays.asList("getObjectInfo", "loadFromFile", "loadFromBuffer", "loadFromRepository", "loadFromCurrentRepository", "saveToFile", "saveToBuffer", "saveToRepository", "setAgreeKeyFromRepository", "setExchangeKey", "generateKeyPair", "getPublicKey", "createPublicKey", "deletePublicKey"));
                return fields;
            }
        }

        public ICryptoKeyPair()
        {
        }

        public ICryptoKeyPair(ICryptoKeyPairIntf obj)
        {
            vTable = new VTable(obj);
            vTable.write();
            cloopVTable = vTable.getPointer();
            write();
        }

        @Override
        protected VTable createVTable()
        {
            return new VTable(cloopVTable);
        }

        public com.sun.jna.Pointer getObjectInfo()
        {
            VTable vTable = getVTable();
            com.sun.jna.Pointer result = vTable.getObjectInfo.invoke(this);
            return result;
        }

        public int loadFromFile(String fileName)
        {
            VTable vTable = getVTable();
            int result = vTable.loadFromFile.invoke(this, fileName);
            return result;
        }

        public int loadFromBuffer(com.sun.jna.Pointer buffer, int length)
        {
            VTable vTable = getVTable();
            int result = vTable.loadFromBuffer.invoke(this, buffer, length);
            return result;
        }

        public int loadFromRepository(ICryptoRepository repository, String name)
        {
            VTable vTable = getVTable();
            int result = vTable.loadFromRepository.invoke(this, repository, name);
            return result;
        }

        public int loadFromCurrentRepository()
        {
            VTable vTable = getVTable();
            int result = vTable.loadFromCurrentRepository.invoke(this);
            return result;
        }

        public int saveToFile(String fileName)
        {
            VTable vTable = getVTable();
            int result = vTable.saveToFile.invoke(this, fileName);
            return result;
        }

        public int saveToBuffer(com.sun.jna.Pointer buffer, int length, int[] realLength)
        {
            VTable vTable = getVTable();
            int result = vTable.saveToBuffer.invoke(this, buffer, length, realLength);
            return result;
        }

        public int saveToRepository(ICryptoRepository repository, String name)
        {
            VTable vTable = getVTable();
            int result = vTable.saveToRepository.invoke(this, repository, name);
            return result;
        }

        public int setAgreeKeyFromRepository(ICryptoRepository repository)
        {
            VTable vTable = getVTable();
            int result = vTable.setAgreeKeyFromRepository.invoke(this, repository);
            return result;
        }

        public int setExchangeKey(ICryptoKey key)
        {
            VTable vTable = getVTable();
            int result = vTable.setExchangeKey.invoke(this, key);
            return result;
        }

        public int generateKeyPair()
        {
            VTable vTable = getVTable();
            int result = vTable.generateKeyPair.invoke(this);
            return result;
        }

        public int getPublicKey(ICryptoKey key)
        {
            VTable vTable = getVTable();
            int result = vTable.getPublicKey.invoke(this, key);
            return result;
        }

        public int createPublicKey(ICryptoKey[] key)
        {
            VTable vTable = getVTable();
            int result = vTable.createPublicKey.invoke(this, key);
            return result;
        }

        public int deletePublicKey(ICryptoKey key)
        {
            VTable vTable = getVTable();
            int result = vTable.deletePublicKey.invoke(this, key);
            return result;
        }
    }

    public static class ICryptoRandomFactory extends IVersioned implements ICryptoRandomFactoryIntf
    {
        public static class VTable extends IVersioned.VTable
        {
            public static interface Callback_getObjectInfo extends com.sun.jna.Callback
            {
                public com.sun.jna.Pointer invoke(ICryptoRandomFactory self);
            }

            public static interface Callback_generateRandom extends com.sun.jna.Callback
            {
                public int invoke(ICryptoRandomFactory self, byte[] buffer, int length, ICryptoProvider providerName);
            }

            public VTable(com.sun.jna.Pointer pointer)
            {
                super(pointer);
            }

            public VTable(final ICryptoRandomFactoryIntf obj)
            {
                super(obj);

                getObjectInfo = new Callback_getObjectInfo() {
                    @Override
                    public com.sun.jna.Pointer invoke(ICryptoRandomFactory self)
                    {
                        return obj.getObjectInfo();
                    }
                };

                generateRandom = new Callback_generateRandom() {
                    @Override
                    public int invoke(ICryptoRandomFactory self, byte[] buffer, int length, ICryptoProvider providerName)
                    {
                        return obj.generateRandom(buffer, length, providerName);
                    }
                };
            }

            public VTable()
            {
            }

            public Callback_getObjectInfo getObjectInfo;
            public Callback_generateRandom generateRandom;

            @Override
            protected java.util.List<String> getFieldOrder()
            {
                java.util.List<String> fields = super.getFieldOrder();
                fields.addAll(java.util.Arrays.asList("getObjectInfo", "generateRandom"));
                return fields;
            }
        }

        public ICryptoRandomFactory()
        {
        }

        public ICryptoRandomFactory(ICryptoRandomFactoryIntf obj)
        {
            vTable = new VTable(obj);
            vTable.write();
            cloopVTable = vTable.getPointer();
            write();
        }

        @Override
        protected VTable createVTable()
        {
            return new VTable(cloopVTable);
        }

        public com.sun.jna.Pointer getObjectInfo()
        {
            VTable vTable = getVTable();
            com.sun.jna.Pointer result = vTable.getObjectInfo.invoke(this);
            return result;
        }

        public int generateRandom(byte[] buffer, int length, ICryptoProvider providerName)
        {
            VTable vTable = getVTable();
            int result = vTable.generateRandom.invoke(this, buffer, length, providerName);
            return result;
        }
    }

    public static class ICryptoHashFactory extends IVersioned implements ICryptoHashFactoryIntf
    {
        public static class VTable extends IVersioned.VTable
        {
            public static interface Callback_getObjectInfo extends com.sun.jna.Callback
            {
                public com.sun.jna.Pointer invoke(ICryptoHashFactory self);
            }

            public static interface Callback_createHash extends com.sun.jna.Callback
            {
                public int invoke(ICryptoHashFactory self, byte[] buffer, int bufferLength, byte[] hash, int hashLength, int[] realHashLength, boolean asString);
            }

            public static interface Callback_setKeyForHash extends com.sun.jna.Callback
            {
                public int invoke(ICryptoHashFactory self, ICryptoKey key);
            }

            public VTable(com.sun.jna.Pointer pointer)
            {
                super(pointer);
            }

            public VTable(final ICryptoHashFactoryIntf obj)
            {
                super(obj);

                getObjectInfo = new Callback_getObjectInfo() {
                    @Override
                    public com.sun.jna.Pointer invoke(ICryptoHashFactory self)
                    {
                        return obj.getObjectInfo();
                    }
                };

                createHash = new Callback_createHash() {
                    @Override
                    public int invoke(ICryptoHashFactory self, byte[] buffer, int bufferLength, byte[] hash, int hashLength, int[] realHashLength, boolean asString)
                    {
                        return obj.createHash(buffer, bufferLength, hash, hashLength, realHashLength, asString);
                    }
                };

                setKeyForHash = new Callback_setKeyForHash() {
                    @Override
                    public int invoke(ICryptoHashFactory self, ICryptoKey key)
                    {
                        return obj.setKeyForHash(key);
                    }
                };
            }

            public VTable()
            {
            }

            public Callback_getObjectInfo getObjectInfo;
            public Callback_createHash createHash;
            public Callback_setKeyForHash setKeyForHash;

            @Override
            protected java.util.List<String> getFieldOrder()
            {
                java.util.List<String> fields = super.getFieldOrder();
                fields.addAll(java.util.Arrays.asList("getObjectInfo", "createHash", "setKeyForHash"));
                return fields;
            }
        }

        public ICryptoHashFactory()
        {
        }

        public ICryptoHashFactory(ICryptoHashFactoryIntf obj)
        {
            vTable = new VTable(obj);
            vTable.write();
            cloopVTable = vTable.getPointer();
            write();
        }

        @Override
        protected VTable createVTable()
        {
            return new VTable(cloopVTable);
        }

        public com.sun.jna.Pointer getObjectInfo()
        {
            VTable vTable = getVTable();
            com.sun.jna.Pointer result = vTable.getObjectInfo.invoke(this);
            return result;
        }

        public int createHash(byte[] buffer, int bufferLength, byte[] hash, int hashLength, int[] realHashLength, boolean asString)
        {
            VTable vTable = getVTable();
            int result = vTable.createHash.invoke(this, buffer, bufferLength, hash, hashLength, realHashLength, asString);
            return result;
        }

        public int setKeyForHash(ICryptoKey key)
        {
            VTable vTable = getVTable();
            int result = vTable.setKeyForHash.invoke(this, key);
            return result;
        }
    }

    public static class ICryptoSymmetricFactory extends IVersioned implements ICryptoSymmetricFactoryIntf
    {
        public static class VTable extends IVersioned.VTable
        {
            public static interface Callback_getObjectInfo extends com.sun.jna.Callback
            {
                public com.sun.jna.Pointer invoke(ICryptoSymmetricFactory self);
            }

            public static interface Callback_encrypt extends com.sun.jna.Callback
            {
                public int invoke(ICryptoSymmetricFactory self, byte[] data, int dataLength, byte[] cryptData, int cryptDataLength, int[] realCryptDataLength, ICryptoKey key);
            }

            public static interface Callback_decrypt extends com.sun.jna.Callback
            {
                public int invoke(ICryptoSymmetricFactory self, byte[] cryptData, int cryptDataLength, byte[] data, int dataLength, int[] realDataLength, ICryptoKey key);
            }

            public static interface Callback_createKey extends com.sun.jna.Callback
            {
                public int invoke(ICryptoSymmetricFactory self, ICryptoRepository repository, ICryptoKey[] key);
            }

            public static interface Callback_deleteKey extends com.sun.jna.Callback
            {
                public int invoke(ICryptoSymmetricFactory self, ICryptoKey key);
            }

            public VTable(com.sun.jna.Pointer pointer)
            {
                super(pointer);
            }

            public VTable(final ICryptoSymmetricFactoryIntf obj)
            {
                super(obj);

                getObjectInfo = new Callback_getObjectInfo() {
                    @Override
                    public com.sun.jna.Pointer invoke(ICryptoSymmetricFactory self)
                    {
                        return obj.getObjectInfo();
                    }
                };

                encrypt = new Callback_encrypt() {
                    @Override
                    public int invoke(ICryptoSymmetricFactory self, byte[] data, int dataLength, byte[] cryptData, int cryptDataLength, int[] realCryptDataLength, ICryptoKey key)
                    {
                        return obj.encrypt(data, dataLength, cryptData, cryptDataLength, realCryptDataLength, key);
                    }
                };

                decrypt = new Callback_decrypt() {
                    @Override
                    public int invoke(ICryptoSymmetricFactory self, byte[] cryptData, int cryptDataLength, byte[] data, int dataLength, int[] realDataLength, ICryptoKey key)
                    {
                        return obj.decrypt(cryptData, cryptDataLength, data, dataLength, realDataLength, key);
                    }
                };

                createKey = new Callback_createKey() {
                    @Override
                    public int invoke(ICryptoSymmetricFactory self, ICryptoRepository repository, ICryptoKey[] key)
                    {
                        return obj.createKey(repository, key);
                    }
                };

                deleteKey = new Callback_deleteKey() {
                    @Override
                    public int invoke(ICryptoSymmetricFactory self, ICryptoKey key)
                    {
                        return obj.deleteKey(key);
                    }
                };
            }

            public VTable()
            {
            }

            public Callback_getObjectInfo getObjectInfo;
            public Callback_encrypt encrypt;
            public Callback_decrypt decrypt;
            public Callback_createKey createKey;
            public Callback_deleteKey deleteKey;

            @Override
            protected java.util.List<String> getFieldOrder()
            {
                java.util.List<String> fields = super.getFieldOrder();
                fields.addAll(java.util.Arrays.asList("getObjectInfo", "encrypt", "decrypt", "createKey", "deleteKey"));
                return fields;
            }
        }

        public ICryptoSymmetricFactory()
        {
        }

        public ICryptoSymmetricFactory(ICryptoSymmetricFactoryIntf obj)
        {
            vTable = new VTable(obj);
            vTable.write();
            cloopVTable = vTable.getPointer();
            write();
        }

        @Override
        protected VTable createVTable()
        {
            return new VTable(cloopVTable);
        }

        public com.sun.jna.Pointer getObjectInfo()
        {
            VTable vTable = getVTable();
            com.sun.jna.Pointer result = vTable.getObjectInfo.invoke(this);
            return result;
        }

        public int encrypt(byte[] data, int dataLength, byte[] cryptData, int cryptDataLength, int[] realCryptDataLength, ICryptoKey key)
        {
            VTable vTable = getVTable();
            int result = vTable.encrypt.invoke(this, data, dataLength, cryptData, cryptDataLength, realCryptDataLength, key);
            return result;
        }

        public int decrypt(byte[] cryptData, int cryptDataLength, byte[] data, int dataLength, int[] realDataLength, ICryptoKey key)
        {
            VTable vTable = getVTable();
            int result = vTable.decrypt.invoke(this, cryptData, cryptDataLength, data, dataLength, realDataLength, key);
            return result;
        }

        public int createKey(ICryptoRepository repository, ICryptoKey[] key)
        {
            VTable vTable = getVTable();
            int result = vTable.createKey.invoke(this, repository, key);
            return result;
        }

        public int deleteKey(ICryptoKey key)
        {
            VTable vTable = getVTable();
            int result = vTable.deleteKey.invoke(this, key);
            return result;
        }
    }

    public static class ICryptoSignatureFactory extends IVersioned implements ICryptoSignatureFactoryIntf
    {
        public static class VTable extends IVersioned.VTable
        {
            public static interface Callback_getObjectInfo extends com.sun.jna.Callback
            {
                public com.sun.jna.Pointer invoke(ICryptoSignatureFactory self);
            }

            public static interface Callback_createKeyPair extends com.sun.jna.Callback
            {
                public int invoke(ICryptoSignatureFactory self, ICryptoRepository repository, ICryptoKeyPair[] key);
            }

            public static interface Callback_deleteKeyPair extends com.sun.jna.Callback
            {
                public int invoke(ICryptoSignatureFactory self, ICryptoKeyPair keyPair);
            }

            public static interface Callback_sign extends com.sun.jna.Callback
            {
                public int invoke(ICryptoSignatureFactory self, byte[] data, int dataLength, ICryptoSignature signature, ICryptoKeyPair privateKey);
            }

            public static interface Callback_verifySign extends com.sun.jna.Callback
            {
                public int invoke(ICryptoSignatureFactory self, byte[] data, int dataLength, ICryptoSignature signature, ICryptoKey publicKey);
            }

            public static interface Callback_createSignature extends com.sun.jna.Callback
            {
                public int invoke(ICryptoSignatureFactory self, ICryptoSignature[] signature);
            }

            public static interface Callback_deleteSignature extends com.sun.jna.Callback
            {
                public int invoke(ICryptoSignatureFactory self, ICryptoSignature signature);
            }

            public VTable(com.sun.jna.Pointer pointer)
            {
                super(pointer);
            }

            public VTable(final ICryptoSignatureFactoryIntf obj)
            {
                super(obj);

                getObjectInfo = new Callback_getObjectInfo() {
                    @Override
                    public com.sun.jna.Pointer invoke(ICryptoSignatureFactory self)
                    {
                        return obj.getObjectInfo();
                    }
                };

                createKeyPair = new Callback_createKeyPair() {
                    @Override
                    public int invoke(ICryptoSignatureFactory self, ICryptoRepository repository, ICryptoKeyPair[] key)
                    {
                        return obj.createKeyPair(repository, key);
                    }
                };

                deleteKeyPair = new Callback_deleteKeyPair() {
                    @Override
                    public int invoke(ICryptoSignatureFactory self, ICryptoKeyPair keyPair)
                    {
                        return obj.deleteKeyPair(keyPair);
                    }
                };

                sign = new Callback_sign() {
                    @Override
                    public int invoke(ICryptoSignatureFactory self, byte[] data, int dataLength, ICryptoSignature signature, ICryptoKeyPair privateKey)
                    {
                        return obj.sign(data, dataLength, signature, privateKey);
                    }
                };

                verifySign = new Callback_verifySign() {
                    @Override
                    public int invoke(ICryptoSignatureFactory self, byte[] data, int dataLength, ICryptoSignature signature, ICryptoKey publicKey)
                    {
                        return obj.verifySign(data, dataLength, signature, publicKey);
                    }
                };

                createSignature = new Callback_createSignature() {
                    @Override
                    public int invoke(ICryptoSignatureFactory self, ICryptoSignature[] signature)
                    {
                        return obj.createSignature(signature);
                    }
                };

                deleteSignature = new Callback_deleteSignature() {
                    @Override
                    public int invoke(ICryptoSignatureFactory self, ICryptoSignature signature)
                    {
                        return obj.deleteSignature(signature);
                    }
                };
            }

            public VTable()
            {
            }

            public Callback_getObjectInfo getObjectInfo;
            public Callback_createKeyPair createKeyPair;
            public Callback_deleteKeyPair deleteKeyPair;
            public Callback_sign sign;
            public Callback_verifySign verifySign;
            public Callback_createSignature createSignature;
            public Callback_deleteSignature deleteSignature;

            @Override
            protected java.util.List<String> getFieldOrder()
            {
                java.util.List<String> fields = super.getFieldOrder();
                fields.addAll(java.util.Arrays.asList("getObjectInfo", "createKeyPair", "deleteKeyPair", "sign", "verifySign", "createSignature", "deleteSignature"));
                return fields;
            }
        }

        public ICryptoSignatureFactory()
        {
        }

        public ICryptoSignatureFactory(ICryptoSignatureFactoryIntf obj)
        {
            vTable = new VTable(obj);
            vTable.write();
            cloopVTable = vTable.getPointer();
            write();
        }

        @Override
        protected VTable createVTable()
        {
            return new VTable(cloopVTable);
        }

        public com.sun.jna.Pointer getObjectInfo()
        {
            VTable vTable = getVTable();
            com.sun.jna.Pointer result = vTable.getObjectInfo.invoke(this);
            return result;
        }

        public int createKeyPair(ICryptoRepository repository, ICryptoKeyPair[] key)
        {
            VTable vTable = getVTable();
            int result = vTable.createKeyPair.invoke(this, repository, key);
            return result;
        }

        public int deleteKeyPair(ICryptoKeyPair keyPair)
        {
            VTable vTable = getVTable();
            int result = vTable.deleteKeyPair.invoke(this, keyPair);
            return result;
        }

        public int sign(byte[] data, int dataLength, ICryptoSignature signature, ICryptoKeyPair privateKey)
        {
            VTable vTable = getVTable();
            int result = vTable.sign.invoke(this, data, dataLength, signature, privateKey);
            return result;
        }

        public int verifySign(byte[] data, int dataLength, ICryptoSignature signature, ICryptoKey publicKey)
        {
            VTable vTable = getVTable();
            int result = vTable.verifySign.invoke(this, data, dataLength, signature, publicKey);
            return result;
        }

        public int createSignature(ICryptoSignature[] signature)
        {
            VTable vTable = getVTable();
            int result = vTable.createSignature.invoke(this, signature);
            return result;
        }

        public int deleteSignature(ICryptoSignature signature)
        {
            VTable vTable = getVTable();
            int result = vTable.deleteSignature.invoke(this, signature);
            return result;
        }
    }

    public static class ICryptoSignature extends IVersioned implements ICryptoSignatureIntf
    {
        public static class VTable extends IVersioned.VTable
        {
            public static interface Callback_getObjectInfo extends com.sun.jna.Callback
            {
                public com.sun.jna.Pointer invoke(ICryptoSignature self);
            }

            public static interface Callback_saveToFile extends com.sun.jna.Callback
            {
                public int invoke(ICryptoSignature self, String fileName);
            }

            public static interface Callback_saveToBuffer extends com.sun.jna.Callback
            {
                public int invoke(ICryptoSignature self, com.sun.jna.Pointer buffer, int length, int[] realLength);
            }

            public static interface Callback_loadFromFile extends com.sun.jna.Callback
            {
                public int invoke(ICryptoSignature self, String fileName);
            }

            public static interface Callback_loadFromBuffer extends com.sun.jna.Callback
            {
                public int invoke(ICryptoSignature self, com.sun.jna.Pointer buffer, int length);
            }

            public VTable(com.sun.jna.Pointer pointer)
            {
                super(pointer);
            }

            public VTable(final ICryptoSignatureIntf obj)
            {
                super(obj);

                getObjectInfo = new Callback_getObjectInfo() {
                    @Override
                    public com.sun.jna.Pointer invoke(ICryptoSignature self)
                    {
                        return obj.getObjectInfo();
                    }
                };

                saveToFile = new Callback_saveToFile() {
                    @Override
                    public int invoke(ICryptoSignature self, String fileName)
                    {
                        return obj.saveToFile(fileName);
                    }
                };

                saveToBuffer = new Callback_saveToBuffer() {
                    @Override
                    public int invoke(ICryptoSignature self, com.sun.jna.Pointer buffer, int length, int[] realLength)
                    {
                        return obj.saveToBuffer(buffer, length, realLength);
                    }
                };

                loadFromFile = new Callback_loadFromFile() {
                    @Override
                    public int invoke(ICryptoSignature self, String fileName)
                    {
                        return obj.loadFromFile(fileName);
                    }
                };

                loadFromBuffer = new Callback_loadFromBuffer() {
                    @Override
                    public int invoke(ICryptoSignature self, com.sun.jna.Pointer buffer, int length)
                    {
                        return obj.loadFromBuffer(buffer, length);
                    }
                };
            }

            public VTable()
            {
            }

            public Callback_getObjectInfo getObjectInfo;
            public Callback_saveToFile saveToFile;
            public Callback_saveToBuffer saveToBuffer;
            public Callback_loadFromFile loadFromFile;
            public Callback_loadFromBuffer loadFromBuffer;

            @Override
            protected java.util.List<String> getFieldOrder()
            {
                java.util.List<String> fields = super.getFieldOrder();
                fields.addAll(java.util.Arrays.asList("getObjectInfo", "saveToFile", "saveToBuffer", "loadFromFile", "loadFromBuffer"));
                return fields;
            }
        }

        public ICryptoSignature()
        {
        }

        public ICryptoSignature(ICryptoSignatureIntf obj)
        {
            vTable = new VTable(obj);
            vTable.write();
            cloopVTable = vTable.getPointer();
            write();
        }

        @Override
        protected VTable createVTable()
        {
            return new VTable(cloopVTable);
        }

        public com.sun.jna.Pointer getObjectInfo()
        {
            VTable vTable = getVTable();
            com.sun.jna.Pointer result = vTable.getObjectInfo.invoke(this);
            return result;
        }

        public int saveToFile(String fileName)
        {
            VTable vTable = getVTable();
            int result = vTable.saveToFile.invoke(this, fileName);
            return result;
        }

        public int saveToBuffer(com.sun.jna.Pointer buffer, int length, int[] realLength)
        {
            VTable vTable = getVTable();
            int result = vTable.saveToBuffer.invoke(this, buffer, length, realLength);
            return result;
        }

        public int loadFromFile(String fileName)
        {
            VTable vTable = getVTable();
            int result = vTable.loadFromFile.invoke(this, fileName);
            return result;
        }

        public int loadFromBuffer(com.sun.jna.Pointer buffer, int length)
        {
            VTable vTable = getVTable();
            int result = vTable.loadFromBuffer.invoke(this, buffer, length);
            return result;
        }
    }

    public static class ICryptoCertificateFactory extends IVersioned implements ICryptoCertificateFactoryIntf
    {
        public static class VTable extends IVersioned.VTable
        {
            public static interface Callback_getObjectInfo extends com.sun.jna.Callback
            {
                public com.sun.jna.Pointer invoke(ICryptoCertificateFactory self);
            }

            public static interface Callback_createCertificate extends com.sun.jna.Callback
            {
                public int invoke(ICryptoCertificateFactory self, ICryptoRepository repository, ICryptoCertificate[] certificate);
            }

            public static interface Callback_deleteCertificate extends com.sun.jna.Callback
            {
                public int invoke(ICryptoCertificateFactory self, ICryptoCertificate certificate);
            }

            public static interface Callback_encrypt extends com.sun.jna.Callback
            {
                public int invoke(ICryptoCertificateFactory self, byte[] data, int dataLength, byte[] cryptData, int cryptDataLength, int[] realCryptDataLength, ICryptoCertificate certificate);
            }

            public static interface Callback_decrypt extends com.sun.jna.Callback
            {
                public int invoke(ICryptoCertificateFactory self, byte[] cryptData, int cryptDataLength, byte[] data, int dataLength, int[] realDataLength, ICryptoRepository repository);
            }

            public VTable(com.sun.jna.Pointer pointer)
            {
                super(pointer);
            }

            public VTable(final ICryptoCertificateFactoryIntf obj)
            {
                super(obj);

                getObjectInfo = new Callback_getObjectInfo() {
                    @Override
                    public com.sun.jna.Pointer invoke(ICryptoCertificateFactory self)
                    {
                        return obj.getObjectInfo();
                    }
                };

                createCertificate = new Callback_createCertificate() {
                    @Override
                    public int invoke(ICryptoCertificateFactory self, ICryptoRepository repository, ICryptoCertificate[] certificate)
                    {
                        return obj.createCertificate(repository, certificate);
                    }
                };

                deleteCertificate = new Callback_deleteCertificate() {
                    @Override
                    public int invoke(ICryptoCertificateFactory self, ICryptoCertificate certificate)
                    {
                        return obj.deleteCertificate(certificate);
                    }
                };

                encrypt = new Callback_encrypt() {
                    @Override
                    public int invoke(ICryptoCertificateFactory self, byte[] data, int dataLength, byte[] cryptData, int cryptDataLength, int[] realCryptDataLength, ICryptoCertificate certificate)
                    {
                        return obj.encrypt(data, dataLength, cryptData, cryptDataLength, realCryptDataLength, certificate);
                    }
                };

                decrypt = new Callback_decrypt() {
                    @Override
                    public int invoke(ICryptoCertificateFactory self, byte[] cryptData, int cryptDataLength, byte[] data, int dataLength, int[] realDataLength, ICryptoRepository repository)
                    {
                        return obj.decrypt(cryptData, cryptDataLength, data, dataLength, realDataLength, repository);
                    }
                };
            }

            public VTable()
            {
            }

            public Callback_getObjectInfo getObjectInfo;
            public Callback_createCertificate createCertificate;
            public Callback_deleteCertificate deleteCertificate;
            public Callback_encrypt encrypt;
            public Callback_decrypt decrypt;

            @Override
            protected java.util.List<String> getFieldOrder()
            {
                java.util.List<String> fields = super.getFieldOrder();
                fields.addAll(java.util.Arrays.asList("getObjectInfo", "createCertificate", "deleteCertificate", "encrypt", "decrypt"));
                return fields;
            }
        }

        public ICryptoCertificateFactory()
        {
        }

        public ICryptoCertificateFactory(ICryptoCertificateFactoryIntf obj)
        {
            vTable = new VTable(obj);
            vTable.write();
            cloopVTable = vTable.getPointer();
            write();
        }

        @Override
        protected VTable createVTable()
        {
            return new VTable(cloopVTable);
        }

        public com.sun.jna.Pointer getObjectInfo()
        {
            VTable vTable = getVTable();
            com.sun.jna.Pointer result = vTable.getObjectInfo.invoke(this);
            return result;
        }

        public int createCertificate(ICryptoRepository repository, ICryptoCertificate[] certificate)
        {
            VTable vTable = getVTable();
            int result = vTable.createCertificate.invoke(this, repository, certificate);
            return result;
        }

        public int deleteCertificate(ICryptoCertificate certificate)
        {
            VTable vTable = getVTable();
            int result = vTable.deleteCertificate.invoke(this, certificate);
            return result;
        }

        public int encrypt(byte[] data, int dataLength, byte[] cryptData, int cryptDataLength, int[] realCryptDataLength, ICryptoCertificate certificate)
        {
            VTable vTable = getVTable();
            int result = vTable.encrypt.invoke(this, data, dataLength, cryptData, cryptDataLength, realCryptDataLength, certificate);
            return result;
        }

        public int decrypt(byte[] cryptData, int cryptDataLength, byte[] data, int dataLength, int[] realDataLength, ICryptoRepository repository)
        {
            VTable vTable = getVTable();
            int result = vTable.decrypt.invoke(this, cryptData, cryptDataLength, data, dataLength, realDataLength, repository);
            return result;
        }
    }

    public static class ICryptoCertificate extends IVersioned implements ICryptoCertificateIntf
    {
        public static class VTable extends IVersioned.VTable
        {
            public static interface Callback_getObjectInfo extends com.sun.jna.Callback
            {
                public com.sun.jna.Pointer invoke(ICryptoCertificate self);
            }

            public static interface Callback_loadFromFile extends com.sun.jna.Callback
            {
                public int invoke(ICryptoCertificate self, String fileName);
            }

            public static interface Callback_loadFromBuffer extends com.sun.jna.Callback
            {
                public int invoke(ICryptoCertificate self, com.sun.jna.Pointer buffer, int length);
            }

            public static interface Callback_loadFromBinaryBuffer extends com.sun.jna.Callback
            {
                public int invoke(ICryptoCertificate self, com.sun.jna.Pointer buffer, int length);
            }

            public static interface Callback_loadFromRepository extends com.sun.jna.Callback
            {
                public int invoke(ICryptoCertificate self, ICryptoRepository repository, String name);
            }

            public static interface Callback_saveToFile extends com.sun.jna.Callback
            {
                public int invoke(ICryptoCertificate self, String fileName);
            }

            public static interface Callback_saveToBuffer extends com.sun.jna.Callback
            {
                public int invoke(ICryptoCertificate self, com.sun.jna.Pointer buffer, int length, int[] realLength);
            }

            public static interface Callback_saveToBinaryBuffer extends com.sun.jna.Callback
            {
                public int invoke(ICryptoCertificate self, com.sun.jna.Pointer buffer, int length, int[] realLength);
            }

            public static interface Callback_verifyCertificate extends com.sun.jna.Callback
            {
                public int invoke(ICryptoCertificate self, ICryptoCertificate certificate);
            }

            public static interface Callback_getId extends com.sun.jna.Callback
            {
                public int invoke(ICryptoCertificate self, byte[] id, int[] length);
            }

            public static interface Callback_getIssuerName extends com.sun.jna.Callback
            {
                public int invoke(ICryptoCertificate self, byte[] issuerName, int[] length);
            }

            public static interface Callback_getPublicKey extends com.sun.jna.Callback
            {
                public int invoke(ICryptoCertificate self, ICryptoKey key);
            }

            public static interface Callback_getPublicKeyMethod extends com.sun.jna.Callback
            {
                public int invoke(ICryptoCertificate self, com.sun.jna.Pointer method, int[] length);
            }

            public static interface Callback_createPublicKeyFromCertificate extends com.sun.jna.Callback
            {
                public int invoke(ICryptoCertificate self, ICryptoRepository repository, ICryptoKey[] key);
            }

            public static interface Callback_deleteCertificatePublicKey extends com.sun.jna.Callback
            {
                public int invoke(ICryptoCertificate self, ICryptoKey key);
            }

            public static interface Callback_getSerialNumber extends com.sun.jna.Callback
            {
                public int invoke(ICryptoCertificate self, byte[] serialNumber, int[] length);
            }

            public static interface Callback_getOwnerName extends com.sun.jna.Callback
            {
                public int invoke(ICryptoCertificate self, com.sun.jna.Pointer ownerName, int[] length, String user_dn);
            }

            public static interface Callback_getKeyContainer extends com.sun.jna.Callback
            {
                public int invoke(ICryptoCertificate self, com.sun.jna.Pointer container, int[] length);
            }

            public VTable(com.sun.jna.Pointer pointer)
            {
                super(pointer);
            }

            public VTable(final ICryptoCertificateIntf obj)
            {
                super(obj);

                getObjectInfo = new Callback_getObjectInfo() {
                    @Override
                    public com.sun.jna.Pointer invoke(ICryptoCertificate self)
                    {
                        return obj.getObjectInfo();
                    }
                };

                loadFromFile = new Callback_loadFromFile() {
                    @Override
                    public int invoke(ICryptoCertificate self, String fileName)
                    {
                        return obj.loadFromFile(fileName);
                    }
                };

                loadFromBuffer = new Callback_loadFromBuffer() {
                    @Override
                    public int invoke(ICryptoCertificate self, com.sun.jna.Pointer buffer, int length)
                    {
                        return obj.loadFromBuffer(buffer, length);
                    }
                };

                loadFromBinaryBuffer = new Callback_loadFromBinaryBuffer() {
                    @Override
                    public int invoke(ICryptoCertificate self, com.sun.jna.Pointer buffer, int length)
                    {
                        return obj.loadFromBinaryBuffer(buffer, length);
                    }
                };

                loadFromRepository = new Callback_loadFromRepository() {
                    @Override
                    public int invoke(ICryptoCertificate self, ICryptoRepository repository, String name)
                    {
                        return obj.loadFromRepository(repository, name);
                    }
                };

                saveToFile = new Callback_saveToFile() {
                    @Override
                    public int invoke(ICryptoCertificate self, String fileName)
                    {
                        return obj.saveToFile(fileName);
                    }
                };

                saveToBuffer = new Callback_saveToBuffer() {
                    @Override
                    public int invoke(ICryptoCertificate self, com.sun.jna.Pointer buffer, int length, int[] realLength)
                    {
                        return obj.saveToBuffer(buffer, length, realLength);
                    }
                };

                saveToBinaryBuffer = new Callback_saveToBinaryBuffer() {
                    @Override
                    public int invoke(ICryptoCertificate self, com.sun.jna.Pointer buffer, int length, int[] realLength)
                    {
                        return obj.saveToBinaryBuffer(buffer, length, realLength);
                    }
                };

                verifyCertificate = new Callback_verifyCertificate() {
                    @Override
                    public int invoke(ICryptoCertificate self, ICryptoCertificate certificate)
                    {
                        return obj.verifyCertificate(certificate);
                    }
                };

                getId = new Callback_getId() {
                    @Override
                    public int invoke(ICryptoCertificate self, byte[] id, int[] length)
                    {
                        return obj.getId(id, length);
                    }
                };

                getIssuerName = new Callback_getIssuerName() {
                    @Override
                    public int invoke(ICryptoCertificate self, byte[] issuerName, int[] length)
                    {
                        return obj.getIssuerName(issuerName, length);
                    }
                };

                getPublicKey = new Callback_getPublicKey() {
                    @Override
                    public int invoke(ICryptoCertificate self, ICryptoKey key)
                    {
                        return obj.getPublicKey(key);
                    }
                };

                getPublicKeyMethod = new Callback_getPublicKeyMethod() {
                    @Override
                    public int invoke(ICryptoCertificate self, com.sun.jna.Pointer method, int[] length)
                    {
                        return obj.getPublicKeyMethod(method, length);
                    }
                };

                createPublicKeyFromCertificate = new Callback_createPublicKeyFromCertificate() {
                    @Override
                    public int invoke(ICryptoCertificate self, ICryptoRepository repository, ICryptoKey[] key)
                    {
                        return obj.createPublicKeyFromCertificate(repository, key);
                    }
                };

                deleteCertificatePublicKey = new Callback_deleteCertificatePublicKey() {
                    @Override
                    public int invoke(ICryptoCertificate self, ICryptoKey key)
                    {
                        return obj.deleteCertificatePublicKey(key);
                    }
                };

                getSerialNumber = new Callback_getSerialNumber() {
                    @Override
                    public int invoke(ICryptoCertificate self, byte[] serialNumber, int[] length)
                    {
                        return obj.getSerialNumber(serialNumber, length);
                    }
                };

                getOwnerName = new Callback_getOwnerName() {
                    @Override
                    public int invoke(ICryptoCertificate self, com.sun.jna.Pointer ownerName, int[] length, String user_dn)
                    {
                        return obj.getOwnerName(ownerName, length, user_dn);
                    }
                };

                getKeyContainer = new Callback_getKeyContainer() {
                    @Override
                    public int invoke(ICryptoCertificate self, com.sun.jna.Pointer container, int[] length)
                    {
                        return obj.getKeyContainer(container, length);
                    }
                };
            }

            public VTable()
            {
            }

            public Callback_getObjectInfo getObjectInfo;
            public Callback_loadFromFile loadFromFile;
            public Callback_loadFromBuffer loadFromBuffer;
            public Callback_loadFromBinaryBuffer loadFromBinaryBuffer;
            public Callback_loadFromRepository loadFromRepository;
            public Callback_saveToFile saveToFile;
            public Callback_saveToBuffer saveToBuffer;
            public Callback_saveToBinaryBuffer saveToBinaryBuffer;
            public Callback_verifyCertificate verifyCertificate;
            public Callback_getId getId;
            public Callback_getIssuerName getIssuerName;
            public Callback_getPublicKey getPublicKey;
            public Callback_getPublicKeyMethod getPublicKeyMethod;
            public Callback_createPublicKeyFromCertificate createPublicKeyFromCertificate;
            public Callback_deleteCertificatePublicKey deleteCertificatePublicKey;
            public Callback_getSerialNumber getSerialNumber;
            public Callback_getOwnerName getOwnerName;
            public Callback_getKeyContainer getKeyContainer;

            @Override
            protected java.util.List<String> getFieldOrder()
            {
                java.util.List<String> fields = super.getFieldOrder();
                fields.addAll(java.util.Arrays.asList("getObjectInfo", "loadFromFile", "loadFromBuffer", "loadFromBinaryBuffer", "loadFromRepository", "saveToFile", "saveToBuffer", "saveToBinaryBuffer", "verifyCertificate", "getId", "getIssuerName", "getPublicKey", "getPublicKeyMethod", "createPublicKeyFromCertificate", "deleteCertificatePublicKey", "getSerialNumber", "getOwnerName", "getKeyContainer"));
                return fields;
            }
        }

        public ICryptoCertificate()
        {
        }

        public ICryptoCertificate(ICryptoCertificateIntf obj)
        {
            vTable = new VTable(obj);
            vTable.write();
            cloopVTable = vTable.getPointer();
            write();
        }

        @Override
        protected VTable createVTable()
        {
            return new VTable(cloopVTable);
        }

        public com.sun.jna.Pointer getObjectInfo()
        {
            VTable vTable = getVTable();
            com.sun.jna.Pointer result = vTable.getObjectInfo.invoke(this);
            return result;
        }

        public int loadFromFile(String fileName)
        {
            VTable vTable = getVTable();
            int result = vTable.loadFromFile.invoke(this, fileName);
            return result;
        }

        public int loadFromBuffer(com.sun.jna.Pointer buffer, int length)
        {
            VTable vTable = getVTable();
            int result = vTable.loadFromBuffer.invoke(this, buffer, length);
            return result;
        }

        public int loadFromBinaryBuffer(com.sun.jna.Pointer buffer, int length)
        {
            VTable vTable = getVTable();
            int result = vTable.loadFromBinaryBuffer.invoke(this, buffer, length);
            return result;
        }

        public int loadFromRepository(ICryptoRepository repository, String name)
        {
            VTable vTable = getVTable();
            int result = vTable.loadFromRepository.invoke(this, repository, name);
            return result;
        }

        public int saveToFile(String fileName)
        {
            VTable vTable = getVTable();
            int result = vTable.saveToFile.invoke(this, fileName);
            return result;
        }

        public int saveToBuffer(com.sun.jna.Pointer buffer, int length, int[] realLength)
        {
            VTable vTable = getVTable();
            int result = vTable.saveToBuffer.invoke(this, buffer, length, realLength);
            return result;
        }

        public int saveToBinaryBuffer(com.sun.jna.Pointer buffer, int length, int[] realLength)
        {
            VTable vTable = getVTable();
            int result = vTable.saveToBinaryBuffer.invoke(this, buffer, length, realLength);
            return result;
        }

        public int verifyCertificate(ICryptoCertificate certificate)
        {
            VTable vTable = getVTable();
            int result = vTable.verifyCertificate.invoke(this, certificate);
            return result;
        }

        public int getId(byte[] id, int[] length)
        {
            VTable vTable = getVTable();
            int result = vTable.getId.invoke(this, id, length);
            return result;
        }

        public int getIssuerName(byte[] issuerName, int[] length)
        {
            VTable vTable = getVTable();
            int result = vTable.getIssuerName.invoke(this, issuerName, length);
            return result;
        }

        public int getPublicKey(ICryptoKey key)
        {
            VTable vTable = getVTable();
            int result = vTable.getPublicKey.invoke(this, key);
            return result;
        }

        public int getPublicKeyMethod(com.sun.jna.Pointer method, int[] length)
        {
            VTable vTable = getVTable();
            int result = vTable.getPublicKeyMethod.invoke(this, method, length);
            return result;
        }

        public int createPublicKeyFromCertificate(ICryptoRepository repository, ICryptoKey[] key)
        {
            VTable vTable = getVTable();
            int result = vTable.createPublicKeyFromCertificate.invoke(this, repository, key);
            return result;
        }

        public int deleteCertificatePublicKey(ICryptoKey key)
        {
            VTable vTable = getVTable();
            int result = vTable.deleteCertificatePublicKey.invoke(this, key);
            return result;
        }

        public int getSerialNumber(byte[] serialNumber, int[] length)
        {
            VTable vTable = getVTable();
            int result = vTable.getSerialNumber.invoke(this, serialNumber, length);
            return result;
        }

        public int getOwnerName(com.sun.jna.Pointer ownerName, int[] length, String user_dn)
        {
            VTable vTable = getVTable();
            int result = vTable.getOwnerName.invoke(this, ownerName, length, user_dn);
            return result;
        }

        public int getKeyContainer(com.sun.jna.Pointer container, int[] length)
        {
            VTable vTable = getVTable();
            int result = vTable.getKeyContainer.invoke(this, container, length);
            return result;
        }
    }

    public static class ICryptoRepository extends IVersioned implements ICryptoRepositoryIntf
    {
        public static class VTable extends IVersioned.VTable
        {
            public static interface Callback_getObjectInfo extends com.sun.jna.Callback
            {
                public com.sun.jna.Pointer invoke(ICryptoRepository self);
            }

            public static interface Callback_getRepositoryName extends com.sun.jna.Callback
            {
                public int invoke(ICryptoRepository self, com.sun.jna.Pointer name, int length, int[] realLength);
            }

            public static interface Callback_open extends com.sun.jna.Callback
            {
                public int invoke(ICryptoRepository self, String path, int openMode, int repositoryLocation, int providerType);
            }

            public static interface Callback_close extends com.sun.jna.Callback
            {
                public int invoke(ICryptoRepository self);
            }

            public static interface Callback_createPublicKey extends com.sun.jna.Callback
            {
                public int invoke(ICryptoRepository self, int method, ICryptoKey[] key);
            }

            public static interface Callback_getPublicKey extends com.sun.jna.Callback
            {
                public int invoke(ICryptoRepository self, ICryptoKey key);
            }

            public static interface Callback_deletePublicKey extends com.sun.jna.Callback
            {
                public int invoke(ICryptoRepository self, ICryptoKey key);
            }

            public static interface Callback_isOpened extends com.sun.jna.Callback
            {
                public boolean invoke(ICryptoRepository self);
            }

            public VTable(com.sun.jna.Pointer pointer)
            {
                super(pointer);
            }

            public VTable(final ICryptoRepositoryIntf obj)
            {
                super(obj);

                getObjectInfo = new Callback_getObjectInfo() {
                    @Override
                    public com.sun.jna.Pointer invoke(ICryptoRepository self)
                    {
                        return obj.getObjectInfo();
                    }
                };

                getRepositoryName = new Callback_getRepositoryName() {
                    @Override
                    public int invoke(ICryptoRepository self, com.sun.jna.Pointer name, int length, int[] realLength)
                    {
                        return obj.getRepositoryName(name, length, realLength);
                    }
                };

                open = new Callback_open() {
                    @Override
                    public int invoke(ICryptoRepository self, String path, int openMode, int repositoryLocation, int providerType)
                    {
                        return obj.open(path, openMode, repositoryLocation, providerType);
                    }
                };

                close = new Callback_close() {
                    @Override
                    public int invoke(ICryptoRepository self)
                    {
                        return obj.close();
                    }
                };

                createPublicKey = new Callback_createPublicKey() {
                    @Override
                    public int invoke(ICryptoRepository self, int method, ICryptoKey[] key)
                    {
                        return obj.createPublicKey(method, key);
                    }
                };

                getPublicKey = new Callback_getPublicKey() {
                    @Override
                    public int invoke(ICryptoRepository self, ICryptoKey key)
                    {
                        return obj.getPublicKey(key);
                    }
                };

                deletePublicKey = new Callback_deletePublicKey() {
                    @Override
                    public int invoke(ICryptoRepository self, ICryptoKey key)
                    {
                        return obj.deletePublicKey(key);
                    }
                };

                isOpened = new Callback_isOpened() {
                    @Override
                    public boolean invoke(ICryptoRepository self)
                    {
                        return obj.isOpened();
                    }
                };
            }

            public VTable()
            {
            }

            public Callback_getObjectInfo getObjectInfo;
            public Callback_getRepositoryName getRepositoryName;
            public Callback_open open;
            public Callback_close close;
            public Callback_createPublicKey createPublicKey;
            public Callback_getPublicKey getPublicKey;
            public Callback_deletePublicKey deletePublicKey;
            public Callback_isOpened isOpened;

            @Override
            protected java.util.List<String> getFieldOrder()
            {
                java.util.List<String> fields = super.getFieldOrder();
                fields.addAll(java.util.Arrays.asList("getObjectInfo", "getRepositoryName", "open", "close", "createPublicKey", "getPublicKey", "deletePublicKey", "isOpened"));
                return fields;
            }
        }

        public ICryptoRepository()
        {
        }

        public ICryptoRepository(ICryptoRepositoryIntf obj)
        {
            vTable = new VTable(obj);
            vTable.write();
            cloopVTable = vTable.getPointer();
            write();
        }

        @Override
        protected VTable createVTable()
        {
            return new VTable(cloopVTable);
        }

        public com.sun.jna.Pointer getObjectInfo()
        {
            VTable vTable = getVTable();
            com.sun.jna.Pointer result = vTable.getObjectInfo.invoke(this);
            return result;
        }

        public int getRepositoryName(com.sun.jna.Pointer name, int length, int[] realLength)
        {
            VTable vTable = getVTable();
            int result = vTable.getRepositoryName.invoke(this, name, length, realLength);
            return result;
        }

        public int open(String path, int openMode, int repositoryLocation, int providerType)
        {
            VTable vTable = getVTable();
            int result = vTable.open.invoke(this, path, openMode, repositoryLocation, providerType);
            return result;
        }

        public int close()
        {
            VTable vTable = getVTable();
            int result = vTable.close.invoke(this);
            return result;
        }

        public int createPublicKey(int method, ICryptoKey[] key)
        {
            VTable vTable = getVTable();
            int result = vTable.createPublicKey.invoke(this, method, key);
            return result;
        }

        public int getPublicKey(ICryptoKey key)
        {
            VTable vTable = getVTable();
            int result = vTable.getPublicKey.invoke(this, key);
            return result;
        }

        public int deletePublicKey(ICryptoKey key)
        {
            VTable vTable = getVTable();
            int result = vTable.deletePublicKey.invoke(this, key);
            return result;
        }

        public boolean isOpened()
        {
            VTable vTable = getVTable();
            boolean result = vTable.isOpened.invoke(this);
            return result;
        }
    }

    public static class ICryptoProvider extends IVersioned implements ICryptoProviderIntf
    {
        public static class VTable extends IVersioned.VTable
        {
            public static interface Callback_getObjectInfo extends com.sun.jna.Callback
            {
                public com.sun.jna.Pointer invoke(ICryptoProvider self);
            }

            public static interface Callback_createRepository extends com.sun.jna.Callback
            {
                public int invoke(ICryptoProvider self, ICryptoRepository[] repository, int type, String pin);
            }

            public static interface Callback_deleteRepository extends com.sun.jna.Callback
            {
                public int invoke(ICryptoProvider self, ICryptoRepository repository);
            }

            public VTable(com.sun.jna.Pointer pointer)
            {
                super(pointer);
            }

            public VTable(final ICryptoProviderIntf obj)
            {
                super(obj);

                getObjectInfo = new Callback_getObjectInfo() {
                    @Override
                    public com.sun.jna.Pointer invoke(ICryptoProvider self)
                    {
                        return obj.getObjectInfo();
                    }
                };

                createRepository = new Callback_createRepository() {
                    @Override
                    public int invoke(ICryptoProvider self, ICryptoRepository[] repository, int type, String pin)
                    {
                        return obj.createRepository(repository, type, pin);
                    }
                };

                deleteRepository = new Callback_deleteRepository() {
                    @Override
                    public int invoke(ICryptoProvider self, ICryptoRepository repository)
                    {
                        return obj.deleteRepository(repository);
                    }
                };
            }

            public VTable()
            {
            }

            public Callback_getObjectInfo getObjectInfo;
            public Callback_createRepository createRepository;
            public Callback_deleteRepository deleteRepository;

            @Override
            protected java.util.List<String> getFieldOrder()
            {
                java.util.List<String> fields = super.getFieldOrder();
                fields.addAll(java.util.Arrays.asList("getObjectInfo", "createRepository", "deleteRepository"));
                return fields;
            }
        }

        public ICryptoProvider()
        {
        }

        public ICryptoProvider(ICryptoProviderIntf obj)
        {
            vTable = new VTable(obj);
            vTable.write();
            cloopVTable = vTable.getPointer();
            write();
        }

        @Override
        protected VTable createVTable()
        {
            return new VTable(cloopVTable);
        }

        public com.sun.jna.Pointer getObjectInfo()
        {
            VTable vTable = getVTable();
            com.sun.jna.Pointer result = vTable.getObjectInfo.invoke(this);
            return result;
        }

        public int createRepository(ICryptoRepository[] repository, int type, String pin)
        {
            VTable vTable = getVTable();
            int result = vTable.createRepository.invoke(this, repository, type, pin);
            return result;
        }

        public int deleteRepository(ICryptoRepository repository)
        {
            VTable vTable = getVTable();
            int result = vTable.deleteRepository.invoke(this, repository);
            return result;
        }
    }

    public static class IListCryptoObjects extends IVersioned implements IListCryptoObjectsIntf
    {
        public static class VTable extends IVersioned.VTable
        {
            public static interface Callback_list extends com.sun.jna.Callback
            {
                public void invoke(IListCryptoObjects self, CryptoObjectInfo[] objInfo);
            }

            public VTable(com.sun.jna.Pointer pointer)
            {
                super(pointer);
            }

            public VTable(final IListCryptoObjectsIntf obj)
            {
                super(obj);

                list = new Callback_list() {
                    @Override
                    public void invoke(IListCryptoObjects self, CryptoObjectInfo[] objInfo)
                    {
                        obj.list(objInfo);
                    }
                };
            }

            public VTable()
            {
            }

            public Callback_list list;

            @Override
            protected java.util.List<String> getFieldOrder()
            {
                java.util.List<String> fields = super.getFieldOrder();
                fields.addAll(java.util.Arrays.asList("list"));
                return fields;
            }
        }

        public IListCryptoObjects()
        {
        }

        public IListCryptoObjects(IListCryptoObjectsIntf obj)
        {
            vTable = new VTable(obj);
            vTable.write();
            cloopVTable = vTable.getPointer();
            write();
        }

        @Override
        protected VTable createVTable()
        {
            return new VTable(cloopVTable);
        }

        public void list(CryptoObjectInfo[] objInfo)
        {
            VTable vTable = getVTable();
            vTable.list.invoke(this, objInfo);
        }
    }

    public static class ICryptoFactory extends IPluginBase implements ICryptoFactoryIntf
    {
        public static class VTable extends IPluginBase.VTable
        {
            public static interface Callback_setTrace extends com.sun.jna.Callback
            {
                public void invoke(ICryptoFactory self, boolean need);
            }

            public static interface Callback_getCryptoProvider extends com.sun.jna.Callback
            {
                public ICryptoProvider invoke(ICryptoFactory self, CryptoObjectInfo[] objInfo);
            }

            public static interface Callback_getCryptoRandomFactory extends com.sun.jna.Callback
            {
                public ICryptoRandomFactory invoke(ICryptoFactory self, CryptoObjectInfo[] objInfo);
            }

            public static interface Callback_getCryptoHashFactory extends com.sun.jna.Callback
            {
                public ICryptoHashFactory invoke(ICryptoFactory self, CryptoObjectInfo[] objInfo);
            }

            public static interface Callback_getCryptoSymmetricFactory extends com.sun.jna.Callback
            {
                public ICryptoSymmetricFactory invoke(ICryptoFactory self, CryptoObjectInfo[] objInfo);
            }

            public static interface Callback_getCryptoSignatureFactory extends com.sun.jna.Callback
            {
                public ICryptoSignatureFactory invoke(ICryptoFactory self, CryptoObjectInfo[] objInfo);
            }

            public static interface Callback_getCryptoCertificateFactory extends com.sun.jna.Callback
            {
                public ICryptoCertificateFactory invoke(ICryptoFactory self, CryptoObjectInfo[] objInfo);
            }

            public static interface Callback_getCryptoObjects extends com.sun.jna.Callback
            {
                public int invoke(ICryptoFactory self, int type, IListCryptoObjects callback);
            }

            public VTable(com.sun.jna.Pointer pointer)
            {
                super(pointer);
            }

            public VTable(final ICryptoFactoryIntf obj)
            {
                super(obj);

                setTrace = new Callback_setTrace() {
                    @Override
                    public void invoke(ICryptoFactory self, boolean need)
                    {
                        obj.setTrace(need);
                    }
                };

                getCryptoProvider = new Callback_getCryptoProvider() {
                    @Override
                    public ICryptoProvider invoke(ICryptoFactory self, CryptoObjectInfo[] objInfo)
                    {
                        return obj.getCryptoProvider(objInfo);
                    }
                };

                getCryptoRandomFactory = new Callback_getCryptoRandomFactory() {
                    @Override
                    public ICryptoRandomFactory invoke(ICryptoFactory self, CryptoObjectInfo[] objInfo)
                    {
                        return obj.getCryptoRandomFactory(objInfo);
                    }
                };

                getCryptoHashFactory = new Callback_getCryptoHashFactory() {
                    @Override
                    public ICryptoHashFactory invoke(ICryptoFactory self, CryptoObjectInfo[] objInfo)
                    {
                        return obj.getCryptoHashFactory(objInfo);
                    }
                };

                getCryptoSymmetricFactory = new Callback_getCryptoSymmetricFactory() {
                    @Override
                    public ICryptoSymmetricFactory invoke(ICryptoFactory self, CryptoObjectInfo[] objInfo)
                    {
                        return obj.getCryptoSymmetricFactory(objInfo);
                    }
                };

                getCryptoSignatureFactory = new Callback_getCryptoSignatureFactory() {
                    @Override
                    public ICryptoSignatureFactory invoke(ICryptoFactory self, CryptoObjectInfo[] objInfo)
                    {
                        return obj.getCryptoSignatureFactory(objInfo);
                    }
                };

                getCryptoCertificateFactory = new Callback_getCryptoCertificateFactory() {
                    @Override
                    public ICryptoCertificateFactory invoke(ICryptoFactory self, CryptoObjectInfo[] objInfo)
                    {
                        return obj.getCryptoCertificateFactory(objInfo);
                    }
                };

                getCryptoObjects = new Callback_getCryptoObjects() {
                    @Override
                    public int invoke(ICryptoFactory self, int type, IListCryptoObjects callback)
                    {
                        return obj.getCryptoObjects(type, callback);
                    }
                };
            }

            public VTable()
            {
            }

            public Callback_setTrace setTrace;
            public Callback_getCryptoProvider getCryptoProvider;
            public Callback_getCryptoRandomFactory getCryptoRandomFactory;
            public Callback_getCryptoHashFactory getCryptoHashFactory;
            public Callback_getCryptoSymmetricFactory getCryptoSymmetricFactory;
            public Callback_getCryptoSignatureFactory getCryptoSignatureFactory;
            public Callback_getCryptoCertificateFactory getCryptoCertificateFactory;
            public Callback_getCryptoObjects getCryptoObjects;

            @Override
            protected java.util.List<String> getFieldOrder()
            {
                java.util.List<String> fields = super.getFieldOrder();
                fields.addAll(java.util.Arrays.asList("setTrace", "getCryptoProvider", "getCryptoRandomFactory", "getCryptoHashFactory", "getCryptoSymmetricFactory", "getCryptoSignatureFactory", "getCryptoCertificateFactory", "getCryptoObjects"));
                return fields;
            }
        }

        public ICryptoFactory()
        {
        }

        public ICryptoFactory(ICryptoFactoryIntf obj)
        {
            vTable = new VTable(obj);
            vTable.write();
            cloopVTable = vTable.getPointer();
            write();
        }

        @Override
        protected VTable createVTable()
        {
            return new VTable(cloopVTable);
        }

        public void setTrace(boolean need)
        {
            VTable vTable = getVTable();
            vTable.setTrace.invoke(this, need);
        }

        public ICryptoProvider getCryptoProvider(CryptoObjectInfo[] objInfo)
        {
            VTable vTable = getVTable();
            ICryptoProvider result = vTable.getCryptoProvider.invoke(this, objInfo);
            return result;
        }

        public ICryptoRandomFactory getCryptoRandomFactory(CryptoObjectInfo[] objInfo)
        {
            VTable vTable = getVTable();
            ICryptoRandomFactory result = vTable.getCryptoRandomFactory.invoke(this, objInfo);
            return result;
        }

        public ICryptoHashFactory getCryptoHashFactory(CryptoObjectInfo[] objInfo)
        {
            VTable vTable = getVTable();
            ICryptoHashFactory result = vTable.getCryptoHashFactory.invoke(this, objInfo);
            return result;
        }

        public ICryptoSymmetricFactory getCryptoSymmetricFactory(CryptoObjectInfo[] objInfo)
        {
            VTable vTable = getVTable();
            ICryptoSymmetricFactory result = vTable.getCryptoSymmetricFactory.invoke(this, objInfo);
            return result;
        }

        public ICryptoSignatureFactory getCryptoSignatureFactory(CryptoObjectInfo[] objInfo)
        {
            VTable vTable = getVTable();
            ICryptoSignatureFactory result = vTable.getCryptoSignatureFactory.invoke(this, objInfo);
            return result;
        }

        public ICryptoCertificateFactory getCryptoCertificateFactory(CryptoObjectInfo[] objInfo)
        {
            VTable vTable = getVTable();
            ICryptoCertificateFactory result = vTable.getCryptoCertificateFactory.invoke(this, objInfo);
            return result;
        }

        public int getCryptoObjects(int type, IListCryptoObjects callback)
        {
            VTable vTable = getVTable();
            int result = vTable.getCryptoObjects.invoke(this, type, callback);
            return result;
        }
    }

    public static class ILdapPlugin extends IReferenceCounted implements ILdapPluginIntf
    {
        public static class VTable extends IReferenceCounted.VTable
        {
            public static interface Callback_connect extends com.sun.jna.Callback
            {
                public void invoke(ILdapPlugin self);
            }

            public static interface Callback_is_connected extends com.sun.jna.Callback
            {
                public boolean invoke(ILdapPlugin self);
            }

            public static interface Callback_bind extends com.sun.jna.Callback
            {
                public boolean invoke(ILdapPlugin self);
            }

            public static interface Callback_bind_as extends com.sun.jna.Callback
            {
                public boolean invoke(ILdapPlugin self, String user, String password);
            }

            public static interface Callback_find_user extends com.sun.jna.Callback
            {
                public boolean invoke(ILdapPlugin self, String name, com.sun.jna.Pointer password, com.sun.jna.Pointer mf_password, com.sun.jna.Pointer hash_alg);
            }

            public static interface Callback_find_srp_user extends com.sun.jna.Callback
            {
                public boolean invoke(ILdapPlugin self, String name, com.sun.jna.Pointer verifier, com.sun.jna.Pointer salt);
            }

            public static interface Callback_get_certificate extends com.sun.jna.Callback
            {
                public int invoke(ILdapPlugin self, String name, com.sun.jna.Pointer buffer, int[] buffer_length, String attr_name);
            }

            public static interface Callback_get_user_attr extends com.sun.jna.Callback
            {
                public boolean invoke(ILdapPlugin self, String name, String attr, com.sun.jna.Pointer value);
            }

            public static interface Callback_get_policy extends com.sun.jna.Callback
            {
                public boolean invoke(ILdapPlugin self, String name, com.sun.jna.Pointer policy, ISC_TIMESTAMP[] passwd_time, int[] failed_count, ISC_TIMESTAMP[] access_time);
            }

            public static interface Callback_set_policy extends com.sun.jna.Callback
            {
                public boolean invoke(ILdapPlugin self, String name, String policy, ISC_TIMESTAMP[] passwd_time, int[] failed_count, ISC_TIMESTAMP[] access_time);
            }

            public static interface Callback_get_password_history extends com.sun.jna.Callback
            {
                public boolean invoke(ILdapPlugin self, String name, com.sun.jna.Pointer buffer, int[] buffer_length);
            }

            public static interface Callback_find_user_groups extends com.sun.jna.Callback
            {
                public void invoke(ILdapPlugin self, com.sun.jna.Pointer userId);
            }

            public static interface Callback_change_legacy_password extends com.sun.jna.Callback
            {
                public int invoke(ILdapPlugin self, String name, String password);
            }

            public static interface Callback_change_mf_password extends com.sun.jna.Callback
            {
                public int invoke(ILdapPlugin self, String name, String password, com.sun.jna.Pointer hash);
            }

            public static interface Callback_change_srp_password extends com.sun.jna.Callback
            {
                public int invoke(ILdapPlugin self, String name, String password, com.sun.jna.Pointer verifier, com.sun.jna.Pointer salt);
            }

            public VTable(com.sun.jna.Pointer pointer)
            {
                super(pointer);
            }

            public VTable(final ILdapPluginIntf obj)
            {
                super(obj);

                connect = new Callback_connect() {
                    @Override
                    public void invoke(ILdapPlugin self)
                    {
                        obj.connect();
                    }
                };

                is_connected = new Callback_is_connected() {
                    @Override
                    public boolean invoke(ILdapPlugin self)
                    {
                        return obj.is_connected();
                    }
                };

                bind = new Callback_bind() {
                    @Override
                    public boolean invoke(ILdapPlugin self)
                    {
                        return obj.bind();
                    }
                };

                bind_as = new Callback_bind_as() {
                    @Override
                    public boolean invoke(ILdapPlugin self, String user, String password)
                    {
                        return obj.bind_as(user, password);
                    }
                };

                find_user = new Callback_find_user() {
                    @Override
                    public boolean invoke(ILdapPlugin self, String name, com.sun.jna.Pointer password, com.sun.jna.Pointer mf_password, com.sun.jna.Pointer hash_alg)
                    {
                        return obj.find_user(name, password, mf_password, hash_alg);
                    }
                };

                find_srp_user = new Callback_find_srp_user() {
                    @Override
                    public boolean invoke(ILdapPlugin self, String name, com.sun.jna.Pointer verifier, com.sun.jna.Pointer salt)
                    {
                        return obj.find_srp_user(name, verifier, salt);
                    }
                };

                get_certificate = new Callback_get_certificate() {
                    @Override
                    public int invoke(ILdapPlugin self, String name, com.sun.jna.Pointer buffer, int[] buffer_length, String attr_name)
                    {
                        return obj.get_certificate(name, buffer, buffer_length, attr_name);
                    }
                };

                get_user_attr = new Callback_get_user_attr() {
                    @Override
                    public boolean invoke(ILdapPlugin self, String name, String attr, com.sun.jna.Pointer value)
                    {
                        return obj.get_user_attr(name, attr, value);
                    }
                };

                get_policy = new Callback_get_policy() {
                    @Override
                    public boolean invoke(ILdapPlugin self, String name, com.sun.jna.Pointer policy, ISC_TIMESTAMP[] passwd_time, int[] failed_count, ISC_TIMESTAMP[] access_time)
                    {
                        return obj.get_policy(name, policy, passwd_time, failed_count, access_time);
                    }
                };

                set_policy = new Callback_set_policy() {
                    @Override
                    public boolean invoke(ILdapPlugin self, String name, String policy, ISC_TIMESTAMP[] passwd_time, int[] failed_count, ISC_TIMESTAMP[] access_time)
                    {
                        return obj.set_policy(name, policy, passwd_time, failed_count, access_time);
                    }
                };

                get_password_history = new Callback_get_password_history() {
                    @Override
                    public boolean invoke(ILdapPlugin self, String name, com.sun.jna.Pointer buffer, int[] buffer_length)
                    {
                        return obj.get_password_history(name, buffer, buffer_length);
                    }
                };

                find_user_groups = new Callback_find_user_groups() {
                    @Override
                    public void invoke(ILdapPlugin self, com.sun.jna.Pointer userId)
                    {
                        obj.find_user_groups(userId);
                    }
                };

                change_legacy_password = new Callback_change_legacy_password() {
                    @Override
                    public int invoke(ILdapPlugin self, String name, String password)
                    {
                        return obj.change_legacy_password(name, password);
                    }
                };

                change_mf_password = new Callback_change_mf_password() {
                    @Override
                    public int invoke(ILdapPlugin self, String name, String password, com.sun.jna.Pointer hash)
                    {
                        return obj.change_mf_password(name, password, hash);
                    }
                };

                change_srp_password = new Callback_change_srp_password() {
                    @Override
                    public int invoke(ILdapPlugin self, String name, String password, com.sun.jna.Pointer verifier, com.sun.jna.Pointer salt)
                    {
                        return obj.change_srp_password(name, password, verifier, salt);
                    }
                };
            }

            public VTable()
            {
            }

            public Callback_connect connect;
            public Callback_is_connected is_connected;
            public Callback_bind bind;
            public Callback_bind_as bind_as;
            public Callback_find_user find_user;
            public Callback_find_srp_user find_srp_user;
            public Callback_get_certificate get_certificate;
            public Callback_get_user_attr get_user_attr;
            public Callback_get_policy get_policy;
            public Callback_set_policy set_policy;
            public Callback_get_password_history get_password_history;
            public Callback_find_user_groups find_user_groups;
            public Callback_change_legacy_password change_legacy_password;
            public Callback_change_mf_password change_mf_password;
            public Callback_change_srp_password change_srp_password;

            @Override
            protected java.util.List<String> getFieldOrder()
            {
                java.util.List<String> fields = super.getFieldOrder();
                fields.addAll(java.util.Arrays.asList("connect", "is_connected", "bind", "bind_as", "find_user", "find_srp_user", "get_certificate", "get_user_attr", "get_policy", "set_policy", "get_password_history", "find_user_groups", "change_legacy_password", "change_mf_password", "change_srp_password"));
                return fields;
            }
        }

        public ILdapPlugin()
        {
        }

        public ILdapPlugin(ILdapPluginIntf obj)
        {
            vTable = new VTable(obj);
            vTable.write();
            cloopVTable = vTable.getPointer();
            write();
        }

        @Override
        protected VTable createVTable()
        {
            return new VTable(cloopVTable);
        }

        public void connect()
        {
            VTable vTable = getVTable();
            vTable.connect.invoke(this);
        }

        public boolean is_connected()
        {
            VTable vTable = getVTable();
            boolean result = vTable.is_connected.invoke(this);
            return result;
        }

        public boolean bind()
        {
            VTable vTable = getVTable();
            boolean result = vTable.bind.invoke(this);
            return result;
        }

        public boolean bind_as(String user, String password)
        {
            VTable vTable = getVTable();
            boolean result = vTable.bind_as.invoke(this, user, password);
            return result;
        }

        public boolean find_user(String name, com.sun.jna.Pointer password, com.sun.jna.Pointer mf_password, com.sun.jna.Pointer hash_alg)
        {
            VTable vTable = getVTable();
            boolean result = vTable.find_user.invoke(this, name, password, mf_password, hash_alg);
            return result;
        }

        public boolean find_srp_user(String name, com.sun.jna.Pointer verifier, com.sun.jna.Pointer salt)
        {
            VTable vTable = getVTable();
            boolean result = vTable.find_srp_user.invoke(this, name, verifier, salt);
            return result;
        }

        public int get_certificate(String name, com.sun.jna.Pointer buffer, int[] buffer_length, String attr_name)
        {
            VTable vTable = getVTable();
            int result = vTable.get_certificate.invoke(this, name, buffer, buffer_length, attr_name);
            return result;
        }

        public boolean get_user_attr(String name, String attr, com.sun.jna.Pointer value)
        {
            VTable vTable = getVTable();
            boolean result = vTable.get_user_attr.invoke(this, name, attr, value);
            return result;
        }

        public boolean get_policy(String name, com.sun.jna.Pointer policy, ISC_TIMESTAMP[] passwd_time, int[] failed_count, ISC_TIMESTAMP[] access_time)
        {
            VTable vTable = getVTable();
            boolean result = vTable.get_policy.invoke(this, name, policy, passwd_time, failed_count, access_time);
            return result;
        }

        public boolean set_policy(String name, String policy, ISC_TIMESTAMP[] passwd_time, int[] failed_count, ISC_TIMESTAMP[] access_time)
        {
            VTable vTable = getVTable();
            boolean result = vTable.set_policy.invoke(this, name, policy, passwd_time, failed_count, access_time);
            return result;
        }

        public boolean get_password_history(String name, com.sun.jna.Pointer buffer, int[] buffer_length)
        {
            VTable vTable = getVTable();
            boolean result = vTable.get_password_history.invoke(this, name, buffer, buffer_length);
            return result;
        }

        public void find_user_groups(com.sun.jna.Pointer userId)
        {
            VTable vTable = getVTable();
            vTable.find_user_groups.invoke(this, userId);
        }

        public int change_legacy_password(String name, String password)
        {
            VTable vTable = getVTable();
            int result = vTable.change_legacy_password.invoke(this, name, password);
            return result;
        }

        public int change_mf_password(String name, String password, com.sun.jna.Pointer hash)
        {
            VTable vTable = getVTable();
            int result = vTable.change_mf_password.invoke(this, name, password, hash);
            return result;
        }

        public int change_srp_password(String name, String password, com.sun.jna.Pointer verifier, com.sun.jna.Pointer salt)
        {
            VTable vTable = getVTable();
            int result = vTable.change_srp_password.invoke(this, name, password, verifier, salt);
            return result;
        }
    }

    public static class ILdapFactory extends IPluginBase implements ILdapFactoryIntf
    {
        public static class VTable extends IPluginBase.VTable
        {
            public static interface Callback_getLdapPlugin extends com.sun.jna.Callback
            {
                public ILdapPlugin invoke(ILdapFactory self, IStatus status);
            }

            public VTable(com.sun.jna.Pointer pointer)
            {
                super(pointer);
            }

            public VTable(final ILdapFactoryIntf obj)
            {
                super(obj);

                getLdapPlugin = new Callback_getLdapPlugin() {
                    @Override
                    public ILdapPlugin invoke(ILdapFactory self, IStatus status)
                    {
                        try
                        {
                            return obj.getLdapPlugin(status);
                        }
                        catch (Throwable t)
                        {
                            FbException.catchException(status, t);
                            return null;
                        }
                    }
                };
            }

            public VTable()
            {
            }

            public Callback_getLdapPlugin getLdapPlugin;

            @Override
            protected java.util.List<String> getFieldOrder()
            {
                java.util.List<String> fields = super.getFieldOrder();
                fields.addAll(java.util.Arrays.asList("getLdapPlugin"));
                return fields;
            }
        }

        public ILdapFactory()
        {
        }

        public ILdapFactory(ILdapFactoryIntf obj)
        {
            vTable = new VTable(obj);
            vTable.write();
            cloopVTable = vTable.getPointer();
            write();
        }

        @Override
        protected VTable createVTable()
        {
            return new VTable(cloopVTable);
        }

        public ILdapPlugin getLdapPlugin(IStatus status) throws FbException
        {
            VTable vTable = getVTable();
            ILdapPlugin result = vTable.getLdapPlugin.invoke(this, status);
            FbException.checkException(status);
            return result;
        }
    }
}