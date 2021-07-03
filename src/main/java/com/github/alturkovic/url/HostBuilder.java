/*
 * MIT License
 *
 * Copyright (c) 2021 Alen Turkovic
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.github.alturkovic.url;

public class HostBuilder {
    private String host;

    /**
     * Initialize a new builder from {@code host}.
     *
     * @param host to initialize from
     * @return builder instance
     */
    public static HostBuilder of(String host) {
        HostBuilder builder = new HostBuilder();
        builder.host(host);
        return builder;
    }

    /**
     * Set the new host.
     *
     * @param host to set
     * @return this builder
     */
    public HostBuilder host(String host) {
        if (StringUtils.isBlank(host)) {
            throw new IllegalArgumentException("Host cannot be undefined");
        }

        this.host = host;
        return this;
    }

    /**
     * Add subdomain to the host.
     *
     * @param subdomain to add
     * @return this builder
     */
    public HostBuilder subdomain(String subdomain) {
        if (StringUtils.isBlank(subdomain)) {
            return this;
        }

        boolean includesWww = includesWww();
        if (includesWww) {
            withoutWww();
        }

        this.host = subdomain + "." + host;

        if (includesWww) {
            withWww();
        }

        return this;
    }

    /**
     * Include 'www.' in the hostname.
     *
     * @return this builder
     */
    public HostBuilder withWww() {
        return host(WwwPrefix.INCLUDE.normalizeHost(host));
    }

    /**
     * Exclude 'www.' from the hostname.
     *
     * @return this builder
     */
    public HostBuilder withoutWww() {
        return host(WwwPrefix.EXCLUDE.normalizeHost(host));
    }

    /**
     * Create the host string.
     *
     * @return formatted host
     */
    public String build() {
        return host;
    }

    private boolean includesWww() {
        return host.startsWith("www.");
    }
}
