package com.skidrunner.mygame.dodger;

import com.jme3.app.Application;
import com.jme3.app.state.BaseAppState;
import com.jme3.asset.AssetManager;
import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.input.InputManager;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Plane;
import com.jme3.math.Ray;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.debug.WireBox;
import com.jme3.scene.shape.Box;
import java.util.Vector;

/**
 * 
 */
public class DodgerAppState extends BaseAppState implements ActionListener {

	private InputManager inputManager;
	private Camera camera;
	private boolean isCursorDown;
	private Vector2f cursorPosition;
	private Vector3f cursorLocation;
	private Ray cursorRay;
	private Plane cursorPlane;
	private Node scene;
	private Node screen;
	private Spatial player;
	private Vector<Spatial> enemies;
	private Spatial playerTarget;
	private BitmapText scoreText;
	private BitmapText highScoreText;
	private float score;
	private int highScore;
	private float resetTimer;

	@Override
	protected void initialize(Application application) {
		AssetManager assetManager = application.getAssetManager();
		inputManager = application.getInputManager();
		camera = application.getCamera();
		BitmapFont font = assetManager.loadFont("Interface/Fonts/Default.fnt");
		cursorPosition = new Vector2f();
		cursorLocation = new Vector3f();
		cursorRay = new Ray();
		cursorPlane = new Plane();
		cursorPlane.setOriginNormal(Vector3f.ZERO, Vector3f.UNIT_Z);
		scene = new Node("Dodger Scene");
		DirectionalLight sun = new DirectionalLight();
		sun.setColor(new ColorRGBA(1.0f, 1.0f, 1.0f, 1.0f));
		sun.setDirection(new Vector3f(-2, -2, 1).normalizeLocal());
		scene.addLight(sun);
		Node playerNode = new Node("Player");
		Material playerMaterial = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
		playerMaterial.setBoolean("UseMaterialColors", true);
		playerMaterial.setColor("Diffuse", new ColorRGBA(0.0f, 0.0f, 0.8f, 1.0f));
		playerMaterial.setColor("Specular", new ColorRGBA(0.8f, 0.8f, 1.0f, 1.0f));
		playerMaterial.setFloat("Shininess", 64.0f);
		Geometry playerGeometry = new Geometry("Player", new Box(1.0f, 1.0f, 1.0f));
		playerGeometry.setMaterial(playerMaterial);
		playerNode.attachChild(playerGeometry);
		PlayerControl playerControl = new PlayerControl();
		playerControl.setSpeed(48);
		playerNode.addControl(new PlayerControl());
		scene.attachChild(playerNode);
		Node enemyNode = new Node("Enemy");
		Material enemyMaterial = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
		enemyMaterial.setBoolean("UseMaterialColors", true);
		enemyMaterial.setColor("Diffuse", new ColorRGBA(0.0f, 0.8f, 0.0f, 1.0f));
		enemyMaterial.setColor("Specular", new ColorRGBA(0.8f, 1.0f, 0.8f, 1.0f));
		enemyMaterial.setFloat("Shininess", 64.0f);
		Geometry enemyGeometry = new Geometry("Enemy", new Box(1.0f, 1.0f, 1.0f));
		enemyGeometry.setMaterial(enemyMaterial);
		enemyNode.attachChild(enemyGeometry);
		Node markerNode = new Node("Marker");
		Material markerMaterial = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
		markerMaterial.setColor("Color", new ColorRGBA(1.0f, 1.0f, 1.0f, 1.0f));
		Geometry markerGeometry = new Geometry("Marker", new WireBox(1.0f, 1.0f, 1.0f));
		markerGeometry.setMaterial(markerMaterial);
		markerNode.attachChild(markerGeometry);
		scene.attachChild(markerNode);
		player = playerNode;
		playerTarget = markerNode;
		enemies = new Vector<>();
		for(int i = 0; i < 25; i++) {
			Spatial enemy = enemyNode.clone();
			EnemyControl enemyControl =new EnemyControl();
			enemyControl.setSpeed(50 + (FastMath.rand.nextFloat() * 50));
			enemy.addControl(enemyControl);
			enemies.add(enemy);
			scene.attachChild(enemy);
		}
		screen = new Node("Dodger Screen");
		screen.setQueueBucket(RenderQueue.Bucket.Gui);
		scoreText = font.createLabel("Score");
		screen.attachChild(scoreText);
		highScoreText = font.createLabel("High Score");
		screen.attachChild(highScoreText);
		inputManager.addMapping("Cursor", new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
		inputManager.addListener(this, "Cursor");
	}

	@Override
	protected void cleanup(Application application) {
	}

	@Override
	protected void onEnable() {
		Application application = getApplication();
		camera = application.getCamera();
		float textSize;
		if(camera.getWidth() < camera.getHeight()) {
			textSize = 32 * (camera.getWidth() / 544.0f);
		} else {
			textSize = 32 * (camera.getHeight() / 544.0f);
		}
		scoreText.setSize(textSize);
		highScoreText.setSize(textSize);
		reset();
		application.getViewPort().attachScene(scene);
		application.getGuiViewPort().attachScene(screen);
	}

	@Override
	protected void onDisable() {
		Application application = getApplication();
		application.getViewPort().detachScene(scene);
		application.getGuiViewPort().detachScene(screen);
	}

	@Override
	public void onAction(String name, boolean isPressed, float p3) {
		if(!isEnabled()) {
			return;
		}
		if("Cursor".equals(name)) {
			isCursorDown = isPressed;
		}
	}

	@Override
	public void update(float time) {
		score += (time * 100);
		if(((int)(score)) > highScore) {
			highScore = ((int)(score));
		}
		if(resetTimer < 0) {
			scoreText.setText(String.format("%09d" , ((int)(score))));
			scoreText.setLocalTranslation((camera.getWidth() - scoreText.getLineWidth()) * 0.5f, camera.getHeight(), 0.0f);
			highScoreText.setText(String.format("%09d" , highScore));
			highScoreText.setLocalTranslation((camera.getWidth() - highScoreText.getLineWidth()) * 0.5f, camera.getHeight() - (scoreText.getLineHeight() * 1.5f), 0.0f);
			updateInput(time);
			player.getControl(PlayerControl.class)
				.getTarget().set(cursorLocation.add(0.0f, 2.0f, 0.0f));
			CollisionResults results = new CollisionResults();
			if(0 <= scene.collideWith(player.getWorldBound(), results)) {
				for(CollisionResult result : results) {
					if("Enemy".equals(result.getGeometry().getParent().getName())) {
						((Geometry)(((Node)(player)).getChild("Player")))
							.getMaterial().setColor("Diffuse", ColorRGBA.Red);
						resetTimer = 5;
						break;
					}
				}
			}
			scene.updateLogicalState(time);
			screen.updateLogicalState(time);
		} else {
			resetTimer -= time;
			if(resetTimer < 0) {
				reset();	
			}
		}
		scene.updateGeometricState();
		screen.updateGeometricState();
	}

	private void updateInput(float time) {
		if(!isCursorDown) {
			return;
		}
		cursorPosition.set(inputManager.getCursorPosition());
		camera.getWorldCoordinates(cursorPosition, 0, cursorRay.origin);
		camera.getWorldCoordinates(cursorPosition, 1, cursorRay.direction)
			.subtractLocal(cursorRay.origin)
			.normalizeLocal();
		if(cursorRay.intersectsWherePlane(cursorPlane, cursorLocation)) {
			playerTarget.setLocalTranslation(cursorLocation);
		}
	}

	private void reset() {
		camera.setLocation(new Vector3f(0.0f, 0.0f, (-10.0f) * FastMath.tan(FastMath.DEG_TO_RAD * 67.5f)));
		camera.lookAtDirection(Vector3f.ZERO, Vector3f.UNIT_Y);
		Vector3f enemyTargetMinimum = new Vector3f(-10 * ((float)(camera.getWidth())) / ((float)(camera.getHeight())), -10, camera.getFrustumNear())
			.addLocal(camera.getLocation());
		Vector3f enemyTargetMaximum = new Vector3f(10 * ((float)(camera.getWidth())) / ((float)(camera.getHeight())), 10, camera.getFrustumNear())
			.addLocal(camera.getLocation());
		Vector3f enemyOrigin = new Vector3f(0.0f, 0.0f, camera.getFrustumFar())
			.addLocal(camera.getLocation());
		cursorLocation.set(0, 0, 0);
		player.setLocalTranslation(0, 0, 0);
		playerTarget.setLocalTranslation(0, 0, 0);
		Vector3f offset = new Vector3f();
		for(Spatial enemy : enemies) {
			EnemyControl control = enemy.getControl(EnemyControl.class);
			control.getOrigin().set(enemyOrigin);
			control.getTargetMinimum().set(enemyTargetMinimum);
			control.getTargetMaximum().set(enemyTargetMaximum);
			control.reset();
			offset.set(enemyOrigin).interpolateLocal(control.getTarget(), FastMath.rand.nextFloat());
			enemy.setLocalTranslation(offset);
		}
		((Geometry)(((Node)(player)).getChild("Player")))
			.getMaterial().setColor("Diffuse", ColorRGBA.Blue);
		score = 0;
	}

}
