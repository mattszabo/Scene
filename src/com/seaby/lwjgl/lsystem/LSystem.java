package com.seaby.lwjgl.lsystem;

import java.util.Random;
import java.util.Stack;
import java.util.Vector;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.Sphere;

/**
 * http://homepages.cs.ncl.ac.uk/j.d.hook/lsystems.html
 */


/***********
 * LSYSTEM										
 *
 * Copyright (c) 2010, Jonathan David Hook (j.d.hook@ncl.ac.uk)
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. All advertising materials mentioning features or use of this software
 *    must display the following acknowledgement:
 *    This product includes software developed by the <organization>.
 * 4. Neither the name of the <organization> nor the
 *    names of its contributors may be used to endorse or promote products
 *    derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY <COPYRIGHT HOLDER> ''AS IS'' AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

/*************
 * CONSTANTS *
 *************/
public class LSystem
{
	private static final float DEFAULT_FORWARD_LENGTH = 0.006f;
	private static final float DEFAULT_SPHERE_RADIUS = 0.015f;
	private static final float DEFAULT_TURN_VALUE = 35.0f;
	private static final float DEFAULT_VARIATION = 5.0f; 
	private static final int DEFAULT_ITERATIONS = 3;
	
	private static final char SYMBOL_FORWARD_F = 'F';
	private static final char SYMBOL_FORWARD_G = 'G';
	private static final char SYMBOL_FORWARD_NO_DRAW_F = 'f';
	private static final char SYMBOL_FORWARD_NO_DRAW_G = 'g';
	private static final char SYMBOL_PITCH_DOWN = '&';
	private static final char SYMBOL_PITCH_UP = '%';
	private static final char SYMBOL_POP_MATRIX = ']';
	private static final char SYMBOL_PUSH_MATRIX = '[';
	private static final char SYMBOL_ROLL_LEFT = '/';
	private static final char SYMBOL_ROLL_RIGHT = '\\';
	private static final char SYMBOL_SPHERE = '@';
	private static final char SYMBOL_TURN_180 = '|';
	private static final char SYMBOL_TURN_LEFT = '+';
	private static final char SYMBOL_TURN_RIGHT = '-';

	String axiom;
	boolean dirty;
	int iterations;
	float forwardLength; 
	Stack<Float> matrixStack;
	Vector<ReproductionRule> rules;
	String result;
	float sphereRadius;
	float turnValue;
	float variation;
	
/****************
 * CONSTRUCTORS *
 ****************/
 public void LSystem()
{
	this.axiom = "";
	this.iterations = DEFAULT_ITERATIONS;
	this.forwardLength = DEFAULT_FORWARD_LENGTH;
	this.turnValue = DEFAULT_TURN_VALUE;
	this.sphereRadius = DEFAULT_SPHERE_RADIUS;
	this.variation = DEFAULT_VARIATION;
	dirty = true;
}

public LSystem(String axiom, 
				   Vector<ReproductionRule> rules, 
				   int iterations, 
				   float forwardLength, 
				   float turnValue, 
				   float sphereRadius,
				   float variation)
{
	this.axiom = axiom;
	this.iterations = iterations;
	this.rules = rules;
	this.forwardLength = forwardLength;
	this.turnValue = turnValue;
	this.sphereRadius = sphereRadius;
	this.variation = variation;
	dirty = true;
}

//~LSystem(void)
//{
//	eraseStack();
//}

/***************************
 * PUBLIC MEMBER FUNCTIONS *
 ***************************/
public void addRule(ReproductionRule r)
{
//	rules.push_back(r);
	rules.add(r);
}

public String getResult()
{
	return result;
}

public void render()
{
	if(dirty)
	{
		result = generateResult(axiom, 0);
		dirty = false;
	}
	
	GL11.glMatrixMode(GL11.GL_MODELVIEW);
	GL11.glPushMatrix();

	initStack();

	for(int i = 0; i < result.length(); i++)
	{
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		GL11.glPushMatrix();


//		GL11.glMultMatrixf(matrixStack.top());
		//GL11.glMultMatrix(matrixStack.); //mine
		
		float v;
		char[] c = result.toCharArray();
		switch(c[i])
		{
			case SYMBOL_FORWARD_F:
			case SYMBOL_FORWARD_G:
				drawLine(forwardLength);
				translate(0.0f, forwardLength, 0.0f);
				break;
			case SYMBOL_FORWARD_NO_DRAW_F:
			case SYMBOL_FORWARD_NO_DRAW_G:
				translate(0.0f, forwardLength, 0.0f);
				break;
			case SYMBOL_PITCH_DOWN:
				v = vary(turnValue);
				rotate(v, 0.0f, 1.0f, 0.0f);
				break;
			case SYMBOL_PITCH_UP:
				v = vary(turnValue);
				rotate(v, 0.0f, 1.0f, 0.0f);
				break;
			case SYMBOL_POP_MATRIX:
				popMatrix();
				break;
			case SYMBOL_PUSH_MATRIX:
				pushMatrix();
				break;
			case SYMBOL_ROLL_LEFT:
				rotate(turnValue, 1.0f, 0.0f, 0.0f);
				break;
			case SYMBOL_ROLL_RIGHT:
				rotate(-turnValue, 1.0f, 0.0f, 0.0f);
				break;
			case SYMBOL_SPHERE:
				drawSphere(sphereRadius);
				break;
			case SYMBOL_TURN_180:
				rotate(180.0f, 0.0f, 0.0f, 0.0f);
			case SYMBOL_TURN_LEFT:
				rotate(-turnValue, 0.0f, 0.0f, 1.0f);
				break;
			case SYMBOL_TURN_RIGHT:
				rotate(turnValue, 0.0f, 0.0f, 1.0f);
				break;
		}
		
		GL11.glPopMatrix();
	}

	GL11.glPopMatrix();
	
}

public void setAxiom(String axiom)
{
	this.axiom = axiom;
	dirty = true;
}

public void setForwardLength(float forwardLength)
{
	this.forwardLength = forwardLength;
}

public void setIterations(int iterations)
{
	this.iterations = iterations;
	dirty = true;
}

public void setSphereRadius(float sphereRadius)
{
	this.sphereRadius = sphereRadius;
}

public void setTurnValue(float turnValue)
{
	this.turnValue = turnValue;
}

public void setVariation(float variation)
{
	this.variation = variation;
}

/******************************
 * PROTECTED MEMBER FUNCTIONS *
 ******************************/
public float getNoise()
{
	Random r = new Random();
	float f = r.nextFloat()*100.0f;
	return f;
//	return (float)rand() / (float)RAND_MAX; 
}

public void drawLine(float length)
{
	int lineDl = -1;
	if(lineDl == -1)
	{
		lineDl = GL11.glGenLists(1);
		GL11.glNewList(lineDl, GL11.GL_COMPILE);

		GL11.glBegin(GL11.GL_LINE_STRIP);
			GL11.glVertex3f(0.0f, 0.0f, 0.0f);
			GL11.glVertex3f(0.0f, 1.0f, 0.0f);
		GL11.glEnd();

		GL11.glEndList();
	}

	GL11.glMatrixMode(GL11.GL_MODELVIEW);
	GL11.glPushMatrix();
	GL11.glScalef(length, length, length);
	GL11.glCallList(lineDl);
	GL11.glPopMatrix();
}

public void drawSphere(float radius)
{
	int sphereDl = -1;
	if(sphereDl == -1)
	{
		sphereDl = GL11.glGenLists(1);

		GL11.glNewList(sphereDl, GL11.GL_COMPILE);
		
//		GLU.GLUquadricObj q = GLU.gluNewQuadric();
		Sphere sphere = new Sphere();
//		gluSphere(q, 1.0f, 50, 50);
		sphere.draw(1.0f, 50, 50);
//		gluDeleteQuadric(q);

		GL11.glEndList();
	}
	GL11.glScalef(radius, radius, radius);
	GL11.glCallList(sphereDl);
}

/****************************
 * PRIVATE MEMBER FUNCTIONS *
 ****************************/
//public void eraseStack()
//{
//	while(!matrixStack.empty())
//	{
//		GLfloat *top = matrixStack.top();
//		matrixStack.pop();
//		delete [] top;
//	}
//}

public String generateResult(String str, int count)
{
	char[] c = str.toCharArray();
	for(int i = 0; i < str.length(); i++)
	{
		for(int j = 0; j < rules.size(); j++)
		{
			if(c[i] == rules.get(j).from)
			{
				//str.replace(i, 1, rules.get(j).to); //theirs
				//replace pos i, 1 char, with rules[j].to //mine
				i += rules.get(j).to.length();
				break;
			}
		}
	}

	if(count < iterations) return generateResult(str, count + 1);
	else return str;
}

public void initStack()
{
	matrixStack.clear();

	Float[] matrix = new Float[16];
	GL11.glMatrixMode(GL11.GL_MODELVIEW);
	GL11.glPushMatrix();
	GL11.glLoadIdentity();
	//GL11.glGetFloatv(GL11.GL_MODELVIEW_MATRIX, matrix);
	GL11.glPopMatrix();

	//matrixStack.push(matrix);
}

public void popMatrix()
{
	if(matrixStack.size() > 0)
	{
//		float top = matrixStack.top();
		float top = matrixStack.firstElement();
		matrixStack.pop();
		//delete [] top;
	}

	if(matrixStack.empty())
	{
		initStack();
	}
}

public void pushMatrix()
{
	Float[] copy = new Float[16];
//	Float top = matrixStack.top();
	Float top = matrixStack.firstElement();
	for(int i = 0; i < 16; i++)
	{
		//copy[i] = top[i]; //theirs
	}

	//matrixStack.push(copy); //theirs
}

public void rotate(float r, float rx, float ry, float rz)
{
	//Float topOfStack = matrixStack.top(); //theirs
	GL11.glMatrixMode(GL11.GL_MODELVIEW);
	GL11.glPushMatrix();
	GL11.glLoadIdentity();
	//GL11.glMultMatrixf(topOfStack); //theirs
	GL11.glRotatef(r, rx, ry, rz);
	//GL11.glGetFloatv(GL11.GL_MODELVIEW_MATRIX, topOfStack); //theirs
	GL11.glPopMatrix();
}

public void translate(float tx, float ty, float tz)
{
	//GLfloat *topOfStack = matrixStack.top(); //theirs
	GL11.glMatrixMode(GL11.GL_MODELVIEW);
	GL11.glPushMatrix();
	GL11.glLoadIdentity();
	//GL11.glMultMatrixf(topOfStack); //theirs
	GL11.glTranslatef(tx, ty, tz);
	//GL11.glGetFloatv(GL11.GL_MODELVIEW_MATRIX, topOfStack); //theirs
	GL11.glPopMatrix();
}

public float vary(float v)
{
	float n = getNoise();
	n -= 0.5f;
	float var = (variation * n);
	return v + var;
}

public class ReproductionRule
{
	char from;
	String to;
}

public class Point3f
{
	float x, y, z;
};

}