package trixt0r.watcher;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Gdx;

import static com.badlogic.gdx.graphics.GL20.*;

import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.BufferUtils;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.ObjectIntMap;

public class ShaderManager implements Disposable{
	
	private ArrayMap<SimpleEntry<String, String>, ShaderProgram> shaders;
	private ArrayMap<SimpleEntry<String, String>, ShaderProgram> toReload;
	
	public ShaderManager(){
		this.shaders = new ArrayMap<SimpleEntry<String, String>, ShaderProgram>();
		this.toReload = new ArrayMap<SimpleEntry<String, String>, ShaderProgram>();
	}
	
	public void dispose(){
		for(SimpleEntry<String, String> e: this.shaders.keys())
			this.shaders.get(e).dispose();;
	}
	
	public void reloadShaders(){
		Gdx.app.postRunnable(new Runnable(){

			@Override
			public void run() {
				for(SimpleEntry<String, String> e: toReload.keys())
					reloadShader(e);
				toReload.clear();
			}
		});
	}
	
	@SuppressWarnings("unchecked")
	private void reloadShader(SimpleEntry<String, String> e){
		try {
			ShaderProgram shader = this.shaders.get(e);
			ShaderProgram newShader = new ShaderProgram(Gdx.files.absolute(e.getKey()), Gdx.files.absolute(e.getValue()));
			if(!newShader.isCompiled()){
				System.err.println("Could not compile shader:\n "+newShader.getLog());
				return;
			}
			Field vertexField = shader.getClass().getDeclaredField("vertexShaderHandle");
			Field fragmentField = shader.getClass().getDeclaredField("fragmentShaderHandle");
			Field programField = shader.getClass().getDeclaredField("program");
			
			Field vertSourceField = shader.getClass().getDeclaredField("vertexShaderSource");
			Field fragSourceField = shader.getClass().getDeclaredField("fragmentShaderSource");
			
			Field uniformsField = shader.getClass().getDeclaredField("uniforms");
			Field uniformTypesField = shader.getClass().getDeclaredField("uniformTypes");
			Field uniformSizesField = shader.getClass().getDeclaredField("uniformSizes");
			Field uniformNamesField = shader.getClass().getDeclaredField("uniformNames");
			
			Field attributesField = shader.getClass().getDeclaredField("attributes");
			Field attributeTypesField = shader.getClass().getDeclaredField("attributeTypes");
			Field attributeSizesField = shader.getClass().getDeclaredField("attributeSizes");
			Field attributeNamesField = shader.getClass().getDeclaredField("attributeNames");
			
			vertexField.setAccessible(true);
			fragmentField.setAccessible(true);
			programField.setAccessible(true);
			
			vertSourceField.setAccessible(true);
			fragSourceField.setAccessible(true);
			
			uniformsField.setAccessible(true);
			uniformTypesField.setAccessible(true);
			uniformSizesField.setAccessible(true);
			uniformNamesField.setAccessible(true);
			
			attributesField.setAccessible(true);
			attributeTypesField.setAccessible(true);
			attributeSizesField.setAccessible(true);
			attributeNamesField.setAccessible(true);
			
			Integer programHandle = (Integer) programField.get(shader);
			
			shader.begin();
			IntBuffer params = BufferUtils.newIntBuffer(1);
			Gdx.gl20.glGetProgramiv(programHandle, GL_ACTIVE_UNIFORMS, params);
			ArrayList<ShaderInfo> infos = new ArrayList<ShaderInfo>();
			for(int i = 0; i< params.get(0); i++){
				IntBuffer size = BufferUtils.newIntBuffer(1);
				IntBuffer type = BufferUtils.newIntBuffer(1);
				ShaderInfo info = new ShaderInfo();
				Gdx.gl20.glGetActiveUniform(programHandle, i, size, type);
				info.type = type.get(0);
				FloatBuffer b = ShaderUtils.getFloatBufferForType(info.type);
				Gdx.gl20.glGetUniformfv(programHandle, i, b); //Load floating point values in current shader
				info.floats = b;
				infos.add(info);
				type.clear();
				size.clear();
			}
			params.clear();
			int[] texIds = new int[32];
			IntBuffer p = BufferUtils.newIntBuffer(16);
			for(int i = 0; i < texIds.length; i++){
				Gdx.gl20.glActiveTexture(GL_TEXTURE0+i);
				Gdx.gl20.glGetIntegerv(GL_TEXTURE_BINDING_2D, p);
				texIds[i] = p.get(0);
			}
			shader.end();
			//Dispose older shader
			Gdx.gl20.glUseProgram(0);
			Gdx.gl20.glDeleteShader((int) vertexField.get(shader));
			Gdx.gl20.glDeleteShader((int) fragmentField.get(shader));
			Gdx.gl20.glDeleteProgram((int) programField.get(shader));
			
			 //Pass old values to shader
			newShader.begin();
			int texIndex = 0;
			for(int i = 0; i < infos.size(); i++){
				if(infos.get(i).type == GL_SAMPLER_2D){
					Gdx.gl20.glActiveTexture(GL_TEXTURE0+texIndex);
					Gdx.gl20.glEnable(GL_TEXTURE_2D);
					Gdx.gl20.glBindTexture(GL_TEXTURE_2D, texIds[texIndex]);
					Gdx.gl20.glUniform1i(i, texIndex++);
				}
				else ShaderUtils.setUniformf(i, infos.get(i).type, infos.get(i).floats);
			}
			newShader.end();
			
			//Change gl handles
			vertexField.set(shader, (Integer) vertexField.get(newShader));
			fragmentField.set(shader, (Integer) fragmentField.get(newShader));
			programField.set(shader, (Integer) programField.get(newShader));
			vertSourceField.set(shader, (String) vertSourceField.get(newShader));
			fragSourceField.set(shader, (String) fragSourceField.get(newShader));
			uniformNamesField.set(shader, (String[]) uniformNamesField.get(newShader));
			attributeNamesField.set(shader, (String[]) attributeNamesField.get(newShader));
			
			this.copyFromTo((ObjectIntMap<String>) uniformsField.get(shader),
					(ObjectIntMap<String>) uniformsField.get(newShader));
			this.copyFromTo((ObjectIntMap<String>) uniformTypesField.get(shader),
					(ObjectIntMap<String>) uniformTypesField.get(newShader));
			this.copyFromTo((ObjectIntMap<String>) uniformSizesField.get(shader),
					(ObjectIntMap<String>) uniformSizesField.get(newShader));
			
			this.copyFromTo((ObjectIntMap<String>) attributesField.get(shader),
					(ObjectIntMap<String>) attributesField.get(newShader));
			this.copyFromTo((ObjectIntMap<String>) attributeTypesField.get(shader),
					(ObjectIntMap<String>) attributeTypesField.get(newShader));
			this.copyFromTo((ObjectIntMap<String>) attributeSizesField.get(shader),
					(ObjectIntMap<String>) attributeSizesField.get(newShader));
			
			vertexField.setAccessible(false);
			fragmentField.setAccessible(false);
			programField.setAccessible(false);
			
			vertSourceField.setAccessible(false);
			fragSourceField.setAccessible(false);
			
			uniformsField.setAccessible(false);
			uniformTypesField.setAccessible(false);
			uniformSizesField.setAccessible(false);
			uniformNamesField.setAccessible(false);
			
			attributesField.setAccessible(false);
			attributeTypesField.setAccessible(false);
			attributeSizesField.setAccessible(false);
			attributeNamesField.setAccessible(false);
		}
		catch(Exception e1) {
			e1.printStackTrace();
		}
	}
	
	private void copyFromTo(ObjectIntMap<String> from, ObjectIntMap<String> to){
		for(String s: to.keys())
			from.put(s, to.get(s, 0));
	}
	
	public void pushShaderToReload(String vertex, String fragment){
		SimpleEntry<String, String> e = this.getEntryFor(vertex, fragment);
		this.toReload.put(e, this.shaders.get(e));
	}
	
	public void addShader(String vertex, String fragment, ShaderProgram shader){
		SimpleEntry<String, String> e = this.getEntryFor(vertex, fragment);
		this.shaders.put((e == null) ? new SimpleEntry<String, String>(vertex, fragment) :  e, shader);
	}
	
	private SimpleEntry<String, String> getEntryFor(String vertex, String fragment){
		for(SimpleEntry<String, String> e: shaders.keys())
			if(e.getKey().equals(vertex) && e.getValue().equals(fragment)) return e;
		return null;
	}
	
	public String getVertexShaderFor(String fragment){
		List<String> vertexes = this.getVertexShadersFor(fragment);
		return (vertexes.size() > 0) ?  vertexes.get(0): null;
	}
	
	public List<String> getVertexShadersFor(String fragment){
		List<String> vertexes = new ArrayList<String>();
		for(SimpleEntry<String, String> e: this.shaders.keys())
			if(e.getValue().equals(fragment)) 
				vertexes.add(e.getKey());
		return vertexes;
	}
	
	public String getFragmentShaderFor(String vertex){
		List<String> fragments = this.getFragmentShadersFor(vertex);
		return (fragments.size() > 0) ?  fragments.get(0): null;
	}
	
	public List<String> getFragmentShadersFor(String vertex){
		List<String> fragments = new ArrayList<String>();
		for(SimpleEntry<String, String> e: this.shaders.keys())
			if(e.getKey().equals(vertex))
				fragments.add(e.getValue());
		return fragments;
	}
	
	private static class ShaderInfo{
		FloatBuffer floats;
		int type;
	}

}
