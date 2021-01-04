
package com.manicken;

import processing.app.Base;
import processing.app.BaseNoGui;
import processing.app.Editor;
import processing.app.tools.Tool;
import processing.app.Sketch;
import processing.app.PreferencesData;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JMenuBar;
import javax.swing.MenuElement;

import static processing.app.I18n.tr; // translate (multi language support)

import com.manicken.Reflect2;

public class CustomMenu {

	public JMenuItem[] items;
	public Editor editor;
	
	public String toolMenuTitle;

	public JMenu toolsMenu = null;
	public JMenu extensionsMenu = null;
	public JMenuBar menubar = null;

	private boolean initOnce = false;

	public CustomMenu(Editor editor, String toolMenuTitle, JMenuItem[] items)
	{
		this.editor = editor;
		this.toolMenuTitle = toolMenuTitle;
		this.items = items;

		menubar = editor.getJMenuBar();
		toolsMenu = (JMenu) Reflect2.GetField("toolsMenu", editor);
	}
    public void Init(boolean useSeparateExtensionsMainMenu)
	{
		if (initOnce) return; // it can only be initialized once
		initOnce = true;

		if (useSeparateExtensionsMainMenu)
			initAtSeparateExtensionsMenu();
		else
			initAtToolsMenu();
		
	}


	private void initAtSeparateExtensionsMenu()
	{
		int existingExtensionsMenuIndex = GetMenuBarItemIndex(menubar, tr("Extensions"));
		int toolsMenuIndex = GetMenuBarItemIndex(menubar, tr("Tools"));
		

		if (existingExtensionsMenuIndex == -1)
			extensionsMenu = new JMenu(tr("Extensions"));
		else
			extensionsMenu = (JMenu)menubar.getSubElements()[existingExtensionsMenuIndex];

		JMenu thisToolMenu = new JMenu(toolMenuTitle);	

		if (existingExtensionsMenuIndex == -1)
			menubar.add(extensionsMenu, toolsMenuIndex+1);
		menubar.revalidate(); // "repaint" menu bar with the new item
		extensionsMenu.add(thisToolMenu);
		// create new special menu
		CreatePluginMenu(thisToolMenu);
		// remove original menu at the moment sometimes buggy
		//JMenu toolsMenu = (JMenu) Reflect.GetField("toolsMenu", this.editor);
		//int thisToolMenuIndex = GetMenuItemIndex(toolsMenu, thisToolMenuTitle);
		//toolsMenu.remove(thisToolMenuIndex);
		//toolsMenu.revalidate();
	}

	private void initAtToolsMenu()
	{
		
		int thisToolIndex = GetMenuItemIndex(toolsMenu, toolMenuTitle);
		JMenu thisToolMenu = new JMenu(toolMenuTitle);
		// create new special menu
		CreatePluginMenu(thisToolMenu);
		// replace original menu
		toolsMenu.remove(thisToolIndex);
		toolsMenu.insert(thisToolMenu, thisToolIndex);
	}

	private void CreatePluginMenu(JMenu thisToolMenu)
	{
		for (int i = 0; i < items.length; i++)
		{
			if (items[i] != null)
				thisToolMenu.add(items[i]);
			else
				thisToolMenu.addSeparator();
		}
		//thisToolMenu.add(new javax.swing.JTextField("Linenumber Spacing"));
	}

	public static JMenuItem Item(String text, java.awt.event.ActionListener action)
	{
		JMenuItem newItem = new JMenuItem(text);
		newItem.addActionListener(action);
		return newItem;
	}
	public static JMenuItem Seperator()
	{
		return null;
	}

	/**
	 * Workaround
	 * To get the current "Initial" plugin menu index
	 * So that it can be replaced by
	 * a custom menu item with submenus
	 * @param menu
	 * @param name
	 * @return
	 */
	public static int GetMenuItemIndex(JMenu menu, String name) {
		//System.out.println("try get menu: " + name);
		for ( int i = 0; i < menu.getItemCount(); i++) {
			//System.out.println("try get menu item @ " + i);
			JMenuItem item = menu.getItem(i);
			if (item == null) continue; // happens on seperators
			if (item.getText() == name)
				return i;
		}
		return -1;
	}

	/**
	 * Experimental way of getting tools menu, not working at the moment
	 * @param menuBar
	 * @param name
	 * @return
	 */
	public static int GetMenuBarItemIndex(JMenuBar menuBar, String name) {
		//System.out.println("try get menu: " + name);
		MenuElement[] items = menuBar.getSubElements();
		for ( int i = 0; i < items.length; i++) {
			//System.out.println("try get menu item @ " + i);
			JMenu menu = (JMenu)items[i];
			if (menu == null) continue; // happens on seperators
			if (menu.getText() == name)
				return i;
		}
		return -1;
	}

	
}
