# clj-style

Front-end for different static analyzers tools for Clojure.

Goals:

- Support for different tools
- Commong error format
- Correct interaction witht the shell

## Usage

Run all the checks

```bash
$ lein check
```

Run a check for a particular library

```bash
$ lein check kibit
```

Fix errors automatically

```bash
$ lein fix
```

Fix errors using a particular library
```bash
$ lein fix cljfmt
```

## Supported libraries

- cljfmt
- kibit
- eastwood
