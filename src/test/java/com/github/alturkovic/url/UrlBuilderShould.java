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

import static org.assertj.core.api.Assertions.*;

class UrlBuilderShould {

    @Test
    void initializeFromOnlyHttpBasedProtocol() {
        assertThat(UrlBuilder.of("http://localhost.com").build().toString()).isEqualTo("http://localhost.com");
        assertThat(UrlBuilder.of("https://localhost.com").build().toString()).isEqualTo("https://localhost.com");
        assertThatThrownBy(() -> UrlBuilder.of("redis://localhost:6379"));
    }

    @Test
    void initializeUsingHttpsAsDefaultProtocol() {
        assertThat(UrlBuilder.of("localhost.com").build().toString()).isEqualTo("https://localhost.com");
        assertThat(UrlBuilder.of("//localhost.com").build().toString()).isEqualTo("https://localhost.com");
    }

    @Test
    void overrideProtocol() {
        assertThat(UrlBuilder.of("http://localhost.com")
            .protocol("https")
            .build().toString()).isEqualTo("https://localhost.com");

        assertThat(UrlBuilder.of("http://localhost.com")
            .withHttps()
            .build().toString()).isEqualTo("https://localhost.com");

        assertThat(UrlBuilder.of("https://localhost.com")
            .withHttp()
            .build().toString()).isEqualTo("http://localhost.com");
    }

    @Test
    void overrideUserInfo() {
        assertThat(UrlBuilder.of("http://localhost.com")
            .userInfo("root:pass")
            .build().toString()).isEqualTo("http://root:pass@localhost.com");

        assertThat(UrlBuilder.of("http://localhost.com")
            .userInfo("root", "pass")
            .build().toString()).isEqualTo("http://root:pass@localhost.com");

        assertThat(UrlBuilder.of("http://localhost.com")
            .user("root")
            .build().toString()).isEqualTo("http://root@localhost.com");

        assertThat(UrlBuilder.of("http://root@localhost.com")
            .user("admin")
            .build().toString()).isEqualTo("http://admin@localhost.com");

        assertThat(UrlBuilder.of("http://root:pass@localhost.com")
            .userInfo("admin:123")
            .build().toString()).isEqualTo("http://admin:123@localhost.com");

        assertThat(UrlBuilder.of("http://root:pass@localhost.com")
            .user("admin")
            .build().toString()).isEqualTo("http://admin:pass@localhost.com");

        assertThat(UrlBuilder.of("http://root:pass@localhost.com")
            .password("123")
            .build().toString()).isEqualTo("http://root:123@localhost.com");

        assertThat(UrlBuilder.of("http://root:pass@localhost.com")
            .withoutUserInfo()
            .build().toString()).isEqualTo("http://localhost.com");

        assertThat(UrlBuilder.of("http://root:pass@localhost.com")
            .withoutPassword()
            .build().toString()).isEqualTo("http://root@localhost.com");
    }

    @Test
    void overridePort() {
        assertThat(UrlBuilder.of("http://localhost.com")
            .port(8080)
            .build().toString()).isEqualTo("http://localhost.com:8080");

        assertThat(UrlBuilder.of("http://localhost.com:9000")
            .port(8080)
            .build().toString()).isEqualTo("http://localhost.com:8080");

        assertThat(UrlBuilder.of("http://localhost.com:9000")
            .withoutPort()
            .build().toString()).isEqualTo("http://localhost.com");
    }

    @Test
    void overridePath() {
        assertThat(UrlBuilder.of("http://localhost.com")
            .path(path -> path.set("a"))
            .build().toString()).isEqualTo("http://localhost.com/a");

        assertThat(UrlBuilder.of("http://localhost.com/a")
            .path(path -> path.set("b"))
            .build().toString()).isEqualTo("http://localhost.com/b");

        assertThat(UrlBuilder.of("http://localhost.com/a")
            .withoutPath()
            .build().toString()).isEqualTo("http://localhost.com");
    }

    @Test
    void overrideQuery() {
        assertThat(UrlBuilder.of("http://localhost.com")
            .query(query -> query.set("a", "1"))
            .build().toString()).isEqualTo("http://localhost.com?a=1");

        assertThat(UrlBuilder.of("http://localhost.com?a=1")
            .query(query -> query.set("a", "2"))
            .build().toString()).isEqualTo("http://localhost.com?a=2");

        assertThat(UrlBuilder.of("http://localhost.com?a=1")
            .withoutQuery()
            .build().toString()).isEqualTo("http://localhost.com");
    }

    @Test
    void overrideFragment() {
        assertThat(UrlBuilder.of("http://localhost.com")
            .fragment("A")
            .build().toString()).isEqualTo("http://localhost.com#A");

        assertThat(UrlBuilder.of("http://localhost.com#A")
            .fragment("B")
            .build().toString()).isEqualTo("http://localhost.com#B");

        assertThat(UrlBuilder.of("http://localhost.com#A")
            .withoutFragment()
            .build().toString()).isEqualTo("http://localhost.com");
    }

    @Test
    void overrideTrailingSlash() {
        assertThat(UrlBuilder.of("http://localhost.com").build().toString())
            .isEqualTo("http://localhost.com");

        assertThat(UrlBuilder.of("http://localhost.com/").build().toString())
            .isEqualTo("http://localhost.com/");

        assertThat(UrlBuilder.of("http://localhost.com#A/")
            .build().toString()).isEqualTo("http://localhost.com#A/");

        assertThat(UrlBuilder.of("http://localhost.com?a=1/")
            .build().toString()).isEqualTo("http://localhost.com?a=1/");

        assertThat(UrlBuilder.of("http://localhost.com")
            .withTrailingSlash()
            .build().toString()).isEqualTo("http://localhost.com/");

        assertThat(UrlBuilder.of("http://localhost.com/")
            .withoutTrailingSlash()
            .build().toString()).isEqualTo("http://localhost.com");
    }

    @Test
    void strip() {
        assertThat(UrlBuilder.of("http://www.localhost.com/a?b=1#c")
            .strip()
            .build().toString()).isEqualTo("http://www.localhost.com");

        assertThat(UrlBuilder.of("http://www.example.com:8080/a?b=1#c/")
            .strip()
            .build().toString()).isEqualTo("http://www.example.com:8080");
    }

    @Test
    void buildComplex() {
        assertThat(UrlBuilder.of("localhost.com/a?b=1#C/")
            .withHttp()
            .host(HostBuilder::withWww)
            .query(ParameterBuilder::clear)
            .build().toString()).isEqualTo("http://www.localhost.com/a#C/");

        assertThat(UrlBuilder.of("localhost.com/a/b?q=1")
            .withHttp()
            .host(HostBuilder::withWww)
            .withTrailingSlash()
            .path(path -> path.set("c"))
            .query(ParameterBuilder::clear)
            .build().toString()).isEqualTo("http://www.localhost.com/c/");
    }
}