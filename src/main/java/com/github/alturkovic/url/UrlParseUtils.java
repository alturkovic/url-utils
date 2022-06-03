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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import static java.nio.charset.StandardCharsets.UTF_8;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
final class UrlParseUtils {

    static URI parse(String url) {
        try {
            int colonIndex = url.indexOf(':');
            int fragmentIndex = url.indexOf('#', colonIndex + 1);
            String scheme = url.substring(0, colonIndex);
            String ssp = url.substring(colonIndex + 1, (fragmentIndex > 0 ? fragmentIndex : url.length()));
            String fragment = (fragmentIndex > 0 ? url.substring(fragmentIndex + 1) : null);
            return new URI(scheme, percentDecode(ssp), percentDecode(fragment));
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("Cannot parse url: " + url, e);
        }
    }

    // Taken from: https://github.com/smola/galimatias
    public static String percentDecode(final String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }

        try {
            final ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            int idx = 0;
            while (idx < input.length()) {
                boolean isEOF = false;
                int c = input.codePointAt(idx);

                while (!isEOF && c != '%') {
                    if (c <= 0x7F) {
                        bytes.write((byte) c);
                        idx++;
                    } else {
                        bytes.write(new String(Character.toChars(c)).getBytes(UTF_8));
                        idx += Character.charCount(c);
                    }
                    isEOF = idx >= input.length();
                    c = (isEOF)? 0x00 : input.codePointAt(idx);
                }

                if (c == '%' && (input.length() <= idx + 2 ||
                    !isASCIIHexDigit(input.charAt(idx + 1)) ||
                    !isASCIIHexDigit(input.charAt(idx + 2)))) {
                    bytes.write((byte) c);
                    idx++;
                } else {
                    while (c == '%' && input.length() > idx + 2 &&
                        isASCIIHexDigit(input.charAt(idx + 1)) &&
                        isASCIIHexDigit(input.charAt(idx + 2))) {
                        bytes.write(hexToInt(input.charAt(idx + 1), input.charAt(idx + 2)));
                        idx += 3;
                        c = (input.length() <= idx)? 0x00 : input.codePointAt(idx);
                    }
                }
            }
            return new String(bytes.toByteArray(), UTF_8);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    private static boolean isASCIIHexDigit(final int c) {
        return (c >= 0x0041 && c <= 0x0046) || (c >= 0x0061 && c <= 0x0066) || isASCIIDigit(c);
    }

    private static boolean isASCIIDigit(final int c) {
        return c >= 0x0030 && c <= 0x0039;
    }

    private static int hexToInt(final char c1, final char c2) {
        return Integer.parseInt(new String(new char[]{c1, c2}), 16);
    }
}
