package graphs;

import java.util.LinkedList;
import java.util.ArrayList;
import java.util.Queue;

public class ResidualNetwork implements Cloneable {
	private ArrayList<LinkedList<ResidualNetworkEdge>> data;
	private int nbNodes;
	private int start;
    private int end;
	
	public ResidualNetwork(int size, int start, int end) {
		this.data = new ArrayList<LinkedList<ResidualNetworkEdge>>(size);
		this.nbNodes = size;
		this.start = start;
		this.end = end;

		for(int i=0; i<this.nbNodes; i++){
			this.data.add(new LinkedList<ResidualNetworkEdge>());
		}
	}

	public boolean areConnected(int from, int to){
		if(from < 0 || to < 0 || from >= this.data.size() || to >= this.data.size())
			return false;
		for(ResidualNetworkEdge n : this.data.get(from)){
			if(n.getTo() == to){
				return true;
			}
		}
		return false;
	}

	public void addEdge(int from, int to, int flow, int capacity) throws ResidualNetworkException{
		if(from < 0 || to < 0 || from >= this.data.size() || to >= this.data.size() || flow<0 || capacity<0 || flow>capacity)
			throw new ResidualNetworkException("Invalid parameters");
		if(!this.areConnected(from, to)){
			ResidualNetworkEdge n = new ResidualNetworkEdge(from, to, capacity-flow, flow);
			this.data.get(from).add(n);
			this.data.get(to).add(n);
		}
	}

	public int getNbNodes(){
		return this.data.size();
	}

	public int getStart(){
		return this.start;
	}

	public int getEnd(){
		return this.end;
	}

	public int getFlow(){
		int flow = 0;
		for(ResidualNetworkEdge n : this.data.get(this.getEnd())){
			flow += n.getFlow();
		}
		return flow;
	}

	public void resetFlow(){
		try{
			for(int i=0; i<this.data.size(); i++){
				for(ResidualNetworkEdge n : this.data.get(i)){
					n.setForwardResidualCapacity(n.getCapacity());
					n.setBackwardResidualCapacity(0);
				}
			}
		} catch(ResidualNetworkException e){
			System.err.println("Unexpected exception:");
			System.err.println(e);
		}
	}

	public void showGraph(){
		//System.out.print(this.nbNodes);
		//System.out.print(this.nbEdges);

		for(int i=0; i<this.data.size(); i++){
			System.out.print(i+": ");
			for(ResidualNetworkEdge n : this.data.get(i)){
				if(i==n.getFrom())
					System.out.print("{"+n.getTo()+"["+n.getFlow()+"/"+n.getCapacity()+"]} ");
			}
			System.out.println("");
		}
	}

	private static ResidualNetworkEdge getEdge(ResidualNetwork g, int from, int to){
		for(ResidualNetworkEdge n : g.data.get(from)){
			if(n.getFrom() == to || n.getTo() == to)
				return n;
		}
		return null;
	}

	private static int getMinIncrementForPath(ResidualNetwork g, int[] edgeTo) throws ResidualNetworkException{
		int node = g.getEnd();
		int next = edgeTo[node];
		ResidualNetworkEdge edge = getEdge(g, node, next);
		if(edge == null){
			throw new ResidualNetworkException("no edge found between the 2 nodes");
		}

		int min = edge.getForwardResidualCapacity();
		node = next;

		while(node != g.getStart()){
			next = edgeTo[node];
			edge = getEdge(g, node, next);
			if(edge.getForwardResidualCapacity()<min){
				min = edge.getForwardResidualCapacity();
			}
			node = next;
		}
		return min;
	}

	private static void augmentPath(ResidualNetwork g, int[] edgeTo) throws ResidualNetworkException{
		int node = g.getEnd();
		int next = edgeTo[node];
		ResidualNetworkEdge edge = null;
		
		try{
			int increment = getMinIncrementForPath(g, edgeTo);
			while(node != g.getStart()){
				next = edgeTo[node];
				edge = getEdge(g, node, next);
				if(edge.getFrom() == next)
					edge.increaseFlow(increment);
				else
					edge.decreaseFlow(increment);
				node = next;
			}
		} catch(ResidualNetworkException e){
			throw e;
		}
	}

	private static boolean fulkersonAugmentBFSPath(ResidualNetwork g) throws ResidualNetworkException{
		boolean[] marked = new boolean[g.getNbNodes()];
		int[] edgeTo = new int[g.getNbNodes()];
		Queue<Integer> queue = new LinkedList<Integer>();
		int currentNode = 0;

		for(int i=0; i<g.getNbNodes(); i++){
			marked[i] = false;
			edgeTo[i] = -1;
		}
		marked[g.getStart()] = true;
		queue.add(g.getStart());

		try{
			while(!queue.isEmpty()){
				currentNode = queue.poll();
				for(ResidualNetworkEdge n : g.data.get(currentNode)){
					if(!marked[n.getTo()] && n.getForwardResidualCapacity()>0){
						edgeTo[n.getTo()] = currentNode;
						marked[n.getTo()] = true;
						
						if(n.getTo() == g.getEnd()){
							augmentPath(g, edgeTo);
							return true;
						}
						queue.add(n.getTo());
					}
					if(!marked[n.getFrom()] && n.getBackwardResidualCapacity()>0){
						edgeTo[n.getFrom()] = currentNode;
						marked[n.getFrom()] = true;
						queue.add(n.getTo());
					}
				}
			}
			return false;
		} catch(ResidualNetworkException e){
			throw e;
		}
	}

	public static void fulkerson(ResidualNetwork g){
		try{
			while(fulkersonAugmentBFSPath(g));
		} catch(ResidualNetworkException e){
			e.printStackTrace();
			System.err.println(e);
		}
	}

	public Object clone() throws CloneNotSupportedException {
		ResidualNetwork clone = new ResidualNetwork(this.getNbNodes(), this.getStart(), this.getEnd());
		for(int i=0; i<this.getNbNodes(); i++){
			for(ResidualNetworkEdge e : this.data.get(i)){
				if(!clone.areConnected(e.getFrom(), e.getTo())){
					ResidualNetworkEdge n = new ResidualNetworkEdge(e.getFrom(), e.getTo(), e.getForwardResidualCapacity(), e.getBackwardResidualCapacity());
					clone.data.get(e.getFrom()).add(n);
					clone.data.get(e.getTo()).add(n);
				}
			}
		}
		return clone;
	}

	public static boolean[] minCut(ResidualNetwork g) throws ResidualNetworkException {
		try{
			ResidualNetwork graph = (ResidualNetwork) g.clone();
			fulkerson(graph);

			int node = graph.getStart();
			boolean marked[] = new boolean[graph.getNbNodes()];
			boolean stop = true;
			
			for(int i=0; i<marked.length; i++){
				marked[i] = false;
			}

			marked[node] = true;
			for(int i=0; i<graph.getNbNodes(); i++){
				stop = true;
				for(ResidualNetworkEdge e : graph.data.get(node)){
					if(e.getForwardResidualCapacity()>0 && e.getFrom() == node && !marked[e.getTo()]){
						node = e.getTo();
						marked[node] = true;
						stop = false;
						break;
					} else if(e.getBackwardResidualCapacity()>0 && e.getTo() == node && !marked[e.getFrom()]){
						node = e.getFrom();
						marked[node] = true;
						stop = false;
						break;
					}
				}
				if(stop)
					break;
			}
			return marked;
		} catch(CloneNotSupportedException e){
			throw new ResidualNetworkException("graph could not be cloned");
		}
	}
}