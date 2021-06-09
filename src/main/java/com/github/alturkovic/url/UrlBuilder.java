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

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.function.Consumer;

import static com.github.alturkovic.url.ProtocolEnforcer.addProtocolIfMissing;
import static com.github.alturkovic.url.StringUtils.endsWith;
import static com.github.alturkovic.url.StringUtils.hasText;
import static com.github.alturkovic.url.UrlParseUtils.asUri;
import static com.github.alturkovic.url.UrlParseUtils.asUrl;

/**
 * Used to build urls as {@link URI}s.
 */
@Getter(AccessLevel.PACKAGE)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UrlBuilder {
    public static final String DEFAULT_PROTOCOL = "https";

    private String protocol;
    private UserInfo userInfo;
    private HostBuilder host;
    private Integer port;
    private PathBuilder path;
    private ParameterBuilder query;
    private String fragment;

    private boolean appendTrailingSlash;

    /**
     * Initialize a new builder from {@code uri}.
     * <p>
     * If {@code uri} does not have a defined protocol, 'https' will be used as default.
     *
     * @param uri to initialize from
     * @return builder instance
     */
    public static UrlBuilder of(URI uri) {
        UrlBuilder builder = new UrlBuilder()
            .protocol(uri.getScheme() == null ? DEFAULT_PROTOCOL : uri.getScheme())
            .userInfo(uri.getRawUserInfo())
            .port(uri.getPort())
            .fragment(StringUtils.removeSuffix(uri.getRawFragment(), "/"));

        builder.host = HostBuilder.of(uri.getHost());
        builder.path = PathBuilder.of(uri.getRawPath());
        builder.query = ParameterBuilder.of(uri.getRawQuery(), "&");

        if (hasText(uri.getRawFragment())) {
            if (endsWith(uri.getRawFragment(), "/")) {
                builder.appendTrailingSlash = true;
            }
        } else if (hasText(uri.getRawQuery())) {
            if (endsWith(uri.getRawQuery(), "/")) {
                builder.appendTrailingSlash = true;
            }
        } else if (hasText(uri.getRawPath())) {
            if (endsWith(uri.getRawPath(), "/")) {
                builder.appendTrailingSlash = true;
            }
        }

        return builder;
    }

    /**
     * Initialize a new builder from {@code urlString}.
     * <p>
     * If {@code urlString} does not have a defined protocol, 'https' will be used as default.
     *
     * @param urlString to initialize from
     * @return builder instance
     */
    public static UrlBuilder of(String urlString) {
        urlString = addProtocolIfMissing(urlString, DEFAULT_PROTOCOL);
        URL url = asUrl(urlString);
        URI uri = asUri(url);
        return of(uri);
    }

    /**
     * Set the protocol value.
     *
     * @param protocol to set
     * @return this builder
     * @throws IllegalArgumentException if {@code protocol} is blank, {@code null} or not {@code http(s)}
     */
    public UrlBuilder protocol(String protocol) {
        if (StringUtils.isBlank(protocol)) {
            throw new IllegalArgumentException("Protocol cannot be undefined");
        }

        if (!protocol.equals("http") && !protocol.equals("https")) {
            throw new IllegalArgumentException("Only http(s) protocols supported. Provided: " + protocol);
        }

        this.protocol = protocol;
        return this;
    }

    /**
     * Set the userInfo value.
     *
     * @param userInfo to set
     * @return this builder
     */
    public UrlBuilder userInfo(String userInfo) {
        this.userInfo = UserInfo.of(userInfo);
        return this;
    }

    /**
     * Set the userInfo values.
     *
     * @param user     to set
     * @param password to set
     * @return this builder
     */
    public UrlBuilder userInfo(String user, String password) {
        this.userInfo.setUser(user);
        this.userInfo.setPassword(password);
        return this;
    }

    /**
     * Set the user for the userInfo.
     *
     * @param user to set
     * @return this builder
     */
    public UrlBuilder user(String user) {
        this.userInfo.setUser(user);
        return this;
    }

    /**
     * Set the password for the userInfo.
     *
     * @param password to set
     * @return this builder
     */
    public UrlBuilder password(String password) {
        this.userInfo.setPassword(password);
        return this;
    }

    /**
     * Set the port value.
     *
     * @param port to set
     * @return this builder
     */
    public UrlBuilder port(Integer port) {
        this.port = port;
        return this;
    }

    /**
     * Modify the host.
     *
     * @param consumer to modify host
     * @return this builder
     */
    public UrlBuilder host(Consumer<HostBuilder> consumer) {
        consumer.accept(host);
        return this;
    }

    /**
     * Modify the path.
     *
     * @param consumer to modify path
     * @return this builder
     */
    public UrlBuilder path(Consumer<PathBuilder> consumer) {
        consumer.accept(path);
        return this;
    }

    /**
     * Modify the query.
     *
     * @param consumer to modify query
     * @return this builder
     */
    public UrlBuilder query(Consumer<ParameterBuilder> consumer) {
        consumer.accept(query);
        return this;
    }

    /**
     * Set the fragment value.
     *
     * @param fragment to set
     * @return this builder
     */
    public UrlBuilder fragment(String fragment) {
        this.fragment = fragment;
        return this;
    }

    /**
     * Set protocol value to 'http'.
     *
     * @return this builder
     */
    public UrlBuilder withHttp() {
        return protocol("http");
    }

    /**
     * Set protocol value to 'https'.
     *
     * @return this builder
     */
    public UrlBuilder withHttps() {
        return protocol("https");
    }

    /**
     * Remove the userInfo value.
     *
     * @return this builder
     */
    public UrlBuilder withoutUserInfo() {
        return userInfo(null);
    }

    /**
     * Remove the password from userInfo value.
     *
     * @return this builder
     */
    public UrlBuilder withoutPassword() {
        return password(null);
    }

    /**
     * Remove the port value.
     *
     * @return this builder
     */
    public UrlBuilder withoutPort() {
        return port(null);
    }

    /**
     * Remove the path value.
     *
     * @return this builder
     */
    public UrlBuilder withoutPath() {
        path.clear();
        return this;
    }

    /**
     * Remove the query value.
     *
     * @return this builder
     */
    public UrlBuilder withoutQuery() {
        query.clear();
        return this;
    }

    /**
     * Remove the fragment value.
     *
     * @return this builder
     */
    public UrlBuilder withoutFragment() {
        return fragment(null);
    }

    /**
     * Include the trailing slash.
     *
     * @return this builder
     */
    public UrlBuilder withTrailingSlash() {
        this.appendTrailingSlash = true;
        return this;
    }

    /**
     * Exclude the trailing slash.
     *
     * @return this builder
     */
    public UrlBuilder withoutTrailingSlash() {
        this.appendTrailingSlash = false;
        return this;
    }

    /**
     * Exclude everything except the host.
     *
     * @return this builder
     */
    public UrlBuilder strip() {
        withoutUserInfo();
        withoutPort();
        withoutPath();
        withoutQuery();
        withoutFragment();
        withoutTrailingSlash();
        return this;
    }

    /**
     * Create the {@link URI} from the registered values.
     *
     * @return {@link URI} instance
     */
    public URI build() {
        try {
            String builtHost = host.build();
            String builtPath = path.build();
            String builtQuery = query.build("&");
            String formattedUserInfo = this.userInfo.format();
            int definedPort = port == null ? -1 : port;
            String definedFragment = fragment;

            // Java URI has trailingSlash on the last defined component
            if (appendTrailingSlash) {
                if (hasText(definedFragment)) {
                    definedFragment += "/";
                } else if (hasText(builtQuery)) {
                    builtQuery += "/";
                } else if (builtPath == null) {
                    builtPath = "/";
                } else if (!endsWith(builtPath, "/")) {
                    builtPath += "/";
                }
            } else {
                if (StringUtils.isBlank(builtQuery)) {
                    builtPath = StringUtils.removeSuffix(builtPath, "/");
                }
            }

            return new URI(protocol, formattedUserInfo, builtHost, definedPort, builtPath, builtQuery, definedFragment);
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * Convert this builder to a {@link UrlParser}.
     * <p>
     * Changes to each won't affect the other.
     *
     * @return this as {@link UrlParser}
     */
    public UrlParser asParser() {
        return UrlParser.of(build());
    }
}
