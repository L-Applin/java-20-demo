# Java 20 demon

## compile and run

### Manually (recommanded)

Make sure you are using Java v20

```bash
java -version

openjdk version "20" 2023-03-21
OpenJDK Runtime Environment (build 20+36-2344)
OpenJDK 64-Bit Server VM (build 20+36-2344, mixed mode, sharing)
```

To run one of the class with Java 20 just run the following commands
```
mkdir out
DEMO=Concurrent
javac --enable-preview --release 20 --add-modules jdk.incubator.concurrent -d out/ src/ca/applin/demo/$DEMO.java
java --enable-preview --add-modules jdk.incubator.concurrent -cp out ca.applin.demo.$DEMO
```

Replace `DEMO=Concurrent` by `DEMO=Pattern` or other class in the `ca.applin.demo` package to run those instead.
Or simply run `./demo.sh` with the class name as argument, ie 
```
./demo.sh Concurrent
```

### IntelliJ
- In `Preferences > Build, Execution, Deployment > Java Compiler` change:
  - in `Additional command line parameters` copy `--enable-preview --add-modules jdk.incubator.concurrent`
- In `Project Structure > Project` make sure:
  - `SDK`: your java 20 JDK
  - `Langage level`: `X - Experimental features`
- In your `Run commands` add
  - `Edit configuration > Mpdify options > Add VM option` and paste `--enable-preview --add-modules jdk.incubator.concurrent`  