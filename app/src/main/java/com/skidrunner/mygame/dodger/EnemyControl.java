package com.skidrunner.mygame.dodger;
import com.jme3.scene.control.AbstractControl;
import com.jme3.renderer.ViewPort;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Spatial;
import com.jme3.math.Vector3f;
import com.jme3.math.FastMath;

public class EnemyControl extends SeekerControl {
	
	private boolean exiting;
	private final Vector3f origin = new Vector3f(0.0f, 100.0f, 0.0f);
	private final Vector3f targetMaximum = new Vector3f(0.0f, 0.0f, 0.0f);
	private final Vector3f targetMinimum = new Vector3f(0.0f, 0.0f, 0.0f);
	
	public Vector3f getOrigin() {
		return origin;
	}
	
	public Vector3f getTargetMaximum() {
		return targetMaximum;
	}
	
	public Vector3f getTargetMinimum() {
		return targetMinimum;
	}
	
	@Override
	void onTargetReached() {
		Vector3f target = getTarget();
		if(!exiting) {
			exiting = true;
			target.addLocal(0.0f, 0.0f, -50.0f);
		} else {
			reset();
		}
	}
	
	public void reset() {
		Vector3f target = getTarget();
		spatial.setLocalTranslation(origin);
		target.set(targetMaximum);
		target.subtractLocal(targetMinimum);
		target.multLocal(FastMath.rand.nextFloat(), FastMath.rand.nextFloat(), 1.0f);
		target.addLocal(targetMinimum);
		exiting = false;
	}
	
}
