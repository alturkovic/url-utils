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
import java.util.*;
import java.util.stream.Collectors;

import static com.github.alturkovic.url.DefaultPortMapper.getDefaultPort;
import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;

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
        return builder.getHost().build();
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
            return getDefaultPort(getProtocol());
        }

        return port;
    }

    /**
     * Get the path from the initialized url.
     *
     * @return the path
     */
    public Optional<String> getPath() {
        return Optional.ofNullable(builder.getPath().build());
    }

    /**
     * Get the path segments from the initialized url.
     *
     * @return the path segments
     */
    public Optional<List<String>> getPathSegments() {
        return Optional.of(extractPathSegments(builder.getPath()));
    }

    /**
     * Get the query from the initialized url.
     *
     * @return the query
     */
    public Optional<String> getQuery() {
        return Optional.ofNullable(builder.getQuery().build("&"));
    }

    /**
     * Get the fragment from the initialized url.
     *
     * @return the fragment
     */
    public Optional<String> getFragment() {
        return Optional.ofNullable(builder.getFragment());
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
     * Get parameter values for {@code name} parameter.
     *
     * @param name of the parameter
     * @return {@code name} parameter values
     */
    public List<String> getQueryParameters(String name) {
        return getQueryParameters().getOrDefault(name, emptyList());
    }

    /**
     * Get first parameter value for {@code name} parameter.
     *
     * @param name of the parameter
     * @return first {@code name} parameter value
     */
    public Optional<String> getQueryParameter(String name) {
        return getQueryParameters(name).stream().findFirst();
    }

    /**
     * Get the matrix parameters from the initialized url on the {@code path}.
     *
     * @param path to extract matrix parameters for
     * @return the matrix parameters on the {@code path}
     */
    public Map<String, List<String>> getMatrixParameters(String path) {
        PathBuilder pathBuilder = PathBuilder.of(path);
        return getMatrixParameters(extractPathSegments(pathBuilder));
    }

    /**
     * Get the matrix parameters from the initialized url on the {@code path}.
     *
     * @param path to extract matrix parameters for
     * @return the matrix parameters on the {@code path}
     */
    public Map<String, List<String>> getMatrixParameters(List<String> path) {
        List<PathBuilder.PathSegment> segments = builder.getPath().getMatchingSegments(path);
        if (segments.isEmpty()) {
            return emptyMap();
        }

        PathBuilder.PathSegment lastSegment = segments.get(segments.size() - 1);
        return asParameterMap(lastSegment.getParameters().getParameters());
    }

    /**
     * Get the file of the initialized url.
     *
     * @return file
     */
    public Optional<String> getFile() {
        return getPathSegments()
            .filter(segments -> !segments.isEmpty())
            .map(this::getLastSegment)
            .filter(this::containsDot);
    }

    /**
     * Get the file type of the initialized url.
     *
     * @return file type
     */
    public Optional<String> getFileType() {
        return getFile().map(this::extractContentAfterDot);
    }

    /**
     * Convert this parser to {@link UrlBuilder}.
     * <p>
     * Changes to each won't affect the other.
     *
     * @return this as {@link UrlBuilder}
     */
    public UrlBuilder asBuilder() {
        return UrlBuilder.of(builder.build());
    }

    private Map<String, List<String>> asParameterMap(List<UrlParameter> parameters) {
        Map<String, List<String>> result = new HashMap<>();
        for (UrlParameter parameter : parameters) {
            result.computeIfAbsent(parameter.getName(), s -> new ArrayList<>()).add(parameter.getValue());
        }
        return result;
    }

    private List<String> extractPathSegments(PathBuilder pathBuilder) {
        return pathBuilder.getPathSegments().stream()
            .map(PathBuilder.PathSegment::getPath)
            .collect(Collectors.toList());
    }

    private String getLastSegment(List<String> segments) {
        return segments.get(segments.size() - 1);
    }

    private boolean containsDot(String path) {
        return getDotIndex(path) != -1;
    }

    private int getDotIndex(String path) {
        return path.lastIndexOf(".");
    }

    private String extractContentAfterDot(String path) {
        return path.substring(getDotIndex(path) + 1);
    }
}
