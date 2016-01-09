Nextras ORM Plugin for PhpStorm
===============================

Provides support for [Nextras ORM](https://github.com/nextras/orm) – an ORM library for PHP.

## Contributing

### Provisioning

- Install [IntelliJ IDEA Ultimate](https://www.jetbrains.com/idea/)
- Install PHP plugin
- Open this project
- Navigate to File, Project Structure modal dialog, switch to Libraries and click + to add New Project Library, Java.
  - Navigate to your installation of PhpStorm and select `plugins/php/lib/php-openapi.jar` and `php.jar` (make sure the lib is imported with name `php-openapi`)
  - From your installation of IDEA add `IntelliJ IDEA 15.app/Contents/redist/annotations.jar` (import with name `annotations`)

### Debug

- Run, Edit Configurations, +, Plugin
- keep defaults, but change Use classpath of module from `[none]` to `nextras-orm-plugin`
- optionally change JRE to PHPStorm
