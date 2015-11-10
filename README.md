# clj-style

Front-end for different static analyzers tools for Clojure.

Goals:

- Support for different libraries
- Common error format
- Correct interaction with the shell

## Usage

Run all the checks:

```bash
$ lein check
```

Run a check for a particular library:

```bash
$ lein check kibit
```

Fix errors automatically:

```bash
$ lein fix
```

Fix errors using a particular library:

```bash
$ lein fix cljfmt
```

## Configuration

To change the default minimum coverage, you can change the :min-coverage key in your profile:

```clojure
:profiles {:dev {:clj-style {:min-coverage 80}}}
```

The default min-coverage value is 90%.

## Supported libraries

- cljfmt
- kibit
- eastwood
- cloverage
