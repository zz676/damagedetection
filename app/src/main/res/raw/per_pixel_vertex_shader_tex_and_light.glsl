uniform mat4 u_MVPMatrix;		// A constant representing the combined model/view/projection matrix.      		       
uniform mat4 u_MVMatrix;

attribute vec4 a_Position;		// Per-vertex position information we will pass in.
attribute vec3 a_Normal;		// Per-vertex normal information we will pass in.
//attribute vec2 a_TexCoordinate; // Per-vertex texture coordinate information we will pass in.

// material
uniform vec4 matAmbient;
uniform vec4 matDiffuse;
uniform vec4 matSpecular;

// lighting
uniform vec4 lightPos;
uniform vec4 lightColor;

// normals to pass on
varying vec3 lightDir, eyeVec;

varying vec3 v_Position;		// This will be passed into the fragment shader.
varying vec3 v_Normal;			// This will be passed into the fragment shader.
//varying vec2 v_TexCoordinate;   // This will be passed into the fragment shader.

		  
// The entry point for our vertex shader.  
void main()                                                 	
{

    v_Position = vec3(u_MVMatrix * a_Position);
    //v_TexCoordinate = a_TexCoordinate;
    v_Normal = vec3(u_MVMatrix * vec4(a_Normal, 1.0));

	// gl_Position is a special variable used to store the final position.
	// Multiply the vertex by the matrix to get the final point in normalized screen coordinates.

    vec4 position = u_MVPMatrix * a_Position;
    lightDir = lightPos.xyz - position.xyz;
    eyeVec = -position.xyz;

    gl_Position = u_MVPMatrix * a_Position;
}                                                          