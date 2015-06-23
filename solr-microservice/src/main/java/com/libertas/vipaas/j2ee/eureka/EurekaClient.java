package com.libertas.vipaas.j2ee.eureka;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.URL;
import java.net.UnknownHostException;
import java.text.MessageFormat;
import java.util.Enumeration;
import java.util.Properties;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EurekaClient extends HttpServlet {
    private static final String SERVER_ADDRESS = "server.address";
    private static final String SERVER_PORT = "server.port";
    private static final String VIRTUAL_HOST_NAME = "eureka.instance.virtualHostName";
    private static final String APPLICATION_NAME = "spring.application.name";
    private static final String EUREKA_URL_KEY = "eureka.client.serviceUrl.defaultZone";
    private static final long serialVersionUID = -7484811115247518878L;
    private String eurekaUrl;
    private String serverAddresses;
    private String serverPort;
    private String appName;

    private String virtualHostName;

    @Override
    protected void doGet(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {
        resp.getWriter().write("{\"status\":\"OK\"}");
    }

    @Override
    protected void doPut(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {
        doGet(req, resp);
    }

    @Override
    public void init(final ServletConfig config) throws ServletException {
        super.init(config);

        eurekaUrl = config.getInitParameter(EUREKA_URL_KEY);
        appName = config.getInitParameter(APPLICATION_NAME);
        serverAddresses = config.getInitParameter(SERVER_ADDRESS);
        serverPort = config.getInitParameter(SERVER_PORT);
        virtualHostName = config.getInitParameter(VIRTUAL_HOST_NAME);
        loadFromProperties();
        pingEurekaServer();
    }

    private void loadFromProperties() {
        final Properties properties = new Properties();

        try {
            final ClassLoader classLoader = EurekaClient.class.getClassLoader();

            final InputStream stream = classLoader.getResourceAsStream("eureka-client.properties");

            properties.load(stream);

            eurekaUrl = System.getProperty(EUREKA_URL_KEY,properties.getProperty(EUREKA_URL_KEY, eurekaUrl));
            appName = System.getProperty(APPLICATION_NAME,properties.getProperty(APPLICATION_NAME, appName));
            serverAddresses = System.getProperty(SERVER_ADDRESS,properties.getProperty(SERVER_ADDRESS, serverAddresses));
            serverPort = System.getProperty(SERVER_PORT,properties.getProperty(SERVER_PORT, serverPort));
            virtualHostName = System.getProperty(VIRTUAL_HOST_NAME,properties.getProperty(VIRTUAL_HOST_NAME, virtualHostName));
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }

    private static InetAddress getLocalHostLANAddress() throws UnknownHostException {
        try {
            InetAddress candidateAddress = null;
            // Iterate all NICs (network interface cards)...
            for (Enumeration<NetworkInterface> ifaces = NetworkInterface.getNetworkInterfaces(); ifaces.hasMoreElements();) {
                NetworkInterface iface = (NetworkInterface) ifaces.nextElement();
                // Iterate all IP addresses assigned to each card...
                for (Enumeration<InetAddress> inetAddrs = iface.getInetAddresses(); inetAddrs.hasMoreElements();) {
                    InetAddress inetAddr = (InetAddress) inetAddrs.nextElement();
                    if (!inetAddr.isLoopbackAddress()) {

                        if (inetAddr.isSiteLocalAddress()) {
                            // Found non-loopback site-local address. Return it immediately...
                            return inetAddr;
                        }
                        else if (candidateAddress == null) {
                            // Found non-loopback address, but not necessarily site-local.
                            // Store it as a candidate to be returned if site-local address is not subsequently found...
                            candidateAddress = inetAddr;
                            // Note that we don't repeatedly assign non-loopback non-site-local addresses as candidates,
                            // only the first. For subsequent iterations, candidate will be non-null.
                        }
                    }
                }
            }
            if (candidateAddress != null) {
                // We did not find a site-local address, but we found some other non-loopback address.
                // Server might have a non-site-local address assigned to its NIC (or it might be running
                // IPv6 which deprecates the "site-local" concept).
                // Return this non-loopback candidate address...
                return candidateAddress;
            }
            // At this point, we did not find a non-loopback address.
            // Fall back to returning whatever InetAddress.getLocalHost() returns...
            InetAddress jdkSuppliedAddress = InetAddress.getLocalHost();
            if (jdkSuppliedAddress == null) {
                throw new UnknownHostException("The JDK InetAddress.getLocalHost() method unexpectedly returned null.");
            }
            return jdkSuppliedAddress;
        }
        catch (Exception e) {
            UnknownHostException unknownHostException = new UnknownHostException("Failed to determine LAN address: " + e);
            unknownHostException.initCause(e);
            throw unknownHostException;
        }
    }
    private void pingEurekaServer() {
        final TimerTask timerTask = new TimerTask() {

            @Override
            public void run() {
                try {
                	  String [] addresses=serverAddresses.split(",");
                      for(String address:addresses){
	                    final URL url = new URL(String.format("%s/%s", eurekaUrl, appName));

	                    log.info("Eureka URL found:{}",url);

	                    final HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();

	                    httpConn.setRequestProperty("Content-Type", "application/json; charset=utf-8");
	                    httpConn.setRequestMethod("POST");
	                    httpConn.setDoOutput(true);
	                    httpConn.setDoInput(true);

	                    final OutputStream outputStream = httpConn.getOutputStream();
	                    StringBuffer buffer = new StringBuffer();

	                    String data = buffer.append("{\n").append(
	                            " \"instance\": {\n" ).append(
	                            " \"hostName\": \"").append(InetAddress.getLocalHost().getHostName()).append("\",\n" ).append(
	                            " \"app\":\"").append(address).append("\",\n" ).append(
	                            " \"ipAddr\": \"").append(getLocalHostLANAddress().getHostAddress()).append("\",\n" ).append(
	                            " \"vipAddress\": \"").append(address).append("\",\n" ).append(
	                            " \"appGroupName\": \""+appName+"\",\n" ).append(
	                            " \"secureVipAddress\": \"\",\n" ).append(
	                            " \"status\": \"UP\",\n" ).append(
	                            " \"port\":").append(serverPort).append(",\n" ).append(
	                            " \"securePort\":0, \n" ).append(
	                            " \"dataCenterInfo\": {\n" ).append(
	                            "   \"name\" :\"MyOwn\"\n" ).append(
	                            " }\n" ).append(
	                            " }\n" ).append(
	                            "}").toString();
	                    outputStream.write(data.getBytes());

	                    log.info("Sending heartbeat to EUreka server:{}",data);

	                    try (final BufferedReader in = new BufferedReader(new
	                            InputStreamReader(httpConn.getInputStream()))) {
	                        String responseString;
	                        String outputString = "";

	                        while ((responseString = in.readLine()) != null)
	                            outputString += responseString;

	                       log.info("Eureka server heartbeat respose:{}",outputString);
	                       Thread.sleep(2000);
	                    }

                    }
                } catch (final Exception e) {
                    log.error("Cannot connect to " + eurekaUrl +
                            "\n" + e.getLocalizedMessage(),e);
                }
            }
        };

        final ScheduledExecutorService scheduledThreadPool = Executors.newScheduledThreadPool(1);

        scheduledThreadPool.scheduleAtFixedRate(timerTask, 1, 30, TimeUnit.SECONDS);
    }
}
