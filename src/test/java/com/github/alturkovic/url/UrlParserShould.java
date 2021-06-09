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

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class UrlParserShould {

    @Test
    void parse() {
        assertThat(UrlParser.parse("example.com"))
            .hasToString("https://example.com");
    }

    @Test
    void getProtocol() {
        assertThat(UrlParser.of("http://localhost:8080/").getProtocol())
            .isEqualTo("http");
    }

    @Test
    void getDefaultProtocol() {
        assertThat(UrlParser.of("localhost:8080/").getProtocol())
            .isEqualTo("https");
    }

    @Test
    void getHost() {
        assertThat(UrlParser.of("localhost:8080/").getHost())
            .isEqualTo("localhost");
    }

    @Test
    void getPort() {
        assertThat(UrlParser.of("localhost:8080/").getPort())
            .isEqualTo(8080);
    }

    @Test
    void getDefaultHttpPort() {
        assertThat(UrlParser.of("http://localhost/").getPort())
            .isEqualTo(80);
    }

    @Test
    void getDefaultHttpsPort() {
        assertThat(UrlParser.of("https://localhost/").getPort())
            .isEqualTo(443);
    }

    @Test
    void getPath() {
        assertThat(UrlParser.of("localhost:8080/api/test").getPath())
            .isEqualTo("/api/test");
    }

    @Test
    void getQuery() {
        assertThat(UrlParser.of("localhost:8080?a=1&b=2/").getQuery())
            .isEqualTo("a=1&b=2");
    }

    @Test
    void getFragment() {
        assertThat(UrlParser.of("localhost:8080#DOWNLOAD/").getFragment())
            .isEqualTo("DOWNLOAD");
    }

    @Test
    void getQueryParameters() {
        Map<String, List<String>> parameters = UrlParser.of("localhost:8080?a=1&b=2&b&c=3").getQueryParameters();
        assertThat(parameters.get("a")).containsExactly("1");
        assertThat(parameters.get("b")).containsExactly("2", null);
        assertThat(parameters.get("c")).containsExactly("3");
    }

    @Test
    void getMatrixParameters() {
        UrlParser parser = UrlParser.of("localhost:8080/first;a=1;b=2;b=3/second;c=1;d;d=2");

        Map<String, List<String>> firstParameters = parser.getMatrixParameters("first");
        assertThat(firstParameters).containsExactlyInAnyOrderEntriesOf(Map.of(
            "a", List.of("1"),
            "b", List.of("2", "3")
        ));

        Map<String, List<String>> secondParameters = parser.getMatrixParameters(List.of("first", "second"));
        assertThat(secondParameters).containsExactlyInAnyOrderEntriesOf(Map.of(
            "c", List.of("1"),
            "d", Arrays.asList(null, "2")
        ));
    }
}