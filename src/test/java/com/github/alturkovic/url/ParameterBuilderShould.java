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

import static java.util.Arrays.asList;
import static java.util.Collections.singletonMap;
import static org.assertj.core.api.Assertions.assertThat;

class ParameterBuilderShould {

    @Test
    void dropInvalidParameters() {
        assertThat(ParameterBuilder.of("a=b=c", "&").build("&"))
            .isNull();

        assertThat(ParameterBuilder.of("a=b?c=d", "&").build("&"))
            .isNull();

        assertThat(ParameterBuilder.of("a=b&c=d=e", "&").build("&"))
            .isEqualTo("a=b");
    }

    @Test
    void addParameter() {
        ParameterBuilder parameter = ParameterBuilder.of("a=1", "&");
        parameter.add("a");
        parameter.add("b", "2");
        parameter.add("c", "3");
        parameter.add(singletonMap("c", "4"));
        assertThat(parameter.build("&")).isEqualTo("a=1&a&b=2&c=3&c=4");
    }

    @Test
    void addParameters() {
        ParameterBuilder parameter = ParameterBuilder.of("a=1", "&");
        parameter.addAll("a", asList("2", "3"));
        parameter.addAll(singletonMap("b", asList("4", "5")));
        assertThat(parameter.build("&")).isEqualTo("a=1&a=2&a=3&b=4&b=5");
    }

    @Test
    void setParameter() {
        ParameterBuilder parameter = ParameterBuilder.of("a=1&a&b=2&c=3&c=4", "&");
        parameter.set("a");
        parameter.set("b", "1");
        parameter.set(singletonMap("c", "2"));
        assertThat(parameter.build("&")).isEqualTo("a&b=1&c=2");
    }

    @Test
    void clearParameters() {
        assertThat(ParameterBuilder.of("a=1", "&").clear().build("&")).isNull();
    }

    @Test
    void removeParameter() {
        ParameterBuilder parameter = ParameterBuilder.of("a=1&a&b=2&c=3&c=4&d=5", "&");
        parameter.remove("a");
        parameter.remove(asList("b", "c"));
        assertThat(parameter.build("&")).isEqualTo("d=5");
    }

    @Test
    void removeParametersByNameCondition() {
        ParameterBuilder parameter = ParameterBuilder.of("a=1&a&b=2&c=3&c=4&d=5", "&");
        parameter.removeBy((name, value) -> "a".equals(name));
        assertThat(parameter.build("&")).isEqualTo("b=2&c=3&c=4&d=5");
    }

    @Test
    void removeParametersByValueCondition() {
        ParameterBuilder parameter = ParameterBuilder.of("a=1&a&b=2&c=3&c=2&d=2", "&");
        parameter.removeBy((name, value) -> "2".equals(value));
        assertThat(parameter.build("&")).isEqualTo("a=1&a&c=3");
    }
}