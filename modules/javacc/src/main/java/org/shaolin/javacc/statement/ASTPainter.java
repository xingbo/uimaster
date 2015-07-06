package org.shaolin.javacc.statement;

import java.awt.BorderLayout;
import java.awt.Container;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.*;

/**
 * The class to paint the statement AST with tree view
 * 
 */
public class ASTPainter extends JFrame
{
	
	/**
	 * @param args
	 */
    static final int WIDTH = 200;
    
    static final int HEIGHT = 300;
    
    private static final long serialVersionUID = 1L;
    
    /**
     * Get the tree node for the statement
     * 
     * @param root the root statement node of the block
     * @return the tree node for the statement
     */
    private DefaultMutableTreeNode paintAST(Statement root)
    {
    	
    	DefaultMutableTreeNode astRoot=new DefaultMutableTreeNode("Root");
    	root.paintAST(astRoot);
    	return astRoot;
    }
    
    
    /**
     * Paint the tree using tree view
     * 
     * @param root the root statement node of the block
     */
    public void paintTree(Statement root)
    {
	    JTree tree;
	    DefaultMutableTreeNode rootNode = paintAST(root);
	    tree = new JTree(rootNode);	    	    
	    JScrollPane treeView = new JScrollPane(tree);
        Container content = this.getContentPane();
        content.add(treeView, BorderLayout.CENTER);
        this.setSize(WIDTH, HEIGHT);
        this.setVisible(true);
    }

}
