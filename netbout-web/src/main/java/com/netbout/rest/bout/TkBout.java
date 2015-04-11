/**
 * Copyright (c) 2009-2014, netbout.com
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are PROHIBITED without prior written permission from
 * the author. This product may NOT be used anywhere and on any computer
 * except the server platform of netbout Inc. located at www.netbout.com.
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
package com.netbout.rest.bout;

import com.netbout.rest.inbox.TkIndex;
import com.netbout.spi.Base;
import com.netbout.spi.Bout;
import com.netbout.spi.Friends;
import com.netbout.spi.Inbox;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.logging.Level;
import org.takes.facets.fork.FkMethods;
import org.takes.facets.fork.FkRegex;
import org.takes.facets.fork.TkFork;
import org.takes.tk.TkWrap;

/**
 * Bout.
 *
 * @author Yegor Bugayenko (yegor@tpc2.com)
 * @version $Id$
 * @since 2.14
 */
public final class TkBout extends TkWrap {

    /**
     * Ctor.
     * @param base Base
     */
    public TkBout(final Base base) {
        super(
            new TkFork(
                new FkRegex(
                    "/b/[0-9]+",
                    new TkFork(
                        new FkMethods("GET", new TkIndex(base))
                    )
                ),
                new FkRegex(
                    "/acc/save",
                    new TkFork(
                        new FkMethods("POST", new TkSaveEmail(base))
                    )
                )
            )
        );
    }

    /**
     * Get bout.
     * @return The bout
     * @throws java.io.IOException If fails
     */
    private Bout bout() throws IOException {
        final Bout bout;
        try {
            bout = this.alias().inbox().bout(this.number);
        } catch (final Inbox.BoutNotFoundException ex) {
            throw new WebApplicationException(
                ex, HttpURLConnection.HTTP_NOT_FOUND
            );
        }
        if (!new Friends.Search(bout.friends()).exists(this.alias().name())) {
            throw FlashInset.forward(
                this.uriInfo().getBaseUri(),
                String.format("you're not in bout #%d", bout.number()),
                Level.WARNING
            );
        }
        return bout;
    }

}