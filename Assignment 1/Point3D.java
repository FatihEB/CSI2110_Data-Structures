// Fatih Bahceci - 300348275

import java.util.*;

class Point3D {
	private int cluster_label;
	private double x, y, z;
	
	public Point3D(double x, double y, double z){
		this.x = x;
		this.y = y;
		this.z = z;
		this.cluster_label = -1; // Not set yet
	}
	
	public double getX() { 
		return x;
	}
	
	public double getY(){
		return y;
	}
	
	public double getZ(){
		return z;
	}
	// Gets the cluster label
	public int getClusterLabel(){
		return cluster_label;
	}
	//Allows you to retroactively set the cluster label of a ponit
	public void setClusterLabel(int cluster_label){
		this.cluster_label = cluster_label;
	}
	
	// Calculates the euclidian distance of a point from the current point.
	public double euclDistance(Point3D pt){
		double xPow, yPow, zPow, distance;
		
		// Calculates x, y, and z's distance from the current point to the power of 2
		xPow = Math.pow(this.x - pt.x, 2); 
		yPow = Math.pow(this.y - pt.y, 2);
		zPow = Math.pow(this.z - pt.z, 2);
		
		// x^2 + y^2 + z^2 = d^2
		distance = Math.sqrt(xPow + yPow + zPow);
		
		return distance;
	}
}