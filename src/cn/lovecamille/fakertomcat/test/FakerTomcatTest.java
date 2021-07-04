package cn.lovecamille.fakertomcat.test;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.TimeInterval;
import cn.hutool.core.util.NetUtil;
import cn.hutool.core.util.StrUtil;
import cn.lovecamille.fakertomcat.util.MiniBrowser;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static cn.lovecamille.fakertomcat.util.MiniBrowser.getContentBytes;

/**
 * @author wuyan
 */
public class FakerTomcatTest {

    private static int port = 18080;
    private static String ip = "127.0.0.1";

    @BeforeClass
    public static void beforeClass() {
        // check if FakerTomcat has been launched before testing.
        if (NetUtil.isUsableLocalPort(port)) {
            System.err.println("Please launch the FakerTomcat in port:" + port + " or testing will not be executed");
            System.exit(1);
        } else {
            System.out.println("It has been detected that FakerTomcat has been launched, so unit testing starts");
        }
    }

    @Test
    public void testFakerTomcat() {
        String html = getContentString("/");
        Assert.assertEquals(html, "Hello FakerTomcat from WU Yan");
    }

    @Test
    public void testHtml() {
        String html = getContentString("/demo.html");
        Assert.assertEquals(html, "Hello FakerTomcat from demo.html");
    }

    @Test
    public void testTimeConsumerHtml() throws InterruptedException {
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(20, 20, 60, TimeUnit.SECONDS,
                new LinkedBlockingQueue<Runnable>(10));
        TimeInterval timeInterval = DateUtil.timer();
        for (int i = 0; i < 3; i++) {
            threadPoolExecutor.execute(() -> getContentString("/timeConsumer.html"));
        }
        threadPoolExecutor.shutdown();
        threadPoolExecutor.awaitTermination(1, TimeUnit.HOURS);

        long duration = timeInterval.intervalMs();

        Assert.assertTrue(duration < 3000);
    }

    @Test
    public void testaIndex() {
        String html = getContentString("/index");
        Assert.assertEquals(html, "Hello FakerTomcat from index.html@index");
    }

    @Test
    public void testbIndex() {
        String html = getContentString("/b");
        Assert.assertEquals(html, "Hello FakerTomcat from index.html@b");
    }

    @Test
    public void test404() {
        String response = getHttpString("/not_exist.html");
        containAssert(response, "HTTP/1.1 404 Not Found");
    }

    @Test
    public void test500() {
        String response = getHttpString("/500.html");
        containAssert(response, "HTTP/1.1 500 Internal Server Error");
    }

    @Test
    public void testExtTxt() {
        String httpResponse = getHttpString("/ext.txt");
        containAssert(httpResponse, "Content-Type:text/plain");
    }

    @Test
    public void testPng() {
        byte[] bytes = getContentBytes("/logo.png");
        int pngFileLength = 1672;
        Assert.assertEquals(pngFileLength, bytes.length);
    }

    @Test
    public void testPdf() {
        byte[] bytes = getContentBytes("/etf.pdf");
        int pdfFileLength = 3590775;
        Assert.assertEquals(pdfFileLength, bytes.length);
    }

    private String getHttpString(String uri) {
        String url = StrUtil.format("http://{}:{}{}", ip, port, uri);
        String http = MiniBrowser.getHttpString(url);
        return http;
    }

    private String getContentString(String uri) {
        String url = StrUtil.format("http://{}:{}{}", ip, port, uri);
        String content = MiniBrowser.getContentString(url);
        return content;
    }

    private void containAssert(String html, String string) {
        boolean match = StrUtil.containsAny(html, string);
        Assert.assertTrue(match);
    }
}
