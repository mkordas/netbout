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
package com.netbout.rest;

import com.jcabi.aspects.Tv;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

/**
 * Favicon rendering.
 *
 * @author Yegor Bugayenko (yegor@tpc2.com)
 * @version $Id$
 * @since 2.7
 */
@Path("/favicon.ico")
public final class FaviconRs extends BaseRs {

    /**
     * Get icon in GIF format.
     * @param unread Number of unread messages
     * @return The image binary
     * @throws IOException If fails
     */
    @GET
    @Produces("image/gif")
    public byte[] gif(@PathParam("unread") final Long unread)
        throws IOException {
        final int width = 64;
        final int height = 64;
        final BufferedImage image = new BufferedImage(
            width, height, BufferedImage.TYPE_INT_RGB
        );
        final Graphics graph = image.getGraphics();
        graph.setColor(new Color(0x4b, 0x42, 0x50));
        graph.fillRect(0, 0, width, height);
        if (unread > 0L) {
            final String text;
            if (unread >= (long) Tv.HUNDRED) {
                text = "99+";
            } else {
                text = Long.toString(unread);
            }
            graph.setColor(Color.WHITE);
            graph.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, height / 2));
            graph.drawString(
                text,
                width - width / Tv.TEN
                    - graph.getFontMetrics().stringWidth(text),
                height - height / Tv.TEN
            );
        }
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "gif", baos);
        return baos.toByteArray();
    }

}
