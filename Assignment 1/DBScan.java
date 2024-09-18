// Fatih Bahceci - 300348275

import java.util.*;
import java.io.*;

class DBScan {
	private List<Point3D> points;
	private int minPts, clusterCount;
	private double eps;
	
	public DBScan(List<Point3D> points){
		this.points = points;
		this.minPts = 0;
		this.eps = 0;
		this.clusterCount = 0; // no clusters yet

	}
	
	public void setEps(double eps){
		this.eps = eps;
	}
	
	public void setMinPts(int minPts){
		this.minPts = minPts;
	}
	
	//Finds and groups all clusters
	public void findClusters(){
		clusterCount = 0;
		Stack<Point3D> stack = new Stack<>();
		// Nearest Neighbor var
		NearestNeighbors nn = new NearestNeighbors(points);
		
		//Iterates through all points,counts the amount of clusters
		for(Point3D P : points){
			if(P.getClusterLabel() != -1){
				continue;
			}
			
			List<Point3D> neighbors = nn.rangeQuery(eps, P);
			
			if(neighbors.size() < minPts){
				P.setClusterLabel(0);
				continue;
			}
			//Increases cluster count
			clusterCount++;
			
			P.setClusterLabel(clusterCount);
			stack.push(P); // pushin p
			// Runs until the stack is completely empty
			while(!stack.isEmpty()) {
				//Finds all points within the range of eps from the top of the stack
				Point3D top = stack.pop();
				List<Point3D> Neighbors = nn.rangeQuery(eps, top);
				
				
				if(Neighbors.size() >= minPts){
					//Iterates through all neighbors
					for(Point3D R : Neighbors){
						
						if(R.getClusterLabel()== 0){
							R.setClusterLabel(clusterCount); // assign cluster label
						}
						
						if(R.getClusterLabel() != -1){
							continue; // skip if processed
						}
						
						R.setClusterLabel(clusterCount);
						stack.push(R);
						
						
						
					}
				}
			}
		}
	}
	
	// Returns the count of clusters
	public int getNumberOfClusters(){
		return clusterCount;
	}
	
	// Returns the list of points
	public List<Point3D> getPoints(){
		return points;
	}
	
	public static List<Point3D> read(String filename) throws IOException {
		List<Point3D> points =new ArrayList<>();
		
		BufferedReader buffered_reader= new BufferedReader(new FileReader(filename));
		
		String line;
		
		while ((line = buffered_reader.readLine())!= null){
			// Skips first line
			if(line.startsWith("x,y,z")){
				continue;
			}
			// Parses through text, reads numbers as double and splits them into xyz
			String[] coords = line.split(",");
			double x = Double.parseDouble(coords[0]);
			double y = Double.parseDouble(coords[1]);
			double z = Double.parseDouble(coords[2]);
			points.add(new Point3D(x,y,z));
		}
		buffered_reader.close();
		return points;
	}
	
	public void save(String filename) throws IOException{
		// Opens file
		BufferedWriter buffered_writer = new BufferedWriter(new FileWriter(filename));
		Random rand = new Random();
		// Initializes old cluster and rgb
		int old_cluster = 0;
		double r = 0;
		double g = 0;
		double b = 0;
		// Writes title line
		buffered_writer.write("x,y,z,C,R,G,B\n");
		for (Point3D point : points){
			int cluster_label = point.getClusterLabel();
			// Only randomizes color if the cluster label is different from the last one
			if(old_cluster != cluster_label){
				r = rand.nextDouble();
				g = rand.nextDouble();
				b = rand.nextDouble();
			}
			// Writes the line of code with cluster label and color
			buffered_writer.write(point.getX() + "," + point.getY() + "," + point.getZ() + "," + point.getClusterLabel() + "," + r + "," + g + "," + b + "\n");
			old_cluster = cluster_label;
		}
		buffered_writer.close();
	}
public static void main(String[] args) throws IOException {
        // Shows usage method if incorrect input is put in
		if (args.length != 3) {
            System.out.println("Usage: java DBScan <filename> <eps> <minPts>");
            return;
        }
        
        String filename = args[0];
		// Parses eps as a double
        double eps = Double.parseDouble(args[1]);
		//parses minpts as integer
        int minPts = Integer.parseInt(args[2]);
        
        List<Point3D> points = DBScan.read(filename);
        DBScan dbscan = new DBScan(points);
        dbscan.setEps(eps);
        dbscan.setMinPts(minPts);
        dbscan.findClusters();
        // Names file
        String outputFilename = String.format("%s_clusters_%s_%s_%s.csv", filename, eps, minPts, dbscan.getNumberOfClusters());
        dbscan.save(outputFilename);
       
        
    }
}	
	
	
	
	
	
	
