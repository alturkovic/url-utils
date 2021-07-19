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
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;

import java.net.*;
import java.nio.charset.StandardCharsets;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
final class UrlParseUtils {

    static URI parse(String urlString) {
        URL url = asUrl(urlString);
        return asUri(url);
    }

    static URL asUrl(String urlString) {
        try {
            return new URL(urlString);
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("Cannot parse url: " + urlString, e);
        }
    }

    static URI asUri(URL url) {
        try {
            return new URI(
                url.getProtocol(),
                url.getAuthority(),
                url.getPath(),
                url.getQuery(),
                url.getRef()
            );
        } catch (URISyntaxException e) {
            try {
                return url.toURI();
            } catch (URISyntaxException ignored) {
                // fallback used for root based matrix-variables bug with Java URI
            }
            throw new IllegalArgumentException("Cannot parse uri: " + url, e);
        }
    }

    @SneakyThrows
    static String decode(String url) {
        return URLDecoder.decode(url, StandardCharsets.UTF_8.name());
    }
}
