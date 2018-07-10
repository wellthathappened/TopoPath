// Name: Ian Lewis
// Date: 11/01/2016
// Course: COP 3503C

// A block of code in this file was borrowed from toposort.java written by 
// Sean Szumlanski.

import java.io.File;
import java.util.Scanner;
import java.util.HashSet;
import java.util.LinkedList;
import java.io.FileNotFoundException;

// Our vertex class keeps track of the vertex number, the amount of verticies a 
// given vertex connects TO, whether or not it has been visited and a list of
// the verticies our vertex connects to.
class Vertex
{
    int val;
    int outgoingConnectionCount;
    boolean visited = false;
    Vertex [] outgoingConnections;
}

public class TopoPath
{
    public static int n, nodesVisited = 0;
    public static HashSet<Integer> clearedNodes = new HashSet<>();
    
    public static boolean hasTopoPath(String filename) throws FileNotFoundException
    {
        // We declare our array for counting incoming connections for each
        // individual vertex. We keep this outside vertex class for easier use.
        // Our linked list keeps a spatially efficient list of starting nodes in
        // our graph.
        int [] incomingConnectionCount;
        LinkedList<Integer> startingPoints = new LinkedList();
        
        Scanner in = new Scanner(new File(filename));
        n = in.nextInt();
        
        Vertex [] graph = new Vertex[n];
        
        // We initialize the size and vertex numbers in our graph.
        for(int i = 0;i < n;i++)
        {
            graph[i] = new Vertex();
            graph[i].val = i + 1;
        }
        
        // We initialize each vertex's connection list and count.
        for(int i = 0;i < n;i++)
        {
            graph[i].outgoingConnectionCount = in.nextInt();
            graph[i].outgoingConnections = new Vertex[graph[i].outgoingConnectionCount];
            
            for(int j = 0;j < graph[i].outgoingConnectionCount;j++)
                graph[i].outgoingConnections[j] = graph[in.nextInt() - 1];
        }
        
        incomingConnectionCount = new int[graph.length];
        
		
        // "Count the number of incoming edges incident to each vertex. For sparse
        // graphs, this could be made more efficient by using an adjacency list."
        // - Sean Szumlanski
        for (int i = 0; i < graph.length; i++)
            for (int j = 0; j < graph[i].outgoingConnectionCount; j++)
                if(graph[i].outgoingConnections[j].val >= 1)
                    incomingConnectionCount[graph[i].outgoingConnections[j].val - 1]++;
		/*
			The code between the above comment block and this comment block was written 
			by Sean Szumlanski.
		*/
        
        // We use our incoming connection counter to help find exactly what
        // nodes we can start our traversal from. This helps reduce runtime.
        for(int i = 0;i < n;i++)
            if(incomingConnectionCount[i] == 0)
                startingPoints.add(i);
        
        
        // For every starting node, we make sure each vertex is marked as
        // unvisited first, then we begin traversing our paths.
        for(int i = 0;i < startingPoints.size();i++)
        {
            for(int j = 0;j < n;j++)
                graph[j].visited = false;
            
            if(traverse(graph[startingPoints.get(i)]) == true)
                return true;
        }
        
        return false;
    }
    
    // A method to navigate every possible path by DFS until we find our given
    // TopoPath
    private static boolean traverse(Vertex currentVertex)
    {
        boolean foundPath = false;
        
        // If we hit a loop, we return false so that we know to go back and
        // either try a different path or start over from a new starting node.
        if(currentVertex.visited == true)
            return false;
        
        // Otherwise we visit our given node and continue on.
        else if(currentVertex.visited == false)
        {
            currentVertex.visited = true;
            nodesVisited++;
        }
        
        // We take a DFS approach to traversing our graph.
        // We go as far as possible down a given path and if we hit a roadblock,
        // we backtrack and reset our progress along the way.
        for(int i = 0;i < currentVertex.outgoingConnectionCount;i++)
        {
            foundPath = traverse(currentVertex.outgoingConnections[i]);
            
            if(foundPath == true)
                return true;
            
            else if(foundPath == false)
            {
                // We reset the nodes we visited when traversing down a an
                // unsuccessful path and clear our record of nodes to reset our
                // hashset for the next time we need to reset our progress.
                resetNodes(currentVertex.outgoingConnections[i]);
                clearedNodes.clear();
                
                // If we've exhausted all options and we still haven't found a
                // path, we reset our progress and backtrack to the last node
                // in which we had a remaining path to follow.
                if(i == (currentVertex.outgoingConnectionCount - 1))
                {
                    currentVertex.visited = false;
                    nodesVisited --;
                    return false;
                }
            }
        }
        
        // If our conditions are met, we return true.
        return ((currentVertex.outgoingConnectionCount == 0) && (nodesVisited == n));
    }
    
    // A method to reset traversal progress when an incorrect path is found
    private static void resetNodes(Vertex currentVertex)
    {
        if(clearedNodes.add(currentVertex.val) == false)
            return;
        
        if(currentVertex.visited == true)
        {
            currentVertex.visited = false;
            nodesVisited--;
        }
        
        // We ensure to clear every node we have visited down this path.
        for(int i = 0;i < currentVertex.outgoingConnectionCount;i++)
            resetNodes(currentVertex.outgoingConnections[i]);
    }
    
    public static double difficultyRating()
    {
        return 3.5;
    }
    
    public static double hoursSpent()
    {
        return 6;
    }
}