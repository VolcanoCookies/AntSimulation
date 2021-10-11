#ifdef GL_ES
precision mediump float;
#endif

uniform float u_width;
uniform float u_height;

uniform float x_center;
uniform float y_center;

varying vec4 v_col;

void main() {

	float pos_x = gl_FragCoord.x / u_width;
	float pos_y = gl_FragCoord.y / u_height;

	float distance_square = pow(pos_x, 2.0) + pow(pos_y, 2.0);

	gl_FragColor = vec4(pos_x, pos_y, sin(pos_x) / cos(pos_y), 1);

	//gl_FragColor = gl_FragCoord / mult;

	//vec3 color = vec3(u_time, gl_FragCoord.y, 0.0);
	//gl_FragColor = vec4(color, 1.0);
	//gl_FragColor =  gl_FragCoord/ 50f;
}