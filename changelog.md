# Changelog

## Unreleased

- Fixed crash in RepositoryMapperMethodMarkerProvider

### Changed

## 2.0.4 - 2025-06-13

### Changed

- Fixed auto-importing autocompleted class name in modifiers.

## 2.0.3 - 2025-06-13

### Changed

- Added autocompletion for class names in modifiers {wrapper }{m:1 }{1:m }{m:m }{1:1 }

## 2.0.2 - 2025-01-23

### Changed

- Fixed autocompletion & reference contribution (ctrl+click) in `getBy()` methods on various complex expression. #106

## 2.0.1 - 2025-01-22

### Changed

- Fixed autocompletion & reference contribution (ctrl+click) in `findBy()` methods on various complex expression. #103

## 2.0.0 - 2024-12-15

### Changed

- Dropped support for Orm v3 and v4 - only Orm 5 is supported; specifically, the generics are well supported directly in the PhpStorm, so this functionality got broken/semi-functional here and since Orm 5 utilizes generics properly, this is now not supported here; since Orm 4 is not using generics fully, this plugin will not bring there the type resolution anymore.
- Fix missing support for like operator preventing reference detection in `findBy()` methods.
- Add support for resolving filtering expressions when a relationship is defined by a generic `ManyHasMany<AnotherEntity>`.
- Fix an exception when searching usages.

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
