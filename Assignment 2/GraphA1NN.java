// Fatih Bahceci, CSI2110 2024, 300348275
/* ---------------------------------------------------------------------------------
The GraphA1NN class is the starting class for the graph-based ANN search

(c) Robert Laganiere, CSI2110 2024
------------------------------------------------------------------------------------*/

import java.io.*;
import java.util.*;


class GraphA1NN{
	
	UndirectedGraph<LabelledPoint> annGraph;
    private PointSet dataset;
	private int S;
	//private int TempV = 1000;
	
	// construct a graph from a file
    public GraphA1NN(String fvecs_filename) {

	    annGraph= new UndirectedGraph<>();
		dataset= new PointSet(PointSet.read_ANN_SIFT(fvecs_filename));
    }
	public PointSet getDataSet(){
		return dataset;
	}
	// construct a graph from a dataset
    public GraphA1NN(PointSet set){
		
	   annGraph= new UndirectedGraph<>();
       this.dataset = set;
    }
	
	public void setS(int S){
		this.S = S;
	}

    // build the graph
    public void constructKNNGraph(int K) {
		
		for (int i = 0; i < dataset.getPointsList().size(); i++){
			LabelledPoint vertex_1 = dataset.getPointsList().get(i);
			PriorityQueue<LabelledPoint> pq = new PriorityQueue<>(Comparator.comparingDouble(vertex_1::distanceTo)); // Priority queue that ranks based on distance from vertex_1
			
			for(int j = 0; j<dataset.getPointsList().size(); j++){
				if(i!=j){
					LabelledPoint vertex_2 = dataset.getPointsList().get(j);
					pq.add(vertex_2);
				}
			}
			for(int k = 0; k < K; k++){
				annGraph.addEdge(vertex_1, pq.poll()); // Adds the top K nearest points as neighbors.
			}			
		}
		
		

	}
	// A seperate knn graph constructor that is called on with the adjacency ArrayList given by readAdjacencyFile
	public void constructKNNGraph(int K, ArrayList<List<Integer> > adjacency) { 
		
		for (int i = 0; i < adjacency.size(); i++){
			LabelledPoint vertex_1 = dataset.getPointsList().get(i);
			PriorityQueue<LabelledPoint> pq = new PriorityQueue<>(Comparator.comparingDouble(vertex_1::distanceTo)); // Priority queue that ranks based on distance from vertex_1
			
			
			for(int k = 0; k < K; k++){
				annGraph.addEdge(vertex_1, dataset.getPointsList().get(adjacency.get(i).get(k))); // Adds the top K nearest points as neighbors.
			}			
		}
		
		

	}
	public LabelledPoint Find1NN(LabelledPoint pt){
		Random random = new Random();
		int random_ent = random.nextInt(dataset.getPointsList().size()); // Creates a random entry point from anywhere int he tree
		
        LabelledPoint entryPoint = dataset.getPointsList().get(random_ent);
        PriorityQueue<LabelledPoint> candidateQueue = new PriorityQueue<>(Comparator.comparingDouble(pt::distanceTo));
        Set<LabelledPoint> visited = new HashSet<>();
        List<LabelledPoint> A = new ArrayList<>(S);
        candidateQueue.add(entryPoint);
		
		
        while (!candidateQueue.isEmpty() && A.size() < S) { // Keeps looping until either A is greater than size S or there are no candidates
            LabelledPoint current = candidateQueue.poll();
            if (!visited.contains(current)) {
                visited.add(current);
                A.add(current); // Current is added to the A array

                for (LabelledPoint neighbor : annGraph.getNeighbors(current)) { // If a neighbor is not visisted, it is added to candidates
                    if (!visited.contains(neighbor)) {
                        candidateQueue.add(neighbor);
                    }
                }
            }
        }
        A.sort(Comparator.comparingDouble(pt::distanceTo));
        return A.get(1); // Return the closest point found
	}
	
	public static ArrayList<List<Integer> > readAdjacencyFile(String fileName, int numberOfVertices) 
	                                                                 throws Exception, IOException
	{	
		ArrayList<List<Integer> > adjacency= new ArrayList<List<Integer> >(numberOfVertices);
		for (int i=0; i<numberOfVertices; i++) 
			adjacency.add(new LinkedList<>());
		
		// read the file line by line
	    String line;
        BufferedReader flightFile = 
        	      new BufferedReader( new FileReader(fileName));
        
		// each line contains the vertex number followed 
		// by the adjacency list
        while( ( line = flightFile.readLine( ) ) != null ) {
			StringTokenizer st = new StringTokenizer( line, ":,");
			int vertex= Integer.parseInt(st.nextToken().trim());
			while (st.hasMoreTokens()) { 
			    adjacency.get(vertex).add(Integer.parseInt(st.nextToken().trim()));
			}
        } 
	
	    return adjacency;
	}
	
	public int size() { return annGraph.size(); }
	
	public class BoundedPriorityQueue<LabelledPoint> extends PriorityQueue<LabelledPoint>{
		private final int maxSize;

		public BoundedPriorityQueue(int maxSize, Comparator<? super LabelledPoint> comparator) {
			super(maxSize, comparator);
			this.maxSize = maxSize;
		}
		

		@Override
		public boolean add(LabelledPoint e) {
			if (size() < maxSize) {
				return super.add(e);
			} else if (comparator().compare(e, peek()) > 0) {

				return super.add(e);
			} else {
				return false;
			}
		}
	}
	

	
	
    public static void main(String[] args) throws IOException, Exception {
		
		ArrayList<List<Integer> > adjacency= GraphA1NN.readAdjacencyFile("knn.txt", 10000);
		long totalTime = 0;
		int correctCount = 0;
		int queryIndex = 0;
        String line;
		String verificationFile = "";
		BufferedReader verificationReader = null;

		if (args.length < 4) {
            System.out.println("Usage: java GraphA1NN <k> <S> <pointSetFile> <queryFile>");
            return;
        }
		Random random = new Random();
        int k = Integer.parseInt(args[0]);
        int S = Integer.parseInt(args[1]);
        String pointSetFile = args[2];
        String queryFile = args[3];
		
		if(args.length > 4){
			verificationFile = args[4]; // If there is an arguement for a verification file,checks output
		}
		
		if(!verificationFile.isEmpty()){
			verificationReader = new BufferedReader(new FileReader(verificationFile));
		}
			GraphA1NN graph = new GraphA1NN(pointSetFile);
		PointSet queryPoints = new PointSet(PointSet.read_ANN_SIFT(queryFile));
		
		if(pointSetFile.equals("siftsmall_base.fvecs")){ // If the small database is ran, then uses knn.txt instead of constructing the graph
			graph.constructKNNGraph(k, adjacency);
		}
		else{
			graph.constructKNNGraph(k);
		}
		graph.setS(S);
       
	   for (int i = 0; i < queryPoints.getPointsList().size(); i++) {
			LabelledPoint query = queryPoints.getPointsList().get(i);
			long startTime = System.currentTimeMillis();

			LabelledPoint nn = graph.Find1NN(query);
			long endTime = System.currentTimeMillis();
			
			if(!verificationFile.isEmpty()){ // Only verifies if a verification file is given
				line = verificationReader.readLine();
				String[] parts = line.split(":");
				List<Integer> expectedNeighbors = new ArrayList<>();
				for (String neighbor : parts[1].split(",")) {
					expectedNeighbors.add(Integer.parseInt(neighbor.trim()));
				}

				if (expectedNeighbors.contains(nn.getLabel())) {
					correctCount++;
				}	
				
			}
			queryIndex+=1;
		
			
			
            System.out.println("Query: " + i + " Result: " + nn.getLabel());
	   

			totalTime += (endTime - startTime); // recodrds total time
	   }
		
			System.out.println("Total time: " + totalTime + "ms");
			if(!verificationFile.isEmpty()){
				System.out.println("Accuracy: " + (correctCount / (double) queryIndex) * 100 + "%");
			}
	
	}
}
	
	
	   

   



