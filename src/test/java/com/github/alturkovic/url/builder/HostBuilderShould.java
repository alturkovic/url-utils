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

import com.github.alturkovic.url.builder.HostBuilder;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class HostBuilderShould {

    @Test
    void overrideHost() {
        assertThat(HostBuilder.of("localhost.com")
            .host("example.net")
            .build()).isEqualTo("example.net");
    }

    @Test
    void notAllowBlankHost() {
        assertThatThrownBy(() -> HostBuilder.of("localhost.com").host(null));
        assertThatThrownBy(() -> HostBuilder.of("localhost.com").host(""));
        assertThatThrownBy(() -> HostBuilder.of("localhost.com").host(" "));
    }

    @Test
    void overrideWww() {
        assertThat(HostBuilder.of("localhost.com").build())
            .isEqualTo("localhost.com");

        assertThat(HostBuilder.of("www.localhost.com").build())
            .isEqualTo("www.localhost.com");

        assertThat(HostBuilder.of("localhost.com")
            .withWww()
            .build()).isEqualTo("www.localhost.com");

        assertThat(HostBuilder.of("www.localhost.com")
            .withoutWww()
            .build()).isEqualTo("localhost.com");
    }

    @Test
    void addSubdomain() {
        assertThat(HostBuilder.of("localhost.com")
            .subdomain("b")
            .withWww()
            .subdomain("a")
            .build()).isEqualTo("www.a.b.localhost.com");
    }
}