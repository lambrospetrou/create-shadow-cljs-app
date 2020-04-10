# Changelog

## 1.1.4

- Use `Shadow-CLJS`'s built-in server during watch mode instead of having to run separate server to serve the built assets.

## 1.1.3

- Only `shelljs` is now a non-dev dependency, which should make it slightly faster to run through `npx`

## 1.1.2

- Fixed a typo in the package name shown when running the initializer
- Minor refactoring in the code

## 1.1.1

- Fixed a typo in the usage typical example shown by `-h/--help`

## 1.1.0

### Removed

- Removed the option `-i/--install` and only retained the `--no-install` which still skips running `npm install` to the newly created project.

### Other

- Minor code refactoring

## 1.0.3

- Ported code to ClojureScript

## 1.0.2

- Fixed usage with `npx create-shadow-cljs-app <name>`

## <= 1.0.1

- **DEPRECATED**
- Does not work properly with `npx`
