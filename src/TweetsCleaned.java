import java.io.FileReader;
import java.io.FileWriter;
import java.io.LineNumberReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * This file implements Feature 1.
 * @author Ajay
 *
 */

public class TweetsCleaned {
	public static void main(String args[]) throws Exception{
		final TweetsCleaned tc = new TweetsCleaned();

		final List<TweetDataStructure> tdsList = tc.extractFeed("./tweet_input/tweets.txt");
		tc.generateReport(tdsList);
	}

	/**
	 * This method processes the tweets, counts the number of tweets that contain unicode values and
	 * finally generates the output in ft1.txt
	 *
	 * @param tdsList list of data structure containing tweets.
	 * @throws Exception
	 */
	private void generateReport(final List<TweetDataStructure> tdsList) throws Exception{
		final FileWriter fileWriter = new FileWriter("./tweet_output/ft1.txt", false);

		int totalUnicodeTweets = 0;
		for( TweetDataStructure tds : tdsList ){
			fileWriter.append(tds.toString()).append("\n");

			if( tds.textHasUnicode() ){
				totalUnicodeTweets++;
			}
		}

		fileWriter.append("\n");
		fileWriter.append(totalUnicodeTweets + " tweets contained unicode.").append("\n");
		fileWriter.close();
	}

	// This method will read tweets from tweet_input.txt and return a list of Tweets.
	public List<TweetDataStructure> extractFeed(String inputFile) throws Exception{
		final LineNumberReader lnr = new LineNumberReader(new FileReader(inputFile));

		final List<TweetDataStructure> tdsList = new ArrayList<TweetDataStructure>();
		String tweetJson = null;
		while( (tweetJson = lnr.readLine()) != null){
			try{
				int beginIndexOfCreatedAt = tweetJson.indexOf("\"created_at\":");

				int endIndexOfCreatedAt = tweetJson.indexOf("\",", beginIndexOfCreatedAt);

				String createdAt = tweetJson.substring(beginIndexOfCreatedAt+14, endIndexOfCreatedAt);

				int beginIndexOfText = tweetJson.indexOf("\"text\":");
				int endIndexOfText = tweetJson.indexOf("\",", beginIndexOfText);

				String text = tweetJson.substring(beginIndexOfText+8, endIndexOfText);

				tdsList.add(new TweetDataStructure(createdAt, text));
			}
			catch(Exception e){
				// If invalid tweet found, move onto next.
			}
		}

		lnr.close();

		return tdsList;
	}

	class TweetDataStructure {
		private String createdAt;
		private String text;


		public TweetDataStructure(String createdAt, String text) {
			super();
			this.createdAt = createdAt;
			this.text = text;
		}

		public Date getCreatedAtAsDate() throws ParseException{
			final SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd hh:mm:ss Z yyyy");
			return sdf.parse(createdAt);
		}

		public boolean textHasUnicode(){
			Pattern pattern = Pattern.compile("\\\\u(\\p{XDigit}{4})");
			boolean b = pattern.matcher(this.text).find();

			return b;
		}

		/** Added to support Second Feature
		 * This method scans the text of the tweet for #<HashTag>
		 * A HashTag is assumed to begin with a "#" (Pound) and end with a " " (space)
		 * @return A set of unique HashTags
		 */
		public Set<String> getHashTags(){
			final Set<String> hashTags = new HashSet<String>();
			int nextIndexOfHash = -1;
			while( (nextIndexOfHash = this.text.indexOf("#", nextIndexOfHash+1)) != -1 ){
				int endIndexOfHashTag = this.text.indexOf(" ", nextIndexOfHash);
				if( endIndexOfHashTag < 0 ){
					endIndexOfHashTag = this.text.length();
				}
				String hashTag = this.text.substring(nextIndexOfHash, endIndexOfHashTag);
				hashTags.add(hashTag);
			}

			return hashTags;
		}

		public String getCreatedAt() {
			return createdAt;
		}
		public void setCreatedAt(String createdAt) {
			this.createdAt = createdAt;
		}
		public String getText() {
			return text;
		}
		public void setText(String text) {
			this.text = text;
		}

		@Override
		public String toString() {
			// A tweet's text is considered "clean" once all of the escape characters (e.g. \n, \", \/ ) and unicode have been removed.
			String cleanText = text.replaceAll("\n", "").replaceAll("\"", "").replaceAll("/", "").replaceAll("\\\\u(\\p{XDigit}{4})", "");

			return cleanText + " (timestamp: " +createdAt+ ")";
		}
	}
}
