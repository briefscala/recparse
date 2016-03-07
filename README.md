## Rec Parse - parse arguments with shapeless

[![Join the chat at https://gitter.im/briefscala/recparse](https://badges.gitter.im/briefscala/recparse.svg)](https://gitter.im/briefscala/recparse?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)

Rec Parse leverage shapeless extensible records and scalaz `ValidationNel` to parse and validate application arguments.

### Example

There is a Main example here `com.briefscala.Main.scala`

Run it with

```
$ sbt "run --file-path /some/path -sep | --is-new=true -len 3453456"
```
