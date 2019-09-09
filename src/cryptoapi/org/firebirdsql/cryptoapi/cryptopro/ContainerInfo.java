package org.firebirdsql.cryptoapi.cryptopro;

/**
 * @author roman.kisluhin
 * @version 1.0
 *          Date: 16.01.13
 *          Time: 20:35
 */
public class ContainerInfo {
  public String containerName;
  public byte[] certData;
  public int keySpec;
  public int provType;

  public ContainerInfo(String containerName, byte[] certData, int keySpec, int provType) {
    this.containerName = containerName;
    this.certData = certData;
    this.keySpec = keySpec;
    this.provType = provType;
  }
}
