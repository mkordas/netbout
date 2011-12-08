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
package com.netbout.rest;

import com.netbout.bus.Bus;
import com.netbout.bus.DefaultBus;
import com.netbout.hub.DefaultHub;
import com.netbout.hub.Hub;
import com.netbout.spi.Helper;
import com.netbout.spi.Identity;
import com.netbout.spi.cpa.CpaHelper;
import com.netbout.utils.Promoter;
import com.sun.jersey.api.client.Client;
import com.ymock.util.Logger;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;

/**
 * Starter.
 *
 * @author Yegor Bugayenko (yegor@netbout.com)
 * @version $Id$
 */
@Provider
public final class Starter implements ContextResolver<Starter> {

    /**
     * Bus.
     */
    private final transient Bus bus = new DefaultBus();

    /**
     * Hub.
     */
    private final transient Hub hub = new DefaultHub(this.bus);

    /**
     * Public ctor.
     * @param context Servlet context
     * @param info URI info
     * @checkstyle ExecutableStatementCount (3 lines)
     */
    public Starter(@Context final ServletContext context) {
        final long start = System.currentTimeMillis();
        context.setAttribute("com.netbout.rest.HUB", this.hub);
        context.setAttribute("com.netbout.rest.BUS", this.bus);
        this.start();
        Logger.info(
            this,
            "#Starter(%s): done in %dms",
            context.getClass().getName(),
            System.currentTimeMillis() - start
        );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Starter getContext(final Class<?> cls) {
        throw new UnsupportedOperationException("Starter#getContext()");
    }

    /**
     * Start all.
     */
    private void start() {
        final Promoter promoter = new Promoter(this.hub);
        try {
            final Identity starter = this.hub
                .user(
                    UriBuilder.fromUri("http://www.netbout.com")
                        .path("/hauth")
                        .build()
                        .toString()
                )
                .identity("nb:starter");
            promoter.promote(
                starter.friend("nb:db"),
                new URL("file", "", "com.netbout.db")
            );
            final List<String> helpers = bus.make("get-all-helpers")
                .synchronously()
                .asDefault(new ArrayList<String>())
                .exec();
            for (String name : helpers) {
                final String url = bus.make("get-helper-url")
                    .synchronously()
                    .asDefault("")
                    .exec();
                promoter.promote(starter.friend(name), new URL(url));
            }
        } catch (com.netbout.spi.UnreachableIdentityException ex) {
            throw new IllegalStateException(ex);
        } catch (java.net.MalformedURLException ex) {
            throw new IllegalStateException(ex);
        }
    }

}
