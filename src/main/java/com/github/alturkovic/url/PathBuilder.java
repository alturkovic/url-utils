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
import lombok.Data;
import lombok.Getter;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.github.alturkovic.url.UrlParseUtils.decode;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;

/**
 * Used to build the URL path.
 */
@Getter(AccessLevel.PACKAGE)
public class PathBuilder {
    private final List<PathSegment> pathSegments = new ArrayList<>();

    /**
     * Initialize a new builder from {@code path}.
     *
     * @param path to initialize from
     * @return builder instance
     */
    public static PathBuilder of(String path) {
        if (StringUtils.isBlank(path)) {
            return new PathBuilder();
        }

        return initialize(path);
    }

    /**
     * Add a segment to the path.
     *
     * @param pathSegment to add
     * @return this builder
     */
    public PathBuilder addSegment(String pathSegment) {
        return add(singletonList(pathSegment));
    }

    /**
     * Add the segments from {@code path}.
     * <p>
     * The provided {@code path} will be split using '/'.
     *
     * @param path to remove
     * @return this builder
     */
    public PathBuilder add(String path) {
        PathBuilder builder = PathBuilder.of(path);
        this.pathSegments.addAll(builder.getPathSegments());
        return this;
    }

    /**
     * Add segments to the path.
     *
     * @param pathSegments to add
     * @return this builder
     */
    public PathBuilder add(List<String> pathSegments) {
        pathSegments.stream()
            .map(PathSegment::of)
            .forEach(this.pathSegments::add);
        return this;
    }

    /**
     * Set path to the new segment.
     *
     * @param path to set to
     * @return this builder
     */
    public PathBuilder set(String path) {
        clear();
        return add(singletonList(path));
    }

    /**
     * Set path to the new segments.
     *
     * @param pathSegments to set to
     * @return this builder
     */
    public PathBuilder set(List<String> pathSegments) {
        clear();
        return add(pathSegments);
    }

    /**
     * Clear the path segments.
     *
     * @return this builder
     */
    public PathBuilder clear() {
        this.pathSegments.clear();
        return this;
    }

    /**
     * Remove the segments from the path.
     *
     * @param pathSegments to remove
     * @return this builder
     * @throws IllegalArgumentException if {@code pathSegments} do not match the current path
     */
    public PathBuilder remove(List<String> pathSegments) {
        getMatchingSegments(pathSegments).clear();
        return this;
    }

    /**
     * Remove the segments from the path.
     * <p>
     * The provided {@code path} will be split using '/'.
     *
     * @param path to remove
     * @return this builder
     * @throws IllegalArgumentException if {@code pathSegments} do not match the current path
     */
    public PathBuilder remove(String path) {
        return remove(PathBuilder.of(path).getPathSegments().stream()
            .map(PathSegment::getPath)
            .collect(Collectors.toList()));
    }

    /**
     * Remove the indexed segment from the path.
     *
     * @param index of the path segment to remove
     * @return this builder
     */
    public PathBuilder remove(int index) {
        this.pathSegments.remove(index);
        return this;
    }

    /**
     * Remove path segments matching {@code condition}.
     *
     * @param condition to evaluate which segments to remove
     * @return this builder
     */
    public PathBuilder removeBy(Predicate<String> condition) {
        this.pathSegments.removeIf(pathSegment -> condition.test(pathSegment.getPath()));
        return this;
    }

    /**
     * Remove the first segment from the path.
     *
     * @return this builder
     */
    public PathBuilder removeFirst() {
        return remove(0);
    }

    /**
     * Remove the last segment from the path.
     *
     * @return this builder
     */
    public PathBuilder removeLast() {
        return remove(this.pathSegments.size() - 1);
    }

    /**
     * Modify the matrix parameters from the last path segment.
     *
     * @param consumer to modify parameters
     * @return this builder
     */
    public PathBuilder parameters(Consumer<ParameterBuilder> consumer) {
        if (this.pathSegments.isEmpty()) {
            appendEmptyPath();
        }

        consumer.accept(getLastParameterBuilder());
        return this;
    }

    /**
     * Create the path string from the registered segments and parameters.
     *
     * @return formatted path
     */
    public String build() {
        if (this.pathSegments.isEmpty()) {
            return null;
        }

        String path = this.pathSegments.stream()
            .map(PathSegment::format)
            .collect(Collectors.joining("/"));

        if (path.startsWith(";")) {
            return path;
        }

        return "/" + path;
    }

    List<PathSegment> getMatchingSegments(List<String> pathSegments) {
        if (this.pathSegments.isEmpty()) {
            return emptyList();
        }

        for (int i = 0; i < pathSegments.size(); i++) {
            assertPathElementsMatch(pathSegments.get(i), i);
        }
        return this.pathSegments.subList(0, pathSegments.size());
    }

    private void appendEmptyPath() {
        this.pathSegments.add(PathSegment.empty());
    }

    private PathSegment getLastPathSegment() {
        int lastIndex = this.pathSegments.size() - 1;
        return this.pathSegments.get(lastIndex);
    }

    private ParameterBuilder getLastParameterBuilder() {
        PathSegment pathSegment = getLastPathSegment();
        return pathSegment.getParameters();
    }

    private void assertPathElementsMatch(String indexedPathPart, int index) {
        String pathSegmentPart = pathSegments.get(index).getPath();
        if (!indexedPathPart.equals(pathSegmentPart)) {
            throw new IllegalArgumentException(String.format(
                "Path: '%s' at index %d does not match registered path: %s",
                indexedPathPart, index, pathSegments
            ));
        }
    }

    private static PathBuilder initialize(String path) {
        path = StringUtils.removePrefix(path, "/");

        PathBuilder builder = new PathBuilder();
        String[] paths = path.split("/", -1);
        for (String p : paths) {
            builder.addSegment(decode(p, StandardCharsets.UTF_8));
        }
        return builder;
    }

    @Data
    @AllArgsConstructor
    static class PathSegment {
        private String path;
        private ParameterBuilder parameters;

        static PathSegment empty() {
            return new PathSegment(null, new ParameterBuilder());
        }

        static PathSegment of(String path) {
            if (path.contains(";")) {
                String[] parts = path.split(";", 2);
                return new PathSegment(parts[0], ParameterBuilder.of(parts[1], ";"));
            }

            return new PathSegment(path, new ParameterBuilder());
        }

        String format() {
            StringBuilder result = new StringBuilder();
            if (StringUtils.hasText(this.path)) {
                result.append(this.path);
            }

            String builtParameters = this.parameters.build(";");
            if (StringUtils.hasText(builtParameters)) {
                result.append(";").append(builtParameters);
            }

            return result.toString();
        }
    }
}
