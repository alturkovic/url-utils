![Release](https://jitpack.io/v/alturkovic/url-utils.svg)

`url-utils` library can be used to create or modify urls in Java.

The library can be used to build URI instances or to parse already built URI instances to extract data from them.

The main API classes are:
 - `UrlBuilder` - to build new URI instances
 - `UrlParser` - to extract information from URI instances

## Examples

1. Add path or query parameters using lambdas.
```java
System.out.println(UrlBuilder.of("www.youtube.com")
    .host(HostBuilder::withoutWww)
    .path(path -> path.add("watch"))
    .query(query -> query.add("v", "dQw4w9WgXcQ"))
    .build()); // https://youtube.com/watch?v=dQw4w9WgXcQ
```

2. Remove URL components other than host.
```java
System.out.println(UrlBuilder.of("https://stackoverflow.com/questions/1732348/regex-match-open-tags-except-xhtml-self-contained-tags/1732454#1732454")
    .strip()
    .build()); // https://stackoverflow.com
```

3. Build URLs with matrix parameters.
```java
System.out.println(UrlBuilder.of("localhost")
    .port(8080)
    .path(path -> path
        .add("person").parameters(parameters -> parameters
            .add("name", "John")
            .add("age", "25")
        ).add("search"))
    .query(query -> query.add("q", "phrase"))
    .build()); // https://localhost:8080/person;name=John;age=25/search?q=phrase
```

4. Extract URL components.
```java
UrlParser parsed = UrlParser.of("https://www.youtube.com/watch?v=dQw4w9WgXcQ");
System.out.println(parsed.getQueryParameters()); // {v=[dQw4w9WgXcQ]}
System.out.println(parsed.getPort()); // 443
```
5. Parse un-encoded URLs.
```java
URI uri = UrlParser.parse("example.com/a b"); // https://example.com/a%20b
```

6. Extract file type.
```java
System.out.println(UrlParser.of("localhost/api/data.csv").getFileType()); // csv
```

## Importing into your project using Maven

Add the JitPack repository into your `pom.xml`.

```xml

<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>
```

Add the following under your `<dependencies>`:

```xml

<dependencies>
    <dependency>
        <groupId>com.github.alturkovic</groupId>
        <artifactId>url-utils</artifactId>
        <version>1.0.4</version>
    </dependency>
</dependencies>
```