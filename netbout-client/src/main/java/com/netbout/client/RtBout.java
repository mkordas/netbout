/**
 * Copyright (c) 2009-2014, Netbout.com
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are PROHIBITED without prior written permission from
 * the author. This product may NOT be used anywhere and on any computer
 * except the server platform of netBout Inc. located at www.netbout.com.
 * Federal copyright law prohibits unauthorized reproduction by any means
 * and imposes fines up to $25,000 for violation. If you received
 * this code accidentally and without intent to use it, please report this
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
package com.netbout.client;

import com.jcabi.aspects.Immutable;
import com.jcabi.http.Request;
import com.jcabi.http.response.XmlResponse;
import com.netbout.spi.Attachments;
import com.netbout.spi.Bout;
import com.netbout.spi.Friends;
import com.netbout.spi.Messages;
import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import org.apache.commons.lang3.time.DateFormatUtils;

/**
 * REST bout.
 *
 * @author Yegor Bugayenko (yegor@tpc2.com)
 * @version $Id$
 * @since 2.0
 */
@Immutable
final class RtBout implements Bout {

    /**
     * Request to use.
     */
    private final transient Request request;

    /**
     * Public ctor.
     * @param req Request to use
     */
    RtBout(final Request req) {
        this.request = req;
    }

    @Override
    public long number() throws IOException {
        return Long.parseLong(
            this.request.fetch()
                .as(XmlResponse.class)
                .xml()
                .xpath("/page/bout/number/text()")
                .get(0)
        );
    }

    @Override
    public Date date() throws IOException {
        try {
            return DateFormatUtils.ISO_DATETIME_FORMAT.parse(
                this.request.fetch()
                    .as(XmlResponse.class)
                    .xml()
                    .xpath("/page/bout/date/text()")
                    .get(0)
            );
        } catch (final ParseException ex) {
            throw new IllegalStateException(ex);
        }
    }

    @Override
    public String title() throws IOException {
        return this.request.fetch()
            .as(XmlResponse.class)
            .xml()
            .xpath("/page/bout/title/text()")
            .get(0);
    }

    @Override
    public void rename(final String text) {
        throw new UnsupportedOperationException("#rename()");
    }

    @Override
    public Messages messages() {
        return new RtMessages(this.request);
    }

    @Override
    public Friends friends() {
        throw new UnsupportedOperationException("#friends()");
    }

    @Override
    public Attachments attachments() {
        return new RtAttachments(this.request);
    }
}