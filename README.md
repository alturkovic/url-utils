![Release](https://jitpack.io/v/alturkovic/url-utils.svg)

`url-utils` library can be used to create or modify urls in Java.

The library can be used to build URI instances or to parse already built URI instances to extract data from them.

The main API classes are:
 - `UrlBuilder` - to build new URI instances
 - `UrlParser` - to extract information from URI instances

## Examples

```java
System.out.println(UrlBuilder.of("www.youtube.com")
    .withoutWww()
    .path(path -> path.add("watch"))
    .query(query -> query.add("v", "dQw4w9WgXcQ"))
    .build()); // https://youtube.com/watch?v=dQw4w9WgXcQ
```

```java
System.out.println(UrlBuilder.of("https://stackoverflow.com/questions/1732348/regex-match-open-tags-except-xhtml-self-contained-tags/1732454#1732454")
    .strip()
    .build()); // https://stackoverflow.com
```

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

```java
UrlParser parsed = UrlParser.of("https://www.youtube.com/watch?v=dQw4w9WgXcQ");
System.out.println(parsed.getQueryParameters()); // {v=[dQw4w9WgXcQ]}
System.out.println(parsed.getPort()); // 443
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
        <version>1.0.0</version>
    </dependency>
</dependencies>
```