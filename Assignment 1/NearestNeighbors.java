// Fatih Bahceci 300348275

import java.util.*;

class NearestNeighbors{
	private List<Point3D> points;
	
	public NearestNeighbors(List<Point3D> points){
		this.points = points;
	}
	
	public List<Point3D> rangeQuery(double eps, Point3D P){
		List<Point3D> neighbors = new ArrayList<>();
		// Iterates through all points
		for(Point3D point: points){
			if(P.euclDistance(point) <= eps){
				neighbors.add(point);
			}
		}
		return neighbors;
	}
}