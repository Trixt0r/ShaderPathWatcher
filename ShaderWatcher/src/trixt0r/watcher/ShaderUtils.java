package trixt0r.watcher;

import java.nio.FloatBuffer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.BufferUtils;

import static com.badlogic.gdx.graphics.GL20.*;

public class ShaderUtils {

	
	public static FloatBuffer getFloatBufferForType(int type){
		switch(type){
		case GL_FLOAT_VEC2: return BufferUtils.newFloatBuffer(2);
		case GL_FLOAT_VEC3: return BufferUtils.newFloatBuffer(3);
		case GL_FLOAT_VEC4: return BufferUtils.newFloatBuffer(4);
		case GL_FLOAT_MAT2: return BufferUtils.newFloatBuffer(4);
		case GL_FLOAT_MAT3: return BufferUtils.newFloatBuffer(9);
		case GL_FLOAT_MAT4: return BufferUtils.newFloatBuffer(16);
		default: if(type == GL_TEXTURE)	System.out.println("Texture found!");
				return BufferUtils.newFloatBuffer(1);
		}
	}
	
	public static void setUniformf(int location, int type, FloatBuffer buffer){
		switch(type){
		case GL_FLOAT_VEC2: Gdx.gl20.glUniform2fv(location, buffer.capacity(), buffer); break;
		case GL_FLOAT_VEC3: Gdx.gl20.glUniform3fv(location, buffer.capacity(), buffer); break;
		case GL_FLOAT_VEC4: Gdx.gl20.glUniform4fv(location, buffer.capacity(), buffer); break;
		case GL_FLOAT_MAT2: Gdx.gl20.glUniformMatrix2fv(location, buffer.capacity(), false, buffer); break;
		case GL_FLOAT_MAT3: Gdx.gl20.glUniformMatrix3fv(location, buffer.capacity(), false, buffer); break;
		case GL_FLOAT_MAT4: Gdx.gl20.glUniformMatrix4fv(location, buffer.capacity(), false, buffer); break;
		case GL_TEXTURE_2D: System.out.println("Found bound texture!"); break;
		default: Gdx.gl20.glUniform1fv(location, buffer.capacity(), buffer); break;
		}
	}
	
	public static void setUniformf(int location, int type, float[] buffer){
		setUniformf(location, type, FloatBuffer.wrap(buffer));
	}
}
