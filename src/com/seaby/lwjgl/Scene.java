package com.seaby.lwjgl;


import java.util.ArrayList;

import org.lwjgl.Sys;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;

import com.seaby.lwjgl.tree.ITreeConstants;
import com.seaby.lwjgl.tree.Tree;
 
/**
 * My scene
 *
 * @author Matt
 * @version 1.0
 */
public class Scene
{
	/** Game title */
	public static final String GAME_TITLE = "My Game";
	
	/** Desired frame time */
	private static final int FRAMERATE = 60;
	
	/** Exit the game */
	private static boolean finished;
	
	private ArrayList<Tree> treeArray;
	
	private static int frameCounter = 0;
	
	public Scene()
	{
		treeArray = new ArrayList<Tree>();
		// random branches stop growing
		treeArray.add(new Tree(-600.0f, 0.0f, 0.0f, false, false, true, true));
//		// random angle
		treeArray.add(new Tree(-300.0f, 0.0f, 0.0f, false, true, false, true));
		// symmetrical (no random angle), fully grown
		treeArray.add(new Tree(0.0f, 0.0f, 0.0f, false, false, false, true));
		// random number of child branches
		treeArray.add(new Tree(300.0f, 0.0f, 0.0f, true, false, false, true));
//		// random number of child branches and random angle 
		treeArray.add(new Tree(600.0f, 0.0f, 0.0f, true, true, false, true));
		
		try
		{
			init(false);
			run();
		}
		catch (Exception e)
		{
			e.printStackTrace(System.err);
			Sys.alert(GAME_TITLE, "An error occured and the game will exit.");
		}
		finally
		{
			cleanup();
		}
		System.exit(0);
	}
 
	/**
	* Initialise the game
	* @throws Exception if init fails
	*/
	private static void init(boolean fullscreen) throws Exception
	{
		// Create a fullscreen window with 1:1 orthographic 2D projection (default)
		Display.setTitle(GAME_TITLE);
		Display.setFullscreen(fullscreen);
		
		// Enable vsync if we can (due to how OpenGL works, it cannot be guarenteed to always work)
		Display.setVSyncEnabled(true);
		
		// Create default display of 640x480
		Display.create();
	}
	
	/**
	* Runs the game (the "main loop")
	*/
	private void run()
	{
		while (!finished)
		{
			// Always call Window.update(), all the time - it does some behind the
			// scenes work, and also displays the rendered output
			Display.update();

			// Check for close requests
			if (Display.isCloseRequested())
			{
				finished = true;
			} 
			
			// The window is in the foreground, so we should play the game
			else if (Display.isActive())
			{
				logic();
				render();
				Display.sync(FRAMERATE);
			}

			// The window is not in the foreground, so we can allow other stuff to run and
			// infrequently update
			else
			{
				try
				{
					Thread.sleep(100);
				}
				catch (InterruptedException e)
				{
				}
				
				logic();

				// Only bother rendering if the window is visible or dirty
				if (Display.isVisible() || Display.isDirty())
				{
					render();
				}
			}
		}
	}
	
	/**
	* Do any game-specific cleanup
	*/
	private void cleanup()
	{
		// Close the window
		Display.destroy();
	}
	
	/**
	* Do all calculations, handle input, etc.
	*/
	private void logic()
	{
		// Example input handler: we'll check for the ESC key and finish the game instantly when it's pressed
		if (Keyboard.isKeyDown(Keyboard.KEY_ESCAPE))
		{
			finished = true;
		}
	}

	/**
	* Render the current frame
	*/
	private void render()
	{
		frameCounter++;
		
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glLoadIdentity();
		GL11.glOrtho(0, Display.getDisplayMode().getWidth(), 0, Display.getDisplayMode().getHeight(), -1, 1);
		GL11.glMatrixMode(GL11.GL_MODELVIEW);

		// clear the screen (a sky blue colour)
		GL11.glClearColor(0.0f, 0.0f, 1.0f, 1.0f);
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_STENCIL_BUFFER_BIT);

		GL11.glPushMatrix();
		GL11.glTranslatef(Display.getDisplayMode().getWidth() / 2, Display.getDisplayMode().getHeight() / 2, 0.0f);
		// draw the ground
		GL11.glBegin(GL11.GL_QUADS);
		GL11.glColor3f(0.0f, 1.0f, 0.0f);
		GL11.glVertex2i(-(Display.getDisplayMode().getWidth() / 2), -(Display.getDisplayMode().getHeight() / 2)); //bottom left
		GL11.glVertex2i(Display.getDisplayMode().getWidth() / 2, -(Display.getDisplayMode().getHeight() / 2)); //bottom right
		GL11.glVertex2i(Display.getDisplayMode().getWidth() / 2, 0); //top right
		GL11.glVertex2i(-(Display.getDisplayMode().getWidth() / 2), 0); //top left
		GL11.glEnd();
		GL11.glPopMatrix();
		
		// position trunk in middle of screen
		GL11.glPushMatrix();//height /2
		GL11.glTranslatef(Display.getDisplayMode().getWidth() / 2, Display.getDisplayMode().getHeight()/2, 0.0f);
		
		boolean spacePressed  = hasInput(Keyboard.KEY_SPACE);
		
		
		for(Tree t : treeArray)
		{
			GL11.glPushMatrix();
			GL11.glTranslatef(t.getxPos(), 0.0f, 0.0f);
			if(spacePressed)
			{				
				t.grow();
			}
			t.draw(ITreeConstants.TREE_ROOT_LEVEL);
			GL11.glPopMatrix();
		}
		
		GL11.glPopMatrix();
	}
	
	/**
	 * @param direction
	 * @return
	 */
	private boolean hasInput(int key)
	{
		switch(key)
		{
			case Keyboard.KEY_SPACE:
				return
				Keyboard.isKeyDown(Keyboard.KEY_SPACE);
		}
		return false;
	}

}