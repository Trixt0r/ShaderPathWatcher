package trixt0r.watcher;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.badlogic.gdx.utils.Array;

public class ShaderWatcher extends TimerTask {

	public static Array<String> vertShaderEndings = new Array<String>();
	public static Array<String> fragShaderEndings = new Array<String>();
	
	static{
		fragShaderEndings.add("frag");
		fragShaderEndings.add("f");
		fragShaderEndings.add("glfrag");
		fragShaderEndings.add("fragment");
		fragShaderEndings.add("fshader");
		fragShaderEndings.add("fsh");
	}
	static{
		vertShaderEndings.add("vert");
		vertShaderEndings.add("v");
		vertShaderEndings.add("glvert");
		vertShaderEndings.add("vertex");
		vertShaderEndings.add("vshader");
		vertShaderEndings.add("vsh");
	}
	
	private Path shadersPath;
	private String pathName;
	private WatchService watcher;
	private ShaderManager manager;
	//private GLDeferredRenderContext context;
	
	public ShaderWatcher(String path, ShaderManager manager){
		super();
		this.pathName = path;
		this.shadersPath = Paths.get(path);
        try {
			watcher = shadersPath.getFileSystem().newWatchService();
	        shadersPath.register(watcher, StandardWatchEventKinds.ENTRY_MODIFY);
		} catch (IOException e) {
			e.printStackTrace();
		}
        this.manager = manager;
        new Timer().scheduleAtFixedRate(this, 0, 1000);
	}

	@Override
	public void run() {
        try {
        	WatchKey key = watcher.take();
		    List<WatchEvent<?>> events = key.pollEvents();
		    for (WatchEvent<?> event : events) {
			   	String fileName = event.context().toString();
			   	ArrayList<SimpleEntry<String, String>> toPush = new ArrayList<SimpleEntry<String, String>>();
			   	boolean push = true;
		        if(isFragmentShaderFile(fileName)){
		        	String fragment = this.pathName+"/"+fileName;
		        	List<String> vertexes = manager.getVertexShadersFor(fragment);
		        	for(String vert: vertexes){
		        		SimpleEntry<String, String> e = new SimpleEntry<String, String>(vert, fragment);
		        		toPush.add(e);
		        	}
		        } else if(isVertexShaderFile(fileName)){
		        	String vertex = this.pathName+"/"+fileName;
		        	List<String> fragments = manager.getFragmentShadersFor(vertex);
		        	for(String frag: fragments){
		        		SimpleEntry<String, String> e = new SimpleEntry<String, String>(vertex, frag);
		        		toPush.add(e);
		        	}
		        } else push = false;
		        
		        if(push)
		        	for(SimpleEntry<String, String> entry: toPush)
		        		manager.pushShaderToReload(entry.getKey(), entry.getValue());
		    }
		    manager.reloadShaders();
		    key.reset();
		} catch (Exception e) {
		    System.out.println("Error: " + e.toString());
		}
	}
	
	private static String getExtension(String name){
        int dotIndex = name.lastIndexOf('.');
        if (dotIndex == -1) return "";
        return name.substring(dotIndex + 1);
	}
	
	private static boolean isFragmentShaderFile(String name){
		for(String ending: fragShaderEndings)
			if(getExtension(name).equalsIgnoreCase(ending)) return true;
		return false;
	}
	
	private static boolean isVertexShaderFile(String name){
		for(String ending: vertShaderEndings)
			if(getExtension(name).equalsIgnoreCase(ending)) return true;
		return false;
	}

}
