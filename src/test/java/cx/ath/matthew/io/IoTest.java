/*
 * Java IO Library
 *
 * Copyright (c) Matthew Johnson 2005
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *
 * To Contact the author, please email src@matthew.ath.cx
 *
 */

package cx.ath.matthew.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

public class IoTest extends Assert {

    @Test
    @Ignore(value = "Only works if xsltproc is installed")
    public void test1() throws IOException {
        PrintWriter out = new PrintWriter(new OutputStreamWriter(new ExecOutputStream(System.out, "xsltproc mcr.xsl -")));/// java cx.ath.matthew.io.findeof")));

        out.println("<?xml version='1.0'?>");
        out.println("   <?xml-stylesheet href='style/mcr.xsl' type='text/xsl'?>");
        out.println("   <mcr xmlns:xi='http://www.w3.org/2001/XInclude'>");
        out.println("   <title>TEST</title>");
        out.println("   <content title='TEST'>");
        out.println("hello, he is helping tie up helen's lemmings");
        out.println("we are being followed and we break out");
        out.println("   </content>");
        out.println("   </mcr>");
        out.close();
    }

    @Test
    @Ignore(value = "Only works if xsltproc is installed")
    public void test2() throws IOException {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(new ExecInputStream(System.in, "xsltproc mcr.xsl -")))) {
            String s;
            while (null != (s = in.readLine())) System.out.println(s);
        }
    }

    @Test
    @Ignore
    public void test3() throws IOException {
        String file = "";
        BufferedReader r = new BufferedReader(new InputStreamReader(System.in));

        try (PrintWriter p = new PrintWriter(new TeeOutputStream(System.out, file))) {

        String s;
        while (null != (s = r.readLine()))
           p.println(s);


        } finally {
            r.close();
        }
    }
}
