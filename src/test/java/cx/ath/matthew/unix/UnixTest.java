package cx.ath.matthew.unix;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringReader;

import org.junit.Assert;
import org.junit.Test;

public class UnixTest extends Assert {

    @Test
    public void testUnixSocket() throws IOException, InterruptedException {
        ServerThread serverThread = new ServerThread("test server");
        serverThread.setDaemon(false);
        serverThread.start();
        Thread.sleep(2000L);
        UnixSocket s = new UnixSocket(new UnixSocketAddress("testsock", true));
        OutputStream os = s.getOutputStream();
        BufferedReader r = new BufferedReader(new StringReader("Test Message"));
        String l;
        while (null != (l = r.readLine())) {
           byte[] buf = (l+"\n").getBytes();
           os.write(buf, 0, buf.length);
        }
        s.close();
        for (int i = 1000; i < 10000; i+=1000) {
            Thread.sleep(i);
            if (serverThread.success) {
                serverThread.interrupt();
                return;
            }
        }
        fail("Test failed");
    }

    private class ServerThread extends Thread {

        private boolean success = false;

        ServerThread(String _name) {
            super(_name);
        }

        @Override
        public void run() {
            UnixServerSocket ss;
            try {
                ss = new UnixServerSocket(new UnixSocketAddress("testsock", true));
                UnixSocket s = ss.accept();
                BufferedReader r = new BufferedReader(new InputStreamReader(s.getInputStream()));
                String l;
                while (null != (l = r.readLine())) {
                    success = "Test Message".equals(l);
                }
                s.close();
                ss.close();
            } catch (IOException _ex) {
                throw new RuntimeException(_ex);
            }

        }
    }
}
