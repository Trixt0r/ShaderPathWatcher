#ifdef GL_ES
#define LOWP lowp
precision mediump float;
#else
#define LOWP
#endif 
varying LOWP vec4 v_color;
varying vec2 v_texCoords;
uniform sampler2D u_texture;
uniform float time;

//Standard
/*void main()
{
	gl_FragColor = v_color * texture2D(u_texture, v_texCoords);
}*/
//Waves
void main()
{
	vec2 t = vec2(v_texCoords.x+cos(v_texCoords.y+time*0.1)*.15,
					v_texCoords.y+sin(v_texCoords.x+time*0.1)*0.25);
	gl_FragColor = v_color * texture2D(u_texture, t); 
}
//Shockwaves
/*const float xA = 5.5, xB = 4.0;
const float xfsin = 0.5, xfcos = 0.5;
const float xxAw = 1.0, xxBw = 1.0, xyAw = 1.0, xyBw = 1.0;

const float yA = 5.5, yB = 2.25;
const float yfsin = 0.5, yfcos = 0.5;
const float yxAw = 1.0, yxBw = 1.0, yyAw = 1.0, yyBw = 1.0;
void main(void)
{
  vec2 v = vec2(v_texCoords);
  vec2 rip = vec2(0.0);
  float t = 0.15;
  float r = mod(time*.01, 1.0 + t);
  float d = length(vec2(.5,.5)-v);
  float di = r-t-d;
  float dj = r-d;
  rip.x = cos(v.x*xxAw + v.y*xyAw+ time*xfsin)*xA * sin(v.y*xyBw + v.x*xxBw + time*xfcos)*xB;
  rip.y += sin(v.x*yxAw + v.y*yyAw + time *yfsin)*yA * cos(v.y*yyBw + v.x*yxBw + time*yfcos)*yB;
  v += rip*(min(.0, di)*max(.0, dj));
  v.x = clamp(v.x, 0.01, .99);
  v.y = clamp(v.y, 0.01, .99);
  gl_FragColor = texture2D(u_texture, v) *max(1.0,-100.0*max(.0, dj)* min(.0,di) );
}*/