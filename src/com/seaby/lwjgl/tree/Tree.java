package com.seaby.lwjgl.tree;

import org.lwjgl.opengl.GL11;

public class Tree
{
	private final float xPos;
	private final float yPos;
	private final float angle;
	private float width;
	private float height;
	private float heightGrowthConstant = ITreeConstants.HEIGHT_GROWTH_STEP*(1/ITreeConstants.AGE_RATE);
	private float widthGrowthConstant = ITreeConstants.WIDTH_GROWTH_STEP*(1/ITreeConstants.AGE_RATE);
	
	private int growthCounter = 0;
	
	private boolean isGrown;
	private boolean isRandomChildBranches;
	private boolean isRandomBranchAngle;
	private boolean isRandomStopGrowing;
	private boolean stopGrowing;
	private boolean isGrowing;
	
	private int age;

	private Tree leftBranch;
	private Tree rightBranch;
	private Tree middleBranch;
	
	public Tree()
	{
		this(0.0f,0.0f,0.0f, false, false, false, false);
	}
	
	public Tree(float xPos, float yPos, float angle,
			boolean isRandomChildBranches, boolean isRandomBranchAngle, boolean isRandomStopGrowing, boolean isRoot)
	{

		this.xPos = xPos;
		this.yPos = yPos;
		this.angle = angle;
		width = 0.0f;
		height = 0.0f;
		age = 1;
		
		isGrown = false;
		isGrowing = false;
		
		this.isRandomChildBranches = isRandomChildBranches;
		this.isRandomBranchAngle = isRandomBranchAngle;
		this.isRandomStopGrowing = isRandomStopGrowing;
	}
	
	public float getWidth()
	{
		return width;
	}

	public float getHeight()
	{
		return height;
	}

	public float getxPos()
	{
		return xPos;
	}

	public float getyPos()
	{
		return yPos;
	}

	public float getAngle()
	{
		return angle;
	}
	
	public int getAge()
	{
		return age;
	}
	
	public void grow()
	{
		if(!isGrowing)
		{
			isGrowing = true;
			// If the branches haven't been grown then grow them
			if(!isGrown)
			{
				boolean createLeftBranch = true;
				boolean createRightBranch = true;
				boolean createMiddleBranch = true;
				
				float leftBranchAngle = ITreeConstants.BRANCH_ANGLE;
				float rightBranchAngle = -ITreeConstants.BRANCH_ANGLE;
				float middleBranchAngle = 0.0f;
				
				// both random options have the same change of occurring
				if(isRandomChildBranches || isRandomStopGrowing)
				{
					createLeftBranch = (Math.random() * 100.0f > 50.0f ? true : false);
					createRightBranch = (Math.random() * 100.0f > 50.0f ? true : false);
					createMiddleBranch = (Math.random() * 100.0f > 50.0f ? true : false);
				}
	
				if(isRandomBranchAngle)
				{
					leftBranchAngle = (float)(ITreeConstants.BRANCH_ANGLE + Math.random() * ITreeConstants.BRANCH_ANGLE_RANGE);
					rightBranchAngle = (float)-(ITreeConstants.BRANCH_ANGLE + Math.random() * ITreeConstants.BRANCH_ANGLE_RANGE);
					middleBranchAngle = (float)(Math.random() + ITreeConstants.BRANCH_ANGLE_RANGE);
				}
				// no child branches were created
				if(!createLeftBranch && !createRightBranch && !createMiddleBranch)
				{
					middleBranch = new Tree(
							xPos,
							yPos+(height/2),
							middleBranchAngle,
							isRandomChildBranches,
							isRandomBranchAngle,
							isRandomStopGrowing,
							false
					);
					stopGrowing = true;
				}
				else
				{
					if(createLeftBranch)
					{
						leftBranch = new Tree(
								xPos+(width/2),
								yPos+(height/2),
								leftBranchAngle,
								isRandomChildBranches,
								isRandomBranchAngle,
								isRandomStopGrowing,
								false
						);
					}
					if(createRightBranch)
					{
						rightBranch = new Tree(
								xPos+(width/2),
								yPos+(height/2),
								rightBranchAngle,
								isRandomChildBranches,
								isRandomBranchAngle,
								isRandomStopGrowing,
								false
						);
					}
					if(createMiddleBranch)
					{
						middleBranch = new Tree(
								xPos,
								yPos+(height/2),
								middleBranchAngle,
								isRandomChildBranches,
								isRandomBranchAngle,
								isRandomStopGrowing,
								false
						);
					}
				}		
				isGrown = true;
			}
			else
			{
				// If the branches have been grown then give them branches
				if(!stopGrowing)
				{
					if(leftBranch != null)
						leftBranch.grow();
					if(rightBranch != null)
						rightBranch.grow();
					if(middleBranch != null)
						middleBranch.grow();
				}
			}
		}
	}
	
	public void draw(int level)
	{
		GL11.glPushMatrix();
		GL11.glRotatef(angle, 0.0f, 0.0f, 1.0f);
		// draw root
		drawTreeParts(level);
		// draw branches
		if(isGrown)
		{
			drawSubTrees(level);
		}
		GL11.glPopMatrix();
	}
	
	public void drawTreeParts(int level)
	{
		if(isGrowing)
		{
			if(age < ITreeConstants.MAX_AGE)
			{
				growthCounter++;
				height += heightGrowthConstant;
				width += widthGrowthConstant;
				
				if(growthCounter % ITreeConstants.AGE_RATE == 0)
				{
					age++;
					widthGrowthConstant = ITreeConstants.WIDTH_GROWTH_STEP*(1/ITreeConstants.AGE_RATE)*age;
					isGrowing=false;
				}
			}
		}
		
		TreeParts.drawBranch(width, height);

		if(age == 1)
		{
			TreeParts.drawLeaf(width, height, level);
		}
	}
	
	private void drawSubTrees(int level)
	{
		GL11.glTranslatef(0.0f,height, 0.0f);
		if(middleBranch != null)
			middleBranch.draw(level);
		if(leftBranch != null)
			leftBranch.draw(level);
		if(rightBranch != null)
			rightBranch.draw(level);
	}
}
