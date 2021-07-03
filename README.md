![Release](https://jitpack.io/v/alturkovic/url-utils.svg)

`url-utils` helps you operate with URLs in Java.

The library can be used to build, parse, compare, etc. URI instances.

The main API classes are:
 - `UrlBuilder` - to build new URI instances
 - `UrlParser` - to extract information from URI instances
 - `UrlMatcher` - to check if URI is under any registered URI
 - `UrlEquals` - to check if 2 URIs are equal

## Examples

### Building

1. Add path or query parameters using lambda builders.
```java
System.out.println(UrlBuilder.of("www.youtube.com")
    .host(HostBuilder::withoutWww)
    .path(path -> path.add("watch"))
    .query(query -> query.add("v", "dQw4w9WgXcQ"))
    .build()); // https://youtube.com/watch?v=dQw4w9WgXcQ
```

2. Remove URL components except the protocol and the host.
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

### Parse

1. Extract URL components.
```java
UrlParser parser = UrlParser.of("https://www.youtube.com/watch?v=dQw4w9WgXcQ");
System.out.println(parser.getQueryParameters()); // {v=[dQw4w9WgXcQ]}
System.out.println(parser.getPort()); // 443

```
2. Parse un-encoded URLs.
```java
URI uri = UrlParser.parse("example.com/a b"); // https://example.com/a%20b
```

3. Extract file type.
```java
System.out.println(UrlParser.of("localhost/api/data.csv").getFileType()); // csv
```

### Match

Check if URI is located on a path of any URI previously registered.
```java
UrlMatcher matcher = new UrlMatcher();
matcher.register(UrlParser.parse("example.com/a/b"));
System.out.println(matcher.matches(UrlParser.parse("example.com/a/b"))); // true
System.out.println(matcher.matches(UrlParser.parse("example.com/a/b/c"))); // true
System.out.println(matcher.matches(UrlParser.parse("example.com/b"))); // false
System.out.println(matcher.matches(UrlParser.parse("another.com"))); // false
```

### Equals

`URL.equals` is [broken](https://stackoverflow.com/questions/3771081/proper-way-to-check-for-url-equality/).

```java
URI source = UrlParser.parse("http://example.com");
URI target = UrlParser.parse("http://example.com:80/");
System.out.println(source.equals(target)); // false
System.out.println(UrlEquals.areUrlsEqual(source, target)); // true
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
        <version>1.2.0</version>
    </dependency>
</dependencies>
```