package graphs;

public class TestGraphs {
	public static void main(String[] args) {
		ResidualNetwork g = new ResidualNetwork(8, 0, 7);
		
		try{
			g.addEdge(0,1,0, 10);
			g.addEdge(0,2,0, 5);
			g.addEdge(0,3,0, 15);

			g.addEdge(1,2,0, 4);
			g.addEdge(2,3,0, 4);
			
			g.addEdge(1,4,0, 9);
			g.addEdge(1,5,0, 15);
			g.addEdge(2,5,0, 8);
			g.addEdge(6,2,0, 6);
			g.addEdge(3,6,0, 16);

			g.addEdge(4,5,0, 15);
			g.addEdge(5,6,0, 15);
			
			g.addEdge(4,7,0, 10);
			g.addEdge(5,7,0, 10);
			g.addEdge(6,7,0, 10);

			System.out.println("flow: "+g.getFlow());
			ResidualNetwork.fulkerson(g);
			g.showGraph();
			System.out.println("flow: "+g.getFlow());
			boolean[] minCut = ResidualNetwork.minCut(g);
			String set1="",set2="";
			for(int i=0; i<minCut.length; i++){
				if(minCut[i])
					set1+=i+" ";
				else
					set2+=i+" ";
			}
			System.out.println("Min cut: {"+set1+"} and {"+set2+"}");
		} catch(ResidualNetworkException e){
			System.err.println(e);
		}
	}
}
