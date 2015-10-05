precision mediump float;       	// Set the default precision to medium. We don't need as high of a 
								// precision in the fragment shader.
// light
uniform vec4 lightPos;
uniform vec4 lightColor;

// material
uniform vec4 matAmbient;
uniform vec4 matDiffuse;
uniform vec4 matSpecular;


// eye pos
uniform vec3 eyePos;

// from vertex s
varying vec3 lightDir, eyeVec;

varying vec3 v_Normal;

// The entry point for our fragment shader.
void main()                    		
{

    vec4 b = lightColor;
    vec4 c = matAmbient;
    vec4 d = matDiffuse;
    vec4 e = matSpecular;
    vec3 g = eyePos;

    vec3 N = normalize(v_Normal);
    vec3 E = normalize(eyeVec);

    vec3 L = normalize(lightDir);
    vec3 reflectV = reflect(-L, N);

    vec4 ambientTerm = matAmbient * lightColor;
    vec4 diffuseTerm = matDiffuse * max(dot(N, L), 0.0);
    vec4 specularTerm = matSpecular * max(dot(reflectV, E), 1.0);

	// Multiply the color by the diffuse illumination level and texture value to get final output color.
    //gl_FragColor = (diffuse * texture2D(u_Texture, v_TexCoordinate));
    gl_FragColor =  ambientTerm * diffuseTerm ;
  }                                                                     	

