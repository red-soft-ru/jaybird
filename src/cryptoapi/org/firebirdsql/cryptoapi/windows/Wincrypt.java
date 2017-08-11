package org.firebirdsql.cryptoapi.windows;

/**
 * @author roman.kisluhin
 * @version 1.0
 *          Date: 17.03.2011
 *          Time: 20:43:21
 */
@SuppressWarnings({"UnusedDeclaration"})
public class Wincrypt {
  public static int CRYPT_ASN_ENCODING = 0x00000001;
  public static int CRYPT_NDR_ENCODING = 0x00000002;
  public static int X509_ASN_ENCODING = 0x00000001;
  public static int X509_NDR_ENCODING = 0x00000002;
  public static int PKCS_7_ASN_ENCODING = 0x00010000;
  public static int PKCS_7_NDR_ENCODING = 0x00020000;

  public static int PROV_RSA_FULL = 1;
  public static int PROV_RSA_SIG = 2;
  public static int PROV_DSS = 3;
  public static int PROV_FORTEZZA = 4;
  public static int PROV_MS_EXCHANGE = 5;
  public static int PROV_SSL = 6;
  public static int PROV_RSA_SCHANNEL = 12;
  public static int PROV_DSS_DH = 13;
  public static int PROV_EC_ECDSA_SIG = 14;
  public static int PROV_EC_ECNRA_SIG = 15;
  public static int PROV_EC_ECDSA_FULL = 16;
  public static int PROV_EC_ECNRA_FULL = 17;
  public static int PROV_DH_SCHANNEL = 18;
  public static int PROV_SPYRUS_LYNKS = 20;
  public static int PROV_RNG = 21;
  public static int PROV_INTEL_SEC = 22;
  public static int PROV_REPLACE_OWF = 23;
  public static int PROV_RSA_AES = 24;

  public static int CRYPT_VERIFYCONTEXT = 0xF0000000;
  public static int CRYPT_NEWKEYSET = 0x00000008;
  public static int CRYPT_DELETEKEYSET = 0x00000010;
  public static int CRYPT_MACHINE_KEYSET = 0x00000020;
  public static int CRYPT_SILENT = 0x00000040;

  public static int AT_KEYEXCHANGE   = 1;
  public static int AT_SIGNATURE     = 2;
  
  // Algorithm classes
  public static int ALG_CLASS_ANY = 0;
  public static int ALG_CLASS_SIGNATURE = 1 << 13;
  public static int ALG_CLASS_MSG_ENCRYPT = 2 << 13;
  public static int ALG_CLASS_DATA_ENCRYPT = 3 << 13;
  public static int ALG_CLASS_HASH = 4 << 13;
  public static int ALG_CLASS_KEY_EXCHANGE = 5 << 13;
  public static int ALG_CLASS_ALL = 7 << 13;

  // Algorithm types
  public static int ALG_TYPE_ANY = 0;
  public static int ALG_TYPE_DSS = 1 << 9;
  public static int ALG_TYPE_RSA = 2 << 9;
  public static int ALG_TYPE_BLOCK = 3 << 9;
  public static int ALG_TYPE_STREAM = 4 << 9;
  public static int ALG_TYPE_DH = 5 << 9;
  public static int ALG_TYPE_SECURECHANNEL = 6 << 9;

  // hash params
  public static int HP_ALGID = 0x0001;  // Hash algorithm
  public static int HP_HASHVAL = 0x0002;  // Hash value
  public static int HP_HASHSIZE = 0x0004;  // Hash value size
  public static int HP_HMAC_INFO = 0x0005;  // information for creating an HMAC
  public static int HP_TLS1PRF_LABEL = 0x0006;  // label for TLS1 PRF
  public static int HP_TLS1PRF_SEED = 0x0007;  // seed for TLS1 PRF

  // KP_MODE
  public static int CRYPT_MODE_CBC = 1;       // Cipher block chaining
  public static int CRYPT_MODE_ECB = 2;       // Electronic code book
  public static int CRYPT_MODE_OFB = 3;       // Output feedback mode
  public static int CRYPT_MODE_CFB = 4;       // Cipher feedback mode
  public static int CRYPT_MODE_CTS = 5;       // Ciphertext stealing mode

  // dwParam
  public static int KP_IV               = 1;       // Initialization vector
  public static int KP_SALT             = 2;       // Salt value
  public static int KP_PADDING          = 3;       // Padding values
  public static int KP_MODE             = 4;       // Mode of the cipher
  public static int KP_MODE_BITS        = 5;       // Number of bits to feedback
  public static int KP_PERMISSIONS      = 6;       // Key permissions DWORD
  public static int KP_ALGID            = 7;       // Key algorithm
  public static int KP_BLOCKLEN         = 8;       // Block size of the cipher
  public static int KP_KEYLEN           = 9;       // Length of key in bits
  public static int KP_SALT_EX = 10;      // Length of salt in bytes
  public static int KP_P = 11;      // DSS/Diffie-Hellman P value
  public static int KP_G = 12;      // DSS/Diffie-Hellman G value
  public static int KP_Q = 13;      // DSS Q value
  public static int KP_X = 14;      // Diffie-Hellman X value
  public static int KP_Y = 15;      // Y value
  public static int KP_RA = 16;      // Fortezza RA value
  public static int KP_RB = 17;      // Fortezza RB value
  public static int KP_INFO = 18;      // for putting information into an RSA envelope
  public static int KP_EFFECTIVE_KEYLEN = 19;      // setting and getting RC2 effective key length
  public static int KP_SCHANNEL_ALG = 20;      // for setting the Secure Channel algorithms
  public static int KP_CLIENT_RANDOM = 21;      // for setting the Secure Channel client random data
  public static int KP_SERVER_RANDOM = 22;      // for setting the Secure Channel server random data
  public static int KP_RP = 23;
  public static int KP_PRECOMP_MD5 = 24;
  public static int KP_PRECOMP_SHA = 25;
  public static int KP_CERTIFICATE = 26;      // for setting Secure Channel certificate data (PCT1)
  public static int KP_CLEAR_KEY = 27;      // for setting Secure Channel clear key data (PCT1)
  public static int KP_PUB_EX_LEN = 28;
  public static int KP_PUB_EX_VAL = 29;
  public static int KP_KEYVAL = 30;
  public static int KP_ADMIN_PIN = 31;
  public static int KP_KEYEXCHANGE_PIN = 32;
  public static int KP_SIGNATURE_PIN = 33;
  public static int KP_PREHASH = 34;
  public static int KP_ROUNDS = 35;
  public static int KP_OAEP_PARAMS = 36;      // for setting OAEP params on RSA keys
  public static int KP_CMS_KEY_INFO = 37;
  public static int KP_CMS_DH_KEY_INFO = 38;
  public static int KP_PUB_PARAMS = 39;      // for setting public parameters
  public static int KP_VERIFY_PARAMS = 40;      // for verifying DSA and DH parameters
  public static int KP_HIGHEST_VERSION = 41;      // for TLS protocol version setting
  public static int KP_GET_USE_COUNT = 42;      // for use with PP_CRYPT_COUNT_KEY_USE contexts

  //
  // When building a chain, the there are various parameters used for finding
  // issuing certificates and trust lists.  They are identified in the
  // following structure
  //

  // Default usage match type is AND with value zero
  public static int USAGE_MATCH_TYPE_AND = 0x00000000;
  public static int USAGE_MATCH_TYPE_OR = 0x00000001;

  //+-------------------------------------------------------------------------
  //  Certificate name types
  //--------------------------------------------------------------------------
  public static int CERT_NAME_EMAIL_TYPE = 1;
  public static int CERT_NAME_RDN_TYPE = 2;
  public static int CERT_NAME_ATTR_TYPE = 3;
  public static int CERT_NAME_SIMPLE_DISPLAY_TYPE = 4;
  public static int CERT_NAME_FRIENDLY_DISPLAY_TYPE = 5;
  public static int CERT_NAME_DNS_TYPE = 6;
  public static int CERT_NAME_URL_TYPE = 7;
  public static int CERT_NAME_UPN_TYPE = 8;

  //+-------------------------------------------------------------------------
  //  Certificate name flags
  //--------------------------------------------------------------------------
  public static int CERT_NAME_ISSUER_FLAG = 0x1;
  public static int CERT_NAME_DISABLE_IE4_UTF8_FLAG = 0x00010000;

  // Generic sub-ids
  public static int ALG_SID_ANY                    = 0;

  // Some RSA sub-ids
  public static int ALG_SID_RSA_ANY                = 0;
  public static int ALG_SID_RSA_PKCS               = 1;
  public static int ALG_SID_RSA_MSATWORK           = 2;
  public static int ALG_SID_RSA_ENTRUST            = 3;
  public static int ALG_SID_RSA_PGP                = 4;

  // Some DSS sub-ids
  //
  public static int ALG_SID_DSS_ANY                = 0;
  public static int ALG_SID_DSS_PKCS               = 1;
  public static int ALG_SID_DSS_DMS                = 2;

  // Block cipher sub ids
  // DES sub_ids
  public static int ALG_SID_DES                    = 1;
  public static int ALG_SID_3DES                   = 3;
  public static int ALG_SID_DESX                   = 4;
  public static int ALG_SID_IDEA                   = 5;
  public static int ALG_SID_CAST                   = 6;
  public static int ALG_SID_SAFERSK64              = 7;
  public static int ALG_SID_SAFERSK128             = 8;
  public static int ALG_SID_3DES_112               = 9;
  public static int ALG_SID_CYLINK_MEK             = 12;
  public static int ALG_SID_RC5                    = 13;
  public static int ALG_SID_AES_128                = 14;
  public static int ALG_SID_AES_192                = 15;
  public static int ALG_SID_AES_256                = 16;
  public static int ALG_SID_AES                    = 17;

  // Fortezza sub-ids
  public static int ALG_SID_SKIPJACK               = 10;
  public static int ALG_SID_TEK                    = 11;

  // KP_MODE
  public static int CRYPT_MODE_CBCI                = 6;       // ANSI CBC Interleaved
  public static int CRYPT_MODE_CFBP                = 7;       // ANSI CFB Pipelined
  public static int CRYPT_MODE_OFBP                = 8;       // ANSI OFB Pipelined
  public static int CRYPT_MODE_CBCOFM              = 9;       // ANSI CBC + OF Masking
  public static int CRYPT_MODE_CBCOFMI             = 10;      // ANSI CBC + OFM Interleaved

  // RC2 sub-ids
  public static int ALG_SID_RC2                    = 2;

  // Stream cipher sub-ids
  public static int ALG_SID_RC4                    = 1;
  public static int ALG_SID_SEAL                   = 2;

  // Diffie-Hellman sub-ids
  public static int ALG_SID_DH_SANDF               = 1;
  public static int ALG_SID_DH_EPHEM               = 2;
  public static int ALG_SID_AGREED_KEY_ANY         = 3;
  public static int ALG_SID_KEA                    = 4;
  
  // Hash sub ids
  public static int ALG_SID_MD2                    = 1;
  public static int ALG_SID_MD4                    = 2;
  public static int ALG_SID_MD5                    = 3;
  public static int ALG_SID_SHA                    = 4;
  public static int ALG_SID_SHA1                   = 4;
  public static int ALG_SID_MAC                    = 5;
  public static int ALG_SID_RIPEMD                 = 6;
  public static int ALG_SID_RIPEMD160              = 7;
  public static int ALG_SID_SSL3SHAMD5             = 8;
  public static int ALG_SID_HMAC                   = 9;
  public static int ALG_SID_TLS1PRF                = 10;
  public static int ALG_SID_HASH_REPLACE_OWF       = 11;
  public static int ALG_SID_SHA_256                = 12;
  public static int ALG_SID_SHA_384                = 13;
  public static int ALG_SID_SHA_512                = 14;

  // secure channel sub ids
  public static int ALG_SID_SSL3_MASTER            = 1;
  public static int ALG_SID_SCHANNEL_MASTER_HASH   = 2;
  public static int ALG_SID_SCHANNEL_MAC_KEY       = 3;
  public static int ALG_SID_PCT1_MASTER            = 4;
  public static int ALG_SID_SSL2_MASTER            = 5;
  public static int ALG_SID_TLS1_MASTER            = 6;
  public static int ALG_SID_SCHANNEL_ENC_KEY       = 7;

  // algorithm identifier definitions
  public static int CALG_MD2                = ALG_CLASS_HASH | ALG_TYPE_ANY | ALG_SID_MD2;
  public static int CALG_MD4                = ALG_CLASS_HASH | ALG_TYPE_ANY | ALG_SID_MD4;
  public static int CALG_MD5                = ALG_CLASS_HASH | ALG_TYPE_ANY | ALG_SID_MD5;
  public static int CALG_SHA                = ALG_CLASS_HASH | ALG_TYPE_ANY | ALG_SID_SHA;
  public static int CALG_SHA1               = ALG_CLASS_HASH | ALG_TYPE_ANY | ALG_SID_SHA1;
  public static int CALG_MAC                = ALG_CLASS_HASH | ALG_TYPE_ANY | ALG_SID_MAC;
  public static int CALG_RSA_SIGN           = ALG_CLASS_SIGNATURE | ALG_TYPE_RSA | ALG_SID_RSA_ANY;
  public static int CALG_DSS_SIGN           = ALG_CLASS_SIGNATURE | ALG_TYPE_DSS | ALG_SID_DSS_ANY;
  public static int CALG_NO_SIGN            = ALG_CLASS_SIGNATURE | ALG_TYPE_ANY | ALG_SID_ANY;
  public static int CALG_RSA_KEYX           = ALG_CLASS_KEY_EXCHANGE|ALG_TYPE_RSA|ALG_SID_RSA_ANY;
  public static int CALG_DES                = ALG_CLASS_DATA_ENCRYPT|ALG_TYPE_BLOCK|ALG_SID_DES;
  public static int CALG_3DES_112           = ALG_CLASS_DATA_ENCRYPT|ALG_TYPE_BLOCK|ALG_SID_3DES_112;
  public static int CALG_3DES               = ALG_CLASS_DATA_ENCRYPT|ALG_TYPE_BLOCK|ALG_SID_3DES;
  public static int CALG_DESX               = ALG_CLASS_DATA_ENCRYPT|ALG_TYPE_BLOCK|ALG_SID_DESX;
  public static int CALG_RC2                = ALG_CLASS_DATA_ENCRYPT|ALG_TYPE_BLOCK|ALG_SID_RC2;
  public static int CALG_RC4                = ALG_CLASS_DATA_ENCRYPT|ALG_TYPE_STREAM|ALG_SID_RC4;
  public static int CALG_SEAL               = ALG_CLASS_DATA_ENCRYPT|ALG_TYPE_STREAM|ALG_SID_SEAL;
  public static int CALG_DH_SF              = ALG_CLASS_KEY_EXCHANGE|ALG_TYPE_DH|ALG_SID_DH_SANDF;
  public static int CALG_DH_EPHEM           = ALG_CLASS_KEY_EXCHANGE|ALG_TYPE_DH|ALG_SID_DH_EPHEM;
  public static int CALG_AGREEDKEY_ANY      = ALG_CLASS_KEY_EXCHANGE|ALG_TYPE_DH|ALG_SID_AGREED_KEY_ANY;
  public static int CALG_KEA_KEYX           = ALG_CLASS_KEY_EXCHANGE|ALG_TYPE_DH|ALG_SID_KEA;
  public static int CALG_HUGHES_MD5         = ALG_CLASS_KEY_EXCHANGE|ALG_TYPE_ANY|ALG_SID_MD5;
  public static int CALG_SKIPJACK           = ALG_CLASS_DATA_ENCRYPT|ALG_TYPE_BLOCK|ALG_SID_SKIPJACK;
  public static int CALG_TEK                = ALG_CLASS_DATA_ENCRYPT|ALG_TYPE_BLOCK|ALG_SID_TEK;
  public static int CALG_CYLINK_MEK         = ALG_CLASS_DATA_ENCRYPT|ALG_TYPE_BLOCK|ALG_SID_CYLINK_MEK;
  public static int CALG_SSL3_SHAMD5        = ALG_CLASS_HASH | ALG_TYPE_ANY | ALG_SID_SSL3SHAMD5;
  public static int CALG_SSL3_MASTER        = ALG_CLASS_MSG_ENCRYPT|ALG_TYPE_SECURECHANNEL|ALG_SID_SSL3_MASTER;
  public static int CALG_SCHANNEL_MASTER_HASH   = ALG_CLASS_MSG_ENCRYPT|ALG_TYPE_SECURECHANNEL|ALG_SID_SCHANNEL_MASTER_HASH;
  public static int CALG_SCHANNEL_MAC_KEY   = ALG_CLASS_MSG_ENCRYPT|ALG_TYPE_SECURECHANNEL|ALG_SID_SCHANNEL_MAC_KEY;
  public static int CALG_SCHANNEL_ENC_KEY   = ALG_CLASS_MSG_ENCRYPT|ALG_TYPE_SECURECHANNEL|ALG_SID_SCHANNEL_ENC_KEY;
  public static int CALG_PCT1_MASTER        = ALG_CLASS_MSG_ENCRYPT|ALG_TYPE_SECURECHANNEL|ALG_SID_PCT1_MASTER;
  public static int CALG_SSL2_MASTER        = ALG_CLASS_MSG_ENCRYPT|ALG_TYPE_SECURECHANNEL|ALG_SID_SSL2_MASTER;
  public static int CALG_TLS1_MASTER        = ALG_CLASS_MSG_ENCRYPT|ALG_TYPE_SECURECHANNEL|ALG_SID_TLS1_MASTER;
  public static int CALG_RC5                = ALG_CLASS_DATA_ENCRYPT|ALG_TYPE_BLOCK|ALG_SID_RC5;
  public static int CALG_HMAC               = ALG_CLASS_HASH | ALG_TYPE_ANY | ALG_SID_HMAC;
  public static int CALG_TLS1PRF            = ALG_CLASS_HASH | ALG_TYPE_ANY | ALG_SID_TLS1PRF;
  public static int CALG_HASH_REPLACE_OWF   = ALG_CLASS_HASH | ALG_TYPE_ANY | ALG_SID_HASH_REPLACE_OWF;
  public static int CALG_AES_128            = ALG_CLASS_DATA_ENCRYPT|ALG_TYPE_BLOCK|ALG_SID_AES_128;
  public static int CALG_AES_192            = ALG_CLASS_DATA_ENCRYPT|ALG_TYPE_BLOCK|ALG_SID_AES_192;
  public static int CALG_AES_256            = ALG_CLASS_DATA_ENCRYPT|ALG_TYPE_BLOCK|ALG_SID_AES_256;
  public static int CALG_AES                = ALG_CLASS_DATA_ENCRYPT|ALG_TYPE_BLOCK|ALG_SID_AES;
  public static int CALG_SHA_256            = ALG_CLASS_HASH | ALG_TYPE_ANY | ALG_SID_SHA_256;
  public static int CALG_SHA_384            = ALG_CLASS_HASH | ALG_TYPE_ANY | ALG_SID_SHA_384;
  public static int CALG_SHA_512            = ALG_CLASS_HASH | ALG_TYPE_ANY | ALG_SID_SHA_512;

  /* Algorithm types */
  public static int ALG_TYPE_GR3410         = (7 << 9);
  /* GR3411 sub-ids */
  public static int ALG_SID_GR3411          = 30;
  public static int ALG_SID_GR3411_HASH     = 39;
  public static int ALG_SID_GR3411_HASH34   = 40;

  /* GOST_DH sub ids */
  public static int ALG_SID_DH_EX_SF          = 30;
  public static int ALG_SID_DH_EX_EPHEM       = 31;
  public static int ALG_SID_PRO_AGREEDKEY_DH  = 33;
  public static int ALG_SID_PRO_SIMMETRYKEY   = 34;
  public static int ALG_SID_GR3410            = 30;
  public static int ALG_SID_GR3410EL          = 35;
  public static int ALG_SID_DH_EL_SF          = 36;
  public static int ALG_SID_DH_EL_EPHEM       = 37;
  public static int ALG_SID_GR3410_94_ESDH    = 39;
  public static int ALG_SID_GR3410_01_ESDH    = 40;

  // crypto pro ids
  /* G28147 sub_ids */
  public static final int ALG_SID_G28147             = 30;
  public static final int ALG_SID_PRODIVERS          = 38;
  public static final int ALG_SID_RIC1DIVERS         = 40;

  // crypto pro alg
  public static final int CALG_GR3411 = Wincrypt.ALG_CLASS_HASH | Wincrypt.ALG_TYPE_ANY | ALG_SID_GR3411;
  public static final int CALG_GR3410 = (ALG_CLASS_SIGNATURE | ALG_TYPE_GR3410 | ALG_SID_GR3410);

  public static final int ALG_SID_PRO_EXP            = 31;
  public static final int CALG_DH_EL_EPHEM = (ALG_CLASS_KEY_EXCHANGE | ALG_TYPE_DH | ALG_SID_DH_EL_EPHEM);
  public static final int CALG_PRO_EXPORT = ALG_CLASS_DATA_ENCRYPT | ALG_TYPE_BLOCK | ALG_SID_PRO_EXP;
  public static final int CALG_G28147 = (ALG_CLASS_DATA_ENCRYPT | ALG_TYPE_BLOCK | ALG_SID_G28147);

  // crypto pro key props
  public static final int SEANCE_VECTOR_LEN       = 8;
  public static final int SECRET_KEY_LEN          = 32;
  /*! \ingroup ProCSPData
   *  \brief Длина в байтах ключа ГОСТ 28147-89
   * \sa SECRET_KEY_LEN
   */
  public static final int G28147_KEYLEN            = SECRET_KEY_LEN;

  /*! \ingroup ProCSPData
   *  \brief Длина в байтах имитовставки при импорте/экспорте
   */
  public static int EXPORT_IMIT_SIZE                = 4;

  // crypto pro special key params
  public static final int KP_HASHOID  = 103;
  public static final int KP_DHOID    = 106;

  // exported key blob definitions
  public static byte SIMPLEBLOB             = 0x1;
  public static byte PUBLICKEYBLOB          = 0x6;
  public static byte PRIVATEKEYBLOB         = 0x7;
  public static byte PLAINTEXTKEYBLOB       = 0x8;
  public static byte OPAQUEKEYBLOB          = 0x9;
  public static byte PUBLICKEYBLOBEX        = 0xA;
  public static byte SYMMETRICWRAPKEYBLOB   = 0xB;

  public static byte CUR_BLOB_VERSION       = 2;
  
  // dwFlag definitions for CryptGenKey
  public static int CRYPT_EXPORTABLE       = 0x00000001;
  public static int CRYPT_USER_PROTECTED   = 0x00000002;
  public static int CRYPT_CREATE_SALT      = 0x00000004;
  public static int CRYPT_UPDATE_KEY       = 0x00000008;
  public static int CRYPT_NO_SALT          = 0x00000010;
  public static int CRYPT_PREGEN           = 0x00000040;
  public static int CRYPT_RECIPIENT        = 0x00000010;
  public static int CRYPT_INITIATOR        = 0x00000040;
  public static int CRYPT_ONLINE           = 0x00000080;
  public static int CRYPT_SF               = 0x00000100;
  public static int CRYPT_CREATE_IV        = 0x00000200;
  public static int CRYPT_KEK              = 0x00000400;
  public static int CRYPT_DATA_KEY         = 0x00000800;
  public static int CRYPT_VOLATILE         = 0x00001000;
  public static int CRYPT_SGCKEY           = 0x00002000;
  public static int CRYPT_ARCHIVABLE       = 0x00004000;
  
  //
  // CryptGetProvParam
  //
  public static int PP_ENUMALGS            = 1;
  public static int PP_ENUMCONTAINERS      = 2;
  public static int PP_IMPTYPE             = 3;
  public static int PP_NAME                = 4;
  public static int PP_VERSION             = 5;
  public static int PP_CONTAINER           = 6;
  public static int PP_CHANGE_PASSWORD     = 7;
  public static int PP_KEYSET_SEC_DESCR    = 8;       // get/set security descriptor of keyset
  public static int PP_CERTCHAIN           = 9;       // for retrieving certificates from tokens
  public static int PP_KEY_TYPE_SUBTYPE    = 10;
  public static int PP_PROVTYPE            = 16;
  public static int PP_KEYSTORAGE          = 17;
  public static int PP_APPLI_CERT          = 18;
  public static int PP_SYM_KEYSIZE         = 19;
  public static int PP_SESSION_KEYSIZE     = 20;
  public static int PP_UI_PROMPT           = 21;
  public static int PP_ENUMALGS_EX         = 22;
  public static int PP_ENUMMANDROOTS       = 25;
  public static int PP_ENUMELECTROOTS      = 26;
  public static int PP_KEYSET_TYPE         = 27;
  public static int PP_ADMIN_PIN           = 31;
  public static int PP_KEYEXCHANGE_PIN     = 32;
  public static int PP_SIGNATURE_PIN       = 33;
  public static int PP_SIG_KEYSIZE_INC     = 34;
  public static int PP_KEYX_KEYSIZE_INC    = 35;
  public static int PP_UNIQUE_CONTAINER    = 36;
  public static int PP_SGC_INFO            = 37;
  public static int PP_USE_HARDWARE_RNG    = 38;
  public static int PP_KEYSPEC             = 39;
  public static int PP_ENUMEX_SIGNING_PROT = 40;
  public static int PP_CRYPT_COUNT_KEY_USE = 41;
  public static int PP_ENUMREADERS         = 114;

  public static int CRYPT_MEDIA   = 0x20;
  public static int CRYPT_FQCN    = 0x10;
  public static int CRYPT_UNIQUE  = 0x08;
  public static int CRYPT_FINISH  = 0x04;

  public static final String ERR_CRYPT_MEDIA_NO_MEDIA   = "NO_MEDIA";
  public static final String ERR_CRYPT_MEDIA_NO_FKC     = "NO_FKC";
  public static final String ERR_CRYPT_MEDIA_FKC        = "IS_FKC";
  public static final String ERR_CRYPT_MEDIA_NO_UNIQUE  = "NO_UNIQUE";

  // By default, when the CurrentUser "Root" store is opened, any SystemRegistry
  // roots not also on the protected root list are deleted from the cache before
  // CertOpenStore() returns. Set the following flag to return all the roots
  // in the SystemRegistry without checking the protected root list.
  public static int CERT_SYSTEM_STORE_UNPROTECTED_FLAG     = 0x40000000;

  // Location of the system store:
  public static int CERT_SYSTEM_STORE_LOCATION_MASK        = 0x00FF0000;
  public static int CERT_SYSTEM_STORE_LOCATION_SHIFT       = 16;


  //  Registry: HKEY_CURRENT_USER or HKEY_LOCAL_MACHINE
  public static int CERT_SYSTEM_STORE_CURRENT_USER_ID      = 1;
  public static int CERT_SYSTEM_STORE_LOCAL_MACHINE_ID     = 2;
  //  Registry: HKEY_LOCAL_MACHINE\Software\Microsoft\Cryptography\Services
  public static int CERT_SYSTEM_STORE_CURRENT_SERVICE_ID   = 4;
  public static int CERT_SYSTEM_STORE_SERVICES_ID          = 5;
  //  Registry: HKEY_USERS
  public static int CERT_SYSTEM_STORE_USERS_ID             = 6;

  //  Registry: HKEY_CURRENT_USER\Software\Policies\Microsoft\SystemCertificates
  public static int CERT_SYSTEM_STORE_CURRENT_USER_GROUP_POLICY_ID   = 7;
  //  Registry: HKEY_LOCAL_MACHINE\Software\Policies\Microsoft\SystemCertificates
  public static int CERT_SYSTEM_STORE_LOCAL_MACHINE_GROUP_POLICY_ID  = 8;

  //  Registry: HKEY_LOCAL_MACHINE\Software\Microsoft\EnterpriseCertificates
  public static int CERT_SYSTEM_STORE_LOCAL_MACHINE_ENTERPRISE_ID    = 9;

  public static int CERT_SYSTEM_STORE_CURRENT_USER          =
      CERT_SYSTEM_STORE_CURRENT_USER_ID << CERT_SYSTEM_STORE_LOCATION_SHIFT;
  public static int CERT_SYSTEM_STORE_LOCAL_MACHINE         =
      CERT_SYSTEM_STORE_LOCAL_MACHINE_ID << CERT_SYSTEM_STORE_LOCATION_SHIFT;
  public static int CERT_SYSTEM_STORE_CURRENT_SERVICE       =
      CERT_SYSTEM_STORE_CURRENT_SERVICE_ID << CERT_SYSTEM_STORE_LOCATION_SHIFT;
  public static int CERT_SYSTEM_STORE_SERVICES              =
      CERT_SYSTEM_STORE_SERVICES_ID << CERT_SYSTEM_STORE_LOCATION_SHIFT;
  public static int CERT_SYSTEM_STORE_USERS                 =
      CERT_SYSTEM_STORE_USERS_ID << CERT_SYSTEM_STORE_LOCATION_SHIFT;

  public static int CERT_SYSTEM_STORE_CURRENT_USER_GROUP_POLICY   =
      CERT_SYSTEM_STORE_CURRENT_USER_GROUP_POLICY_ID << CERT_SYSTEM_STORE_LOCATION_SHIFT;
  public static int CERT_SYSTEM_STORE_LOCAL_MACHINE_GROUP_POLICY  =
      CERT_SYSTEM_STORE_LOCAL_MACHINE_GROUP_POLICY_ID <<
      CERT_SYSTEM_STORE_LOCATION_SHIFT;

  public static int CERT_SYSTEM_STORE_LOCAL_MACHINE_ENTERPRISE  =
      CERT_SYSTEM_STORE_LOCAL_MACHINE_ENTERPRISE_ID <<
      CERT_SYSTEM_STORE_LOCATION_SHIFT;
  
  //+-------------------------------------------------------------------------
  //  Certificate Information Flags
  //--------------------------------------------------------------------------
  public static int CERT_INFO_VERSION_FLAG                     = 1;
  public static int CERT_INFO_SERIAL_NUMBER_FLAG               = 2;
  public static int CERT_INFO_SIGNATURE_ALGORITHM_FLAG         = 3;
  public static int CERT_INFO_ISSUER_FLAG                      = 4;
  public static int CERT_INFO_NOT_BEFORE_FLAG                  = 5;
  public static int CERT_INFO_NOT_AFTER_FLAG                   = 6;
  public static int CERT_INFO_SUBJECT_FLAG                     = 7;
  public static int CERT_INFO_SUBJECT_PUBLIC_KEY_INFO_FLAG     = 8;
  public static int CERT_INFO_ISSUER_UNIQUE_ID_FLAG            = 9;
  public static int CERT_INFO_SUBJECT_UNIQUE_ID_FLAG           = 10;
  public static int CERT_INFO_EXTENSION_FLAG                   = 11;

  //+-------------------------------------------------------------------------
  // Certificate comparison functions
  //--------------------------------------------------------------------------
  public static int CERT_COMPARE_MASK          = 0xFFFF;
  public static int CERT_COMPARE_SHIFT         = 16;
  public static int CERT_COMPARE_ANY           = 0;
  public static int CERT_COMPARE_SHA1_HASH     = 1;
  public static int CERT_COMPARE_NAME          = 2;
  public static int CERT_COMPARE_ATTR          = 3;
  public static int CERT_COMPARE_MD5_HASH      = 4;
  public static int CERT_COMPARE_PROPERTY      = 5;
  public static int CERT_COMPARE_PUBLIC_KEY    = 6;
  public static int CERT_COMPARE_HASH          = CERT_COMPARE_SHA1_HASH;
  public static int CERT_COMPARE_NAME_STR_A    = 7;
  public static int CERT_COMPARE_NAME_STR_W    = 8;
  public static int CERT_COMPARE_KEY_SPEC      = 9;
  public static int CERT_COMPARE_ENHKEY_USAGE  = 10;
  public static int CERT_COMPARE_CTL_USAGE     = CERT_COMPARE_ENHKEY_USAGE;
  public static int CERT_COMPARE_SUBJECT_CERT  = 11;
  public static int CERT_COMPARE_ISSUER_OF     = 12;
  public static int CERT_COMPARE_EXISTING      = 13;
  public static int CERT_COMPARE_SIGNATURE_HASH= 14;
  public static int CERT_COMPARE_KEY_IDENTIFIER= 15;
  public static int CERT_COMPARE_CERT_ID       = 16;
  public static int CERT_COMPARE_CROSS_CERT_DIST_POINTS = 17;

  public static int CERT_COMPARE_PUBKEY_MD5_HASH = 18;
  
  //+-------------------------------------------------------------------------
  //  dwFindType
  //
  //  The dwFindType definition consists of two components:
  //   - comparison function
  //   - certificate information flag
  //--------------------------------------------------------------------------
  public static int CERT_FIND_ANY          = CERT_COMPARE_ANY << CERT_COMPARE_SHIFT;
  public static int CERT_FIND_SHA1_HASH    = CERT_COMPARE_SHA1_HASH << CERT_COMPARE_SHIFT;
  public static int CERT_FIND_MD5_HASH     = CERT_COMPARE_MD5_HASH << CERT_COMPARE_SHIFT;
  public static int CERT_FIND_SIGNATURE_HASH = CERT_COMPARE_SIGNATURE_HASH << CERT_COMPARE_SHIFT;
  public static int CERT_FIND_KEY_IDENTIFIER = CERT_COMPARE_KEY_IDENTIFIER << CERT_COMPARE_SHIFT;
  public static int CERT_FIND_HASH           = CERT_FIND_SHA1_HASH;
  public static int CERT_FIND_PROPERTY       = CERT_COMPARE_PROPERTY << CERT_COMPARE_SHIFT;
  public static int CERT_FIND_PUBLIC_KEY     = CERT_COMPARE_PUBLIC_KEY << CERT_COMPARE_SHIFT;
  public static int CERT_FIND_SUBJECT_NAME   = CERT_COMPARE_NAME << CERT_COMPARE_SHIFT |
                                   CERT_INFO_SUBJECT_FLAG;
  public static int CERT_FIND_SUBJECT_ATTR   = CERT_COMPARE_ATTR << CERT_COMPARE_SHIFT |
                                   CERT_INFO_SUBJECT_FLAG;
  public static int CERT_FIND_ISSUER_NAME    = CERT_COMPARE_NAME << CERT_COMPARE_SHIFT |
                                   CERT_INFO_ISSUER_FLAG;
  public static int CERT_FIND_ISSUER_ATTR    = CERT_COMPARE_ATTR << CERT_COMPARE_SHIFT |
                                   CERT_INFO_ISSUER_FLAG;
  public static int CERT_FIND_SUBJECT_STR_A  = CERT_COMPARE_NAME_STR_A << CERT_COMPARE_SHIFT |
                                   CERT_INFO_SUBJECT_FLAG;
  public static int CERT_FIND_SUBJECT_STR_W  = CERT_COMPARE_NAME_STR_W << CERT_COMPARE_SHIFT |
                                   CERT_INFO_SUBJECT_FLAG;
  public static int CERT_FIND_SUBJECT_STR    = CERT_FIND_SUBJECT_STR_W;
  public static int CERT_FIND_ISSUER_STR_A   = CERT_COMPARE_NAME_STR_A << CERT_COMPARE_SHIFT |
                                   CERT_INFO_ISSUER_FLAG;
  public static int CERT_FIND_ISSUER_STR_W   = CERT_COMPARE_NAME_STR_W << CERT_COMPARE_SHIFT |
                                   CERT_INFO_ISSUER_FLAG;
  public static int CERT_FIND_ISSUER_STR     = CERT_FIND_ISSUER_STR_W;
  public static int CERT_FIND_KEY_SPEC       = CERT_COMPARE_KEY_SPEC << CERT_COMPARE_SHIFT;
  public static int CERT_FIND_ENHKEY_USAGE   = CERT_COMPARE_ENHKEY_USAGE << CERT_COMPARE_SHIFT;
  public static int CERT_FIND_CTL_USAGE      = CERT_FIND_ENHKEY_USAGE;

  public static int CERT_FIND_SUBJECT_CERT = CERT_COMPARE_SUBJECT_CERT << CERT_COMPARE_SHIFT;
  public static int CERT_FIND_ISSUER_OF    = CERT_COMPARE_ISSUER_OF << CERT_COMPARE_SHIFT;
  public static int CERT_FIND_EXISTING     = CERT_COMPARE_EXISTING << CERT_COMPARE_SHIFT;
  public static int CERT_FIND_CERT_ID      = CERT_COMPARE_CERT_ID << CERT_COMPARE_SHIFT;
  public static int CERT_FIND_CROSS_CERT_DIST_POINTS =
                      CERT_COMPARE_CROSS_CERT_DIST_POINTS << CERT_COMPARE_SHIFT;
  
  public static int CERT_FIND_PUBKEY_MD5_HASH =
                      CERT_COMPARE_PUBKEY_MD5_HASH << CERT_COMPARE_SHIFT;

  //+-------------------------------------------------------------------------
  //  Certificate name string types
  //--------------------------------------------------------------------------
  public static int CERT_SIMPLE_NAME_STR       = 1;
  public static int CERT_OID_NAME_STR          = 2;
  public static int CERT_X500_NAME_STR         = 3;

  //+-------------------------------------------------------------------------
  //  Certificate versions
  //--------------------------------------------------------------------------
  public static int CERT_V1     = 0;
  public static int CERT_V2     = 1;
  public static int CERT_V3     = 2;

//+-------------------------------------------------------------------------
//  Certificate Store verify/results flags
//--------------------------------------------------------------------------
  public static int CERT_STORE_SIGNATURE_FLAG          = 0x00000001;
  public static int CERT_STORE_TIME_VALIDITY_FLAG      = 0x00000002;
  public static int CERT_STORE_REVOCATION_FLAG         = 0x00000004;
  public static int CERT_STORE_NO_CRL_FLAG             = 0x00010000;
  public static int CERT_STORE_NO_ISSUER_FLAG          = 0x00020000;

  public static int CERT_STORE_BASE_CRL_FLAG           = 0x00000100;
  public static int CERT_STORE_DELTA_CRL_FLAG          = 0x00000200;

  //+-------------------------------------------------------------------------
  //  Predefined X509 certificate data structures that can be encoded / decoded.
  //--------------------------------------------------------------------------
  public static int CRYPT_ENCODE_DECODE_NONE         = 0;
  public static int X509_CERT                        = 1;
  public static int X509_CERT_TO_BE_SIGNED           = 2;
  public static int X509_CERT_CRL_TO_BE_SIGNED       = 3;
  public static int X509_CERT_REQUEST_TO_BE_SIGNED   = 4;
  public static int X509_EXTENSIONS                  = 5;
  public static int X509_NAME_VALUE                  = 6;
  public static int X509_NAME                        = 7;
  public static int X509_PUBLIC_KEY_INFO             = 8;
  public static int X509_AUTHORITY_KEY_ID            = 9;
  public static int X509_KEY_ATTRIBUTES              = 10;
  public static int X509_OCTET_STRING                = 25;
  public static int X509_CHOICE_OF_TIME              = 30;

  
// Byte[0]
  public static int CERT_DIGITAL_SIGNATURE_KEY_USAGE     = 0x80;
  public static int CERT_NON_REPUDIATION_KEY_USAGE       = 0x40;
  public static int CERT_KEY_ENCIPHERMENT_KEY_USAGE      = 0x20;
  public static int CERT_DATA_ENCIPHERMENT_KEY_USAGE     = 0x10;
  public static int CERT_KEY_AGREEMENT_KEY_USAGE         = 0x08;
  public static int CERT_KEY_CERT_SIGN_KEY_USAGE         = 0x04;
  public static int CERT_OFFLINE_CRL_SIGN_KEY_USAGE      = 0x02;
  public static int CERT_CRL_SIGN_KEY_USAGE              = 0x02;
  public static int CERT_ENCIPHER_ONLY_KEY_USAGE         = 0x01;
  // Byte[1]
  public static int CERT_DECIPHER_ONLY_KEY_USAGE         = 0x80;
  
  //+-------------------------------------------------------------------------
  //  CERT_RDN attribute Object Identifiers
  //--------------------------------------------------------------------------
  // Labeling attribute types:
  public static final String szOID_COMMON_NAME                   = "2.5.4.3";  // case-ignore string
  public static final String szOID_SUR_NAME                      = "2.5.4.4";  // case-ignore string
  public static final String szOID_DEVICE_SERIAL_NUMBER          = "2.5.4.5";  // printable string

  // Geographic attribute types:
  public static final String szOID_COUNTRY_NAME                  = "2.5.4.6";  // printable 2char string
  public static final String szOID_LOCALITY_NAME                 = "2.5.4.7";  // case-ignore string
  public static final String szOID_STATE_OR_PROVINCE_NAME        = "2.5.4.8";  // case-ignore string
  public static final String szOID_STREET_ADDRESS                = "2.5.4.9";  // case-ignore string

  // Organizational attribute types:
  public static final String szOID_ORGANIZATION_NAME             = "2.5.4.10"; // case-ignore string
  public static final String szOID_ORGANIZATIONAL_UNIT_NAME      = "2.5.4.11"; // case-ignore string
  public static final String szOID_TITLE                         = "2.5.4.12"; // case-ignore string

  // Explanatory attribute types:
  public static final String szOID_DESCRIPTION                   = "2.5.4.13"; // case-ignore string
  public static final String szOID_SEARCH_GUIDE                  = "2.5.4.14";
  public static final String szOID_BUSINESS_CATEGORY             = "2.5.4.15"; // case-ignore string

  // Postal addressing attribute types:
  public static final String szOID_POSTAL_ADDRESS                = "2.5.4.16";
  public static final String szOID_POSTAL_CODE                   = "2.5.4.17"; // case-ignore string
  public static final String szOID_POST_OFFICE_BOX               = "2.5.4.18"; // case-ignore string
  public static final String szOID_PHYSICAL_DELIVERY_OFFICE_NAME = "2.5.4.19"; // case-ignore string

  // Telecommunications addressing attribute types:
  public static final String szOID_TELEPHONE_NUMBER              = "2.5.4.20"; // telephone number
  public static final String szOID_TELEX_NUMBER                  = "2.5.4.21";
  public static final String szOID_TELETEXT_TERMINAL_IDENTIFIER  = "2.5.4.22";
  public static final String szOID_FACSIMILE_TELEPHONE_NUMBER    = "2.5.4.23";
  public static final String szOID_X21_ADDRESS                   = "2.5.4.24"; // numeric string
  public static final String szOID_INTERNATIONAL_ISDN_NUMBER     = "2.5.4.25"; // numeric string
  public static final String szOID_REGISTERED_ADDRESS            = "2.5.4.26";
  public static final String szOID_DESTINATION_INDICATOR         = "2.5.4.27"; // printable string

  // Preference attribute types:
  public static final String szOID_PREFERRED_DELIVERY_METHOD     = "2.5.4.28";

  // OSI application attribute types:
  public static final String szOID_PRESENTATION_ADDRESS          = "2.5.4.29";
  public static final String szOID_SUPPORTED_APPLICATION_CONTEXT = "2.5.4.30";

  // Relational application attribute types:
  public static final String szOID_MEMBER                        = "2.5.4.31";
  public static final String szOID_OWNER                         = "2.5.4.32";
  public static final String szOID_ROLE_OCCUPANT                 = "2.5.4.33";
  public static final String szOID_SEE_ALSO                      = "2.5.4.34";

  // Security attribute types:
  public static final String szOID_USER_PASSWORD                 = "2.5.4.35";
  public static final String szOID_USER_CERTIFICATE              = "2.5.4.36";
  public static final String szOID_CA_CERTIFICATE                = "2.5.4.37";
  public static final String szOID_AUTHORITY_REVOCATION_LIST     = "2.5.4.38";
  public static final String szOID_CERTIFICATE_REVOCATION_LIST   = "2.5.4.39";
  public static final String szOID_CROSS_CERTIFICATE_PAIR        = "2.5.4.40";

  // Undocumented attribute types???
  //  public static final String szOID_???                         = "2.5.4.41"
  public static final String szOID_GIVEN_NAME                    = "2.5.4.42"; // case-ignore string
  public static final String szOID_INITIALS                      = "2.5.4.43"; // case-ignore string

  // The DN Qualifier attribute type specifies disambiguating information to add
  // to the relative distinguished name of an entry. It is intended to be used
  // for entries held in multiple DSAs which would otherwise have the same name,
  // and that its value be the same in a given DSA for all entries to which
  // the information has been added.
  public static final String szOID_DN_QUALIFIER                  = "2.5.4.46";

  // Pilot user attribute types:
  public static final String szOID_DOMAIN_COMPONENT  = "0.9.2342.19200300.100.1.25"; // IA5, UTF8 string

  //+-------------------------------------------------------------------------
  //  Extension Object Identifiers
  //--------------------------------------------------------------------------
  public static final String szOID_AUTHORITY_KEY_IDENTIFIER  = "2.5.29.1";
  public static final String szOID_KEY_ATTRIBUTES            = "2.5.29.2";
  public static final String szOID_CERT_POLICIES_95          = "2.5.29.3";
  public static final String szOID_KEY_USAGE_RESTRICTION     = "2.5.29.4";
  public static final String szOID_SUBJECT_ALT_NAME          = "2.5.29.7";
  public static final String szOID_ISSUER_ALT_NAME           = "2.5.29.8";
  public static final String szOID_BASIC_CONSTRAINTS         = "2.5.29.10";
  public static final String szOID_KEY_USAGE                 = "2.5.29.15";
  public static final String szOID_PRIVATEKEY_USAGE_PERIOD   = "2.5.29.16";
  public static final String szOID_BASIC_CONSTRAINTS2        = "2.5.29.19";

  public static final String szCPGUID_PRIVATEKEY_USAGE_PERIOD_Encode = "{E36FC6F5-4880-4CB7-BA51-1FCD92CA1453}"; // CryptoPro WinCryptEx.h

  public static final String szOID_CERT_POLICIES             = "2.5.29.32";
  public static final String szOID_ANY_CERT_POLICY           = "2.5.29.32.0";

  public static final String szOID_AUTHORITY_KEY_IDENTIFIER2 = "2.5.29.35";
  public static final String szOID_SUBJECT_KEY_IDENTIFIER    = "2.5.29.14";
  public static final String szOID_SUBJECT_ALT_NAME2         = "2.5.29.17";
  public static final String szOID_ISSUER_ALT_NAME2          = "2.5.29.18";
  public static final String szOID_CRL_REASON_CODE           = "2.5.29.21";
  public static final String szOID_REASON_CODE_HOLD          = "2.5.29.23";
  public static final String szOID_CRL_DIST_POINTS           = "2.5.29.31";
  public static final String szOID_ENHANCED_KEY_USAGE        = "2.5.29.37";
  
// szOID_CRL_NUMBER -- Base CRLs only.  Monotonically increasing sequence
// number for each CRL issued by a CA.
  public static final String szOID_CRL_NUMBER                = "2.5.29.20";
// szOID_DELTA_CRL_INDICATOR -- Delta CRLs only.  Marked critical.
// Contains the minimum base CRL Number that can be used with a delta CRL.
  public static final String szOID_DELTA_CRL_INDICATOR       = "2.5.29.27";
  public static final String szOID_ISSUING_DIST_POINT        = "2.5.29.28";
// szOID_FRESHEST_CRL -- Base CRLs only.  Formatted identically to a CDP
// extension that holds URLs to fetch the delta CRL.
  public static final String szOID_FRESHEST_CRL              = "2.5.29.46";
  public static final String szOID_NAME_CONSTRAINTS          = "2.5.29.30";

// Note on 1/1/2000 szOID_POLICY_MAPPINGS was changed from "2.5.29.5"
  public static final String szOID_POLICY_MAPPINGS           = "2.5.29.33";
  public static final String szOID_LEGACY_POLICY_MAPPINGS    = "2.5.29.5";
  public static final String szOID_POLICY_CONSTRAINTS        = "2.5.29.36";

  // Certificate template for RA
  public static final String szOID_CERTIFICATE_TEMPLATE = "1.3.6.1.4.1.311.21.7";

  public static int  CERT_ALT_NAME_OTHER_NAME        = 1;
  public static int  CERT_ALT_NAME_RFC822_NAME       = 2;
  public static int  CERT_ALT_NAME_DNS_NAME          = 3;
  public static int  CERT_ALT_NAME_X400_ADDRESS      = 4;
  public static int  CERT_ALT_NAME_DIRECTORY_NAME    = 5;
  public static int  CERT_ALT_NAME_EDI_PARTY_NAME    = 6;
  public static int  CERT_ALT_NAME_URL               = 7;
  public static int  CERT_ALT_NAME_IP_ADDRESS        = 8;
  public static int  CERT_ALT_NAME_REGISTERED_ID     = 9;
  
  // Following are the definitions of various algorithm object identifiers
  // RSA
  public static final String szOID_RSA              = "1.2.840.113549";
  public static final String szOID_PKCS             = "1.2.840.113549.1";
  public static final String szOID_RSA_HASH         = "1.2.840.113549.2";
  public static final String szOID_RSA_ENCRYPT      = "1.2.840.113549.3";

  public static final String szOID_PKCS_1           = "1.2.840.113549.1.1";
  public static final String szOID_PKCS_2           = "1.2.840.113549.1.2";
  public static final String szOID_PKCS_3           = "1.2.840.113549.1.3";
  public static final String szOID_PKCS_4           = "1.2.840.113549.1.4";
  public static final String szOID_PKCS_5           = "1.2.840.113549.1.5";
  public static final String szOID_PKCS_6           = "1.2.840.113549.1.6";
  public static final String szOID_PKCS_7           = "1.2.840.113549.1.7";
  public static final String szOID_PKCS_8           = "1.2.840.113549.1.8";
  public static final String szOID_PKCS_9           = "1.2.840.113549.1.9";
  public static final String szOID_PKCS_10          = "1.2.840.113549.1.10";
  public static final String szOID_PKCS_12          = "1.2.840.113549.1.12";

  public static final String szOID_RSA_RSA          = "1.2.840.113549.1.1.1";
  public static final String szOID_RSA_MD2RSA       = "1.2.840.113549.1.1.2";
  public static final String szOID_RSA_MD4RSA       = "1.2.840.113549.1.1.3";
  public static final String szOID_RSA_MD5RSA       = "1.2.840.113549.1.1.4";
  public static final String szOID_RSA_SHA1RSA      = "1.2.840.113549.1.1.5";
  public static final String szOID_RSA_SETOAEP_RSA  = "1.2.840.113549.1.1.6";

  public static final String szOID_RSA_DH           = "1.2.840.113549.1.3.1";

  public static final String szOID_RSA_data         = "1.2.840.113549.1.7.1";
  public static final String szOID_RSA_signedData   = "1.2.840.113549.1.7.2";
  public static final String szOID_RSA_envelopedData= "1.2.840.113549.1.7.3";
  public static final String szOID_RSA_signEnvData  = "1.2.840.113549.1.7.4";
  public static final String szOID_RSA_digestedData = "1.2.840.113549.1.7.5";
  public static final String szOID_RSA_hashedData   = "1.2.840.113549.1.7.5";
  public static final String szOID_RSA_encryptedData= "1.2.840.113549.1.7.6";

  public static final String szOID_RSA_emailAddr    = "1.2.840.113549.1.9.1";
  public static final String szOID_RSA_unstructName = "1.2.840.113549.1.9.2";
  public static final String szOID_RSA_contentType  = "1.2.840.113549.1.9.3";
  public static final String szOID_RSA_messageDigest= "1.2.840.113549.1.9.4";
  public static final String szOID_RSA_signingTime  = "1.2.840.113549.1.9.5";
  public static final String szOID_RSA_counterSign  = "1.2.840.113549.1.9.6";
  public static final String szOID_RSA_challengePwd = "1.2.840.113549.1.9.7";
  public static final String szOID_RSA_unstructAddr = "1.2.840.113549.1.9.8";
  public static final String szOID_RSA_extCertAttrs = "1.2.840.113549.1.9.9";
  public static final String szOID_RSA_certExtensions = "1.2.840.113549.1.9.14";
  public static final String szOID_RSA_SMIMECapabilities = "1.2.840.113549.1.9.15";
  public static final String szOID_RSA_preferSignedData = "1.2.840.113549.1.9.15.1";

  public static final String szOID_RSA_SMIMEalg             = "1.2.840.113549.1.9.16.3";
  public static final String szOID_RSA_SMIMEalgESDH         = "1.2.840.113549.1.9.16.3.5";
  public static final String szOID_RSA_SMIMEalgCMS3DESwrap  = "1.2.840.113549.1.9.16.3.6";
  public static final String szOID_RSA_SMIMEalgCMSRC2wrap   = "1.2.840.113549.1.9.16.3.7";

  public static final String szOID_RSA_MD2          = "1.2.840.113549.2.2";
  public static final String szOID_RSA_MD4          = "1.2.840.113549.2.4";
  public static final String szOID_RSA_MD5          = "1.2.840.113549.2.5";

  public static final String szOID_RSA_RC2CBC       = "1.2.840.113549.3.2";
  public static final String szOID_RSA_RC4          = "1.2.840.113549.3.4";
  public static final String szOID_RSA_DES_EDE3_CBC = "1.2.840.113549.3.7";
  public static final String szOID_RSA_RC5_CBCPad   = "1.2.840.113549.3.9";


  public static final String szOID_ANSI_X942        = "1.2.840.10046";
  public static final String szOID_ANSI_X942_DH     = "1.2.840.10046.2.1";

  public static final String szOID_X957             = "1.2.840.10040";
  public static final String szOID_X957_DSA         = "1.2.840.10040.4.1";
  public static final String szOID_X957_SHA1DSA     = "1.2.840.10040.4.3";
  
  //+-------------------------------------------------------------------------
  //  CERT_RDN Attribute Value Types
  //
  //  For RDN_ENCODED_BLOB, the Value's CERT_RDN_VALUE_BLOB is in its encoded
  //  representation. Otherwise, its an array of bytes.
  //
  //  For all CERT_RDN types, Value.cbData is always the number of bytes, not
  //  necessarily the number of elements in the string. For instance,
  //  RDN_UNIVERSAL_STRING is an array of ints (cbData == intCnt * 4) and
  //  RDN_BMP_STRING is an array of unsigned shorts (cbData == ushortCnt * 2).
  //
  //  A RDN_UTF8_STRING is an array of UNICODE characters (cbData == charCnt *2).
  //  These UNICODE characters are encoded as UTF8 8 bit characters.
  //
  //  For CertDecodeName, two 0 bytes are always appended to the end of the
  //  string (ensures a CHAR or WCHAR string is null terminated).
  //  These added 0 bytes are't included in the BLOB.cbData.
  //--------------------------------------------------------------------------
  public static int  CERT_RDN_ANY_TYPE               = 0;
  public static int  CERT_RDN_ENCODED_BLOB           = 1;
  public static int  CERT_RDN_OCTET_STRING           = 2;
  public static int  CERT_RDN_NUMERIC_STRING         = 3;
  public static int  CERT_RDN_PRINTABLE_STRING       = 4;
  public static int  CERT_RDN_TELETEX_STRING         = 5;
  public static int  CERT_RDN_T61_STRING             = 5;
  public static int  CERT_RDN_VIDEOTEX_STRING        = 6;
  public static int  CERT_RDN_IA5_STRING             = 7;
  public static int  CERT_RDN_GRAPHIC_STRING         = 8;
  public static int  CERT_RDN_VISIBLE_STRING         = 9;
  public static int  CERT_RDN_ISO646_STRING          = 9;
  public static int  CERT_RDN_GENERAL_STRING         = 10;
  public static int  CERT_RDN_UNIVERSAL_STRING       = 11;
  public static int  CERT_RDN_INT4_STRING            = 11;
  public static int  CERT_RDN_BMP_STRING             = 12;
  public static int  CERT_RDN_UNICODE_STRING         = 12;
  public static int  CERT_RDN_UTF8_STRING            = 13;

  public static int  CERT_RDN_TYPE_MASK                 = 0x000000FF;
  public static int  CERT_RDN_FLAGS_MASK                = 0xFF000000;
  
  // Microsoft extensions or attributes
  public static final String szOID_CERT_EXTENSIONS          = "1.3.6.1.4.1.311.2.1.14";
  public static final String szOID_NEXT_UPDATE_LOCATION     = "1.3.6.1.4.1.311.10.2";
  public static final String szOID_REMOVE_CERTIFICATE       = "1.3.6.1.4.1.311.10.8.1";
  public static final String szOID_CROSS_CERT_DIST_POINTS   = "1.3.6.1.4.1.311.10.9.1";
  /**
   * Следующая дата публикации
   */
  public static final String szOID_CRL_NEXT_PUBLISH         = "1.3.6.1.4.1.311.21.4";
  

  public static final String szOID_CP_GOST_28147            = "1.2.643.2.2.21"; // Алгоритм шифрования ГОСТ 28147-89
  public static final String szOID_CP_GOST_R3411            = "1.2.643.2.2.9";  // Функция хэширования ГОСТ Р 34.11-94
  public static final String szOID_CP_GOST_R3410            = "1.2.643.2.2.20"; // Алгоритм ГОСТ Р 34.10-94, используемый при экспорте/импорте ключей
  public static final String szOID_CP_GOST_R3410EL          = "1.2.643.2.2.19"; // Алгоритм ГОСТ Р 34.10-2001, используемый при экспорте/импорте ключей
  public static final String szOID_CP_DH_EX	                = "1.2.643.2.2.99"; // Алгоритм Диффи-Хеллмана на базе потенциальной функции
  public static final String szOID_CP_DH_EL	                = "1.2.643.2.2.98"; // Алгоритм Диффи-Хеллмана на базе эллиптической кривой
  public static final String szOID_CP_GOST_R3411_R3410      = "1.2.643.2.2.4";  // Алгоритм цифровой подписи ГОСТ Р 34.10-94
  public static final String szOID_CP_GOST_R3411_R3410EL    = "1.2.643.2.2.3";  // Алгоритм цифровой подписи ГОСТ Р 34.10-2001
  public static final String szOID_KP_TLS_PROXY	            = "1.2.643.2.2.34.1"; // Аудит TLS-трафика
  public static final String szOID_KP_RA_CLIENT_AUTH        = "1.2.643.2.2.34.2"; // Идентификация пользователя на центре регистрации
  public static final String szOID_KP_WEB_CONTENT_SIGNING	  = "1.2.643.2.2.34.3"; // Подпись содержимого сервера Интернет

  //+-------------------------------------------------------------------------
  //  Enhanced Key Usage (Purpose) Object Identifiers
  //--------------------------------------------------------------------------
  public static final String szOID_PKIX_KP                  = "1.3.6.1.5.5.7.3";

  // Consistent key usage bits: DIGITAL_SIGNATURE, KEY_ENCIPHERMENT
  // or KEY_AGREEMENT
  public static final String szOID_PKIX_KP_SERVER_AUTH      = "1.3.6.1.5.5.7.3.1";

  // Consistent key usage bits: DIGITAL_SIGNATURE
  public static final String szOID_PKIX_KP_CLIENT_AUTH      = "1.3.6.1.5.5.7.3.2";

  // Consistent key usage bits: DIGITAL_SIGNATURE
  public static final String szOID_PKIX_KP_CODE_SIGNING     = "1.3.6.1.5.5.7.3.3";

  // Consistent key usage bits: DIGITAL_SIGNATURE, NON_REPUDIATION and/or
  // (KEY_ENCIPHERMENT or KEY_AGREEMENT)
  public static final String szOID_PKIX_KP_EMAIL_PROTECTION = "1.3.6.1.5.5.7.3.4";

  // Consistent key usage bits: DIGITAL_SIGNATURE and/or
  // (KEY_ENCIPHERMENT or KEY_AGREEMENT)
  public static final String szOID_PKIX_KP_IPSEC_END_SYSTEM = "1.3.6.1.5.5.7.3.5";

  // Consistent key usage bits: DIGITAL_SIGNATURE and/or
  // (KEY_ENCIPHERMENT or KEY_AGREEMENT)
  public static final String szOID_PKIX_KP_IPSEC_TUNNEL     = "1.3.6.1.5.5.7.3.6";

  // Consistent key usage bits: DIGITAL_SIGNATURE and/or
  // (KEY_ENCIPHERMENT or KEY_AGREEMENT)
  public static final String szOID_PKIX_KP_IPSEC_USER       = "1.3.6.1.5.5.7.3.7";

  // Consistent key usage bits: DIGITAL_SIGNATURE or NON_REPUDIATION
  public static final String szOID_PKIX_KP_TIMESTAMP_SIGNING  = "1.3.6.1.5.5.7.3.8";


  // IKE (Internet Key Exchange) Intermediate KP for an IPsec end entity.
  // Defined in draft-ietf-ipsec-pki-req-04.txt, December 14, 1999.
  public static final String szOID_IPSEC_KP_IKE_INTERMEDIATE = "1.3.6.1.5.5.8.2.2";

//+-------------------------------------------------------------------------
// Add certificate/CRL, encoded, context or element disposition values.
//--------------------------------------------------------------------------
  public static int  CERT_STORE_ADD_NEW                                  = 1;
  public static int  CERT_STORE_ADD_USE_EXISTING                         = 2;
  public static int  CERT_STORE_ADD_REPLACE_EXISTING                     = 3;
  public static int  CERT_STORE_ADD_ALWAYS                               = 4;
  public static int  CERT_STORE_ADD_REPLACE_EXISTING_INHERIT_PROPERTIES  = 5;
  public static int  CERT_STORE_ADD_NEWER                                = 6;
  public static int  CERT_STORE_ADD_NEWER_INHERIT_PROPERTIES             = 7;

  //+-------------------------------------------------------------------------
  //  Certificate, CRL and CTL property IDs
  //
  //  See CertSetCertificateContextProperty or CertGetCertificateContextProperty
  //  for usage information.
  //--------------------------------------------------------------------------
  public static int CERT_KEY_PROV_HANDLE_PROP_ID       = 1;
  public static int CERT_KEY_PROV_INFO_PROP_ID         = 2;
  public static int CERT_SHA1_HASH_PROP_ID             = 3;
  public static int CERT_MD5_HASH_PROP_ID              = 4;
  public static int CERT_HASH_PROP_ID                  = CERT_SHA1_HASH_PROP_ID;
  public static int CERT_KEY_CONTEXT_PROP_ID           = 5;
  public static int CERT_KEY_SPEC_PROP_ID              = 6;
  public static int CERT_IE30_RESERVED_PROP_ID         = 7;
  public static int CERT_PUBKEY_HASH_RESERVED_PROP_ID  = 8;
  public static int CERT_ENHKEY_USAGE_PROP_ID          = 9;
  public static int CERT_CTL_USAGE_PROP_ID             = CERT_ENHKEY_USAGE_PROP_ID;
  public static int CERT_NEXT_UPDATE_LOCATION_PROP_ID  = 10;
  public static int CERT_FRIENDLY_NAME_PROP_ID         = 11;
  public static int CERT_PVK_FILE_PROP_ID              = 12;
  public static int CERT_DESCRIPTION_PROP_ID           = 13;
  public static int CERT_ACCESS_STATE_PROP_ID          = 14;
  public static int CERT_SIGNATURE_HASH_PROP_ID        = 15;
  public static int CERT_SMART_CARD_DATA_PROP_ID       = 16;
  public static int CERT_EFS_PROP_ID                   = 17;
  public static int CERT_FORTEZZA_DATA_PROP_ID         = 18;
  public static int CERT_ARCHIVED_PROP_ID              = 19;
  public static int CERT_KEY_IDENTIFIER_PROP_ID        = 20;
  public static int CERT_AUTO_ENROLL_PROP_ID           = 21;
  public static int CERT_PUBKEY_ALG_PARA_PROP_ID       = 22;
  public static int CERT_CROSS_CERT_DIST_POINTS_PROP_ID= 23;
  public static int CERT_ISSUER_PUBLIC_KEY_MD5_HASH_PROP_ID    = 24;
  public static int CERT_SUBJECT_PUBLIC_KEY_MD5_HASH_PROP_ID   = 25;
  public static int CERT_ENROLLMENT_PROP_ID            = 26;
  public static int CERT_DATE_STAMP_PROP_ID            = 27;
  public static int CERT_ISSUER_SERIAL_NUMBER_MD5_HASH_PROP_ID  = 28;
  public static int CERT_SUBJECT_NAME_MD5_HASH_PROP_ID = 29;
  public static int CERT_EXTENDED_ERROR_INFO_PROP_ID   = 30;
  
  // Note, 32 - 35 are reserved for the CERT, CRL, CTL and KeyId file element IDs.
  //       36 - 63 are reserved for future element IDs.
  
  public static int CERT_RENEWAL_PROP_ID               = 64;
  public static int CERT_ARCHIVED_KEY_HASH_PROP_ID     = 65;
  public static int CERT_AUTO_ENROLL_RETRY_PROP_ID     = 66;
  public static int CERT_AIA_URL_RETRIEVED_PROP_ID     = 67;
  public static int CERT_FIRST_RESERVED_PROP_ID        = 68;
  
  public static int CERT_LAST_RESERVED_PROP_ID         = 0x00007FFF;
  public static int CERT_FIRST_USER_PROP_ID            = 0x00008000;
  public static int CERT_LAST_USER_PROP_ID             = 0x0000FFFF;

  // dwFlags has the following defines
  public static int  CRYPT_STRING_BASE64HEADER          = 0x00000000;
  public static int  CRYPT_STRING_BASE64                = 0x00000001;
  public static int  CRYPT_STRING_BINARY                = 0x00000002;
  public static int  CRYPT_STRING_BASE64REQUESTHEADER   = 0x00000003;
  public static int  CRYPT_STRING_HEX                   = 0x00000004;
  public static int  CRYPT_STRING_HEXASCII              = 0x00000005;
  public static int  CRYPT_STRING_BASE64_ANY            = 0x00000006;
  public static int  CRYPT_STRING_ANY                   = 0x00000007;
  public static int  CRYPT_STRING_HEX_ANY               = 0x00000008;
  public static int  CRYPT_STRING_BASE64X509CRLHEADER   = 0x00000009;
  public static int  CRYPT_STRING_HEXADDR               = 0x0000000a;
  public static int  CRYPT_STRING_HEXASCIIADDR          = 0x0000000b;

  public static int  CRYPT_STRING_NOCR                  = 0x80000000;

// CryptBinaryToString uses the following flags
// CRYPT_STRING_BASE64HEADER - base64 format with certificate begin
//                             and end headers
// CRYPT_STRING_BASE64 - only base64 without headers
// CRYPT_STRING_BINARY - pure binary copy
// CRYPT_STRING_BASE64REQUESTHEADER - base64 format with request begin
//                                    and end headers
// CRYPT_STRING_BASE64X509CRLHEADER - base64 format with x509 crl begin
//                                    and end headers
// CRYPT_STRING_HEX - only hex format
// CRYPT_STRING_HEXASCII - hex format with ascii char display
// CRYPT_STRING_HEXADDR - hex format with address display
// CRYPT_STRING_HEXASCIIADDR - hex format with ascii char and address display
//
// CryptBinaryToString accepts CRYPT_STRING_NOCR or'd into one of the above.
// When set, line breaks contain only LF, instead of CR-LF pairs.

// CryptStringToBinary uses the following flags
// CRYPT_STRING_BASE64_ANY tries the following, in order:
//    CRYPT_STRING_BASE64HEADER
//    CRYPT_STRING_BASE64
// CRYPT_STRING_ANY tries the following, in order:
//    CRYPT_STRING_BASE64_ANY
//    CRYPT_STRING_BINARY -- should always succeed
// CRYPT_STRING_HEX_ANY tries the following, in order:
//    CRYPT_STRING_HEXADDR
//    CRYPT_STRING_HEXASCIIADDR
//    CRYPT_STRING_HEXASCII
//    CRYPT_STRING_HEX

  //
  // The following are error status bits
  //

  // These can be applied to certificates and chains
  public static int CERT_TRUST_NO_ERROR                            = 0x00000000;
  public static int CERT_TRUST_IS_NOT_TIME_VALID                   = 0x00000001;
  public static int CERT_TRUST_IS_NOT_TIME_NESTED                  = 0x00000002;
  public static int CERT_TRUST_IS_REVOKED                          = 0x00000004;
  public static int CERT_TRUST_IS_NOT_SIGNATURE_VALID              = 0x00000008;
  public static int CERT_TRUST_IS_NOT_VALID_FOR_USAGE              = 0x00000010;
  public static int CERT_TRUST_IS_UNTRUSTED_ROOT                   = 0x00000020;
  public static int CERT_TRUST_REVOCATION_STATUS_UNKNOWN           = 0x00000040;
  public static int CERT_TRUST_IS_CYCLIC                           = 0x00000080;

  public static int CERT_TRUST_INVALID_EXTENSION                   = 0x00000100;
  public static int CERT_TRUST_INVALID_POLICY_CONSTRAINTS          = 0x00000200;
  public static int CERT_TRUST_INVALID_BASIC_CONSTRAINTS           = 0x00000400;
  public static int CERT_TRUST_INVALID_NAME_CONSTRAINTS            = 0x00000800;
  public static int CERT_TRUST_HAS_NOT_SUPPORTED_NAME_CONSTRAINT   = 0x00001000;
  public static int CERT_TRUST_HAS_NOT_DEFINED_NAME_CONSTRAINT     = 0x00002000;
  public static int CERT_TRUST_HAS_NOT_PERMITTED_NAME_CONSTRAINT   = 0x00004000;
  public static int CERT_TRUST_HAS_EXCLUDED_NAME_CONSTRAINT        = 0x00008000;

  public static int CERT_TRUST_IS_OFFLINE_REVOCATION               = 0x01000000;
  public static int CERT_TRUST_NO_ISSUANCE_CHAIN_POLICY            = 0x02000000;


  // These can be applied to chains only

  public static int CERT_TRUST_IS_PARTIAL_CHAIN                    = 0x00010000;
  public static int CERT_TRUST_CTL_IS_NOT_TIME_VALID               = 0x00020000;
  public static int CERT_TRUST_CTL_IS_NOT_SIGNATURE_VALID          = 0x00040000;
  public static int CERT_TRUST_CTL_IS_NOT_VALID_FOR_USAGE          = 0x00080000;

  // CERT_CHAIN_CACHE_END_CERT can be used here as well
  // Revocation flags are in the high nibble
  public static int CERT_CHAIN_REVOCATION_CHECK_END_CERT           = 0x10000000;
  public static int CERT_CHAIN_REVOCATION_CHECK_CHAIN              = 0x20000000;
  public static int CERT_CHAIN_REVOCATION_CHECK_CHAIN_EXCLUDE_ROOT = 0x40000000;
  public static int CERT_CHAIN_REVOCATION_CHECK_CACHE_ONLY         = 0x80000000;

  public static final int CRYPT_FIRST = 0x1;
  public static final int CRYPT_NEXT = 0x2;

  //+-------------------------------------------------------------------------
  //  Open dwFlags
  //--------------------------------------------------------------------------
  public static final int CMSG_BARE_CONTENT_FLAG              = 0x00000001;
  public static final int CMSG_LENGTH_ONLY_FLAG               = 0x00000002;
  public static final int CMSG_DETACHED_FLAG                  = 0x00000004;
  public static final int CMSG_AUTHENTICATED_ATTRIBUTES_FLAG  = 0x00000008;
  public static final int CMSG_CONTENTS_OCTETS_FLAG           = 0x00000010;
  public static final int CMSG_MAX_LENGTH_FLAG                = 0x00000020;

  // When set, nonData type inner content is encapsulated within an
  // OCTET STRING. Applicable to both Signed and Enveloped messages.
  public static final int CMSG_CMS_ENCAPSULATED_CONTENT_FLAG  = 0x00000040;

  // If set, then, the hCryptProv passed to CryptMsgOpenToEncode or
  // CryptMsgOpenToDecode is released on the final CryptMsgClose.
  // Not released if CryptMsgOpenToEncode or CryptMsgOpenToDecode fails.
  //
  // Also applies to hNCryptKey where applicable.
  //
  // Note, the envelope recipient hCryptProv's aren't released.
  public static final int CMSG_CRYPT_RELEASE_CONTEXT_FLAG     = 0x00008000;

  //+-------------------------------------------------------------------------
  //  Message types
  //--------------------------------------------------------------------------
  public static final int CMSG_DATA                    = 1;
  public static final int CMSG_SIGNED                  = 2;
  public static final int CMSG_ENVELOPED               = 3;
  public static final int CMSG_SIGNED_AND_ENVELOPED    = 4;
  public static final int CMSG_HASHED                  = 5;
  public static final int CMSG_ENCRYPTED               = 6;


  //+-------------------------------------------------------------------------
  //  Message control types
  //--------------------------------------------------------------------------
  public static final int CMSG_CTRL_VERIFY_SIGNATURE        = 1;
  public static final int CMSG_CTRL_DECRYPT                 = 2;
  public static final int CMSG_CTRL_VERIFY_HASH             = 5;
  public static final int  CMSG_CTRL_ADD_SIGNER             = 6;
  public static final int  CMSG_CTRL_DEL_SIGNER             = 7;
  public static final int  CMSG_CTRL_ADD_SIGNER_UNAUTH_ATTR = 8;
  public static final int  CMSG_CTRL_DEL_SIGNER_UNAUTH_ATTR = 9;
  public static final int  CMSG_CTRL_ADD_CERT               = 10;
  public static final int  CMSG_CTRL_DEL_CERT               = 11;
  public static final int  CMSG_CTRL_ADD_CRL                = 12;
  public static final int  CMSG_CTRL_DEL_CRL                = 13;
  public static final int  CMSG_CTRL_ADD_ATTR_CERT          = 14;
  public static final int  CMSG_CTRL_DEL_ATTR_CERT          = 15;
  public static final int  CMSG_CTRL_KEY_TRANS_DECRYPT      = 16;
  public static final int  CMSG_CTRL_KEY_AGREE_DECRYPT      = 17;
  public static final int  CMSG_CTRL_MAIL_LIST_DECRYPT      = 18;
  public static final int  CMSG_CTRL_VERIFY_SIGNATURE_EX    = 19;
  public static final int  CMSG_CTRL_ADD_CMS_SIGNER_INFO    = 20;

  //+-------------------------------------------------------------------------
  //  Get parameter types and their corresponding data structure definitions.
  //--------------------------------------------------------------------------
  public static final int  CMSG_TYPE_PARAM                              = 1;
  public static final int  CMSG_CONTENT_PARAM                           = 2;
  public static final int  CMSG_BARE_CONTENT_PARAM                      = 3;
  public static final int  CMSG_INNER_CONTENT_TYPE_PARAM                = 4;
  public static final int  CMSG_SIGNER_COUNT_PARAM                      = 5;
  public static final int  CMSG_SIGNER_INFO_PARAM                       = 6;
  public static final int  CMSG_SIGNER_CERT_INFO_PARAM                  = 7;
  public static final int  CMSG_SIGNER_HASH_ALGORITHM_PARAM             = 8;
  public static final int  CMSG_SIGNER_AUTH_ATTR_PARAM                  = 9;
  public static final int  CMSG_SIGNER_UNAUTH_ATTR_PARAM                = 10;
  public static final int  CMSG_CERT_COUNT_PARAM                        = 11;
  public static final int  CMSG_CERT_PARAM                              = 12;
  public static final int  CMSG_CRL_COUNT_PARAM                         = 13;
  public static final int  CMSG_CRL_PARAM                               = 14;
  public static final int  CMSG_ENVELOPE_ALGORITHM_PARAM                = 15;
  public static final int  CMSG_RECIPIENT_COUNT_PARAM                   = 17;
  public static final int  CMSG_RECIPIENT_INDEX_PARAM                   = 18;
  public static final int  CMSG_RECIPIENT_INFO_PARAM                    = 19;
  public static final int  CMSG_HASH_ALGORITHM_PARAM                    = 20;
  public static final int  CMSG_HASH_DATA_PARAM                         = 21;
  public static final int  CMSG_COMPUTED_HASH_PARAM                     = 22;
  public static final int  CMSG_ENCRYPT_PARAM                           = 26;
  public static final int  CMSG_ENCRYPTED_DIGEST                        = 27;
  public static final int  CMSG_ENCODED_SIGNER                          = 28;
  public static final int  CMSG_ENCODED_MESSAGE                         = 29;
  public static final int  CMSG_VERSION_PARAM                           = 30;
  public static final int  CMSG_ATTR_CERT_COUNT_PARAM                   = 31;
  public static final int  CMSG_ATTR_CERT_PARAM                         = 32;
  public static final int  CMSG_CMS_RECIPIENT_COUNT_PARAM               = 33;
  public static final int  CMSG_CMS_RECIPIENT_INDEX_PARAM               = 34;
  public static final int  CMSG_CMS_RECIPIENT_ENCRYPTED_KEY_INDEX_PARAM = 35;
  public static final int  CMSG_CMS_RECIPIENT_INFO_PARAM                = 36;
  public static final int  CMSG_UNPROTECTED_ATTR_PARAM                  = 37;
  public static final int  CMSG_SIGNER_CERT_ID_PARAM                    = 38;
  public static final int  CMSG_CMS_SIGNER_INFO_PARAM                   = 39;

  public static final int CRL_FIND_ANY                = 0;
  public static final int CRL_FIND_ISSUED_BY          = 1;
  public static final int CRL_FIND_EXISTING           = 2;
  public static final int CRL_FIND_ISSUED_FOR         = 3;

  public static final int CRYPT_ACQUIRE_CACHE_FLAG               = 0x00000001;
  public static final int CRYPT_ACQUIRE_USE_PROV_INFO_FLAG       = 0x00000002;
  public static final int CRYPT_ACQUIRE_COMPARE_KEY_FLAG         = 0x00000004;
  public static final int CRYPT_ACQUIRE_NO_HEALING               = 0x00000008;

  public static final int CRYPT_ACQUIRE_SILENT_FLAG              = 0x00000040;

  public static final int CRYPT_ACQUIRE_NCRYPT_KEY_FLAGS_MASK    = 0x00070000;
  public static final int CRYPT_ACQUIRE_ALLOW_NCRYPT_KEY_FLAG    = 0x00010000;
  public static final int CRYPT_ACQUIRE_PREFER_NCRYPT_KEY_FLAG   = 0x00020000;
  public static final int CRYPT_ACQUIRE_ONLY_NCRYPT_KEY_FLAG     = 0x00040000;
}
