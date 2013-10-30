ShaderPathWatcher
=================

A simple library to watch specified directories, containing glsl shader files and reload them on a change
(i.e. when saving the shader file). This library is based on LibGDX.
But it should not be that hard to adapt it to any other library using Java and OpenGL.

Usage
-----

If you are loading your shaders, create a new ShaderManager object and add all loaded ShaderPrograms to this manager.
After creating all your shaders, create for every path, containing glsl shader files, a new ShaderWatcher object which
will watch the given path.
Here is an example:

```
ShaderProgram s = new ShaderProgram(Gdx.files.absolute("assets/batch.vert"),Gdx.files.absolute("assets/batch.frag"));
ShaderManager manager = new ShaderManager();
manager.addShader("assets/batch.vert", "assets/batch.frag", this.s);
new ShaderWatcher("assets", manager);
```
If you do not want to dispose your shader programs manually,
let the ShaderManager object do it for you with ´manager.dipose()´.

A ShaderWatcher will only look into one directory and not in the subdirectories. So if you
have more than one directory, conatining shader files, you have to create for each subdirectory one ShaderWatcher object.

There are already pre-defined vertex and fragment shader endings, if you want to add some, simply add those to the
corresponding static Array objects `ShaderWatcher.vertShaderEndings(".vertexEnding");` and
`(ShaderWatcher.fragShaderEndings(".fragmentEnding");`.
