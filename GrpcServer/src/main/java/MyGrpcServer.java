import io.grpc.Server;
import io.grpc.ServerBuilder;
import com.example.service.HelloService;

import java.io.IOException;

public class MyGrpcServer {
    static public void main(String[] args) throws IOException, InterruptedException {
        Server server = ServerBuilder.forPort( 8082 )
                .addService(new HelloService() )
                .build();

        System.out.println( "Starting server..." );
        server.start();
        System.out.println( "Server started!" );
        server.awaitTermination();
    }
}