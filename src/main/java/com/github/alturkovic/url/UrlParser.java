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
import lombok.AllArgsConstructor;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Used to extract values from urls.
 */
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class UrlParser {
    private final UrlBuilder builder;

    /**
     * Initialize a new builder from a {@link String}.
     * <p>
     * If the protocol is missing from it, 'https' will be used by default.
     *
     * @param url to initialize from
     * @return builder instance
     */
    public static UrlParser of(String url) {
        return new UrlParser(UrlBuilder.of(url));
    }

    /**
     * Initialize a new builder from a {@link URI}.
     * <p>
     * If the protocol is missing from it, 'https' will be used by default.
     *
     * @param url to initialize from
     * @return builder instance
     */
    public static UrlParser of(URI url) {
        return new UrlParser(UrlBuilder.of(url));
    }

    /**
     * Parse {@link URI} from a string.
     * <p>
     * If the protocol is missing from it, 'https' will be used by default.
     * <p>
     * This method parses un-encoded URI's that {@link URI#create(String)} cannot.
     *
     * @param url to initialize from
     * @return builder instance
     */
    public static URI parse(String url) {
        return UrlBuilder.of(url).build();
    }

    /**
     * Get the protocol from the initialized url.
     *
     * @return the protocol
     */
    public String getProtocol() {
        return builder.getProtocol();
    }

    /**
     * Get the host from the initialized url.
     *
     * @return the host
     */
    public String getHost() {
        return builder.getHost();
    }

    /**
     * Get the port from the initialized url.
     * <p>
     * If it was not provided, the default port for the protocol will be returned.
     *
     * @return the port
     */
    public int getPort() {
        int port = builder.getPort();

        if (port == -1) {
            String protocol = getProtocol();
            if (protocol.equals("http")) {
                return 80;
            } else if (protocol.equals("https")) {
                return 443;
            }
        }

        return port;
    }

    /**
     * Get the path from the initialized url.
     *
     * @return the path
     */
    public String getPath() {
        return builder.getPath().build();
    }

    /**
     * Get the query from the initialized url.
     *
     * @return the query
     */
    public String getQuery() {
        return builder.getQuery().build("&");
    }

    /**
     * Get the fragment from the initialized url.
     *
     * @return the fragment
     */
    public String getFragment() {
        return builder.getFragment();
    }

    /**
     * Get the query parameters from the initialized url.
     *
     * @return the query parameters
     */
    public Map<String, List<String>> getQueryParameters() {
        return asParameterMap(builder.getQuery().getParameters());
    }

    /**
     * Get the matrix parameters from the initialized url on the {@code path}.
     *
     * @param path to extract matrix parameters for
     * @return the matrix parameters on the {@code path}
     */
    public Map<String, List<String>> getMatrixParameters(String path) {
        PathBuilder pathBuilder = PathBuilder.of(path);
        return getMatrixParameters(pathBuilder.getPathSegments().stream()
            .map(PathBuilder.PathSegment::getPath)
            .collect(Collectors.toList()));
    }

    /**
     * Get the matrix parameters from the initialized url on the {@code path}.
     *
     * @param path to extract matrix parameters for
     * @return the matrix parameters on the {@code path}
     */
    public Map<String, List<String>> getMatrixParameters(List<String> path) {
        List<PathBuilder.PathSegment> segments = builder.getPath().getMatchingSegments(path);
        PathBuilder.PathSegment lastSegment = segments.get(segments.size() - 1);
        return asParameterMap(lastSegment.getParameters().getParameters());
    }

    private Map<String, List<String>> asParameterMap(List<UrlParameter> parameters) {
        Map<String, List<String>> result = new HashMap<>();
        for (UrlParameter parameter : parameters) {
            result.computeIfAbsent(parameter.getName(), s -> new ArrayList<>()).add(parameter.getValue());
        }
        return result;
    }
}
