package org.firebirdsql.cryptoapi.windows.sspi;

import com.sun.jna.Library;

/**
 * User: xmel
 * Date: 6/13/12
 * Time: 8:47 PM
 */
public interface SspiLib extends Library {
  //
  //  Credential Use Flags
  //
  public static final long SECPKG_CRED_INBOUND       = 0x00000001;
  public static final long SECPKG_CRED_OUTBOUND      = 0x00000002;
  public static final long SECPKG_CRED_BOTH          = 0x00000003;
  public static final long SECPKG_CRED_DEFAULT       = 0x00000004;
  public static final long SECPKG_CRED_RESERVED      = 0xF0000000;

  public static final int SEC_E_OK                  = 0x00000000;
  public static final int SEC_E_UNKNOWN_CREDENTIALS = 0x8009030D;
  public static final int SEC_E_NO_CREDENTIALS      = 0x8009030E;
  public static final int SEC_E_INVALID_HANDLE      = 0x80090301;
  public static final int SEC_I_CONTINUE_NEEDED     = 0x00090312;
  public static final int SEC_E_INSUFFICIENT_MEMORY = 0x80090300;
  public static final int SEC_E_CONTEXT_EXPIRED     = 0x80090317;
  public static final int SEC_I_CONTEXT_EXPIRED     = 0x00090317;
  public static final int SEC_I_RENEGOTIATE         = 0x00090321;
  public static final int SEC_E_INCOMPLETE_MESSAGE  = 0x80090318;

  public static final int SCH_CRED_NO_SYSTEM_MAPPER                    = 0x00000002;
  public static final int SCH_CRED_NO_SERVERNAME_CHECK                 = 0x00000004;
  public static final int SCH_CRED_MANUAL_CRED_VALIDATION              = 0x00000008;
  public static final int SCH_CRED_NO_DEFAULT_CREDS                    = 0x00000010;
  public static final int SCH_CRED_AUTO_CRED_VALIDATION                = 0x00000020;
  public static final int SCH_CRED_USE_DEFAULT_CREDS                   = 0x00000040;
  
  public static final int SCH_CRED_REVOCATION_CHECK_END_CERT           = 0x00000100;
  public static final int SCH_CRED_REVOCATION_CHECK_CHAIN              = 0x00000200;
  public static final int SCH_CRED_REVOCATION_CHECK_CHAIN_EXCLUDE_ROOT = 0x00000400;
  public static final int SCH_CRED_IGNORE_NO_REVOCATION_CHECK          = 0x00000800;
  public static final int SCH_CRED_IGNORE_REVOCATION_OFFLINE           = 0x00001000;

  public static final long ISC_REQ_DELEGATE                 = 0x00000001;
  public static final long ISC_REQ_MUTUAL_AUTH              = 0x00000002;
  public static final long ISC_REQ_REPLAY_DETECT            = 0x00000004;
  public static final long ISC_REQ_SEQUENCE_DETECT          = 0x00000008;
  public static final long ISC_REQ_CONFIDENTIALITY          = 0x00000010;
  public static final long ISC_REQ_USE_SESSION_KEY          = 0x00000020;
  public static final long ISC_REQ_PROMPT_FOR_CREDS         = 0x00000040;
  public static final long ISC_REQ_USE_SUPPLIED_CREDS       = 0x00000080;
  public static final long ISC_REQ_ALLOCATE_MEMORY          = 0x00000100;
  public static final long ISC_REQ_USE_DCE_STYLE            = 0x00000200;
  public static final long ISC_REQ_DATAGRAM                 = 0x00000400;
  public static final long ISC_REQ_CONNECTION               = 0x00000800;
  public static final long ISC_REQ_CALL_LEVEL               = 0x00001000;
  public static final long ISC_REQ_FRAGMENT_SUPPLIED        = 0x00002000;
  public static final long ISC_REQ_EXTENDED_ERROR           = 0x00004000;
  public static final long ISC_REQ_STREAM                   = 0x00008000;
  public static final long ISC_REQ_INTEGRITY                = 0x00010000;
  public static final long ISC_REQ_IDENTIFY                 = 0x00020000;
  public static final long ISC_REQ_NULL_SESSION             = 0x00040000;
  public static final long ISC_REQ_MANUAL_CRED_VALIDATION   = 0x00080000;
  public static final long ISC_REQ_RESERVED1                = 0x00100000;
  public static final long ISC_REQ_FRAGMENT_TO_FIT          = 0x00200000;

  public static final long ISC_RET_DELEGATE                 = 0x00000001;
  public static final long ISC_RET_MUTUAL_AUTH              = 0x00000002;
  public static final long ISC_RET_REPLAY_DETECT            = 0x00000004;
  public static final long ISC_RET_SEQUENCE_DETECT          = 0x00000008;
  public static final long ISC_RET_CONFIDENTIALITY          = 0x00000010;
  public static final long ISC_RET_USE_SESSION_KEY          = 0x00000020;
  public static final long ISC_RET_USED_COLLECTED_CREDS     = 0x00000040;
  public static final long ISC_RET_USED_SUPPLIED_CREDS      = 0x00000080;
  public static final long ISC_RET_ALLOCATED_MEMORY         = 0x00000100;
  public static final long ISC_RET_USED_DCE_STYLE           = 0x00000200;
  public static final long ISC_RET_DATAGRAM                 = 0x00000400;
  public static final long ISC_RET_CONNECTION               = 0x00000800;
  public static final long ISC_RET_INTERMEDIATE_RETURN      = 0x00001000;
  public static final long ISC_RET_CALL_LEVEL               = 0x00002000;
  public static final long ISC_RET_EXTENDED_ERROR           = 0x00004000;
  public static final long ISC_RET_STREAM                   = 0x00008000;
  public static final long ISC_RET_INTEGRITY                = 0x00010000;
  public static final long ISC_RET_IDENTIFY                 = 0x00020000;
  public static final long ISC_RET_NULL_SESSION             = 0x00040000;
  public static final long ISC_RET_MANUAL_CRED_VALIDATION   = 0x00080000;
  public static final long ISC_RET_RESERVED1                = 0x00100000;
  public static final long ISC_RET_FRAGMENT_ONLY            = 0x00200000;

  public static final long ASC_REQ_DELEGATE                 = 0x00000001;
  public static final long ASC_REQ_MUTUAL_AUTH              = 0x00000002;
  public static final long ASC_REQ_REPLAY_DETECT            = 0x00000004;
  public static final long ASC_REQ_SEQUENCE_DETECT          = 0x00000008;
  public static final long ASC_REQ_CONFIDENTIALITY          = 0x00000010;
  public static final long ASC_REQ_USE_SESSION_KEY          = 0x00000020;
  public static final long ASC_REQ_ALLOCATE_MEMORY          = 0x00000100;
  public static final long ASC_REQ_USE_DCE_STYLE            = 0x00000200;
  public static final long ASC_REQ_DATAGRAM                 = 0x00000400;
  public static final long ASC_REQ_CONNECTION               = 0x00000800;
  public static final long ASC_REQ_CALL_LEVEL               = 0x00001000;
  public static final long ASC_REQ_EXTENDED_ERROR           = 0x00008000;
  public static final long ASC_REQ_STREAM                   = 0x00010000;
  public static final long ASC_REQ_INTEGRITY                = 0x00020000;
  public static final long ASC_REQ_LICENSING                = 0x00040000;
  public static final long ASC_REQ_IDENTIFY                 = 0x00080000;
  public static final long ASC_REQ_ALLOW_NULL_SESSION       = 0x00100000;
  public static final long ASC_REQ_ALLOW_NON_USER_LOGONS    = 0x00200000;
  public static final long ASC_REQ_ALLOW_CONTEXT_REPLAY     = 0x00400000;
  public static final long ASC_REQ_FRAGMENT_TO_FIT          = 0x00800000;
  public static final long ASC_REQ_FRAGMENT_SUPPLIED        = 0x00002000;

  public static final long ASC_RET_DELEGATE                 = 0x00000001;
  public static final long ASC_RET_MUTUAL_AUTH              = 0x00000002;
  public static final long ASC_RET_REPLAY_DETECT            = 0x00000004;
  public static final long ASC_RET_SEQUENCE_DETECT          = 0x00000008;
  public static final long ASC_RET_CONFIDENTIALITY          = 0x00000010;
  public static final long ASC_RET_USE_SESSION_KEY          = 0x00000020;
  public static final long ASC_RET_ALLOCATED_MEMORY         = 0x00000100;
  public static final long ASC_RET_USED_DCE_STYLE           = 0x00000200;
  public static final long ASC_RET_DATAGRAM                 = 0x00000400;
  public static final long ASC_RET_CONNECTION               = 0x00000800;
  public static final long ASC_RET_CALL_LEVEL               = 0x00002000; // skipped 1000 to be like ISC_
  public static final long ASC_RET_THIRD_LEG_FAILED         = 0x00004000;
  public static final long ASC_RET_EXTENDED_ERROR           = 0x00008000;
  public static final long ASC_RET_STREAM                   = 0x00010000;
  public static final long ASC_RET_INTEGRITY                = 0x00020000;
  public static final long ASC_RET_LICENSING                = 0x00040000;
  public static final long ASC_RET_IDENTIFY                 = 0x00080000;
  public static final long ASC_RET_NULL_SESSION             = 0x00100000;
  public static final long ASC_RET_ALLOW_NON_USER_LOGONS    = 0x00200000;
  public static final long ASC_RET_ALLOW_CONTEXT_REPLAY     = 0x00400000;
  public static final long ASC_RET_FRAGMENT_ONLY            = 0x00800000;

  public static final long SECBUFFER_VERSION = 0;

  public static final long SECBUFFER_EMPTY             = 0;   // Undefined, replaced by provider
  public static final long SECBUFFER_DATA              = 1;   // Packet data
  public static final long SECBUFFER_TOKEN             = 2;   // Security token
  public static final long SECBUFFER_PKG_PARAMS        = 3;   // Package specific parameters
  public static final long SECBUFFER_MISSING           = 4;   // Missing Data indicator
  public static final long SECBUFFER_EXTRA             = 5;   // Extra data
  public static final long SECBUFFER_STREAM_TRAILER    = 6;   // Security Trailer
  public static final long SECBUFFER_STREAM_HEADER     = 7;   // Security Header
  public static final long SECBUFFER_NEGOTIATION_INFO  = 8;   // Hints from the negotiation pkg
  public static final long SECBUFFER_PADDING           = 9;   // non-data padding
  public static final long SECBUFFER_STREAM            = 10;  // whole encrypted message
  public static final long SECBUFFER_MECHLIST          = 11;
  public static final long SECBUFFER_MECHLIST_SIGNATURE = 12;
  public static final long SECBUFFER_TARGET            = 13;
  public static final long SECBUFFER_CHANNEL_BINDINGS  = 14;
  //
  // QueryContextAttributes/QueryCredentialsAttribute extensions
  //
  public static final long SECPKG_ATTR_ISSUER_LIST           = 0x50;   // (OBSOLETE) returns SecPkgContext_IssuerListInfo
  public static final long SECPKG_ATTR_REMOTE_CRED           = 0x51;   // (OBSOLETE) returns SecPkgContext_RemoteCredentialInfo
  public static final long SECPKG_ATTR_LOCAL_CRED            = 0x52;   // (OBSOLETE) returns SecPkgContext_LocalCredentialInfo
  public static final long SECPKG_ATTR_REMOTE_CERT_CONTEXT   = 0x53;   // returns PPCCERT_CONTEXT
  public static final long SECPKG_ATTR_LOCAL_CERT_CONTEXT    = 0x54;   // returns PPCCERT_CONTEXT
  public static final long SECPKG_ATTR_ROOT_STORE            = 0x55;   // returns HCERTCONTEXT to the root store
  public static final long SECPKG_ATTR_SUPPORTED_ALGS        = 0x56;   // returns SecPkgCred_SupportedAlgs
  public static final long SECPKG_ATTR_CIPHER_STRENGTHS      = 0x57;   // returns SecPkgCred_CipherStrengths
  public static final long SECPKG_ATTR_SUPPORTED_PROTOCOLS   = 0x58;   // returns SecPkgCred_SupportedProtocols
  public static final long SECPKG_ATTR_ISSUER_LIST_EX        = 0x59;   // returns SecPkgContext_IssuerListInfoEx
  public static final long SECPKG_ATTR_CONNECTION_INFO       = 0x5a;   // returns SecPkgContext_ConnectionInfo
  public static final long SECPKG_ATTR_EAP_KEY_BLOCK         = 0x5b;   // returns SecPkgContext_EapKeyBlock
  public static final long SECPKG_ATTR_MAPPED_CRED_ATTR      = 0x5c;   // returns SecPkgContext_MappedCredAttr
  public static final long SECPKG_ATTR_SESSION_INFO          = 0x5d;   // returns SecPkgContext_SessionInfo
  public static final long SECPKG_ATTR_APP_DATA              = 0x5e;   // sets/returns SecPkgContext_SessionAppData
  public static final long SECPKG_ATTR_REMOTE_CERTIFICATES   = 0x5F;   // returns SecPkgContext_Certificates
  public static final long SECPKG_ATTR_CLIENT_CERT_POLICY    = 0x60;   // sets    SecPkgCred_ClientCertCtlPolicy
  public static final long SECPKG_ATTR_CC_POLICY_RESULT      = 0x61;   // returns SecPkgContext_ClientCertPolicyResult
  public static final long SECPKG_ATTR_USE_NCRYPT            = 0x62;   // Sets the CRED_FLAG_USE_NCRYPT_PROVIDER FLAG on cred group
  public static final long SECPKG_ATTR_LOCAL_CERT_INFO       = 0x63;   // returns SecPkgContext_CertInfo
  public static final long SECPKG_ATTR_CIPHER_INFO           = 0x64;   // returns new CNG SecPkgContext_CipherInfo
  //
  //  Security Context Attributes:
  //
  public static final long SECPKG_ATTR_SIZES           = 0;
  public static final long SECPKG_ATTR_NAMES           = 1;
  public static final long SECPKG_ATTR_LIFESPAN        = 2;
  public static final long SECPKG_ATTR_DCE_INFO        = 3;
  public static final long SECPKG_ATTR_STREAM_SIZES    = 4;
  public static final long SECPKG_ATTR_KEY_INFO        = 5;
  public static final long SECPKG_ATTR_AUTHORITY       = 6;
  public static final long SECPKG_ATTR_PROTO_INFO      = 7;
  public static final long SECPKG_ATTR_PASSWORD_EXPIRY = 8;
  public static final long SECPKG_ATTR_SESSION_KEY     = 9;
  public static final long SECPKG_ATTR_PACKAGE_INFO    = 10;
  public static final long SECPKG_ATTR_USER_FLAGS      = 11;
  public static final long SECPKG_ATTR_NEGOTIATION_INFO = 12;
  public static final long SECPKG_ATTR_NATIVE_NAMES    = 13;
  public static final long SECPKG_ATTR_FLAGS           = 14;
  public static final long SECPKG_ATTR_USE_VALIDATED   = 15;
  public static final long SECPKG_ATTR_CREDENTIAL_NAME = 16;
  public static final long SECPKG_ATTR_TARGET_INFORMATION = 17;
  public static final long SECPKG_ATTR_ACCESS_TOKEN    = 18;

  public static final long SECPKG_ATTR_UNIQUE_BINDINGS   = 25;
  public static final long SECPKG_ATTR_ENDPOINT_BINDINGS = 26;
  //
  //
  // ApplyControlToken PkgParams types
  //
  // These identifiers are the DWORD types
  // to be passed into ApplyControlToken
  // through a PkgParams buffer.
  public static final int SCHANNEL_RENEGOTIATE   = 0;   // renegotiate a connection
  public static final int SCHANNEL_SHUTDOWN      = 1;   // gracefully close down a connection
  public static final int SCHANNEL_ALERT         = 2;   // build an error message
  public static final int SCHANNEL_SESSION       = 3;   // session control
  // Session control flags
  public static final int SSL_SESSION_ENABLE_RECONNECTS  = 1;
  public static final int SSL_SESSION_DISABLE_RECONNECTS = 2;


  public static final String UNISP_NAME = "Microsoft Unified Security Protocol Provider";
}
