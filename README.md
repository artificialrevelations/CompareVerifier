# ComparatorVerifier
ComparatorVerifier is a pure Java library that can be used in Junit tests to verify that classes implementing `Comparable` interfaces are defined correctly.

By default verification includes:
 * compareTo being consistent with equals
 * compareTo failing on a null argument
 * satisfying `sig(a.compareTo(b)) == -sig(b.compareTo(a))`
 * satisfying `sig(a.compareTo(c)) == sig(b.compareTo(c)) => sig(a.compareTo(b)) == 0`
 * satisfying `sig(a.compareTo(b)) > 0 && sig(b.compareTo(c)) > 0 => sig(a.compareTo(c)) > 0`
 
Usage
--------
```java
    ComparableVerifier.<Foo>forInstances(
        lessInstancesCreator,
        equalInstancesCreator,
        greaterInstancesCreator
    ).verify();    
```

Where:
- `lessInstancesCreator` creates instance of the class implementing `Comparable` that are less then instances created by `equalInstancesCreator`
- `equalInstancesCreator` creates instances that should be equal to each other, they should be greater then those created by
`lessInstancesCreator` and less then those created by `greaterInstancesCreator`
- `greaterInstancesCreator` creates instances that should be greater then those created by the two other creators

It's possible to disable one or more of the default verification checks by using set of "suppress" methods.

```java
    ComparableVerifier.<Foo>forInstances(
        lessInstancesCreator,
        equalInstancesCreator,
        greaterInstancesCreator
    )
    .suppressConsistentWithEquals(true)
    .suppressExceptionOnCompareToNull(true)
    .verify(); 
```

Download
--------

You can use this library in conjunction with JUnit `4.12`. Currently the only available comfortable way to import the library is through the use of [JITPACK.io](https://jitpack.io/):

In your `build.gradle` file at the end of `repositories` section add this line:

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
	        testImplementation 'com.github.foobaz42:ComparatorVerifier:master-SNAPSHOT'
	}
```

TODO
----

There are several missing things:
- transitivity checks
- other suppress options
- detailed usage examples
- documentation
- github release
- CI
