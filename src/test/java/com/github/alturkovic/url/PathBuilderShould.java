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
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PathBuilderShould {

    @Test
    void addToPath() {
        PathBuilder path = PathBuilder.of("a");
        path.addSegment("b").addSegment("").add(asList("c", "d")).add("e/f");
        assertThat(path.build()).isEqualTo("/a/b//c/d/e/f");
    }

    @Test
    void addEmptyPathSegment() {
        PathBuilder path = PathBuilder.of("a/");
        path.addSegment("");
        assertThat(path.build()).isEqualTo("/a//");
    }

    @Test
    void setPath() {
        assertThat(PathBuilder.of("/a/b").set("c").build()).isEqualTo("/c");
        assertThat(PathBuilder.of("/a/b").set(asList("c", "d")).build()).isEqualTo("/c/d");
    }

    @Test
    void clearPath() {
        assertThat(PathBuilder.of("/a").clear().build()).isNull();
    }

    @Test
    void removePartOfPath() {
        assertThat(PathBuilder.of("/a/b/c").removeFirst().build()).isEqualTo("/b/c");
        assertThat(PathBuilder.of("/a/b/c").removeLast().build()).isEqualTo("/a/b");
        assertThat(PathBuilder.of("/a/b/c").remove(1).build()).isEqualTo("/a/c");
        assertThat(PathBuilder.of("/a/b/c").remove(asList("a", "b")).build()).isEqualTo("/c");
        assertThat(PathBuilder.of("/a/b/c").remove("a/b").build()).isEqualTo("/c");
    }

    @Test
    void notRemovePartOfPathIfItDoesNotMatch() {
        assertThatThrownBy(() -> PathBuilder.of("/a/b/c").remove("b/c").build());
        assertThatThrownBy(() -> PathBuilder.of("/a/b/c").remove(asList("b", "c")).build());
    }

    @Test
    void removeByCondition() {
        PathBuilder path = PathBuilder.of("/a/b/c/a/c");
        path.removeBy("a"::equals);
        assertThat(path.build()).isEqualTo("/b/c/c");
    }

    @Test
    void modifyMatrixParameters() {
        assertThat(PathBuilder.of("").parameters(params -> params
            .set("a", "1")
            .set("b", "2")
        ).build()).isEqualTo(";a=1;b=2");

        assertThat(PathBuilder.of("/a").parameters(params -> params
            .set("b", "1")
            .set("c", "2")
        ).build()).isEqualTo("/a;b=1;c=2");
    }
}