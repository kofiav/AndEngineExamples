package org.andengine.examples;

import org.andengine.engine.camera.Camera;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.AutoParallaxBackground;
import org.andengine.entity.scene.background.ParallaxBackground.ParallaxEntity;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.util.FPSLogger;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.texture.region.TiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.ui.activity.SimpleBaseGameActivity;

import android.util.DisplayMetrics;

/**
 * (c) 2010 Nicolas Gramlich
 * (c) 2011 Zynga
 *
 * @author Nicolas Gramlich
 * @since 19:58:39 - 19.07.2010
 */
public class AutoParallaxBackgroundExample extends SimpleBaseGameActivity {
	// ===========================================================
	// Constants
	// ===========================================================

	private int CAMERA_WIDTH;
	private int CAMERA_HEIGHT;

	// ===========================================================
	// Fields
	// ===========================================================

	private BitmapTextureAtlas mBitmapTextureAtlas;
	private TiledTextureRegion mPlayerTextureRegion;
	private TiledTextureRegion mEnemyTextureRegion;

	private BitmapTextureAtlas mAutoParallaxBackgroundTexture;

	private ITextureRegion mParallaxLayerBack;
	private ITextureRegion mParallaxLayerMid;
	private ITextureRegion mParallaxLayerFront;

	// ===========================================================
	// Constructors
	// ===========================================================

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================

	@Override
	public EngineOptions onCreateEngineOptions() {
        final DisplayMetrics displayMetrics = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        CAMERA_WIDTH = displayMetrics.widthPixels;
        CAMERA_HEIGHT = displayMetrics.heightPixels;
		final Camera camera = new Camera(0, // x
				                         0, // y
				                         CAMERA_WIDTH, 
				                         CAMERA_HEIGHT
				                         );

		return new EngineOptions(true,                                                    // Full-screen
				                 ScreenOrientation.PORTRAIT_FIXED,                        // Aspect-ratio
				                 new RatioResolutionPolicy(CAMERA_WIDTH, CAMERA_HEIGHT),  // ??
				                 camera                                                   // Camera
				                 );
	}

	@Override
	public void onCreateResources() {
		/*
		 * Set path where images are located
		 */
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");
		
		this.mBitmapTextureAtlas = new BitmapTextureAtlas(this.getTextureManager(), // Texture manager
				                                          256,                      // Width of texture atlas
				                                          128,                      // Height of texture atlas
				                                          TextureOptions.BILINEAR   // Rendering options (TextureOptions.BILINEAR minimizes pixelation)
				                                          );
		this.mPlayerTextureRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(this.mBitmapTextureAtlas,  // The texture atlas
				                                                                                this,                      // The base context
				                                                                                "player.png",              // Sprite
				                                                                                0,                         // x offset
				                                                                                0,                         // y offset
				                                                                                3,                         // # of tile columns (in PNG file)
				                                                                                4                          // # of tile rows (in PNG file)
				                                                                                );
		
		this.mEnemyTextureRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(this.mBitmapTextureAtlas, 
				                                                                               this, 
				                                                                               "enemy.png", 
				                                                                               73,
				                                                                               0, 
				                                                                               3, 
				                                                                               4
				                                                                               );
		this.mBitmapTextureAtlas.load();

		this.mAutoParallaxBackgroundTexture = new BitmapTextureAtlas(this.getTextureManager(),   // Texture manager
				                                                     1024,                       // Width of texture atlas
				                                                     1024                        // Height of texture atlas
				                                                     );
		
		this.mParallaxLayerFront = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mAutoParallaxBackgroundTexture,     // Texture Atlas
				                                                                          this,                                    // Context
				                                                                          "parallax_background_layer_front.png",   // Sprite
				                                                                          0,                                       // x offset
				                                                                          0                                        // y offset
				                                                                          );
		
		this.mParallaxLayerBack = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mAutoParallaxBackgroundTexture, 
				                                                                         this, 
				                                                                         "parallax_background_layer_back.png", 
				                                                                         0, 
				                                                                         188
				                                                                         );
		
		this.mParallaxLayerMid = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mAutoParallaxBackgroundTexture, 
				                                                                        this, 
				                                                                        "parallax_background_layer_mid.png", 
				                                                                        0, 
				                                                                        669
				                                                                        );
		
		this.mAutoParallaxBackgroundTexture.load();
	}

	@Override
	public Scene onCreateScene() {
		this.mEngine.registerUpdateHandler(new FPSLogger());

		final Scene scene = new Scene();
		
		final AutoParallaxBackground autoParallaxBackground = new AutoParallaxBackground(0,    // R
				                                                                         0,    // G
				                                                                         0,    // B
				                                                                         5     // Parallax change per second
				                                                                         );
		
		final VertexBufferObjectManager vertexBufferObjectManager = this.getVertexBufferObjectManager();
		
		Sprite mParallaxLayerBackSprite = new Sprite(0,                                                     // x
				                                     CAMERA_HEIGHT - this.mParallaxLayerBack.getHeight(),   // y
				                                     this.mParallaxLayerBack,                               // Texture region
				                                     vertexBufferObjectManager                              // VertexBufferObjectManager
				                                     );
		
		ParallaxEntity mParallaxLayerBackEntity = new ParallaxEntity(0.0f,                      // Parallax factor
				                                                     mParallaxLayerBackSprite   // Sprite
				                                                     );
		
		autoParallaxBackground.attachParallaxEntity(mParallaxLayerBackEntity);
		
		Sprite mParallaxLayerMidSprite = new Sprite(0, 
				                                    80, 
				                                    this.mParallaxLayerMid, 
				                                    vertexBufferObjectManager
				                                    );
		
		ParallaxEntity mParallaxLayerMidEntity = new ParallaxEntity(-5.0f, 
				                                                    mParallaxLayerMidSprite
				                                                    );
		
		autoParallaxBackground.attachParallaxEntity(mParallaxLayerMidEntity);
		
		Sprite mParallaxLayerFront = new Sprite(0, 
				                                CAMERA_HEIGHT - this.mParallaxLayerFront.getHeight(), 
				                                this.mParallaxLayerFront, 
				                                vertexBufferObjectManager
				                                );
		
		ParallaxEntity mParallaxLayerFrontEntity = new ParallaxEntity(-10.0f, 
				                                                      mParallaxLayerFront
				                                                      );
		
		autoParallaxBackground.attachParallaxEntity(mParallaxLayerFrontEntity);
		
		scene.setBackground(autoParallaxBackground);

		/* Calculate the coordinates for the face, so its centered on the camera. */
		final float playerX = (CAMERA_WIDTH - this.mPlayerTextureRegion.getWidth()) / 2;
		final float playerY = CAMERA_HEIGHT - this.mPlayerTextureRegion.getHeight() - 5;

		/* Create two sprits and add it to the scene. */
		final AnimatedSprite player = new AnimatedSprite(playerX, playerY, this.mPlayerTextureRegion, vertexBufferObjectManager);
		player.setScaleCenterY(this.mPlayerTextureRegion.getHeight());
		player.setScale(2);
		player.animate(new long[]{200, 200, 200}, 3, 5, true);

		final AnimatedSprite enemy = new AnimatedSprite(playerX - 80, playerY, this.mEnemyTextureRegion, vertexBufferObjectManager);
		enemy.setScaleCenterY(this.mEnemyTextureRegion.getHeight());
		enemy.setScale(2);
		enemy.animate(new long[]{200, 200, 200}, 3, 5, true);

		scene.attachChild(player);
		scene.attachChild(enemy);

		return scene;
	}

	// ===========================================================
	// Methods
	// ===========================================================

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
