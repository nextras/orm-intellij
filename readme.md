Nextras ORM Plugin for PhpStorm
===============================

Provides support for [Nextras ORM](https://github.com/nextras/orm) – an ORM library for PHP.

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
