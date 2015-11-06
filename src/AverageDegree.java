import java.io.FileWriter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * This file implements Feature 2.
 * 
 * @author Ajay
 *
 */
public class AverageDegree {
	public static void main(String args[]) throws Exception{
		final AverageDegree ad = new AverageDegree();
		final TweetsCleaned tc = new TweetsCleaned();
		final List<TweetsCleaned.TweetDataStructure> tdsList = tc.extractFeed("./tweet_input/tweets.txt");
		ad.evictTweetsOlderThan60Seconds(tdsList);
		final Set<Node> graphs = ad.generateGraph(tdsList);
		ad.calculateAndReportAverageDegree(graphs);
	}
	
	private void calculateAndReportAverageDegree(final Set<Node> graphs) throws Exception{
		Double totalNodesWithDegreeGreaterThan0 = 0.0;
		Double sumOfAllDegrees = 0.0;
		for(final Node node : graphs){
			if( node.getDegree() > 0 ){
				totalNodesWithDegreeGreaterThan0+=1;
				sumOfAllDegrees += node.getDegree();
			}
		}
		
		Double averageDegree = round(sumOfAllDegrees/totalNodesWithDegreeGreaterThan0);
		
		final FileWriter fileWriter = new FileWriter("./tweet_output/ft2.txt", true);
		fileWriter.append( averageDegree.toString() ).append( "\n" );
		fileWriter.close();
	}
	
	private void evictTweetsOlderThan60Seconds(final List<TweetsCleaned.TweetDataStructure> tdsList) throws Exception{
		final Date createdAtOfLastTweet = tdsList.get(tdsList.size()-1).getCreatedAtAsDate();
		
		final Calendar cSixtySecondsFromLastTweet = Calendar.getInstance();
		cSixtySecondsFromLastTweet.setTime(createdAtOfLastTweet);
		cSixtySecondsFromLastTweet.add(Calendar.SECOND, -60);
		
		final Iterator<TweetsCleaned.TweetDataStructure> iterator = tdsList.iterator();
		while(iterator.hasNext()){
			final TweetsCleaned.TweetDataStructure tds = iterator.next();
			
			if( tds.getCreatedAtAsDate().before(cSixtySecondsFromLastTweet.getTime()) ){
				iterator.remove();
			}
		}
	}
		
	private Set<Node> generateGraph(final List<TweetsCleaned.TweetDataStructure> tdsList){
		// The following set contains unique nodes in the graph.
		final Set<Node> graphs = new HashSet<Node>();
		
		for(final TweetsCleaned.TweetDataStructure tds : tdsList){
			final List<String> hashTags = new ArrayList(tds.getHashTags());
			Node prevNode = null;
			for( int i=0; i<hashTags.size(); i++ ){
				final String hashTag = hashTags.get(i);
				
				Node node = getNodeFromGraph(graphs, new Node(hashTag) );
				if( prevNode != null ){
					prevNode.addConnection(node);
				}
				prevNode = node;
			}
			
			// Connect last Node back to first Node. The searchGraphForNode will not return null as the first node should be part of graphs collection now.
			if( prevNode != null ){
				prevNode.addConnection( getNodeFromGraph(graphs, new Node(hashTags.get(0))) );
			}
		}
		
		return graphs;
	}
	
	private Node getNodeFromGraph(final Set<Node> graphs, Node nodeToSearch){
		for(final Node node : graphs){
			if( node.equals( nodeToSearch ) ){
				return node;
			}
		}
		
		// If nodeToSearch is not found in the graph, then add it as a new Node to the graph.
		graphs.add(nodeToSearch);
		return nodeToSearch;
	}
	
	public static Double round(Double d){
		BigDecimal bd = new BigDecimal( d );
		bd = bd.setScale(2, BigDecimal.ROUND_HALF_UP);
		return bd.doubleValue();
	}

	class Node {
		private String hashTag;
		private Set<Node> connections;

		public Node(String hashTag) {
			super();
			this.hashTag = hashTag;
			this.connections = new HashSet<Node>();
		}

		public String getHashTag() {
			return hashTag;
		}
		
		public int getDegree(){
			return this.connections.size();
		}
		
		public void addConnection(Node node){
			// Only tweets that contain two or more DISTINCT hashtags can create new edges. 
			// If the node being tried to connect to; is itself that means there is a single hashTag in the tweet and no connection should be defined.
			if(!node.equals(this)){
				this.connections.add(node);
				// Connect the target node back to source node.
				node.connections.add(this);
			}
		}
		
		public Set<Node> getConnections() {
			return connections;
		}
		
		@Override
		public boolean equals(Object obj) {
			return ((Node)obj).getHashTag().equals(this.getHashTag());
		}
		
		@Override
		public int hashCode() {
			return this.getHashTag().hashCode();
		}
	}
}
