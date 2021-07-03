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
import java.net.URL;

/**
 * {@link URL#equals(Object)} is <a href="https://stackoverflow.com/questions/3771081/proper-way-to-check-for-url-equality/">broken</a>.
 */
public class UrlEquals {

    /**
     * Check if {@code source} URL is equal to {@code target} URL.
     *
     * @param source to compare
     * @param target to compare with
     * @return {@code true} if they are equal, {@code false} otherwise
     */
    public static boolean areUrlsEqual(URI source, URI target) {
        UrlParser sourceParser = parse(source);
        UrlParser targetParser = parse(target);

        if (!sourceParser.getProtocol().equals(targetParser.getProtocol())) {
            return false;
        }

        if (!sourceParser.asBuilder().getUserInfo().equals(targetParser.asBuilder().getUserInfo())) {
            return false;
        }

        if (!sourceParser.getHost().equals(targetParser.getHost())) {
            return false;
        }

        if (sourceParser.getPort() != targetParser.getPort()) {
            return false;
        }

        if (!sourceParser.getPath().equals(targetParser.getPath())) {
            return false;
        }

        if (!sourceParser.getQueryParameters().equals(targetParser.getQueryParameters())) {
            return false;
        }

        if (!sourceParser.getFragment().equals(targetParser.getFragment())) {
            return false;
        }

        return true;
    }

    private static UrlParser parse(URI source) {
        return UrlBuilder.of(source)
            .withoutTrailingSlash()
            .asParser();
    }
}
