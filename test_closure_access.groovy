
import okhttp3.OkHttpClient

public class TestClient {
    private static final OkHttpClient client = new OkHttpClient()

    static def doSomething = {
        println "Client: " + client
    }
}

TestClient.doSomething()
