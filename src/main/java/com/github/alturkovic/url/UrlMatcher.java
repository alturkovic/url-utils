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

import java.net.URI;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * Check if {@link URI} matches any of the registered {@link URI}s paths.
 */
public class UrlMatcher {
    private final Node root = new Node();

    /**
     * Register {@code url} for path matching.
     *
     * @param url to register
     */
    public void register(URI url) {
        Deque<String> elements = split(url);
        this.root.add(elements);
    }

    /**
     * Check if this {@link URI} is on a registered path.
     *
     * @param url to check
     * @return {@code true} if matches, {@code false otherwise}
     */
    public boolean matches(URI url) {
        Deque<String> elements = split(url);
        return root.contains(elements);
    }

    private static Deque<String> split(URI url) {
        UrlParser parser = UrlBuilder.of(url)
            .host(HostBuilder::withoutWww)
            .asParser();

        Deque<String> elements = new LinkedList<>();
        elements.add(parser.getHost());
        parser.getPathSegments().ifPresent(elements::addAll);
        return elements;
    }
    
    private static class Node {
        private final Map<String, Node> elements = new HashMap<>(0);
        private boolean matcher;

        public boolean contains(Deque<String> parts) {
            if (matcher) {
                return true;
            }

            String element = parts.pop();
            Node node = elements.get(element);
            if (node == null) {
                return false;
            }

            return node.contains(parts);
        }

        public void add(Deque<String> parts) {
            if (parts.isEmpty()) {
                matcher = true;
                return;
            }

            String element = parts.pop();

            Node mappedNode = this.elements.get(element);
            if (mappedNode == null) {
                Node child = new Node();
                this.elements.put(element, child);
                child.add(parts);
            } else {
                mappedNode.add(parts);
            }
        }
    }
}
