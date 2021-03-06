/*
 * Firebird Open Source JavaEE Connector - JDBC Driver
 *
 * Distributable under LGPL license.
 * You may obtain a copy of the License at http://www.gnu.org/copyleft/lgpl.html
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * LGPL License for more details.
 *
 * This file was created by members of the firebird development team.
 * All individual contributions remain the Copyright (C) of those
 * individuals.  Contributors to this file are either listed here or
 * can be obtained from a source control history command.
 *
 * All rights reserved.
 */
package org.firebirdsql.gds.ng;

import static java.util.Objects.requireNonNull;

/**
 * Abstract mutable implementation of {@link IAttachProperties}.
 *
 * @author <a href="mailto:mrotteveel@users.sourceforge.net">Mark Rotteveel</a>
 * @since 3.0
 */
public abstract class AbstractAttachProperties<T extends IAttachProperties<T>> implements IAttachProperties<T> {

    private String serverName = IAttachProperties.DEFAULT_SERVER_NAME;
    private int portNumber = IAttachProperties.DEFAULT_PORT;
    private String user;
    private String password;
    private String passwordEnc;
    private String roleName;
    private String charSet;
    private String encoding;
    private String certificate;
    private String certificateBase64;
    private boolean notEncryptPassword;
    private String repositoryPin;
    private int providerID;
    private int socketBufferSize = IAttachProperties.DEFAULT_SOCKET_BUFFER_SIZE;
    private int soTimeout = IAttachProperties.DEFAULT_SO_TIMEOUT;
    private int connectTimeout = IAttachProperties.DEFAULT_CONNECT_TIMEOUT;
    private boolean useGSSAuth = IAttachProperties.DEFAULT_GSS_AUTH;
    private WireCrypt wireCrypt = WireCrypt.DEFAULT;
    private String dbCryptConfig;
    private String authPlugins;
    private boolean wireCompression;
    private String excludeCryptoPlugins;
    private boolean verifyServerCertificate = IAttachProperties.DEFAULT_SERVER_CERTIFICATE;
    private String effectiveLogin;

    /**
     * Copy constructor for IAttachProperties.
     * <p>
     * All properties defined in {@link IAttachProperties} are copied from <code>src</code> to the new instance.
     * </p>
     *
     * @param src
     *         Source to copy from
     */
    protected AbstractAttachProperties(IAttachProperties<T> src) {
        if (src != null) {
            serverName = src.getServerName();
            portNumber = src.getPortNumber();
            user = src.getUser();
            password = src.getPassword();
            passwordEnc = src.getPasswordEnc();
            roleName = src.getRoleName();
            charSet = src.getCharSet();
            encoding = src.getEncoding();
            socketBufferSize = src.getSocketBufferSize();
            soTimeout = src.getSoTimeout();
            connectTimeout = src.getConnectTimeout();
            useGSSAuth = src.isUseGSSAuth();
            wireCrypt = src.getWireCrypt();
            dbCryptConfig = src.getDbCryptConfig();
            authPlugins = src.getAuthPlugins();
            wireCompression = src.isWireCompression();
            certificate = src.getCertificate();
            certificateBase64 = src.getCertificateBase64();
            notEncryptPassword = src.isNotEncryptedPassword();
            repositoryPin = src.getRepositoryPin();
            providerID = src.getProviderID();
            verifyServerCertificate = src.getVerifyServerCertificate();
            effectiveLogin = src.getEffectiveLogin();
            excludeCryptoPlugins = src.getExcludeCryptoPlugins();
        }
    }

    /**
     * Default constructor for AbstractAttachProperties
     */
    protected AbstractAttachProperties() {
    }

    @Override
    public String getServerName() {
        return serverName;
    }

    @Override
    public void setServerName(String serverName) {
        this.serverName = serverName;
        dirtied();
    }

    @Override
    public int getPortNumber() {
        return portNumber;
    }

    @Override
    public void setPortNumber(int portNumber) {
        this.portNumber = portNumber;
        dirtied();
    }

    @Override
    public String getUser() {
        return user;
    }

    @Override
    public void setUser(String user) {
        this.user = user;
        dirtied();
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public void setPassword(String password) {
        this.password = password;
        dirtied();
    }

    @Override
    public String getPasswordEnc() {
        return passwordEnc;
    }

    @Override
    public void setPasswordEnc(String passwordEnc) {
        this.passwordEnc = passwordEnc;
        dirtied();
    }

    @Override
    public String getRoleName() {
        return roleName;
    }

    @Override
    public void setRoleName(String roleName) {
        this.roleName = roleName;
        dirtied();
    }

    @Override
    public String getCharSet() {
        return charSet;
    }

    @Override
    public void setCharSet(String charSet) {
        this.charSet = charSet;
        dirtied();
    }

    @Override
    public String getEncoding() {
        return encoding;
    }

    @Override
    public void setEncoding(String encoding) {
        this.encoding = encoding;
        dirtied();
    }

    @Override
    public int getSocketBufferSize() {
        return socketBufferSize;
    }

    @Override
    public void setSocketBufferSize(int socketBufferSize) {
        this.socketBufferSize = socketBufferSize;
        dirtied();
    }

    @Override
    public int getSoTimeout() {
        return soTimeout;
    }

    @Override
    public void setSoTimeout(int soTimeout) {
        this.soTimeout = soTimeout;
        dirtied();
    }

    @Override
    public int getConnectTimeout() {
        return connectTimeout;
    }

    @Override
    public void setConnectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
        dirtied();
    }

    @Override
    public boolean isUseGSSAuth() {
        return useGSSAuth;
    }

    @Override
    public void setUseGSSAuth(boolean useGSSAuth) {
        this.useGSSAuth = useGSSAuth;
        dirtied();
    }


    @Override
    public WireCrypt getWireCrypt() {
        return wireCrypt;
    }

    @Override
    public void setWireCrypt(WireCrypt wireCrypt) {
        this.wireCrypt = requireNonNull(wireCrypt, "wireCrypt");
        dirtied();
    }
	
	@Override
    public String getCertificate() {
        return certificate;
    }

    @Override
    public void setCertificate(String certificate) {
        this.certificate = certificate;
        dirtied();
    }

    @Override
    public String getCertificateBase64() {
        return certificateBase64;
    }

    @Override
    public void setCertificateBase64(String certificateBase64) {
        this.certificateBase64 = certificateBase64;
        dirtied();
    }

    @Override
    public boolean isNotEncryptedPassword() {
        return notEncryptPassword;
    }

    @Override
    public void setNotEncryptedPassword(final boolean notEncryptPassword) {
        this.notEncryptPassword = notEncryptPassword;
        dirtied();
    }


    @Override
    public String getRepositoryPin() {
        return repositoryPin;
    }

    @Override
    public void setRepositoryPin(String pin) {
        this.repositoryPin = pin;
        dirtied();
    }

    @Override
    public int getProviderID() {
        return providerID;
    }

    @Override
    public void setProviderID(int providerID) {
        this.providerID = providerID;
        dirtied();
    }

    @Override
    public boolean getVerifyServerCertificate() {
        return verifyServerCertificate;
    }

    @Override
    public void setVerifyServerCertificate(boolean verify) {
        this.verifyServerCertificate = verify;
        dirtied();
    }

    @Override
    public String getEffectiveLogin() {
        return effectiveLogin;
    }

    @Override
    public void setEffectiveLogin(String login) {
        this.effectiveLogin = login;
        dirtied();
    }

    @Override
    public String getDbCryptConfig() {
        return dbCryptConfig;
    }

    @Override
    public void setDbCryptConfig(String dbCryptConfig) {
        this.dbCryptConfig = dbCryptConfig;
        dirtied();
    }

    @Override
    public String getAuthPlugins() {
        return authPlugins;
    }

    @Override
    public void setAuthPlugins(String authPlugins) {
        this.authPlugins = authPlugins;
        dirtied();
    }

    @Override
    public boolean isWireCompression() {
        return wireCompression;
    }

    @Override
    public void setWireCompression(boolean wireCompression) {
        this.wireCompression = wireCompression;
        dirtied();
    }

    @Override
    public String getExcludeCryptoPlugins() {
        return excludeCryptoPlugins;
    }

    @Override
    public void setExcludeCryptoPlugins(String authPlugins) {
        this.excludeCryptoPlugins = authPlugins;
        dirtied();
    }

    /**
     * Called by setters if they have been called.
     */
    protected abstract void dirtied();
}
