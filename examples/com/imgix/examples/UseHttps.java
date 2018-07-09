import com.imgix.URLBuilder;
import java.util.Map;
import java.util.HashMap;

public class UseHttps {
	public static void main(String[] args) {
		URLBuilder builder = new URLBuilder("demos.imgix.net");
		builder.setUseHttps(true);
		Map<String, String> params = new HashMap<String, String>();
		params.put("w", "100");
		params.put("h", "100");
		System.out.println(builder.createURL("bridge.png", params));
	}
}
