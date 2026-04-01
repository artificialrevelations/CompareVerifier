# TODO

A list of improvements and features planned for future sessions.

## Verification

- Implement missing `Comparable` contract check:
  `sgn(a.compareTo(c)) == sgn(b.compareTo(c)) => sgn(a.compareTo(b)) == 0`
  Currently documented in `ComparableVerifier` Javadoc but not verified.

- Add support for verifying `Comparator` implementations in addition to `Comparable`.

## Tests

- Migrate from deprecated `ExpectedException` rule to `assertThrows()` (JUnit 4.13+)
- Standardize exception message matchers — mix of plain `String` and `CoreMatchers.containsString()`
- Add edge case tests: combining multiple suppress methods together
- Add tests for the missing contract check once implemented

## CI

- Migrate from CircleCI to GitHub Actions with a Java version matrix (8, 11, 17, 21)

## Build

- Add proper `mise.toml` configuration for the project
