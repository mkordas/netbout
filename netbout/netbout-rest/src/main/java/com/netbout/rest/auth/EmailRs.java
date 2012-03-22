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
package com.netbout.rest.auth;

import com.netbout.notifiers.email.EmailFarm;
import com.netbout.rest.AbstractPage;
import com.netbout.rest.AbstractRs;
import com.netbout.rest.LoginRequiredException;
import com.netbout.rest.page.PageBuilder;
import com.netbout.spi.Identity;
import com.netbout.spi.Urn;
import com.netbout.spi.text.SecureString;
import java.net.URL;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

/**
 * Authorizer of "urn:email:..." identities.
 *
 * @author Yegor Bugayenko (yegor@netbout.com)
 * @version $Id$
 */
@Path("/email")
public final class EmailRs extends AbstractRs {

    /**
     * Authentication page.
     * @param iname Name of identity
     * @param secret The secret code
     * @return The JAX-RS response
     * @todo #158 Path annotation: http://java.net/jira/browse/JERSEY-739
     */
    @GET
    @Path("/")
    public Response auth(@QueryParam("identity") final Urn iname,
        @QueryParam("secret") final String secret) {
        return new PageBuilder()
            .build(AbstractPage.class)
            .init(this, false)
            .render()
            .authenticated(this.authenticate(iname, secret))
            .build();
    }

    /**
     * Validate them.
     * @param iname Name of identity
     * @param secret The secret code
     * @return The identity just authenticated
     */
    private Identity authenticate(final Urn iname, final String secret) {
        if ((iname == null) || (secret == null)) {
            throw new LoginRequiredException(
                this,
                "Both 'identity' and 'secret' query params are mandatory"
            );
        }
        if (!EmailFarm.NID.equals(iname.nid())) {
            throw new LoginRequiredException(
                this,
                String.format("Bad namespace '%s' in '%s'", iname.nid(), iname)
            );
        }
        if (!iname.nss().matches(EmailFarm.EMAIL_REGEX)) {
            throw new LoginRequiredException(
                this,
                String.format("Invalid name '%s' in '%s'", iname.nss(), iname)
            );
        }
        try {
            if (!SecureString.valueOf(secret).text().equals(iname.toString())) {
                throw new LoginRequiredException(
                    this,
                    String.format("Wrong secret '%s' for '%s'", secret, iname)
                );
            }
        } catch (com.netbout.spi.text.StringDecryptionException ex) {
            throw new LoginRequiredException(this, ex);
        }
        return this.resolve(iname);
    }

    /**
     * Resolve it.
     * @param iname Name of identity
     * @return The identity just authenticated
     */
    private Identity resolve(final Urn iname) {
        Identity identity;
        try {
            identity = new ResolvedIdentity(
                new URL("http://www.netbout.com/email"),
                iname
            );
            identity.profile().setPhoto(
                new URL("http://cdn.netbout.com/email.png")
            );
        } catch (java.net.MalformedURLException ex) {
            throw new IllegalArgumentException(ex);
        }
        identity.profile().alias(iname.nss());
        return identity;
    }

}
