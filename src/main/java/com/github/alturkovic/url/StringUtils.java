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

@NoArgsConstructor(access = AccessLevel.PRIVATE)
final class StringUtils {

    static boolean endsWith(String text, String suffix) {
        return text != null && text.endsWith(suffix);
    }

    static String removePrefix(String text, String prefix) {
        if (hasText(text) && text.startsWith(prefix)) {
            return text.substring(prefix.length());
        }

        return text;
    }

    static String removeSuffix(String text, String suffix) {
        if (hasText(text) && text.endsWith(suffix)) {
            return text.substring(0, text.lastIndexOf(suffix));
        }

        return text;
    }

    static String addPrefix(String text, String prefix) {
        if (isBlank(text)) {
            return prefix;
        }

        if (text.startsWith(prefix)) {
            return text;
        }

        return prefix + text;
    }

    static boolean hasText(String text) {
        return !isBlank(text);
    }

    static boolean isBlank(String text) {
        return text == null || text.isBlank();
    }
}
