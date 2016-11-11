/*
 * Sudokuki - essential sudoku game
 * Copyright (C) 2007-2016 Sylvain Vedrenne
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.jankenpoi.sudokuki.ui.text;

import java.io.Console;

import net.jankenpoi.sudokuki.model.GridChangedEvent;
import net.jankenpoi.sudokuki.model.GridModel;
import net.jankenpoi.sudokuki.view.GridView;

public class TextView extends GridView {

	public TextView(GridModel model) {
		super(model);
	}

	private Console console;
	private Thread th;

	@Override
	public void display() {
		System.out.println("TextView.display() Thread:"+Thread.currentThread());
		for (int li=0; li<9; li++) {
			for (int co=0; co<9; co++) {
				System.out.print(getValueAt(li, co));
			}
			System.out.println();
		}

		if (console == null) {
			System.out.println("TextView.display() Thread:"+Thread.currentThread());
			console = System.console();
		}
		if (th == null) {
			th = new Thread(new Runnable() {

				public void run() {
					while (true) {
						System.out.println("TextView.display() Thread:"+Thread.currentThread());
						System.out.println("TextView.display() console:"+console);
						if (console != null) {
							String str = console.readLine("A string: ");
							System.out.println("TextView.display() the string:"+str);
						}
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
			});
			th.start();
		}
	}

	public void gridChanged(GridChangedEvent event) {
		System.out.println("TextView.gridChanged()");
		display();
	}
	
	@Override
	public void close() {
		// TODO Auto-generated method stub

	}

	public void gridComplete() {
		// TODO Auto-generated method stub
		
	}

	public void gridResolved() {
		// TODO Auto-generated method stub
		
	}

}
