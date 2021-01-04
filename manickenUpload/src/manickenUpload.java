/* -*- mode: java; c-basic-offset: 2; indent-tabs-mode: nil -*- */

/*
  Part of the Processing project - http://processing.org

  Copyright (c) 2008 Ben Fry and Casey Reas
  Copyright (c) 2020 Jannik LS Svensson (1984)- Sweden

  This program is free software; you can redistribute it and/or modify
  it under the terms of the GNU General Public License as published by
  the Free Software Foundation; either version 2 of the License, or
  (at your option) any later version.

  This program is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU General Public License for more details.

  You should have received a copy of the GNU General Public License
  along with this program; if not, write to the Free Software Foundation,
  Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

package com.manicken;

import javax.swing.JMenuItem;
import javax.swing.JMenu;
import javax.swing.Box;
import javax.swing.JOptionPane; // used by settings dialog

import processing.app.Editor;
import processing.app.PreferencesData;
import processing.app.EditorToolbar;
import processing.app.tools.Tool;

import processing.app.MyEditorToolbar;

import com.manicken.CustomMenu;
import com.manicken.CustomUploader;
import com.manicken.SettingsDialog;

/**
 * 
 */
public class manickenUpload implements Tool
{
	int retryCount = 0;

	Editor editor;// for the plugin
	EditorToolbar originalEditorToolBar; // used to restore the original toolbar when this plugin is deactivated
	MyEditorToolbar myEditorToolbar;
	CustomUploader customUploader;

	String thisToolMenuTitle = "Manicken Upload Only";

	public void init(Editor editor) { // required by tool loader
		this.editor = editor;
		customUploader = new CustomUploader(editor);

		// workaround to make sure that init is run after the Arduino IDE gui has loaded
		// otherwise any System.out(will never be shown at the init phase) 
		editor.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowOpened(java.awt.event.WindowEvent e) {
			  init();
			}
		});
	}

	public void run() {// required by tool loader
		// this is not used when using custom menu (see down @initMenu())
	}

	public String getMenuTitle() {// required by tool loader
		return thisToolMenuTitle;
	}

	private void UploadOnly(Boolean isShiftDown)
	{

		customUploader.handleExport(isShiftDown, myEditorToolbar, retryCount);
		//System.out.println("Upload only pressed");
	}

	private void Activate()
	{
		PreferencesData.setBoolean("manicken.uploadOnly.activated", true);
		Box upper = (Box)Reflect2.GetField("upper", editor);
		JMenu toolbarMenu = (JMenu)Reflect2.GetField("toolbarMenu", editor);
		myEditorToolbar = new MyEditorToolbar(editor, toolbarMenu, (Boolean isShiftDown) -> UploadOnly(isShiftDown));
		for (int i = 0; i < upper.getComponentCount(); i++)
		{
			if (upper.getComponent(i).getClass().getName().equals(processing.app.EditorToolbar.class.getName()))
			{
				originalEditorToolBar = (EditorToolbar)upper.getComponent(i); // used to restore the original toolbar when this plugin is deactivated
				upper.remove(i);
				upper.add(myEditorToolbar, i);
				System.out.println("Upload only - Activated");
				editor.setVisible(false); // fixes some Java bug
				editor.setVisible(true);
				return;
			}
		}
		System.err.println("Upload only - NOT Activated");
	}

	private void Deactivate()
	{
		PreferencesData.setBoolean("manicken.uploadOnly.activated", false);
		Box upper = (Box)Reflect2.GetField("upper", editor);
		for (int i = 0; i < upper.getComponentCount(); i++)
		{
			if (upper.getComponent(i).getClass().getName().equals(processing.app.MyEditorToolbar.class.getName()))
			{
				upper.remove(i);
				upper.add(originalEditorToolBar, i); // restore the original toolbar
				System.out.println("Upload only - Deactivated");
				return;
			}
		}
		System.err.println("Upload only - NOT Deactivated");
	}

	/**
	 * This is the code that runs after the Arduino IDE GUI has been loaded
	 */
	private void init() {
		try{
			CustomMenu cm = new CustomMenu(this.editor, thisToolMenuTitle, 
				new JMenuItem[] {
					CustomMenu.Item("Activate", event -> Activate()),
					CustomMenu.Seperator(),
					CustomMenu.Item("Deactivate", event -> Deactivate()),
					CustomMenu.Seperator(),
					CustomMenu.Item("Settings", event -> ShowSettingsDialog()),
				});
			cm.Init(true);
			retryCount = PreferencesData.getInteger("manicken.uploadOnly.retryCount", retryCount);

			if (PreferencesData.getBoolean("manicken.uploadOnly.activated", false))
			{
				Activate();
			}
				
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println(thisToolMenuTitle + " could not start!!!");
			return;
		}
	}

	private void ShowSettingsDialog()
	{
		SettingsDialog sd = new SettingsDialog();
		sd.txtRetryCount.setText(Integer.toString(retryCount));
		int result = JOptionPane.showConfirmDialog(editor, sd, "Upload without compile - settings" ,JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
			
		if (result != JOptionPane.OK_OPTION) {
			System.out.println("Cancelled");
			return;
		}
		int rc = 0;
		try {
			rc = Integer.parseInt(sd.txtRetryCount.getText());
			retryCount = rc;
			PreferencesData.setInteger("manicken.uploadOnly.retryCount", retryCount);
		}
		catch (Exception e) {System.err.println("Warning Retry count cannot be empty!\nprevious value used:" + retryCount);}
	
	}

}
