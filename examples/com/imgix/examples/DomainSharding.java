import com.imgix.URLBuilder;
import java.util.Map;
import java.util.HashMap;

public class DomainSharding {
	public static void main(String[] args) {
		String[] domains = new String[] { "demos-1.imgix.net", "demos-2.imgix.net", "demos-3.imgix.net"};
		URLBuilder builder = new URLBuilder(domains);

		Map<String, String> params = new HashMap<String, String>();
		params.put("w", "100");
		params.put("h", "100");
		System.out.println(builder.createURL("bridge.png", params));
		System.out.println(builder.createURL("flower.png", params));
	}
}
