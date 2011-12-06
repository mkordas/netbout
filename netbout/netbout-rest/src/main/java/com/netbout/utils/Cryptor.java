/**
 * Copyright (c) 2009-2011, netBout.com
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are PROHIBITED without prior written permission from
 * the author. This product may NOT be used anywhere and on any computer
 * except the server platform of netBout Inc. located at www.netbout.com.
 * Federal copyright law prohibits unauthorized reproduction by any means
 * and imposes fines up to $25,000 for violation. If you received
 * this code occasionally and without intent to use it, please report this
 * incident to the author by email.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 */
package com.netbout.utils;

import com.netbout.hub.Hub;
import com.netbout.spi.Identity;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;

/**
 * Encrypts and decrypts.
 *
 * @author Yegor Bugayenko (yegor@netbout.com)
 * @version $Id$
 */
public final class Cryptor {

    /**
     * Separator between name and hash.
     */
    private static final String SEPARATOR = ".";

    /**
     * Encrypt user+identity into text.
     * @param identity The identity
     * @return Encrypted string
     */
    public String encrypt(final Identity identity) {
        final StringBuilder builder = new StringBuilder();
        builder
            .append(TextUtils.toBase(identity.user()))
            .append(this.SEPARATOR)
            .append(TextUtils.toBase(identity.name()));
        return TextUtils.toBase(this.pack(builder.toString()));
    }

    /**
     * Get identity from hash.
     * @param hub Hub where to get identities
     * @param hash The hash to use
     * @return The name found in it
     * @throws DecryptionException If we can't decrypt it
     */
    public Identity decrypt(final Hub hub, final String hash)
        throws DecryptionException {
        if (hash == null) {
            throw new DecryptionException(hash, "Hash is NULL");
        }
        final String[] parts = StringUtils.split(
            TextUtils.fromBase(this.unpack(hash)),
            this.SEPARATOR
        );
        if (parts.length != 2) {
            throw new DecryptionException(hash, "Not enough parts");
        }
        final String uname = TextUtils.fromBase(parts[0]);
        final String iname = TextUtils.fromBase(parts[1]);
        try {
            return hub.user(uname).identity(iname);
        } catch (com.netbout.spi.UnreachableIdentityException ex) {
            throw new DecryptionException(ex);
        }
    }

    /**
     * Pack with encryption.
     * @param text The text to work with
     * @return The packed string
     */
    private String pack(final String text) {
        return text;
    }

    /**
     * Decrypt and unpack.
     * @param hash Packed text
     * @return The original string
     */
    private String unpack(final String hash) {
        return hash;
    }

}
