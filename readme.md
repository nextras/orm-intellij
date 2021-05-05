Nextras ORM Plugin for PhpStorm
===============================

<!-- Plugin description -->
Provides support for [Nextras ORM](https://github.com/nextras/orm) – an ORM library for PHP.

Supported features:
- **Entity modifiers highlighting** - see clearly your modifiers;
- **Entity modifiers reference contributor** - renames of property changes references to it in modifiers;
- **Entity setter & getter generator** - helps you to create these methods;
- **Collection expression auto-completion** - suggests entity properties and allow relationships traversing;
- **Collection & Repository entity type propagation** - propagates specific entity types for `find*`, `get*`, and persisting methods;
<!-- Plugin description end -->

## Contributing

### Provisioning

- Install [IntelliJ IDEA Ultimate](https://www.jetbrains.com/idea/)
- Install PHP plugin
- Open this project
- In `build.gradle` set a proper path to your PhpStorm / comment out the `alternativeIdePath` to debug in IEDA Ultimate

### Debug

- Run, Edit Configurations, +, Plugin
- keep defaults, but change Use classpath of module from `[none]` to `nextras-orm-plugin`
- optionally change JRE to PHPStorm
