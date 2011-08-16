package com.seaby.lwjgl.tree;

import org.lwjgl.opengl.GL11;

public class TreeParts
{
	public static void drawBranch(float width, float height)
	{
		// brown
		GL11.glColor3f(
				139/255.0f,
				69/255.0f,
				19/255.0f
		);
	
		GL11.glBegin(GL11.GL_QUADS);
		GL11.glVertex2f(-width, 0.0f);
		GL11.glVertex2f(width, 0.0f);
		GL11.glVertex2f(width, height);
		GL11.glVertex2f(-width, height);
		GL11.glEnd();
	}
	
	public static void drawLeaf(float width, float height, int level)
	{
		if(level == ITreeConstants.MAX_AGE-1)
		{
			// brown
			GL11.glColor3f(
					139/255.0f,
					69/255.0f,
					19/255.0f
			);
		}
		else
		{
			// green
			GL11.glColor3f(
					1/255.0f,
					102/255.0f,
					1/255.0f
			);
		}
		
		GL11.glBegin(GL11.GL_QUADS);
		GL11.glVertex2f(-ITreeConstants.LEAF_WIDTH, height);
		GL11.glVertex2f(ITreeConstants.LEAF_WIDTH, height);
		GL11.glVertex2f(ITreeConstants.LEAF_WIDTH, height+ITreeConstants.LEAF_HEIGHT);
		GL11.glVertex2f(-ITreeConstants.LEAF_WIDTH, height+ITreeConstants.LEAF_HEIGHT);
		GL11.glEnd();
	}
}
