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

import static com.github.alturkovic.url.UrlParser.parse;
import static org.assertj.core.api.Assertions.assertThat;

class UrlMatcherShould {

    @Test
    void matchRegisteredWithoutPathIgnoringWww() {
        UrlMatcher matcher = new UrlMatcher();
        matcher.register(parse("example.com"));

        assertThat(matcher.matches(parse("example.com"))).isTrue();
        assertThat(matcher.matches(parse("www.example.com"))).isTrue();
    }

    @Test
    void matchRegisteredWithPath() {
        UrlMatcher matcher = new UrlMatcher();
        matcher.register(parse("example.com/a"));

        assertThat(matcher.matches(parse("example.com/a"))).isTrue();
        assertThat(matcher.matches(parse("example.com/b"))).isFalse();
    }

    @Test
    void matchRegisteredWithLongerPath() {
        UrlMatcher matcher = new UrlMatcher();
        matcher.register(parse("example.com/a"));

        assertThat(matcher.matches(parse("example.com/a/b"))).isTrue();
    }

    @Test
    void matchRegisteredWithShorterPath() {
        UrlMatcher matcher = new UrlMatcher();
        matcher.register(parse("example.com/a/b"));

        assertThat(matcher.matches(parse("example.com/a"))).isFalse();
    }

    @Test
    void notMatchUnregistered() {
        UrlMatcher matcher = new UrlMatcher();
        matcher.register(parse("example.com"));

        assertThat(matcher.matches(parse("another.com"))).isFalse();
    }

    @Test
    void matchRegisteredWithShortestFirst() {
        UrlMatcher matcher = new UrlMatcher();
        matcher.register(parse("example.com/a"));
        matcher.register(parse("example.com/a/b"));

        assertThat(matcher.matches(parse("example.com/a"))).isTrue();
        assertThat(matcher.matches(parse("example.com/a/b"))).isTrue();

        assertThat(matcher.matches(parse("example.com/b"))).isFalse();
    }

    @Test
    void matchRegisteredWithShortestLast() {
        UrlMatcher matcher = new UrlMatcher();
        matcher.register(parse("example.com/a/b"));
        matcher.register(parse("example.com/a"));

        assertThat(matcher.matches(parse("example.com/a"))).isTrue();
        assertThat(matcher.matches(parse("example.com/a/b"))).isTrue();

        assertThat(matcher.matches(parse("example.com/b"))).isFalse();
    }
}