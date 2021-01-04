package com.manicken;

import com.manicken.Reflect2;
import processing.app.MyEditorToolbar;

import processing.app.Editor;
import processing.app.MyEditorToolbar;
import processing.app.PreferencesData;
import static processing.app.I18n.tr; // translate (multi language support)

import java.lang.Thread;

import javax.swing.JMenu;

public class CustomUploader {

    Editor editor;
    

    private MyUploadHandler uploadHandler;
    private MyUploadHandler uploadUsingProgrammerHandler;
    private Runnable timeoutUploadHandler;
    MyEditorToolbar toolbar;
    processing.app.EditorStatus status;
    int retryCount = 0;

    public CustomUploader(Editor editor)
    {
        this.editor = editor;

        uploadHandler = new MyUploadHandler();
        uploadHandler.setUsingProgrammer(false);
        uploadUsingProgrammerHandler = new MyUploadHandler();
        uploadUsingProgrammerHandler.setUsingProgrammer(true);
        timeoutUploadHandler = new MyTimeoutUploadHandler();
    }

    synchronized public void handleExport(final boolean usingProgrammer, final MyEditorToolbar toolbar, int retryCount) {
        this.toolbar = toolbar;
        this.retryCount = retryCount;
        /* if (PreferencesData.getBoolean("editor.save_on_verify")) {
          if (sketch.isModified() && !sketchController.isReadOnly()) {
            handleSave(true);
          }
        }*/
        toolbar.activateExport();
        processing.app.EditorConsole console = (processing.app.EditorConsole)Reflect2.GetField("console", editor);
        console.clear();
        status = (processing.app.EditorStatus)Reflect2.GetField("status", editor);
        status.progress(tr("Uploading to I/O Board..."));
    
        //Reflect2.SetField("avoidMultipleOperations", editor, true);
        editor.avoidMultipleOperations = true;
    
        new Thread(timeoutUploadHandler).start();
        new Thread(usingProgrammer ? uploadUsingProgrammerHandler : uploadHandler).start();
      }

      class MyUploadHandler implements Runnable {
        boolean usingProgrammer = false;
    
        public void setUsingProgrammer(boolean usingProgrammer) {
          this.usingProgrammer = usingProgrammer;
        }
    
        public void run() {
           // processing.app.EditorStatus status = (processing.app.EditorStatus)Reflect2.GetField("status", editor);
          try {
            Reflect2.SetField("uploading", editor, true);
            //uploading = true;
    
            //removeAllLineHighlights();
            processing.app.AbstractMonitor serialMonitor = (processing.app.AbstractMonitor)Reflect2.GetField("serialMonitor", editor);
            if (serialMonitor != null) {
              serialMonitor.suspend();
            }
            processing.app.AbstractMonitor serialPlotter = (processing.app.AbstractMonitor)Reflect2.GetField("serialPlotter", editor);
            if (serialPlotter != null) {
              serialPlotter.suspend();
            }
    
            //boolean success = sketchController.exportApplet(usingProgrammer);
            boolean success = myExportApplet(usingProgrammer);


            if (success) {
                editor.statusNotice(tr("Done uploading."));
            }
          } catch (processing.app.SerialNotFoundException e) {
              JMenu portMenu = (JMenu)Reflect2.GetField("portMenu", editor);
            if (portMenu.getItemCount() == 0) {
                editor.statusError(tr("Serial port not selected."));
            } else {
              //if (editor.serialPrompt()) {
              if ((boolean)Reflect2.InvokeMethod("serialPrompt", editor)) {
                run();
              } else {
                editor.statusNotice(tr("Upload canceled."));
              }
            }
          } catch (processing.app.helpers.PreferencesMapException e) {
            editor.statusError(processing.app.I18n.format(
                        tr("Error while uploading: missing '{0}' configuration parameter"),
                        e.getMessage()));
          } catch (processing.app.debug.RunnerException e) {
            //statusError("Error during upload.");
            //e.printStackTrace();
            status.unprogress();
            editor.statusError(e);
          } catch (Exception e) {
            e.printStackTrace();
          } finally {
            //editor.populatePortMenu();
            Reflect2.InvokeMethod("populatePortMenu", editor);
            //Reflect2.SetField("avoidMultipleOperations", editor, false);
            editor.avoidMultipleOperations = false;
          }
          status.unprogress();
          Reflect2.SetField("uploading", editor, false);
          //uploading = false;
          //toolbar.clear();
          toolbar.deactivateExport();
    
          //editor.resumeOrCloseSerialMonitor();
          Reflect2.InvokeMethod("resumeOrCloseSerialMonitor", editor);
          //editor.resumeOrCloseSerialPlotter();
          Reflect2.InvokeMethod("resumeOrCloseSerialPlotter", editor);
          processing.app.Base base = (processing.app.Base)Reflect2.GetField("base", editor);
          base.onBoardOrPortChange();
        }
      }
      class MyTimeoutUploadHandler implements Runnable {

        public void run() {
          try {
            //10 seconds, than reactivate upload functionality and let the programmer pid being killed
            Thread.sleep(1000 * 10);
            boolean uploading = (boolean)Reflect2.GetField("uploading", editor);
            if (uploading) {
                editor.avoidMultipleOperations = false;
                //Reflect2.SetField("avoidMultipleOperations", editor, false);
            }
          } catch (InterruptedException e) {
              // noop
          }
        }
      }

      /**
   * Handle export to applet.
   */
  //String foundName = null;
  protected boolean myExportApplet(boolean usingProgrammer) throws Exception {
    // build the sketch
    status.progressNotice(tr("Compiling sketch..."));
    //if (foundName == null)
    //    foundName = editor.getSketchController().build(false, false);

    String foundName = editor.getSketch().getName() + ".ino";

    System.out.println("uploading: " + foundName + ".hex");
    // (already reported) error during export, exit this function
    if (foundName == null) return false;

//    // If name != exportSketchName, then that's weirdness
//    // BUG unfortunately, that can also be a bug in the preproc :(
//    if (!name.equals(foundName)) {
//      Base.showWarning("Error during export",
//                       "Sketch name is " + name + " but the sketch\n" +
//                       "name in the code was " + foundName, null);
//      return false;
//    }

    status.progressNotice(tr("Uploading..."));
    status.progressUpdate(0);
    //boolean success = upload(foundName, usingProgrammer);
    boolean success = true;
    
    int currRetryCount = 0;
    do {
        success = (boolean)Reflect2.InvokeMethod2("upload", editor.getSketchController(), Reflect2.asArr(foundName, usingProgrammer), Reflect2.asArr(String.class, boolean.class));
      } while (!success && currRetryCount++ < retryCount);

    status.progressUpdate(100);
    return success;
  }
}
