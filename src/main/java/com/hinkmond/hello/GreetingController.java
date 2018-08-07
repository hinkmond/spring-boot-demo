package com.hinkmond.hello;

import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMethod;


@RestController
public class GreetingController {

    private static final String TEMPLATE = "Hello, %s!  Elapsed time = %d";
    private final AtomicLong counter = new AtomicLong();

    @CrossOrigin(origins = "*")
    @RequestMapping(value = "/downloadtest", method = RequestMethod.GET)
    public Greeting greeting(@RequestParam(value = "size", defaultValue = "40MB") String name,
            HttpServletRequest request,
            HttpServletResponse response) {

        byte[] buffer = new byte[65536];
        Random random = new Random();
        for (int i = 0; i < 65535; i++) {
          int randomLimitedInt = random.nextInt(26) + 'a';
          buffer[i] = (byte) randomLimitedInt;
         }
  
        long totalWriten = 0;

        response.setContentType("application/octet-stream");  //$NON-NLS-1$
        response.setContentLength((int) 40000053);
        try {
            // Make sure we get an un-encrypted channel, so that CPU is not a limiting factor for speed measurement
            if (request.isSecure()) {
                response.sendRedirect(response.encodeRedirectURL(getInsecureRequestURLWithQueryString(request)));
                return new Greeting(counter.incrementAndGet(), "Converted to Insecure Request");
            }
            ServletOutputStream output = response.getOutputStream();
            while (totalWriten < 40000000) {
                int toWrite = (int) Math.min(40000000 - totalWriten, buffer.length);
                output.write(buffer, 0, toWrite);
                totalWriten += toWrite;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new Greeting(counter.incrementAndGet(),
                String.format(TEMPLATE, name, 100));
    }

    	/**
	 * Reconstructs the URL to be an insecure request.
         * 
	 * @param request	a <code>HttpServletRequest</code> object
	 *			containing the client's request
	 *
	 * @return		a <code>String</code> object containing
	 *			the reconstructed insecure URL
	 */
	private static String getInsecureRequestURLWithQueryString(HttpServletRequest request) {
		StringBuilder url = new StringBuilder(32);
		int port = request.getServerPort();
		if (port < 0) {
			port = 80; // Work around java.net.URL bug
		}
		String queryString = request.getQueryString();

		url.append("http");	// force this connection to be unsecure: http
		url.append("://");      // $NON-NLS-1$
		url.append(request.getServerName());
		url.append(request.getRequestURI());
		if (queryString != null) {
			url.append('?').append(queryString);
		}
		return url.toString();
	}
}