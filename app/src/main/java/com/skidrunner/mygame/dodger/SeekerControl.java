package com.skidrunner.mygame.dodger;
import com.jme3.math.Vector3f;
import com.jme3.scene.control.AbstractControl;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;

public abstract class SeekerControl extends AbstractControl {

	private float speed = 12;

	private final Vector3f target;
	private final Vector3f movement;

	public SeekerControl() {
		target = new Vector3f();
		movement = new Vector3f();
	}

	public float getSpeed() {
		return speed;
	}

	public void setSpeed(float speed) {
		this.speed = speed;
	}

	public Vector3f getTarget() {
		return target;
	}

	@Override
	protected void controlUpdate(float time) {
		Vector3f location = spatial.getLocalTranslation();
		movement.set(target)
			.subtractLocal(location);
		float distance = movement.length();
		
		if(distance > 0) {
			if(distance < (speed * time)) {
				spatial.setLocalTranslation(location);
				onTargetReached();
			} else {
				movement.normalizeLocal()
					.multLocal(speed * time);
				spatial.move(movement);
			}
		}
	}

	@Override
	protected void controlRender(RenderManager manager, ViewPort viewPort) {
		// TODO: Implement this method
	}

	abstract void onTargetReached();
	
	
}
