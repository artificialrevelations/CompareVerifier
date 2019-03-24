# CompareVerifier
CompareVerifier is a pure Java library that can be used in Junit tests to verify that classes implementing `Comparable` interfaces are defined correctly.

By default verification includes:
 * compareTo being consistent with equals
 * compareTo failing on a null argument
 * satisfying `sgn(a.compareTo(b)) == -sgn(b.compareTo(a))`
 * satisfying `sgn(a.compareTo(c)) == sgn(b.compareTo(c)) => sgn(a.compareTo(b)) == 0`
 * satisfying `sgn(a.compareTo(b)) > 0 && sgn(b.compareTo(c)) > 0 => sgn(a.compareTo(c)) > 0`
 
Usage
--------
```java
    final VerificationInstancesCreator<BigDecimal> lesserCreator =
            VerificationInstancesCreators.from(
                new BigDecimal("0.0"), // smaller then the objects returned by
                new BigDecimal("1.0"), // both equal creator and greater creator
                new BigDecimal("2.0")
            );
    final VerificationInstancesCreator<BigDecimal> equalCreator =
            VerificationInstancesCreators.from(
                new BigDecimal("42.0"),
                new BigDecimal("42.0")
            );
    final VerificationInstancesCreator<BigDecimal> greaterCreator =
            VerificationInstancesCreators.from(
                new BigDecimal("101.0"), // larger then the objects returned by
                new BigDecimal("202.0"), // both equal creator and lesser creator
                new BigDecimal("303.0")
	    );
    
    ComparableVerifier
        .<Foo>forInstances(lesserCreator, equalCreator, greaterCreator)
	.verify();    
```

Where:
- `lessInstancesCreator` creates instance of the class implementing `Comparable` that are less then instances created by `equalInstancesCreator`
- `equalInstancesCreator` creates instances that should be equal to each other, they should be greater then those created by
`lessInstancesCreator` and less then those created by `greaterInstancesCreator`
- `greaterInstancesCreator` creates instances that should be greater then those created by the two other creators

It's possible to disable one or more of the default verification checks by using set of "suppress" methods.

```java
    ComparableVerifier
        .<Foo>forInstances(lesserCreator, equalCreator, greaterCreator)
        .suppressConsistentWithEquals(true)
        .suppressExceptionOnCompareToNull(true)
        .verify(); 
```

Download
--------

You can use this library in conjunction with JUnit `4.12`. There are two ways of getting the library:
- through [releases](https://github.com/foobaz42/CompareVerifier/releases)
- through the use of [JITPACK.io](https://jitpack.io/)

To configure your project with JITPACK you need to in your `build.gradle` file at the end of `repositories` section add this line:

```groovy
	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
```

Add `ComparatorVerifier` to the list of your test dependencies:

```groovy
	dependencies {
	        testImplementation 'com.github.foobaz42:CompareVerifier:master-SNAPSHOT'
	}
```

TODO
----

- detailed usage examples
