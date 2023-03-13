package graphs;

public class ResidualNetworkEdge {
    private int from;
    private int to;
    private int forwardResidualCapacity; // how much the flow can be increased
    private int backwardResidualCapacity; // how much the flow can be decreased

    public ResidualNetworkEdge(){
        this.from = 0;
        this.to = 0;
        this.forwardResidualCapacity = 0;
        this.backwardResidualCapacity = 0;
    }

    public ResidualNetworkEdge(int from, int to, int forward, int backward){
        this.from = from;
        this.to = to;
        this.forwardResidualCapacity = forward;
        this.backwardResidualCapacity = backward;
    }

    public int getFrom(){
        return this.from;
    }

    public void setFrom(int from) {
        this.from = from;
    }
    
    public int getTo(){
        return this.to;
    }

    public void setTo(int to) {
        this.to = to;
    }

    public int getForwardResidualCapacity() {
        return forwardResidualCapacity;
    }

    public void setForwardResidualCapacity(int value) throws ResidualNetworkException {
        if(value<0)
            throw new ResidualNetworkException("capacity and flow can't be negatives");
        this.forwardResidualCapacity = value;
    }

    public int getBackwardResidualCapacity() {
        return backwardResidualCapacity;
    }

    public void setBackwardResidualCapacity(int value) throws ResidualNetworkException {
		if(value<0)
            throw new ResidualNetworkException("capacity and flow can't be negatives");
        this.backwardResidualCapacity = value;
    }
    
    public int getFlow(){
        return this.getBackwardResidualCapacity();
    }

    public int getCapacity(){
        return this.getBackwardResidualCapacity()+this.getForwardResidualCapacity();
    }

    public void increaseFlow(int value) throws ResidualNetworkException {
		int newForward = this.getForwardResidualCapacity() - value;
		int newBackward = this.getBackwardResidualCapacity() + value;

		if(newForward<0 || newBackward<0)
            throw new ResidualNetworkException("flow can't get "+(value<0?"decreased":"increased")+" anymore");
        this.setForwardResidualCapacity(newForward);
		this.setBackwardResidualCapacity(newBackward);
    }

    public void decreaseFlow(int value) throws ResidualNetworkException {
		int newForward = this.getForwardResidualCapacity() + value;
		int newBackward = this.getBackwardResidualCapacity() - value;

		if(newForward<0 || newBackward<0)
            throw new ResidualNetworkException("flow can't get "+(value>0?"reduced":"increased")+" anymore");
        this.setForwardResidualCapacity(newForward);
		this.setBackwardResidualCapacity(newBackward);
    }
}

