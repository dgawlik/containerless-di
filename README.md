# containerless-di

### Why

Most of the web development in Java happens in Spring which is at it's core container with many helper beans. Container
doing dependency injection decreases coupling between beans.

But this approach has drawbacks:

* Scanning and autowiring happens only on container's init. Cool extension would be to reactively rewire beans on
  demand, for example by invoking http endpoint. As far as I know this functionality is present in Spring but limited.
* Spring is very heavy and poking around is impractical
* Tests and autowiring don't go well together. Either it's full blown integration testing or setting mocks by hand.

Moreover you can make couple of observations:

* classpath scanning is really fast, given you are not loading every class to jvm. There are tools like ASM and
  javassist that allow you to analyze bytecode in memory (check for injection points).
* given above (scanning not costly) why not make DI more lightweight and assemble beans on demand
* you can externalize created beans to some store and modification of this store gives you ablity to enforce custom
  scopes

### Usage

```java
var cache=new Cache();
var env=new Environment
  .withClasspathFile("/some.resource")
  .withMap(Map.of("a",1,"b",2));
var prio = Map.of(A.class, 1, B.class, 2);

var res=Injector.forClass(SomeClass.class)
  .scanning("io.dgawlik.scratchpad")
  .cache(cache)
  .environment(env)
  .priorities(prio)
  .assemble();
```

```java
public class A {
    public final Integer val1;

    @Inject
    public A(@Value("a") Integer val1) {
        this.val1 = val1;
    }
}
```

```java
public class B {
    public final Integer val2;

    @Inject
    public B(@Value("b") Integer val2) {
        this.val2 = val2;
    }
}
```

```java
public class Composite {
    public final A a;
    public final B b;

    @Inject
    public Composite(A a, B b) {
        this.a = a;
        this.b = b;
    }
}

```

`Injector` does the autowiring on demand. There are two cases of
injection: `@Inject` annotation on constructor and empty arg constructor for convenience.
This is standard autowiring as in Spring, you code against interface
and any assignable class can be injected.

`@Value` does injection of properties in very limited way but
conversion to primitive types is supported.

While autowiring `Injector` stores any temporary beans in graph
in `Cache`. If you reuse it for same call you get singleton-like scoping.
Any custom scoping can be achieved by modifying cache in between the
calls. You can also preload the cache and get them injected (this would be
the case for injecting mocks).

There are 2 ways of impacting autowiring: priorities and `@Qualifier`.
You can set priorities map from class to integer per call and switch autowired
beans, because only the lowest number wins. `@Qualifier` on the other hand
limits matching beans to only those with same label (you put it on constructor and constructors params).

### Further work

I used reflections lib which is great, but I'm not sure if it
doesn't load scanned classes to jvm. It would be nice to switch to
ASM.