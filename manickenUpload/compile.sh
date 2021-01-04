
arduinoInstallDir=~/arduino-1.8.13
arduinoSketchBookDir=~/Arduino
className=manickenUpload

function cleanPrevBinFiles {
    cd ./bin
    rm -r *
    cd ..
}

function compile {
    mkdir ./bin
    javac -cp .:$arduinoInstallDir/lib/pde.jar:$arduinoInstallDir/lib/arduino-core.jar:%arduinoInstallDir/lib/jtouchbar-1.0.0.jar -d bin ./src/*.java
    buildStatus=$?
}

function makeJar {
    cd bin
    jar cvf $classname.jar *
    cd ..
}

function copyfiles {
    cp ./bin/$classname.jar ./tool/$classname.jar
	mkdir $arduinoSketchBookDir/tools/$classname/tool
	mkdir $arduinoSketchBookDir/tools/$classname/src
    cp -r ./tool/* $arduinoSketchBookDir/tools/$classname/tool
    cp -r ./src/* $arduinoSketchBookDir/tools/$classname/src

    echo ***************
    echo *** Success ***
    echo ***************
}

function doStuff {
    cleanPrevBinFiles
    compile
    success=0
    if [ $buildStatus -eq $success ];
    then
        makeJar
        copyfiles	
    else
        echo Compile error
    fi
	cleanPrevBinFiles
}

doStuff

##read -p "Press [Enter] key to continue..."