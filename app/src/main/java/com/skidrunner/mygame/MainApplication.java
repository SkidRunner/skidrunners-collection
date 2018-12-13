package com.skidrunner.mygame;

import com.jme3.app.SimpleApplication;
import com.skidrunner.mygame.dodger.DodgerAppState;

public class MainApplication extends SimpleApplication {

	@Override
	public void simpleInitApp() {
		flyCam.setEnabled(false);
		stateManager.attach(new DodgerAppState());
	}
	
}
