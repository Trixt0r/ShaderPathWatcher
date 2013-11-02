package trixt0r.test;

import trixt0r.watcher.ShaderManager;
import trixt0r.watcher.ShaderWatcher;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;

public class ShaderWatcherTest implements ApplicationListener{
	
	Texture tex;
	SpriteBatch batch;
	OrthographicCamera cam;
	ShaderProgram s;
	ShaderManager manager;
	float time = 0;

	@Override
	public void create() {
		ShaderProgram.pedantic = false;
		this.tex = new Texture(Gdx.files.absolute("assets/libgdx.png"));
		this.cam = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		this.batch = new SpriteBatch();
		this.s = new ShaderProgram(Gdx.files.absolute("assets/batch.vert"),Gdx.files.absolute("assets/batch.frag"));
		this.batch.setShader(s);
		this.manager = new ShaderManager();
		this.manager.addShader("assets/batch.vert", "assets/batch.frag", this.s);
		new ShaderWatcher("assets", manager);
	}

	@Override
	public void resize(int width, int height) {
		this.cam.setToOrtho(false, width, height);
		this.cam.position.set(Gdx.graphics.getWidth()/2, Gdx.graphics.getHeight()/2, 0);
		this.cam.update();
		this.batch.setProjectionMatrix(this.cam.combined);
	}

	@Override
	public void render() {
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		Gdx.gl.glClearColor(1, 1, 1, 1);
		
		this.s.begin();
		this.s.setUniformf("time", time);
		this.s.end();
		time++;
		
		this.batch.begin();
			this.batch.draw(tex, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		this.batch.end();
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}

	@Override
	public void dispose() {
		this.tex.dispose();
		this.batch.dispose();
		this.manager.dispose();
	}

}
