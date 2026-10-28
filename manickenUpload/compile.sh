
#!/usr/bin/env bash

set -euo pipefail

# Resolve the directory containing this script.
SCRIPT_DIR="$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")" && pwd -P)"

arduinoInstallDir="$HOME/arduino-1.8.13"
arduinoSketchBookDir="$HOME/Arduino"
className="manickenUpload"

BIN_DIR="$SCRIPT_DIR/bin"

function cleanPrevBinFiles {
    if [[ ! -d "$BIN_DIR" ]]; then
        return 0
    fi

    # Refuse to clean anything except the expected build directory.
    if [[ "$BIN_DIR" != "$SCRIPT_DIR/bin" ]]; then
        echo "Invalid build directory!"
        return 1
    fi

    find "$BIN_DIR" -mindepth 1 -maxdepth 1 -exec rm -rf -- {} +
}

function compile {
    mkdir -p "$BIN_DIR"

    javac -cp ".:$arduinoInstallDir/lib/pde.jar:$arduinoInstallDir/lib/arduino-core.jar:$arduinoInstallDir/lib/jtouchbar-1.0.0.jar" \
        -d "$BIN_DIR" "$SCRIPT_DIR"/src/*.java
}

function makeJar {
    (
        cd "$BIN_DIR"
        jar cvf "$className.jar" *
    )
}

function copyfiles {
    cp "$BIN_DIR/$className.jar" "$SCRIPT_DIR/tool/$className.jar"

    mkdir -p "$arduinoSketchBookDir/tools/$className/tool"
    mkdir -p "$arduinoSketchBookDir/tools/$className/src"

    cp -r "$SCRIPT_DIR"/tool/* "$arduinoSketchBookDir/tools/$className/tool/"
    cp -r "$SCRIPT_DIR"/src/* "$arduinoSketchBookDir/tools/$className/src/"

    echo "***************"
    echo "*** Success ***"
    echo "***************"
}

function doStuff {
    cleanPrevBinFiles
    compile
    makeJar
    copyfiles
    cleanPrevBinFiles
}

doStuff
