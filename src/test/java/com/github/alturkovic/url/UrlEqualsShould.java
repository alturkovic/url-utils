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

import org.junit.jupiter.api.Test;

import java.net.URI;

import static com.github.alturkovic.url.UrlEquals.areUrlsEqual;
import static org.assertj.core.api.Assertions.assertThat;

class UrlEqualsShould {

    @Test
    void equalWithAllComponents() {
        URI source = URI.create("https://user:pass@example.com:443/path?q=1#FRAGMENT");
        URI target = URI.create("https://user:pass@example.com:443/path?q=1#FRAGMENT");
        assertThat(areUrlsEqual(source, target)).isTrue();
    }

    @Test
    void equalWithDefaultPort() {
        URI source = URI.create("https://example.com");
        URI target = URI.create("https://example.com:443");
        assertThat(areUrlsEqual(source, target)).isTrue();
    }

    @Test
    void equalWithSpecificPort() {
        URI source = URI.create("https://example.com:8080");
        URI target = URI.create("https://example.com:8080");
        assertThat(areUrlsEqual(source, target)).isTrue();
    }

    @Test
    void equalWithTrailingSlash() {
        URI source = URI.create("https://example.com");
        URI target = URI.create("https://example.com/");
        assertThat(areUrlsEqual(source, target)).isTrue();
    }

    @Test
    void equalWithoutPassword() {
        URI source = URI.create("https://user@example.com");
        URI target = URI.create("https://user:@example.com");
        assertThat(areUrlsEqual(source, target)).isTrue();
    }

    @Test
    void equalWithRearrangedQueryParameters() {
        URI source = URI.create("https://example.com?a=1&b=2");
        URI target = URI.create("https://example.com?b=2&a=1");
        assertThat(areUrlsEqual(source, target)).isTrue();
    }

    @Test
    void notEqualDifferentProtocols() {
        URI source = URI.create("http://example.com");
        URI target = URI.create("https://example.com");
        assertThat(areUrlsEqual(source, target)).isFalse();
    }

    @Test
    void notEqualDifferentUsers() {
        URI source = URI.create("https://user1@example.com");
        URI target = URI.create("https://user2@example.com");
        assertThat(areUrlsEqual(source, target)).isFalse();
    }

    @Test
    void notEqualDifferentPasswords() {
        URI source = URI.create("https://user:pass1@example.com");
        URI target = URI.create("https://user:pass2@example.com");
        assertThat(areUrlsEqual(source, target)).isFalse();
    }

    @Test
    void notEqualDifferentHosts() {
        URI source = URI.create("https://example.com");
        URI target = URI.create("https://another.com");
        assertThat(areUrlsEqual(source, target)).isFalse();
    }

    @Test
    void notEqualDifferentPorts() {
        URI source = URI.create("https://example.com");
        URI target = URI.create("https://example.com:8080");
        assertThat(areUrlsEqual(source, target)).isFalse();
    }

    @Test
    void notEqualDifferentPaths() {
        URI source = URI.create("https://example.com/a");
        URI target = URI.create("https://example.com/b");
        assertThat(areUrlsEqual(source, target)).isFalse();
    }

    @Test
    void notEqualDifferentQueries() {
        URI source = URI.create("https://example.com?q=1");
        URI target = URI.create("https://example.com?q=2");
        assertThat(areUrlsEqual(source, target)).isFalse();
    }

    @Test
    void notEqualDifferentFragments() {
        URI source = URI.create("https://example.com#FRAGMENT");
        URI target = URI.create("https://example.com#DIFFERENT");
        assertThat(areUrlsEqual(source, target)).isFalse();
    }
}