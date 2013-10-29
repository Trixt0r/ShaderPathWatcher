package trixt0r.test;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

public class Main {
	
	public static void main(String[] args) {
		LwjglApplicationConfiguration cfg =  new LwjglApplicationConfiguration();
		cfg.title = "Spriter - demo";
		cfg.useGL20 = true;
		cfg.width = 640;
		cfg.height = 480;
		ShaderWatcherTest demo = new ShaderWatcherTest();
		new LwjglApplication(demo, cfg);
	}

}
