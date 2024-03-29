# Changelog

## Unreleased

### Changed
- Remove entity type propagation on repository/collection/relationships in `find*`/`get*` and similar methods. This functionality is now built-in in PHPStorm as it supports enough of generics. 
- Drop support for Orm v3.
- Fix missing support for like operator preventing reference detection in `findBy()` methods.
- Add support for resolving filtering expressions when relationship is defined by a generic `ManyHasMany<AnotherEntity>`.

## 0.9.1 - 2023-07-28

### Changed
- Fix compatibility with the latest PHPStorm.
- Internal library updates.

## 0.9.0 - 2022-11-17

### Changed
- Added support for marking entity getters/setters as implicitly used; closes #12
- Fixed support for PHPStorm 2022.3 EAP, minimal version is PHPStorm 2022.3.

## 0.8.3

### Changed
- Removed upper bound of compatibility constraint for latest PhpStorm. (Second try)
- Updated build dependencies.

## 0.8.2

### Changed
- Removed upper bound of compatibility constraint for latest PhpStorm.

## 0.8.1

### Fixed
- Fixed entity type resolution on IModel::getRepository() expression.

## 0.8.0

### Added
- Added auto-completion & reference to entity properties in hasValue() method.

### Fixed
- Fixed entity type resolution for variables typed with "resolution-invalid" types (e.g. `IRepository` interface); closes #18

### Changed
- The plugin internal continuous delivery system & checks reworked.
