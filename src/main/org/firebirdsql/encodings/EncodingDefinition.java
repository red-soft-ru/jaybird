/*
 * Public Firebird Java API.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *    1. Redistributions of source code must retain the above copyright notice,
 *       this list of conditions and the following disclaimer.
 *    2. Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *    3. The name of the author may not be used to endorse or promote products
 *       derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO
 * EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS;
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR
 * OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.firebirdsql.encodings;

import java.nio.charset.Charset;

/**
 * Definition of a Firebird encoding. This is a mapping from the Firebird encoding to a Java Charset and additional
 * information needed by Jaybird to process this encoding.
 *
 * @author Mark Rotteveel
 * @since 3.0
 */
public interface EncodingDefinition {

    /**
     * @return Maximum number of bytes per character.
     */
    int getMaxBytesPerChar();

    /**
     * @return Java name of the encoding
     */
    String getJavaEncodingName();

    /**
     * @return Java {@link java.nio.charset.Charset} for this encoding
     */
    Charset getJavaCharset();

    /**
     * @return Firebird name of the encoding
     */
    String getFirebirdEncodingName();

    /**
     * @return Firebird id of the encoding
     */
    int getFirebirdCharacterSetId();

    /**
     * Can (or should) this encoding be used for reverse mapping from Java to Firebird.
     * <p>
     * The best example of this is the Firebird character set <code>UNICODE-FSS</code> which maps to the
     * Java character set <code>UTF-8</code>, but when Java character set<code>UTF-8</code> is requested, Jaybird
     * should (in general) map to Firebird character set <code>UTF8</code>.
     * </p>
     *
     * @return <code>true</code> when this encoding maps from Java to Firebird, <code>false</code> otherwise
     */
    boolean isFirebirdOnly();

    /**
     * Can this implementation create an {@link Encoding} instance, or does it provide information only (e.g. about
     * unsupported character sets)
     *
     * @return <code>true</code> if this EncodingDefinition only provides information, and is not capable of building a
     *         concrete implementation.
     */
    boolean isInformationOnly();

    /**
     * Gets the {@link Encoding} based on this definition.
     * <p>
     * Implementations can return the same instance on every call, or create a new one each time this method is called
     * </p>
     *
     * @return Encoding object or <code>null</code> if this is an information only EncodingDefinition
     * @see #isInformationOnly()
     */
    Encoding getEncoding();
}
