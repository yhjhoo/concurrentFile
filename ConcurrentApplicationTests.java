package me.prince.concurrent;

import org.apache.commons.io.IOUtils;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.web.client.RestTemplate;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ConcurrentApplicationTests {

    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {
        long start = System.currentTimeMillis();
        ExecutorService executorService = Executors.newFixedThreadPool(30);
        BufferedWriter bufferedWriter = Files.newBufferedWriter(new File("text.txt").toPath());
        WriterService writerService = new WriterService(bufferedWriter);

        List<? extends Future<?>> futureList = IntStream.range(0, 100).boxed().map(
                integer -> executorService.submit(() -> {
                    try {
                        InputStream response = callHttp();
                        writerService.writeFile(integer, response);
//                        String httpStr = callHttpStr();
//                        writerService.writeFile(integer, httpStr);
                    } catch (IOException | InterruptedException e) {
                        e.printStackTrace();
                    }
                })
        ).collect(Collectors.toList());
        for (Future f : futureList) {
            f.get();
        }

        executorService.shutdown();

        System.out.println(System.currentTimeMillis() - start);
    }


    private static String callHttpStr() {
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.getForEntity("http://localhost:8080/person", String.class).getBody();
    }

    private static InputStream callHttp() throws IOException {
//        RestTemplate restTemplate = new RestTemplate();
//        String response = restTemplate.getForEntity("http://localhost:8080/person", String.class).getBody();

        HttpClient httpClient = HttpClientBuilder.create().build();
        return httpClient.execute(new HttpGet("http://localhost:8080/person")).getEntity().getContent();
    }

}


class WriterService {
    BufferedWriter bufferedWriter;

    public WriterService(BufferedWriter bufferedWriter) {
        this.bufferedWriter = bufferedWriter;

    }

    public synchronized void writeFile(Integer integer, String s) throws IOException, InterruptedException {
        bufferedWriter.newLine();
        bufferedWriter.write(integer + ", ");
        bufferedWriter.write(s);
        bufferedWriter.flush();
    }

    public synchronized void writeFile(Integer integer, InputStream response) throws InterruptedException, IOException {
        bufferedWriter.newLine();
        bufferedWriter.write(integer + ", ");

        byte[] buffer = new byte[8 * 1024];
        int read;
        while((read = response.read(buffer)) != -1) {
            bufferedWriter.write(new String(buffer), 0, read);
        }

//        bufferedWriter.write(IOUtils.toString(response, Charset.defaultCharset()));
        bufferedWriter.flush();
        response.close();
    }
}
