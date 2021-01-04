# manickenUpload
a Arduino IDE "plugin" that changes the <br>
default upload "button" (compile->upload) functionality with a one without the compile<br>

## Install

* global:<br>
&nbsp;&nbsp;download this repository by either Code-Download Zip or<br>
&nbsp;&nbsp;&nbsp;&nbsp;by git clone https://github.com/manicken/arduinoPrefsSaver.git<br>
&nbsp;&nbsp;then extract/open the repository<br>

* global (into sketchbook folder (defined in Arduino IDE - Preferenses):<br>
&nbsp;&nbsp;make a new folder in the above defined sketchbook folder<br>
&nbsp;&nbsp;called tools<br>
&nbsp;&nbsp;then copy the folder manickenUpload from the repository into this new "tools" folder.<br>

### Alternative install

* on windows / linux (into Arduino IDE install dir):<br>
&nbsp;&nbsp;copy folder manickenUpload to [Arduino IDE install location]/tools directory<br>
&nbsp;&nbsp;ex: /Arduino-1.8.13/tools<br>

* on mac (into Arduino IDE package):<br>
&nbsp;&nbsp;In Applications right click and click on "Show Package Contents", then browse Contents -> Java -> tools<br>
&nbsp;&nbsp;by holding the Option key(copy) drag folder manickenUpload from the downloaded repository to the open tools folder above<br>
&nbsp;&nbsp;select replace it you allready have an older version<br>

## Compiling (optional)

Download and Install Java SDK8 (1.8) 32bit<br>
(Arduino IDE uses Java 8 (1.8))<br>

two script is provided:<br>
&nbsp;&nbsp;for windows the .bat file<br>
&nbsp;&nbsp;for linux/mac the .sh file<br>

## Features

Custom menu at mainManuBar-Extensions-"Manicken Upload Only"<br>
that contains:<br>
Activate()  - activates the Upload Only Button<br>
Deactivate()  - deactivates the Upload Only Button and restores the original functionality<br>
<br>
note. the button is only replaced by the "plugin" at startup when activated<br>
and if the plugin is removed or disabled the original button functionality is restored.

## Settings

the activated state is stored in Arduino global preferences.txt<br>
with the name:<br>
manicken.uploadOnly.activated<br>

## Requirements

none

## Known Issues

none

## Release Notes

### 1.0.0

* First release

-----------------------------------------------------------------------------------------------------------