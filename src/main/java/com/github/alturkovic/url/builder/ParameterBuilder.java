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

import lombok.AccessLevel;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Used to build parameters. The delimiter determines what type of query it builds.
 * <p>
 * It can be used to either build the query parameters or path matrix parameters
 */
@Getter(AccessLevel.PACKAGE)
public class ParameterBuilder {
    private final List<UrlParameter> parameters = new ArrayList<>();

    /**
     * Initialize a new builder from {@code query} using {@code delimiter} to split parameter pairs.
     *
     * @param query     to initialize from
     * @param delimiter to separate parameter pairs
     * @return builder instance
     */
    public static ParameterBuilder of(String query, String delimiter) {
        if (StringUtils.isBlank(query)) {
            return new ParameterBuilder();
        }

        return initialize(query, delimiter);
    }

    /**
     * Add a parameter without a value.
     *
     * @param name of the parameter
     * @return this builder
     */
    public ParameterBuilder add(String name) {
        return add(UrlParameter.named(name));
    }

    /**
     * Add a parameter with a value.
     *
     * @param name  of the parameter
     * @param value of the parameter
     * @return this builder
     */
    public ParameterBuilder add(String name, String value) {
        return add(UrlParameter.of(name, value));
    }

    /**
     * Add all parameters from {@code parameters} with an optional value.
     *
     * @param parameters to add
     * @return this builder
     */
    public ParameterBuilder add(Map<String, String> parameters) {
        parameters.forEach(this::add);
        return this;
    }

    /**
     * Set a parameter without a value.
     *
     * @param name of the parameter
     * @return this builder
     */
    public ParameterBuilder set(String name) {
        remove(name);
        return add(name);
    }

    /**
     * Set a parameter without a value.
     *
     * @param name  of the parameter
     * @param value of the parameter
     * @return this builder
     */
    public ParameterBuilder set(String name, String value) {
        remove(name);
        return add(name, value);
    }

    /**
     * Set all parameters from {@code parameters} with an optional value.
     *
     * @param parameters to add
     * @return this builder
     */
    public ParameterBuilder set(Map<String, String> parameters) {
        parameters.keySet().forEach(this::remove);
        return add(parameters);
    }

    /**
     * Remove a parameter.
     *
     * @param name of the parameter
     * @return this builder
     */
    public ParameterBuilder remove(String name) {
        return remove(List.of(name));
    }

    /**
     * Remove {@code parameters}.
     *
     * @param parameters of parameters to remove
     * @return this builder
     */
    public ParameterBuilder remove(List<String> parameters) {
        this.parameters.removeIf(parameter -> parameters.contains(parameter.getName()));
        return this;
    }

    /**
     * Remove all parameters.
     *
     * @return this builder
     */
    public ParameterBuilder clear() {
        parameters.clear();
        return this;
    }

    /**
     * Create the parameter string from the registered parameter pairs.
     *
     * @param delimiter to separate parameter pairs
     * @return formatted parameters
     */
    public String build(String delimiter) {
        if (parameters.isEmpty()) {
            return null;
        }

        return String.join(delimiter, constructParameterPairs());
    }

    private ParameterBuilder add(UrlParameter parameter) {
        this.parameters.add(parameter);
        return this;
    }

    private List<String> constructParameterPairs() {
        return parameters.stream()
            .map(UrlParameter::format)
            .collect(Collectors.toList());
    }

    private static ParameterBuilder initialize(String query, String delimiter) {
        query = StringUtils.removeSuffix(query, "/");

        String[] parameters = query.split(delimiter);

        ParameterBuilder builder = new ParameterBuilder();
        for (String parameter : parameters) {
            UrlParameter.parse(parameter).ifPresent(builder::add);
        }
        return builder;
    }
}
