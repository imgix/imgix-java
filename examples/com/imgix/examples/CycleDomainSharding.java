import com.imgix.URLBuilder;
import java.util.Map;
import java.util.HashMap;

public class CycleDomainSharding {
	public static void main(String[] args) {
		String[] domains = new String[] { "demos-1.imgix.net", "demos-2.imgix.net", "demos-3.imgix.net"};
		URLBuilder builder = new URLBuilder(domains);

		builder.setShardStratgy(URLBuilder.ShardStrategy.CYCLE);

		Map<String, String> params = new HashMap<String, String>();
		params.put("w", "100");
		params.put("h", "100");

		for (int i = 0; i < 4; i++) {
			System.out.println(builder.createURL("bridge.png", params));
		}
	}
}
