# create-shadow-cljs-app

This is an NPM initializer to ease the creation of projects using the **awesome** [Shadow-CLJS](https://shadow-cljs.github.io/docs/UsersGuide.html).

The default template provides out-of-the-box setup for a Node script, a standard `commonjs` library, and a browser-compatible output.

## Get started

To create a project named `cljs-app`, run any of the following commands:

```
npx create-shadow-cljs-app cljs-app

# or 

npm init shadow-cljs-app cljs-app
```

This will create a directory `cljs-app` with code for the three main targets supported.

To create release artifacts for all of the available targets run `npm run release`.

Check the `README.md` of the created project for more details on what you get.

## Options

To see all the options available run `npx create-shadow-cljs-app --help`.

## Contributing

The goal of this project is to provide a no-fuss starting template for projects to use `Shadow-CLJS`.

I am happy to accept pull requests as long as the goal of the projects remains as is.

When choosing between simplicity and features, always prefer simplicity.
