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

package com.github.alturkovic.url.builder;

import java.util.Optional;

class ProtocolEnforcer {
    static String addProtocolIfMissing(String url, String protocol) {
        if (url.startsWith("//")) {
            return protocol + ":" + url;
        }

        return extractProtocol(url)
            .map(p -> {
                if (!p.equals("http") && !p.equals("https")) {
                    throw new IllegalArgumentException("Only http(s) protocols supported but found: " + p);
                }
                return url;
            }).orElse(protocol + "://" + url);
    }

    private static Optional<String> extractProtocol(String url) {
        StringBuilder found = new StringBuilder();
        for (char c : url.toCharArray()) {
            found.append(c);

            if (c == '/' && found.toString().endsWith("://")) {
                found.setLength(found.length() - 3);
                return Optional.of(found.toString());
            }
        }

        return Optional.empty();
    }
}