## Rec Parse - parse application arguments with shapeless and scalaz

Rec Parse leverage shapeless extensible records and scalaz `ValidationNel` to parse and validate application arguments.

### Example

There is a Main example here `com.briefscala.Main.scala`

Run it with

```
$ sbt "run --file-path /some/path -sep | --is-new=true -len 3453456"
```
